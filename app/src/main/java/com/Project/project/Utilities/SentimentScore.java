package com.Project.project.Utilities;

/**
 * Contains the sentiment score summary from user's questionnaires.
 */
public class SentimentScore {

    private int amountOfQuestionnaires;
    private int totalScore;
    private int maxPossibleTotalScore;
    private int average;
    private int maxPossibleAverage;

    public SentimentScore(int amount, int totalScore, int maxPossibleTotalscore, int average, int maxPossibleAverage) {
        this.amountOfQuestionnaires = amount;
        this.totalScore = totalScore;
        this.maxPossibleTotalScore = maxPossibleTotalscore;
        this.average = average;
        this.maxPossibleAverage = maxPossibleAverage;
    }

    /**
     * Getters and Setters
     *
     * @return
     */

    public int getAmountOfQuestionnaires() {
        return amountOfQuestionnaires;
    }

    public void setAmountOfQuestionnaires(int amountOfQuestionnaires) {
        this.amountOfQuestionnaires = amountOfQuestionnaires;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getMaxPossibleTotalScore() {
        return maxPossibleTotalScore;
    }

    public void setMaxPossibleTotalScore(int maxPossibleTotalScore) {
        this.maxPossibleTotalScore = maxPossibleTotalScore;
    }

    public int getAverage() {
        return average;
    }

    public void setAverage(int average) {
        this.average = average;
    }

    public int getMaxPossibleAverage() {
        return maxPossibleAverage;
    }

    public void setMaxPossibleAverage(int maxPossibleAverage) {
        this.maxPossibleAverage = maxPossibleAverage;
    }
}
