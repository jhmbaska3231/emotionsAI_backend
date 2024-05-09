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
    private List<TargetEmotionRequest> targetEmotionsList;

    // public DiaryWithTargetEmotionsDTO(Diary diary, List<TargetEmotionRequest> targetEmotionsList) {
    //     this.diaryId = diary.getDiary_id();
    //     this.date = diary.getDate();
    //     this.inputText = diary.getInput_text();
    //     this.emotionalIntensity = diary.getEmotional_intensity();
    //     this.overallSentiment = diary.getOverall_sentiment();
    //     this.targetEmotionsList = targetEmotionsList;
    // }
    

}
