package com.Project.project.Fitbit.Utilities;

public class SleepSample extends FitbitSample {
    int totalMinutesAsleep, totalSleepRecords, totalTimeInBed;

    public SleepSample(int totalMinutesAsleep, int totalSleepRecords, int totalTimeInBed) {
        this.totalMinutesAsleep = totalMinutesAsleep;
        this.totalSleepRecords = totalSleepRecords;
        this.totalTimeInBed = totalTimeInBed;
    }

    public int getTotalMinutesAsleep() {
        return totalMinutesAsleep;
    }

    public int getTotalSleepRecords() {
        return totalSleepRecords;
    }

    public int getTotalTimeInBed() {
        return totalTimeInBed;
    }

    @Override
    public String toString() {
        return "SleepSample{" +
                "totalMinutesAsleep=" + totalMinutesAsleep +
                ", totalSleepRecords=" + totalSleepRecords +
                ", totalTimeInBed=" + totalTimeInBed +
                '}';
    }
}
