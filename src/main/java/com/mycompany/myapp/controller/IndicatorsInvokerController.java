package com.mycompany.myapp.controller;

import com.mycompany.myapp.service.AnalyzeSentimentService;
import com.mycompany.myapp.service.EmotionDetectionService;
import model.InvokerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
public class IndicatorsInvokerController {

    @Autowired
    private EmotionDetectionService emotionDetectionService;

    @Autowired
    private AnalyzeSentimentService analyzeSentimentService;

    @GetMapping("/submit/{id}/{index}")
    String DetermineIndicator(@PathVariable("id") String customerId, @PathVariable("index") int index) throws Exception {
        String avgEmotion = emotionDetectionService.getEmotions();
        String analyzedTone = analyzeSentimentService.getToneOfSpeech();
        String resultedIndicator= "";
        switch(avgEmotion) {
            case "sad" :
                if (analyzedTone.equals("Negative")){
                        resultedIndicator = "MildMDD";
                } else if (analyzedTone.equals("Neutral")){
                    resultedIndicator = "low";
                } else if (analyzedTone.equals("Positive")) {
                    resultedIndicator = "None";
                }
                break;
            case "angry" :
            if (analyzedTone.equals("Negative")){
                resultedIndicator = "SevereMDD";
            } else if (analyzedTone.equals("Neutral")){
                resultedIndicator = "Mild";
            } else if (analyzedTone.equals("Positive")) {
                resultedIndicator = "Moderate";
            }
                break;
            default:
                resultedIndicator = "Mild";
                break;
        }
        return resultedIndicator;
    }
}
