package fun.wsss.web.audit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.wsss.web.audit.entity.AuditLog;
import fun.wsss.web.audit.mapper.AuditLogMapper;
import fun.wsss.web.audit.service.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 审计日志
 *
 * @author Wsssfun
 */
@Service
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog> implements AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    @Override
    public int cleanBefore(int retentionDays) {
        if (retentionDays <= 0) {
            return 0;
        }
        Date before = new Date(System.currentTimeMillis() - retentionDays * 24L * 60 * 60 * 1000);
        QueryWrapper<AuditLog> query = new QueryWrapper<>();
        query.lambda().lt(AuditLog::getCreateTime, before);
        int removed = baseMapper.delete(query);
        if (removed > 0) {
            log.info("清理 {} 天前的操作日志，共 {} 条", retentionDays, removed);
        }
        return removed;
    }
}
