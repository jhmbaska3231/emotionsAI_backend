package com.example.fyp;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranscribeLimitDTO {
    
        private LocalDateTime transcribeTime;
        
}
