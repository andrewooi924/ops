package com.optcg.app.domain.model;

import com.optcg.app.VendorPrice;

import java.util.List;

/**
 * The richer price data shown in the card price dialog: a historical trend plus the
 * current per-vendor listing. Fetched together (one network round-trip) by the remote
 * source.
 */
public class PriceDetail {

    public final List<PriceTrendPoint> trend;
    public final List<VendorPrice> vendors;

    public PriceDetail(List<PriceTrendPoint> trend, List<VendorPrice> vendors) {
        this.trend = trend;
        this.vendors = vendors;
    }
}
