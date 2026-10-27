package fun.wsss.web.club.entity;

import lombok.Data;

/**
 * 我管理的社团
 *
 * @author Wsssfun
 */
@Data
public class ClubTeamVo {
    private Long teamId;
    private String teamName;
    /** 2-社长 1-社团管理员 */
    private Integer role;
}
