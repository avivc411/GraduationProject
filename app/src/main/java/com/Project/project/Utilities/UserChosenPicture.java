package com.Project.project.Utilities;

import android.graphics.Bitmap;

import com.Project.project.RekognitionManagment.RekognitionFeature;

import java.util.List;

/**
 * Responsible for information regarded to chosen pictures that displaying on user's screen.
 */
public class UserChosenPicture {
    Bitmap picture;
    String date;
    List<RekognitionFeature> rekognitionFeatures;
    String semanticPlace;

    public UserChosenPicture(Bitmap picture, String date, List<RekognitionFeature> rekognitionFeatures, String semanticPlace) {
        this.picture = picture;
        this.date = date;
        this.rekognitionFeatures = rekognitionFeatures;
        this.semanticPlace = semanticPlace;
    }

    public String getSemanticPlace() {
        return semanticPlace;
    }

    public void setSemanticPlace(String semanticPlace) {
        this.semanticPlace = semanticPlace;
    }

    public List<RekognitionFeature> getRekognitionFeatures() {
        return rekognitionFeatures;
    }

    public void setRekognitionFeatures(List<RekognitionFeature> rekognitionFeatures) {
        this.rekognitionFeatures = rekognitionFeatures;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
