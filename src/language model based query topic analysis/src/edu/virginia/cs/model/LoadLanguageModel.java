/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.cs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import edu.virginia.cs.utility.FileOperations;

/**
 *
 * @author Wasi
 */
public class LoadLanguageModel {

    private final ArrayList<LanguageModel> languageModels;
    /* Reference model used for smoothing purpose */
    private final HashMap<String, Float> referenceModel;
    private final FileOperations fiop;
    private int level;

    public LoadLanguageModel(HashMap<String, Float> refModel) {
        languageModels = new ArrayList<>();
        referenceModel = refModel;
        fiop = new FileOperations();
    }

    /**
     * Returns the list of loaded language models.
     *
     * @return list of language models
     */
    public ArrayList<LanguageModel> getLanguageModels() {
        return languageModels;
    }

    /**
     * Loads language models up to level 'param' from all language models of
     * DMOZ categories.
     *
     * @param folder
     * @param param depth of the hierarchy
     * @return list of language models
     */
    public ArrayList<LanguageModel> loadModels(String folder, int param) {
        level = param;
        LoadDirectory(folder, null, 0, "Top");
//        for (LanguageModel lm : languageModels) {
//            System.out.println("------------------------------------------------");
//            System.out.println("Topic Name - " + lm.getTopic_name());
//            if (lm.getParent() != null) {
//                System.out.println("Parent Topic Name - " + lm.getParent().getTopic_name());
//            }
//            if (lm.hasChildren()) {
//                System.out.println("Number of Childrens - " + lm.getListOfChildren().size());
//            }
//            System.out.println(lm.getUnigramLM().toString());
//            System.out.println(lm.getBigramLM().toString());
//            System.out.println(lm.getTrigramLM().toString());
//            System.out.println(lm.getFourgramLM().toString());
//            System.out.println("------------------------------------------------");
//        }
        System.out.println("Number of models loaded = " + languageModels.size());
        return languageModels;
    }

    /**
     * Load a directory and returns the corresponding language model.
     *
     * @param folder folder name in which current language model resides
     * @param parent parent language model
     * @param param current level of the hierarchy
     * @param name category name
     * @return language model
     */
    private LanguageModel LoadDirectory(String folder, LanguageModel parent, int param, String name) {
        if (param > level) {
            return null;
        }
        LanguageModel LM = new LanguageModel(referenceModel);
        LM.setParent(parent);
        LM.setTopic_name(name);
        LM.setLevel(param);
        File dir = new File(folder);
        for (File f : dir.listFiles()) {
            if (f.isFile()) {
                switch (f.getName()) {
                    case "unigram_LM.txt": {
                        ArrayList<String> lines = fiop.LoadFile(f.getAbsolutePath(), -1);
                        HashMap<String, Integer> unigramLM = new HashMap<>();
                        for (int i = 1; i < lines.size(); i++) {
                            String[] split = lines.get(i).split("\t");
                            unigramLM.put(split[0], Integer.parseInt(split[1]));
                        }
                        LM.setTotalUnigrams(Integer.parseInt(lines.get(0)));
                        LM.setTotalUniqueUnigrams(lines.size() - 1);
                        LM.setUnigramLM(unigramLM);
                        break;
                    }
                    case "bigram_LM.txt": {
                        ArrayList<String> lines = fiop.LoadFile(f.getAbsolutePath(), -1);
                        HashMap<String, Integer> bigramLM = new HashMap<>();
                        for (int i = 1; i < lines.size(); i++) {
                            String[] split = lines.get(i).split("\t");
                            bigramLM.put(split[0], Integer.parseInt(split[1]));
                        }
                        LM.setBigramLM(bigramLM);
                        break;
                    }
                    case "trigram_LM.txt": {
                        ArrayList<String> lines = fiop.LoadFile(f.getAbsolutePath(), -1);
                        HashMap<String, Integer> trigramLM = new HashMap<>();
                        for (int i = 1; i < lines.size(); i++) {
                            String[] split = lines.get(i).split("\t");
                            trigramLM.put(split[0], Integer.parseInt(split[1]));
                        }
                        LM.setTrigramLM(trigramLM);
                        break;
                    }
                    case "fourgram_LM.txt": {
                        ArrayList<String> lines = fiop.LoadFile(f.getAbsolutePath(), -1);
                        HashMap<String, Integer> fourgramLM = new HashMap<>();
                        for (int i = 1; i < lines.size(); i++) {
                            String[] split = lines.get(i).split("\t");
                            fourgramLM.put(split[0], Integer.parseInt(split[1]));
                        }
                        LM.setFourgramLM(fourgramLM);
                        break;
                    }
                    default:
                        System.out.println("unknown file found...");
                        break;
                }
            } else if (f.isDirectory()) {
                LanguageModel temp = LoadDirectory(f.getAbsolutePath(), LM, param + 1, f.getName());
                if (temp != null) {
                    LM.addChildren(temp);
                }
            }
        }
        LM.setMaxMinProb("unigram");
        LM.setMaxMinProb("bigram");
        LM.setMaxMinProb("trigram");
        LM.setMaxMinProb("fourgram");
        LM.setTopic_id(languageModels.size());
        languageModels.add(LM);
        return LM;
    }
}
