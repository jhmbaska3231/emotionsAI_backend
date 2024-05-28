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

    // @GetMapping // for testing
    // public ResponseEntity<List<Diary>> getAllDiaries() {
    //     return new ResponseEntity<List<Diary>>(diaryService.allDiaries(), HttpStatus.OK);
    // }

    // @GetMapping("/{diary_id}") // for testing
    // public ResponseEntity<Optional<Diary>> getSingleDiary(@PathVariable int diary_id) {
    //     return new ResponseEntity<Optional<Diary>>(diaryService.singleDiary(diary_id), HttpStatus.OK);
    // }

    @GetMapping("/with-emotions")
    public ResponseEntity<List<DiaryWithTargetEmotionsDTO>> getAllDiariesWithTargetEmotions() {
        List<DiaryWithTargetEmotionsDTO> diaries = diaryService.allDiariesWithTargetEmotions();
        return new ResponseEntity<>(diaries, HttpStatus.OK);
    }

    @GetMapping("/with-emotions/{diaryId}")
    public ResponseEntity<DiaryWithTargetEmotionsDTO> getDiaryWithTargetEmotionsById(@PathVariable int diaryId) {
        Optional<DiaryWithTargetEmotionsDTO> diaryOptional = diaryService.getDiaryWithTargetEmotionsById(diaryId);
        return diaryOptional.map(diary -> new ResponseEntity<>(diary, HttpStatus.OK))
                            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/month/{month}")
    public ResponseEntity<List<DiaryWithTargetEmotionsDTO>> getAllDiariesWithTargetEmotionsByMonth(@PathVariable int month) {
        List<DiaryWithTargetEmotionsDTO> diaries = diaryService.allDiariesWithTargetEmotionsByMonth(month);
        return new ResponseEntity<>(diaries, HttpStatus.OK);
    }

    // more ideal way of saving a diary in a single atomic transaction
    @PostMapping("/with-emotions")
    public ResponseEntity<DiaryWithTargetEmotionsDTO> createDiaryWithTargetEmotions(@RequestBody DiaryWithTargetEmotionsDTO request) {
        DiaryWithTargetEmotionsDTO createdDiary = diaryService.createDiaryWithTargetEmotions(request);
        return new ResponseEntity<>(createdDiary, HttpStatus.CREATED);
    }

}
