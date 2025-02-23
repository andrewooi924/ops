package com.optcg.app;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

public class CollectionFragment extends Fragment implements CollectionAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private CollectionAdapter collectionAdapter;
    private List<Integer> menuImages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.collectionRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns

        // Initialize list of image resources
        menuImages = Arrays.asList(
                R.drawable.op01_collection, R.drawable.op02_collection,
                R.drawable.op03_collection, R.drawable.op04_collection,
                R.drawable.op05_collection, R.drawable.op06_collection,
                R.drawable.op07_collection, R.drawable.op08_collection,
                R.drawable.op09_collection, R.drawable.op10_collection,
                R.drawable.eb01_collection, R.drawable.eb02_collection
                );

        // Set up adapter with only images
        collectionAdapter = new CollectionAdapter(getContext(), menuImages, this);
        recyclerView.setAdapter(collectionAdapter);
    }

    public void onItemClick(int position, ImageView sharedImageView) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = OP01CollectionFragment.newInstance(sharedImageView.getTransitionName());
                break;
            case 1:
                fragment = OP02CollectionFragment.newInstance(sharedImageView.getTransitionName());
                break;
            case 2:
                fragment = OP03CollectionFragment.newInstance(sharedImageView.getTransitionName());
                break;
            case 3:
                fragment = OP04CollectionFragment.newInstance(sharedImageView.getTransitionName());
                break;
            case 4:
                fragment = OP05CollectionFragment.newInstance(sharedImageView.getTransitionName());
                break;
            case 5:
                fragment = OP06CollectionFragment.newInstance(sharedImageView.getTransitionName());
                break;
            case 6:
                fragment = OP07CollectionFragment.newInstance(sharedImageView.getTransitionName());
                break;
            case 7:
                fragment = OP08CollectionFragment.newInstance(sharedImageView.getTransitionName());
                break;
            case 8:
                fragment = OP09CollectionFragment.newInstance(sharedImageView.getTransitionName());
                break;
            case 9:
                fragment = OP10CollectionFragment.newInstance(sharedImageView.getTransitionName());
                break;
            default:
                fragment = new OP01CollectionFragment();
        }
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.addSharedElement(sharedImageView, sharedImageView.getTransitionName());
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}