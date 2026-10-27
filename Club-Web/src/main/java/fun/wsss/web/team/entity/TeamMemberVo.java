package fun.wsss.web.team.entity;

import lombok.Data;

/**
 * 社团成员（门户展示用，不含手机号等隐私字段）
 *
 * @author Wsssfun
 */
@Data
public class TeamMemberVo {
    private String userName;
    /** 0-普通成员 1-管理员 2-社长 */
    private Integer role;
    private String joinTime;
}
