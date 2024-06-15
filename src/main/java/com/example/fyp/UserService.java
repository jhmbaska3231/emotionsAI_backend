package com.example.fyp;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public PaidUser upgradeToPaidUser(String freeUserId, SubscriptionPlan subscriptionPlan) {
        FreeUser freeUser = (FreeUser) userRepository.findById(freeUserId)
                .orElseThrow(() -> new EntityNotFoundException("FreeUser not found"));

        // Create a new instance of PaidUser
        PaidUser paidUser = new PaidUser();
        paidUser.setName(freeUser.getName());
        paidUser.setEmail(freeUser.getEmail());
        paidUser.setDiariesList(freeUser.getDiariesList());

        // Create a new Subscription
        Subscription subscription = new Subscription();
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(subscriptionPlan == SubscriptionPlan.MONTHLY 
                ? LocalDate.now().plusMonths(1)
                : LocalDate.now().plusYears(1));
        subscription.setSubscriptionPlan(subscriptionPlan);
        subscription.setPaidUser(paidUser);

        // Associate Subscription with PaidUser
        paidUser.setSubscription(subscription);

        // Save the new PaidUser
        PaidUser savedPaidUser = userRepository.save(paidUser);

        // Remove the old FreeUser
        userRepository.delete(freeUser);

        return savedPaidUser;
    }

}
