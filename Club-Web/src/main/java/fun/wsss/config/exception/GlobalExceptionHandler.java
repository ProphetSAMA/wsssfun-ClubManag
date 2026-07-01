package fun.wsss.config.exception;

import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author Wsssfun
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResultVo handleException(Exception e) {
        e.printStackTrace();
        return ResultUtils.error("服务器内部错误: " + e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResultVo handleIllegalArgument(IllegalArgumentException e) {
        return ResultUtils.error(e.getMessage());
    }
}
