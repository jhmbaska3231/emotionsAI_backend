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

         // Updated prompt
         String paidPrompt = "You will be provided a text. Your task is to analyze the provided text and determine the emotion(s) it conveys from a provided list of emotions. <emotions_list> \"””Joy Happiness Sadness Anger Fear Surprise Disgust Contempt Love Trust Anticipation Guilt Shame Excitement Gratitude Envy Jealousy Empathy Compassion Pride Hope Confusion Regret Loneliness Boredom Satisfaction Anxiety\"”” </emotions_list> \n\n" +
                "Use the following step-by-step instructions to each sentence in the text. Enclose all your work for these instructions in a similar structure as the original text.\n\n" +
                "Step 1 - Identify the suitable emotion(s) presented.\n\n" +
                "Step 2 - Assess the emotional intensity as \"high,\" \"medium,\" or \"low\".\n\n" +
                "Step 3 - Indicate the sentiment as \"positive,\" \"neutral,\" or \"negative\".\n\n" +
                "Step 4 - Add a weight to the detected emotion. The weight measures how much the emotion contributes to the overall sentiment of the text.\n\n" +
                "Step 5 - At the end of the sentence, in parentheses, display the emotion detected, the emotional intensity, the sentiment and the weight of the emotion relative to the whole text. For example, (Joy, high, positive, 34%)\n\n" +
                "Annotated Text refers to your resultant work for the instructions you followed previously.\n\n" +
                "Detected Emotions is a list of all the detected emotions and their weightage relative to the whole text. i.e. Joy (50%), Sadness (50%)\n\n" +
                "Overall Emotional Intensity is the average intensity of the whole text.\n\n" +
                "Overall Sentiment Intensity is the average sentiment of the whole text.\n\n" +
                "Only respond with this template enclosed in triple quotation:\n" +
                "”””\n" +
                "Annotated Text: {}\n" +
                "Detected Emotions(s): x (a%), y (b%), z (c%)\n" +
                 "Overall Emotional Intensity: d\n" +
                "Overall Sentiment: e\n" +
                "”””";

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
                throw new IOException("Unexpected code " + response);
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
