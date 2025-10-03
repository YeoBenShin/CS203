package CS203G3.tariff_backend.interceptor;

import CS203G3.tariff_backend.config.RateLimitConfig;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitConfig rateLimitConfig;

    public RateLimitInterceptor(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientId = getClientIdentifier(request);
        String endpoint = request.getRequestURI();
        
        Bucket bucket = rateLimitConfig.resolveBucket(clientId, endpoint);
        
        if (bucket.tryConsume(1)) {
            return true;
        }
        
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Retry-After", "60");
        response.getWriter().write("Rate limit exceeded. Please try again later.");
        return false;
    }

    private String getClientIdentifier(HttpServletRequest request) {
        // First try to get from authentication
        String userId = request.getHeader("X-User-ID"); // Assuming you set this in your auth filter
        if (userId != null && !userId.isEmpty()) {
            return userId;
        }
        
        // Fallback to IP address
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        return request.getRemoteAddr();
    }
}