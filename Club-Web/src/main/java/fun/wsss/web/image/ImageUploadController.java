package fun.wsss.web.image;


import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    /**
     * 上传路径
     */
    @Value("${web.uploadpath}")
    private String webUploadPath;

    /**
     * 上传图片
     *
     * @param file 图片
     * @return 结果
     */
    @RequestMapping("/uploadImage")
    public ResultVo uploadImage(@RequestParam("file") MultipartFile file) {

        // 图片上传路径
        String url = null;

        // 获取文件名
        String fileName = file.getOriginalFilename();

        // 获取文件后缀
        String fileExtenionName = null;

        // 若文件名不为空
        if (fileName != null) {
            fileExtenionName = fileName.substring(fileName.lastIndexOf("."));
        }

        // 生成新的文件名
        String newFileName = UUID.randomUUID() + fileExtenionName;
        String filePath = webUploadPath;
        File fileDir = new File(filePath);

        // 如果文件夹不存在则创建
        if (!fileDir.exists()) {
            fileDir.mkdirs();
            // 设置文件权限
            fileDir.setWritable(true);
        }

        File targetFile = new File(filePath, newFileName);

        try {
            file.transferTo(targetFile);
            url = "/" + targetFile.getName();
        } catch (IOException e) {
            return null;
        }

        return ResultUtils.success("成功", "/images" + url);
    }
}
