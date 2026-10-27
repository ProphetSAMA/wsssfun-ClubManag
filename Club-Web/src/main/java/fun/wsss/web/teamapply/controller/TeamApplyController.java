package fun.wsss.web.teamapply.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.status.StatusCode;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.member.entity.Member;
import fun.wsss.web.member.service.MemberService;
import fun.wsss.web.team.entity.Team;
import fun.wsss.web.team.service.TeamService;
import fun.wsss.web.teamapply.entity.TeamApply;
import fun.wsss.web.teamapply.entity.TeamApplyVo;
import fun.wsss.web.teamapply.mapper.TeamApplyMapper;
import fun.wsss.web.teamapply.service.TeamApplyService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * 社团成立申请
 *
 * <p>任何登录用户都可以提交申请；只有平台管理员能审核。审核通过时自动创建社团，
 * 并把申请人加入该社团、角色设为社长（2），这样申请人立刻就能在门户「社团管理」里操作。
 *
 * @author Wsssfun
 */
@RestController
@RequestMapping("/api/team-apply")
public class TeamApplyController {

    private final TeamApplyService teamApplyService;
    private final TeamApplyMapper teamApplyMapper;
    private final TeamService teamService;
    private final MemberService memberService;

    public TeamApplyController(TeamApplyService teamApplyService,
                               TeamApplyMapper teamApplyMapper,
                               TeamService teamService,
                               MemberService memberService) {
        this.teamApplyService = teamApplyService;
        this.teamApplyMapper = teamApplyMapper;
        this.teamService = teamService;
        this.memberService = memberService;
    }

    private static ResultVo forbidden(String msg) {
        return ResultUtils.error(msg, StatusCode.FORBIDDEN);
    }

    /**
     * 提交成立申请
     *
     * @param body   { name, categoryId, reason }
     * @param userId 当前用户
     * @return 结果
     */
    @PostMapping
    public ResultVo submit(@RequestBody Map<String, Object> body,
                           @RequestAttribute(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return ResultUtils.error("未登录，请先登录", StatusCode.UNAUTHORIZED);
        }
        String name = body.get("name") == null ? null : String.valueOf(body.get("name")).trim();
        if (name == null || name.isEmpty()) {
            return ResultUtils.error("请填写社团名称");
        }
        if (name.length() > 100) {
            return ResultUtils.error("社团名称不能超过 100 个字");
        }

        // 已经有待审核的申请就不要再堆了
        QueryWrapper<TeamApply> pending = new QueryWrapper<>();
        pending.lambda().eq(TeamApply::getApplicantId, userId).eq(TeamApply::getStatus, 0);
        if (teamApplyService.count(pending) > 0) {
            return ResultUtils.error("你还有待审核的申请，请等待处理");
        }

        // 与已有社团 / 待审申请重名
        QueryWrapper<Team> sameName = new QueryWrapper<>();
        sameName.lambda().eq(Team::getName, name);
        if (teamService.count(sameName) > 0) {
            return ResultUtils.error("已存在同名社团");
        }
        QueryWrapper<TeamApply> sameApply = new QueryWrapper<>();
        sameApply.lambda().eq(TeamApply::getName, name).eq(TeamApply::getStatus, 0);
        if (teamApplyService.count(sameApply) > 0) {
            return ResultUtils.error("已有同名社团正在审核中");
        }

        TeamApply apply = new TeamApply();
        apply.setName(name);
        Object categoryId = body.get("categoryId");
        if (categoryId != null && !String.valueOf(categoryId).isBlank()) {
            apply.setCategoryId(Long.valueOf(String.valueOf(categoryId)));
        }
        apply.setReason(body.get("reason") == null ? null : String.valueOf(body.get("reason")));
        apply.setApplicantId(userId);
        apply.setStatus(0);
        apply.setCreateTime(new Date());
        return teamApplyService.save(apply)
                ? ResultUtils.success("申请已提交，等待管理员审核")
                : ResultUtils.error("提交失败");
    }

    /**
     * 我的申请记录
     *
     * @param userId 当前用户
     * @return 申请列表
     */
    @GetMapping("/my")
    public ResultVo my(@RequestAttribute(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return ResultUtils.error("未登录，请先登录", StatusCode.UNAUTHORIZED);
        }
        QueryWrapper<TeamApply> query = new QueryWrapper<>();
        query.lambda().eq(TeamApply::getApplicantId, userId).orderByDesc(TeamApply::getCreateTime);
        return ResultUtils.success("查询成功", teamApplyService.list(query));
    }

