package com.Project.project.GPS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPS {
    private static GPS instance;
    Location gps_loc, network_loc, final_loc;
    double longitude, latitude;
    Boolean LocationFound = false;
    String userCountry, userAddress;
    LocationManager locationManager;
    private Context context;
    private Handler mHandler = new Handler();
    /**
     * try every 1 sec to get location, if not found, recursion loop until found.
     */
    private Runnable updateTask = new Runnable() {
        public void run() {
            gps_loc = getGPSlocation(locationManager);
            network_loc = getNetworklocation(locationManager);
            Boolean check = checkLocation();
            if (check) {
                try {
                    LocationFound = true;
                    synchronized (this) {
                        getAddress();
                    }
//                    Toast.makeText(context, "Location Found - address:" + userCountry + "  " + userAddress + "  " + latitude + " ," + longitude, Toast.LENGTH_LONG).show();
                    System.out.println("Location Found - address:" + userCountry + "  " + userAddress + "  " + latitude + " ," + longitude);

                } catch (Exception e) {
                    Toast.makeText(context, " error found! " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            } else {
                mHandler.postDelayed(updateTask, 1000);
            }
        }
    };

    /**
     * class to create GPS constructor and start searching for gps location
     *
     * @param context
     */
    public GPS(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // check android permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // call updateTask after 5 seconds
        mHandler.postDelayed(updateTask, 1000);
    }

    public static GPS getInstance(Context context) {
        if (instance == null)
            instance = new GPS(context);
        return instance;
    }

    /**
     * check if location found
     *
     * @return true if location found, else false
     */
    public boolean checkLocation() {
        if (gps_loc != null) {
            final_loc = gps_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
            return true;
        } else if (network_loc != null) {
            final_loc = network_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
            return true;
        } else {
            latitude = 0.0;
            longitude = 0.0;
            return false;
        }
    }

    /**
     * update location of latitude and altitude by string text - country+address
     *
     * @throws IOException
     */
    private void getAddress() throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        if (addresses != null && addresses.size() > 0) {
            userCountry = addresses.get(0).getCountryName();
            userAddress = addresses.get(0).getAddressLine(0);
        }
    }

    @SuppressLint("MissingPermission")
    private Location getGPSlocation(LocationManager locationManager) {
        gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return gps_loc;
    }

    @SuppressLint("MissingPermission")
    private Location getNetworklocation(LocationManager locationManager) {
        network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return network_loc;
    }

    /**
     * @return location by string
     */
    public String getAddressString() {
        if (LocationFound)
            return (userCountry + ", " + userAddress);
        else
            return ("Location not found");
    }

    /**
     * get latitude and longitude
     *
     * @return longitude, latitude
     */

    public double getLongitude() {
        if (LocationFound)
            return longitude;
        else
            return 34.7913;
    }

    public void setLongitude(double longitude) {
        if (LocationFound)
            this.longitude = longitude;
    }

    public double getLatitude() {
        if (LocationFound)
            return latitude;
        else
            return 31.25181;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}