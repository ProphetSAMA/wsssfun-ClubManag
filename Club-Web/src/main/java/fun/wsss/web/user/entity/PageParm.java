package fun.wsss.web.user.entity;


import lombok.Data;

/**
 * 分页参数
 *
 * @author Wsssfun
 * @date 2024/10/24 00:33
 */
@Data
public class PageParm {
    private Long currentPage = 1L;
    private Long pageSize = 10L;
    private String nickName;
    /** 登录账号，模糊匹配 */
    private String username;
    /** 手机号，模糊匹配 */
    private String phone;
    /** 0-普通用户 1-管理员 */
    private Integer isAdmin;

}
