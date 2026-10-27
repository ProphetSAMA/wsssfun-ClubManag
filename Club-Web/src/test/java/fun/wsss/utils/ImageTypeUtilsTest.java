package fun.wsss.utils;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 图片嗅探只认文件头，不认文件名和 content-type。
 */
class ImageTypeUtilsTest {

    private static byte[] pad(byte[] head, int total) {
        byte[] data = new byte[Math.max(total, head.length)];
        System.arraycopy(head, 0, data, 0, head.length);
        return data;
    }

    @Test
    void detectsJpeg() {
        byte[] head = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        assertThat(ImageTypeUtils.extensionOf(pad(head, 32))).isEqualTo("jpg");
    }

    @Test
    void detectsPng() {
        byte[] head = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0};
        assertThat(ImageTypeUtils.extensionOf(pad(head, 32))).isEqualTo("png");
    }

    @Test
    void detectsGif() {
        byte[] head = "GIF89a".getBytes(StandardCharsets.US_ASCII);
        assertThat(ImageTypeUtils.extensionOf(pad(head, 32))).isEqualTo("gif");
    }

    @Test
    void detectsWebp() {
        byte[] head = {'R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'E', 'B', 'P'};
        assertThat(ImageTypeUtils.extensionOf(pad(head, 32))).isEqualTo("webp");
    }

    @Test
    void rejectsHtmlPretendingToBeImage() {
        byte[] head = "<html><body>".getBytes(StandardCharsets.US_ASCII);
        assertThat(ImageTypeUtils.extensionOf(pad(head, 32))).isNull();
    }

    @Test
    void rejectsShortOrEmptyHeader() {
        assertThat(ImageTypeUtils.extensionOf(new byte[0])).isNull();
        assertThat(ImageTypeUtils.extensionOf(null)).isNull();
        assertThat(ImageTypeUtils.extensionOf(new byte[]{1, 2})).isNull();
    }
}
