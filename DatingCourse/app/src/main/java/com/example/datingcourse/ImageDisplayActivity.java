package com.example.datingcourse;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class ImageDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        ImageView imageView = findViewById(R.id.displayImageView);

        // URL을 Intent에서 가져옵니다.
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");

        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
    }
}
