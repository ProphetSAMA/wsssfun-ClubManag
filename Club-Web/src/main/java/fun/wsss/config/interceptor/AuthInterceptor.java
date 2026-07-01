package fun.wsss.config.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import fun.wsss.config.jwt.JwtUtils;
import fun.wsss.status.StatusCode;
import fun.wsss.utils.ResultUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录认证拦截器
 *
 * @author Wsssfun
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    public AuthInterceptor(JwtUtils jwtUtils, ObjectMapper objectMapper) {
        this.jwtUtils = jwtUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            writeError(response, "未登录，请先登录", StatusCode.UNAUTHORIZED);
            return false;
        }

        if (!jwtUtils.validateToken(token)) {
            writeError(response, "登录已过期，请重新登录", StatusCode.UNAUTHORIZED);
            return false;
        }

        // 将用户信息存入 request 属性，供 Controller 使用
        request.setAttribute("userId", jwtUtils.getUserId(token));
        request.setAttribute("username", jwtUtils.getUsername(token));
        request.setAttribute("isAdmin", jwtUtils.getIsAdmin(token));
        return true;
    }

    private void writeError(HttpServletResponse response, String msg, int code) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200);
        response.getWriter().write(objectMapper.writeValueAsString(ResultUtils.error(msg, code)));
    }
}
