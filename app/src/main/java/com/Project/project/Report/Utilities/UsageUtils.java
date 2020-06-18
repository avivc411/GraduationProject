package com.Project.project.Report.Utilities;

import com.Project.project.UsageManagment.UsageProperties;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsageUtils {

    /**
     * Public method - getting the usage information from the db.
     *
     * @param questionnaires
     * @return UsageProperties list of usages for relevant questionnaires.
     */
    public static List<UsageProperties> getUsages(List<String> questionnaires) {
        List<ParseObject> parseObjects = fetchUsages(questionnaires);
        if (parseObjects == null) {
            return null;
        }
        Map<String, List<ParseObject>> stringListMap = createUsageMapForQuestionnaireAndRows(parseObjects);
        List<UsageProperties> usagePropertiesList = getUsageProperties(stringListMap);
        System.out.println("usage properties list size: " + usagePropertiesList.size());
        return usagePropertiesList;
    }

    /**
     * Retrieves usages from db that connected to topQuestionnaires.
     *
     * @param questionnaires
     * @return list of Usages as Parse Object.
     */
    private static List<ParseObject> fetchUsages(List<String> questionnaires) {
        System.out.println("begin fetchUsages");
        ParseQuery<ParseObject> query = new ParseQuery<>("Usage");
        List<ParseObject> usages = null;
        try {
            usages = query.whereContainedIn("questionnaire", questionnaires).find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("done fetchUsages" + usages.size());
        return usages;
    }

    /**
     * Mapping between questionnaire id --> connected Usage.
     *
     * @param usages - usages parse object.
     * @return Map<Questionnaire id, List < connected Usage>>
     */
    private static Map<String, List<ParseObject>> createUsageMapForQuestionnaireAndRows(List<ParseObject> usages) {
        System.out.println("begin createUsageMapForQuestionnaireAndRows ");
        Map<String, List<ParseObject>> questionnaireAndParseObjects = new HashMap<>();
        // Setting the map each questionnaire and his ParseObjects.
        //todo check if exists
        for (int i = 0; i < usages.size(); i++) {
            String questionnaire = usages.get(i).getString("questionnaire");
            List<ParseObject> currentParseObjects = questionnaireAndParseObjects.get(questionnaire);
            if (currentParseObjects != null) {
                currentParseObjects.add(usages.get(i));
            } else {
                currentParseObjects = new ArrayList<>();
                currentParseObjects.add(usages.get(i));
            }
            questionnaireAndParseObjects.put(questionnaire, currentParseObjects);

        }
        System.out.println("done createUsageMapForQuestionnaireAndRows " + questionnaireAndParseObjects.size());
        return questionnaireAndParseObjects;
    }

    /**
     * Converting Usage parse object to UsageProperties.
     *
     * @param questionnaireAndParseObjects
     * @return UsageProperties list.
     */
    private static List<UsageProperties> getUsageProperties(Map<String, List<ParseObject>>
                                                                    questionnaireAndParseObjects) {
        if (questionnaireAndParseObjects == null) {
            return null;
        }
        System.out.println(" begin getUsageProperties");
        List<UsageProperties> usagePropertiesList = new ArrayList<>();
        for (Map.Entry<String, List<ParseObject>> entry : questionnaireAndParseObjects.entrySet()) {
            Map<String, String> appAndName = new HashMap<>();
            Date date = new Date();
            String key = entry.getKey();
            List<ParseObject> value = entry.getValue();
            for (int i = 0; i < value.size(); i++) {
                if (i == 0) {
                    date = value.get(i).getUpdatedAt();
                }
                String appName = value.get(i).getString("Name");
                String appTime = value.get(i).getString("Time");
                appAndName.put(appName, appTime);
            }
            UsageProperties usageProperties = new UsageProperties(date, appAndName);
            usagePropertiesList.add(usageProperties);
        }
        System.out.println(" done getUsageProperties" + usagePropertiesList.size());
        return usagePropertiesList;
    }
}



