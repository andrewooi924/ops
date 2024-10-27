package com.optcg.app;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

public class CollectionFragment extends Fragment {

    private RecyclerView recyclerView;
    private CollectionAdapter collectionAdapter;
    private List<Integer> menuImages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        Menu menu = toolbar.getMenu();
        resizeMenuIcon(menu.findItem(R.id.action_search), R.dimen.icon_size);
        resizeMenuIcon(menu.findItem(R.id.action_filter), R.dimen.icon_size);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.collectionRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns

        // Initialize list of image resources
        menuImages = Arrays.asList(R.drawable.op01_collection, R.drawable.op02_collection,
                R.drawable.op03_collection, R.drawable.op04_collection,
                R.drawable.op05_collection, R.drawable.op06_collection,
                R.drawable.op07_collection, R.drawable.op08_collection,
                R.drawable.op09_collection);

        // Set up adapter with only images
        collectionAdapter = new CollectionAdapter(getContext(), menuImages);
        recyclerView.setAdapter(collectionAdapter);
    }

    private void resizeMenuIcon(MenuItem menuItem, int sizeResId) {
        Drawable icon = menuItem.getIcon();
        if (icon != null) {
            int size = getResources().getDimensionPixelSize(sizeResId);
            icon.setBounds(0, 0, size, size);
            menuItem.setIcon(icon);
        }
    }

//    private boolean onMenuItemClick(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_search:
//                // Handle search action
//                return true;
//            case R.id.action_filter:
//                // Handle filter action
//                return true;
//            default:
//                return false;
//        }
//    }
}