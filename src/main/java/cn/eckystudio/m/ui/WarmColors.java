package cn.eckystudio.m.ui;

public class WarmColors {
    final static int sColorList[] = new int[] {
            0xFFADD8E6,
            0xFF90EE90,
            0xFFFFA07A,
            0xFF20B2AA,
            0xFFF08080,
            0xFFE0FFFF,
            0xFF87CEFA,
            0xFFFFFFFF,
            0xFFAFEEEE,
            0xFF778899,
            0xFFB0C4DE,
            0xFFFFFFE0,
            0xFF98FB98,
            0xFFDB7093,
            0xFFFFDAB9,
            0xFFB0E0E6
    };

    public static int getColor(int index){
        index = index % sColorList.length;
        return sColorList[index];
    }
}
