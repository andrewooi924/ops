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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OP06CollectionFragment extends Fragment {

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
    private TextView progressSP;
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
        View view = inflater.inflate(R.layout.fragment_op06_collection_fragment, container, false);
        ImageView imageView = view.findViewById(R.id.op06_set_img);
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
        recyclerView = view.findViewById(R.id.op06CollectionRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 columns

        // Initialize list of image resources
        menuImages = Arrays.asList(
            R.drawable.op03_008_p1, R.drawable.op03_114_p2, R.drawable.op04_024_p2, R.drawable.op04_064_p2, R.drawable.op05_051_p2, R.drawable.op05_091_p2, R.drawable.op06_001, R.drawable.op06_001_p1, R.drawable.op06_002, R.drawable.op06_003, R.drawable.op06_004, R.drawable.op06_005, R.drawable.op06_006, R.drawable.op06_007, R.drawable.op06_007_p1, R.drawable.op06_008, R.drawable.op06_009, R.drawable.op06_009_p1, R.drawable.op06_010, R.drawable.op06_011, R.drawable.op06_012, R.drawable.op06_013, R.drawable.op06_013_p1, R.drawable.op06_014, R.drawable.op06_015, R.drawable.op06_016, R.drawable.op06_017, R.drawable.op06_018, R.drawable.op06_019, R.drawable.op06_020, R.drawable.op06_020_p1, R.drawable.op06_021, R.drawable.op06_021_p1, R.drawable.op06_022, R.drawable.op06_022_p1, R.drawable.op06_023, R.drawable.op06_024, R.drawable.op06_025, R.drawable.op06_025_p1, R.drawable.op06_026, R.drawable.op06_027, R.drawable.op06_028, R.drawable.op06_029, R.drawable.op06_030, R.drawable.op06_031, R.drawable.op06_032, R.drawable.op06_033, R.drawable.op06_034, R.drawable.op06_035, R.drawable.op06_035_p1, R.drawable.op06_036, R.drawable.op06_037, R.drawable.op06_038, R.drawable.op06_039, R.drawable.op06_040, R.drawable.op06_041, R.drawable.op06_042, R.drawable.op06_042_p1, R.drawable.op06_043, R.drawable.op06_043_p1, R.drawable.op06_044, R.drawable.op06_045, R.drawable.op06_046, R.drawable.op06_047, R.drawable.op06_048, R.drawable.op06_049, R.drawable.op06_050, R.drawable.op06_050_p1, R.drawable.op06_051, R.drawable.op06_052, R.drawable.op06_053, R.drawable.op06_054, R.drawable.op06_055, R.drawable.op06_056, R.drawable.op06_057, R.drawable.op06_058, R.drawable.op06_059, R.drawable.op06_060, R.drawable.op06_061, R.drawable.op06_061_p1, R.drawable.op06_062, R.drawable.op06_062_p1, R.drawable.op06_063, R.drawable.op06_064, R.drawable.op06_065, R.drawable.op06_066, R.drawable.op06_067, R.drawable.op06_068, R.drawable.op06_069, R.drawable.op06_069_p1, R.drawable.op06_070, R.drawable.op06_071, R.drawable.op06_072, R.drawable.op06_073, R.drawable.op06_074, R.drawable.op06_075, R.drawable.op06_076, R.drawable.op06_077, R.drawable.op06_078, R.drawable.op06_079, R.drawable.op06_080, R.drawable.op06_080_p1, R.drawable.op06_081, R.drawable.op06_081_p1, R.drawable.op06_082, R.drawable.op06_083, R.drawable.op06_084, R.drawable.op06_085, R.drawable.op06_086, R.drawable.op06_086_p1, R.drawable.op06_087, R.drawable.op06_088, R.drawable.op06_089, R.drawable.op06_090, R.drawable.op06_091, R.drawable.op06_092, R.drawable.op06_093, R.drawable.op06_093_p1, R.drawable.op06_094, R.drawable.op06_095, R.drawable.op06_096, R.drawable.op06_097, R.drawable.op06_098, R.drawable.op06_099, R.drawable.op06_100, R.drawable.op06_101, R.drawable.op06_101_p1, R.drawable.op06_102, R.drawable.op06_103, R.drawable.op06_104, R.drawable.op06_105, R.drawable.op06_106, R.drawable.op06_106_p1, R.drawable.op06_107, R.drawable.op06_107_p1, R.drawable.op06_108, R.drawable.op06_109, R.drawable.op06_110, R.drawable.op06_111, R.drawable.op06_112, R.drawable.op06_113, R.drawable.op06_114, R.drawable.op06_115, R.drawable.op06_116, R.drawable.op06_117, R.drawable.op06_118, R.drawable.op06_118_p1, R.drawable.op06_118_p2, R.drawable.op06_119, R.drawable.op06_119_p1
        );
        // Set up adapter with only images
        setAdapter = new SetAdapter(getContext(), menuImages);
        recyclerView.setAdapter(setAdapter);

        progressCircle = view.findViewById(R.id.op06ProgressCircle);
        progressText = view.findViewById(R.id.op06ProgressText);
        progressC = view.findViewById(R.id.op06CCollected);
        progressUC = view.findViewById(R.id.op06UCCollected);
        progressR = view.findViewById(R.id.op06RCollected);
        progressSR = view.findViewById(R.id.op06SRCollected);
        progressL = view.findViewById(R.id.op06LCollected);
        progressSEC = view.findViewById(R.id.op06SECCollected);
        progressMR = view.findViewById(R.id.op06MRCollected);
        progressSP = view.findViewById(R.id.op06SPCollected);

        sharedPreferences = requireActivity().getSharedPreferences("COLLECTION_PREFS", MODE_PRIVATE);
        int totalCount = sharedPreferences.getInt("op06_total_count", 0);
        float progress = ((float) totalCount / 150) * 100;
        progressCircle.setProgress(progress);
        progressText.setText((int) progress + "%");

        int totalC = sharedPreferences.getInt("op06_total_c", 0);
        progressC.setText(totalC + "/45");

        int totalUC = sharedPreferences.getInt("op06_total_uc", 0);
        progressUC.setText(totalUC + "/30");

        int totalR = sharedPreferences.getInt("op06_total_r", 0);
        progressR.setText(totalR + "/32");

        int totalSR = sharedPreferences.getInt("op06_total_sr", 0);
        progressSR.setText(totalSR + "/20");

        int totalL = sharedPreferences.getInt("op06_total_l", 0);
        progressL.setText(totalL + "/12");

        int totalSEC = sharedPreferences.getInt("op06_total_sec", 0);
        progressSEC.setText(totalSEC + "/4");

        int totalMR = sharedPreferences.getInt("op06_total_mr", 0);
        progressMR.setText(totalMR + "/1");

        int totalSP = sharedPreferences.getInt("op06_total_sp", 0);
        progressSP.setText(totalSP + "/6");
    }

    public static OP06CollectionFragment newInstance(String transitionName) {
        OP06CollectionFragment fragment = new OP06CollectionFragment();
        Bundle args = new Bundle();
        args.putString("transitionName", transitionName);
        fragment.setArguments(args);
        return fragment;
    }
}