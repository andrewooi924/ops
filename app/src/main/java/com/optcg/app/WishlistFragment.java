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

    private List<CardPrice> leaderList, spList, promoList, champList;

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        RecyclerView rvLeader = view.findViewById(R.id.recycler_leader);
        RecyclerView rvSP = view.findViewById(R.id.recycler_sp);
        RecyclerView rvPromo = view.findViewById(R.id.recycler_promo);
        RecyclerView rvChamp = view.findViewById(R.id.recycler_champ);

        rvLeader.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvSP.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPromo.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvChamp.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        leaderList = new ArrayList<>();
        leaderList.add(new CardPrice(R.drawable.op01_001_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-001/l-p"));
        leaderList.add(new CardPrice(R.drawable.op01_003_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-003/l-p"));
        leaderList.add(new CardPrice(R.drawable.op02_002_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-002/l-p"));
        leaderList.add(new CardPrice(R.drawable.op03_001_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-001/l-p"));
        leaderList.add(new CardPrice(R.drawable.op03_022_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-022/l-p"));
        leaderList.add(new CardPrice(R.drawable.op03_040_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-040/l-p"));
        leaderList.add(new CardPrice(R.drawable.op04_001_p1, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-001/l-p"));
        leaderList.add(new CardPrice(R.drawable.op04_019_p1, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-019/l-p"));
        leaderList.add(new CardPrice(R.drawable.op04_039_p1, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-039/l-p"));
        leaderList.add(new CardPrice(R.drawable.op07_079_p1, "https://onepiece-card-atari.jp/expansion/500-yeas-in-the-future/card/op07-079/l-p"));
        leaderList.add(new CardPrice(R.drawable.op07_097_p1, "https://onepiece-card-atari.jp/expansion/500-yeas-in-the-future/card/op07-097/l-p"));
        leaderList.add(new CardPrice(R.drawable.op11_022_p1, "https://onepiece-card-atari.jp/expansion/a-fist-of-divine-speed/card/op11-022/l-p"));
        leaderList.add(new CardPrice(R.drawable.op11_040_p1, "https://onepiece-card-atari.jp/expansion/a-fist-of-divine-speed/card/op11-040/l-p"));
        leaderList.add(new CardPrice(R.drawable.op12_001_p1, "https://onepiece-card-atari.jp/expansion/legacy-of-the-master/card/op12-001/l-p"));
        leaderList.add(new CardPrice(R.drawable.op12_020_p1, "https://onepiece-card-atari.jp/expansion/legacy-of-the-master/card/op12-020/l-p"));
        leaderList.add(new CardPrice(R.drawable.op12_040_p1, "https://onepiece-card-atari.jp/expansion/legacy-of-the-master/card/op12-040/l-p"));
        leaderList.add(new CardPrice(R.drawable.op12_041_p1, "https://onepiece-card-atari.jp/expansion/legacy-of-the-master/card/op12-041/l-p"));
        leaderList.add(new CardPrice(R.drawable.op12_061_p1, "https://onepiece-card-atari.jp/expansion/legacy-of-the-master/card/op12-061/l-p"));
        leaderList.add(new CardPrice(R.drawable.op12_081_p1, "https://onepiece-card-atari.jp/expansion/legacy-of-the-master/card/op12-081/l-p"));

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
        spList.add(new CardPrice(R.drawable.op_st18_005_p1, "https://onepiece-card-atari.jp/expansion/a-fist-of-divine-speed/card/st18-005/sp"));
        spList.add(new CardPrice(R.drawable.op06_119_p2, "https://onepiece-card-atari.jp/expansion/a-fist-of-divine-speed/card/op06-119/sp"));
        spList.add(new CardPrice(R.drawable.eb01_057_p2, "https://onepiece-card-atari.jp/expansion/a-fist-of-divine-speed/card/eb01-057/sp"));
        spList.add(new CardPrice(R.drawable.op07_085_p2, "https://onepiece-card-atari.jp/expansion/a-fist-of-divine-speed/card/op07-085/sp"));
        spList.add(new CardPrice(R.drawable.op09_005_p1, "https://onepiece-card-atari.jp/expansion/a-fist-of-divine-speed/card/op09-005/sp"));
        spList.add(new CardPrice(R.drawable.op_st16_004_p1, "https://onepiece-card-atari.jp/expansion/a-fist-of-divine-speed/card/st16-004/sp"));
        spList.add(new CardPrice(R.drawable.op_st18_004_p1, "https://onepiece-card-atari.jp/expansion/legacy-of-the-master/card/st18-004/sp"));
        spList.add(new CardPrice(R.drawable.op_st13_011_p2, "https://onepiece-card-atari.jp/expansion/legacy-of-the-master/card/st13-011/sp"));
        spList.add(new CardPrice(R.drawable.op09_013_p1, "https://onepiece-card-atari.jp/expansion/legacy-of-the-master/card/op09-013/sp"));
        spList.add(new CardPrice(R.drawable.op10_082_p2, "https://onepiece-card-atari.jp/expansion/legacy-of-the-master/card/op10-082/sp"));
        spList.add(new CardPrice(R.drawable.op06_050_p2, "https://onepiece-card-atari.jp/expansion/legacy-of-the-master/card/op06-050/sp"));
        spList.add(new CardPrice(R.drawable.op09_037_p2, "https://onepiece-card-atari.jp/expansion/legacy-of-the-master/card/op09-037/sp"));

        promoList = new ArrayList<>();
//        promoList.add(new CardPrice(R.drawable.p_035, "https://tier-one-onepiece.jp/view/item/000000000891"));
        promoList.add(new CardPrice(R.drawable.p_036, "https://tier-one-onepiece.jp/view/item/000000000902"));
        promoList.add(new CardPrice(R.drawable.p_037, "https://tier-one-onepiece.jp/view/item/000000000903"));
        promoList.add(new CardPrice(R.drawable.p_038, "https://tier-one-onepiece.jp/view/item/000000000896"));
        promoList.add(new CardPrice(R.drawable.p_039, "https://tier-one-onepiece.jp/view/item/000000000892"));
//        promoList.add(new CardPrice(R.drawable.p_041, "https://tier-one-onepiece.jp/view/item/000000000911"));
        promoList.add(new CardPrice(R.drawable.p_041_p4, "https://tier-one-onepiece.jp/view/item/000000002201"));
        promoList.add(new CardPrice(R.drawable.p_042, "https://tier-one-onepiece.jp/view/item/000000000904"));
        promoList.add(new CardPrice(R.drawable.p_043, "https://tier-one-onepiece.jp/view/item/000000000907"));
        promoList.add(new CardPrice(R.drawable.p_044, "https://tier-one-onepiece.jp/view/item/000000001346"));
//        promoList.add(new CardPrice(R.drawable.p_045, "https://tier-one-onepiece.jp/view/item/000000001297"));
//        promoList.add(new CardPrice(R.drawable.p_046, "https://tier-one-onepiece.jp/view/item/000000001298"));
        promoList.add(new CardPrice(R.drawable.p_052, "https://tier-one-onepiece.jp/view/item/000000001511"));
        promoList.add(new CardPrice(R.drawable.p_053, "https://tier-one-onepiece.jp/view/item/000000001512"));
        promoList.add(new CardPrice(R.drawable.p_054, "https://tier-one-onepiece.jp/view/item/000000001513"));
        promoList.add(new CardPrice(R.drawable.p_055, "https://tier-one-onepiece.jp/view/item/000000001514"));
        promoList.add(new CardPrice(R.drawable.p_056, "https://tier-one-onepiece.jp/view/item/000000001515"));
        promoList.add(new CardPrice(R.drawable.p_062, "https://tier-one-onepiece.jp/view/item/000000001867"));
        promoList.add(new CardPrice(R.drawable.p_063, "https://tier-one-onepiece.jp/view/item/000000001876"));
        promoList.add(new CardPrice(R.drawable.p_064, "https://tier-one-onepiece.jp/view/item/000000001297"));
        promoList.add(new CardPrice(R.drawable.p_065, "https://tier-one-onepiece.jp/view/item/000000002215"));
        promoList.add(new CardPrice(R.drawable.p_066, "https://tier-one-onepiece.jp/view/item/000000002216"));
        promoList.add(new CardPrice(R.drawable.p_067, "https://tier-one-onepiece.jp/view/item/000000002217"));
        promoList.add(new CardPrice(R.drawable.p_068, "https://tier-one-onepiece.jp/view/item/000000002218"));
        promoList.add(new CardPrice(R.drawable.p_069, "https://tier-one-onepiece.jp/view/item/000000002310"));
        promoList.add(new CardPrice(R.drawable.p_070, "https://tier-one-onepiece.jp/view/item/000000002356"));
        promoList.add(new CardPrice(R.drawable.p_071, "https://tier-one-onepiece.jp/view/item/000000002356"));
        promoList.add(new CardPrice(R.drawable.p_072, "https://tier-one-onepiece.jp/view/item/000000003955"));
        promoList.add(new CardPrice(R.drawable.p_072_p1, "https://tier-one-onepiece.jp/view/item/000000003693"));
        promoList.add(new CardPrice(R.drawable.p_073, "https://tier-one-onepiece.jp/view/item/000000003701"));
        promoList.add(new CardPrice(R.drawable.p_076, "https://tier-one-onepiece.jp/view/item/000000002311"));
        promoList.add(new CardPrice(R.drawable.p_077, "https://tier-one-onepiece.jp/view/item/000000002532"));
//        promoList.add(new CardPrice(R.drawable.p_078, "https://tier-one-onepiece.jp/view/item/000000003670"));
        promoList.add(new CardPrice(R.drawable.p_079, "https://tier-one-onepiece.jp/view/item/000000003672"));
        promoList.add(new CardPrice(R.drawable.p_080, "https://tier-one-onepiece.jp/view/item/000000004674"));
//        promoList.add(new CardPrice(R.drawable.p_081, "https://tier-one-onepiece.jp/view/item/000000003664"));
        promoList.add(new CardPrice(R.drawable.p_082, "https://tier-one-onepiece.jp/view/item/000000004676"));
//        promoList.add(new CardPrice(R.drawable.p_083, "https://tier-one-onepiece.jp/view/item/000000004677"));
        promoList.add(new CardPrice(R.drawable.p_084, "https://tier-one-onepiece.jp/view/item/000000003708"));
        promoList.add(new CardPrice(R.drawable.p_085, "https://tier-one-onepiece.jp/view/item/000000003717"));
        promoList.add(new CardPrice(R.drawable.p_086, "https://tier-one-onepiece.jp/view/item/000000003669"));
        promoList.add(new CardPrice(R.drawable.p_087, "https://tier-one-onepiece.jp/view/item/000000004243"));
//        promoList.add(new CardPrice(R.drawable.p_088, "https://tier-one-onepiece.jp/view/item/000000003878"));
        promoList.add(new CardPrice(R.drawable.p_089, "https://tier-one-onepiece.jp/view/item/000000004678"));
        promoList.add(new CardPrice(R.drawable.p_090, "https://tier-one-onepiece.jp/view/item/000000004551"));
        promoList.add(new CardPrice(R.drawable.p_091, "https://tier-one-onepiece.jp/view/item/000000004679"));
        promoList.add(new CardPrice(R.drawable.p_092, "https://tier-one-onepiece.jp/view/item/000000004680"));
        promoList.add(new CardPrice(R.drawable.p_093, "https://tier-one-onepiece.jp/view/item/000000004651"));

        champList = new ArrayList<>();
//        champList.add(new CardPrice(R.drawable.op01_025_p4, ));

        CardAdapter champAdapter = new CardAdapter(requireContext(), champList, CardAdapter.Mode.WISHLIST);
        rvChamp.setAdapter(champAdapter);

        CardAdapter leaderAdapter = new CardAdapter(requireContext(), leaderList, CardAdapter.Mode.WISHLIST);
        rvLeader.setAdapter(leaderAdapter);

        CardAdapter spAdapter = new CardAdapter(requireContext(), spList, CardAdapter.Mode.WISHLIST);
        rvSP.setAdapter(spAdapter);

        CardAdapter promoAdapter = new CardAdapter(requireContext(), promoList, CardAdapter.Mode.WISHLIST);
        rvPromo.setAdapter(promoAdapter);

        return view;
    }
}