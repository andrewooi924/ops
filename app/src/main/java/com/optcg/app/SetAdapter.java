package com.optcg.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.ViewHolder> {

    private Context context;
    private List<Integer> menuImages;
    private LruCache<String, Bitmap> imageCache;
    private boolean isFirstLoad = true;

    public SetAdapter(Context context, List<Integer> menuImages) {
        this.context = context;
        this.menuImages = menuImages;
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
//            holder.menuImage.setImageResource(drawableId);
            bitmap = BitmapFactory.decodeResource(holder.itemView.getResources(), drawableId);
            addBitmapToCache(String.valueOf(drawableId), bitmap);
            holder.menuImage.setImageBitmap(bitmap);
        }

        if (isFirstLoad) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_in_up);
            animation.setStartOffset(position * 50);
            holder.itemView.startAnimation(animation);
        }
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
}