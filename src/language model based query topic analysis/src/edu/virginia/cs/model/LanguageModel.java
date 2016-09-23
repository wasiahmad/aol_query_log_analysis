/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.cs.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Wasi
 */
public class LanguageModel {

    private final int BUCKET_SIZE = 10;
    private int topic_id;
    private String topic_name;
    private HashMap<String, Integer> unigramLM;
    private HashMap<String, Integer> bigramLM;
    private HashMap<String, Integer> trigramLM;
    private HashMap<String, Integer> fourgramLM;
    private int totalUnigrams;
    private int totalUniqueUnigrams;
    private LanguageModel parent;
    private ArrayList<LanguageModel> listOfChildren;
    private final double lambda;
    private int level;
    private double maxProbUnigram;
    private double minProbUnigram;
    private double maxProbBigram;
    private double minProbBigram;
    private double maxProbTrigram;
    private double minProbTrigram;
    private double maxProbFourgram;
    private double minProbFourgram;
    private final Double[] probArrayUnigram;
    private final Double[] probArrayBigram;
    private final Double[] probArrayTrigram;
    private final Double[] probArrayFourgram;
    private double topicPrior;
    /* Reference model used for smoothing purpose */
    private final HashMap<String, Float> referenceModel;

    public LanguageModel(HashMap<String, Float> refModel) {
        referenceModel = refModel;
        this.unigramLM = new HashMap<>();
        this.bigramLM = new HashMap<>();
        this.trigramLM = new HashMap<>();
        this.fourgramLM = new HashMap<>();
        this.listOfChildren = new ArrayList<>();
        this.probArrayUnigram = new Double[BUCKET_SIZE];
        this.probArrayBigram = new Double[BUCKET_SIZE];
        this.probArrayTrigram = new Double[BUCKET_SIZE];
        this.probArrayFourgram = new Double[BUCKET_SIZE];
        lambda = 0.9;
        topicPrior = -1;
    }

    /**
     * Return the topic id of the language model.
     *
     * @return topic id of the language model
     */
    public int getTopic_id() {
        return topic_id;
    }

    /**
     * Computes topic prior based on the number of nodes in the subtree rooted
     * at topic t divided by total number of nodes in the topic hierarchy.
     *
     * @param totalNodes
     * @return
     */
    public double getTopic_Prior(int totalNodes) {
        if (topicPrior == -1) {
            double totalNodesInSubTree = 1;
            LinkedList<LanguageModel> list = new LinkedList<>();
            list.addAll(listOfChildren);
            while (!list.isEmpty()) {
                LanguageModel lm = list.poll();
                list.addAll(lm.getListOfChildren());
                totalNodesInSubTree++;
            }
            topicPrior = totalNodesInSubTree / totalNodes;
        }
        return topicPrior;
    }

    /**
     * Set the topic id of the language model.
     *
     * @param topicId
     */
    public void setTopic_id(int topicId) {
        this.topic_id = topicId;
    }

    /**
     * Returns the topic name of the language model.
     *
     * @return topic name of the language model
     */
    public String getTopic_name() {
        return topic_name;
    }

    /**
     * Set the topic name of the language model.
     *
     * @param topic_name
     */
    public void setTopic_name(String topic_name) {
        this.topic_name = topic_name;
    }

    /**
     * get the unigram language model.
     *
     * @return unigram language model
     */
    public HashMap<String, Integer> getUnigramLM() {
        return unigramLM;
    }

    /**
     * Set the unigram language model.
     *
     * @param unigramLM
     */
    public void setUnigramLM(HashMap<String, Integer> unigramLM) {
        this.unigramLM = unigramLM;
    }

    /**
     * get the bigram language model.
     *
     * @return bigram language model
     */
    public HashMap<String, Integer> getBigramLM() {
        return bigramLM;
    }

    /**
     * Set the bigram language model.
     *
     * @param bigramLM
     */
    public void setBigramLM(HashMap<String, Integer> bigramLM) {
        this.bigramLM = bigramLM;
    }

    /**
     * get the trigram language model.
     *
     * @return trigram language model
     */
    public HashMap<String, Integer> getTrigramLM() {
        return trigramLM;
    }

    /**
     * Set the trigram language model.
     *
     * @param trigramLM
     */
    public void setTrigramLM(HashMap<String, Integer> trigramLM) {
        this.trigramLM = trigramLM;
    }

    /**
     * get the fourgram language model.
     *
     * @return fourgram language model
     */
    public HashMap<String, Integer> getFourgramLM() {
        return fourgramLM;
    }

    /**
     * Set the fourgram language model.
     *
     * @param fourgramLM
     */
    public void setFourgramLM(HashMap<String, Integer> fourgramLM) {
        this.fourgramLM = fourgramLM;
    }

