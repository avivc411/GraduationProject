package com.Project.project.Report.Utilities;

import com.Project.project.Activities.QuestionnaireActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resopnsible for handling information regarded to radar chart of
 * RekognitionEmotionsChartActivity.
 */
public class RekognitionEmotionsData {
    // Mapping feature name --> Question: Average answers.
    // e.g. Radio --> <<Upset:2><Hostile:3>>
    Map<String, Map<String, Integer>> rekognitionFeatureAverageScores;
    // Chart labels.
    String[] labels;

    // Constructor.
    public RekognitionEmotionsData(Map<String, Map<String, Integer>> rekognitionFeatureAverageScore) {
        this.rekognitionFeatureAverageScores =
                rekognitionFeatureAverageScore;
        labels = setLabels();
    }

    public Map<String, Map<String, Integer>> getRekognitionFeatureAverageScores() {
        return rekognitionFeatureAverageScores;
    }

    public void setRekognitionFeatureAverageScores(Map<String, Map<String, Integer>>
                                                           rekognitionFeatureAverageScores) {
        this.rekognitionFeatureAverageScores = rekognitionFeatureAverageScores;
        // Converting the questions to their original names. e.g. quest_1 --> Upset.
        this.rekognitionFeatureAverageScores = convertQuestionNames(rekognitionFeatureAverageScores);
    }

    public String[] getLabels() {
        return labels;
    }

    /**
     * Get list of features.
     * @return list of user's features.
     */
    public List<String> getFeatures() {
        List<String> features = new ArrayList<>();
        rekognitionFeatureAverageScores.forEach((feature, v) -> {
            features.add(feature);
        });
        return features;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }

    /**
     * Converting the questions names as it shows in db to original names.
     * e.g. Radio -> <quest_1, 3> convert to: Radio -> <Upset, 3>.
     * @param rekognitionFeatureAverageScore
     * @return Map<Feature name, Map<question original name, average score>>.
     */
    private Map<String, Map<String, Integer>> convertQuestionNames
            (Map<String, Map<String, Integer>> rekognitionFeatureAverageScore) {
        // Return output.
        Map<String, Map<String, Integer>> rekognitionFeatureAverageScoreFixedQuestionsNames
                = new HashMap<>();
        // Map of questions in db --> original questions names.
        Map<String, String> questionNameInDbToOriginalName =
                QuestionnaireActivity.getQuestionNamesInDbToOriginalNames();

        rekognitionFeatureAverageScore.forEach((k, v) -> {
            Map<String, Integer> questionToScore = new HashMap<>();
            v.forEach((kk, vv) -> {
                String questionName = questionNameInDbToOriginalName.get(kk);
                questionToScore.put(questionName, vv);
            });
            rekognitionFeatureAverageScoreFixedQuestionsNames.put(k, questionToScore);
        });
        return rekognitionFeatureAverageScoreFixedQuestionsNames;
    }

    /**
     * Setting labels for radar chart.
     * @return chart's labels.
     */
    private String[] setLabels() {
        labels = new String[]{"Upset", "Hostile", "Alert", "Ashamed", "Inspired",
                "Nervous", "Determined", "Attentive", "Afraid", "Active"};
        return labels;
    }
}
