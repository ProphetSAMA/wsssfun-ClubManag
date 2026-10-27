package fun.wsss.web.club.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fun.wsss.web.club.entity.ClubTeamVo;
import fun.wsss.web.member.entity.Member;
import fun.wsss.web.member.mapper.MemberMapper;
import fun.wsss.web.member.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 社团管理员权限判断
 *
 * <p>member.role：2-社长、1-社团管理员、0-普通成员。
 * 这里只判断"是否某个社团的管理者"，平台管理员那套权限仍由 AdminInterceptor 负责。
 *
 * @author Wsssfun
 */
@Service
public class ClubAuthService {

    private final MemberService memberService;
    private final MemberMapper memberMapper;

    public ClubAuthService(MemberService memberService, MemberMapper memberMapper) {
        this.memberService = memberService;
        this.memberMapper = memberMapper;
    }

    /**
     * 我管理的社团
     *
     * @param userId 当前用户
     * @return 社团列表（可能为空）
     */
    public List<ClubTeamVo> managedTeams(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return memberMapper.selectManagedTeams(userId);
    }

    /**
     * 我在这个社团里的角色
     *
     * @param userId 当前用户
     * @param teamId 社团ID
     * @return 0/1/2，不是成员返回 null
     */
    public Integer roleIn(Long userId, Long teamId) {
        if (userId == null || teamId == null) {
            return null;
        }
        QueryWrapper<Member> query = new QueryWrapper<>();
        query.lambda()
                .eq(Member::getUserId, userId)
                .eq(Member::getTeamId, teamId)
                .eq(Member::getStatus, 1);
        Member member = memberService.getOne(query);
        return member == null ? null : member.getRole();
    }

    /**
     * 是否是某个社团的管理者（社长或社团管理员）
     *
     * @param userId 当前用户
     * @param teamId 社团ID
     * @return 是否可管理
     */
    public boolean isManager(Long userId, Long teamId) {
        Integer role = roleIn(userId, teamId);
        return role != null && role >= 1;
    }

    /**
     * 是否是某个社团的社长
     */
    public boolean isPresident(Long userId, Long teamId) {
        return Objects.equals(2, roleIn(userId, teamId));
    }
}
