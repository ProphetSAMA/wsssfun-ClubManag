package fun.wsss.config.interceptor;

import fun.wsss.web.audit.entity.AuditLog;
import fun.wsss.web.audit.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Date;
import java.util.Set;

/**
 * 操作审计拦截器
 *
 * <p>只记录"会改数据"的请求（POST / PUT / DELETE / PATCH），读请求不记，避免日志被刷爆。
 * 出于安全考虑不记录请求体——登录、改密码这些接口的参数里有明文密码。
 *
 * @author Wsssfun
 */
@Component
public class AuditInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuditInterceptor.class);

    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "DELETE", "PATCH");

    private static final String START_ATTR = "auditStart";

    private final AuditLogService auditLogService;

    public AuditInterceptor(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_ATTR, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        if (!WRITE_METHODS.contains(request.getMethod().toUpperCase())) {
            return;
        }
        try {
            AuditLog record = new AuditLog();
            Object userId = request.getAttribute("userId");
            if (userId instanceof Long) {
                record.setUserId((Long) userId);
            }
            Object username = request.getAttribute("username");
            if (username instanceof String) {
                record.setUsername((String) username);
            }
            record.setMethod(request.getMethod().toUpperCase());
            String path = request.getRequestURI();
            record.setPath(path.length() > 200 ? path.substring(0, 200) : path);
            record.setIp(clientIp(request));
            record.setStatus(response.getStatus());
            Object start = request.getAttribute(START_ATTR);
            if (start instanceof Long) {
                record.setDurationMs((int) Math.min(Integer.MAX_VALUE, System.currentTimeMillis() - (Long) start));
            }
            record.setCreateTime(new Date());
            auditLogService.save(record);
        } catch (Exception e) {
            // 审计失败不影响业务
            log.warn("写入审计日志失败 path={}", request.getRequestURI(), e);
        }
    }

    /**
     * 取真实客户端 IP（兼容反向代理）
     */
    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            int comma = forwarded.indexOf(',');
            return (comma > 0 ? forwarded.substring(0, comma) : forwarded).trim();
        }
        return request.getRemoteAddr();
    }
}
