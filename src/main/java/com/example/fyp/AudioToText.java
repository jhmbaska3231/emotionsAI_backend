package com.example.fyp;

import okhttp3.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AudioToText {

    private static final String API_URL = "https://api.openai.com/v1/audio/transcriptions";
    private static final String API_KEY = "sk-proj-fvYUlxUBz1u1HFy6V8ogT3BlbkFJ5UpcEXlVUmXVkHBivGBW";
    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024; // 25 MB

    private static final String[] SUPPORTED_TYPES = {"mp3", "mp4", "mpeg", "mpga", "m4a", "wav", "webm"};

    // Sample usage
    // public static void main(String[] args) throws IOException {
    //     File audioFile = new File("path_to_audio_file.wav");
    //     String transcript = transcribeAudio(audioFile);
    //     System.out.println("Transcription: " + transcript);
    // }

    public static String transcribeAudio(File audioFile) throws IOException {
        if (!isSupportedFileType(audioFile)) {
            throw new IOException("Unsupported file type. Supported types are: mp3, mp4, mpeg, mpga, m4a, wav, and webm.");
        }

        if (audioFile.length() > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds the maximum limit of 25 MB.");
        }

        OkHttpClient client = new OkHttpClient();

        // Create the file body for the request
        RequestBody fileBody = RequestBody.create(audioFile, MediaType.parse(getMediaType(audioFile)));

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
        // Parse the JSON response to get the transcription text
        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
        return jsonObject.get("text").getAsString();
    }

    private static boolean isSupportedFileType(File file) {
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        for (String type : SUPPORTED_TYPES) {
            if (type.equals(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    private static String getMediaType(File file) {
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        switch (fileExtension) {
            case "mp3":
            case "mpga":
                return "audio/mpeg";
            case "mp4":
            case "m4a":
                return "audio/mp4";
            case "mpeg":
                return "audio/mpeg";
            case "wav":
                return "audio/wav";
            case "webm":
                return "audio/webm";
            default:
                return "application/octet-stream";
        }
    }
}
