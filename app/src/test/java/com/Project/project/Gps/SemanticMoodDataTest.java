package com.Project.project.Gps;

import android.content.Context;
import android.location.LocationManager;

import com.Project.project.GPS.GPS;
import com.Project.project.GPS.SemanticMoodData;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class SemanticMoodDataTest {
    Context context = mock(Context.class);
    Date d = new Date();
    SemanticMoodData semanticMoodData = new SemanticMoodData("1234",
            d,"Gym",8);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void defaultSettings(){
       semanticMoodData.setDate(d);
       semanticMoodData.setMood(8);
       semanticMoodData.setQustionnaireId("1234");
       semanticMoodData.setSemanticPlace("Gym");
    }

    @Test
    public void testGetQustionnaireId() {
        Assert.assertEquals("1234",semanticMoodData.getQustionnaireId());
    }

    @Test
    public void testSetQustionnaireId() {
        semanticMoodData.setQustionnaireId("2222");
        Assert.assertEquals("2222",semanticMoodData.getQustionnaireId());
    }

    @Test
    public void testGetDate() {
        Assert.assertEquals(d,semanticMoodData.getDate());
    }

    @Test
    public void testSetDate() {
        Date date = new Date();
        semanticMoodData.setDate(date);
        Assert.assertEquals(date,semanticMoodData.getDate());
    }

    @Test
    public void testGetSemanticPlace() {
        Assert.assertEquals("Gym",semanticMoodData.getSemanticPlace());
    }

    @Test
    public void testSetSemanticPlace() {
        semanticMoodData.setSemanticPlace("Home");
        Assert.assertEquals("Home",semanticMoodData.getSemanticPlace());
    }

    @Test
    public void testGetMood() { Assert.assertEquals(8,semanticMoodData.getMood());
    }

    @Test
    public void testSetMood() {
        semanticMoodData.setMood(2);
        Assert.assertEquals(2,semanticMoodData.getMood());
    }
}