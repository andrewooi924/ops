package com.optcg.app;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.MenuViewHolder> {

    private List<String> menuItems;
    private List<Integer> menuImages;
    private Context context;
    private FragmentManager fragmentManager;

    public MainMenuAdapter(FragmentManager fragmentManager, Context context, List<String> menuItems, List<Integer> menuImages) {
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.menuItems = menuItems;
        this.menuImages = menuImages;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_menu_adapter, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        holder.menuImage.setImageResource(menuImages.get(position));

        // Set up the click listener to open the desired activity
        holder.itemView.setOnClickListener(v -> {
            if (position == 0) {
                Intent intent = new Intent(context, OP01SimActivity.class);
                context.startActivity(intent);
            }
            else if (position == 1) {
                Intent intent = new Intent(context, OP02SimActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {

        public ImageView menuImage;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            menuImage = itemView.findViewById(R.id.menu_item_image);
        }
    }
}