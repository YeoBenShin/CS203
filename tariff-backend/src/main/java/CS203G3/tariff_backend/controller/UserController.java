package CS203G3.tariff_backend.controller;

import CS203G3.tariff_backend.model.User;
import CS203G3.tariff_backend.repository.UserRepository;
// import CS203G3.tariff_backend.webhook.ClerkWebhookVerifier;

import java.util.Map;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class UserController {
    // private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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

    @GetMapping("/user/{uuid}")
    public @ResponseBody ResponseEntity<User> getUser(@PathVariable String uuid) {
        User user = userRepository.findByUuid(uuid);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/")
    public @ResponseBody String healthCheck() {
        return "all is good";
    }
}