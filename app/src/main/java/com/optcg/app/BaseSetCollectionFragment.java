package com.optcg.app;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.optcg.app.data.repository.CollectionRepository;
import com.optcg.app.di.ServiceLocator;

/**
 * Shared implementation of a per-set collection screen. Behaviour is identical to the
 * original OPxxCollectionFragment classes; the layout, view ids, card list, completion
 * denominator and per-rarity rows are supplied as data via {@link #getConfig()}.
 */
public abstract class BaseSetCollectionFragment extends Fragment {

    protected static final String ARG_TRANSITION_NAME = "transitionName";

    /** Each set supplies its layout/view ids, card list and progress rows here. */
    protected abstract SetCollectionConfig getConfig();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(requireContext()).inflateTransition(R.transition.change_image_transform));
        setSharedElementReturnTransition(TransitionInflater.from(requireContext()).inflateTransition(R.transition.change_image_transform));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SetCollectionConfig config = getConfig();
        View view = inflater.inflate(config.layoutResId, container, false);
        ImageView imageView = view.findViewById(config.setImgId);
        if (getArguments() != null) {
            String transitionName = getArguments().getString(ARG_TRANSITION_NAME);
            imageView.setTransitionName(transitionName);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SetCollectionConfig config = getConfig();

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(config.recyclerViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 columns

        // Set up adapter with only images
        SetAdapter setAdapter = new SetAdapter(getContext(), config.menuImages);
        recyclerView.setAdapter(setAdapter);

        CircularProgressBar progressCircle = view.findViewById(config.progressCircleId);
        TextView progressText = view.findViewById(config.progressTextId);

        CollectionRepository collectionRepository = ServiceLocator.get(requireContext()).collectionRepository();
        int totalCount = collectionRepository.getTotalCount(config.prefix);
        float progress = ((float) totalCount / config.totalDenominator) * 100;
        progressCircle.setProgress(progress);
        progressText.setText((int) progress + "%");

        for (SetCollectionConfig.Row row : config.rows) {
            int count = collectionRepository.getTotal(config.prefix, row.keySuffix);
            TextView rowText = view.findViewById(row.viewId);
            rowText.setText(count + "/" + row.denominator);
        }
    }
}
