/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.cs.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.virginia.cs.utility.StringTokenizer;
import java.util.Objects;

/**
 *
 * @author Wasi
 */
public class GenerateCoverQuery {

    private final int totalNodesInTopicHierarchy;
    private final ArrayList<LanguageModel> languageModels;
    private ArrayList<Integer> coverQueryTopics;
    private int currentQueryTopicNo;
    private final StringTokenizer tokenizer;

    private final FileWriter fw;

    public GenerateCoverQuery(ArrayList<LanguageModel> list, FileWriter writer) {
        languageModels = list;
        totalNodesInTopicHierarchy = list.size();
        /* No stopword removal and no stemming during inference and generation step */
        tokenizer = new StringTokenizer(false, true);
        fw = writer;
    }

    /**
     * Generate random number using poisson distribution.
     *
     * @param lambda average query length
     * @return
     */
    private int getPoisson(double lambda) {
        int n = 1;
        double prob = 1.0;
        Random r = new Random();

        while (true) {
            prob *= r.nextDouble();
            if (prob < Math.exp(-lambda)) {
                break;
            }
            n += 1;
        }
        return n - 1;
    }

    /**
     * Generate a random number within a range.
     *
     * @param start
     * @param end
     * @return
     */
    private int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    /**
     * Computes and return a cover query topic.
     *
     * @param level
     * @param queryTopic
     * @param fromSibling
     * @return the cover query topic
     */
    private LanguageModel getCoverQueryTopic(int level, int queryTopic, boolean fromSibling) {
        ArrayList<LanguageModel> listModels = new ArrayList<>();
        if (fromSibling) {
            /**
             * Selecting a cover query topic only from sibling topics.
             */
            int topicId = languageModels.get(queryTopic).getTopic_id();
            for (LanguageModel lm : languageModels.get(queryTopic).getParent().getListOfChildren()) {
                if (lm.getTopic_id() != topicId) {
                    listModels.add(lm);
                }
            }
        } else {
            /**
             * Selecting a cover query topic from same level but no from sibling
             * topics.
             */
            int parentTopicId = languageModels.get(queryTopic).getParent().getTopic_id();
            for (LanguageModel lm : languageModels) {
                if (lm.getLevel() == level && lm.getParent().getTopic_id() != parentTopicId) {
                    listModels.add(lm);
                }
            }
        }
        if (listModels.isEmpty()) {
            return null;
        }
        int topic = getRandom(0, listModels.size());
        return listModels.get(topic);
    }

