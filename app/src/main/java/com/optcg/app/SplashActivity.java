package com.optcg.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Set your splash screen layout

        new Handler().postDelayed(() -> {
            // Start your main activity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close splash activity
        }, 1500); // Show splash for 3 seconds
    }
}