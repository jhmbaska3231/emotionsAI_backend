package com.example.fyp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// import com.google.gson.JsonArray;
// import com.google.gson.JsonObject;
// import com.google.gson.JsonParser;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.common.content.ContentPart;
import io.github.sashirestela.openai.common.function.FunctionExecutor;
import io.github.sashirestela.openai.domain.assistant.ThreadMessageRequest;
import io.github.sashirestela.openai.domain.assistant.ThreadMessageRole;
import io.github.sashirestela.openai.domain.assistant.ThreadRequest;
import io.github.sashirestela.openai.domain.assistant.ThreadRun;
import io.github.sashirestela.openai.domain.assistant.ThreadRunRequest;
import io.github.sashirestela.openai.domain.assistant.ThreadRunSubmitOutputRequest;
import io.github.sashirestela.openai.domain.assistant.ThreadRunSubmitOutputRequest.ToolOutput;
import jakarta.annotation.PostConstruct;

// import okhttp3.MediaType;
// import okhttp3.OkHttpClient;
// import okhttp3.Request;
// import okhttp3.RequestBody;
// import okhttp3.Response;

// import org.apache.commons.io.IOUtils;

// jame's account's userId: 34d8b4c8-9061-7075-e98d-3173bb8c43a1

@Service
public class TranscribeService {

    @Value("${openai.api.key}")
    private String apiKey;

    // private static final String apiUrl = "https://api.openai.com/v1/chat/completions";

    private SimpleOpenAI openAIClient; // new code
    // private SimpleOpenAI openAIClient = SimpleOpenAI.builder().apiKey(apiKey).build(); // new code
    private FunctionExecutor functionExecutor; // Ensure this is initialized with your functions // new code

    private final DiaryService diaryService;

    // General context based on Singapore's calendar of holidays
    private static final Map<Month, String> GENERAL_CONTEXTS = new HashMap<>();
    static {
        GENERAL_CONTEXTS.put(Month.JANUARY, "New Year's Day and preparation for Chinese New Year.");
        GENERAL_CONTEXTS.put(Month.FEBRUARY, "Chinese New Year celebrations.");
        GENERAL_CONTEXTS.put(Month.MARCH, "No major holidays.");
        GENERAL_CONTEXTS.put(Month.APRIL, "Good Friday and preparation for Hari Raya Puasa.");
        GENERAL_CONTEXTS.put(Month.MAY, "Labour Day and Hari Raya Puasa celebrations.");
        GENERAL_CONTEXTS.put(Month.JUNE, "Vesak Day and school holidays.");
        GENERAL_CONTEXTS.put(Month.JULY, "No major holidays.");
        GENERAL_CONTEXTS.put(Month.AUGUST, "National Day and celebrations of Singapore's independence.");
        GENERAL_CONTEXTS.put(Month.SEPTEMBER, "Mid-Autumn Festival preparations.");
        GENERAL_CONTEXTS.put(Month.OCTOBER, "Children's Day and preparations for Deepavali.");
        GENERAL_CONTEXTS.put(Month.NOVEMBER, "Deepavali celebrations.");
        GENERAL_CONTEXTS.put(Month.DECEMBER, "Christmas and year-end festivities.");
    }

