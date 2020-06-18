package com.Project.project.Report.Utilities;

import com.Project.project.GPS.LocationProperties;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class LocationUtils {
    /**
     * Finds user's locations as ParserObjects and convert them the LocationProperties object.
     *
     * @param topLocationsParseObjects
     * @return
     */
    public static List<LocationProperties> fetchLocationProperties(List<ParseObject> topLocationsParseObjects) {
        // Get properties & Creates new LocationProperties objects list.
        List<LocationProperties> userLocationsProperties = createLocations(topLocationsParseObjects);
        return userLocationsProperties;
    }

    /**
     * Generates LocationProperties objects from ParseObjects.
     *
     * @param gpsParseObjects
     * @return
     */
    private static List<LocationProperties> createLocations(List<ParseObject> gpsParseObjects) {
        List<LocationProperties> locationPropertiesList = new ArrayList<>();
        for (int i = 0; i < gpsParseObjects.size(); i++) {
            String address = gpsParseObjects.get(i).getString("address");
            double latitude = gpsParseObjects.get(i).getDouble("latitude");
            double longitude = gpsParseObjects.get(i).getDouble("longitude");
            String semanticContext = gpsParseObjects.get(i).getString("semantic_context");
            String questionnaireId = gpsParseObjects.get(i).getString("questionnaire");
            LatLng latLng = new LatLng(latitude, longitude);
            LocationProperties currentLocationProperties =
                    new LocationProperties(questionnaireId, latLng, address, semanticContext);
            locationPropertiesList.add(currentLocationProperties);

        }
        return locationPropertiesList;
    }

    /**
     * Fetch from GPS table all the fields that belongs to relevant Questionnaire's ids.
     *
     * @param topLocationsQuestionnaireIds
     * @param questionnaireIdToGPSParseObject
     * @return
     */
    public static List<ParseObject> fetchLocationsOfQuestionnairesIDS(
            List<String> topLocationsQuestionnaireIds,
            Map<String, ParseObject> questionnaireIdToGPSParseObject) {
        return topLocationsQuestionnaireIds.stream()
                .map(questionnaireId -> questionnaireIdToGPSParseObject.get(questionnaireId))
                .filter(parseObject -> parseObject != null)
                .collect(Collectors.toList());
    }


    public static LocationProperties createLocationProperties(ParseObject gpsParseObject) {
        if (gpsParseObject == null)
            return null;

        String address = gpsParseObject.getString("address");
        double latitude = gpsParseObject.getDouble("latitude");
        double longitude = gpsParseObject.getDouble("longitude");
        String semanticContext = gpsParseObject.getString("semantic_context");
        String questionnaireId = gpsParseObject.getString("questionnaire");
        LatLng latLng = new LatLng(latitude, longitude);
        LocationProperties currentLocationProperties = new LocationProperties(questionnaireId,
                latLng, address, semanticContext);

        return currentLocationProperties;
    }

}
