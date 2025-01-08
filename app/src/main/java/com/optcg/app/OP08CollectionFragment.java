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

public class OP08CollectionFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_op08_collection_fragment, container, false);
        ImageView imageView = view.findViewById(R.id.op08_set_img);
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
        recyclerView = view.findViewById(R.id.op08CollectionRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 columns

        // Initialize list of image resources
        menuImages = Arrays.asList(
                R.drawable.op02_013_p3, R.drawable.op03_112_p4, R.drawable.op08_001, R.drawable.op08_001_p1, R.drawable.op08_002, R.drawable.op08_002_p1, R.drawable.op08_003, R.drawable.op08_004, R.drawable.op08_005, R.drawable.op08_006, R.drawable.op08_007, R.drawable.op08_007_p1, R.drawable.op08_008, R.drawable.op08_009, R.drawable.op08_010, R.drawable.op08_011, R.drawable.op08_012, R.drawable.op08_013, R.drawable.op08_014, R.drawable.op08_015, R.drawable.op08_015_p1, R.drawable.op08_016, R.drawable.op08_017, R.drawable.op08_018, R.drawable.op08_019, R.drawable.op08_020, R.drawable.op08_021, R.drawable.op08_021_p1, R.drawable.op08_022, R.drawable.op08_023, R.drawable.op08_023_p1, R.drawable.op08_024, R.drawable.op08_025, R.drawable.op08_026, R.drawable.op08_027, R.drawable.op08_028, R.drawable.op08_029, R.drawable.op08_030, R.drawable.op08_030_p1, R.drawable.op08_031, R.drawable.op08_032, R.drawable.op08_033, R.drawable.op08_034, R.drawable.op08_035, R.drawable.op08_036, R.drawable.op08_037, R.drawable.op08_038, R.drawable.op08_039, R.drawable.op08_040, R.drawable.op08_041, R.drawable.op08_042, R.drawable.op08_043, R.drawable.op08_043_p1, R.drawable.op08_044, R.drawable.op08_045, R.drawable.op08_046, R.drawable.op08_047, R.drawable.op08_048, R.drawable.op08_049, R.drawable.op08_050, R.drawable.op08_051, R.drawable.op08_052, R.drawable.op08_052_p1, R.drawable.op08_053, R.drawable.op08_054, R.drawable.op08_055, R.drawable.op08_056, R.drawable.op08_057, R.drawable.op08_057_p1, R.drawable.op08_058, R.drawable.op08_058_p1, R.drawable.op08_059, R.drawable.op08_060, R.drawable.op08_061, R.drawable.op08_062, R.drawable.op08_063, R.drawable.op08_064, R.drawable.op08_065, R.drawable.op08_066, R.drawable.op08_067, R.drawable.op08_067_p1, R.drawable.op08_068, R.drawable.op08_069, R.drawable.op08_069_p1, R.drawable.op08_070, R.drawable.op08_071, R.drawable.op08_072, R.drawable.op08_073, R.drawable.op08_074, R.drawable.op08_074_p1, R.drawable.op08_075, R.drawable.op08_076, R.drawable.op08_077, R.drawable.op08_078, R.drawable.op08_079, R.drawable.op08_079_p1, R.drawable.op08_080, R.drawable.op08_080_p1, R.drawable.op08_081, R.drawable.op08_082, R.drawable.op08_083, R.drawable.op08_084, R.drawable.op08_084_p1, R.drawable.op08_085, R.drawable.op08_086, R.drawable.op08_087, R.drawable.op08_088, R.drawable.op08_089, R.drawable.op08_090, R.drawable.op08_091, R.drawable.op08_092, R.drawable.op08_093, R.drawable.op08_094, R.drawable.op08_095, R.drawable.op08_096, R.drawable.op08_097, R.drawable.op08_098, R.drawable.op08_098_p1, R.drawable.op08_099, R.drawable.op08_100, R.drawable.op08_101, R.drawable.op08_102, R.drawable.op08_103, R.drawable.op08_104, R.drawable.op08_105, R.drawable.op08_105_p1, R.drawable.op08_106, R.drawable.op08_106_p1, R.drawable.op08_107, R.drawable.op08_108, R.drawable.op08_109, R.drawable.op08_110, R.drawable.op08_110_p1, R.drawable.op08_111, R.drawable.op08_112, R.drawable.op08_112_p1, R.drawable.op08_113, R.drawable.op08_114, R.drawable.op08_115, R.drawable.op08_116, R.drawable.op08_117, R.drawable.op08_118, R.drawable.op08_118_p1, R.drawable.op08_118_p2, R.drawable.op08_119, R.drawable.op08_119_p1, R.drawable.op_st02_007_p3, R.drawable.op_st03_004_p2, R.drawable.op_st04_005_p3, R.drawable.op_st06_006_p2
        );

        // Set up adapter with only images
        setAdapter = new SetAdapter(getContext(), menuImages);
        recyclerView.setAdapter(setAdapter);

        progressCircle = view.findViewById(R.id.op08ProgressCircle);
        progressText = view.findViewById(R.id.op08ProgressText);
        progressC = view.findViewById(R.id.op08CCollected);
        progressUC = view.findViewById(R.id.op08UCCollected);
        progressR = view.findViewById(R.id.op08RCollected);
        progressSR = view.findViewById(R.id.op08SRCollected);
        progressL = view.findViewById(R.id.op08LCollected);
        progressSEC = view.findViewById(R.id.op08SECCollected);
        progressMR = view.findViewById(R.id.op08MRCollected);
        progressSP = view.findViewById(R.id.op08SPCollected);

        sharedPreferences = requireActivity().getSharedPreferences("COLLECTION_PREFS", MODE_PRIVATE);
        int totalCount = sharedPreferences.getInt("op08_total_count", 0);
        float progress = ((float) totalCount / 150) * 100;
        progressCircle.setProgress(progress);
        progressText.setText((int) progress + "%");

        int totalC = sharedPreferences.getInt("op08_total_c", 0);
        progressC.setText(totalC + "/45");

        int totalUC = sharedPreferences.getInt("op08_total_uc", 0);
        progressUC.setText(totalUC + "/30");

        int totalR = sharedPreferences.getInt("op08_total_r", 0);
        progressR.setText(totalR + "/32");

        int totalSR = sharedPreferences.getInt("op08_total_sr", 0);
        progressSR.setText(totalSR + "/20");

        int totalL = sharedPreferences.getInt("op08_total_l", 0);
        progressL.setText(totalL + "/12");

        int totalSEC = sharedPreferences.getInt("op08_total_sec", 0);
        progressSEC.setText(totalSEC + "/4");

        int totalMR = sharedPreferences.getInt("op08_total_mr", 0);
        progressMR.setText(totalMR + "/1");

        int totalSP = sharedPreferences.getInt("op08_total_sp", 0);
        progressSP.setText(totalSP + "/6");
    }

    public static OP08CollectionFragment newInstance(String transitionName) {
        OP08CollectionFragment fragment = new OP08CollectionFragment();
        Bundle args = new Bundle();
        args.putString("transitionName", transitionName);
        fragment.setArguments(args);
        return fragment;
    }
}