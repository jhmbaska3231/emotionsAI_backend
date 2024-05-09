// data access/intermediary layer: between service class and database
package com.example.fyp;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {
   
    // @Query("SELECT d.diary_id, d.date, d.input_text, d.emotional_intensity, d.overall_sentiment, te.emotion, te.emotion_percentage " +
    //        "FROM Diary d LEFT JOIN d.targetEmotionsList te")
    // List<Object[]> findAllDiariesWithTargetEmotions();

    @Query("SELECT new com.example.fyp.DiaryRequest(d.diary_id, d.date, d.input_text, d.emotional_intensity, d.overall_sentiment, te.emotion, te.emotion_percentage) " +
           "FROM Diary d LEFT JOIN d.targetEmotionsList te")
    List<DiaryRequest> findAllDiariesWithTargetEmotions();

    @Query("SELECT new com.example.fyp.DiaryRequest(d.diary_id, d.date, d.input_text, d.emotional_intensity, d.overall_sentiment, te.emotion, te.emotion_percentage) " + 
            "FROM Diary d LEFT JOIN d.targetEmotionsList te " +
            "WHERE d.diary_id = :diaryId")
    Optional<DiaryRequest> findDiaryWithTargetEmotionsById(@Param("diaryId") int diaryId);

    // @Query("SELECT new com.example.fyp.DiaryWithTargetEmotionsDTO(d.diaryId, d.date, d.inputText, d.emotionalIntensity, d.overallSentiment, te.emotionId, te.emotionName, te.emotionIntensity) " +
    //         "FROM Diary d LEFT JOIN d.targetEmotionsList te WHERE MONTH(d.date) = :month")
    // List<DiaryWithTargetEmotionsDTO> findDiariesWithTargetEmotionsByMonth(@Param("month") int month);


    // @Query("SELECT new com.example.fyp.DiaryWithTargetEmotionsDTO(d, " +
    //         "new com.example.fyp.TargetEmotionRequest(te.emotionId, te.emotionName, te.emotionIntensity)) " +
    //         "FROM Diary d LEFT JOIN d.targetEmotionsList te WHERE MONTH(d.date) = :month")
    // List<DiaryWithTargetEmotionsDTO> findDiariesWithTargetEmotionsByMonth(@Param("month") int month);



    // @Query("SELECT DISTINCT d FROM Diary d LEFT JOIN FETCH d.targetEmotionsList te WHERE MONTH(d.date) = :month")
    // List<Diary> findDiariesWithTargetEmotionsByMonth(@Param("month") int month);

}
