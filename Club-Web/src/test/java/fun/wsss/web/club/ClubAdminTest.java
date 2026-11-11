package fun.wsss.web.club;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.config.jwt.JwtUtils;
import fun.wsss.web.club.entity.ClubMemberVo;
import fun.wsss.web.member.entity.Member;
import fun.wsss.web.member.mapper.MemberMapper;
import fun.wsss.web.member.service.MemberService;
import fun.wsss.web.news.entity.News;
import fun.wsss.web.news.service.NewsService;
import fun.wsss.web.signup.mapper.SignupMapper;
import fun.wsss.web.team.entity.Team;
import fun.wsss.web.team.service.TeamService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 社团管理员权限：社长/社团管理员能管自己社团，普通成员和非本社团成员不能。
 */
@SpringBootTest
@AutoConfigureMockMvc
class ClubAdminTest {

    private static final long PRESIDENT = 5L;
    private static final long MANAGER = 6L;
    private static final long NORMAL = 7L;
    private static final long TEAM_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberMapper memberMapper;

    @MockBean
    private NewsService newsService;

    @MockBean
    private SignupMapper signupMapper;

    @MockBean
    private TeamService teamService;

    private String token(long userId) {
        return jwtUtils.generateToken(userId, "u" + userId, 0);
    }

    private Member membership(long userId, long teamId, int role) {
        Member member = new Member();
        member.setId(100 + userId);
        member.setUserId(userId);
        member.setTeamId(teamId);
        member.setRole(role);
        member.setStatus(1);
        return member;
    }

    private void givenRole(long userId, int role) {
        given(memberService.getOne(any())).willReturn(membership(userId, TEAM_ID, role));
    }

