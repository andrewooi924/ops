package com.optcg.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.bumptech.glide.Glide;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    private Context context;
    private List<Integer> menuImages;
    private OnItemClickListener onItemClickListener;

    public CollectionAdapter(Context context, List<Integer> menuImages, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.menuImages = menuImages;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_collection_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String transitionName = "collectionImage_" + position;
        holder.menuImage.setTransitionName(transitionName);
        Glide.with(context)
                .load(menuImages.get(position))
                .into(holder.menuImage);
        holder.itemView.setOnClickListener(v ->
            onItemClickListener.onItemClick(position, holder.menuImage)
        );
    }

    @Override
    public int getItemCount() {
        return menuImages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView menuImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            menuImage = itemView.findViewById(R.id.menu_item_image);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ImageView sharedImageView);
    }
}