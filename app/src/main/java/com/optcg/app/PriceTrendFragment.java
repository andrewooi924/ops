package com.optcg.app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

public class PriceTrendFragment extends Fragment {

    private List<Entry> priceEntries;

    public PriceTrendFragment(List<Entry> priceEntries) {
        this.priceEntries = priceEntries;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_price_trend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LineChart chart = view.findViewById(R.id.priceTrendChart);
        LineDataSet dataSet = new LineDataSet(priceEntries, "Price Trend");
        dataSet.setColor(Color.parseColor("#FFD700"));
        dataSet.setValueTextColor(Color.TRANSPARENT);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.parseColor("#FFD700"));
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.getDescription().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.getAxisLeft().setGranularity(10f);
        chart.invalidate();
    }
}