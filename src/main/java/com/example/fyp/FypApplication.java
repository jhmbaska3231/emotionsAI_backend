// http://localhost:8080/

package com.example.fyp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class FypApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(FypApplication.class, args);

		// try{
        //     String inputText = "today when i went out it started to rain... then i missed the bus... and was late for work. but thankfully i am able to walk";
        //     String output = DeepPurple.analyzeEmotion(inputText);
        //     System.out.println(output);
            
        // } catch (Exception io){
        //     System.out.println(io);
        // }

	}

}
