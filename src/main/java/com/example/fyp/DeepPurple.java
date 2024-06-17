package com.example.fyp;

import okhttp3.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeepPurple {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "sk-proj-fvYUlxUBz1u1HFy6V8ogT3BlbkFJ5UpcEXlVUmXVkHBivGBW";
    private static final Logger LOGGER = Logger.getLogger(DeepPurple.class.getName());

    // Sample Usage
    // public static void main(String[] args) throws IOException {
    //     String inputText = "Your input text here";
    //     String emotion = analyzeEmotion(inputText);
    //     System.out.println("Emotion: " + emotion);
    // }

    public String analyzeEmotion(String text, String userType) throws IOException {
        if (text == null || text.trim().isEmpty()){
            throw new IllegalArgumentException("Input text cannot be null or empty.");
        }

        OkHttpClient client = new OkHttpClient();

        // Creating Json Objects for System and User
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", createSystemContent(userType));

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
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()){
            if (!response.isSuccessful()){
                throw new IOException("Unexepected code " + response);
            }
            String responseBody = IOUtils.toString(response.body().byteStream(), "UTF-8");
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            // System.out.println(jsonObject);
            return jsonObject.get("choices").getAsJsonArray()
                            .get(0).getAsJsonObject()
                            .get("message").getAsJsonObject()
                            .get("content").getAsString();
        } catch (IOException e){
            LOGGER.log(Level.SEVERE, "Error during API call", e);
            throw e;
        }
    }

    private String createSystemContent(String userType) {
        if ("paid".equalsIgnoreCase(userType)) {
            return "Analyze the provided input text and determine the emotion(s) it conveys from a provided list of emotions." +
                    "Emotions List: \"Joy Happiness Sadness Anger Fear Surprise Disgust Contempt Love Trust Anticipation Guilt Shame Excitement Gratitude Envy Jealousy Empathy Compassion Pride Hope Confusion Regret Loneliness Boredom Satisfaction Anxiety\"" + 
                    "For EACH sentence in the input text, follow these steps:\n" +
                    "1.Identify the suitable emotion(s) presented.\n" +
                    "2. Assess the emotional intensity as \"high\", \"medium\", or \"low\"." +
                    "3. Indicate the sentiment as \"positive\", \"neutral\", or \"negative\"." +
                    "4. Add a weight to the detected emotion. The weight is a percentage of all the emotions present in the whole text. The sum of the percentage weights must add up to 100.\n" +
                    "5. Concatenate a parentheses that contains (Emotions_Detected, Intensity, Sentiment, Weight).\n" +
                    "All the annotated sentences will be combined at the end of the analysis, while maintaining the original structure of the input text. This will be referred to as the \"Annotated Text\" in the output template." +
                    "Here is a template of the output:\n" +
                    "Annotated Text: {}\nDetected Emotions(s): x (a%), y (b%), z (c%)\nOverall Emotional Intensity: {d}\nOverall Sentiment: {e}";

        } else {
            return "Analyze the provided input text and determine the primary emotion it conveys from the list of emotions." + 
                   "Indicate the overall sentiment of the text as \"positive,\" \"neutral,\" or \"negative.\" " + 
                   "Here is the template for the output: \"Target Emotion: x \\n" + 
                   "Overall Sentiment: yy\" \r\n" + 
                   "Emotions List: \"Joy Happiness Sadness Anger Fear Surprise Disgust Contempt Love Trust Anticipation Guilt Shame Excitement Gratitude Envy Jealousy Empathy Compassion Pride Hope Confusion Regret Loneliness Boredom Satisfaction Anxiety\"." + 
                   "Only choose from the emotions list for your answer.";
        }
    }
}
