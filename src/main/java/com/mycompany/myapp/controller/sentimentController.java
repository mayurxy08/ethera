package com.mycompany.myapp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import com.mycompany.myapp.service.SentimentAnalyzeService;
import model.SentimentResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.decoder.adaptation.Stats;
import edu.cmu.sphinx.decoder.adaptation.Transform;
import edu.cmu.sphinx.result.WordResult;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;

@RestController
public class sentimentController {
    private static final String TARGET_URL =
        "https://vision.googleapis.com/v1/images:annotate?";
    private static final String API_KEY =
        "key=AIzaSyBpaE2_RQMjtyS4GHHW3ShlY0NkE7rGdtM";

    @GetMapping("/emotion")
    public String test() throws URISyntaxException, IOException {
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
                System.out.println("summertime sadness");
            }
        }
        return resp;
    }


    @GetMapping("/audio")
    String audioToText() throws Exception {
//        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
//        map.add("file", new ClassPathResource("test.wav"));
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//        RestTemplate template = new RestTemplate();
//        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new    HttpEntity<LinkedMultiValueMap<String, Object>>(
//            map, headers);
//        ResponseEntity<String> result = template.exchange(
//            "https://www.google.com/speech-api/v1/recognize?client=chromium&lang=ar-QA&maxresults=10", HttpMethod.POST, requestEntity,
//            String.class);


//        try (SpeechClient speech = SpeechClient.create()) {
//            Path path = Paths.get("test.wav");
//            byte[] data = Files.readAllBytes(path);
//            ByteString audioBytes = ByteString.copyFrom(data);
//
//            // Configure request with local raw PCM audio
//            RecognitionConfig config =
//                RecognitionConfig.newBuilder()
//                    .setEncoding(AudioEncoding.LINEAR16)
//                    .setLanguageCode("en-US")
//                    .setSampleRateHertz(16000)
//                    .build();
//            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();
//
//            // Use blocking call to get audio transcript
//            RecognizeResponse response = speech.recognize(config, audio);
//            List<SpeechRecognitionResult> results = response.getResultsList();
//
//            for (SpeechRecognitionResult result : results) {
//                // There can be several alternative transcripts for a given chunk of speech. Just use the
//                // first (most likely) one here.
//                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
//                System.out.printf("Transcription: %s%n", alternative.getTranscript());
//            }
//        }
        System.out.println("Loading models...");

        Configuration configuration = new Configuration();
        configuration
            .setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration
            .setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration
            .setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(
            configuration);
        InputStream stream = new ClassPathResource("/test.wav").getInputStream();
        stream.skip(44);

        // Simple recognition with generic model
        String finalWords = "";
        recognizer.startRecognition(stream);
        SpeechResult result;
        while ((result = recognizer.getResult()) != null) {
            System.out.format("Hypothesis: %s\n", result.getHypothesis());
            finalWords = result.getHypothesis();
        }
        recognizer.stopRecognition();
        System.out.println("hypothesis=> "+ finalWords);
        SentimentAnalyzeService sentimentAnalyzeService = new SentimentAnalyzeService();
        sentimentAnalyzeService.initialize();
        SentimentResult response = sentimentAnalyzeService.getSentimentResult(finalWords);
        return response.getSentimentType();
    }
}
