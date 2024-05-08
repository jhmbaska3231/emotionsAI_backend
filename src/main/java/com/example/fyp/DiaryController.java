// api/endpoint layer: get request from user and return a response
package com.example.fyp;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diaries")
public class DiaryController {

    @Autowired // to initialize DiaryService class
    private DiaryService diaryService;

    @GetMapping // this is without target emotion
    public ResponseEntity<List<Diary>> getAllDiaries() {
        return new ResponseEntity<List<Diary>>(diaryService.allDiaries(), HttpStatus.OK);
    }

    @GetMapping("/{diary_id}") // this is without target emotion
    public ResponseEntity<Optional<Diary>> getSingleDiary(@PathVariable int diary_id) {
        return new ResponseEntity<Optional<Diary>>(diaryService.singleDiary(diary_id), HttpStatus.OK);
    }

    @GetMapping("/with-emotions") // this is with target emotion
    public ResponseEntity<List<Object[]>> getAllDiariesWithTargetEmotions() {
        return new ResponseEntity<>(diaryService.allDiariesWithTargetEmotions(), HttpStatus.OK);
    }

}
