/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.cs.postagging;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Wasi
 */
public class POSTagger {

    private final MaxentTagger tagger;

    public POSTagger() {
        /* making all writes to the System.err stream silent */
        PrintStream err = System.err;
        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
            }
        }));
        tagger = new MaxentTagger("model/english-bidirectional-distsim.tagger");
        /* setting System.err stream back to its original state afterwards */
        System.setErr(err);

    }

    public HashMap<String, List<String>> GetTaggedWords(String query, Set<String> tags) {
        HashMap<String, List<String>> taggedWords = new HashMap<>();
        String taggedQuery = tagger.tagString(query);
        for (String tag : tags) {
            List<String> words = new ArrayList<>();
            if (tag.equals("Noun")) {
                if (taggedQuery.contains("_NN")) {
                    String[] split = taggedQuery.split("\\s+");
                    for (String str : split) {
                        if (str.contains("_NN")) {
                            words.add(str.split("_NN")[0]);
                        }
                    }
                }
                taggedWords.put("Noun", words);
            } else if (tag.equals("Verb")) {
                if (taggedQuery.contains("_VB")) {
                    String[] split = taggedQuery.split("\\s+");
                    for (String str : split) {
                        if (str.contains("_VB")) {
                            words.add(str.split("_VB")[0]);
                        }
                    }
                }
                taggedWords.put("Verb", words);
            } else if (tag.equals("Adjective")) {
                if (taggedQuery.contains("_JJ")) {
                    String[] split = taggedQuery.split("\\s+");
                    for (String str : split) {
                        if (str.contains("_JJ")) {
                            words.add(str.split("_JJ")[0]);
                        }
                    }
                }
                taggedWords.put("Adjective", words);
            } else if (tag.equals("Adverb")) {
                if (taggedQuery.contains("_RB")) {
                    String[] split = taggedQuery.split("\\s+");
                    for (String str : split) {
                        if (str.contains("_RB")) {
                            words.add(str.split("_RB")[0]);
                        }
                    }
                }
                taggedWords.put("Adverb", words);
            }
        }
        return taggedWords;
    }
}
