package fun.wsss.web.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.wsss.web.audit.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审计日志
 *
 * @author Wsssfun
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {
}
