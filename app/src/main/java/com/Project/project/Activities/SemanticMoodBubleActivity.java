package com.Project.project.Activities;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.Project.project.GPS.SemanticMoodData;
import com.Project.project.R;
import com.Project.project.Report.Utilities.DateUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.BubbleFormatter;
import com.androidplot.xy.BubbleSeries;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SemanticMoodBubleActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private static XYPlot plot;
    List<SemanticMoodData> semanticMoodList;
    private Spinner semanticLocationMoodSpinner;
    private List<String> ranges;
    List<String> yLabels;
    final String[] places = {"Home", "Friend's home", "Work", "University", "Gym", "Cinema",
            "Shopping mall", "Other"};
    private Map<String, Integer> semanticLocationToYAxis;
    List<SemanticMoodBubleActivity.SemanticDatesIndexs> indexes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semantic_mood_buble);

        // Fetching user's semantic location information.
        semanticMoodList = UserReportActivity.getSemanticMoodDataList();

        // Adding YAxis places.
        semanticLocationToYAxis = getMapToYAxis();


        if (semanticMoodList != null) {
            // Setting dates ranges of user's data.
            indexes = setUpIndexes();
            ranges = setRangesList(indexes, semanticMoodList);

            semanticLocationMoodSpinner = findViewById(R.id.semanticMoodSpinner);
            semanticLocationMoodSpinner.setOnItemSelectedListener(this);
            // Setting adapter to the list.
            ArrayAdapter ad = new ArrayAdapter(this,
                    android.R.layout.simple_expandable_list_item_1, ranges);
            ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Connecting the list view to the adapter.
            semanticLocationMoodSpinner.setAdapter(ad);

        }

        // Initialize XYPlot reference.
        plot = (XYPlot) findViewById(R.id.plottt);


    }

    /**
     * Displaying the bubble chart on the screen with the fit data.
     * @param filteredList
     */
    private void setChart(List<SemanticMoodData> filteredList) {
        if (semanticMoodList == null) {
            return;
        }
        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)

        // Y - LOCATION: 2 ->9
        // VALUE - MOOD
        // X BY ORDER - DATE
        plot.clear();
        BubbleSeries bubleSeriesData = setData(filteredList);
        plot.setDomainBoundaries(-1, 7, BoundaryMode.FIXED);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
        plot.setDomainLabel("Dates");


        plot.setRangeBoundaries(1, 10, BoundaryMode.FIXED);
        plot.setRangeLabel("Locations");


        BubbleFormatter bf1 = new BubbleFormatter(this, R.xml.bubble_formatter1);
        bf1.setPointLabelFormatter(new PointLabelFormatter(Color.BLACK));
        bf1.getPointLabelFormatter().getTextPaint().setTextAlign(Paint.Align.CENTER);
        bf1.getPointLabelFormatter().getTextPaint().setFakeBoldText(true);


        // add series to the xyplot:
        plot.addSeries(bubleSeriesData, bf1);

        List<String> xLabels = new ArrayList<>();
        xLabels = setDates(filteredList);

        showDomainLabels(xLabels, -60f);


        yLabels = getYLabels();
        showRangeLabels(yLabels, 0);

        PanZoom.attach(plot);
        Animation animation;
        animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fadeinslow);
        plot.setAnimation(animation);

        plot.redraw();
        plot.invalidate();

    }

    /**
     * Setting XAxis labels on the chart.
     * @param filteredList -  user's data on specific range of dates.
     * @return list of dates for displaying on XAxis of the chart.
     */
    private List<String> setDates(List<SemanticMoodData> filteredList) {
        return filteredList.stream().map(semanticMoodData ->
                DateUtils.convertDateToString(semanticMoodData.getDate())).
                collect(Collectors.toList());
    }

    /**
     * Setting all of the buble information to chart from user's data.
     * @param filteredList - user's data on specific range of dates.
     * @return BubbleSeries which containing all of user's data for chart.
     */
    private BubbleSeries setData(List<SemanticMoodData> filteredList) {

        List<Number> yAxis = filteredList.stream().map(semanticMoodData ->
                (Number) semanticLocationToYAxis.get(semanticMoodData.getSemanticPlace())).
                collect(Collectors.toList());

        List<Number> values = filteredList.stream().map(semanticMoodData ->
                (Number) semanticMoodData.getMood()).
                collect(Collectors.toList());

        BubbleSeries dataSeries = new BubbleSeries(
                yAxis, values, "seriesData");

        return dataSeries;
    }

    /**
     * Creating a list of all places fitting to the chart displaying.
     * @return list of places customizing to the chart.
     */
    private List<String> getYLabels() {
        List<String> labelsYAxis = Arrays.stream(places).
                collect(Collectors.toList());
        labelsYAxis.add(0, "");
        labelsYAxis.add(1, "");
        return labelsYAxis;
    }

    /**
     *  Setting the XAxis labels on the chart.
     */
    private static void showDomainLabels(List<String> domainLabels, float domainLabelRotation) {
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).getPaint().setColor(Color.BLACK);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setRotation(domainLabelRotation);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                String label;
                try {
                    label = domainLabels.get(i);
                } catch (IndexOutOfBoundsException e) {
                    label = "";
                }
                return toAppendTo.append(label);
            }

            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });
    }

    /**
     * Setting the YAxis labels on the chart.
     * @param rangeLabels
     * @param rangeLabelRotation
     */
    private static void showRangeLabels(List<String> rangeLabels, float rangeLabelRotation) {
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).getPaint().setColor(Color.BLACK);


        // plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setRotation(domainLabelRotation);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                String label;
                try {
                    label = rangeLabels.get(i);
                } catch (IndexOutOfBoundsException e) {
                    label = "";
                }
                return toAppendTo.append(label);
            }

            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });
    }


    int firstAutoInitializeCheck = 0;
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        // Prevent automatically displaying the chart  with data.
        if (firstAutoInitializeCheck > 0 && position != 0) {
            int start = indexes.get(position - 1).getStartIndex();
            int end = indexes.get(position - 1).getEndIndex();
            System.out.println("semanticMoodList size! " + semanticMoodList.size());
            Toast.makeText(SemanticMoodBubleActivity.this, "Selected: " + start +
                            ": " + end,
                    Toast.LENGTH_LONG).show();
            List<SemanticMoodData> filteredList = null;
            filteredList = semanticMoodList.subList(start, end + 1);
            System.out.println("filtered size: " + filteredList.size());
            setChart(filteredList);
        }
        // Clear the data from the chart after another range displayed.
        else if (position==0 && firstAutoInitializeCheck >0){
            plot.invalidate();
            plot.clear();
        }
        firstAutoInitializeCheck = 1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /**
     * Class responsible for store information about start index and end index at every dates interval.
     */
    class SemanticDatesIndexs {
        int startIndex;
        int endIndex;

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }


        public SemanticDatesIndexs(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
    }

    /**
     * Creating indexes of dates from user's data in intervals of 7 days at most.
     * @return list of SemanticDatesIndexs which containing all of the available ranges for
     * displaying user's data.
     */
    private List<SemanticMoodBubleActivity.SemanticDatesIndexs> setUpIndexes() {
        List<SemanticMoodBubleActivity.SemanticDatesIndexs> semanticDatesIndexsList =
                new ArrayList<>();

        if (semanticMoodList.size() < 7) {
            semanticDatesIndexsList.add(new SemanticMoodBubleActivity.SemanticDatesIndexs(0,
                    semanticMoodList.size() - 1));
            return semanticDatesIndexsList;
        }


        int begin = 0;
        int end = 6;
        while (end < semanticMoodList.size()) {
            SemanticMoodBubleActivity.SemanticDatesIndexs semanticDatesIndexs =
                    new SemanticMoodBubleActivity.SemanticDatesIndexs(begin, end);
            begin = begin + 6;
            end = end + 6;
            semanticDatesIndexsList.add(semanticDatesIndexs);
        }
        int counter = 7;
        int endIndexJump = semanticMoodList.size();
        for (int i = semanticMoodList.size(); counter > 0 && i >= 0; i--) {
            endIndexJump = endIndexJump - 1;
            counter = counter - 1;
        }
        SemanticMoodBubleActivity.SemanticDatesIndexs semanticDatesIndexs = new SemanticMoodBubleActivity.SemanticDatesIndexs(endIndexJump, semanticMoodList.size() - 1);
        semanticDatesIndexsList.add(semanticDatesIndexs);

        return semanticDatesIndexsList;
    }

    /**
     * Setting dates list to spinner.
     * @param indexes - dates indexes ranges.
     * @param semanticMoodList- user's data.
     * @return list of dates to spinner.
     */
    private List<String> setRangesList(List<SemanticDatesIndexs> indexes,
                                       List<SemanticMoodData> semanticMoodList) {
        String first = "Dates ranges list";
        List<String> ranges = indexes.stream().map(index ->
                DateUtils.convertDateToString(semanticMoodList.get(index.getStartIndex()).getDate()) + " - " +
                        DateUtils.convertDateToString(semanticMoodList.get(index.getEndIndex()).getDate()))
                .collect(Collectors.toList());
        ranges.add(0, first);
        return ranges;

    }

    /**
     * Mapping between place to integer Y value. e.g. home -> 3.
     * @return Map<String place, Integer YAxis value>
     */
    private Map<String, Integer> getMapToYAxis() {
        Map<String, Integer> semanticLocationToXAxis = new HashMap<>();
        int counter = 2;
        for (int i = 0; i < places.length; i++) {
            semanticLocationToXAxis.put(places[i], counter);
            counter++;
        }
        return semanticLocationToXAxis;
    }


}
