// service layer: to hold application logic
package com.example.fyp;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiaryService {

    @Autowired // to initialize DiaryRepository class
    private DiaryRepository diaryRepository;

    public List<Diary> allDiaries() {
        return diaryRepository.findAll();
    }

    public Optional<Diary> singleDiary(int diary_id) {
        return diaryRepository.findById(diary_id);
    }

    public List<Object[]> allDiariesWithTargetEmotions() {
        return diaryRepository.findAllDiariesWithTargetEmotions();
    }

}
