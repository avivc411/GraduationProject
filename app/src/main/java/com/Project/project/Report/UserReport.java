package com.Project.project.Report;

import android.graphics.Bitmap;

import com.Project.project.GPS.LocationProperties;
import com.Project.project.RekognitionManagment.RekognitionFeature;
import com.Project.project.UsageManagment.UsageProperties;
import com.Project.project.Utilities.SentimentScore;
import com.Project.project.Utilities.UserChosenPicture;

import java.util.List;
import java.util.Map;

/**
 * Contains relevant information for user report data.
 */
public abstract class UserReport {
    SentimentScore sentimentScore;
    String reportSummary;
    Map<String, Integer> userAnswers;
    List<RekognitionFeature> rekognitionFeatures;
    List<LocationProperties> userLocationProperties;
    List<Bitmap> locationPictures;
    List<UserChosenPicture> chosenPictures;
    List<UsageProperties> usageProperties;

    public UserReport(SentimentScore sentimentScore, String reportSummary,
                      Map<String, Integer> userAnswers,
                      List<RekognitionFeature> rekognitionFeatures,
                      List<LocationProperties> userLocationProperties,
                      List<Bitmap> locationPictures,
                      List<UserChosenPicture> chosenPictures,
                      List<UsageProperties> usageProperties) {
        this.sentimentScore = sentimentScore;
        this.reportSummary = reportSummary;
        this.userAnswers = userAnswers;
        this.rekognitionFeatures = rekognitionFeatures;
        this.userLocationProperties = userLocationProperties;
        this.locationPictures = locationPictures;
        this.chosenPictures = chosenPictures;
        this.usageProperties = usageProperties;
    }

    public List<UsageProperties> getUsageProperties() {
        return usageProperties;
    }

    public void setUsageProperties(List<UsageProperties> usageProperties) {
        this.usageProperties = usageProperties;
    }

    public List<LocationProperties> getUserLocationProperties() {
        return userLocationProperties;
    }

    public void setUserLocationProperties(List<LocationProperties> userLocationProperties) {
        this.userLocationProperties = userLocationProperties;
    }

    public List<UserChosenPicture> getChosenPictures() {
        return chosenPictures;
    }

    public void setChosenPictures(List<UserChosenPicture> chosenPictures) {
        this.chosenPictures = chosenPictures;
    }

    public List<Bitmap> getLocationPictures() {
        return locationPictures;
    }

    public void setLocationPictures(List<Bitmap> locationPictures) {
        this.locationPictures = locationPictures;
    }

    /**
     * Setters and Getters
     *
     * @return
     */
    public SentimentScore getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(SentimentScore sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public String getReportSummary() {
        reportSummary = getReportName();
        reportSummary += "\nSentiment score is: " + sentimentScore.getTotalScore() + " / " + sentimentScore.getMaxPossibleTotalScore();
        reportSummary += "\n";
        reportSummary += "Average sentiment score for questionnaire is: " + sentimentScore.getAverage() + " / " + sentimentScore.getMaxPossibleAverage();
        return reportSummary;
    }


    public Map<String, Integer> getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(Map<String, Integer> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public List<RekognitionFeature> getRekognitionFeatures() {
        return rekognitionFeatures;
    }

    public void setRekognitionFeatures(List<RekognitionFeature> rekognitionFeatures) {
        this.rekognitionFeatures = rekognitionFeatures;
    }

    protected abstract String getReportName();


    public enum ReportType {
        POSITIVE,
        NEGATIVE
    }
    protected abstract ReportType getReportType();
}

