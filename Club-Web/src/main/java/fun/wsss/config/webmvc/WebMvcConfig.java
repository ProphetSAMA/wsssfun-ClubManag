package fun.wsss.config.webmvc;

import fun.wsss.config.interceptor.AdminInterceptor;
import fun.wsss.config.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 *
 * @author Wsssfun
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${web.load-path}")
    private String loadPath;

    /** 允许跨域的前端地址，逗号分隔 */
    @Value("${web.cors.allowed-origins:http://localhost:8080,http://localhost:8081}")
    private String allowedOrigins;

    private final AuthInterceptor authInterceptor;
    private final AdminInterceptor adminInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor, AdminInterceptor adminInterceptor) {
        this.authInterceptor = authInterceptor;
        this.adminInterceptor = adminInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry register) {
        // 只放行配置里的前端地址；本项目用请求头 token 鉴权、不用 Cookie，所以关掉 allowCredentials
        String[] origins = allowedOrigins.split(",");
        for (int i = 0; i < origins.length; i++) {
            origins[i] = origins[i].trim();
        }
        register.addMapping("/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("token", "Content-Type")
                .maxAge(3600)
                .allowCredentials(false);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 公开接口由 AuthInterceptor 内部判断（这样公开接口也能解析登录身份）
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**");

        // 管理接口的写操作必须是管理员；成员模块除外（按记录归属判定）
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns(
                        "/api/user/**",
                        "/api/team/**",
                        "/api/category/**",
                        "/api/news/**",
                        "/api/upload/**",
                        "/api/stats/**"
                )
                .excludePathPatterns("/api/user/login", "/api/user/register");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(loadPath);
    }
}
