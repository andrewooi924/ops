package com.optcg.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeckFragment extends Fragment implements HorizontalDeckAdapter.OnImageClickListener {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deck, container, false);

        RecyclerView verticalRecyclerView = view.findViewById(R.id.verticalRecyclerView);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Create a list of rows with titles and drawables
        List<ColorData> rows = new ArrayList<>();

        rows.add(new ColorData("赤", Arrays.asList(R.drawable.op01_001_p1)));

        rows.add(new ColorData("緑", Arrays.asList(R.drawable.op01_031_p1)));

        rows.add(new ColorData("青", Arrays.asList(R.drawable.op01_060_p1)));

        rows.add(new ColorData("紫", Arrays.asList(R.drawable.op01_091_p1)));

        rows.add(new ColorData("黒", Arrays.asList(R.drawable.op02_093_p1)));

        rows.add(new ColorData("黄", Arrays.asList(R.drawable.op05_098_p1)));

        rows.add(new ColorData("赤•緑", Arrays.asList(R.drawable.op01_002_p1)));

        // Pass the data to the adapter
        VerticalDeckAdapter verticalAdapter = new VerticalDeckAdapter(requireContext(), rows, this);
        verticalRecyclerView.setAdapter(verticalAdapter);

        return view;
    }

    @Override
    public void onImageClick(int position, String color, ImageView sharedImageView) {
        Fragment fragment;
        if ("赤".equals(color)) {
            switch (position) {
                case 0:
                    fragment = OP01_001_DeckFragment.newInstance(sharedImageView.getTransitionName());
                    break;
                default:
                    fragment = new OP01_001_DeckFragment();
            }
        }
        else {
            switch (position) {
                case 0:
                    fragment = OP01_002_DeckFragment.newInstance(sharedImageView.getTransitionName());
                    break;
                default:
                    fragment = new OP01_002_DeckFragment();
            }
        }
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.addSharedElement(sharedImageView, sharedImageView.getTransitionName());
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }
}