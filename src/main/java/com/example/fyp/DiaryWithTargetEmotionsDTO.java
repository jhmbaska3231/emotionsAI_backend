// DTO (Data Transfer Object) class
package com.example.fyp;

import java.time.LocalDate;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaryWithTargetEmotionsDTO {

    private int diaryId;
    private LocalDate date;
    private String inputText;
    private String emotionalIntensity;
    private String overallSentiment;
    private String explanation;
    private List<TargetEmotionDTO> targetEmotionsList;
    private String userId;

    public DiaryWithTargetEmotionsDTO(Diary diary, List<TargetEmotionDTO> targetEmotionsList) {
        this.diaryId = diary.getDiaryId();
        this.date = diary.getDate();
        this.inputText = diary.getInputText();
        this.emotionalIntensity = diary.getEmotionalIntensity();
        this.overallSentiment = diary.getOverallSentiment();
        this.explanation = diary.getExplanation();
        this.targetEmotionsList = targetEmotionsList;
        this.userId = diary.getUser().getUserId();
    }

}
