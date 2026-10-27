package fun.wsss.web.user.entity;

import lombok.Data;

/**
 * 注册参数
 *
 * <p>只暴露注册必需的三个字段，避免用户顺手塞 isAdmin / status 之类的东西。
 *
 * @author Wsssfun
 */
@Data
public class RegisterParm {
    private String username;
    private String password;
    private String nickname;
}
