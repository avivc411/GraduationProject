package com.Project.project.Fitbit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;

import com.Project.project.Activities.FitbitPermissionsActivity;
import com.Project.project.Fitbit.Utilities.ActivitySample;
import com.Project.project.Fitbit.Utilities.FitbitSample;
import com.Project.project.Fitbit.Utilities.HeartRateSample;
import com.Project.project.Fitbit.Utilities.HttpConnector;
import com.Project.project.Fitbit.Utilities.SleepSample;
import com.Project.project.Handlers.FitbitDataHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FitbitDataManager {
    private static FitbitDataManager manager;
    private final int YEARInSeconds = 31536000, MONTHInSeconds = 2592000, WEEKInSeconds = 604800, DAYInSeconds = 86400;
    private final String SECOND = "1sec", DAY = "1d", WEEK = "1w", MONTH = "1m", TODAY = "today";
    //intent filter in Manifest - android:scheme :// android:host
    private final String redirectUri = "emotionanalyzer" + "%3A%2F%2F" + "FitbitPermissionsActivity";
    private final String type = "token";
    private final String id = "22BNWP";
    private final String tokenPrefKey = "FitbitAccessToken", userPrefKey = "FitbitUserId";
    private String accessToken, refreshToken, userId;
    private Context context;

    public FitbitDataManager(@NonNull Context context) {
        this.context = context;
    }

    public static FitbitDataManager getInstance(Context context) {
        if (manager == null)
            manager = new FitbitDataManager(context);
        manager.context = context;
        return manager;
    }

    /**
     * Open Fitbit's allowance page in order to give this app permissions to read user's data.
     * After success, the web page calling {@link redirectUri} and
     * opens {@link FitbitPermissionsActivity} activity.
     *
     * @param scope List of data types to ask for.
     */
    public void authenticate(@Nullable List<String> scope) {
        if (readToken())
            return;
        if (scope == null) {
            scope = new ArrayList<>();
            scope.add("sleep");
            scope.add("activity");
            scope.add("heartrate");
        } else if (scope.size() == 0)
            throw new IllegalArgumentException("Scope is empty, operation not required and therefor is canceled.");

        String expirationTime = String.valueOf(YEARInSeconds);

        StringBuilder url = new StringBuilder(String.format(
                "https://www.fitbit.com/oauth2/authorize?" +
                        "response_type=%s" +
                        "&client_id=%s" +
                        "&redirect_uri=%s" +
                        "&prompt=login" +
                        "&expires_in=%s",
                type,
                id,
                redirectUri,
                expirationTime));

        url.append("&scope=");
        for (String attribute : scope)
            url.append(attribute).append("%20");
        //remove redundant %20
        if ("%20".equals(url.substring(url.length() - 3)))
            url = new StringBuilder(url.substring(0, url.length() - 3));

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(url.toString()));
    }

    /**
     * Read given uri, extract access token and user id.
     *
     * @param uri URI from Fitbit allowance page.
     * @throws Exception Error in writing to shared preferences.
     */
    public void readUri(@NonNull Uri uri) throws Exception {
        String data, token, userId;
        data = uri.toString();

        int iToken = data.indexOf("access_token=") + "access_token=".length();
        int iUser = data.indexOf("user_id=") + "user_id=".length();
        //indexOf for token and userid not equal to -1
        if (iToken != 12 && iUser != 7) {
            token = data.substring(iToken, data.indexOf('&'));
            userId = data.substring(iUser, iUser + 6);
            if (!setTokenAndUser(token, userId))
                throw new Exception("Write Token Failed!");
        }
    }

    /**
     * Read token from shared preferences.
     *
     * @return Success of failure.
     */
    private boolean readToken() {
        if (accessToken != null && !accessToken.equals("") &&
                userId != null && !userId.equals(""))
            return true;
        SharedPreferences preferences = context.getSharedPreferences("EmotionAnalyzer", Context.MODE_PRIVATE);
        String token = preferences.getString(tokenPrefKey, ""),
                userId = preferences.getString(userPrefKey, "");
        if (token.equals("") || userId.equals("")) {
            Log.d("Fitbit", "readToken: token isn't stored in shared pref");
            return false;
        }
        accessToken = token;
        this.userId = userId;
        return true;
    }

    /**
     * Set token and write to shared preferences.
     *
     * @param token  Token from Fitbit.
     * @param userId User's id from Fitbit.
     * @return Success of failure.
     */
    private boolean setTokenAndUser(String token, String userId) {
        accessToken = token;
        SharedPreferences preferences = context.getSharedPreferences("EmotionAnalyzer", Context.MODE_PRIVATE);
        Editor editor = preferences.edit();

        editor.putString(tokenPrefKey, token);
        editor.putString(userPrefKey, userId);
        return editor.commit();
    }

    /**
     * Get data from Fitbit in background.
     *
     * @param type    Data type
     * @param handler If inBackground, send response data to handler.
     */
    public void getDataInBackground(DataType type, FitbitDataHandler handler) {
        new Thread(() -> {
            try {
                List<FitbitSample> samples = getData(type);
                handler.onSuccess(samples);
            } catch (Exception e) {
                handler.onFailure(e);
            }
        }).start();
    }

    /**
     * Get data from Fitbit.
     *
     * @param type Data type.
     * @return List of samples, respectively to type.
     */
    public List<FitbitSample> getData(DataType type) {
        if (!readToken())
            throw new SecurityException("Can't read tokens.");
        String url;
        switch (type) {
            case Activity:
                url = getActivityUrl();
                break;
            case HeartRate:
                url = getHeartRateUrl();
                break;
            case Sleep:
                url = getSleepUrl();
                break;
            default:
                throw new UnsupportedOperationException("Type not supported");
        }
        return getData(url, type);
    }

    /**
     * Create HTTP GET call for Fitbit api.
     * Return list of samples after decoding JSON answer.
     *
     * @param url  Url matched to desired data type.
     * @param type Required data type.
     */
    private List<FitbitSample> getData(String url, DataType type) {
        HttpConnector httpConnector = HttpConnector.getInstance();
        JSONObject response = httpConnector.run(url, accessToken);
        if (response == null) {
            return null;
        }
        List<FitbitSample> samples = null;
        try {
            switch (type) {
                case Activity:
                    samples = new ArrayList<>();
                    JSONArray minutesVeryActiveArray = response.getJSONArray("activities-minutesVeryActive");
                    JSONObject jsonActivitySample = minutesVeryActiveArray.getJSONObject(0);
                    ActivitySample activitySample = new ActivitySample(jsonActivitySample.getInt("value"));
                    samples.add(activitySample);
                    break;
                case HeartRate:
                    samples = new ArrayList<>();
                    JSONArray activities_heartArray = response.getJSONArray("activities-heart");
                    JSONObject value = activities_heartArray.getJSONObject(0).getJSONObject("value");
                    JSONArray heartRateZones = value.getJSONArray("heartRateZones");
                    for (int i = 0; i < heartRateZones.length(); i++) {
                        JSONObject jsonSample = heartRateZones.getJSONObject(i);
                        if ("Out of Range".equals(jsonSample.getString("name")))
                            continue;
                        samples.add(new HeartRateSample(
                                jsonSample.getInt("max"),
                                jsonSample.getInt("min"),
                                jsonSample.getString("name")
                        ));
                    }
                    break;
                case Sleep:
                    samples = new ArrayList<>();
                    JSONObject summary = response.getJSONObject("summary");
                    SleepSample sleepSample = new SleepSample(
                            summary.getInt("totalMinutesAsleep"),
                            summary.getInt("totalSleepRecords"),
                            summary.getInt("totalTimeInBed")
                    );
                    samples.add(sleepSample);
                    break;
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            try {
                int code = response.getInt("code");
                String error = response.getString("error");
                Log.d("FitbitManager", "getData: " + code + " - " + error);
                if (code == 403)
                    reAuthenticate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return samples;
    }

    /**
     * In case of token expired, re-authenticate this app
     */
    private void reAuthenticate() {
        setTokenAndUser(null, null);
        authenticate(null);
    }

    /**
     * @return Required URL in order to send request for user's sleep data from Fitbit.
     */
    private String getSleepUrl() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = format.format(new Date(System.currentTimeMillis()));
        return String.format("https://api.fitbit.com/1.2/user/" +
                        "%s" +
                        "/sleep/date/" +
                        "%s" +
                        ".json",
                userId,
                dateString);
    }

    /**
     * @return Required URL in order to send request for user's activity data from Fitbit.
     */
    private String getActivityUrl() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = format.format(new Date(System.currentTimeMillis()));
        //get all activities
        /*return String.format("https://api.fitbit.com/1/user/" +
                        "%s" +
                        "/activities/date/" +
                        "%s" +
                        ".json",
                userId,
                dateString);*/
        //get very Active time
        return String.format("https://api.fitbit.com/1/user/" +
                        "%s" +
                        "/activities/minutesVeryActive/date/" +
                        TODAY +
                        "/" +
                        DAY +
                        ".json",
                userId);
    }

    /**
     * @return Required URL in order to send request for user's heart rate data from Fitbit.
     */
    private String getHeartRateUrl() {
        return String.format("https://api.fitbit.com/1/user/" +
                        "%s" +
                        "/activities/heart/date/" +
                        TODAY +
                        "/" +
                        DAY +
                        ".json",
                userId);
    }

    public enum DataType {HeartRate, Activity, Sleep}
}
