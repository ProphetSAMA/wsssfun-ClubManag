package fun.wsss.status;

/**
 * 状态码
 *
 * @author Wsssfun
 * @date 2024/10/21 02:23
 */

public class StatusCode {
    // 成功
    public static final int OK = 200;
    // 失败
    public static final int ERROR = 500;
    // 未认证
    public static final int UNAUTHORIZED = 401;
    // 未授权
    public static final int FORBIDDEN = 403;
    // 未找到
    public static final int NOT_FOUND = 404;
    // 请求超时
    public static final int REQUEST_TIMEOUT = 408;
    // 服务器错误
    public static final int SERVER_ERROR = 500;
    // 服务不可用
    public static final int SERVICE_UNAVAILABLE = 503;
    // 网关超时
    public static final int GATEWAY_TIMEOUT = 504;
    // 未知错误
    public static final int UNKNOWN_ERROR = 999;
}
