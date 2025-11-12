package CS203G3.tariff_backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import CS203G3.tariff_backend.security.URLValidator;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test Security Configuration
 * Disables Spring Security for controller and integration tests
 * This allows tests to run without authentication
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
    
    @Bean
    public URLValidator urlValidator() {
        URLValidator mockValidator = mock(URLValidator.class);
        // Mock the URLValidator to always return true for tests
        when(mockValidator.isValidURL(anyString())).thenReturn(true);
        return mockValidator;
    }
}
