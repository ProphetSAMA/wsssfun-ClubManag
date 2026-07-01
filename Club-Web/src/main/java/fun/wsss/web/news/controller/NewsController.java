package fun.wsss.web.news.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.news.entity.NewParm;
import fun.wsss.web.news.entity.News;
import fun.wsss.web.news.service.NewsService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 新闻/活动/公告控制器
 *
 * @author Wsssfun
 * @date 2024/11/19 01:42
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
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
     * @param parm 查询条件
     * @return 结果
     */
    @GetMapping("/list")
    public ResultVo list(NewParm parm) {
        IPage<News> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        QueryWrapper<News> query = new QueryWrapper<>();
        query.lambda()
                .eq(StringUtils.isNotEmpty(parm.getType()), News::getType, parm.getType())
                .like(StringUtils.isNotEmpty(parm.getTitle()), News::getTitle, parm.getTitle())
                .orderByDesc(News::getCreateTime);
        IPage<News> list = newsService.page(page, query);
        return ResultUtils.success("查询成功", list);
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
                .orderByDesc(News::getCreateTime)
                .last("limit 8");
        return ResultUtils.success("查询成功", newsService.list(query));
    }
}
