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
		// AudioToText audioToText = new AudioToText();
		// File audioFile = new File("src\\test\\java\\com\\example\\fyp\\valid.wav");
		// try{
		// 	String transcription = audioToText.transcribeAudio(audioFile);
		// 	System.out.println(transcription);
		// }catch (IOException e){
		// 	System.out.println(e);
		// }

	}

	@GetMapping("/")
	public String apiRoot() {
		return "hello world...";
	}

}
