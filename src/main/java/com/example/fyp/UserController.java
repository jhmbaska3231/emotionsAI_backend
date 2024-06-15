package com.example.fyp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO, @AuthenticationPrincipal Jwt jwt) {
        logger.info("Received request to create user: {}", userDTO);

        // Retrieve email from JWT token
        String email = jwt != null ? jwt.getClaim("email") : userDTO.getEmail();
        if (email == null) {
            logger.error("Email is null in the request");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userDTO.setEmail(email);

        // Set userId if present in JWT token
        String userId = jwt != null ? jwt.getSubject() : null;
        if (userId != null) {
            userDTO.setUserId(userId);
        }

        User user = convertToEntity(userDTO);
        User createdUser = userService.createUser(user);
        logger.info("User created successfully: {}", createdUser);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/{freeUserId}/upgrade")
    public ResponseEntity<PaidUser> upgradeToPaidUser(@PathVariable String freeUserId, @RequestParam SubscriptionPlan subscriptionPlan) {
        PaidUser upgradedUser = userService.upgradeToPaidUser(freeUserId, subscriptionPlan);
        logger.info("User with ID {} upgraded to paid user: {}", freeUserId, upgradedUser);
        return new ResponseEntity<>(upgradedUser, HttpStatus.OK);
    }

    private User convertToEntity(UserDTO userDTO) {
        User user;
        switch (userDTO.getUserType()) {
            case "AdminUser":
                user = new AdminUser();
                break;
            case "FreeUser":
                user = new FreeUser();
                ((FreeUser) user).setTranscribeCount(0);
                break;
            case "PaidUser":
                user = new PaidUser();
                break;
            default:
                throw new IllegalArgumentException("Invalid user type");
        }
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        // Set userId if present
        if (userDTO.getUserId() != null) {
            user.setUserId(userDTO.getUserId());
        }
        return user;
    }

}
