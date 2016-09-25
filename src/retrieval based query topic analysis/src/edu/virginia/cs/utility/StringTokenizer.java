/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.cs.utility;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 *
 * @author Wasi
 */
public class StringTokenizer {

    private final SpecialAnalyzer analyzer;

    public StringTokenizer(boolean removeStopWords, boolean doStemming) {
        analyzer = new SpecialAnalyzer(removeStopWords, doStemming);
    }

    /**
     * Method that generates list of tokens from the parameter string.
     *
     * @param string
     * @return list of tokens generated
     */
    public List<String> TokenizeString(String string) {
        List<String> result = new ArrayList<>();
        try {
            TokenStream stream = analyzer.tokenStream(null, new StringReader(string));
            stream.reset();
            while (stream.incrementToken()) {
                result.add(stream.getAttribute(CharTermAttribute.class
                ).toString());
            }
            stream.end();
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
