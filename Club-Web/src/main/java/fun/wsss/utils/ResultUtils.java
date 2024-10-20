package fun.wsss.utils;

import fun.wsss.status.StatusCode;

/**
 * 返回结果工具类
 *
 * @author Wsssfun
 * @date 2024/10/21 02:26
 */

public class ResultUtils {
    /**
     * 无参数返回
     * @return ResultVo
     */
    public static ResultVo success() {
        return Vo(null, StatusCode.OK, null);
    }
    public static ResultVo success(String msg) {
        return Vo(msg, StatusCode.OK, null);
    }

    /**
     * 带参数返回
     * @return ResultVo
     */
    public static ResultVo success(String msg,Object data) {
        return Vo(msg, StatusCode.OK, data);
    }
    public static ResultVo success(String msg, int code, Object data) {
        return Vo(msg, code, data);
    }
    public static ResultVo Vo(String msg, int code, Object data) {
        return new ResultVo(msg, code, data);
    }

    /**
     * 错误返回
     * @return ResultVo
     */
    public static ResultVo error() {
        return Vo(null, StatusCode.ERROR, null);
    }
    public static ResultVo error(String msg) {
        return Vo(msg, StatusCode.ERROR, null);
    }
    public static ResultVo error(String msg, int code) {
        return Vo(msg, code, null);
    }
    public static ResultVo error(String msg, int code, Object data) {
        return Vo(msg, code, data);
    }
    public static ResultVo error(String msg, Object data) {
        return Vo(msg, StatusCode.ERROR, data);
    }

}
