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
    //             String inputText = "today ah i kenna the rain sia... then i missed the bus lor...";
    //             String output = transcribeService.analyzeEmotion(inputText);
    //             System.out.println(output);
    //         } catch (Exception io) {
    //             System.out.println(io);
    //         }
    //     };
    // }

}
