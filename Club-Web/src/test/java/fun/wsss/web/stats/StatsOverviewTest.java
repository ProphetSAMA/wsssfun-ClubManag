package fun.wsss.web.stats;

import fun.wsss.config.jwt.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 看板统计：只有管理员能看。
 */
@SpringBootTest
@AutoConfigureMockMvc
class StatsOverviewTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    private String userToken() {
        return jwtUtils.generateToken(5L, "stu", 0);
    }

    private String adminToken() {
        return jwtUtils.generateToken(1L, "admin", 1);
    }

    @Test
    void anonymousCanNotSeeStats() throws Exception {
        mockMvc.perform(get("/api/stats/overview"))
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void normalUserCanNotSeeStats() throws Exception {
        mockMvc.perform(get("/api/stats/overview").header("token", userToken()))
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void adminSeesCountsAndTrend() throws Exception {
        given(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).willReturn(3L);
        given(jdbcTemplate.queryForList(anyString())).willReturn(List.of());

        mockMvc.perform(get("/api/stats/overview").header("token", adminToken()))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userCount").value(3))
                .andExpect(jsonPath("$.data.signupTrend.length()").value(7));
    }
}
