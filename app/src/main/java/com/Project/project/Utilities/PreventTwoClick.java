package com.Project.project.Utilities;

import android.view.View;

public class PreventTwoClick {
    /**
     * prevent double click on button by hiding button and restoring it after 1 second
     *
     * @param view
     */
    public static void preventTwoClick(final View view) {
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            public void run() {
                view.setEnabled(true);
            }
        }, 1000);
    }
}

