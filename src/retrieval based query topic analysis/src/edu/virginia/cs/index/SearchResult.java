package edu.virginia.cs.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Represents results returned by Lucene. Includes the list of search result
 * objects, the original query, and the highlighted snippets.
 */
public class SearchResult {

    private ArrayList<ResultDoc> results;
    private final int totalHits;
    private final SearchQuery searchQuery;
    private final HashMap<Integer, String> htmlSnippets; // map id to highlighted
    // string

    /**
     * Default constructor to represent an empty search result.
     *
     * @param searchQuery
     */
    public SearchResult(SearchQuery searchQuery) {
        totalHits = 0;
        results = new ArrayList<>();
        this.searchQuery = searchQuery;
        htmlSnippets = new HashMap<>();
    }

    /**
     * Constructor.
     *
     * @param searchQuery
     * @param totalHits
     */
    public SearchResult(SearchQuery searchQuery, int totalHits) {
        this.results = new ArrayList<>();
        this.totalHits = totalHits;
        this.searchQuery = searchQuery;
        htmlSnippets = new HashMap<>();
    }

    /**
     * Adds a search result to this object.
     *
     * @param rdoc
     */
    public void addResult(ResultDoc rdoc) {
        results.add(rdoc);
    }

    /**
     * Set the highlighted text for this document.
     *
     * @param rdoc
     * @param snippet
     */
    public void setSnippet(ResultDoc rdoc, String snippet) {
        htmlSnippets.put(rdoc.id(), snippet);
    }

    /**
     * @param rdoc
     * @return the snippets for the given document
     */
    public String getSnippet(ResultDoc rdoc) {
        return htmlSnippets.get(rdoc.id());
    }

    /**
     * @return the query that returned this result
     */
    public SearchQuery query() {
        return searchQuery;
    }

    /**
     * @return an ArrayList of ResultDocs matching the query
     */
    public ArrayList<ResultDoc> getDocs() {
        return results;
    }

    /**
     * @return the total number of hits from the query
     */
    public int numHits() {
        return totalHits;
    }

    /**
     * Temporarily used to create paginated results.
     *
     * @param from
     */
    public void trimResults(int from) {
        // bounds checking
        if (from >= results.size()) {
            results = new ArrayList<>();
            return;
        }

        int to = results.size();

        // trimming
        List<ResultDoc> newResults = results.subList(from, to);
        results = new ArrayList<>(newResults);
    }

    /**
     * Tells whether two objects are both SearchQueries with equal contents.
     *
     * @param other
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SearchResult)) {
            return false;
        }

        SearchResult otherResult = (SearchResult) other;
        return otherResult.searchQuery.equals(searchQuery)
                && otherResult.results == results
                && otherResult.totalHits == totalHits;

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.results);
        hash = 79 * hash + this.totalHits;
        hash = 79 * hash + Objects.hashCode(this.searchQuery);
        return hash;
    }
}
