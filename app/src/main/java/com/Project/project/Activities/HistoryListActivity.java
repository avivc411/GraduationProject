package com.Project.project.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Project.project.R;

import java.util.Date;
import java.util.List;

/**
 * Responsible for displaying user's reports list and give access to see them.
 */
public class HistoryListActivity extends AppCompatActivity {
    // Chosen report from the list.
    static Date selectedDate;
    // Reports list view.
    ListView listView;
    // All dates list.
    List<Date> allReportsDates;

    /**
     * Passing the selected report.
     *
     * @return selected report.
     */
    public static Date getSelectedDate() {
        return selectedDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Fetching user's history report list.
        allReportsDates = UserReportActivity.getAllReportsDates();
        setContentView(R.layout.activity_history);

        // Matching to xml file.
        listView = findViewById(R.id.historyListView);

        // Setting adapter to the list.
        ArrayAdapter ad = new ArrayAdapter(this,
                android.R.layout.simple_expandable_list_item_1, allReportsDates);
        // Connecting the list view to the adapter.
        listView.setAdapter(ad);

        /**
         * Opening selected report after clicking on it.
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(HistoryListActivity.this, "position: " + position
                        + " " + allReportsDates.get(position), Toast.LENGTH_SHORT).show();
                // Updating the selected report to display.
                selectedDate = allReportsDates.get(position);
                startOtherActivity(SingleReportActivity.class);
            }
        });
        runAnimation();
    }

    /**
     * Starts another activity.
     *
     * @param c the activity class
     */
    private void startOtherActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void runAnimation() {
        Animation animationfade = AnimationUtils.loadAnimation(HistoryListActivity.this, R.anim.lefttoright);
        listView.startAnimation(animationfade);
    }
}
