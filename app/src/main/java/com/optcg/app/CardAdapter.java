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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.optcg.app.data.repository.PriceRepository;
import com.optcg.app.di.ServiceLocator;
import com.optcg.app.domain.model.PriceQuote;
import com.optcg.app.domain.result.Resource;
import com.optcg.app.ui.image.CardImageLoader;
import com.optcg.app.util.CurrencyConverter;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private final Context context;
    private final List<CardPrice> cardList;
    private final PriceRepository priceRepository;
    private final CardImageLoader cardImageLoader;

    public enum Mode {
        PORTFOLIO,
        WISHLIST
    }

    private final Mode mode;

    public CardAdapter(Context context, List<CardPrice> cardList, Mode mode) {
        this.context = context;
        this.cardList = cardList;
        this.mode = mode;
        this.priceRepository = ServiceLocator.get(context).priceRepository();
        this.cardImageLoader = ServiceLocator.get(context).cardImageLoader();
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
        int cardId = card.getImageResId();
        String cardPrefix = context.getResources().getResourceEntryName(cardId);

        // Set the card image (remote + Glide disk cache, bundled fallback by card id).
        cardImageLoader.loadById(cardPrefix, holder.cardImage);

        if (!cardPrefix.startsWith("st") && !cardPrefix.startsWith("p")) {
            holder.itemView.setOnClickListener(v -> {
                FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
                CardPriceDialogFragment dialogFragment = CardPriceDialogFragment.newInstance(
                        card.getImageResId(),
                        card.getUrl()
                );

                dialogFragment.show(fragmentManager, "cardPriceDialog");
            });
        } else {
            holder.itemView.setOnClickListener(null);
        }

        // Prices now come from the repository (offline-first Room cache + remote source) —
        // no scraping in the adapter.
        holder.cardAvgPrice.setText("Fetching...");
        holder.cardMovement.setText("");
        priceRepository.getQuote(card.getUrl(), result -> {
            if (result.data != null) {
                bindQuote(holder, result.data);
            } else if (result.status == Resource.Status.ERROR) {
                holder.cardAvgPrice.setText("Error fetching prices.");
            }
        });

        if (mode == Mode.WISHLIST) {
            ViewGroup.LayoutParams params = holder.cardImage.getLayoutParams();
            params.height = dpToPx(225);
            params.width = dpToPx(169);
            holder.cardImage.setLayoutParams(params);
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

    /** Renders a quote using the original display formatting (movement from soaring−crash diff). */
    private void bindQuote(CardViewHolder holder, PriceQuote quote) {
        String avgPriceInRM = CurrencyConverter.formatRm(Math.abs(CurrencyConverter.yenToRmDisplay(quote.avgYen)));
        int diff = quote.soaringYen - quote.crashYen;
        String diffInRM = CurrencyConverter.formatRm(Math.abs(CurrencyConverter.yenToRmDisplay(diff)));

        String movementText;
        String symbol;
        int symbolColor;

        if (diff > 0) {
            symbol = "▲";
            symbolColor = Color.GREEN;
            movementText = diffInRM + " (" + diff + "円)";
        } else if (diff < 0) {
            symbol = "▼";
            symbolColor = Color.RED;
            movementText = diffInRM + " (" + Math.abs(diff) + "円)";
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

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
