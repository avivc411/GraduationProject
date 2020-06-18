package com.Project.project.DB;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import com.Project.project.Utilities.ServerInfo;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * Used to upload data to the DB. Implements singleton.
 */
public class DataUploader {
    private static DataUploader instance;

    private DataUploader(Context context) {
        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId(ServerInfo.GetServerInfoID())
                // if defined
                .clientKey(ServerInfo.GetServerInfoKey())
                .server(ServerInfo.GetServerInfoLink())
                .build()
        );
    }

    public static DataUploader getInstance(Context context) {
        if (instance == null)
            instance = new DataUploader(context);
        return instance;
    }

    /**
     * Upload an image as an array of bytes.
     *
     * @param image           An image as a Bitmap - will be converted to byte array.
     * @param questionnaireID The questionnaire the image has been taken for.
     * @return Image id in the DB.
     */
    public String uploadImage(Bitmap image, String questionnaireID) {
        ParseObject picture = new ParseObject("Picture");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedPicture = Base64.encodeToString(byteArray, Base64.DEFAULT);

        picture.put("raw_picture", encodedPicture);
        picture.put("questionnaire", questionnaireID);
        try {
            picture.save();
            return picture.getObjectId();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Upload the features of an image that has been analyzed.
     *
     * @param features Map of feature's name to it's confidence.
     * @param imageID  The image id in the DB of the image that has been analyzed.
     * @return Success of failure.
     */
    public boolean uploadImageFeatures(Map<String, Float> features, String imageID) {
        for (String featureName : features.keySet()) {
            ParseObject feature = new ParseObject("Feature");
            feature.put("feature", featureName);
            feature.put("confidence", features.get(featureName));
            feature.put("imageID", imageID);
            try {
                feature.save();
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * Upload a questionnaire answers to the DB - list of ratings to questions (fixed order in the db).
     *
     * @param ratings List of ratings.
     * @return Questionnaire id in the DB.
     */
    public String uploadQuestionnaire(List<Integer> ratings) {
        ParseObject questionnaire = new ParseObject("Questionnaire");
        int i = 0;
        for (int rating : ratings) {
            questionnaire.put("quest_" + (++i), rating);
        }
        try {
            questionnaire.save();
            return questionnaire.getObjectId();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Upload a gps coordinates, address and semantic context to the DB.
     *
     * @param latitude        The latitude coordinate of the gps record.
     * @param longitude       The longitude coordinate of the gps record.
     * @param address         The address of the location that is represented by the coordinates.
     * @param semanticContext The context of the given location, if has any.
     * @param questionnaireID The id of the related questionnaire.
     * @return GPS record id in the DB.
     */
    public String uploadGpsDetails(double latitude, double longitude, String address,
                                   String semanticContext, String questionnaireID) {
        ParseObject gpsRecord = new ParseObject("GPS");
        gpsRecord.put("latitude", latitude);
        gpsRecord.put("longitude", longitude);
        gpsRecord.put("address", address);
        gpsRecord.put("questionnaire", questionnaireID);
        if (semanticContext != null)
            gpsRecord.put("semantic_context", semanticContext);
        try {
            gpsRecord.save();
            return gpsRecord.getObjectId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Upload an app usage data in 24 hours before sending a questionnaire.
     *
     * @param appsUsage       Map of app's name to usage time in HH:MM:SS.
     * @param questionnaireID The id of the related questionnaire.
     * @return All uploads succeeded.
     */
    public boolean uploadUsageDetails(Map<String, String> appsUsage, String questionnaireID) {
        if (appsUsage == null || questionnaireID == null || questionnaireID.equals(""))
            return false;
        for (Map.Entry<String, String> pair : appsUsage.entrySet()) {
            ParseObject usageRecord = new ParseObject("Usage");
            usageRecord.put("Name", pair.getKey());
            usageRecord.put("Time", pair.getValue());
            usageRecord.put("questionnaire", questionnaireID);
            try {
                usageRecord.save();
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
