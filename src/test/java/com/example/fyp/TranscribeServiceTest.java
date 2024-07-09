package com.example.fyp;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import okhttp3.*;

public class TranscribeServiceTest {

    @InjectMocks
    private TranscribeService transcribeService;

    @Mock
    private OkHttpClient mockClient;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAnalyzeEmotion() throws IOException {
        // Mock the response from OkHttpClient
        String jsonResponse = "{ \"choices\": [{ \"message\": { \"content\": \"Annotated Text: 'I just heard the news about the promotion; I'm ecstatic but also a bit nervous about the new responsibilities. (Ecstasy, high, positive, 70%) (Nervousness, medium, negative, 30%)'\\nDetected Emotions: Ecstasy (70%), Nervousness (30%)\\nOverall Emotional Intensity: high\\nOverall Sentiment: mixed (positive and negative)\" } }] }";
        ResponseBody responseBody = ResponseBody.create(jsonResponse, MediaType.parse("application/json"));
        Response mockResponse = new Response.Builder()
                .request(new Request.Builder().url("https://api.openai.com/v1/chat/completions").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(responseBody)
                .build();

        Call mockCall = mock(Call.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        // Test data
        String text = "I just heard the news about the promotion; I'm ecstatic but also a bit nervous about the new responsibilities.";

        // Call the method and verify the result
        String result = transcribeService.analyzeEmotion(text);
        System.out.println(result);
        assertNotNull(result);
        assertTrue(result.contains("Annotated Text: 'I just heard the news about the promotion; I'm ecstatic but also a bit nervous about the new responsibilities."));
        assertTrue(result.contains("(Ecstasy, high, positive, 70%)"));
        assertTrue(result.contains("(Nervousness, medium, negative, 30%)"));
        assertTrue(result.contains("Detected Emotions: Ecstasy (70%), Nervousness (30%)"));
        assertTrue(result.contains("Overall Emotional Intensity: high"));
        assertTrue(result.contains("Overall Sentiment: mixed (positive and negative)"));
    }

    @Test
    public void testAnalyzeEmotion_SecondCase() throws IOException {
        // Mock the response from OkHttpClient
        String jsonResponse = "{ \"choices\": [{ \"message\": { \"content\": \"Annotated Text: 'The meeting dragged on for hours with seemingly no end, which was exhausting. (Exhaustion, high, negative, 40%) Just when I thought it couldn't get any worse, my proposal got unexpectedly approved, turning my exasperation into relief. (Surprise, medium, positive, 30%) (Relief, medium, positive, 30%)'\\nDetected Emotions: Exhaustion (40%), Surprise (30%), Relief (30%)\\nOverall Emotional Intensity: medium\\nOverall Sentiment: slightly positive\" } }] }";
        ResponseBody responseBody = ResponseBody.create(jsonResponse, MediaType.parse("application/json"));
        Response mockResponse = new Response.Builder()
                .request(new Request.Builder().url("https://api.openai.com/v1/chat/completions").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(responseBody)
                .build();

        Call mockCall = mock(Call.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        // Test data
        String text = "The meeting dragged on for hours with seemingly no end, which was exhausting. Just when I thought it couldn't get any worse, my proposal got unexpectedly approved, turning my exasperation into relief.";

        // Call the method and verify the result
        String result = transcribeService.analyzeEmotion(text);
        assertNotNull(result);
        System.out.println(result);
        assertTrue(result.contains("Annotated Text: 'The meeting dragged on for hours with seemingly no end, which was exhausting."));
        assertTrue(result.contains("(Exhaustion, high, negative, 40%)"));
        assertTrue(result.contains("Just when I thought it couldn't get any worse, my proposal got unexpectedly approved, turning my exasperation into relief."));
        assertTrue(result.contains("(Surprise, medium, positive, 30%)"));
        assertTrue(result.contains("(Relief, medium, positive, 30%)"));
        assertTrue(result.contains("Detected Emotions: Exhaustion (40%), Surprise (30%), Relief (30%)"));
        assertTrue(result.contains("Overall Emotional Intensity: medium"));
        assertTrue(result.contains("Overall Sentiment: slightly positive"));
    }
}
