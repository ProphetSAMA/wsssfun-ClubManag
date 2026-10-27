package fun.wsss.config.exception;

import fun.wsss.utils.ResultVo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 异常响应不应该把内部细节回传给客户端。
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void internalErrorDoesNotLeakExceptionMessage() {
        ResultVo vo = handler.handleException(
                new IllegalStateException("Access denied for user 'root'@'localhost' using password: YES"));

        assertThat(vo.getMsg()).isEqualTo("服务器内部错误");
        assertThat(vo.getMsg()).doesNotContain("root").doesNotContain("password");
    }

    @Test
    void businessMessageIsStillReturned() {
        ResultVo vo = handler.handleIllegalArgument(new IllegalArgumentException("用户名不能为空"));

        assertThat(vo.getMsg()).isEqualTo("用户名不能为空");
    }
}
