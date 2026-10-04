package com.jnotifier.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> customCorsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of("http://localhost:4242", "http://localhost:4243",
                "https://admin.jnotifier.devapps.codingworks.in", "https://jnotifier.devapps.codingworks.in",
                "https://admin.jnotifier.codingworks.in", "https://jobnotifier.codingworks.in","https://www.thejobnotifier.in",
                "https://thejobnotifier.in"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // CRITICAL FIX: Allow all headers (Content-Type, Authorization, etc.)
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Retry-After"));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));

        // MAGIC LINE: This ensures CORS is the very first thing processed,
        // ahead of Spring Security and any exception handlers.
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return bean;
    }
}