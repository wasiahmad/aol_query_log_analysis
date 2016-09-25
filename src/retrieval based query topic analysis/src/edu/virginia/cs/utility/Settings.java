/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.cs.utility;

/**
 *
 * @author Wasi
 */
public class Settings {

    private String LuceneIndexPath;
    private String UserSearchLogPath;
    private String LanguageModelPath;
    private String ReferenceModelPath;
    private String AOLDictionaryPath;
    private String termIndexPath;
    private int NumberOfCoverQuery;
    private int NumberOfThreads;
    private boolean ClientSideRanking;

    public String getLuceneIndexPath() {
        return LuceneIndexPath;
    }

    public void setLuceneIndexPath(String LuceneIndexPath) {
        this.LuceneIndexPath = LuceneIndexPath;
    }

    public String getUserSearchLogPath() {
        return UserSearchLogPath;
    }

    public void setUserSearchLogPath(String UserSearchLogPath) {
        this.UserSearchLogPath = UserSearchLogPath;
    }

    public String getLanguageModelPath() {
        return LanguageModelPath;
    }

    public void setLanguageModelPath(String LanguageModelPath) {
        this.LanguageModelPath = LanguageModelPath;
    }

    public String getReferenceModelPath() {
        return ReferenceModelPath;
    }

    public void setReferenceModelPath(String ReferenceModelPath) {
        this.ReferenceModelPath = ReferenceModelPath;
    }

    public String getAOLDictionaryPath() {
        return AOLDictionaryPath;
    }

    public void setAOLDictionaryPath(String AOLDictionaryPath) {
        this.AOLDictionaryPath = AOLDictionaryPath;
    }

    public String getTermIndexPath() {
        return termIndexPath;
    }

    public void setTermIndexPath(String termIndexPath) {
        this.termIndexPath = termIndexPath;
    }

    public int getNumberOfCoverQuery() {
        return NumberOfCoverQuery;
    }

    public void setNumberOfCoverQuery(int NumberOfCoverQuery) {
        this.NumberOfCoverQuery = NumberOfCoverQuery;
    }

    public int getNumberOfThreads() {
        return NumberOfThreads;
    }

    public void setNumberOfThreads(int NumberOfThreads) {
        this.NumberOfThreads = NumberOfThreads;
    }

    public boolean isClientSideRanking() {
        return ClientSideRanking;
    }

    public void setClientSideRanking(boolean ClientSideRanking) {
        this.ClientSideRanking = ClientSideRanking;
    }

}
