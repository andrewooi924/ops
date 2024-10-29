package com.optcg.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainMenuAdapter mainMenuAdapter;
    private List<String> menuItems;   // Assuming you have data for menu items
    private List<Integer> menuImages; // Assuming you have data for menu images

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
        mainMenuAdapter = new MainMenuAdapter(getContext(), menuItems, menuImages);
        recyclerView.setAdapter(mainMenuAdapter);
    }
}