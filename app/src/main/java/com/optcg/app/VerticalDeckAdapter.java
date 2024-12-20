package com.optcg.app;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerticalDeckAdapter extends RecyclerView.Adapter<VerticalDeckAdapter.VerticalDeckViewHolder> {

    private static final Map<String, Integer> COLOR_MAP = new HashMap<>();
    private final Context context;
    private final List<ColorData> data;
    private HorizontalDeckAdapter.OnImageClickListener onImageClickListener;

    public VerticalDeckAdapter(Context context, List<ColorData> data, HorizontalDeckAdapter.OnImageClickListener onImageClickListener) {
        this.context = context;
        this.data = data;
        this.onImageClickListener = onImageClickListener;

        COLOR_MAP.put("赤", ContextCompat.getColor(context, R.color.card_red));     // Red
        COLOR_MAP.put("緑", ContextCompat.getColor(context, R.color.card_green));   // Green
        COLOR_MAP.put("青", ContextCompat.getColor(context, R.color.card_blue));    // Blue
        COLOR_MAP.put("紫", ContextCompat.getColor(context, R.color.card_purple)); // Purple
        COLOR_MAP.put("黒", ContextCompat.getColor(context, R.color.card_black));   // Black
        COLOR_MAP.put("黄", ContextCompat.getColor(context, R.color.card_yellow));  // Yellow
    }

    @NonNull
    @Override
    public VerticalDeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vertical_deck, parent, false);
        return new VerticalDeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalDeckViewHolder holder, int position) {
        // Get the drawable list for the current row
        ColorData horizontalData = data.get(position);

        // Set the title for the row
        holder.rowTitle.setText(getColoredTitle(horizontalData.getTitle()));

        // Set up the horizontal RecyclerView
        HorizontalDeckAdapter horizontalAdapter = new HorizontalDeckAdapter(context, horizontalData.getDrawables(), horizontalData.getTitle(), new HorizontalDeckAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(int position, String color, ImageView sharedImageView) {
                onImageClickListener.onImageClick(position, horizontalData.getTitle(), sharedImageView);
            }
        });
        holder.horizontalRecyclerView.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        );
        holder.horizontalRecyclerView.setAdapter(horizontalAdapter);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private CharSequence getColoredTitle(String title) {
        // Split the title by "/" to handle color combinations
        if (title.contains("•")) {
            SpannableStringBuilder builder = new SpannableStringBuilder();

            String[] parts = title.split("•");
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i].trim(); // Trim spaces around colors

                // Append the part with the corresponding color
                if (COLOR_MAP.containsKey(part)) {
                    builder.append(part, new ForegroundColorSpan(COLOR_MAP.get(part)), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    builder.append(part); // Append as-is if no color mapping exists
                }

                // Add "/" separator (except after the last part)
                if (i < parts.length - 1) {
                    builder.append("•");
                }
            }

            return builder;
        } else {
            // Handle single color titles or default titles
            if (COLOR_MAP.containsKey(title)) {
                SpannableString spannableTitle = new SpannableString(title);
                spannableTitle.setSpan(new ForegroundColorSpan(COLOR_MAP.get(title)), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return spannableTitle;
            } else {
                // Default behavior for titles without known color names
                return title;
            }
        }
    }

    public static class VerticalDeckViewHolder extends RecyclerView.ViewHolder {
        TextView rowTitle;
        RecyclerView horizontalRecyclerView;

        public VerticalDeckViewHolder(@NonNull View itemView) {
            super(itemView);
            rowTitle = itemView.findViewById(R.id.rowTitle);
            horizontalRecyclerView = itemView.findViewById(R.id.horizontalRecyclerView);
        }
    }
}