// service layer: to hold business logic
package com.example.fyp;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
public class TargetEmotionService {

    @Autowired // to initialize TargetEmotionRepository class
    private TargetEmotionRepository targetEmotionRepository;

    @Autowired
    private DiaryRepository diaryRepository;
    
    // @Autowired
    // private MongoTemplate mongoTemplate;

    // public TargetEmotion createTargetEmotion(String emotion, Double emotion_percentage, int diary_id) {
    //     TargetEmotion te = targetEmotionRepository.insert(new TargetEmotion(emotion, emotion_percentage));

    //     mongoTemplate.update(Diary.class)
    //         .matching(Criteria.where("diary_id").is(diary_id))
    //         .apply(new Update().push("id").value(te));
        
    //     return te;
    // }

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
            // Save the TargetEmotion
            return targetEmotionRepository.save(targetEmotion);
        } else {
            return null;
        }
    }

}
