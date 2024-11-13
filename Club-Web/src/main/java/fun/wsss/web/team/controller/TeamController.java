package fun.wsss.web.team.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.team.entity.Team;
import fun.wsss.web.team.entity.TeamPage;
import fun.wsss.web.team.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 社团控制器
 *
 * @author Wsssfun
 * @date 2024/11/1 21:26
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/team")
public class TeamController {
    @Autowired
    private TeamService teamService;

    /**
     * 新增社团
     *
     * @param team 社团
     * @return 结果
     */
    @PostMapping
    public ResultVo add(@RequestBody Team team) {
        team.setCreateTime(new Date());
        if (teamService.save(team)) {
            return ResultUtils.success("新增成功!");
        }
        return ResultUtils.error("新增失败!");
    }

    /**
     * 编辑社团
     *
     * @param team 社团
     * @return 结果
     */
    @PutMapping
    public ResultVo edit(@RequestBody Team team) {
        if (teamService.updateById(team)) {
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    /**
     * 删除社团
     *
     * @param id 社团id
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
        if (teamService.removeById(id)) {
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }

    /**
     * 查询社团列表
     *
     * @param parm 查询条件
     * @return 结果
     */
    @GetMapping("/list")
    public ResultVo list(TeamPage parm) {
        // 构造分页对象
        IPage<Team> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        // 构造查询条件
        QueryWrapper<Team> query = new QueryWrapper<>();
        // 模糊查询 社团名称 并按创建时间升序排序
        query.lambda().like(StringUtils.isNotEmpty(parm.getName()), Team::getName, parm.getName())
                .orderByAsc(Team::getCreateTime);
        // 分页查询
        IPage<Team> list = teamService.page(page, query);
        return ResultUtils.success("查询成功", list);

    }
}
