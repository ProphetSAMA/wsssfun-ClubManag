package fun.wsss.web.user.contronller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.user.entity.PageParm;
import fun.wsss.web.user.entity.User;
import fun.wsss.web.user.service.UserService;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户Controller控制器
 *
 * @author Wsssfun
 * @date 2024/10/24 00:22
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 新增用户
     * @param user
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
     * @param user
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
     * @param userId
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
     * @param parm
     * @return 状态
     */
    @GetMapping("/list")
    public ResultVo list(PageParm parm) {
        IPage<User> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());

        //构造查询条件
        QueryWrapper<User> query = new QueryWrapper<>();
        if (StringUtil.isNullOrEmpty(parm.getNickName())) {
            query.lambda().like(User::getNickname, parm.getNickName());
        }

        IPage<User> list = userService.page(page, query);

        return ResultUtils.success("查询成功", list);

    }
}
