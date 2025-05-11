package com.example.test;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.CollapsingToolbarLayout;

public class FishActivity extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbarLayout;
    ScrollView scrollView;
    ImageView scroll_image;
    TextView scroll_protection;
    TextView scroll_size;
    TextView scroll_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish);
        this.collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        this.scroll_protection = (TextView) findViewById(R.id.scroll_protection);
        this.scroll_size = (TextView) findViewById(R.id.scroll_size);
        this.scroll_image = (ImageView) findViewById(R.id.scroll_image);
        this.collapsingToolbarLayout.setTitle(getIntent().getStringExtra("Title"));
        this.scroll_protection.setText(getIntent().getStringExtra("Protection"));
        this.scroll_size.setText(getIntent().getStringExtra("Size"));
        this.scroll_image.setImageResource(getIntent().getIntExtra("Image", 0));
    }
}