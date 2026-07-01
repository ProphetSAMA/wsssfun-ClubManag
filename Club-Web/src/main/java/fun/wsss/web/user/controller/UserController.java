package fun.wsss.web.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.user.entity.PageParm;
import fun.wsss.web.user.entity.User;
import fun.wsss.web.user.service.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * 用户Controller控制器
 *
 * @author Wsssfun
 * @date 2024/10/24 00:22
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 状态
     */
    @PostMapping
    public ResultVo add(@RequestBody User user) {
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
        return ResultUtils.success("查询成功", list);
    }
}
