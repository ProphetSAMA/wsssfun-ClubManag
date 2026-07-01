package fun.wsss.web.image;

import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 图片上传控制器
 *
 * @author Wsssfun
 * @date 2024/11/9 00:02
 */
@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {

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
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            return ResultUtils.error("文件名为空");
        }

        String extension = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + extension;

        File dir = new File(webUploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File targetFile = new File(webUploadPath, newFileName);
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            return ResultUtils.error("上传失败: " + e.getMessage());
        }

        return ResultUtils.success("上传成功", "/images/" + newFileName);
    }
}
