package com.optcg.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.github.mikephil.charting.data.Entry;
import com.optcg.app.data.repository.PriceRepository;
import com.optcg.app.di.ServiceLocator;
import com.optcg.app.domain.model.PriceTrendPoint;

import java.util.ArrayList;
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

            String cardId = requireContext().getResources().getResourceEntryName(imageResId);
            ServiceLocator.get(requireContext()).cardImageLoader().loadById(cardId, cardImage);
        }

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView titlePriceTrend = view.findViewById(R.id.titlePriceTrend);
        TextView titleVendorPrices = view.findViewById(R.id.titleVendorPrices);
        ViewPager2 viewPager = view.findViewById(R.id.priceViewPager);

        // Trend + vendor data now comes from the repository (no scraping/parsing here).
        PriceRepository priceRepository = ServiceLocator.get(requireContext()).priceRepository();
        priceRepository.getPriceDetail(cardUrl, result -> {
            if (!isAdded() || result.data == null) {
                return;
            }

            List<Entry> priceTrend = new ArrayList<>();
            for (PriceTrendPoint point : result.data.trend) {
                priceTrend.add(new Entry(point.x, point.price));
            }

            List<Fragment> fragments = new ArrayList<>();
            fragments.add(new PriceTrendFragment(priceTrend));
            fragments.add(new VendorPricesFragment(result.data.vendors));

            FragmentStateAdapter adapter = new PriceViewPagerAdapter(getActivity(), fragments);
            viewPager.setAdapter(adapter);

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    if (position == 0) {
                        titlePriceTrend.setVisibility(View.VISIBLE);
                        titleVendorPrices.setVisibility(View.GONE);
                    } else if (position == 1) {
                        titlePriceTrend.setVisibility(View.GONE);
                        titleVendorPrices.setVisibility(View.VISIBLE);
                    }
                }
            });
        });

        return view;
    }
}
