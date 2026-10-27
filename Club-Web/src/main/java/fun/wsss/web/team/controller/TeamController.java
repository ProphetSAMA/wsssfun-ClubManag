package fun.wsss.web.team.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.team.entity.Team;
import fun.wsss.web.team.entity.TeamMemberVo;
import fun.wsss.web.team.mapper.TeamMapper;
import fun.wsss.web.team.entity.TeamPage;
import fun.wsss.web.team.service.TeamService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 社团控制器
 *
 * @author Wsssfun
 * @date 2024/11/1 21:26
 */
@RestController
@RequestMapping("/api/team")
public class TeamController {

    private final TeamService teamService;
    private final TeamMapper teamMapper;

    public TeamController(TeamService teamService, TeamMapper teamMapper) {
        this.teamService = teamService;
        this.teamMapper = teamMapper;
    }

    /**
     * 新增社团
     *
     * @param team 社团信息
     * @return 结果
     */
    @PostMapping
    public ResultVo add(@RequestBody Team team) {
        team.setCreateTime(new Date());
        if (teamService.save(team)) {
            return ResultUtils.success("新增成功");
        }
        return ResultUtils.error("新增失败");
    }

    /**
     * 编辑社团
     *
     * @param team 社团信息
     * @return 结果
     */
    @PutMapping
    public ResultVo edit(@RequestBody Team team) {
        if (teamService.updateById(team)) {
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }

    /**
     * 删除社团
     *
     * @param id 社团ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
        if (teamService.removeById(id)) {
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败");
    }

    /**
     * 社团详情（门户/后台共用）
     *
     * @param id 社团ID
     * @return 社团信息
     */
    @GetMapping("/{id}")
    public ResultVo detail(@PathVariable("id") Long id) {
        Team team = teamService.getById(id);
        if (team == null) {
            return ResultUtils.error("社团不存在");
        }
        return ResultUtils.success("查询成功", team);
    }

    /**
     * 社团成员（门户展示，不返回手机号等隐私字段）
     *
     * @param id 社团ID
     * @return 成员列表
     */
    @GetMapping("/{id}/members")
    public ResultVo members(@PathVariable("id") Long id) {
        List<TeamMemberVo> list = teamMapper.selectMembers(id);
        return ResultUtils.success("查询成功", list);
    }

    /**
     * 查询社团列表
     *
     * @param parm 查询条件
     * @return 结果
     */
    @GetMapping("/list")
    public ResultVo list(TeamPage parm) {
        IPage<Team> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        QueryWrapper<Team> query = new QueryWrapper<>();
        query.lambda()
                .like(StringUtils.isNotEmpty(parm.getName()), Team::getName, parm.getName())
                .orderByAsc(Team::getCreateTime);
        IPage<Team> list = teamService.page(page, query);
        return ResultUtils.success("查询成功", list);
    }

    /**
     * 查询置顶社团
     *
     * @return 结果
     */
    @GetMapping("/getTopTeam")
    public ResultVo getTopTeam() {
        QueryWrapper<Team> query = new QueryWrapper<>();
        query.lambda()
                .eq(Team::getHasTop, "1")
                .orderByAsc(Team::getCreateTime);
        return ResultUtils.success("查询成功", teamService.list(query));
    }
}
