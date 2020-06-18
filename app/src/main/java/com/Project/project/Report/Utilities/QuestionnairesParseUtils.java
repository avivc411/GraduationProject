package com.Project.project.Report.Utilities;

import com.Project.project.Report.UserReport;
import com.Project.project.Report.UserReport.ReportType;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class QuestionnairesParseUtils {

    /**
     * Calculates user's answers
     *
     * @param questionnaires - the questionnaires that the user send
     * @return question name and question's calculated answer
     */
    public static Map<String, Integer> getUserAnswers(List<ParseObject> questionnaires) {
        Map<String, Integer> questionAndAnswers = new HashMap<>();
        questionAndAnswers = initializeMap(10);
        if (questionnaires.size() == 0) {
            return questionAndAnswers;
        }
        for (int row = 0; row < questionnaires.size(); row++) {
            // Fetch answers from all columns.
            for (int col = 1; col <= 10; col++) {
                int columnQuestValue = questionnaires.get(row).getInt("quest_" + col);
                // update the fit question sum
                int updatedValue = questionAndAnswers.get("quest_" + col) + columnQuestValue;
                questionAndAnswers.put("quest_" + col, updatedValue);
            }
        }
        //Set the average value.
        for (int col = 1; col <= 10; col++) {
            int currentValue = (questionAndAnswers.get("quest_" + col));
            int newValue = currentValue / questionnaires.size();
            questionAndAnswers.put("quest_" + col, newValue);

        }
        return questionAndAnswers;
    }

    /**
     * Creates a list of integers from all input rows of a given column.
     *
     * @param reportType          - report type
     * @param parseObjectsList - rows from questionnair's db
     * @return list of numeric values from the fit column in the parse list
     */
    public static List<Integer> fetchScoresFromTableColumn(ReportType reportType,
                                                           List<ParseObject> parseObjectsList) {
        String column = "";
        switch (reportType) {
            case POSITIVE:
                column = "total_pos_score";
                break;
            case NEGATIVE:
                column = "total_neg_score";
                break;
            default:
                // Shouldn't happen.
        }
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < parseObjectsList.size(); i++) {
            list.add(parseObjectsList.get(i).getInt(column));
        }
        return list;
    }


    /**
     * Return rows that satisfies the equal query for specific row and value.
     *
     * @param className
     * @param valueToSearch
     * @param columnName
     * @return
     */
    public static List<ParseObject> getParserObjectsEqual(String className, String valueToSearch,
                                                          String columnName) {
        List<String> data = new ArrayList<>();
        data.add(valueToSearch);
        return getParserObjectsEqual(className, data, columnName);
    }

    /**
     * Return rows that satisfies the equal query for specific row and value.
     *
     * @param className
     * @param valueToSearch
     * @param columnName
     * @return
     */
    static List<ParseObject> getParserObjectsEqual(String className, List<String> valueToSearch,
                                                   String columnName) {
        List<ParseObject> data = new ArrayList<>();
        for (int index = 0; index < valueToSearch.size(); index++) {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(className);
            query.whereEqualTo(columnName, valueToSearch.get(index));
            try {
                List<ParseObject> queryResult = query.find();
                if (queryResult.isEmpty() == false)
                    data = copyToData(data, queryResult);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * Add more parse object rows to original list.
     *
     * @param original
     * @param queryResult
     * @return
     */
    private static List<ParseObject> copyToData(List<ParseObject> original,
                                                List<ParseObject> queryResult) {
        List<ParseObject> copyTo = original;
        for (int queryData = 0; queryData < queryResult.size(); queryData++) {
            copyTo.add(queryResult.get(queryData));
        }
        return copyTo;
    }

    /**
     * Initializing map in size of number of questions, with fitting columns names.
     *
     * @param numOfQuestions - amount of questions in questionnaire
     * @return
     */
    private static Map<String, Integer> initializeMap(int numOfQuestions) {
        Map<String, Integer> questionAndAnswers = new HashMap<>();
        for (int col = 1; col <= 10; col++) {
            questionAndAnswers.put("quest_" + col, 0);
        }
        return questionAndAnswers;
    }
}
