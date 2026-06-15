package com.optcg.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Per-set configuration for a collection screen (the OPxxCollectionFragment grids).
 * Captures the layout/view ids, the full card image list, the overall completion
 * denominator and the per-rarity progress rows. {@link BaseSetCollectionFragment}
 * renders identical UI to the original hand-written fragments from this data.
 */
public class SetCollectionConfig {

    /** A single "collected X / total" progress row. */
    public static class Row {
        public final int viewId;       // e.g. R.id.op01CCollected
        public final String keySuffix; // e.g. "c" -> reads prefs key "<prefix>_total_c"
        public final int denominator;  // e.g. 49

        public Row(int viewId, String keySuffix, int denominator) {
            this.viewId = viewId;
            this.keySuffix = keySuffix;
            this.denominator = denominator;
        }
    }

    public final String prefix;          // e.g. "op01"
    public final int layoutResId;
    public final int setImgId;
    public final int recyclerViewId;
    public final int progressCircleId;
    public final int progressTextId;
    public final int totalDenominator;   // used for the completion percentage
    public final List<Integer> menuImages;
    public final List<Row> rows;

    private SetCollectionConfig(Builder b) {
        this.prefix = b.prefix;
        this.layoutResId = b.layoutResId;
        this.setImgId = b.setImgId;
        this.recyclerViewId = b.recyclerViewId;
        this.progressCircleId = b.progressCircleId;
        this.progressTextId = b.progressTextId;
        this.totalDenominator = b.totalDenominator;
        this.menuImages = b.menuImages;
        this.rows = b.rows;
    }

    public static class Builder {
        private final String prefix;
        private final int layoutResId;
        private final int setImgId;
        private final int recyclerViewId;
        private final int progressCircleId;
        private final int progressTextId;
        private final int totalDenominator;
        private List<Integer> menuImages = new ArrayList<>();
        private final List<Row> rows = new ArrayList<>();

        public Builder(String prefix, int layoutResId, int setImgId, int recyclerViewId,
                       int progressCircleId, int progressTextId, int totalDenominator) {
            this.prefix = prefix;
            this.layoutResId = layoutResId;
            this.setImgId = setImgId;
            this.recyclerViewId = recyclerViewId;
            this.progressCircleId = progressCircleId;
            this.progressTextId = progressTextId;
            this.totalDenominator = totalDenominator;
        }

        public Builder menuImages(List<Integer> images) {
            this.menuImages = images;
            return this;
        }

        public Builder row(int viewId, String keySuffix, int denominator) {
            this.rows.add(new Row(viewId, keySuffix, denominator));
            return this;
        }

        public SetCollectionConfig build() {
            return new SetCollectionConfig(this);
        }
    }
}
