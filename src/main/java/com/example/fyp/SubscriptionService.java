package com.example.fyp;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserService userService;

    public Subscription getSubscriptionByUserId(String userId) {
        return subscriptionRepository.findByPaidUser_UserId(userId);
    }

    @Transactional
    public Subscription updateSubscriptionPlan(String userId, SubscriptionPlan newPlan) {
        Subscription subscription = subscriptionRepository.findByPaidUser_UserId(userId);

        if (subscription == null) {
            throw new RuntimeException("Subscription not found");
        }

        subscription.setSubscriptionPlan(newPlan);
        subscription.setEndDate(calculateNewEndDate(newPlan));

        return subscriptionRepository.save(subscription);
    }

    private LocalDate calculateNewEndDate(SubscriptionPlan plan) {
        LocalDate currentDate = LocalDate.now();
        if (plan == SubscriptionPlan.MONTHLY) {
            return currentDate.plusMonths(1);
        } else if (plan == SubscriptionPlan.YEARLY) {
            return currentDate.plusYears(1);
        } else {
            throw new IllegalArgumentException("Invalid plan type");
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")  // cron expression for every day at midnight
    @Transactional
    public void checkForExpiredSubscriptions() {
        LocalDate currentDate = LocalDate.now();
        List<Subscription> expiredSubscriptions = subscriptionRepository.findByEndDateBefore(currentDate);

        for (Subscription subscription : expiredSubscriptions) {
            userService.unsubscribe(subscription.getPaidUser().getUserId());
        }
    }
    
}
