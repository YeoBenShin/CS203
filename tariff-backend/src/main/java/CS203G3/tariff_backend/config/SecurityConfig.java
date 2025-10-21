package CS203G3.tariff_backend.config;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;

import CS203G3.tariff_backend.repository.UserRepository;
import CS203G3.tariff_backend.model.User;

@Configuration
public class SecurityConfig {

    @Value("${clerk.jwk-set-uri}")
    private String jwkSetUri;

    private UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configure(http))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/health").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/tariffs").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,"/api/tariffs/{tariffId}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/tariffs/{tariffId}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,"/api/countries").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,"/api/countries/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/countries/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,"/api/products").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,"/api/products/{hSCode}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/products/{hSCode}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/products/{hSCode}").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwkSetUri(jwkSetUri)
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            ); // enable JWT validation;
            // jwkSetUri + Spring's oauth2ResourceServer is what validates the token (signature, exp, nbf);
        return http.build();
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        // default extraction from scope / authorities if present
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("SCOPE_"); // keep as-is for scopes if used

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // start with default granted authorities (scopes)
            java.util.List<GrantedAuthority> authorities = new java.util.ArrayList<>();
            authorities.addAll(grantedAuthoritiesConverter.convert(jwt)
                    .stream()
                    .map(g -> new SimpleGrantedAuthority(g.getAuthority()))
                    .collect(Collectors.toList()));

            String userId = jwt.getSubject();
            if (userId != null) {
                User user = userRepository.findByUuid(userId);
                // System.out.println("User ID from JWT: " + userId);
                // System.out.println("User fetched from DB: " + user);
                // System.out.println("Is user admin? " + (user != null ? user.isAdmin() : "N/A"));
                if (user != null && user.isAdmin()) {
                    // System.out.println("Assigning ROLE_ADMIN to user: " + userId);
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }
            }
            return authorities;
        });

        return converter;
    }
}
