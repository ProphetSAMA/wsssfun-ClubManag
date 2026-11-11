package fun.wsss.web.audit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.wsss.web.audit.entity.AuditLog;
import fun.wsss.web.audit.mapper.AuditLogMapper;
import fun.wsss.web.audit.service.AuditLogService;
import org.springframework.stereotype.Service;

/**
 * 审计日志
 *
 * @author Wsssfun
 */
@Service
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog> implements AuditLogService {
}
