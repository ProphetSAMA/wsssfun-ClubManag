package fun.wsss.web.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.config.jwt.JwtUtils;
import fun.wsss.web.member.mapper.MemberMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 登出：把当前 token 拉黑，过期前都不能再用。
 */
@SpringBootTest
@AutoConfigureMockMvc
class LogoutTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    /** 受保护接口用 /api/member/list，这里把它背后的查询挡住，避免依赖真实表 */
    @MockBean
    private MemberMapper memberMapper;

    @Test
    void tokenIsRejectedAfterLogout() throws Exception {
        given(memberMapper.selectMemberPage(any(), any(), any(), any())).willReturn(new Page<>());
        String token = jwtUtils.generateToken(5L, "stu", 0);

        // 登出前可以访问受保护接口
        mockMvc.perform(get("/api/member/list").header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/api/user/logout").header("token", token))
                .andExpect(jsonPath("$.code").value(200));

        // 登出后同一个 token 失效
        mockMvc.perform(get("/api/member/list").header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));

        // 换一个新 token 依然可用
        String fresh = jwtUtils.generateToken(5L, "stu", 0);
        mockMvc.perform(get("/api/member/list").header("token", fresh))
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void anonymousCanNotLogout() throws Exception {
        mockMvc.perform(post("/api/user/logout"))
                .andExpect(jsonPath("$.code").value(401));
    }
}
