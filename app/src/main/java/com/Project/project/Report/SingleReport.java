package com.Project.project.Report;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.Project.project.GPS.LocationProperties;
import com.Project.project.RekognitionManagment.RekognitionFeature;
import com.Project.project.UsageManagment.UsageProperties;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Respnsible for containing single report information.
 */
public class SingleReport implements Parcelable {
    public static final Creator<SingleReport> CREATOR = new Creator<SingleReport>() {
        @Override
        public SingleReport createFromParcel(Parcel in) {
            return new SingleReport(in);
        }

        @Override
        public SingleReport[] newArray(int size) {
            return new SingleReport[size];
        }
    };
    Date date;
    Map<String, Integer> userAnswers;
    List<RekognitionFeature> rekognitionFeatures;
    LocationProperties userLocationProperties;
    Bitmap picture;
    UsageProperties usageProperties;

    public SingleReport(Date date, Map<String, Integer> userAnswers,
                        List<RekognitionFeature> rekognitionFeatures,
                        LocationProperties userLocationProperties,
                        Bitmap picture, UsageProperties usageProperties) {
        this.date = date;
        this.userAnswers = userAnswers;
        this.rekognitionFeatures = rekognitionFeatures;
        this.userLocationProperties = userLocationProperties;
        this.picture = picture;
        this.usageProperties = usageProperties;
    }

    protected SingleReport(Parcel in) {
        userLocationProperties = in.readParcelable(LocationProperties.class.getClassLoader());
        picture = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public LocationProperties getUserLocationProperties() {
        return userLocationProperties;
    }

    public void setUserLocationProperties(LocationProperties userLocationProperties) {
        this.userLocationProperties = userLocationProperties;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public UsageProperties getUsageProperties() {
        return usageProperties;
    }

    public void setUsageProperties(UsageProperties usageProperties) {
        this.usageProperties = usageProperties;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(userLocationProperties, i);
        parcel.writeParcelable(picture, i);
    }
}
