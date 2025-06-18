package com.optcg.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {

    private List<CardPrice> leaderList, spList, promoList;

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        RecyclerView rvLeader = view.findViewById(R.id.recycler_leader);
        RecyclerView rvSP = view.findViewById(R.id.recycler_sp);
        RecyclerView rvPromo = view.findViewById(R.id.recycler_promo);

        rvLeader.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvSP.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPromo.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        leaderList = new ArrayList<>();
        leaderList.add(new CardPrice(R.drawable.op01_001_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-001/l-p"));
        leaderList.add(new CardPrice(R.drawable.op01_002_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-002/l-p"));
        leaderList.add(new CardPrice(R.drawable.op01_003_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-003/l-p"));
        leaderList.add(new CardPrice(R.drawable.op01_060_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-060/l-p"));
        leaderList.add(new CardPrice(R.drawable.op01_061_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-061/l-p"));
        leaderList.add(new CardPrice(R.drawable.op02_002_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-002/l-p"));
        leaderList.add(new CardPrice(R.drawable.op02_049_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-049/l-p"));
        leaderList.add(new CardPrice(R.drawable.op02_072_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-072/l-p"));
        leaderList.add(new CardPrice(R.drawable.op02_093_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-093/l-p"));
        leaderList.add(new CardPrice(R.drawable.op03_001_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-001/l-p"));
        leaderList.add(new CardPrice(R.drawable.op03_022_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-022/l-p"));
        leaderList.add(new CardPrice(R.drawable.op03_040_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-040/l-p"));
        leaderList.add(new CardPrice(R.drawable.op03_058_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-058/l-p"));
        leaderList.add(new CardPrice(R.drawable.op03_076_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-076/l-p"));
        leaderList.add(new CardPrice(R.drawable.op03_077_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-077/l-p"));
        leaderList.add(new CardPrice(R.drawable.op03_099_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-099/l-p"));
        leaderList.add(new CardPrice(R.drawable.op04_001_p1, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-001/l-p"));
        leaderList.add(new CardPrice(R.drawable.op04_019_p1, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-019/l-p"));
        leaderList.add(new CardPrice(R.drawable.op04_039_p1, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-039/l-p"));
        leaderList.add(new CardPrice(R.drawable.op04_040_p1, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-040/l-p"));
        leaderList.add(new CardPrice(R.drawable.op04_058_p1, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-058/l-p"));
        leaderList.add(new CardPrice(R.drawable.op07_038_p1, "https://onepiece-card-atari.jp/expansion/500-yeas-in-the-future/card/op07-038/l-p"));
        leaderList.add(new CardPrice(R.drawable.op07_079_p1, "https://onepiece-card-atari.jp/expansion/500-yeas-in-the-future/card/op07-079/l-p"));
        leaderList.add(new CardPrice(R.drawable.op07_097_p1, "https://onepiece-card-atari.jp/expansion/500-yeas-in-the-future/card/op07-097/l-p"));
        leaderList.add(new CardPrice(R.drawable.op08_098_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-098/l-p"));
        leaderList.add(new CardPrice(R.drawable.op09_001_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-001/l-p"));
        leaderList.add(new CardPrice(R.drawable.op10_001_p1, "https://onepiece-card-atari.jp/expansion/royal-blood/card/op10-001/l-p"));
        leaderList.add(new CardPrice(R.drawable.op10_003_p1, "https://onepiece-card-atari.jp/expansion/royal-blood/card/op10-003/l-p"));
        leaderList.add(new CardPrice(R.drawable.op10_042_p1, "https://onepiece-card-atari.jp/expansion/royal-blood/card/op10-042/l-p"));
        leaderList.add(new CardPrice(R.drawable.op10_099_p1, "https://onepiece-card-atari.jp/expansion/royal-blood/card/op10-099/l-p"));

        leaderList.add(new CardPrice(R.drawable.eb01_001_p1, "https://onepiece-card-atari.jp/expansion/memorial-collection/card/eb01-001/l-p"));
        leaderList.add(new CardPrice(R.drawable.eb02_010_p1, "https://onepiece-card-atari.jp/expansion/anime-25th-collection/card/eb02-010/l-p"));

        spList = new ArrayList<>();
        spList.add(new CardPrice(R.drawable.op01_016_p4, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op01-016/sp"));
        spList.add(new CardPrice(R.drawable.op02_120_p2, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op02-120/sp"));
        spList.add(new CardPrice(R.drawable.op01_121_p2, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op01-121/sp"));
        spList.add(new CardPrice(R.drawable.op05_100_p2, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-100/sp"));
        spList.add(new CardPrice(R.drawable.op04_044_p2, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op04-044/sp"));
        spList.add(new CardPrice(R.drawable.op03_092_p2, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op03-092/sp"));
        spList.add(new CardPrice(R.drawable.op_st02_007_p3, "https://onepiece-card-atari.jp/expansion/two-legends/card/st02-007/sp"));
        spList.add(new CardPrice(R.drawable.op02_013_p3, "https://onepiece-card-atari.jp/expansion/two-legends/card/op02-013/sp"));
        spList.add(new CardPrice(R.drawable.op03_112_p4, "https://onepiece-card-atari.jp/expansion/two-legends/card/op03-112/sp"));
        spList.add(new CardPrice(R.drawable.op_st06_006_p2, "https://onepiece-card-atari.jp/expansion/two-legends/card/st06-006/sp"));
        spList.add(new CardPrice(R.drawable.op_st03_004_p2, "https://onepiece-card-atari.jp/expansion/two-legends/card/st03-004/sp"));
        spList.add(new CardPrice(R.drawable.op_st04_005_p3, "https://onepiece-card-atari.jp/expansion/two-legends/card/st04-005/sp"));
        spList.add(new CardPrice(R.drawable.op08_106_p4, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op08-106/sp"));
        spList.add(new CardPrice(R.drawable.op07_051_p3, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op07-051/sp"));
        spList.add(new CardPrice(R.drawable.op05_067_p4, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op05-067/sp"));
        spList.add(new CardPrice(R.drawable.op04_119_p2, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op04-119/sp"));
        spList.add(new CardPrice(R.drawable.op07_015_p2, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op07-015/sp"));
        spList.add(new CardPrice(R.drawable.op05_093_p2, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op05-093/sp"));

        promoList = new ArrayList<>();

        CardAdapter leaderAdapter = new CardAdapter(requireContext(), leaderList, CardAdapter.Mode.WISHLIST);
        rvLeader.setAdapter(leaderAdapter);

        CardAdapter spAdapter = new CardAdapter(requireContext(), spList, CardAdapter.Mode.WISHLIST);
        rvSP.setAdapter(spAdapter);

        CardAdapter promoAdapter = new CardAdapter(requireContext(), promoList, CardAdapter.Mode.WISHLIST);
        rvPromo.setAdapter(promoAdapter);

        return view;
    }
}