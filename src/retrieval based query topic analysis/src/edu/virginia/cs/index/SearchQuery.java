package edu.virginia.cs.index;

import java.util.ArrayList;

public class SearchQuery {

    private ArrayList<String> fields;
    private String queryText;
    private int numResults;
    private int from;
    private final static int DEFAULT_NUM_RESULTS = 100;
    private final static String DEFAULT_FIELD = "content";

    public SearchQuery queryText(String queryText) {
        this.queryText = queryText;
        return this;
    }

    public SearchQuery fields(ArrayList<String> fields) {
        this.fields = new ArrayList<>(fields);
        return this;
    }

    public ArrayList<String> fields() {
        return fields;
    }

    public String queryText() {
        return queryText;
    }

    public SearchQuery fields(String field) {
        fields = new ArrayList<>();
        fields.add(field);
        return this;
    }

    public int numResults() {
        return numResults;
    }

    public SearchQuery numResults(int numResults) {
        this.numResults = numResults;
        return this;
    }

    public int fromDoc() {
        return from;
    }

    public SearchQuery fromDoc(int fromDoc) {
        this.from = fromDoc;
        return this;
    }

    public SearchQuery(String queryText, ArrayList<String> fields) {
        this.queryText = queryText;
        this.numResults = DEFAULT_NUM_RESULTS;
        this.fields = fields;
        from = 0;
    }

    public SearchQuery() {
        this.queryText = null;
        this.numResults = DEFAULT_NUM_RESULTS;
        this.fields = new ArrayList<>();
        fields.add(DEFAULT_FIELD);
        from = 0;
    }

    public SearchQuery(String queryText, String field) {
        this.queryText = queryText;
        this.numResults = DEFAULT_NUM_RESULTS;
        fields = new ArrayList<>();
        fields.add(field);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SearchQuery)) {
            return false;
        }

        SearchQuery otherQuery = (SearchQuery) other;
        return otherQuery.queryText.equals(queryText)
                && otherQuery.fields == fields
                && otherQuery.numResults == numResults
                && otherQuery.from == from;
    }
}
