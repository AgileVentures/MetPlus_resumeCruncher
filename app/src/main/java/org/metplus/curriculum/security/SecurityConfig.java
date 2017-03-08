package org.metplus.curriculum.security;

import org.metplus.curriculum.security.filters.ApplicationLoginFilter;
import org.metplus.curriculum.security.filters.ApplicationTokenAuthenticationFilter;
import org.metplus.curriculum.security.services.LocalTokenService;
import org.metplus.curriculum.security.services.TokenService;
import org.metplus.curriculum.security.useCases.UserTryToLogin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebMvcConfigurerAdapter {

    String username = "backend_admin";
    String password = "backendpassword";

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(applicationLoginFilter()).addPathPatterns("/**/authenticate");
        registry.addInterceptor(applicationTokenAuthenticationFilter()).addPathPatterns("**");
    }

    private ApplicationLoginFilter applicationLoginFilter() {
        return new ApplicationLoginFilter(useCaseUserTryToLogin(), localTokenService());
    }

    private ApplicationTokenAuthenticationFilter applicationTokenAuthenticationFilter() {
        return new ApplicationTokenAuthenticationFilter(localTokenService());
    }

    private UserTryToLogin useCaseUserTryToLogin() {
        return new UserTryToLogin(username, password);
    }

    @Bean
    public TokenService localTokenService() {
        return new LocalTokenService();
    }
}
