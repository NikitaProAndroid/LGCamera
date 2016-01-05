package com.lge.morpho.util.ImageConverter;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageConverterJNI {
    public final native int argb88882rgb888(int i, int i2, int[] iArr, byte[] bArr);

    public final native int argb88882yuv420p(int i, int i2, int[] iArr, byte[] bArr);

    public final native int rgb8882argb8888(int i, int i2, byte[] bArr, int[] iArr);

    public final native int rgb8882yuv420p(int i, int i2, byte[] bArr, byte[] bArr2);

    public final native int yuv420p2argb8888(int i, int i2, byte[] bArr, int[] iArr);

    public final native int yuv420p2rgb888(int i, int i2, byte[] bArr, byte[] bArr2);

    public final native int yuv420sp2argb8888(int i, int i2, byte[] bArr, int[] iArr);

    public final native int yuv420sp2rgb565(int i, int i2, byte[] bArr, short[] sArr);

    public final native int yuv4442argb8888(int i, int i2, byte[] bArr, int[] iArr);

    public final native int yvu420sp2argb8888(int i, int i2, byte[] bArr, int[] iArr);

    public final native int yvu420sp2bmp(int i, int i2, byte[] bArr, Bitmap bitmap);

    public final native int yvu420sp2rgb565(int i, int i2, byte[] bArr, short[] sArr);

    public final native int yvu420sp2yuv444(int i, int i2, byte[] bArr, byte[] bArr2);

    static {
        try {
            System.loadLibrary("morphoimageconverter_4");
        } catch (UnsatisfiedLinkError e) {
            Log.d("ImageConverterJNI", e.getMessage());
            Log.d("ImageConverterJNI", "can't loadLibrary");
        }
    }
}
