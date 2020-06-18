package com.Project.project.Activities;

import android.animation.ArgbEvaluator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.Project.project.GPS.LocationProperties;
import com.Project.project.GPS.SemanticMoodData;
import com.Project.project.PictureSwift.Adapter;
import com.Project.project.PictureSwift.Model;
import com.Project.project.R;
import com.Project.project.RekognitionManagment.RekognitionDataHandler;
import com.Project.project.RekognitionManagment.RekognitionFeature;
import com.Project.project.Report.NegativeSentimentUserReport;
import com.Project.project.Report.PositiveSentimentUserReport;
import com.Project.project.Report.UserReport;
import com.Project.project.Report.UserReport.ReportType;
import com.Project.project.Report.UserReportProcessor;
import com.Project.project.Report.Utilities.RekognitionEmotionsData;
import com.Project.project.UsageManagment.UsageProperties;
import com.Project.project.Utilities.UserChosenPicture;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.parse.ParseException;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.Project.project.Utilities.PreventTwoClick.preventTwoClick;

/**
 * Activity class for displaying user's reports.
 */
public class UserReportActivity extends AppCompatActivity {

    /**
     * Graphs colours
     **/
    public static final int[] GRAPH_COLORS = {
            Color.rgb(85, 88, 205), Color.rgb(92, 247, 87), Color.rgb(163, 29, 32),
            Color.rgb(140, 234, 255), Color.rgb(255, 140, 157), Color.rgb(143, 136, 158),
            Color.rgb(192, 255, 140), Color.rgb(247, 255, 133),
            Color.rgb(242, 150, 116), Color.rgb(76, 135, 194)
    };
    public static List<Bitmap> userLocationPictures;
    static List<Date> allReportsDates = null;
    static RekognitionEmotionsData rekognitionEmotionsData = null;
    static List<SemanticMoodData> semanticMoodDataList = null;
    String username;
    UserReportProcessor.RangeFilter rangeFilter;
    UserReportProcessor userReportProcessor;
    // Screen
    TextView userInfoTextView;
    Button generalReportBtn, negativeSentimentBtn, positiveSentimentBtn, emotionPiechartBtn,
            emotionBarchartBtn, emotionLocationMapBtn, usageBarchartBtn, historyButton, dayAfterDayReport, semanticMoodBtn;
    ViewPager viewPager;
    Adapter adapter;
    List<Model> models;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    RekognitionDataHandler rekognitionDataHandler;
    List<LocationProperties> userPlaces;
    List<UserChosenPicture> userChosenPictures;
    // Charts
    PieChart pieChart;
    BarChart barChart;
    BarChart usageBarChart;
    // User reports
    PositiveSentimentUserReport positiveSentimentUserReport = null;
    NegativeSentimentUserReport negativeSentimentUserReport = null;
    ProgressDialog dialog;

    ReportType reportTypeToDisplay;
    // Data fetched from UserReportProcessor
    private Map<String, Integer> userAnswers;

    private List<UsageProperties> usageInformation;

    public static List<Date> getAllReportsDates() {
        return allReportsDates;
    }

    public static RekognitionEmotionsData getRekognitionEmotionsData() {
        return rekognitionEmotionsData;
    }

    public static List<SemanticMoodData> getSemanticMoodDataList() {
        return semanticMoodDataList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_report);

        userAnswers = new HashMap<>();
        connectButtonToXml();
        runAnimation();

        Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        // Get the enum filter
        rangeFilter = (UserReportProcessor.RangeFilter) intent.getSerializableExtra("Filter");

        //UserReportProcessor.RangeFilter rangeFilter = intent.get("Username");
        setButtonsInvisible();

