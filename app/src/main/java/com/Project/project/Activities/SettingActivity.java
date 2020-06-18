package com.Project.project.Activities;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.Project.project.Handlers.GetSettingsHandler;
import com.Project.project.Handlers.SetSettingsHandler;
import com.Project.project.R;
import com.Project.project.UserManagment.AWSCognitoUserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.Project.project.Utilities.PreventTwoClick.preventTwoClick;

public class SettingActivity extends AppCompatActivity {
    Switch switchCamera, switchApp, switchGPS;
    TextView textViewCamera, textViewGPS, textViewApp;
    Button applySetting;

    Map<String, String> settings = new HashMap<>();
    SetSettingsHandler settingsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        System.out.println("SettingActivity onCreate");

        settingsHandler = new SetSettingsHandler(this);
        connectButtonToXml();
        runAnimation();
        setListeners();
        readSettingsAndUpdateView();
    }

    private void setListeners() {
        switchGPS.setOnCheckedChangeListener(
                (buttonView, isChecked) -> settings.put("GPS", (isChecked ? "1" : "0")));
        switchCamera.setOnCheckedChangeListener(
                (buttonView, isChecked) -> settings.put("Camera", (isChecked ? "1" : "0")));
        switchApp.setOnCheckedChangeListener(
                (buttonView, isChecked) -> settings.put("Usage", (isChecked ? "1" : "0")));
        applySetting.setOnClickListener(view -> {
            requestAllPermissions();
            preventTwoClick(view);
        });
    }

    /**
     * Request for permissions to use app's features.
     */
    private void requestAllPermissions() {
        System.out.println("SettingActivity request All");
        List<String> permissionToAsk = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        boolean usagePermissionNecessary = false;

        for (String attribute : settings.keySet()) {
            String asPermission = attributeToPermission(attribute);
            if ("1".equals(settings.get(attribute)) &&
                    !(packageManager.checkPermission(asPermission, getPackageName()) == PackageManager.PERMISSION_GRANTED)) {
                switch (attribute) {
                    case "GPS":
                        permissionToAsk.add(attributeToPermission("GPS"));
                        permissionToAsk.add(attributeToPermission("FineGPS"));
                        break;
                    case "Usage":
                        usagePermissionNecessary = true;
                        break;
                    default:
                        permissionToAsk.add(asPermission);
                        break;
                }
            }
        }
        String[] permissionToAskArray = new String[permissionToAsk.size()];
        permissionToAsk.toArray(permissionToAskArray);

        //need to ask special permission for usage
        if (usagePermissionNecessary && !checkSpecialPermissionGranted())
            requestUsageSpecialPermission();
        else
            requestSimplePermissions(permissionToAskArray);
    }

    /**
     * Request simple permissions.
     *
     * @param permissionToAsk An array of permissions.
     */
    private void requestSimplePermissions(String[] permissionToAsk) {
        System.out.println("SettingActivity request Simple");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionToAsk.length > 0) {
            System.out.println("REQUESTING PERMISSIONS");
            requestPermissions(permissionToAsk, 101);
        } else
            saveSettings();
    }

    /**
     * Returning from requesting simple permissions.
     * In case some permission hasn't been given, ask the user to give permission or dismiss not allowed features.
     *
     * @param requestCode  101
     * @param permissions  A list of permissions.
     * @param grantResults 0 for granted, -1 for not.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("RESULT FROM REQUEST " + requestCode);
        if (permissions.length == grantResults.length)
            for (int i = 0; i < permissions.length; i++)
                //requested permission is not given
                if (!"".equals(permissions[i]) && grantResults[i] != 0) {
                    Toast.makeText(this, "Please Give Permissions Or Dismiss Features",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
        saveSettings();
    }

    /**
     * Reform the attribute to Android permissions format.
     *
     * @param attribute Attribute as saved in settings.
     * @return Corresponding permission to the attribute.
     */
    private String attributeToPermission(String attribute) {
        switch (attribute) {
            case "Camera":
//                return AppOpsManager.OPSTR_CAMERA;
                return "android.permission.CAMERA";
            case "GPS":
//                return AppOpsManager.OPSTR_COARSE_LOCATION;
                return "android.permission.ACCESS_COARSE_LOCATION";
            case "Usage":
                return AppOpsManager.OPSTR_GET_USAGE_STATS;
            //return "android.permission.PACKAGE_USAGE_STATS";
            case "FineGPS":
//                return AppOpsManager.OPSTR_FINE_LOCATION;
                return "android.permission.ACCESS_FINE_LOCATION";
            default:
                return "";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        runAnimation();
    }

    /**
     * run animation on class
     */
    private void runAnimation() {
        Animation animationright = AnimationUtils.loadAnimation(SettingActivity.this, R.anim.righttoleft);
        Animation animationleft = AnimationUtils.loadAnimation(SettingActivity.this, R.anim.lefttoright);
        Animation animationbot = AnimationUtils.loadAnimation(SettingActivity.this, R.anim.bottotop);
        switchCamera.startAnimation(animationright);
        switchApp.startAnimation(animationright);
        switchGPS.startAnimation(animationright);
        textViewCamera.startAnimation(animationleft);
        textViewGPS.startAnimation(animationleft);
        textViewApp.startAnimation(animationleft);
        applySetting.startAnimation(animationbot);
    }

    /**
     * connect xml and class
     */
    private void connectButtonToXml() {
        switchCamera = findViewById(R.id.switchcamera);
        switchApp = findViewById(R.id.switchMontorApp);
        switchGPS = findViewById(R.id.switchGPS);

        textViewCamera = findViewById(R.id.textViewCamera);
        textViewGPS = findViewById(R.id.textViewGPS);
        textViewApp = findViewById(R.id.textViewMontorApp);

        applySetting = findViewById(R.id.applySetting);
    }

    /**
     * Read user's settings, update global variable "settings" and change the switches accordingly.
     * Runs on a different thread with GetSettingsHandler.
     */
    private void readSettingsAndUpdateView() {
        new Thread(() -> {
            AWSCognitoUserManager.getInstance(this).getAttributes(new GetSettingsHandler(settings));
            for (String name : settings.keySet()) {
                switch (name) {
                    case "Camera":
                        runOnUiThread(() -> switchCamera.setChecked("1".equals(settings.get(name))));
                        break;
                    case "GPS":
                        runOnUiThread(() -> switchGPS.setChecked("1".equals(settings.get(name))));
                        break;
                    case "Usage":
                        runOnUiThread(() -> switchApp.setChecked("1".equals(settings.get(name))));
                        break;
                    default:
                        break;
                }
            }
        }).start();
    }

    public Map<String, String> getSetting() {
        Thread t = new Thread(() -> {
            AWSCognitoUserManager.getInstance(this).getAttributes(new GetSettingsHandler(settings));
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return settings;
    }

    /**
     * Save current user's settings according to global variable "settings".
     * Runs on a different thread with GetSettingsHandler.
     */
    private void saveSettings() {
        settings.put("firstLogin", "0");
        AWSCognitoUserManager.getInstance(this).setAttributesInBackground(settings, settingsHandler);
    }

    /**
     * Check if special permission Usage is granted.
     *
     * @return Usage reading allowed.
     */
    private boolean checkSpecialPermissionGranted() {
        System.out.println("SettingActivity checkSpecialPermissionGranted");
        boolean granted;
        int mode;

        mode = ((AppOpsManager) getSystemService(Context.APP_OPS_SERVICE))
                .checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        android.os.Process.myUid(), getPackageName());
        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }
        if (granted) {
            System.out.println("SettingActivity checkSpecialPermissionGranted : : GRANTED");
            return true;
        }
        System.out.println("SettingActivity checkSpecialPermissionGranted : : NOT GRANTED");
        return false;
    }

    /**
     * Ask user for special permission to read app's usage data.
     * After given permission, ask for all other needed permissions.
     * In case of user refuses to give permission, sets the switch off and wait for user to click on save setting again.
     */
    public void requestUsageSpecialPermission() {
        System.out.println("SettingActivity request Special");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Application Statistics Permission");
        builder.setMessage("Please enable access to app usage statistics");

        builder.setPositiveButton("Enable", (dialog, which) -> {
            Intent settingsIntent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            settingsIntent.setData(Uri.fromParts("package", getPackageName(), null));
            if (settingsIntent.resolveActivity(getPackageManager()) != null) {
                //startActivity(settingsIntent);
                settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(settingsIntent, 202);
            } else {
                Toast.makeText(this, "Please open settings and enable usage data access",
                        Toast.LENGTH_LONG).show();
                Intent securityIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                securityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (securityIntent.resolveActivity(getPackageManager()) != null)
                    //startActivity(securityIntent);
                    startActivityForResult(securityIntent, 202);
            }
        });

        builder.setNeutralButton("Cancel", (dialog, which) -> {
            settings.put("Usage", "0");
            runOnUiThread(() -> switchApp.setChecked(false));
            runOnUiThread(() ->
                    Toast.makeText(this, "Allow Usage Or Dismiss Feature", Toast.LENGTH_LONG).show());
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}