// 文件路径: src/main/java/com/example/pocketbank_plus/config/CorsConfig.java

package com.example.pocketbank_plus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许所有来源（开发阶段可以这么用）
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // Spring Boot 2.4+ 推荐使用 allowedOriginPatterns 代替 allowedOrigins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // 如果前端需要携带 cookie 等凭证，需要设为 true
    }
}