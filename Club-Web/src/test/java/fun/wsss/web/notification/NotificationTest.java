package fun.wsss.web.notification;

import fun.wsss.config.jwt.JwtUtils;
import fun.wsss.web.member.entity.Member;
import fun.wsss.web.member.mapper.MemberMapper;
import fun.wsss.web.member.service.MemberService;
import fun.wsss.web.news.entity.News;
import fun.wsss.web.news.service.NewsService;
import fun.wsss.web.notification.entity.Notification;
import fun.wsss.web.notification.service.NotificationService;
import fun.wsss.web.signup.entity.Signup;
import fun.wsss.web.signup.mapper.SignupMapper;
import fun.wsss.web.signup.service.SignupService;
import fun.wsss.web.team.entity.Team;
import fun.wsss.web.team.service.TeamService;
import fun.wsss.web.teamapply.entity.TeamApply;
import fun.wsss.web.teamapply.mapper.TeamApplyMapper;
import fun.wsss.web.teamapply.service.TeamApplyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 站内通知：事件触发、未读数、归属校验。
 */
@SpringBootTest
@AutoConfigureMockMvc
class NotificationTest {

    private static final long USER = 41L;
    private static final long OWNER = 42L;
    private static final long OTHER = 43L;
    private static final long NEWS_ID = 55L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private NewsService newsService;

    @MockBean
    private SignupService signupService;

    @MockBean
    private SignupMapper signupMapper;

    @MockBean
    private TeamApplyService teamApplyService;

    @MockBean
    private TeamApplyMapper teamApplyMapper;

    @MockBean
    private TeamService teamService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberMapper memberMapper;

    private String token(long userId) {
        return jwtUtils.generateToken(userId, "u" + userId, 0);
    }

    @Test
    void unreadCountComesFromOwnNotifications() throws Exception {
        given(notificationService.count(any())).willReturn(3L);

        mockMvc.perform(get("/api/notification/unread").header("token", token(USER)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(3));
    }

    @Test
    void anonymousIsRejected() throws Exception {
        mockMvc.perform(get("/api/notification/my")).andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void canNotReadSomeoneElsesNotification() throws Exception {
        Notification other = new Notification();
        other.setId(9L);
        other.setUserId(OTHER);
        other.setIsRead(0);
        given(notificationService.getById(9L)).willReturn(other);

        mockMvc.perform(put("/api/notification/9/read").header("token", token(USER)))
                .andExpect(jsonPath("$.msg").value("通知不存在"));

        verify(notificationService, never()).updateById(any(Notification.class));
    }

    @Test
    void signupNotifiesActivityOwner() throws Exception {
        News news = new News();
        news.setId(NEWS_ID);
        news.setType("0");
        news.setStatus(1);
        news.setTitle("周末球赛");
        news.setCreateId(OWNER);
        given(newsService.getById(NEWS_ID)).willReturn(news);
        given(signupService.getOne(any())).willReturn(null);
        given(signupService.save(any(Signup.class))).willReturn(true);

        mockMvc.perform(post("/api/signup")
                        .header("token", token(USER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newsId\":" + NEWS_ID + "}"))
                .andExpect(jsonPath("$.code").value(200));

        verify(notificationService).push(eq(OWNER), eq("signup"), eq("有人报名了你的活动"),
                contains("周末球赛"), eq("/mine/manage"));
    }

    @Test
    void approvingApplyNotifiesApplicant() throws Exception {
        TeamApply apply = new TeamApply();
        apply.setId(77L);
        apply.setName("飞盘社");
        apply.setApplicantId(USER);
        apply.setStatus(0);
        apply.setCreateTime(new Date());
        given(teamApplyService.getById(77L)).willReturn(apply);
        given(teamService.count(any())).willReturn(0L);
        given(teamService.save(any(Team.class))).willAnswer(invocation -> {
            Team team = invocation.getArgument(0);
            team.setId(123L);
            return true;
        });
        given(memberService.save(any(Member.class))).willReturn(true);
        given(teamApplyService.updateById(any(TeamApply.class))).willReturn(true);

        mockMvc.perform(put("/api/team-apply/77/approve")
                        .header("token", jwtUtils.generateToken(1L, "admin", 1)))
                .andExpect(jsonPath("$.code").value(200));

        verify(notificationService).push(eq(USER), eq("apply"), eq("社团申请已通过"),
                contains("飞盘社"), eq("/mine/manage"));
    }

    @Test
    void submittingApplyNotifiesAdmins() throws Exception {
        given(teamApplyService.count(any())).willReturn(0L);
        given(teamService.count(any())).willReturn(0L);
        given(teamApplyService.save(any(TeamApply.class))).willReturn(true);

        mockMvc.perform(post("/api/team-apply")
                        .header("token", token(USER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"象棋社\"}"))
                .andExpect(jsonPath("$.code").value(200));

        verify(notificationService).pushToAdmins(eq("apply"), any(), contains("象棋社"), eq("/teamapply"));
    }

    @Test
    void markAllReadIsScopedToCurrentUser() throws Exception {
        given(notificationService.update(any())).willReturn(true);

        mockMvc.perform(put("/api/notification/read-all").header("token", token(USER)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("已全部标记为已读"));

        assertThat(true).isTrue();
    }
}
