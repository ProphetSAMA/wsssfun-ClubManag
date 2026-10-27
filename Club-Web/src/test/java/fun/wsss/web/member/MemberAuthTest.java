package fun.wsss.web.member;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.config.jwt.JwtUtils;
import fun.wsss.web.member.entity.Member;
import fun.wsss.web.member.entity.MemberVo;
import fun.wsss.web.member.mapper.MemberMapper;
import fun.wsss.web.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 成员模块的权限校验。
 *
 * <p>覆盖三条规则：普通用户只能操作自己的成员记录、角色只能由管理员改、
 * 成员列表不再允许匿名访问。
 */
@SpringBootTest
@AutoConfigureMockMvc
class MemberAuthTest {

    /** 普通用户，user_id = 5 */
    private static final long USER_ID = 5L;
    /** 管理员，user_id = 1 */
    private static final long ADMIN_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberMapper memberMapper;

    private String userToken() {
        return jwtUtils.generateToken(USER_ID, "student", 0);
    }

    private String adminToken() {
        return jwtUtils.generateToken(ADMIN_ID, "admin", 1);
    }

    private Member memberOf(long id, long ownerId) {
        Member member = new Member();
        member.setId(id);
        member.setTeamId(1L);
        member.setUserId(ownerId);
        member.setStatus(1);
        return member;
    }

    @Test
    void anonymousCanNotListMembers() throws Exception {
        mockMvc.perform(get("/api/member/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));

        verify(memberMapper, never()).selectMemberPage(any(), any(), any(), any());
    }

    @Test
    void normalUserOnlyQueriesOwnMemberships() throws Exception {
        given(memberMapper.selectMemberPage(any(Page.class), any(), any(), any()))
                .willReturn(new Page<MemberVo>(1, 10));

        // 传别人的 userId 也没用，会被改写成 token 里的用户
        mockMvc.perform(get("/api/member/list")
                        .param("userId", String.valueOf(ADMIN_ID))
                        .header("token", userToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(memberMapper).selectMemberPage(any(Page.class), any(), userIdCaptor.capture(), any());
        assertThat(userIdCaptor.getValue()).isEqualTo(USER_ID);
    }

    @Test
    void adminCanQueryAnyUser() throws Exception {
        given(memberMapper.selectMemberPage(any(Page.class), any(), any(), any()))
                .willReturn(new Page<MemberVo>(1, 10));

        mockMvc.perform(get("/api/member/list")
                        .param("userId", String.valueOf(USER_ID))
                        .header("token", adminToken()))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(memberMapper).selectMemberPage(any(Page.class), any(), userIdCaptor.capture(), any());
        assertThat(userIdCaptor.getValue()).isEqualTo(USER_ID);
    }

    @Test
    void normalUserCanNotJoinOnBehalfOfOthers() throws Exception {
        given(memberService.count(any(Wrapper.class))).willReturn(0L);
        given(memberService.save(any(Member.class))).willReturn(true);

        mockMvc.perform(post("/api/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":88,\"teamId\":1,\"userId\":999,\"role\":2}")
                        .header("token", userToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberService).save(captor.capture());
        Member saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getRole()).isZero();
        assertThat(saved.getId()).isNull();
    }

    @Test
    void adminCanJoinOnBehalfOfOthers() throws Exception {
        given(memberService.count(any(Wrapper.class))).willReturn(0L);
        given(memberService.save(any(Member.class))).willReturn(true);

        mockMvc.perform(post("/api/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"teamId\":1,\"userId\":999,\"role\":1}")
                        .header("token", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberService).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(999L);
        assertThat(captor.getValue().getRole()).isEqualTo(1);
    }

    @Test
    void normalUserCanNotQuitSomeoneElsesMembership() throws Exception {
        given(memberService.getById(7L)).willReturn(memberOf(7L, 999L));

        mockMvc.perform(delete("/api/member/7").header("token", userToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));

        verify(memberService, never()).updateById(any(Member.class));
    }

    @Test
    void normalUserCanQuitOwnMembership() throws Exception {
        given(memberService.getById(7L)).willReturn(memberOf(7L, USER_ID));
        given(memberService.updateById(any(Member.class))).willReturn(true);

        mockMvc.perform(delete("/api/member/7").header("token", userToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void normalUserCanNotChangeRoles() throws Exception {
        mockMvc.perform(put("/api/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":7,\"role\":2}")
                        .header("token", userToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));

        verify(memberService, never()).updateById(any(Member.class));
    }

    @Test
    void adminCanChangeRoles() throws Exception {
        given(memberService.updateById(any(Member.class))).willReturn(true);

        mockMvc.perform(put("/api/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":7,\"role\":2}")
                        .header("token", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
