package fun.wsss.web.audit.task;

import fun.wsss.web.audit.service.AuditLogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 审计日志清理
 *
 * <p>审计日志是"只增不读"的表，不清理会一直涨。每天凌晨删掉保留期之外的记录，
 * 保留天数由 security.audit.retention-days 配置（默认 90 天，设 0 关闭）。
 *
 * @author Wsssfun
 */
@Component
public class AuditCleanTask {

    private final AuditLogService auditLogService;

    @Value("${security.audit.retention-days:90}")
    private int retentionDays;

    public AuditCleanTask(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Scheduled(cron = "0 30 3 * * ?")
    public void clean() {
        auditLogService.cleanBefore(retentionDays);
    }
}
