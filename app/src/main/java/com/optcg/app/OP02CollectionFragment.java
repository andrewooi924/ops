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

public class OP02CollectionFragment extends Fragment {

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
    private TextView progressMR;
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
        View view = inflater.inflate(R.layout.fragment_op02_collection_fragment, container, false);
        ImageView imageView = view.findViewById(R.id.op02_set_img);
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
        recyclerView = view.findViewById(R.id.op02CollectionRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 columns

        // Initialize list of image resources
        menuImages = Arrays.asList(
                R.drawable.op02_001, R.drawable.op02_001_p1, R.drawable.op02_002, R.drawable.op02_002_p1, R.drawable.op02_003, R.drawable.op02_004, R.drawable.op02_004_p1, R.drawable.op02_005, R.drawable.op02_006, R.drawable.op02_007, R.drawable.op02_008, R.drawable.op02_009, R.drawable.op02_009_p1, R.drawable.op02_010, R.drawable.op02_011, R.drawable.op02_012, R.drawable.op02_013, R.drawable.op02_013_p1, R.drawable.op02_013_p2, R.drawable.op02_014, R.drawable.op02_015, R.drawable.op02_016, R.drawable.op02_017, R.drawable.op02_017_p1, R.drawable.op02_018, R.drawable.op02_018_p1, R.drawable.op02_019, R.drawable.op02_020, R.drawable.op02_021, R.drawable.op02_022, R.drawable.op02_023, R.drawable.op02_024, R.drawable.op02_025, R.drawable.op02_025_p1, R.drawable.op02_026, R.drawable.op02_026_p1, R.drawable.op02_027, R.drawable.op02_028, R.drawable.op02_029, R.drawable.op02_030, R.drawable.op02_030_p1, R.drawable.op02_031, R.drawable.op02_031_p1, R.drawable.op02_032, R.drawable.op02_033, R.drawable.op02_034, R.drawable.op02_035, R.drawable.op02_036, R.drawable.op02_036_p1, R.drawable.op02_037, R.drawable.op02_038, R.drawable.op02_039, R.drawable.op02_040, R.drawable.op02_041, R.drawable.op02_041_p1, R.drawable.op02_042, R.drawable.op02_043, R.drawable.op02_044, R.drawable.op02_045, R.drawable.op02_046, R.drawable.op02_047, R.drawable.op02_048, R.drawable.op02_049, R.drawable.op02_049_p1, R.drawable.op02_050, R.drawable.op02_051, R.drawable.op02_051_p1, R.drawable.op02_052, R.drawable.op02_053, R.drawable.op02_054, R.drawable.op02_055, R.drawable.op02_056, R.drawable.op02_057, R.drawable.op02_058, R.drawable.op02_058_p1, R.drawable.op02_059, R.drawable.op02_059_p1, R.drawable.op02_060, R.drawable.op02_061, R.drawable.op02_062, R.drawable.op02_062_p1, R.drawable.op02_063, R.drawable.op02_064, R.drawable.op02_065, R.drawable.op02_066, R.drawable.op02_067, R.drawable.op02_068, R.drawable.op02_069, R.drawable.op02_070, R.drawable.op02_071, R.drawable.op02_071_p1, R.drawable.op02_072, R.drawable.op02_072_p1, R.drawable.op02_073, R.drawable.op02_073_p1, R.drawable.op02_074, R.drawable.op02_075, R.drawable.op02_076, R.drawable.op02_077, R.drawable.op02_078, R.drawable.op02_079, R.drawable.op02_080, R.drawable.op02_081, R.drawable.op02_082, R.drawable.op02_083, R.drawable.op02_084, R.drawable.op02_085, R.drawable.op02_085_p1, R.drawable.op02_086, R.drawable.op02_086_p1, R.drawable.op02_087, R.drawable.op02_088, R.drawable.op02_089, R.drawable.op02_090, R.drawable.op02_091, R.drawable.op02_092, R.drawable.op02_093, R.drawable.op02_093_p1, R.drawable.op02_094, R.drawable.op02_095, R.drawable.op02_096, R.drawable.op02_096_p1, R.drawable.op02_097, R.drawable.op02_098, R.drawable.op02_099, R.drawable.op02_099_p1, R.drawable.op02_100, R.drawable.op02_101, R.drawable.op02_102, R.drawable.op02_103, R.drawable.op02_104, R.drawable.op02_105, R.drawable.op02_105_p1, R.drawable.op02_106, R.drawable.op02_107, R.drawable.op02_108, R.drawable.op02_108_p1, R.drawable.op02_109, R.drawable.op02_110, R.drawable.op02_111, R.drawable.op02_112, R.drawable.op02_113, R.drawable.op02_114, R.drawable.op02_114_p1, R.drawable.op02_115, R.drawable.op02_115_p1, R.drawable.op02_116, R.drawable.op02_117, R.drawable.op02_118, R.drawable.op02_119, R.drawable.op02_120, R.drawable.op02_120_p1, R.drawable.op02_121, R.drawable.op02_121_p1
                );
        // Set up adapter with only images
        setAdapter = new SetAdapter(getContext(), menuImages);
        recyclerView.setAdapter(setAdapter);

        progressCircle = view.findViewById(R.id.op02ProgressCircle);
        progressText = view.findViewById(R.id.op02ProgressText);
        progressC = view.findViewById(R.id.op02CCollected);
        progressUC = view.findViewById(R.id.op02UCCollected);
        progressR = view.findViewById(R.id.op02RCollected);
        progressSR = view.findViewById(R.id.op02SRCollected);
        progressL = view.findViewById(R.id.op02LCollected);
        progressSEC = view.findViewById(R.id.op02SECCollected);
        progressMR = view.findViewById(R.id.op02MRCollected);

        sharedPreferences = requireActivity().getSharedPreferences("COLLECTION_PREFS", MODE_PRIVATE);
        int totalCount = sharedPreferences.getInt("op02_total_count", 0);
        float progress = ((float) totalCount / 154) * 100;
        progressCircle.setProgress(progress);
        progressText.setText((int) progress + "%");

        int totalC = sharedPreferences.getInt("op02_total_c", 0);
        progressC.setText(totalC + "/47");

        int totalUC = sharedPreferences.getInt("op02_total_uc", 0);
        progressUC.setText(totalUC + "/34");

        int totalR = sharedPreferences.getInt("op02_total_r", 0);
        progressR.setText(totalR + "/32");

        int totalSR = sharedPreferences.getInt("op02_total_sr", 0);
        progressSR.setText(totalSR + "/20");

        int totalL = sharedPreferences.getInt("op02_total_l", 0);
        progressL.setText(totalL + "/16");

        int totalSEC = sharedPreferences.getInt("op02_total_sec", 0);
        progressSEC.setText(totalSEC + "/4");

        int totalMR = sharedPreferences.getInt("op02_total_mr", 0);
        progressMR.setText(totalMR + "/1");
    }

    public static OP02CollectionFragment newInstance(String transitionName) {
        OP02CollectionFragment fragment = new OP02CollectionFragment();
        Bundle args = new Bundle();
        args.putString("transitionName", transitionName);
        fragment.setArguments(args);
        return fragment;
    }
}