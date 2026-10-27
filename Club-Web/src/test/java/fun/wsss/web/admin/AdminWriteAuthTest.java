package fun.wsss.web.admin;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.config.jwt.JwtUtils;
import fun.wsss.web.member.entity.Member;
import fun.wsss.web.member.mapper.MemberMapper;
import fun.wsss.web.member.service.MemberService;
import fun.wsss.web.team.entity.Team;
import fun.wsss.web.team.service.TeamService;
import fun.wsss.web.user.entity.User;
import fun.wsss.web.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 管理接口的写权限：只有 is_admin = 1 的 token 能改数据。
 *
 * <p>读接口保持开放（门户首页、列表都要用），除了会暴露全员资料的 /api/user/list。
 * 成员模块不在此列——普通用户加入/退出社团是正常操作。
 */
@SpringBootTest
@AutoConfigureMockMvc
class AdminWriteAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private TeamService teamService;

    @MockBean
    private UserService userService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberMapper memberMapper;

    private String userToken() {
        return jwtUtils.generateToken(5L, "student", 0);
    }

    private String adminToken() {
        return jwtUtils.generateToken(1L, "admin", 1);
    }

    @Test
    void anonymousCanNotWrite() throws Exception {
        mockMvc.perform(post("/api/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"x\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));

        verify(teamService, never()).save(any(Team.class));
    }

    @Test
    void normalUserCanNotCreateTeam() throws Exception {
        mockMvc.perform(post("/api/team")
                        .header("token", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"x\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));

        verify(teamService, never()).save(any(Team.class));
    }

    @Test
    void adminCanCreateTeam() throws Exception {
        given(teamService.save(any(Team.class))).willReturn(true);

        mockMvc.perform(post("/api/team")
                        .header("token", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"新社团\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void publicReadIsUnaffected() throws Exception {
        given(teamService.page(any(Page.class), any())).willReturn(new Page<Team>(1, 10));

        // 门户首页/社团列表是游客也能看的
        mockMvc.perform(get("/api/team/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void normalUserCanNotReadUserList() throws Exception {
        mockMvc.perform(get("/api/user/list").header("token", userToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));

        verify(userService, never()).page(any(), any());
    }

    @Test
    void adminCanReadUserList() throws Exception {
        given(userService.page(any(Page.class), any())).willReturn(new Page<User>(1, 10));

        mockMvc.perform(get("/api/user/list").header("token", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void normalUserCanStillJoinClub() throws Exception {
        given(memberService.count(any(Wrapper.class))).willReturn(0L);
        given(memberService.save(any(Member.class))).willReturn(true);

        mockMvc.perform(post("/api/member")
                        .header("token", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"teamId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void normalUserCanNotUploadImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "a.png", "image/png", new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/upload/uploadImage").file(file).header("token", userToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }
}
