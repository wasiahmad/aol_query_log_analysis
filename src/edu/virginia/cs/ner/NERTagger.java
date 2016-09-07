/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.cs.ner;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 *
 * @author Wasi
 */
public class NERTagger {

    private final CRFClassifier<CoreLabel> classifier;

    public NERTagger(String model) {
        String serializedClassifier = model;
        PrintStream err = System.err;
        System.setErr(new PrintStream(new OutputStream() {
            public void write(int b) {
            }
        }));
        classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
        /* setting System.err stream back to its original state afterwards */
        System.setErr(err);
    }

    public LinkedHashMap<String, LinkedHashSet<String>> identifyNER(String text) {
        LinkedHashMap<String, LinkedHashSet<String>> map = new <String, LinkedHashSet<String>>LinkedHashMap();
        List<List<CoreLabel>> classify = classifier.classify(text);
        String lastTaggedCategory = null;
        String lastTaggedWord = null;
        for (List<CoreLabel> coreLabels : classify) {
            for (CoreLabel coreLabel : coreLabels) {
                String word = coreLabel.word();
                String category = coreLabel.get(CoreAnnotations.AnswerAnnotation.class);
                if (!"O".equals(category)) {
                    if (lastTaggedCategory != null && lastTaggedCategory.equals(category)) {
                        map.get(category).remove(lastTaggedWord);
                        word = lastTaggedWord + " " + word;
                    }
                    if (map.containsKey(category)) {
                        map.get(category).add(word);
                    } else {
                        LinkedHashSet<String> temp = new LinkedHashSet<>();
                        temp.add(word);
                        map.put(category, temp);
                    }
                    lastTaggedWord = word;
                    lastTaggedCategory = category;
                }
            }
        }
        return map;
    }
}