    /**
     * 后台申请列表（管理员）
     *
     * @param status      0-待审 1-通过 2-驳回，不传查全部
     * @param keyword     社团名/申请人
     * @param currentPage 页码
     * @param pageSize    每页条数
     * @param isAdmin     当前用户是否管理员
     * @return 分页结果
     */
    @GetMapping("/list")
    public ResultVo list(@RequestParam(required = false) Integer status,
                         @RequestParam(required = false) String keyword,
                         @RequestParam(defaultValue = "1") Integer currentPage,
                         @RequestParam(defaultValue = "10") Integer pageSize,
                         @RequestAttribute(value = "isAdmin", required = false) Integer isAdmin) {
        if (isAdmin == null || isAdmin != 1) {
            return forbidden("只有管理员可以查看申请列表");
        }
        Page<TeamApplyVo> page = new Page<>(currentPage, pageSize);
        IPage<TeamApplyVo> result = teamApplyMapper.selectApplyPage(page, status, keyword);
        return ResultUtils.success("查询成功", result);
    }

    /**
     * 通过申请：建社团 + 申请人成为社长
     *
     * @param id      申请ID
     * @param isAdmin 当前用户是否管理员
     * @param userId  审核人
     * @return 结果
     */
    @PutMapping("/{id}/approve")
    @Transactional(rollbackFor = Exception.class)
    public ResultVo approve(@PathVariable("id") Long id,
                            @RequestAttribute(value = "isAdmin", required = false) Integer isAdmin,
                            @RequestAttribute(value = "userId", required = false) Long userId) {
        if (isAdmin == null || isAdmin != 1) {
            return forbidden("只有管理员可以审核");
        }
        TeamApply apply = teamApplyService.getById(id);
        if (apply == null) {
            return ResultUtils.error("申请不存在");
        }
        if (apply.getStatus() != null && apply.getStatus() != 0) {
            return ResultUtils.error("该申请已处理");
        }

        QueryWrapper<Team> sameName = new QueryWrapper<>();
        sameName.lambda().eq(Team::getName, apply.getName());
        if (teamService.count(sameName) > 0) {
            return ResultUtils.error("已存在同名社团，请驳回后让申请人改名");
        }

        Team team = new Team();
        team.setName(apply.getName());
        team.setCategoryId(apply.getCategoryId());
        team.setHasTop("0");
        team.setCreateTime(new Date());
        if (!teamService.save(team)) {
            return ResultUtils.error("创建社团失败");
        }

        // 申请人直接成为社长，省得再手动加一次
        Member member = new Member();
        member.setTeamId(team.getId());
        member.setUserId(apply.getApplicantId());
        member.setRole(2);
        member.setStatus(1);
        member.setJoinTime(new Date());
        memberService.save(member);

        apply.setStatus(1);
        apply.setTeamId(team.getId());
        apply.setReviewId(userId);
        apply.setReviewTime(new Date());
        teamApplyService.updateById(apply);

        return ResultUtils.success("已通过，社团已创建");
    }

    /**
     * 驳回申请
     *
     * @param id      申请ID
     * @param body    { reason }
     * @param isAdmin 当前用户是否管理员
     * @param userId  审核人
     * @return 结果
     */
    @PutMapping("/{id}/reject")
    public ResultVo reject(@PathVariable("id") Long id,
                           @RequestBody(required = false) Map<String, Object> body,
                           @RequestAttribute(value = "isAdmin", required = false) Integer isAdmin,
                           @RequestAttribute(value = "userId", required = false) Long userId) {
        if (isAdmin == null || isAdmin != 1) {
            return forbidden("只有管理员可以审核");
        }
        TeamApply apply = teamApplyService.getById(id);
        if (apply == null) {
            return ResultUtils.error("申请不存在");
        }
        if (apply.getStatus() != null && apply.getStatus() != 0) {
            return ResultUtils.error("该申请已处理");
        }
        apply.setStatus(2);
        apply.setReviewId(userId);
        apply.setReviewTime(new Date());
        apply.setRejectReason(body == null || body.get("reason") == null
                ? null
                : String.valueOf(body.get("reason")));
        return teamApplyService.updateById(apply)
                ? ResultUtils.success("已驳回")
                : ResultUtils.error("操作失败");
    }
}
