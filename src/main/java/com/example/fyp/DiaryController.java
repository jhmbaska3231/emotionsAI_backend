// api/endpoint layer: get request from user and return a response
package com.example.fyp;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diaries")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    // get diaries for specific user
    @GetMapping("/with-emotions/user/{userId}")
    public ResponseEntity<List<DiaryWithTargetEmotionsDTO>> getDiariesWithTargetEmotionsByUserId(@PathVariable int userId) {
        List<DiaryWithTargetEmotionsDTO> diaries = diaryService.allDiariesWithTargetEmotionsByUserId(userId);
        return new ResponseEntity<>(diaries, HttpStatus.OK);
    }

    // get diaries for specific month and for specific user
    @GetMapping("/user/{userId}/month/{month}")
    public ResponseEntity<List<DiaryWithTargetEmotionsDTO>> getAllDiariesWithTargetEmotionsByMonthAndUserId(
            @PathVariable int userId, @PathVariable int month) {
        List<DiaryWithTargetEmotionsDTO> diaries = diaryService.allDiariesWithTargetEmotionsByMonthAndUserId(userId, month);
        return new ResponseEntity<>(diaries, HttpStatus.OK);
    }

    // more ideal way of saving a diary in a single atomic transaction
    @PostMapping("/with-emotions")
    public ResponseEntity<DiaryWithTargetEmotionsDTO> createDiaryWithTargetEmotions(@RequestBody DiaryWithTargetEmotionsDTO request) {
        DiaryWithTargetEmotionsDTO createdDiary = diaryService.createDiaryWithTargetEmotions(request);
        return new ResponseEntity<>(createdDiary, HttpStatus.CREATED);
    }

}
