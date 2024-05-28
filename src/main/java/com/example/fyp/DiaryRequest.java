// DTO (Data Transfer Object) class
package com.example.fyp;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaryRequest {

    private int diaryId;
    private LocalDate date;
    private String inputText;
    private String emotionalIntensity;
    private String overallSentiment;
    private String emotion;
    private Double emotionPercentage;

}

