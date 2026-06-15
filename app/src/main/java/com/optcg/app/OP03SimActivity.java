package com.optcg.app;

/**
 * Pack-opening simulator for set OP03. All behaviour lives in
 * {@link BasePackSimActivity}; this class only supplies the per-set data.
 */
public class OP03SimActivity extends BasePackSimActivity {

    @Override
    protected PackSimConfig getConfig() {
        return new PackSimConfig.Builder("op03", R.layout.activity_op03_sim, R.drawable.op03_pack)
                .c(new int[]{
                        R.drawable.op03_004,
                        R.drawable.op03_006,
                        R.drawable.op03_007,
                        R.drawable.op03_009,
                        R.drawable.op03_010,
                        R.drawable.op03_019,
                        R.drawable.op03_020,
                        R.drawable.op03_023,
                        R.drawable.op03_027,
                        R.drawable.op03_031,
                        R.drawable.op03_032,
                        R.drawable.op03_035,
                        R.drawable.op03_036,
                        R.drawable.op03_037,
                        R.drawable.op03_042,
                        R.drawable.op03_043,
                        R.drawable.op03_046,
                        R.drawable.op03_052,
                        R.drawable.op03_053,
                        R.drawable.op03_054,
                        R.drawable.op03_055,
                        R.drawable.op03_061,
                        R.drawable.op03_064,
                        R.drawable.op03_065,
                        R.drawable.op03_068,
                        R.drawable.op03_069,
                        R.drawable.op03_073,
                        R.drawable.op03_075,
                        R.drawable.op03_082,
                        R.drawable.op03_083,
                        R.drawable.op03_084,
                        R.drawable.op03_085,
                        R.drawable.op03_087,
                        R.drawable.op03_091,
                        R.drawable.op03_095,
                        R.drawable.op03_098,
                        R.drawable.op03_100,
                        R.drawable.op03_101,
                        R.drawable.op03_103,
                        R.drawable.op03_106,
                        R.drawable.op03_107,
                        R.drawable.op03_109,
                        R.drawable.op03_111,
                        R.drawable.op03_120,
                        R.drawable.op03_121
                })
                .uc(new int[]{
                        R.drawable.op03_002,
                        R.drawable.op03_005,
                        R.drawable.op03_008,
                        R.drawable.op03_011,
                        R.drawable.op03_014,
                        R.drawable.op03_015,
                        R.drawable.op03_017,
                        R.drawable.op03_026,
                        R.drawable.op03_029,
                        R.drawable.op03_033,
                        R.drawable.op03_034,
                        R.drawable.op03_039,
                        R.drawable.op03_045,
                        R.drawable.op03_048,
                        R.drawable.op03_049,
                        R.drawable.op03_050,
                        R.drawable.op03_056,
                        R.drawable.op03_059,
                        R.drawable.op03_060,
                        R.drawable.op03_063,
                        R.drawable.op03_067,
                        R.drawable.op03_074,
                        R.drawable.op03_079,
                        R.drawable.op03_088,
                        R.drawable.op03_093,
                        R.drawable.op03_094,
                        R.drawable.op03_096,
                        R.drawable.op03_104,
                        R.drawable.op03_105,
                        R.drawable.op03_116,
                        R.drawable.op03_117,
                        R.drawable.op03_118
                })
                .r(new int[]{
                        R.drawable.op03_003,
                        R.drawable.op03_012,
                        R.drawable.op03_016,
                        R.drawable.op03_018,
                        R.drawable.op03_024,
                        R.drawable.op03_028,
                        R.drawable.op03_030,
                        R.drawable.op03_038,
                        R.drawable.op03_044,
                        R.drawable.op03_047,
                        R.drawable.op03_051,
                        R.drawable.op03_057,
                        R.drawable.op03_062,
                        R.drawable.op03_070,
                        R.drawable.op03_071,
                        R.drawable.op03_072,
                        R.drawable.op03_081,
                        R.drawable.op03_086,
                        R.drawable.op03_089,
                        R.drawable.op03_090,
                        R.drawable.op03_097,
                        R.drawable.op03_102,
                        R.drawable.op03_110,
                        R.drawable.op03_112,
                        R.drawable.op03_115,
                        R.drawable.op03_119
                })
                .sr(new int[]{
                        R.drawable.op03_013,
                        R.drawable.op03_025,
                        R.drawable.op03_041,
                        R.drawable.op03_066,
                        R.drawable.op03_078,
                        R.drawable.op03_080,
                        R.drawable.op03_092,
                        R.drawable.op03_108,
                        R.drawable.op03_113,
                        R.drawable.op03_114
                })
                .aar(new int[]{
                        R.drawable.op03_018_p1,
                        R.drawable.op03_024_p1,
                        R.drawable.op03_047_p1,
                        R.drawable.op03_081_p1,
                        R.drawable.op03_086_p1,
                        R.drawable.op03_112_p1
                })
                .aasr(new int[]{
                        R.drawable.op03_013_p1,
                        R.drawable.op03_025_p1,
                        R.drawable.op03_041_p1,
                        R.drawable.op03_066_p1,
                        R.drawable.op03_078_p1,
                        R.drawable.op03_080_p1,
                        R.drawable.op03_092_p1,
                        R.drawable.op03_108_p1,
                        R.drawable.op03_113_p1,
                        R.drawable.op03_114_p1
                })
                .aasec(new int[]{
                        R.drawable.op03_122_p1,
                        R.drawable.op03_123_p1
                })
                .aal(new int[]{
                        R.drawable.op03_001_p1,
                        R.drawable.op03_021_p1,
                        R.drawable.op03_022_p1,
                        R.drawable.op03_040_p1,
                        R.drawable.op03_058_p1,
                        R.drawable.op03_076_p1,
                        R.drawable.op03_077_p1,
                        R.drawable.op03_099_p1
                })
                .sec(new int[]{
                        R.drawable.op03_122,
                        R.drawable.op03_123
                })
                .mr(new int[]{
                        R.drawable.op03_122_p2
                })
                .l(new int[]{
                        R.drawable.op03_001,
                        R.drawable.op03_021,
                        R.drawable.op03_022,
                        R.drawable.op03_040,
                        R.drawable.op03_058,
                        R.drawable.op03_076,
                        R.drawable.op03_077,
                        R.drawable.op03_099
                })
                .sp(new int[]{
                        R.drawable.op01_051_p2,
                        R.drawable.st01_012_p1,
                        R.drawable.st03_009_p1,
                        R.drawable.st04_003_p1
                })
                .build();
    }
}
