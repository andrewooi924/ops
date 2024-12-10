package com.optcg.app;

public class VendorPrice {
    private String vendorName;
    private float price;

    public VendorPrice(String vendorName, float price) {
        this.vendorName = vendorName;
        this.price = price;
    }

    public String getVendorName() {
        return vendorName;
    }

    public float getPrice() {
        return price;
    }
}
