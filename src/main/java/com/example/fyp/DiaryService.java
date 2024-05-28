// service layer: to hold business logic
package com.example.fyp;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private UserRepository userRepository;

    public List<DiaryWithTargetEmotionsDTO> allDiariesWithTargetEmotions() {
        List<Diary> diaries = diaryRepository.findAllDiariesWithTargetEmotions();
        return diaries.stream().map(diary -> {
            List<TargetEmotionDTO> targetEmotionsList = diary.getTargetEmotionsList().stream()
                    .map(te -> new TargetEmotionDTO(te.getEmotion(), te.getEmotion_percentage()))
                    .collect(Collectors.toList());
            return new DiaryWithTargetEmotionsDTO(diary, targetEmotionsList);
        }).collect(Collectors.toList());
    }

    public List<DiaryWithTargetEmotionsDTO> allDiariesWithTargetEmotionsByUserId(int userId) {
        List<Diary> diaries = diaryRepository.findDiariesWithTargetEmotionsByUserId(userId);
        return diaries.stream().map(diary -> {
            List<TargetEmotionDTO> targetEmotionsList = diary.getTargetEmotionsList().stream()
                    .map(te -> new TargetEmotionDTO(te.getEmotion(), te.getEmotion_percentage()))
                    .collect(Collectors.toList());
            return new DiaryWithTargetEmotionsDTO(diary, targetEmotionsList);
        }).collect(Collectors.toList());
    }

    // convert the fetched data into the DiaryWithTargetEmotionsDTO
    public List<DiaryWithTargetEmotionsDTO> allDiariesWithTargetEmotionsByMonth(int month) {
        List<Diary> diaries = diaryRepository.findDiariesWithTargetEmotionsByMonth(month);
        return diaries.stream().map(diary -> {
            List<TargetEmotionDTO> targetEmotionsList = diary.getTargetEmotionsList().stream()
                    .map(te -> new TargetEmotionDTO(te.getEmotion(), te.getEmotion_percentage()))
                    .collect(Collectors.toList());
            return new DiaryWithTargetEmotionsDTO(diary, targetEmotionsList);
        }).collect(Collectors.toList());
    }

    public List<DiaryWithTargetEmotionsDTO> allDiariesWithTargetEmotionsByMonthAndUserId(int userId, int month) {
        List<Diary> diaries = diaryRepository.findDiariesWithTargetEmotionsByMonthAndUserId(userId, month);
        return diaries.stream().map(diary -> {
            List<TargetEmotionDTO> targetEmotionsList = diary.getTargetEmotionsList().stream()
                    .map(te -> new TargetEmotionDTO(te.getEmotion(), te.getEmotion_percentage()))
                    .collect(Collectors.toList());
            return new DiaryWithTargetEmotionsDTO(diary, targetEmotionsList);
        }).collect(Collectors.toList());
    }

    // more ideal way of saving a diary in a single atomic transaction
    public DiaryWithTargetEmotionsDTO createDiaryWithTargetEmotions(DiaryWithTargetEmotionsDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Diary diary = new Diary();
        diary.setDate(request.getDate());
        diary.setInput_text(request.getInputText());
        diary.setEmotional_intensity(request.getEmotionalIntensity());
        diary.setOverall_sentiment(request.getOverallSentiment());
        diary.setUser(user);

        List<TargetEmotion> targetEmotions = request.getTargetEmotionsList().stream()
                .map(teRequest -> new TargetEmotion(teRequest.getEmotion(), teRequest.getEmotionPercentage()))
                .collect(Collectors.toList());

        targetEmotions.forEach(te -> te.setDiary(diary));
        diary.setTargetEmotionsList(targetEmotions);

        Diary savedDiary = diaryRepository.save(diary);

        List<TargetEmotionDTO> targetEmotionsDTOList = savedDiary.getTargetEmotionsList().stream()
                .map(te -> new TargetEmotionDTO(te.getEmotion(), te.getEmotion_percentage()))
                .collect(Collectors.toList());

        return new DiaryWithTargetEmotionsDTO(savedDiary, targetEmotionsDTOList);
    }

}
