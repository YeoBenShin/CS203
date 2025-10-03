package CS203G3.tariff_backend.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Different rate limits for different endpoints
    private static final int CALCULATION_LIMIT = 100;  // 100 requests
    private static final int TARIFF_CREATION_LIMIT = 50;  // 50 requests
    private static final int GENERAL_LIMIT = 200;  // 200 requests
    private static final Duration WINDOW_SIZE = Duration.ofMinutes(1);  // Per minute

    @Bean
    public Map<String, Bucket> buckets() {
        return buckets;
    }

    public Bucket resolveBucket(String key, String endpoint) {
        return buckets.computeIfAbsent(key, k -> createNewBucket(endpoint));
    }

    private Bucket createNewBucket(String endpoint) {
        int requestLimit = switch (endpoint.toLowerCase()) {
            case "/api/calculator/calculate" -> CALCULATION_LIMIT;
            case "/api/tariffs" -> TARIFF_CREATION_LIMIT;
            default -> GENERAL_LIMIT;
        };

        Bandwidth bandwidth = Bandwidth.builder()
            .capacity(requestLimit)
            .refillGreedy(requestLimit, WINDOW_SIZE)
            .initialTokens(requestLimit)
            .build();
            
        return Bucket.builder()
            .addLimit(bandwidth)
            .build();
    }
}