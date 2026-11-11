package fun.wsss.web.user;

import fun.wsss.config.jwt.JwtUtils;
import fun.wsss.web.user.entity.User;
import fun.wsss.web.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 头像路径校验与密保找回密码。
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserSecurityTest {

    private static final long USER_ID = 70L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserService userService;

    private String token() {
        return jwtUtils.generateToken(USER_ID, "sec-user", 0);
    }

    private User userWithAnswer(String answer) {
        User user = new User();
        user.setUserId(USER_ID);
        user.setUsername("sec-user");
        user.setPassword(passwordEncoder.encode("oldpass12"));
        user.setSecurityQuestion("我最喜欢的社团是？");
        user.setSecurityAnswerHash(passwordEncoder.encode(answer));
        return user;
    }

    @Test
    void avatarMustBeUnderImages() throws Exception {
        mockMvc.perform(put("/api/user/avatar")
                        .header("token", token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"avatar\":\"/etc/passwd\"}"))
                .andExpect(jsonPath("$.msg").value("头像路径不合法"));

        verify(userService, never()).updateById(any(User.class));
    }

    @Test
    void avatarSavesRelativePath() throws Exception {
        given(userService.update(any())).willReturn(true);

        mockMvc.perform(put("/api/user/avatar")
                        .header("token", token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"avatar\":\"/images/abc.png\"}"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("头像已更新"));
    }

    @Test
    void settingSecurityQuestionNeedsCurrentPassword() throws Exception {
        given(userService.getById(USER_ID)).willReturn(userWithAnswer("吉他社"));

        mockMvc.perform(put("/api/user/security-question")
                        .header("token", token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"wrong\",\"question\":\"q\",\"answer\":\"a\"}"))
                .andExpect(jsonPath("$.msg").value("当前密码不正确"));
    }

    @Test
    void unknownAccountDoesNotRevealWhetherItExists() throws Exception {
        given(userService.getOne(any())).willReturn(null);

        mockMvc.perform(post("/api/user/forgot/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nobody\"}"))
                .andExpect(jsonPath("$.msg").value("该账号没有设置密保问题，请联系管理员重置密码"));
    }

    @Test
    void forgotQueryReturnsQuestion() throws Exception {
        given(userService.getOne(any())).willReturn(userWithAnswer("吉他社"));

        mockMvc.perform(post("/api/user/forgot/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"sec-user\"}"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.question").value("我最喜欢的社团是？"));
    }

    @Test
    void wrongAnswerIsRejected() throws Exception {
        given(userService.getOne(any())).willReturn(userWithAnswer("吉他社"));

        mockMvc.perform(post("/api/user/forgot/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"sec-user\",\"answer\":\"街舞社\",\"newPassword\":\"newpass123\"}"))
                .andExpect(jsonPath("$.msg").value("密保答案不正确"));

        verify(userService, never()).updateById(any(User.class));
    }

    @Test
    void correctAnswerResetsPassword() throws Exception {
        User user = userWithAnswer("吉他社");
        given(userService.getOne(any())).willReturn(user);
        given(userService.updateById(any(User.class))).willReturn(true);

        mockMvc.perform(post("/api/user/forgot/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"sec-user\",\"answer\":\"吉他社\",\"newPassword\":\"newpass123\"}"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("密码已重置，请用新密码登录"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).updateById(captor.capture());
        assertThat(passwordEncoder.matches("newpass123", captor.getValue().getPassword())).isTrue();
    }

    @Test
    void shortPasswordIsRejected() throws Exception {
        mockMvc.perform(post("/api/user/forgot/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"sec-user\",\"answer\":\"吉他社\",\"newPassword\":\"short\"}"))
                .andExpect(jsonPath("$.msg").value("密码至少 8 位"));
    }
}
