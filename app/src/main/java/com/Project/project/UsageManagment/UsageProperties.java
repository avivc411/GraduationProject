package com.Project.project.UsageManagment;

import java.util.Date;
import java.util.Map;

public class UsageProperties {
    Date date;
    // name of application: time of use. e.g. facebook 01:20:14.
    Map<String, String> applicationAndTime;

    public UsageProperties(Date date, Map<String, String> applicationAndTime) {
        this.date = date;
        this.applicationAndTime = applicationAndTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Map<String, String> getApplicationAndTime() {
        return applicationAndTime;
    }

    public void setApplicationAndTime(Map<String, String> applicationAndTime) {
        this.applicationAndTime = applicationAndTime;
    }
}

