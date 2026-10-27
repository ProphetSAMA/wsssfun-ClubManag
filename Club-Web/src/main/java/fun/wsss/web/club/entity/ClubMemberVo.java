package fun.wsss.web.club.entity;

import lombok.Data;

/**
 * 社团成员（社团管理员视角，含联系方式）
 *
 * @author Wsssfun
 */
@Data
public class ClubMemberVo {
    /** member 表主键 */
    private Long id;
    private Long userId;
    private String userName;
    private String phone;
    /** 0-普通成员 1-管理员 2-社长 */
    private Integer role;
    private String joinTime;
}
