package fun.wsss.config.jwt;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 已注销 token 的黑名单
 *
 * <p>JWT 是无状态的，本身无法撤销；这里在内存里记录被注销的 jti 及其过期时间，
 * 拦截器每次校验时查一下。单实例部署够用，多实例需要换成 Redis 之类的共享存储。
 *
 * @author Wsssfun
 */
@Component
public class TokenBlacklist {

    /** jti -> 过期时间戳（毫秒），过期后自动清理 */
    private final Map<String, Long> revoked = new ConcurrentHashMap<>();

    private static final int CLEANUP_THRESHOLD = 1000;

    /**
     * 注销一个 token
     *
     * @param jti        令牌 ID
     * @param expiresAt 过期时间戳（毫秒）
     */
    public void revoke(String jti, long expiresAt) {
        if (jti == null || jti.isEmpty()) {
            return;
        }
        revoked.put(jti, expiresAt);
        if (revoked.size() > CLEANUP_THRESHOLD) {
            long now = System.currentTimeMillis();
            revoked.entrySet().removeIf(entry -> entry.getValue() < now);
        }
    }

    /**
     * 是否已注销
     *
     * @param jti 令牌 ID
     * @return true 表示这个 token 已经登出，不能再使用
     */
    public boolean isRevoked(String jti) {
        if (jti == null || jti.isEmpty()) {
            return false;
        }
        Long expiresAt = revoked.get(jti);
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt < System.currentTimeMillis()) {
            revoked.remove(jti);
            return false;
        }
        return true;
    }

    /** 仅供测试/排查使用 */
    public int size() {
        return revoked.size();
    }
}
