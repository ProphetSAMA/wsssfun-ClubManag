package fun.wsss.utils;

import fun.wsss.status.StatusCode;

/**
 * 数据返回工具
 *
 * @author Wsssfun
 */
public class ResultUtils {

    public static ResultVo success() {
        return Vo(null, StatusCode.SUCCESS_CODE, null);
    }
    public static ResultVo success(String msg) {
        return Vo(null, StatusCode.SUCCESS_CODE, null);
    }
}
