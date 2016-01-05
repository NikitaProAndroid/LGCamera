package com.lge.almalence.app.clearshot;

public final class SkiaWrapper {
    public static native void readDimensions(int i, int i2, int[] iArr);

    static {
        System.loadLibrary("skiawrapper");
    }

    private SkiaWrapper() {
    }
}
