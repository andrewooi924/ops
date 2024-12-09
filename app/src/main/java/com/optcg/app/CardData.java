package com.optcg.app;

public class CardData {
    private String avgPrice;
    private String soaringPrice;
    private String crashPrice;

    public CardData(String avgPrice, String soaringPrice, String crashPrice) {
        this.avgPrice = avgPrice;
        this.soaringPrice = soaringPrice;
        this.crashPrice = crashPrice;
    }

    // Getter for average price
    public String getAvgPrice() {
        return avgPrice;
    }

    // Setter for average price
    public void setAvgPrice(String avgPrice) {
        this.avgPrice = avgPrice;
    }

    // Getter for soaring price
    public String getSoaringPrice() {
        return soaringPrice;
    }

    // Setter for soaring price
    public void setSoaringPrice(String soaringPrice) {
        this.soaringPrice = soaringPrice;
    }

    // Getter for crash price
    public String getCrashPrice() {
        return crashPrice;
    }

    // Setter for crash price
    public void setCrashPrice(String crashPrice) {
        this.crashPrice = crashPrice;
    }
}
