package fun.wsss.web.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户实体类
 *
 * @author Wsssfun
 * @date 2024/10/22 23:55
 */
@Data
@TableName("sys_user")
public class User {

    /**
     * 主键ID
     */
    @TableId(value = "user_id")
    private Long userId;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 昵称
     */
    @TableField("nick_name")
    private String nickname;

    /**
     * 性别
     */
    @TableField("sex")
    private String sex;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 状态
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否为管理员
     */
    @TableField("is_admin")
    private Integer isAdmin;

    /**
     * 头像相对路径
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 密保问题
     */
    @TableField("security_question")
    private String securityQuestion;

    /**
     * 密保答案（BCrypt），只用于校验，不返回给前端
     */
    @TableField("security_answer_hash")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String securityAnswerHash;
}
