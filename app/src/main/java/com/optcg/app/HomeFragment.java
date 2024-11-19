package com.optcg.app;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainMenuAdapter mainMenuAdapter;
    private List<String> menuItems;   // Assuming you have data for menu items
    private List<Integer> menuImages; // Assuming you have data for menu images
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private CircleIndicator3 indicator;
    private List<Integer> imageResources;
    private List<Card> cards;
    private static final String PREFS_NAME = "USER_PREFS";
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Prepare data for the adapter (menuItems and menuImages need to be initialized here or passed in)
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

        // Initialize and set the adapter
        mainMenuAdapter = new MainMenuAdapter(requireActivity().getSupportFragmentManager(), getContext(), menuItems, menuImages);
        recyclerView.setAdapter(mainMenuAdapter);

        viewPager = view.findViewById(R.id.viewPager);
        indicator = view.findViewById(R.id.indicator);

        cards = loadCardsFromJson();
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int berries = sharedPreferences.getInt("berries", 0);

        imageResources = Arrays.asList(
                R.drawable.op01_001, R.drawable.op01_002, R.drawable.op01_003, R.drawable.op01_004, R.drawable.op01_005
        );

        viewPagerAdapter = new ViewPagerAdapter(cards, imageResources);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                page.setScaleY(0.85f + (1 - Math.abs(position)) * 0.15f);
            }
        });

        indicator.setViewPager(viewPager);
    }

    private List<Card> loadCardsFromJson() {
        List<Card> cards = new ArrayList<>();
        try {
            InputStream inputStream = requireActivity().getAssets().open("cards.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Card>>() {}.getType();
            cards = gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cards;
    }
}