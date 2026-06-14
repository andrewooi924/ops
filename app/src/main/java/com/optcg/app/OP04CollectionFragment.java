package com.optcg.app;

import android.os.Bundle;

import java.util.Arrays;

/**
 * Collection screen for set OP04. All behaviour lives in
 * {@link BaseSetCollectionFragment}; this class only supplies the per-set data.
 */
public class OP04CollectionFragment extends BaseSetCollectionFragment {

    public static OP04CollectionFragment newInstance(String transitionName) {
        OP04CollectionFragment fragment = new OP04CollectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRANSITION_NAME, transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected SetCollectionConfig getConfig() {
        return new SetCollectionConfig.Builder("op04", R.layout.fragment_op04_collection_fragment, R.id.op04_set_img,
                R.id.op04CollectionRecyclerView, R.id.op04ProgressCircle, R.id.op04ProgressText, 149)
                .menuImages(Arrays.asList(
                        R.drawable.op01_047_p2,
                        R.drawable.op01_078_p2,
                        R.drawable.op02_004_p2,
                        R.drawable.op02_085_p2,
                        R.drawable.op02_099_p2,
                        R.drawable.op04_001,
                        R.drawable.op04_001_p1,
                        R.drawable.op04_002,
                        R.drawable.op04_003,
                        R.drawable.op04_004,
                        R.drawable.op04_005,
                        R.drawable.op04_006,
                        R.drawable.op04_007,
                        R.drawable.op04_008,
                        R.drawable.op04_009,
                        R.drawable.op04_010,
                        R.drawable.op04_011,
                        R.drawable.op04_012,
                        R.drawable.op04_013,
                        R.drawable.op04_013_p1,
                        R.drawable.op04_014,
                        R.drawable.op04_015,
                        R.drawable.op04_016,
                        R.drawable.op04_017,
                        R.drawable.op04_018,
                        R.drawable.op04_019,
                        R.drawable.op04_019_p1,
                        R.drawable.op04_020,
                        R.drawable.op04_020_p1,
                        R.drawable.op04_021,
                        R.drawable.op04_022,
                        R.drawable.op04_023,
                        R.drawable.op04_024,
                        R.drawable.op04_024_p1,
                        R.drawable.op04_025,
                        R.drawable.op04_026,
                        R.drawable.op04_027,
                        R.drawable.op04_028,
                        R.drawable.op04_028_p1,
                        R.drawable.op04_029,
                        R.drawable.op04_030,
                        R.drawable.op04_030_p1,
                        R.drawable.op04_031,
                        R.drawable.op04_031_p1,
                        R.drawable.op04_032,
                        R.drawable.op04_033,
                        R.drawable.op04_034,
                        R.drawable.op04_035,
                        R.drawable.op04_036,
                        R.drawable.op04_037,
                        R.drawable.op04_038,
                        R.drawable.op04_039,
                        R.drawable.op04_039_p1,
                        R.drawable.op04_040,
                        R.drawable.op04_040_p1,
                        R.drawable.op04_041,
                        R.drawable.op04_042,
                        R.drawable.op04_043,
                        R.drawable.op04_044,
                        R.drawable.op04_044_p1,
                        R.drawable.op04_045,
                        R.drawable.op04_046,
                        R.drawable.op04_047,
                        R.drawable.op04_048,
                        R.drawable.op04_049,
                        R.drawable.op04_050,
                        R.drawable.op04_051,
                        R.drawable.op04_051_p1,
                        R.drawable.op04_052,
                        R.drawable.op04_053,
                        R.drawable.op04_054,
                        R.drawable.op04_055,
                        R.drawable.op04_056,
                        R.drawable.op04_057,
                        R.drawable.op04_058,
                        R.drawable.op04_058_p1,
                        R.drawable.op04_059,
                        R.drawable.op04_060,
                        R.drawable.op04_060_p1,
                        R.drawable.op04_061,
                        R.drawable.op04_062,
                        R.drawable.op04_063,
                        R.drawable.op04_064,
                        R.drawable.op04_064_p1,
                        R.drawable.op04_065,
                        R.drawable.op04_066,
                        R.drawable.op04_067,
                        R.drawable.op04_068,
                        R.drawable.op04_069,
                        R.drawable.op04_070,
                        R.drawable.op04_071,
                        R.drawable.op04_072,
                        R.drawable.op04_072_p1,
                        R.drawable.op04_073,
                        R.drawable.op04_074,
                        R.drawable.op04_075,
                        R.drawable.op04_076,
                        R.drawable.op04_077,
                        R.drawable.op04_078,
                        R.drawable.op04_079,
                        R.drawable.op04_080,
                        R.drawable.op04_081,
                        R.drawable.op04_082,
                        R.drawable.op04_082_p1,
                        R.drawable.op04_083,
                        R.drawable.op04_083_p1,
                        R.drawable.op04_083_p2,
                        R.drawable.op04_084,
                        R.drawable.op04_085,
                        R.drawable.op04_086,
                        R.drawable.op04_087,
                        R.drawable.op04_088,
                        R.drawable.op04_089,
                        R.drawable.op04_090,
                        R.drawable.op04_090_p1,
                        R.drawable.op04_091,
                        R.drawable.op04_092,
                        R.drawable.op04_093,
                        R.drawable.op04_094,
                        R.drawable.op04_095,
                        R.drawable.op04_096,
                        R.drawable.op04_097,
                        R.drawable.op04_098,
                        R.drawable.op04_099,
                        R.drawable.op04_100,
                        R.drawable.op04_100_p1,
                        R.drawable.op04_101,
                        R.drawable.op04_102,
                        R.drawable.op04_103,
                        R.drawable.op04_104,
                        R.drawable.op04_104_p1,
                        R.drawable.op04_105,
                        R.drawable.op04_106,
                        R.drawable.op04_107,
                        R.drawable.op04_108,
                        R.drawable.op04_109,
                        R.drawable.op04_110,
                        R.drawable.op04_111,
                        R.drawable.op04_112,
                        R.drawable.op04_112_p1,
                        R.drawable.op04_113,
                        R.drawable.op04_114,
                        R.drawable.op04_115,
                        R.drawable.op04_116,
                        R.drawable.op04_117,
                        R.drawable.op04_118,
                        R.drawable.op04_118_p1,
                        R.drawable.op04_119,
                        R.drawable.op04_119_p1
                ))
                .row(R.id.op04CCollected, "c", 45)
                .row(R.id.op04UCCollected, "uc", 30)
                .row(R.id.op04RCollected, "r", 32)
                .row(R.id.op04SRCollected, "sr", 20)
                .row(R.id.op04LCollected, "l", 12)
                .row(R.id.op04SECCollected, "sec", 4)
                .row(R.id.op04MRCollected, "mr", 1)
                .row(R.id.op04SPCollected, "sp", 5)
                .build();
    }
}
