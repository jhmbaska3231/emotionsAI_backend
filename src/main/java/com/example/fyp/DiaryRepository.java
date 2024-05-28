// data access/intermediary layer: between service class and database
package com.example.fyp;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {

    @Query("SELECT d FROM Diary d LEFT JOIN FETCH d.targetEmotionsList")
    List<Diary> findAllDiariesWithTargetEmotions();

    @Query("SELECT d FROM Diary d LEFT JOIN FETCH d.targetEmotionsList te WHERE d.user.user_id = :userId")
    List<Diary> findDiariesWithTargetEmotionsByUserId(@Param("userId") int userId);

    @Query("SELECT d FROM Diary d LEFT JOIN FETCH d.targetEmotionsList WHERE MONTH(d.date) = :month")
    List<Diary> findDiariesWithTargetEmotionsByMonth(@Param("month") int month);

    @Query("SELECT d FROM Diary d LEFT JOIN FETCH d.targetEmotionsList te WHERE d.user.user_id = :userId AND MONTH(d.date) = :month")
    List<Diary> findDiariesWithTargetEmotionsByMonthAndUserId(@Param("userId") int userId, @Param("month") int month);

}
