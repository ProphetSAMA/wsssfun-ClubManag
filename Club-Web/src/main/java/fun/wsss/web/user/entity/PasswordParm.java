package fun.wsss.web.user.entity;

import lombok.Data;

/**
 * 修改密码参数
 *
 * @author Wsssfun
 */
@Data
public class PasswordParm {
    /** 原密码（明文，用于校验身份） */
    private String oldPassword;
    /** 新密码（明文，服务端加密后存储） */
    private String newPassword;
}
