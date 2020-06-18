package com.Project.project.Report.Utilities;

import android.content.Context;

import com.Project.project.GPS.LocationProperties;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class LocationUtilTest {
    Context context = mock(Context.class);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFetchLocationProperties() {
        List<ParseObject> topLocationsParseObjects = new ArrayList<>();
        ParseObject gpsParseObject = new ParseObject("Test");
        gpsParseObject.put("address", "Shlome Ha meleh");
        gpsParseObject.put("latitude", 34.12);
        gpsParseObject.put("longitude", 34.12);
        gpsParseObject.put("semantic_context", "Home");
        gpsParseObject.put("questionnaire", "1234");
        topLocationsParseObjects.add(gpsParseObject);
        List<LocationProperties> output = LocationUtils.fetchLocationProperties
                (topLocationsParseObjects);

        LatLng latLng = new LatLng(34.12, 34.12);
        LocationProperties expectedProperties = new LocationProperties("1234",
                latLng, "Shlome Ha meleh", "Home");
        List<LocationProperties> expected = new ArrayList<>();
        expected.add(expectedProperties);
        LatLng expectedLatlng = expected.get(0).getLatLng();
        LatLng outputLatlng = output.get(0).getLatLng();
        Assert.assertEquals(expectedLatlng, outputLatlng);
        Assert.assertTrue(expected.get(0).getSentimentPlace().equals(output.get(0).getSentimentPlace()));
        Assert.assertTrue(expected.get(0).getAddress().equals(output.get(0).getAddress()));
        Assert.assertTrue(expected.get(0).getQuestionnaireId().equals(output.get(0).getQuestionnaireId()));
    }

    @Test
    public void testFetchLocationsOfQuestionnairesIDS() {
        List<String> topLocationsQuestionnaireIds = new ArrayList<>();
        topLocationsQuestionnaireIds.add("1234");
        Map<String, ParseObject> questionnaireIdToGPSParseObject = new HashMap<>();
        ParseObject parseObject = new ParseObject("test");
        List<ParseObject> expected = new ArrayList<>();
        expected.add(parseObject);
        questionnaireIdToGPSParseObject.put("1234", parseObject);
        List<ParseObject> output = LocationUtils.fetchLocationsOfQuestionnairesIDS(topLocationsQuestionnaireIds,
                questionnaireIdToGPSParseObject);
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testFetchLocationsOfQuestionnairesIDSNull() {
        List<String> topLocationsQuestionnaireIds = new ArrayList<>();
        topLocationsQuestionnaireIds.add("1234");
        Map<String, ParseObject> questionnaireIdToGPSParseObject = new HashMap<>();
        ParseObject parseObject = null;
        List<ParseObject> expected = new ArrayList<>();
        questionnaireIdToGPSParseObject.put("1234", parseObject);
        List<ParseObject> output = LocationUtils.fetchLocationsOfQuestionnairesIDS(topLocationsQuestionnaireIds,
                questionnaireIdToGPSParseObject);
        Assert.assertEquals(expected, output);
    }


    @Test
    public void testCreateLocationPropertiesNullCheck() {
        ParseObject gpsParseObject = null;
        LocationProperties output = LocationUtils.createLocationProperties(gpsParseObject);
        Assert.assertNull(output);
    }

    @Test
    public void testCreateLocationPropertiesCheck() {
        ParseObject gpsParseObject = new ParseObject("Questionnaire");
        gpsParseObject.put("address", "Shlome Ha meleh");
        gpsParseObject.put("latitude", "34.12");
        gpsParseObject.put("longitude", "34.12");
        gpsParseObject.put("semantic_context", "Home");
        gpsParseObject.put("questionnaire", "1234");
        LocationProperties output = LocationUtils.createLocationProperties(gpsParseObject);

        LatLng latLng = new LatLng(34.12, 34.12);
        LocationProperties expectedProperties = new LocationProperties("1234",
                latLng, "Shlome Ha meleh", "Home");
    }


}