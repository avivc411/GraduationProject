package com.Project.project.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Project.project.R;
import com.Project.project.Report.Utilities.RekognitionEmotionsData;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Responsible for displaying features and questionnaires's answers as radar chart.
 */
public class RekognitionEmotionsChartActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {
    // Chart.
    private RadarChart featuresEmotionsChart;
    // Chart's labels.
    private String[] labels;
    // List of user's features.
    private List<String> features;
    // Spinner containing features list.
    private Spinner featureSpinner;
    private TextView bottomText;
    // RekognitionEmotionsData containing user's information.
    private RekognitionEmotionsData rekognitionEmotionsData;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekognition_emotions_chart);
        // User's rekognition feature to emotions data.
        rekognitionEmotionsData = UserReportActivity.getRekognitionEmotionsData();

        featuresEmotionsChart = findViewById(R.id.featuresEmotionsChart);
        // Xml connection.
        featuresEmotionsChart = (RadarChart) findViewById(R.id.featuresEmotionsChart);
        featuresEmotionsChart.getDescription().setText("");

        // Sorted features list.
        features = rekognitionEmotionsData.getFeatures();
        Collections.sort(features);

        // Chart labels.
        labels = rekognitionEmotionsData.getLabels();
        bottomText = findViewById(R.id.bottomText);
        featureSpinner = findViewById(R.id.featureSpinner);

        // Xml connection.
        featureSpinner = (Spinner) findViewById(R.id.featureSpinner);
        featureSpinner.setOnItemSelectedListener(this);
        // Setting adapter to the list.
        ArrayAdapter ad = new ArrayAdapter(this,
                android.R.layout.simple_expandable_list_item_1, features);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Connecting the list view to the adapter.
        featureSpinner.setAdapter(ad);
    }

    // Open feature's chart when selecting feature.
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        // On selecting a spinner item.
        String item = adapterView.getItemAtPosition(position).toString();
        String itemDoubleCheck = features.get(position);

        // Showing selected spinner item.
        Toast.makeText(RekognitionEmotionsChartActivity.this, "Selected: " + item +
                        ": " + itemDoubleCheck,
                Toast.LENGTH_LONG).show();
        // Setting feature information on the chart.
        setChartForFeature(features.get(position));
    }


    /**
     * Displaying relevant chart for specific feature.
     *
     * @param featureName - chart's information regarded to.
     */
    private void setChartForFeature(String featureName) {
        bottomText.setText("Radar chart:\n\n" +
                "Average feature score in all questionnaires containing it.");
        // Chart description.
        //featuresEmotionsChart.getDescription().setText(
                ;
//        featuresEmotionsChart.getDescription().setText("Radar chart:\n\n " +
//                "Average feature score in all questionnaires containing it.");
//        featuresEmotionsChart.getDescription().setTextSize(12f);
        RadarDataSet dataSet = new RadarDataSet(getDataValues(featureName), "Emotions grades.");
        dataSet.setColor(Color.parseColor("#518ac2"));
        dataSet.setLineWidth(4f); // width of line that user select
        dataSet.setValueTextColor(Color.parseColor("#3b6394"));
        dataSet.setValueTextSize(14f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#4a9bff"));

        // Data.
        RadarData data = new RadarData();
        data.addDataSet(dataSet);
        data.setValueTextSize(16f);

        // XAxis.
        XAxis xAxis = featuresEmotionsChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setTextSize(16f); //size of afraid/angry...

        // YAxis.
        YAxis yAxis = featuresEmotionsChart.getYAxis();
        yAxis.setGranularity(1.0f);
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(4);
        yAxis.setTextSize(12f); //size of 1/2/3/4/5

        // Chart.
        featuresEmotionsChart.setData(data);
        featuresEmotionsChart.animateXY(500, 3000);
        featuresEmotionsChart.invalidate();

    }

    /**
     * Setting radar entries of specific feature.
     *
     * @param featureName - the feature we want to setting information for.
     * @return List<RadarEntry> of information regarded to featureName.
     */
    private List<RadarEntry> getDataValues(String featureName) {
        ArrayList<RadarEntry> dataVals = new ArrayList<>();
        // All features information.
        Map<String, Map<String, Integer>> featuresScores =
                rekognitionEmotionsData.getRekognitionFeatureAverageScores();

        // Specific featureName information.
        Map<String, Integer> featureScores = featuresScores.get(featureName);

        // Setting average score for every label.
        for (int i = 0; i < labels.length; i++) {
            dataVals.add(new RadarEntry(featureScores.get(labels[i])));
        }
        return dataVals;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Showing selected spinner item.
        Toast.makeText(RekognitionEmotionsChartActivity.this, "Selected: "
                        + adapterView.getItemAtPosition(0).toString() +
                        ": " + features.get(0),
                Toast.LENGTH_LONG).show();
    }
}
