// DTO (Data Transfer Object) class
package com.example.fyp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// less ideal way of saving a diary and multiple target emotions with multiple requests
public class TargetEmotionRequest {

    private String emotion;
    private Double emotionPercentage;
    private Integer diaryId;

}

