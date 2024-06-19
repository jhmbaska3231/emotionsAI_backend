package com.example.fyp;

import java.time.Duration;
import java.time.LocalDateTime;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class TranscribeService {
    
    @Autowired
    private UserRepository userRepository;

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String apiUrl = "https://api.openai.com/v1/chat/completions";

    // validation for free user
    public void transcribeText(String userId, String textToTranscribe) {
        User user = userRepository.findById(userId)
                                   .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        if (!(user instanceof FreeUser)) {
            throw new IllegalArgumentException("User is not a FreeUser");
        }
        
        FreeUser freeUser = (FreeUser) user;
        
        if (is24HoursPassed(freeUser.getLastTranscribeTime())) {
            freeUser.setTranscribeCount(0);
        }
        
        if (freeUser.getTranscribeCount() >= 3) {
            throw new IllegalStateException("Free user has reached the transcribe count limit.");
        }
        
        freeUser.setTranscribeCount(freeUser.getTranscribeCount() + 1);
        freeUser.setLastTranscribeTime(LocalDateTime.now());
        
        userRepository.save(freeUser);
    }
    
    private boolean is24HoursPassed(LocalDateTime lastTranscribeTime) {
        if (lastTranscribeTime == null) {
            return true; // If lastTranscribeTime is null, treat it as 24 hours passed
        }
        
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(lastTranscribeTime, currentTime);
        
        return duration.toHours() >= 24;
    }

    public String analyzeEmotion(String text) throws IOException {

        OkHttpClient client = new OkHttpClient();

        // Creating Json Objects for System and User
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "Analyze the provided input text and determine the emotion(s) it conveys from the list of emotions. After identifying the suitable emotion(s), assess the emotional intensity of the text as \"high,\" \"medium,\" or \"low. Next, indicate the overall sentiment of the text as \"positive,\" \"neutral,\" or \"negative.\" Finally, for each emotion detected above, please add a weightage percentage point beside it, the sum of the percentage point of all emotions must add up to 100. Here is the template for the output (a,b,c are the percentages that sum up to 100): \"Target Emotion(s): x (a%), y (b%), z (c%) \\n" + //
        "Emotional Intensity: xx \\n" + //
        "Overall Sentiment: yy\" \r\n" + //
        "Emotions List: \"Joy Happiness Sadness Anger Fear Surprise Disgust Contempt Love Trust Anticipation Guilt Shame Excitement Gratitude Envy Jealousy Empathy Compassion Pride Hope Confusion Regret Loneliness Boredom Satisfaction Anxiety\". Only choose from the emotions list for your answer.");

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", text);

        JsonArray messages = new JsonArray();
        messages.add(systemMessage);
        messages.add(userMessage);

        JsonObject requestBodyJson = new JsonObject();
        requestBodyJson.addProperty("model", "gpt-3.5-turbo-0125"); // Using gpt-3.5
        requestBodyJson.add("messages", messages);

        RequestBody body = RequestBody.create(requestBodyJson.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = IOUtils.toString(response.body().byteStream(), "UTF-8");
        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
        return jsonObject.get("choices").getAsJsonArray()
                         .get(0).getAsJsonObject()
                         .get("message").getAsJsonObject()
                         .get("content").getAsString();
                         
    }

}
