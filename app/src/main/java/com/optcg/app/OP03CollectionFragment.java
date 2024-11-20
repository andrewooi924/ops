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

public class OP03CollectionFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_op03_collection_fragment, container, false);
        ImageView imageView = view.findViewById(R.id.op03_set_img);
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
        recyclerView = view.findViewById(R.id.op03CollectionRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 columns

        // Initialize list of image resources
        menuImages = Arrays.asList(
            R.drawable.op01_051_p2, R.drawable.op03_001, R.drawable.op03_001_p1, R.drawable.op03_002, R.drawable.op03_003, R.drawable.op03_004, R.drawable.op03_005, R.drawable.op03_006, R.drawable.op03_007, R.drawable.op03_008, R.drawable.op03_009, R.drawable.op03_010, R.drawable.op03_011, R.drawable.op03_012, R.drawable.op03_013, R.drawable.op03_013_p1, R.drawable.op03_014, R.drawable.op03_015, R.drawable.op03_016, R.drawable.op03_017, R.drawable.op03_018, R.drawable.op03_018_p1, R.drawable.op03_019, R.drawable.op03_020, R.drawable.op03_021, R.drawable.op03_021_p1, R.drawable.op03_022, R.drawable.op03_022_p1, R.drawable.op03_023, R.drawable.op03_024, R.drawable.op03_024_p1, R.drawable.op03_025, R.drawable.op03_025_p1, R.drawable.op03_026, R.drawable.op03_027, R.drawable.op03_028, R.drawable.op03_029, R.drawable.op03_030, R.drawable.op03_031, R.drawable.op03_032, R.drawable.op03_033, R.drawable.op03_034, R.drawable.op03_035, R.drawable.op03_036, R.drawable.op03_037, R.drawable.op03_038, R.drawable.op03_039, R.drawable.op03_040, R.drawable.op03_040_p1, R.drawable.op03_041, R.drawable.op03_041_p1, R.drawable.op03_042, R.drawable.op03_043, R.drawable.op03_044, R.drawable.op03_045, R.drawable.op03_046, R.drawable.op03_047, R.drawable.op03_047_p1, R.drawable.op03_048, R.drawable.op03_049, R.drawable.op03_050, R.drawable.op03_051, R.drawable.op03_052, R.drawable.op03_053, R.drawable.op03_054, R.drawable.op03_055, R.drawable.op03_056, R.drawable.op03_057, R.drawable.op03_058, R.drawable.op03_058_p1, R.drawable.op03_059, R.drawable.op03_060, R.drawable.op03_061, R.drawable.op03_062, R.drawable.op03_063, R.drawable.op03_064, R.drawable.op03_065, R.drawable.op03_066, R.drawable.op03_066_p1, R.drawable.op03_067, R.drawable.op03_068, R.drawable.op03_069, R.drawable.op03_070, R.drawable.op03_071, R.drawable.op03_072, R.drawable.op03_073, R.drawable.op03_074, R.drawable.op03_075, R.drawable.op03_076, R.drawable.op03_076_p1, R.drawable.op03_077, R.drawable.op03_077_p1, R.drawable.op03_078, R.drawable.op03_078_p1, R.drawable.op03_079, R.drawable.op03_080, R.drawable.op03_080_p1, R.drawable.op03_081, R.drawable.op03_081_p1, R.drawable.op03_082, R.drawable.op03_083, R.drawable.op03_084, R.drawable.op03_085, R.drawable.op03_086, R.drawable.op03_086_p1, R.drawable.op03_087, R.drawable.op03_088, R.drawable.op03_089, R.drawable.op03_090, R.drawable.op03_091, R.drawable.op03_092, R.drawable.op03_092_p1, R.drawable.op03_093, R.drawable.op03_094, R.drawable.op03_095, R.drawable.op03_096, R.drawable.op03_097, R.drawable.op03_098, R.drawable.op03_099, R.drawable.op03_099_p1, R.drawable.op03_100, R.drawable.op03_101, R.drawable.op03_102, R.drawable.op03_103, R.drawable.op03_104, R.drawable.op03_105, R.drawable.op03_106, R.drawable.op03_107, R.drawable.op03_108, R.drawable.op03_108_p1, R.drawable.op03_109, R.drawable.op03_110, R.drawable.op03_111, R.drawable.op03_112, R.drawable.op03_112_p1, R.drawable.op03_113, R.drawable.op03_113_p1, R.drawable.op03_114, R.drawable.op03_114_p1, R.drawable.op03_115, R.drawable.op03_116, R.drawable.op03_117, R.drawable.op03_118, R.drawable.op03_119, R.drawable.op03_120, R.drawable.op03_121, R.drawable.op03_122, R.drawable.op03_122_p1, R.drawable.op03_122_p2, R.drawable.op03_123, R.drawable.op03_123_p1, R.drawable.st01_012_p1, R.drawable.st03_009_p1, R.drawable.st04_003_p1
        );
        // Set up adapter with only images
        setAdapter = new SetAdapter(getContext(), menuImages);
        recyclerView.setAdapter(setAdapter);

        progressCircle = view.findViewById(R.id.op03ProgressCircle);
        progressText = view.findViewById(R.id.op03ProgressText);
        progressC = view.findViewById(R.id.op03CCollected);
        progressUC = view.findViewById(R.id.op03UCCollected);
        progressR = view.findViewById(R.id.op03RCollected);
        progressSR = view.findViewById(R.id.op03SRCollected);
        progressL = view.findViewById(R.id.op03LCollected);
        progressSEC = view.findViewById(R.id.op03SECCollected);
        progressSP = view.findViewById(R.id.op03SPCollected);
        progressMR = view.findViewById(R.id.op03MRCollected);

        sharedPreferences = requireActivity().getSharedPreferences("COLLECTION_PREFS", MODE_PRIVATE);
        int totalCount = sharedPreferences.getInt("op03_total_count", 0);
        float progress = ((float) totalCount / 154) * 100;
        progressCircle.setProgress(progress);
        progressText.setText((int) progress + "%");

        int totalC = sharedPreferences.getInt("op03_total_c", 0);
        progressC.setText(totalC + "/45");

        int totalUC = sharedPreferences.getInt("op03_total_uc", 0);
        progressUC.setText(totalUC + "/32");

        int totalR = sharedPreferences.getInt("op03_total_r", 0);
        progressR.setText(totalR + "/32");

        int totalSR = sharedPreferences.getInt("op03_total_sr", 0);
        progressSR.setText(totalSR + "/20");

        int totalL = sharedPreferences.getInt("op03_total_l", 0);
        progressL.setText(totalL + "/16");

        int totalSEC = sharedPreferences.getInt("op03_total_sec", 0);
        progressSEC.setText(totalSEC + "/4");

        int totalSP = sharedPreferences.getInt("op03_total_sp", 0);
        progressSP.setText(totalSP + "/4");

        int totalMR = sharedPreferences.getInt("op03_total_mr", 0);
        progressMR.setText(totalMR + "/1");
    }

    public static OP03CollectionFragment newInstance(String transitionName) {
        OP03CollectionFragment fragment = new OP03CollectionFragment();
        Bundle args = new Bundle();
        args.putString("transitionName", transitionName);
        fragment.setArguments(args);
        return fragment;
    }
}