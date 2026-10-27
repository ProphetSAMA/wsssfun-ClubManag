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
        if (StringUtils.isNotEmpty(parm.getNickName())) {
            query.lambda().like(User::getNickname, parm.getNickName());
        }
        IPage<User> list = userService.page(page, query);
        // 列表不需要密码，且不应该把哈希下发到前端
        list.getRecords().forEach(item -> item.setPassword(null));
        return ResultUtils.success("查询成功", list);
    }
}
