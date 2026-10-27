package fun.wsss.web.teamapply.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 社团成立申请
 *
 * @author Wsssfun
 */
@Data
@TableName("team_apply")
public class TeamApply {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 拟成立的社团名称 */
    private String name;

    private Long categoryId;

    /** 申请理由 */
    private String reason;

    /** 申请人 */
    private Long applicantId;

    /** 0-待审核 1-已通过 2-已驳回 */
    private Integer status;

    private Long reviewId;

    private Date reviewTime;

    private String rejectReason;

    /** 通过后创建的社团 */
    private Long teamId;

    private Date createTime;
}
