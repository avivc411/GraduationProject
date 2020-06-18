package com.Project.project.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.Project.project.R;
import com.Project.project.Report.UserReportProcessor;
import com.Project.project.Report.Utilities.DateUtils;
import com.Project.project.Report.Utilities.XYPlotSetter;
import com.Project.project.Utilities.XYGraphDataHelper;
import com.androidplot.xy.XYPlot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Lets user choose which emotions to show and present them in xy plot.
 */
public class XYGraphActivity extends AppCompatActivity implements MultiChoiceListFragment.DataReceiver{
    private MultiChoiceListFragment mlcFrag;
    private XYPlotFragment xyPlotFrag;
    private FragmentManager fragmentManager;

    //plot managing
    private XYGraphDataHelper dataHelper;
    private String[] emotionsToChoose = {"Upset", "Hostile", "Alert", "Ashamed", "Inspired", "Nervous", "Determined", "Attentive", "Afraid", "Active"};
    private int passedRanges = 0, daysRange;
    private XYPlot plot;
    private TextView noDataText;

    //plot data variables
    Map<String, List<Integer>> emotionsData;
    private List<String> domainLabels;
    private int rangeSize;
    private String title;
    private String rangeTitle;
    private String domainTitle;
    private boolean zoom;
    private boolean showLegend;
    private boolean showTitle;
    private boolean showDomainTitle;
    private boolean showRangeTitle;
    private boolean showDomainLabels;
    private boolean showRangeLabels;
    private boolean scaleRange;
    private float domainLabelRotation;

    //filters
    private List<String> availableObjectFilters = new ArrayList<>(),
            availableLocationsFilters = new ArrayList<>(),
            availableActivityFilters = new ArrayList<>();

    private Set<String> chosenObjectFilters = new HashSet<>(),
            chosenLocationsFilters = new HashSet<>(),
            chosenActivityFilters = new HashSet<>();

    private Spinner spinner;
    private TextView filterText;
    private boolean firstSelection = true;

    public XYGraphActivity() {
        setXYGraphParams(5,
                "", "", "",
                true, true, true, false, false,
                true, false, false, 0);
    }

    /**
     * Set all available parameters.
     * @param title Graph main title.
     * @param zoom Enable zoom.
     * @param scaleRange Fit range automatically.
     * @param domainLabelRotation Domain's labels angle, anticlockwise.
     */
    public void setXYGraphParams(int rangeSize, String title, String rangeTitle, String domainTitle,
                                 boolean zoom, boolean showLegend, boolean showTitle, boolean showDomainTitle,
                                 boolean showRangeTitle, boolean showDomainLabels, boolean showRangeLabels,
                                 boolean scaleRange, float domainLabelRotation){
        this.rangeSize = rangeSize;
        this.title = title;
        this.rangeTitle = rangeTitle;
        this.domainTitle = domainTitle;
        this.zoom = zoom;
        this.showLegend = showLegend;
        this.showTitle = showTitle;
        this.showDomainTitle = showDomainTitle;
        this.showRangeTitle = showRangeTitle;
        this.showDomainLabels = showDomainLabels;
        this.showRangeLabels = showRangeLabels;
        this.scaleRange = scaleRange;
        this.domainLabelRotation = domainLabelRotation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x_y_graph);

        try {
            switch ((UserReportProcessor.RangeFilter) Objects.requireNonNull(getIntent().getSerializableExtra("Filter"))) {
                case NONE_FILTER:
                    daysRange = -1;
                    break;
                case LAST_SEVEN_DAYS:
                    daysRange = 7;
                    break;
                default:
                    daysRange = 30;
                    break;
            }
        } catch (NullPointerException e) {
            daysRange = -1;
        }

        fragmentManager = getSupportFragmentManager();

        mlcFrag = MultiChoiceListFragment.newInstance(emotionsToChoose);
        xyPlotFrag = XYPlotFragment.newInstance();

