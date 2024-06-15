package com.example.fyp;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    List<Subscription> findByEndDateBefore(LocalDate date);
}
