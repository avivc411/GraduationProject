package com.Project.project.RekognitionManagment;

import android.content.Context;
import android.graphics.Bitmap;

import com.Project.project.UserManagment.AWSCognitoUserManager;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to upload and analyse an image to Rekognition.
 */
public class RekognitionImageUploader {
    private AmazonRekognitionClient rekognitionClient;

    /**
     * Connect to Rekognition, if not connected.
     *
     * @param context Context of the calling activity.
     */
    public void connect(Context context) {
        if (rekognitionClient == null) {
            rekognitionClient = new AmazonRekognitionClient(AWSCognitoUserManager.getInstance(context).getRekognitionIdentityPoolCredentials(context));
            rekognitionClient.setRegion(Region.getRegion(Regions.US_EAST_2));
        }
    }

    /**
     * Analyse an image. Creating request and run it on Rekognition in separated thread.
     *
     * @param context   Context of the calling activity.
     * @param imgBitmap Image representation in bitmap.
     * @return
     */
    public Map<String, Float> analyseImage(Context context, Bitmap imgBitmap) {
        try {
            connect(context);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);

            DetectLabelsRequest request = new DetectLabelsRequest()
                    .withImage(new Image()
                            .withBytes(byteBuffer))
                    .withMaxLabels(10)
                    .withMinConfidence(77F);

            Map<String, Float> features = new HashMap<>();
            Thread t = new Thread(() -> analyseImage(request, features));
            t.start();
            t.join();
            return features;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Send a request to Rekognition.
     *
     * @param request  Request to send.
     * @param features Map of feature to confidence.
     */
    private void analyseImage(DetectLabelsRequest request, Map<String, Float> features) {
        try {
            DetectLabelsResult result = rekognitionClient.detectLabels(request);
            List<Label> labels = result.getLabels();

            for (Label label : labels) {
                System.out.println(label.getName() + ": " + label.getConfidence().toString());
                features.put(label.getName(), label.getConfidence());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