        //show emotions choosing list
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.plotLayout, xyPlotFrag);
        fragmentTransaction.add(R.id.plotLayout, mlcFrag);

        fragmentTransaction.hide(xyPlotFrag);
        fragmentTransaction.show(mlcFrag);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        setSpinner();
    }

    /**
     * Get chosen items by user, show xy plot of it in new fragment.
     *
     * @param data Chosen items
     */
    @Override
    public void onDataReceived(List<String> data) {
        //hide emotion choosing list, show plot
        fragmentManager.beginTransaction().hide(mlcFrag).show(xyPlotFrag).commit();

        plot = findViewById(R.id.plot);
        noDataText = findViewById(R.id.noDataText);

        findViewById(R.id.leftArrowLayout).setOnClickListener(this::prevWeek);
        findViewById(R.id.rightArrowLayout).setOnClickListener(this::nextWeek);

        mlcFrag.onDestroy();

        dataHelper = new XYGraphDataHelper(this,
                getIntent().getStringExtra("Username"), data, 7);
        getEmotionsData();
        getAvailableFilters();

        spinner.setEnabled(true);
        spinner.setVisibility(View.VISIBLE);
        filterText.setVisibility(View.VISIBLE);

        showXYPlot();
    }

    /**
     * Set plot parameters and present it.
     */
    private void showXYPlot() {
        if (emotionsData == null)
            noDataText.setVisibility(View.VISIBLE);
        else
            noDataText.setVisibility(View.INVISIBLE);

        XYPlotSetter.setPlot(this,
                plot,
                emotionsData,
                domainLabels,
                domainLabels.size(),
                rangeSize,
                title,
                rangeTitle, domainTitle,
                zoom,
                showLegend,
                showTitle, showDomainTitle, showRangeTitle, showDomainLabels, showRangeLabels,
                scaleRange,
                domainLabelRotation);
    }

    /**
     * Set global variables according to chosen emotions' data from db.
     */
    private void getEmotionsData() {
        dataHelper.setActivitiesFilter(chosenActivityFilters);
        dataHelper.setLocationsFilter(chosenLocationsFilters);
        dataHelper.setObjectsFilter(chosenObjectFilters);

        emotionsData = dataHelper.getData();
        domainLabels = dataHelper.getDaysByLetter();

        Date first = new Date(), last = new Date();
        dataHelper.setFirstLastDateInRange(first, last);
        title = "Week "+
                DateUtils.convertDateToString(first, "dd-MM-yyyy")
                +" --- "+
                DateUtils.convertDateToString(last, "dd-MM-yyyy");

        showXYPlot();
    }

    /**
     * Show next week's data.
     */
    public void nextWeek(View view) {
        System.out.println("next week");
        if (passedRanges == 0)
            return;
        System.out.println("not skipped, passedRanges: "+passedRanges);

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
        Thread t = new Thread(() -> {
            passedRanges -= 7;
            emotionsData = dataHelper.getNextRangeData();
            domainLabels = dataHelper.getDaysByLetter();
            Date first = new Date(), last = new Date();
            dataHelper.setFirstLastDateInRange(first, last);
            title = "Week "+
                    DateUtils.convertDateToString(first, "dd-MM-yyyy")
                    +" --- "+
                    DateUtils.convertDateToString(last, "dd-MM-yyyy");
        });
        t.start();
        try {
            t.join();
            showXYPlot();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dialog.dismiss();
    }

    /**
     * Show previous week data.
     */
    public void prevWeek(View view) {
        System.out.println("prev week");
        if (daysRange > 0 && passedRanges >= daysRange)
            return;
        System.out.println("not skipped, passedRanges: "+passedRanges);

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        Thread t = new Thread(() -> {

            passedRanges += 7;
            emotionsData = dataHelper.getPrevRangeData();
            domainLabels = dataHelper.getDaysByLetter();
            Date first = new Date(), last = new Date();
            dataHelper.setFirstLastDateInRange(first, last);
            title = "Week "+
                    DateUtils.convertDateToString(first, "dd-MM-yyyy")
                    +" --- "+
                    DateUtils.convertDateToString(last, "dd-MM-yyyy");
        });

        t.start();
        try {
            t.join();
            showXYPlot();
        } catch (InterruptedException e) {
            e.printStackTrace();
            dialog.dismiss();
        }

        dialog.dismiss();
    }

    private void getAvailableFilters() {
        dataHelper.getAvailableFilters(availableObjectFilters, availableLocationsFilters, availableActivityFilters);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Set spinner properties.
     */
    private void setSpinner() {
        spinner = findViewById(R.id.filterSpinner);
        filterText = findViewById(R.id.filterTextView);

        String[] filters = {"Object", "Location", "Activity", "Clear Filters"};

        ArrayAdapter<String> spinnerListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1, filters);
        spinnerListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerListAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(firstSelection){
                    firstSelection = false;
                    return;
                }
//                String item = parent.getItemAtPosition(position).toString();
//                switch (item) {
                switch (position){
//                    case "Object":
                    case 0:
                        selectFilterValues(availableObjectFilters, chosenObjectFilters, true);
                        break;
                    case 1:
//                    case "Location":
                        selectFilterValues(availableLocationsFilters, chosenLocationsFilters, false);
                        break;
                    case 2:
//                    case "Activity":
                        selectFilterValues(availableActivityFilters, chosenActivityFilters, true);
                        break;
                    case 3:
//                    case "Clear Filters":
                        chosenActivityFilters.clear();
                        chosenLocationsFilters.clear();
                        chosenObjectFilters.clear();
                        getEmotionsData();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner.setEnabled(false);
        spinner.setVisibility(View.INVISIBLE);
        filterText.setVisibility(View.INVISIBLE);
    }

    /**
     * Show dialog in which user can choose values to filter by.
     * @param availableFilters Available values.
     * @param chosenFilters To update after choosing filter values.
     */
    public void selectFilterValues(List<String> availableFilters, Set<String> chosenFilters, boolean multi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (availableFilters != null && availableFilters.size() > 0) {
            builder.setTitle("Pick a value to filter by");
            String[] filtersArray = new String[availableFilters.size()];
            availableFilters.toArray(filtersArray);
            boolean[] checked = new boolean[filtersArray.length];
            int checkedItem = 0;
            if(multi)
                builder.setMultiChoiceItems(filtersArray, checked, (dialog, which, isChecked) -> chosenFilters.add(filtersArray[which]));
            else
                builder.setSingleChoiceItems(filtersArray, checkedItem, ((dialog, which) -> chosenFilters.add(filtersArray[which])));
            builder.setPositiveButton(R.string.done, (dialog, which) -> {
                getEmotionsData();
                dialog.dismiss();
            });
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        } else {
            builder.setTitle("No values available");
            builder.setPositiveButton(R.string.done, (dialog, which) -> dialog.dismiss());
        }
        builder.show();
        clearChosenFilters();
    }

    private void clearChosenFilters(){
        chosenObjectFilters.clear();
        chosenActivityFilters.clear();
        chosenLocationsFilters.clear();
    }

    /**
     * Custom spinner with option to choose same filter again.
     */
    public static class SameChoiceSpinner extends androidx.appcompat.widget.AppCompatSpinner{
        public SameChoiceSpinner(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override public void
        setSelection(int position, boolean animate)
        {
            boolean sameSelected = position == getSelectedItemPosition();
            super.setSelection(position, animate);
            if (sameSelected) {
                // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
                try{
                    getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
                }catch (NullPointerException ignored){}
            }
        }

        @Override public void
        setSelection(int position)
        {
            boolean sameSelected = position == getSelectedItemPosition();
            super.setSelection(position);
            if (sameSelected) {
                // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
                try{
                    getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
                }catch (NullPointerException ignored){}
            }
        }
    }
}
