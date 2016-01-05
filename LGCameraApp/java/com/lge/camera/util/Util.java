package com.lge.camera.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import com.lge.camera.R;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.BaseEngine;
import com.lge.voiceshutter.library.LGKeyRec;
import java.io.FileDescriptor;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class Util {
    public static final int DIRECTION_DOWN = 3;
    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_RIGHT = 1;
    public static final int DIRECTION_UP = 2;
    private static final int NUMBER_ONE = 1;
    private static final String TAG = "CameraApp";
    public static final int UNCONSTRAINED = -1;

    private Util() {
    }

    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees == 0 || b == null) {
            return b;
        }
        Matrix m = new Matrix();
        m.setRotate((float) degrees, ((float) b.getWidth()) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK, ((float) b.getHeight()) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
        try {
            Bitmap b2 = Bitmap.createBitmap(b, DIRECTION_LEFT, DIRECTION_LEFT, b.getWidth(), b.getHeight(), m, true);
            if (b == b2) {
                return b;
            }
            b.recycle();
            return b2;
        } catch (OutOfMemoryError ex) {
            CamLog.e(TAG, "OutOfMemoryError : " + ex);
            return b;
        }
    }

    public static Bitmap rotateAndMirror(Bitmap b, int degrees, boolean mirror) {
        if ((degrees == 0 && !mirror) || b == null) {
            return b;
        }
        Matrix m = new Matrix();
        m.setRotate((float) degrees, ((float) b.getWidth()) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK, ((float) b.getHeight()) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
        if (mirror) {
            m.postScale(-1.0f, RotateView.DEFAULT_TEXT_SCALE_X);
            degrees = (degrees + CameraConstants.DEGREE_360) % CameraConstants.DEGREE_360;
            if (degrees == 0 || degrees == MediaProviderUtils.ROTATION_180) {
                m.postTranslate((float) b.getWidth(), 0.0f);
            } else if (degrees == 90 || degrees == Tag.IMAGE_DESCRIPTION) {
                m.postTranslate((float) b.getHeight(), 0.0f);
            } else {
                throw new IllegalArgumentException("Invalid degrees=" + degrees);
            }
        }
        try {
            Bitmap b2 = Bitmap.createBitmap(b, DIRECTION_LEFT, DIRECTION_LEFT, b.getWidth(), b.getHeight(), m, true);
            if (b == b2) {
                return b;
            }
            b.recycle();
            return b2;
        } catch (OutOfMemoryError ex) {
            CamLog.e(TAG, "OutOfMemoryError : " + ex);
            return b;
        }
    }

    public static Bitmap getRoundedImage(Bitmap bmp, int width, int height, int radius) {
        if (bmp == null) {
            return null;
        }
        int padding = (width - radius) / DIRECTION_UP;
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Bitmap resize = Bitmap.createScaledBitmap(bmp, width, height, true);
        BitmapShader shader = new BitmapShader(resize, TileMode.CLAMP, TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);
        Canvas canvas = new Canvas(output);
        Rect rect = new Rect(padding, padding, padding + radius, padding + radius);
        RectF rectf = new RectF(rect);
        canvas.drawOval(rectf, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(resize, rect, rectf, paint);
        resize.recycle();
        bmp.recycle();
        return output;
    }

    public static Bitmap drawTextToBitmap(Context gContext, Bitmap gBitmap, int x, int y, String gText, int gFontSize, int gColor) {
        return drawTextToBitmap(gContext, gBitmap, x, y, gText, gFontSize, gColor, null);
    }

    public static Bitmap drawTextToBitmap(Context gContext, Bitmap gBitmap, int x, int y, String gText, int gFontSize, int gColor, String gTypeFace) {
        Bitmap bitmap = gBitmap;
        Config bitmapConfig = bitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(NUMBER_ONE);
        paint.setColor(gColor);
        paint.setTextSize((float) gFontSize);
        paint.setShadowLayer(RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, UNCONSTRAINED);
        paint.getTextBounds(gText, DIRECTION_LEFT, gText.length(), new Rect());
        if (gTypeFace != null) {
            paint.setTypeface(Typeface.createFromAsset(gContext.getAssets(), gTypeFace));
        }
        canvas.drawText(gText, (float) x, (float) y, paint);
        return bitmap;
    }

    public static Bitmap createCaptureBitmap(byte[] data, int orientation) {
        return rotate(makeBitmap(data, UNCONSTRAINED, 51200), orientation);
    }

    public static Bitmap makeBitmapFromRawData(byte[] rawData, int width, int height) {
        try {
            ByteBuffer bf = ByteBuffer.allocate(rawData.length * 4);
            bf.put(rawData);
            Bitmap bmp = Bitmap.createBitmap(width, height, Config.RGB_565);
            bmp.copyPixelsFromBuffer(bf);
            return bmp;
        } catch (OutOfMemoryError ex) {
            CamLog.e(TAG, "Got oom exception ", ex);
            return null;
        }
    }

    public static <T> int indexOf(T[] array, T s) {
        for (int i = DIRECTION_LEFT; i < array.length; i += NUMBER_ONE) {
            if (array[i].equals(s)) {
                return i;
            }
        }
        return UNCONSTRAINED;
    }

    public static Bitmap makeBitmap(byte[] jpegData, int minSideLength, int maxNumOfPixels) {
        Bitmap bitmap = null;
        try {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(jpegData, DIRECTION_LEFT, jpegData.length, options);
            if (options.mCancel || options.outWidth == UNCONSTRAINED || options.outHeight == UNCONSTRAINED) {
                CamLog.w(TAG, "makeBitmap decordByteArray fail");
                return bitmap;
            }
            options.inSampleSize = computeSampleSize(options, UNCONSTRAINED, maxNumOfPixels);
            CamLog.d(TAG, "maxNumOfPixels : " + maxNumOfPixels + "options.inSampleSize: " + options.inSampleSize);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Config.ARGB_8888;
            bitmap = BitmapFactory.decodeByteArray(jpegData, DIRECTION_LEFT, jpegData.length, options);
            return bitmap;
        } catch (OutOfMemoryError ex) {
            CamLog.e(TAG, "Got oom exception ", ex);
        }
    }

    public static Bitmap makeBitmap(byte[] jpegData, boolean isFlip) {
        try {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(jpegData, DIRECTION_LEFT, jpegData.length, options);
            if (options.mCancel || options.outWidth == UNCONSTRAINED || options.outHeight == UNCONSTRAINED) {
                return null;
            }
            options.inJustDecodeBounds = false;
            options.inDither = true;
            options.inPreferredConfig = Config.ARGB_8888;
            options.inMutable = true;
            if (!isFlip) {
                return BitmapFactory.decodeByteArray(jpegData, DIRECTION_LEFT, jpegData.length, options);
            }
            Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, DIRECTION_LEFT, jpegData.length, options);
            Matrix sideInversion = new Matrix();
            if (ExifUtil.getOrientation(jpegData) == 6 || ExifUtil.getOrientation(jpegData) == 8) {
                sideInversion.setScale(RotateView.DEFAULT_TEXT_SCALE_X, -1.0f);
            } else {
                sideInversion.setScale(-1.0f, RotateView.DEFAULT_TEXT_SCALE_X);
            }
            Bitmap convertBmp = Bitmap.createBitmap(bitmap, DIRECTION_LEFT, DIRECTION_LEFT, bitmap.getWidth(), bitmap.getHeight(), sideInversion, false);
            bitmap.recycle();
            return convertBmp;
        } catch (OutOfMemoryError ex) {
            CamLog.e(TAG, "Got oom exception ", ex);
            return null;
        }
    }

    public static int computeSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        int i = NUMBER_ONE;
        if (maxNumOfPixels != NUMBER_ONE) {
            int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
            if (initialSize <= 8) {
                i = NUMBER_ONE;
                while (i < initialSize) {
                    i <<= NUMBER_ONE;
                }
            } else {
                i = ((initialSize + 7) / 8) * 8;
            }
            CamLog.d(TAG, "computeSampleSize() return = " + i);
        }
        return i;
    }

    private static int computeInitialSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        double w = (double) options.outWidth;
        double h = (double) options.outHeight;
        int lowerBound = maxNumOfPixels == UNCONSTRAINED ? NUMBER_ONE : (int) Math.ceil(Math.sqrt((w * h) / ((double) maxNumOfPixels)));
        int upperBound = minSideLength == UNCONSTRAINED ? 128 : (int) Math.min(Math.floor(w / ((double) minSideLength)), Math.floor(h / ((double) minSideLength)));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if (maxNumOfPixels == UNCONSTRAINED && minSideLength == UNCONSTRAINED) {
            return NUMBER_ONE;
        }
        if (minSideLength != UNCONSTRAINED) {
            return upperBound;
        }
        return lowerBound;
    }

    public static Bitmap loadBitmapfromFile(String strFilename, int samplesize) {
        Options opts = new Options();
        opts.inSampleSize = samplesize;
        return BitmapFactory.decodeFile(strFilename, opts);
    }

    public static void clearImageViewDrawable(ImageView imageView) {
        if (imageView != null) {
            try {
                Drawable drawable = imageView.getDrawable();
                if (drawable != null) {
                    imageView.setImageDrawable(null);
                    drawable.setCallback(null);
                    recycleBitmapDrawable(drawable);
                }
            } catch (Exception e) {
                CamLog.e(TAG, "clearImageViewDrawable Exception ", e);
            }
        }
    }

    public static void clearImageViewDrawableOnly(ImageView imageView) {
        if (imageView != null) {
            try {
                Drawable drawable = imageView.getDrawable();
                if (drawable != null) {
                    imageView.setImageDrawable(null);
                    drawable.setCallback(null);
                }
            } catch (Exception e) {
                CamLog.e(TAG, "clearImageViewDrawable Exception ", e);
            }
        }
    }

    public static void recycleBitmapDrawable(Drawable drawable) {
        if (drawable != null) {
            try {
                if (drawable instanceof BitmapDrawable) {
                    Bitmap bm = ((BitmapDrawable) drawable).getBitmap();
                    if (bm != null) {
                        bm.recycle();
                    }
                }
                drawable.setCallback(null);
            } catch (Exception e) {
                CamLog.e(TAG, "recycleBitmapDrawable Exception ", e);
            }
        }
    }

    public static void clearImageViewBackgroundDrawable(ImageView imageView) {
        if (imageView != null) {
            try {
                if (imageView.getBackground() != null) {
                    imageView.getBackground().setCallback(null);
                    imageView.setBackground(null);
                }
            } catch (Exception e) {
                CamLog.e(TAG, "clearImageViewDrawable Exception ", e);
            }
        }
    }

    public static void recursiveRecycle(View rootView) {
        if (rootView != null) {
            rootView.setBackground(null);
            if (rootView instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) rootView;
                int count = group.getChildCount();
                for (int i = DIRECTION_LEFT; i < count; i += NUMBER_ONE) {
                    recursiveRecycle(group.getChildAt(i));
                }
                if (!(rootView instanceof AdapterView)) {
                    group.removeAllViews();
                }
            }
            if (rootView instanceof ImageView) {
                ((ImageView) rootView).setImageDrawable(null);
            }
        }
    }

    public static void Assert(boolean cond) {
        if (!cond) {
            throw new AssertionError();
        }
    }

    public static Animation slideOut(View view, int to, int duration, AnimationListener al) {
        if (view == null) {
            return null;
        }
        Animation anim;
        view.clearAnimation();
        view.setVisibility(4);
        switch (to) {
            case DIRECTION_LEFT /*0*/:
                anim = new TranslateAnimation(0.0f, (float) (((-view.getWidth()) * DialogCreater.DIALOG_ID_HELP_NIGHT) / 100), 0.0f, 0.0f);
                break;
            case NUMBER_ONE /*1*/:
                anim = new TranslateAnimation(0.0f, (float) ((view.getWidth() * DialogCreater.DIALOG_ID_HELP_NIGHT) / 100), 0.0f, 0.0f);
                break;
            case DIRECTION_UP /*2*/:
                anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (((-view.getHeight()) * DialogCreater.DIALOG_ID_HELP_NIGHT) / 100));
                break;
            case DIRECTION_DOWN /*3*/:
                anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) ((view.getHeight() * DialogCreater.DIALOG_ID_HELP_NIGHT) / 100));
                break;
            default:
                throw new IllegalArgumentException(Integer.toString(to));
        }
        anim.setDuration((long) duration);
        anim.setAnimationListener(al);
        anim.setInterpolator(new AccelerateInterpolator(CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK));
        view.startAnimation(anim);
        return anim;
    }

    public static Animation slideIn(View view, int from, int duration, AnimationListener al) {
        if (view == null) {
            return null;
        }
        Animation anim;
        view.clearAnimation();
        view.setVisibility(4);
        switch (from) {
            case DIRECTION_LEFT /*0*/:
                anim = new TranslateAnimation((float) (((-view.getWidth()) * DialogCreater.DIALOG_ID_HELP_NIGHT) / 100), 0.0f, 0.0f, 0.0f);
                break;
            case NUMBER_ONE /*1*/:
                anim = new TranslateAnimation((float) ((view.getWidth() * DialogCreater.DIALOG_ID_HELP_NIGHT) / 100), 0.0f, 0.0f, 0.0f);
                break;
            case DIRECTION_UP /*2*/:
                anim = new TranslateAnimation(0.0f, 0.0f, (float) (((-view.getHeight()) * DialogCreater.DIALOG_ID_HELP_NIGHT) / 100), 0.0f);
                break;
            case DIRECTION_DOWN /*3*/:
                anim = new TranslateAnimation(0.0f, 0.0f, (float) ((view.getHeight() * DialogCreater.DIALOG_ID_HELP_NIGHT) / 100), 0.0f);
                break;
            default:
                throw new IllegalArgumentException(Integer.toString(from));
        }
        anim.setDuration((long) duration);
        anim.setAnimationListener(al);
        anim.setInterpolator(new DecelerateInterpolator(CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK));
        view.startAnimation(anim);
        return anim;
    }

    public static void startAlphaAnimation(View view, int start, int end, int duration, AnimationListener al) {
        if (view != null) {
            Animation anim = new AlphaAnimation((float) start, (float) end);
            anim.setDuration((long) duration);
            anim.setAnimationListener(al);
            view.clearAnimation();
            view.startAnimation(anim);
        }
    }

    public static <T> T checkNotNull(T object) {
        if (object != null) {
            return object;
        }
        throw new NullPointerException();
    }

    public static boolean equals(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static boolean isPowerOf2(int n) {
        return ((-n) & n) == n;
    }

    public static int nextPowerOf2(int n) {
        n += UNCONSTRAINED;
        n |= n >>> 16;
        n |= n >>> 8;
        n |= n >>> 4;
        n |= n >>> DIRECTION_UP;
        return (n | (n >>> NUMBER_ONE)) + NUMBER_ONE;
    }

    public static int distance(int start, int end) {
        return Math.abs(start - end);
    }

    public static float distance(float x, float y, float sx, float sy) {
        float dx = x - sx;
        float dy = y - sy;
        return FloatMath.sqrt((dx * dx) + (dy * dy));
    }

    public static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    public static float dpToPx(Context context, float dp) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.density * dp;
    }

    public static int[] SizeString2WidthHeight(String sizeString) {
        int[] sizeArray = new int[DIRECTION_UP];
        String[] sizeStringArray = sizeString.split("@")[DIRECTION_LEFT].split("x");
        sizeArray[DIRECTION_LEFT] = Integer.parseInt(sizeStringArray[DIRECTION_LEFT]);
        sizeArray[NUMBER_ONE] = Integer.parseInt(sizeStringArray[NUMBER_ONE]);
        return sizeArray;
    }

    public static String size2String(int width, int height) {
        String divideChar = "x";
        return String.valueOf(width) + "x" + String.valueOf(height);
    }

    public static int[] adjustViewSize(int[] standard, int[] input) {
        int nor_width = standard[DIRECTION_LEFT];
        int nor_height = standard[NUMBER_ONE];
        int inWidth = input[DIRECTION_LEFT];
        int inHeight = input[NUMBER_ONE];
        int outWidth = inWidth;
        int outHeight = inHeight;
        if (inWidth > inHeight) {
            outWidth = nor_width;
            outHeight = (inHeight * outWidth) / inWidth;
            if (outHeight > nor_height) {
                outHeight = nor_height;
                outWidth = (inWidth * outHeight) / inHeight;
            }
        } else {
            outHeight = nor_height;
            outWidth = (inWidth * outHeight) / inHeight;
            if (outWidth > nor_width) {
                outWidth = nor_width;
                outHeight = (inHeight * outWidth) / inWidth;
            }
        }
        int[] adjustedSize = new int[DIRECTION_UP];
        adjustedSize[DIRECTION_LEFT] = outWidth;
        adjustedSize[NUMBER_ONE] = outHeight;
        return adjustedSize;
    }

    public static void debugStackTrace(String from) {
        Log.e(TAG, "[Debug] Printing stack trace : from - " + from);
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (int i = DIRECTION_DOWN; i < elements.length; i += NUMBER_ONE) {
            StackTraceElement s = elements[i];
            Log.d(TAG, "[Debug] \tat " + from + s.getClassName() + "." + s.getMethodName() + "(" + s.getFileName() + ":" + s.getLineNumber() + ")");
        }
    }

    public static long getIdFromUri(Activity activity, Uri contentUri) {
        long j = -1;
        String[] proj = new String[NUMBER_ONE];
        proj[DIRECTION_LEFT] = "_id";
        if (!(activity == null || contentUri == null)) {
            Cursor cursor = null;
            try {
                cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
                if (cursor != null) {
                    if (cursor.getCount() != 0) {
                        int column_index = cursor.getColumnIndexOrThrow("_id");
                        cursor.moveToFirst();
                        j = cursor.getLong(column_index);
                        if (cursor != null) {
                            cursor.close();
                        }
                    } else if (cursor != null) {
                        cursor.close();
                    }
                } else if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                CamLog.e(TAG, "Could not ID from URI", e);
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return j;
    }

    public static void broadcastNewPicture(Context context, Uri uri) {
        context.sendBroadcast(new Intent("android.hardware.action.NEW_PICTURE", uri));
        context.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", uri));
    }

    public static void requestUpBoxBackupPhoto(Context context, String filepath, boolean cond) {
        if (FunctionProperties.isUPlusBox() && cond) {
            ArrayList<String> pathList = new ArrayList();
            pathList.add(filepath);
            Intent intent = new Intent();
            intent.setAction("lg.uplusbox.intent.action.CLOUD_BACKUP_PHOTO");
            intent.putStringArrayListExtra("extra_file_path", pathList);
            context.sendBroadcast(intent);
            return;
        }
        CamLog.i(TAG, "SetUplusBoxMode : model is not supported.");
    }

    public static double getPictureSizeScale(int cameraID, String shotMode, int projectCode, String pictureSize) {
        if (shotMode.equals(CameraConstants.TYPE_SHOTMODE_CONTINUOUS)) {
            switch (projectCode) {
                case DIRECTION_UP /*2*/:
                case DIRECTION_DOWN /*3*/:
                case BaseEngine.DEFAULT_PRIORITY /*10*/:
                    return 0.9d;
                default:
                    return 1.111111d;
            }
        } else if (CameraConstants.TYPE_SHOTMODE_PANORAMA.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA.equals(shotMode) || cameraID == NUMBER_ONE) {
            return 1.111111d;
        } else {
            if (CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA.equals(shotMode)) {
                return 0.5d;
            }
            if (shotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
                return 3.1d;
            }
            switch (projectCode) {
                case DIRECTION_DOWN /*3*/:
                case BaseEngine.DEFAULT_PRIORITY /*10*/:
                    return 1.1d;
                case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
                case Tag.GPS_MAP_DATUM /*18*/:
                case ModelProperties.CODE_GXR /*32*/:
                    if (MultimediaProperties.EFFECTS_ENFORCED_SIZE_FOR_UVGA.equals(pictureSize)) {
                        CamLog.i(TAG, "getPictureSizeScale1 is = " + pictureSize);
                        return 1.1d;
                    }
                    CamLog.i(TAG, "getPictureSizeScale2 is = " + pictureSize);
                    return 0.8d;
                default:
                    return 0.8d;
            }
        }
    }

    public static int getSampleSize(byte[] jpegData, FileDescriptor fd, String filePath, Options opts, int targetWidth, int targetHeight) {
        if (opts == null) {
            return 4;
        }
        int imageLength;
        int targetLength;
        opts.inJustDecodeBounds = true;
        if (jpegData != null) {
            BitmapFactory.decodeByteArray(jpegData, DIRECTION_LEFT, jpegData.length, opts);
        } else if (fd != null) {
            BitmapFactory.decodeFileDescriptor(fd, null, opts);
        } else if (filePath != null) {
            BitmapFactory.decodeFile(filePath, opts);
        }
        int imageWidth = opts.outWidth;
        int imageHeight = opts.outHeight;
        if (imageHeight >= imageWidth) {
            imageLength = imageHeight;
        } else {
            imageLength = imageWidth;
        }
        if (targetHeight >= targetWidth) {
            targetLength = targetHeight;
        } else {
            targetLength = targetWidth;
        }
        if (imageWidth <= 0 || imageHeight <= 0) {
            return 4;
        }
        int sampleSize = NUMBER_ONE;
        while (imageLength / DIRECTION_UP >= targetLength) {
            imageLength /= DIRECTION_UP;
            sampleSize *= DIRECTION_UP;
        }
        opts.inJustDecodeBounds = false;
        CamLog.i(TAG, "getSampleSize is = " + sampleSize);
        return sampleSize;
    }

    public static int[] getFitSizeOfBitmapForLCD(Activity activity, int imageWidth, int imageHeight) {
        int[] bitmapSize = new int[DIRECTION_UP];
        int dstWidth = DIRECTION_LEFT;
        int dstHeight = DIRECTION_LEFT;
        if (activity != null) {
            int lcdWidth = Common.getPixelFromDimens(activity.getApplicationContext(), R.dimen.lcd_width);
            int lcdHeight = Common.getPixelFromDimens(activity.getApplicationContext(), R.dimen.lcd_height);
            float imageRatio;
            if (imageWidth >= imageHeight) {
                imageRatio = ((float) imageWidth) / ((float) imageHeight);
                if (imageRatio > ((float) lcdWidth) / ((float) lcdHeight)) {
                    dstWidth = lcdWidth;
                    dstHeight = (int) (((float) lcdWidth) / imageRatio);
                } else {
                    dstHeight = lcdHeight;
                    dstWidth = (int) (((float) lcdHeight) * imageRatio);
                }
            } else {
                imageRatio = ((float) imageHeight) / ((float) imageWidth);
                if (imageRatio > ((float) lcdWidth) / ((float) lcdHeight)) {
                    dstHeight = lcdWidth;
                    dstWidth = (int) (((float) lcdWidth) / imageRatio);
                } else {
                    dstWidth = lcdHeight;
                    dstHeight = (int) (((float) lcdHeight) * imageRatio);
                }
            }
        }
        bitmapSize[DIRECTION_LEFT] = dstWidth;
        bitmapSize[NUMBER_ONE] = dstHeight;
        CamLog.d(TAG, "dstWidth = " + dstWidth + ", dstHeight = " + dstHeight);
        return bitmapSize;
    }

    public static int[] calcFitSizeOfImageForLCD(Activity activity, int imageWidth, int imageHeight, int orientation) {
        int[] bitmapSize = new int[DIRECTION_UP];
        int dstWidth = DIRECTION_LEFT;
        int dstHeight = DIRECTION_LEFT;
        if (activity != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            ((WindowManager) activity.getSystemService("window")).getDefaultDisplay().getMetrics(outMetrics);
            int lcdWidth = outMetrics.widthPixels;
            int lcdHeight = outMetrics.heightPixels;
            if (orientation == NUMBER_ONE || orientation == DIRECTION_DOWN) {
                lcdWidth = outMetrics.heightPixels;
                lcdHeight = outMetrics.widthPixels;
            }
            float imageRatio;
            if (imageWidth >= imageHeight) {
                imageRatio = ((float) imageWidth) / ((float) imageHeight);
                if (imageRatio > ((float) lcdWidth) / ((float) lcdHeight)) {
                    if (orientation == 0 || orientation == DIRECTION_UP) {
                        dstWidth = lcdWidth;
                        dstHeight = (int) (((float) lcdWidth) / imageRatio);
                    } else {
                        dstWidth = lcdHeight;
                        dstHeight = (int) (((float) lcdHeight) / imageRatio);
                    }
                } else if (orientation == 0 || orientation == DIRECTION_UP) {
                    dstHeight = lcdHeight;
                    dstWidth = (int) (((float) lcdHeight) * imageRatio);
                } else {
                    dstWidth = lcdHeight;
                    dstHeight = (int) (((float) lcdHeight) / imageRatio);
                }
            } else {
                imageRatio = ((float) imageHeight) / ((float) imageWidth);
                if (imageRatio > ((float) lcdWidth) / ((float) lcdHeight)) {
                    if (orientation == 0 || orientation == DIRECTION_UP) {
                        dstHeight = lcdHeight;
                        dstWidth = (int) (((float) lcdHeight) / imageRatio);
                    } else {
                        dstHeight = lcdWidth;
                        dstWidth = (int) (((float) lcdWidth) / imageRatio);
                    }
                } else if (orientation == 0 || orientation == DIRECTION_UP) {
                    dstHeight = lcdHeight;
                    dstWidth = (int) (((float) lcdHeight) / imageRatio);
                } else {
                    dstWidth = lcdHeight;
                    dstHeight = (int) (((float) lcdHeight) * imageRatio);
                }
            }
        }
        bitmapSize[DIRECTION_LEFT] = dstWidth;
        bitmapSize[NUMBER_ONE] = dstHeight;
        CamLog.d(TAG, "dstWidth = " + dstWidth + ", dstHeight = " + dstHeight);
        return bitmapSize;
    }

    public static int getDisplayRotation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        CamLog.i(TAG, "getDisplayRotation = " + rotation);
        switch (rotation) {
            case DIRECTION_LEFT /*0*/:
                return DIRECTION_LEFT;
            case DIRECTION_UP /*2*/:
                return MediaProviderUtils.ROTATION_180;
            case DIRECTION_DOWN /*3*/:
                return Tag.IMAGE_DESCRIPTION;
            default:
                return 90;
        }
    }

    public static boolean isConfigureLandscape(Resources resource) {
        if (resource == null || resource.getConfiguration().orientation != DIRECTION_UP) {
            return false;
        }
        return true;
    }

    public static boolean isEqualDegree(Resources resource, int current, int input) {
        if (isConfigureLandscape(resource)) {
            if (current == input) {
                return true;
            }
            return false;
        } else if ((current + 90) % CameraConstants.DEGREE_360 != input) {
            return false;
        } else {
            return true;
        }
    }

    public static int convertDegree(Resources resource, int degree) {
        return isConfigureLandscape(resource) ? degree : (degree + 90) % CameraConstants.DEGREE_360;
    }

    public static int convertLayoutDegree(Resources resource, int degree) {
        return isConfigureLandscape(resource) ? degree : (degree + Tag.IMAGE_DESCRIPTION) % CameraConstants.DEGREE_360;
    }

    public static int convertDegreeToSurfaceRotation(int degree) {
        CamLog.i(TAG, "degree = " + degree);
        switch (degree) {
            case MediaProviderUtils.ROTATION_90 /*90*/:
                return NUMBER_ONE;
            case MediaProviderUtils.ROTATION_180 /*180*/:
                return DIRECTION_UP;
            case Tag.IMAGE_DESCRIPTION /*270*/:
                return DIRECTION_DOWN;
            default:
                return DIRECTION_LEFT;
        }
    }

    public static int[] appendToIntArray(int[] array, int element) {
        int length = array.length;
        array = Arrays.copyOf(array, length + NUMBER_ONE);
        array[length] = element;
        return array;
    }

    public static CharSequence[] appendToCharSequenceArray(CharSequence[] array, CharSequence element) {
        int length = array.length;
        array = (CharSequence[]) Arrays.copyOf(array, length + NUMBER_ONE);
        array[length] = element;
        return array;
    }
}
