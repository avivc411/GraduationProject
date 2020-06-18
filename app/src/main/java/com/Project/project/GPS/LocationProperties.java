package com.Project.project.GPS;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Responsible for handling information regarded to gps location.
 */

public class LocationProperties implements Parcelable {
    // Parcelable adjustments.
    public static final Creator<LocationProperties> CREATOR = new Creator<LocationProperties>() {
        @Override
        public LocationProperties createFromParcel(Parcel in) {
            return new LocationProperties(in);
        }

        @Override
        public LocationProperties[] newArray(int size) {
            return new LocationProperties[size];
        }
    };

    // Location's questionnaire.
    String questionnaireId;
    // Location coordinates.
    LatLng latLng;
    String address;
    // Semantic place. e.g. gym.
    String sentimentPlace;

    protected LocationProperties(Parcel in) {
        questionnaireId = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        address = in.readString();
        sentimentPlace = in.readString();
    }

    public String getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(String questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    // Constructor.
    public LocationProperties(String questionnaireId, LatLng latLng, String address,
                              String sentimentPlace) {
        this.questionnaireId = questionnaireId;
        this.latLng = latLng;
        this.address = address;
        this.sentimentPlace = sentimentPlace;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSentimentPlace() {
        return sentimentPlace;
    }

    public void setSentimentPlace(String sentimentPlace) {
        this.sentimentPlace = sentimentPlace;
    }


    /**
     * Functions from Parcelable in order to send this object via intents.
     *
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Define how to send this object between intents.
     *
     * @param parcel
     * @param i
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(questionnaireId);
        parcel.writeParcelable(latLng, i);
        parcel.writeString(address);
        parcel.writeString(sentimentPlace);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
