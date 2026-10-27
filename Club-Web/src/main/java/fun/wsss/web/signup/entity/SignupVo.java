package fun.wsss.web.signup.entity;

import lombok.Data;

/**
 * 报名视图对象（关联活动与用户信息）
 *
 * @author Wsssfun
 */
@Data
public class SignupVo {
    private Long id;
    private Long newsId;
    private Long userId;
    private Integer status;

    /** 活动标题 */
    private String title;
    /** 活动封面 */
    private String image;
    /** 活动时间（news.create_time） */
    private String activityTime;
    /** 报名时间 */
    private String signupTime;

    /** 报名用户（管理员查看名单时用） */
    private String userName;
    private String phone;
}
