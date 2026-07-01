package fun.wsss.web.member.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 社团成员实体类
 *
 * @author Wsssfun
 */
@Data
@TableName("member")
public class Member {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 社团ID
     */
    private Long teamId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色: 0-普通成员, 1-管理员, 2-社长
     */
    private Integer role;

    /**
     * 状态: 0-已退出, 1-正常
     */
    private Integer status;

    /**
     * 加入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date joinTime;
}
