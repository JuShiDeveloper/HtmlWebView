package com.mapleaf.myapplication.webview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mapleaf.myapplication.R;

public class PhotoActivity extends AppCompatActivity {

    private ImageView imageView;
    private String[] imageUrls;
    private String img;
    private String httpUrl = "https://msl-creative.s3.amazonaws.com/static/upload/2018/10/16/wallpaper_22.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        imageUrls = getIntent().getStringArrayExtra("imageUrls");
        img = getIntent().getStringExtra("curImageUrl");

        imageView = findViewById(R.id.imageView_1);

        Log.v("==yufei==","img = "+img);
        Glide.with(this).load(img).into(imageView);
    }

}
