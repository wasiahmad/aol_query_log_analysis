/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.cs.postagging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wasi
 */
public class Main {

    private POSTagger tagger = null;
    private Set<String> tags = null;

    private void AnalyzeQueryLog(ArrayList<String> userQueries, String userId) {
        HashSet<String> uqeries = new HashSet<>(userQueries);
        HashSet<String> nouns = new HashSet<>();
        HashSet<String> verbs = new HashSet<>();
        HashSet<String> adjectives = new HashSet<>();
        HashSet<String> adverbs = new HashSet<>();
        for (String query : uqeries) {
            for (Map.Entry<String, List<String>> entry : tagger.GetTaggedWords(query, tags).entrySet()) {
                if (entry.getKey().equals("Noun")) {
                    nouns.addAll(entry.getValue());
                } else if (entry.getKey().equals("Verb")) {
                    verbs.addAll(entry.getValue());
                } else if (entry.getKey().equals("Adjective")) {
                    adjectives.addAll(entry.getValue());
                } else if (entry.getKey().equals("Adverb")) {
                    adverbs.addAll(entry.getValue());
                }
            }
        }
        System.out.println("****************************************************");
        System.out.println("User Id: " + userId);
        System.out.println("Number of unique queries: " + uqeries.size());
        System.out.println("Total POSTags found - [Noun]:" + nouns.size() + ", [Verb]:"
                + verbs.size() + ", [Adjective]:" + adjectives.size() + ", [Adverb]:" + adverbs.size());
        System.out.println("----------------------------------------------------");
        System.out.println("Nouns: " + nouns.toString());
        System.out.println("----------------------------------------------------");
        System.out.println("Verbs: " + verbs.toString());
        System.out.println("----------------------------------------------------");
        System.out.println("Adjectives: " + adjectives.toString());
        System.out.println("----------------------------------------------------");
        System.out.println("Adverbs: " + adverbs.toString());
        System.out.println("----------------------------------------------------");
    }

    private void LoadDirectory(String folder) throws Throwable {
        int numberOfDocumentsLoaded = 0;
        File dir = new File(folder);
        for (File f : dir.listFiles()) {
            if (f.isFile()) {
                numberOfDocumentsLoaded++;
                String userId = f.getName().split("\\.")[0];
                AnalyzeQueryLog(LoadFile(f.getAbsolutePath()), userId);
            } else if (f.isDirectory()) {
                LoadDirectory(f.getAbsolutePath());
            }
        }
        System.out.println("Loading " + numberOfDocumentsLoaded + " documents from " + folder);
    }

    private ArrayList<String> LoadFile(String file) {
        ArrayList<String> userQueries = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
            String line;
            boolean isQuery = true;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    isQuery = true;
                    continue;
                }
                if (isQuery) {
                    isQuery = false;
                    String query = line.trim().split("\t")[0];
                    userQueries.add(query);
                }
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(edu.virginia.cs.ner.Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userQueries;
    }

    public void StartAnalysis() throws Throwable {
        tagger = new POSTagger();
        tags = new HashSet<>(Arrays.asList("Noun", "Verb", "Adjective", "Adverb"));
        LoadDirectory("user_search_logs/");
    }

    public static void main(String[] args) {
        try {
            new edu.virginia.cs.postagging.Main().StartAnalysis();
        } catch (Throwable ex) {
            Logger.getLogger(edu.virginia.cs.postagging.Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
