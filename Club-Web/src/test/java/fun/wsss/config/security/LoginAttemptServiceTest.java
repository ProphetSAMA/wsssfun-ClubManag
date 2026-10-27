package fun.wsss.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 登录失败计数：达到上限锁定，成功后清零。
 */
class LoginAttemptServiceTest {

    private LoginAttemptService service;

    @BeforeEach
    void setUp() {
        service = new LoginAttemptService();
        ReflectionTestUtils.setField(service, "maxAttempts", 3);
        ReflectionTestUtils.setField(service, "lockMinutes", 15L);
    }

    @Test
    void locksAfterMaxAttempts() {
        assertThat(service.lockedSeconds("u")).isZero();
        service.onFailure("u");
        service.onFailure("u");
        assertThat(service.lockedSeconds("u")).isZero();
        service.onFailure("u");
        assertThat(service.lockedSeconds("u")).isGreaterThan(0);
    }

    @Test
    void successResetsCounter() {
        service.onFailure("v");
        service.onFailure("v");
        service.onSuccess("v");
        service.onFailure("v");
        service.onFailure("v");
        assertThat(service.lockedSeconds("v")).isZero();
    }
}
