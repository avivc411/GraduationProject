package com.Project.project.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Containing mood alert information.
 */
public class MoodAlert extends DialogFragment {
    int checkedItemPosition;
    MoodAlert.SingleChoiceListener chosenMoodListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            chosenMoodListener = (SingleChoiceListener) context;
        } catch (Exception e) {
            e.toString();
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Instantiate alert dialog builder.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog moodGradeDialog;

        // Data source.
        final String[] moodGrade = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        builder.setTitle("Thanks! How is your mood?\n 1 - Unhappy | 9 - Very Happy");
        // Default mood: 4.
        checkedItemPosition = 3;
        // Setting up builder.
        // Updating chosen mood.
        builder.setSingleChoiceItems(moodGrade, checkedItemPosition,
                (dialogInterface, position) -> checkedItemPosition = position);

        // Add OK button.
        builder.setPositiveButton("OK", (dialog, position) -> {
            // User clicked OK.
            chosenMoodListener.onAgreeButtonClicked(moodGrade, checkedItemPosition);
        });
        builder.setCancelable(false);

        // Create dialog.
        moodGradeDialog = builder.create();
        return moodGradeDialog;
    }


    public interface SingleChoiceListener {
        void onAgreeButtonClicked(String[] list, int position);
    }
}
