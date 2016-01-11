package com.lge.media;

import android.content.res.AssetManager.AssetInputStream;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class LGMovie {
    private final long mLGNativeMovie;

    public static native LGMovie decodeByteArray(byte[] bArr, int i, int i2);

    private static native LGMovie nativeDecodeAsset(long j);

    private static native LGMovie nativeDecodeStream(InputStream inputStream);

    private static native void nativeDestructor(long j);

    public native void draw(Canvas canvas, float f, float f2, Paint paint);

    public native int duration();

    public native int height();

    public native boolean isOpaque();

    public native boolean setTime(int i);

    public native int width();

    static {
        System.loadLibrary("hook_jni");
    }

    private LGMovie(long LGnativeMovie) {
        if (LGnativeMovie == 0) {
            throw new RuntimeException("native LGMovie creation failed");
        }
        this.mLGNativeMovie = LGnativeMovie;
    }

    public void draw(Canvas canvas, float x, float y) {
        draw(canvas, x, y, null);
    }

    public static LGMovie decodeStream(InputStream is) {
        if (is == null) {
            return null;
        }
        if (is instanceof AssetInputStream) {
            return nativeDecodeAsset(((AssetInputStream) is).getNativeAsset());
        }
        return nativeDecodeStream(is);
    }

    public static LGMovie decodeFile(String pathName) {
        try {
            return decodeTempStream(new FileInputStream(pathName));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    protected void finalize() throws Throwable {
        try {
            nativeDestructor(this.mLGNativeMovie);
        } finally {
            super.finalize();
        }
    }

    private static LGMovie decodeTempStream(InputStream is) {
        LGMovie moov = null;
        try {
            moov = decodeStream(is);
            is.close();
            return moov;
        } catch (IOException e) {
            return moov;
        }
    }
}
