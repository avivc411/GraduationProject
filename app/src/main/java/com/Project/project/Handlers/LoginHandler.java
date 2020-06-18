package com.Project.project.Handlers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.widget.Toast;

import com.Project.project.Activities.MainActivity;
import com.Project.project.UserManagment.AWSCognitoUserManager;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidentityprovider.model.TooManyRequestsException;
import com.amazonaws.services.cognitoidentityprovider.model.UserNotConfirmedException;

/**
 * Handles the login process results on AWS Cognito users pool.
 */
public class LoginHandler implements AuthenticationHandler {
    private Activity activity;
    private AWSCognitoUserManager userManager;
    private Intent continuationIntent, alterContinuationIntent;
    private Dialog loginDialog;
    private String username, password;
    private boolean rememberMe;

    public LoginHandler(Activity activity, AWSCognitoUserManager userManager, Intent continuationIntent,
                        Intent alterContinuationIntent, Dialog loginDialog, String username, String password,
                        boolean rememberMe) {
        this.activity = activity;
        this.userManager = userManager;
        this.continuationIntent = continuationIntent;
        this.alterContinuationIntent = alterContinuationIntent;
        this.loginDialog = loginDialog;
        this.username = username;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    /**
     * Set relevant fields on AWSCognitoUserManager, dismiss login animation and starts the next activity.
     *
     * @param userSession Current user's session.
     * @param newDevice   Current user's device.
     */
    @Override
    public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
        if(rememberMe)
            userManager.rememberUser(activity, username, password);
        userManager.getAttributesInBackground(
                new FirstLoginDetailsHandler(
                        activity,
                        continuationIntent,
                        alterContinuationIntent)
        );
        loginDialog.dismiss();
    }

    /**
     * Provide user's login details to the login process.
     *
     * @param authenticationContinuation Next task in the process.
     * @param userId                     User's username.
     */
    @Override
    public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
        if (username == null || password == null)
            return;
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, password, null);
        authenticationContinuation.setAuthenticationDetails(authenticationDetails);
        authenticationContinuation.continueTask();
    }

    @Override
    public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
    }

    @Override
    public void authenticationChallenge(ChallengeContinuation continuation) {
    }

    /**
     * On event of failure, inform user and act accordingly.
     *
     * @param exception
     */
    @Override
    public void onFailure(Exception exception) {
        loginDialog.dismiss();
        if (exception instanceof UserNotConfirmedException) {
            if (activity instanceof MainActivity) {
                MainActivity ma = (MainActivity) activity;
                ma.switchView("firstLogin");
            }
        } else if (exception instanceof TooManyRequestsException) {
            Toast.makeText(activity, "Too Many Attempts, Please Try Again Later", Toast.LENGTH_SHORT).show();
        } else if (exception instanceof NotAuthorizedException) {
            Toast.makeText(activity, "Wrong Username Or Password", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Error Occurred, Please Try Again Later", Toast.LENGTH_SHORT).show();
        }
        exception.printStackTrace();
    }

    public void setUsername(String username) {
        this.username = username;
        alterContinuationIntent.putExtra("Username", username);
        continuationIntent.putExtra("Username", username);
    }

    public void setPassword(String password) {
        this.password = password;
    }
}