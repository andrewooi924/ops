package com.optcg.app.domain.model;

/**
 * A single point on a card's historical price trend. Kept free of any charting-library
 * type so the data layer doesn't depend on the UI; the chart screen maps these to its
 * own {@code Entry} objects.
 */
public class PriceTrendPoint {

    public final float x;
    public final float price;

    public PriceTrendPoint(float x, float price) {
        this.x = x;
        this.price = price;
    }
}