        userReportProcessor = UserReportProcessor.getInstance(UserReportActivity.this, username);
        try {
            positiveSentimentUserReport = userReportProcessor.getPositiveSentimentReport(rangeFilter);
            negativeSentimentUserReport = userReportProcessor.getNegativeSentimentReport(rangeFilter);
            allReportsDates = userReportProcessor.getAllReportsDates(rangeFilter);
            rekognitionEmotionsData = userReportProcessor.getRekognitionEmotionsData();
            semanticMoodDataList = userReportProcessor.getSemanticMoodData(rangeFilter);


            // generalUserReport = userReportProcessor.getGeneralSentimentReport(rangeFilter);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setButtonsInvisible() {
        barChart.setVisibility(View.INVISIBLE);
        pieChart.setVisibility(View.INVISIBLE);
        usageBarChart.setVisibility(View.INVISIBLE);
        viewPager.setVisibility(View.INVISIBLE);
        emotionPiechartBtn.setVisibility(View.INVISIBLE);
        emotionBarchartBtn.setVisibility(View.INVISIBLE);
        emotionLocationMapBtn.setVisibility(View.INVISIBLE);
        usageBarchartBtn.setVisibility(View.INVISIBLE);
//        generalReportBtn.setVisibility(View.INVISIBLE);
//        dayAfterDayReport.setVisibility(View.INVISIBLE);
//        semanticMoodBtn.setVisibility(View.INVISIBLE);
    }

