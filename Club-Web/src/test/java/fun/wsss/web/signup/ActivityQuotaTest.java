package fun.wsss.web.signup;

import fun.wsss.config.jwt.JwtUtils;
import fun.wsss.web.news.entity.News;
import fun.wsss.web.news.service.NewsService;
import fun.wsss.web.signup.entity.Signup;
import fun.wsss.web.signup.mapper.SignupMapper;
import fun.wsss.web.signup.service.SignupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 活动名额：满员后不能再报名，0 表示不限。
 */
@SpringBootTest
@AutoConfigureMockMvc
class ActivityQuotaTest {

    private static final long USER_ID = 30L;
    private static final long NEWS_ID = 40L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private NewsService newsService;

    @MockBean
    private SignupService signupService;

    @MockBean
    private SignupMapper signupMapper;

    private String token() {
        return jwtUtils.generateToken(USER_ID, "quota-user", 0);
    }

    private void givenActivity(int max) {
        News news = new News();
        news.setId(NEWS_ID);
        news.setType("0");
        news.setStatus(1);
        news.setMaxParticipants(max);
        given(newsService.getById(NEWS_ID)).willReturn(news);
        given(signupService.getOne(any())).willReturn(null);
    }

    @Test
    void rejectWhenFull() throws Exception {
        givenActivity(2);
        given(signupService.count(any())).willReturn(2L);

        mockMvc.perform(post("/api/signup")
                        .header("token", token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newsId\":" + NEWS_ID + "}"))
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("名额已满（2/2）"));

        verify(signupService, never()).save(any(Signup.class));
    }

    @Test
    void allowWhenSeatsLeft() throws Exception {
        givenActivity(5);
        given(signupService.count(any())).willReturn(3L);
        given(signupService.save(any(Signup.class))).willReturn(true);

        mockMvc.perform(post("/api/signup")
                        .header("token", token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newsId\":" + NEWS_ID + "}"))
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void zeroMeansUnlimited() throws Exception {
        givenActivity(0);
        given(signupService.count(any())).willReturn(999L);
        given(signupService.save(any(Signup.class))).willReturn(true);

        mockMvc.perform(post("/api/signup")
                        .header("token", token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newsId\":" + NEWS_ID + "}"))
                .andExpect(jsonPath("$.code").value(200));
    }
}
