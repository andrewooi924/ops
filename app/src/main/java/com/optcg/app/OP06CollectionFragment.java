package com.optcg.app;

import android.os.Bundle;

import java.util.Arrays;

/**
 * Collection screen for set OP06. All behaviour lives in
 * {@link BaseSetCollectionFragment}; this class only supplies the per-set data.
 */
public class OP06CollectionFragment extends BaseSetCollectionFragment {

    public static OP06CollectionFragment newInstance(String transitionName) {
        OP06CollectionFragment fragment = new OP06CollectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRANSITION_NAME, transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected SetCollectionConfig getConfig() {
        return new SetCollectionConfig.Builder("op06", R.layout.fragment_op06_collection_fragment, R.id.op06_set_img,
                R.id.op06CollectionRecyclerView, R.id.op06ProgressCircle, R.id.op06ProgressText, 150)
                .menuImages(Arrays.asList(
                        R.drawable.op03_008_p1,
                        R.drawable.op03_114_p2,
                        R.drawable.op04_024_p2,
                        R.drawable.op04_064_p2,
                        R.drawable.op05_051_p2,
                        R.drawable.op05_091_p2,
                        R.drawable.op06_001,
                        R.drawable.op06_001_p1,
                        R.drawable.op06_002,
                        R.drawable.op06_003,
                        R.drawable.op06_004,
                        R.drawable.op06_005,
                        R.drawable.op06_006,
                        R.drawable.op06_007,
                        R.drawable.op06_007_p1,
                        R.drawable.op06_008,
                        R.drawable.op06_009,
                        R.drawable.op06_009_p1,
                        R.drawable.op06_010,
                        R.drawable.op06_011,
                        R.drawable.op06_012,
                        R.drawable.op06_013,
                        R.drawable.op06_013_p1,
                        R.drawable.op06_014,
                        R.drawable.op06_015,
                        R.drawable.op06_016,
                        R.drawable.op06_017,
                        R.drawable.op06_018,
                        R.drawable.op06_019,
                        R.drawable.op06_020,
                        R.drawable.op06_020_p1,
                        R.drawable.op06_021,
                        R.drawable.op06_021_p1,
                        R.drawable.op06_022,
                        R.drawable.op06_022_p1,
                        R.drawable.op06_023,
                        R.drawable.op06_024,
                        R.drawable.op06_025,
                        R.drawable.op06_025_p1,
                        R.drawable.op06_026,
                        R.drawable.op06_027,
                        R.drawable.op06_028,
                        R.drawable.op06_029,
                        R.drawable.op06_030,
                        R.drawable.op06_031,
                        R.drawable.op06_032,
                        R.drawable.op06_033,
                        R.drawable.op06_034,
                        R.drawable.op06_035,
                        R.drawable.op06_035_p1,
                        R.drawable.op06_036,
                        R.drawable.op06_037,
                        R.drawable.op06_038,
                        R.drawable.op06_039,
                        R.drawable.op06_040,
                        R.drawable.op06_041,
                        R.drawable.op06_042,
                        R.drawable.op06_042_p1,
                        R.drawable.op06_043,
                        R.drawable.op06_043_p1,
                        R.drawable.op06_044,
                        R.drawable.op06_045,
                        R.drawable.op06_046,
                        R.drawable.op06_047,
                        R.drawable.op06_048,
                        R.drawable.op06_049,
                        R.drawable.op06_050,
                        R.drawable.op06_050_p1,
                        R.drawable.op06_051,
                        R.drawable.op06_052,
                        R.drawable.op06_053,
                        R.drawable.op06_054,
                        R.drawable.op06_055,
                        R.drawable.op06_056,
                        R.drawable.op06_057,
                        R.drawable.op06_058,
                        R.drawable.op06_059,
                        R.drawable.op06_060,
                        R.drawable.op06_061,
                        R.drawable.op06_061_p1,
                        R.drawable.op06_062,
                        R.drawable.op06_062_p1,
                        R.drawable.op06_063,
                        R.drawable.op06_064,
                        R.drawable.op06_065,
                        R.drawable.op06_066,
                        R.drawable.op06_067,
                        R.drawable.op06_068,
                        R.drawable.op06_069,
                        R.drawable.op06_069_p1,
                        R.drawable.op06_070,
                        R.drawable.op06_071,
                        R.drawable.op06_072,
                        R.drawable.op06_073,
                        R.drawable.op06_074,
                        R.drawable.op06_075,
                        R.drawable.op06_076,
                        R.drawable.op06_077,
                        R.drawable.op06_078,
                        R.drawable.op06_079,
                        R.drawable.op06_080,
                        R.drawable.op06_080_p1,
                        R.drawable.op06_081,
                        R.drawable.op06_081_p1,
                        R.drawable.op06_082,
                        R.drawable.op06_083,
                        R.drawable.op06_084,
                        R.drawable.op06_085,
                        R.drawable.op06_086,
                        R.drawable.op06_086_p1,
                        R.drawable.op06_087,
                        R.drawable.op06_088,
                        R.drawable.op06_089,
                        R.drawable.op06_090,
                        R.drawable.op06_091,
                        R.drawable.op06_092,
                        R.drawable.op06_093,
                        R.drawable.op06_093_p1,
                        R.drawable.op06_094,
                        R.drawable.op06_095,
                        R.drawable.op06_096,
                        R.drawable.op06_097,
                        R.drawable.op06_098,
                        R.drawable.op06_099,
                        R.drawable.op06_100,
                        R.drawable.op06_101,
                        R.drawable.op06_101_p1,
                        R.drawable.op06_102,
                        R.drawable.op06_103,
                        R.drawable.op06_104,
                        R.drawable.op06_105,
                        R.drawable.op06_106,
                        R.drawable.op06_106_p1,
                        R.drawable.op06_107,
                        R.drawable.op06_107_p1,
                        R.drawable.op06_108,
                        R.drawable.op06_109,
                        R.drawable.op06_110,
                        R.drawable.op06_111,
                        R.drawable.op06_112,
                        R.drawable.op06_113,
                        R.drawable.op06_114,
                        R.drawable.op06_115,
                        R.drawable.op06_116,
                        R.drawable.op06_117,
                        R.drawable.op06_118,
                        R.drawable.op06_118_p1,
                        R.drawable.op06_118_p2,
                        R.drawable.op06_119,
                        R.drawable.op06_119_p1
                ))
                .row(R.id.op06CCollected, "c", 45)
                .row(R.id.op06UCCollected, "uc", 30)
                .row(R.id.op06RCollected, "r", 32)
                .row(R.id.op06SRCollected, "sr", 20)
                .row(R.id.op06LCollected, "l", 12)
                .row(R.id.op06SECCollected, "sec", 4)
                .row(R.id.op06MRCollected, "mr", 1)
                .row(R.id.op06SPCollected, "sp", 6)
                .build();
    }
}
