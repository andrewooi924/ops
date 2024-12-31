package com.optcg.app;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PortfolioMarkerView extends MarkerView {
    private final TextView markerText;
    private final SimpleDateFormat dateFormat;
    private final DecimalFormat decimalFormat;
    private final Calendar calendar;
    private LineChart lineChart;

    public PortfolioMarkerView(Context context, int layoutResource, LineChart chart) {
        super(context, layoutResource);
        markerText = findViewById(R.id.markerText);
        dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault()); // e.g., Dec 31
        decimalFormat = new DecimalFormat("#,###.00"); // Format y-values with commas
        calendar = Calendar.getInstance();
        this.lineChart = chart;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // Calculate the date from the x-value
        Date baseDate = new Date(2024 - 1900, 11, 28); // December 28, 2024
        calendar.setTime(baseDate);
        calendar.add(Calendar.DAY_OF_YEAR, (int) e.getX()); // Add x days to the base date

        String formattedDate = dateFormat.format(calendar.getTime());
        String formattedYValue = decimalFormat.format(e.getY());
        markerText.setText(String.format("%s MYR %s", formattedYValue, formattedDate));

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        float offsetX = -(getWidth() / 2f);
        float offsetY = -(posY - lineChart.getHeight() + 550); // Position above the chart, consistent for all markers

        // Use the chart's dimensions directly
        if (lineChart != null) {
            float chartWidth = lineChart.getWidth();

            // Adjust for leftmost marker
            if (posX + offsetX < 0) {
                offsetX = -posX + 20; // Align to the left edge
            }

            // Adjust for rightmost marker
            if (posX + offsetX + getWidth() > chartWidth) {
                offsetX = chartWidth - posX - getWidth() - 20; // Align to the right edge
            }
        }

        return new MPPointF(offsetX, offsetY);
    }
}

