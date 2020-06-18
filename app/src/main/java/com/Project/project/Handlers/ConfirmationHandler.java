package com.Project.project.Handlers;

import android.widget.Toast;

import com.Project.project.Activities.MainActivity;
import com.amazonaws.services.cognitoidentityprovider.model.CodeMismatchException;
import com.amazonaws.services.cognitoidentityprovider.model.ExpiredCodeException;

public class ConfirmationHandler implements com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler {
    private MainActivity activity;

    public ConfirmationHandler(MainActivity mainActivity) {
        activity = mainActivity;
    }

    @Override
    public void onSuccess() {
        System.out.println("CONFIRMATION SUCCESS");
        activity.confirmed();
    }

    @Override
    public void onFailure(Exception exception) {
        System.out.println("CONFIRMATION Failure");
        if (exception instanceof CodeMismatchException) {
            Toast.makeText(activity, "Mismatch Code", Toast.LENGTH_SHORT).show();
        } else if (exception instanceof ExpiredCodeException) {
            Toast.makeText(activity, "Code Expired", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(activity, "Error Occurred", Toast.LENGTH_SHORT).show();
        exception.printStackTrace();
    }
}
