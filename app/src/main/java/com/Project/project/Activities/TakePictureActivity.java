package com.Project.project.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Project.project.DB.DataUploader;
import com.Project.project.R;
import com.Project.project.RekognitionManagment.RekognitionImageUploader;

import java.util.Map;

import static com.Project.project.Utilities.PreventTwoClick.preventTwoClick;

public class TakePictureActivity extends AppCompatActivity {
    private static final int Image_Capture_Code = 1;
    Button takePhoto, uploadPhoto, skipButton;
    private ImageView imgCaptured;
    private String rowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        connectButtonToXml();
        runAnimation();

        uploadPhoto.setOnClickListener(view -> {
            preventTwoClick(view);
            upload(view);
        });
    }


    /**
     * Set global params according to captured picture.
     *
     * @param requestCode Request's code.
     * @param resultCode  Result's code.
     * @param data        Picture taking intent - get the picture from.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Intent intent = getIntent();
                rowId = intent.getStringExtra("rowId");
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                imgCaptured.setImageBitmap(bp);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Call system intent that enable taking a picture.
     *
     * @param view Current view.
     */
    public void capture(View view) {
        preventTwoClick(view);
        Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cInt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(cInt, Image_Capture_Code);
    }

    public void skipButton(View view) {
        Map<String, String> setting = new SettingActivity().getSetting();
        if ("1".equals(setting.get("GPS")))
            startGpsActivity();
        else
            finish();
    }

    public void uploadPhoto() {
        Bitmap bitmap = ((BitmapDrawable) imgCaptured.getDrawable()).getBitmap();
        RekognitionImageUploader rekognitionImageUploader = new RekognitionImageUploader();
        Map<String, Float> features = rekognitionImageUploader.analyseImage(this, bitmap);
        DataUploader dataUploader = DataUploader.getInstance(this);
        String imageID = dataUploader.uploadImage(bitmap, rowId);
        if (dataUploader.uploadImageFeatures(features, imageID)) {
            runOnUiThread(() -> {
                uploadPhoto.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Image Upload Succeed!", Toast.LENGTH_SHORT).show();
                Map<String, String> setting = new SettingActivity().getSetting();
                if ("1".equals(setting.get("GPS")))
                    startGpsActivity();
                else
                    finish();
            });
        } else {
            runOnUiThread(() -> {
                uploadPhoto.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
            });
        }
    }

    /**
     * Upload a picture to the db
     *
     * @param view current view
     */
    public void upload(View view) {
        if (imgCaptured.getDrawable() == null) {
            Toast.makeText(TakePictureActivity.this, "No picture to upload", Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(() -> {
                Toast.makeText(this, "Uploading Image", Toast.LENGTH_SHORT).show();
                uploadPhoto.setVisibility(View.INVISIBLE);
                new Thread(this::uploadPhoto).start();
            });
        }
    }

    private void startGpsActivity() {
        finish();
        Intent intent = getIntent();
        rowId = intent.getStringExtra("rowId");
        intent = new Intent(this, GPSActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("rowId", rowId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runAnimation();
    }


    /**
     * run animation of page
     */
    private void runAnimation() {
        Animation animationbot = AnimationUtils.loadAnimation(TakePictureActivity.this, R.anim.bottotop);
        Animation animationfade = AnimationUtils.loadAnimation(TakePictureActivity.this, R.anim.fadeinslow);
        takePhoto.startAnimation(animationbot);
        uploadPhoto.startAnimation(animationbot);
        skipButton.startAnimation(animationbot);
        imgCaptured.startAnimation(animationfade);
    }

    /**
     * connect xml and class
     */
    private void connectButtonToXml() {
        takePhoto = findViewById(R.id.btn_Take_Picture);
        uploadPhoto = findViewById(R.id.btn_upload_photo);
        imgCaptured = findViewById(R.id.capturedImage);
        skipButton = findViewById(R.id.skipButton);
    }
}