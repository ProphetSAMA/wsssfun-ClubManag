package fun.wsss.web.club.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.status.StatusCode;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.club.service.ClubAuthService;
import fun.wsss.web.member.entity.Member;
import fun.wsss.web.member.mapper.MemberMapper;
import fun.wsss.web.member.service.MemberService;
import fun.wsss.web.news.entity.News;
import fun.wsss.web.news.service.NewsService;
import fun.wsss.web.signup.entity.Signup;
import fun.wsss.web.signup.entity.SignupVo;
import fun.wsss.web.signup.mapper.SignupMapper;
import fun.wsss.web.signup.service.SignupService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 社团管理员接口
 *
 * <p>社长（role=2）与社团管理员（role=1）管理自己社团：成员、社团自建活动、活动报名名单。
 * 权限不依赖平台的管理员标识，而是按 member 表里的归属判断，所以普通学生只要当上社长就能用。
 *
 * @author Wsssfun
 */
@RestController
@RequestMapping("/api/club")
public class ClubController {

    private final ClubAuthService clubAuthService;
    private final MemberService memberService;
    private final MemberMapper memberMapper;
    private final NewsService newsService;
    private final SignupMapper signupMapper;
    private final SignupService signupService;

    public ClubController(ClubAuthService clubAuthService,
                          MemberService memberService,
                          MemberMapper memberMapper,
                          NewsService newsService,
                          SignupMapper signupMapper,
                          SignupService signupService) {
        this.clubAuthService = clubAuthService;
        this.memberService = memberService;
        this.memberMapper = memberMapper;
        this.newsService = newsService;
        this.signupMapper = signupMapper;
        this.signupService = signupService;
    }

    private static ResultVo forbidden(String msg) {
        return ResultUtils.error(msg, StatusCode.FORBIDDEN);
    }

    private ResultVo checkLogin(Long userId) {
        return userId == null ? ResultUtils.error("未登录，请先登录", StatusCode.UNAUTHORIZED) : null;
    }

    /**
     * 我管理的社团（社长 / 社团管理员）
     *
     * @param userId 当前用户
     * @return 社团列表
     */
    @GetMapping("/my")
    public ResultVo my(@RequestAttribute(value = "userId", required = false) Long userId) {
        ResultVo unauthorized = checkLogin(userId);
        if (unauthorized != null) {
            return unauthorized;
        }
        return ResultUtils.success("查询成功", clubAuthService.managedTeams(userId));
    }

    /**
     * 社团成员列表
     *
     * @param teamId 社团ID
     * @param userId 当前用户
     * @return 成员列表
     */
    @GetMapping("/{teamId}/members")
    public ResultVo members(@PathVariable("teamId") Long teamId,
                            @RequestAttribute(value = "userId", required = false) Long userId) {
        ResultVo unauthorized = checkLogin(userId);
        if (unauthorized != null) {
            return unauthorized;
        }
        if (!clubAuthService.isManager(userId, teamId)) {
            return forbidden("没有该社团的管理权限");
        }
        return ResultUtils.success("查询成功", memberMapper.selectClubMembers(teamId));
    }

    /**
     * 设置成员角色（仅普通成员与管理员之间切换，社长不能在这里改）
     *
     * @param teamId 社团ID
     * @param id     member 主键
     * @param body   { "role": 0|1 }
     * @param userId 当前用户
     * @return 结果
     */
    @PutMapping("/{teamId}/member/{id}")
    public ResultVo updateMemberRole(@PathVariable("teamId") Long teamId,
                                     @PathVariable("id") Long id,
                                     @RequestBody Map<String, Integer> body,
                                     @RequestAttribute(value = "userId", required = false) Long userId) {
        ResultVo unauthorized = checkLogin(userId);
        if (unauthorized != null) {
            return unauthorized;
        }
        if (!clubAuthService.isManager(userId, teamId)) {
            return forbidden("没有该社团的管理权限");
        }
        Integer role = body.get("role");
        if (role == null || role < 0 || role > 1) {
            return ResultUtils.error("只能设置为普通成员或社团管理员");
        }
        Member target = memberService.getById(id);
        if (target == null || !Objects.equals(target.getTeamId(), teamId)
                || target.getStatus() == null || target.getStatus() != 1) {
            return ResultUtils.error("成员不存在");
        }
        if (Objects.equals(target.getUserId(), userId)) {
            return ResultUtils.error("不能修改自己的角色");
        }
        if (Objects.equals(target.getRole(), 2)) {
            return ResultUtils.error("不能修改社长");
        }
        target.setRole(role);
        return memberService.updateById(target)
                ? ResultUtils.success("设置成功")
                : ResultUtils.error("设置失败");
    }

