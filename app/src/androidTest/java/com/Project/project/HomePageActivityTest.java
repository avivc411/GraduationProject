package com.Project.project;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.Project.project.Activities.HomePageActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class HomePageActivityTest {
    @Rule
//    public ActivityTestRule<HomePageActivity> activity = new ActivityTestRule<>(HomePageActivity.class);
    public IntentsTestRule<HomePageActivity> activityIntent = new IntentsTestRule<>(HomePageActivity.class);

    @Test
    /**
     * test that all buttons change and are checked as default and latter unchecked
     */
    public void testWatchReportButton() {
        onView(withId(R.id.watchReportBtn)).perform(click());
    }

    public void testSurveryReportButton() {
        onView(withId(R.id.fill_survey)).perform(click());
    }

    public void testSettingButton() {
        onView(withId(R.id.setting)).perform(click());
    }

    public void testNotifyButton() {
        onView(withId(R.id.notifiyremind)).perform(click());
    }
}







