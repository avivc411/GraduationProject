package com.Project.project.Report.Utilities;

import com.Project.project.RekognitionManagment.RekognitionFeature;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RekognitionFeaturesUtils {

    /**
     * Retrives all RekognitionFeatures from user's pictures.
     *
     * @param topQuestionnaires                    - top selected user's questionnaires.
     * @param questionnaireIdToImageParseObjectMap - map between questionnaire id --> Image.
     * @param imageIdToRekognitionParseObjectMap   - map between Image id --> Rekognition.
     * @return list of RekognitionFeature connected to user's pictures from topQuestionnaires.
     */
    public static List<RekognitionFeature> getUserRekognitionFeatures(List<String> topQuestionnaires,
                                                                      Map<String, ParseObject> questionnaireIdToImageParseObjectMap,
                                                                      Map<String, List<ParseObject>> imageIdToRekognitionParseObjectMap) {
        List<String> imageIds = new ArrayList<>();
        for (Map.Entry<String, ParseObject> entry : questionnaireIdToImageParseObjectMap.entrySet()) {
            if (topQuestionnaires.contains(entry.getKey())) {
                ParseObject imageParseObject = entry.getValue();
                imageIds.add(imageParseObject.getObjectId());
            }
        }
        List<ParseObject> rekognitionParseObjectList = new ArrayList<>();
        for (Map.Entry<String, List<ParseObject>> entry : imageIdToRekognitionParseObjectMap.entrySet()) {
            if (imageIds.contains(entry.getKey())) {
                rekognitionParseObjectList.addAll(entry.getValue());
            }
        }
        return getRekognitionFeatures(rekognitionParseObjectList);
    }


    /**
     * Retrives all RekognitionFeatures from user's pictures.
     *
     * @param userRekognitionData
     * @return
     */
    public static List<RekognitionFeature> getRekognitionFeatures(List<ParseObject> userRekognitionData) {
        if (userRekognitionData == null) {
            return null;
        }
        List<RekognitionFeature> rekognitionFeatures = new ArrayList<>();
        for (int dataRow = 0; dataRow < userRekognitionData.size(); dataRow++) {
            String featureName = userRekognitionData.get(dataRow).getString("feature");
            double featureConfidence = userRekognitionData.get(dataRow).getDouble("confidence");
            RekognitionFeature rekognitionFeature = new RekognitionFeature(featureName, featureConfidence, 0);
            rekognitionFeatures.add(rekognitionFeature);
        }
        return rekognitionFeatures;
    }


}
