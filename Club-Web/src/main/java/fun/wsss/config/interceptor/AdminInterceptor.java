package fun.wsss.config.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import fun.wsss.status.StatusCode;
import fun.wsss.utils.ResultUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * 管理接口鉴权拦截器
 *
 * <p>规则：
 * <ol>
 *   <li>写操作（非 GET / HEAD / OPTIONS）必须是管理员；</li>
 *   <li>读操作默认放行，但 {@code ADMIN_ONLY_READ_PATHS} 里的接口例外。</li>
 * </ol>
 * 权限必须由 token 里的 {@code isAdmin} 决定，而不是只靠前端路由守卫。
 *
 * <p>成员模块不在拦截范围内：加入 / 退出社团是普通用户的正常操作，
 * 其权限已由 {@code MemberController} 按记录归属判定。
 *
 * @author Wsssfun
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    /**
     * 只读但同样只给管理员看的接口（会暴露全员手机号等隐私信息）
     */
    private static final Set<String> ADMIN_ONLY_READ_PATHS = Set.of(
            "/api/user/list",
            "/api/stats/overview"
    );

    /**
     * 自助接口：登录用户操作自己的数据，不属于管理操作
     */
    private static final Set<String> SELF_SERVICE_PATHS = Set.of(
            "/api/user/profile",
            "/api/user/password"
    );

    private final ObjectMapper objectMapper;

    public AdminInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // 读操作默认放行（列表、详情等游客也可能要用），除少数只给管理员的接口
        boolean isRead = "GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method);
        if (isRead && !ADMIN_ONLY_READ_PATHS.contains(request.getRequestURI())) {
            return true;
        }

        // isAdmin 由 AuthInterceptor 从 token 解析后放进 request
        Object isAdmin = request.getAttribute("isAdmin");
        if (isAdmin instanceof Integer && (Integer) isAdmin == 1) {
            return true;
        }

        // 改自己的资料/密码不需要管理员，token 已在 AuthInterceptor 校验过
        if (SELF_SERVICE_PATHS.contains(request.getRequestURI())) {
            return true;
        }

        writeError(response, "没有权限执行该操作", StatusCode.FORBIDDEN);
        return false;
    }

    private void writeError(HttpServletResponse response, String msg, int code) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200);
        response.getWriter().write(objectMapper.writeValueAsString(ResultUtils.error(msg, code)));
    }
}
