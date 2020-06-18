package com.Project.project.UserManagment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.Project.project.Handlers.GetSettingsHandler;
import com.Project.project.Handlers.LoginHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;

public class AWSCognitoUserManagerTest {
    private Context context;
    private Activity activity;
    private String username, password, usernameWithAtt, usernameWithAttPass;
    private AWSCognitoUserManager awsCognitoUserManager;
    private Map<String, String> attributes;

    @Before
    public void setUp() throws Exception {
        context = mock(Context.class);
        activity = mock(Activity.class);
        username = "test";
        password = "test12";
        usernameWithAtt = "testAtt";
        usernameWithAttPass = "testAttPass";
        attributes = new HashMap<>();
        awsCognitoUserManager = AWSCognitoUserManager.getInstance(context);
    }

    @Test
    public void getInstance() {
        AWSCognitoUserManager awsCognitoUserManager2 = AWSCognitoUserManager.getInstance(context);
        assertEquals(awsCognitoUserManager, awsCognitoUserManager2);
    }

    @Test
    public void login() {
        AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                assertEquals(userSession.getUsername(), username);
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
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

            @Override
            public void onFailure(Exception exception) {
                fail();
            }
        };

        AWSCognitoUserManager awsCognitoUserManager = AWSCognitoUserManager.getInstance(context);
        awsCognitoUserManager.login(username, password, authenticationHandler);
    }

    @Test
    public void register() {
        String email = "test@gmail.com";
        SignUpHandler signUpHandler = new SignUpHandler() {
            @Override
            public void onSuccess(CognitoUser user, SignUpResult signUpResult) {
                assertEquals(user.getUserId(), username);
            }

            @Override
            public void onFailure(Exception exception) {
                fail();
            }
        };
        awsCognitoUserManager.register(username, password, email, null, signUpHandler);
    }

    @Test
    public void registerWithAttributes() {
        String email = "testAtt@gmail.com";
        SignUpHandler signUpHandler = new SignUpHandler() {
            @Override
            public void onSuccess(CognitoUser user, SignUpResult signUpResult) {
                assertEquals(user.getUserId(), username);
            }

            @Override
            public void onFailure(Exception exception) {
                fail();
            }
        };

        attributes.put("custom:Camera", "1");
        attributes.put("custom:GPS", "1");
        attributes.put("custom:Usage", "1");
        attributes.put("custom:firstLogin", "1");

        awsCognitoUserManager.register(username, password, email, attributes, signUpHandler);
    }

    @Test
    public void getRekognitionIdentityPoolCredentials() {
    }

    @Test
    public void rememberUser() {
        awsCognitoUserManager.rememberUser(context, username, password);
        SharedPreferences preferences = context.getSharedPreferences("EmotionAnalyzer", Context.MODE_PRIVATE);
        assertEquals(preferences.getString("Username", ""), username);
        assertEquals(preferences.getString("Password", ""), password);
    }

    @Test
    public void autoConnect1() {
        awsCognitoUserManager.logout(context);
        assertFalse(awsCognitoUserManager.autoConnect(context, null));
    }

    @Test
    public void autoConnect2() {
        login();

        AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                assertEquals(userSession.getUsername(), username);
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
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

            @Override
            public void onFailure(Exception exception) {
                fail();
            }
        };

        LoginHandlerStub stub = new LoginHandlerStub(activity,
                awsCognitoUserManager,
                null,
                null,
                new Dialog(context),
                username,
                password,
                false);
        stub.setHandler(authenticationHandler);

        assertTrue(awsCognitoUserManager.autoConnect(context, stub));
    }

    @Test
    public void logout() {
        awsCognitoUserManager.logout(context);
        assertFalse(awsCognitoUserManager.autoConnect(context, null));
    }

    @Test
    public void getAttributesEmpty() {
        AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                assertEquals(userSession.getUsername(), username);
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
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

            @Override
            public void onFailure(Exception exception) {
                fail();
            }
        };
        attributes.clear();

        awsCognitoUserManager.logout(context);
        awsCognitoUserManager.login(username, password, authenticationHandler);

        awsCognitoUserManager.getAttributes(new GetSettingsHandler(attributes));
        assertEquals(attributes.size(), 0);
    }

    @Test
    public void getAttributesFull(){
        AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                assertEquals(userSession.getUsername(), username);
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
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

            @Override
            public void onFailure(Exception exception) {
                fail();
            }
        };
        Map<String, String> attributesCopy = new HashMap<>();
        attributesCopy.put("Camera", "1");
        attributesCopy.put("GPS", "1");
        attributesCopy.put("Usage", "1");
        attributesCopy.put("firstLogin", "1");

        attributes.clear();

        awsCognitoUserManager.logout(context);
        awsCognitoUserManager.login(usernameWithAtt, usernameWithAttPass, authenticationHandler);

        awsCognitoUserManager.getAttributes(new GetSettingsHandler(attributes));

        for(Map.Entry entry : attributes.entrySet()) {
            assertTrue(attributesCopy.containsKey(entry.getKey()));
            assertEquals(entry.getValue(), attributesCopy.get(entry.getKey()));
        }
    }

    @Test
    public void getAttributesInBackground() {
    }

    @Test
    public void setAttributes() {
    }

    @Test
    public void setAttributesInBackground() {
    }

    @Test
    public void forgotPassword() {
    }

    @Test
    public void verify() {
    }

    @Test
    public void deleteUser(){
        GenericHandler handler = new GenericHandler() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(Exception exception) {

            }
        };
        awsCognitoUserManager.deleteUser(context, username, handler);
    }
}