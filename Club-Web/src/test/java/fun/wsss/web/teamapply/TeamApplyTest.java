package fun.wsss.web.teamapply;

import fun.wsss.config.jwt.JwtUtils;
import fun.wsss.web.member.entity.Member;
import fun.wsss.web.member.service.MemberService;
import fun.wsss.web.team.entity.Team;
import fun.wsss.web.team.service.TeamService;
import fun.wsss.web.teamapply.entity.TeamApply;
import fun.wsss.web.teamapply.mapper.TeamApplyMapper;
import fun.wsss.web.teamapply.service.TeamApplyService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 社团成立申请：学生提交、管理员审核、通过后自动建社团并把人设为社长。
 */
@SpringBootTest
@AutoConfigureMockMvc
class TeamApplyTest {

    private static final long APPLICANT = 20L;
    private static final long ADMIN = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private TeamApplyService teamApplyService;

    @MockBean
    private TeamApplyMapper teamApplyMapper;

    @MockBean
    private TeamService teamService;

    @MockBean
    private MemberService memberService;

    private String token(long userId, int isAdmin) {
        return jwtUtils.generateToken(userId, "u" + userId, isAdmin);
    }

    @Test
    void anonymousCanNotSubmit() throws Exception {
        mockMvc.perform(post("/api/team-apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"飞盘社\"}"))
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void applicantIsTakenFromTokenNotFromBody() throws Exception {
        given(teamApplyService.count(any())).willReturn(0L);
        given(teamService.count(any())).willReturn(0L);
        given(teamApplyService.save(any(TeamApply.class))).willReturn(true);

        mockMvc.perform(post("/api/team-apply")
                        .header("token", token(APPLICANT, 0))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"飞盘社\",\"categoryId\":1,\"reason\":\"想玩飞盘\",\"applicantId\":999}"))
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<TeamApply> captor = ArgumentCaptor.forClass(TeamApply.class);
        verify(teamApplyService).save(captor.capture());
        assertThat(captor.getValue().getApplicantId()).isEqualTo(APPLICANT);
        assertThat(captor.getValue().getStatus()).isZero();
        assertThat(captor.getValue().getName()).isEqualTo("飞盘社");
    }

    @Test
    void canNotSubmitTwiceWhilePending() throws Exception {
        given(teamApplyService.count(any())).willReturn(1L);

        mockMvc.perform(post("/api/team-apply")
                        .header("token", token(APPLICANT, 0))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"飞盘社\"}"))
                .andExpect(jsonPath("$.msg").value("你还有待审核的申请，请等待处理"));

        verify(teamApplyService, never()).save(any(TeamApply.class));
    }

    @Test
    void canNotUseExistingTeamName() throws Exception {
        given(teamApplyService.count(any())).willReturn(0L);
        given(teamService.count(any())).willReturn(1L);

        mockMvc.perform(post("/api/team-apply")
                        .header("token", token(APPLICANT, 0))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"吉他社\"}"))
                .andExpect(jsonPath("$.msg").value("已存在同名社团"));
    }

    @Test
    void normalUserCanNotListApplications() throws Exception {
        mockMvc.perform(get("/api/team-apply/list").header("token", token(APPLICANT, 0)))
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void approveCreatesTeamAndMakesApplicantPresident() throws Exception {
        TeamApply apply = new TeamApply();
        apply.setId(7L);
        apply.setName("飞盘社");
        apply.setCategoryId(1L);
        apply.setApplicantId(APPLICANT);
        apply.setStatus(0);
        apply.setCreateTime(new Date());
        given(teamApplyService.getById(7L)).willReturn(apply);
        given(teamService.count(any())).willReturn(0L);
        given(teamService.save(any(Team.class))).willAnswer(invocation -> {
            Team team = invocation.getArgument(0);
            team.setId(88L);
            return true;
        });
        given(memberService.save(any(Member.class))).willReturn(true);
        given(teamApplyService.updateById(any(TeamApply.class))).willReturn(true);

        mockMvc.perform(put("/api/team-apply/7/approve").header("token", token(ADMIN, 1)))
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<Team> teamCaptor = ArgumentCaptor.forClass(Team.class);
        verify(teamService).save(teamCaptor.capture());
        assertThat(teamCaptor.getValue().getName()).isEqualTo("飞盘社");

        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberService).save(memberCaptor.capture());
        assertThat(memberCaptor.getValue().getUserId()).isEqualTo(APPLICANT);
        assertThat(memberCaptor.getValue().getRole()).isEqualTo(2);
        assertThat(memberCaptor.getValue().getTeamId()).isEqualTo(88L);

        assertThat(apply.getStatus()).isEqualTo(1);
        assertThat(apply.getTeamId()).isEqualTo(88L);
        assertThat(apply.getReviewId()).isEqualTo(ADMIN);
    }

    @Test
    void canNotApproveTwice() throws Exception {
        TeamApply apply = new TeamApply();
        apply.setId(8L);
        apply.setStatus(1);
        given(teamApplyService.getById(8L)).willReturn(apply);

        mockMvc.perform(put("/api/team-apply/8/approve").header("token", token(ADMIN, 1)))
                .andExpect(jsonPath("$.msg").value("该申请已处理"));

        verify(teamService, never()).save(any(Team.class));
    }

    @Test
    void rejectStoresReason() throws Exception {
        TeamApply apply = new TeamApply();
        apply.setId(9L);
        apply.setStatus(0);
        given(teamApplyService.getById(9L)).willReturn(apply);
        given(teamApplyService.updateById(any(TeamApply.class))).willReturn(true);

        mockMvc.perform(put("/api/team-apply/9/reject")
                        .header("token", token(ADMIN, 1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"社团数量已满\"}"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("已驳回"));

        assertThat(apply.getStatus()).isEqualTo(2);
        assertThat(apply.getRejectReason()).isEqualTo("社团数量已满");
    }

    @Test
    void normalUserCanNotApprove() throws Exception {
        mockMvc.perform(put("/api/team-apply/7/approve").header("token", token(APPLICANT, 0)))
                .andExpect(jsonPath("$.code").value(403));

        verify(teamApplyService, never()).getById(any());
    }
}
