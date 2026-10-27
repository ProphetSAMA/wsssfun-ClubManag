package fun.wsss.config.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import fun.wsss.config.jwt.JwtUtils;
import fun.wsss.status.StatusCode;
import fun.wsss.utils.ResultUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * 登录认证拦截器
 *
 * <p>公开接口（首页、列表、详情等）允许游客访问，但**如果带了有效 token 仍会解析身份**，
 * 这样同一个接口对不同身份可以给出不同结果（例如管理员能看到草稿）。
 * 之前把公开接口写在 excludePathPatterns 里，拦截器完全不执行，拿不到 isAdmin。
 *
 * @author Wsssfun
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /** 游客可访问的接口 */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/user/login",
            "/api/user/register",
            "/api/news/list",
            "/api/news/detail/**",
            "/api/news/getTopActivity",
            "/api/news/getTopNotice",
            "/api/news/getNewActivity",
            "/api/news/getNewNotice",
            "/api/team/list",
            "/api/team/getTopTeam",
            "/api/category/list",
            "/api/category/getSelectList",
            "/api/signup/count"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

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

        boolean publicPath = isPublicPath(request.getRequestURI());
        String token = request.getHeader("token");

        if (token != null && !token.isEmpty()) {
            if (jwtUtils.validateToken(token)) {
                // 将用户信息存入 request 属性，供 Controller / AdminInterceptor 使用
                request.setAttribute("userId", jwtUtils.getUserId(token));
                request.setAttribute("username", jwtUtils.getUsername(token));
                request.setAttribute("isAdmin", jwtUtils.getIsAdmin(token));
                return true;
            }
            if (!publicPath) {
                writeError(response, "登录已过期，请重新登录", StatusCode.UNAUTHORIZED);
                return false;
            }
            // 公开接口上带了失效 token：按游客处理
            return true;
        }

        if (publicPath) {
            return true;
        }

        writeError(response, "未登录，请先登录", StatusCode.UNAUTHORIZED);
        return false;
    }

    private boolean isPublicPath(String uri) {
        for (String pattern : PUBLIC_PATHS) {
            if (pathMatcher.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }

    private void writeError(HttpServletResponse response, String msg, int code) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200);
        response.getWriter().write(objectMapper.writeValueAsString(ResultUtils.error(msg, code)));
    }
}
