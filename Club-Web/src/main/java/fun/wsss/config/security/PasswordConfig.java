package fun.wsss.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密配置
 *
 * <p>只引入 spring-security-crypto，不使用完整的 Spring Security 过滤链：
 * 项目的鉴权由 {@code AuthInterceptor} + JWT 负责。
 *
 * @author Wsssfun
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
