package com.Project.project.UsageManagment;

import android.content.Context;

import com.Project.project.UsageManagment.UsageProperties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class UsagePropertiesTest {
    Context context = mock(Context.class);
    Date date = new Date();
    Map<String, String> applicationAndTime = new HashMap<>();
    UsageProperties usageProperties = new UsageProperties(date,applicationAndTime);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        applicationAndTime.put("facebook","02:40:24");
    }

    @After
    public void defaultSettings() {
        usageProperties.setDate(date);
        usageProperties.setApplicationAndTime(applicationAndTime);
    }

    @Test
    public void testGetDate() {
        Assert.assertEquals(date,usageProperties.getDate());
    }

    @Test
    public void testSetDate() {
        Date newDate = new Date();
        usageProperties.setDate(newDate);
        Assert.assertEquals(newDate,usageProperties.getDate());
    }

    @Test
    public void testGetApplicationAndTime() {
        Assert.assertEquals(applicationAndTime,usageProperties.getApplicationAndTime());
    }

    @Test
    public void testSetApplicationAndTime() {
        Map<String, String> applicationAndTimenew = new HashMap<>();
        applicationAndTimenew.put("facebook","02:40:24");
        usageProperties.setApplicationAndTime(applicationAndTimenew);
        Assert.assertEquals(applicationAndTimenew,usageProperties.getApplicationAndTime());

    }

}