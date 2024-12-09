package com.optcg.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
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


//        fetchCardPrice();

        cardAdapter = new CardAdapter(cardList);
        recyclerView.setAdapter(cardAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        return view;
    }

    private void fetchCardPrice() {
        getActivity().runOnUiThread(() -> {
//            tvFetch.setVisibility(View.VISIBLE);
//            priceContainer.setVisibility(View.GONE);
//            cardImage.setVisibility(View.GONE);
//            tvPrice.setVisibility(View.GONE);
        });

        // Allow network operation on the main thread for testing (not recommended for production)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        new Thread(() -> {
            try {
                Thread.sleep(500);

                // URL of the page to scrape
                String url = "https://onepiece-card-atari.jp/expansion/romance-dawn/card/op01-002/l-p";

                // Fetch and parse the HTML document
                Document doc = Jsoup.connect(url).get();

                String avgPrice = doc.select("table.table_info tbody tr td").first().text();

                // Select soaring and crash price elements
                Element soaringElement = doc.selectFirst(".movement_price_box .soaring");
                Element crashElement = doc.selectFirst(".movement_price_box .crash");

                // Extract and clean the text
                String soaringText = soaringElement != null ? soaringElement.text().replace("月間高騰差額", "").replace("+", "").trim() : "0円";
                String crashText = crashElement != null ? crashElement.text().replace("月間暴落差額", "").replace("-", "").trim() : "0円";

                // Update the UI
                getActivity().runOnUiThread(() -> {
                    updateUI(avgPrice, soaringText, crashText);
//                    tvFetch.setVisibility(View.GONE);
//                    priceContainer.setVisibility(View.VISIBLE);
//                    cardImage.setVisibility(View.VISIBLE);
//                    tvPrice.setVisibility(View.VISIBLE);
                });
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> showError("Error fetching price: " + e.getMessage()));
            }
        }).start();
    }

    private void updateUI(String avgPrice, String soaringText, String crashText) {
//        cardImage.setImageResource(R.drawable.op01_002_p1);
//        tvPrice.setText("AVG: RM" + parsePriceToInt(avgPrice) * 0.025 + " (" + avgPrice + ")");
//
//        // Clear the container
//        priceContainer.removeAllViews();

        // Parse prices as integers, handle exceptions for invalid values
        int soaringValue = parsePriceToInt(soaringText);
        int crashValue = parsePriceToInt(crashText);

        // Convert to RM by multiplying by 0.025
        String soaringInRM = formatAsRM(soaringValue * 0.025);
        String crashInRM = formatAsRM(crashValue * 0.025);

        // Determine what to display
        String symbol;
        int symbolColor;
        String displayText;

        if (soaringValue != 0) {
            symbol = "▲"; // Compact upward arrow
            symbolColor = Color.GREEN; // Green for upward arrow
            displayText = soaringInRM + " (" + soaringText + ")";
        } else if (crashValue != 0) {
            symbol = "▼"; // Compact downward arrow
            symbolColor = Color.RED; // Red for downward arrow
            displayText = crashInRM + " (" + crashText + ")";
        } else {
            symbol = "●";
            symbolColor = Color.parseColor("#FFD700"); // Golden yellow
            displayText = "RM0.00 (0円)";
        }

        // Create and style the symbol TextView
        TextView symbolTextView = new TextView(getContext());
        symbolTextView.setText(symbol);
        symbolTextView.setTextColor(symbolColor);
        symbolTextView.setTextSize(20);

        // Create and style the price TextView (default color, black or inherited)
        TextView priceTextView = new TextView(getContext());
        priceTextView.setText(displayText);
        priceTextView.setTextSize(18);

        // Add views to the container
//        priceContainer.addView(symbolTextView);
//        priceContainer.addView(priceTextView);
    }

    private int parsePriceToInt(String priceText) {
        try {
            // Remove non-numeric characters and parse the result as an integer
            String numericValue = priceText.replaceAll("[^0-9]", "");
            return Integer.parseInt(numericValue);
        } catch (NumberFormatException e) {
            // Return 0 if parsing fails
            return 0;
        }
    }

    private String formatAsRM(double value) {
        return String.format("RM%.2f", value);
    }

    private void showError(String errorMessage) {
        // Clear the container
//        priceContainer.removeAllViews();

        // Create and display an error message
        TextView errorTextView = new TextView(getContext());
        errorTextView.setText(errorMessage);
        errorTextView.setTextColor(Color.RED);
        errorTextView.setTextSize(18);
//        priceContainer.addView(errorTextView);
    }
}