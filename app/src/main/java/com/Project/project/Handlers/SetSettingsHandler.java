package com.Project.project.Handlers;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.UpdateAttributesHandler;

import java.util.List;

/**
 * Handles an update of user's attributes to AWS Cognito users pool.
 */
public class SetSettingsHandler implements UpdateAttributesHandler {
    private Activity activity;

    public SetSettingsHandler(Activity activity) {
        this.activity = activity;
    }

    /**
     * Write not first use in shared pref.
     * Show a toast in the calling activity.
     *
     * @param attributesVerificationList irrelevant.
     */
    @Override
    public void onSuccess(List<CognitoUserCodeDeliveryDetails> attributesVerificationList) {
        System.out.println("SetSettingsHandler SUCCESS");
        activity.getSharedPreferences("EmotionAnalyzer", Context.MODE_PRIVATE)
                .edit()
                .putString("FirstUse", "no")
                .apply();
        activity.finish();
    }

    /**
     * Show a toast in the calling activity.
     *
     * @param exception
     */
    @Override
    public void onFailure(Exception exception) {
        System.out.println("SetSettingsHandler FAILURE");
        activity.runOnUiThread(() -> Toast.makeText(activity, "Fail :(", Toast.LENGTH_SHORT).show());
        exception.printStackTrace();
    }
}