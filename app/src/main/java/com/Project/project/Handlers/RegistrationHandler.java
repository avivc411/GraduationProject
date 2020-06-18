package com.Project.project.Handlers;

import android.app.Activity;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult;
import com.amazonaws.services.cognitoidentityprovider.model.UsernameExistsException;

/**
 * Handles sign up process results on AWS Cognito users' pool.
 */
public class RegistrationHandler implements SignUpHandler {
    private Activity activity;

    public RegistrationHandler(Activity activity) {
        this.activity = activity;
    }

    /**
     * Show a toast in the calling activity.
     *
     * @param user         currently irrelevant.
     * @param signUpResult currently irrelevant.
     */
    @Override
    public void onSuccess(CognitoUser user, SignUpResult signUpResult) {
        Toast.makeText(activity, "You have been registered successfully!", Toast.LENGTH_SHORT).show();
        activity.finish();
    }

    /**
     * Show a toast with information on the failure reason in the calling activity.
     *
     * @param exception
     */
    @Override
    public void onFailure(Exception exception) {
        exception.printStackTrace();
        if (exception instanceof UsernameExistsException)
            Toast.makeText(activity, "Username already exists", Toast.LENGTH_SHORT).show();
        else if (exception instanceof InvalidPasswordException)
            Toast.makeText(activity, "Password is illegal", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(activity, "Error occurred", Toast.LENGTH_SHORT).show();
    }
}
