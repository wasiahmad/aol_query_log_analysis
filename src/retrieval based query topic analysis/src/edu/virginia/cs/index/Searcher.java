package edu.virginia.cs.index;

import edu.virginia.cs.utility.SpecialAnalyzer;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {

    private IndexSearcher indexSearcher;
    private SpecialAnalyzer analyzer;
    private static SimpleHTMLFormatter formatter;
    private static final int NUM_FRAGMENTS = 4;
    private static final String DEFAULT_FIELD = "content";

    /**
     * Sets up the Lucene index Searcher with the specified index.
     *
     * @param indexPath The path to the desired Lucene index.
     */
    public Searcher(String indexPath) {
        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
            indexSearcher = new IndexSearcher(reader);
            analyzer = new SpecialAnalyzer();
            formatter = new SimpleHTMLFormatter("****", "****");
        } catch (IOException ex) {
            Logger.getLogger(Searcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setSimilarity(Similarity sim) {
        indexSearcher.setSimilarity(sim);
    }

    /**
     * The main search function.
     *
     * @param searchQuery Set this object's attributes as needed.
     * @return
     */
    public SearchResult search(SearchQuery searchQuery) {
        BooleanQuery combinedQuery = new BooleanQuery();
        for (String field : searchQuery.fields()) {
            QueryParser parser = new QueryParser(Version.LUCENE_46, field, analyzer);
            try {
                Query textQuery = parser.parse(searchQuery.queryText());
                combinedQuery.add(textQuery, BooleanClause.Occur.MUST);
            } catch (ParseException ex) {
                Logger.getLogger(Searcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return runSearch(combinedQuery, searchQuery);
    }

    /**
     * The simplest search function. Searches the abstract field and returns a
     * the default number of results.
     *
     * @param queryText The text to search
     * @return the SearchResult
     */
    public SearchResult search(String queryText) {
        return search(new SearchQuery(queryText, DEFAULT_FIELD));
    }

    /**
     * Performs the actual Lucene search.
     *
     * @param luceneQuery
     * @param numResults
     * @return the SearchResult
     */
    private SearchResult runSearch(Query luceneQuery, SearchQuery searchQuery) {
        try {
            TopDocs docs = indexSearcher.search(luceneQuery, searchQuery.fromDoc() + searchQuery.numResults());
            ScoreDoc[] hits = docs.scoreDocs;
            String field = searchQuery.fields().get(0);

            SearchResult searchResult = new SearchResult(searchQuery, docs.totalHits);
            for (ScoreDoc hit : hits) {
                Document doc = indexSearcher.doc(hit.doc);
                ResultDoc rdoc = new ResultDoc(hit.doc);

                String highlighted;
                try {
                    Highlighter highlighter = new Highlighter(formatter, new QueryScorer(luceneQuery));
                    String contents = doc.getField(field).stringValue();
                    rdoc.content(contents);
                    rdoc.url(doc.getField("url").stringValue());
                    rdoc.topic(doc.getField("topic").stringValue());
                    String[] snippets = highlighter.getBestFragments(analyzer, field, contents, NUM_FRAGMENTS);
                    highlighted = createOneSnippet(snippets);
                } catch (InvalidTokenOffsetsException ex) {
                    Logger.getLogger(Searcher.class.getName()).log(Level.SEVERE, null, ex);
                    highlighted = "(no snippets yet)";
                }

                searchResult.addResult(rdoc);
                searchResult.setSnippet(rdoc, highlighted);
            }

            searchResult.trimResults(searchQuery.fromDoc());
            return searchResult;

        } catch (IOException ex) {
            Logger.getLogger(Searcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new SearchResult(searchQuery);
    }

    /**
     * Create one string of all the extracted snippets from the highlighter
     *
     * @param snippets
     * @return
     */
    private String createOneSnippet(String[] snippets) {
        String result = " ... ";
        for (String s : snippets) {
            result += s + " ... ";
        }
        return result;
    }
}
