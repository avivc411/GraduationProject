package com.Project.project.Handlers;

import android.app.Activity;
import android.widget.Toast;

import com.Project.project.Activities.MainActivity;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;

/**
 * Handles forgot password process results.
 */
public class ForgotPasswordRequestHandler implements ForgotPasswordHandler {
    private Activity activity;
    private ForgotPasswordContinuation continuation;

    public ForgotPasswordRequestHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onSuccess() {
        System.out.println("ForgotPasswordRequestHandler SUCCESS");
        Toast.makeText(activity,
                "Your Password Has Been Reset!", Toast.LENGTH_SHORT).show();
        MainActivity ma = (MainActivity) activity;
        ma.switchView("");
    }

    @Override
    public void getResetCode(ForgotPasswordContinuation continuation) {
        Toast.makeText(activity,
                "Your Code Sent To " + continuation.getParameters().getDestination(), Toast.LENGTH_LONG).show();
        System.out.println("### Your Code Sent To " + continuation.getParameters().getDestination());
        this.continuation = continuation;
        MainActivity ma = (MainActivity) activity;
        ma.switchView("enterCodeNewPass");
    }

    @Override
    public void onFailure(Exception exception) {
        System.out.println("ForgotPasswordRequestHandler FAILURE");
        exception.printStackTrace();
    }

    /**
     * Set new password and code on continuation in order to confirm password change.
     *
     * @param verificationCode Code sent in email to the user.
     * @param newPassword      User's new password.
     */
    public void confirmPassword(String verificationCode, String newPassword) {
        continuation.setPassword(newPassword);
        continuation.setVerificationCode(verificationCode);
        continuation.continueTask();
    }
}