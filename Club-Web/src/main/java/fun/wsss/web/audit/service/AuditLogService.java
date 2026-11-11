package fun.wsss.web.audit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fun.wsss.web.audit.entity.AuditLog;

/**
 * 审计日志
 *
 * @author Wsssfun
 */
public interface AuditLogService extends IService<AuditLog> {

    /**
     * 清理 N 天前的日志
     *
     * @param retentionDays 保留天数，<= 0 表示不清理
     * @return 删除条数
     */
    int cleanBefore(int retentionDays);
}