    /**
     * get the parent language models.
     *
     * @return parent language model
     */
    public LanguageModel getParent() {
        return parent;
    }

    /**
     * Returns total number of unigrams in the language model.
     *
     * @return
     */
    public int getTotalUnigrams() {
        return totalUnigrams;
    }

    /**
     * Sets total number of unigrams of the language model.
     *
     * @param totalUnigrams
     */
    public void setTotalUnigrams(int totalUnigrams) {
        this.totalUnigrams = totalUnigrams;
    }

    /**
     *
     * @return
     */
    public int getTotalUniqueUnigrams() {
        return totalUniqueUnigrams;
    }

    /**
     *
     * @param totalUniqueUnigrams
     */
    public void setTotalUniqueUnigrams(int totalUniqueUnigrams) {
        this.totalUniqueUnigrams = totalUniqueUnigrams;
    }

    /**
     * Set the parent language model.
     *
     * @param parent
     */
    public void setParent(LanguageModel parent) {
        this.parent = parent;
    }

    /**
     * get the children language models.
     *
     * @return list of child language models
     */
    public ArrayList<LanguageModel> getListOfChildren() {
        return listOfChildren;
    }

    /**
     * Set the children language models.
     *
     * @param listOfChildren
     */
    public void setListOfChildren(ArrayList<LanguageModel> listOfChildren) {
        this.listOfChildren = listOfChildren;
    }

    /**
     * Add a children to the list of child language models.
     *
     * @param children
     */
    public void addChildren(LanguageModel children) {
        this.listOfChildren.add(children);
    }

    public boolean hasChildren() {
        return listOfChildren.size() > 0;
    }

    /**
     * Checks whether a language model is empty or not.
     *
     * @return
     */
    public boolean isEmpty() {
        return unigramLM.isEmpty();
    }

    /**
     * Computes probability from reference model for smoothing purpose.
     *
     * @param unigram
     * @return
     */
    public double getReferenceProbability(String unigram) {
        if (referenceModel.containsKey(unigram)) {
            return referenceModel.get(unigram);
        }
        return 0.00000001;
    }

    /**
     * Computes joint probability or conditional probability of a unigram.
     *
     * @param unigram
     * @return
     */
    public double getProbabilityUnigram(String unigram) {
        double prob;
        /* Computing probability of a unigram using linear interpolation smoothing */
        if (unigramLM.containsKey(unigram)) {
            prob = (1.0 - lambda) * (unigramLM.get(unigram) / totalUnigrams) + lambda * getReferenceProbability(unigram);
        } else {
            prob = lambda * getReferenceProbability(unigram);
        }
        return prob;
    }

    /**
     * Computes joint probability or conditional probability of a bigram.
     *
     * @param bigram
     * @param isJoint
     * @return
     */
    public double getProbabilityBigram(String bigram, boolean isJoint) {
        double prob;
        /* Computing probability of a bigram using linear interpolation smoothing */
        String[] split = bigram.split(" ");
        if (bigramLM.containsKey(bigram)) {
            prob = (1.0 - lambda) * (bigramLM.get(bigram) / unigramLM.get(split[0]));
        } else {
            prob = 0.0;
        }
        prob += lambda * getProbabilityUnigram(split[0]);
        if (isJoint) {
            prob *= getProbabilityUnigram(split[0]);
        }
        return prob;
    }

    /**
     * Computes joint probability or conditional probability of a trigram.
     *
     * @param trigram
     * @param isJoint
     * @return
     */
    public double getProbabilityTrigram(String trigram, boolean isJoint) {
        double prob;
        /* Computing probability of a trigram using linear interpolation smoothing */
        String[] split = trigram.split(" ");
        String prevBigram = split[0] + " " + split[1];
        if (trigramLM.containsKey(trigram)) {
            prob = (1.0 - lambda) * (trigramLM.get(trigram) / bigramLM.get(prevBigram));
        } else {
            prob = 0.0;
        }
        String bigram = split[1] + " " + split[2];
        prob += lambda * getProbabilityBigram(bigram, false);
        if (isJoint) {
            prob *= getProbabilityBigram(prevBigram, false);
            prob *= getProbabilityUnigram(split[0]);
        }
        return prob;
    }

