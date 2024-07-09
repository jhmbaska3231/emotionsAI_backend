// package com.example.fyp;

// import org.junit.Test;
// import org.junit.Before;
// import static org.junit.Assert.*;
// import java.io.File;
// import java.io.IOException;

// public class AudioToTextTest {
//     private AudioToText audioToText;

//     @Before
//     public void setUp() {
//         audioToText = new AudioToText();
//     }

//     @Test
//     public void testTranscribeAudio_ValidFile() throws IOException {
//         File audioFile = new File("src\\test\\java\\com\\example\\fyp\\valid.wav");
//         String transcript = audioToText.transcribeAudio(audioFile);
//         System.out.println(transcript);
//         assertNotNull(transcript);
//         assertFalse(transcript.isEmpty());
//     }

//     @Test
//     public void testTranscribeAudio_UnsupportedFileType() {
//         File audioFile = new File("src\\test\\java\\com\\example\\fyp\\test.txt");
//         try {
//             audioToText.transcribeAudio(audioFile);
//             fail("Expected IOException to be thrown");
//         } catch (IOException e) {
//             assertEquals("Unsupported file type. Supported types are: mp3, mp4, mpeg, mpga, m4a, wav, and webm.", e.getMessage());
//         }
//     }

//     @Test
//     public void testTranscribeAudio_FileTooLarge() {
//         File audioFile = new File("src\\test\\java\\com\\example\\fyp\\large.wav");
//         try {
//             audioToText.transcribeAudio(audioFile);
//             fail("Expected IOException to be thrown");
//         } catch (IOException e) {
//             assertEquals("File size exceeds the maximum limit of 25 MB.", e.getMessage());
//         }
//     }

//     @Test(expected = IOException.class)
//     public void testTranscribeAudio_NonExistentFile() throws IOException {
//         File audioFile = new File("nonexistent.wav");
//         audioToText.transcribeAudio(audioFile);
//     }

//     @Test
//     public void testIsSupportedFileType() {
//         File mp3File = new File("src\\test\\java\\com\\example\\fyp\\test.mp3");
//         File txtFile = new File("src\\test\\java\\com\\example\\fyp\\test.txt");
        
//         assertTrue(audioToText.isSupportedFileType(mp3File));
//         assertFalse(audioToText.isSupportedFileType(txtFile));
//     }

//     @Test
//     public void testGetMediaType() {
//         File mp3File = new File("src\\test\\java\\com\\example\\fyp\\test.mp3");
//         File wavFile = new File("src\\test\\java\\com\\example\\fyp\\valid.wav");
//         File txtFile = new File("src\\test\\java\\com\\example\\fyp\\test.txt");

//         assertEquals("audio/mpeg", audioToText.getMediaType(mp3File));
//         assertEquals("audio/wav", audioToText.getMediaType(wavFile));
//         assertEquals("application/octet-stream", audioToText.getMediaType(txtFile));
//     }
    
// }
