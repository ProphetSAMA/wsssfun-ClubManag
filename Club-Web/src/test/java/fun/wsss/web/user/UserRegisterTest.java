package fun.wsss.web.user;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 游客注册。
 *
 * <p>注册接口必须对未登录用户开放，且新账号只能是普通用户。
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserRegisterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserService userService;

    @Test
    void anonymousCanRegister() throws Exception {
        given(userService.count(any(Wrapper.class))).willReturn(0L);
        given(userService.save(any(User.class))).willReturn(true);

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newbie\",\"password\":\"secret123\",\"nickname\":\"新同学\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("newbie");
        assertThat(saved.getPassword()).startsWith("$2");
        assertThat(passwordEncoder.matches("secret123", saved.getPassword())).isTrue();
        assertThat(saved.getNickname()).isEqualTo("新同学");
        assertThat(saved.getIsAdmin()).isZero();
        assertThat(saved.getStatus()).isEqualTo(1);
    }

    @Test
    void nicknameDefaultsToUsername() throws Exception {
        given(userService.count(any(Wrapper.class))).willReturn(0L);
        given(userService.save(any(User.class))).willReturn(true);

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"noname\",\"password\":\"secret123\"}"))
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(captor.capture());
        assertThat(captor.getValue().getNickname()).isEqualTo("noname");
    }

    @Test
    void duplicateUsernameIsRejected() throws Exception {
        given(userService.count(any(Wrapper.class))).willReturn(1L);

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newbie\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("用户名已存在"));

        verify(userService, never()).save(any(User.class));
    }

    @Test
    void shortPasswordIsRejected() throws Exception {
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newbie\",\"password\":\"123\"}"))
                .andExpect(jsonPath("$.msg").value("密码至少 6 位"));

        verify(userService, never()).save(any(User.class));
    }

    @Test
    void invalidUsernameIsRejected() throws Exception {
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"新 同学\",\"password\":\"secret123\"}"))
                .andExpect(jsonPath("$.code").value(500));

        verify(userService, never()).save(any(User.class));
    }

    @Test
    void bodyCannotEscalateToAdmin() throws Exception {
        given(userService.count(any(Wrapper.class))).willReturn(0L);
        given(userService.save(any(User.class))).willReturn(true);

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"sneaky\",\"password\":\"secret123\",\"isAdmin\":1,\"status\":0}"))
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(captor.capture());
        assertThat(captor.getValue().getIsAdmin()).isZero();
        assertThat(captor.getValue().getStatus()).isEqualTo(1);
    }
}
