package com.example.fyp;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.io.IOException;

public class DeepPurpleTest {

    private DeepPurple deepPurple;

    @Before
    public void setUp(){
        deepPurple = new DeepPurple();
    }

    @Test
    public void testAnalyzeEmotion_HappyText_PaidUser() throws IOException {
        String inputText = "I am so happy and excited!";
        String result = deepPurple.analyzeEmotion(inputText, "paid");

        System.out.println(result);
        assertNotNull(result);
        assertTrue(result.contains("Joy") || result.contains("Happiness"));
        assertTrue(result.contains("Emotional Intensity"));
        assertTrue(result.contains("Overall Sentiment: Positive"));
    }

    @Test
    public void testAnalyzeEmotion_HappyText_FreeUser() throws IOException {
        String inputText = "I am so happy and excited!";
        String result = deepPurple.analyzeEmotion(inputText, "free");

        System.out.println(result);
        assertNotNull(result);
        assertTrue(result.contains("Joy") || result.contains("Happiness"));
        assertTrue(result.contains("Overall Sentiment: Positive"));
    }

    @Test
    public void testAnalyzeEmotion_SadText_PaidUser() throws IOException {
        String inputText = "I am feeling very sad and lonely.";
        String result = deepPurple.analyzeEmotion(inputText, "paid");

        assertNotNull(result);
        assertTrue(result.contains("Sadness") || result.contains("Loneliness"));
        assertTrue(result.contains("Emotional Intensity"));
        assertTrue(result.contains("Overall Sentiment: Negative"));
    }

    @Test
    public void testAnalyzeEmotion_SadText_FreeUser() throws IOException {
        String inputText = "I am feeling very sad and lonely.";
        String result = deepPurple.analyzeEmotion(inputText, "free");

        assertNotNull(result);
        assertTrue(result.contains("Sadness") || result.contains("Loneliness"));
        assertTrue(result.contains("Overall Sentiment: Negative"));
    }

    @Test
    public void testAnalyzeEmotion_NeutralText_PaidUser() throws IOException {
        String inputText = "Today is an average day, nothing special.";
        String result = deepPurple.analyzeEmotion(inputText, "paid");

        assertNotNull(result);
        assertTrue(result.contains("Emotional Intensity"));
        assertTrue(result.contains("Overall Sentiment: Neutral"));
    }

    @Test
    public void testAnalyzeEmotion_NeutralText_FreeUser() throws IOException {
        String inputText = "Today is an average day, nothing special.";
        String result = deepPurple.analyzeEmotion(inputText, "free");

        assertNotNull(result);
        assertTrue(result.contains("Overall Sentiment: Neutral"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzeEmotion_EmptyText_PaidUser() throws IOException {
        deepPurple.analyzeEmotion("", "paid");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzeEmotion_EmptyText_FreeUser() throws IOException {
        deepPurple.analyzeEmotion("", "free");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzeEmotion_NullText_PaidUser() throws IOException {
        deepPurple.analyzeEmotion(null, "paid");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzeEmotion_NullText_FreeUser() throws IOException {
        deepPurple.analyzeEmotion(null, "free");
    }

    @Test
    public void testAnalyzeEmotion_ComplexText_PaidUser() throws IOException {
        String inputText = "I feel happy about the new project but also anxious about the deadlines.";
        String result = deepPurple.analyzeEmotion(inputText, "paid");

        assertNotNull(result);
        assertTrue(result.contains("Joy") || result.contains("Happiness"));
        assertTrue(result.contains("Anxiety"));
        assertTrue(result.contains("Emotional Intensity"));
        assertTrue(result.contains("Overall Sentiment"));
    }

    @Test
    public void testAnalyzeEmotion_ComplexText_FreeUser() throws IOException {
        String inputText = "I feel happy about the new project but also anxious about the deadlines.";
        String result = deepPurple.analyzeEmotion(inputText, "free");

        assertNotNull(result);
        assertTrue(result.contains("Overall Sentiment"));
    }

    @Test
    public void testAnalyzeEmotion_ResponseFormat_PaidUser() throws IOException {
        String inputText = "This is a test input to verify the response format.";
        String result = deepPurple.analyzeEmotion(inputText, "paid");

        assertNotNull(result);
        assertTrue(result.contains("Target Emotion(s):"));
        assertTrue(result.contains("Emotional Intensity:"));
        assertTrue(result.contains("Overall Sentiment:"));
    }

    @Test
    public void testAnalyzeEmotion_ResponseFormat_FreeUser() throws IOException {
        String inputText = "This is a test input to verify the response format.";
        String result = deepPurple.analyzeEmotion(inputText, "free");

        assertNotNull(result);
        assertTrue(result.contains("Target Emotion:"));
        assertTrue(result.contains("Overall Sentiment:"));
    }
}