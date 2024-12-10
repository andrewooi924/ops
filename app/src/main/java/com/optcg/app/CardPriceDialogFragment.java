package com.optcg.app;

import static com.optcg.app.NetworkUtils.fetchHtml;

import static org.jsoup.internal.StringUtil.isNumeric;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardPriceDialogFragment extends DialogFragment {

    private static final String ARG_IMAGE_RES_ID = "imageResId";
    private static final String ARG_CARD_URL = "cardUrl";
    private String cardUrl;

    public static CardPriceDialogFragment newInstance(int imageResId, String cardUrl) {
        CardPriceDialogFragment fragment = new CardPriceDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_RES_ID, imageResId);
        args.putString(ARG_CARD_URL, cardUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_price_dialog, container, false);

        ImageView cardImage = view.findViewById(R.id.cpdImage);

        if (getArguments() != null) {
            int imageResId = getArguments().getInt(ARG_IMAGE_RES_ID);
            cardUrl = getArguments().getString(ARG_CARD_URL);

            cardImage.setImageResource(imageResId);
        }

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Thread(() -> {
            // Fetch HTML content
            String htmlContent = fetchHtml(cardUrl);

            // Parse data
            List<Entry> priceTrend = parsePriceTrend(htmlContent);
            List<VendorPrice> vendorPrices = parseVendorPrices(htmlContent);
            System.out.println(vendorPrices);

            // Update UI on the main thread
            requireActivity().runOnUiThread(() -> {
                displayPriceTrend(priceTrend);

                RecyclerView recyclerView = view.findViewById(R.id.vendorPricesRecycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                recyclerView.setAdapter(new VendorPriceAdapter(vendorPrices));
            });
        }).start();
    }

    private List<Entry> parsePriceTrend(String html) {
        List<Entry> priceEntries = new ArrayList<>();
        try {
            Document document = Jsoup.parse(html);
            Elements rows = document.select("table.table_info tbody tr");
            for (int i = rows.size() - 9; i >= 0; i--) {
                Element row = rows.get(i);
                String priceStr = row.select("td").get(0).text().replace("円", "").replace(",", "");
                String dateStr = row.select("td").get(1).text();

                float price = Float.parseFloat(priceStr);
                priceEntries.add(new Entry((rows.size() - 9) - i, price)); // Using index as X value
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Debugging: Print the parsed price entries
        for (Entry entry : priceEntries) {
            Log.d("PriceTrend", "X: " + entry.getX() + ", Y: " + entry.getY());
        }
        return priceEntries;
    }

    private void displayPriceTrend(List<Entry> priceEntries) {
        LineChart chart = getView().findViewById(R.id.priceTrendChart);

        LineDataSet dataSet = new LineDataSet(priceEntries, "Price Trend");
        dataSet.setColor(Color.parseColor("#FFD700")); // Set the line color (for example, blue)
        dataSet.setValueTextColor(Color.TRANSPARENT); // Set the text color of values on the graph
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.parseColor("#FFD700"));
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);

        // Add the data to the chart
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Set the chart's description text color
        chart.getDescription().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.getLegend().setEnabled(false);

        // Set the X and Y axis text color to white (important for dark mode)
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);

        // Set the chart background color (optional for dark mode)
//        chart.setBackgroundColor(Color.BLACK);

        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.getAxisLeft().setGranularity(10f);

        chart.invalidate(); // Refresh the chart
    }

    private List<VendorPrice> parseVendorPrices(String html) {
        List<VendorPrice> vendorPrices = new ArrayList<>();
        try {
            Document document = Jsoup.parse(html);
            Elements rows = document.select("table.table_info tbody tr");

            for (Element row : rows) {
                Elements cells = row.select("td");
                if (cells.size() >= 2) { // Ensure there are at least two columns
                    String vendor = cells.get(0).text();
                    String priceStr = cells.get(1).text().replace("円", "").replace(",", "");

                    // Only try to parse the price if it's numeric
                    if (isNumeric(priceStr)) {
                        float price = Float.parseFloat(priceStr);
                        vendorPrices.add(new VendorPrice(vendor, price));
                    } else {
                        // Log the issue with invalid price
                        Log.w("CardPriceDialog", "Invalid price format: " + priceStr);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vendorPrices;
    }
}