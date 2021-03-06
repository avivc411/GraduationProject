package com.Project.project.Report;

import android.graphics.Bitmap;

import com.Project.project.GPS.LocationProperties;
import com.Project.project.RekognitionManagment.RekognitionFeature;
import com.Project.project.UsageManagment.UsageProperties;
import com.Project.project.Utilities.SentimentScore;
import com.Project.project.Utilities.UserChosenPicture;

import java.util.List;
import java.util.Map;

/**
 * Contains relevant information for user negative report data.
 */
public class NegativeSentimentUserReport extends UserReport {
    public NegativeSentimentUserReport(SentimentScore sentimentScore, String reportSummary,
                                       Map<String, Integer> userAnswers,
                                       List<RekognitionFeature> rekognitionFeatures,
                                       List<LocationProperties> userLocationProperties,
                                       List<Bitmap> locationPictures,
                                       List<UserChosenPicture> chosenPictures,
                                       List<UsageProperties> usageProperties) {
        super(sentimentScore, reportSummary, userAnswers, rekognitionFeatures
                , userLocationProperties, locationPictures, chosenPictures
                , usageProperties);
    }

    @Override
    protected String getReportName() {
        return "Negative Report";
    }

    @Override
    protected ReportType getReportType() {
        return ReportType.NEGATIVE;
    }
}
