package com.Project.project.Handlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;

public class FirstLoginDetailsHandler implements GetDetailsHandler {
    private Activity activity;
    private Intent continuationIntent, alterContinuationIntent;

    public FirstLoginDetailsHandler(Activity activity, Intent continuationIntent, Intent alterContinuationIntent) {
        this.activity = activity;
        this.continuationIntent = continuationIntent;
        this.alterContinuationIntent = alterContinuationIntent;
    }

    /**
     * Continue login process. In case of first login or first use of the app, the user needs to set his setting,
     * otherwise continue to home page.
     *
     * @param cognitoUserDetails Users details from Cognito users pool.
     */
    @Override
    public void onSuccess(CognitoUserDetails cognitoUserDetails) {
        System.out.println("FirstLoginDetailsHandler SUCCESS");

        activity.startActivity(continuationIntent);
        if ("1".equals(cognitoUserDetails.getAttributes().getAttributes().get("custom:firstLogin")) ||
                activity.getSharedPreferences(
                        "EmotionAnalyzer", Context.MODE_PRIVATE)
                        .getString("FirstUse", "")
                        .equals(""))
            activity.startActivityForResult(alterContinuationIntent, 555);
    }

    /**
     * Send requested permissions to intent.
     *
     * @param cognitoUserDetails Requested permissions details in user's attributes.
     * @param intent             Intent to send details to.
     */
    private void putExtra(CognitoUserDetails cognitoUserDetails, Intent intent) {
        intent.putExtra("custom:GPS",
                cognitoUserDetails.getAttributes().getAttributes().get("custom:GPS"));
        intent.putExtra("custom:Camera",
                cognitoUserDetails.getAttributes().getAttributes().get("custom:Camera"));
        intent.putExtra("custom:Usage",
                cognitoUserDetails.getAttributes().getAttributes().get("custom:Usage"));
    }

    @Override
    public void onFailure(Exception exception) {
        System.out.println("FirstLoginDetailsHandler FAILURE");
        exception.printStackTrace();
        Toast.makeText(activity, "Error Occurred", Toast.LENGTH_SHORT).show();
    }
}