    @Test
    void anonymousCanNotUseClubApi() throws Exception {
        mockMvc.perform(get("/api/club/my"))
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void normalMemberCanNotListMembers() throws Exception {
        givenRole(NORMAL, 0);

        mockMvc.perform(get("/api/club/" + TEAM_ID + "/members").header("token", token(NORMAL)))
                .andExpect(jsonPath("$.code").value(403));

        verify(memberMapper, never()).selectClubMembers(any());
    }

    @Test
    void presidentCanListMembers() throws Exception {
        givenRole(PRESIDENT, 2);
        ClubMemberVo vo = new ClubMemberVo();
        vo.setUserId(7L);
        vo.setUserName("小明");
        vo.setRole(0);
        given(memberMapper.selectClubMembers(TEAM_ID)).willReturn(List.of(vo));

        mockMvc.perform(get("/api/club/" + TEAM_ID + "/members").header("token", token(PRESIDENT)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].userName").value("小明"));
    }

    @Test
    void presidentCanPromoteMember() throws Exception {
        givenRole(PRESIDENT, 2);
        Member target = membership(NORMAL, TEAM_ID, 0);
        given(memberService.getById(107L)).willReturn(target);
        given(memberService.updateById(any(Member.class))).willReturn(true);

        mockMvc.perform(put("/api/club/" + TEAM_ID + "/member/107")
                        .header("token", token(PRESIDENT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":1}"))
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberService).updateById(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo(1);
    }

    @Test
    void canNotChangeOwnRoleOrPresidentRole() throws Exception {
        givenRole(PRESIDENT, 2);
        given(memberService.getById(105L)).willReturn(membership(PRESIDENT, TEAM_ID, 2));

        mockMvc.perform(put("/api/club/" + TEAM_ID + "/member/105")
                        .header("token", token(PRESIDENT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":0}"))
                .andExpect(jsonPath("$.msg").value("不能修改自己的角色"));

        // 换个目标：同为社长
        given(memberService.getById(109L)).willReturn(membership(9L, TEAM_ID, 2));
        mockMvc.perform(put("/api/club/" + TEAM_ID + "/member/109")
                        .header("token", token(PRESIDENT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":1}"))
                .andExpect(jsonPath("$.msg").value("不能修改社长"));
    }

    @Test
    void canNotManageAnotherClub() throws Exception {
        // 我是社团 1 的管理员，却去操作社团 2（查询条件是 teamId=2，模拟查不到记录）
        given(memberService.getOne(any())).willReturn(null);

        mockMvc.perform(get("/api/club/2/members").header("token", token(PRESIDENT)))
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void clubNewsIsForcedToOwnTeam() throws Exception {
        givenRole(PRESIDENT, 2);
        given(newsService.save(any(News.class))).willReturn(true);

        mockMvc.perform(post("/api/club/" + TEAM_ID + "/news")
                        .header("token", token(PRESIDENT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"周末篮球赛\",\"teamId\":99,\"type\":\"1\","
                                + "\"details\":\"<p>hi</p>\",\"hasTop\":\"1\"}"))
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<News> captor = ArgumentCaptor.forClass(News.class);
        verify(newsService).save(captor.capture());
        News saved = captor.getValue();
        assertThat(saved.getTeamId()).isEqualTo(TEAM_ID);
        assertThat(saved.getType()).isEqualTo("0");
        assertThat(saved.getCreateId()).isEqualTo(PRESIDENT);
    }

    @Test
    void canNotEditAnotherClubsNews() throws Exception {
        givenRole(PRESIDENT, 2);
        News other = new News();
        other.setId(50L);
        other.setTeamId(2L);
        given(newsService.getById(50L)).willReturn(other);

        mockMvc.perform(put("/api/club/" + TEAM_ID + "/news/50")
                        .header("token", token(PRESIDENT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"改\"}"))
                .andExpect(jsonPath("$.msg").value("内容不存在"));

        verify(newsService, never()).updateById(any(News.class));
    }

    @Test
    void managerCanSeeSignupListOfOwnActivity() throws Exception {
        givenRole(MANAGER, 1);
        News activity = new News();
        activity.setId(30L);
        activity.setTeamId(TEAM_ID);
        given(newsService.getById(30L)).willReturn(activity);
        given(signupMapper.selectSignupsByNews(any(), eq(30L))).willReturn(new Page<>());

        mockMvc.perform(get("/api/club/" + TEAM_ID + "/news/30/signups").header("token", token(MANAGER)))
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void presidentCanUpdateClubProfile() throws Exception {
        givenRole(PRESIDENT, 2);
        Team team = new Team();
        team.setId(TEAM_ID);
        team.setName("吉他社");
        given(teamService.getById(TEAM_ID)).willReturn(team);
        given(teamService.updateById(any(Team.class))).willReturn(true);

        mockMvc.perform(put("/api/club/" + TEAM_ID)
                        .header("token", token(PRESIDENT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"details\":\"新的简介\",\"image\":\"/images/a.png\"}"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("社团资料已保存"));

        ArgumentCaptor<Team> captor = ArgumentCaptor.forClass(Team.class);
        verify(teamService).updateById(captor.capture());
        assertThat(captor.getValue().getDetails()).isEqualTo("新的简介");
        assertThat(captor.getValue().getImage()).isEqualTo("/images/a.png");
    }

    @Test
    void normalMemberCanNotUpdateClubProfile() throws Exception {
        givenRole(NORMAL, 0);

        mockMvc.perform(put("/api/club/" + TEAM_ID)
                        .header("token", token(NORMAL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"details\":\"x\"}"))
                .andExpect(jsonPath("$.code").value(403));

        verify(teamService, never()).updateById(any(Team.class));
    }

    @Test
    void presidentCanRemoveMember() throws Exception {
        givenRole(PRESIDENT, 2);
        given(memberService.getById(107L)).willReturn(membership(NORMAL, TEAM_ID, 0));
        given(memberService.updateById(any(Member.class))).willReturn(true);

        mockMvc.perform(delete("/api/club/" + TEAM_ID + "/member/107").header("token", token(PRESIDENT)))
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberService).updateById(captor.capture());
        assertThat(captor.getValue().getStatus()).isZero();
    }
}
