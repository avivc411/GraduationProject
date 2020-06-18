package com.Project.project.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.Project.project.DB.DataUploader;
import com.Project.project.R;
import com.Project.project.UsageManagment.UsageRetriever;
import com.Project.project.Utilities.ListViewAdapter;
import com.Project.project.Utilities.MoodAlert;
import com.Project.project.Utilities.Question;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.Project.project.Utilities.PreventTwoClick.preventTwoClick;

public class QuestionnaireActivity extends AppCompatActivity implements MoodAlert.SingleChoiceListener {
    String rowId;
    Button sendQuest;
    private ListView listView;
    private ArrayAdapter<Question> adapter;
    private ArrayList<Question> questionsList;
    private ArrayList<Integer> imageList;
    int mood = 3;
    private ParseObject newQuestionnaire;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("3a40729a203a63626664fdb4f37a1bd8802c1581")
                // if defined
                .clientKey("1872ec8a614edffc2e594daa17dd6355f0f6140a")
                .server("http://3.19.234.120:80/parse/")
                .build()
        );
        // Toast.makeText(Questionnaire.this, "userName: "+userName , Toast.LENGTH_LONG).show();
        listView = findViewById(R.id.list_view);
        setListOfData();
        adapter = new ListViewAdapter(this, R.layout.item_listview, questionsList, imageList);
        listView.setAdapter(adapter);
        connectButtonToXml();
        runAnimation();
    }

    // the function send the users's answers to the server
    public void sendQuestionnaire(View view) {
        preventTwoClick(view);


        // todo with DataUploader.
        // DB update with new data.
        dialog = new ProgressDialog(QuestionnaireActivity.this);
        dialog.setMessage("Sending questionnaire to server, Please wait a few seconds");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
        Toast.makeText(QuestionnaireActivity.this, "sending..", Toast.LENGTH_SHORT).show();

        // Calculates positive and negative sentiment questions score.
        int positiveScore = getPositiveScore(adapter);
        int negativeScore = getNegativeScore(adapter);
        // db update.
        newQuestionnaire = new ParseObject("Questionnaire");
        newQuestionnaire.put("total_pos_score", positiveScore);
        newQuestionnaire.put("total_neg_score", negativeScore);
        newQuestionnaire.put("total_combination_score", positiveScore + negativeScore);
        newQuestionnaire.put("upload_date", new Date());
        newQuestionnaire.put("user", getIntent().getStringExtra("Username"));

        // Updating score for each question in db.
        for (int i = 0; i < adapter.getCount(); i++) {
            String colName = "quest_" + (i + 1);
            newQuestionnaire.put(colName, adapter.getItem(i).getRating());
        }

        // Show Mood dialog, and finish the activity only after choosing the user's mood.
        DialogFragment singleChoice = new MoodAlert();
        singleChoice.show(getSupportFragmentManager(), "Single choice dialog");
    }

    /**
     * Updates information about user's location.
     */
    @SuppressLint("LongLogTag")
    public void TakePicture() {
        //Update Usage in db
        if (DataUploader.getInstance(this)
                .uploadUsageDetails(UsageRetriever.getInstance(this).getData(), rowId))
            Log.i("Questionnaire Usage Upload", "success");
        else Log.i("Questionnaire Usage Upload", "failure");
        finish();
        // Opens new activity - take picture.
        startOtherActivity(TakePictureActivity.class, rowId);
    }

    /**
     * Calculates positive sentiment questions score from user's questionnaire.
     *
     * @param adapter
     * @return positive sentiment total score for questionnaire.
     */
    private int getPositiveScore(ArrayAdapter<Question> adapter) {
        int positiveQuestionsScore = 0;
        for (int i = 0; i < 5; i++) {
            positiveQuestionsScore += adapter.getItem(i).getRating();
        }
        return positiveQuestionsScore;
    }

    /**
     * Calculates negative sentiment questions score from user's questionnaire.
     *
     * @param adapter
     * @return negative sentiment total score for questionnaire.
     */
    private int getNegativeScore(ArrayAdapter<Question> adapter) {
        int negativeQuestionsScore = 0;
        for (int i = 5; i < adapter.getCount(); i++) {
            negativeQuestionsScore += adapter.getItem(i).getRating();
        }
        return negativeQuestionsScore;
    }

    // Initializing questionnaire's questions list.
    private void setListOfData() {
        // Initializing list from top.
        questionsList = new ArrayList<>();
        questionsList.add(new Question(1, "Upset", 4, R.drawable.smile1));
        questionsList.add(new Question(2, "Hostile", 4, R.drawable.smile2));
        questionsList.add(new Question(3, "Alert", 4, R.drawable.smile3));
        questionsList.add(new Question(4, "Ashamed", 4, R.drawable.smile4));
        questionsList.add(new Question(5, "Inspired", 4, R.drawable.smile5));
        questionsList.add(new Question(6, "Nervous", 4, R.drawable.smile6));
        questionsList.add(new Question(7, "Determined", 4, R.drawable.smile7));
        questionsList.add(new Question(8, "Attentive", 4, R.drawable.smile8));
        questionsList.add(new Question(9, "Afraid", 4, R.drawable.smile9));
        questionsList.add(new Question(10, "Active", 4, R.drawable.smile10));
    }

    public static Map<String, String> getQuestionNamesInDbToOriginalNames() {
        Map<String, String> questions = new HashMap<>();
        questions.put("quest_1", "Upset");
        questions.put("quest_2", "Hostile");
        questions.put("quest_3", "Alert");
        questions.put("quest_4", "Ashamed");
        questions.put("quest_5", "Inspired");
        questions.put("quest_6", "Nervous");
        questions.put("quest_7", "Determined");
        questions.put("quest_8", "Attentive");
        questions.put("quest_9", "Afraid");
        questions.put("quest_10", "Active");
        questions.put("mood", "Mood");
        return questions;
    }

    public static Map<String, String> getOriginalNamesToQuestionNamesInDb() {
        Map<String, String> questions = new HashMap<>();
        questions.put("Upset", "quest_1");
        questions.put("Hostile", "quest_2");
        questions.put("Alert", "quest_3");
        questions.put("Ashamed", "quest_4");
        questions.put("Inspired", "quest_5");
        questions.put("Nervous", "quest_6");
        questions.put("Determined", "quest_7");
        questions.put("Attentive", "quest_8");
        questions.put("Afraid", "quest_9");
        questions.put("Active", "quest_10");
        questions.put("Mood", "mood");
        return questions;
    }

    /**
     * Starting another activity.
     *
     * @param c     - the new activity class to start.
     * @param rowId - the questionnaire's id.
     */
    private void startOtherActivity(Class c, String rowId) {
        finish();
        Intent intent = new Intent(this, c);
        intent.putExtra("rowId", rowId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runAnimation();
    }

    /**
     * run animation on page
     */
    private void runAnimation() {
        Animation animationbot = AnimationUtils.loadAnimation(QuestionnaireActivity.this, R.anim.toptodown);
        Animation animationfade = AnimationUtils.loadAnimation(QuestionnaireActivity.this, R.anim.lefttoright);
        listView.startAnimation(animationfade);
        sendQuest.startAnimation(animationbot);
    }

    /**
     * connect xml and classes
     */
    private void connectButtonToXml() {
        sendQuest = findViewById(R.id.sendQuestionnaireBtn);
    }

    @Override
    public void onAgreeButtonClicked(String[] list, int position) {
        System.out.println("selected mood grade: " + list[position]);
        mood = Integer.parseInt(list[position]);

        try {
            newQuestionnaire.put("mood", mood);
            newQuestionnaire.save();
            // ID of inserted row.
            rowId = newQuestionnaire.getObjectId();
            Map<String, String> setting = new SettingActivity().getSetting();
            System.out.println(setting.get("Camera"));
            if (setting.get("Camera").equals("1"))
                TakePicture();
            else if (setting.get("GPS").equals("1"))
                startOtherActivity(GPSActivity.class, rowId);
            else
                finish();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            dialog.dismiss();
        }
    }
}