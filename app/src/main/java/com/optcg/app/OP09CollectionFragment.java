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

public class OP09CollectionFragment extends Fragment {

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
    private TextView progressGSP;
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
        View view = inflater.inflate(R.layout.fragment_op09_collection_fragment, container, false);
        ImageView imageView = view.findViewById(R.id.op09_set_img);
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
        recyclerView = view.findViewById(R.id.op09CollectionRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 columns

        // Initialize list of image resources
        menuImages = Arrays.asList(
                R.drawable.op04_119_p2, R.drawable.op05_067_p4, R.drawable.op05_093_p2, R.drawable.op05_119_p5, R.drawable.op07_015_p2, R.drawable.op07_051_p3, R.drawable.op08_106_p4, R.drawable.op09_001, R.drawable.op09_001_p1, R.drawable.op09_002, R.drawable.op09_002_p1, R.drawable.op09_003, R.drawable.op09_004, R.drawable.op09_004_p1, R.drawable.op09_004_p2, R.drawable.op09_004_p3, R.drawable.op09_005, R.drawable.op09_006, R.drawable.op09_007, R.drawable.op09_008, R.drawable.op09_009, R.drawable.op09_009_p1, R.drawable.op09_010, R.drawable.op09_011, R.drawable.op09_012, R.drawable.op09_013, R.drawable.op09_014, R.drawable.op09_015, R.drawable.op09_016, R.drawable.op09_017, R.drawable.op09_018, R.drawable.op09_019, R.drawable.op09_020, R.drawable.op09_021, R.drawable.op09_022, R.drawable.op09_022_p1, R.drawable.op09_023, R.drawable.op09_023_p1, R.drawable.op09_024, R.drawable.op09_025, R.drawable.op09_026, R.drawable.op09_027, R.drawable.op09_028, R.drawable.op09_029, R.drawable.op09_030, R.drawable.op09_031, R.drawable.op09_032, R.drawable.op09_033, R.drawable.op09_034, R.drawable.op09_034_p1, R.drawable.op09_035, R.drawable.op09_036, R.drawable.op09_037, R.drawable.op09_037_p1, R.drawable.op09_038, R.drawable.op09_039, R.drawable.op09_040, R.drawable.op09_041, R.drawable.op09_042, R.drawable.op09_042_p1, R.drawable.op09_043, R.drawable.op09_044, R.drawable.op09_045, R.drawable.op09_046, R.drawable.op09_046_p1, R.drawable.op09_047, R.drawable.op09_048, R.drawable.op09_048_p1, R.drawable.op09_049, R.drawable.op09_050, R.drawable.op09_050_p1, R.drawable.op09_051, R.drawable.op09_051_p1, R.drawable.op09_051_p2, R.drawable.op09_051_p3, R.drawable.op09_052, R.drawable.op09_053, R.drawable.op09_054, R.drawable.op09_055, R.drawable.op09_056, R.drawable.op09_057, R.drawable.op09_058, R.drawable.op09_059, R.drawable.op09_060, R.drawable.op09_061, R.drawable.op09_061_p1, R.drawable.op09_062, R.drawable.op09_062_p1, R.drawable.op09_063, R.drawable.op09_064, R.drawable.op09_065, R.drawable.op09_065_p1, R.drawable.op09_066, R.drawable.op09_067, R.drawable.op09_068, R.drawable.op09_069, R.drawable.op09_069_p1, R.drawable.op09_070, R.drawable.op09_071, R.drawable.op09_072, R.drawable.op09_072_p1, R.drawable.op09_073, R.drawable.op09_074, R.drawable.op09_075, R.drawable.op09_076, R.drawable.op09_077, R.drawable.op09_078, R.drawable.op09_079, R.drawable.op09_080, R.drawable.op09_081, R.drawable.op09_081_p1, R.drawable.op09_082, R.drawable.op09_083, R.drawable.op09_084, R.drawable.op09_085, R.drawable.op09_086, R.drawable.op09_087, R.drawable.op09_088, R.drawable.op09_089, R.drawable.op09_090, R.drawable.op09_091, R.drawable.op09_092, R.drawable.op09_093, R.drawable.op09_093_p1, R.drawable.op09_093_p2, R.drawable.op09_093_p3, R.drawable.op09_094, R.drawable.op09_095, R.drawable.op09_096, R.drawable.op09_097, R.drawable.op09_098, R.drawable.op09_099, R.drawable.op09_100, R.drawable.op09_101, R.drawable.op09_102, R.drawable.op09_103, R.drawable.op09_103_p1, R.drawable.op09_104, R.drawable.op09_105, R.drawable.op09_106, R.drawable.op09_107, R.drawable.op09_107_p1, R.drawable.op09_108, R.drawable.op09_109, R.drawable.op09_110, R.drawable.op09_111, R.drawable.op09_112, R.drawable.op09_113, R.drawable.op09_114, R.drawable.op09_115, R.drawable.op09_116, R.drawable.op09_117, R.drawable.op09_118, R.drawable.op09_118_p1, R.drawable.op09_118_p2, R.drawable.op09_119, R.drawable.op09_119_p1, R.drawable.op09_119_p2
        );

        // Set up adapter with only images
        setAdapter = new SetAdapter(getContext(), menuImages);
        recyclerView.setAdapter(setAdapter);

        progressCircle = view.findViewById(R.id.op09ProgressCircle);
        progressText = view.findViewById(R.id.op09ProgressText);
        progressC = view.findViewById(R.id.op09CCollected);
        progressUC = view.findViewById(R.id.op09UCCollected);
        progressR = view.findViewById(R.id.op09RCollected);
        progressSR = view.findViewById(R.id.op09SRCollected);
        progressL = view.findViewById(R.id.op09LCollected);
        progressSEC = view.findViewById(R.id.op09SECCollected);
        progressMR = view.findViewById(R.id.op09MRCollected);
        progressSP = view.findViewById(R.id.op09SPCollected);
        progressGSP = view.findViewById(R.id.op09GSPCollected);

        sharedPreferences = requireActivity().getSharedPreferences("COLLECTION_PREFS", MODE_PRIVATE);
        int totalCount = sharedPreferences.getInt("op09_total_count", 0);
        float progress = ((float) totalCount / 158) * 100;
        progressCircle.setProgress(progress);
        progressText.setText((int) progress + "%");

        int totalC = sharedPreferences.getInt("op09_total_c", 0);
        progressC.setText(totalC + "/45");

        int totalUC = sharedPreferences.getInt("op09_total_uc", 0);
        progressUC.setText(totalUC + "/30");

        int totalR = sharedPreferences.getInt("op09_total_r", 0);
        progressR.setText(totalR + "/32");

        int totalSR = sharedPreferences.getInt("op09_total_sr", 0);
        progressSR.setText(totalSR + "/20");

        int totalL = sharedPreferences.getInt("op09_total_l", 0);
        progressL.setText(totalL + "/12");

        int totalSEC = sharedPreferences.getInt("op09_total_sec", 0);
        progressSEC.setText(totalSEC + "/4");

        int totalMR = sharedPreferences.getInt("op09_total_mr", 0);
        progressMR.setText(totalMR + "/4");

        int totalSP = sharedPreferences.getInt("op09_total_sp", 0);
        progressSP.setText(totalSP + "/10");

        int totalGSP = sharedPreferences.getInt("op09_total_gsp", 0);
        progressGSP.setText(totalGSP + "/1");
    }

    public static OP09CollectionFragment newInstance(String transitionName) {
        OP09CollectionFragment fragment = new OP09CollectionFragment();
        Bundle args = new Bundle();
        args.putString("transitionName", transitionName);
        fragment.setArguments(args);
        return fragment;
    }
}