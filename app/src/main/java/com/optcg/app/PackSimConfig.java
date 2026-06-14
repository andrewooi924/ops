package com.optcg.app;

/**
 * Per-set configuration for a pack-opening simulator. All the variation between the
 * OPxxSimActivity screens is data: the card pools, the prefs key prefix, the layout
 * and pack art, whether the L tier splits 50/50 into an "SP" alternate, and whether
 * the set has an extra top-tier "ultra" rarity (e.g. ODA / GSP).
 *
 * <p>{@link BasePackSimActivity} consumes this config and reproduces the exact gacha
 * behaviour the individual activities used to hand-code.
 */
public class PackSimConfig {

    public final String prefix;        // e.g. "op01" — drives COLLECTION_PREFS keys
    public final int layoutResId;      // e.g. R.layout.activity_op01_sim
    public final int packDrawable;     // e.g. R.drawable.op01_pack
    public final float packScale;      // per-set pack art scale (default 1.35)

    public final int[] cCards;
    public final int[] ucCards;
    public final int[] rCards;
    public final int[] srCards;
    public final int[] aarCards;
    public final int[] aasrCards;
    public final int[] aasecCards;
    public final int[] aalCards;
    public final int[] secCards;
    public final int[] mrCards;
    public final int[] lCards;

    /** Non-null enables the L-tier 50/50 split into an "SP" rarity. */
    public final int[] spCards;
    /** Non-null enables an extra top-tier rarity drawn at 1/11520 (and pity %11520). */
    public final int[] ultraCards;
    /** Name of the ultra rarity, e.g. "ODA" or "GSP". Non-null iff ultraCards is set. */
    public final String ultraRarity;

    private PackSimConfig(Builder b) {
        this.prefix = b.prefix;
        this.layoutResId = b.layoutResId;
        this.packDrawable = b.packDrawable;
        this.packScale = b.packScale;
        this.cCards = b.cCards;
        this.ucCards = b.ucCards;
        this.rCards = b.rCards;
        this.srCards = b.srCards;
        this.aarCards = b.aarCards;
        this.aasrCards = b.aasrCards;
        this.aasecCards = b.aasecCards;
        this.aalCards = b.aalCards;
        this.secCards = b.secCards;
        this.mrCards = b.mrCards;
        this.lCards = b.lCards;
        this.spCards = b.spCards;
        this.ultraCards = b.ultraCards;
        this.ultraRarity = b.ultraRarity;
    }

    public boolean hasSpSplit() {
        return spCards != null;
    }

    public boolean hasUltra() {
        return ultraCards != null && ultraRarity != null;
    }

    public static class Builder {
        private final String prefix;
        private final int layoutResId;
        private final int packDrawable;
        private float packScale = 1.35f;

        private int[] cCards = new int[0];
        private int[] ucCards = new int[0];
        private int[] rCards = new int[0];
        private int[] srCards = new int[0];
        private int[] aarCards = new int[0];
        private int[] aasrCards = new int[0];
        private int[] aasecCards = new int[0];
        private int[] aalCards = new int[0];
        private int[] secCards = new int[0];
        private int[] mrCards = new int[0];
        private int[] lCards = new int[0];
        private int[] spCards = null;
        private int[] ultraCards = null;
        private String ultraRarity = null;

        public Builder(String prefix, int layoutResId, int packDrawable) {
            this.prefix = prefix;
            this.layoutResId = layoutResId;
            this.packDrawable = packDrawable;
        }

        public Builder packScale(float v) { this.packScale = v; return this; }

        public Builder c(int[] v) { this.cCards = v; return this; }
        public Builder uc(int[] v) { this.ucCards = v; return this; }
        public Builder r(int[] v) { this.rCards = v; return this; }
        public Builder sr(int[] v) { this.srCards = v; return this; }
        public Builder aar(int[] v) { this.aarCards = v; return this; }
        public Builder aasr(int[] v) { this.aasrCards = v; return this; }
        public Builder aasec(int[] v) { this.aasecCards = v; return this; }
        public Builder aal(int[] v) { this.aalCards = v; return this; }
        public Builder sec(int[] v) { this.secCards = v; return this; }
        public Builder mr(int[] v) { this.mrCards = v; return this; }
        public Builder l(int[] v) { this.lCards = v; return this; }
        public Builder sp(int[] v) { this.spCards = v; return this; }
        public Builder ultra(String rarity, int[] v) { this.ultraRarity = rarity; this.ultraCards = v; return this; }

        public PackSimConfig build() {
            return new PackSimConfig(this);
        }
    }
}
