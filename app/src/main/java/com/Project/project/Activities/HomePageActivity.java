package com.Project.project.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Project.project.Fitbit.FitbitDataManager;
import com.Project.project.GPS.GPS;
import com.Project.project.R;
import com.parse.Parse;
import com.parse.ParseUser;

import static com.Project.project.Utilities.PreventTwoClick.preventTwoClick;

public class HomePageActivity extends AppCompatActivity {
    String username;
    TextView welcomeUser, textView1, textView2, textView3, textView4, textView5, textView6;
    Button reports, question, fitbit, test, profile, logout;
    ImageView Ireports, Iquestion, Ifitbit, Itest, Iprofile, Ilogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("9d57252736a6272d4d0bba3eac263f7e8cabe9ee")
                // if defined
                .clientKey("a04d0b19725f3a07f263ca9fe235df9a9e95ffeb")
                .server("http://3.14.254.134:80/parse")
                .build()
        );

        setContentView(R.layout.home_page);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        // Start searching for location
        if ((getPackageManager().checkPermission("android.permission.ACCESS_FINE_LOCATION", getPackageName()) == PackageManager.PERMISSION_GRANTED) && (getPackageManager().checkPermission("android.permission.ACCESS_COARSE_LOCATION", getPackageName()) == PackageManager.PERMISSION_GRANTED))
            GPS.getInstance(this);
        connectButtonToXml();
        // Capture the layout's TextView and set the string as its text
        welcomeUser.setText("Hello " + username);
        runAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectButtonToXml();
        runAnimation();
    }

    /**
     * animation function to move objects
     */
    private void runAnimation() {
        Animation animationleft = AnimationUtils.loadAnimation(HomePageActivity.this, R.anim.lefttoright);
        Animation animationright = AnimationUtils.loadAnimation(HomePageActivity.this, R.anim.righttoleft);
        Animation animationtop = AnimationUtils.loadAnimation(HomePageActivity.this, R.anim.toptodown);
        textView1.startAnimation(animationleft);
        textView2.startAnimation(animationright);
        textView3.startAnimation(animationleft);
        textView4.startAnimation(animationright);
        textView5.startAnimation(animationleft);
        textView6.startAnimation(animationright);

        reports.startAnimation(animationleft);
        question.startAnimation(animationright);
        fitbit.startAnimation(animationleft);
        test.startAnimation(animationright);
        profile.startAnimation(animationleft);
        logout.startAnimation(animationright);
        welcomeUser.startAnimation(animationtop);

        Ireports.startAnimation(animationleft);
        Iquestion.startAnimation(animationright);
        Ifitbit.startAnimation(animationleft);
        Itest.startAnimation(animationright);
        Iprofile.startAnimation(animationleft);
        Ilogout.startAnimation(animationright);
    }

    /**
     * connect xml and class
     */
    private void connectButtonToXml() {
        reports = findViewById(R.id.watchReportBtn);
        question = findViewById(R.id.fill_survey);
        fitbit = findViewById(R.id.setting);
        test = findViewById(R.id.fitbit);
        profile = findViewById(R.id.notifiyremind);
        logout = findViewById(R.id.logout);
        welcomeUser = findViewById(R.id.show_user_welcome);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);
        Ireports = findViewById(R.id.imageView4);
        Iquestion = findViewById(R.id.imageView2);
        Ifitbit = findViewById(R.id.imageView5);
        Itest = findViewById(R.id.imageView6);
        Iprofile = findViewById(R.id.imageView7);
        Ilogout = findViewById(R.id.imageView8);
    }

    /**
     * menu functions
     */
    public void moveToQuestionnaire(View view) {
        startOtherActivity(QuestionnaireActivity.class);
    }

    public void GpsShowLocation(View view) {
        preventTwoClick(view);
        if ((getPackageManager().checkPermission("android.permission.ACCESS_FINE_LOCATION", getPackageName()) == PackageManager.PERMISSION_GRANTED) && (getPackageManager().checkPermission("android.permission.ACCESS_COARSE_LOCATION", getPackageName()) == PackageManager.PERMISSION_GRANTED)) {
            GPS gpsTTT = GPS.getInstance(this);
            System.out.println("first:" + gpsTTT.getLatitude());
            System.out.println(gpsTTT.getLongitude());
            System.out.println(gpsTTT.getAddressString());
        } else
            System.out.println("gps not allowed");
    }

    public void notificationActivity(View view) {
        preventTwoClick(view);
        startOtherActivity(NotificationActivity.class);
    }

    public void SettingActivity(View view) {
        preventTwoClick(view);
        startOtherActivity(SettingActivity.class);
    }

    public void watchReport(View view) {
        preventTwoClick(view);
        startOtherActivity(ChooseReportActivity.class);
    }

    public void watchRadar(View view) {
        preventTwoClick(view);
        //startOtherActivity(BubbleTempActi.class);
    }

    /**
     * Logout the current user, if logged in
     *
     * @param view current view
     */
    public void logout(View view) {
        preventTwoClick(view);
        if (ParseUser.getCurrentUser() != null)
            ParseUser.logOut();
        TextView textView = findViewById(R.id.show_user_welcome);
        textView.setText("");
        Parse.destroy();
        finish();
    }

    /**
     * Destroying Parse, starts other activity
     *
     * @param c the activity class
     */
    private void startOtherActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.putExtra("Username", username);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            System.out.println("no function is set for this item");
            return false;
        }
        // return true if you handled the button click, otherwise return false.
    }

    public void onBackPressed() {
        finish();
    }

    public void connectFitbit(View view) {
        FitbitDataManager.getInstance(this).authenticate(null);
    }
}
