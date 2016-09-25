package edu.virginia.cs.main;

import edu.virginia.cs.index.OkapiBM25;
import edu.virginia.cs.index.ResultDoc;
import edu.virginia.cs.index.Searcher;
import edu.virginia.cs.utility.MapUtilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Evaluate {

    private final static String INDEX_PATH = "lucene-DMOZ-index";
    private static Searcher SEARCHER = null;
    private FileWriter fw;

    public Evaluate() {
        try {
            fw = new FileWriter("500test-result.txt");
        } catch (IOException ex) {
            Logger.getLogger(Evaluate.class.getName()).log(Level.SEVERE, null, ex);
        }
        SEARCHER = new Searcher(INDEX_PATH);
        SEARCHER.setSimilarity(new OkapiBM25());
    }

    public void StartAnalysis(String method) throws Throwable {
        AnalyzeQueryLog(LoadFile("top 500 queries.txt"));
    }

    public static void main(String[] args) throws IOException {
        String method = "--ok";
        try {
            new Evaluate().StartAnalysis(method);
        } catch (Throwable ex) {
            Logger.getLogger(Evaluate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void AnalyzeQueryLog(ArrayList<String> userQueries) throws IOException {
        for (String query : userQueries) {
            ArrayList<ResultDoc> results = SEARCHER.search(query).getDocs();
            if (results.isEmpty()) {
                fw.write("Query: " + query + "\n");
                fw.write("No Search Results Found!!" + "\n");
                fw.flush();
                continue;
            }
            HashMap<String, Double> topicMap = new HashMap<>();
            int rank = 1;
            for (ResultDoc rdoc : results) {
                double score = ((int) ((1.0 / rank) * 100)) / 100.0;
                if (topicMap.containsKey(rdoc.topic())) {
                    score += topicMap.get(rdoc.topic());
                    score = ((int) (score * 100)) / 100.0;
                    topicMap.put(rdoc.topic(), score);
                } else {
                    topicMap.put(rdoc.topic(), score);
                }
                rank++;
            }
            fw.write("Query: " + query + "\n");
            int i = 1;
            for (Map.Entry<String, Double> entry : MapUtilities.sortMapByValue(topicMap, false, 5).entrySet()) {
                fw.write("Topic# " + i + " - " + entry.getKey() + "[" + entry.getValue() + "]" + "\n");
                i++;
            }
            fw.flush();
        }
        fw.close();
    }

    private ArrayList<String> LoadFile(String file) {
        ArrayList<String> userQueries = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
            String line;
            while ((line = reader.readLine()) != null) {
                userQueries.add(line);
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(Evaluate.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userQueries;
    }

}
