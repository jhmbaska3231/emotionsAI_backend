package com.example.fyp;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AudioToTextController {

    @Autowired
    private AudioToTextService audioToTextService;

    @PostMapping("/api/audiototext")
    public String convertAudioToText(@RequestBody File inputAudio) throws IOException {
        return audioToTextService.convertAudioToText(inputAudio);
    }
    
}
