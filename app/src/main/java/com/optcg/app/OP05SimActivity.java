package com.optcg.app;

/**
 * Pack-opening simulator for set OP05. All behaviour lives in
 * {@link BasePackSimActivity}; this class only supplies the per-set data.
 */
public class OP05SimActivity extends BasePackSimActivity {

    @Override
    protected PackSimConfig getConfig() {
        return new PackSimConfig.Builder("op05", R.layout.activity_op05_sim, R.drawable.op05_pack)
                .c(new int[]{
                        R.drawable.op05_009,
                        R.drawable.op05_011,
                        R.drawable.op05_012,
                        R.drawable.op05_013,
                        R.drawable.op05_014,
                        R.drawable.op05_018,
                        R.drawable.op05_024,
                        R.drawable.op05_025,
                        R.drawable.op05_026,
                        R.drawable.op05_028,
                        R.drawable.op05_031,
                        R.drawable.op05_033,
                        R.drawable.op05_035,
                        R.drawable.op05_038,
                        R.drawable.op05_040,
                        R.drawable.op05_044,
                        R.drawable.op05_045,
                        R.drawable.op05_046,
                        R.drawable.op05_047,
                        R.drawable.op05_052,
                        R.drawable.op05_053,
                        R.drawable.op05_056,
                        R.drawable.op05_058,
                        R.drawable.op05_063,
                        R.drawable.op05_065,
                        R.drawable.op05_066,
                        R.drawable.op05_068,
                        R.drawable.op05_072,
                        R.drawable.op05_075,
                        R.drawable.op05_077,
                        R.drawable.op05_083,
                        R.drawable.op05_084,
                        R.drawable.op05_087,
                        R.drawable.op05_089,
                        R.drawable.op05_090,
                        R.drawable.op05_092,
                        R.drawable.op05_095,
                        R.drawable.op05_097,
                        R.drawable.op05_107,
                        R.drawable.op05_108,
                        R.drawable.op05_109,
                        R.drawable.op05_110,
                        R.drawable.op05_112,
                        R.drawable.op05_113,
                        R.drawable.op05_116
                })
                .uc(new int[]{
                        R.drawable.op05_003,
                        R.drawable.op05_004,
                        R.drawable.op05_008,
                        R.drawable.op05_010,
                        R.drawable.op05_020,
                        R.drawable.op05_021,
                        R.drawable.op05_027,
                        R.drawable.op05_029,
                        R.drawable.op05_036,
                        R.drawable.op05_039,
                        R.drawable.op05_048,
                        R.drawable.op05_049,
                        R.drawable.op05_054,
                        R.drawable.op05_059,
                        R.drawable.op05_061,
                        R.drawable.op05_062,
                        R.drawable.op05_070,
                        R.drawable.op05_073,
                        R.drawable.op05_078,
                        R.drawable.op05_079,
                        R.drawable.op05_080,
                        R.drawable.op05_081,
                        R.drawable.op05_085,
                        R.drawable.op05_096,
                        R.drawable.op05_099,
                        R.drawable.op05_103,
                        R.drawable.op05_104,
                        R.drawable.op05_111,
                        R.drawable.op05_114,
                        R.drawable.op05_117
                })
                .r(new int[]{
                        R.drawable.op05_005,
                        R.drawable.op05_015,
                        R.drawable.op05_016,
                        R.drawable.op05_017,
                        R.drawable.op05_019,
                        R.drawable.op05_023,
                        R.drawable.op05_030,
                        R.drawable.op05_034,
                        R.drawable.op05_037,
                        R.drawable.op05_042,
                        R.drawable.op05_050,
                        R.drawable.op05_055,
                        R.drawable.op05_057,
                        R.drawable.op05_064,
                        R.drawable.op05_067,
                        R.drawable.op05_071,
                        R.drawable.op05_076,
                        R.drawable.op05_082,
                        R.drawable.op05_086,
                        R.drawable.op05_088,
                        R.drawable.op05_094,
                        R.drawable.op05_101,
                        R.drawable.op05_102,
                        R.drawable.op05_105,
                        R.drawable.op05_106,
                        R.drawable.op05_115
                })
                .sr(new int[]{
                        R.drawable.op05_006,
                        R.drawable.op05_007,
                        R.drawable.op05_032,
                        R.drawable.op05_043,
                        R.drawable.op05_051,
                        R.drawable.op05_069,
                        R.drawable.op05_074,
                        R.drawable.op05_091,
                        R.drawable.op05_093,
                        R.drawable.op05_100
                })
                .aar(new int[]{
                        R.drawable.op05_015_p1,
                        R.drawable.op05_034_p1,
                        R.drawable.op05_055_p1,
                        R.drawable.op05_067_p1,
                        R.drawable.op05_088_p1,
                        R.drawable.op05_102_p1
                })
                .aasr(new int[]{
                        R.drawable.op05_006_p1,
                        R.drawable.op05_007_p1,
                        R.drawable.op05_032_p1,
                        R.drawable.op05_043_p1,
                        R.drawable.op05_051_p1,
                        R.drawable.op05_069_p1,
                        R.drawable.op05_074_p1,
                        R.drawable.op05_091_p1,
                        R.drawable.op05_093_p1,
                        R.drawable.op05_100_p1,
                        R.drawable.st01_012_p4
                })
                .aasec(new int[]{
                        R.drawable.op05_118_p1,
                        R.drawable.op05_119_p1
                })
                .aal(new int[]{
                        R.drawable.op05_001_p1,
                        R.drawable.op05_002_p1,
                        R.drawable.op05_022_p1,
                        R.drawable.op05_041_p1,
                        R.drawable.op05_060_p1,
                        R.drawable.op05_098_p1
                })
                .sec(new int[]{
                        R.drawable.op05_118,
                        R.drawable.op05_119
                })
                .mr(new int[]{
                        R.drawable.op05_069_p2,
                        R.drawable.op05_074_p2,
                        R.drawable.op05_119_p2
                })
                .l(new int[]{
                        R.drawable.op05_001,
                        R.drawable.op05_002,
                        R.drawable.op05_022,
                        R.drawable.op05_041,
                        R.drawable.op05_060,
                        R.drawable.op05_098
                })
                .sp(new int[]{
                        R.drawable.op01_016_p4,
                        R.drawable.op01_121_p2,
                        R.drawable.op02_120_p2,
                        R.drawable.op03_092_p2,
                        R.drawable.op04_044_p2,
                        R.drawable.op05_100_p2
                })
                .ultra("ODA", new int[]{
                        R.drawable.st01_012_p3
                })
                .build();
    }
}
