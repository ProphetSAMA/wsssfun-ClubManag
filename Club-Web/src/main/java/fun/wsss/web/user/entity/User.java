package fun.wsss.web.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Wsssfun
 * @date 2024/10/22 23:55
 */

@Data
@TableName("sys_user")
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
}
