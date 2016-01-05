package com.lge.camera.util;

import android.graphics.Bitmap;
import com.lge.camera.properties.CameraConstants;
import com.lge.olaworks.define.Ola_ShotParam;

public class ColorConverter {
    public static final native void yuv420spToArgb8888(Bitmap bitmap, byte[] bArr);

    public static final native void yuv420spToBitmap(Bitmap bitmap, byte[] bArr, int i, int i2);

    public static final native void yuv420spToRGB(int[] iArr, byte[] bArr, int i, int i2);

    static {
        System.loadLibrary("ColorConverter");
    }

    public static void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
        int frameSize = width * height;
        int yp = 0;
        for (int j = 0; j < height; j++) {
            int u = 0;
            int v = 0;
            int i = 0;
            int uvp = frameSize + ((j >> 1) * width);
            while (i < width) {
                int uvp2;
                int y = (yuv420sp[yp] & Ola_ShotParam.AnimalMask_Random) - 16;
                if (y < 0) {
                    y = 0;
                }
                if ((i & 1) == 0) {
                    uvp2 = uvp + 1;
                    v = (yuv420sp[uvp] & Ola_ShotParam.AnimalMask_Random) - 128;
                    u = (yuv420sp[uvp2] & Ola_ShotParam.AnimalMask_Random) - 128;
                    uvp2++;
                } else {
                    uvp2 = uvp;
                }
                int y1192 = y * 1192;
                int r = y1192 + (v * 1634);
                int g = (y1192 - (v * 833)) - (u * CameraConstants.TRANS_INTERVAL);
                int b = y1192 + (u * 2066);
                if (r < 0) {
                    r = 0;
                } else if (r > 262143) {
                    r = 262143;
                }
                if (g < 0) {
                    g = 0;
                } else if (g > 262143) {
                    g = 262143;
                }
                if (b < 0) {
                    b = 0;
                } else if (b > 262143) {
                    b = 262143;
                }
                rgb[yp] = ((-16777216 | ((r << 6) & 16711680)) | ((g >> 2) & 65280)) | ((b >> 10) & Ola_ShotParam.AnimalMask_Random);
                i++;
                yp++;
                uvp = uvp2;
            }
        }
    }

    private int checkColorBoundary(int value) {
        if (value < 0) {
            return 0;
        }
        if (value > 262143) {
            return 262143;
        }
        return value;
    }
}
