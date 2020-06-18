package com.Project.project.Gps;

import android.content.Context;
import android.location.LocationManager;

import com.Project.project.GPS.GPS;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class GPSTest {
    Context context = mock(Context.class);
    GPS gps = GPS.getInstance(context);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void getRealAddress() {
        gps.setLatitude(31.2617762);
        gps.setLongitude(34.7963455);
        assertEquals(gps.getAddressString(), "Location not found");
    }

    @Test
    public void getAddressString() {
        assertEquals(gps.getAddressString(), "Location not found");
    }

    @Test
    public void getLongitude() {
        double longgps = gps.getLongitude();
        assertEquals(longgps, 34.7913, 0.1);
    }

    @Test
    public void getLatitude() {
        assertEquals(gps.getLatitude(), 31.25181, 1);
    }

    @Test
    public void isGpsOn() {
        final Context context = mock(Context.class);
        final LocationManager manager = mock(LocationManager.class);
        Mockito.when(context.getSystemService(Context.LOCATION_SERVICE)).thenReturn(manager);
        Mockito.when(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);
    }
}