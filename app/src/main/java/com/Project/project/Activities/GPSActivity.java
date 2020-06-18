package com.Project.project.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.Project.project.DB.DataUploader;
import com.Project.project.GPS.GPS;
import com.Project.project.R;
import com.Project.project.UsageManagment.UsageRetriever;
import com.Project.project.Utilities.SentimentPlacesAlert;

public class GPSActivity extends AppCompatActivity implements SentimentPlacesAlert.SingleChoiceListener {
    TextView location, title;
    String questionnaireID;
    Button apply;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        connectButtonToXml();
//        runAnimation();
        Intent intent = getIntent();
        questionnaireID = intent.getStringExtra("rowId");
//        apply.setOnClickListener(view -> {
//            preventTwoClick(view);
//            updateGpsLocation();
//        });
//        set location text
        GPS gps = GPS.getInstance(this);
        location.setText(gps.getAddressString());
//        runAnimation();
        updateGpsLocation();

    }

    private void updateGpsLocation() {
        // A null listener allows the button to dismiss the dialog and take no further action.

        DialogFragment singleChoice = new SentimentPlacesAlert();
        singleChoice.setCancelable(false);
        singleChoice.show(getSupportFragmentManager(), "Single choice dialog");
        // todo update location send to db and then open take picture activity

    }


    /**
     * Updates information about user's location.
     *
     * @param list     - location sentiment places.
     * @param position - the chosen sentiment index.
     */
    public void onAgreeButtonClicked(String[] list, int position) {
        System.out.println("selected location: " + list[position]);
        // User's sentiment place. e.g. gym.
        String chosenSentimentPlace = list[position];

        GPS userCurrentGpsLocation = GPS.getInstance(this);

        // Updating db - GPS location & sentiment location.
        updateDbLocation(userCurrentGpsLocation, chosenSentimentPlace);
        //Update Usage in db
        if (DataUploader.getInstance(this)
                .uploadUsageDetails(UsageRetriever.getInstance(this).getData(), questionnaireID))
            Log.i("Questionnaire Usage Upload", "success");
        else Log.i("Questionnaire Usage Upload", "failure");

        finish();
    }

    /**
     * Setting the location's information to the db and uploading it to the db.
     *
     * @param userCurrentGpsLocation
     * @param chosenSentimentPlace
     */
    private void updateDbLocation(GPS userCurrentGpsLocation, String chosenSentimentPlace) {
        String address = userCurrentGpsLocation.getAddressString();
        System.out.println("address founded before sent to db: " + address);
        double latitude = userCurrentGpsLocation.getLatitude();
        double longitude = userCurrentGpsLocation.getLongitude();
        DataUploader dataUploader = DataUploader.getInstance(this);
        dataUploader.uploadGpsDetails(latitude, longitude, address, chosenSentimentPlace, questionnaireID);
        System.out.println("done");
    }


    @Override
    protected void onResume() {
        super.onResume();
//        runAnimation();
    }

    /**
     * run animation of class
     */
    private void runAnimation() {
        Animation animationtop = AnimationUtils.loadAnimation(GPSActivity.this, R.anim.toptodown);
        Animation animationbot = AnimationUtils.loadAnimation(GPSActivity.this, R.anim.bottotop);
        location.startAnimation(animationtop);
        title.startAnimation(animationtop);
        apply.startAnimation(animationbot);
    }

    /**
     * connect class and xml objects
     */
    private void connectButtonToXml() {
        location = findViewById(R.id.location);
        title = findViewById(R.id.title);
        apply = findViewById(R.id.apply_gps);
    }

    /**
     * Starting another activity.
     *
     * @param c - the new activity class to start.
     */
    private void startOtherActivity(Class c) {
        finish();
        Intent intent = new Intent(this, c);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}