    /**
     * Computes joint probability or conditional probability of a fourgram.
     *
     * @param fourgram
     * @param isJoint
     * @return if isJoint is true, returns joint probability, otherwise
     * conditional probability
     */
    public double getProbabilityFourgram(String fourgram, boolean isJoint) {
        double prob;
        /* Computing probability of a fourgram using linear interpolation smoothing */
        String[] split = fourgram.split(" ");
        String prevTrigram = split[0] + " " + split[1] + " " + split[2];
        if (fourgramLM.containsKey(fourgram)) {
            prob = (1.0 - lambda) * (fourgramLM.get(fourgram) / trigramLM.get(prevTrigram));
        } else {
            prob = 0.0;
        }
        String trigram = split[1] + " " + split[2] + " " + split[3];
        prob += lambda * getProbabilityTrigram(trigram, false);
        if (isJoint) {
            prob *= getProbabilityBigram(prevTrigram, false);
            String bigram = split[0] + " " + split[1];
            prob *= getProbabilityBigram(bigram, false);
            prob *= getProbabilityUnigram(split[0]);
        }
        return prob;
    }

    /**
     * Computes joint probability of a n-gram where n>4. Suppose n=6, then joint
     * probability formula is, P(w6 w5 w4 w3 w2 w1) = P(w6 | w5 w4 w3) * P(w5 |
     * w4 w3 w2) * P(w4 | w3 w2 w1) * P(w3 | w2 w1) * P(w2 | w1) * P(w1)
     *
     * @param ngram
     * @param n
     * @return
     */
    public double getProbabilityNgram(String ngram, int n) {
        double prob = 1.0;
        /* Computing probability of a n-gram using linear interpolation smoothing */
        String[] split = ngram.split(" ");
        for (int i = split.length - 1; i >= 0; i--) {
            if (i >= 3) {
                String fourgram = split[i - 3] + " " + split[i - 2] + " " + split[i - 1] + " " + split[i];
                prob *= getProbabilityFourgram(fourgram, false);
            } else if (i == 2) {
                String trigram = split[i - 2] + " " + split[i - 1] + " " + split[i];
                prob *= getProbabilityBigram(trigram, false);
            } else if (i == 1) {
                String bigram = split[i - 1] + " " + split[i];
                prob *= getProbabilityBigram(bigram, false);
            } else {
                prob *= getProbabilityUnigram(split[0]);
            }
        }
        return prob;
    }

    /**
     * Set the Maximum and Minimum probability of the language model.
     *
     * @param param either unigram or bigram or trigram or fourgram
     */
    public void setMaxMinProb(String param) {
        double max = -1.0;
        double min = -1.0;
        HashMap<String, Double> tempMap = new HashMap<>();
        if (param.equals("unigram")) {
            if (unigramLM.isEmpty()) {
                return;
            }
            for (Entry<String, Integer> entry : unigramLM.entrySet()) {
                double prob = getProbabilityUnigram(entry.getKey());
                tempMap.put(entry.getKey(), prob);
                if (max < prob) {
                    max = prob;
                }
                if (min > prob) {
                    min = prob;
                }
            }
            maxProbUnigram = max;
            minProbUnigram = min;
            List<Entry<String, Double>> list = sortByComparator(tempMap, false);
            int size = list.size() % BUCKET_SIZE == 0 ? list.size() / BUCKET_SIZE : (list.size() / BUCKET_SIZE) + 1;
            for (int i = 1; i <= BUCKET_SIZE; i++) {
                int index = i * size;
                if (index >= list.size()) {
                    index = list.size() - 1;
                    probArrayUnigram[i - 1] = list.get(index).getValue();
                } else {
                    probArrayUnigram[i - 1] = list.get(index).getValue();
                }
            }
        } else if (param.equals("bigram")) {
            if (bigramLM.isEmpty()) {
                return;
            }
            for (Entry<String, Integer> entry : bigramLM.entrySet()) {
                double prob = getProbabilityBigram(entry.getKey(), true);
                tempMap.put(entry.getKey(), prob);
                if (max < prob) {
                    max = prob;
                }
                if (min > prob) {
                    min = prob;
                }
            }
            maxProbBigram = max;
            minProbBigram = min;
            List<Entry<String, Double>> list = sortByComparator(tempMap, false);
            int size = list.size() % BUCKET_SIZE == 0 ? list.size() / BUCKET_SIZE : (list.size() / BUCKET_SIZE) + 1;
            for (int i = 1; i <= BUCKET_SIZE; i++) {
                int index = i * size;
                if (index >= list.size()) {
                    index = list.size() - 1;
                    probArrayBigram[i - 1] = list.get(index).getValue();
                } else {
                    probArrayBigram[i - 1] = list.get(index).getValue();
                }
            }
        } else if (param.equals("trigram")) {
            if (trigramLM.isEmpty()) {
                return;
            }
            for (Entry<String, Integer> entry : trigramLM.entrySet()) {
                double prob = getProbabilityTrigram(entry.getKey(), true);
                tempMap.put(entry.getKey(), prob);
                if (max < prob) {
                    max = prob;
                }
                if (min > prob) {
                    min = prob;
                }
            }
            maxProbTrigram = max;
            minProbTrigram = min;
            List<Entry<String, Double>> list = sortByComparator(tempMap, false);
            int size = list.size() % BUCKET_SIZE == 0 ? list.size() / BUCKET_SIZE : (list.size() / BUCKET_SIZE) + 1;
            for (int i = 1; i <= BUCKET_SIZE; i++) {
                int index = i * size;
                if (index >= list.size()) {
                    index = list.size() - 1;
                    probArrayTrigram[i - 1] = list.get(index).getValue();
                } else {
                    probArrayTrigram[i - 1] = list.get(index).getValue();
                }
            }
        } else if (param.equals("fourgram")) {
            if (fourgramLM.isEmpty()) {
                return;
            }
            for (Entry<String, Integer> entry : fourgramLM.entrySet()) {
                double prob = getProbabilityFourgram(entry.getKey(), true);
                tempMap.put(entry.getKey(), prob);
                if (max < prob) {
                    max = prob;
                }
                if (min > prob) {
                    min = prob;
                }
            }
            maxProbFourgram = max;
            minProbFourgram = min;
            List<Entry<String, Double>> list = sortByComparator(tempMap, false);
            int size = list.size() % BUCKET_SIZE == 0 ? list.size() / BUCKET_SIZE : (list.size() / BUCKET_SIZE) + 1;
            for (int i = 1; i <= BUCKET_SIZE; i++) {
                int index = i * size;
                if (index >= list.size()) {
                    index = list.size() - 1;
                    probArrayFourgram[i - 1] = list.get(index).getValue();
                } else {
                    probArrayFourgram[i - 1] = list.get(index).getValue();
                }
            }
        } else {
            System.err.println("Unknown Parameter...!");
        }
    }

