package com.example.fyp;

import okhttp3.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class DeepPurple {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "sk-proj-fvYUlxUBz1u1HFy6V8ogT3BlbkFJ5UpcEXlVUmXVkHBivGBW";

    // Sample Usage
    // public static void main(String[] args) throws IOException {
    //     String inputText = "Your input text here";
    //     String emotion = analyzeEmotion(inputText);
    //     System.out.println("Emotion: " + emotion);
    // }

    public static String analyzeEmotion(String text) throws IOException {
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
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
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
