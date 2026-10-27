package fun.wsss.web.image;

import fun.wsss.utils.ImageTypeUtils;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 图片上传控制器
 *
 * <p>服务端只信任文件头：扩展名由嗅探结果决定，客户端传来的文件名和
 * content-type 都不参与，避免把 .html/.jsp 之类改名成 .png 传上来。
 *
 * @author Wsssfun
 * @date 2024/11/9 00:02
 */
@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {

    /** 单张图片上限，和前端 UploadImage 组件的提示保持一致 */
    private static final long MAX_SIZE = 5 * 1024 * 1024L;

    /** 孤儿图片至少保留这么久再清理，避免刚上传还没保存业务数据就被删 */
    private static final long KEEP_RECENT_MILLIS = 24L * 60 * 60 * 1000;

    @Value("${web.uploadpath}")
    private String webUploadPath;

    private final JdbcTemplate jdbc;

    public ImageUploadController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 图片访问路径
     */
    @PostMapping("/uploadImage")
    public ResultVo uploadImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResultUtils.error("请选择要上传的图片");
        }
        if (file.getSize() > MAX_SIZE) {
            return ResultUtils.error("图片大小不能超过 5MB");
        }

        String extension = ImageTypeUtils.extensionOf(readHeader(file));
        if (extension == null) {
            return ResultUtils.error("只支持 jpg、png、gif、webp 格式的图片");
        }

        String newFileName = UUID.randomUUID() + "." + extension;

        File dir = new File(webUploadPath);
        if (!dir.exists() && !dir.mkdirs()) {
            return ResultUtils.error("上传目录创建失败");
        }

        File targetFile = new File(dir, newFileName);
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            return ResultUtils.error("上传失败: " + e.getMessage());
        }

        return ResultUtils.success("上传成功", "/images/" + newFileName);
    }

    /**
     * 删除一张上传的图片
     *
     * @param filename 文件名（不含路径）
     * @return 结果
     */
    @DeleteMapping
    public ResultVo delete(@RequestParam("filename") String filename) {
        if (filename == null || filename.isBlank()
                || filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return ResultUtils.error("文件名不合法");
        }
        File target = new File(webUploadPath, filename);
        if (!target.exists() || !target.isFile()) {
            return ResultUtils.error("文件不存在");
        }
        return target.delete() ? ResultUtils.success("删除成功") : ResultUtils.error("删除失败");
    }

    /**
     * 清理没有被引用的图片
     *
     * <p>数据库里 team.image / news.image 是逗号分隔的相对路径，这里取出所有文件名，
     * 与上传目录里的文件比对，删掉没被引用、且超过保留期的文件。
     *
     * @return 扫描/删除/跳过数量
     */
    @PostMapping("/cleanup")
    public ResultVo cleanup() {
        Map<String, Object> result = new HashMap<>();
        File dir = new File(webUploadPath);
        if (!dir.exists() || !dir.isDirectory()) {
            result.put("scanned", 0);
            result.put("deleted", 0);
            result.put("skippedRecent", 0);
            result.put("msg", "上传目录不存在");
            return ResultUtils.success("没有可清理的图片", result);
        }

        Set<String> referenced = new HashSet<>();
        for (Map<String, Object> row : jdbc.queryForList(
                "SELECT image FROM team UNION ALL SELECT image FROM news")) {
            Object value = row.get("image");
            if (value == null) {
                continue;
            }
            for (String part : String.valueOf(value).split(",")) {
                String trimmed = part.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                int slash = Math.max(trimmed.lastIndexOf('/'), trimmed.lastIndexOf('\\'));
                referenced.add(slash >= 0 ? trimmed.substring(slash + 1) : trimmed);
            }
        }

        long now = System.currentTimeMillis();
        int scanned = 0;
        int deleted = 0;
        int skippedRecent = 0;
        File[] files = dir.listFiles(File::isFile);
        if (files != null) {
            for (File file : files) {
                scanned++;
                if (referenced.contains(file.getName())) {
                    continue;
                }
                if (now - file.lastModified() < KEEP_RECENT_MILLIS) {
                    skippedRecent++;
                    continue;
                }
                if (file.delete()) {
                    deleted++;
                }
            }
        }
        result.put("scanned", scanned);
        result.put("deleted", deleted);
        result.put("skippedRecent", skippedRecent);
        return ResultUtils.success("清理完成", result);
    }

    /**
     * 读取文件头，用于判断真实图片类型
     */
    private byte[] readHeader(MultipartFile file) {
        byte[] header = new byte[ImageTypeUtils.HEADER_LENGTH];
        try (InputStream in = file.getInputStream()) {
            int read = in.readNBytes(header, 0, header.length);
            if (read < header.length) {
                // 文件比文件头还短，不是合法图片
                return new byte[0];
            }
            return header;
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
