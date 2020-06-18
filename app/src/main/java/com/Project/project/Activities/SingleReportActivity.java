package com.Project.project.Activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Project.project.R;
import com.Project.project.RekognitionManagment.RekognitionFeature;
import com.Project.project.Report.SingleReport;
import com.Project.project.Report.UserReportProcessor;
import com.Project.project.UsageManagment.UsageProperties;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SingleReportActivity extends AppCompatActivity {
    Map<String, String> questionsMapToNames;
    TextView dateTV;
    TextView moodTV;
    ImageView userPictureIV;
    ListView questionsLV;
    TextView questionAnswerTV;
    List<String> questionsNames;
    TextView locationTV;
    TextView featuresTV;
    TextView usageTV;
    TextView title;
    SpannableString text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_report);

        Date chosenDate = HistoryListActivity.getSelectedDate();
        SingleReport singleReport =
                UserReportProcessor.getInstance(null, null)
                        .getSingleSentimentReport(chosenDate);
        Toast.makeText(SingleReportActivity.this, "singleReport: " +
                singleReport.getDate(), Toast.LENGTH_SHORT).show();

        //connect xml and class
        connectXML();
        showReport(singleReport);
        runAnimation();

    }

    /**
     * show all the report info nicely
     *
     * @param singleReport - object with data of report
     */
    private void showReport(SingleReport singleReport) {
        // DATE PART.
        text = getSpannableString("Date Of Questionnaire\n", singleReport.getDate().toString());
        dateTV.setText(text, TextView.BufferType.SPANNABLE);

        // PICTURE PART.
        if (singleReport.getPicture() != null) {
            userPictureIV.setImageBitmap(singleReport.getPicture());
        }

        // QUESTIONS ANSWERS PART.
        text = getSpannableString("Your Mood Grade Was: ", "" + (singleReport.getUserAnswers().get("mood")));
        moodTV.setText(text, TextView.BufferType.SPANNABLE);

        questionsMapToNames = QuestionnaireActivity.getQuestionNamesInDbToOriginalNames();
        fetchNamesAndScores(singleReport.getUserAnswers());

        // LOCATION PART.
        System.out.println("$$" + singleReport.getUserLocationProperties());
        if (singleReport.getUserLocationProperties() != null) {
            text = getSpannableString("Gps Location\n", singleReport.getUserLocationProperties().getAddress());
            locationTV.setText(text, TextView.BufferType.SPANNABLE);
//                    + "\n\nYou Have Categorized This Place As: " + singleReport.getUserLocationProperties().getSentimentPlace());
        }

        // Rekognition features part.
        if (singleReport.getRekognitionFeatures() != null) {
            fetchFeatures(singleReport.getRekognitionFeatures());
        }

        // Applications usage part.
        if (singleReport.getUsageProperties() != null) {
            fetchUsage(singleReport.getUsageProperties());
        }
    }


    /**
     * get app usage - whatsupp youtube ...
     *
     * @param usageProperties
     */
    private void fetchUsage(UsageProperties usageProperties) {
        String usage = "";
        for (Map.Entry<String, String> entry : usageProperties.getApplicationAndTime().entrySet()) {
            usage = usage + "\n" + entry.getKey() + " : " + entry.getValue();
        }
        text = getSpannableString("Application Usage", "" + (usage));
        usageTV.setText(text, TextView.BufferType.SPANNABLE);
    }

    /**
     * get recognition that were recognized in pictures
     *
     * @param rekognitionFeatures
     */
    private void fetchFeatures(List<RekognitionFeature> rekognitionFeatures) {
        StringBuilder features = new StringBuilder();
        for (RekognitionFeature rekognitionFeature : rekognitionFeatures) {
            features.append("" + rekognitionFeature.getFeatureName() + ", ");
        }
        if (features.length() > 3)
            features.setCharAt(features.length() - 2, '.');
        text = getSpannableString("Features Recognized In Picture\n", "" + (features));
        featuresTV.setText(text, TextView.BufferType.SPANNABLE);
    }

    /**
     * get the answers of user for questionnaire
     *
     * @param userAnswers
     */
    private void fetchNamesAndScores(Map<String, Integer> userAnswers) {
        String outputQuestionsAndAnswers = "";
        for (Map.Entry<String, Integer> entry : userAnswers.entrySet()) {
            if (entry.getKey() != "mood") {
                String questionId = questionsMapToNames.get(entry.getKey());
                int questionScore = entry.getValue();
                outputQuestionsAndAnswers = outputQuestionsAndAnswers + "\n" +
                        questionId + ": " + questionScore;
            }
        }
        text = getSpannableString("Feeling Rating (1-5)", "" + (outputQuestionsAndAnswers));
        questionAnswerTV.setText(text, TextView.BufferType.SPANNABLE);
    }

    /**
     * connect xml and gui
     */
    private void connectXML() {
        title = findViewById(R.id.title_history);
        dateTV = findViewById(R.id.dateTV);
        userPictureIV = findViewById(R.id.userPictureIV);
        moodTV = findViewById(R.id.moodTV);
        locationTV = findViewById(R.id.locationTV);
        featuresTV = findViewById(R.id.featuresTV);
        usageTV = findViewById(R.id.usageTV);
        questionAnswerTV = findViewById(R.id.questionAnswerTV);

    }

    /**
     * show text nicly
     *
     * @param bold    - bolded text
     * @param notBold - no bolded text
     * @return
     */
    private SpannableString getSpannableString(String bold, String notBold) {
        SpannableString text = new SpannableString(bold + notBold);
        text.setSpan(new StyleSpan(Typeface.BOLD), 0, bold.length(), 0);
        text.setSpan(new ForegroundColorSpan(Color.parseColor("#518ac2")), 0, bold.length(), 0);
        return text;
    }

    /**
     * run animation on page
     */
    private void runAnimation() {
        Animation animationRight = AnimationUtils.loadAnimation(SingleReportActivity.this, R.anim.righttoleft);
        Animation animationLeft = AnimationUtils.loadAnimation(SingleReportActivity.this, R.anim.lefttoright);
        Animation animationTop = AnimationUtils.loadAnimation(SingleReportActivity.this, R.anim.toptodown);
        dateTV.startAnimation(animationLeft);
        userPictureIV.startAnimation(animationRight);
        moodTV.startAnimation(animationLeft);
        locationTV.startAnimation(animationLeft);
        featuresTV.startAnimation(animationLeft);
        usageTV.startAnimation(animationLeft);
        questionAnswerTV.startAnimation(animationLeft);
        title.startAnimation(animationTop);
    }


    // Questions names.
    /**
     questionsMapToNames = QuestionnaireActivity.getQuestionNamesInDbToOriginalNames();
     questionsNames = fetchNamesAndScore(singleReport.getUserAnswers());
     questionsLV = (ListView) findViewById(R.id.questionsLV);
     // Setting adapter to the list.
     ArrayAdapter ad = new ArrayAdapter(this,
     android.R.layout.simple_list_item_1, questionsNames);
     // Connecting the list view to the adapter.
     questionsLV.setAdapter(ad);
     **/
}
