/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.cs.utility;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Wasi
 */
public class MapUtilities {

    /**
     * Method that generate the id of all users for evaluation.
     *
     * @param unsortMap unsorted Map
     * @param order if true, then sort in ascending order, otherwise in
     * descending order
     * @return sorted Map
     */
    public static <K, V> Map<K, V> sortMapByValue(Map<K, V> unsortMap, final boolean order) {
        List<Map.Entry<K, V>> list = new LinkedList<>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                if (order) {
                    return ((Comparable<V>) ((Map.Entry<K, V>) (o1)).getValue()).compareTo(((Map.Entry<K, V>) (o2)).getValue());
                } else {
                    return ((Comparable<V>) ((Map.Entry<K, V>) (o2)).getValue()).compareTo(((Map.Entry<K, V>) (o1)).getValue());
                }
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * Method that generate the id of all users for evaluation.
     *
     * @param unsortMap unsorted Map
     * @param order if true, then sort in ascending order, otherwise in
     * descending order
     * @param k return only the top k elements
     * @return sorted Map of k elements
     */
    public static <K, V> Map<K, V> sortMapByValue(Map<K, V> unsortMap, final boolean order, int k) {
        List<Map.Entry<K, V>> list = new LinkedList<>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                if (order) {
                    return ((Comparable<V>) ((Map.Entry<K, V>) (o1)).getValue()).compareTo(((Map.Entry<K, V>) (o2)).getValue());
                } else {
                    return ((Comparable<V>) ((Map.Entry<K, V>) (o2)).getValue()).compareTo(((Map.Entry<K, V>) (o1)).getValue());
                }
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        int i = 0;
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
            i++;
            if (i == k) {
                break;
            }
        }

        return result;
    }
}
