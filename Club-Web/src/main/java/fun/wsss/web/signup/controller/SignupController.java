package fun.wsss.web.signup.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.status.StatusCode;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.news.entity.News;
import fun.wsss.web.news.service.NewsService;
import fun.wsss.web.signup.entity.Signup;
import fun.wsss.web.signup.entity.SignupVo;
import fun.wsss.web.signup.mapper.SignupMapper;
import fun.wsss.web.signup.service.SignupService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 活动报名控制器
 *
 * <p>报名/取消都是"对自己"的操作，userId 一律取自 token；
 * 查看某个活动的报名名单属于管理操作，仅管理员可用。
 *
 * @author Wsssfun
 */
@RestController
@RequestMapping("/api/signup")
public class SignupController {

    private final SignupService signupService;
    private final SignupMapper signupMapper;
    private final NewsService newsService;

    public SignupController(SignupService signupService, SignupMapper signupMapper, NewsService newsService) {
        this.signupService = signupService;
        this.signupMapper = signupMapper;
        this.newsService = newsService;
    }

    private static boolean isAdmin(Integer isAdmin) {
        return isAdmin != null && isAdmin == 1;
    }

    /**
     * 报名活动
     *
     * @param signup 只需要 newsId
     * @param userId 当前登录用户（来自 token）
     * @return 结果
     */
    @PostMapping
    public ResultVo signup(@RequestBody Signup signup,
                           @RequestAttribute(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return ResultUtils.error("未登录，请先登录", StatusCode.UNAUTHORIZED);
        }
        Long newsId = signup.getNewsId();
        if (newsId == null) {
            return ResultUtils.error("请选择要报名的活动");
        }
        News news = newsService.getById(newsId);
        if (news == null || !"0".equals(news.getType())) {
            return ResultUtils.error("活动不存在");
        }
        if (news.getStatus() != null && news.getStatus() != 1) {
            return ResultUtils.error("活动尚未发布");
        }

        QueryWrapper<Signup> query = new QueryWrapper<>();
        query.lambda().eq(Signup::getNewsId, newsId).eq(Signup::getUserId, userId);
        Signup exist = signupService.getOne(query);
        if (exist != null && exist.getStatus() != null && exist.getStatus() == 1) {
            return ResultUtils.error("已经报过名了");
        }
        if (exist != null) {
            // 取消过再报名：复用原来的行，表上有 (news_id, user_id) 唯一索引
            exist.setStatus(1);
            exist.setCreateTime(new Date());
            return signupService.updateById(exist)
                    ? ResultUtils.success("报名成功")
                    : ResultUtils.error("报名失败");
        }

        Signup record = new Signup();
        record.setNewsId(newsId);
        record.setUserId(userId);
        record.setStatus(1);
        record.setCreateTime(new Date());
        return signupService.save(record)
                ? ResultUtils.success("报名成功")
                : ResultUtils.error("报名失败");
    }

    /**
     * 取消报名
     *
     * @param newsId 活动ID
     * @param userId 当前登录用户（来自 token）
     * @return 结果
     */
    @DeleteMapping("/{newsId}")
    public ResultVo cancel(@PathVariable("newsId") Long newsId,
                           @RequestAttribute(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return ResultUtils.error("未登录，请先登录", StatusCode.UNAUTHORIZED);
        }
        QueryWrapper<Signup> query = new QueryWrapper<>();
        query.lambda().eq(Signup::getNewsId, newsId).eq(Signup::getUserId, userId);
        Signup exist = signupService.getOne(query);
        if (exist == null || exist.getStatus() == null || exist.getStatus() != 1) {
            return ResultUtils.error("还没有报名这个活动");
        }
        exist.setStatus(0);
        return signupService.updateById(exist)
                ? ResultUtils.success("已取消报名")
                : ResultUtils.error("取消失败");
    }

    /**
     * 我报名的活动
     *
     * @param currentPage 页码
     * @param pageSize    每页条数
     * @param userId      当前登录用户（来自 token）
     * @return 分页结果
     */
    @GetMapping("/my")
    public ResultVo my(@RequestParam(defaultValue = "1") Integer currentPage,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestAttribute(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return ResultUtils.error("未登录，请先登录", StatusCode.UNAUTHORIZED);
        }
        Page<SignupVo> page = new Page<>(currentPage, pageSize);
        IPage<SignupVo> list = signupMapper.selectMySignups(page, userId);
        return ResultUtils.success("查询成功", list);
    }

    /**
     * 某个活动的报名人数（门户列表用，游客也能看）
     *
     * @param newsId 活动ID
     * @return 人数
     */
    @GetMapping("/count")
    public ResultVo count(@RequestParam Long newsId) {
        QueryWrapper<Signup> query = new QueryWrapper<>();
        query.lambda().eq(Signup::getNewsId, newsId).eq(Signup::getStatus, 1);
        return ResultUtils.success("查询成功", signupService.count(query));
    }

    /**
     * 某个活动的报名名单（管理员）
     *
     * @param newsId      活动ID
     * @param currentPage 页码
     * @param pageSize    每页条数
     * @param isAdmin     是否管理员（来自 token）
     * @return 分页结果
     */
    @GetMapping("/list")
    public ResultVo list(@RequestParam Long newsId,
                         @RequestParam(defaultValue = "1") Integer currentPage,
                         @RequestParam(defaultValue = "20") Integer pageSize,
                         @RequestAttribute(value = "isAdmin", required = false) Integer isAdmin) {
        if (!isAdmin(isAdmin)) {
            return ResultUtils.error("没有权限查看报名名单", StatusCode.FORBIDDEN);
        }
        Page<SignupVo> page = new Page<>(currentPage, pageSize);
        IPage<SignupVo> list = signupMapper.selectSignupsByNews(page, newsId);
        return ResultUtils.success("查询成功", list);
    }
}
