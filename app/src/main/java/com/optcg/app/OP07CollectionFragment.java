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

public class OP07CollectionFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_op07_collection_fragment, container, false);
        ImageView imageView = view.findViewById(R.id.op07_set_img);
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
        recyclerView = view.findViewById(R.id.op07CollectionRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 columns

        // Initialize list of image resources
        menuImages = Arrays.asList(
            R.drawable.op01_035_p2, R.drawable.op01_073_p2, R.drawable.op03_003_p1, R.drawable.op03_078_p2, R.drawable.op05_074_p3, R.drawable.op06_101_p2, R.drawable.op07_001, R.drawable.op07_001_p1, R.drawable.op07_002, R.drawable.op07_003, R.drawable.op07_004, R.drawable.op07_005, R.drawable.op07_005_p1, R.drawable.op07_006, R.drawable.op07_007, R.drawable.op07_008, R.drawable.op07_009, R.drawable.op07_010, R.drawable.op07_011, R.drawable.op07_012, R.drawable.op07_013, R.drawable.op07_014, R.drawable.op07_015, R.drawable.op07_015_p1, R.drawable.op07_016, R.drawable.op07_017, R.drawable.op07_018, R.drawable.op07_019, R.drawable.op07_019_p1, R.drawable.op07_020, R.drawable.op07_021, R.drawable.op07_022, R.drawable.op07_022_p1, R.drawable.op07_023, R.drawable.op07_024, R.drawable.op07_025, R.drawable.op07_026, R.drawable.op07_026_p1, R.drawable.op07_027, R.drawable.op07_028, R.drawable.op07_029, R.drawable.op07_029_p1, R.drawable.op07_030, R.drawable.op07_031, R.drawable.op07_032, R.drawable.op07_033, R.drawable.op07_034, R.drawable.op07_035, R.drawable.op07_036, R.drawable.op07_037, R.drawable.op07_038, R.drawable.op07_038_p1, R.drawable.op07_039, R.drawable.op07_040, R.drawable.op07_041, R.drawable.op07_042, R.drawable.op07_043, R.drawable.op07_044, R.drawable.op07_045, R.drawable.op07_045_p1, R.drawable.op07_046, R.drawable.op07_046_p1, R.drawable.op07_047, R.drawable.op07_047_p1, R.drawable.op07_048, R.drawable.op07_049, R.drawable.op07_050, R.drawable.op07_051, R.drawable.op07_051_p1, R.drawable.op07_051_p2, R.drawable.op07_052, R.drawable.op07_053, R.drawable.op07_054, R.drawable.op07_055, R.drawable.op07_056, R.drawable.op07_057, R.drawable.op07_058, R.drawable.op07_059, R.drawable.op07_059_p1, R.drawable.op07_060, R.drawable.op07_061, R.drawable.op07_062, R.drawable.op07_063, R.drawable.op07_064, R.drawable.op07_064_p1, R.drawable.op07_065, R.drawable.op07_066, R.drawable.op07_067, R.drawable.op07_068, R.drawable.op07_069, R.drawable.op07_070, R.drawable.op07_071, R.drawable.op07_071_p1, R.drawable.op07_072, R.drawable.op07_072_p1, R.drawable.op07_073, R.drawable.op07_074, R.drawable.op07_075, R.drawable.op07_076, R.drawable.op07_077, R.drawable.op07_078, R.drawable.op07_079, R.drawable.op07_079_p1, R.drawable.op07_080, R.drawable.op07_081, R.drawable.op07_082, R.drawable.op07_083, R.drawable.op07_084, R.drawable.op07_085, R.drawable.op07_085_p1, R.drawable.op07_086, R.drawable.op07_087, R.drawable.op07_088, R.drawable.op07_089, R.drawable.op07_090, R.drawable.op07_091, R.drawable.op07_092, R.drawable.op07_093, R.drawable.op07_094, R.drawable.op07_095, R.drawable.op07_096, R.drawable.op07_097, R.drawable.op07_097_p1, R.drawable.op07_098, R.drawable.op07_099, R.drawable.op07_100, R.drawable.op07_101, R.drawable.op07_102, R.drawable.op07_103, R.drawable.op07_104, R.drawable.op07_105, R.drawable.op07_106, R.drawable.op07_107, R.drawable.op07_107_p1, R.drawable.op07_108, R.drawable.op07_109, R.drawable.op07_109_p1, R.drawable.op07_110, R.drawable.op07_111, R.drawable.op07_111_p1, R.drawable.op07_112, R.drawable.op07_113, R.drawable.op07_114, R.drawable.op07_115, R.drawable.op07_116, R.drawable.op07_117, R.drawable.op07_118, R.drawable.op07_118_p1, R.drawable.op07_119, R.drawable.op07_119_p1
        );
        // Set up adapter with only images
        setAdapter = new SetAdapter(getContext(), menuImages);
        recyclerView.setAdapter(setAdapter);

        progressCircle = view.findViewById(R.id.op07ProgressCircle);
        progressText = view.findViewById(R.id.op07ProgressText);
        progressC = view.findViewById(R.id.op07CCollected);
        progressUC = view.findViewById(R.id.op07UCCollected);
        progressR = view.findViewById(R.id.op07RCollected);
        progressSR = view.findViewById(R.id.op07SRCollected);
        progressL = view.findViewById(R.id.op07LCollected);
        progressSEC = view.findViewById(R.id.op07SECCollected);
        progressMR = view.findViewById(R.id.op07MRCollected);
        progressSP = view.findViewById(R.id.op07SPCollected);

        sharedPreferences = requireActivity().getSharedPreferences("COLLECTION_PREFS", MODE_PRIVATE);
        int totalCount = sharedPreferences.getInt("op07_total_count", 0);
        float progress = ((float) totalCount / 150) * 100;
        progressCircle.setProgress(progress);
        progressText.setText((int) progress + "%");

        int totalC = sharedPreferences.getInt("op07_total_c", 0);
        progressC.setText(totalC + "/45");

        int totalUC = sharedPreferences.getInt("op07_total_uc", 0);
        progressUC.setText(totalUC + "/30");

        int totalR = sharedPreferences.getInt("op07_total_r", 0);
        progressR.setText(totalR + "/32");

        int totalSR = sharedPreferences.getInt("op07_total_sr", 0);
        progressSR.setText(totalSR + "/20");

        int totalL = sharedPreferences.getInt("op07_total_l", 0);
        progressL.setText(totalL + "/12");

        int totalSEC = sharedPreferences.getInt("op07_total_sec", 0);
        progressSEC.setText(totalSEC + "/4");

        int totalMR = sharedPreferences.getInt("op07_total_mr", 0);
        progressMR.setText(totalMR + "/1");

        int totalSP = sharedPreferences.getInt("op07_total_sp", 0);
        progressSP.setText(totalSP + "/6");
    }

    public static OP07CollectionFragment newInstance(String transitionName) {
        OP07CollectionFragment fragment = new OP07CollectionFragment();
        Bundle args = new Bundle();
        args.putString("transitionName", transitionName);
        fragment.setArguments(args);
        return fragment;
    }
}