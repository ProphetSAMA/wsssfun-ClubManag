package fun.wsss.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * VO类
 *
 * @author Wsssfun
 * @date 2024/10/21 02:20
 */

@Data
@AllArgsConstructor
public class ResultVo<T> {
    private String msg;
    private int code;
    private T data;
}
