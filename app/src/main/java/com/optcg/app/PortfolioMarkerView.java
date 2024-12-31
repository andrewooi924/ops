package com.optcg.app;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class PortfolioMarkerView extends MarkerView {
    private final TextView markerText;

    public PortfolioMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        markerText = findViewById(R.id.markerText); // The TextView in your custom layout
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // Display the Entry value
        markerText.setText(String.format("x: %.0f\ny: %.2f", e.getX(), e.getY()));
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        // Position the marker above the data point
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}

