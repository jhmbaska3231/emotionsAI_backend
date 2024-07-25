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
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public Subscription getSubscriptionByUserId(String userId) {
        return subscriptionRepository.findByPaidUser_UserId(userId);
    }

    // @Scheduled(cron = "0 0 0 * * ?") // cron schedule to run daily at midnight
    // @Transactional
    // public void checkAndRevertExpiredSubscriptions() {
    //     LocalDate today = LocalDate.now();
    //     List<Subscription> expiredSubscriptions = subscriptionRepository.findByEndDateBefore(today);

    //     for (Subscription subscription : expiredSubscriptions) {
    //         PaidUser paidUser = subscription.getPaidUser();
    //         FreeUser freeUser = new FreeUser();
    //         freeUser.setUserId(paidUser.getUserId());
    //         freeUser.setName(paidUser.getName());
    //         freeUser.setEmail(paidUser.getEmail());
    //         freeUser.setDiariesList(paidUser.getDiariesList());
    //         freeUser.setTranscribeCount(0); // Reset the transcribe count
    //         freeUser.setLastTranscribeTime(null); // Reset the last transcribe time

    //         userRepository.delete(paidUser);
    //         userRepository.save(freeUser);

    //         subscriptionRepository.delete(subscription); // Clean up the expired subscription
    //     }
    // }
    
}
