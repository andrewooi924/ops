package com.optcg.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnHome, btnShop, btnCollection, btnPrice, btnStats;
    private View currentSelectedTab;

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

        btnHome = findViewById(R.id.btnHome);
        btnShop = findViewById(R.id.btnShop);
        btnCollection = findViewById(R.id.btnCollection);
        btnPrice = findViewById(R.id.btnPrice);
        btnStats = findViewById(R.id.btnStats);

        // Default on opening app
        setTabSelected(btnHome);
        loadFragment(new HomeFragment());

        btnHome.setOnClickListener(v -> {
            setTabSelected(v);
            loadFragment(new HomeFragment());
        });
        btnShop.setOnClickListener(v -> {
            setTabSelected(v);
            loadFragment(new ShopFragment());
        });
        btnCollection.setOnClickListener(v -> {
            setTabSelected(v);
            loadFragment(new CollectionFragment());
        });
        btnPrice.setOnClickListener(v -> {
            setTabSelected(v);
            loadFragment(new PricesFragment());
        });
        btnStats.setOnClickListener(v -> {
            setTabSelected(v);
            loadFragment(new StatsFragment());
        });
    }

    private void setTabSelected(View selectedTab) {
        if (currentSelectedTab != null) {
            currentSelectedTab.setSelected(false);
        }

        selectedTab.setSelected(true);
        currentSelectedTab = selectedTab;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }
}