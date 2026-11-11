package fun.wsss.web.audit;

import fun.wsss.config.jwt.JwtUtils;
import fun.wsss.web.audit.entity.AuditLog;
import fun.wsss.web.audit.service.AuditLogService;
import fun.wsss.web.news.service.NewsService;
import fun.wsss.web.user.entity.User;
import fun.wsss.web.user.service.UserService;
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
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 操作日志：写操作会被记录，读操作不会；只有管理员能查。
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuditLogTest {

    private static final long USER = 80L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private AuditLogService auditLogService;

    @MockBean
    private UserService userService;

    @MockBean
    private NewsService newsService;

    private String token() {
        return jwtUtils.generateToken(USER, "audit-user", 0);
    }

    @Test
    void writeRequestIsRecorded() throws Exception {
        given(auditLogService.save(any(AuditLog.class))).willReturn(true);
        User user = new User();
        user.setUserId(USER);
        user.setPassword("$2a$10$abcdefghijklmnopqrstuv");
        given(userService.getById(USER)).willReturn(user);
        given(userService.updateById(any(User.class))).willReturn(true);

        mockMvc.perform(put("/api/user/profile")
                        .header("token", token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"新昵称\",\"sex\":\"0\",\"phone\":\"13800000000\"}"))
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogService).save(captor.capture());
        AuditLog log = captor.getValue();
        assertThat(log.getMethod()).isEqualTo("PUT");
        assertThat(log.getPath()).isEqualTo("/api/user/profile");
        assertThat(log.getUserId()).isEqualTo(USER);
        assertThat(log.getUsername()).isEqualTo("audit-user");
        assertThat(log.getDurationMs()).isNotNull();
    }

    @Test
    void readRequestIsNotRecorded() throws Exception {
        given(newsService.page(any(), any())).willReturn(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>());
        mockMvc.perform(get("/api/news/list")).andExpect(jsonPath("$.code").value(200));

        verify(auditLogService, org.mockito.Mockito.never()).save(any(AuditLog.class));
    }

    @Test
    void normalUserCanNotReadAuditList() throws Exception {
        mockMvc.perform(get("/api/audit/list").header("token", token()))
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void adminCanReadAuditList() throws Exception {
        given(auditLogService.page(any())).willReturn(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>());

        mockMvc.perform(get("/api/audit/list")
                        .header("token", jwtUtils.generateToken(1L, "admin", 1)))
                .andExpect(jsonPath("$.code").value(200));
    }
}
