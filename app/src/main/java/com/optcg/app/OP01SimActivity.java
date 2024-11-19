package com.optcg.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class OP01SimActivity extends AppCompatActivity {

    private FrameLayout cardContainer;
    private FrameLayout resultContainer;
    private Button resetButton;
    private Button backButton;
    private TextView tvPacksOpened;
    private long lastClickTime = 0;
    private boolean pity;
    private static final String PREFS_NAME = "COLLECTION_PREFS";
    private static final String KEY_PACKS_OPENED = "op01_packs_opened";
    private static final String TOTAL_COUNT = "op01_total_count";
    private static final String TOTAL_C = "op01_total_c";
    private static final String TOTAL_UC = "op01_total_uc";
    private static final String TOTAL_R = "op01_total_r";
    private static final String TOTAL_SR = "op01_total_sr";
    private static final String TOTAL_L = "op01_total_l";
    private static final String TOTAL_SEC = "op01_total_sec";
    private SharedPreferences sharedPreferences;
    private SharedPreferences userPreferences;
    private List<Integer> pulledCards = new ArrayList<>();
    private List<Boolean> isNew = new ArrayList<>();
    private int[] cCards = {
            R.drawable.op01_007, R.drawable.op01_008, R.drawable.op01_009, R.drawable.op01_010,
            R.drawable.op01_012, R.drawable.op01_018, R.drawable.op01_019, R.drawable.op01_020,
            R.drawable.op01_023, R.drawable.op01_027, R.drawable.op01_028,
            R.drawable.op01_034, R.drawable.op01_036, R.drawable.op01_037, R.drawable.op01_038,
            R.drawable.op01_043, R.drawable.op01_044, R.drawable.op01_045, R.drawable.op01_048, R.drawable.op01_050,
            R.drawable.op01_053, R.drawable.op01_055, R.drawable.op01_059,
            R.drawable.op01_064, R.drawable.op01_065, R.drawable.op01_066,
            R.drawable.op01_072, R.drawable.op01_075, R.drawable.op01_076, R.drawable.op01_080,
            R.drawable.op01_081, R.drawable.op01_082, R.drawable.op01_087, R.drawable.op01_089,
            R.drawable.op01_092, R.drawable.op01_099, R.drawable.op01_100,
            R.drawable.op01_103, R.drawable.op01_104, R.drawable.op01_105, R.drawable.op01_107, R.drawable.op01_110,
            R.drawable.op01_113, R.drawable.op01_115, R.drawable.op01_117
    };
    private int[] ucCards = {
            R.drawable.op01_006, R.drawable.op01_011, R.drawable.op01_014, R.drawable.op01_015,
            R.drawable.op01_021, R.drawable.op01_022, R.drawable.op01_029, R.drawable.op01_030,
            R.drawable.op01_032, R.drawable.op01_033, R.drawable.op01_039, R.drawable.op01_042,
            R.drawable.op01_052, R.drawable.op01_056, R.drawable.op01_057, R.drawable.op01_063,
            R.drawable.op01_077, R.drawable.op01_083, R.drawable.op01_084, R.drawable.op01_085,
            R.drawable.op01_088, R.drawable.op01_090, R.drawable.op01_095, R.drawable.op01_098,
            R.drawable.op01_101, R.drawable.op01_106, R.drawable.op01_108, R.drawable.op01_109,
            R.drawable.op01_116, R.drawable.op01_118,
    };
    private int[] rCards = {
            R.drawable.op01_004, R.drawable.op01_005, R.drawable.op01_013, R.drawable.op01_016,
            R.drawable.op01_017, R.drawable.op01_026, R.drawable.op01_035, R.drawable.op01_040,
            R.drawable.op01_041, R.drawable.op01_046, R.drawable.op01_049, R.drawable.op01_054,
            R.drawable.op01_058, R.drawable.op01_068, R.drawable.op01_069, R.drawable.op01_071,
            R.drawable.op01_073, R.drawable.op01_074, R.drawable.op01_079, R.drawable.op01_086,
            R.drawable.op01_093, R.drawable.op01_097, R.drawable.op01_102, R.drawable.op01_111,
            R.drawable.op01_112, R.drawable.op01_114,R.drawable.op01_119,
    };
    private int[] srCards = {
            R.drawable.op01_024, R.drawable.op01_025, R.drawable.op01_047, R.drawable.op01_051,
            R.drawable.op01_067, R.drawable.op01_070, R.drawable.op01_078, R.drawable.op01_094,
            R.drawable.op01_096,
    };

    private int[] aarCards = {
        R.drawable.op01_013_p1, R.drawable.op01_016_p1, R.drawable.op01_073_p1, R.drawable.op01_093_p1,
        R.drawable.op01_097_p1, R.drawable.op01_102_p1
    };

    private int[] aasrCards = {
        R.drawable.op01_040_p1, R.drawable.op01_051_p1, R.drawable.op01_024_p1, R.drawable.op01_025_p1,
        R.drawable.op01_047_p1, R.drawable.op01_067_p1, R.drawable.op01_070_p1, R.drawable.op01_078_p1,
        R.drawable.op01_094_p1, R.drawable.op01_096_p1
    };

    private int[] aasecCards = {
        R.drawable.op01_120_p1, R.drawable.op01_121_p1
    };

    private int[] aalCards = {
            R.drawable.op01_001_p1, R.drawable.op01_002_p1, R.drawable.op01_003_p1, R.drawable.op01_031_p1,
            R.drawable.op01_060_p1, R.drawable.op01_061_p1, R.drawable.op01_062_p1, R.drawable.op01_091_p1,

    };
    private int[] secCards = {R.drawable.op01_120, R.drawable.op01_121};
    private int[] mrCards = {R.drawable.op01_120_p2};
    private int[] lCards = {R.drawable.op01_001, R.drawable.op01_002, R.drawable.op01_003, R.drawable.op01_031, R.drawable.op01_060, R.drawable.op01_061, R.drawable.op01_062, R.drawable.op01_091};
    private int[] otherCards = {R.drawable.op01_008_p1, R.drawable.op01_034_p1, R.drawable.op01_048_p1, R.drawable.op01_064_p1, R.drawable.op01_077_p1, R.drawable.op01_109_p1};
    private int[] cardResources;
    private CardViewModel cardViewModel;
    private String rarity;
    private boolean isAA = false;
    private boolean isManga = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_op01_sim);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButton.clearAnimation(); // Animation needs to be cleared so that button is not permanently visible for animation
                resetButton.setVisibility(View.GONE);
                backButton.clearAnimation();
                backButton.setVisibility(View.GONE);
                resultContainer.removeAllViews();
                resultContainer.setVisibility(View.GONE);
                pulledCards.clear();
                isNew.clear();
                handleCardStack();
            }
        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cardContainer = findViewById(R.id.cardContainer);
        resultContainer = findViewById(R.id.resultContainer);
        pity = false;

        // Handle shared preferences
        userPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int packsOpened = sharedPreferences.getInt(KEY_PACKS_OPENED, 1);

        handleCardStack();

        tvPacksOpened = findViewById(R.id.tvPacksOpened);
        tvPacksOpened.setText(String.valueOf(packsOpened));

        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);
    }

    private void handleCardStack() {

        // Reset flags
        isManga = false;
        isAA = false;

        // Handle pity system
        handlePitySystem();

        if (!pity) {
            // Handling last card
            if (Math.random() < 0.50) {
                cardResources = rCards;
                rarity = "R";
            } else {
                double prob = Math.random();
                if (prob < (1.0 / 1728)) {
                    cardResources = mrCards;
                    rarity = "SEC";
                    isManga = true;
                } else if (prob < ((1.0 / 1728) + (1.0 / 144))) {
                    cardResources = secCards;
                    rarity = "SEC";
                } else if (prob < ((1.0 / 1728) + (1.0 / 144) + (1.0 / 72))) {
                    cardResources = aalCards;
                    rarity = "L";
                    isAA = true;
                } else if (prob < ((1.0 / 1728) + (1.0 / 144) + (1.0 / 72) + (2.0 / 48))) { // 1.0 / 48 -> 2.0 / 48
                    if (Math.random() < 0.33) {
                        cardResources = aasecCards;
                        rarity = "SEC";
                    }
                    else if (Math.random() < 0.66) {
                        cardResources = aasrCards;
                        rarity = "SR";
                    }
                    else {
                        cardResources = aarCards;
                        rarity = "R";
                    }
                    isAA = true;
                } else {
                    if (Math.random() < 0.75) {
                        cardResources = srCards;
                        rarity = "SR";
                    }
                    else {
                        cardResources = lCards;
                        rarity = "L";
                    }
                }
            }
        }

        pity = false;

        ImageView card6 = new ImageView(this);
        int randomIndex = (int) (Math.random() * cardResources.length);
        String cardId6 = getResources().getResourceEntryName(cardResources[randomIndex]);
        String rarity6 = rarity;
        card6.setImageResource(cardResources[randomIndex]);
        pulledCards.add(cardResources[randomIndex]);
        if (!sharedPreferences.getBoolean(cardId6 + "_isCollected", false)) {
            isNew.add(true);
        }
        else {
            isNew.add(false);
            SharedPreferences.Editor editor = userPreferences.edit();
            switch (rarity6) {
                case "R":
                    if (isAA) {
                        editor.putInt("berries", userPreferences.getInt("berries", 0) + 1000);
                    }
                    else {
                        editor.putInt("berries", userPreferences.getInt("berries", 0) + 300);
                    }
                    break;
                case "SR":
                    if (isAA) {
                        editor.putInt("berries", userPreferences.getInt("berries", 0) + 3000);
                    }
                    else {
                        editor.putInt("berries", userPreferences.getInt("berries", 0) + 500);
                    }
                    break;
                case "L":
                    if (isAA) {
                        editor.putInt("berries", userPreferences.getInt("berries", 0) + 10000);
                    }
                    else {
                        editor.putInt("berries", userPreferences.getInt("berries", 0) + 300);
                    }
                    break;
                case "SEC":
                    if (isManga) {
                        editor.putInt("berries", userPreferences.getInt("berries", 0) + 50000);
                    }
                    else if (isAA) {
                        editor.putInt("berries", userPreferences.getInt("berries", 0) + 10000);
                    }
                    else {
                        editor.putInt("berries", userPreferences.getInt("berries", 0) + 5000);
                    }
                    break;
            }
            editor.apply();
        }
        card6.setLayoutParams(new FrameLayout.LayoutParams(890, 2700));
        card6.setTranslationX(230);
        cardContainer.addView(card6);
        card6.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 animate(card6);

                 // Set delay to prevent background touches from interfering with animation (reset button)
                 if (System.currentTimeMillis() - lastClickTime < 1000) {
                     return;
                 }
                 lastClickTime = System.currentTimeMillis();
                 showButtons();

                 // Update packs opened
                 int packsOpened = sharedPreferences.getInt(KEY_PACKS_OPENED, 1) + 1;
                 int newCount = sharedPreferences.getInt(cardId6 + "_count", 0) + 1;
                 SharedPreferences.Editor editor = sharedPreferences.edit();
                 if (!sharedPreferences.getBoolean(cardId6 + "_isCollected", false)) {
                     editor.putInt(TOTAL_COUNT, sharedPreferences.getInt(TOTAL_COUNT, 0) + 1);
                     if (rarity6.equals("R")) {
                         editor.putInt(TOTAL_R, sharedPreferences.getInt(TOTAL_R, 0) + 1);
                     }
                     else if (rarity6.equals("SR")) {
                         editor.putInt(TOTAL_SR, sharedPreferences.getInt(TOTAL_SR, 0) + 1);
                     }
                     else if (rarity6.equals("L")) {
                         editor.putInt(TOTAL_L, sharedPreferences.getInt(TOTAL_L, 0) + 1);
                     }
                    else if (rarity6.equals("SEC")) {
                         editor.putInt(TOTAL_SEC, sharedPreferences.getInt(TOTAL_SEC, 0) + 1);
                    }
                 }
                 editor.putInt(KEY_PACKS_OPENED, packsOpened);
                 editor.putInt(cardId6 + "_count", newCount);
                 editor.putBoolean(cardId6 + "_isCollected", true);
                 editor.apply();

                 tvPacksOpened.setText(String.valueOf(packsOpened));
             }
         });


        ImageView card5 = new ImageView(this);
        cardResources = rCards;
        rarity = "R";
        randomIndex = (int) (Math.random() * cardResources.length);
        String cardId5 = getResources().getResourceEntryName(cardResources[randomIndex]);
        card5.setImageResource(cardResources[randomIndex]);
        pulledCards.add(cardResources[randomIndex]);
        if (!sharedPreferences.getBoolean(cardId5 + "_isCollected", false)) {
            isNew.add(true);
        }
        else {
            isNew.add(false);
            SharedPreferences.Editor editor = userPreferences.edit();
            editor.putInt("berries", userPreferences.getInt("berries", 0) + 300);
            editor.apply();
        }
        card5.setLayoutParams(new FrameLayout.LayoutParams(890, 2700));
        card5.setTranslationX(230);
        cardContainer.addView(card5);
        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animate(card5);
                int newCount = sharedPreferences.getInt(cardId5 + "_count", 0) + 1;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!sharedPreferences.getBoolean(cardId5 + "_isCollected", false)) {
                    editor.putInt(TOTAL_COUNT, sharedPreferences.getInt(TOTAL_COUNT, 0) + 1);
                    editor.putInt(TOTAL_R, sharedPreferences.getInt(TOTAL_R, 0) + 1);
                }
                editor.putInt(cardId5 + "_count", newCount);
                editor.putBoolean(cardId5 + "_isCollected", true);
                editor.apply();
            }
        });

        // Rest of the cards
        ImageView card4 = new ImageView(this);
        if (Math.random() < 0.33) {
            cardResources = ucCards;
            rarity = "UC";
        }
        else {
            cardResources = cCards;
            rarity = "C";
        }
        randomIndex = (int) (Math.random() * cardResources.length);
        String cardId4 = getResources().getResourceEntryName(cardResources[randomIndex]);
        card4.setImageResource(cardResources[randomIndex]);
        pulledCards.add(cardResources[randomIndex]);
        if (!sharedPreferences.getBoolean(cardId4 + "_isCollected", false)) {
            isNew.add(true);
        }
        else {
            isNew.add(false);
            SharedPreferences.Editor editor = userPreferences.edit();
            switch (rarity) {
                case "C":
                    editor.putInt("berries", userPreferences.getInt("berries", 0) + 100);
                    break;
                case "UC":
                    editor.putInt("berries", userPreferences.getInt("berries", 0) + 200);
                    break;
            }
            editor.apply();
        }
        card4.setLayoutParams(new FrameLayout.LayoutParams(890, 2700));
        card4.setTranslationX(230);
        cardContainer.addView(card4);
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animate(card4);
                int newCount = sharedPreferences.getInt(cardId4 + "_count", 0) + 1;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!sharedPreferences.getBoolean(cardId4 + "_isCollected", false)) {
                    editor.putInt(TOTAL_COUNT, sharedPreferences.getInt(TOTAL_COUNT, 0) + 1);
                    if (rarity.equals("C")) {
                        editor.putInt(TOTAL_C, sharedPreferences.getInt(TOTAL_C, 0) + 1);
                    }
                    else if (rarity.equals("UC")) {
                        editor.putInt(TOTAL_UC, sharedPreferences.getInt(TOTAL_UC, 0) + 1);
                    }
                }
                editor.putInt(cardId4 + "_count", newCount);
                editor.putBoolean(cardId4 + "_isCollected", true);
                editor.apply();
            }
        });

        ImageView card3 = new ImageView(this);
        if (Math.random() < 0.33) {
            cardResources = ucCards;
            rarity = "UC";
        }
        else {
            cardResources = cCards;
            rarity = "C";
        }
        randomIndex = (int) (Math.random() * cardResources.length);
        String cardId3 = getResources().getResourceEntryName(cardResources[randomIndex]);
        card3.setImageResource(cardResources[randomIndex]);
        pulledCards.add(cardResources[randomIndex]);
        if (!sharedPreferences.getBoolean(cardId3 + "_isCollected", false)) {
            isNew.add(true);
        }
        else {
            isNew.add(false);
            SharedPreferences.Editor editor = userPreferences.edit();
            switch (rarity) {
                case "C":
                    editor.putInt("berries", userPreferences.getInt("berries", 0) + 100);
                    break;
                case "UC":
                    editor.putInt("berries", userPreferences.getInt("berries", 0) + 200);
                    break;
            }
            editor.apply();
        }
        card3.setLayoutParams(new FrameLayout.LayoutParams(890, 2700));
        card3.setTranslationX(230);
        cardContainer.addView(card3);
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animate(card3);
                int newCount = sharedPreferences.getInt(cardId3 + "_count", 0) + 1;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!sharedPreferences.getBoolean(cardId3 + "_isCollected", false)) {
                    editor.putInt(TOTAL_COUNT, sharedPreferences.getInt(TOTAL_COUNT, 0) + 1);
                    if (rarity.equals("C")) {
                        editor.putInt(TOTAL_C, sharedPreferences.getInt(TOTAL_C, 0) + 1);
                    }
                    else if (rarity.equals("UC")) {
                        editor.putInt(TOTAL_UC, sharedPreferences.getInt(TOTAL_UC, 0) + 1);
                    }
                }
                editor.putInt(cardId3 + "_count", newCount);
                editor.putBoolean(cardId3 + "_isCollected", true);
                editor.apply();
            }
        });

        ImageView card2 = new ImageView(this);
        if (Math.random() < 0.33) {
            cardResources = ucCards;
            rarity = "UC";
        }
        else {
            cardResources = cCards;
            rarity = "C";
        }
        randomIndex = (int) (Math.random() * cardResources.length);
        String cardId2 = getResources().getResourceEntryName(cardResources[randomIndex]);
        card2.setImageResource(cardResources[randomIndex]);
        pulledCards.add(cardResources[randomIndex]);
        if (!sharedPreferences.getBoolean(cardId2 + "_isCollected", false)) {
            isNew.add(true);
        }
        else {
            isNew.add(false);
            SharedPreferences.Editor editor = userPreferences.edit();
            switch (rarity) {
                case "C":
                    editor.putInt("berries", userPreferences.getInt("berries", 0) + 100);
                    break;
                case "UC":
                    editor.putInt("berries", userPreferences.getInt("berries", 0) + 200);
                    break;
            }
            editor.apply();
        }
        card2.setLayoutParams(new FrameLayout.LayoutParams(890, 2700));
        card2.setTranslationX(230);
        cardContainer.addView(card2);
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animate(card2);
                int newCount = sharedPreferences.getInt(cardId2 + "_count", 0) + 1;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!sharedPreferences.getBoolean(cardId2 + "_isCollected", false)) {
                    editor.putInt(TOTAL_COUNT, sharedPreferences.getInt(TOTAL_COUNT, 0) + 1);
                    if (rarity.equals("C")) {
                        editor.putInt(TOTAL_C, sharedPreferences.getInt(TOTAL_C, 0) + 1);
                    }
                    else if (rarity.equals("UC")) {
                        editor.putInt(TOTAL_UC, sharedPreferences.getInt(TOTAL_UC, 0) + 1);
                    }
                }
                editor.putInt(cardId2 + "_count", newCount);
                editor.putBoolean(cardId2 + "_isCollected", true);
                editor.apply();
            }
        });

        ImageView card1 = new ImageView(this);
        if (Math.random() < 0.33) {
            cardResources = ucCards;
            rarity = "UC";
        }
        else {
            cardResources = cCards;
            rarity = "C";
        }
        randomIndex = (int) (Math.random() * cardResources.length);
        String cardId1 = getResources().getResourceEntryName(cardResources[randomIndex]);
        card1.setImageResource(cardResources[randomIndex]);
        pulledCards.add(cardResources[randomIndex]);
        if (!sharedPreferences.getBoolean(cardId1 + "_isCollected", false)) {
            isNew.add(true);
        }
        else {
            isNew.add(false);
            SharedPreferences.Editor editor = userPreferences.edit();
            switch (rarity) {
                case "C":
                    editor.putInt("berries", userPreferences.getInt("berries", 0) + 100);
                    break;
                case "UC":
                    editor.putInt("berries", userPreferences.getInt("berries", 0) + 200);
                    break;
            }
            editor.apply();
        }
        card1.setLayoutParams(new FrameLayout.LayoutParams(890, 2700));
        card1.setTranslationX(230);
        cardContainer.addView(card1);
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animate(card1);
                int newCount = sharedPreferences.getInt(cardId1 + "_count", 0) + 1;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!sharedPreferences.getBoolean(cardId1 + "_isCollected", false)) {
                    editor.putInt(TOTAL_COUNT, sharedPreferences.getInt(TOTAL_COUNT, 0) + 1);
                    if (rarity.equals("C")) {
                        editor.putInt(TOTAL_C, sharedPreferences.getInt(TOTAL_C, 0) + 1);
                    }
                    else if (rarity.equals("UC")) {
                        editor.putInt(TOTAL_UC, sharedPreferences.getInt(TOTAL_UC, 0) + 1);
                    }
                }
                editor.putInt(cardId1 + "_count", newCount);
                editor.putBoolean(cardId1 + "_isCollected", true);
                editor.apply();
            }
        });

        // Card pack
        ImageView pack = new ImageView(this);
        pack.setImageResource(R.drawable.op01_pack);
        pack.setLayoutParams(new FrameLayout.LayoutParams(1350, 2700));
        pack.setScaleX(1.35f);
        pack.setScaleY(1.35f);
        cardContainer.addView(pack);
        pack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animate(pack);
            }
        });
    }

    private void handlePitySystem() {
        int packsOpened = sharedPreferences.getInt(KEY_PACKS_OPENED, 1);
        if (packsOpened > 0) {
            if ((packsOpened + 1) % 1728 == 0) {
                cardResources = mrCards;
                rarity = "SEC";
                isManga = true;
                pity = true;
            }
            else if ((packsOpened + 1) % 288 == 0) {
                cardResources = aalCards;
                rarity = "L";
                isAA = true;
                pity = true;
            }
            else if ((packsOpened + 1) % 24 == 0) {
                if (Math.random() < 0.99) {
                    if (Math.random() < 0.33) {
                        cardResources = secCards;
                        rarity = "SEC";
                    }
                    else if (Math.random() < 0.66) {
                        cardResources = aasrCards;
                        isAA = true;
                        rarity = "SR";
                    }
                    else {
                        cardResources = aarCards;
                        isAA = true;
                        rarity = "R";
                    }
                    pity = true;
                }
                else {
                    cardResources = aasecCards;
                    isAA = true;
                    rarity = "SEC";
                    pity = true;
                }
            }
        }
    }

    private void animate(ImageView card) {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        ObjectAnimator translationX = ObjectAnimator.ofFloat(card, "translationX", -screenWidth*3);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(card, "translationY", screenHeight/2);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(card, "rotation", 0f, -90f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationX, translationY, rotation);
        animatorSet.setDuration(1000);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cardContainer.removeView(card);
            }
        });

        animatorSet.start();
    }

    private void showButtons() {
        resetButton.setVisibility(View.VISIBLE);
        TranslateAnimation slideResetUp = new TranslateAnimation(0, 0, resetButton.getHeight() + getResources().getDisplayMetrics().heightPixels, 0);
        slideResetUp.setInterpolator(new AccelerateDecelerateInterpolator());
        slideResetUp.setDuration(500);
        slideResetUp.setFillAfter(true);
        resetButton.startAnimation(slideResetUp);

        backButton.setVisibility(View.VISIBLE);
        TranslateAnimation slideBackUp = new TranslateAnimation(0, 0, backButton.getHeight() + getResources().getDisplayMetrics().heightPixels, 0);
        slideBackUp.setInterpolator(new AccelerateDecelerateInterpolator());
        slideBackUp.setDuration(500);
        slideBackUp.setFillAfter(true);
        backButton.startAnimation(slideBackUp);

        showResults();
    }

    @Override
    public void onBackPressed() {
    }

    private void showResults() {
        GridLayout gridLayout = new GridLayout(this);
        FrameLayout.LayoutParams gridParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, // Full width
                ViewGroup.LayoutParams.WRAP_CONTENT  // Adjust height dynamically
        );