    /**
     * Generate cover query of length 1 using unigram language model.
     *
     * @param level
     * @param bucketNum
     * @param trueQuTopic
     * @param fromSibling
     * @param coverQuTopic
     * @return the cover query
     */
    private String getCQfromUnigramLM(int level, int bucketNum, int trueQuTopic, boolean fromSibling, int coverQuTopic) {
        LanguageModel lm;
        if (coverQuTopic == -1) {
            lm = getCoverQueryTopic(level, trueQuTopic, fromSibling);
            if (lm == null) {
                return "CQ topic not found!";
            }
        } else {
            lm = languageModels.get(coverQuTopic);
        }
        if (lm.isEmpty()) {
            return null;
        }
        ArrayList<String> possibleCoverQ = new ArrayList<>();
        Double[] probArray = lm.getProbArrayUnigram();
        for (String unigram : lm.getUnigramLM().keySet()) {
            /* P(d,t) = P(d|t)*P(t) */
            double prob = lm.getProbabilityUnigram(unigram);
            int bNum = getBucketNumber(prob, probArray);
            if (bNum == bucketNum) {
                possibleCoverQ.add(unigram);
            } else if (Objects.equals(probArray[bNum - 1], probArray[bucketNum - 1])) {
                possibleCoverQ.add(unigram);
            }
        }
        if (possibleCoverQ.size() > 0) {
            StringTokenizer st = new StringTokenizer(true, true);
            for (int i = 0; i < possibleCoverQ.size(); i++) {
                int coverQNum = getRandom(0, possibleCoverQ.size());
                String cQuery = possibleCoverQ.get(coverQNum);
                if (st.TokenizeString(cQuery).size() == 1) {
                    coverQueryTopics.add(lm.getTopic_id());
                    return cQuery;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    /**
     * Returns a unigram from a given language model and bucket number.
     *
     * @param lm
     * @param bucketNum
     * @return
     */
    private String getUniGramFromLM(LanguageModel lm, int bucketNum) {
        ArrayList<String> possibleCoverQ = new ArrayList<>();
        Double[] probArray = lm.getProbArrayUnigram();
        for (String unigram : lm.getUnigramLM().keySet()) {
            /* P(d,t) = P(d|t)*P(t) */
            double prob = lm.getProbabilityUnigram(unigram);
            int bNum = getBucketNumber(prob, probArray);
            if (bNum == bucketNum) {
                possibleCoverQ.add(unigram);
            } else if (Objects.equals(probArray[bNum - 1], probArray[bucketNum - 1])) {
                possibleCoverQ.add(unigram);
            }
        }
        if (possibleCoverQ.size() > 0) {
            StringTokenizer st = new StringTokenizer(true, true);
            for (int i = 0; i < possibleCoverQ.size(); i++) {
                int coverQNum = getRandom(0, possibleCoverQ.size());
                String cQuery = possibleCoverQ.get(coverQNum);
                if (st.TokenizeString(cQuery).size() == 1) {
                    return cQuery;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    /**
     * Generate cover query of length 2 using bigram language model.
     *
     * @param level
     * @param bucketNum
     * @param trueQuTopic
     * @param fromSibling
     * @param coverQuTopic
     * @return the cover query
     */
    private String getCQfromBigramLM(int level, int bucketNum, int trueQuTopic, boolean fromSibling, int coverQuTopic) {
        LanguageModel lm;
        if (coverQuTopic == -1) {
            lm = getCoverQueryTopic(level, trueQuTopic, fromSibling);
            if (lm == null) {
                return "CQ topic not found!";
            }
        } else {
            lm = languageModels.get(coverQuTopic);
        }
        if (lm.getBigramLM().isEmpty()) {
            return null;
        }
        ArrayList<String> possibleCoverQ = new ArrayList<>();
        Double[] probArray = lm.getProbArrayBigram();
        for (String bigram : lm.getBigramLM().keySet()) {
            /* P(d,t) = P(d|t)*P(t) */
            double prob = lm.getProbabilityBigram(bigram, true);
            int bNum = getBucketNumber(prob, probArray);
            if (bNum == bucketNum) {
                possibleCoverQ.add(bigram);
            } else if (Objects.equals(probArray[bNum - 1], probArray[bucketNum - 1])) {
                possibleCoverQ.add(bigram);
            }
        }
        if (possibleCoverQ.size() > 0) {
            StringTokenizer st = new StringTokenizer(true, true);
            for (int i = 0; i < possibleCoverQ.size(); i++) {
                int coverQNum = getRandom(0, possibleCoverQ.size());
                String cQuery = possibleCoverQ.get(coverQNum);
                if (st.TokenizeString(cQuery).size() == 2) {
                    coverQueryTopics.add(lm.getTopic_id());
                    return cQuery;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    /**
     * Returns a bigram from a given language model and bucket number. Returns a
     * bigram which contains the unigram if provided.
     *
     * @param lm
     * @param bucketNum
     * @param unigram
     * @return
     */
    private String getCQfromBigramLM(LanguageModel lm, int bucketNum, String unigram) {
        ArrayList<String> possibleCoverQ = new ArrayList<>();
        Double[] probArray = lm.getProbArrayBigram();
        if (unigram == null) {
            for (String bigram : lm.getBigramLM().keySet()) {
                double prob = lm.getProbabilityBigram(bigram, true);
                int bNum = getBucketNumber(prob, probArray);
                if (bNum == bucketNum) {
                    possibleCoverQ.add(bigram);
                } else if (Objects.equals(probArray[bNum - 1], probArray[bucketNum - 1])) {
                    possibleCoverQ.add(bigram);
                }
            }
        } else {
            for (String bigram : lm.getBigramLM().keySet()) {
                String prevGram = bigram.split(" ")[0];
                if (prevGram.equals(unigram)) {
                    possibleCoverQ.add(bigram);
                }
            }
        }
        if (possibleCoverQ.size() > 0) {
            StringTokenizer st = new StringTokenizer(true, true);
            for (int i = 0; i < possibleCoverQ.size(); i++) {
                int coverQNum = getRandom(0, possibleCoverQ.size());
                String cQuery = possibleCoverQ.get(coverQNum);
                if (st.TokenizeString(cQuery).size() == 2) {
                    if (unigram == null) {
                        coverQueryTopics.add(lm.getTopic_id());
                    }
                    return cQuery;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    /**
     * Generate cover query of length 3 using trigram language model.
     *
     * @param level
     * @param bucketNum
     * @param trueQuTopic
     * @param fromSibling
     * @param coverQuTopic
     * @return the cover query
     */
    private String getCQfromTrigramLM(int level, int bucketNum, int trueQuTopic, boolean fromSibling, int coverQuTopic) {
        LanguageModel lm;
        if (coverQuTopic == -1) {
            lm = getCoverQueryTopic(level, trueQuTopic, fromSibling);
            if (lm == null) {
                return "CQ topic not found!";
            }
        } else {
            lm = languageModels.get(coverQuTopic);
        }
        if (lm.getTrigramLM().isEmpty()) {
            return null;
        }
        ArrayList<String> possibleCoverQ = new ArrayList<>();
        Double[] probArray = lm.getProbArrayTrigram();
        for (String trigram : lm.getTrigramLM().keySet()) {
            /* P(d,t) = P(d|t)*P(t) */
            double prob = lm.getProbabilityTrigram(trigram, true);
            int bNum = getBucketNumber(prob, probArray);
            if (bNum == bucketNum) {
                possibleCoverQ.add(trigram);
            } else if (Objects.equals(probArray[bNum - 1], probArray[bucketNum - 1])) {
                possibleCoverQ.add(trigram);
            }
        }
        if (possibleCoverQ.size() > 0) {
            int coverQNum = getRandom(0, possibleCoverQ.size());
            coverQueryTopics.add(lm.getTopic_id());
            return possibleCoverQ.get(coverQNum);
        } else {
            return null;
        }
    }

    /**
     * Returns a trigram from a given language model and bucket number. Returns
     * a trigram which contains the bigram if provided.
     *
     * @param lm
     * @param bucketNum
     * @param bigram
     * @return
     */
    private String getTriGramFromLM(LanguageModel lm, int bucketNum, String bigram) {
        ArrayList<String> possibleCoverQ = new ArrayList<>();
        Double[] probArray = lm.getProbArrayTrigram();
        if (bigram == null) {
            for (String trigram : lm.getTrigramLM().keySet()) {
                /* P(d,t) = P(d|t)*P(t) */
                double prob = lm.getProbabilityTrigram(trigram, true);
                int bNum = getBucketNumber(prob, probArray);
                if (bNum == bucketNum) {
                    possibleCoverQ.add(trigram);
                } else if (Objects.equals(probArray[bNum - 1], probArray[bucketNum - 1])) {
                    possibleCoverQ.add(trigram);
                }
            }
        } else {
            for (String trigram : lm.getTrigramLM().keySet()) {
                String[] split = trigram.split(" ");
                String prevBigram = split[0] + " " + split[1];
                if (prevBigram.equals(bigram)) {
                    possibleCoverQ.add(trigram);
                }
            }
        }
        if (possibleCoverQ.size() > 0) {
            int coverQNum = getRandom(0, possibleCoverQ.size());
            if (bigram == null) {
                coverQueryTopics.add(lm.getTopic_id());
            }
            return possibleCoverQ.get(coverQNum);
        } else {
            return null;
        }
    }

    /**
     * Generate cover query of length 4 using fourgram language model.
     *
     * @param level
     * @param bucketNum
     * @param trueQuTopic
     * @param fromSibling
     * @param coverQuTopic
     * @return the cover query
     */
    private String getCQfromFourgramLM(int level, int bucketNum, int trueQuTopic, boolean fromSibling, int coverQuTopic) {
        LanguageModel lm;
        if (coverQuTopic == -1) {
            lm = getCoverQueryTopic(level, trueQuTopic, fromSibling);
            if (lm == null) {
                return "CQ topic not found!";
            }
        } else {
            lm = languageModels.get(coverQuTopic);
        }
        if (lm.getFourgramLM().isEmpty()) {
            return null;
        }
        ArrayList<String> possibleCoverQ = new ArrayList<>();
        Double[] probArray = lm.getProbArrayFourgram();
//        System.out.println(Arrays.toString(probArray));
        for (String fourgram : lm.getFourgramLM().keySet()) {
            /* P(d,t) = P(d|t)*P(t) */
            double prob = lm.getProbabilityFourgram(fourgram, true);
            int bNum = getBucketNumber(prob, probArray);
            if (bNum == bucketNum) {
                possibleCoverQ.add(fourgram);
            } else if (Objects.equals(probArray[bNum - 1], probArray[bucketNum - 1])) {
                possibleCoverQ.add(fourgram);
            }
        }
        if (possibleCoverQ.size() > 0) {
            int coverQNum = getRandom(0, possibleCoverQ.size());
            coverQueryTopics.add(lm.getTopic_id());
            return possibleCoverQ.get(coverQNum);
        } else {
            return null;
        }
    }

    /**
     * Returns a fourgram from a given language model and bucket number. Returns
     * a fourgram which contains the trigram if provided.
     *
     * @param lm
     * @param bucketNum
     * @param trigram
     * @return
     */
    private String getFourGramFromLM(LanguageModel lm, int bucketNum, String trigram) {
        ArrayList<String> possibleCoverQ = new ArrayList<>();
        Double[] probArray = lm.getProbArrayFourgram();
        if (trigram == null) {
            for (String fourgram : lm.getFourgramLM().keySet()) {
                /* P(d,t) = P(d|t)*P(t) */
                double prob = lm.getProbabilityFourgram(fourgram, true);
                int bNum = getBucketNumber(prob, probArray);
                if (bNum == bucketNum) {
                    possibleCoverQ.add(trigram);
                } else if (Objects.equals(probArray[bNum - 1], probArray[bucketNum - 1])) {
                    possibleCoverQ.add(fourgram);
                }
            }
        } else {
            for (String fourgram : lm.getFourgramLM().keySet()) {
                /* P(d,t) = P(d|t)*P(t) */
                String[] split = fourgram.split(" ");
                String prevTrigram = split[0] + " " + split[1] + " " + split[2];
                if (prevTrigram.equals(trigram)) {
                    possibleCoverQ.add(fourgram);
                }
            }
        }
        if (possibleCoverQ.size() > 0) {
            int coverQNum = getRandom(0, possibleCoverQ.size());
            if (trigram == null) {
                coverQueryTopics.add(lm.getTopic_id());
            }
            return possibleCoverQ.get(coverQNum);
        } else {
            return null;
        }
    }

    /**
     * Generate cover query of length greater than 4 using a special procedure.
     *
     * @param level
     * @param bucketNum
     * @param trueQuTopic
     * @param fromSibling
     * @param coverQuTopic
     * @return the cover query
     */
    private String getCQfromNgramLM(int length, int level, int bucketNum, int trueQuTopic, boolean fromSibling, int coverQuTopic) {
        LanguageModel lm;
        if (coverQuTopic == -1) {
            lm = getCoverQueryTopic(level, trueQuTopic, fromSibling);
            if (lm == null) {
                return "CQ topic not found!";
            }
        } else {
            lm = languageModels.get(coverQuTopic);
        }
        if (lm.isEmpty()) {
            return null;
        }
        ArrayList<String> cQuery = new ArrayList<>();
        for (int k = 0; k < length; k++) {
            String tempFourgram;
            if (cQuery.size() >= 3) {
                String trigram = "";
                for (int x = cQuery.size() - 3; x < cQuery.size(); x++) {
                    trigram += cQuery.get(x) + " ";
                }
                trigram = trigram.trim();
                tempFourgram = getFourGramFromLM(lm, bucketNum, trigram);
            } else {
                tempFourgram = null;
            }
            if (tempFourgram == null) {
                String tempTrigram;
                if (cQuery.size() >= 2) {
                    int l = cQuery.size() - 1;
                    String bigram = cQuery.get(l - 1) + " " + cQuery.get(l);
                    tempTrigram = getTriGramFromLM(lm, bucketNum, bigram);
                } else {
                    tempTrigram = null;
                }
                if (tempTrigram == null) {
                    String tempBigram;
                    if (cQuery.size() >= 1) {
                        String unigram = cQuery.get(cQuery.size() - 1);
                        tempBigram = getCQfromBigramLM(lm, bucketNum, unigram);
                    } else {
                        tempBigram = null;
                    }
                    if (tempBigram == null) {
                        String tempUnigram = getUniGramFromLM(lm, bucketNum);
                        if (tempUnigram != null) {
                            cQuery.add(tempUnigram);
                        }
                    } else {
                        List<String> tokens = tokenizer.TokenizeString(tempBigram);
                        if (tokens.size() == 2) {
                            cQuery.add(tokens.get(1));
                        }
                    }
                } else {
                    List<String> tokens = tokenizer.TokenizeString(tempTrigram);
                    if (tokens.size() == 3) {
                        cQuery.add(tokens.get(2));
                    }
                }
            } else {
                List<String> tokens = tokenizer.TokenizeString(tempFourgram);
                if (tokens.size() == 4) {
                    cQuery.add(tokens.get(3));
                }
            }
        }
        if (cQuery.size() < length) {
            return null;
        } else {
            String coverQ = "";
            for (String term : cQuery) {
                coverQ += term + " ";
            }
            coverQ = coverQ.trim();
            coverQueryTopics.add(lm.getTopic_id());
            return coverQ;
        }
    }

    /**
     * Creates a cover query based on the original user query length. Unigram,
     * bigram, trigram and fourgram language models are used to generate cover
     * queries of length 1, 2, 3 and 4 respectively. Cover queries of length
     * greater than 4 are created by a special procedure.
     *
     * @param queryLength
     * @param bucketNum
     * @param level
     * @param trueQuTopic
     * @param fromSibling
     * @param coverQuTopic
     */
    private String generateCoverQuery(int queryLength, int bucketNum, int level, int trueQuTopic, boolean fromSibling, int coverQuTopic) {
        int coverQuLen = getPoisson(queryLength);
        if (coverQuLen == 0) {
            return null;
        }
        String coverQ;
        switch (coverQuLen) {
            case 1:
                coverQ = getCQfromUnigramLM(level, bucketNum, trueQuTopic, fromSibling, coverQuTopic);
                break;
            case 2:
                coverQ = getCQfromBigramLM(level, bucketNum, trueQuTopic, fromSibling, coverQuTopic);
                break;
            case 3:
                coverQ = getCQfromTrigramLM(level, bucketNum, trueQuTopic, fromSibling, coverQuTopic);
                break;
            case 4:
                coverQ = getCQfromFourgramLM(level, bucketNum, trueQuTopic, fromSibling, coverQuTopic);
                break;
            default:
                coverQ = getCQfromNgramLM(coverQuLen, level, bucketNum, trueQuTopic, fromSibling, coverQuTopic);
                break;
        }
        return coverQ;
    }

    /**
     *
     * @param probability
     * @param max
     * @param min
     * @return
     */
    private int getBucketNumber(double probability, double max, double min) {
        double difference = (max - min) / 10.0;
        if (probability >= (max - difference)) {
            return 1;
        } else if (probability >= (max - 2 * difference)) {
            return 2;
        } else if (probability >= (max - 3 * difference)) {
            return 3;
        } else if (probability >= (max - 4 * difference)) {
            return 4;
        } else if (probability >= (max - 5 * difference)) {
            return 5;
        } else if (probability >= (max - 6 * difference)) {
            return 6;
        } else if (probability >= (max - 7 * difference)) {
            return 7;
        } else if (probability >= (max - 8 * difference)) {
            return 8;
        } else if (probability >= (max - 9 * difference)) {
            return 9;
        } else {
            return 10;
        }
    }

    /**
     *
     * @param probability
     *
     * @return
     */
    private int getBucketNumber(double probability, Double[] probArray) {
        for (int i = 0; i < probArray.length; i++) {
            if (probability >= probArray[i]) {
                return i + 1;
            }
        }
        return probArray.length;
    }

    /**
     * Returns the best topic that characterize the given query.
     *
     * @param tokens
     * @param n size of the query
     * @return
     */
    private int getBestTopic(String query, int n, String trueQuery) {
        /* probability of query according to best unigram language model */
        double[] maxProb = {0, 0, 0, 0, 0};
        int[] topicNo = {-1, -1, -1, -1, -1};
        /* topic index of the best unigram language model */
        int index = 0;
        for (LanguageModel lm : languageModels) {
            if (lm.isEmpty()) {
                index++;
                continue;
            }
            double prob;
            if (n == 1) {
                prob = lm.getProbabilityUnigram(query) * lm.getTopic_Prior(totalNodesInTopicHierarchy);
            } else if (n == 2) {
                prob = lm.getProbabilityBigram(query, true) * lm.getTopic_Prior(totalNodesInTopicHierarchy);
            } else if (n == 3) {
                prob = lm.getProbabilityTrigram(query, true) * lm.getTopic_Prior(totalNodesInTopicHierarchy);
            } else if (n == 4) {
                prob = lm.getProbabilityFourgram(query, true) * lm.getTopic_Prior(totalNodesInTopicHierarchy);
            } else {
                prob = lm.getProbabilityNgram(query, n) * lm.getTopic_Prior(totalNodesInTopicHierarchy);
            }

            for (int i = 0; i < 5; i++) {
                if (prob > maxProb[i]) {
                    for (int j = 4; j > i; j--) {
                        maxProb[j] = maxProb[j - 1];
                    }
                    maxProb[i] = prob;
                    topicNo[i] = index;
                    break;
                }
            }
            index++;
        }
        try {
            fw.write("Query: " + trueQuery.split("\t")[0] + "\n");
            for (int i = 0; i < 5; i++) {
                if (topicNo[i] != -1) {
                    fw.write("Topic#" + (i + 1) + " - " + getTopicPath(topicNo[i]) + " [" + maxProb[i] + "]" + "\n");
                    fw.flush();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GenerateCoverQuery.class.getName()).log(Level.SEVERE, null, ex);
        }

        return topicNo[0];
    }

    private String getTopicPath(int topicN) {
        String path = "";
        path = languageModels.get(topicN).getTopic_name();
        LanguageModel lm = languageModels.get(topicN).getParent();
        while (lm != null) {
            path += "<-" + lm.getTopic_name();
            lm = lm.getParent();
        }
        return path;
    }

    /**
     * Generate a list of integers where the first value is the query length,
     * second value is the inferred topic number and the last one is the bucket
     * number from to which the query belongs.
     *
     * @param query true user query
     * @return list of integers
     */
    private ArrayList<Integer> getScore(String query) {
        ArrayList<Integer> scores = new ArrayList<>();
        String tempQuery = query.split("\t")[0];
        List<String> tokens = tokenizer.TokenizeString(tempQuery);
        String modifiedQuery = "";
        for (String token : tokens) {
            modifiedQuery += token + " ";
        }
        modifiedQuery = modifiedQuery.trim();
        int topicNo = getBestTopic(modifiedQuery, tokens.size(), query);
        scores.add(tokens.size());
        scores.add(topicNo);
        if (topicNo == -1) {
            return scores;
        }

        LanguageModel selectedModel = languageModels.get(topicNo);
        int n = tokens.size();
        if (n == 1) {
            Double[] probArray = selectedModel.getProbArrayUnigram();
            double prob = selectedModel.getProbabilityUnigram(modifiedQuery);
            scores.add(getBucketNumber(prob, probArray));
        } else if (n == 2) {
            Double[] probArray = selectedModel.getProbArrayBigram();
            double prob = selectedModel.getProbabilityBigram(modifiedQuery, true);
            scores.add(getBucketNumber(prob, probArray));
        } else if (n == 3) {
            Double[] probArray = selectedModel.getProbArrayTrigram();
            double prob = selectedModel.getProbabilityTrigram(modifiedQuery, true);
            scores.add(getBucketNumber(prob, probArray));
        } else if (n == 4) {
            Double[] probArray = selectedModel.getProbArrayFourgram();
            double prob = selectedModel.getProbabilityFourgram(modifiedQuery, true);
            scores.add(getBucketNumber(prob, probArray));
        } else {
            Double[] probArray = selectedModel.getProbArrayFourgram();
            double prob = selectedModel.getProbabilityNgram(modifiedQuery, n);
            scores.add(getBucketNumber(prob, probArray));
        }
        return scores;
    }

    /**
     * Check if the user query is sequentially edited or not.
     *
     * @param previousQuTopic
     * @param currentQuTopic
     * @return
     */
    private int checkSequentialEdited(int previousQuTopic, int currentQuTopic) {
        int parentId = languageModels.get(previousQuTopic).getParent().getTopic_id();
        /**
         * Check if current query topic is the parent of previous query topic,
         * then sequential editing is true.
         */
        if (parentId == currentQuTopic) {
            return 1;
        }
        /**
         * Check if current query topic is the child of previous query topic,
         * then sequential editing is true.
         */
        for (LanguageModel lm : languageModels.get(previousQuTopic).getListOfChildren()) {
            int childTopicId = lm.getTopic_id();
            if (childTopicId == currentQuTopic) {
                return -1;
            }
        }
        /**
         * Current query topic is neither parent nor child of the previous query
         * topic, so return 0.
         */
        return 0;
    }

    /**
     * Creates N cover queries based on true user query. Handle sequentially
     * edited queries by using lastQueryTopicNo.
     *
     * @param query true user query
     * @param N number of cover queries required
     * @param previousQueryTopicNo -1 if the previous query and current query
     * belong to two different session.
     * @param coverQTopics
     * @return list of cover queries
     */
    public ArrayList<String> generateNQueries(String query, int N, int previousQueryTopicNo, ArrayList<Integer> coverQTopics) {
        ArrayList<String> coverQueries = new ArrayList<>();
        ArrayList<Integer> scores = getScore(query);
        if (scores.get(1) == -1) {
            /* if topic of the query can not be inferred */
            return null;
        }
        currentQueryTopicNo = scores.get(1);
        if (N == 0) {
            return coverQueries;
        }

        int currentQueryTopicLevel = languageModels.get(currentQueryTopicNo).getLevel();
        int seqEdited = 0;
        if (previousQueryTopicNo != -1) {
            seqEdited = checkSequentialEdited(previousQueryTopicNo, currentQueryTopicNo);
        }
        if (seqEdited == 1) {
            try {
                fw.write("*****current query is sequentially edited from previous query*****" + "\n");
                fw.flush();
            } catch (IOException ex) {
                Logger.getLogger(GenerateCoverQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        int bucketNum = scores.get(2);
        int count = 0;

        coverQueryTopics = new ArrayList<>();
        while (true) {
            String cQuery;
            if (seqEdited == 0) {
                /* Current query is not sequentially edited. */
                if (count < N / 2) {
                    /* Generating cover query from sibling topics. */
                    cQuery = generateCoverQuery(scores.get(0), bucketNum, currentQueryTopicLevel, currentQueryTopicNo, true, -1);
                    if (cQuery == null) {
                    }
                } else {
                    /* Generating cover query from same level of original query but not from sibling topics. */
                    cQuery = generateCoverQuery(scores.get(0), bucketNum, currentQueryTopicLevel, currentQueryTopicNo, false, -1);
                }
                if (cQuery != null) {
                    if (!cQuery.equals("CQ topic not found!")) {
                        coverQueries.add(cQuery);
                    }
                    count++;
                }
                if (count == N) {
                    break;
                }
            } else {
                /**
                 * Current query is sequentially edited, so cover queries will
                 * be generated based on previous cover query topics.
                 */
                if (count == coverQTopics.size()) {
                    break;
                }
                int coverQuTopic = coverQTopics.get(count);
                if (seqEdited == 1) {
                    /**
                     * Cover query should be generated from parent topic of the
                     * previous cover query.
                     */
                    if (languageModels.get(coverQuTopic).getParent().isEmpty()) {
                        cQuery = "CQ topic not found!";
                    } else {
                        int topicId = languageModels.get(coverQuTopic).getParent().getTopic_id();
                        cQuery = generateCoverQuery(scores.get(0), bucketNum, currentQueryTopicLevel, currentQueryTopicNo, false, topicId);
                    }
                } else {
                    /**
                     * Cover query should be generated from child topic of the
                     * previous cover query.
                     */
                    int numChildren = languageModels.get(coverQuTopic).getListOfChildren().size();
                    if (numChildren > 0) {
                        int rand = -1;
                        for (int x = 0; x < numChildren; x++) {
                            rand = getRandom(0, numChildren);
                            LanguageModel tempLm = languageModels.get(coverQuTopic).getListOfChildren().get(rand);
                            if (!tempLm.isEmpty()) {
                                break;
                            } else {
                                rand = -1;
                            }
                        }
                        if (rand == -1) {
                            cQuery = "CQ topic not found!";
                        } else {
                            int topicId = languageModels.get(coverQuTopic).getListOfChildren().get(rand).getTopic_id();
                            cQuery = generateCoverQuery(scores.get(0), bucketNum, currentQueryTopicLevel, currentQueryTopicNo, false, topicId);
                        }
                    } else {
                        cQuery = "CQ topic not found!";
                    }
                }
                if (cQuery != null) {
                    if (!cQuery.equals("CQ topic not found!")) {
                        coverQueries.add(cQuery);
                    }
                    count++;
                }
            }
        }
        if (coverQueries.isEmpty()) {
            return null;
        }
        return coverQueries;
    }

    /**
     *
     * @return
     */
    public int getLastQueryTopicNo() {
        return currentQueryTopicNo;
    }

    public ArrayList<Integer> getCoverQueryTopics() {
        return coverQueryTopics;
    }

    public ArrayList<String> getCoverQueryTopics(String query) {
        ArrayList<String> topics = new ArrayList<>();
        for (int i = 0; i < coverQueryTopics.size(); i++) {
            String topic = languageModels.get(coverQueryTopics.get(i)).getTopic_name();
            LanguageModel tempLM = languageModels.get(coverQueryTopics.get(i)).getParent();
            while (tempLM != null) {
                topic += "<-" + tempLM.getTopic_name();
                tempLM = tempLM.getParent();
            }
            topics.add(topic);
        }
        return topics;
    }

}
