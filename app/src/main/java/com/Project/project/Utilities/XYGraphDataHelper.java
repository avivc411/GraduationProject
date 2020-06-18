package com.Project.project.Utilities;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.Project.project.Activities.QuestionnaireActivity;
import com.Project.project.Activities.UserReportActivity;
import com.Project.project.GPS.SemanticMoodData;
import com.Project.project.RekognitionManagment.RekognitionFeature;
import com.Project.project.Report.SingleReport;
import com.Project.project.Report.UserReportProcessor;
import com.Project.project.Report.Utilities.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manage XY Graph data
 */
public class XYGraphDataHelper {
    private List<String> emotionsToGet, daysByLetters, daysByDates;
    private Set<String> objectsInReports, locationsInReports, activitiesInReports,
            objectsFilter, locationsFilter, activitiesFilter;

//    private Map<String, RekognitionFeature> featureNameToFeature;
//    private Map<String, LocationProperties> locationNameToLocationProperty;
//    private Map<String, ActivitySample> activityNameToActivitySampleMap;

    private List<Date> availableDates;
    private UserReportProcessor userReportProcessor;
    private int daysRange, lastDayInRangeIndex, firstDayInRangeIndex;
    private Calendar calendar;

    private Map<String, String> emotionToQuestion;
    private Map<String, List<Integer>> emotionsScore;

    /**
     * @param activity        Calling activity.
     * @param username        Requested user data.
     * @param emotionsToGet   Chosen emotions.
     * @param dataScreenRange Range of data in every screen of the plot.
     */
    public XYGraphDataHelper(Activity activity, String username, List<String> emotionsToGet, int dataScreenRange) {
        this.emotionsToGet = emotionsToGet;
        this.daysRange = dataScreenRange;

        userReportProcessor = UserReportProcessor.getInstance(activity, username);

        calendar = Calendar.getInstance();

        emotionsScore = new HashMap<>();
        for (String emotion : emotionsToGet)
            emotionsScore.put(emotion, new ArrayList<>());

        emotionToQuestion = QuestionnaireActivity.getOriginalNamesToQuestionNamesInDb();

        availableDates = UserReportActivity.getAllReportsDates();
        availableDates.sort((o1, o2) -> (
                o1.after(o2) ?
                        1 :
                        (o2.after(o1) ?
                                -1 :
                                0)));

        firstDayInRangeIndex = 0;
        lastDayInRangeIndex = availableDates.size() - 1;

        objectsInReports = new HashSet<>();
        locationsInReports = new HashSet<>();
        activitiesInReports = new HashSet<>();
        objectsFilter = new HashSet<>();
        locationsFilter = new HashSet<>();
        activitiesFilter = new HashSet<>();

//        featureNameToFeature = new HashMap<>();
//        locationNameToLocationProperty = new HashMap<>();
//        activityNameToActivitySampleMap = new HashMap<>();
    }

    /**
     * Default settings.
     */
    public Map<String, List<Integer>> getData() {
        return getData(1, availableDates.size(), 0);
    }

    /**
     * Get current data range.
     *
     * @param direction Search direction - 1 for next range, -1 for previous range.
     * @param border    Search border - how far to search in availableDates
     * @param initial   First index to search in.
     * @return Emotion to list of scores.
     */
    private Map<String, List<Integer>> getData(int direction, int border, int initial) {
        boolean hasData = false, inRange = false;
        Date current, firstDateInRange = new Date(), lastDateInRange = new Date();
        daysByDates = new ArrayList<>();
        daysByLetters = new ArrayList<>();
        for (List l : emotionsScore.values())
            l.clear();
        setFirstLastDateInRange(firstDateInRange, lastDateInRange);
        SingleReport report;
        //get reports which sent in daysRange dates' range - first day in the range is before, last day is after
        try {
            int i;
            for (i = initial; i != border; i += direction) {
                current = availableDates.get(i);
                //if not in range
                if ((current.before(lastDateInRange) && current.before(firstDateInRange)) ||
                        (current.after(lastDateInRange) && current.after(firstDateInRange))) {
                    //has been in range - no need to continue searching
                    if (inRange) {
                        i += direction;
                        break;
                    }
                } else {
                    inRange = true;
                    report = userReportProcessor.getSingleSentimentReport(current);
                    if (report != null && filter(report)) {
                        hasData = true;
                        buildDomainLabels(report);
                        for (String emotion : emotionsToGet)
                            emotionsScore.get(emotion).add(report.getUserAnswers().get(emotionToQuestion.get(emotion)));
                    }
                }
            }
            /*if(inRange) {
                if (direction > 0) {
                    firstDayInRangeIndex = lastDayInRangeIndex;
                    lastDayInRangeIndex = i - 1;
                } else {
                    lastDayInRangeIndex = firstDayInRangeIndex;
                    firstDayInRangeIndex = i + 1;
                }
            }
            else{
                if (direction > 0)
                    firstDayInRangeIndex = lastDayInRangeIndex;
                else
                    lastDayInRangeIndex = firstDayInRangeIndex;
            }*/
            return (hasData ? emotionsScore : null);
        } catch (Exception e) {
            e.printStackTrace();
            daysByDates.clear();
            daysByLetters.clear();
            return null;
        }
    }

