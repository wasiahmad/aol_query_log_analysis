/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.cs.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Wasi
 */
public class UserSession {

    private final ArrayList<Query> allQueriesInSession;
    private final HashMap<Integer, ArrayList<String>> originalQueryToCoverQuery;
    private final HashMap<Integer, ArrayList<Integer>> coverQueryTopics;

    public UserSession() {
        allQueriesInSession = new ArrayList<>();
        originalQueryToCoverQuery = new HashMap<>();
        coverQueryTopics = new HashMap<>();
    }

    /**
     *
     * @param query
     * @param queryId
     * @param queryTopicNo
     */
    public void addUserQuery(String query, int queryId, int queryTopicNo) {
        Query userQuery = new Query(queryId, query, queryTopicNo);
        allQueriesInSession.add(userQuery);
    }

    /**
     *
     * @param origQueryId
     * @param coverQueries
     */
    public void setCoverQueries(int origQueryId, ArrayList<String> coverQueries) {
        originalQueryToCoverQuery.put(origQueryId, coverQueries);
    }

    /**
     *
     * @param origQueryId
     * @param cQuTopics
     */
    public void setCoverQueryTopics(int origQueryId, ArrayList<Integer> cQuTopics) {
        coverQueryTopics.put(origQueryId, cQuTopics);
    }

    /**
     * Checks whether a query is repeated in the same user session.
     *
     * @param query
     * @return
     */
    public int isRepeatedQuery(String query) {
        for (Query userQuery : allQueriesInSession) {
            if (userQuery.getQuery().equals(query)) {
                return userQuery.getQueryId();
            }
        }
        return -1;
    }

    /**
     *
     * @return
     */
    public int getLastQueryTopicNo() {
        if (allQueriesInSession.isEmpty()) {
            return -1;
        }
        return allQueriesInSession.get(allQueriesInSession.size() - 1).getQueryTopicNo();
    }

    /**
     *
     * @return
     */
    public ArrayList<Integer> getCoverQuTopicsOfLastQuery() {
        if (allQueriesInSession.isEmpty()) {
            return null;
        } else {
            int id = allQueriesInSession.get(allQueriesInSession.size() - 1).getQueryId();
            return coverQueryTopics.get(id);
        }
    }

    /**
     * Return all cover queries which were generated previously for a user
     * query.
     *
     * @param queryId
     * @return
     */
    public ArrayList<String> getCoverQueries(int queryId) {
        return originalQueryToCoverQuery.get(queryId);
    }
}

class Query {

    private final int queryId;
    private final String query;
    private final int queryTopicNo;

    public Query(int queryId, String query, int queryTopicNo) {
        this.queryId = queryId;
        this.query = query;
        this.queryTopicNo = queryTopicNo;
    }

    public int getQueryId() {
        return queryId;
    }

    public String getQuery() {
        return query;
    }

    public int getQueryTopicNo() {
        return queryTopicNo;
    }
}
