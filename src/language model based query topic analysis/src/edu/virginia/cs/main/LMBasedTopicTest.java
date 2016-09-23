/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.cs.main;

import edu.virginia.cs.model.GenerateCoverQuery;
import edu.virginia.cs.model.LanguageModel;
import edu.virginia.cs.model.LoadLanguageModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wua4nw
 */
public class LMBasedTopicTest {

    private FileWriter fw;
    private final GenerateCoverQuery gCoverQuery;

    public LMBasedTopicTest() {
        try {
            fw = new FileWriter("500test-result.txt");
        } catch (IOException ex) {
            Logger.getLogger(LMBasedTopicTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        LoadLanguageModel llm = new LoadLanguageModel(new HashMap<>());
        llm.loadModels("data/language_models/", 3);
        System.out.println("Loading completed...");
        ArrayList<LanguageModel> list = llm.getLanguageModels();
        gCoverQuery = new GenerateCoverQuery(list, fw);
    }

    private void AnalyzeQueryLog(ArrayList<String> userQueries) throws IOException {
        for (String query : userQueries) {
            gCoverQuery.generateNQueries(query, 0, -1, null);
        }
    }

    private ArrayList<String> LoadFile(String file) {
        ArrayList<String> userQueries = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line + "\t" + "0000-00-00 00:00:00";
                userQueries.add(line);
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(LMBasedTopicTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userQueries;
    }

    public void StartAnalysis() throws Throwable {
        AnalyzeQueryLog(LoadFile("top 500 queries.txt"));
    }

    public static void main(String[] args) {
        try {
            new LMBasedTopicTest().StartAnalysis();
        } catch (Throwable ex) {
            Logger.getLogger(LMBasedTopicTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
