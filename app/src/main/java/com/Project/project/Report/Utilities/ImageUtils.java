package com.Project.project.Report.Utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.Project.project.RekognitionManagment.RekognitionFeature;
import com.Project.project.Utilities.UserChosenPicture;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ImageUtils {
    public static List<UserChosenPicture> getChosenPictures
            (List<ParseObject> foundPicturesParseObjectsIds,
             Map<String, List<ParseObject>> imageIdToRekognitionParseObjectMap,
             Map<String, ParseObject> imageIdToGPSParseObjectMap) {
        List<UserChosenPicture> userChosenPictureList =
                foundPicturesParseObjectsIds.stream()
                        .map(foundPicturesParseObject -> {
                            System.out.println("$$$"+foundPicturesParseObject);
                                    Date date = foundPicturesParseObject.getCreatedAt();
                                    String strDate = DateUtils.convertDateToString(date);

                                    String encodedImage = foundPicturesParseObject.getString("raw_picture");
                                    Bitmap decodedImage = getDecodedPicture(encodedImage);

                                    String imageId = foundPicturesParseObject.getObjectId();
                                    List<ParseObject> rekognitionParseObjectList =
                                            imageIdToRekognitionParseObjectMap.getOrDefault(imageId, null);
                                    List<RekognitionFeature> rekognitionFeatures =
                                            RekognitionFeaturesUtils.getRekognitionFeatures(rekognitionParseObjectList);
                                    String semanticPlace;
                                    try {
                                        semanticPlace = imageIdToGPSParseObjectMap.
                                                get(foundPicturesParseObject.getObjectId()).getString("semantic_context");
                                    } catch (Exception e) {
                                        e.toString();
                                        semanticPlace = null;
                                    }
                                    return new UserChosenPicture(decodedImage, strDate, rekognitionFeatures, semanticPlace);
                                }
                        ).collect(Collectors.toList());

        return userChosenPictureList;
    }


    /**
     * Fetching rows from Picture table in db that connected to questionnaires.
     *
     * @param questionnaireIds - questionnaire's ids that might have pictures.
     * @return list of ParseObjects that found in table Picture.
     */
    public static List<ParseObject> fetchPictuesObjects(List<String> questionnaireIds,
                                                        Map<String, ParseObject>
                                                                questionnaireIdToImageParseObjectMap) {
        return questionnaireIds.stream()
                .map(questionnaireId ->
                        questionnaireIdToImageParseObjectMap.get(questionnaireId))
                .collect(Collectors.toList());
    }


    /**
     * Fetch from ParseObjects encoded images and decoded it.
     *
     * @param gpsParseObjects - list of ParseObjects which contatins encoded images.
     * @return decoded images.
     */
    public static List<Bitmap> getBitmapForGpsParseObjects(
                               List<ParseObject> gpsParseObjects,
                               Map<String, ParseObject> questionnaireIdToImageParseObjectMap) {
        List<Bitmap> bitmapList = new ArrayList<>();

        for (int i = 0; i < gpsParseObjects.size(); i++) {
            String questionnaireId = gpsParseObjects.get(i).getString("questionnaire");
            ParseObject bitmapParseObject = questionnaireIdToImageParseObjectMap.get(questionnaireId);
            // Not found in db.
            if (bitmapParseObject == null) {
                bitmapList.add(null);
            }
            // Found in db.
            else {
                String locationPictureAddress = bitmapParseObject.getString("raw_picture");
                Bitmap bitmap = getDecodedPicture(locationPictureAddress);
                bitmapList.add(bitmap);
            }
        }
        return bitmapList;
    }

    /**
     * Decoding a picture.
     *
     * @param encodedPicture - picture from user's questionnaire, which encoded.
     * @return decoded picture.
     */
    public static Bitmap getDecodedPicture(String encodedPicture) {
        byte[] decodedStringByteArray = Base64.decode(encodedPicture.getBytes(), 0);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedStringByteArray, 0, decodedStringByteArray.length);
        return bitmap;

    }
}
