package fun.wsss.web.user.entity;

import lombok.Data;

/**
 * 登录请求参数
 *
 * @author Wsssfun
 */
@Data
public class LoginDTO {
    private String username;
    private String password;
}
