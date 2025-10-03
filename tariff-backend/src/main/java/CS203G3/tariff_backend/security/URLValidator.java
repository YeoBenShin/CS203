package CS203G3.tariff_backend.security;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class URLValidator {
    // Define allowed domains and IP ranges
    private static final Set<String> ALLOWED_DOMAINS = Set.of(
        "localhost",
        "127.0.0.1"
        // Add other allowed domains here
    );

    public boolean isValidURL(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            
            // Check if host is null (invalid URL)
            if (host == null) {
                return false;
            }

            // Check if host is in allowed domains
            if (!ALLOWED_DOMAINS.contains(host.toLowerCase())) {
                return false;
            }

            // Prevent accessing internal networks
            if (isInternalIP(host)) {
                return ALLOWED_DOMAINS.contains(host);
            }

            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private boolean isInternalIP(String host) {
        if (host.equals("localhost") || host.equals("127.0.0.1")) {
            return true;
        }

        // Check for private IP ranges
        String[] ipParts = host.split("\\.");
        if (ipParts.length != 4) {
            return false;
        }

        try {
            int firstOctet = Integer.parseInt(ipParts[0]);
            int secondOctet = Integer.parseInt(ipParts[1]);

            // Check for private IP ranges
            return (firstOctet == 10) ||                                  // 10.0.0.0/8
                   (firstOctet == 172 && secondOctet >= 16 && secondOctet <= 31) ||  // 172.16.0.0/12
                   (firstOctet == 192 && secondOctet == 168);            // 192.168.0.0/16
        } catch (NumberFormatException e) {
            return false;
        }
    }
}