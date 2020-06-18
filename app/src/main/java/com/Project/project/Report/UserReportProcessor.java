package com.Project.project.Report;

import android.content.Context;
import android.graphics.Bitmap;

import com.Project.project.GPS.LocationProperties;
import com.Project.project.GPS.SemanticMoodData;
import com.Project.project.RekognitionManagment.RekognitionFeature;
import com.Project.project.Report.UserReport.ReportType;
import com.Project.project.Report.Utilities.ImageUtils;
import com.Project.project.Report.Utilities.LocationUtils;
import com.Project.project.Report.Utilities.QuestionnairesFilteringUtils;
import com.Project.project.Report.Utilities.QuestionnairesParseUtils;
import com.Project.project.Report.Utilities.RekognitionEmotionsData;
import com.Project.project.Report.Utilities.RekognitionFeaturesUtils;
import com.Project.project.Report.Utilities.UsageUtils;
import com.Project.project.UsageManagment.UsageProperties;
import com.Project.project.Utilities.SentimentScore;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Connecting to the db and creates reports.
 */
public class UserReportProcessor {
    private static UserReportProcessor userReportProcessorInstance = null;
    private final List<ParseObject> questionnaires;
    private Map<Date, ParseObject> dateToQuestionnaireParseObjectMap = null;
    private static Map<String, Integer> questionnaireIdToMoodScore;
    private final Map<String, ParseObject> questionnaireIdToImageParseObjectMap;
    private final Map<String, List<ParseObject>> imageIdToRekognitionParseObjectMap;
    private final Map<String, ParseObject> imageIdToGPSParseObjectMap;
    // TODO: 22-Apr-20 add it to all relevant places - saarm
    private final Map<String, ParseObject> questionnaireIdToGPSParseObject;
    private String username;

    /**
     * Constructor.
     *
     * @param context
     * @param username
     */
    private UserReportProcessor(Context context, String username) {
        this.username = username;
        questionnaires = QuestionnairesParseUtils.getParserObjectsEqual(
                "Questionnaire", username, "user");

        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId("3a40729a203a63626664fdb4f37a1bd8802c1581")
                // if defined
                .clientKey("1872ec8a614edffc2e594daa17dd6355f0f6140a")
                .server("http://3.19.234.120:80/parse/")
                .build()
        );

        Thread dateToQuestionnaireThread = new Thread(() -> {
            // Mapping Questionnaire Date --> Questionnaire (Parse object).
            dateToQuestionnaireParseObjectMap = dateToQuestionnaireParseObject();

            // TODO: 26-Apr-20 check if need to change in another places - saarm
            questionnaireIdToMoodScore = questionnaireIdToMoodScore();
        }, "second_thread");
        dateToQuestionnaireThread.start();

        // Mapping QuestionnaireId --> Image (Parse object).
        questionnaireIdToImageParseObjectMap = questionnaireIdToImageParseObject();

        // Mapping Image Id--> List of Rekognition (features) rows (ParseObject).
        imageIdToRekognitionParseObjectMap =
                imageIdToRekognitionParseObject(questionnaireIdToImageParseObjectMap);

        // Mapping Image Id --> GPS (ParseObject).
        imageIdToGPSParseObjectMap = imageIdToGPSParseObject(questionnaireIdToImageParseObjectMap);

