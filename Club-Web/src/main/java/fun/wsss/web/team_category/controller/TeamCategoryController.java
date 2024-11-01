package fun.wsss.web.team_category.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.team_category.entity.CategoryParm;
import fun.wsss.web.team_category.entity.TeamCategory;
import fun.wsss.web.team_category.service.TeamCategoryService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 活动分类控制器
 *
 * @author Wsssfun
 * @date 2024/10/30 02:31
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/category")
public class TeamCategoryController {
    @Autowired
    private TeamCategoryService teamCategoryService;

    public TeamCategoryController(TeamCategoryService teamCategoryService) {
        this.teamCategoryService = teamCategoryService;
    }

    /**
     * 添加活动
     *
     * @param teamCategory 活动
     * @return 结果
     */
    @PostMapping
    public ResultVo add(@RequestBody TeamCategory teamCategory) {
        if (teamCategoryService.save(teamCategory)) {
            return ResultUtils.success("添加成功");
        }
        return ResultUtils.error("添加失败");
    }

    /**
     * 编辑活动
     *
     * @param teamCategory 活动
     * @return 结果
     */
    @PutMapping
    public ResultVo edit(@RequestBody TeamCategory teamCategory) {
        if (teamCategoryService.updateById(teamCategory)) {
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }

    /**
     * 删除活动
     *
     * @param id 活动id
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
        if (teamCategoryService.removeById(id)) {
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败");
    }

    /**
     * 查询活动列表
     *
     * @param parm 查询条件
     * @return 结果
     */
    @GetMapping("/list")
    public ResultVo list(CategoryParm parm) {
        // 构造分页对象
        IPage<TeamCategory> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        // 构造查询条件
        QueryWrapper<TeamCategory> query = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(parm.getName())) {
            query.lambda().like(TeamCategory::getName, parm.getName());
        }
        // 排序
        query.lambda().orderByAsc(TeamCategory::getOrderNum);
        IPage<TeamCategory> list = teamCategoryService.page(page, query);
        return ResultUtils.success("查询成功", list);
    }
}
