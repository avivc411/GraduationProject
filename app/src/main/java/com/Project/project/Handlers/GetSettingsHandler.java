package com.Project.project.Handlers;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;

import java.util.Map;

/**
 * Handles getting details from AWS Cognito users pool.
 */
public class GetSettingsHandler implements GetDetailsHandler {
    private Map<String, String> settings;

    public GetSettingsHandler(Map<String, String> settings) {
        this.settings = settings;
    }

    /**
     * Copy user's attributes from cognitoUserDetails to settings.
     * Custom attributes being copy without the "custom:" prefix.
     *
     * @param cognitoUserDetails User's attributes.
     */
    @Override
    public void onSuccess(CognitoUserDetails cognitoUserDetails) {
        System.out.println("GetSettingsHandler  SUCCESS");
        Map<String, String> attributes = cognitoUserDetails.getAttributes().getAttributes();
        for (String name : attributes.keySet()) {
            String[] splitName = name.split(":");
            if (splitName.length == 2)
                settings.put(splitName[splitName.length - 1], attributes.get(name));
        }
    }

    @Override
    public void onFailure(Exception exception) {
        System.out.println("GetSettingsHandler FAILURE");
        exception.printStackTrace();
    }
}