    /**
     * 移出成员（逻辑删除，记录保留）
     *
     * @param teamId 社团ID
     * @param id     member 主键
     * @param userId 当前用户
     * @return 结果
     */
    @DeleteMapping("/{teamId}/member/{id}")
    public ResultVo removeMember(@PathVariable("teamId") Long teamId,
                                 @PathVariable("id") Long id,
                                 @RequestAttribute(value = "userId", required = false) Long userId) {
        ResultVo unauthorized = checkLogin(userId);
        if (unauthorized != null) {
            return unauthorized;
        }
        if (!clubAuthService.isManager(userId, teamId)) {
            return forbidden("没有该社团的管理权限");
        }
        Member target = memberService.getById(id);
        if (target == null || !Objects.equals(target.getTeamId(), teamId)
                || target.getStatus() == null || target.getStatus() != 1) {
            return ResultUtils.error("成员不存在");
        }
        if (Objects.equals(target.getUserId(), userId)) {
            return ResultUtils.error("不能移除自己");
        }
        if (Objects.equals(target.getRole(), 2)) {
            return ResultUtils.error("不能移除社长");
        }
        target.setStatus(0);
        return memberService.updateById(target)
                ? ResultUtils.success("已移出社团")
                : ResultUtils.error("操作失败");
    }

    /**
     * 本社团的活动列表（含草稿）
     *
     * @param teamId      社团ID
     * @param currentPage 页码
     * @param pageSize    每页条数
     * @param userId      当前用户
     * @return 分页结果
     */
    @GetMapping("/{teamId}/news")
    public ResultVo news(@PathVariable("teamId") Long teamId,
                         @RequestParam(defaultValue = "1") Integer currentPage,
                         @RequestParam(defaultValue = "10") Integer pageSize,
                         @RequestAttribute(value = "userId", required = false) Long userId) {
        ResultVo unauthorized = checkLogin(userId);
        if (unauthorized != null) {
            return unauthorized;
        }
        if (!clubAuthService.isManager(userId, teamId)) {
            return forbidden("没有该社团的管理权限");
        }
        Page<News> page = new Page<>(currentPage, pageSize);
        QueryWrapper<News> query = new QueryWrapper<>();
        query.lambda().eq(News::getTeamId, teamId).orderByDesc(News::getCreateTime);
        return ResultUtils.success("查询成功", newsService.page(page, query));
    }

    /**
     * 发布社团活动
     *
     * <p>类型固定为活动（0），归属强制为当前社团，发布人记为当前用户。
     *
     * @param teamId 社团ID
     * @param news   内容
     * @param userId 当前用户
     * @return 结果
     */
    @PostMapping("/{teamId}/news")
    public ResultVo addNews(@PathVariable("teamId") Long teamId,
                            @RequestBody News news,
                            @RequestAttribute(value = "userId", required = false) Long userId) {
        ResultVo unauthorized = checkLogin(userId);
        if (unauthorized != null) {
            return unauthorized;
        }
        if (!clubAuthService.isManager(userId, teamId)) {
            return forbidden("没有该社团的管理权限");
        }
        if (news.getTitle() == null || news.getTitle().isBlank()) {
            return ResultUtils.error("标题不能为空");
        }
        news.setId(null);
        news.setTeamId(teamId);
        news.setType("0");
        news.setCreateId(userId);
        news.setCreateTime(new Date());
        news.setViewCount(0);
        if (news.getHasTop() == null) {
            news.setHasTop("0");
        }
        if (news.getStatus() == null) {
            news.setStatus(1);
        }
        if (news.getMaxParticipants() == null || news.getMaxParticipants() < 0) {
            news.setMaxParticipants(0);
        }
        return newsService.save(news)
                ? ResultUtils.success("发布成功")
                : ResultUtils.error("发布失败");
    }

    /**
     * 编辑社团活动（只能改本社团的内容）
     *
     * @param teamId 社团ID
     * @param id     内容ID
     * @param news   修改内容
     * @param userId 当前用户
     * @return 结果
     */
    @PutMapping("/{teamId}/news/{id}")
    public ResultVo editNews(@PathVariable("teamId") Long teamId,
                             @PathVariable("id") Long id,
                             @RequestBody News news,
                             @RequestAttribute(value = "userId", required = false) Long userId) {
        ResultVo unauthorized = checkLogin(userId);
        if (unauthorized != null) {
            return unauthorized;
        }
        if (!clubAuthService.isManager(userId, teamId)) {
            return forbidden("没有该社团的管理权限");
        }
        News exist = newsService.getById(id);
        if (exist == null || !Objects.equals(exist.getTeamId(), teamId)) {
            return ResultUtils.error("内容不存在");
        }
        exist.setTitle(news.getTitle());
        exist.setImage(news.getImage());
        exist.setDetails(news.getDetails());
        if (news.getStatus() != null) {
            exist.setStatus(news.getStatus());
        }
        if (news.getHasTop() != null) {
            exist.setHasTop(news.getHasTop());
        }
        if (news.getMaxParticipants() != null && news.getMaxParticipants() >= 0) {
            exist.setMaxParticipants(news.getMaxParticipants());
        }
        return newsService.updateById(exist)
                ? ResultUtils.success("保存成功")
                : ResultUtils.error("保存失败");
    }

