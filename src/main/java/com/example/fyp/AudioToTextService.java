package com.example.fyp;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.commons.io.IOUtils;

@Service
public class AudioToTextService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String apiUrl = "https://api.openai.com/v1/audio/transcriptions";

    public String convertAudioToText(File audioFile) throws IOException {
        
        OkHttpClient client = new OkHttpClient();
        
        // Create the file body for the request
        RequestBody fileBody = RequestBody.create(audioFile, MediaType.parse("audio/wav"));

        // Build the multipart form request body
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", audioFile.getName(), fileBody)
                .addFormDataPart("model", "whisper-1")
                .build();
        
        // Build the HTTP request
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(formBody)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("API request failed with status: " + response.code() + ", body: " + response.body().string());
        }

        String responseBody = IOUtils.toString(response.body().byteStream(), "UTF-8");
        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

        return jsonObject.get("text").getAsString();

    }

}