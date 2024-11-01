package com.optcg.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.ViewHolder> {

    private Context context;
    private List<Integer> menuImages;
    private LruCache<String, Bitmap> imageCache;
    private boolean isFirstLoad = true;
    private final CardViewModel cardViewModel;

    public SetAdapter(Context context, List<Integer> menuImages) {
        this.context = context;
        this.menuImages = menuImages;
        this.cardViewModel = new ViewModelProvider((FragmentActivity) context).get(CardViewModel.class);
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        imageCache = new LruCache<>(cacheSize);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_set_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int drawableId = menuImages.get(position);
        Bitmap bitmap = getBitmapFromCache(String.valueOf(drawableId));

        if (bitmap != null) {
            holder.menuImage.setImageBitmap(bitmap);
        } else {
            bitmap = BitmapFactory.decodeResource(holder.itemView.getResources(), drawableId);
            addBitmapToCache(String.valueOf(drawableId), bitmap);
            holder.menuImage.setImageBitmap(bitmap);
        }
        String upCardId = context.getResources().getResourceEntryName(drawableId);
        boolean isCollected = isCardCollected(upCardId);
        int count = getCardCount(upCardId);
        if (isCollected) {
            holder.menuImage.setColorFilter(null);
        }
        else {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            holder.menuImage.setColorFilter(filter);
        }

        if (isFirstLoad) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_in_up);
            animation.setStartOffset(position * 50);
            holder.itemView.startAnimation(animation);
        }

        holder.itemView.setOnClickListener(v -> {
            int imageResId = menuImages.get(position);  // assuming menuImages is a list of image resource IDs
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
        return menuImages.size();
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
        public ImageView menuImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            menuImage = itemView.findViewById(R.id.menu_item_image);
        }
    }

    private void addBitmapToCache(String key, Bitmap bitmap) {
        if (getBitmapFromCache(key) == null) {
            imageCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromCache(String key) {
        return imageCache.get(key);
    }

    private boolean isCardCollected(String cardId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("OP01_PREFS", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(cardId + "_isCollected", false);
    }

    private int getCardCount(String cardId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("OP01_PREFS", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(cardId + "_count", 0);
    }
}