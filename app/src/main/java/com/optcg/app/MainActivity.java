package com.optcg.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MainMenuAdapter mainMenuAdapter;
    private List<String> menuItems;
    private List<Integer> menuImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        menuItems = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            menuItems.add("OP0" + i);
        }

        menuImages = new ArrayList<>();
        menuImages.add(R.drawable.op01_box);
        menuImages.add(R.drawable.op02_box);
        menuImages.add(R.drawable.op03_box);
        menuImages.add(R.drawable.op04_box);
        menuImages.add(R.drawable.op05_box);
        menuImages.add(R.drawable.op06_box);
        menuImages.add(R.drawable.op07_box);
        menuImages.add(R.drawable.op08_box);
        menuImages.add(R.drawable.op09_box);

        mainMenuAdapter = new MainMenuAdapter(this, menuItems, menuImages);
        recyclerView.setAdapter(mainMenuAdapter);
    }}