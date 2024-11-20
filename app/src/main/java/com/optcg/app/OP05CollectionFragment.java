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

public class OP05CollectionFragment extends Fragment {

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
    private TextView progressSP;
    private TextView progressMR;
    private TextView progressODA;
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
        View view = inflater.inflate(R.layout.fragment_op05_collection_fragment, container, false);
        ImageView imageView = view.findViewById(R.id.op05_set_img);
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
        recyclerView = view.findViewById(R.id.op05CollectionRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 columns

        // Initialize list of image resources
        menuImages = Arrays.asList(
                R.drawable.op01_016_p4, R.drawable.op01_121_p2, R.drawable.op02_120_p2, R.drawable.op03_092_p2, R.drawable.op04_044_p2, R.drawable.op05_001, R.drawable.op05_001_p1, R.drawable.op05_002, R.drawable.op05_002_p1, R.drawable.op05_003, R.drawable.op05_004, R.drawable.op05_005, R.drawable.op05_006, R.drawable.op05_006_p1, R.drawable.op05_007, R.drawable.op05_007_p1, R.drawable.op05_008, R.drawable.op05_009, R.drawable.op05_010, R.drawable.op05_011, R.drawable.op05_012, R.drawable.op05_013, R.drawable.op05_014, R.drawable.op05_015, R.drawable.op05_015_p1, R.drawable.op05_016, R.drawable.op05_017, R.drawable.op05_018, R.drawable.op05_019, R.drawable.op05_020, R.drawable.op05_021, R.drawable.op05_022, R.drawable.op05_022_p1, R.drawable.op05_023, R.drawable.op05_024, R.drawable.op05_025, R.drawable.op05_026, R.drawable.op05_027, R.drawable.op05_028, R.drawable.op05_029, R.drawable.op05_030, R.drawable.op05_031, R.drawable.op05_032, R.drawable.op05_032_p1, R.drawable.op05_033, R.drawable.op05_034, R.drawable.op05_034_p1, R.drawable.op05_035, R.drawable.op05_036, R.drawable.op05_037, R.drawable.op05_038, R.drawable.op05_039, R.drawable.op05_040, R.drawable.op05_041, R.drawable.op05_041_p1, R.drawable.op05_042, R.drawable.op05_043, R.drawable.op05_043_p1, R.drawable.op05_044, R.drawable.op05_045, R.drawable.op05_046, R.drawable.op05_047, R.drawable.op05_048, R.drawable.op05_049, R.drawable.op05_050, R.drawable.op05_051, R.drawable.op05_051_p1, R.drawable.op05_052, R.drawable.op05_053, R.drawable.op05_054, R.drawable.op05_055, R.drawable.op05_055_p1, R.drawable.op05_056, R.drawable.op05_057, R.drawable.op05_058, R.drawable.op05_059, R.drawable.op05_060, R.drawable.op05_060_p1, R.drawable.op05_061, R.drawable.op05_062, R.drawable.op05_063, R.drawable.op05_064, R.drawable.op05_065, R.drawable.op05_066, R.drawable.op05_067, R.drawable.op05_067_p1, R.drawable.op05_068, R.drawable.op05_069, R.drawable.op05_069_p1, R.drawable.op05_069_p2, R.drawable.op05_070, R.drawable.op05_071, R.drawable.op05_072, R.drawable.op05_073, R.drawable.op05_074, R.drawable.op05_074_p1, R.drawable.op05_074_p2, R.drawable.op05_075, R.drawable.op05_076, R.drawable.op05_077, R.drawable.op05_078, R.drawable.op05_079, R.drawable.op05_080, R.drawable.op05_081, R.drawable.op05_082, R.drawable.op05_083, R.drawable.op05_084, R.drawable.op05_085, R.drawable.op05_086, R.drawable.op05_087, R.drawable.op05_088, R.drawable.op05_088_p1, R.drawable.op05_089, R.drawable.op05_090, R.drawable.op05_091, R.drawable.op05_091_p1, R.drawable.op05_092, R.drawable.op05_093, R.drawable.op05_093_p1, R.drawable.op05_094, R.drawable.op05_095, R.drawable.op05_096, R.drawable.op05_097, R.drawable.op05_098, R.drawable.op05_098_p1, R.drawable.op05_099, R.drawable.op05_100, R.drawable.op05_100_p1, R.drawable.op05_100_p2, R.drawable.op05_101, R.drawable.op05_102, R.drawable.op05_102_p1, R.drawable.op05_103, R.drawable.op05_104, R.drawable.op05_105, R.drawable.op05_106, R.drawable.op05_107, R.drawable.op05_108, R.drawable.op05_109, R.drawable.op05_110, R.drawable.op05_111, R.drawable.op05_112, R.drawable.op05_113, R.drawable.op05_114, R.drawable.op05_115, R.drawable.op05_116, R.drawable.op05_117, R.drawable.op05_118, R.drawable.op05_118_p1, R.drawable.op05_119, R.drawable.op05_119_p1, R.drawable.op05_119_p2, R.drawable.st01_012_p4, R.drawable.st01_012_p3
        );
        // Set up adapter with only images
        setAdapter = new SetAdapter(getContext(), menuImages);
        recyclerView.setAdapter(setAdapter);

        progressCircle = view.findViewById(R.id.op05ProgressCircle);
        progressText = view.findViewById(R.id.op05ProgressText);
        progressC = view.findViewById(R.id.op05CCollected);
        progressUC = view.findViewById(R.id.op05UCCollected);
        progressR = view.findViewById(R.id.op05RCollected);
        progressSR = view.findViewById(R.id.op05SRCollected);
        progressL = view.findViewById(R.id.op05LCollected);
        progressSEC = view.findViewById(R.id.op05SECCollected);
        progressSP = view.findViewById(R.id.op05SPCollected);
        progressMR = view.findViewById(R.id.op05MRCollected);
        progressODA = view.findViewById(R.id.op05ODACollected);

        sharedPreferences = requireActivity().getSharedPreferences("COLLECTION_PREFS", MODE_PRIVATE);
        int totalCount = sharedPreferences.getInt("op05_total_count", 0);
        float progress = ((float) totalCount / 154) * 100;
        progressCircle.setProgress(progress);
        progressText.setText((int) progress + "%");

        int totalC = sharedPreferences.getInt("op05_total_c", 0);
        progressC.setText(totalC + "/45");

        int totalUC = sharedPreferences.getInt("op05_total_uc", 0);
        progressUC.setText(totalUC + "/30");

        int totalR = sharedPreferences.getInt("op05_total_r", 0);
        progressR.setText(totalR + "/32");

        int totalSR = sharedPreferences.getInt("op05_total_sr", 0);
        progressSR.setText(totalSR + "/21");

        int totalL = sharedPreferences.getInt("op05_total_l", 0);
        progressL.setText(totalL + "/12");

        int totalSEC = sharedPreferences.getInt("op05_total_sec", 0);
        progressSEC.setText(totalSEC + "/4");

        int totalSP = sharedPreferences.getInt("op05_total_sp", 0);
        progressSP.setText(totalSP + "/6");

        int totalMR = sharedPreferences.getInt("op05_total_mr", 0);
        progressMR.setText(totalMR + "/3");

        int totalODA = sharedPreferences.getInt("op05_total_oda", 0);
        progressODA.setText(totalODA + "/1");
    }

    public static OP05CollectionFragment newInstance(String transitionName) {
        OP05CollectionFragment fragment = new OP05CollectionFragment();
        Bundle args = new Bundle();
        args.putString("transitionName", transitionName);
        fragment.setArguments(args);
        return fragment;
    }
}