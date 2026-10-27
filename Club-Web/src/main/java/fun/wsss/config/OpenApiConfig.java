package fun.wsss.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 接口文档配置
 *
 * <p>访问 /swagger-ui.html 查看交互式文档，JSON 见 /v3/api-docs。
 * 除公开接口外，请求头需要带登录返回的 token。
 *
 * @author Wsssfun
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI clubOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("高校社团管理系统 API")
                .description("门户与后台共用的一套接口；除公开接口外，请求头需带 token")
                .version("1.0.0"));
    }
}
