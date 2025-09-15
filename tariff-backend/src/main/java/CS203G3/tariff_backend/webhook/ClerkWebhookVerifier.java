package CS203G3.tariff_backend.webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class ClerkWebhookVerifier {

    @Value("${clerk.webhook.signing-secret}")
    private String signingSecret;

    public boolean verifySignature(String payload, String headerSignature) {
        try {
            // Extract actual signature (after "v1=")
            String signature = headerSignature.split(",")[0].replace("v1=", "").trim();

            // Compute HMAC SHA256 of the payload
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(signingSecret.getBytes(), "HmacSHA256");
            hmac.init(secretKeySpec);
            byte[] computedHash = hmac.doFinal(payload.getBytes());

            String computedSignature = Base64.getEncoder().encodeToString(computedHash);

            return computedSignature.equals(signature);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
