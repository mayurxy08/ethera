package com.mycompany.myapp.service;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import model.SentimentResult;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Service
public class AnalyzeSentimentService {

    public String getToneOfSpeech() throws Exception{
        Configuration configuration = new Configuration();
        configuration
            .setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration
            .setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration
            .setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(
            configuration);
        InputStream stream = new FileInputStream(new File("./test.wav"));
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
