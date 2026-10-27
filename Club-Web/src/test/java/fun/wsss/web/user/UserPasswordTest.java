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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 密码存储与校验。
 *
 * <p>新密码一律 BCrypt 存储；老库里遗留的明文密码在登录成功时静默升级，
 * 保证历史账号不被破坏。
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserPasswordTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private UserService userService;

    /** 管理接口都要过 AuthInterceptor，测试里直接发一个合法的管理员 token */
    private String adminToken() {
        return jwtUtils.generateToken(1L, "admin", 1);
    }

    private User userWith(String storedPassword) {
        User user = new User();
        user.setUserId(5L);
        user.setUsername("stu");
        user.setPassword(storedPassword);
        user.setNickname("学生");
        user.setPhone("13800000000");
        user.setStatus(1);
        user.setIsAdmin(0);
        return user;
    }

    private String loginBody(String username, String password) {
        return "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
    }

    @Test
    void legacyPlainPasswordLoginIsUpgradedToBcrypt() throws Exception {
        given(userService.getOne(any())).willReturn(userWith("123456"));
        given(userService.updateById(any(User.class))).willReturn(true);

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("stu", "123456")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).updateById(captor.capture());
        String upgraded = captor.getValue().getPassword();
        assertThat(upgraded).startsWith("$2");
        assertThat(passwordEncoder.matches("123456", upgraded)).isTrue();
    }

    @Test
    void wrongPasswordIsRejectedAndNothingIsWritten() throws Exception {
        given(userService.getOne(any())).willReturn(userWith("123456"));

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("stu", "wrong")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("密码错误"));

        verify(userService, never()).updateById(any(User.class));
    }

    @Test
    void bcryptPasswordLoginWorksAndIsNotRehashed() throws Exception {
        String stored = passwordEncoder.encode("secret123");
        given(userService.getOne(any())).willReturn(userWith(stored));

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("stu", "secret123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService, never()).updateById(any(User.class));
    }

    @Test
    void creatingUserStoresBcryptHash() throws Exception {
        given(userService.save(any(User.class))).willReturn(true);

        mockMvc.perform(post("/api/user")
                        .header("token", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newbie\",\"password\":\"abc123\",\"nickname\":\"新人\",\"status\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(captor.capture());
        assertThat(captor.getValue().getPassword()).startsWith("$2");
        assertThat(passwordEncoder.matches("abc123", captor.getValue().getPassword())).isTrue();
    }

    @Test
    void creatingUserWithoutPasswordFails() throws Exception {
        mockMvc.perform(post("/api/user")
                        .header("token", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newbie\",\"nickname\":\"新人\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        verify(userService, never()).save(any(User.class));
    }

    @Test
    void editingWithoutPasswordLeavesItUntouched() throws Exception {
        given(userService.updateById(any(User.class))).willReturn(true);

        mockMvc.perform(put("/api/user")
                        .header("token", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":5,\"nickname\":\"改个名\",\"password\":\"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).updateById(captor.capture());
        assertThat(captor.getValue().getPassword()).isEmpty();
    }

    @Test
    void editingWithNewPasswordStoresBcryptHash() throws Exception {
        given(userService.updateById(any(User.class))).willReturn(true);

        mockMvc.perform(put("/api/user")
                        .header("token", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":5,\"password\":\"newpass1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).updateById(captor.capture());
        assertThat(captor.getValue().getPassword()).startsWith("$2");
        assertThat(passwordEncoder.matches("newpass1", captor.getValue().getPassword())).isTrue();
    }
}
