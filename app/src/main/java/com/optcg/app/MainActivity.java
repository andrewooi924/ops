package com.optcg.app;

import android.content.SharedPreferences;
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
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnHome, btnShop, btnCollection, btnPrice, btnDeck;
    private View currentSelectedTab;
    private CardViewModel cardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        schedulePortfolioUpdate();
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
        btnDeck = findViewById(R.id.btnDeck);

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
        btnDeck.setOnClickListener(v -> {
            setTabSelected(v);
            loadFragment(new DeckFragment());
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

    private long calculateDelay() {
        Calendar current = Calendar.getInstance();
        Calendar next11AM = Calendar.getInstance();
        next11AM.set(Calendar.HOUR_OF_DAY, 11);
        next11AM.set(Calendar.MINUTE, 0);
        next11AM.set(Calendar.SECOND, 0);

        if (current.after(next11AM)) {
            next11AM.add(Calendar.DAY_OF_YEAR, 1);
        }

        return next11AM.getTimeInMillis() - current.getTimeInMillis();
    }

    private void schedulePortfolioUpdate() {
        long initialDelay = calculateDelay();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // Require network
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                PortfolioUpdateWorker.class,
                24, TimeUnit.HOURS
        ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build();


        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "PortfolioUpdateWork",
                ExistingPeriodicWorkPolicy.REPLACE, // Replace if already scheduled
                workRequest
        );
    }
}