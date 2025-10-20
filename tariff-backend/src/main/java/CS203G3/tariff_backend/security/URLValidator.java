package CS203G3.tariff_backend.security;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class URLValidator {
    private final Set<String> allowedDomains;

    public URLValidator(@Value("${FRONTEND_ORIGIN:localhost,127.0.0.1}") String allowedDomainsStr) {
        this.allowedDomains = new HashSet<>(Arrays.asList(allowedDomainsStr.split(",")));
    }

    public boolean isValidURL(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host == null) {
                return false;
            }

            // Check if host is in allowed domains or is a subdomain
            return allowedDomains.stream()
                    .anyMatch(domain -> host.equals(domain) || host.endsWith("." + domain));

        } catch (Exception e) {
            return false;
        }
    }
}