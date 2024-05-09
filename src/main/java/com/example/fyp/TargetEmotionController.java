// api/endpoint layer: get request from user and return a response
package com.example.fyp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/target_emotions")
public class TargetEmotionController {
    
    @Autowired
    private TargetEmotionService targetEmotionService;
    
    // @PostMapping // uses map, bad
    // public ResponseEntity<TargetEmotion> createTargetEmotion(@RequestBody Map<String, Double, Integer> payload) {
    //     return new ResponseEntity<TargetEmotion>(targetEmotionService.createTargetEmotion(payload.get("emotion"), payload.get("emotion_percentage"), payload.get("diary_id")), HttpStatus.CREATED);
    // }

    @PostMapping
    public ResponseEntity<TargetEmotion> createTargetEmotion(@RequestBody TargetEmotionRequest request) {
        return new ResponseEntity<>(targetEmotionService.createTargetEmotion(request), HttpStatus.CREATED);
    }

}
