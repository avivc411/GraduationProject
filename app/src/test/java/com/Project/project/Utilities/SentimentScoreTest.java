package com.Project.project.Utilities;

import android.content.Context;

import com.Project.project.GPS.SemanticMoodData;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.mockito.Mockito.mock;

public class SentimentScoreTest {
    Context context = mock(Context.class);
   SentimentScore sentimentScore = new SentimentScore(2,16,20,
           8,12);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void defaultSettings(){
        sentimentScore.setAmountOfQuestionnaires(2);
        sentimentScore.setTotalScore(16);
        sentimentScore.setMaxPossibleTotalScore(20);
        sentimentScore.setAverage(8);
        sentimentScore.setMaxPossibleAverage(12);
    }

    @Test
    public void testGetAmountOfQuestionnaires() {
        Assert.assertEquals(2,sentimentScore.getAmountOfQuestionnaires());
    }

    @Test
    public void testSetAmountOfQuestionnaires() {
        sentimentScore.setAmountOfQuestionnaires(4);
        Assert.assertEquals(4,sentimentScore.getAmountOfQuestionnaires());

    }


    @Test
    public void testGetTotalScore() {
        Assert.assertEquals(16,sentimentScore.getTotalScore());
    }

    @Test
    public void testSetTotalScore() {
        sentimentScore.setTotalScore(19);
        Assert.assertEquals(19,sentimentScore.getTotalScore());
    }

    @Test
    public void testGetMaxPossibleTotalScore() {
        Assert.assertEquals(20,sentimentScore.getMaxPossibleTotalScore());

    }

    @Test
    public void testSetMaxPossibleTotalScore() {
        sentimentScore.setMaxPossibleTotalScore(24);
        Assert.assertEquals(24,sentimentScore.getMaxPossibleTotalScore());
    }

    @Test
    public void testGetAverage() {
        Assert.assertEquals(8,sentimentScore.getAverage());
    }

    @Test
    public void testSetAverage() {
        sentimentScore.setAverage(9);
        Assert.assertEquals(9,sentimentScore.getAverage());

    }

    @Test
    public void testGetMaxPossibleAverage() {
        Assert.assertEquals(12,sentimentScore.getMaxPossibleAverage());
    }

    @Test
    public void testSetMaxPossibleAverage() {
        sentimentScore.setMaxPossibleAverage(10);
        Assert.assertEquals(10,sentimentScore.getMaxPossibleAverage());
    }
}