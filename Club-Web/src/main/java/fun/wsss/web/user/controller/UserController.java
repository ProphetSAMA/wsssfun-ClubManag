package fun.wsss.web.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.config.jwt.JwtUtils;
import fun.wsss.config.jwt.TokenBlacklist;
import fun.wsss.config.security.LoginAttemptService;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.user.entity.*;
import fun.wsss.web.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 用户Controller控制器
 *
 * @author Wsssfun
 * @date 2024/10/24 00:22
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    /** BCrypt 哈希形如 $2a$10$... 共 60 位 */
    private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[aby]\\$\\d{2}\\$.{53}$");

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklist tokenBlacklist;
    private final LoginAttemptService loginAttemptService;

    public UserController(UserService userService,
                          JwtUtils jwtUtils,
                          PasswordEncoder passwordEncoder,
                          TokenBlacklist tokenBlacklist,
                          LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.tokenBlacklist = tokenBlacklist;
        this.loginAttemptService = loginAttemptService;
    }

    private static boolean isBcrypt(String password) {
        return password != null && BCRYPT_PATTERN.matcher(password).matches();
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录参数
     * @return 登录结果（含 token）
     */
    @PostMapping("/login")
    public ResultVo login(@RequestBody LoginDTO loginDTO) {
        if (StringUtils.isEmpty(loginDTO.getUsername()) || StringUtils.isEmpty(loginDTO.getPassword())) {
            return ResultUtils.error("用户名和密码不能为空");
        }

        long lockedSeconds = loginAttemptService.lockedSeconds(loginDTO.getUsername());
        if (lockedSeconds > 0) {
            long minutes = Math.max(1, (lockedSeconds + 59) / 60);
            return ResultUtils.error("登录失败次数过多，请 " + minutes + " 分钟后再试");
        }

        // 用 LIMIT 1 取一条：库里如果存在历史重复账号（没有唯一索引时可能出现），
        // getOne 会抛 TooManyResultsException，导致整个登录接口 500
        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getUsername, loginDTO.getUsername()).last("LIMIT 1");
        User user = userService.getOne(query);

        if (user == null) {
            // 统一文案，避免通过"用户不存在/密码错误"猜账号
            loginAttemptService.onFailure(loginDTO.getUsername());
            return ResultUtils.error("用户名或密码错误");
        }

        boolean matched;
        if (isBcrypt(user.getPassword())) {
            matched = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
        } else {
            // 兼容历史明文密码：校验通过后顺手升级为 BCrypt，用户无感知
            matched = loginDTO.getPassword().equals(user.getPassword());
            if (matched) {
                user.setPassword(passwordEncoder.encode(loginDTO.getPassword()));
                userService.updateById(user);
            }
        }
        if (!matched) {
            loginAttemptService.onFailure(loginDTO.getUsername());
            return ResultUtils.error("用户名或密码错误");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            return ResultUtils.error("账号已被禁用");
        }

        loginAttemptService.onSuccess(loginDTO.getUsername());

        String token = jwtUtils.generateToken(user.getUserId(), user.getUsername(), user.getIsAdmin());
        LoginVO loginVO = new LoginVO(token, user.getUserId(), user.getUsername(), user.getNickname(), user.getIsAdmin());
        return ResultUtils.success("登录成功", loginVO);
    }

    /**
     * 用户注册
     *
     * <p>游客可调用。新用户一律是普通用户：状态启用、is_admin = 0，
     * 密码加密存储。用户名唯一，重复注册会被告知。
     *
     * @param parm 注册参数
     * @return 结果
     */
    @PostMapping("/register")
    public ResultVo register(@RequestBody RegisterParm parm) {
        String username = parm.getUsername() == null ? "" : parm.getUsername().trim();
        String password = parm.getPassword() == null ? "" : parm.getPassword();

        if (username.length() < 3 || username.length() > 20) {
            return ResultUtils.error("用户名长度需在 3 到 20 个字符之间");
        }
        if (!username.matches("^[A-Za-z0-9_]+$")) {
            return ResultUtils.error("用户名只能包含字母、数字和下划线");
        }
        if (password.length() < 8) {
            return ResultUtils.error("密码至少 8 位");
        }

        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getUsername, username);
        if (userService.count(query) > 0) {
            return ResultUtils.error("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(StringUtils.isEmpty(parm.getNickname()) ? username : parm.getNickname().trim());
        user.setSex("0");
        user.setStatus(1);
        user.setIsAdmin(0);
        if (userService.save(user)) {
            return ResultUtils.success("注册成功");
        }
        return ResultUtils.error("注册失败");
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    public ResultVo info(@RequestAttribute Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return ResultUtils.error("用户不存在");
        }
        user.setPassword(null);
        return ResultUtils.success("查询成功", user);
    }

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 状态
     */
    @PostMapping
    public ResultVo add(@RequestBody User user) {
        if (StringUtils.isEmpty(user.getPassword())) {
            return ResultUtils.error("密码不能为空");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (userService.save(user)) {
            return ResultUtils.success("添加成功");
        }
        return ResultUtils.error("添加失败");
    }

    /**
     * 退出登录
     *
     * <p>JWT 无状态，这里把当前 token 的 jti 加入黑名单，过期前都不能再用。
     *
     * @param jti               当前令牌 ID（来自拦截器）
     * @param tokenExpiration   当前令牌过期时间（来自拦截器）
     * @return 结果
     */
    @PostMapping("/logout")
    public ResultVo logout(@RequestAttribute(value = "jti", required = false) String jti,
                           @RequestAttribute(value = "tokenExpiration", required = false) Long tokenExpiration) {
        if (jti != null && !jti.isEmpty()) {
            long expiresAt = tokenExpiration == null
                    ? System.currentTimeMillis() + 24L * 60 * 60 * 1000
                    : tokenExpiration;
            tokenBlacklist.revoke(jti, expiresAt);
        }
        return ResultUtils.success("已退出登录");
    }


    /**
     * 设置头像
     *
     * <p>前端先调 /api/upload/avatar 拿到路径，再调这里保存；只接受 /images/ 下的相对路径。
     *
     * @param body   { avatar: "/images/xxx.png" }
     * @param userId 当前用户
     * @return 结果
     */
    @PutMapping("/avatar")
    public ResultVo updateAvatar(@RequestBody Map<String, String> body,
                                 @RequestAttribute("userId") Long userId) {
        String avatar = body == null ? null : body.get("avatar");
        if (avatar != null && !avatar.isBlank()) {
            avatar = avatar.trim();
            if (!avatar.startsWith("/images/") || avatar.contains("..")) {
                return ResultUtils.error("头像路径不合法");
            }
        }
        // 用 UpdateWrapper 显式 set：全局 update-strategy 是 not_empty，
        // 走实体更新时空字符串会被跳过，导致"恢复默认头像"拼出没有 SET 的 SQL
        boolean clear = avatar == null || avatar.isBlank();
        UpdateWrapper<User> update = new UpdateWrapper<>();
        update.lambda().eq(User::getUserId, userId).set(User::getAvatar, clear ? null : avatar);
        return userService.update(update)
                ? ResultUtils.success(clear ? "已恢复默认头像" : "头像已更新")
                : ResultUtils.error("保存失败");
    }

    /**
     * 查看自己的密保问题
     *
     * @param userId 当前用户
     * @return 问题（未设置时为空）
     */
    @GetMapping("/security-question")
    public ResultVo getSecurityQuestion(@RequestAttribute("userId") Long userId) {
        User user = userService.getById(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("question", user == null ? null : user.getSecurityQuestion());
        return ResultUtils.success("查询成功", data);
    }

    /**
     * 设置 / 修改密保问题
     *
     * <p>需要校验当前密码，避免别人拿到登录态后顺手把找回方式改掉。
     *
     * @param parm   { password, question, answer }
     * @param userId 当前用户
     * @return 结果
     */
    @PutMapping("/security-question")
    public ResultVo setSecurityQuestion(@RequestBody Map<String, String> parm,
                                        @RequestAttribute("userId") Long userId) {
        String password = parm.get("password");
        String question = parm.get("question");
        String answer = parm.get("answer");
        if (password == null || password.isEmpty()) {
            return ResultUtils.error("请输入当前密码");
        }
        if (question == null || question.isBlank() || question.length() > 100) {
            return ResultUtils.error("请填写密保问题（100 字以内）");
        }
        if (answer == null || answer.isBlank() || answer.length() > 50) {
            return ResultUtils.error("请填写密保答案（50 字以内）");
        }
        User user = userService.getById(userId);
        if (user == null) {
            return ResultUtils.error("用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResultUtils.error("当前密码不正确");
        }
        user.setSecurityQuestion(question.trim());
        user.setSecurityAnswerHash(passwordEncoder.encode(answer.trim()));
        return userService.updateById(user)
                ? ResultUtils.success("密保问题已保存")
                : ResultUtils.error("保存失败");
    }

    /**
     * 忘记密码：查询密保问题（未登录可调用）
     *
     * @param body { username }
     * @return 密保问题
     */
    @PostMapping("/forgot/query")
    public ResultVo forgotQuery(@RequestBody Map<String, String> body) {
        String username = body == null ? null : body.get("username");
        if (username == null || username.isBlank()) {
            return ResultUtils.error("请输入账号");
        }
        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getUsername, username.trim()).last("LIMIT 1");
        User user = userService.getOne(query);
        // 不区分"账号不存在"和"没设置密保"，减少账号枚举
        if (user == null || user.getSecurityQuestion() == null) {
            return ResultUtils.error("该账号没有设置密保问题，请联系管理员重置密码");
        }
        Map<String, String> data = new HashMap<>();
        data.put("question", user.getSecurityQuestion());
        return ResultUtils.success("查询成功", data);
    }

    /**
     * 忘记密码：回答密保问题并重置密码（未登录可调用）
     *
     * @param body { username, answer, newPassword }
     * @return 结果
     */
    @PostMapping("/forgot/reset")
    public ResultVo forgotReset(@RequestBody Map<String, String> body) {
        String username = body == null ? null : body.get("username");
        String answer = body == null ? null : body.get("answer");
        String newPassword = body == null ? null : body.get("newPassword");
        if (username == null || username.isBlank() || answer == null || newPassword == null) {
            return ResultUtils.error("参数不完整");
        }
        if (newPassword.length() < 8) {
            return ResultUtils.error("密码至少 8 位");
        }
        String key = "forgot:" + username.trim();
        long locked = loginAttemptService.lockedSeconds(key);
        if (locked > 0) {
            long minutes = Math.max(1, (locked + 59) / 60);
            return ResultUtils.error("尝试次数过多，请 " + minutes + " 分钟后再试");
        }
        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getUsername, username.trim()).last("LIMIT 1");
        User user = userService.getOne(query);
        if (user == null || user.getSecurityAnswerHash() == null) {
            return ResultUtils.error("密保信息不匹配");
        }
        if (!passwordEncoder.matches(answer.trim(), user.getSecurityAnswerHash())) {
            loginAttemptService.onFailure(key);
            return ResultUtils.error("密保答案不正确");
        }
        loginAttemptService.onSuccess(key);
        user.setPassword(passwordEncoder.encode(newPassword));
        return userService.updateById(user)
                ? ResultUtils.success("密码已重置，请用新密码登录")
                : ResultUtils.error("重置失败");
    }
    /**
     * 续签 token
     *
     * <p>前端在业务码 401（登录过期）时先调这里换一个新 token 再重试原请求，
     * 避免用户填表到一半被踢回登录页。这里要求当前 token 仍然有效。
     *
     * @param userId  当前登录用户（来自 token）
     * @param username 用户名（来自 token）
     * @param isAdmin 是否管理员（来自 token）
     * @return 新 token
     */
    @PostMapping("/refresh")
    public ResultVo refresh(@RequestAttribute("userId") Long userId,
                            @RequestAttribute("username") String username,
                            @RequestAttribute(value = "isAdmin", required = false) Integer isAdmin) {
        String token = jwtUtils.generateToken(userId, username, isAdmin);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return ResultUtils.success("续签成功", data);
    }

    /**
     * 修改自己的资料
     *
     * <p>只允许改昵称、手机号、性别。用户名、密码、管理员标识都不在这里改，
     * 免得普通用户顺手把自己提成管理员。
     *
     * @param parm   前端提交的资料
     * @param userId 当前登录用户（来自 token）
     * @return 状态
     */
    @PutMapping("/profile")
    public ResultVo updateProfile(@RequestBody User parm, @RequestAttribute("userId") Long userId) {
        User exist = userService.getById(userId);
        if (exist == null) {
            return ResultUtils.error("用户不存在");
        }
        if (StringUtils.isEmpty(parm.getNickname())) {
            return ResultUtils.error("昵称不能为空");
        }
        exist.setNickname(parm.getNickname());
        exist.setPhone(parm.getPhone());
        exist.setSex(parm.getSex());
        if (userService.updateById(exist)) {
            return ResultUtils.success("保存成功");
        }
        return ResultUtils.error("保存失败");
    }

    /**
     * 修改自己的密码
     *
     * <p>必须校验原密码；新密码由服务端加密后存储。
     * 历史明文密码同样可以正常修改。
     *
     * @param parm   原密码 + 新密码
     * @param userId 当前登录用户（来自 token）
     * @return 状态
     */
    @PutMapping("/password")
    public ResultVo changePassword(@RequestBody PasswordParm parm, @RequestAttribute("userId") Long userId) {
        if (StringUtils.isEmpty(parm.getOldPassword()) || StringUtils.isEmpty(parm.getNewPassword())) {
            return ResultUtils.error("原密码和新密码不能为空");
        }
        if (parm.getNewPassword().length() < 8) {
            return ResultUtils.error("新密码至少 8 位");
        }
        User exist = userService.getById(userId);
        if (exist == null) {
            return ResultUtils.error("用户不存在");
        }
        boolean matched = isBcrypt(exist.getPassword())
                ? passwordEncoder.matches(parm.getOldPassword(), exist.getPassword())
                : parm.getOldPassword().equals(exist.getPassword());
        if (!matched) {
            return ResultUtils.error("原密码不正确");
        }
        exist.setPassword(passwordEncoder.encode(parm.getNewPassword()));
        if (userService.updateById(exist)) {
            return ResultUtils.success("密码修改成功");
        }
        return ResultUtils.error("密码修改失败");
    }

    /**
     * 编辑用户
     *
     * @param user 用户信息
     * @return 状态
     */
    @PutMapping
    public ResultVo edit(@RequestBody User user) {
        // 留空表示不修改密码；只有明文新密码才需要重新加密
        if (StringUtils.isNotEmpty(user.getPassword()) && !isBcrypt(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (userService.updateById(user)) {
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 状态
     */
    @DeleteMapping("/{userId}")
    public ResultVo delete(@PathVariable("userId") Long userId) {
        if (userService.removeById(userId)) {
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败");
    }

    /**
     * 查询用户列表
     *
     * @param parm 分页参数
     * @return 用户列表
     */
    @GetMapping("/list")
    public ResultVo list(PageParm parm) {
        IPage<User> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda()
                .like(StringUtils.isNotEmpty(parm.getNickName()), User::getNickname, parm.getNickName())
                .like(StringUtils.isNotEmpty(parm.getUsername()), User::getUsername, parm.getUsername())
                .like(StringUtils.isNotEmpty(parm.getPhone()), User::getPhone, parm.getPhone())
                .eq(parm.getIsAdmin() != null, User::getIsAdmin, parm.getIsAdmin())
                .orderByDesc(User::getUserId);
        IPage<User> list = userService.page(page, query);
        // 列表不需要密码，且不应该把哈希下发到前端
        list.getRecords().forEach(item -> item.setPassword(null));
        return ResultUtils.success("查询成功", list);
    }
}
