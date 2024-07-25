package com.example.fyp;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    Subscription findByPaidUser_UserId(String userId);

    // List<Subscription> findByEndDateBefore(LocalDate date);

}
