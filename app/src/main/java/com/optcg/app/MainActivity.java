package com.optcg.app;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnHome, btnShop, btnCollection, btnPrice, btnStats;
    private View currentSelectedTab;
    private CardViewModel cardViewModel;

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

        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.change_image_transform));
        getWindow().setSharedElementExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.change_image_transform));

        btnHome = findViewById(R.id.btnHome);
        btnShop = findViewById(R.id.btnShop);
        btnCollection = findViewById(R.id.btnCollection);
        btnPrice = findViewById(R.id.btnPrice);
        btnStats = findViewById(R.id.btnStats);

        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);

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

        // Display size of shared preferences
        File prefsFile = new File(getApplicationInfo().dataDir + "/shared_prefs/OP01_PREFS.xml");
        Log.d("LENGTH", "" + prefsFile.length());
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

//    public CardRepository getCardRepository() {
//        return cardRepository;
//    }
}