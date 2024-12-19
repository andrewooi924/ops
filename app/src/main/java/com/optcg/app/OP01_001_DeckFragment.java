package com.optcg.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class OP01_001_DeckFragment extends Fragment {

    private static final String ARG_TRANSITION_NAME = "transitionName";

    public static OP01_001_DeckFragment newInstance(String transitionName) {
        OP01_001_DeckFragment fragment = new OP01_001_DeckFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRANSITION_NAME, transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_op01_001_deck, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        ImageView imageView = view.findViewById(R.id.fullImageView);
//        if (getArguments() != null) {
//            String transitionName = getArguments().getString(ARG_TRANSITION_NAME);
//            imageView.setTransitionName(transitionName);

            // Set the image resource here based on the transition name or position
//        }
    }
}
