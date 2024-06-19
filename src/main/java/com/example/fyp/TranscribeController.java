package com.example.fyp;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranscribeController {

    @PostMapping("/api/transcribe")
    public String analyzeEmotion(@RequestBody String inputText) throws IOException {
        return TranscribeLogic.analyzeEmotion(inputText);
    }
    
}
