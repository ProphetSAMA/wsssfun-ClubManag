package fun.wsss.config.interceptor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.web.ClubWebApplication;
import fun.wsss.web.news.service.NewsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 限流：登录接口每分钟 3 次（本用例通过 properties 调小），超出返回 429。
 */
@SpringBootTest(classes = ClubWebApplication.class, properties = {
        "security.rate-limit.enabled=true",
        "security.rate-limit.auth-per-minute=3",
        "security.rate-limit.read-per-minute=1000"
})
@AutoConfigureMockMvc
class RateLimitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsService newsService;

    private void login() throws Exception {
        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"nobody\",\"password\":\"x\"}"));
    }

    @Test
    void authEndpointIsThrottled() throws Exception {
        login();
        login();
        login();

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nobody\",\"password\":\"x\"}"))
                .andExpect(jsonPath("$.code").value(429))
                .andExpect(jsonPath("$.msg").value("请求过于频繁，请稍后再试"));
    }

    @Test
    void readEndpointStillWorks() throws Exception {
        given(newsService.page(any(), any())).willReturn(new Page<>());
        mockMvc.perform(get("/api/news/list")).andExpect(jsonPath("$.code").value(200));
    }
}
