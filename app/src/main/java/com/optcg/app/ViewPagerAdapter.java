package com.optcg.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    private List<Card> cards;
    private List<Integer> imageResources;
    private int randomInt;

    public ViewPagerAdapter(List<Card> cards, List<Integer> imageResources) {
        this.cards = cards;
        this.imageResources = imageResources;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewpager_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        randomInt = (int)(Math.random()*1373);
        Card card = cards.get(randomInt);
        int imageResourceId = holder.itemView.getContext().getResources()
                .getIdentifier(card.getId(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageView.setImageResource(imageResourceId);
        holder.tvId.setText(card.getNumber());
        holder.tvName.setText(card.getName());
        holder.tvRarity.setText(card.getRarity());
        holder.tvEffect.setText(card.getEffect());
        holder.tvSet.setText(card.getSet());
    }

    @Override
    public int getItemCount() {
        return imageResources.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvId, tvName, tvRarity, tvEffect, tvSet;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageCard);
            tvId = view.findViewById(R.id.tvId);
            tvName = view.findViewById(R.id.tvName);
            tvRarity = view.findViewById(R.id.tvRarity);
            tvEffect = view.findViewById(R.id.tvEffect);
            tvSet = view.findViewById(R.id.tvSet);
        }
    }
}
