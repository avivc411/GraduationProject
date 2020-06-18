package com.Project.project.RekognitionManagment;

import java.util.Objects;

/**
 * Contain information about Rekognition feature object.
 */
public class RekognitionFeature {
    String featureName;
    double confidence;
    double distribution;

    /**
     * Constructor.
     *
     * @param featureName
     * @param confidence
     * @param distribution
     */
    public RekognitionFeature(String featureName, double confidence, double distribution) {
        this.featureName = featureName;
        this.confidence = confidence;
        this.distribution = distribution;
    }

    /**
     * Getters and Seters.
     *
     * @return
     */

    public double getDistribution() {
        return distribution;
    }

    public void setDistribution(double distribution) {
        this.distribution = distribution;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RekognitionFeature feature = (RekognitionFeature) o;
        return featureName.equals(feature.featureName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(featureName);
    }
}
