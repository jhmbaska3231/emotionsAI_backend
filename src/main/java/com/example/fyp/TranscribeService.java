package com.example.fyp;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TranscribeService {
    
    @Autowired
    private UserRepository userRepository;

    public void transcribeText(int userId, String textToTranscribe) {
        User user = userRepository.findById(userId)
                                   .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        if (!(user instanceof FreeUser)) {
            throw new IllegalArgumentException("User is not a FreeUser");
        }
        
        FreeUser freeUser = (FreeUser) user;
        
        if (is24HoursPassed(freeUser.getLast_transcribe_time())) {
            freeUser.setTranscribe_count(0);
        }
        
        if (freeUser.getTranscribe_count() >= 3) {
            throw new IllegalStateException("Free user has reached the transcribe count limit.");
        }
        
        // Perform the transcription operation here
        // ...........................................
        // ...........................................
        // ...........................................
        
        freeUser.setTranscribe_count(freeUser.getTranscribe_count() + 1);
        freeUser.setLast_transcribe_time(LocalDateTime.now());
        
        userRepository.save(freeUser);
    }
    
    private boolean is24HoursPassed(LocalDateTime lastTranscribeTime) {
        if (lastTranscribeTime == null) {
            return true; // If lastTranscribeTime is null, treat it as 24 hours passed
        }
        
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(lastTranscribeTime, currentTime);
        
        return duration.toHours() >= 24;
    }

}
