package com.misonamoo.niaportal.config;

import com.misonamoo.niaportal.interceptor.BaseHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public BaseHandlerInterceptor baseHandlerInterceptor() {
        return new BaseHandlerInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(baseHandlerInterceptor());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 uri에 대해 http://localhost:8080, http://localhost:3000 접근을 허용한다.
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080", "http://localhost:3000");
    }
}