    /**
     * 删除社团活动
     *
     * @param teamId 社团ID
     * @param id     内容ID
     * @param userId 当前用户
     * @return 结果
     */
    @DeleteMapping("/{teamId}/news/{id}")
    public ResultVo deleteNews(@PathVariable("teamId") Long teamId,
                               @PathVariable("id") Long id,
                               @RequestAttribute(value = "userId", required = false) Long userId) {
        ResultVo unauthorized = checkLogin(userId);
        if (unauthorized != null) {
            return unauthorized;
        }
        if (!clubAuthService.isManager(userId, teamId)) {
            return forbidden("没有该社团的管理权限");
        }
        News exist = newsService.getById(id);
        if (exist == null || !Objects.equals(exist.getTeamId(), teamId)) {
            return ResultUtils.error("内容不存在");
        }
        return newsService.removeById(id)
                ? ResultUtils.success("删除成功")
                : ResultUtils.error("删除失败");
    }

    /**
     * 活动报名名单
     *
     * @param teamId 社团ID
     * @param newsId 活动ID
     * @param userId 当前用户
     * @return 名单
     */
    @GetMapping("/{teamId}/news/{newsId}/signups")
    public ResultVo signups(@PathVariable("teamId") Long teamId,
                            @PathVariable("newsId") Long newsId,
                            @RequestAttribute(value = "userId", required = false) Long userId) {
        ResultVo unauthorized = checkLogin(userId);
        if (unauthorized != null) {
            return unauthorized;
        }
        if (!clubAuthService.isManager(userId, teamId)) {
            return forbidden("没有该社团的管理权限");
        }
        News news = newsService.getById(newsId);
        if (news == null || !Objects.equals(news.getTeamId(), teamId)) {
            return ResultUtils.error("活动不存在");
        }
        List<SignupVo> list = signupMapper.selectSignupsByNews(new Page<>(1, 500), newsId).getRecords();
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("signed", list.size());
        data.put("maxParticipants", news.getMaxParticipants() == null ? 0 : news.getMaxParticipants());
        return ResultUtils.success("查询成功", data);
    }

    /**
     * 核销签到
     *
     * <p>社团管理员在报名名单里给到场的人打勾；重复点击就是改回未签到。
     *
     * @param teamId   社团ID
     * @param newsId   活动ID
     * @param signupId 报名记录ID
     * @param body     { checkedIn: true/false }
     * @param userId   当前用户
     * @return 结果
     */
    @PutMapping("/{teamId}/news/{newsId}/signup/{signupId}/checkin")
    public ResultVo checkin(@PathVariable("teamId") Long teamId,
                            @PathVariable("newsId") Long newsId,
                            @PathVariable("signupId") Long signupId,
                            @RequestBody(required = false) Map<String, Boolean> body,
                            @RequestAttribute(value = "userId", required = false) Long userId) {
        ResultVo unauthorized = checkLogin(userId);
        if (unauthorized != null) {
            return unauthorized;
        }
        if (!clubAuthService.isManager(userId, teamId)) {
            return forbidden("没有该社团的管理权限");
        }
        News news = newsService.getById(newsId);
        if (news == null || !Objects.equals(news.getTeamId(), teamId)) {
            return ResultUtils.error("活动不存在");
        }
        Signup signup = signupService.getById(signupId);
        if (signup == null || !Objects.equals(signup.getNewsId(), newsId)
                || signup.getStatus() == null || signup.getStatus() != 1) {
            return ResultUtils.error("报名记录不存在");
        }
        boolean checked = body != null && Boolean.TRUE.equals(body.get("checkedIn"));
        signup.setCheckedIn(checked ? 1 : 0);
        signup.setCheckinTime(checked ? new Date() : null);
        return signupService.updateById(signup)
                ? ResultUtils.success(checked ? "已签到" : "已取消签到")
                : ResultUtils.error("操作失败");
    }
}
