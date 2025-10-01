package CS203G3.tariff_backend.interceptor;

import CS203G3.tariff_backend.security.URLValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SSRFPreventionInterceptor implements HandlerInterceptor {
    
    private final URLValidator urlValidator;
    
    public SSRFPreventionInterceptor(URLValidator urlValidator) {
        this.urlValidator = urlValidator;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Get the Host header
        String host = request.getHeader("Host");
        if (host == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        // Get the Referer header
        String referer = request.getHeader("Referer");
        if (referer != null && !urlValidator.isValidURL(referer)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        // Get the Origin header
        String origin = request.getHeader("Origin");
        if (origin != null && !urlValidator.isValidURL(origin)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        // Check for common SSRF headers
        String[] ssrfHeaders = {
            "X-Forwarded-For",
            "X-Forwarded-Host",
            "X-Forwarded-Proto",
            "X-Client-IP",
            "Client-IP",
            "X-Custom-IP-Authorization",
            "X-Originating-IP",
            "X-Remote-IP",
            "X-Remote-Addr"
        };

        for (String header : ssrfHeaders) {
            String value = request.getHeader(header);
            if (value != null && !urlValidator.isValidURL("http://" + value)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }

        return true;
    }
}