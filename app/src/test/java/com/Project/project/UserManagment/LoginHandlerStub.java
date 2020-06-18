package com.Project.project.UserManagment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;

import com.Project.project.Handlers.LoginHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

public class LoginHandlerStub extends LoginHandler {
    public LoginHandlerStub(Activity activity, AWSCognitoUserManager userManager, Intent continuationIntent, Intent alterContinuationIntent, Dialog loginDialog, String username, String password, boolean rememberMe) {
        super(activity, userManager, continuationIntent, alterContinuationIntent, loginDialog, username, password, rememberMe);
    }

    private AuthenticationHandler handler;

    public void setHandler(AuthenticationHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
        handler.onSuccess(userSession, newDevice);
    }

    @Override
    public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
        super.getAuthenticationDetails(authenticationContinuation, userId);
    }

    @Override
    public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
        super.getMFACode(continuation);
    }

    @Override
    public void authenticationChallenge(ChallengeContinuation continuation) {
        super.authenticationChallenge(continuation);
    }

    @Override
    public void onFailure(Exception exception) {
        handler.onFailure(exception);
    }

    @Override
    public void setUsername(String username) {
        super.setUsername(username);
    }

    @Override
    public void setPassword(String password) {
        super.setPassword(password);
    }
}
