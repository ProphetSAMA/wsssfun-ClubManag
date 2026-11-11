package fun.wsss.web.club;

import fun.wsss.config.jwt.JwtUtils;
import fun.wsss.web.member.entity.Member;
import fun.wsss.web.member.mapper.MemberMapper;
import fun.wsss.web.member.service.MemberService;
import fun.wsss.web.news.entity.News;
import fun.wsss.web.news.service.NewsService;
import fun.wsss.web.signup.entity.Signup;
import fun.wsss.web.signup.mapper.SignupMapper;
import fun.wsss.web.signup.service.SignupService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 活动签到：社团管理员给自己的活动核销，别人不能。
 */
@SpringBootTest
@AutoConfigureMockMvc
class ClubCheckinTest {

    private static final long MANAGER = 5L;
    private static final long OUTSIDER = 9L;
    private static final long TEAM_ID = 1L;
    private static final long NEWS_ID = 30L;
    private static final long SIGNUP_ID = 60L;

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
    private SignupService signupService;

    private String token(long userId) {
        return jwtUtils.generateToken(userId, "u" + userId, 0);
    }

    private void givenManager(long userId) {
        Member member = new Member();
        member.setUserId(userId);
        member.setTeamId(TEAM_ID);
        member.setRole(2);
        member.setStatus(1);
        given(memberService.getOne(any())).willReturn(member);
    }

    private void givenActivityAndSignup() {
        News news = new News();
        news.setId(NEWS_ID);
        news.setTeamId(TEAM_ID);
        given(newsService.getById(NEWS_ID)).willReturn(news);

        Signup signup = new Signup();
        signup.setId(SIGNUP_ID);
        signup.setNewsId(NEWS_ID);
        signup.setUserId(77L);
        signup.setStatus(1);
        signup.setCheckedIn(0);
        given(signupService.getById(SIGNUP_ID)).willReturn(signup);
        given(signupService.updateById(any(Signup.class))).willReturn(true);
    }

    private String url() {
        return "/api/club/" + TEAM_ID + "/news/" + NEWS_ID + "/signup/" + SIGNUP_ID + "/checkin";
    }

    @Test
    void managerCanCheckIn() throws Exception {
        givenManager(MANAGER);
        givenActivityAndSignup();

        mockMvc.perform(put(url())
                        .header("token", token(MANAGER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"checkedIn\":true}"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("已签到"));

        ArgumentCaptor<Signup> captor = ArgumentCaptor.forClass(Signup.class);
        verify(signupService).updateById(captor.capture());
        assertThat(captor.getValue().getCheckedIn()).isEqualTo(1);
        assertThat(captor.getValue().getCheckinTime()).isNotNull();
    }

    @Test
    void canToggleBackToUnchecked() throws Exception {
        givenManager(MANAGER);
        givenActivityAndSignup();

        mockMvc.perform(put(url())
                        .header("token", token(MANAGER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"checkedIn\":false}"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("已取消签到"));

        ArgumentCaptor<Signup> captor = ArgumentCaptor.forClass(Signup.class);
        verify(signupService).updateById(captor.capture());
        assertThat(captor.getValue().getCheckedIn()).isZero();
        assertThat(captor.getValue().getCheckinTime()).isNull();
    }

    @Test
    void outsiderCanNotCheckIn() throws Exception {
        given(memberService.getOne(any())).willReturn(null);
        givenActivityAndSignup();

        mockMvc.perform(put(url())
                        .header("token", token(OUTSIDER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"checkedIn\":true}"))
                .andExpect(jsonPath("$.code").value(403));

        verify(signupService, never()).updateById(any(Signup.class));
    }
}