    /**
     * Returns the level of the language model in the tree hierarchy.
     *
     * @return
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the level of the language model in the tree hierarchy.
     *
     * @param level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Returns the upper bound of the unigram language model.
     *
     * @return maximum probability
     */
    public double getMaxProbUnigram() {
        return maxProbUnigram;
    }

    /**
     * Returns the lower bound of the unigram language model.
     *
     * @return
     */
    public double getMinProbUnigram() {
        return minProbUnigram;
    }

    /**
     * Returns the upper bound of the bigram language model.
     *
     * @return
     */
    public double getMaxProbBigram() {
        return maxProbBigram;
    }

    /**
     * Returns the lower bound of the bigram language model.
     *
     * @return
     */
    public double getMinProbBigram() {
        return minProbBigram;
    }

    /**
     * Returns the upper bound of the trigram language model.
     *
     * @return
     */
    public double getMaxProbTrigram() {
        return maxProbTrigram;
    }

    /**
     * Returns the lower bound of the trigram language model.
     *
     * @return
     */
    public double getMinProbTrigram() {
        return minProbTrigram;
    }

    /**
     * Returns the upper bound of the fourgram language model.
     *
     * @return
     */
    public double getMaxProbFourgram() {
        return maxProbFourgram;
    }

    /**
     * Returns the lower bound of the fourgram language model.
     *
     * @return
     */
    public double getMinProbFourgram() {
        return minProbFourgram;
    }

    /**
     *
     * @return
     */
    public Double[] getProbArrayUnigram() {
        return probArrayUnigram;
    }

    /**
     *
     * @return
     */
    public Double[] getProbArrayBigram() {
        return probArrayBigram;
    }

    /**
     *
     * @return
     */
    public Double[] getProbArrayTrigram() {
        return probArrayTrigram;
    }

    /**
     *
     * @return
     */
    public Double[] getProbArrayFourgram() {
        return probArrayFourgram;
    }

    @Override
    public String toString() {
        return "LanguageModel{" + "topic_name=" + topic_name + ", unigramLM=" + unigramLM + ", bigramLM=" + bigramLM + ", trigramLM=" + trigramLM + ", parent=" + parent + ", listOfChildren=" + listOfChildren + '}';
    }

    /**
     * Method that generate the id of all users for evaluation.
     *
     * @param unsortMap unsorted Map
     * @param order if true, then sort in ascending order, otherwise in
     * descending order
     * @return sorted Map
     */
    private List<Entry<String, Double>> sortByComparator(Map<String, Double> unsortMap, final boolean order) {
        List<Entry<String, Double>> list = new LinkedList<>(unsortMap.entrySet());
        // Sorting the list based on values
        Collections.sort(list, (Entry<String, Double> o1, Entry<String, Double> o2) -> {
            if (order) {
                return o1.getValue().compareTo(o2.getValue());
            } else {
                return o2.getValue().compareTo(o1.getValue());

            }
        });
        return list;
    }

}