    /**
     * create the scroll images with text
     */
    private void setScrollImage() {
        models = new ArrayList<>();
        // list to keep all images as drawable objects
        List<Drawable> imagesList = new ArrayList<>();
        List<String> imageTitleList = new ArrayList<>();
        List<String> imageSentenceList = new ArrayList<>();

        for (int i = 0; i < userChosenPictures.size(); i++) {
            double confidence = 0;
            String reconizedItem = "";
            //image
            imagesList.add(new BitmapDrawable(getResources(), userChosenPictures.get(i).getPicture()));
            //text
            List<RekognitionFeature> currentRekog = userChosenPictures.get(i).getRekognitionFeatures();
            if (currentRekog != null) {
                for (int k = 0; k < userChosenPictures.get(i).getRekognitionFeatures().size(); k++)
                    if (userChosenPictures.get(i).getRekognitionFeatures().get(k).getConfidence() > confidence) {
                        confidence = userChosenPictures.get(i).getRekognitionFeatures().get(k).getConfidence();
                        reconizedItem = userChosenPictures.get(i).getRekognitionFeatures().get(0).getFeatureName();
                    }
            }
            String datePicture = userChosenPictures.get(i).getDate();
            String output = "This picture was taken on " + datePicture + "\nWe have reconized " + reconizedItem + " in a confidence of " + confidence + "%" + " in this picture";
            imageSentenceList.add(output);
            imageTitleList.add(userChosenPictures.get(i).getSemanticPlace());
        }
        for (int k = 0; k < userChosenPictures.size(); k++) {
            models.add(new Model(imagesList.get(k), imageTitleList.get(k), imageSentenceList.get(k)));
            if (k == 4)
                break;
        }
        adapter = new Adapter(models, this);

        viewPager.setAdapter(adapter);
//        viewPager.setPadding(130, 0, 130, 0);
        viewPager.setCurrentItem(0);

        colors = new Integer[]{
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4)
        };

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position < (adapter.getCount() - 1) && position < (colors.length - 1)) {
                    viewPager.setBackgroundColor(
                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                } else {
                    viewPager.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void displayGraphButtonsPosNeg() {
        emotionBarchartBtn.setVisibility(View.VISIBLE);
        emotionPiechartBtn.setVisibility(View.VISIBLE);
        emotionLocationMapBtn.setVisibility(View.VISIBLE);
        usageBarchartBtn.setVisibility(View.VISIBLE);
        generalReportBtn.setVisibility(View.INVISIBLE);
        dayAfterDayReport.setVisibility(View.INVISIBLE);
        semanticMoodBtn.setVisibility(View.INVISIBLE);
        positiveSentimentBtn.setVisibility(View.INVISIBLE);
        negativeSentimentBtn.setVisibility(View.INVISIBLE);
        historyButton.setVisibility(View.INVISIBLE);
        runbotAnimatiaon();
    }

    /**
     * Displays or hide graph's buttons on the screen.
     *
     * @param toShow
     */
    private void displayGraphButtonsPosNeg(boolean toShow) {
        // Show graph buttons
        if (toShow) {
            emotionBarchartBtn.setVisibility(View.VISIBLE);
            emotionPiechartBtn.setVisibility(View.VISIBLE);
            emotionLocationMapBtn.setVisibility(View.VISIBLE);
            usageBarchartBtn.setVisibility(View.VISIBLE);
            generalReportBtn.setVisibility(View.INVISIBLE);
            dayAfterDayReport.setVisibility(View.INVISIBLE);
            semanticMoodBtn.setVisibility(View.INVISIBLE);
            positiveSentimentBtn.setVisibility(View.INVISIBLE);
            negativeSentimentBtn.setVisibility(View.INVISIBLE);
            historyButton.setVisibility(View.INVISIBLE);
            runbotAnimatiaon();
        }
        // Hide graph buttons
        else {
            positiveSentimentBtn.setVisibility(View.VISIBLE);
            negativeSentimentBtn.setVisibility(View.VISIBLE);
            historyButton.setVisibility(View.VISIBLE);
            emotionBarchartBtn.setVisibility(View.INVISIBLE);
            emotionLocationMapBtn.setVisibility(View.INVISIBLE);
            usageBarchartBtn.setVisibility(View.INVISIBLE);
            emotionPiechartBtn.setVisibility(View.INVISIBLE);
            generalReportBtn.setVisibility(View.INVISIBLE);
            dayAfterDayReport.setVisibility(View.INVISIBLE);
            semanticMoodBtn.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Load user's general sentiment report.
     *
     * @param view
     */
    public void loadUserGeneralSentimentStatistics(View view) {
        preventTwoClick(view);
        startOtherActivity(RekognitionEmotionsChartActivity.class);
    }

    /**
     * Load user's general sentiment report.
     *
     * @param view
     */
    public void loadHistory(View view) {
        preventTwoClick(view);
        startOtherActivity(HistoryListActivity.class);
    }

    /**
     * Starts other activity
     *
     * @param c the activity class
     */
    private void startOtherActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.putExtra("Username", username);
        intent.putExtra("Filter", rangeFilter);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    /**
     * Load user's positive sentiment report.
     *
     * @param view
     */
    public void loadUserPositiveSentimentStatistics(View view) {
        preventTwoClick(view);
        reportTypeToDisplay = ReportType.POSITIVE;
        changeScreenData(ReportType.POSITIVE);
    }

    /**
     * Load user's negative sentiment report.
     *
     * @param view
     */
    public void loadUserNegativeSentimentStatistics(View view) {
        preventTwoClick(view);
        reportTypeToDisplay = ReportType.NEGATIVE;
        changeScreenData(ReportType.NEGATIVE);
    }

    /**
     * Displays the chosen report on the screen.
     *
     * @param type
     */
    private void changeScreenData(ReportType type) {
        UserReport reportToDisplay = null;
        switch (type) {
            case POSITIVE:
                reportToDisplay = positiveSentimentUserReport;
                break;
            case NEGATIVE:
                reportToDisplay = negativeSentimentUserReport;
                break;
            default:
                // Shouldn't happen.
        }

        if (reportToDisplay == null) {
            displayGraphButtonsPosNeg(false);
            return;
        }
        displayGraphButtonsPosNeg(true);
        userInfoTextView.setText(reportToDisplay.getReportSummary());
        barChart.setVisibility(View.INVISIBLE);
        pieChart.setVisibility(View.INVISIBLE);
        usageBarChart.setVisibility(View.INVISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        userAnswers = reportToDisplay.getUserAnswers();
        rekognitionDataHandler = new RekognitionDataHandler(reportToDisplay.getRekognitionFeatures());

        userChosenPictures = reportToDisplay.getChosenPictures();
        // System.out.println("chosen pictures: " + userChosenPictures.size());
        setScrollImage();

        userPlaces = reportToDisplay.getUserLocationProperties();
        userLocationPictures = reportToDisplay.getLocationPictures();

        usageInformation = reportToDisplay.getUsageProperties();

    }

    /**
     * Creates Pie chart of emotion distribution based on user's answers.
     *
     * @param view
     */
    public void createPieChart(View view) {

        // Set questions names for graph.
        Map<String, String> questionsNameList = setLabelsNames();


        int currentIndex = 0;
        ArrayList<PieEntry> entries = new ArrayList<>();
        // Set pieChart values.
        for (Map.Entry entry : userAnswers.entrySet()) {
            // Get question number
            String questionName = (String) entry.getKey();

            // question exists
            if (questionsNameList.containsKey(questionName)) {

                questionName = questionsNameList.get(questionName);
                int questionValue = (int) entry.getValue();
                PieEntry pieEntry = new PieEntry(questionValue, questionName);
                entries.add(pieEntry);
            }
            currentIndex = currentIndex + 1;
        }

        // Set pieChart display properties.
        PieDataSet pieDataSet = new PieDataSet(entries, "Names");
        pieDataSet.setValueTextSize(10);
        pieDataSet.setColors(GRAPH_COLORS);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(16);
        pieChart.setData(pieData);
        pieChart.animateXY(3000, 3000);
        Description description = new Description();
        description.setText("");
        pieChart.setEntryLabelTextSize(18);
        pieChart.setDescription(description);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
    }

    /**
     * Displays the pie chart on the screen.
     *
     * @param view
     */
    public void openEmotionPieGraph(View view) {
        viewPager.setVisibility(View.INVISIBLE);
        barChart.setVisibility(View.INVISIBLE);
        usageBarChart.setVisibility(View.INVISIBLE);
        pieChart.setVisibility(View.VISIBLE);
        createPieChart(view);
    }

    /**
     * Displays the bar chart on the screen.
     *
     * @param view
     */
    public void openEmotionBarGraph(View view) {
        viewPager.setVisibility(View.INVISIBLE);
        pieChart.setVisibility(View.INVISIBLE);
        usageBarChart.setVisibility(View.INVISIBLE);
        barChart.setVisibility(View.VISIBLE);


        List<RekognitionFeature> rekognitionFeatures;
        rekognitionFeatures = rekognitionDataHandler.getTopNFeaturesByDistribution(7);
        ArrayList<BarEntry> entries = setEntriesForBarGraph(rekognitionFeatures);

        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(false);
        barChart.animateY(1500);

        setupXAxis(rekognitionFeatures);
        // Set the data and list of labels into chart.
        BarDataSet bardataset = new BarDataSet(entries, "");
        bardataset.setValueTextSize(12);
        bardataset.setColors(GRAPH_COLORS);
        // Set bar with the new data.
        BarData data = new BarData(bardataset);

        barChart.setData(data);
        barChart.invalidate(); // refresh

    }

    /**
     * Setup X axis with labels.
     *
     * @return ValueFormatter formatter for the dataset.
     */
    private void setupXAxis(List<RekognitionFeature> rekognitionFeatures) {
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        for (int index = 0; index < rekognitionFeatures.size(); index++) {
            xAxisLabel.add(rekognitionFeatures.get(index).getFeatureName());
        }
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return xAxisLabel.get((int) value % xAxisLabel.size());
            }
        };
        XAxis xAxis = barChart.getXAxis();
        YAxis rightAxis = barChart.getAxisRight();
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextSize(12);
        rightAxis.setTextSize(12);
        xAxis.setTextSize(10);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setValueFormatter(formatter);
    }

    /**
     * Set the Bar entries for Bar Chart.
     *
     * @param rekognitionFeatures
     * @return
     */
    private ArrayList<BarEntry> setEntriesForBarGraph(List<RekognitionFeature> rekognitionFeatures) {
        ArrayList<BarEntry> entriesData = new ArrayList<>();
        for (int index = 0; index < rekognitionFeatures.size(); index++) {
            BarEntry barEntry = new BarEntry(index, (float) rekognitionFeatures.get(index).getDistribution());
            entriesData.add(barEntry);
        }
        return entriesData;
    }

    private Map<String, String> setLabelsNames() {
        Map<String, String> questionsNameList = new HashMap<>();

        // Which question's to display
        switch (reportTypeToDisplay) {
            case POSITIVE:
                questionsNameList.put("quest_3", "Alert");
                questionsNameList.put("quest_5", "Inspired");
                questionsNameList.put("quest_7", "Determined");
                questionsNameList.put("quest_8", "Attentive");
                questionsNameList.put("quest_10", "Active");
                break;
            case NEGATIVE:
                questionsNameList.put("quest_1", "Upset");
                questionsNameList.put("quest_2", "Hostile");
                questionsNameList.put("quest_4", "Ashamed");
                questionsNameList.put("quest_6", "Nervous");
                questionsNameList.put("quest_9", "Afraid");
                break;
            default:
                questionsNameList.put("quest_1", "Upset");
                questionsNameList.put("quest_2", "Hostile");
                questionsNameList.put("quest_3", "Alert");
                questionsNameList.put("quest_4", "Ashamed");
                questionsNameList.put("quest_5", "Inspired");
                questionsNameList.put("quest_6", "Nervous");
                questionsNameList.put("quest_7", "Determined");
                questionsNameList.put("quest_8", "Attentive");
                questionsNameList.put("quest_9", "Afraid");
                questionsNameList.put("quest_10", "Active");
        }
        return questionsNameList;
    }

    /**
     * run animation on page
     */
    private void runAnimation() {
        Animation animationtopdown = AnimationUtils.loadAnimation(UserReportActivity.this, R.anim.toptodown);
        Animation animationbot = AnimationUtils.loadAnimation(UserReportActivity.this, R.anim.bottotop);
        historyButton.startAnimation(animationtopdown);
        negativeSentimentBtn.startAnimation(animationtopdown);
        positiveSentimentBtn.startAnimation(animationtopdown);
        generalReportBtn.startAnimation(animationbot);
        dayAfterDayReport.startAnimation(animationbot);
        semanticMoodBtn.startAnimation(animationbot);
    }

    private void runbotAnimatiaon() {
        Animation animationbot = AnimationUtils.loadAnimation(UserReportActivity.this, R.anim.bottotop);
        Animation animationzoom = AnimationUtils.loadAnimation(UserReportActivity.this, R.anim.zoomin);
        emotionBarchartBtn.startAnimation(animationbot);
        emotionLocationMapBtn.startAnimation(animationbot);
        emotionPiechartBtn.startAnimation(animationbot);
        usageBarchartBtn.startAnimation(animationbot);
        userInfoTextView.startAnimation(animationzoom);
    }

    /**
     * connect buttons from xml to class
     */
    private void connectButtonToXml() {
        pieChart = findViewById(R.id.pieChartReport);
        barChart = findViewById(R.id.barChartReport);
        usageBarChart = findViewById(R.id.usageChart);
        viewPager = findViewById(R.id.viewPager);
        userInfoTextView = findViewById(R.id.userPositiveInfoTextView);
        emotionBarchartBtn = findViewById(R.id.emotionBarchartBtn);
        emotionPiechartBtn = findViewById(R.id.emotionPiechartBtn);
        generalReportBtn = findViewById(R.id.generalReportBtn);
        dayAfterDayReport = findViewById(R.id.dayAfterDayReport);
        semanticMoodBtn = findViewById(R.id.semanticMoodBtn);
        negativeSentimentBtn = findViewById(R.id.negativeSentimentBtn);
        positiveSentimentBtn = findViewById(R.id.positiveSentimentBtn);
        emotionLocationMapBtn = findViewById(R.id.emotionLocationMapBtn);
        usageBarchartBtn = findViewById(R.id.usageBarchartBtn);
        historyButton = findViewById(R.id.historyButton);

    }


    public void openEmotionLocationMap(View view) {
        preventTwoClick(view);
        if (userPlaces.size() < 2) {
            Toast.makeText(getApplicationContext(), "Not enough places for tour", Toast.LENGTH_LONG).show();
        } else {
            System.out.println("show locations on map");
            Intent intent = new Intent(this, PlacesOnMapActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // todo show button only if user have more than 1 place in db!
            System.out.println("size before sent to intent: " + userPlaces.size());
            intent.putParcelableArrayListExtra("places", (ArrayList<? extends Parcelable>) userPlaces);
            //intent.putParcelableArrayListExtra("pictures", (ArrayList<? extends Parcelable>) userLocationPictures);
            // IMAGES ON MAP CANCELED FOR NOW


            startActivity(intent);
        }
    }


    /**
     * private ByteArrayOutputStream[] convertBitmapToByte(List<Bitmap> userLocationPictures) {
     * List<ByteArrayOutputStream> byteArrayOutputStreams = new ArrayList<>();
     * for (int position = 0; position < userLocationPictures.size(); position++){
     * ByteArrayOutputStream stream = new ByteArrayOutputStream();
     * userLocationPictures.get(position).compress(Bitmap.CompressFormat.PNG, 100, stream);
     * byte[] byteArray = stream.toByteArray();
     * <p>
     * }
     * }
     **/

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * Displays the usage chart on the screen.
     *
     * @param view
     */
    public void openUsageGraph(View view) {
        viewPager.setVisibility(View.INVISIBLE);
        barChart.setVisibility(View.INVISIBLE);
        pieChart.setVisibility(View.INVISIBLE);
        usageBarChart.setVisibility(View.VISIBLE);
        createUsageChart(view);
    }

    /**
     * Creating usage chart.
     *
     * @param view
     */
    public void createUsageChart(View view) {
        float barWidth;
        float barSpace;
        float groupSpace;
        // Chart design.
        barWidth = 0.2f;
        barSpace = 0f;
        groupSpace = 0.2f;
        int groupCount = usageInformation.size();

        // Need to move to the on create.
        usageBarChart.setDescription(null);
        usageBarChart.setPinchZoom(false);
        usageBarChart.setScaleEnabled(false);
        usageBarChart.setDrawBarShadow(false);
        usageBarChart.setDrawGridBackground(false);

        // Xaxis - Dates.
        ArrayList xVals = new ArrayList();
        xVals = getXvaluesUsage(usageInformation);

        // Usage information columns.
        ArrayList whatsappValues = new ArrayList();
        ArrayList instagramValues = new ArrayList();
        ArrayList facebookValues = new ArrayList();
        ArrayList youtubeValues = new ArrayList();
        // Updating columns values.
        setUsageColumnsInformation(usageInformation, whatsappValues, instagramValues
                , facebookValues, youtubeValues);

        // Colors of labels. e.g. facebook's color.
        int[] colors = {Color.rgb(252, 175, 69),
                Color.rgb(37, 211, 102),
                Color.rgb(66, 103, 178),
                Color.rgb(255, 0, 0)};

        // Sets of usage information for user's applications.
        BarDataSet setInstagram, setWhatsapp, setFacebook, setYoutube;
        setInstagram = new BarDataSet(instagramValues, "Instagram");
        setInstagram.setColor(colors[0]);

        setWhatsapp = new BarDataSet(whatsappValues, "Whatsapp");
        setWhatsapp.setColor(colors[1]);

        setFacebook = new BarDataSet(facebookValues, "Facebook");
        setFacebook.setColor(colors[2]);

        setYoutube = new BarDataSet(youtubeValues, "Youtube");
        setYoutube.setColor(colors[3]);
        // Grouping the data from different applications.
        BarData dataUsage = new BarData(setInstagram, setWhatsapp, setFacebook, setYoutube);
        dataUsage.setValueFormatter(new LargeValueFormatter());
        usageBarChart.setData(dataUsage);
        usageBarChart.getBarData().setBarWidth(barWidth);
        usageBarChart.getXAxis().setAxisMinimum(0);
        usageBarChart.getXAxis().setAxisMaximum(0 +
                usageBarChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        usageBarChart.groupBars(0, groupSpace, barSpace);
        usageBarChart.getData().setHighlightEnabled(false);
        usageBarChart.invalidate();


        //X-axis.
        XAxis xAxisUsageBar = usageBarChart.getXAxis();
        xAxisUsageBar.setGranularity(1f);
        xAxisUsageBar.setGranularityEnabled(true);
        xAxisUsageBar.setCenterAxisLabels(true);
        xAxisUsageBar.setDrawGridLines(false);
        xAxisUsageBar.setAxisMaximum(5);
        xAxisUsageBar.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisUsageBar.setValueFormatter(new IndexAxisValueFormatter(xVals));

        //Y-axis.
        usageBarChart.getAxisRight().setEnabled(false);
        YAxis leftAxisUsageBar = usageBarChart.getAxisLeft();
        leftAxisUsageBar.setValueFormatter(new LargeValueFormatter());
        leftAxisUsageBar.setDrawGridLines(true);
        leftAxisUsageBar.setSpaceTop(35f);
        leftAxisUsageBar.setAxisMinimum(0f);
        leftAxisUsageBar.setGranularity(1f);

        // Animate chart.
        usageBarChart.animateXY(1500, 3000);
        usageBarChart.invalidate();


    }

    /**
     * Updating the columns's values of the chart.
     *
     * @param usageInformation - user's usage information.
     * @param whatsappValues   - whatsapp usage.
     * @param instagramValues  - instagram usage.
     * @param facebookValues   - facebook usage.
     * @param youtubeValues    - youtube usage.
     */
    private void setUsageColumnsInformation(List<UsageProperties> usageInformation,
                                            ArrayList whatsappValues,
                                            ArrayList instagramValues,
                                            ArrayList facebookValues,
                                            ArrayList youtubeValues) {
        for (int i = 0; i < usageInformation.size(); i++) {
            for (Map.Entry<String, String> entry :
                    usageInformation.get(i).getApplicationAndTime().entrySet()) {
                String appName = entry.getKey();
                String time = entry.getValue();
                float timeFloat = parseToFloatFromString(time);

                switch (appName) {
                    case "whatsapp":
                        whatsappValues.add(new BarEntry(i, timeFloat));
                        break;
                    case "instagram":
                        instagramValues.add(new BarEntry(i, timeFloat));
                        break;
                    case "facebook":
                        facebookValues.add(new BarEntry(i, timeFloat));
                        break;
                    case "youtube":
                        youtubeValues.add(new BarEntry(i, timeFloat));
                        break;
                    default:
                        System.out.println(" unrecognized application");
                }
            }
        }
    }

    /**
     * Converting the time from string to float (hours). e.g. 01:30:00 as string -> 1.5 hour.
     *
     * @param time - string containing the usage's time.
     * @return hours of usage as float, keeps only 3 digits after decimal point.
     */
    private float parseToFloatFromString(String time) {
        time.split(":");
        String[] splitTime = time.split("[:]");
        float totalTime = Float.parseFloat(splitTime[0]) + (Float.parseFloat(splitTime[1]) / 60) + (Float.parseFloat(splitTime[2]) / 3600);
        totalTime = Float.parseFloat(new DecimalFormat("##.###").format(totalTime));
        return totalTime;
    }

    /**
     * Set the dates values to xAxis in usage's chart.
     *
     * @param usageInformation - user's usage information.
     * @return xAxis's dates.
     */
    private ArrayList getXvaluesUsage(List<UsageProperties> usageInformation) {
        ArrayList xVals = new ArrayList();
        for (int i = 0; i < usageInformation.size(); i++) {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String strDate = dateFormat.format(usageInformation.get(i).getDate());
            xVals.add(strDate);

        }
        return xVals;
    }

    public void showWeeklyReport(View view) {
        preventTwoClick(view);
        startOtherActivity(XYGraphActivity.class);
    }

    public void showSemanticLocationReport(View view) {
        startOtherActivity(SemanticMoodBubleActivity.class);
    }
}