    public TranscribeService(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    // this is to retrieve the api key // new code
    @PostConstruct
    public void init() {
        openAIClient = SimpleOpenAI.builder().apiKey(apiKey).build();
    }

    public String transcribeTextToEmotion(String userId, String text, String assistantId) {
        // System.out.println("Step 1");
        // Retrieve user's diary entries
        List<DiaryWithTargetEmotionsDTO> diaryEntries = diaryService.allDiariesWithTargetEmotionsByMonthAndUserId(userId, LocalDateTime.now().getMonthValue());
    
        // General context for the current month
        String generalContext = GENERAL_CONTEXTS.get(LocalDateTime.now().getMonth());
        String userContext;

        // Check if there are no diary entries
        if (diaryEntries.isEmpty()) {
            System.out.println("No diary entries found for user: " + userId);
            userContext = "No User Context Available";
        }else{
            // Prepare the context string
            StringBuilder contextBuilder = new StringBuilder();
            for (DiaryWithTargetEmotionsDTO entry : diaryEntries) {
                System.out.println("\nDiary Content: " + entry.getInputText());
                System.out.println("\nDiary Entry: " + entry.toString());
                contextBuilder.append(entry.toString()).append("\n\n");
            }
            userContext = contextBuilder.toString();
        }  

        // Combine user context with general context
        String combinedContext = "### General Context ###\n" + generalContext + "\n\n### User Context ###\n" + userContext;
    
        return transcribeTextWithAssistant(userId, text, assistantId, combinedContext);
    }
    
    // Transcribe Function with Assistant API // new code
    public String transcribeTextWithAssistant(String userId, String text, String assistantId, String context) {

        // Step 2: Upload diary entries to an Assistant thread
        // System.out.println("Step 2");
        var thread = openAIClient.threads().create(ThreadRequest.builder().build()).join();
        var threadId = thread.getId();

        // Step 3: Add the context and user input as a single message
        // System.out.println("Step 3");
        String combinedMessage = context + "\n\n### Analyze the following text ###\n" + text;
        
        // Step 4: Submit input text with context
        // System.out.println("Step 4");
        openAIClient.threadMessages()
                .create(threadId, ThreadMessageRequest.builder()
                        .role(ThreadMessageRole.USER)
                        .content(combinedMessage)
                        .build())
                .join();

        // Step 5: Run the thread
        // System.out.println("Step 5");
        var threadRun = openAIClient.threadRuns()
                .createAndPoll(threadId, ThreadRunRequest.builder()
                        .assistantId(assistantId)
                        .build());

        return handleRun(threadRun, threadId);
    }

    // new code
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

    // analyzeEmotion Function // new code
    public String analyzeEmotion(String userId, String text) throws IOException {
        return transcribeTextToEmotion(userId, text, "asst_cp386dW1y0ZPURztEfWkDf1W");
    }

    // public String analyzeEmotion(String text) throws IOException {

    //     OkHttpClient client = new OkHttpClient();

    //     // Creating Json Objects for System and User
    //     String prompt = "###Instruction###\n" +
    //         "You will be provided a text. Your task is to analyze the provided text to determine the emotion(s) it conveys from the provided list. Assess not only individual sentences but also consider the overall context and emotional flow of the entire text.\n" +
    //         "\n" +
    //         "###Emotions List###\n" +
    //         "\"Joy, Happiness, Sadness, Anger, Fear, Surprise, Disgust, Contempt, Love, Trust, Anticipation, Guilt, Shame, Excitement, Gratitude, Envy, Jealousy, Empathy, Compassion, Pride, Hope, Confusion, Regret, Loneliness, Boredom, Satisfaction, Anxiety\"\n" +
    //         "\n" +
    //         "###Steps###\n" +
    //         "1. **Identify the Suitable Emotion(s):** For each sentence and for the overall text, identify the emotion(s) expressed.\n" +
    //         "2. **Assess Emotional Intensity:** Determine the emotional intensity (High, Medium, Low) for each detected emotion.\n" +
    //         "   - *Define Criteria*: Provide specific linguistic or contextual cues to classify intensity levels.\n" +
    //         "3. **Determine Sentiment:** Indicate the sentiment as \"Positive,\" \"Neutral,\" or \"Negative\" for each emotion.\n" +
    //         "4. **Calculate Weight:** Assign a weight to each detected emotion relative to the entire text, based on the proportion of emotional words or intensity levels contributing to that emotion.\n" +
    //         "   - *Normalization*: Ensure that the sum of weights across all emotions equals 100%.\n" +
    //         "5. **Consider Emotional Flow:** Analyze how emotions develop or change throughout the text, noting any overarching themes or shifts.\n" +
    //         "6. **Address Mixed Sentiments and Subtleties:** Evaluate and note any mixed sentiments, such as bittersweet or melancholic expressions, and consider subtleties like sarcasm or irony.\n" +
    //         "\n" +
    //         "###Output Template###\n" +
    //         "```\n" +
    //         "Annotated Text: {}\n" +
    //         "Detected Emotion(s): x (a%), y (b%), z (c%)\n" +
    //         "Overall Emotional Intensity: {majority intensity}\n" +
    //         "Overall Sentiment: {majority sentiment} (i% Positive, j% Neutral, k% Negative)\n" +
    //         "Explanation: Additional observations on emotional flow, mixed sentiments, or linguistic subtleties.\n" +
    //         "```\n" +
    //         "\n" +
    //         "###Example###\n" +
    //         "Text: \"I felt great joy when I received the news, but also a tinge of sadness.\"\n" +
    //         "\n" +
    //         "Annotated Text: I felt great joy when I received the news(Joy, High, Positive, 70%), but also a tinge of sadness. (Sadness, Low, Negative, 30%)\n" +
    //         "Detected Emotion(s): Joy (70%), Sadness (30%)\n" +
    //         "Overall Emotional Intensity: High\n" +
    //         "Overall Sentiment: Positive (70% Positive, 0% Neutral, 30% Negative)\n" +
    //         "Explanation: The joy overwhelms the sadness in emotional contribution, highlighting a predominantly positive reaction with a minor negative undertone.\n";

    //     // System JsonObject
    //     JsonObject systemMessage = new JsonObject();
    //     systemMessage.addProperty("role", "system");
    //     systemMessage.addProperty("content", prompt);

    //     // User JsonObject
    //     JsonObject userMessage = new JsonObject();
    //     userMessage.addProperty("role", "user");
    //     userMessage.addProperty("content", text);

    //     JsonArray messages = new JsonArray();
    //     messages.add(systemMessage);
    //     messages.add(userMessage);

    //     JsonObject requestBodyJson = new JsonObject();
    //     // requestBodyJson.addProperty("model", "gpt-3.5-turbo-0125"); // using default gpt-3.5 model
    //     // requestBodyJson.addProperty("model", "ft:gpt-3.5-turbo-0125:personal::9jACErVy"); // using fine tuned model
    //     requestBodyJson.addProperty("model", "gpt-4o"); // using gpt-4o model
    //     requestBodyJson.add("messages", messages);

    //     RequestBody body = RequestBody.create(requestBodyJson.toString(), MediaType.parse("application/json"));

    //     Request request = new Request.Builder()
    //             .url(apiUrl)
    //             .post(body)
    //             .addHeader("Authorization", "Bearer " + apiKey)
    //             .addHeader("Content-Type", "application/json")
    //             .build();

    //     Response response = client.newCall(request).execute();
    //     if (!response.isSuccessful()) {
    //         throw new IOException("Unexpected code " + response);
    //     }

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
