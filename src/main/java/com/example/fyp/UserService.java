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
    public PaidUser upgradeToPaidUser(int freeUserId, SubscriptionPlan subscriptionPlan) {
        User user = userRepository.findById(freeUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!(user instanceof FreeUser)) {
            throw new IllegalArgumentException("User is not a free user");
        }

        FreeUser freeUser = (FreeUser) user;

        PaidUser paidUser = new PaidUser();
        paidUser.setName(freeUser.getName());
        paidUser.setEmail(freeUser.getEmail());
        paidUser.setDiariesList(freeUser.getDiariesList());

        Subscription subscription = new Subscription();
        subscription.setStart_date(LocalDate.now());
        // setEnd_date
        subscription.setEndDate(subscriptionPlan == SubscriptionPlan.MONTHLY 
                ? LocalDate.now().plusMonths(1)
                : LocalDate.now().plusYears(1));
        subscription.setSubscription_plan(subscriptionPlan);
        subscription.setPaidUser(paidUser);

        paidUser.setSubscription(subscription);

        userRepository.delete(freeUser); // Remove the old free user

        return userRepository.save(paidUser);
    }
    
}
