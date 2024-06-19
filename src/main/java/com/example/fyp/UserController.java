package com.example.fyp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private JwtDecoder lambdaJwtDecoder;
    
    // @PostMapping
    // public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO, @AuthenticationPrincipal Jwt jwt) {

    //     // set userId if present in JWT token
    //     String userId = jwt != null ? jwt.getSubject() : null;
    //     if (userId != null) {
    //         userDTO.setUserId(userId);
    //     }

    //     // retrieve email from JWT token
    //     String email = jwt != null ? jwt.getClaim("email") : userDTO.getEmail();
    //     if (email == null) {
    //         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    //     }
    //     userDTO.setEmail(email);        

    //     User user = convertToEntity(userDTO);
    //     User createdUser = userService.createUser(user);
    //     return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    // }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO, @RequestHeader("Authorization") String authorizationHeader) {
        // Extract JWT token from the Authorization header
        String jwtToken = extractJwtToken(authorizationHeader);
        
        // Decode JWT token using lambdaJwtDecoder
        Jwt jwt = decodeJwtToken(jwtToken);
        
        // Perform authentication and authorization based on the decoded JWT token
        
        // set userId if present in JWT token
        String userId = jwt != null ? jwt.getSubject() : null;
        if (userId != null) {
            userDTO.setUserId(userId);
        }

        // retrieve email from JWT token
        String email = jwt != null ? jwt.getClaim("email") : userDTO.getEmail();
        if (email == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userDTO.setEmail(email);        

        User user = convertToEntity(userDTO);
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    
    // Method to extract JWT token from the Authorization header
    private String extractJwtToken(String authorizationHeader) {
        // Assuming the Authorization header is in the format "Bearer <token>"
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
    
    // Method to decode JWT token using lambdaJwtDecoder
    private Jwt decodeJwtToken(String jwtToken) {
        try {
            return lambdaJwtDecoder.decode(jwtToken);
        } catch (JwtException e) {
            // Handle JWT decoding exception
            return null;
        }
    }

    @PostMapping("/{userId}/upgrade")
    public ResponseEntity<PaidUser> upgradeToPaidUser(@PathVariable String userId, @RequestParam SubscriptionPlan subscriptionPlan) {
        PaidUser upgradedUser = userService.upgradeToPaidUser(userId, subscriptionPlan);
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
        if (userDTO.getUserId() != null) {
            user.setUserId(userDTO.getUserId());
        }
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        return user;
    }

}
