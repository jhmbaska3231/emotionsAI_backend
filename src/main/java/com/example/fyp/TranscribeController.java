package com.example.fyp;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TranscribeController {

    @Autowired
    private TranscribeService transcribeService;

    @PostMapping("/transcribe")
    public String analyzeEmotion(@RequestBody String inputText) throws IOException {
        return transcribeService.analyzeEmotion(inputText);
    }
    
}
