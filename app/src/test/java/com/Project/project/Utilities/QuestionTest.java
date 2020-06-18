package com.Project.project.Utilities;

import android.content.Context;
import android.content.Intent;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;

public class QuestionTest {
    Context context = mock(Context.class);
   Question question = new Question(24,"happy",4,2);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void defaultSettings(){
        question.setId(24);
        question.setImage(2);
        question.setName("happy");
        question.setRating(4);
    }

    @Test
    public void testGetId() {
        Assert.assertEquals(24,question.getId());
    }

    @Test
    public void testSetId() {
        question.setId(20);
        Assert.assertEquals(20,question.getId());
    }

    @Test
    public void testGetImage() {
        Integer integer = 2;
        Assert.assertEquals(integer,question.getImage());
    }

    @Test
    public void testGetName() {
        Assert.assertEquals("happy",question.getName());
    }

    @Test
    public void testSetName() {
        question.setName("excited");
        Assert.assertEquals("excited",question.getName());
    }

    @Test
    public void testGetRating() {
        Assert.assertEquals(4,question.getRating());
    }

    @Test
    public void testSetRating() {
        question.setRating(8);
        Assert.assertEquals(8,question.getRating());
    }




}