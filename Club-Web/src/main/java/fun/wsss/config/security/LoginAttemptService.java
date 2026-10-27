package fun.wsss.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录失败次数限制
 *
 * <p>同一账号连续失败达到上限后锁定一段时间，抵御最简单的在线撞库。
 * 内存实现，重启即清零；多实例部署需要换成 Redis。
 *
 * @author Wsssfun
 */
@Component
public class LoginAttemptService {

    private static final class Attempt {
        private int count;
        private long lockedUntil;
    }

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    @Value("${security.login.max-attempts:5}")
    private int maxAttempts;

    @Value("${security.login.lock-minutes:15}")
    private long lockMinutes;

    /**
     * 账号是否处于锁定期
     *
     * @param username 登录账号
     * @return 剩余锁定秒数，0 表示未锁定
     */
    public long lockedSeconds(String username) {
        Attempt attempt = attempts.get(username);
        if (attempt == null || attempt.lockedUntil == 0) {
            return 0;
        }
        long remain = attempt.lockedUntil - System.currentTimeMillis();
        if (remain <= 0) {
            attempts.remove(username);
            return 0;
        }
        return (remain + 999) / 1000;
    }

    /**
     * 记一次失败
     *
     * @param username 登录账号
     */
    public void onFailure(String username) {
        Attempt attempt = attempts.computeIfAbsent(username, key -> new Attempt());
        synchronized (attempt) {
            attempt.count++;
            if (attempt.count >= maxAttempts) {
                attempt.lockedUntil = System.currentTimeMillis() + lockMinutes * 60_000L;
                attempt.count = 0;
            }
        }
    }

    /**
     * 登录成功后清除计数
     *
     * @param username 登录账号
     */
    public void onSuccess(String username) {
        attempts.remove(username);
    }
}
