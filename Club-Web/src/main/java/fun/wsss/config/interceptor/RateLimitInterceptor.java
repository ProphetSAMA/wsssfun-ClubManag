package fun.wsss.config.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import fun.wsss.status.StatusCode;
import fun.wsss.utils.ResultUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 接口限流
 *
 * <p>按"客户端 + 分组"做每分钟固定窗口计数：
 * <ul>
 *   <li>auth：登录、注册、找回密码，最容易被撞库，限制最严；</li>
 *   <li>write：其它写操作；</li>
 *   <li>read：读操作，给得宽松一些。</li>
 * </ul>
 * 单实例内存实现，多实例部署需要换成 Redis 计数。
 *
 * @author Wsssfun
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    /** 一分钟窗口内的计数 */
    private static final class Window {
        private long minute;
        private int count;
    }

    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    @Value("${security.rate-limit.enabled:true}")
    private boolean enabled;

    @Value("${security.rate-limit.auth-per-minute:20}")
    private int authLimit;

    @Value("${security.rate-limit.write-per-minute:60}")
    private int writeLimit;

    @Value("${security.rate-limit.read-per-minute:300}")
    private int readLimit;

    public RateLimitInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!enabled) {
            return true;
        }
        String method = request.getMethod().toUpperCase();
        if ("OPTIONS".equals(method)) {
            return true;
        }

        String path = request.getRequestURI();
        String group;
        int limit;
        if (isAuthPath(path)) {
            group = "auth";
            limit = authLimit;
        } else if ("GET".equals(method) || "HEAD".equals(method)) {
            group = "read";
            limit = readLimit;
        } else {
            group = "write";
            limit = writeLimit;
        }

        String key = clientIp(request) + "|" + group;
        if (!allow(key, limit)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(200);
            response.getWriter().write(objectMapper.writeValueAsString(
                    ResultUtils.error("请求过于频繁，请稍后再试", StatusCode.TOO_MANY_REQUESTS)));
            return false;
        }
        return true;
    }

    private boolean isAuthPath(String path) {
        return path.startsWith("/api/user/login")
                || path.startsWith("/api/user/register")
                || path.startsWith("/api/user/forgot")
                || path.startsWith("/api/user/refresh");
    }

    /**
     * 固定窗口计数
     */
    private boolean allow(String key, int limit) {
        long minute = System.currentTimeMillis() / 60_000L;
        Window window = windows.computeIfAbsent(key, k -> new Window());
        synchronized (window) {
            if (window.minute != minute) {
                window.minute = minute;
                window.count = 0;
            }
            window.count++;
            if (windows.size() > 10_000) {
                // 简单自清理，防止长期运行后堆积
                windows.entrySet().removeIf(entry -> entry.getValue().minute != minute);
            }
            return window.count <= limit;
        }
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            int comma = forwarded.indexOf(',');
            return (comma > 0 ? forwarded.substring(0, comma) : forwarded).trim();
        }
        return request.getRemoteAddr();
    }
}
