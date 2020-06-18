package com.Project.project.Fitbit.Utilities;

public class HeartRateSample extends FitbitSample {
    private int min, max;
    private String name;

    public HeartRateSample(int max, int min, String name) {
        this.min = min;
        this.max = max;
        this.name = name;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "HeartRateSample{" +
                "min=" + min +
                ", max=" + max +
                ", name='" + name + '\'' +
                '}';
    }
}
