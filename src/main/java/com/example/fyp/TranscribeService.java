package com.example.fyp;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

// import com.google.gson.JsonArray;
// import com.google.gson.JsonObject;
// import com.google.gson.JsonParser;

// import okhttp3.MediaType;
// import okhttp3.OkHttpClient;
// import okhttp3.Request;
// import okhttp3.RequestBody;
// import okhttp3.Response;

import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.common.content.ContentPart;
import io.github.sashirestela.openai.common.function.FunctionExecutor;
import io.github.sashirestela.openai.domain.assistant.ThreadMessageRequest;
import io.github.sashirestela.openai.domain.assistant.ThreadMessageRole;
import io.github.sashirestela.openai.domain.assistant.ThreadRunRequest;
import io.github.sashirestela.openai.domain.assistant.ThreadRequest;
import io.github.sashirestela.openai.domain.assistant.ThreadRun;
import io.github.sashirestela.openai.domain.assistant.ThreadRunSubmitOutputRequest;
import io.github.sashirestela.openai.domain.assistant.ThreadRunSubmitOutputRequest.ToolOutput;

// import org.apache.commons.io.IOUtils;

@Service
public class TranscribeService {
    
    @Autowired
    private UserRepository userRepository;

    @Value("${openai.api.key}")
    private String apiKey;
    
    // private static final String apiUrl = "https://api.openai.com/v1/chat/completions";
    private SimpleOpenAI openAIClient =  SimpleOpenAI.builder().apiKey(apiKey).build();
    private FunctionExecutor functionExecutor; // Ensure this is initialized with your functions

    // Validation for free user
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
    

    // Transcribe Function with Assistant API
    public String transcribeTextToEmotion(String text, String assistantId) {
        // Create a new thread
        var thread = openAIClient.threads().create(ThreadRequest.builder().build()).join();
        var threadId = thread.getId();

        // Add the user message to the thread
        openAIClient.threadMessages()
                .create(threadId, ThreadMessageRequest.builder()
                        .role(ThreadMessageRole.USER)
                        .content(text)
                        .build())
                .join();

        // Create and run the thread
        var threadRun = openAIClient.threadRuns()
                .createAndPoll(threadId, ThreadRunRequest.builder()
                        .assistantId(assistantId)
                        .build());

        // Handle the thread run response
        return handleRun(threadRun, threadId);
    }

    private String handleRun(ThreadRun threadRun, String threadId) {
        StringBuilder responseContent = new StringBuilder();

        if (threadRun.getStatus().equals(ThreadRun.RunStatus.REQUIRES_ACTION)) {
            var toolCalls = threadRun.getRequiredAction().getSubmitToolOutputs().getToolCalls();
            var toolOutputs = functionExecutor.executeAll(toolCalls,
                    (toolCallId, result) -> ToolOutput.builder()
                            .toolCallId(toolCallId)
                            .output(result)
                            .build());
            var runSubmitTool = openAIClient.threadRuns()
                    .submitToolOutputAndPoll(threadId, threadRun.getId(), ThreadRunSubmitOutputRequest.builder()
                            .toolOutputs(toolOutputs)
                            .build());
            responseContent.append(handleRun(runSubmitTool, threadId));
        } else {
            var threadMessages = openAIClient.threadMessages().getList(threadId).join();
            var answer = threadMessages.stream()
                    .filter(msg -> msg.getRole().equals(ThreadMessageRole.ASSISTANT))
                    .flatMap(msg -> msg.getContent().stream())
                    .filter(contentPart -> contentPart instanceof ContentPart.ContentPartTextAnnotation)
                    .map(contentPart -> (ContentPart.ContentPartTextAnnotation) contentPart)
                    .map(contentPartTextAnnotation -> contentPartTextAnnotation.getText().getValue())
                    .collect(Collectors.joining("\n"));
            // System.out.println(answer.getClass());
            responseContent.append(answer);
        }

        
        return responseContent.toString().trim();
    }

    // analyzeEmotion Functions
    public String analyzeEmotion(String text) throws IOException {
        return transcribeTextToEmotion(text, "asst_cp386dW1y0ZPURztEfWkDf1W");
    }

    public String analyzeEmotionFreeUser(String text) throws IOException {
        return transcribeTextToEmotion(text, "asst_wc3JF2CDHF6BgY0wgcc8y3SW");
    }

    // private String analyzeEmotionWithModel(String text, String model) throws IOException {
    //     OkHttpClient client = new OkHttpClient();
    
    //     String prompt = generatePrompt();
    
    //     JsonObject requestBodyJson = new JsonObject();
    //     requestBodyJson.addProperty("model", model);
    //     requestBodyJson.add("messages", createMessages(prompt, text));
    
    //     RequestBody body = RequestBody.create(requestBodyJson.toString(), MediaType.parse("application/json"));
    
