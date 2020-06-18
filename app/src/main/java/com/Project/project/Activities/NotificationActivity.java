package com.Project.project.Activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Project.project.R;
import com.Project.project.Utilities.AlarmReceiver;

import java.util.Calendar;

import static com.Project.project.Utilities.PreventTwoClick.preventTwoClick;

/**
 * Creating notifications screen.
 */
public class NotificationActivity extends AppCompatActivity implements View.OnClickListener {

    String userName;

    // Radio Group elements.
    RadioGroup radioGroupDays;
    RadioButton radioButton;
    TextView textView, textview2;
    Button setNotificationBtn, cancelNotificationBtn;
    TimePicker timePickerNotification;
    private int numberOfDaysToRepeat = 1;

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        userName = getIntent().getStringExtra("Username");

        // Set Onclick listener.
        findViewById(R.id.setNotificationBtn).setOnClickListener(this);
        findViewById(R.id.cancelNotificationBtn).setOnClickListener(this);

        connectXML();

        runAnimation();

    }


    @Override
    public void onClick(View view) {
        // Prevent Two Click
        preventTwoClick(view);
        TimePicker timePickerNotification = findViewById(R.id.timePickerNotification);
        timePickerNotification.setIs24HourView(true);
        // Set notificationId & text message.
        Intent intent = new Intent(NotificationActivity.this, AlarmReceiver.class);
        int notificationId = 1;
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("userName", userName);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // getBroadcast (context, requestCode, intent, flags)
        PendingIntent alarmIntent = PendingIntent.getBroadcast(NotificationActivity.this
                , 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        int hour = 0, minute = 0;

        switch (view.getId()) {
            // Set notification.
            case R.id.setNotificationBtn:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hour = timePickerNotification.getHour();
                    minute = timePickerNotification.getMinute();
                }
                // Chosen time for notification.
                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, hour);
                startTime.set(Calendar.MINUTE, minute);
                startTime.set(Calendar.SECOND, 0);
                long notificationTime = startTime.getTimeInMillis();


                // Set alarm
                //  alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + alarmStartTime, alarmIntent);
                //  alarm.set(AlarmManager.RTC_WAKEUP, notificationTime, alarmIntent);

                // 1000 = 1 sec, 60= 1 minute, 60 = 1 hour, 24 = day,numberOfDaysToRepeat = days interval. .
                // Message displays.
                String daysMessage = "days";
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime,
                        1000 * 60 * 60 * 24 * numberOfDaysToRepeat, alarmIntent);
                if (numberOfDaysToRepeat == 1) {
                    daysMessage = "day";
                }
                Toast.makeText(this, " Done. Notification set to " + hour + ":" +
                                minute + " repeat every: " + numberOfDaysToRepeat + " " + daysMessage
                        , Toast.LENGTH_LONG).show();
                startOtherActivity(HomePageActivity.class);
                break;
            // Cancel notification.
            case R.id.cancelNotificationBtn:
                alarm.cancel(alarmIntent);
                Toast.makeText(this, " Canceled", Toast.LENGTH_LONG).show();
                startOtherActivity(HomePageActivity.class);
                break;
        }
    }

    /**
     * Updates days interval between alerts after choosing interval.
     *
     * @param view
     */
    public void checkButton(View view) {
        int radioId = radioGroupDays.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        CharSequence repeatedDays = radioButton.getText();
        String daysToRepeat = repeatedDays.toString();
        // Updataing.
        numberOfDaysToRepeat = Integer.parseInt(daysToRepeat);
        Toast.makeText(this, " radioButton: " + numberOfDaysToRepeat, Toast.LENGTH_SHORT).show();
    }

    /**
     * Starts other activity.
     *
     * @param c
     */
    private void startOtherActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.putExtra("Username", userName);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * run animation on page
     */
    private void runAnimation() {
        Animation animationbottop = AnimationUtils.loadAnimation(this, R.anim.bottotop);
        Animation animationleftright = AnimationUtils.loadAnimation(this, R.anim.lefttoright);
        Animation animationzoom = AnimationUtils.loadAnimation(this, R.anim.zoomin);

        textView.startAnimation(animationleftright);
        radioGroupDays.startAnimation(animationleftright);
        textview2.startAnimation(animationleftright);
        timePickerNotification.startAnimation(animationzoom);
        setNotificationBtn.startAnimation(animationbottop);
        cancelNotificationBtn.startAnimation(animationbottop);
    }

    private void connectXML() {
        radioGroupDays = findViewById(R.id.radioGroupDays);
        textView = findViewById(R.id.textView);
        textview2 = findViewById(R.id.textview2);
        setNotificationBtn = findViewById(R.id.setNotificationBtn);
        cancelNotificationBtn = findViewById(R.id.cancelNotificationBtn);
        timePickerNotification = findViewById(R.id.timePickerNotification);
    }
}