package com.example.fyp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class AudioToTextController {

    @Autowired
    private AudioToTextService audioToTextService;

    @PostMapping("/audiototext")
    public String convertAudioToText(@RequestParam("inputAudio") MultipartFile inputAudio) throws IOException {
        File file = convertMultipartFileToFile(inputAudio);
        return audioToTextService.convertAudioToText(file);
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipartFile.getBytes());
        }
        return file;
    }
    
}
