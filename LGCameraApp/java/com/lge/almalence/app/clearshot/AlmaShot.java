package com.lge.almalence.app.clearshot;

public final class AlmaShot {
    public static native String ConvertFromJpeg(int[] iArr, int[] iArr2, int i, int i2, int i3);

    public static native int[] ConvertToARGB(int i, int i2, int i3);

    public static native byte[] ConvertToJpeg(int i, int i2, int i3);

    public static native String Initialize();

    public static native int MovObjEnumerate(int i, int i2, int i3, byte[] bArr, byte[] bArr2, int i4);

    public static native int MovObjProcess(int i, int i2, int i3, int[] iArr, byte[] bArr);

    public static native int[] NV21toARGB(int i, int i2, int i3, int i4, int i5);

    public static native int Release(int i);

    static {
        System.loadLibrary("almashot-clr");
    }
}
