package fun.wsss.web.user;

import fun.wsss.config.jwt.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * token 续签：拿旧 token 换新 token，身份保持不变。
 */
@SpringBootTest
@AutoConfigureMockMvc
class TokenRefreshTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void anonymousCanNotRefresh() throws Exception {
        mockMvc.perform(post("/api/user/refresh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void refreshKeepsIdentity() throws Exception {
        String oldToken = jwtUtils.generateToken(5L, "stu", 0);
        // JWT 的 iat/exp 只精确到秒，同一秒内换签会得到完全一样的 token，等一下再换
        Thread.sleep(1100);

        String body = mockMvc.perform(post("/api/user/refresh").header("token", oldToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn().getResponse().getContentAsString();

        String newToken = body.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
        assertThat(newToken).isNotEqualTo(oldToken);
        assertThat(jwtUtils.getUserId(newToken)).isEqualTo(5L);
        assertThat(jwtUtils.getUsername(newToken)).isEqualTo("stu");
        assertThat(jwtUtils.getIsAdmin(newToken)).isZero();
        assertThat(jwtUtils.validateToken(newToken)).isTrue();
    }
}
