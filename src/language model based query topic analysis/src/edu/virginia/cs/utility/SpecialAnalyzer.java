package edu.virginia.cs.utility;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

/**
 * Analyzer performs the following functions: - make all tokens lowercase -
 * remove stop words based on standard list - have max token length of 35
 * characters - run porter stemmer on each token
 */
public class SpecialAnalyzer extends Analyzer {

    private final boolean removeStopWords;
    private final boolean doStemming;

    public SpecialAnalyzer(boolean stopWordRemoval, boolean stemming) {
        removeStopWords = stopWordRemoval;
        doStemming = stemming;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName,
            Reader reader) {
        Tokenizer source = new StandardTokenizer(Version.LUCENE_46, reader);
        TokenStream filter = new StandardFilter(Version.LUCENE_46, source);
        filter = new LowerCaseFilter(Version.LUCENE_46, filter);
        filter = new LengthFilter(Version.LUCENE_46, filter, 2, 35);
        if (doStemming) {
            filter = new PorterStemFilter(filter);
        }
        if (removeStopWords) {
            filter = new StopFilter(Version.LUCENE_46, filter,
                    StopFilter.makeStopSet(Version.LUCENE_46, Stopwords.STOPWORDS));
        }
        return new TokenStreamComponents(source, filter);
    }
}
