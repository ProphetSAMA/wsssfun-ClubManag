package fun.wsss.web.image;

import fun.wsss.utils.ImageTypeUtils;
import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    @Value("${web.uploadpath}")
    private String webUploadPath;

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
