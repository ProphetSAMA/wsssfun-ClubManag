package fun.wsss.web.teamapply.entity;

import lombok.Data;

/**
 * 社团申请（后台列表，带申请人/分类名称）
 *
 * @author Wsssfun
 */
@Data
public class TeamApplyVo {
    private Long id;
    private String name;
    private String categoryName;
    private String reason;
    private String applicantName;
    private String phone;
    private Integer status;
    private String createTime;
    private String reviewTime;
    private String rejectReason;
    private Long teamId;
}
