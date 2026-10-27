package fun.wsss.web.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.config.jwt.JwtUtils;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.user.entity.*;
import fun.wsss.web.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    public UserController(UserService userService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
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

        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getUsername, loginDTO.getUsername());
        User user = userService.getOne(query);

        if (user == null) {
            return ResultUtils.error("用户不存在");
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
            return ResultUtils.error("密码错误");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            return ResultUtils.error("账号已被禁用");
        }

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
        if (password.length() < 6) {
            return ResultUtils.error("密码至少 6 位");
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
        if (parm.getNewPassword().length() < 6) {
            return ResultUtils.error("新密码至少 6 位");
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
