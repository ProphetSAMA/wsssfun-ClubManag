package fun.wsss.web.member.entity;

import lombok.Data;

/**
 * 成员查询参数
 *
 * @author Wsssfun
 */
@Data
public class MemberParm {
    private Integer currentPage = 1;
    private Integer pageSize = 10;
    private Long teamId;
    private Long userId;
}
