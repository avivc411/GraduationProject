package com.Project.project.GPS;

import java.util.Date;

/**
 * Responsible for containing data for Semantic chart.
 */
public class SemanticMoodData {
    public SemanticMoodData(String qustionnaireId, Date date, String semanticPlace, int mood) {
        this.qustionnaireId = qustionnaireId;
        this.date = date;
        this.semanticPlace = semanticPlace;
        this.mood = mood;
    }

    String qustionnaireId;
    Date date;

    public String getQustionnaireId() {
        return qustionnaireId;
    }

    public void setQustionnaireId(String qustionnaireId) {
        this.qustionnaireId = qustionnaireId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSemanticPlace() {
        return semanticPlace;
    }

    public void setSemanticPlace(String semanticPlace) {
        this.semanticPlace = semanticPlace;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    String semanticPlace;
    int mood;
}


