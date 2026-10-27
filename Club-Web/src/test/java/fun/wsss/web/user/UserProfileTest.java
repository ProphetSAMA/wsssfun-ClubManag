package fun.wsss.web.user;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import fun.wsss.config.jwt.JwtUtils;
import fun.wsss.web.user.entity.User;
import fun.wsss.web.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 普通用户自助修改资料 / 密码。
 *
 * <p>背景：/api/user 的写操作收归管理员后，普通用户连自己的昵称和密码都改不了，
 * 这两个接口把「改自己」的能力放回来，同时不允许借此改用户名、密码哈希或管理员标识。
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserProfileTest {

    private static final long USER_ID = 5L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserService userService;

    private User storedUser;

    @BeforeEach
    void setUp() {
        storedUser = new User();
        storedUser.setUserId(USER_ID);
        storedUser.setUsername("stu");
        storedUser.setPassword(passwordEncoder.encode("oldpass123"));
        storedUser.setNickname("原名");
        storedUser.setPhone("13800000000");
        storedUser.setSex("0");
        storedUser.setStatus(1);
        storedUser.setIsAdmin(0);
    }

    private String userToken() {
        return jwtUtils.generateToken(USER_ID, "stu", 0);
    }

    @Test
    void normalUserCanUpdateOwnProfile() throws Exception {
        given(userService.getById(USER_ID)).willReturn(storedUser);
        given(userService.updateById(any(User.class))).willReturn(true);

        // 请求体里带上 userId / password / isAdmin，都不应该生效
        mockMvc.perform(put("/api/user/profile")
                        .header("token", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"username\":\"hacker\",\"nickname\":\"新名字\","
                                + "\"phone\":\"13900000000\",\"sex\":\"1\","
                                + "\"password\":\"xxx\",\"isAdmin\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).updateById(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getUsername()).isEqualTo("stu");
        assertThat(saved.getNickname()).isEqualTo("新名字");
        assertThat(saved.getPhone()).isEqualTo("13900000000");
        assertThat(saved.getSex()).isEqualTo("1");
        assertThat(saved.getIsAdmin()).isZero();
        assertThat(saved.getPassword()).isEqualTo(storedUser.getPassword());
    }

    @Test
    void anonymousCanNotUpdateProfile() throws Exception {
        mockMvc.perform(put("/api/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"x\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));

        verify(userService, never()).updateById(any(User.class));
    }

    @Test
    void profileRejectsEmptyNickname() throws Exception {
        given(userService.getById(USER_ID)).willReturn(storedUser);

        mockMvc.perform(put("/api/user/profile")
                        .header("token", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"\"}"))
                .andExpect(jsonPath("$.code").value(500));

        verify(userService, never()).updateById(any(User.class));
    }

    @Test
    void changePasswordWithCorrectOldPassword() throws Exception {
        given(userService.getById(USER_ID)).willReturn(storedUser);
        given(userService.updateById(any(User.class))).willReturn(true);

        mockMvc.perform(put("/api/user/password")
                        .header("token", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"oldpass123\",\"newPassword\":\"newpass456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).updateById(captor.capture());
        String stored = captor.getValue().getPassword();
        assertThat(stored).startsWith("$2");
        assertThat(passwordEncoder.matches("newpass456", stored)).isTrue();
    }

    @Test
    void changePasswordRejectsWrongOldPassword() throws Exception {
        given(userService.getById(USER_ID)).willReturn(storedUser);

        mockMvc.perform(put("/api/user/password")
                        .header("token", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"wrong\",\"newPassword\":\"newpass456\"}"))
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("原密码不正确"));

        verify(userService, never()).updateById(any(User.class));
    }

    @Test
    void changePasswordRejectsTooShortPassword() throws Exception {
        given(userService.getById(USER_ID)).willReturn(storedUser);

        mockMvc.perform(put("/api/user/password")
                        .header("token", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"oldpass123\",\"newPassword\":\"123\"}"))
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("新密码至少 6 位"));

        verify(userService, never()).updateById(any(User.class));
    }

    @Test
    void legacyPlainPasswordCanAlsoBeChanged() throws Exception {
        storedUser.setPassword("oldplain");
        given(userService.getById(USER_ID)).willReturn(storedUser);
        given(userService.updateById(any(User.class))).willReturn(true);

        mockMvc.perform(put("/api/user/password")
                        .header("token", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"oldplain\",\"newPassword\":\"newpass456\"}"))
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).updateById(captor.capture());
        assertThat(captor.getValue().getPassword()).startsWith("$2");
    }
}
