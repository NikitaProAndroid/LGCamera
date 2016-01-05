package com.lge.olaworks.define;

public class Ola_ImageFormat {
    public static final int BGR888 = 258;
    public static final int BGR8880 = 260;
    public static final int GRAY = 1;
    private static final String PREFIX_FORMAT_STR = "OLA_IMAGE_FORMAT_";
    public static final int RGB888 = 257;
    public static final int RGB8880 = 259;
    public static final int RGB_LABEL = 256;
    public static final int YUVPACKED_LABEL = 512;
    public static final int YUVPACKED_Y411 = 513;
    public static final int YUVPLANAR_LABEL = 1024;
    public static final int YUVPLANAR_NV12 = 1025;
    public static final int YUVPLANAR_NV12_SPLIT = 1027;
    public static final int YUVPLANAR_NV21 = 1026;
    public static final int YUVPLANAR_NV21_SPLIT = 1028;

    public static int format(String str) {
        if (str.startsWith(PREFIX_FORMAT_STR)) {
            str = str.substring(PREFIX_FORMAT_STR.length());
        }
        if (str.compareToIgnoreCase("GRAY") == 0) {
            return GRAY;
        }
        if (str.compareToIgnoreCase("RGB888") == 0) {
            return RGB888;
        }
        if (str.compareToIgnoreCase("BGR888") == 0) {
            return BGR888;
        }
        if (str.compareToIgnoreCase("RGB8880") == 0) {
            return RGB8880;
        }
        if (str.compareToIgnoreCase("BGR8880") == 0) {
            return BGR8880;
        }
        if (str.compareToIgnoreCase("YUVPACKED_Y411") == 0) {
            return YUVPACKED_Y411;
        }
        if (str.compareToIgnoreCase("YUVPLANAR_NV12") == 0) {
            return YUVPLANAR_NV12;
        }
        if (str.compareToIgnoreCase("YUVPLANAR_NV21") == 0) {
            return YUVPLANAR_NV21;
        }
        if (str.compareToIgnoreCase("YUVPLANAR_NV12_SPLIT") == 0) {
            return YUVPLANAR_NV12_SPLIT;
        }
        if (str.compareToIgnoreCase("YUVPLANAR_NV21_SPLIT") == 0) {
            return YUVPLANAR_NV21_SPLIT;
        }
        return 0;
    }

    public static String format(int format) {
        String str = null;
        switch (format) {
            case GRAY /*1*/:
                str = "GRAY";
                break;
            case RGB888 /*257*/:
                str = "RGB888";
                break;
            case BGR888 /*258*/:
                str = "BGR888";
                break;
            case RGB8880 /*259*/:
                str = "RGB8880";
                break;
            case BGR8880 /*260*/:
                str = "BGR8880";
                break;
            case YUVPACKED_Y411 /*513*/:
                str = "YUVPACKED_Y411";
                break;
            case YUVPLANAR_NV12 /*1025*/:
                str = "YUVPLANAR_NV12";
                break;
            case YUVPLANAR_NV21 /*1026*/:
                str = "YUVPLANAR_NV21";
                break;
            case YUVPLANAR_NV12_SPLIT /*1027*/:
                str = "YUVPLANAR_NV12_SPLIT";
                break;
            case YUVPLANAR_NV21_SPLIT /*1028*/:
                str = "YUVPLANAR_NV21_SPLIT";
                break;
        }
        if (str == null) {
            return null;
        }
        return PREFIX_FORMAT_STR + str;
    }
}
