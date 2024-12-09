package com.optcg.app;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<CardPrice> cardList;
    private Map<String, CardData> cardDataCache = new HashMap<>();

    public CardAdapter(List<CardPrice> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardPrice card = cardList.get(position);

        // Set the card image
        holder.cardImage.setImageResource(card.getImageResId());

        CardData cardData = cardDataCache.get(card.getUrl());

        if (cardData != null) {
            updateCardView(holder, cardData);
        }
        else {
            // Display placeholder text while fetching
            holder.cardAvgPrice.setText("Fetching...");
            holder.cardMovement.setText("");

            // Fetch the prices for this card asynchronously
            fetchCardPrices(card.getUrl(), holder);
        }
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImage;
        TextView cardAvgPrice, cardMovement;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.cp_image);
            cardAvgPrice = itemView.findViewById(R.id.card_avg_price);
            cardMovement = itemView.findViewById(R.id.card_movement);
        }
    }

    private void fetchCardPrices(String url, CardViewHolder holder) {
        new Thread(() -> {
            try {
                // Fetch and parse the HTML document
                Document doc = Jsoup.connect(url).get();

                // Extract average price
                String avgPrice = doc.select("table.table_info tbody tr td").first().text();

                // Extract soaring and crash prices
                Element soaringElement = doc.selectFirst(".movement_price_box .soaring");
                Element crashElement = doc.selectFirst(".movement_price_box .crash");

                String soaringText = soaringElement != null ? soaringElement.text().replace("月間高騰差額", "").replace("+", "").trim() : "0円";
                String crashText = crashElement != null ? crashElement.text().replace("月間暴落差額", "").replace("-", "").trim() : "0円";

                // Update UI
                String soaringInRM = formatAsRM(parsePriceToInt(soaringText) * 0.025);
                String crashInRM = formatAsRM(parsePriceToInt(crashText) * 0.025);
                String avgPriceInRM = formatAsRM(parsePriceToInt(avgPrice) * 0.025);

                CardData cardData = new CardData(avgPrice, soaringText, crashText);
                cardDataCache.put(url, cardData);

                String movementText;
                String symbol;
                int symbolColor;

                if (!soaringText.equals("0円")) {
                    symbol = "▲";
                    symbolColor = Color.GREEN;
                    movementText = soaringInRM + " (" + soaringText + ")";
                } else if (!crashText.equals("0円")) {
                    symbol = "▼";
                    symbolColor = Color.RED;
                    movementText = crashInRM + " (" + crashText + ")";
                } else {
                    symbol = "●";
                    symbolColor = Color.parseColor("#FFD700");
                    movementText = "RM0.00 (0円)";
                }

                String finalAvgPrice = "Avg: " + avgPriceInRM + " (" + avgPrice + ")";
                String finalMovementText = movementText;
                int finalSymbolColor = symbolColor;
                String finalSymbol = symbol;

                // Update the UI on the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    holder.cardAvgPrice.setText(finalAvgPrice);

                    // Use SpannableString for the symbol and text
                    SpannableString spannableMovement = new SpannableString(finalSymbol + " " + finalMovementText);

                    // Apply color only to the symbol
                    spannableMovement.setSpan(new ForegroundColorSpan(finalSymbolColor), 0, finalSymbol.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    // Set the styled text
                    holder.cardMovement.setText(spannableMovement);
                });
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> holder.cardAvgPrice.setText("Error fetching prices."));
            }
        }).start();
    }

    private int parsePriceToInt(String priceText) {
        try {
            return Integer.parseInt(priceText.replace(",", "").replace("円", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String formatAsRM(double value) {
        return String.format("RM%.2f", value);
    }

    private void updateCardView(CardViewHolder holder, CardData cardData) {
        // Update the view using the cached card data
        String avgPriceInRM = formatAsRM(parsePriceToInt(cardData.getAvgPrice()) * 0.025);
        String soaringInRM = formatAsRM(parsePriceToInt(cardData.getSoaringPrice()) * 0.025);
        String crashInRM = formatAsRM(parsePriceToInt(cardData.getCrashPrice()) * 0.025);

        String movementText;
        String symbol;
        int symbolColor;

        if (!cardData.getSoaringPrice().equals("0円")) {
            symbol = "▲";
            symbolColor = Color.GREEN;
            movementText = soaringInRM + " (" + cardData.getSoaringPrice() + ")";
        } else if (!cardData.getCrashPrice().equals("0円")) {
            symbol = "▼";
            symbolColor = Color.RED;
            movementText = crashInRM + " (" + cardData.getCrashPrice() + ")";
        } else {
            symbol = "●";
            symbolColor = Color.parseColor("#FFD700");
            movementText = "RM0.00 (0円)";
        }

        String finalAvgPrice = "Avg: " + avgPriceInRM + " (" + cardData.getAvgPrice() + ")";
        String finalMovementText = movementText;

        // Update the UI
        new Handler(Looper.getMainLooper()).post(() -> {
            holder.cardAvgPrice.setText(finalAvgPrice);

            // Use SpannableString for the symbol and text
            SpannableString spannableMovement = new SpannableString(symbol + " " + finalMovementText);

            // Apply color only to the symbol
            spannableMovement.setSpan(new ForegroundColorSpan(symbolColor), 0, symbol.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Set the styled text
            holder.cardMovement.setText(spannableMovement);
        });
    }
}