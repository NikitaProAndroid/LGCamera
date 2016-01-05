package com.lge.olaworks.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.olaworks.datastruct.JOlaBitmap;
import com.lge.olaworks.define.Ola_ReturnType;
import com.lge.olaworks.jni.OlaAndroidBitmapJNI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OlaBitmapUtil {
    public static final int IMAGE_TYPE_JPEG = 0;
    public static final int IMAGE_TYPE_JPS = 1;
    private static final String TAG = "cvBitmap";

    public static Bitmap rotate(Bitmap bitmap, int degrees) {
        Bitmap result = bitmap;
        if (!(degrees == 0 || bitmap == null)) {
            Matrix mat = new Matrix();
            mat.setRotate((float) degrees, ((float) bitmap.getWidth()) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK, ((float) bitmap.getHeight()) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
            try {
                result = Bitmap.createBitmap(bitmap, IMAGE_TYPE_JPEG, IMAGE_TYPE_JPEG, bitmap.getWidth(), bitmap.getHeight(), mat, true);
            } catch (OutOfMemoryError e) {
            }
        }
        return result;
    }

    public static Bitmap hflip(Bitmap bitmap) {
        Bitmap result = bitmap;
        if (bitmap != null) {
            Matrix mat = new Matrix();
            mat.preScale(-1.0f, RotateView.DEFAULT_TEXT_SCALE_X);
            try {
                result = Bitmap.createBitmap(bitmap, IMAGE_TYPE_JPEG, IMAGE_TYPE_JPEG, bitmap.getWidth(), bitmap.getHeight(), mat, true);
            } catch (OutOfMemoryError e) {
            }
        }
        return result;
    }

    public static Bitmap resizeFixedRatio(Bitmap bitmap, int pixel_size) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (pixel_size >= width * height) {
            return null;
        }
        double ratio = Math.sqrt(((double) pixel_size) / ((double) (width * height)));
        width = ((int) (((double) width) * ratio)) & -2;
        height = ((int) (((double) height) * ratio)) & -2;
        Bitmap retBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
        new Canvas(retBitmap).drawBitmap(bitmap, new Rect(IMAGE_TYPE_JPEG, IMAGE_TYPE_JPEG, bitmap.getWidth(), bitmap.getHeight()), new Rect(IMAGE_TYPE_JPEG, IMAGE_TYPE_JPEG, width, height), null);
        return retBitmap;
    }

    public static void drawFixedRatio(Canvas canvas, Bitmap bitmap, Rect bound) {
        int bound_width = bound.width();
        int bound_height = bound.height();
        Rect src_rect = new Rect(IMAGE_TYPE_JPEG, IMAGE_TYPE_JPEG, bitmap.getWidth(), bitmap.getHeight());
        Rect dst_rect = new Rect(bound);
        double src_ratio = ((double) bitmap.getWidth()) / ((double) bitmap.getHeight());
        double dst_ratio = ((double) bound_width) / ((double) bound_height);
        int diff;
        if (src_ratio < dst_ratio) {
            diff = bound_width - ((int) (((double) bound_height) * src_ratio));
            dst_rect.left += diff / 2;
            dst_rect.right -= diff / 2;
        } else if (src_ratio > dst_ratio) {
            diff = bound_height - ((int) (((double) bound_width) * src_ratio));
            dst_rect.top += diff / 2;
            dst_rect.bottom -= diff / 2;
        }
        canvas.drawBitmap(bitmap, src_rect, dst_rect, null);
    }

    public static Bitmap[] loadBitmap(Context ctx, Uri imageUri, int image_type) {
        Bitmap[] retBitmaps = null;
        String str;
        Object[] objArr;
        switch (image_type) {
            case IMAGE_TYPE_JPEG /*0*/:
                retBitmaps = new Bitmap[IMAGE_TYPE_JPS];
                retBitmaps[IMAGE_TYPE_JPEG] = loadBitmap(ctx, imageUri);
                if (retBitmaps[IMAGE_TYPE_JPEG] == null) {
                    str = TAG;
                    objArr = new Object[IMAGE_TYPE_JPS];
                    objArr[IMAGE_TYPE_JPEG] = imageUri.toString();
                    Log.e(str, String.format("uri(%s) is empty", objArr));
                    return null;
                }
                break;
            case IMAGE_TYPE_JPS /*1*/:
                Bitmap org_source = loadBitmap(ctx, imageUri);
                if (org_source != null) {
                    retBitmaps = SplitBitmap(org_source);
                    org_source.recycle();
                    break;
                }
                str = TAG;
                objArr = new Object[IMAGE_TYPE_JPS];
                objArr[IMAGE_TYPE_JPEG] = imageUri.toString();
                Log.e(str, String.format("uri(%s) is empty", objArr));
                return null;
        }
        return retBitmaps;
    }

    public static Bitmap loadBitmap(Context ctx, Uri imageUri) {
        InputStream input = null;
        try {
            input = ctx.getContentResolver().openInputStream(imageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (input == null) {
            Log.e(TAG, "mInputStream == null, uri : " + imageUri);
            return null;
        }
        Options opts = new Options();
        opts.inPreferredConfig = Config.ARGB_8888;
        return BitmapFactory.decodeStream(input, null, opts);
    }

    public static Bitmap[] SplitBitmap(Bitmap source) {
        Bitmap[] retBitmap = new Bitmap[2];
        int width = source.getWidth() / 2;
        int height = source.getHeight();
        retBitmap[IMAGE_TYPE_JPEG] = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        retBitmap[IMAGE_TYPE_JPS] = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        new Canvas(retBitmap[IMAGE_TYPE_JPEG]).drawBitmap(source, new Rect(IMAGE_TYPE_JPEG, IMAGE_TYPE_JPEG, width, height), new Rect(IMAGE_TYPE_JPEG, IMAGE_TYPE_JPEG, width, height), null);
        new Canvas(retBitmap[IMAGE_TYPE_JPS]).drawBitmap(source, new Rect(width, IMAGE_TYPE_JPEG, width * 2, height), new Rect(IMAGE_TYPE_JPEG, IMAGE_TYPE_JPEG, width, height), null);
        return retBitmap;
    }

    public static Bitmap MergeBitmap(Bitmap left, Bitmap right) {
        int width = left.getWidth();
        int height = left.getHeight();
        Bitmap retBitmap = Bitmap.createBitmap(width * 2, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(retBitmap);
        canvas.drawBitmap(left, new Rect(IMAGE_TYPE_JPEG, IMAGE_TYPE_JPEG, width, height), new Rect(IMAGE_TYPE_JPEG, IMAGE_TYPE_JPEG, width, height), null);
        canvas.drawBitmap(right, new Rect(IMAGE_TYPE_JPEG, IMAGE_TYPE_JPEG, width, height), new Rect(width, IMAGE_TYPE_JPEG, width * 2, height), null);
        return retBitmap;
    }

    public static Bitmap loadBitmap(byte[] jpegData) {
        return loadBitmap(jpegData, (int) IMAGE_TYPE_JPS);
    }

    public static Bitmap loadBitmap(byte[] jpegData, int sampleSize) {
        Bitmap bitmap = null;
        try {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(jpegData, IMAGE_TYPE_JPEG, jpegData.length, options);
            if (!(options.mCancel || options.outWidth == -1 || options.outHeight == -1)) {
                options.inSampleSize = sampleSize;
                options.inJustDecodeBounds = false;
                options.inDither = true;
                options.inPreferredConfig = Config.ARGB_8888;
                bitmap = BitmapFactory.decodeByteArray(jpegData, IMAGE_TYPE_JPEG, jpegData.length, options);
            }
        } catch (OutOfMemoryError ex) {
            Log.e(TAG, "Got oom exception ", ex);
        }
        return bitmap;
    }

    public static Bitmap loadBitmap(String filePath, String fileName) {
        File file = new File(filePath, fileName);
        Options opts = new Options();
        opts.inPreferredConfig = Config.ARGB_8888;
        return BitmapFactory.decodeFile(file.getPath(), opts);
    }

    public static Bitmap loadBitmap(String fileName) {
        File file = new File(fileName);
        Options opts = new Options();
        opts.inPreferredConfig = Config.ARGB_8888;
        return BitmapFactory.decodeFile(file.getPath(), opts);
    }

    public static int saveBitmap(Bitmap bitmap, String filePath, String fileName) {
        OutputStream outputStream;
        FileNotFoundException ex;
        IOException e;
        Throwable th;
        try {
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            OutputStream outputStream2 = new FileOutputStream(new File(filePath, fileName));
            try {
                bitmap.compress(CompressFormat.JPEG, 75, outputStream2);
                outputStream2.close();
                outputStream = outputStream2;
                return IMAGE_TYPE_JPEG;
            } catch (FileNotFoundException e2) {
                ex = e2;
                outputStream = outputStream2;
                Log.w(TAG, ex);
                return Ola_ReturnType.OLA_ERROR_FILE_NOT_FOUND;
            } catch (IOException e3) {
                e = e3;
                outputStream = outputStream2;
                Log.w(TAG, e);
                return -100;
            } catch (Throwable th2) {
                th = th2;
                outputStream = outputStream2;
                throw th;
            }
        } catch (FileNotFoundException e4) {
            ex = e4;
            Log.w(TAG, ex);
            return Ola_ReturnType.OLA_ERROR_FILE_NOT_FOUND;
        } catch (IOException e5) {
            e = e5;
            Log.w(TAG, e);
            return -100;
        } catch (Throwable th3) {
            th = th3;
            throw th;
        }
    }

    public static Options getBitmapOptions(String fileName) {
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, opts);
        return opts;
    }

    public static int convert(Bitmap dst, JOlaBitmap src) {
        try {
            return OlaAndroidBitmapJNI.convert(dst, src);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return -1;
        }
    }

    public static int convert(JOlaBitmap dst, Bitmap src) {
        try {
            return OlaAndroidBitmapJNI.convert(dst, src);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return -1;
        }
    }

    public static int zoomIn(JOlaBitmap dst, JOlaBitmap src, float zoomRatio) {
        try {
            return OlaAndroidBitmapJNI.zoomInJOlaBitmap(src, dst, zoomRatio);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return -1;
        }
    }

    public static int mirror(Bitmap bitmap, boolean x_axes, boolean y_axes) {
        int ret = IMAGE_TYPE_JPEG;
        if (x_axes) {
            try {
                if (Ola_ReturnType.success(IMAGE_TYPE_JPEG)) {
                    ret = OlaAndroidBitmapJNI.mirrorXBitmap(bitmap);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return -1;
            }
        }
        if (y_axes && Ola_ReturnType.success(ret)) {
            return OlaAndroidBitmapJNI.mirrorYBitmap(bitmap);
        }
        return ret;
    }

    public static int rotateYuv(JOlaBitmap olaBitmap) {
        return -2;
    }
}
