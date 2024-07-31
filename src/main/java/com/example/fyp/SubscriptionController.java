package com.example.fyp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    
    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/{userId}")
    public ResponseEntity<Subscription> getSubscriptionByUserId(@PathVariable String userId) {
        Subscription subscription = subscriptionService.getSubscriptionByUserId(userId);
        if (subscription != null) {
            return ResponseEntity.ok(subscription);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{userId}/update-plan")
    public ResponseEntity<Subscription> updateSubscriptionPlan(@PathVariable String userId, @RequestParam SubscriptionPlan newPlan) {
        Subscription updatedSubscription = subscriptionService.updateSubscriptionPlan(userId, newPlan);
        return ResponseEntity.ok(updatedSubscription);
    }

}