        // TODO: 22-Apr-20 add it to all relevant places - saarm
        questionnaireIdToGPSParseObject = questionnaireToGPSParseObject();

    }

    /**
     * Mapping between questionnaireID --> Mood score.
     *
     * @return Map <questionnaireID , Mood ><
     */
    private Map<String, Integer> questionnaireIdToMoodScore() {
        Map<String, Integer> questionnaireAndMoodScore = new HashMap<>();

        for (ParseObject questionnaireParseObject : questionnaires) {
            questionnaireAndMoodScore.put(
                    questionnaireParseObject.getObjectId(),
                    questionnaireParseObject.getInt("mood"));
        }
        return questionnaireAndMoodScore;

    }

    /**
     * Static method to create instance of Singleton class.
     *
     * @param context
     * @param username
     */
    public static UserReportProcessor getInstance(Context context, String username) {
        if (userReportProcessorInstance == null)
            userReportProcessorInstance = new UserReportProcessor(context, username);

        return userReportProcessorInstance;
    }

    /**
     * Delete current userReportProcessorInstance singletone. Make it null.
     */
    public static void invalidate() {
        userReportProcessorInstance = null;
    }

    /**
     * Calculates sentiment score from user's questionnaires.
     *
     * @param list
     * @param maxScore
     * @param amountOfQuestions
     * @param amountOfQuestionnaires
     * @return
     */
    private static SentimentScore getScoreInfo(List<Integer> list, int maxScore,
                                               int amountOfQuestions,
                                               int amountOfQuestionnaires) {
        int sum = 0, counter = 0;
        SentimentScore sentimentScore = new SentimentScore(
                -1, -1, -1, -1, -1);
        for (Integer score : list) {
            sum += score;
            counter++;
        }
        // PositiveSentiment report or NegativeSentiment report.
        sentimentScore.setAmountOfQuestionnaires(amountOfQuestionnaires);
        sentimentScore.setTotalScore(sum);
        sentimentScore.setMaxPossibleTotalScore((maxScore * amountOfQuestions * counter));
        sentimentScore.setAverage(counter == 0 ? 0 : (sum / counter));
        sentimentScore.setMaxPossibleAverage(maxScore * amountOfQuestions);

        return sentimentScore;
    }

    /**
     * Creates Positive sentiment report from user's questionnaires.
     *
     * @return
     * @throws ParseException
     */
    public PositiveSentimentUserReport getPositiveSentimentReport(RangeFilter rangeFilter)
            throws ParseException {
        // Filter user's questionnaires by chosen filter.
        List<ParseObject> filteredQuestionnaires =
                QuestionnairesFilteringUtils.filterQuestionnairesByRelevantDates(questionnaires, rangeFilter);

        PositiveSentimentUserReport positiveSentimentUserReport = new PositiveSentimentUserReport(
                null, null,
                null, null, null, null,
                null, null);

        if (filteredQuestionnaires.size() > 0) {
            setSentimentUserReport(positiveSentimentUserReport, filteredQuestionnaires);

            return positiveSentimentUserReport;
            // No questionnaires exists
        } else {
            return null;
        }
    }


    /**
     * Creates Negative sentiment report from user's questionnaires.
     *
     * @return
     * @throws ParseException
     */
    public NegativeSentimentUserReport getNegativeSentimentReport(RangeFilter rangeFilter)
            throws ParseException {
        // Filter user's questionnaires by chosen filter.
        List<ParseObject> filteredQuestionnaires =
                QuestionnairesFilteringUtils.filterQuestionnairesByRelevantDates(questionnaires, rangeFilter);

        NegativeSentimentUserReport negativeSentimentUserReport = new NegativeSentimentUserReport(
                null, null,
                null, null, null, null
                , null, null);

        if (filteredQuestionnaires.size() > 0) {
            setSentimentUserReport(negativeSentimentUserReport, filteredQuestionnaires);

            return negativeSentimentUserReport;
            // No questionnaires exists
        } else {
            return null;
        }
    }

    public RekognitionEmotionsData getRekognitionEmotionsData() {
        // Rekognition part.
        RekognitionEmotionsData rekognitionEmotionsData = new
                RekognitionEmotionsData(null);
        //System.out.println("begin getStatisticalReport");

        Map<String, List<String>> rekognitionMap = rekognitionFeatureToQuestionnaireIdList();

        Map<String, Map<String, Integer>> rekognitionFeatureAverageScore =
                rekognitionFeatureAverageScore(rekognitionMap);

        rekognitionEmotionsData.setRekognitionFeatureAverageScores(rekognitionFeatureAverageScore);
        return rekognitionEmotionsData;
    }

    public List<SemanticMoodData> getSemanticMoodData(RangeFilter rangeFilter) {
        SemanticMoodData semanticMoodData = new SemanticMoodData(null, null,
                null, 9);

        List<ParseObject> filteredQuestionnaires =
                QuestionnairesFilteringUtils.filterQuestionnairesByRelevantDates(questionnaires,
                        rangeFilter);

        List<SemanticMoodData> semanticMoodDataList = setSemanticMoodData(filteredQuestionnaires);

        return semanticMoodDataList;
    }

    private List<SemanticMoodData> setSemanticMoodData(List<ParseObject> filteredQuestionnaires) {
        List<SemanticMoodData> semanticMoodDataList = new ArrayList<>();
        filteredQuestionnaires.stream().forEach(questionnaire -> {
            String qustionnaireId = questionnaire.getObjectId();
            // Questionnaire has GPS.
            if (questionnaireIdToGPSParseObject.get(qustionnaireId) != null){
                String semanticPlace = questionnaireIdToGPSParseObject.get
                        (qustionnaireId).
                        getString("semantic_context");

                int mood = questionnaire.getInt("mood");
                Date date = questionnaire.getDate("upload_date");
                SemanticMoodData semanticMoodData = new SemanticMoodData(qustionnaireId, date,
                        semanticPlace, mood);
                semanticMoodDataList.add(semanticMoodData);
            }
        });
        return semanticMoodDataList;
    }

    /**
     * Mapping between feature name to map of question name and average score.
     * e.g. Radio --> <<question_1,4>,<question2_3>..>.
     *
     * @param rekognitionMap - Map of feature name--> list of questionnaires contatining this feature.
     * @return Map<Feature name, Map < question name, average score>>.
     */
    private Map<String, Map<String, Integer>> rekognitionFeatureAverageScore(Map<String, List<String>> rekognitionMap) {
        Map<String, Map<String, Integer>> rekognitionFeatureAverageScore = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : rekognitionMap.entrySet()) {
            // Connected questionnaires to feature.
            List<ParseObject> connectedQuestionnaires = null;
            for (int i = 0; i < entry.getValue().size(); i++) {
                connectedQuestionnaires = new ArrayList<>();
                int finalI = i;
                ParseObject questionnaire = questionnaires.stream()
                        .filter(x -> entry.getValue().get(finalI).equals(x.getObjectId()))
                        .findAny()
                        .orElse(null);
                connectedQuestionnaires.add(questionnaire);
            }
            Map<String, Integer> questionToAverageScore =
                    questionToAverageScore(connectedQuestionnaires);
            rekognitionFeatureAverageScore.put(entry.getKey(), questionToAverageScore);
        }
        return rekognitionFeatureAverageScore;
    }

    /**
     * Mapping question's name to average score of answers to this question,
     * from all connected questionnaires.
     * e.g. 3 questionnaires, quest_1 answers are: 4,2,5 --> Map: <quest_1, average(4,2,5)>.
     *
     * @param questionnaires - the questionnaires we calculating the average scores for.
     * @return Map<question name, average score for this question>.
     */
    private Map<String, Integer> questionToAverageScore(List<ParseObject> questionnaires) {
        Map<String, Integer> questionToAverageAnswer = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            String question = "quest_" + i;
            List<Integer> questionAnswers = questionnaires.stream()
                    .map(q -> q.getInt(question)).collect(Collectors.toList());
            Integer sum = questionAnswers.stream()
                    .reduce(0, Integer::sum);
            questionToAverageAnswer.put(question, (sum / questionnaires.size()));
        }
        return questionToAverageAnswer;
    }

    /**
     * Mapping between: Feature name--> questionnaires that containing it.
     *
     * @return Map<Feature name, List < Connected questionnaires>>.
     */
    private Map<String, List<String>> rekognitionFeatureToQuestionnaireIdList() {
        Map<String, List<String>> swappedMAP = new HashMap<>();
        Map<String, String> imageIdToQuestionnaireIdMap =
                questionnaireIdToImageParseObjectMap.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getValue().getObjectId(),
                                entry -> entry.getKey()));

        for (Map.Entry<String, List<ParseObject>> entry :
                imageIdToRekognitionParseObjectMap.entrySet()) {
            String imageId = entry.getKey();
            String questionnaireId = imageIdToQuestionnaireIdMap.get(imageId);

            ArrayList<String> features = entry.getValue().stream()
                    .map(rekognitionParseObject ->
                            rekognitionParseObject.getString("feature"))
                    .collect(Collectors.toCollection(ArrayList::new));

            features.forEach(feature ->
                    swappedMAP.computeIfAbsent(feature, k -> new ArrayList<>()).add(questionnaireId)
            );
        }

        return swappedMAP;
    }


    /**
     * Fetching list of all user's questionnaires dates, after filtering the questionnaires.
     *
     * @param rangeFilter - filtering the questionnaires by it.
     * @return list of dates of user's questionnaires.
     */
    public List<Date> getAllReportsDates(RangeFilter rangeFilter) {
        // Filter user's questionnaires by chosen filter.
        List<ParseObject> filteredQuestionnaires =
                QuestionnairesFilteringUtils.filterQuestionnairesByRelevantDates
                        (questionnaires, rangeFilter);
        return filteredQuestionnaires.stream()
                .map(questionnaire -> questionnaire.getDate("upload_date"))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Fetching mood score for specific questionnaire.
     *
     * @param questionnaireId - selected questionnaire to get mood for.
     * @return mood of chosen questionnaire.
     */
    public static int getQuestionnaireIdToMoodScore(String questionnaireId) {
        return questionnaireIdToMoodScore.get(questionnaireId);
    }

    /**
     * Creating SingleReport from questionnaire.
     *
     * @param date - date of questionnaire.
     * @return SingleReport of current questionnaire.
     */
    public SingleReport getSingleSentimentReport(Date date) {
        if (dateToQuestionnaireParseObjectMap == null) {
            return null;
        }
        // TODO: 02/05/2020 fix case questionnaire is null - there's no questionnaire for this date
        ParseObject questionnaire = dateToQuestionnaireParseObjectMap.get(date);
        if (questionnaire == null)
            return null;
        // Get answers.
        Map<String, Integer> questionAndAnswerMap = setQuestionnairesAnswers(questionnaire);
        // Get location.
        String questionnaireId = questionnaire.getObjectId();
        LocationProperties locationProperties = LocationUtils.createLocationProperties
                (questionnaireIdToGPSParseObject.get(questionnaireId));
        // Get picture.
        ParseObject image = questionnaireIdToImageParseObjectMap.get(questionnaireId);
        Bitmap bitmap = null;
        List<RekognitionFeature> rekognitionFeatures = new ArrayList<>();
        if (image != null) {
            // Get as bitmap.
            bitmap = ImageUtils.getDecodedPicture(image.getString("raw_picture"));
            // Get rekognition.
            rekognitionFeatures = RekognitionFeaturesUtils.getRekognitionFeatures
                    (imageIdToRekognitionParseObjectMap.get(image.getObjectId()));
        }
        // Get usage.
        // TODO: 22-Apr-20 change it to regular map and not with list - saarm
        List<String> questionnairesToGetUsage = new ArrayList<>();
        questionnairesToGetUsage.add(questionnaireId);
        List<UsageProperties> usageUtils = UsageUtils.getUsages(questionnairesToGetUsage);
        UsageProperties usageProperties = null;
        if (usageUtils != null && usageUtils.size() > 0) {
            usageProperties = usageUtils.get(0);
        }
        SingleReport singleReport = new SingleReport(date, questionAndAnswerMap, rekognitionFeatures,
                locationProperties, bitmap, usageProperties);
        return singleReport;
    }

    /**
     * Mapping: question name --> question answer.
     *
     * @param questionnaire - specific questionnaire to creating map for.
     * @return Map<question name, question answer>.
     */
    private Map<String, Integer> setQuestionnairesAnswers(ParseObject questionnaire) {
        String questName = "quest_";
        Map<String, Integer> questionToAnswerMap = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            questionToAnswerMap.put(questName + i, questionnaire.getInt(questName + i));
        }
        questionToAnswerMap.put("mood", questionnaire.getInt("mood"));

        return questionToAnswerMap;
    }


    /**
     * Mapping: image Id  --> GPS parse object.
     *
     * @param questionnaireIdToImageParseObjectMap
     * @return Map <Image Id, GPS ParseObject>
     */
    private Map<String, ParseObject> imageIdToGPSParseObject(Map<String, ParseObject> questionnaireIdToImageParseObjectMap) {
        Map<String, ParseObject> imageToGps = new HashMap<>();

        for (Map.Entry<String, ParseObject> entry : questionnaireIdToImageParseObjectMap.entrySet()) {
            ParseQuery<ParseObject> query = new ParseQuery<>("GPS");
            String questionnaireId = entry.getKey();
            String imageId = entry.getValue().getObjectId();

            query.whereEqualTo("questionnaire", questionnaireId);
            List<ParseObject> parseObject = null;
            try {
                parseObject = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Found in db.
            if (parseObject.isEmpty() == false) {
                imageToGps.put(imageId, parseObject.get(0));
            }
        }
        return imageToGps;
    }



    private Map<String, ParseObject> questionnaireToGPSParseObject() {
        Map<String, ParseObject> questionnaireToGps = new HashMap<>();

        for (int i = 0; i < questionnaires.size(); i++) {
            ParseQuery<ParseObject> query = new ParseQuery<>("GPS");
            String questionnaireId = questionnaires.get(i).getObjectId();
            query.whereEqualTo("questionnaire", questionnaireId);
            List<ParseObject> parseObject = null;
            try {
                parseObject = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Found in db.
            if (parseObject.isEmpty() == false) {
                questionnaireToGps.put(questionnaireId, parseObject.get(0));
            }
        }
        return questionnaireToGps;
    }


    /**
     * Retrieve top N questionnaires by column grade.
     *
     * @param filteredQuestionnaires - user's questionnaires.
     * @param reportType             - report type.
     * @param n                      - amount of top questionnaires to retrieve.
     * @return questionnaire's ids of top N questionnaires.
     */
    private List<String> getTopNQuestionnaires(List<ParseObject> filteredQuestionnaires,
                                               ReportType reportType, int n) {
        int factor = (reportType == ReportType.POSITIVE ? 1 : -1);
        List<String> sortedQuestionnaires = filteredQuestionnaires.stream()
                .sorted((q1, q2) ->
                        ((Integer) (q2.getInt("mood") * factor))
                                .compareTo(q1.getInt("mood") * factor))
                .map(questionnaire -> questionnaire.getObjectId())
                .collect(Collectors.toList());

        return sortedQuestionnaires.subList(
                Math.max(0, sortedQuestionnaires.size() - n), sortedQuestionnaires.size());
    }

    /**
     * Mapping between questionnaire's date --> questionnaire's parse object.
     *
     * @return Map<questionnaire ' s date, questionnaire ' s parse object>.
     */
    public Map<Date, ParseObject> dateToQuestionnaireParseObject() {
        Map<Date, ParseObject> dateAndQuestionnaire = new HashMap<>();

        for (ParseObject questionnaireParseObject : questionnaires) {
            dateAndQuestionnaire.put(
                    questionnaireParseObject.getDate("upload_date"), questionnaireParseObject);
        }
        return dateAndQuestionnaire;
    }

    /**
     * Creating map: questionnaire id --> connected image.
     *
     * @return Map<questionnaire id, connected image>.
     */
    private Map<String, ParseObject> questionnaireIdToImageParseObject() {
        Map<String, ParseObject> questionnaireAndImage = new HashMap<>();

        for (ParseObject questionnaireParseObject : questionnaires) {
            ParseQuery<ParseObject> query = new ParseQuery<>("Picture");
            String questionnaireId = questionnaireParseObject.getObjectId();

            query.whereEqualTo("questionnaire", questionnaireId);
            List<ParseObject> parseObject = null;
            try {
                parseObject = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Found in db.
            if (parseObject.isEmpty() == false) {
                questionnaireAndImage.put(questionnaireId, parseObject.get(0));
            }
        }

        return questionnaireAndImage;
    }

    /**
     * Creating map: image id --> connected features.
     *
     * @param questionnaireIdToImageParseObjectMap - map between questionnaire id to image.
     * @return Map<image id, list of features>.
     */
    private Map<String, List<ParseObject>> imageIdToRekognitionParseObject(
            Map<String, ParseObject> questionnaireIdToImageParseObjectMap) {
        Map<String, List<ParseObject>> imageAndRekognition = new HashMap<>();
        for (ParseObject imageParseObject : questionnaireIdToImageParseObjectMap.values()) {
            ParseQuery<ParseObject> query = new ParseQuery<>("Feature");
            String imageId = imageParseObject.getObjectId();

            query.whereEqualTo("imageID", imageId);
            List<ParseObject> parseObject = null;
            try {
                parseObject = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Found in db.
            if (parseObject.isEmpty() == false) {
                imageAndRekognition.put(imageId, parseObject);
            }
        }
        return imageAndRekognition;
    }


    /**
     * Set the Sentiment user report.
     *
     * @param userReport
     * @param filteredQuestionnaires
     */
    private void setSentimentUserReport(UserReport userReport,
                                        List<ParseObject> filteredQuestionnaires) {
        ReportType reportType = userReport.getReportType();
        Predicate<Integer> questionnairesByScorePredicate =
                reportType == ReportType.POSITIVE ?
                        score -> score >= 5 :
                        score -> score <= 5;

        filteredQuestionnaires = filteredQuestionnaires.stream()
                .filter(q ->
                        questionnairesByScorePredicate.test(q.getInt("mood")))
                .collect(Collectors.toList());

        List<Integer> scores =
                QuestionnairesParseUtils.fetchScoresFromTableColumn(
                        reportType, filteredQuestionnaires);
        SentimentScore sentimentScore =
                getScoreInfo(scores, 5, 5, filteredQuestionnaires.size());
        userReport.setSentimentScore(sentimentScore);

        // Top chosen questionnaire's ids. N = amount of top questionnaires.
        List<String> topQuestionnaireIds =
                getTopNQuestionnaires(filteredQuestionnaires, reportType, 10);

        /**
         * Images process.
         */
        // Relevant images objects found that connected to topQuestionnaires.
        List<ParseObject> foundPicturesParseObjectsIds =
                ImageUtils.fetchPictuesObjects(
                        topQuestionnaireIds, questionnaireIdToImageParseObjectMap);
        foundPicturesParseObjectsIds = filterNullObjects(foundPicturesParseObjectsIds);

        // Chosen pictures part.
        if (foundPicturesParseObjectsIds != null) {
            userReport.setChosenPictures(
                    ImageUtils.getChosenPictures(foundPicturesParseObjectsIds,
                            imageIdToRekognitionParseObjectMap, imageIdToGPSParseObjectMap));
        }

        // USAGE Process.
        List<UsageProperties> usagePropertiesList = UsageUtils.getUsages(topQuestionnaireIds);
        userReport.setUsageProperties(usagePropertiesList);
        // Questionnaires answers.
        userReport.setUserAnswers(
                QuestionnairesParseUtils.getUserAnswers(filteredQuestionnaires));

        /**
         * REKOGNITION.
         */
        List<RekognitionFeature> userRekognitionFeatures =
                RekognitionFeaturesUtils.getUserRekognitionFeatures(topQuestionnaireIds,
                        questionnaireIdToImageParseObjectMap, imageIdToRekognitionParseObjectMap);
        userReport.setRekognitionFeatures(userRekognitionFeatures);

        /**
         * LOCATION.
         */
        // GPS ParseObjects that connected to topQuestionnaires.
        List<ParseObject> gpsParseObjects =
                LocationUtils.fetchLocationsOfQuestionnairesIDS(
                        topQuestionnaireIds, questionnaireIdToGPSParseObject);

        // LOCATIONS IMAGES.
        List<Bitmap> usersBitmapsLocations =
                ImageUtils.getBitmapForGpsParseObjects(
                        gpsParseObjects, questionnaireIdToImageParseObjectMap);
        userReport.setLocationPictures(usersBitmapsLocations);

        // LOCATION PROPERTIES.
        List<LocationProperties> locationProperties =
                LocationUtils.fetchLocationProperties(gpsParseObjects);
        userReport.setUserLocationProperties(locationProperties);
    }

    private List<ParseObject> filterNullObjects(List<ParseObject> foundPicturesParseObjectsIds) {
        List<ParseObject> nullObjectsToDelete = new ArrayList<>();
        foundPicturesParseObjectsIds.stream()
                .filter(parseObject -> parseObject == null)
                .forEach(parseObject -> {
                    nullObjectsToDelete.add(parseObject);
                });
        foundPicturesParseObjectsIds.removeAll(nullObjectsToDelete);
        return foundPicturesParseObjectsIds;
    }


    // Types of periodic reports the user can choose.
    public enum RangeFilter {
        JANUARY,
        FEBRUARY,
        MARCH,
        APRIL,
        MAY,
        JUNE,
        JULY,
        AUGUST,
        SEPTEMBER,
        OCTOBER,
        NOVEMBER,
        DECEMBER,
        LAST_SEVEN_DAYS,
        LAST_THIRTY_DAYS,
        NONE_FILTER
    }
}