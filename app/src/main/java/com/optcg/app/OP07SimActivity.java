package com.optcg.app;

/**
 * Pack-opening simulator for set OP07. All behaviour lives in
 * {@link BasePackSimActivity}; this class only supplies the per-set data.
 */
public class OP07SimActivity extends BasePackSimActivity {

    @Override
    protected PackSimConfig getConfig() {
        return new PackSimConfig.Builder("op07", R.layout.activity_op07_sim, R.drawable.op07_pack)
                .c(new int[]{
                        R.drawable.op07_006,
                        R.drawable.op07_007,
                        R.drawable.op07_009,
                        R.drawable.op07_011,
                        R.drawable.op07_012,
                        R.drawable.op07_018,
                        R.drawable.op07_020,
                        R.drawable.op07_024,
                        R.drawable.op07_025,
                        R.drawable.op07_027,
                        R.drawable.op07_028,
                        R.drawable.op07_030,
                        R.drawable.op07_035,
                        R.drawable.op07_039,
                        R.drawable.op07_042,
                        R.drawable.op07_044,
                        R.drawable.op07_049,
                        R.drawable.op07_050,
                        R.drawable.op07_052,
                        R.drawable.op07_055,
                        R.drawable.op07_058,
                        R.drawable.op07_061,
                        R.drawable.op07_062,
                        R.drawable.op07_063,
                        R.drawable.op07_067,
                        R.drawable.op07_069,
                        R.drawable.op07_074,
                        R.drawable.op07_076,
                        R.drawable.op07_078,
                        R.drawable.op07_081,
                        R.drawable.op07_084,
                        R.drawable.op07_086,
                        R.drawable.op07_087,
                        R.drawable.op07_089,
                        R.drawable.op07_090,
                        R.drawable.op07_095,
                        R.drawable.op07_099,
                        R.drawable.op07_100,
                        R.drawable.op07_102,
                        R.drawable.op07_103,
                        R.drawable.op07_106,
                        R.drawable.op07_108,
                        R.drawable.op07_110,
                        R.drawable.op07_115,
                        R.drawable.op07_117
                })
                .uc(new int[]{
                        R.drawable.op07_008,
                        R.drawable.op07_013,
                        R.drawable.op07_014,
                        R.drawable.op07_017,
                        R.drawable.op07_023,
                        R.drawable.op07_031,
                        R.drawable.op07_033,
                        R.drawable.op07_034,
                        R.drawable.op07_037,
                        R.drawable.op07_040,
                        R.drawable.op07_041,
                        R.drawable.op07_043,
                        R.drawable.op07_048,
                        R.drawable.op07_056,
                        R.drawable.op07_060,
                        R.drawable.op07_065,
                        R.drawable.op07_068,
                        R.drawable.op07_070,
                        R.drawable.op07_075,
                        R.drawable.op07_082,
                        R.drawable.op07_083,
                        R.drawable.op07_088,
                        R.drawable.op07_092,
                        R.drawable.op07_094,
                        R.drawable.op07_098,
                        R.drawable.op07_101,
                        R.drawable.op07_104,
                        R.drawable.op07_105,
                        R.drawable.op07_113,
                        R.drawable.op07_114
                })
                .r(new int[]{
                        R.drawable.op07_002,
                        R.drawable.op07_003,
                        R.drawable.op07_004,
                        R.drawable.op07_005,
                        R.drawable.op07_010,
                        R.drawable.op07_016,
                        R.drawable.op07_021,
                        R.drawable.op07_022,
                        R.drawable.op07_032,
                        R.drawable.op07_036,
                        R.drawable.op07_046,
                        R.drawable.op07_047,
                        R.drawable.op07_053,
                        R.drawable.op07_054,
                        R.drawable.op07_057,
                        R.drawable.op07_066,
                        R.drawable.op07_071,
                        R.drawable.op07_073,
                        R.drawable.op07_077,
                        R.drawable.op07_080,
                        R.drawable.op07_091,
                        R.drawable.op07_093,
                        R.drawable.op07_096,
                        R.drawable.op07_107,
                        R.drawable.op07_112,
                        R.drawable.op07_116
                })
                .sr(new int[]{
                        R.drawable.op07_015,
                        R.drawable.op07_026,
                        R.drawable.op07_029,
                        R.drawable.op07_045,
                        R.drawable.op07_051,
                        R.drawable.op07_064,
                        R.drawable.op07_072,
                        R.drawable.op07_085,
                        R.drawable.op07_109,
                        R.drawable.op07_111
                })
                .aar(new int[]{
                        R.drawable.op07_005_p1,
                        R.drawable.op07_022_p1,
                        R.drawable.op07_046_p1,
                        R.drawable.op07_047_p1,
                        R.drawable.op07_071_p1,
                        R.drawable.op07_107_p1
                })
                .aasr(new int[]{
                        R.drawable.op07_015_p1,
                        R.drawable.op07_026_p1,
                        R.drawable.op07_029_p1,
                        R.drawable.op07_045_p1,
                        R.drawable.op07_051_p1,
                        R.drawable.op07_064_p1,
                        R.drawable.op07_072_p1,
                        R.drawable.op07_085_p1,
                        R.drawable.op07_109_p1,
                        R.drawable.op07_111_p1
                })
                .aasec(new int[]{
                        R.drawable.op07_118_p1,
                        R.drawable.op07_119_p1
                })
                .aal(new int[]{
                        R.drawable.op07_001_p1,
                        R.drawable.op07_019_p1,
                        R.drawable.op07_038_p1,
                        R.drawable.op07_059_p1,
                        R.drawable.op07_079_p1,
                        R.drawable.op07_097_p1
                })
                .sec(new int[]{
                        R.drawable.op07_118,
                        R.drawable.op07_119
                })
                .mr(new int[]{
                        R.drawable.op07_051_p2
                })
                .l(new int[]{
                        R.drawable.op07_001,
                        R.drawable.op07_019,
                        R.drawable.op07_038,
                        R.drawable.op07_059,
                        R.drawable.op07_079,
                        R.drawable.op07_097
                })
                .sp(new int[]{
                        R.drawable.op01_035_p2,
                        R.drawable.op01_073_p2,
                        R.drawable.op03_003_p1,
                        R.drawable.op03_078_p2,
                        R.drawable.op05_074_p3,
                        R.drawable.op06_101_p2
                })
                .packScale(1.7f)
                .build();
    }
}
