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
    public PaidUser upgradeToPaidUser(String userId, SubscriptionPlan subscriptionPlan) {
        // find the existing FreeUser
        FreeUser freeUser = (FreeUser) userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("FreeUser not found"));

        // create a new instance of PaidUser using the existing FreeUser's data
        PaidUser paidUser = new PaidUser();
        paidUser.setUserId(freeUser.getUserId());
        paidUser.setName(freeUser.getName());
        paidUser.setEmail(freeUser.getEmail());
        paidUser.setDiariesList(freeUser.getDiariesList());

        // create a new Subscription
        Subscription subscription = new Subscription();
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(subscriptionPlan == SubscriptionPlan.MONTHLY 
                ? LocalDate.now().plusMonths(1)
                : LocalDate.now().plusYears(1));
        subscription.setSubscriptionPlan(subscriptionPlan);
        subscription.setPaidUser(paidUser);

        // associate Subscription with PaidUser
        paidUser.setSubscription(subscription);

        // remove the old FreeUser
        userRepository.delete(freeUser);

        // save the new PaidUser
        PaidUser savedPaidUser = userRepository.save(paidUser);        

        return savedPaidUser;
    }

}
