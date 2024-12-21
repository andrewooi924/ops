package com.optcg.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder> {
    private Context context;
    private List<Integer> deckImages;
    private LruCache<String, Bitmap> imageCache;
    private boolean isFirstLoad = true;
    private List<Integer> cardCounts;
    private List<CardPrice> cardList;
    private Map<String, CardData> cardDataCache = new HashMap<>();
    private final CardViewModel cardViewModel;
    private final List<Double> priceList = Collections.synchronizedList(new ArrayList<>());
    private double totalPrice = 0;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public DeckAdapter(Context context, List<Integer> deckImages, List<Integer> cardCounts, List<CardPrice> cardList) {
        this.context = context;
        this.deckImages = deckImages;
        this.cardCounts = cardCounts;
        this.cardList = cardList;

        this.cardViewModel = new ViewModelProvider((FragmentActivity) context).get(CardViewModel.class);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_deck_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int deckImage = deckImages.get(position);
        Bitmap bitmap = getBitmapFromCache(String.valueOf(deckImage));

        int count = cardCounts.get(position);
        holder.deckImageCount.setText(String.valueOf(count));

        CardPrice cp = cardList.get(position);
        CardData cardData = cardDataCache.get(cp.getUrl());
        if (cardData != null) {
            updateCardView(holder, cardData);
        }
        else {
            // Display placeholder text while fetching
            holder.cardAvgPrice.setText("Fetching...");
            holder.cardMovement.setText("");

            // Fetch the prices for this card asynchronously
            fetchCardPrices(cp.getUrl(), holder, context.getResources().getResourceEntryName(deckImage), count);
        }

        if (bitmap != null) {
            holder.deckImage.setImageBitmap(bitmap);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), deckImage);
            imageCache.put(String.valueOf(deckImage), bitmap);
            holder.deckImage.setImageBitmap(bitmap);
        }

        if (isFirstLoad) {
            holder.itemView.animate().alpha(1).setDuration(300).start();
            isFirstLoad = false;
        }

        holder.itemView.setOnClickListener(v -> {
            int imageResId = deckImages.get(position);  // assuming menuImages is a list of image resource IDs
            String cardId = context.getResources().getResourceEntryName(imageResId); // get the name of the image resource
            cardViewModel.getCardById(cardId).observe((FragmentActivity) context, card -> {
                if (card != null) {
                    String cardName = card.getName(); // replace with actual card name from data
                    String cardNumber = "ID: " + card.getNumber(); // replace with actual card number from data
                    String cardRarity = "Rarity: " + card.getRarity(); // replace with actual card rarity from data
                    String cardRole = "Role: " + card.getRole(); // replace with actual card role from data
                    String cardCost = "Cost: " + card.getCost(); // replace with actual card cost from data
                    if (card.getRarity().equals("L")) {
                        cardCost = "Life: " + card.getLife();
                    }
                    String cardAttribute = "Attribute: " + card.getAttribute(); // replace with actual card attribute from data
                    String cardPower = "Power: " + card.getPower(); // replace with actual card power from data
                    String cardCounter = "Counter: " + card.getCounter(); // replace with actual card counter from data
                    String cardColor = "Color: " + card.getColor(); // replace with actual card color from data
                    String cardType = "Type: " + card.getType(); // replace with actual card type from data
                    String cardEffect = "Effect: " + card.getEffect(); // replace with actual card effect from data
                    String cardSet = "Set: " + card.getSet(); // replace with actual card set from data
                    String cardCount = "Number of Duplicates: " + 0; // replace with actual card count from data
                    if (count > 1) {
                        cardCount = "Number of Duplicates: " + (count - 1); // replace with actual card count from data"
                    }

                    CardDetailsDialogFragment dialogFragment = CardDetailsDialogFragment.newInstance(imageResId, cardName, cardNumber, cardRarity, cardRole, cardCost, cardAttribute, cardPower, cardCounter, cardColor, cardType, cardEffect, cardSet, cardCount);
                    dialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "cardDetails");                }
            });

        });
    }

    @Override
    public int getItemCount() {
        return deckImages.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        // Load up until last visible card on entering fragment
        if (isFirstLoad && holder.getAdapterPosition() == 11) {
            isFirstLoad = false;
        }
    }

    private Bitmap getBitmapFromCache(String key) {
        return imageCache.get(key);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView deckImage;
        public TextView deckImageCount, cardAvgPrice, cardMovement;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deckImage = itemView.findViewById(R.id.deck_image);
            deckImageCount = itemView.findViewById(R.id.deck_image_count);
            cardAvgPrice = itemView.findViewById(R.id.dc_avg);
            cardMovement = itemView.findViewById(R.id.dc_movement);
        }
    }

    private void fetchCardPrices(String url, DeckAdapter.ViewHolder holder, String prefix, int count) {

        CountDownLatch latch = new CountDownLatch(1);

        if (prefix.startsWith("st")) {
            executorService.submit(() -> fetchCardPricesB(url, holder, latch, count));
        }
        else {
            executorService.submit(() -> fetchCardPricesA(url, holder, latch, count));
        }

        try {
            latch.await();
            new Handler(Looper.getMainLooper()).post(() -> {
                cardViewModel.setTotalPrice(totalPrice);
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void fetchCardPricesA(String url, DeckAdapter.ViewHolder holder, CountDownLatch latch, int count) {
        new Thread(() -> {
            try {
                // Fetch and parse the HTML document
                Document doc = Jsoup.connect(url).get();

                // Extract average price
                String avgPrice = doc.select("table.table_info tbody tr td").first().text();
                double avgPriceA = Double.parseDouble(avgPrice.replaceAll("[^\\d]", "")) * count;
                priceList.add(avgPriceA);
                addToTotalPrice(avgPriceA);

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
            } finally {
                latch.countDown();
            }
        }).start();
    }

    private void fetchCardPricesB(String url, DeckAdapter.ViewHolder holder, CountDownLatch latch, int count) {
        new Thread(() -> {
            try {
                // Fetch and parse the HTML document
                Document doc = Jsoup.connect(url).get();

                // Extract the price from the item-price span
                Element priceElement = doc.selectFirst(".item-price-wrap .item-price span[data-id^='makeshop-item-price']");
                String avgPrice = priceElement != null ? priceElement.text().replaceAll("[^\\d]", "") : "0"; // Extract numeric value (removing non-numeric characters)
                double avgPriceB = Double.parseDouble(avgPrice) * count;
                priceList.add(avgPriceB);
                addToTotalPrice(avgPriceB);

                // Process and convert average price into RM format
                String avgPriceInRM = formatAsRM(parsePriceToInt(avgPrice) * 0.025);

                // Create CardData object for caching (so we can use it later if needed)
                CardData cardData = new CardData(avgPrice, "0円", "0円");
                cardDataCache.put(url, cardData);

                // Set a placeholder for no movement (since soaring and crashing are not being used)
                String movementText = "RM0.00 (0円)";
                String symbol = "●";  // No movement symbol
                int symbolColor = Color.parseColor("#FFD700");  // Gold color for no movement

                // Final formatted average price and movement text
                String finalAvgPrice = "Avg: " + avgPriceInRM + " (" + avgPrice + "円)";
                String finalMovementText = movementText;

                // Update the UI on the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    // Update the average price
                    holder.cardAvgPrice.setText(finalAvgPrice);

                    // Use SpannableString for the symbol and text
                    SpannableString spannableMovement = new SpannableString(symbol + " " + finalMovementText);

                    // Apply color only to the symbol
                    spannableMovement.setSpan(new ForegroundColorSpan(symbolColor), 0, symbol.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    // Set the styled text for movement
                    holder.cardMovement.setText(spannableMovement);
                });

            } catch (Exception e) {
                // If there is an error fetching or parsing, display an error message
                new Handler(Looper.getMainLooper()).post(() -> holder.cardAvgPrice.setText("Error fetching prices."));
            } finally {
                latch.countDown();
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

    private void updateCardView(DeckAdapter.ViewHolder holder, CardData cardData) {
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

    // Add price to the total
    private void addToTotalPrice(double price) {
        totalPrice += price;

        // Ensure the UI update is done on the main thread
        new Handler(Looper.getMainLooper()).post(() -> {
            // Update the ViewModel with the new total
            cardViewModel.setTotalPrice(totalPrice);
        });
    }
}
