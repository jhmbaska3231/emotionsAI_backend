package com.example.fyp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
        User user = convertToEntity(userDTO);
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/{freeUserId}/upgrade")
    public ResponseEntity<PaidUser> upgradeToPaidUser(@PathVariable int freeUserId, @RequestParam SubscriptionPlan subscriptionPlan) {
        PaidUser upgradedUser = userService.upgradeToPaidUser(freeUserId, subscriptionPlan);
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
                ((FreeUser) user).setTranscribe_count(0);
                break;
            case "PaidUser":
                user = new PaidUser();
                break;
            default:
                throw new IllegalArgumentException("Invalid user type");
        }
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        return user;
    }

}
