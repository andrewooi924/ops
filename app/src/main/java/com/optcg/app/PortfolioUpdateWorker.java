package com.optcg.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PortfolioUpdateWorker extends Worker {

    private List<PersonalCard> personalCardList;

    public PortfolioUpdateWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        personalCardList = new ArrayList<>();
        personalCardList.add(new PersonalCard(R.drawable.op01_031_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-031/l-p", 30.0f));
        personalCardList.add(new PersonalCard(R.drawable.op01_062_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-062/l-p", 60.0f));
        personalCardList.add(new PersonalCard(R.drawable.op01_091_p1, "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-091/l-p", 30.0f));
        personalCardList.add(new PersonalCard(R.drawable.op02_001_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-001/l-p", 0.0f));
        personalCardList.add(new PersonalCard(R.drawable.op02_025_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/p02-025/l-p", 30.0f));
        personalCardList.add(new PersonalCard(R.drawable.op02_026_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-026/l-p", 59.4f));
        personalCardList.add(new PersonalCard(R.drawable.op02_071_p1, "https://onepiece-card-atari.jp/expansion/paramount-war/card/op02-071/l-p", 30.0f));
        personalCardList.add(new PersonalCard(R.drawable.op03_021_p1, "https://onepiece-card-atari.jp/expansion/mighty-enemies/card/op03-021/l-p", 0.0f));
        personalCardList.add(new PersonalCard(R.drawable.op04_020_p1, "https://onepiece-card-atari.jp/expansion/kingdoms-of-intrigue/card/op04-020/l-p", 18.0f));
        personalCardList.add(new PersonalCard(R.drawable.op05_001_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-001/l-p", 32.4f));
        personalCardList.add(new PersonalCard(R.drawable.op05_002_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-002/l-p", 28.5f));
        personalCardList.add(new PersonalCard(R.drawable.op05_022_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-022/l-p", 20.4f));
        personalCardList.add(new PersonalCard(R.drawable.op05_041_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-041/l-p", 18.0f));
        personalCardList.add(new PersonalCard(R.drawable.op05_060_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-060/l-p", 84.0f));
        personalCardList.add(new PersonalCard(R.drawable.op05_098_p1, "https://onepiece-card-atari.jp/expansion/awakening-of-the-new-era/card/op05-098/l-p", 48.0f));
        personalCardList.add(new PersonalCard(R.drawable.op06_001_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-001/l-p", 98.4f));
        personalCardList.add(new PersonalCard(R.drawable.op06_020_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-020/l-p", 16.8f));
        personalCardList.add(new PersonalCard(R.drawable.op06_021_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-021/l-p", 77.1f));
        personalCardList.add(new PersonalCard(R.drawable.op06_042_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-042/l-p", 105.0f));
        personalCardList.add(new PersonalCard(R.drawable.op06_080_p1, "https://onepiece-card-atari.jp/expansion/wings-of-captain/card/op06-080/l-p", 30.0f));
        personalCardList.add(new PersonalCard(R.drawable.op07_001_p1, "https://onepiece-card-atari.jp/expansion/500-yeas-in-the-future/card/op07-001/l-p", 18.0f));
        personalCardList.add(new PersonalCard(R.drawable.op07_059_p1, "https://onepiece-card-atari.jp/expansion/500-yeas-in-the-future/card/op07-059/l-p", 18.0f));
        personalCardList.add(new PersonalCard(R.drawable.op08_001_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-001/l-p", 26.4f));
        personalCardList.add(new PersonalCard(R.drawable.op08_002_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-002/l-p", 32.4f));
        personalCardList.add(new PersonalCard(R.drawable.op08_021_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-021/l-p", 20.4f));
        personalCardList.add(new PersonalCard(R.drawable.op08_057_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-057/l-p", 26.4f));
        personalCardList.add(new PersonalCard(R.drawable.op08_058_p1, "https://onepiece-card-atari.jp/expansion/two-legends/card/op08-058/l-p", 72.0f));
        personalCardList.add(new PersonalCard(R.drawable.op09_022_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-022/l-p", 27.0f));
        personalCardList.add(new PersonalCard(R.drawable.op09_042_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-042/l-p", 32.4f));
        personalCardList.add(new PersonalCard(R.drawable.op09_061_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-061/l-p", 83.4f));
        personalCardList.add(new PersonalCard(R.drawable.op09_062_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-062/l-p", 28.5f));
        personalCardList.add(new PersonalCard(R.drawable.op09_081_p1, "https://onepiece-card-atari.jp/expansion/emperors-in-the-new-world/card/op09-081/l-p", 56.4f));
        personalCardList.add(new PersonalCard(R.drawable.op10_002_p1, "https://onepiece-card-atari.jp/expansion/royal-blood/card/op10-002/l-p", 14.4f));
        personalCardList.add(new PersonalCard(R.drawable.prb01_001_p1, "https://onepiece-card-atari.jp/expansion/one-piece-card-the-best/card/prb01-001/l-p", 60.0f));
        personalCardList.add(new PersonalCard(R.drawable.eb01_021_p1, "https://onepiece-card-atari.jp/expansion/memorial-collection/card/eb01-021/l-p", 15.0f));
        personalCardList.add(new PersonalCard(R.drawable.eb01_040_p1, "https://onepiece-card-atari.jp/expansion/memorial-collection/card/eb01-040/l-p", 15.0f));

        // PROMOS
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

        // LUFFY DON
        personalCardList.add(new PersonalCard(R.drawable.op01_001_p1, "https://tier-one-onepiece.jp/view/item/000000000835", 7.44f));

        // ZORO DON
        personalCardList.add(new PersonalCard(R.drawable.op01_001_p1, "https://tier-one-onepiece.jp/view/item/000000000836", 7.44f));

        // USOPP DON
        personalCardList.add(new PersonalCard(R.drawable.op01_001_p1, "https://tier-one-onepiece.jp/view/item/000000000837", 7.44f));

        // SANJI DON
        personalCardList.add(new PersonalCard(R.drawable.op01_001_p1, "https://tier-one-onepiece.jp/view/item/000000000839", 7.44f));

        // NAMI DON
        personalCardList.add(new PersonalCard(R.drawable.op01_001_p1, "https://tier-one-onepiece.jp/view/item/000000000838", 7.44f));

        // CHOPPER DON
        personalCardList.add(new PersonalCard(R.drawable.op01_001_p1, "https://tier-one-onepiece.jp/view/item/000000000840", 7.44f));

        // ROBIN DON
        personalCardList.add(new PersonalCard(R.drawable.op01_001_p1, "https://tier-one-onepiece.jp/view/item/000000000841", 7.44f));

        // FRANKY DON
        personalCardList.add(new PersonalCard(R.drawable.op01_001_p1, "https://tier-one-onepiece.jp/view/item/000000000842", 7.44f));

        // BROOK DON
        personalCardList.add(new PersonalCard(R.drawable.op01_001_p1, "https://tier-one-onepiece.jp/view/item/000000000843", 7.44f));

        // JINBE DON
        personalCardList.add(new PersonalCard(R.drawable.op01_001_p1, "https://tier-one-onepiece.jp/view/item/000000000844", 7.44f));
    }

    @NonNull
    @Override
    public Result doWork() {
        updatePortfolioTotalValue();
        return Result.success();
    }

    private void updatePortfolioTotalValue() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("PortfolioData", Context.MODE_PRIVATE);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        float totalValue = calculateTotalValue();

        sharedPreferences.edit()
                .putFloat(today, totalValue)
                .apply();
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
        }
    }
}
