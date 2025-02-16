package com.optcg.app;

public class CardPrice {
    private int imageResId;
    private String url;
    private int price;

    public CardPrice(int imageResId, String url) {
        this.imageResId = imageResId;
        this.url = url;
        this.price = 0;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getUrl() {
        return url;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
