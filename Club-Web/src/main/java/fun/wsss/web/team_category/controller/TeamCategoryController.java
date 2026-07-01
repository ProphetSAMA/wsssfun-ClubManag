package fun.wsss.web.team_category.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.team_category.entity.CategoryParm;
import fun.wsss.web.team_category.entity.SelectType;
import fun.wsss.web.team_category.entity.TeamCategory;
import fun.wsss.web.team_category.service.TeamCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 社团分类控制器
 *
 * @author Wsssfun
 * @date 2024/10/30 02:31
 */
@RestController
@RequestMapping("/api/category")
public class TeamCategoryController {

    private final TeamCategoryService teamCategoryService;

    public TeamCategoryController(TeamCategoryService teamCategoryService) {
        this.teamCategoryService = teamCategoryService;
    }

    /**
     * 添加分类
     *
     * @param teamCategory 分类信息
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
     * 编辑分类
     *
     * @param teamCategory 分类信息
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
     * 删除分类
     *
     * @param id 分类ID
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
     * 查询分类列表
     *
     * @param parm 查询条件
     * @return 结果
     */
    @GetMapping("/list")
    public ResultVo list(CategoryParm parm) {
        IPage<TeamCategory> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        QueryWrapper<TeamCategory> query = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(parm.getName())) {
            query.lambda().like(TeamCategory::getName, parm.getName());
        }
        query.lambda().orderByAsc(TeamCategory::getOrderNum);
        IPage<TeamCategory> list = teamCategoryService.page(page, query);
        return ResultUtils.success("查询成功", list);
    }

    /**
     * 获取下拉框数据
     *
     * @return 下拉框选项列表
     */
    @GetMapping("/getSelectList")
    public ResultVo getSelectList() {
        List<TeamCategory> list = teamCategoryService.list();
        List<SelectType> selectList = list.stream()
                .map(item -> {
                    SelectType type = new SelectType();
                    type.setLabel(item.getName());
                    type.setValue(item.getId());
                    return type;
                })
                .collect(Collectors.toList());
        return ResultUtils.success("查询成功", selectList);
    }
}
