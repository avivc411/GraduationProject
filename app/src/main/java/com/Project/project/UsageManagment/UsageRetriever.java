package com.Project.project.UsageManagment;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UsageRetriever {
    private static UsageRetriever instance;
    private Context context;
    private Set<String> packagesToTrack;

    private UsageRetriever(Context context) {
        this.context = context;
        buildPackagesToTrack();
    }

    public static UsageRetriever getInstance(Context context) {
        if (instance == null)
            instance = new UsageRetriever(context);
        return instance;
    }

    private void buildPackagesToTrack() {
        packagesToTrack = new HashSet<>();
        packagesToTrack.add("facebook");
        packagesToTrack.add("whatsapp");
        packagesToTrack.add("instagram");
        packagesToTrack.add("youtube");
    }

    /**
     * Retrieve usage time for specific apps (packagesToTrack)
     *
     * @return Map of app name (simplify) to usage time (HH:MM:SS)
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public Map<String, String> getData() {
        if (Build.VERSION.SDK_INT < 21)
            return null;
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usm == null)
            return null;
        long currentTime = System.currentTimeMillis(), lastSampleTime = currentTime - 1000 * 60 * 60 * 24;

        Map<String, UsageStats> appsUsingStats = usm.queryAndAggregateUsageStats(lastSampleTime, currentTime);

        if (appsUsingStats.size() == 0) {
            Log.i("Usage", "Is Empty!");
            return null;
        }
        Map<String, String> packageToUsageTime = new HashMap<>();

        for (Map.Entry<String, UsageStats> pair :
                appsUsingStats.entrySet()) {
            String appName = pair.getKey();
            long totalTime = pair.getValue().getTotalTimeInForeground();

            //used less than a minute
            if (totalTime / 1000 < 60)
                continue;

            //relevant package
            for (String part : appName.split("\\."))
                if (packagesToTrack.contains(part.toLowerCase())) {
                    packageToUsageTime.put(part, formatTime(totalTime));
                }
        }
        return packageToUsageTime;
    }

    /**
     * @return Time in String in format HH:MM:SS
     */
    private String formatTime(long milliseconds) {
        long timeInSeconds = milliseconds / 1000;
        String ans = "";

        if (timeInSeconds > 60 * 60) {
            if (timeInSeconds / (60 * 60) < 10)
                ans += "0";
            ans += (int) timeInSeconds / (60 * 60) + ":";

            timeInSeconds -= (timeInSeconds / 3600) * 3600;
        } else
            ans += "00:";

        if (timeInSeconds > 60) {
            if (timeInSeconds / 60 < 10)
                ans += "0";
            ans += (int) timeInSeconds / 60 + ":";
            timeInSeconds -= (timeInSeconds / 60) * 60;
        } else
            ans += "00:";

        if (timeInSeconds < 10)
            ans += "0";
        ans += timeInSeconds;

        return ans;
    }
}
