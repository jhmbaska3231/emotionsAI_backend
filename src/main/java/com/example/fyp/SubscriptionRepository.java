package com.example.fyp;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    Subscription findByPaidUser_UserId(String userId);

}
