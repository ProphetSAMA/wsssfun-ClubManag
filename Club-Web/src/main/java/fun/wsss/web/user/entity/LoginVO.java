package fun.wsss.web.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应数据
 *
 * @author Wsssfun
 */
@Data
@AllArgsConstructor
public class LoginVO {
    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private Integer isAdmin;
}
