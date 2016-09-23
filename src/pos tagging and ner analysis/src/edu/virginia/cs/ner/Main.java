/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.cs.ner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wasi
 */
public class Main {

    private NERTagger tagger;
    private int totalQueries = 0;
    private int totalPerson = 0;
    private int totalLocation = 0;
    private int totalOrganization = 0;

    private String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    private String CapitalizeWords(String text) {
        String retValue = "";
        String[] words = text.split("\\s+");
        for (String word : words) {
            retValue += capitalizeFirstLetter(word) + " ";
        }
        return retValue.trim();
    }

    private void AnalyzeQueryLog(ArrayList<String> userQueries, String userId) {
        int locationMentioned = 0;
        int personMentioned = 0;
        int organizationMentioned = 0;
        HashSet<String> uqeries = new HashSet<>(userQueries);
        HashSet<String> personNames = new HashSet<>();
        HashSet<String> locations = new HashSet<>();
        HashSet<String> organizations = new HashSet<>();
        for (String query : uqeries) {
            query = CapitalizeWords(query);
            for (Map.Entry<String, LinkedHashSet<String>> entry : tagger.identifyNER(query).entrySet()) {
                if (entry.getKey().equals("PERSON")) {
                    personMentioned++;
                    personNames.addAll(entry.getValue());
                } else if (entry.getKey().equals("ORGANIZATION")) {
                    organizationMentioned++;
                    organizations.addAll(entry.getValue());
                } else if (entry.getKey().equals("LOCATION")) {
                    locationMentioned++;
                    locations.addAll(entry.getValue());
                }
            }
        }
        System.out.println("****************************************************");
        System.out.println("User Id: " + userId);
        System.out.println("Number of unique queries: " + uqeries.size());
        System.out.println("Entities found in #queries - [Person]:" + personMentioned
                + ", [Organization]:" + organizationMentioned + ", [Location]:" + locationMentioned);
        System.out.println("----------------------------------------------------");
        System.out.println("Persons: " + personNames.toString());
        System.out.println("----------------------------------------------------");
        System.out.println("Locations: " + locations.toString());
        System.out.println("----------------------------------------------------");
        System.out.println("Organizations: " + organizations.toString());
        System.out.printf("%-8d\t%-8d\t%-8d\t%-8d\t%-8d\n", Integer.parseInt(userId), uqeries.size(), personMentioned, locationMentioned, organizationMentioned);
        totalQueries += uqeries.size();
        totalPerson += personMentioned;
        totalLocation += locationMentioned;
        totalOrganization += organizationMentioned;
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
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userQueries;
    }

    public void StartAnalysis() throws Throwable {
        tagger = new NERTagger("model/english.all.3class.distsim.crf.ser.gz");
        LoadDirectory("user_search_logs/");
        System.out.println("****************************************************");
        System.out.println("Total Queries = " + totalQueries);
        System.out.println("Total Persons = " + totalPerson);
        System.out.println("Total Locations = " + totalLocation);
        System.out.println("Total Organizations = " + totalOrganization);
    }

    public static void main(String[] args) {
        try {
            new Main().StartAnalysis();
        } catch (Throwable ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
