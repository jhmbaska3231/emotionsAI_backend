// http://localhost:8080/

package com.example.fyp;
import com.example.fyp.DeepPurple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableScheduling

public class FypApplication {

	public static void main(String[] args) {
		// SpringApplication.run(FypApplication.class, args);
		try{
            String inputText = "Every time I think about that missed opportunity, it's like a weight on my chest, making me wonder if I'll ever feel truly content. ";
            String output = DeepPurple.analyzeEmotion(inputText);
            System.out.println(output);
            
        } catch (Exception io){
            System.out.println(io);
        }
	}

	@GetMapping("/")
	public String apiRoot() {
		return "hello world...";
	}

}
