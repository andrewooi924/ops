package com.optcg.app;

public class CardPrice {
    private int imageResId;
    private String url;

    public CardPrice(int imageResId, String url) {
        this.imageResId = imageResId;
        this.url = url;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getUrl() {
        return url;
    }
}
