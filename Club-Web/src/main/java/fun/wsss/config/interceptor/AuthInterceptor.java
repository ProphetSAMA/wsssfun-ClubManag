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
 *
 * <p>公开规则按「方法 + 路径」匹配：同样是 /api/team/*，GET 是公开的详情，
 * DELETE 仍然需要登录，不会被误放行。
 *
 * @author Wsssfun
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /** 公开接口：方法 + Ant 路径 */
    private record Rule(String method, String pattern) {
    }

    private static final List<Rule> PUBLIC_RULES = List.of(
            new Rule("POST", "/api/user/login"),
            new Rule("POST", "/api/user/register"),
            new Rule("GET", "/api/news/list"),
            new Rule("GET", "/api/news/detail/**"),
            new Rule("GET", "/api/news/getTopActivity"),
            new Rule("GET", "/api/news/getTopNotice"),
            new Rule("GET", "/api/news/getNewActivity"),
            new Rule("GET", "/api/news/getNewNotice"),
            new Rule("GET", "/api/team/list"),
            new Rule("GET", "/api/team/getTopTeam"),
            new Rule("GET", "/api/team/*"),
            new Rule("GET", "/api/team/*/members"),
            new Rule("GET", "/api/category/list"),
            new Rule("GET", "/api/category/getSelectList"),
            new Rule("GET", "/api/signup/count")
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

        boolean publicPath = isPublic(request);
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

    private boolean isPublic(HttpServletRequest request) {
        String method = request.getMethod().toUpperCase();
        String uri = request.getRequestURI();
        for (Rule rule : PUBLIC_RULES) {
            if (rule.method().equals(method) && pathMatcher.match(rule.pattern(), uri)) {
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
