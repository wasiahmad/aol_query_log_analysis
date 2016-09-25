package edu.virginia.cs.index;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class OkapiBM25 extends SimilarityBase {

    double k1 = 1.2; // range 1.2 - 2
    double k2 = 750; // range 0 - 1000
    double b = 0.75; // range 0.75 - 1.2

    /**
     * Returns a score for a single term in the document.
     *
     * @param stats Provides access to corpus-level statistics
     * @param termFreq
     * @param docLength
     */
    @Override
    protected float score(BasicStats stats, float termFreq, float docLength) {
        float relevance;
        float part1 = (float) (Math.log((stats.getNumberOfDocuments() - stats.getDocFreq() + 0.5) / (stats.getDocFreq() + 0.5)) / Math.log(Math.E));
        float part2 = (float) (((k1 + 1) * termFreq) / ((k1 * (1 - b + b * (docLength / stats.getAvgFieldLength()))) + termFreq));
        float part3 = (float) (((k2 + 1) * 1) / (k2 + 1));
        relevance = part1 * part2 * part3;
        return relevance;
    }

    @Override
    public String toString() {
        return "Okapi BM25";
    }

}
