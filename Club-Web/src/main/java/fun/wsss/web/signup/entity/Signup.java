package fun.wsss.web.signup.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 活动报名
 *
 * <p>同一用户对同一活动只保留一行：取消报名把 status 置 0，重新报名再置回 1。
 *
 * @author Wsssfun
 */
@Data
@TableName("activity_signup")
public class Signup {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 活动 id（news.type = '0' 的记录） */
    private Long newsId;

    /** 报名用户 */
    private Long userId;

    /** 1-已报名 0-已取消 */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
