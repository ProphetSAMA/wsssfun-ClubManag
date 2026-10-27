package fun.wsss.utils;

/**
 * 图片类型嗅探
 *
 * <p>只信任文件头（magic bytes），不信任客户端的 Content-Type 和文件名后缀：
 * 前端可以把任意文件改名成 .png 或伪造 content-type。
 *
 * @author Wsssfun
 */
public final class ImageTypeUtils {

    /** 读取文件头需要的字节数（WEBP 需要 12 字节） */
    public static final int HEADER_LENGTH = 12;

    private ImageTypeUtils() {
    }

    /**
     * 根据文件头判断图片扩展名
     *
     * @param header 文件的前 {@link #HEADER_LENGTH} 个字节
     * @return jpg / png / gif / webp；不是支持的图片格式时返回 null
     */
    public static String extensionOf(byte[] header) {
        if (header == null || header.length < 3) {
            return null;
        }
        // JPEG: FF D8 FF
        if (header.length >= 3
                && (header[0] & 0xFF) == 0xFF
                && (header[1] & 0xFF) == 0xD8
                && (header[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        // PNG: 89 50 4E 47 0D 0A 1A 0A
        if (header.length >= 8
                && (header[0] & 0xFF) == 0x89
                && header[1] == 'P'
                && header[2] == 'N'
                && header[3] == 'G'
                && (header[4] & 0xFF) == 0x0D
                && (header[5] & 0xFF) == 0x0A
                && (header[6] & 0xFF) == 0x1A
                && (header[7] & 0xFF) == 0x0A) {
            return "png";
        }
        // GIF: GIF87a / GIF89a
        if (header.length >= 6
                && header[0] == 'G'
                && header[1] == 'I'
                && header[2] == 'F'
                && header[3] == '8'
                && (header[4] == '7' || header[4] == '9')
                && header[5] == 'a') {
            return "gif";
        }
        // WEBP: RIFF????WEBP
        if (header.length >= 12
                && header[0] == 'R'
                && header[1] == 'I'
                && header[2] == 'F'
                && header[3] == 'F'
                && header[8] == 'W'
                && header[9] == 'E'
                && header[10] == 'B'
                && header[11] == 'P') {
            return "webp";
        }
        return null;
    }
}
