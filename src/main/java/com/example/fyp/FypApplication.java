package com.example.fyp;

// import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.context.ApplicationContext;
// import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FypApplication {

	public static void main(String[] args) {
		SpringApplication.run(FypApplication.class, args);
	}

    // sample usage to test transcribe
    // @Bean
    // public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    //     return args -> {
    //         // Get TranscribeService bean from application context
    //         TranscribeService transcribeService = ctx.getBean(TranscribeService.class);
    //         try {
    //             String inputText = "annoyed";
    //             String userId = "34d8b4c8-9061-7075-e98d-3173bb8c43a1"; // james user_id            
    //             System.out.println("Step 0");
    //             String output = transcribeService.analyzeEmotion(userId, inputText);
    //             System.out.println(output);
    //         } catch (Exception io) {
    //             System.out.println(io);
    //         }
    //     };
    // }

}
