package com.optcg.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PricesFragment extends Fragment {

    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private List<CardPrice> cardList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prices, container, false);
        recyclerView = view.findViewById(R.id.pf_rv);

        cardList = new ArrayList<>();
        // OP01
        cardList.add(new CardPrice(R.drawable.op01_001_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-001/l-p"));
        cardList.add(new CardPrice(R.drawable.op01_002_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-002/l-p"));
        cardList.add(new CardPrice(R.drawable.op01_003_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-003/l-p"));
        cardList.add(new CardPrice(R.drawable.op01_031_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-031/l-p"));
        cardList.add(new CardPrice(R.drawable.op01_060_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-060/l-p"));
        cardList.add(new CardPrice(R.drawable.op01_061_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-061/l-p"));
        cardList.add(new CardPrice(R.drawable.op01_062_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-062/l-p"));
        cardList.add(new CardPrice(R.drawable.op01_091_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-091/l-p"));

        // OP02
        cardList.add(new CardPrice(R.drawable.op02_001_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-001/l-p"));
        cardList.add(new CardPrice(R.drawable.op02_002_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-002/l-p"));
        cardList.add(new CardPrice(R.drawable.op02_025_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/p02-025/l-p"));
        cardList.add(new CardPrice(R.drawable.op02_026_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-026/l-p"));
        cardList.add(new CardPrice(R.drawable.op02_049_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-049/l-p"));
        cardList.add(new CardPrice(R.drawable.op02_071_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-071/l-p"));
        cardList.add(new CardPrice(R.drawable.op02_072_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-072/l-p"));
        cardList.add(new CardPrice(R.drawable.op02_093_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-093/l-p"));

        // OP05
        cardList.add(new CardPrice(R.drawable.op05_001_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-001/l-p"));
        cardList.add(new CardPrice(R.drawable.op05_002_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-002/l-p"));
        cardList.add(new CardPrice(R.drawable.op05_022_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-022/l-p"));
        cardList.add(new CardPrice(R.drawable.op05_041_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-041/l-p"));
        cardList.add(new CardPrice(R.drawable.op05_060_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-060/l-p"));
        cardList.add(new CardPrice(R.drawable.op05_098_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-098/l-p"));

        // OP06
        cardList.add(new CardPrice(R.drawable.op06_001_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-001/l-p"));
        cardList.add(new CardPrice(R.drawable.op06_020_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-020/l-p"));
        cardList.add(new CardPrice(R.drawable.op06_021_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-021/l-p"));
        cardList.add(new CardPrice(R.drawable.op06_022_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-022/l-p"));
        cardList.add(new CardPrice(R.drawable.op06_042_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-042/l-p"));
        cardList.add(new CardPrice(R.drawable.op06_080_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-080/l-p"));

        // OP08
        cardList.add(new CardPrice(R.drawable.op08_001_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-001/l-p"));
        cardList.add(new CardPrice(R.drawable.op08_002_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-002/l-p"));
        cardList.add(new CardPrice(R.drawable.op08_021_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-021/l-p"));
        cardList.add(new CardPrice(R.drawable.op08_057_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-057/l-p"));
        cardList.add(new CardPrice(R.drawable.op08_058_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-058/l-p"));
        cardList.add(new CardPrice(R.drawable.op08_098_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-098/l-p"));

        // PRB01
        cardList.add(new CardPrice(R.drawable.prb01_001_p1, "https://onepiece-card-atari.jp/expansion/one-piece-card-the-best/card/prb01-001/l-p"));

        // OP09
        cardList.add(new CardPrice(R.drawable.op09_001_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-001/l-p"));
        cardList.add(new CardPrice(R.drawable.op09_022_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-022/l-p"));
        cardList.add(new CardPrice(R.drawable.op09_042_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-042/l-p"));
        cardList.add(new CardPrice(R.drawable.op09_061_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-061/l-p"));
        cardList.add(new CardPrice(R.drawable.op09_062_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-062/l-p"));
        cardList.add(new CardPrice(R.drawable.op09_081_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-081/l-p"));

        cardAdapter = new CardAdapter(cardList);
        recyclerView.setAdapter(cardAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        return view;
    }
}