package fun.wsss.web.news.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.news.entity.NewParm;
import fun.wsss.web.news.entity.News;
import fun.wsss.web.news.mapper.NewsMapper;
import fun.wsss.web.news.service.NewsService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 新闻/活动/公告控制器
 *
 * <p>内容有草稿/发布两种状态：门户只应看到已发布的，后台列表两种都能看到。
 *
 * @author Wsssfun
 * @date 2024/11/19 01:42
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {

    /** 已发布 */
    private static final int STATUS_PUBLISHED = 1;

    private final NewsService newsService;
    private final NewsMapper newsMapper;

    public NewsController(NewsService newsService, NewsMapper newsMapper) {
        this.newsService = newsService;
        this.newsMapper = newsMapper;
    }

    private static boolean isAdmin(Integer isAdmin) {
        return isAdmin != null && isAdmin == 1;
    }

    /**
     * 新增新闻/活动/公告
     *
     * @param news 内容
     * @return 结果
     */
    @PostMapping
    public ResultVo add(@RequestBody News news) {
        news.setCreateTime(new Date());
        if (news.getStatus() == null) {
            news.setStatus(STATUS_PUBLISHED);
        }
        news.setViewCount(0);
        if (newsService.save(news)) {
            return ResultUtils.success("新增成功");
        }
        return ResultUtils.error("新增失败");
    }

    /**
     * 编辑
     *
     * @param news 内容
     * @return 结果
     */
    @PutMapping
    public ResultVo edit(@RequestBody News news) {
        if (newsService.updateById(news)) {
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }

    /**
     * 删除
     *
     * @param id ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
        if (newsService.removeById(id)) {
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败");
    }

    /**
     * 分页查询
     *
     * <p>普通用户只看已发布内容；管理员可查询全部，也可用 status 精确筛选。
     *
     * @param parm    查询条件
     * @param isAdmin 是否管理员（来自 token）
     * @return 结果
     */
    @GetMapping("/list")
    public ResultVo list(NewParm parm,
                         @RequestAttribute(value = "isAdmin", required = false) Integer isAdmin) {
        IPage<News> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        QueryWrapper<News> query = new QueryWrapper<>();
        query.lambda()
                .eq(StringUtils.isNotEmpty(parm.getType()), News::getType, parm.getType())
                .like(StringUtils.isNotEmpty(parm.getTitle()), News::getTitle, parm.getTitle())
                .orderByDesc(News::getCreateTime);
        if (isAdmin(isAdmin)) {
            query.lambda().eq(parm.getStatus() != null, News::getStatus, parm.getStatus());
        } else {
            query.lambda().eq(News::getStatus, STATUS_PUBLISHED);
        }
        IPage<News> list = newsService.page(page, query);
        return ResultUtils.success("查询成功", list);
    }

    /**
     * 内容详情（门户用）
     *
     * <p>已发布内容任何人都能看，并且顺手把浏览量 +1；草稿只有管理员能看。
     *
     * @param id      ID
     * @param isAdmin 是否管理员（来自 token，可匿名）
     * @return 结果
     */
    @GetMapping("/detail/{id}")
    public ResultVo detail(@PathVariable("id") Long id,
                           @RequestAttribute(value = "isAdmin", required = false) Integer isAdmin) {
        News news = newsService.getById(id);
        if (news == null) {
            return ResultUtils.error("内容不存在");
        }
        if (!isAdmin(isAdmin) && (news.getStatus() == null || news.getStatus() != STATUS_PUBLISHED)) {
            return ResultUtils.error("内容不存在或未发布");
        }
        newsMapper.increaseView(id);
        int current = news.getViewCount() == null ? 0 : news.getViewCount();
        news.setViewCount(current + 1);
        return ResultUtils.success("查询成功", news);
    }

    /**
     * 推荐的活动
     */
    @GetMapping("/getTopActivity")
    public ResultVo getTopActivity() {
        QueryWrapper<News> query = new QueryWrapper<>();
        query.lambda()
                .eq(News::getType, "0")
                .eq(News::getHasTop, "1")
                .eq(News::getStatus, STATUS_PUBLISHED)
                .orderByDesc(News::getCreateTime);
        return ResultUtils.success("查询成功", newsService.list(query));
    }

    /**
     * 推荐的公告
     */
    @GetMapping("/getTopNotice")
    public ResultVo getTopNotice() {
        QueryWrapper<News> query = new QueryWrapper<>();
        query.lambda()
                .eq(News::getType, "2")
                .eq(News::getHasTop, "1")
                .eq(News::getStatus, STATUS_PUBLISHED)
                .orderByDesc(News::getCreateTime);
        return ResultUtils.success("查询成功", newsService.list(query));
    }

    /**
     * 最新活动
     */
    @GetMapping("/getNewActivity")
    public ResultVo getNewActivity() {
        QueryWrapper<News> query = new QueryWrapper<>();
        query.lambda()
                .eq(News::getType, "0")
                .eq(News::getStatus, STATUS_PUBLISHED)
                .orderByDesc(News::getCreateTime)
                .last("limit 8");
        return ResultUtils.success("查询成功", newsService.list(query));
    }

    /**
     * 最新新闻
     */
    @GetMapping("/getNewNotice")
    public ResultVo getNewNotice() {
        QueryWrapper<News> query = new QueryWrapper<>();
        query.lambda()
                .eq(News::getType, "1")
                .eq(News::getStatus, STATUS_PUBLISHED)
                .orderByDesc(News::getCreateTime)
                .last("limit 8");
        return ResultUtils.success("查询成功", newsService.list(query));
    }
}
