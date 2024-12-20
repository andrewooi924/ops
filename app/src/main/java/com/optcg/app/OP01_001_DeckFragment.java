package com.optcg.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OP01_001_DeckFragment extends Fragment {

    private RecyclerView recyclerView;
    private DeckAdapter deckAdapter;
    private List<Integer> deckImages;
    private List<Integer> cardCounts;
    private List<CardPrice> cardList;
    private static final String ARG_TRANSITION_NAME = "transitionName";

    public static OP01_001_DeckFragment newInstance(String transitionName) {
        OP01_001_DeckFragment fragment = new OP01_001_DeckFragment();
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
        View view = inflater.inflate(R.layout.fragment_op01_001_deck, container, false);
        ImageView imageView = view.findViewById(R.id.op01_001_img);
        if (getArguments() != null) {
            String transitionName = getArguments().getString("transitionName");
            imageView.setTransitionName(transitionName);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.op01_001_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // Vertical list

        deckImages = Arrays.asList(
                R.drawable.eb01_006,
                R.drawable.eb01_009,
                R.drawable.op01_016,
                R.drawable.op01_025,
                R.drawable.op02_015,
                R.drawable.op04_009,
                R.drawable.op04_010,
                R.drawable.op07_015,
                R.drawable.op08_007,
                R.drawable.op08_010,
                R.drawable.op08_013,
                R.drawable.op08_015,
                R.drawable.op10_011,
                R.drawable.st01_016
        );

        cardCounts = Arrays.asList(
               4,4,4,4,2,4,4,3,4,4,4,4,4,1
        );

        cardList = new ArrayList<>();
        cardList.add(new CardPrice(R.drawable.eb01_006, "https://onepiece-card-atari.jp/expansion/memorial-collection/card/eb01-006/sr"));
        cardList.add(new CardPrice(R.drawable.eb01_009, "https://onepiece-card-atari.jp/expansion/memorial-collection/card/eb01-009/c"));
        cardList.add(new CardPrice(R.drawable.op01_016, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-016/r"));
        cardList.add(new CardPrice(R.drawable.op01_025, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-025/sr"));
        cardList.add(new CardPrice(R.drawable.op02_015, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-015/uc"));
        cardList.add(new CardPrice(R.drawable.op04_009, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-009/uc"));
        cardList.add(new CardPrice(R.drawable.op04_010, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-010/c"));
        cardList.add(new CardPrice(R.drawable.op07_015, "https://onepiece-card-atari.jp/expansion/500-yeas-in-the-future/card/op07-015/sr"));
        cardList.add(new CardPrice(R.drawable.op08_007, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-007/sr"));
        cardList.add(new CardPrice(R.drawable.op08_010, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-010/uc"));
        cardList.add(new CardPrice(R.drawable.op08_013, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-013/uc"));
        cardList.add(new CardPrice(R.drawable.op08_015, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-015/r"));
        cardList.add(new CardPrice(R.drawable.op10_011, "https://onepiece-card-atari.jp/expansion/royal-blood/card/op10-011/r"));
        cardList.add(new CardPrice(R.drawable.st01_016, "https://tier-one-onepiece.jp/view/item/000000000016"));

        deckAdapter = new DeckAdapter(getContext(), deckImages, cardCounts, cardList);
        recyclerView.setAdapter(deckAdapter);
    }
}
