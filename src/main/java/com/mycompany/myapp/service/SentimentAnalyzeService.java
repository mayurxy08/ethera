package com.mycompany.myapp.service;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import model.SentimentClassification;
import model.SentimentResult;
import org.ejml.simple.SimpleMatrix;
import org.springframework.stereotype.Service;
import java.util.Properties;

public class SentimentAnalyzeService {
    static Properties props;
    static StanfordCoreNLP pipeline;

    public void initialize() {
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and sentiment
        props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        props.setProperty("tokenize.whitespace", "true");
        props.setProperty("ssplit.eolonly", "true");
        // Add something here, or it will load all three default models
//        props.setProperty("ner.model","english.all.3class.distsim.crf.ser.gz");
        pipeline = new StanfordCoreNLP(props);

    }

    public SentimentResult getSentimentResult(String text) {

        SentimentResult sentimentResult = new SentimentResult();
        SentimentClassification sentimentClass = new SentimentClassification();

        if (text != null && text.length() > 0) {

            // run all Annotators on the text
            Annotation annotation = pipeline.process(text);

            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                // this is the parse tree of the current sentence
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
                String sentimentType = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

                sentimentClass.setVeryPositive((double)Math.round(sm.get(4) * 100d));
                sentimentClass.setPositive((double)Math.round(sm.get(3) * 100d));
                sentimentClass.setNeutral((double)Math.round(sm.get(2) * 100d));
                sentimentClass.setNegative((double)Math.round(sm.get(1) * 100d));
                sentimentClass.setVeryNegative((double)Math.round(sm.get(0) * 100d));

                sentimentResult.setSentimentScore(RNNCoreAnnotations.getPredictedClass(tree));
                sentimentResult.setSentimentType(sentimentType);
                sentimentResult.setSentimentClass(sentimentClass);
            }

        }


        return sentimentResult;
    }
}
