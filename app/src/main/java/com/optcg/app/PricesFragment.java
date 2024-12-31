package com.optcg.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

public class PricesFragment extends Fragment {

    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private List<CardPrice> cardList;
    private List<PersonalCard> personalCardList;
    private LineChart lineChart;
    private List<Entry> priceEntries;
    private SharedPreferences sharedPreferences;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private TextView portfolioTotalValue;
    private TextView portfolioSubtitle;

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
//        cardList.add(new CardPrice(R.drawable.op01_031_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-031/l-p"));
        cardList.add(new CardPrice(R.drawable.op01_060_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-060/l-p"));
        cardList.add(new CardPrice(R.drawable.op01_061_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-061/l-p"));
        cardList.add(new CardPrice(R.drawable.op01_062_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-062/l-p"));
//        cardList.add(new CardPrice(R.drawable.op01_091_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-091/l-p"));

        // OP02
//        cardList.add(new CardPrice(R.drawable.op02_001_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-001/l-p"));
        cardList.add(new CardPrice(R.drawable.op02_002_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-002/l-p"));
//        cardList.add(new CardPrice(R.drawable.op02_025_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/p02-025/l-p"));
//        cardList.add(new CardPrice(R.drawable.op02_026_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-026/l-p"));
        cardList.add(new CardPrice(R.drawable.op02_049_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-049/l-p"));
//        cardList.add(new CardPrice(R.drawable.op02_071_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-071/l-p"));
        cardList.add(new CardPrice(R.drawable.op02_072_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-072/l-p"));
        cardList.add(new CardPrice(R.drawable.op02_093_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-093/l-p"));

        // OP03
        cardList.add(new CardPrice(R.drawable.op03_001_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-001/l-p"));
        cardList.add(new CardPrice(R.drawable.op03_022_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-022/l-p"));
        cardList.add(new CardPrice(R.drawable.op03_040_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-040/l-p"));
        cardList.add(new CardPrice(R.drawable.op03_058_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-058/l-p"));
        cardList.add(new CardPrice(R.drawable.op03_076_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-076/l-p"));
        cardList.add(new CardPrice(R.drawable.op03_077_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-077/l-p"));
        cardList.add(new CardPrice(R.drawable.op03_099_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-099/l-p"));

        // OP04
        cardList.add(new CardPrice(R.drawable.op04_001_p1, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-001/l-p"));
        cardList.add(new CardPrice(R.drawable.op04_019_p1, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-019/l-p"));
        cardList.add(new CardPrice(R.drawable.op04_020_p1, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-020/l-p"));
        cardList.add(new CardPrice(R.drawable.op04_039_p1, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-039/l-p"));
        cardList.add(new CardPrice(R.drawable.op04_040_p1, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-040/l-p"));
        cardList.add(new CardPrice(R.drawable.op04_058_p1, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-058/l-p"));

        // OP05
//        cardList.add(new CardPrice(R.drawable.op05_001_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-001/l-p"));
//        cardList.add(new CardPrice(R.drawable.op05_002_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-002/l-p"));
//        cardList.add(new CardPrice(R.drawable.op05_022_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-022/l-p"));
//        cardList.add(new CardPrice(R.drawable.op05_041_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-041/l-p"));
//        cardList.add(new CardPrice(R.drawable.op05_060_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-060/l-p"));
//        cardList.add(new CardPrice(R.drawable.op05_098_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-098/l-p"));

        // OP06
        cardList.add(new CardPrice(R.drawable.op06_001_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-001/l-p"));
//        cardList.add(new CardPrice(R.drawable.op06_020_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-020/l-p"));
//        cardList.add(new CardPrice(R.drawable.op06_021_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-021/l-p"));
        cardList.add(new CardPrice(R.drawable.op06_022_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-022/l-p"));
//        cardList.add(new CardPrice(R.drawable.op06_042_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-042/l-p"));
//        cardList.add(new CardPrice(R.drawable.op06_080_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-080/l-p"));

        // EB01
        cardList.add(new CardPrice(R.drawable.eb01_001_p1, "https://onepiece-card-atari.jp/expansion/memorial-collection/card/eb01-001/l-p"));
        cardList.add(new CardPrice(R.drawable.eb01_021_p1, "https://onepiece-card-atari.jp/expansion/memorial-collection/card/eb01-021/l-p"));
        cardList.add(new CardPrice(R.drawable.eb01_040_p1, "https://onepiece-card-atari.jp/expansion/memorial-collection/card/eb01-040/l-p"));

        // OP07
        cardList.add(new CardPrice(R.drawable.op07_001_p1, "https://onepiece-card-atari.jp/expansion/500-yeas-in-the-future/card/op07-001/l-p"));
        cardList.add(new CardPrice(R.drawable.op07_019_p1, "https://onepiece-card-atari.jp/expansion/500-yeas-in-the-future/card/op07-019/l-p"));
        cardList.add(new CardPrice(R.drawable.op07_038_p1, "https://onepiece-card-atari.jp/expansion/500-yeas-in-the-future/card/op07-038/l-p"));
        cardList.add(new CardPrice(R.drawable.op07_059_p1, "https://onepiece-card-atari.jp/expansion/500-yeas-in-the-future/card/op07-059/l-p"));
        cardList.add(new CardPrice(R.drawable.op07_079_p1, "https://onepiece-card-atari.jp/expansion/500-yeas-in-the-future/card/op07-079/l-p"));
        cardList.add(new CardPrice(R.drawable.op07_097_p1, "https://onepiece-card-atari.jp/expansion/500-yeas-in-the-future/card/op07-097/l-p"));

        // OP08
//        cardList.add(new CardPrice(R.drawable.op08_001_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-001/l-p"));
//        cardList.add(new CardPrice(R.drawable.op08_002_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-002/l-p"));
//        cardList.add(new CardPrice(R.drawable.op08_021_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-021/l-p"));
//        cardList.add(new CardPrice(R.drawable.op08_057_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-057/l-p"));
//        cardList.add(new CardPrice(R.drawable.op08_058_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-058/l-p"));
        cardList.add(new CardPrice(R.drawable.op08_098_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-098/l-p"));

        // PRB01
//        cardList.add(new CardPrice(R.drawable.prb01_001_p1, "https://onepiece-card-atari.jp/expansion/one-piece-card-the-best/card/prb01-001/l-p"));

        // OP09
        cardList.add(new CardPrice(R.drawable.op09_001_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-001/l-p"));
//        cardList.add(new CardPrice(R.drawable.op09_022_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-022/l-p"));
//        cardList.add(new CardPrice(R.drawable.op09_042_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-042/l-p"));
        cardList.add(new CardPrice(R.drawable.op09_061_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-061/l-p"));
//        cardList.add(new CardPrice(R.drawable.op09_062_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-062/l-p"));
        cardList.add(new CardPrice(R.drawable.op09_081_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-081/l-p"));

        // OP10
        cardList.add(new CardPrice(R.drawable.op10_001_p1, "https://onepiece-card-atari.jp/expansion/royal-blood/card/op10-001/l-p"));
        cardList.add(new CardPrice(R.drawable.op10_002_p1, "https://onepiece-card-atari.jp/expansion/royal-blood/card/op10-002/l-p"));
        cardList.add(new CardPrice(R.drawable.op10_003_p1, "https://onepiece-card-atari.jp/expansion/royal-blood/card/op10-003/l-p"));
        cardList.add(new CardPrice(R.drawable.op10_022_p1, "https://onepiece-card-atari.jp/expansion/royal-blood/card/op10-022/l-p"));
        cardList.add(new CardPrice(R.drawable.op10_042_p1, "https://onepiece-card-atari.jp/expansion/royal-blood/card/op10-042/l-p"));
        cardList.add(new CardPrice(R.drawable.op10_099_p1, "https://onepiece-card-atari.jp/expansion/royal-blood/card/op10-099/l-p"));

        // SP
        cardList.add(new CardPrice(R.drawable.op01_016_p4, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op01-016/sp"));
        cardList.add(new CardPrice(R.drawable.op02_120_p2, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op02-120/sp"));
        cardList.add(new CardPrice(R.drawable.op01_121_p2, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op01-121/sp"));
        cardList.add(new CardPrice(R.drawable.op05_100_p2, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-100/sp"));
        cardList.add(new CardPrice(R.drawable.op04_044_p2, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op04-044/sp"));
        cardList.add(new CardPrice(R.drawable.op03_092_p2, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op03-092/sp"));
        cardList.add(new CardPrice(R.drawable.op_st02_007_p3, "https://onepiece-card-atari.jp/expansion/two-legends/card/st02-007/sp"));
        cardList.add(new CardPrice(R.drawable.op03_112_p4, "https://onepiece-card-atari.jp/expansion/two-legends/card/op03-112/sp"));
        cardList.add(new CardPrice(R.drawable.op02_013_p3, "https://onepiece-card-atari.jp/expansion/two-legends/card/op02-013/sp"));
        cardList.add(new CardPrice(R.drawable.op_st03_004_p2, "https://onepiece-card-atari.jp/expansion/two-legends/card/st03-004/sp"));
        cardList.add(new CardPrice(R.drawable.op_st06_006_p2, "https://onepiece-card-atari.jp/expansion/two-legends/card/st06-006/sp"));
        cardList.add(new CardPrice(R.drawable.op_st04_005_p3, "https://onepiece-card-atari.jp/expansion/two-legends/card/st04-005/sp"));
        cardList.add(new CardPrice(R.drawable.op08_106_p4, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op08-106/sp"));
        cardList.add(new CardPrice(R.drawable.op07_051_p3, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op07-051/sp"));
        cardList.add(new CardPrice(R.drawable.op05_067_p4, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op05-067/sp"));
        cardList.add(new CardPrice(R.drawable.op07_015_p2, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op07-015/sp"));
        cardList.add(new CardPrice(R.drawable.op05_093_p2, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op05-093/sp"));
        cardList.add(new CardPrice(R.drawable.op04_119_p2, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op04-119/sp"));

        // PROMO
        cardList.add(new CardPrice(R.drawable.p_035, "https://tier-one-onepiece.jp/view/item/000000000891"));
        cardList.add(new CardPrice(R.drawable.p_036, "https://tier-one-onepiece.jp/view/item/000000000902"));
        cardList.add(new CardPrice(R.drawable.p_037, "https://tier-one-onepiece.jp/view/item/000000000903"));
        cardList.add(new CardPrice(R.drawable.p_038, "https://tier-one-onepiece.jp/view/item/000000000896"));
        cardList.add(new CardPrice(R.drawable.p_039, "https://tier-one-onepiece.jp/view/item/000000000892"));
        cardList.add(new CardPrice(R.drawable.p_042, "https://tier-one-onepiece.jp/view/item/000000002262"));
        cardList.add(new CardPrice(R.drawable.p_043, "https://tier-one-onepiece.jp/view/item/000000000907"));
        cardList.add(new CardPrice(R.drawable.p_044, "https://tier-one-onepiece.jp/view/item/000000001346"));
        cardList.add(new CardPrice(R.drawable.p_045, "https://tier-one-onepiece.jp/view/item/000000001297"));
        cardList.add(new CardPrice(R.drawable.p_046, "https://tier-one-onepiece.jp/view/item/000000001298"));
        cardList.add(new CardPrice(R.drawable.p_047, "https://tier-one-onepiece.jp/view/item/000000001521"));
        cardList.add(new CardPrice(R.drawable.p_048, "https://tier-one-onepiece.jp/view/item/000000001522"));
        cardList.add(new CardPrice(R.drawable.p_049, "https://tier-one-onepiece.jp/view/item/000000001523"));
        cardList.add(new CardPrice(R.drawable.p_050, "https://tier-one-onepiece.jp/view/item/000000001524"));
        cardList.add(new CardPrice(R.drawable.p_051, "https://tier-one-onepiece.jp/view/item/000000001525"));
        cardList.add(new CardPrice(R.drawable.p_052, "https://tier-one-onepiece.jp/view/item/000000001511"));
        cardList.add(new CardPrice(R.drawable.p_054, "https://tier-one-onepiece.jp/view/item/000000001513"));
        cardList.add(new CardPrice(R.drawable.p_056, "https://tier-one-onepiece.jp/view/item/000000001515"));
        cardList.add(new CardPrice(R.drawable.p_063, "https://tier-one-onepiece.jp/view/item/000000001876"));
        cardList.add(new CardPrice(R.drawable.p_066, "https://tier-one-onepiece.jp/view/item/000000002216"));
        cardList.add(new CardPrice(R.drawable.p_067, "https://tier-one-onepiece.jp/view/item/000000002217"));
        cardList.add(new CardPrice(R.drawable.p_068, "https://tier-one-onepiece.jp/view/item/000000002218"));
        cardList.add(new CardPrice(R.drawable.p_069, "https://tier-one-onepiece.jp/view/item/000000002310"));
        cardList.add(new CardPrice(R.drawable.p_070, "https://tier-one-onepiece.jp/view/item/000000002356"));
        cardList.add(new CardPrice(R.drawable.p_072, "https://tier-one-onepiece.jp/view/item/000000003693"));
        cardList.add(new CardPrice(R.drawable.p_076, "https://tier-one-onepiece.jp/view/item/000000002311"));
        cardList.add(new CardPrice(R.drawable.p_077, "https://tier-one-onepiece.jp/view/item/000000002532"));
        cardList.add(new CardPrice(R.drawable.p_078, "https://tier-one-onepiece.jp/view/item/000000002533"));
        cardList.add(new CardPrice(R.drawable.p_079, "https://tier-one-onepiece.jp/view/item/000000003672"));
        cardList.add(new CardPrice(R.drawable.p_081, "https://tier-one-onepiece.jp/view/item/000000003664"));
        cardList.add(new CardPrice(R.drawable.p_084, "https://tier-one-onepiece.jp/view/item/000000003708"));
        cardList.add(new CardPrice(R.drawable.p_085, "https://tier-one-onepiece.jp/view/item/000000003717"));
        cardList.add(new CardPrice(R.drawable.p_086, "https://tier-one-onepiece.jp/view/item/000000003669"));
        cardList.add(new CardPrice(R.drawable.p_088, "https://tier-one-onepiece.jp/view/item/000000003878"));

        // BOA
        cardList.add(new CardPrice(R.drawable.op01_078_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-078/sr-p"));
        cardList.add(new CardPrice(R.drawable.op01_078_p3, "https://onepiece-card-atari.jp/expansion/one-piece-card-the-best/card/op01-078/sr-p"));
        cardList.add(new CardPrice(R.drawable.op07_051_p1, "https://onepiece-card-atari.jp/expansion/500-yeas-in-the-future/card/op07-051/sr-p"));
        cardList.add(new CardPrice(R.drawable.op_st03_013_p4, "https://onepiece-card-atari.jp/expansion/one-piece-card-the-best/card/st03-013/c-p"));

        cardAdapter = new CardAdapter(getContext(), cardList);
        recyclerView.setAdapter(cardAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        sharedPreferences = requireContext().getSharedPreferences("PortfolioData", Context.MODE_PRIVATE);

        personalCardList = new ArrayList<>();
        personalCardList.add(new PersonalCard(R.drawable.op01_031_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-031/l-p", 30.0f));
        personalCardList.add(new PersonalCard(R.drawable.op01_091_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-091/l-p", 30.0f));
        personalCardList.add(new PersonalCard(R.drawable.op02_001_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-001/l-p", 0.0f));
        personalCardList.add(new PersonalCard(R.drawable.op02_025_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/p02-025/l-p", 30.0f));
        personalCardList.add(new PersonalCard(R.drawable.op02_026_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-026/l-p", 59.4f));
        personalCardList.add(new PersonalCard(R.drawable.op02_071_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-071/l-p", 30.0f));
        personalCardList.add(new PersonalCard(R.drawable.op03_021_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-021/l-p", 0.0f));
        personalCardList.add(new PersonalCard(R.drawable.op05_001_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-001/l-p", 32.4f));
        personalCardList.add(new PersonalCard(R.drawable.op05_002_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-002/l-p", 28.5f));
        personalCardList.add(new PersonalCard(R.drawable.op05_022_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-022/l-p", 20.4f));
        personalCardList.add(new PersonalCard(R.drawable.op05_041_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-041/l-p", 18.0f));
        personalCardList.add(new PersonalCard(R.drawable.op05_060_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-060/l-p", 84.0f));
        personalCardList.add(new PersonalCard(R.drawable.op05_098_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-098/l-p", 48.0f));
        personalCardList.add(new PersonalCard(R.drawable.op06_020_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-020/l-p", 16.8f));
        personalCardList.add(new PersonalCard(R.drawable.op06_021_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-021/l-p", 77.1f));
        personalCardList.add(new PersonalCard(R.drawable.op06_042_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-042/l-p", 105.0f));
        personalCardList.add(new PersonalCard(R.drawable.op06_080_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-080/l-p", 30.0f));
        personalCardList.add(new PersonalCard(R.drawable.op08_001_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-001/l-p", 26.4f));
        personalCardList.add(new PersonalCard(R.drawable.op08_002_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-002/l-p", 32.4f));
        personalCardList.add(new PersonalCard(R.drawable.op08_021_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-021/l-p", 20.4f));
        personalCardList.add(new PersonalCard(R.drawable.op08_057_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-057/l-p", 26.4f));
        personalCardList.add(new PersonalCard(R.drawable.op08_058_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-058/l-p", 72.0f));
        personalCardList.add(new PersonalCard(R.drawable.prb01_001_p1, "https://onepiece-card-atari.jp/expansion/one-piece-card-the-best/card/prb01-001/l-p", 60.0f));
        personalCardList.add(new PersonalCard(R.drawable.op09_022_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-022/l-p", 27.0f));
        personalCardList.add(new PersonalCard(R.drawable.op09_042_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-042/l-p", 32.4f));
        personalCardList.add(new PersonalCard(R.drawable.op09_062_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-062/l-p", 28.5f));
        personalCardList.add(new PersonalCard(R.drawable.op01_013_p3, "https://tier-one-onepiece.jp/view/item/000000001871", 16.5f));
        personalCardList.add(new PersonalCard(R.drawable.st01_005_p3, "https://tier-one-onepiece.jp/view/item/000000001872", 4.5f));
        personalCardList.add(new PersonalCard(R.drawable.op02_059_p2, "https://tier-one-onepiece.jp/view/item/000000001869", 58.5f));
        personalCardList.add(new PersonalCard(R.drawable.st03_008_p4, "https://tier-one-onepiece.jp/view/item/000000001870", 16.5f));
        personalCardList.add(new PersonalCard(R.drawable.p_028_p2, "https://tier-one-onepiece.jp/view/item/000000001855", 12.0f));
        personalCardList.add(new PersonalCard(R.drawable.op01_005_p4, "https://tier-one-onepiece.jp/view/item/000000001854", 54.0f));
        personalCardList.add(new PersonalCard(R.drawable.st01_006_p7, "https://tier-one-onepiece.jp/view/item/000000001857", 24.0f));
        personalCardList.add(new PersonalCard(R.drawable.st01_008_p3, "https://tier-one-onepiece.jp/view/item/000000001858", 15.0f));
        personalCardList.add(new PersonalCard(R.drawable.op01_021_p5, "https://tier-one-onepiece.jp/view/item/000000001856", 0.0f));
        personalCardList.add(new PersonalCard(R.drawable.p_001_p5, "https://tier-one-onepiece.jp/view/item/000000001852", 17.25f));
        personalCardList.add(new PersonalCard(R.drawable.op01_016_p6, "https://tier-one-onepiece.jp/view/item/000000001850", 38.25f));
        personalCardList.add(new PersonalCard(R.drawable.st09_012_p1, "https://tier-one-onepiece.jp/view/item/000000001853", 53.25f));
        personalCardList.add(new PersonalCard(R.drawable.st01_013_p4, "https://tier-one-onepiece.jp/view/item/000000001851", 17.25f));

        preloadInitialData();
        lineChart = view.findViewById(R.id.portfolioChart);
        portfolioTotalValue = view.findViewById(R.id.portfolioTotalValue);
        portfolioSubtitle = view.findViewById(R.id.portfolioSubtitle);
        setupLineChart();
        loadAndUpdateData();

        return view;
    }

    private void setupLineChart() {
        lineChart.setNoDataText("Loading portfolio...");
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setHighlightPerDragEnabled(true);
        lineChart.setHighlightPerTapEnabled(true);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateX(200);

        PortfolioMarkerView markerView = new PortfolioMarkerView(getContext(), R.layout.portfolio_marker_view);
        lineChart.setMarker(markerView);

        Paint highlightPaint = lineChart.getRenderer().getPaintHighlight();
        highlightPaint.setPathEffect(new DashPathEffect(new float[]{10f, 5f}, 0)); // Dashed line
        highlightPaint.setStrokeWidth(2f); // Adjust thickness
        highlightPaint.setColor(Color.LTGRAY); // Adjust color

        // Disable horizontal highlight lines explicitly
        lineChart.setDrawMarkers(true); // Ensure marker works on selection

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                updateGraphWithHighlight(e);
            }

            @Override
            public void onNothingSelected() {
                Entry defaultEntry = priceEntries.isEmpty() ? null : priceEntries.get(priceEntries.size() - 1);
                updateGraphWithHighlight(defaultEntry);
            }
        });

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawLabels(false);
        xAxis.setAxisLineWidth(1f);
        xAxis.setAxisLineColor(Color.parseColor("#373737"));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setDrawAxisLine(true);
        rightAxis.setAxisLineWidth(1f);
        rightAxis.setAxisLineColor(Color.parseColor("#373737"));
        rightAxis.setTextColor(Color.parseColor("#BCBCBC"));
        rightAxis.setDrawGridLines(false);
    }

    private void preloadInitialData() {
        String initialDate = "2024-12-28";
        float total = 0;
        for (PersonalCard card : personalCardList) {
            total += card.getInitialPrice();
        }
        if (!sharedPreferences.contains(initialDate)) {
            // Store initial price for 28th December 2024
            sharedPreferences.edit().putFloat(initialDate, total).apply();
        }
    }

    private void loadAndUpdateData() {
        priceEntries = new ArrayList<>();

        // Load stored data
        Map<String, ?> storedData = sharedPreferences.getAll();

        // Convert the stored data into a list and sort it
        List<Map.Entry<String, Float>> sortedEntries = new ArrayList<>();
        for (Map.Entry<String, ?> entry : storedData.entrySet()) {
            try {
                sortedEntries.add(Map.entry(entry.getKey(), Float.parseFloat(entry.getValue().toString())));
            } catch (Exception e) {
                e.printStackTrace(); // Log parsing errors
            }
        }
        sortedEntries.sort(Comparator.comparing(Map.Entry::getKey)); // Sort by date

        // Use a final or effectively final index
        int[] index = {0};
        for (Map.Entry<String, Float> entry : sortedEntries) {
            priceEntries.add(new Entry(index[0], entry.getValue()));
            index[0]++;
        }

        // Immediately update the graph with the stored data
        updateGraph();

        // Check if today's price is already calculated
        String today = dateFormat.format(new Date());
        // Fetch today's total value asynchronously
        new Thread(() -> {
            float totalValue = calculateTotalValue();
            sharedPreferences.edit().putFloat(today, totalValue).apply();

            // Add today's data to the graph on UI thread
            new Handler(Looper.getMainLooper()).post(() -> {
                boolean updated = false;

                // Check if today's entry exists and update it
                for (Entry entry : priceEntries) {
                    if (entry.getX() == (index[0] - 1)) {
                        entry.setY(totalValue);
                        updated = true;
                        break;
                    }
                }

                if (!updated) {
                    priceEntries.add(new Entry(index[0], totalValue)); // Use the updated index
                    index[0]++;
                }

                updateGraph();
                calculatePriceDifference();
            });
        }).start();
    }



    private void updateGraph() {
        LineDataSet lineDataSet = new LineDataSet(priceEntries, "");
        lineDataSet.setColor(Color.parseColor("#FFD700"));       // Line color
        lineDataSet.setCircleColor(Color.parseColor("#FFD700")); // Circle color
        lineDataSet.setDrawValues(false);       // Disable value labels
        lineDataSet.setDrawCircles(false);      // Hide data points (circles)
        lineDataSet.setLineWidth(3f);           // Line thickness

        // Highlight the last data point with a circle
        if (!priceEntries.isEmpty()) {
            int lastIndex = priceEntries.size() - 1;
            Entry lastEntry = priceEntries.get(lastIndex);

            // Add a special dataset for the last point
            List<Entry> lastPoint = new ArrayList<>();
            lastPoint.add(lastEntry);

            LineDataSet lastPointDataSet = new LineDataSet(lastPoint, "");
            lastPointDataSet.setColor(Color.parseColor("#FFD700"));          // Use the same line color
            lastPointDataSet.setCircleColor(Color.parseColor("#FFD700"));     // Circle color for the last point
            lastPointDataSet.setCircleHoleColor(Color.parseColor("#FFD700"));
            lastPointDataSet.setCircleRadius(4f);          // Size of the circle
            lastPointDataSet.setDrawCircles(true);          // Enable circle
            lastPointDataSet.setDrawValues(false);          // No value label
            lastPointDataSet.setHighlightEnabled(false);    // Disable highlighting
            lastPointDataSet.setLineWidth(0f);              // No connecting line for this dataset

            // Combine datasets
            LineData lineData = new LineData(lineDataSet, lastPointDataSet);
            lineChart.setData(lineData);
        } else {
            lineChart.setData(new LineData(lineDataSet));
        }

        lineChart.invalidate(); // Refresh the chart
    }

    private void updateGraphWithHighlight(Entry highlightedEntry) {
        LineDataSet lineDataSet = new LineDataSet(priceEntries, "");
        lineDataSet.setColor(Color.parseColor("#FFD700"));       // Line color
        lineDataSet.setCircleColor(Color.parseColor("#FFD700")); // Circle color
        lineDataSet.setDrawValues(false);       // Disable value labels
        lineDataSet.setDrawCircles(false);      // Hide data points (circles)
        lineDataSet.setLineWidth(3f);           // Line thickness

        // Highlight configuration
        lineDataSet.setHighlightEnabled(true); // Enable highlighting
        lineDataSet.setHighLightColor(Color.LTGRAY); // Vertical line color
        lineDataSet.enableDashedHighlightLine(10f, 5f, 0f); // Dashed effect

        List<Entry> highlightPoints = new ArrayList<>();
        if (highlightedEntry != null) {
            highlightPoints.add(highlightedEntry);
        }

        LineDataSet highlightDataSet = new LineDataSet(highlightPoints, "");
        highlightDataSet.setColor(Color.parseColor("#FFD700"));          // Same line color
        highlightDataSet.setCircleColor(Color.parseColor("#FFD700"));     // Circle color
        highlightDataSet.setCircleHoleColor(Color.parseColor("#FFD700"));
        highlightDataSet.setCircleRadius(4f);          // Size of the circle
        highlightDataSet.setDrawCircles(true);          // Enable circle
        highlightDataSet.setDrawValues(false);          // No value label
        highlightDataSet.setHighlightEnabled(false);    // Disable highlighting
        highlightDataSet.setLineWidth(0f);              // No connecting line for this dataset

        LineData lineData = new LineData(lineDataSet);
        if (!highlightPoints.isEmpty()) {
            lineData.addDataSet(highlightDataSet); // Add the highlight data set
        }

        lineChart.setData(lineData);
        lineChart.invalidate(); // Refresh the chart
    }


    private float calculateTotalValue() {
        float total = 0;
        for (PersonalCard card : personalCardList) {
            if (card.getUrl().contains("card-atari")) {
                float realTimePrice = fetchCardPriceA(card.getUrl());
                total += realTimePrice;
            }
            else if (card.getUrl().contains("tier-one")) {
                float realTimePrice = fetchCardPriceB(card.getUrl());
                total += realTimePrice;
            }
            Log.d("AVGPRICE", "" + total);
        }
        return total;
    }

    private float fetchCardPriceA(String url) {
        try {
            // Fetch and parse the HTML document (this should ideally be done asynchronously)
            Document doc = Jsoup.connect(url).get();
            String avgPriceText = doc.select("table.table_info tbody tr td").first().text();
            return Float.parseFloat(avgPriceText.replaceAll("[^\\d.]", "")) * 0.03f;
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return -1f; // Return -1 if there's an error
        }
    }

    private float fetchCardPriceB(String url) {
        try {
            // Fetch and parse the HTML document (this should ideally be done asynchronously)
            Document doc = Jsoup.connect(url).get();
            Element priceElement = doc.selectFirst(".item-price-wrap .item-price span[data-id^='makeshop-item-price']");
            return Float.parseFloat(priceElement.text().replaceAll("[^\\d.]", "")) * 0.03f;
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return -1f; // Return -1 if there's an error
        }    }

    private void calculatePriceDifference() {
        // Get current date and yesterday's date
        String currentDate = dateFormat.format(new Date());
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        // Retrieve the prices for the current day and yesterday
        float currentPrice = sharedPreferences.getFloat(currentDate, -1f);  // -1f means no value found
        float yesterdayPrice = sharedPreferences.getFloat(yesterdayDate, -1f);

        // 1. Total price for current day
        String totalPrice = decimalFormat.format(currentPrice);

        // 2. Difference between current day and yesterday
        float priceDifference = currentPrice - yesterdayPrice;
        String priceDifferenceFormatted;

        // 3. Percentage increase or decrease from yesterday
        float percentageChange = 0f;
        if (priceDifference != 0f) {
            percentageChange = (priceDifference / yesterdayPrice) * 100;
        }

        String symbol;
        int symbolColor;
        String percentageChangeFormatted;

        if (percentageChange > 0) {
            symbol = "▲";
            symbolColor = Color.GREEN;
            percentageChangeFormatted = decimalFormat.format(percentageChange);
            priceDifferenceFormatted = decimalFormat.format(priceDifference);
        }
        else if (percentageChange < 0) {
            percentageChange = -percentageChange;
            percentageChangeFormatted = decimalFormat.format(percentageChange);
            percentageChangeFormatted = "0" + percentageChangeFormatted;
            priceDifference = -priceDifference;
            priceDifferenceFormatted = decimalFormat.format(priceDifference);
            symbol = "▼";
            symbolColor = Color.RED;
        }
        else {
            symbol = "●";
            symbolColor = Color.parseColor("#FFD700");
            percentageChangeFormatted = "0.00";
            priceDifferenceFormatted = "0.00";
        }


        portfolioTotalValue.setText("MYR " + totalPrice);
        portfolioSubtitle.setText(symbol + " MYR " + priceDifferenceFormatted + " (" + percentageChangeFormatted + "%)");
        portfolioSubtitle.setTextColor(symbolColor);
    }
}