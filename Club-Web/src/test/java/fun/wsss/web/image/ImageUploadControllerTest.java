package fun.wsss.web.image;

import fun.wsss.config.jwt.JwtUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 上传接口：类型由文件头决定，扩展名不可被客户端指定。
 */
@SpringBootTest
@AutoConfigureMockMvc
class ImageUploadControllerTest {

    private static final Path UPLOAD_DIR;

    static {
        try {
            UPLOAD_DIR = Files.createTempDirectory("club-upload-test");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("web.uploadpath", () -> UPLOAD_DIR.toString() + File.separator);
    }

    @AfterAll
    static void cleanUp() throws IOException {
        try (Stream<Path> paths = Files.walk(UPLOAD_DIR)) {
            paths.sorted((a, b) -> b.getNameCount() - a.getNameCount()).forEach(p -> p.toFile().delete());
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    private String adminToken() {
        return jwtUtils.generateToken(1L, "admin", 1);
    }

    private static byte[] pngBytes() {
        byte[] data = new byte[64];
        byte[] head = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
        System.arraycopy(head, 0, data, 0, head.length);
        return data;
    }

    @Test
    void extensionComesFromFileHeaderNotFromFileName() throws Exception {
        long before = uploadedFiles();
        MockMultipartFile file =
                new MockMultipartFile("file", "shell.html", MediaType.TEXT_HTML_VALUE, pngBytes());

        mockMvc.perform(multipart("/api/upload/uploadImage").file(file).header("token", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.endsWith(".png")));

        assertThat(uploadedFiles()).isEqualTo(before + 1);
        assertThat(lastUploadedFileName()).endsWith(".png").doesNotContain("shell");
    }

    @Test
    void nonImageContentIsRejected() throws Exception {
        long before = uploadedFiles();
        MockMultipartFile file = new MockMultipartFile("file", "fake.png", MediaType.IMAGE_PNG_VALUE,
                "<script>alert(1)</script>".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/upload/uploadImage").file(file).header("token", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("只支持 jpg、png、gif、webp 格式的图片"));

        assertThat(uploadedFiles()).isEqualTo(before);
    }

    @Test
    void oversizedImageIsRejected() throws Exception {
        long before = uploadedFiles();
        byte[] big = new byte[6 * 1024 * 1024];
        System.arraycopy(pngBytes(), 0, big, 0, 64);
        MockMultipartFile file = new MockMultipartFile("file", "big.png", MediaType.IMAGE_PNG_VALUE, big);

        mockMvc.perform(multipart("/api/upload/uploadImage").file(file).header("token", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("图片大小不能超过 5MB"));

        assertThat(uploadedFiles()).isEqualTo(before);
    }

    @Test
    void adminCanDeleteUploadedFile() throws Exception {
        File target = UPLOAD_DIR.resolve("to-delete.png").toFile();
        Files.write(target.toPath(), pngBytes());
        assertThat(target.exists()).isTrue();

        mockMvc.perform(delete("/api/upload").param("filename", "to-delete.png").header("token", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertThat(target.exists()).isFalse();
    }

    @Test
    void deleteRejectsPathTraversal() throws Exception {
        mockMvc.perform(delete("/api/upload").param("filename", "../secret.txt").header("token", adminToken()))
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("文件名不合法"));
    }

    @Test
    void cleanupOnlyRemovesUnreferencedOldFiles() throws Exception {
        File referenced = UPLOAD_DIR.resolve("referenced.png").toFile();
        File orphan = UPLOAD_DIR.resolve("orphan.png").toFile();
        File fresh = UPLOAD_DIR.resolve("fresh.png").toFile();
        Files.write(referenced.toPath(), pngBytes());
        Files.write(orphan.toPath(), pngBytes());
        Files.write(fresh.toPath(), pngBytes());
        long twoDaysAgo = System.currentTimeMillis() - 2L * 24 * 60 * 60 * 1000;
        assertThat(referenced.setLastModified(twoDaysAgo)).isTrue();
        assertThat(orphan.setLastModified(twoDaysAgo)).isTrue();

        given(jdbcTemplate.queryForList(anyString()))
                .willReturn(List.of(Map.of("image", "/images/referenced.png")));

        mockMvc.perform(post("/api/upload/cleanup").header("token", adminToken()))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.deleted").value(1))
                // 同一个临时目录里还有其它用例留下的新文件，所以这里是「至少 1 个」而不是精确值
                .andExpect(jsonPath("$.data.skippedRecent").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));

        assertThat(referenced.exists()).isTrue();
        assertThat(orphan.exists()).isFalse();
        assertThat(fresh.exists()).isTrue();
    }

    private long uploadedFiles() throws IOException {
        try (Stream<Path> paths = Files.list(UPLOAD_DIR)) {
            return paths.count();
        }
    }

    private String lastUploadedFileName() throws IOException {
        try (Stream<Path> paths = Files.list(UPLOAD_DIR)) {
            return paths.max((a, b) -> a.getFileName().toString().compareTo(b.getFileName().toString()))
                    .map(p -> p.getFileName().toString()).orElse("");
        }
    }
}