    //     Request request = new Request.Builder()
    //             .url(apiUrl)
    //             .post(body)
    //             .addHeader("Authorization", "Bearer " + apiKey)
    //             .addHeader("Content-Type", "application/json")
    //             .build();
    
    //     Response response = client.newCall(request).execute();
    //     if (!response.isSuccessful()) {
    //         throw new IOException("Unexpected response code: " + response);
    //     }
    
    //     return parseResponse(response);
    // }

    // private String generatePrompt() {
    //     return "###Instruction###\n" +
    //             "You will be provided a text. Your task is to analyze the provided text to determine the emotion(s) it conveys from the provided list. Assess not only individual sentences but also consider the overall context and emotional flow of the entire text.\n" +
    //             "\n" +
    //             "###Emotions List###\n" +
    //             "\"Joy, Happiness, Sadness, Anger, Fear, Surprise, Disgust, Contempt, Love, Trust, Anticipation, Guilt, Shame, Excitement, Gratitude, Envy, Jealousy, Empathy, Compassion, Pride, Hope, Confusion, Regret, Loneliness, Boredom, Satisfaction, Anxiety\"\n" +
    //             "\n" +
    //             "###Steps###\n" +
    //             "1. **Identify the Suitable Emotion(s):** For each sentence and for the overall text, identify the emotion(s) expressed.\n" +
    //             "2. **Assess Emotional Intensity:** Determine the emotional intensity (high, medium, low) for each detected emotion.\n" +
    //             "   - *Define Criteria*: Provide specific linguistic or contextual cues to classify intensity levels.\n" +
    //             "3. **Determine Sentiment:** Indicate the sentiment as \"positive,\" \"neutral,\" or \"negative\" for each emotion.\n" +
    //             "4. **Calculate Weight:** Assign a weight to each detected emotion relative to the entire text, based on the proportion of emotional words or intensity levels contributing to that emotion.\n" +
    //             "   - *Normalization*: Ensure that the sum of weights across all emotions equals 100%.\n" +
    //             "5. **Consider Emotional Flow:** Analyze how emotions develop or change throughout the text, noting any overarching themes or shifts.\n" +
    //             "6. **Address Mixed Sentiments and Subtleties:** Evaluate and note any mixed sentiments, such as bittersweet or melancholic expressions, and consider subtleties like sarcasm or irony.\n" +
    //             "\n" +
    //             "###Output Template###\n" +
    //             "```\n" +
    //             "Annotated Text: {}\n" +
    //             "Detected Emotion(s): x (a%), y (b%), z (c%)\n" +
    //             "Overall Emotional Intensity: {majority intensity}\n" +
    //             "Overall Sentiment: {majority sentiment} (i% positive, j% neutral, k% negative)\n" +
    //             "Notes: Additional observations on emotional flow, mixed sentiments, or linguistic subtleties.\n" +
    //             "```\n" +
    //             "\n" +
    //             "###Example###\n" +
    //             "Text: \"I felt great joy when I received the news, but also a tinge of sadness.\"\n" +
    //             "\n" +
    //             "Annotated Text: I felt great joy when I received the news(Joy, high, positive, 70%), but also a tinge of sadness. (Sadness, low, negative, 30%)\n" +
    //             "Detected Emotions: Joy (70%), Sadness (30%)\n" +
    //             "Overall Emotional Intensity: high\n" +
    //             "Overall Sentiment: positive (70% positive, 0% neutral, 30% negative)\n" +
    //             "Notes: The joy overwhelms the sadness in emotional contribution, highlighting a predominantly positive reaction with a minor negative undertone.\n";
    // }

    // private JsonArray createMessages(String prompt, String text) {
    //     JsonObject systemMessage = new JsonObject();
    //     systemMessage.addProperty("role", "system");
    //     systemMessage.addProperty("content", prompt);
    
    //     JsonObject userMessage = new JsonObject();
    //     userMessage.addProperty("role", "user");
    //     userMessage.addProperty("content", text);
    
    //     JsonArray messages = new JsonArray();
    //     messages.add(systemMessage);
    //     messages.add(userMessage);
    
    //     return messages;
    // }

    // private String parseResponse(Response response) throws IOException {
    //     String responseBody = IOUtils.toString(response.body().byteStream(), "UTF-8");
    //     JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
    
    //     if (jsonObject.has("choices") && !jsonObject.get("choices").isJsonNull()) {
    //         JsonArray choicesArray = jsonObject.get("choices").getAsJsonArray();
            
    //         if (choicesArray.size() > 0) {
    //             JsonObject messageObject = choicesArray.get(0).getAsJsonObject().get("message").getAsJsonObject();
    //             if (messageObject != null && messageObject.has("content")) {
    //                 return messageObject.get("content").getAsString();
    //             }
    //         }
    //     }
    
    //     throw new IOException("Invalid response from the API");
    // }
}
