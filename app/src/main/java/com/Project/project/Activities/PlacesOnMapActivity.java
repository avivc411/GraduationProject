package com.Project.project.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.Project.project.GPS.LocationProperties;
import com.Project.project.R;
import com.Project.project.Report.UserReportProcessor;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class PlacesOnMapActivity extends FragmentActivity implements OnMapReadyCallback {

    GroundOverlayOptions groundOverlayOptions;
    GroundOverlay imageOverlay;
    private GoogleMap mMap;
    private List<LocationProperties> locationpropList;
    private List<Bitmap> userPicturesList;
    private List<Integer> questionnaireScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_on_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        System.out.println(" begin maps activity: ");

        Intent intent = getIntent();
        System.out.println(" got maps activity: ");
        ArrayList<Parcelable> places = intent.getParcelableArrayListExtra("places");
        System.out.println(" got intent places: " + places.size());


        locationpropList = addLocations(places);
        questionnaireScore = fetchMood();
        // CANCELED FOR NOW
        userPicturesList = setImages(UserReportActivity.userLocationPictures);
        //userPicturesList = setImages(pictures);
    }

    /**
     * Fetching the mood for specific questionnaire that connected to location.
     * @return questionnaire's mood.
     */
    private List<Integer> fetchMood() {
        List<Integer> questionnaireMood = new ArrayList<>();
        for (int i = 0; i < locationpropList.size(); i++) {
            int locationMood = UserReportProcessor.getQuestionnaireIdToMoodScore
                    (locationpropList.get(i).getQuestionnaireId());
            questionnaireMood.add(locationMood);
        }
        return questionnaireMood;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final List<LatLng> latLngList = getLatLngFromList(locationpropList);
        setCirclesToLatlngs(latLngList);
        final List<Marker> markerList = getMarkersFromList(locationpropList);


        LatLngBounds.Builder builder = LatLngBounds.builder();
        setLatToBuilder(builder, latLngList);

        final LatLngBounds bounds = builder.build();
        final CameraUpdate initialCameraBounds = CameraUpdateFactory.newLatLngBounds(bounds, 0);

        startShowingOnMap(this, markerList, latLngList, initialCameraBounds);


        try {
            mMap.animateCamera(initialCameraBounds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starting the tour over user's locations.
     *
     * @param markerList
     * @param latLngList
     * @param initialCameraBounds
     */
    private void startShowingOnMap(Context context, final List<Marker> markerList,
                                   final List<LatLng> latLngList,
                                   final CameraUpdate initialCameraBounds) {
        // delays time. Current limit to 5 pictures at most.
        int[] delays = {3500, 8000, 12500, 16500, 20500};

        // Displaying every point.
        int lastTimeIndex = 0;
        for (int i = 0; i < locationpropList.size() && i < 5; i++) {
            lastTimeIndex = i;
            final int finalI = i;
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            Log.i("tag", "showing point");
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngList.get(finalI)), 2000, null);
                            mMap.setBuildingsEnabled(true);
                            mMap.setMinZoomPreference(17.0f);
                            mMap.setMaxZoomPreference(17.0f);

                            markerList.get(finalI).showInfoWindow();

                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            /**
                                             Bitmap bitmap;
                                             bitmap=  BitmapFactory.decodeResource(getResources(), R.drawable.mk);
                                             bitmap=toGrayscale(bitmap);
                                             **/
                                            // CANCELEDE FOR NOW
                                            Bitmap bitmap = userPicturesList.get(finalI);
                                            //Bitmap bitmap = null;
                                            /**
                                             //Bitmap bitmap=null;
                                             //Bitmap bitmap = null;
                                             Bitmap bitmap = null;
                                             try {
                                             // bitmap = BitmapFactory.decodeStream(context.openFileInput("myImage"));
                                             } catch (FileNotFoundException e) {
                                             e.printStackTrace();
                                             }
                                             **/
                                            System.out.println("bitmap ****" + bitmap);
                                            if (bitmap != null) {

                                                BitmapDescriptorFactory.fromBitmap(bitmap);
                                                groundOverlayOptions = new GroundOverlayOptions()
                                                        // .image(BitmapDescriptorFactory.fromResource(R.drawable.mk))
                                                        .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                                                        .position(latLngList.get(finalI), 300f, 200f)
                                                        .transparency(0.5f);
                                                imageOverlay = mMap.addGroundOverlay(groundOverlayOptions);
                                            }
                                        }
                                    }, 3000);
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            if (imageOverlay != null)
                                                imageOverlay.setVisible(false);
                                        }
                                    }, 5000);

                        }
                    },
                    delays[i]);
        }
        // 3 more seconds.
        int nextTime = delays[lastTimeIndex] + 4000;
        // Displaying all from zoom out.
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Log.i("tag", "ALL");
                        mMap.animateCamera(initialCameraBounds, 4000, null);
                        mMap.setMinZoomPreference(0);

                    }
                },
                nextTime);
        nextTime = nextTime + 7000;
        // Closing activity.
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Log.i("tag", "Closing");
                        finish();
                    }
                },
                nextTime);
    }

    /**
     * Adding all the places to the builder - to create boundary.
     *
     * @param builder
     * @param latLngList
     */
    private void setLatToBuilder(LatLngBounds.Builder builder, List<LatLng> latLngList) {
        for (int i = 0; i < latLngList.size(); i++) {
            builder.include(latLngList.get(i));
        }
    }

    /**
     * Setting markers on the map.
     *
     * @param locationpropList
     * @return
     */
    private List<Marker> getMarkersFromList(List<LocationProperties> locationpropList) {
        List<Marker> markerList = new ArrayList<>();

        for (int i = 0; i < locationpropList.size(); i++) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(locationpropList.get(i).getLatLng())
                    .title(locationpropList.get(i).getSentimentPlace() +" , mood score: " +
                            questionnaireScore.get(i))
                    .snippet(locationpropList.get(i).getAddress()));
            markerList.add(marker);
        }
        return markerList;
    }

    /**
     * Adding circles on the map over user's locations.
     *
     * @param latLngList
     */
    private void setCirclesToLatlngs(List<LatLng> latLngList) {
        for (int i = 0; i < latLngList.size(); i++) {
            mMap.addCircle(new CircleOptions()
                    .center(latLngList.get(i))
                    .radius(25)
                    .strokeColor(Color.RED)
                    .fillColor(Color.WHITE));
        }
    }

    /**
     * Creates all the Latlng.
     *
     * @param locationpropList
     * @return
     */
    private List<LatLng> getLatLngFromList(List<LocationProperties> locationpropList) {
        List<LatLng> latLngList = new ArrayList<>();
        for (int i = 0; i < locationpropList.size(); i++) {
            latLngList.add(locationpropList.get(i).getLatLng());
        }
        return latLngList;
    }

    /**
     * Setting locations list as LocationProperties objects.
     *
     * @param place
     * @return
     */
    private List<LocationProperties> addLocations(ArrayList<Parcelable> place) {
        List<LocationProperties> locationPropertiesListFromUser = new ArrayList<>();
        for (int i = 0; i < place.size(); i++) {
            LocationProperties currentLocation = (LocationProperties) place.get(i);
            locationPropertiesListFromUser.add(currentLocation);
        }
        return locationPropertiesListFromUser;
    }


    /**
     * Setting the user's images.
     *
     * @param pictures
     * @return
     */
    private List<Bitmap> setImages(ArrayList<Parcelable> pictures) {
        List<Bitmap> bitmapList = getImagesDecoded(pictures);
        List<Bitmap> grayScaleBitmapListToReturn = new ArrayList<>();

        for (int i = 0; i < bitmapList.size(); i++) {
            if (bitmapList.get(i) == null) {
                grayScaleBitmapListToReturn.add(null);
            } else {
                Bitmap bitmap;
                bitmap = toGrayscale(bitmapList.get(i));
                grayScaleBitmapListToReturn.add(bitmap);
            }
        }
        return grayScaleBitmapListToReturn;
    }

    private List<Bitmap> setImages(List<Bitmap> userLocationPictures) {
        List<Bitmap> grayScaleBitmapListToReturn = new ArrayList<>();
        for (int i = 0; i < userLocationPictures.size(); i++) {
            if (userLocationPictures.get(i) == null) {
                grayScaleBitmapListToReturn.add(null);
            } else {
                Bitmap bitmap;
                bitmap = toGrayscale(userLocationPictures.get(i));
                grayScaleBitmapListToReturn.add(bitmap);
            }
        }
        return grayScaleBitmapListToReturn;
    }

    /**
     * Decoded user's images.
     *
     * @param pictures
     * @return
     */
    private List<Bitmap> getImagesDecoded(ArrayList<Parcelable> pictures) {
        List<Bitmap> userPictures = new ArrayList<>();
        for (int i = 0; i < pictures.size(); i++) {
            Bitmap userCurrentPicture = (Bitmap) pictures.get(i);
            userPictures.add(userCurrentPicture);
        }
        return userPictures;
    }

    /**
     * Changing the color of user's images.
     *
     * @param bmpOriginal
     * @return
     */
    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }


}