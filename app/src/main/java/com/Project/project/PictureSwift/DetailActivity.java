package com.Project.project.PictureSwift;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Project.project.R;

/**
 * when clicking image from report page, what to show in new activity
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // image and text on new page - passed by adapter intent
        TextView textView = findViewById(R.id.textView);
        ImageView imageViewC = findViewById(R.id.imageViewC);
        textView.setText(getIntent().getStringExtra("param"));

        // transfer image as byte array and convert back to Bitmap
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        imageViewC.setImageBitmap(bmp);
    }
}
