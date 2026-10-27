package fun.wsss.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 工具类
 *
 * @author Wsssfun
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret:club-manag-default-secret-key-at-least-32bytes}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    /**
     * 启动时校验密钥：不允许为空或过短，避免带着默认弱密钥上线
     */
    @PostConstruct
    void validateSecret() {
        int length = secret == null ? 0 : secret.getBytes(StandardCharsets.UTF_8).length;
        if (length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET 未配置或长度不足 32 字节（当前 " + length + "）。"
                            + "请在环境变量或 .env 里设置一个足够长的随机字符串，不要写进仓库。");
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param isAdmin  是否管理员
     * @return token
     */
    public String generateToken(Long userId, String username, Integer isAdmin) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                // jti 用于登出后把该 token 加入黑名单
                .id(UUID.randomUUID().toString())
                .subject(username)
                .claim("userId", userId)
                .claim("isAdmin", isAdmin)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析 Token
     *
     * @param token JWT token
     * @return Claims
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从 Token 中获取用户ID
     */
    public Long getUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    /**
     * 从 Token 中获取令牌 ID（jti）
     */
    public String getJti(String token) {
        return parseToken(token).getId();
    }

    /**
     * 从 Token 中获取过期时间
     */
    public Date getExpiration(String token) {
        return parseToken(token).getExpiration();
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsername(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 从 Token 中获取管理员标识
     */
    public Integer getIsAdmin(String token) {
        return parseToken(token).get("isAdmin", Integer.class);
    }
}
