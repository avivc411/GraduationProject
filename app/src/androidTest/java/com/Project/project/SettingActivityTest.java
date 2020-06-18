package com.Project.project;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.Project.project.Activities.SettingActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class SettingActivityTest {
    @Rule
    public ActivityTestRule<SettingActivity> activity = new ActivityTestRule<>(SettingActivity.class);

    @Test
    /**
     * test that all buttons change and are checked as default and latter unchecked
     */
    public void testButtonSwitch() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.switchGPS)).check(matches(isChecked()));
        onView(withId(R.id.switchcamera)).check(matches(isChecked()));
        onView(withId(R.id.switchMontorApp)).check(matches(isChecked()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.switchGPS)).perform(click());
        onView(withId(R.id.switchMontorApp)).perform(click());
        onView(withId(R.id.switchcamera)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.switchcamera)).check(matches(isNotChecked()));
        onView(withId(R.id.switchGPS)).check(matches(isNotChecked()));
        onView(withId(R.id.switchMontorApp)).check(matches(isNotChecked()));
    }
}

