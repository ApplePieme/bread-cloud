package com.breadme.breadcloud.config;

import com.breadme.breadcloud.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 鉴权配置类
 *
 * @author breadme@foxmail.com
 * @date 2022/4/27 19:44
 */
@Configuration
public class AuthConfig implements WebMvcConfigurer {
    @Bean
    public AuthInterceptor authInterceptor() {
        return new AuthInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(authInterceptor());
        registration.addPathPatterns("/**");
        registration.excludePathPatterns("/login", "/register");
    }
}
