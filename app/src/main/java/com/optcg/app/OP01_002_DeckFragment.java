package com.optcg.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class OP01_002_DeckFragment extends Fragment {

    private static final String ARG_TRANSITION_NAME = "transitionName";

    public static OP01_002_DeckFragment newInstance(String transitionName) {
        OP01_002_DeckFragment fragment = new OP01_002_DeckFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRANSITION_NAME, transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(requireContext()).inflateTransition(R.transition.change_image_transform));
        setSharedElementReturnTransition(TransitionInflater.from(requireContext()).inflateTransition(R.transition.change_image_transform));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_op01_002_deck, container, false);
        ImageView imageView = view.findViewById(R.id.op01_002_img);
        if (getArguments() != null) {
            String transitionName = getArguments().getString("transitionName");
            imageView.setTransitionName(transitionName);
        }
        return view;
    }
}
