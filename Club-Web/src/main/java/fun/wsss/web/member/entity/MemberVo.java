package fun.wsss.web.member.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 成员视图对象（包含关联字段）
 *
 * @author Wsssfun
 */
@Data
public class MemberVo {
    private Long id;
    private Long teamId;
    private Long userId;
    private Integer role;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date joinTime;

    /**
     * 关联字段
     */
    private String teamName;
    private String userName;
}
