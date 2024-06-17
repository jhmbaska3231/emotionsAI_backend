// http://localhost:8080/

package com.example.fyp;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@SpringBootApplication
@RestController

public class FypApplication {

	public static void main(String[] args) {
		// SpringApplication.run(FypApplication.class, args);
		// DEMO
		DeepPurple deepPurple = new DeepPurple();
		// AudioToText audioToText = new AudioToText();
		// File audioFile = new File("src\\test\\java\\com\\example\\fyp\\valid.wav");
		try{
			
			// String transcription = audioToText.transcribeAudio(audioFile);
			// System.out.println("Audio File Transcription: ");
			//System.out.println(transcription);
			String testTranscription = "Yesterday was a rollercoaster of emotions. I felt immense joy when I received the job offer I had been waiting for, but that happiness was overshadowed by a sense of guilt for leaving my current team behind. There was also a lingering anxiety about starting a new role in a completely different environment. Despite these mixed feelings, I am hopeful about the future and excited to see where this new opportunity takes me.";
			String analysis = deepPurple.analyzeEmotion(testTranscription, "paid");
			System.out.println("Emotional Analysis");
			System.out.println(analysis);

		}catch (IOException e){
			System.out.println(e);
		}

	}

	@GetMapping("/")
	public String apiRoot() {
		return "hello world...";
	}

}
