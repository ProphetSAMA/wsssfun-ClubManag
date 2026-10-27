package fun.wsss.config.exception;

import fun.wsss.utils.ResultUtils;
import fun.wsss.utils.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * <p>异常细节只写日志，不返回给客户端：原始 message 里可能带 SQL、路径、
 * 账号等内部信息（例如 "Access denied for user 'root'@'localhost'"）。
 *
 * @author Wsssfun
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResultVo handleException(Exception e) {
        log.error("未处理的异常", e);
        return ResultUtils.error("服务器内部错误");
    }

    /**
     * 参数校验类异常是业务自己的提示，可以直接返回
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResultVo handleIllegalArgument(IllegalArgumentException e) {
        return ResultUtils.error(e.getMessage());
    }
}
