package com.optcg.app;

public class PersonalCard {
    private int imageResource;
    private String url;
    private float initialPrice;
    private float realTimePrice = -1f;

    public PersonalCard(int imageResource, String url, float initialPrice) {
        this.imageResource = imageResource;
        this.url = url;
        this.initialPrice = initialPrice;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getUrl() {
        return url;
    }

    public float getInitialPrice() {
        return initialPrice;
    }

    public float getRealTimePrice() {
        return realTimePrice;
    }

    public void setRealTimePrice(float realTimePrice) {
        this.realTimePrice = realTimePrice;
    }
}