// Add padding to the grid layout (for left and right spacing)
        gridParams.setMargins(32, 650, 32, 16); // Left, Top, Right, Bottom margins
        gridLayout.setLayoutParams(gridParams);
        gridLayout.setColumnCount(3); // 3 columns for a 2x3 layout
        gridLayout.setRowCount(2);    // 2 rows
        gridLayout.setPadding(16, 16, 16, 16); // Internal padding

// Add the cards in reverse order
        for (int i = pulledCards.size() - 1; i >= 0; i--) {
            int cardResourceId = pulledCards.get(i);
            boolean isNewCard = isNew.get(i);

            // Create a FrameLayout to hold both the card and the symbol
            FrameLayout cardFrame = new FrameLayout(this);

            // Create ImageView for the card
            ImageView cardView = new ImageView(this);
            cardView.setImageResource(cardResourceId);

            // Set proper size for the card (adjust size as necessary)
            GridLayout.LayoutParams cardParams = new GridLayout.LayoutParams();
            cardParams.width = 400; // Use 0 width and weight to fill the cell equally
            cardParams.height = 600; // Fixed height as before
            cardParams.setMargins(8, 0, 8, 0); // Margins around the card
            cardParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Fill column equally
            cardParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED); // Fill row equally
            cardView.setLayoutParams(cardParams);
            cardView.setScaleType(ImageView.ScaleType.FIT_CENTER); // Scale the image to fit inside the bounds
            cardView.setPadding(2, 0, 2, 0); // Padding for the card

            // Create ImageView for the symbol (circle or any other symbol)
            ImageView symbolView = new ImageView(this);
            symbolView.setImageResource(R.drawable.ic_new); // Replace with your symbol resource
            FrameLayout.LayoutParams symbolParams = new FrameLayout.LayoutParams(
                    80, 80 // Size of the symbol (adjust as needed)
            );
            symbolParams.leftMargin = -3; // Distance from the right edge
            symbolParams.topMargin = 0;   // Distance from the top edge
            symbolView.setLayoutParams(symbolParams);

            // Add both the card and the symbol to the FrameLayout
            cardFrame.addView(cardView);

            if (isNewCard) {
                cardFrame.addView(symbolView);
            }

            // Add the cardFrame (which contains both card and symbol) to the GridLayout
            gridLayout.addView(cardFrame);
        }

        // Add the grid layout to the container
        resultContainer.removeAllViews(); // Clear previous grids if any
        resultContainer.addView(gridLayout);
        resultContainer.setVisibility(View.VISIBLE);

        // Optional: Add an animation for the grid appearance
        TranslateAnimation slideUp = new TranslateAnimation(0, 0, resultContainer.getHeight(), 0);
        slideUp.setInterpolator(new AccelerateDecelerateInterpolator());
        slideUp.setDuration(500);
        slideUp.setFillAfter(true);
        resultContainer.startAnimation(slideUp);
    }
}