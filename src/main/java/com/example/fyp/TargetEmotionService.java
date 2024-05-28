// service layer: to hold business logic
package com.example.fyp;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TargetEmotionService {

    @Autowired
    private TargetEmotionRepository targetEmotionRepository;

    @Autowired
    private DiaryRepository diaryRepository;

    // less ideal way of saving a diary and multiple target emotions with multiple requests
    public TargetEmotion createTargetEmotion(TargetEmotionRequest request) {

        // Fetch the corresponding Diary entity from the database using diaryId
        Optional<Diary> optionalDiary = diaryRepository.findById(request.getDiaryId());
        if (optionalDiary.isPresent()) {
            Diary diary = optionalDiary.get();

            // Create a new TargetEmotion object and set the Diary
            TargetEmotion targetEmotion = new TargetEmotion();
            targetEmotion.setEmotion(request.getEmotion());
            targetEmotion.setEmotion_percentage(request.getEmotionPercentage());
            targetEmotion.setDiary(diary);
            return targetEmotionRepository.save(targetEmotion);

        } else {
            return null;
        }

    }

}
