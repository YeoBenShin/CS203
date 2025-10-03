package CS203G3.tariff_backend.config;

import CS203G3.tariff_backend.interceptor.RateLimitInterceptor;
import CS203G3.tariff_backend.interceptor.SSRFPreventionInterceptor;
import CS203G3.tariff_backend.security.URLValidator;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RateLimitConfig rateLimitConfig;
    private final URLValidator urlValidator;
    private final SSRFPreventionInterceptor ssrfPreventionInterceptor;

    @Value("${FRONTEND_URL:http://localhost:3000}")
    private String frontendUrl;

    public WebConfig(RateLimitConfig rateLimitConfig, URLValidator urlValidator, SSRFPreventionInterceptor ssrfPreventionInterceptor) {
        this.rateLimitConfig = rateLimitConfig;
        this.urlValidator = urlValidator;
        this.ssrfPreventionInterceptor = ssrfPreventionInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Validate frontend URL before adding to CORS configuration
        if (!urlValidator.isValidURL(frontendUrl)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid frontend URL configuration");
        }
        
        registry.addMapping("/api/**")
            .allowedOrigins(frontendUrl)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add SSRF prevention interceptor first
        registry.addInterceptor(ssrfPreventionInterceptor)
                .addPathPatterns("/api/**");

        // Add rate limiting interceptor
        registry.addInterceptor(new RateLimitInterceptor(rateLimitConfig))
                .addPathPatterns("/api/**") // Apply to all API endpoints
                .excludePathPatterns("/api/public/**", // Exclude public endpoints
                                   "/api/products",    // Exclude product listing
                                   "/api/countries",   // Exclude country listing
                                   "/api/tariffs/**"); // Exclude tariff endpoints for now
    }
}