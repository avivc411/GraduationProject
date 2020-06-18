package com.Project.project.Report.Utilities;

import android.content.Context;

import com.Project.project.GPS.LocationProperties;
import com.Project.project.Report.UserReportProcessor;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class QuestionnairesFilteringUtilTest {
    Context context = mock(Context.class);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void filterQuestionnairesByRelevantDatesTestNoneFilter() {
        UserReportProcessor.RangeFilter rangeFilterNone = UserReportProcessor.RangeFilter.NONE_FILTER;
        List<ParseObject> allQuestionnaires = new ArrayList<>();
        ParseObject firstParse = new ParseObject("Questionnaire");
        firstParse.put("upload_date","2020-05-09T09:55:12.743Z");
        allQuestionnaires.add(firstParse);
        List<ParseObject> output = QuestionnairesFilteringUtils.filterQuestionnairesByRelevantDates(
                allQuestionnaires, rangeFilterNone);
        // None Filter should return all questionnaires.
        Assert.assertEquals(allQuestionnaires,output);
    }

    @Test
    public void filterQuestionnairesByRelevantDatesTestMonthFilter() {
        UserReportProcessor.RangeFilter rangeFilterMonth = UserReportProcessor.RangeFilter.MAY;
        List<ParseObject> allQuestionnaires = new ArrayList<>();
        ParseObject firstParse = new ParseObject("Questionnaire");
        Date date = new Date();
        date.setMonth(4);
        System.out.println(date);
        firstParse.put("upload_date",date);
        allQuestionnaires.add(firstParse);
        List<ParseObject> output = QuestionnairesFilteringUtils.filterQuestionnairesByRelevantDates(
                allQuestionnaires, rangeFilterMonth);
        // None Filter should return all questionnaires.
        Assert.assertEquals(allQuestionnaires,output);
    }
}