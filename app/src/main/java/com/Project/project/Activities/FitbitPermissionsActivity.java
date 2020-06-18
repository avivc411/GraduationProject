package com.Project.project.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Project.project.Fitbit.FitbitDataManager;
import com.Project.project.Fitbit.Utilities.FitbitSample;
import com.Project.project.R;

import java.util.Objects;

public class FitbitPermissionsActivity extends AppCompatActivity {
    /**
     * Called from Fitbit api after successful login and allowance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitbit_permissions);
        try {
            FitbitDataManager.getInstance(this).readUri(Objects.requireNonNull(getIntent().getData()));
            Toast.makeText(this,
                    "Success",
                    Toast.LENGTH_LONG)
                    .show();
        } catch (Exception e) {
            Toast.makeText(this,
                    "Operation failed. Please try again later or dismiss Fitbit feature",
                    Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
        }
    }

    public void click(View view) {
        new Thread(() -> {
            for (FitbitSample sample :
                    FitbitDataManager.getInstance(this).getData(FitbitDataManager.DataType.HeartRate))
                System.out.println(sample);
        }).start();
    }
}