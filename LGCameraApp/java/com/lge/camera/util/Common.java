package com.lge.camera.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.PowerManager.WakeLock;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.MediaStore.Images.Media;
import android.provider.Settings.System;
import android.util.Log;
import android.view.IWindowManager;
import android.view.IWindowManager.Stub;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.lge.camera.R;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.library.FaceDetector;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Common {
    public static int APP_CAMCORDER_INSTANCE_COUNT = 0;
    public static int APP_CAMERA_INSTANCE_COUNT = 0;
    public static int APP_CAMLOADING_INSTANCE_COUNT = 0;
    public static int APP_POSTVIEW_INSTANCE_COUNT = 0;
    public static int APP_SHOTMODE_POSTVIEW_INSTANCE_COUNT = 0;
    public static boolean IS_ENTER_CONDITION = false;
    public static final int KEYCODE_TESTMODE_CAMCORDER_CAMCORDER_MODE_OFF = 134;
    public static final int KEYCODE_TESTMODE_CAMCORDER_ERASE_MOVING_FILE = 133;
    public static final int KEYCODE_TESTMODE_CAMCORDER_MODE_ON = 129;
    public static final int KEYCODE_TESTMODE_CAMCORDER_PLAY_MOVING_FILE = 132;
    public static final int KEYCODE_TESTMODE_CAMCORDER_RECORD_STOP_AND_SAVE = 131;
    public static final int KEYCODE_TESTMODE_CAMCORDER_SHOT_RECORD_START = 130;
    public static final int NO_BUTTON_POPUP_DISMISS_DELAY = 2000;
    private static float backupTransitionScale;
    private static float backupWidowScale;
    public static long duration;
    public static long endTime;
    public static long interim_duration;
    public static long interim_endTime;
    public static long interim_startTime;
    public static boolean isChangeAnimationScale;
    private static boolean mSecureCamera;
    public static long mStartTime;
    private static IWindowManager mWindowManager;
    private static boolean sIsQuickWindowCameraMode;
    private static boolean sIsSmartCoverClosed;
    public static long startTime;

    static {
        APP_CAMLOADING_INSTANCE_COUNT = 0;
        APP_CAMERA_INSTANCE_COUNT = 0;
        APP_CAMCORDER_INSTANCE_COUNT = 0;
        APP_POSTVIEW_INSTANCE_COUNT = 0;
        APP_SHOTMODE_POSTVIEW_INSTANCE_COUNT = 0;
        IS_ENTER_CONDITION = true;
        startTime = 0;
        endTime = 0;
        duration = 0;
        interim_startTime = 0;
        interim_endTime = 0;
        interim_duration = 0;
        mSecureCamera = false;
        sIsQuickWindowCameraMode = false;
        sIsSmartCoverClosed = false;
        isChangeAnimationScale = false;
        mStartTime = 0;
    }

    public static void toast(Context context, String msg) {
        try {
            Toast.makeText(context, msg, 0).show();
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "Failed to show toast!", e);
        }
    }

    public static void toastLong(Context context, String msg) {
        Toast.makeText(context, msg, 1).show();
    }

    public static boolean isFileExist(String fileName) {
        return new File(fileName).exists();
    }

    public static TranslateAnimation getTranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, int duration, boolean fillAfter, AnimationListener listener) {
        CamLog.d(FaceDetector.TAG, "TranslateAnimation fromX = " + fromXDelta + " toX = " + toXDelta);
        TranslateAnimation ta = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        ta.setFillAfter(fillAfter);
        ta.setDuration((long) duration);
        if (listener != null) {
            ta.setAnimationListener(listener);
        }
        return ta;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean copyFile(java.lang.String r11, java.lang.String r12) {
        /*
        r8 = 1;
        r7 = new java.io.File;
        r7.<init>(r11);
        r9 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0 = new byte[r9];
        r4 = 0;
        r2 = 0;
        r6 = new java.io.File;	 Catch:{ IOException -> 0x0049 }
        r6.<init>(r12);	 Catch:{ IOException -> 0x0049 }
        r6.createNewFile();	 Catch:{ IOException -> 0x0049 }
        r5 = new java.io.FileOutputStream;	 Catch:{ IOException -> 0x0049 }
        r5.<init>(r6);	 Catch:{ IOException -> 0x0049 }
        r3 = new java.io.FileInputStream;	 Catch:{ IOException -> 0x006b, all -> 0x0064 }
        r3.<init>(r7);	 Catch:{ IOException -> 0x006b, all -> 0x0064 }
    L_0x001e:
        r9 = r3.read(r0);	 Catch:{ IOException -> 0x0029, all -> 0x0067 }
        r10 = -1;
        if (r9 == r10) goto L_0x003a;
    L_0x0025:
        r5.write(r0);	 Catch:{ IOException -> 0x0029, all -> 0x0067 }
        goto L_0x001e;
    L_0x0029:
        r1 = move-exception;
        r1.printStackTrace();	 Catch:{ IOException -> 0x006e, all -> 0x0067 }
        r8 = 0;
        r2 = r3;
    L_0x002f:
        r5.close();	 Catch:{ IOException -> 0x0042, all -> 0x0064 }
        r4 = 0;
    L_0x0033:
        closeSilently(r2);
        closeSilently(r4);
    L_0x0039:
        return r8;
    L_0x003a:
        r5.flush();	 Catch:{ IOException -> 0x0029, all -> 0x0067 }
        r3.close();	 Catch:{ IOException -> 0x0029, all -> 0x0067 }
        r2 = 0;
        goto L_0x002f;
    L_0x0042:
        r1 = move-exception;
        r1.printStackTrace();	 Catch:{ IOException -> 0x006b, all -> 0x0064 }
        r8 = 0;
        r4 = r5;
        goto L_0x0033;
    L_0x0049:
        r1 = move-exception;
    L_0x004a:
        r1.printStackTrace();	 Catch:{ all -> 0x005c }
        r9 = "LGCamera";
        r10 = "Failed to copyFile";
        com.lge.camera.util.CamLog.d(r9, r10);	 Catch:{ all -> 0x005c }
        r8 = 0;
        closeSilently(r2);
        closeSilently(r4);
        goto L_0x0039;
    L_0x005c:
        r9 = move-exception;
    L_0x005d:
        closeSilently(r2);
        closeSilently(r4);
        throw r9;
    L_0x0064:
        r9 = move-exception;
        r4 = r5;
        goto L_0x005d;
    L_0x0067:
        r9 = move-exception;
        r2 = r3;
        r4 = r5;
        goto L_0x005d;
    L_0x006b:
        r1 = move-exception;
        r4 = r5;
        goto L_0x004a;
    L_0x006e:
        r1 = move-exception;
        r2 = r3;
        r4 = r5;
        goto L_0x004a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.util.Common.copyFile(java.lang.String, java.lang.String):boolean");
    }

    public static String getFileFullPathFromUri(ContentResolver resolver, Uri uri) {
        CamLog.i(FaceDetector.TAG, "getFileFullPathFromUri uri = " + uri.toString());
        Cursor cursor = null;
        String result = null;
        try {
            cursor = resolver.query(uri, new String[]{"_data"}, null, null, null);
            if (cursor == null) {
                CamLog.d(FaceDetector.TAG, "error! cursor is null");
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            }
            int column_index = cursor.getColumnIndexOrThrow("_data");
            if (cursor.moveToFirst()) {
                CamLog.d(FaceDetector.TAG, "path from Uri: " + cursor.getString(column_index));
                result = cursor.getString(column_index);
            }
            if (cursor != null) {
                cursor.close();
            }
            return result;
        } catch (Exception ex) {
            CamLog.e(FaceDetector.TAG, "managedQuery() Exception! " + ex.toString());
            result = null;
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getFileNamePathFromUri(ContentResolver resolver, Uri uri) {
        File file;
        Exception ex;
        Throwable th;
        CamLog.i(FaceDetector.TAG, "getFileNamePathFromUri uri = " + uri.toString());
        Cursor cursor = null;
        String result = null;
        try {
            cursor = resolver.query(uri, new String[]{"_data"}, null, null, null);
            if (cursor == null) {
                CamLog.d(FaceDetector.TAG, "error! cursor is null");
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            }
            int column_index = cursor.getColumnIndexOrThrow("_data");
            if (cursor.moveToFirst()) {
                CamLog.d(FaceDetector.TAG, "path from Uri: " + cursor.getString(column_index));
                File file2 = new File(cursor.getString(column_index));
                try {
                    result = file2.getName();
                    file = file2;
                } catch (Exception e) {
                    ex = e;
                    file = file2;
                    try {
                        CamLog.e(FaceDetector.TAG, "managedQuery() Exception! " + ex.toString());
                        result = null;
                        if (cursor != null) {
                            cursor.close();
                        }
                        return result;
                    } catch (Throwable th2) {
                        th = th2;
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    file = file2;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return result;
        } catch (Exception e2) {
            ex = e2;
            CamLog.e(FaceDetector.TAG, "managedQuery() Exception! " + ex.toString());
            result = null;
            if (cursor != null) {
                cursor.close();
            }
            return result;
        }
    }

    public static Uri getUriFromFilePath(ContentResolver resolver, String fullFilePath) {
        CamLog.i(FaceDetector.TAG, "getUriFromFilePath fullFilePath = " + fullFilePath);
        String selection = "_data='" + fullFilePath + "'";
        Uri uri = null;
        Cursor cursor = null;
        try {
            cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, null, selection, null, null);
            if (cursor != null && cursor.moveToNext()) {
                uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, (long) cursor.getInt(0));
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            CamLog.w(FaceDetector.TAG, "deleteTimeMachineImages fail!:", e);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return uri;
    }

    public static String getNameWithoutExtension(ContentResolver resolver, Uri uri) {
        String filename = getFileNamePathFromUri(resolver, uri);
        if (filename == null) {
            return null;
        }
        return filename.substring(0, filename.lastIndexOf(46));
    }

    public static void closeSilently(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable t) {
                CamLog.e(FaceDetector.TAG, "closeSilently : " + t);
            }
        }
    }

    public static String getCurrentDateTime(long dateTime) {
        String stringDateTime = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date(dateTime));
        CamLog.i(FaceDetector.TAG, "dateTime = " + stringDateTime);
        return stringDateTime;
    }

    public static void deleteDirectory(File file) {
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files == null) {
                CamLog.d(FaceDetector.TAG, "file is not a directory, null return");
                return;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
            file.delete();
        }
    }

    public static int getPixelFromDimens(Context context, int id) {
        return Math.round(getDimension(context, id));
    }

    public static float getDimension(Context context, int id) {
        return context.getResources().getDimension(id);
    }

    public static void resetLayoutParameter(LayoutParams lp) {
        if (lp != null) {
            try {
                int ruleSize = lp.getRules().length;
                for (int i = 0; i < ruleSize; i++) {
                    lp.addRule(i, 0);
                }
            } catch (NullPointerException e) {
                CamLog.e(FaceDetector.TAG, "NullPointerException : " + e);
            }
        }
    }

    public static void checkEnteringTime(boolean end) {
        if (end) {
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            if (startTime != 0) {
                Log.i(FaceDetector.TAG, "Entering time is : " + duration);
            }
            startTime = 0;
            endTime = 0;
            return;
        }
        startTime = System.currentTimeMillis();
    }

    public static long interimCheckTime(boolean end) {
        if (end) {
            interim_endTime = System.currentTimeMillis();
            interim_duration = interim_endTime - interim_startTime;
            interim_startTime = 0;
            interim_endTime = 0;
            return interim_duration;
        }
        interim_startTime = System.currentTimeMillis();
        return 0;
    }

    public static boolean isScreenLocked() {
        return mSecureCamera ? false : getScreenLock();
    }

    public static boolean getScreenLock() {
        return !SystemProperties.get(CameraConstants.PROPERTY_LOCKSCREEN, "0").equals("0");
    }

    public static boolean useSecureLockImage() {
        String lock = SystemProperties.get(CameraConstants.PROPERTY_LOCKSCREEN, "0");
        return (!isSecureCamera() || "0".equals(lock) || "1".equals(lock)) ? false : true;
    }

    public static boolean isSecureCamera() {
        return mSecureCamera;
    }

    public static void setSecureCamera(boolean set) {
        mSecureCamera = set;
    }

    public static boolean isFaceUnlock() {
        return isSecureCamera();
    }

    public static void configureWindowFlag(Window window, boolean fullScreen, boolean secureOrQuickCamera) {
        if (window != null) {
            if (fullScreen) {
                window.addFlags(Ola_ImageFormat.YUVPLANAR_LABEL);
            }
            CamLog.d(FaceDetector.TAG, "quickCamCase = " + secureOrQuickCamera);
            if (secureOrQuickCamera) {
                window.addFlags(4718592);
            } else {
                window.clearFlags(4718592);
            }
        }
    }

    public static void setQuickWindowCameraMode(boolean set) {
        sIsQuickWindowCameraMode = set;
    }

    public static boolean isQuickWindowCameraMode() {
        return sIsQuickWindowCameraMode;
    }

    public static boolean isSmartCoverClosed() {
        return sIsSmartCoverClosed;
    }

    public static void setSmartCoverClosed(boolean set) {
        sIsSmartCoverClosed = set;
    }

    public static void turnOffAnimation() {
        CamLog.d(FaceDetector.TAG, "turnOffAnimation");
        isChangeAnimationScale = true;
        mWindowManager = Stub.asInterface(ServiceManager.getService("window"));
        try {
            backupWidowScale = mWindowManager.getAnimationScale(0);
            backupTransitionScale = mWindowManager.getAnimationScale(1);
            mWindowManager.setAnimationScale(0, 0.0f);
            mWindowManager.setAnimationScale(1, 0.0f);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (NoSuchMethodError e2) {
            e2.printStackTrace();
        }
    }

    public static void turnOnAnimation() {
        if (isChangeAnimationScale) {
            isChangeAnimationScale = false;
            CamLog.d(FaceDetector.TAG, "turnOnAnimation");
            mWindowManager = Stub.asInterface(ServiceManager.getService("window"));
            try {
                mWindowManager.setAnimationScale(0, backupWidowScale);
                mWindowManager.setAnimationScale(1, backupTransitionScale);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (NoSuchMethodError e2) {
                e2.printStackTrace();
            }
        }
    }

    public static boolean IsHeatingVideoSize(String recordingSize) {
        if (recordingSize == null) {
            Log.e(FaceDetector.TAG, "===>RecordingSize is null");
            return false;
        }
        int[] size = Util.SizeString2WidthHeight(recordingSize);
        if (size[0] < 1280 || size[1] < 720) {
            return false;
        }
        return true;
    }

    public static void reduceBrightnessMode(Context context, boolean start) {
        if (context != null && ProjectVariables.reduceBrightnessCamcorderMode()) {
            if (start) {
                context.sendBroadcast(new Intent("video_recording_preview_start"));
            } else {
                context.sendBroadcast(new Intent("video_recording_preview_stop"));
            }
        }
    }

    public static void galleryCacheDuringCameraApp(Context context, boolean onresume) {
        if (context != null && ProjectVariables.stopGalleryCacheduringCameraApp()) {
            if (onresume) {
                context.sendBroadcast(new Intent("lge.gallery.cache.stop"));
            } else {
                context.sendBroadcast(new Intent("lge.gallery.cache.start"));
            }
        }
    }

    public static void printRunningTask(Context context) {
        if (context != null) {
            List<RunningTaskInfo> runningTask = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(5);
            if (runningTask != null) {
                int taskSize = runningTask.size();
                CamLog.d(FaceDetector.TAG, "runningTask size:" + taskSize);
                ComponentName topActivity = null;
                for (int i = 0; i < taskSize; i++) {
                    if (runningTask.get(i) != null) {
                        topActivity = ((RunningTaskInfo) runningTask.get(i)).topActivity;
                    }
                    if (topActivity != null) {
                        CamLog.d(FaceDetector.TAG, "runningTask " + topActivity.getPackageName());
                    }
                }
            }
        }
    }

    public static void backlightControl(Activity activity) {
        WindowManager.LayoutParams params;
        if (ProjectVariables.useBackLightControl()) {
            try {
                int curBrightnessMode = System.getInt(activity.getContentResolver(), "screen_brightness_mode");
                int curBrightnessValue = System.getInt(activity.getContentResolver(), "screen_brightness");
                float curValue = ((float) curBrightnessValue) / 255.0f;
                params = activity.getWindow().getAttributes();
                if (curBrightnessMode == 0) {
                    params.screenBrightness = curValue * 0.8f;
                } else {
                    params.screenBrightness = -1.0f;
                }
                activity.getWindow().setAttributes(params);
                CamLog.d(FaceDetector.TAG, "Success to backlight control:curMode = " + curBrightnessMode + ", " + "curBright (30~255) = " + curBrightnessValue + ", " + "val = " + curValue + ", " + "ratio = " + 0.8f + ", " + "set = " + (curValue * 0.8f));
            } catch (Exception e) {
                CamLog.d(FaceDetector.TAG, "Fail to backlight control:", e);
            }
        } else if (CheckStatusManager.useBackLightControlInRecording()) {
            try {
                params = activity.getWindow().getAttributes();
                params.screenBrightness = -1.0f;
                activity.getWindow().setAttributes(params);
                CamLog.d(FaceDetector.TAG, "Success to backlight control: system setting  ");
            } catch (Exception e2) {
                CamLog.d(FaceDetector.TAG, "Fail to backlight control:", e2);
            }
        }
    }

    public static void backlightControlByVal(Activity activity, float scale) {
        try {
            int curBrightnessMode = System.getInt(activity.getContentResolver(), "screen_brightness_mode");
            int curBrightnessValue = System.getInt(activity.getContentResolver(), "screen_brightness");
            float curValue = ((float) curBrightnessValue) / 255.0f;
            float ratio = scale;
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            if (curBrightnessMode == 0) {
                params.screenBrightness = curValue * ratio;
            } else {
                params.screenBrightness = -1.0f;
            }
            activity.getWindow().setAttributes(params);
            CamLog.d(FaceDetector.TAG, "Success to backlight control ByVal:curMode = " + curBrightnessMode + ", " + "curBright (30~255) = " + curBrightnessValue + ", " + "val = " + curValue + ", " + "ratio = " + ratio + ", " + "set = " + (curValue * ratio));
        } catch (Exception e) {
            CamLog.d(FaceDetector.TAG, "Fail to backlight control:", e);
        }
    }

    public static void setBacklightToSystemSetting(Activity activity) {
        try {
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            params.screenBrightness = -1.0f;
            activity.getWindow().setAttributes(params);
            CamLog.d(FaceDetector.TAG, "Success to backlight control: system setting  ");
        } catch (Exception e) {
            CamLog.d(FaceDetector.TAG, "Fail to backlight control:", e);
        }
    }

    public static void setBacklightToMax(Activity activity) {
        try {
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            params.screenBrightness = RotateView.DEFAULT_TEXT_SCALE_X;
            activity.getWindow().setAttributes(params);
        } catch (Exception e) {
            CamLog.d(FaceDetector.TAG, "Fail to backlight control:", e);
        }
    }

    public static void setWakeLock(WakeLock wakeLock, boolean isAcquire) {
        if (wakeLock != null) {
            CamLog.d(FaceDetector.TAG, "WakeLock.isHeld() = " + wakeLock.isHeld() + ", isAcquire = " + isAcquire);
            if (isAcquire) {
                if (!wakeLock.isHeld()) {
                    wakeLock.acquire();
                }
            } else if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
    }

    public static String getUSBconfig() {
        CamLog.d(FaceDetector.TAG, "===>getUSBconfig : " + SystemProperties.get(CameraConstants.PROPERTY_USBCONFING, "0"));
        return SystemProperties.get(CameraConstants.PROPERTY_USBCONFING, "0");
    }

    public static String getUSBstate() {
        IOException e;
        Throwable th;
        FileReader fileReader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            FileReader reader = new FileReader(CameraConstants.PROPERTY_USBSTATE);
            if (reader != null) {
                while (true) {
                    try {
                        int data = reader.read();
                        if (data <= 0) {
                            break;
                        } else if (((char) data) != '\n') {
                            buffer.append((char) data);
                        }
                    } catch (IOException e2) {
                        e = e2;
                        fileReader = reader;
                    } catch (Throwable th2) {
                        th = th2;
                        fileReader = reader;
                    }
                }
                CamLog.d(FaceDetector.TAG, "===> getUSBState: " + buffer.toString() + ", len: " + buffer.toString().length());
                String stringBuffer = buffer.toString();
                if (reader == null) {
                    return stringBuffer;
                }
                try {
                    reader.close();
                    return stringBuffer;
                } catch (IOException e3) {
                    e3.printStackTrace();
                    return stringBuffer;
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e32) {
                    e32.printStackTrace();
                    fileReader = reader;
                }
            }
            fileReader = reader;
            return "0";
        } catch (IOException e4) {
            e32 = e4;
            try {
                e32.printStackTrace();
                if (fileReader != null) {
                    try {
                        fileReader.close();
                    } catch (IOException e322) {
                        e322.printStackTrace();
                    }
                }
                return "0";
            } catch (Throwable th3) {
                th = th3;
                if (fileReader != null) {
                    try {
                        fileReader.close();
                    } catch (IOException e3222) {
                        e3222.printStackTrace();
                    }
                }
                throw th;
            }
        }
    }

    public static int scaleParameter(int value, Parameters params) {
        if (params == null) {
            return 0;
        }
        float min = (float) params.getMinExposureCompensation();
        return Math.round(((float) value) * ((((float) params.getMaxExposureCompensation()) - min) / ((float) CameraConstants.MAX_BRIGHTNESS_STEP))) + ((int) min);
    }

    public static boolean isSupported(Parameters parameters, String flashMode) {
        List<String> supportedModes = parameters.getSupportedFlashModes();
        boolean supported = false;
        if (supportedModes != null) {
            for (String mode : supportedModes) {
                if (mode.equals(flashMode)) {
                    supported = true;
                }
            }
        }
        if (!supported) {
            CamLog.w(FaceDetector.TAG, String.format("Flash mode [%s] not supported.", new Object[]{flashMode}));
        }
        return supported;
    }

    public static boolean isLowLuminance(Parameters parameters, boolean isForCamera) {
        if (parameters == null) {
            return false;
        }
        String luminanceCondition = (isForCamera || ModelProperties.isRenesasISP()) ? parameters.get(CameraConstants.LUMINANCE_CONDITION) : parameters.get(CameraConstants.LUMINANCE_CONDITION_FOR_VIDEO);
        CamLog.d(FaceDetector.TAG, "Current luminanceCondition = " + luminanceCondition + ", camera mode ? : " + isForCamera);
        return CameraConstants.LUMINANCE_LOW.equals(luminanceCondition);
    }

    public static String breakTextToMultiLine(Paint textPaint, String message, int maxWidth) {
        if (message == null || "".equals(message)) {
            return "";
        }
        if (maxWidth == 0 || textPaint == null) {
            return message;
        }
        StringBuffer messageBuffer = new StringBuffer(message);
        StringBuffer breakStringBuffer = new StringBuffer();
        while (messageBuffer.length() > 0) {
            while (messageBuffer.length() > 0 && messageBuffer.charAt(0) == ' ') {
                messageBuffer.deleteCharAt(0);
            }
            String remainString = messageBuffer.toString();
            int breakCount = textPaint.breakText(remainString, true, (float) maxWidth, null);
            String breakString = remainString.substring(0, breakCount);
            if (breakCount < remainString.length()) {
                breakCount = breakString.lastIndexOf(32);
                if (breakCount < 0) {
                    breakCount = breakString.length();
                }
            }
            breakStringBuffer.append(remainString.substring(0, breakCount));
            messageBuffer.delete(0, breakCount);
            if (messageBuffer.length() > 0) {
                breakStringBuffer.append("\n");
            }
        }
        return breakStringBuffer.toString();
    }

    public static void showCheckTimeLog(String comment, boolean start) {
        if (start) {
            mStartTime = System.nanoTime();
            return;
        }
        Log.d(FaceDetector.TAG, "CHECK TIME : " + comment + " time is = " + (System.nanoTime() - mStartTime));
        mStartTime = System.nanoTime();
    }

    public static String chageDateFormatForNaming(String takeTime) {
        String takeTime_f = takeTime.substring(takeTime.length() - 8, takeTime.length()).replace(":", "");
        CamLog.d(FaceDetector.TAG, "chageDateFormatForNaming : " + takeTime_f);
        return takeTime_f;
    }

    public static void setContentDescriptionForAccessibility(Context context, ViewGroup viewGroup) {
        ArrayList<View> group = traverseViewGroup(viewGroup);
        CharSequence contentDescription = context.getString(R.string.dialog_alert_title) + "\n\n";
        for (int i = 0; i < group.size(); i++) {
            if (group.get(i) instanceof TextView) {
                contentDescription = contentDescription.toString() + ((TextView) group.get(i)).getText();
            } else if (group.get(i) instanceof Button) {
                contentDescription = contentDescription.toString() + ((Button) group.get(i)).getText();
            } else {
            }
            contentDescription = contentDescription.toString() + "\n\n";
        }
        viewGroup.setContentDescription(contentDescription);
    }

    public static ArrayList<View> traverseViewGroup(ViewGroup viewGroup) {
        ArrayList<View> returnList = new ArrayList();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                if (viewGroup.getChildAt(i).getVisibility() == 0) {
                    ArrayList<View> subList = traverseViewGroup((ViewGroup) viewGroup.getChildAt(i));
                    for (int j = 0; j < subList.size(); j++) {
                        returnList.add(subList.get(j));
                    }
                }
            } else if (viewGroup.getChildAt(i).getVisibility() == 0) {
                if (viewGroup.getChildAt(i) instanceof TextView) {
                    returnList.add(viewGroup.getChildAt(i));
                } else if (viewGroup.getChildAt(i) instanceof Button) {
                    returnList.add(viewGroup.getChildAt(i));
                }
            }
        }
        return returnList;
    }
}
