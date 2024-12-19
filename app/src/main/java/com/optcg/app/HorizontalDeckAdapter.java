package com.optcg.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HorizontalDeckAdapter extends RecyclerView.Adapter<HorizontalDeckAdapter.HorizontalDeckViewHolder> {

    private final Context context;
    private final List<Integer> drawableIds;
    private final String color;
    private OnImageClickListener onImageClickListener;

    public HorizontalDeckAdapter(Context context, List<Integer> drawableIds, String color, OnImageClickListener onImageClickListener) {
        this.context = context;
        this.drawableIds = drawableIds; // List of drawable resource IDs
        this.color = color;
        this.onImageClickListener = onImageClickListener;
    }

    @NonNull
    @Override
    public HorizontalDeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_horizontal_deck, parent, false);
        return new HorizontalDeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalDeckViewHolder holder, int position) {
//        int drawableId = drawableIds.get(position);
//        holder.imageView.setImageResource(drawableId); // Set the drawable resource

        String transitionName = "image_" + position;
        holder.imageView.setTransitionName(transitionName);
        holder.imageView.setImageResource(drawableIds.get(position));
        holder.itemView.setOnClickListener(v ->
            onImageClickListener.onImageClick(position, color, holder.imageView)
        );
    }

    @Override
    public int getItemCount() {
        return drawableIds.size();
    }

    public static class HorizontalDeckViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public HorizontalDeckViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewHorizontal);
        }
    }

    public interface OnImageClickListener {
        void onImageClick(int position, String color, ImageView sharedImageView);
    }
}