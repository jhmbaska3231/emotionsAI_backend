package com.example.fyp;

import okhttp3.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.File;
// import java.io.FileInputStream;
import java.io.IOException;

public class AudioToText {

    private static final String API_URL = "https://api.openai.com/v1/audio/transcriptions";
    private static final String API_KEY = "sk-proj-fvYUlxUBz1u1HFy6V8ogT3BlbkFJ5UpcEXlVUmXVkHBivGBW";

    // Sample usage
    // public static void main(String[] args) throws IOException {
    //     File audioFile = new File("path_to_audio_file.wav");
    //     String transcript = transcribeAudio(audioFile);
    //     System.out.println("Transcription: " + transcript);
    // }

    public static String transcribeAudio(File audioFile) throws IOException {
        OkHttpClient client = new OkHttpClient();
        
        // Create the file body for the request
        RequestBody fileBody = RequestBody.create(audioFile, MediaType.parse("audio/wav"));

        // Build the multipart form request body
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", audioFile.getName(), fileBody)
                .build();
        
        // Build the HTTP request
        Request request = new Request.Builder()
                .url(API_URL)
                .post(formBody)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        // Execute the request
        Response response = client.newCall(request).execute();
        // Convert response body to string
        String responseBody = IOUtils.toString(response.body().byteStream(), "UTF-8");
        // Parse the JSON response to the transcription text
        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
        return jsonObject.get("text").getAsString();
    }
}
