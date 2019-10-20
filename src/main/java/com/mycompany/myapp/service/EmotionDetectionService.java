package com.mycompany.myapp.service;

import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

@Service
public class EmotionDetectionService {
    private static final String TARGET_URL =
        "https://vision.googleapis.com/v1/images:annotate?";
    private static final String API_KEY =
        "key=AIzaSyBpaE2_RQMjtyS4GHHW3ShlY0NkE7rGdtM";

    public String getEmotions() throws URISyntaxException, IOException {
        URL serverUrl = new URL(TARGET_URL + API_KEY);
        URLConnection urlConnection = serverUrl.openConnection();
        urlConnection.setDoOutput(true);
        HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("Content-Type", "application/json");
        BufferedWriter httpRequestBodyWriter = new BufferedWriter(new
            OutputStreamWriter(httpConnection.getOutputStream()));
        httpRequestBodyWriter.write
            ("{\"requests\":  [{ \"features\":  [ {\"type\": \"FACE_DETECTION\""
                + "}], \"image\": {\"source\": { \"imageUri\":"
                + " \"https://goinswriter.com/wp-content/uploads/2012/07/crying-child.jpg\"}}}]}");
        httpRequestBodyWriter.close();
        String response = httpConnection.getResponseMessage();
        if (httpConnection.getInputStream() == null) {
            System.out.println("No stream");
            return "Failed";
        }

        Scanner httpResponseScanner = new Scanner(httpConnection.getInputStream());
        String resp = "";
        while (httpResponseScanner.hasNext()) {
            String line = httpResponseScanner.nextLine();
            resp += line;
            System.out.println(line);  //  alternatively, print the line of response
        }
        httpResponseScanner.close();
        if (resp.contains("sorrowLikelihood")) {
            if ((resp.indexOf("POSSIBLE") - resp.indexOf("sorrowLikelihood")) < 30 ||
                (resp.indexOf("VERY_LIKELY") - resp.indexOf("sorrowLikelihood")) < 30) {
                return "sad";
            }
        }
        return "angry";
    }
}
