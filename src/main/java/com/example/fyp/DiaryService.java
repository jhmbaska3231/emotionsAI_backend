// service layer: to hold business logic
package com.example.fyp;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;

    public List<Diary> allDiaries() { // for testing
        return diaryRepository.findAll();
    }

    public Optional<Diary> singleDiary(int diary_id) { // for testing
        return diaryRepository.findById(diary_id);
    }

    // public List<Object[]> allDiariesWithTargetEmotions() { // this returns array, bad
    //     return diaryRepository.findAllDiariesWithTargetEmotions();
    // }

    public List<DiaryRequest> allDiariesWithTargetEmotions() {
        return diaryRepository.findAllDiariesWithTargetEmotions();
    }

    public Optional<DiaryRequest> singleDiaryWithTargetEmotions(int diary_id) {
        return diaryRepository.findDiaryWithTargetEmotionsById(diary_id);
    }

}
