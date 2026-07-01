package fun.wsss.web.member.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import fun.wsss.web.member.entity.Member;
import fun.wsss.web.member.entity.MemberParm;
import fun.wsss.web.member.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 社团成员控制器
 *
 * @author Wsssfun
 */
@RestController
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 加入社团
     *
     * @param member 成员信息
     * @return 结果
     */
    @PostMapping
    public ResultVo add(@RequestBody Member member) {
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
     * @param member 成员信息
     * @return 结果
     */
    @PutMapping
    public ResultVo edit(@RequestBody Member member) {
        if (memberService.updateById(member)) {
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }

    /**
     * 退出社团（逻辑删除）
     *
     * @param id 成员ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
        Member member = memberService.getById(id);
        if (member == null) {
            return ResultUtils.error("成员不存在");
        }
        member.setStatus(0);
        if (memberService.updateById(member)) {
            return ResultUtils.success("退出成功");
        }
        return ResultUtils.error("退出失败");
    }

    /**
     * 查询成员列表
     *
     * @param parm 查询条件
     * @return 成员列表
     */
    @GetMapping("/list")
    public ResultVo list(MemberParm parm) {
        IPage<Member> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        QueryWrapper<Member> query = new QueryWrapper<>();
        if (parm.getTeamId() != null) {
            query.lambda().eq(Member::getTeamId, parm.getTeamId());
        }
        if (parm.getUserId() != null) {
            query.lambda().eq(Member::getUserId, parm.getUserId());
        }
        query.lambda().eq(Member::getStatus, 1).orderByDesc(Member::getJoinTime);
        IPage<Member> list = memberService.page(page, query);
        return ResultUtils.success("查询成功", list);
    }
}
