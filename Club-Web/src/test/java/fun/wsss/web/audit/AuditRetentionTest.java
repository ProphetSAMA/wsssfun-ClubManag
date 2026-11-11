package fun.wsss.web.audit;

import fun.wsss.web.audit.mapper.AuditLogMapper;
import fun.wsss.web.audit.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * 审计日志保留策略：按天数清理，0 表示不清理。
 */
@SpringBootTest
class AuditRetentionTest {

    @Autowired
    private AuditLogService auditLogService;

    @MockBean
    private AuditLogMapper auditLogMapper;

    @Test
    void deletesRecordsOlderThanRetention() {
        given(auditLogMapper.delete(any())).willReturn(12);

        int removed = auditLogService.cleanBefore(90);

        assertThat(removed).isEqualTo(12);
        verify(auditLogMapper).delete(any());
    }

    @Test
    void zeroDisablesCleanup() {
        int removed = auditLogService.cleanBefore(0);

        assertThat(removed).isZero();
        verify(auditLogMapper, never()).delete(any());
    }
}
