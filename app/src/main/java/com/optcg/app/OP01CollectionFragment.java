package com.optcg.app;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OP01CollectionFragment extends Fragment {

    private RecyclerView recyclerView;
    private SetAdapter setAdapter;
    private List<Integer> menuImages;
    private CircularProgressBar progressCircle;
    private TextView progressText;
    private TextView progressC;
    private TextView progressUC;
    private TextView progressR;
    private TextView progressSR;
    private TextView progressL;
    private TextView progressSEC;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(requireContext()).inflateTransition(R.transition.change_image_transform));
        setSharedElementReturnTransition(TransitionInflater.from(requireContext()).inflateTransition(R.transition.change_image_transform));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_op01_collection_fragment, container, false);
//        Toolbar toolbar = view.findViewById(R.id.op01_toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_close);
//        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        ImageView imageView = view.findViewById(R.id.op01_set_img);
        if (getArguments() != null) {
            String transitionName = getArguments().getString("transitionName");
            imageView.setTransitionName(transitionName);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.op01CollectionRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 columns

        // Initialize list of image resources
        menuImages = Arrays.asList(
                R.drawable.op01_001, R.drawable.op01_001_p1, R.drawable.op01_002, R.drawable.op01_002_p1, R.drawable.op01_003, R.drawable.op01_003_p1, R.drawable.op01_004, R.drawable.op01_005, R.drawable.op01_006, R.drawable.op01_007, R.drawable.op01_008, R.drawable.op01_008_p1, R.drawable.op01_009, R.drawable.op01_010, R.drawable.op01_011, R.drawable.op01_012, R.drawable.op01_013, R.drawable.op01_013_p1, R.drawable.op01_014, R.drawable.op01_015, R.drawable.op01_016, R.drawable.op01_016_p1, R.drawable.op01_017, R.drawable.op01_018, R.drawable.op01_019, R.drawable.op01_020, R.drawable.op01_021, R.drawable.op01_022, R.drawable.op01_023, R.drawable.op01_024, R.drawable.op01_024_p1, R.drawable.op01_025, R.drawable.op01_025_p1, R.drawable.op01_026, R.drawable.op01_027, R.drawable.op01_028, R.drawable.op01_029, R.drawable.op01_030, R.drawable.op01_031, R.drawable.op01_031_p1, R.drawable.op01_032, R.drawable.op01_033, R.drawable.op01_034, R.drawable.op01_034_p1, R.drawable.op01_035, R.drawable.op01_036, R.drawable.op01_037, R.drawable.op01_038, R.drawable.op01_039, R.drawable.op01_040, R.drawable.op01_040_p1, R.drawable.op01_041, R.drawable.op01_042, R.drawable.op01_043, R.drawable.op01_044, R.drawable.op01_045, R.drawable.op01_046, R.drawable.op01_047, R.drawable.op01_047_p1, R.drawable.op01_048, R.drawable.op01_048_p1, R.drawable.op01_049, R.drawable.op01_050, R.drawable.op01_051, R.drawable.op01_051_p1, R.drawable.op01_052, R.drawable.op01_053, R.drawable.op01_054, R.drawable.op01_055, R.drawable.op01_056, R.drawable.op01_057, R.drawable.op01_058, R.drawable.op01_059, R.drawable.op01_060, R.drawable.op01_060_p1, R.drawable.op01_061, R.drawable.op01_061_p1, R.drawable.op01_062, R.drawable.op01_062_p1, R.drawable.op01_063, R.drawable.op01_064, R.drawable.op01_064_p1, R.drawable.op01_065, R.drawable.op01_066, R.drawable.op01_067, R.drawable.op01_067_p1, R.drawable.op01_068, R.drawable.op01_069, R.drawable.op01_070, R.drawable.op01_070_p1, R.drawable.op01_071, R.drawable.op01_072, R.drawable.op01_073, R.drawable.op01_073_p1, R.drawable.op01_074, R.drawable.op01_075, R.drawable.op01_076, R.drawable.op01_077, R.drawable.op01_077_p1, R.drawable.op01_078, R.drawable.op01_078_p1, R.drawable.op01_079, R.drawable.op01_080, R.drawable.op01_081, R.drawable.op01_082, R.drawable.op01_083, R.drawable.op01_084, R.drawable.op01_085, R.drawable.op01_086, R.drawable.op01_087, R.drawable.op01_088, R.drawable.op01_089, R.drawable.op01_090, R.drawable.op01_091, R.drawable.op01_091_p1, R.drawable.op01_092, R.drawable.op01_093, R.drawable.op01_093_p1, R.drawable.op01_094, R.drawable.op01_094_p1, R.drawable.op01_095, R.drawable.op01_096, R.drawable.op01_096_p1, R.drawable.op01_097, R.drawable.op01_097_p1, R.drawable.op01_098, R.drawable.op01_099, R.drawable.op01_100, R.drawable.op01_101, R.drawable.op01_102, R.drawable.op01_102_p1, R.drawable.op01_103, R.drawable.op01_104, R.drawable.op01_105, R.drawable.op01_106, R.drawable.op01_107, R.drawable.op01_108, R.drawable.op01_109, R.drawable.op01_109_p1, R.drawable.op01_110, R.drawable.op01_111, R.drawable.op01_112, R.drawable.op01_113, R.drawable.op01_114, R.drawable.op01_115, R.drawable.op01_116, R.drawable.op01_117, R.drawable.op01_118, R.drawable.op01_119, R.drawable.op01_120, R.drawable.op01_120_p1, R.drawable.op01_120_p2, R.drawable.op01_121, R.drawable.op01_121_p1);

        // Set up adapter with only images
        setAdapter = new SetAdapter(getContext(), menuImages);
        recyclerView.setAdapter(setAdapter);

        progressCircle = view.findViewById(R.id.op01ProgressCircle);
        progressText = view.findViewById(R.id.op01ProgressText);
        progressC = view.findViewById(R.id.op01CCollected);
        progressUC = view.findViewById(R.id.op01UCCollected);
        progressR = view.findViewById(R.id.op01RCollected);
        progressSR = view.findViewById(R.id.op01SRCollected);
        progressL = view.findViewById(R.id.op01LCollected);
        progressSEC = view.findViewById(R.id.op01SECCollected);

        sharedPreferences = requireActivity().getSharedPreferences("COLLECTION_PREFS", MODE_PRIVATE);
        int totalCount = sharedPreferences.getInt("op01_total_count", 0);
        float progress = ((float) totalCount / 154) * 100;
        progressCircle.setProgress(progress);
        progressText.setText((int) progress + "%");

        int totalC = sharedPreferences.getInt("op01_total_c", 0);
        progressC.setText(totalC + "/49");

        int totalUC = sharedPreferences.getInt("op01_total_uc", 0);
        progressUC.setText(totalUC + "/32");

        int totalR = sharedPreferences.getInt("op01_total_r", 0);
        progressR.setText(totalR + "/32");

        int totalSR = sharedPreferences.getInt("op01_total_sr", 0);
        progressSR.setText(totalSR + "/20");

        int totalL = sharedPreferences.getInt("op01_total_l", 0);
        progressL.setText(totalL + "/16");

        int totalSEC = sharedPreferences.getInt("op01_total_sec", 0);
        progressSEC.setText(totalSEC + "/5");
    }

    public static OP01CollectionFragment newInstance(String transitionName) {
        OP01CollectionFragment fragment = new OP01CollectionFragment();
        Bundle args = new Bundle();
        args.putString("transitionName", transitionName);
        fragment.setArguments(args);
        return fragment;
    }
}