package com.optcg.app;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.optcg.app.data.repository.PriceRepository;
import com.optcg.app.di.ServiceLocator;
import com.optcg.app.domain.model.PriceQuote;
import com.optcg.app.domain.result.Resource;
import com.optcg.app.ui.image.CardImageLoader;
import com.optcg.app.util.CurrencyConverter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder> {
    private final Context context;
    private final List<Integer> deckImages;
    private boolean isFirstLoad = true;
    private final List<Integer> cardCounts;
    private final List<CardPrice> cardList;
    private final CardViewModel cardViewModel;
    private final PriceRepository priceRepository;
    private final CardImageLoader cardImageLoader;
    // Per-position yen contribution to the deck total, so offline-first re-emits don't double-count.
    private final Map<Integer, Double> contributions = Collections.synchronizedMap(new HashMap<>());

    public DeckAdapter(Context context, List<Integer> deckImages, List<Integer> cardCounts, List<CardPrice> cardList) {
        this.context = context;
        this.deckImages = deckImages;
        this.cardCounts = cardCounts;
        this.cardList = cardList;

        this.cardViewModel = new ViewModelProvider((FragmentActivity) context).get(CardViewModel.class);
        this.priceRepository = ServiceLocator.get(context).priceRepository();
        this.cardImageLoader = ServiceLocator.get(context).cardImageLoader();
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
        // Remote image + Glide disk cache, bundled fallback by card id.
        cardImageLoader.loadById(context.getResources().getResourceEntryName(deckImage), holder.deckImage);

        final int count = cardCounts.get(position);
        holder.deckImageCount.setText(String.valueOf(count));

        CardPrice cp = cardList.get(position);
        final int pos = position;
        holder.cardAvgPrice.setText("Fetching...");
        holder.cardMovement.setText("");
        // Prices come from the repository (offline-first cache + remote source) — no scraping here.
        priceRepository.getQuote(cp.getUrl(), result -> {
            if (result.data != null) {
                bindQuote(holder, result.data);
                contributions.put(pos, (double) result.data.avgYen * count);
                pushTotal();
            } else if (result.status == Resource.Status.ERROR) {
                holder.cardAvgPrice.setText("Error fetching prices.");
            }
        });

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

    /** Renders a quote using the deck display formatting (rate 0.025, soaring/crash shown separately). */
    private void bindQuote(ViewHolder holder, PriceQuote quote) {
        String avgPriceInRM = CurrencyConverter.formatRm(CurrencyConverter.yenToRmDeck(quote.avgYen));
        String soaringInRM = CurrencyConverter.formatRm(CurrencyConverter.yenToRmDeck(quote.soaringYen));
        String crashInRM = CurrencyConverter.formatRm(CurrencyConverter.yenToRmDeck(quote.crashYen));

        String movementText;
        String symbol;
        int symbolColor;

        if (!quote.soaringText.equals("0円")) {
            symbol = "▲";
            symbolColor = Color.GREEN;
            movementText = soaringInRM + " (" + quote.soaringText + ")";
        } else if (!quote.crashText.equals("0円")) {
            symbol = "▼";
            symbolColor = Color.RED;
            movementText = crashInRM + " (" + quote.crashText + ")";
        } else {
            symbol = "●";
            symbolColor = Color.parseColor("#FFD700");
            movementText = "RM0.00 (0円)";
        }

        holder.cardAvgPrice.setText("Avg: " + avgPriceInRM + " (" + quote.avgDisplayText + ")");

        SpannableString spannableMovement = new SpannableString(symbol + " " + movementText);
        spannableMovement.setSpan(new ForegroundColorSpan(symbolColor), 0, symbol.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.cardMovement.setText(spannableMovement);
    }

    private void pushTotal() {
        double total = 0;
        synchronized (contributions) {
            for (double v : contributions.values()) {
                total += v;
            }
        }
        cardViewModel.setTotalPrice(total);
    }
}
