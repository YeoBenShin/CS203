package CS203G3.tariff_backend.config;

import CS203G3.tariff_backend.interceptor.RateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RateLimitConfig rateLimitConfig;

    @Value("${FRONTEND_URL:http://localhost:3000}")
    private String frontendUrl;

    public WebConfig(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins(frontendUrl)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitInterceptor(rateLimitConfig))
                .addPathPatterns("/api/**") // Apply to all API endpoints
                .excludePathPatterns("/api/public/**", // Exclude public endpoints
                                   "/api/products",    // Exclude product listing
                                   "/api/countries",   // Exclude country listing
                                   "/api/tariffs/**"); // Exclude tariff endpoints for now
    }
}