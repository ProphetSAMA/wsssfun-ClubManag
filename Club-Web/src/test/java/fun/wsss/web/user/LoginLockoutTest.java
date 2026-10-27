package fun.wsss.web.user;

import fun.wsss.config.security.LoginAttemptService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 连续登录失败会被锁定，锁定后即使密码正确也不放行。
 */
@SpringBootTest
@AutoConfigureMockMvc
class LoginLockoutTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @MockBean
    private fun.wsss.web.user.service.UserService userService;

    private void failLogin(String username) throws Exception {
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"wrong\"}"))
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void accountIsLockedAfterRepeatedFailures() throws Exception {
        String username = "lock-candidate";
        given(userService.getOne(any())).willReturn(null);

        for (int i = 0; i < 5; i++) {
            failLogin(username);
        }

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"wrong\"}"))
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("登录失败次数过多")));
    }
}
