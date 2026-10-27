package fun.wsss.web.news;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.wsss.config.jwt.JwtUtils;
import fun.wsss.web.news.entity.News;
import fun.wsss.web.news.mapper.NewsMapper;
import fun.wsss.web.news.service.NewsService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 内容状态：草稿只有管理员能看，门户只能看到已发布内容。
 */
@SpringBootTest
@AutoConfigureMockMvc
class NewsStatusTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private NewsService newsService;

    @MockBean
    private NewsMapper newsMapper;

    private String userToken() {
        return jwtUtils.generateToken(5L, "stu", 0);
    }

    private String adminToken() {
        return jwtUtils.generateToken(1L, "admin", 1);
    }

    private News news(int status) {
        News n = new News();
        n.setId(7L);
        n.setTitle("草稿内容");
        n.setType("1");
        n.setStatus(status);
        n.setViewCount(0);
        return n;
    }

    @Test
    void draftIsHiddenFromAnonymous() throws Exception {
        given(newsService.getById(7L)).willReturn(news(0));

        mockMvc.perform(get("/api/news/detail/7"))
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("内容不存在或未发布"));

        verify(newsMapper, never()).increaseView(any());
    }

    @Test
    void publishedIsVisibleAndCountsView() throws Exception {
        given(newsService.getById(7L)).willReturn(news(1));
        given(newsMapper.increaseView(7L)).willReturn(1);

        mockMvc.perform(get("/api/news/detail/7"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.viewCount").value(1));

        verify(newsMapper).increaseView(7L);
    }

    @Test
    void adminCanSeeDraft() throws Exception {
        given(newsService.getById(7L)).willReturn(news(0));
        given(newsMapper.increaseView(7L)).willReturn(1);

        mockMvc.perform(get("/api/news/detail/7").header("token", adminToken()))
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void normalUserListOnlyReturnsPublished() throws Exception {
        given(newsService.page(any(Page.class), any())).willReturn(new Page<News>(1, 10));

        mockMvc.perform(get("/api/news/list").header("token", userToken()))
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<Wrapper<News>> captor = ArgumentCaptor.forClass(Wrapper.class);
        verify(newsService).page(any(Page.class), captor.capture());
        assertThat(captor.getValue().getSqlSegment().toLowerCase()).contains("status");
    }

    @Test
    void adminListIsNotRestrictedByStatus() throws Exception {
        given(newsService.page(any(Page.class), any())).willReturn(new Page<News>(1, 10));

        mockMvc.perform(get("/api/news/list").header("token", adminToken()))
                .andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<Wrapper<News>> captor = ArgumentCaptor.forClass(Wrapper.class);
        verify(newsService).page(any(Page.class), captor.capture());
        assertThat(captor.getValue().getSqlSegment().toLowerCase()).doesNotContain("status");
    }
}
