package fun.wsss.web.audit.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 操作审计日志
 *
 * @author Wsssfun
 */
@Data
@TableName("audit_log")
public class AuditLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    /** HTTP 方法 */
    private String method;

    /** 请求路径（不含查询参数） */
    private String path;

    private String ip;

    /** HTTP 状态码 */
    private Integer status;

    /** 处理耗时（毫秒） */
    private Integer durationMs;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
