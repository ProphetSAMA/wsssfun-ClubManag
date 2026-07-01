package fun.wsss.config.webmvc;

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

    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry register) {
        register.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .maxAge(3600)
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/news/getTopActivity",
                        "/api/news/getTopNotice",
                        "/api/news/getNewActivity",
                        "/api/news/getNewNotice",
                        "/api/news/list",
                        "/api/team/list",
                        "/api/team/getTopTeam",
                        "/api/category/list",
                        "/api/category/getSelectList",
                        "/api/member/list"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(loadPath);
    }
}
