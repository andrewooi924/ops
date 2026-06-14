package com.optcg.app;

/**
 * Pack-opening simulator for set OP09. All behaviour lives in
 * {@link BasePackSimActivity}; this class only supplies the per-set data.
 */
public class OP09SimActivity extends BasePackSimActivity {

    @Override
    protected PackSimConfig getConfig() {
        return new PackSimConfig.Builder("op09", R.layout.activity_op09_sim, R.drawable.op09_pack)
                .c(new int[]{
                        R.drawable.op09_003,
                        R.drawable.op09_006,
                        R.drawable.op09_007,
                        R.drawable.op09_010,
                        R.drawable.op09_012,
                        R.drawable.op09_016,
                        R.drawable.op09_019,
                        R.drawable.op09_021,
                        R.drawable.op09_024,
                        R.drawable.op09_028,
                        R.drawable.op09_029,
                        R.drawable.op09_033,
                        R.drawable.op09_036,
                        R.drawable.op09_038,
                        R.drawable.op09_041,
                        R.drawable.op09_047,
                        R.drawable.op09_049,
                        R.drawable.op09_052,
                        R.drawable.op09_054,
                        R.drawable.op09_055,
                        R.drawable.op09_058,
                        R.drawable.op09_060,
                        R.drawable.op09_063,
                        R.drawable.op09_067,
                        R.drawable.op09_071,
                        R.drawable.op09_073,
                        R.drawable.op09_074,
                        R.drawable.op09_079,
                        R.drawable.op09_080,
                        R.drawable.op09_082,
                        R.drawable.op09_085,
                        R.drawable.op09_087,
                        R.drawable.op09_091,
                        R.drawable.op09_092,
                        R.drawable.op09_094,
                        R.drawable.op09_097,
                        R.drawable.op09_099,
                        R.drawable.op09_105,
                        R.drawable.op09_106,
                        R.drawable.op09_110,
                        R.drawable.op09_111,
                        R.drawable.op09_112,
                        R.drawable.op09_113,
                        R.drawable.op09_114,
                        R.drawable.op09_116
                })
                .uc(new int[]{
                        R.drawable.op09_008,
                        R.drawable.op09_011,
                        R.drawable.op09_014,
                        R.drawable.op09_017,
                        R.drawable.op09_018,
                        R.drawable.op09_025,
                        R.drawable.op09_027,
                        R.drawable.op09_030,
                        R.drawable.op09_032,
                        R.drawable.op09_035,
                        R.drawable.op09_040,
                        R.drawable.op09_043,
                        R.drawable.op09_044,
                        R.drawable.op09_045,
                        R.drawable.op09_053,
                        R.drawable.op09_059,
                        R.drawable.op09_064,
                        R.drawable.op09_066,
                        R.drawable.op09_068,
                        R.drawable.op09_070,
                        R.drawable.op09_075,
                        R.drawable.op09_077,
                        R.drawable.op09_084,
                        R.drawable.op09_088,
                        R.drawable.op09_089,
                        R.drawable.op09_098,
                        R.drawable.op09_100,
                        R.drawable.op09_108,
                        R.drawable.op09_109,
                        R.drawable.op09_115
                })
                .r(new int[]{
                        R.drawable.op09_002,
                        R.drawable.op09_005,
                        R.drawable.op09_013,
                        R.drawable.op09_015,
                        R.drawable.op09_020,
                        R.drawable.op09_026,
                        R.drawable.op09_031,
                        R.drawable.op09_034,
                        R.drawable.op09_039,
                        R.drawable.op09_050,
                        R.drawable.op09_051,
                        R.drawable.op09_056,
                        R.drawable.op09_057,
                        R.drawable.op09_069,
                        R.drawable.op09_076,
                        R.drawable.op09_078,
                        R.drawable.op09_083,
                        R.drawable.op09_086,
                        R.drawable.op09_090,
                        R.drawable.op09_095,
                        R.drawable.op09_096,
                        R.drawable.op09_101,
                        R.drawable.op09_102,
                        R.drawable.op09_104,
                        R.drawable.op09_107,
                        R.drawable.op09_117
                })
                .sr(new int[]{
                        R.drawable.op09_004,
                        R.drawable.op09_009,
                        R.drawable.op09_023,
                        R.drawable.op09_037,
                        R.drawable.op09_046,
                        R.drawable.op09_048,
                        R.drawable.op09_065,
                        R.drawable.op09_072,
                        R.drawable.op09_093,
                        R.drawable.op09_103
                })
                .aar(new int[]{
                        R.drawable.op09_002_p1,
                        R.drawable.op09_034_p1,
                        R.drawable.op09_050_p1,
                        R.drawable.op09_051_p1,
                        R.drawable.op09_069_p1,
                        R.drawable.op09_107_p1
                })
                .aasr(new int[]{
                        R.drawable.op09_004_p1,
                        R.drawable.op09_009_p1,
                        R.drawable.op09_023_p1,
                        R.drawable.op09_037_p1,
                        R.drawable.op09_046_p1,
                        R.drawable.op09_048_p1,
                        R.drawable.op09_065_p1,
                        R.drawable.op09_072_p1,
                        R.drawable.op09_093_p1,
                        R.drawable.op09_103_p1
                })
                .aasec(new int[]{
                        R.drawable.op09_118_p1,
                        R.drawable.op09_119_p1
                })
                .aal(new int[]{
                        R.drawable.op09_001_p1,
                        R.drawable.op09_022_p1,
                        R.drawable.op09_042_p1,
                        R.drawable.op09_061_p1,
                        R.drawable.op09_062_p1,
                        R.drawable.op09_081_p1
                })
                .sec(new int[]{
                        R.drawable.op09_118,
                        R.drawable.op09_119
                })
                .mr(new int[]{
                        R.drawable.op09_004_p2,
                        R.drawable.op09_051_p2,
                        R.drawable.op09_093_p2,
                        R.drawable.op09_119_p2
                })
                .l(new int[]{
                        R.drawable.op09_001,
                        R.drawable.op09_022,
                        R.drawable.op09_042,
                        R.drawable.op09_061,
                        R.drawable.op09_062,
                        R.drawable.op09_081
                })
                .sp(new int[]{
                        R.drawable.op04_119_p2,
                        R.drawable.op05_067_p4,
                        R.drawable.op05_093_p2,
                        R.drawable.op05_119_p5,
                        R.drawable.op07_015_p2,
                        R.drawable.op07_051_p3,
                        R.drawable.op08_106_p4,
                        R.drawable.op09_004_p3,
                        R.drawable.op09_051_p3,
                        R.drawable.op09_093_p3
                })
                .ultra("GSP", new int[]{
                        R.drawable.op09_118_p2
                })
                .packScale(1.65f)
                .build();
    }
}
