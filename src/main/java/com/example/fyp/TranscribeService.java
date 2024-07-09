package com.example.fyp;

import java.time.Duration;
import java.time.LocalDateTime;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.commons.io.IOUtils;

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
        System.out.println("\napi key: " + apiKey);

        // Updated prompt
        String paidPrompt = "###Instruction###\n\n" +
            "You will be provided a text. Your task is to analyze the provided text and determine the emotion(s) it conveys from the provided list of emotions.\n\n" +
            "###Emotions List###\n" +
            "\"Joy, Happiness, Sadness, Anger, Fear, Surprise, Disgust, Contempt, Love, Trust, Anticipation, Guilt, Shame, Excitement, Gratitude, Envy, Jealousy, Empathy, Compassion, Pride, Hope, Confusion, Regret, Loneliness, Boredom, Satisfaction, Anxiety\"\n\n" +
            "###Steps###\n" +
            "1. Identify the suitable emotion(s) presented in each sentence.\n" +
            "2. Assess the emotional intensity as \"high,\" \"medium,\" or \"low.\"\n" +
            "3. Indicate the sentiment as \"positive,\" \"neutral,\" or \"negative.\"\n" +
            "4. Add a weight to the detected emotion. The weight measures how much the emotion contributes to the overall sentiment of the text.\n" +
            "5. At the end of each sentence, in parentheses, display the emotion detected, the emotional intensity, the sentiment, and the weight of the emotion relative to the whole text. For example, (Joy, high, positive, 34%)\n\n" +
            "###Output Template###\n" +
            "\"\"\"\n" +
            "Annotated Text: {}\n" +
            "Detected Emotion(s): x (a%), y (b%), z (c%)\n" +
            "Overall Emotional Intensity: d\n" +
            "Overall Sentiment: e\n" +
            "\"\"\"\n\n" +
            "###Example###\n" +
            "Text: \"I felt great joy when I received the news, but also a tinge of sadness.\"\n\n" +
            "Annotated Text: I felt great joy when I received the news, but also a tinge of sadness. (Joy, high, positive, 70%) (Sadness, low, negative, 30%)\n" +
            "Detected Emotions: Joy (70%), Sadness (30%)\n" +
            "Overall Emotional Intensity: high\n" +
            "Overall Sentiment: mixed (positive and negative)";

        // System JsonObject
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", paidPrompt);

        // User JsonObject
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

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response + ": " + response.body().string());
            }
        
            String responseBody = IOUtils.toString(response.body().byteStream(), "UTF-8");
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            return jsonObject.get("choices").getAsJsonArray()
                             .get(0).getAsJsonObject()
                             .get("message").getAsJsonObject()
                             .get("content").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
            return "An error occurred while analyzing emotion.";
        }
        
    }

}