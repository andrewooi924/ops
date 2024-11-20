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

public class OP04CollectionFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_op04_collection_fragment, container, false);
        ImageView imageView = view.findViewById(R.id.op04_set_img);
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
        recyclerView = view.findViewById(R.id.op04CollectionRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 columns

        // Initialize list of image resources
        menuImages = Arrays.asList(
            R.drawable.op01_047_p2, R.drawable.op01_078_p2, R.drawable.op02_004_p2, R.drawable.op02_085_p2, R.drawable.op02_099_p2, R.drawable.op04_001, R.drawable.op04_001_p1, R.drawable.op04_002, R.drawable.op04_003, R.drawable.op04_004, R.drawable.op04_005, R.drawable.op04_006, R.drawable.op04_007, R.drawable.op04_008, R.drawable.op04_009, R.drawable.op04_010, R.drawable.op04_011, R.drawable.op04_012, R.drawable.op04_013, R.drawable.op04_013_p1, R.drawable.op04_014, R.drawable.op04_015, R.drawable.op04_016, R.drawable.op04_017, R.drawable.op04_018, R.drawable.op04_019, R.drawable.op04_019_p1, R.drawable.op04_020, R.drawable.op04_020_p1, R.drawable.op04_021, R.drawable.op04_022, R.drawable.op04_023, R.drawable.op04_024, R.drawable.op04_024_p1, R.drawable.op04_025, R.drawable.op04_026, R.drawable.op04_027, R.drawable.op04_028, R.drawable.op04_028_p1, R.drawable.op04_029, R.drawable.op04_030, R.drawable.op04_030_p1, R.drawable.op04_031, R.drawable.op04_031_p1, R.drawable.op04_032, R.drawable.op04_033, R.drawable.op04_034, R.drawable.op04_035, R.drawable.op04_036, R.drawable.op04_037, R.drawable.op04_038, R.drawable.op04_039, R.drawable.op04_039_p1, R.drawable.op04_040, R.drawable.op04_040_p1, R.drawable.op04_041, R.drawable.op04_042, R.drawable.op04_043, R.drawable.op04_044, R.drawable.op04_044_p1, R.drawable.op04_045, R.drawable.op04_046, R.drawable.op04_047, R.drawable.op04_048, R.drawable.op04_049, R.drawable.op04_050, R.drawable.op04_051, R.drawable.op04_051_p1, R.drawable.op04_052, R.drawable.op04_053, R.drawable.op04_054, R.drawable.op04_055, R.drawable.op04_056, R.drawable.op04_057, R.drawable.op04_058, R.drawable.op04_058_p1, R.drawable.op04_059, R.drawable.op04_060, R.drawable.op04_060_p1, R.drawable.op04_061, R.drawable.op04_062, R.drawable.op04_063, R.drawable.op04_064, R.drawable.op04_064_p1, R.drawable.op04_065, R.drawable.op04_066, R.drawable.op04_067, R.drawable.op04_068, R.drawable.op04_069, R.drawable.op04_070, R.drawable.op04_071, R.drawable.op04_072, R.drawable.op04_072_p1, R.drawable.op04_073, R.drawable.op04_074, R.drawable.op04_075, R.drawable.op04_076, R.drawable.op04_077, R.drawable.op04_078, R.drawable.op04_079, R.drawable.op04_080, R.drawable.op04_081, R.drawable.op04_082, R.drawable.op04_082_p1, R.drawable.op04_083, R.drawable.op04_083_p1, R.drawable.op04_083_p2, R.drawable.op04_084, R.drawable.op04_085, R.drawable.op04_086, R.drawable.op04_087, R.drawable.op04_088, R.drawable.op04_089, R.drawable.op04_090, R.drawable.op04_090_p1, R.drawable.op04_091, R.drawable.op04_092, R.drawable.op04_093, R.drawable.op04_094, R.drawable.op04_095, R.drawable.op04_096, R.drawable.op04_097, R.drawable.op04_098, R.drawable.op04_099, R.drawable.op04_100, R.drawable.op04_100_p1, R.drawable.op04_101, R.drawable.op04_102, R.drawable.op04_103, R.drawable.op04_104, R.drawable.op04_104_p1, R.drawable.op04_105, R.drawable.op04_106, R.drawable.op04_107, R.drawable.op04_108, R.drawable.op04_109, R.drawable.op04_110, R.drawable.op04_111, R.drawable.op04_112, R.drawable.op04_112_p1, R.drawable.op04_113, R.drawable.op04_114, R.drawable.op04_115, R.drawable.op04_116, R.drawable.op04_117, R.drawable.op04_118, R.drawable.op04_118_p1, R.drawable.op04_119, R.drawable.op04_119_p1
        );
        // Set up adapter with only images
        setAdapter = new SetAdapter(getContext(), menuImages);
        recyclerView.setAdapter(setAdapter);

        progressCircle = view.findViewById(R.id.op04ProgressCircle);
        progressText = view.findViewById(R.id.op04ProgressText);
        progressC = view.findViewById(R.id.op04CCollected);
        progressUC = view.findViewById(R.id.op04UCCollected);
        progressR = view.findViewById(R.id.op04RCollected);
        progressSR = view.findViewById(R.id.op04SRCollected);
        progressL = view.findViewById(R.id.op04LCollected);
        progressSEC = view.findViewById(R.id.op04SECCollected);
        progressMR = view.findViewById(R.id.op04MRCollected);
        progressSP = view.findViewById(R.id.op04SPCollected);

        sharedPreferences = requireActivity().getSharedPreferences("COLLECTION_PREFS", MODE_PRIVATE);
        int totalCount = sharedPreferences.getInt("op04_total_count", 0);
        float progress = ((float) totalCount / 149) * 100;
        progressCircle.setProgress(progress);
        progressText.setText((int) progress + "%");

        int totalC = sharedPreferences.getInt("op04_total_c", 0);
        progressC.setText(totalC + "/45");

        int totalUC = sharedPreferences.getInt("op04_total_uc", 0);
        progressUC.setText(totalUC + "/30");

        int totalR = sharedPreferences.getInt("op04_total_r", 0);
        progressR.setText(totalR + "/32");

        int totalSR = sharedPreferences.getInt("op04_total_sr", 0);
        progressSR.setText(totalSR + "/20");

        int totalL = sharedPreferences.getInt("op04_total_l", 0);
        progressL.setText(totalL + "/12");

        int totalSEC = sharedPreferences.getInt("op04_total_sec", 0);
        progressSEC.setText(totalSEC + "/4");

        int totalMR = sharedPreferences.getInt("op04_total_mr", 0);
        progressMR.setText(totalMR + "/1");

        int totalSP = sharedPreferences.getInt("op04_total_sp", 0);
        progressSP.setText(totalSP + "/5");
    }

    public static OP04CollectionFragment newInstance(String transitionName) {
        OP04CollectionFragment fragment = new OP04CollectionFragment();
        Bundle args = new Bundle();
        args.putString("transitionName", transitionName);
        fragment.setArguments(args);
        return fragment;
    }
}