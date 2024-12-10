package com.optcg.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VendorPriceAdapter extends RecyclerView.Adapter<VendorPriceAdapter.VendorViewHolder> {
    private List<VendorPrice> vendorPrices;

    public VendorPriceAdapter(List<VendorPrice> vendorPrices) {
        this.vendorPrices = vendorPrices;
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendor_price, parent, false);
        return new VendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
        VendorPrice vendorPrice = vendorPrices.get(position);
        holder.vendorName.setText(vendorPrice.getVendorName());
        double vendorPriceMYR = vendorPrice.getPrice() * 0.025;
        holder.price.setText(String.format("RM%.2f (¥%,.0f)", vendorPriceMYR, vendorPrice.getPrice()));
    }

    @Override
    public int getItemCount() {
        return vendorPrices.size();
    }

    static class VendorViewHolder extends RecyclerView.ViewHolder {
        TextView vendorName, price;

        public VendorViewHolder(@NonNull View itemView) {
            super(itemView);
            vendorName = itemView.findViewById(R.id.vendor_name);
            price = itemView.findViewById(R.id.vendor_price);
        }
    }
}
