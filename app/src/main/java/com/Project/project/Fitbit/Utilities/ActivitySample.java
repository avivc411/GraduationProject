package com.Project.project.Fitbit.Utilities;

public class ActivitySample extends FitbitSample {
    private int value;

    public ActivitySample(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ActivitySample{" +
                "value=" + value +
                '}';
    }
}
