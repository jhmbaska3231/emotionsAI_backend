// DTO (Data Transfer Object) class
package com.example.fyp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TargetEmotionDTO {

    private String emotion;
    private double emotionPercentage;

    public TargetEmotionDTO(String emotion, Double emotionPercentage) {
        this.emotion = emotion;
        this.emotionPercentage = emotionPercentage;
    }
    
}
