package com.optcg.app;

import android.os.Bundle;

import java.util.Arrays;

/**
 * Collection screen for set OP08. All behaviour lives in
 * {@link BaseSetCollectionFragment}; this class only supplies the per-set data.
 */
public class OP08CollectionFragment extends BaseSetCollectionFragment {

    public static OP08CollectionFragment newInstance(String transitionName) {
        OP08CollectionFragment fragment = new OP08CollectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRANSITION_NAME, transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected SetCollectionConfig getConfig() {
        return new SetCollectionConfig.Builder("op08", R.layout.fragment_op08_collection_fragment, R.id.op08_set_img,
                R.id.op08CollectionRecyclerView, R.id.op08ProgressCircle, R.id.op08ProgressText, 150)
                .menuImages(Arrays.asList(
                        R.drawable.op02_013_p3,
                        R.drawable.op03_112_p4,
                        R.drawable.op08_001,
                        R.drawable.op08_001_p1,
                        R.drawable.op08_002,
                        R.drawable.op08_002_p1,
                        R.drawable.op08_003,
                        R.drawable.op08_004,
                        R.drawable.op08_005,
                        R.drawable.op08_006,
                        R.drawable.op08_007,
                        R.drawable.op08_007_p1,
                        R.drawable.op08_008,
                        R.drawable.op08_009,
                        R.drawable.op08_010,
                        R.drawable.op08_011,
                        R.drawable.op08_012,
                        R.drawable.op08_013,
                        R.drawable.op08_014,
                        R.drawable.op08_015,
                        R.drawable.op08_015_p1,
                        R.drawable.op08_016,
                        R.drawable.op08_017,
                        R.drawable.op08_018,
                        R.drawable.op08_019,
                        R.drawable.op08_020,
                        R.drawable.op08_021,
                        R.drawable.op08_021_p1,
                        R.drawable.op08_022,
                        R.drawable.op08_023,
                        R.drawable.op08_023_p1,
                        R.drawable.op08_024,
                        R.drawable.op08_025,
                        R.drawable.op08_026,
                        R.drawable.op08_027,
                        R.drawable.op08_028,
                        R.drawable.op08_029,
                        R.drawable.op08_030,
                        R.drawable.op08_030_p1,
                        R.drawable.op08_031,
                        R.drawable.op08_032,
                        R.drawable.op08_033,
                        R.drawable.op08_034,
                        R.drawable.op08_035,
                        R.drawable.op08_036,
                        R.drawable.op08_037,
                        R.drawable.op08_038,
                        R.drawable.op08_039,
                        R.drawable.op08_040,
                        R.drawable.op08_041,
                        R.drawable.op08_042,
                        R.drawable.op08_043,
                        R.drawable.op08_043_p1,
                        R.drawable.op08_044,
                        R.drawable.op08_045,
                        R.drawable.op08_046,
                        R.drawable.op08_047,
                        R.drawable.op08_048,
                        R.drawable.op08_049,
                        R.drawable.op08_050,
                        R.drawable.op08_051,
                        R.drawable.op08_052,
                        R.drawable.op08_052_p1,
                        R.drawable.op08_053,
                        R.drawable.op08_054,
                        R.drawable.op08_055,
                        R.drawable.op08_056,
                        R.drawable.op08_057,
                        R.drawable.op08_057_p1,
                        R.drawable.op08_058,
                        R.drawable.op08_058_p1,
                        R.drawable.op08_059,
                        R.drawable.op08_060,
                        R.drawable.op08_061,
                        R.drawable.op08_062,
                        R.drawable.op08_063,
                        R.drawable.op08_064,
                        R.drawable.op08_065,
                        R.drawable.op08_066,
                        R.drawable.op08_067,
                        R.drawable.op08_067_p1,
                        R.drawable.op08_068,
                        R.drawable.op08_069,
                        R.drawable.op08_069_p1,
                        R.drawable.op08_070,
                        R.drawable.op08_071,
                        R.drawable.op08_072,
                        R.drawable.op08_073,
                        R.drawable.op08_074,
                        R.drawable.op08_074_p1,
                        R.drawable.op08_075,
                        R.drawable.op08_076,
                        R.drawable.op08_077,
                        R.drawable.op08_078,
                        R.drawable.op08_079,
                        R.drawable.op08_079_p1,
                        R.drawable.op08_080,
                        R.drawable.op08_080_p1,
                        R.drawable.op08_081,
                        R.drawable.op08_082,
                        R.drawable.op08_083,
                        R.drawable.op08_084,
                        R.drawable.op08_084_p1,
                        R.drawable.op08_085,
                        R.drawable.op08_086,
                        R.drawable.op08_087,
                        R.drawable.op08_088,
                        R.drawable.op08_089,
                        R.drawable.op08_090,
                        R.drawable.op08_091,
                        R.drawable.op08_092,
                        R.drawable.op08_093,
                        R.drawable.op08_094,
                        R.drawable.op08_095,
                        R.drawable.op08_096,
                        R.drawable.op08_097,
                        R.drawable.op08_098,
                        R.drawable.op08_098_p1,
                        R.drawable.op08_099,
                        R.drawable.op08_100,
                        R.drawable.op08_101,
                        R.drawable.op08_102,
                        R.drawable.op08_103,
                        R.drawable.op08_104,
                        R.drawable.op08_105,
                        R.drawable.op08_105_p1,
                        R.drawable.op08_106,
                        R.drawable.op08_106_p1,
                        R.drawable.op08_107,
                        R.drawable.op08_108,
                        R.drawable.op08_109,
                        R.drawable.op08_110,
                        R.drawable.op08_110_p1,
                        R.drawable.op08_111,
                        R.drawable.op08_112,
                        R.drawable.op08_112_p1,
                        R.drawable.op08_113,
                        R.drawable.op08_114,
                        R.drawable.op08_115,
                        R.drawable.op08_116,
                        R.drawable.op08_117,
                        R.drawable.op08_118,
                        R.drawable.op08_118_p1,
                        R.drawable.op08_118_p2,
                        R.drawable.op08_119,
                        R.drawable.op08_119_p1,
                        R.drawable.op_st02_007_p3,
                        R.drawable.op_st03_004_p2,
                        R.drawable.op_st04_005_p3,
                        R.drawable.op_st06_006_p2
                ))
                .row(R.id.op08CCollected, "c", 45)
                .row(R.id.op08UCCollected, "uc", 30)
                .row(R.id.op08RCollected, "r", 32)
                .row(R.id.op08SRCollected, "sr", 20)
                .row(R.id.op08LCollected, "l", 12)
                .row(R.id.op08SECCollected, "sec", 4)
                .row(R.id.op08MRCollected, "mr", 1)
                .row(R.id.op08SPCollected, "sp", 6)
                .build();
    }
}
