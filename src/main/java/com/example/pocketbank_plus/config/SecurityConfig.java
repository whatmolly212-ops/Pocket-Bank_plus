package com.example.pocketbank_plus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. 保留加密器，否则你的登录/注册功能会崩
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. 核心安全配置
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 禁用 CSRF，解决 POST 请求发不出去的问题
                .cors(cors -> cors.disable()) // 暂时禁用自带 cors，我们下面手动配置
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 🔴 重点：放行所有接口，让 AI 能够正常说话
                );
        return http.build();
    }

    // 3. 手动解决跨域（Axios 必备）
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // 允许所有来源
        config.addAllowedHeader("*"); // 允许所有请求头
        config.addAllowedMethod("*"); // 允许所有请求方式 (GET, POST 等)
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}