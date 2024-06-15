// data access/intermediary layer: between service class and database
package com.example.fyp;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {

    @Query("SELECT d FROM Diary d LEFT JOIN FETCH d.targetEmotionsList te WHERE d.user.userId = :userId")
    List<Diary> findDiariesWithTargetEmotionsByUserId(@Param("userId") String userId);

    @Query("SELECT d FROM Diary d LEFT JOIN FETCH d.targetEmotionsList te WHERE d.user.userId = :userId AND MONTH(d.date) = :month")
    List<Diary> findDiariesWithTargetEmotionsByMonthAndUserId(@Param("userId") String userId, @Param("month") int month);

}
