package com.Project.project.UserManagment;

import android.content.Context;
import android.content.SharedPreferences;

import com.Project.project.Handlers.LoginHandler;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.UpdateAttributesHandler;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.model.CognitoIdentityProvider;

import java.util.Map;


/**
 * This class used to manage the app's users through AWS Cognito service as a singleton.
 * In order to operate, this class require poolID, clientID, client secret and region to work in.
 * All those parameters can be achieved from AWS console.
 * The management is based on identity pools.
 */
public class AWSCognitoUserManager {
    private static AWSCognitoUserManager mngr;

    //Cognito variables
    private CognitoUserPool userPool;

    private AWSCognitoUserManager(Context context) {
        if (userPool == null) {
            Regions cognitoRegion = Regions.US_EAST_2;
            String clientSecret = "";
            String clientId = "";
            String userPoolId = "";

            userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, cognitoRegion);
        }
    }

    public static AWSCognitoUserManager getInstance(Context context) {
        if (mngr == null)
            mngr = new AWSCognitoUserManager(context);
        return mngr;
    }

    /**
     * Login to the system.
     *
     * @param username              Username of the user.
     * @param password              Password of the user.
     * @param authenticationHandler Handled to act according to the result.
     *                              Should be received from the calling activity.
     */
    public void login(String username, String password, AuthenticationHandler authenticationHandler) {
        AuthenticationDetails details = new AuthenticationDetails(username, password, null);
        new Thread(
                userPool.getUser(username).initiateUserAuthentication(details, authenticationHandler, true)
        ).start();
    }

    /**
     * Register to the system.
     *
     * @param userName      Username of the user.
     * @param password      Password of the user.
     * @param email         email of the user - to confirm account and to recover password.
     * @param signUpHandler Handled to act according to the result.
     *                      Should be received from the calling activity.
     */
    public void register(String userName, String password, String email, Map<String, String> customAttributes, SignUpHandler signUpHandler) {
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();

        if (email.length() > 0)
            userAttributes.addAttribute("email", email);
        if (customAttributes != null && customAttributes.size() > 0)
            for (String name : customAttributes.keySet())
                userAttributes.addAttribute(name, customAttributes.get(name));
        userPool.signUpInBackground(userName, password, userAttributes, null, signUpHandler);
    }

    /**
     * Creating a credentials provider in order to operate Rekognition.
     *
     * @param context context of the calling activity.
     * @return credential provider.
     */
    public CognitoCachingCredentialsProvider getRekognitionIdentityPoolCredentials(Context context) {
        return new CognitoCachingCredentialsProvider(
                context, // Context
                "us-east-2:18199045-0c5b-4613-a271-e15dfd12a49e", // Identity Pool ID
                Regions.US_EAST_2 // Region
        );
    }

    public void rememberUser(Context context, String username, String password) {
        SharedPreferences preferences = context.getSharedPreferences("EmotionAnalyzer", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Username", username);
        editor.putString("Password", password);
        editor.apply();
    }

    /**
     * Reconnect if token exists.
     */
    public boolean autoConnect(Context context, LoginHandler authenticationHandler) {
        SharedPreferences preferences = context.getSharedPreferences("EmotionAnalyzer", Context.MODE_PRIVATE);
        String username = preferences.getString("Username", "");
        String password = preferences.getString("Password", "");
        if ("".equals(username) || "".equals(password))
            return false;
        authenticationHandler.setUsername(username);
        authenticationHandler.setPassword(password);
        login(username, password, authenticationHandler);
        return true;
    }

    public void logout(Context context) {
        userPool.getCurrentUser().signOut();
        SharedPreferences preferences = context.getSharedPreferences("EmotionAnalyzer", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("Username");
        editor.remove("Password");
        editor.apply();
    }

    /**
     * Get user's attributes.
     *
     * @param handler Handle succession or failure results.
     */
    public void getAttributes(GetDetailsHandler handler) {
        userPool.getCurrentUser().getDetails(handler);
    }

    /**
     * Get user's attributes in background.
     *
     * @param handler Handle succession or failure results.
     */
    public void getAttributesInBackground(GetDetailsHandler handler) {
        userPool.getCurrentUser().getDetailsInBackground(handler);
    }

    /**
     * Update User's attributes.
     *
     * @param attributes Updated attributes.
     * @param handler    Handles results.
     */
    public void setAttributes(Map<String, String> attributes, UpdateAttributesHandler handler) {
        CognitoUserAttributes cognitoUserAttributes = new CognitoUserAttributes();
        for (String name : attributes.keySet())
            cognitoUserAttributes.addAttribute("custom:" + name, attributes.get(name));

        userPool.getCurrentUser().updateAttributes(cognitoUserAttributes, handler);
    }

    /**
     * Update User's attributes in background.
     *
     * @param attributes Updated attributes.
     * @param handler    Handles results.
     */
    public void setAttributesInBackground(Map<String, String> attributes, UpdateAttributesHandler handler) {
        CognitoUserAttributes cognitoUserAttributes = new CognitoUserAttributes();
        for (String name : attributes.keySet())
            cognitoUserAttributes.addAttribute("custom:" + name, attributes.get(name));

        userPool.getCurrentUser().updateAttributesInBackground(cognitoUserAttributes, handler);
    }

    public void forgotPassword(String username, ForgotPasswordHandler handler) {
        userPool.getUser(username).forgotPasswordInBackground(handler);
    }

    /**
     * Verify user's account.
     *
     * @param username User's username.
     * @param code     Verification code from user's email.
     */
    public void verify(String username, String code, GenericHandler handler) {
        userPool.getUser(username).confirmSignUpInBackground(code, true, handler);
    }

    public void deleteUser(Context context, String username, GenericHandler handler){
        try {
            logout(context);
            userPool.getUser(username).deleteUser(handler);
        }catch (Exception ignore){}
    }
}
