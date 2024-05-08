package com.example.fyp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TargetEmotionRepository extends JpaRepository<TargetEmotion, Integer> {
}
