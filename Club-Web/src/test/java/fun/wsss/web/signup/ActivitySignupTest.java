package fun.wsss.web.signup;

import fun.wsss.config.jwt.JwtUtils;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 活动报名：只能给自己报名，名单只有管理员能看。
 */
@SpringBootTest
@AutoConfigureMockMvc
class ActivitySignupTest {

    private static final long USER_ID = 5L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private SignupService signupService;

    @MockBean
    private SignupMapper signupMapper;

    @MockBean
    private NewsService newsService;

    private String userToken() {
        return jwtUtils.generateToken(USER_ID, "stu", 0);
    }

    private String adminToken() {
        return jwtUtils.generateToken(1L, "admin", 1);
    }

    private News activity() {
        News news = new News();
        news.setId(1L);
        news.setTitle("春季篮球联赛");
        news.setType("0");
        news.setStatus(1);
        return news;
    }

    private Signup existing(int status) {
        Signup signup = new Signup();
        signup.setId(9L);
        signup.setNewsId(1L);
        signup.setUserId(USER_ID);
        signup.setStatus(status);
        return signup;
    }

    @Test
    void anonymousCanNotSignup() throws Exception {
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newsId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));

        verify(signupService, never()).save(any(Signup.class));
    }

    @Test
    void signupIsRecordedForCurrentUser() throws Exception {
        given(newsService.getById(1L)).willReturn(activity());
        given(signupService.getOne(any())).willReturn(null);
        given(signupService.save(any(Signup.class))).willReturn(true);

        // 请求体塞 userId 也不应该生效
        mockMvc.perform(post("/api/signup")
                        .header("token", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newsId\":1,\"userId\":99}"))
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<Signup> captor = ArgumentCaptor.forClass(Signup.class);
        verify(signupService).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(USER_ID);
        assertThat(captor.getValue().getNewsId()).isEqualTo(1L);
        assertThat(captor.getValue().getStatus()).isEqualTo(1);
    }

    @Test
    void canNotSignupForNonActivityContent() throws Exception {
        News news = activity();
        news.setType("1");
        given(newsService.getById(1L)).willReturn(news);

        mockMvc.perform(post("/api/signup")
                        .header("token", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newsId\":1}"))
                .andExpect(jsonPath("$.msg").value("活动不存在"));

        verify(signupService, never()).save(any(Signup.class));
    }

    @Test
    void duplicateSignupIsRejected() throws Exception {
        given(newsService.getById(1L)).willReturn(activity());
        given(signupService.getOne(any())).willReturn(existing(1));

        mockMvc.perform(post("/api/signup")
                        .header("token", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newsId\":1}"))
                .andExpect(jsonPath("$.msg").value("已经报过名了"));

        verify(signupService, never()).save(any(Signup.class));
    }

    @Test
    void canSignupAgainAfterCancel() throws Exception {
        given(newsService.getById(1L)).willReturn(activity());
        given(signupService.getOne(any())).willReturn(existing(0));
        given(signupService.updateById(any(Signup.class))).willReturn(true);

        mockMvc.perform(post("/api/signup")
                        .header("token", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newsId\":1}"))
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<Signup> captor = ArgumentCaptor.forClass(Signup.class);
        verify(signupService).updateById(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(1);
        assertThat(captor.getValue().getId()).isEqualTo(9L);
    }

    @Test
    void cancelMarksOwnSignupAsCancelled() throws Exception {
        given(signupService.getOne(any())).willReturn(existing(1));
        given(signupService.updateById(any(Signup.class))).willReturn(true);

        mockMvc.perform(delete("/api/signup/1").header("token", userToken()))
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<Signup> captor = ArgumentCaptor.forClass(Signup.class);
        verify(signupService).updateById(captor.capture());
        assertThat(captor.getValue().getStatus()).isZero();
    }

    @Test
    void mySignupsUseCurrentUser() throws Exception {
        given(signupMapper.selectMySignups(any(), any())).willReturn(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>());

        mockMvc.perform(get("/api/signup/my").header("token", userToken()))
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(signupMapper).selectMySignups(any(), captor.capture());
        assertThat(captor.getValue()).isEqualTo(USER_ID);
    }

    @Test
    void onlyAdminCanSeeSignupList() throws Exception {
        mockMvc.perform(get("/api/signup/list").param("newsId", "1").header("token", userToken()))
                .andExpect(jsonPath("$.code").value(403));

        given(signupMapper.selectSignupsByNews(any(), any())).willReturn(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>());
        mockMvc.perform(get("/api/signup/list").param("newsId", "1").header("token", adminToken()))
                .andExpect(jsonPath("$.code").value(200));
    }
}
