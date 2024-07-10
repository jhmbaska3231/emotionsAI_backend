// package com.example.fyp;

// import org.junit.Test;
// import org.junit.Before;
// import static org.junit.Assert.*;
// import java.io.IOException;

// public class DeepPurpleTest {

//     private DeepPurple deepPurple;

//     @Before
//     public void setUp(){
//         deepPurple = new DeepPurple();
//     }


//     @Test
//     public void testAnalyzeEmotion_HappyText() throws IOException {
//         String inputText = "I am so happy and excited!";
//         String result = deepPurple.analyzeEmotion(inputText);
        
//         assertNotNull(result);
//         assertTrue(result.contains("Joy") || result.contains("Happiness"));
//         assertTrue(result.contains("Emotional Intensity"));
//         assertTrue(result.contains("Overall Sentiment: Positive"));
//     }

//     @Test
//     public void testAnalyzeEmotion_SadText() throws IOException {
//         String inputText = "I am feeling very sad and lonely.";
//         String result = deepPurple.analyzeEmotion(inputText);

//         assertNotNull(result);
//         assertTrue(result.contains("Sadness") || result.contains("Loneliness"));
//         assertTrue(result.contains("Emotional Intensity"));
//         assertTrue(result.contains("Overall Sentiment: Negative"));
//     }

//     @Test
//     public void testAnalyzeEmotion_NeutralText() throws IOException {
//         String inputText = "Today is an average day, nothing special.";
//         String result = deepPurple.analyzeEmotion(inputText);

//         assertNotNull(result);
//         assertTrue(result.contains("Emotional Intensity"));
//         assertTrue(result.contains("Overall Sentiment: Neutral"));
//     }

//     @Test(expected = IllegalArgumentException.class)
//     public void testAnalyzeEmotion_EmptyText() throws IOException {
//         deepPurple.analyzeEmotion("");
//     }

//     @Test(expected = IllegalArgumentException.class)
//     public void testAnalyzeEmotion_NullText() throws IOException {
//         deepPurple.analyzeEmotion(null);
//     }

//     @Test
//     public void testAnalyzeEmotion_ComplexText() throws IOException {
//         String inputText = "I feel happy about the new project but also anxious about the deadlines.";
//         String result = deepPurple.analyzeEmotion(inputText);

//         assertNotNull(result);
//         assertTrue(result.contains("Joy") || result.contains("Happiness"));
//         assertTrue(result.contains("Anxiety"));
//         assertTrue(result.contains("Emotional Intensity"));
//         assertTrue(result.contains("Overall Sentiment"));
//     }

//     @Test
//     public void testAnalyzeEmotion_ResponseFormat() throws IOException {
//         String inputText = "This is a test input to verify the response format.";
//         String result = deepPurple.analyzeEmotion(inputText);

//         assertNotNull(result);
//         assertTrue(result.contains("Target Emotion(s):"));
//         assertTrue(result.contains("Emotional Intensity:"));
//         assertTrue(result.contains("Overall Sentiment:"));
//     }
// }
