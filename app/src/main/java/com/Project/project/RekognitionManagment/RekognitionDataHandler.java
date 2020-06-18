package com.Project.project.RekognitionManagment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Responsible for handling Rekognition data.
 */
public class RekognitionDataHandler {
    List<RekognitionFeature> rekognitionFeatures;

    public RekognitionDataHandler(List<RekognitionFeature> rekognitionFeatures) {
        this.rekognitionFeatures = rekognitionFeatures;
    }

    /**
     * Descending sorting the features by their distribution.
     *
     * @param map
     * @return
     */
    private static Map<String, Integer> sortByValue(Map<String, Integer> map) {
        // Creates a list from elements of HashMap.
        List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());
        // Sorting the list.
        Collections.sort(list, (object1, object2) -> (object2.getValue()).compareTo(object1.getValue()));
        // Put data from sorted list to hashmap.
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    /**
     * Return top N features after sorting.
     *
     * @param N
     * @return
     */
    public List<RekognitionFeature> getTopNFeaturesByDistribution(int N) {
        Map<String, Integer> featureAndDist = setFeatureCounterMap();
        featureAndDist = sortByValue(featureAndDist);
        return getTopFeatures(featureAndDist, N);

    }

    /**
     * Creates map <feature name, feature distribution>
     *
     * @return map:  <feature name,distribution>.
     */
    private Map<String, Integer> setFeatureCounterMap() {
        Map<String, Integer> featureAndCounter = new HashMap<>();
        for (int feature = 0; feature < rekognitionFeatures.size(); feature++) {
            String featureName = rekognitionFeatures.get(feature).getFeatureName();
            // Feature already found in a different picture.
            if (featureAndCounter.containsKey(featureName)) {
                int currentCounter = featureAndCounter.get(featureName);
                featureAndCounter.put(featureName, currentCounter + 1);
            } else {
                featureAndCounter.put(featureName, 1);
            }
        }
        return featureAndCounter;
    }

    /**
     * Return top N features after sorting.
     *
     * @param FeaturesAndCounter
     * @param amount
     * @return top amount features after sorting.
     */
    private List<RekognitionFeature> getTopFeatures(Map<String, Integer> FeaturesAndCounter, int amount) {
        List<RekognitionFeature> topFeatures = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : FeaturesAndCounter.entrySet()) {
            RekognitionFeature rekognitionFeature = new RekognitionFeature(entry.getKey(), 0, entry.getValue());
            topFeatures.add(rekognitionFeature);
        }
        List<RekognitionFeature> topFeaturesSorted = new ArrayList<>();
        int minSize = Math.min(FeaturesAndCounter.size(), amount);
        for (int index = 0; index < minSize; index++) {
            topFeaturesSorted.add(topFeatures.get(index));
        }
        return topFeaturesSorted;
    }
}
