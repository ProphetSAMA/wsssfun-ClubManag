package fun.wsss.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ResultVo 返回值的封装
 * @author Wsssfun
 */
@Data
@AllArgsConstructor
public class ResultVo<T> {
    private String msg;
    private int code;
    private T data;
}
