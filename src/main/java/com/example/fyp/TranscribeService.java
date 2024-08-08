package com.example.fyp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

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

@Service
public class TranscribeService {

    @Value("${openai.api.key}")
    private String apiKey;

    private SimpleOpenAI openAIClient;
    private FunctionExecutor functionExecutor;

    private final DiaryService diaryService;

    public TranscribeService(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    // retrieve api key
    @PostConstruct
    public void init() {
        openAIClient = SimpleOpenAI.builder().apiKey(apiKey).build();
    }

    // General context based on Singapore's 2024 calendar of holidays
    private static final Map<Month, String> GENERAL_CONTEXTS = new HashMap<>();
    static {
        GENERAL_CONTEXTS.put(Month.JANUARY, "New Year's Day and preparation for Chinese New Year.");
        GENERAL_CONTEXTS.put(Month.FEBRUARY, "Chinese New Year celebrations.");
        GENERAL_CONTEXTS.put(Month.MARCH, "Good Friday.");
        GENERAL_CONTEXTS.put(Month.APRIL, "Hari Raya Puasa.");
        GENERAL_CONTEXTS.put(Month.MAY, "Labour Day and Vesak Day celebrations.");
        GENERAL_CONTEXTS.put(Month.JUNE, "Hari Raya Haji");
        GENERAL_CONTEXTS.put(Month.JULY, "No major holidays.");
        GENERAL_CONTEXTS.put(Month.AUGUST, "National Day and celebrations of Singapore's independence.");
        GENERAL_CONTEXTS.put(Month.SEPTEMBER, "No major holidays.");
        GENERAL_CONTEXTS.put(Month.OCTOBER, "Deepavali celebrations.");
        GENERAL_CONTEXTS.put(Month.NOVEMBER, "No major holidays.");
        GENERAL_CONTEXTS.put(Month.DECEMBER, "Christmas and year-end festivities.");
    }
    
    // Transcribe Function with Assistant API
    public String transcribeTextToEmotion(String userId, String text, String assistantId) {
        // Retrieve current month
        int currentMonth = LocalDateTime.now().getMonthValue();

        // Retrieve user's diary entries
        List<DiaryWithTargetEmotionsDTO> diaryEntries = diaryService.allDiariesWithTargetEmotionsByMonthAndUserId(userId, currentMonth);

        // General context for the current month
        String generalContext = GENERAL_CONTEXTS.get(LocalDateTime.now().getMonth());
        String userContext;

        // Check if there are no diary entries
        if (diaryEntries.isEmpty()) {
            // System.out.println("No diary entries found for user: " + userId);
            userContext = "No User Context Available";
        } else {
            // Prepare the context string
            StringBuilder contextBuilder = new StringBuilder();
            for (DiaryWithTargetEmotionsDTO entry : diaryEntries) {
                // System.out.println("\nDiary Content: " + entry.getInputText());
                // System.out.println("\nDiary Entry: " + entry.toString());
                contextBuilder.append(entry.toString()).append("\n\n");
            }
            userContext = contextBuilder.toString();
        }  

        // Combine user context with general context
        String combinedContext = "### General Context ###\n" + generalContext + "\n\n### User Context ###\n" + userContext;

        return transcribeTextWithAssistant(userId, text, assistantId, combinedContext);
    }

    // Transcribe Function with Assistant API
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

    public String analyzeEmotion(String userId, String text) throws IOException {
        return transcribeTextToEmotion(userId, text, "asst_cp386dW1y0ZPURztEfWkDf1W");
    }

}
