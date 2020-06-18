package com.Project.project.Report.Utilities;

import com.Project.project.Report.UserReportProcessor;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class QuestionnairesFilteringUtils {
    /**
     * Filter questionnaires by chosen filters.
     *
     * @return
     */
    public static List<ParseObject> filterQuestionnairesByRelevantDates
    (List<ParseObject> allQuestionnaires,
     UserReportProcessor.RangeFilter rangeFilter) {
        List<ParseObject> filteredQuestionnaires;
        switch (rangeFilter) {
            case NONE_FILTER:
                // No filter needed.
                filteredQuestionnaires = allQuestionnaires;
                break;
            case LAST_SEVEN_DAYS:
                //filter by 7 days
                String stringDate = DateUtils.convertDateToString(new Date());
                // 8 because 7 days ago means the date should be greater than before 8 days.
                // Low limit date is beforeSevenDaysDate.
                String beforeSevenDaysDate = getBeforeNDaysDate(stringDate, 8);
                filteredQuestionnaires = filterByAfterSpecificDate(allQuestionnaires, beforeSevenDaysDate);
                break;
            case LAST_THIRTY_DAYS:
                // filter by last 30 days
                String stringTodayDate = DateUtils.convertDateToString(new Date());
                // 31 because 30 days ago means the date should be greater than before 30 days.
                // Low limit date is beforeSevenDaysDate.
                String beforeThirtyDaysDate = getBeforeNDaysDate(stringTodayDate, 31);
                filteredQuestionnaires = filterByAfterSpecificDate(allQuestionnaires, beforeThirtyDaysDate);
                break;
            default:
                //filter by relevant month
                int filterMonth = getMonthFromRangefilter(rangeFilter);
                filteredQuestionnaires = filterByMonth(filterMonth, allQuestionnaires);
        }
        return filteredQuestionnaires;
    }

    /**
     * Calculates new date - today minus number of days to the past.
     *
     * @param date
     * @param numberOfDaysBackward
     * @return
     */
    private static String getBeforeNDaysDate(String date, int numberOfDaysBackward) {
        // GET DATE FROM THE PAST OR FUTURE
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormat.parse(date));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        // + future days, - past days
        calendar.add(Calendar.DATE, -numberOfDaysBackward);
        // Date is now the new date
        date = simpleDateFormat.format(calendar.getTime());
        return date;
    }


    /**
     * Filter by low limit date. Keeps only questionnaires with date after low limit date.
     *
     * @param questionnaires
     * @param beforeSevenDaysDate
     * @return
     */
    private static List<ParseObject> filterByAfterSpecificDate(List<ParseObject> questionnaires,
                                                               String beforeSevenDaysDate) {
        List<ParseObject> filteredQuestionnaires = new ArrayList<>();
        for (int i = 0; i < questionnaires.size(); i++) {
            String currentDateString = fetchReportDate(questionnaires.get(i));
            // Current date after low limit date.
            if (isDateOnRange(beforeSevenDaysDate, currentDateString)) {
                filteredQuestionnaires.add(questionnaires.get(i));
            }
        }
        return filteredQuestionnaires;
    }


    /**
     * Checking if a specific date is after a lowLimitDate date.
     *
     * @param lowLimitDate
     * @param dateToCheck
     * @return
     */
    private static boolean isDateOnRange(String lowLimitDate, String dateToCheck) {
        Date lowRange = DateUtils.convertStringToDate(lowLimitDate);
        Date checkingDate = DateUtils.convertStringToDate(dateToCheck);
        return checkingDate.after(lowRange);
    }

    /**
     * Filter questionnaires by specific month.
     *
     * @param filterMonth
     * @param questionnaires
     * @return
     */
    private static List<ParseObject> filterByMonth(int filterMonth, List<ParseObject> questionnaires) {
        List<ParseObject> filteredQuestionnaires = new ArrayList<>();
        for (int i = 0; i < questionnaires.size(); i++) {
            String currentDateString = fetchReportDate(questionnaires.get(i));
            Date currentDate = DateUtils.convertStringToDate(currentDateString);
            int currentDateMonth = getMonthFromDate(currentDate);

            // Same month.
            if (filterMonth == currentDateMonth) {
                filteredQuestionnaires.add(questionnaires.get(i));
            }
        }
        return filteredQuestionnaires;
    }

    /**
     * Fetch date from questionnaire.
     * Date returns by MONTH-DAY-YEAR FORMAT
     *
     * @param questionnaire
     * @return
     */
    private static String fetchReportDate(ParseObject questionnaire) {
        // MONTH-DAY-YEAR FORMAT STRING
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String date = simpleDateFormat.format(questionnaire.getDate("upload_date"));
        return date;
    }

    /**
     * Converting RangeFilter month to Integer month.
     *
     * @param rangeFilterMonth
     * @return
     */
    private static int getMonthFromRangefilter(UserReportProcessor.RangeFilter rangeFilterMonth) {
        String[] months = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST",
                "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
        int monthLocation = Arrays.asList(months).indexOf(rangeFilterMonth.toString()) + 1;
        return monthLocation;
    }

    /**
     * Getting month number from date.
     *
     * @param date
     * @return
     */
    private static int getMonthFromDate(Date date) {
        // int month = 0;
        int month = date.getMonth()+1;

        LocalDate localDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            month = localDate.getMonthValue();
        }
        return month;
    }
}
