package fun.wsss.web.member.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.status.StatusCode;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.member.entity.Member;
import fun.wsss.web.member.entity.MemberParm;
import fun.wsss.web.member.entity.MemberVo;
import fun.wsss.web.member.mapper.MemberMapper;
import fun.wsss.web.member.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Objects;

/**
 * 社团成员控制器
 *
 * <p>权限约定：普通用户只能操作自己的成员记录，管理员可以管理任意记录。
 * 当前用户由 AuthInterceptor 解析 token 后放进 request 属性（userId / isAdmin）。
 *
 * @author Wsssfun
 */
@RestController
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;
    private final MemberMapper memberMapper;

    public MemberController(MemberService memberService, MemberMapper memberMapper) {
        this.memberService = memberService;
        this.memberMapper = memberMapper;
    }

    private static boolean isAdmin(Integer isAdmin) {
        return isAdmin != null && isAdmin == 1;
    }

    /**
     * 加入社团
     *
     * <p>普通用户的 userId 一律取自 token，忽略请求体里的值（防止替他人入团）；
     * 管理后台代成员加入时，管理员可以用请求体里的 userId。
     *
     * @param member        成员信息
     * @param currentUserId 当前登录用户（来自 token）
     * @param isAdmin       是否管理员（来自 token）
     * @return 结果
     */
    @PostMapping
    public ResultVo add(@RequestBody Member member,
                        @RequestAttribute(value = "userId", required = false) Long currentUserId,
                        @RequestAttribute(value = "isAdmin", required = false) Integer isAdmin) {
        if (!isAdmin(isAdmin)) {
            if (currentUserId == null) {
                return ResultUtils.error("未登录，请先登录", StatusCode.UNAUTHORIZED);
            }
            // 入团一律按普通成员处理，角色只能由管理员后续调整
            member.setUserId(currentUserId);
            member.setRole(0);
        }
        // 防止前端带 id 把"新增"变成"覆盖更新"
        member.setId(null);

        // 检查是否已经是成员
        QueryWrapper<Member> checkQuery = new QueryWrapper<>();
        checkQuery.lambda()
                .eq(Member::getTeamId, member.getTeamId())
                .eq(Member::getUserId, member.getUserId())
                .eq(Member::getStatus, 1);
        if (memberService.count(checkQuery) > 0) {
            return ResultUtils.error("该用户已是社团成员");
        }

        member.setJoinTime(new Date());
        member.setStatus(1);
        if (member.getRole() == null) {
            member.setRole(0);
        }
        if (memberService.save(member)) {
            return ResultUtils.success("加入成功");
        }
        return ResultUtils.error("加入失败");
    }

    /**
     * 编辑成员（修改角色）
     *
     * <p>角色变更属于管理操作，仅管理员可用。
     *
     * @param member  成员信息
     * @param isAdmin 是否管理员（来自 token）
     * @return 结果
     */
    @PutMapping
    public ResultVo edit(@RequestBody Member member,
                         @RequestAttribute(value = "isAdmin", required = false) Integer isAdmin) {
        if (!isAdmin(isAdmin)) {
            return ResultUtils.error("没有权限修改成员角色", StatusCode.FORBIDDEN);
        }
        if (memberService.updateById(member)) {
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }

    /**
     * 退出社团（逻辑删除）
     *
     * <p>普通用户只能退出自己加入的社团，管理员可以移除任意成员。
     *
     * @param id            成员ID
     * @param currentUserId 当前登录用户（来自 token）
     * @param isAdmin       是否管理员（来自 token）
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id,
                           @RequestAttribute(value = "userId", required = false) Long currentUserId,
                           @RequestAttribute(value = "isAdmin", required = false) Integer isAdmin) {
        Member member = memberService.getById(id);
        if (member == null) {
            return ResultUtils.error("成员不存在");
        }
        if (!isAdmin(isAdmin) && !Objects.equals(member.getUserId(), currentUserId)) {
            return ResultUtils.error("只能退出自己加入的社团", StatusCode.FORBIDDEN);
        }
        member.setStatus(0);
        if (memberService.updateById(member)) {
            return ResultUtils.success("退出成功");
        }
        return ResultUtils.error("退出失败");
    }

    /**
     * 查询成员列表（关联社团名称和用户姓名）
     *
     * <p>普通用户只能查自己的记录，避免用 userId 参数枚举他人。
     *
     * @param parm          查询条件
     * @param currentUserId 当前登录用户（来自 token）
     * @param isAdmin       是否管理员（来自 token）
     * @return 成员列表
     */
    @GetMapping("/list")
    public ResultVo list(MemberParm parm,
                         @RequestAttribute(value = "userId", required = false) Long currentUserId,
                         @RequestAttribute(value = "isAdmin", required = false) Integer isAdmin) {
        Long userId = isAdmin(isAdmin) ? parm.getUserId() : currentUserId;
        Page<MemberVo> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        IPage<MemberVo> list = memberMapper.selectMemberPage(page, parm.getTeamId(), userId, parm.getUserName());
        return ResultUtils.success("查询成功", list);
    }
}
