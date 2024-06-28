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
            return "You will be provided a text. Your task is to analyze the provided text and determine the emotion(s) it conveys from a provided list of emotions.\n" + 
                   "<emotions_list> \"Joy Happiness Sadness Anger Fear Surprise Disgust Contempt Love Trust Anticipation Guilt Shame Excitement Gratitude Envy Jealousy Empathy Compassion Pride Hope Confusion Regret Loneliness Boredom Satisfaction Anxiety\" </emotions_list>\n" + 
                   "\n" + 
                   "Use the following step-by-step instructions for each sentence in the text. Enclose all your work for these instructions in a similar structure as the original text.\n" + 
                   "\n" + 
                   "Step 1 - Identify the suitable emotion(s) presented.\n" + 
                   "\n" + 
                   "Step 2 - Assess the emotional intensity as \"high,\" \"medium,\" or \"low\".\n" + 
                   "\n" + 
                   "Step 3 - Indicate the sentiment as \"positive,\" \"neutral,\" or \"negative\".\n" + 
                   "\n" + 
                   "Step 4 - Add a weight to the detected emotion. The weight measures how much the emotion contributes to the overall sentiment of the text.\n" + 
                   "\n" + 
                   "Step 5 - At the end of the sentence, in parentheses, display the emotion detected, the emotional intensity, the sentiment and the weight of the emotion relative to the whole text. For example, (Joy, high, positive, 34%)\n" + 
                   "\n" + 
                   "Annotated Text refers to your resultant work for the instructions you followed previously.\n" + 
                   "\n" + 
                   "Detected Emotions is a list of all the detected emotions and their weightage relative to the whole text. i.e. Joy (50%), Sadness (50%)\n" + 
                   "\n" + 
                   "Overall Emotional Intensity is the average intensity of the whole text.\n" + 
                   "\n" + 
                   "Overall Sentiment is the average sentiment of the whole text.\n" + 
                   "\n" + 
                   "Only respond with this template enclosed in triple quotation:\n" + 
                   "\"\"\"\n" + 
                   "Annotated Text: {}\n" + 
                   "Detected Emotions(s): x (a%), y (b%), z (c%)\n" + 
                   "Overall Emotional Intensity: d\n" + 
                   "Overall Sentiment: e\n" + 
                   "\"\"\"\n";
        } else {
            return "Analyze the provided input text and determine the primary emotion it conveys from the list of emotions. " + 
                   "Indicate the overall sentiment of the text as \"positive,\" \"neutral,\" or \"negative.\" " + 
                   "Here is the template for the output: \"Target Emotion: x \\n" + 
                   "Overall Sentiment: yy\" \r\n" + 
                   "Emotions List: \"Joy Happiness Sadness Anger Fear Surprise Disgust Contempt Love Trust Anticipation Guilt Shame Excitement Gratitude Envy Jealousy Empathy Compassion Pride Hope Confusion Regret Loneliness Boredom Satisfaction Anxiety\". " + 
                   "Only choose from the emotions list for your answer.";
        }
    }
}
