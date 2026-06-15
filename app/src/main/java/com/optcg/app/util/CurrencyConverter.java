package com.optcg.app.util;

/**
 * Centralizes the yen → RM conversion that was previously hard-coded as magic
 * multipliers scattered across the UI.
 *
 * <p>NOTE: the two rates are intentionally different and are preserved exactly as
 * they were in the original code — the card/portfolio price views used {@code * 0.03}
 * while the deck and vendor views used {@code * 0.025}. They are kept separate on
 * purpose; unifying them would change the numbers shown to users.
 */
public final class CurrencyConverter {

    /** Rate used by card price / portfolio views (was {@code * 0.03}). */
    public static final double YEN_TO_RM_DISPLAY = 0.03;

    /** Rate used by deck and vendor price views (was {@code * 0.025}). */
    public static final double YEN_TO_RM_DECK = 0.025;

    private CurrencyConverter() {
    }

    public static double yenToRmDisplay(double yen) {
        return yen * YEN_TO_RM_DISPLAY;
    }

    public static double yenToRmDeck(double yen) {
        return yen * YEN_TO_RM_DECK;
    }

    /** Formats a RM value, e.g. {@code RM12.34}. Callers apply {@code Math.abs} where needed. */
    public static String formatRm(double value) {
        return String.format("RM%.2f", value);
    }
}
