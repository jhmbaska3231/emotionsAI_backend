package com.example.fyp;

// import java.io.File;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FypApplication {
// public class FypApplication implements CommandLineRunner {

    // @Autowired
    // private AudioToTextService audioToTextService;

	public static void main(String[] args) {
		SpringApplication.run(FypApplication.class, args);
	}

    // sample usage to test transcribe
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            // Get TranscribeService bean from application context
            TranscribeService transcribeService = ctx.getBean(TranscribeService.class);
            try {
                String inputText = "today when i went out it started to rain... then i missed the bus... and was late for work. but thankfully i am able to walk";
                String output = transcribeService.analyzeEmotion(inputText);
                System.out.println(output);
            } catch (Exception io) {
                System.out.println(io);
            }
        };
    }

    // sample usage to test convert audio to text
    // @Override
    // public void run(String... args) throws Exception {
    //     File audioFile = new File("/test audio.mp4");
    //     if (audioFile.exists()) {
    //         try {
    //             String transcript = audioToTextService.convertAudioToText(audioFile);
    //             System.out.println("Transcription: " + transcript);
    //         } catch (Exception e) {
    //             e.printStackTrace();
    //         }
    //     } else {
    //         System.out.println("Audio file not found!");
    //     }
    // }

}