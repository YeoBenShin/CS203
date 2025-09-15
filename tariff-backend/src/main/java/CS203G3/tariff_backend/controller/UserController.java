package CS203G3.tariff_backend.controller;

import CS203G3.tariff_backend.model.User;
import CS203G3.tariff_backend.repository.UserRepository;
import CS203G3.tariff_backend.webhook.ClerkWebhookVerifier;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
class UserController {
    @Autowired
    private UserRepository userRepository;

    // @Autowired
    // private ClerkWebhookVerifier verifier;

    @PostMapping("/clerk/webhook/user") // Map ONLY POST Requests
    public @ResponseBody ResponseEntity<String> addNewUser (@RequestHeader Map<String, String> headers,
            @RequestBody Map<String, Object> body) {
            
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        String uuid = (String) data.get("id");
        System.out.println("uuid: " + uuid);

        // boolean valid = verifier.verifySignature(body, headers.get("svix-signature"));

        // if (!valid) {
        //     return ResponseEntity.status(401).body("Invalid signature");
        // }

        userRepository.save(new User(uuid, false));
        return ResponseEntity.ok("Webhook processed. New user added");
    }

    @GetMapping("/user")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/")
    public @ResponseBody String healthCheck() {
        return "all is good";
    }
}