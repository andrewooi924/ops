package com.optcg.app;

import android.os.Bundle;

import java.util.Arrays;

/**
 * Collection screen for set OP10. All behaviour lives in
 * {@link BaseSetCollectionFragment}; this class only supplies the per-set data.
 */
public class OP10CollectionFragment extends BaseSetCollectionFragment {

    public static OP10CollectionFragment newInstance(String transitionName) {
        OP10CollectionFragment fragment = new OP10CollectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRANSITION_NAME, transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected SetCollectionConfig getConfig() {
        return new SetCollectionConfig.Builder("op10", R.layout.fragment_op10_collection_fragment, R.id.op10_set_img,
                R.id.op10CollectionRecyclerView, R.id.op10ProgressCircle, R.id.op10ProgressText, 150)
                .menuImages(Arrays.asList(
                        R.drawable.eb01_056_p2,
                        R.drawable.op07_021_p1,
                        R.drawable.op10_001,
                        R.drawable.op10_001_p1,
                        R.drawable.op10_002,
                        R.drawable.op10_002_p1,
                        R.drawable.op10_003,
                        R.drawable.op10_003_p1,
                        R.drawable.op10_004,
                        R.drawable.op10_005,
                        R.drawable.op10_005_p1,
                        R.drawable.op10_006,
                        R.drawable.op10_007,
                        R.drawable.op10_008,
                        R.drawable.op10_009,
                        R.drawable.op10_010,
                        R.drawable.op10_011,
                        R.drawable.op10_012,
                        R.drawable.op10_013,
                        R.drawable.op10_014,
                        R.drawable.op10_015,
                        R.drawable.op10_016,
                        R.drawable.op10_016_p1,
                        R.drawable.op10_017,
                        R.drawable.op10_018,
                        R.drawable.op10_019,
                        R.drawable.op10_019_p1,
                        R.drawable.op10_020,
                        R.drawable.op10_021,
                        R.drawable.op10_022,
                        R.drawable.op10_022_p1,
                        R.drawable.op10_023,
                        R.drawable.op10_024,
                        R.drawable.op10_025,
                        R.drawable.op10_026,
                        R.drawable.op10_027,
                        R.drawable.op10_028,
                        R.drawable.op10_029,
                        R.drawable.op10_030,
                        R.drawable.op10_030_p1,
                        R.drawable.op10_031,
                        R.drawable.op10_032,
                        R.drawable.op10_032_p1,
                        R.drawable.op10_033,
                        R.drawable.op10_034,
                        R.drawable.op10_035,
                        R.drawable.op10_036,
                        R.drawable.op10_037,
                        R.drawable.op10_037_p1,
                        R.drawable.op10_038,
                        R.drawable.op10_039,
                        R.drawable.op10_040,
                        R.drawable.op10_041,
                        R.drawable.op10_042,
                        R.drawable.op10_042_p1,
                        R.drawable.op10_043,
                        R.drawable.op10_044,
                        R.drawable.op10_045,
                        R.drawable.op10_045_p1,
                        R.drawable.op10_046,
                        R.drawable.op10_046_p1,
                        R.drawable.op10_047,
                        R.drawable.op10_048,
                        R.drawable.op10_049,
                        R.drawable.op10_050,
                        R.drawable.op10_051,
                        R.drawable.op10_052,
                        R.drawable.op10_053,
                        R.drawable.op10_054,
                        R.drawable.op10_055,
                        R.drawable.op10_056,
                        R.drawable.op10_057,
                        R.drawable.op10_058,
                        R.drawable.op10_058_p1,
                        R.drawable.op10_059,
                        R.drawable.op10_060,
                        R.drawable.op10_061,
                        R.drawable.op10_062,
                        R.drawable.op10_063,
                        R.drawable.op10_064,
                        R.drawable.op10_065,
                        R.drawable.op10_066,
                        R.drawable.op10_067,
                        R.drawable.op10_067_p1,
                        R.drawable.op10_068,
                        R.drawable.op10_069,
                        R.drawable.op10_070,
                        R.drawable.op10_071,
                        R.drawable.op10_071_p1,
                        R.drawable.op10_072,
                        R.drawable.op10_072_p1,
                        R.drawable.op10_073,
                        R.drawable.op10_074,
                        R.drawable.op10_075,
                        R.drawable.op10_076,
                        R.drawable.op10_077,
                        R.drawable.op10_078,
                        R.drawable.op10_079,
                        R.drawable.op10_080,
                        R.drawable.op10_081,
                        R.drawable.op10_082,
                        R.drawable.op10_082_p1,
                        R.drawable.op10_083,
                        R.drawable.op10_084,
                        R.drawable.op10_085,
                        R.drawable.op10_086,
                        R.drawable.op10_087,
                        R.drawable.op10_088,
                        R.drawable.op10_089,
                        R.drawable.op10_090,
                        R.drawable.op10_090_p1,
                        R.drawable.op10_091,
                        R.drawable.op10_092,
                        R.drawable.op10_093,
                        R.drawable.op10_094,
                        R.drawable.op10_095,
                        R.drawable.op10_096,
                        R.drawable.op10_097,
                        R.drawable.op10_098,
                        R.drawable.op10_099,
                        R.drawable.op10_099_p1,
                        R.drawable.op10_100,
                        R.drawable.op10_101,
                        R.drawable.op10_102,
                        R.drawable.op10_103,
                        R.drawable.op10_104,
                        R.drawable.op10_105,
                        R.drawable.op10_106,
                        R.drawable.op10_107,
                        R.drawable.op10_108,
                        R.drawable.op10_109,
                        R.drawable.op10_110,
                        R.drawable.op10_111,
                        R.drawable.op10_111_p1,
                        R.drawable.op10_112,
                        R.drawable.op10_112_p1,
                        R.drawable.op10_113,
                        R.drawable.op10_114,
                        R.drawable.op10_115,
                        R.drawable.op10_116,
                        R.drawable.op10_117,
                        R.drawable.op10_118,
                        R.drawable.op10_118_p1,
                        R.drawable.op10_119,
                        R.drawable.op10_119_p1,
                        R.drawable.op10_119_p2,
                        R.drawable.st12_012_p1,
                        R.drawable.st14_003_p1,
                        R.drawable.st15_002_p1,
                        R.drawable.st18_001_p1
                ))
                .row(R.id.op10CCollected, "c", 45)
                .row(R.id.op10UCCollected, "uc", 30)
                .row(R.id.op10RCollected, "r", 32)
                .row(R.id.op10SRCollected, "sr", 20)
                .row(R.id.op10LCollected, "l", 12)
                .row(R.id.op10SECCollected, "sec", 4)
                .row(R.id.op10MRCollected, "mr", 1)
                .row(R.id.op10SPCollected, "sp", 6)
                .build();
    }
}
