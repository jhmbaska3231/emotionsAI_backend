// data access layer: talks to the database
package com.example.fyp;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {
   
    @Query("SELECT d.diary_id, d.date, d.input_text, d.emotional_intensity, d.overall_sentiment, te.emotion, te.emotion_percentage " +
           "FROM Diary d LEFT JOIN d.targetEmotionsList te")
    List<Object[]> findAllDiariesWithTargetEmotions();

}
