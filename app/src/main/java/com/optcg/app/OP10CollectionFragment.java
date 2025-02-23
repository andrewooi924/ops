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

public class OP10CollectionFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_op10_collection_fragment, container, false);
        ImageView imageView = view.findViewById(R.id.op10_set_img);
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
        recyclerView = view.findViewById(R.id.op10CollectionRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 columns

        // Initialize list of image resources
        menuImages = Arrays.asList(
            R.drawable.eb01_056_p2, R.drawable.op07_021_p1, R.drawable.op10_001, R.drawable.op10_001_p1, R.drawable.op10_002, R.drawable.op10_002_p1, R.drawable.op10_003, R.drawable.op10_003_p1, R.drawable.op10_004, R.drawable.op10_005, R.drawable.op10_005_p1, R.drawable.op10_006, R.drawable.op10_007, R.drawable.op10_008, R.drawable.op10_009, R.drawable.op10_010, R.drawable.op10_011, R.drawable.op10_012, R.drawable.op10_013, R.drawable.op10_014, R.drawable.op10_015, R.drawable.op10_016, R.drawable.op10_016_p1, R.drawable.op10_017, R.drawable.op10_018, R.drawable.op10_019, R.drawable.op10_019_p1, R.drawable.op10_020, R.drawable.op10_021, R.drawable.op10_022, R.drawable.op10_022_p1, R.drawable.op10_023, R.drawable.op10_024, R.drawable.op10_025, R.drawable.op10_026, R.drawable.op10_027, R.drawable.op10_028, R.drawable.op10_029, R.drawable.op10_030, R.drawable.op10_030_p1, R.drawable.op10_031, R.drawable.op10_032, R.drawable.op10_032_p1, R.drawable.op10_033, R.drawable.op10_034, R.drawable.op10_035, R.drawable.op10_036, R.drawable.op10_037, R.drawable.op10_037_p1, R.drawable.op10_038, R.drawable.op10_039, R.drawable.op10_040, R.drawable.op10_041, R.drawable.op10_042, R.drawable.op10_042_p1, R.drawable.op10_043, R.drawable.op10_044, R.drawable.op10_045, R.drawable.op10_045_p1, R.drawable.op10_046, R.drawable.op10_046_p1, R.drawable.op10_047, R.drawable.op10_048, R.drawable.op10_049, R.drawable.op10_050, R.drawable.op10_051, R.drawable.op10_052, R.drawable.op10_053, R.drawable.op10_054, R.drawable.op10_055, R.drawable.op10_056, R.drawable.op10_057, R.drawable.op10_058, R.drawable.op10_058_p1, R.drawable.op10_059, R.drawable.op10_060, R.drawable.op10_061, R.drawable.op10_062, R.drawable.op10_063, R.drawable.op10_064, R.drawable.op10_065, R.drawable.op10_066, R.drawable.op10_067, R.drawable.op10_067_p1, R.drawable.op10_068, R.drawable.op10_069, R.drawable.op10_070, R.drawable.op10_071, R.drawable.op10_071_p1, R.drawable.op10_072, R.drawable.op10_072_p1, R.drawable.op10_073, R.drawable.op10_074, R.drawable.op10_075, R.drawable.op10_076, R.drawable.op10_077, R.drawable.op10_078, R.drawable.op10_079, R.drawable.op10_080, R.drawable.op10_081, R.drawable.op10_082, R.drawable.op10_082_p1, R.drawable.op10_083, R.drawable.op10_084, R.drawable.op10_085, R.drawable.op10_086, R.drawable.op10_087, R.drawable.op10_088, R.drawable.op10_089, R.drawable.op10_090, R.drawable.op10_090_p1, R.drawable.op10_091, R.drawable.op10_092, R.drawable.op10_093, R.drawable.op10_094, R.drawable.op10_095, R.drawable.op10_096, R.drawable.op10_097, R.drawable.op10_098, R.drawable.op10_099, R.drawable.op10_099_p1, R.drawable.op10_100, R.drawable.op10_101, R.drawable.op10_102, R.drawable.op10_103, R.drawable.op10_104, R.drawable.op10_105, R.drawable.op10_106, R.drawable.op10_107, R.drawable.op10_108, R.drawable.op10_109, R.drawable.op10_110, R.drawable.op10_111, R.drawable.op10_111_p1, R.drawable.op10_112, R.drawable.op10_112_p1, R.drawable.op10_113, R.drawable.op10_114, R.drawable.op10_115, R.drawable.op10_116, R.drawable.op10_117, R.drawable.op10_118, R.drawable.op10_118_p1, R.drawable.op10_119, R.drawable.op10_119_p1, R.drawable.op10_119_p2, R.drawable.st12_012_p1, R.drawable.st14_003_p1, R.drawable.st15_002_p1, R.drawable.st18_001_p1
        );

        // Set up adapter with only images
        setAdapter = new SetAdapter(getContext(), menuImages);
        recyclerView.setAdapter(setAdapter);

        progressCircle = view.findViewById(R.id.op10ProgressCircle);
        progressText = view.findViewById(R.id.op10ProgressText);
        progressC = view.findViewById(R.id.op10CCollected);
        progressUC = view.findViewById(R.id.op10UCCollected);
        progressR = view.findViewById(R.id.op10RCollected);
        progressSR = view.findViewById(R.id.op10SRCollected);
        progressL = view.findViewById(R.id.op10LCollected);
        progressSEC = view.findViewById(R.id.op10SECCollected);
        progressMR = view.findViewById(R.id.op10MRCollected);
        progressSP = view.findViewById(R.id.op10SPCollected);

        sharedPreferences = requireActivity().getSharedPreferences("COLLECTION_PREFS", MODE_PRIVATE);
        int totalCount = sharedPreferences.getInt("op10_total_count", 0);
        float progress = ((float) totalCount / 150) * 100;
        progressCircle.setProgress(progress);
        progressText.setText((int) progress + "%");

        int totalC = sharedPreferences.getInt("op10_total_c", 0);
        progressC.setText(totalC + "/45");

        int totalUC = sharedPreferences.getInt("op10_total_uc", 0);
        progressUC.setText(totalUC + "/30");

        int totalR = sharedPreferences.getInt("op10_total_r", 0);
        progressR.setText(totalR + "/32");

        int totalSR = sharedPreferences.getInt("op10_total_sr", 0);
        progressSR.setText(totalSR + "/20");

        int totalL = sharedPreferences.getInt("op10_total_l", 0);
        progressL.setText(totalL + "/12");

        int totalSEC = sharedPreferences.getInt("op10_total_sec", 0);
        progressSEC.setText(totalSEC + "/4");

        int totalMR = sharedPreferences.getInt("op10_total_mr", 0);
        progressMR.setText(totalMR + "/1");

        int totalSP = sharedPreferences.getInt("op10_total_sp", 0);
        progressSP.setText(totalSP + "/6");
    }

    public static OP10CollectionFragment newInstance(String transitionName) {
        OP10CollectionFragment fragment = new OP10CollectionFragment();
        Bundle args = new Bundle();
        args.putString("transitionName", transitionName);
        fragment.setArguments(args);
        return fragment;
    }
}