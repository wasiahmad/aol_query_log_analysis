/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.cs.main;

import edu.virginia.cs.model.GenerateCoverQuery;
import edu.virginia.cs.model.LanguageModel;
import edu.virginia.cs.model.LoadLanguageModel;
import edu.virginia.cs.model.UserSession;
import edu.virginia.cs.utility.StringTokenizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wua4nw
 */
public class QueryCoverQueryTopicAnalysis {

    private GenerateCoverQuery gCoverQuery;
    private FileWriter fw;

    private void AnalyzeQueryLog(ArrayList<String> userQueries, String userId) throws IOException {
        LinkedHashSet<String> uqeries = new LinkedHashSet<>(userQueries);
        fw.write("****************************************************" + "\n");
        fw.write("User Id: " + userId + "\n");
        fw.write("Total queries: " + uqeries.size() + "\n");
        Date lastQuerySubmitTime = null;
        String lastSubmittedQuery = null;
        UserSession session = new UserSession();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int queryId = 1;
        for (String query : uqeries) {
            String queryText = query.split("\t")[0];
            String queryTime = query.split("\t")[1];
            Date curretnQTime = null;
            try {
                curretnQTime = formatter.parse(queryTime);
            } catch (ParseException ex) {
                Logger.getLogger(QueryCoverQueryTopicAnalysis.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (lastQuerySubmitTime != null && lastSubmittedQuery != null) {
                boolean isSame = checkSameSession(lastQuerySubmitTime, curretnQTime, lastSubmittedQuery, queryText);
                if (!isSame) {
                    // start of a new user session
                    session = new UserSession();
                }
            }
            ArrayList<String> coverQueries = null;
            int retVal = session.isRepeatedQuery(queryText);
            if (retVal == -1) {
                coverQueries = gCoverQuery.generateNQueries(query, 2, session.getLastQueryTopicNo(), session.getCoverQuTopicsOfLastQuery());
            } else {
                /* User has repeated a query in the same session */
                fw.write("Reapeated query in the same search session.\n");
                continue;
            }
            if (coverQueries != null) {
                ArrayList<String> topics = gCoverQuery.getCoverQueryTopics(null);
                String output = "";
                for (int i = 0; i < coverQueries.size(); i++) {
                    output += "Cover Query#" + (i + 1) + " : " + coverQueries.get(i) + " [" + topics.get(i) + "]";
                    if (i < coverQueries.size() - 1) {
                        output += ", ";
                    }
                }
                fw.write(output + "\n");
                fw.flush();
            } else {
                fw.write("Cover Queries: [NO COVER QUERIES GENERATED]" + "\n");
            }
            session.addUserQuery(queryText, queryId, gCoverQuery.getLastQueryTopicNo());
            session.setCoverQueries(queryId, coverQueries);
            session.setCoverQueryTopics(queryId, gCoverQuery.getCoverQueryTopics());

            lastQuerySubmitTime = curretnQTime;
            lastSubmittedQuery = queryText;
            queryId++;
        }

    }

    /**
     *
     * @param earlierDate
     * @param laterDate
     * @param lastSubmittedQuery
     * @param currentQuery
     * @return
     */
    private boolean checkSameSession(Date previousDate, Date currentDate, String lastSubmittedQuery, String currentQuery) {
        /**
         * Measuring the time difference between current query and last
         * submitted query in minutes.
         */
        long diffMinutes = -1;
        try {
            //in milliseconds
            long diff = currentDate.getTime() - previousDate.getTime();
            diffMinutes = diff / (60 * 1000);
        } catch (Exception ex) {
            Logger.getLogger(QueryCoverQueryTopicAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }
        /**
         * Measuring the similarity between current query and last submitted
         * query in terms of similar tokens they have.
         */
        StringTokenizer st = new StringTokenizer(true, true);
        HashSet<String> currentQuTokens = new HashSet<>(st.TokenizeString(currentQuery));
        HashSet<String> prevQuTokens = new HashSet<>(st.TokenizeString(lastSubmittedQuery));
        boolean isSimilar = false;
        double count = Math.ceil(currentQuTokens.size() / 2.0);
        HashSet<String> intersection = new HashSet<>(currentQuTokens);
        intersection.retainAll(prevQuTokens);
        if (intersection.size() >= count) {
            isSimilar = true;
        }
        /**
         * If time difference is less than 60 minutes between current query and
         * previous query or if both queries have 50% similarity in terms of
         * having similar tokens, they belong to the same session .
         */
        if (diffMinutes < 60 || isSimilar) {
            return true;
        } else {
            return false;
        }
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
                    String query = line.trim();
                    userQueries.add(query);
                }
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(QueryCoverQueryTopicAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userQueries;
    }

    public void StartAnalysis() throws Throwable {
        System.out.println("Topic testing started...");
        fw = new FileWriter(new File("./results.txt"));
        System.out.println("Loading language models...");
        LoadLanguageModel llm = new LoadLanguageModel(new HashMap<>());
        llm.loadModels("data/language_models/", 3);
        System.out.println("Loading completed...");
        ArrayList<LanguageModel> list = llm.getLanguageModels();
        gCoverQuery = new GenerateCoverQuery(list, fw);
        LoadDirectory("data/user_search_logs/");
        fw.write("****************************************************" + "\n");
        fw.close();
    }

    public static void main(String[] args) {
        try {
            new QueryCoverQueryTopicAnalysis().StartAnalysis();
        } catch (Throwable ex) {
            Logger.getLogger(QueryCoverQueryTopicAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
