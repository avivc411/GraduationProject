package com.Project.project.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Project.project.R;
import com.Project.project.Report.UserReportProcessor;

import static com.Project.project.Report.UserReportProcessor.RangeFilter.LAST_SEVEN_DAYS;
import static com.Project.project.Report.UserReportProcessor.RangeFilter.LAST_THIRTY_DAYS;
import static com.Project.project.Report.UserReportProcessor.RangeFilter.NONE_FILTER;
import static com.Project.project.Utilities.PreventTwoClick.preventTwoClick;

/**
 * Responsible for user's screen to choose the period of report to load.
 */
public class ChooseReportActivity extends AppCompatActivity {
    ListView listViewPeriodicOptions;
    String[] listMonths;

    String userName;
    UserReportProcessor.RangeFilter rangeFilter;

    // Buttons
    Button maxDaysReportBtn;
    Button lastSevenDaysReportBtn;
    Button lastThirtyDaysReportBtn;
    Button monthlyReport;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_report);

        Intent intent = getIntent();
        userName = intent.getStringExtra("Username");

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading reports, please wait a few seconds");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        Thread initializeUserReportProcessorThread = new Thread(() -> {
            UserReportProcessor.getInstance(ChooseReportActivity.this, userName);
            runOnUiThread(() -> {
                dialog.dismiss();
            });
        }, "first_thread");
        initializeUserReportProcessorThread.start();

        // Buttons
        maxDaysReportBtn = findViewById(R.id.maxDaysReportBtn);
        lastSevenDaysReportBtn = findViewById(R.id.lastSevenDaysReportBtn);
        lastThirtyDaysReportBtn = findViewById(R.id.lastThirtyDaysReportBtn);
        monthlyReport = findViewById(R.id.monthlyReport);
        title = findViewById(R.id.title);
        // LIST OF MONTHS
        listViewPeriodicOptions = findViewById(R.id.listViewPeriodicOptions);
        listViewPeriodicOptions.setVisibility(View.INVISIBLE);
        // Months names
        listMonths = new String[]{"January", "February", "March", "April", "May", "June", "July", "August",
                "September", "October", "November", "December"};
        runAnimatiaon();
        // Adapter from the data to the view
        final ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listMonths);

        listViewPeriodicOptions.setAdapter(monthsAdapter);

        // Buttons on click actions
        maxDaysReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prevent Two Click
                preventTwoClick(view);
                setFilter(NONE_FILTER);
                openReportIntent();
            }
        });
        lastSevenDaysReportBtn.setOnClickListener(view -> {
            preventTwoClick(view);
            setFilter(LAST_SEVEN_DAYS);
            openReportIntent();
        });
        lastThirtyDaysReportBtn.setOnClickListener(view -> {
            preventTwoClick(view);
            setFilter(LAST_THIRTY_DAYS);
            openReportIntent();
        });

        // List ON CLICK actions
        listViewPeriodicOptions.setOnItemClickListener((adapterView, view, position, l) -> {
            // Prevent Two Click
            preventTwoClick(view);
            String monthValue = monthsAdapter.getItem(position).toUpperCase();
            // todo send with capital letters the month name
            Toast.makeText(getApplicationContext(), monthValue, Toast.LENGTH_SHORT).show();
            Enum enumMonthValue = Enum.valueOf(UserReportProcessor.RangeFilter.class, monthValue);
            setFilter((UserReportProcessor.RangeFilter) enumMonthValue);
            openReportIntent();
        });
    }

    /**
     * Displays month list.
     *
     * @param view
     */
    public void showMonthsList(View view) {
        preventTwoClick(view);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.righttoleft);
        listViewPeriodicOptions.startAnimation(animation);
        listViewPeriodicOptions.setVisibility(View.VISIBLE);
    }

    /**
     * Open UserReportActivity
     */
    private void openReportIntent() {
        Intent intent = new Intent(this, UserReportActivity.class);
        intent.putExtra("Username", userName);
        intent.putExtra("Filter", rangeFilter);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void runAnimatiaon() {
        Animation animtiontop = AnimationUtils.loadAnimation(this, R.anim.toptodown);
        title.startAnimation(animtiontop);
        maxDaysReportBtn.startAnimation(animtiontop);
        lastSevenDaysReportBtn.startAnimation(animtiontop);
        lastThirtyDaysReportBtn.startAnimation(animtiontop);
        monthlyReport.startAnimation(animtiontop);
    }

    /**
     * Change the RangeFilter.
     *
     * @param rangeFilterToSet
     */
    private void setFilter(UserReportProcessor.RangeFilter rangeFilterToSet) {
        rangeFilter = rangeFilterToSet;
    }

    @Override
    public void onBackPressed() {
        UserReportProcessor.invalidate();
        super.onBackPressed();
    }
}