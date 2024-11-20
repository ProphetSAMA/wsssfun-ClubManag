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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author Wsssfun
 * @date 2024/11/19 01:42
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/news")
public class NewsController {
    @Autowired
    private NewsService newsService;

    /**
     * 新增
     *
     * @param news 内容:公告、新闻、活动
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

    @PutMapping
    public ResultVo edit(@RequestBody News news) {
        if (newsService.updateById(news)) {
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }

    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
        if (newsService.removeById(id)) {
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败");
    }

    @GetMapping("/list")
    public ResultVo list(NewParm parm) {
        // 构造分页对象
        IPage<News> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        // 构造查询条件
        QueryWrapper<News> query = new QueryWrapper<>();
        query.lambda().eq(StringUtils.isNotEmpty(parm.getType()), News::getType, parm.getType())
                .like(StringUtils.isNotEmpty(parm.getTitle()), News::getTitle, parm.getTitle())
                .orderByAsc(News::getCreateTime);

        IPage<News> list = newsService.page(page, query);

        return ResultUtils.success("查询成功", list);
    }
}