    /**
     * Filter report by chosen filters, if there are any.
     *
     * @param report Report to filer.
     * @return Report has all necessary values.
     */
    private boolean filter(SingleReport report) {
        try {
            for (String object : objectsFilter)
                if (!report.getRekognitionFeatures().contains(new RekognitionFeature(object, 0, 0)))
                    return false;
            for (String location : locationsFilter)
                if (!report.getUserLocationProperties().getSentimentPlace().equals(location))
                    return false;
            /*for(String activity : activitiesFilter)
                if(!report.getActivities().contains(activity))
                    return false;*/
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Get all possible filters' values.
     */
    private void buildFilters() {
        objectsInReports.clear();
        activitiesInReports.clear();
        locationsInReports.clear();

        objectsInReports.addAll(UserReportActivity.getRekognitionEmotionsData().getFeatures());
        for (SemanticMoodData semantic : UserReportActivity.getSemanticMoodDataList())
            locationsInReports.add(semantic.getSemanticPlace());
        //activitiesInReports.addAll(UserReportActivity.getUserActivities());
    }

    /**
     * Add report's day\date to domain labels.
     */
    private void buildDomainLabels(SingleReport report) {
        daysByLetters.add(DateUtils.convertDateToString(report.getDate(), "EEEEE"));
        daysByDates.add(DateUtils.convertDateToString(report.getDate(), "dd-MM-yyyy"));
    }

    /**
     * Set first and last according to first and last date in current screen range.
     */
    public void setFirstLastDateInRange(Date first, Date last) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        last.setTime(calendar.getTime().getTime());

        calendar.add(Calendar.DATE, -daysRange);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        first.setTime(calendar.getTime().getTime());

        calendar.add(Calendar.DATE, daysRange);
    }

    /**
     * Set calendar to next range and get next screen range data.
     */
    public Map<String, List<Integer>> getNextRangeData() {
        calendar.add(Calendar.DATE, daysRange);
//        return getData(1, availableDates.size(), firstDayInRangeIndex);
        return getData(1, availableDates.size(), 0);
    }

    /**
     * Set calendar to previous range and get previous screen range data.
     */
    public Map<String, List<Integer>> getPrevRangeData() {
        calendar.add(Calendar.DATE, -daysRange);
//        return getData(-1, 0, firstDayInRangeIndex);
        return getData(1, availableDates.size(), 0);
    }

    /**
     * @return Collected report's dates by first letter of the day.
     */
    public List<String> getDaysByLetter() {
        return daysByLetters;
    }

    /**
     * @return Collected report's dates as dd-MM-yyyy.
     */
    public List<String> getDaysByDates() {
        return daysByDates;
    }

    /**
     * Set given parameters to matching available filters.
     */
    public void getAvailableFilters(List<String> objectFilters, List<String> locationsFilters, List<String> activityFilters) {
        buildFilters();
        objectFilters.clear();
        objectFilters.addAll(objectsInReports);
        locationsFilters.clear();
        locationsFilters.addAll(locationsInReports);
        activityFilters.clear();
        activityFilters.addAll(activitiesInReports);
    }

    public void setObjectsFilter(Set<String> objectsFilter) {
        this.objectsFilter.clear();
        if (objectsFilter != null)
            this.objectsFilter.addAll(objectsFilter);
    }

    public void setActivitiesFilter(Set<String> activitiesFilter) {
        this.activitiesFilter.clear();
        if (activitiesFilter != null)
            this.activitiesFilter.addAll(activitiesFilter);
    }

    public void setLocationsFilter(Set<String> locationsFilter) {
        this.locationsFilter.clear();
        if (locationsFilter != null)
            this.locationsFilter.addAll(locationsFilter);
    }

}
