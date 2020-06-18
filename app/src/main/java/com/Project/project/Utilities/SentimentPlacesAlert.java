package com.Project.project.Utilities;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Responsible for places's sentiment list alert.
 */
public class SentimentPlacesAlert extends DialogFragment {
    int checkedItemPosition;
    SentimentPlacesAlert.SingleChoiceListener chosenPlaceListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            chosenPlaceListener = (SingleChoiceListener) context;
        } catch (Exception e) {
            e.toString();
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Instantiate alert dialog builder.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog locationDialog;

        // Data source.
        final String[] places = {"Home", "Friend's home", "Work", "University", "Gym", "Cinema",
                "Shopping mall", "Other"};
        builder.setTitle("Thanks! Where are you?");
        // Default place: Other.
        checkedItemPosition = places.length - 1;
        // Setting up builder.
        // Updating chosen place.
        builder.setSingleChoiceItems(places, checkedItemPosition, (dialogInterface, position) -> checkedItemPosition = position);

        // Add OK button.
        builder.setPositiveButton("OK", (dialog, position) -> {
            // User clicked OK.
            chosenPlaceListener.onAgreeButtonClicked(places, checkedItemPosition);
        });
        //builder.setNegativeButton("Cancel", null);

        // Create dialog.
        locationDialog = builder.create();
        return locationDialog;
        // Show dialog.
        //locationDialog.show();
    }


    public interface SingleChoiceListener {
        void onAgreeButtonClicked(String[] list, int position);
        // void onNegativeButtonClicked();
    }
}
