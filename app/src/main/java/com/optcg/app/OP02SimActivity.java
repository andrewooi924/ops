package com.optcg.app;

/**
 * Pack-opening simulator for set OP02. All behaviour lives in
 * {@link BasePackSimActivity}; this class only supplies the per-set data.
 */
public class OP02SimActivity extends BasePackSimActivity {

    @Override
    protected PackSimConfig getConfig() {
        return new PackSimConfig.Builder("op02", R.layout.activity_op02_sim, R.drawable.op02_pack)
                .c(new int[]{
                        R.drawable.op02_003,
                        R.drawable.op02_006,
                        R.drawable.op02_007,
                        R.drawable.op02_010,
                        R.drawable.op02_012,
                        R.drawable.op02_016,
                        R.drawable.op02_020,
                        R.drawable.op02_023,
                        R.drawable.op02_024,
                        R.drawable.op02_028,
                        R.drawable.op02_033,
                        R.drawable.op02_035,
                        R.drawable.op02_038,
                        R.drawable.op02_039,
                        R.drawable.op02_043,
                        R.drawable.op02_044,
                        R.drawable.op02_045,
                        R.drawable.op02_048,
                        R.drawable.op02_052,
                        R.drawable.op02_053,
                        R.drawable.op02_054,
                        R.drawable.op02_055,
                        R.drawable.op02_060,
                        R.drawable.op02_066,
                        R.drawable.op02_069,
                        R.drawable.op02_070,
                        R.drawable.op02_074,
                        R.drawable.op02_077,
                        R.drawable.op02_080,
                        R.drawable.op02_081,
                        R.drawable.op02_084,
                        R.drawable.op02_088,
                        R.drawable.op02_091,
                        R.drawable.op02_092,
                        R.drawable.op02_097,
                        R.drawable.op02_100,
                        R.drawable.op02_101,
                        R.drawable.op02_104,
                        R.drawable.op02_105,
                        R.drawable.op02_107,
                        R.drawable.op02_108,
                        R.drawable.op02_109,
                        R.drawable.op02_111,
                        R.drawable.op02_116,
                        R.drawable.op02_118
                })
                .uc(new int[]{
                        R.drawable.op02_005,
                        R.drawable.op02_009,
                        R.drawable.op02_014,
                        R.drawable.op02_015,
                        R.drawable.op02_019,
                        R.drawable.op02_022,
                        R.drawable.op02_027,
                        R.drawable.op02_031,
                        R.drawable.op02_032,
                        R.drawable.op02_034,
                        R.drawable.op02_037,
                        R.drawable.op02_046,
                        R.drawable.op02_056,
                        R.drawable.op02_057,
                        R.drawable.op02_059,
                        R.drawable.op02_061,
                        R.drawable.op02_063,
                        R.drawable.op02_067,
                        R.drawable.op02_078,
                        R.drawable.op02_079,
                        R.drawable.op02_082,
                        R.drawable.op02_086,
                        R.drawable.op02_087,
                        R.drawable.op02_090,
                        R.drawable.op02_094,
                        R.drawable.op02_095,
                        R.drawable.op02_106,
                        R.drawable.op02_112,
                        R.drawable.op02_113,
                        R.drawable.op02_117
                })
                .r(new int[]{
                        R.drawable.op02_008,
                        R.drawable.op02_011,
                        R.drawable.op02_017,
                        R.drawable.op02_018,
                        R.drawable.op02_021,
                        R.drawable.op02_029,
                        R.drawable.op02_040,
                        R.drawable.op02_041,
                        R.drawable.op02_042,
                        R.drawable.op02_047,
                        R.drawable.op02_050,
                        R.drawable.op02_058,
                        R.drawable.op02_064,
                        R.drawable.op02_065,
                        R.drawable.op02_068,
                        R.drawable.op02_073,
                        R.drawable.op02_075,
                        R.drawable.op02_076,
                        R.drawable.op02_083,
                        R.drawable.op02_089,
                        R.drawable.op02_098,
                        R.drawable.op02_102,
                        R.drawable.op02_103,
                        R.drawable.op02_110,
                        R.drawable.op02_115,
                        R.drawable.op02_119
                })
                .sr(new int[]{
                        R.drawable.op02_004,
                        R.drawable.op02_013,
                        R.drawable.op02_030,
                        R.drawable.op02_036,
                        R.drawable.op02_051,
                        R.drawable.op02_062,
                        R.drawable.op02_085,
                        R.drawable.op02_096,
                        R.drawable.op02_099,
                        R.drawable.op02_114
                })
                .aar(new int[]{
                        R.drawable.op02_017_p1,
                        R.drawable.op02_018_p1,
                        R.drawable.op02_041_p1,
                        R.drawable.op02_058_p1,
                        R.drawable.op02_073_p1,
                        R.drawable.op02_115_p1
                })
                .aasr(new int[]{
                        R.drawable.op02_004_p1,
                        R.drawable.op02_013_p1,
                        R.drawable.op02_030_p1,
                        R.drawable.op02_036_p1,
                        R.drawable.op02_051_p1,
                        R.drawable.op02_062_p1,
                        R.drawable.op02_085_p1,
                        R.drawable.op02_096_p1,
                        R.drawable.op02_099_p1,
                        R.drawable.op02_114_p1
                })
                .aasec(new int[]{
                        R.drawable.op02_120_p1,
                        R.drawable.op02_121_p1
                })
                .aal(new int[]{
                        R.drawable.op02_001_p1,
                        R.drawable.op02_002_p1,
                        R.drawable.op02_025_p1,
                        R.drawable.op02_026_p1,
                        R.drawable.op02_049_p1,
                        R.drawable.op02_071_p1,
                        R.drawable.op02_072_p1,
                        R.drawable.op02_093_p1
                })
                .sec(new int[]{
                        R.drawable.op02_120,
                        R.drawable.op02_121
                })
                .mr(new int[]{
                        R.drawable.op02_013_p2
                })
                .l(new int[]{
                        R.drawable.op02_001,
                        R.drawable.op02_002,
                        R.drawable.op02_025,
                        R.drawable.op02_026,
                        R.drawable.op02_049,
                        R.drawable.op02_071,
                        R.drawable.op02_072,
                        R.drawable.op02_093
                })
                .build();
    }
}
