package com.lge.camera.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemProperties;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityManager;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import com.lge.view.ViewUtil;
import java.lang.reflect.Field;
import java.net.URISyntaxException;

public class AppControlUtil {
    public static boolean ENABLE_QUICK_CLIP_KEY = false;
    private static final String HOTKEY_QCLIP_PACKAGE = "com.lge.QuickClip";
    private static final String HOTKEY_QMEMO_PACKAGE = "com.lge.qmemoplus";
    private static final String HOTKEY_SHORT_PACKAGE = "hotkey_short_package";
    public static final int QCLIP_HOTKEY_FLAG_KEYDOWN_SHUTTER = 16;
    public static final int QCLIP_HOTKEY_FLAG_KEYUP_QMEMO = 512;
    public static final int QCLIP_HOTKEY_FLAG_KEYUP_ROTATE = 1024;
    public static final int QCLIP_HOTKEY_FLAG_NONE = 0;
    public static final int QCLIP_HOTKEY_FLAG_NOT_SUPPORT = 1;
    public static final int QCLIP_HOTKEY_FLAG_UNKNOWN = -1;
    public static final int QUICKCLIP_IGNORE_MODE = 1;
    public static final int QUICKCLIP_NORMAL_MODE = 0;
    public static final int QUICKCLIP_RUNTYPE_CLEANVIEW = 1;
    public static final int QUICKCLIP_RUNTYPE_NONE = 0;
    public static final int QUICKCLIP_RUNTYPE_RETURN_TO_QUICKMEMO = 4;
    public static final int QUICKCLIP_RUNTYPE_START = 2;
    public static final int QUICKCLIP_RUNTYPE_STOP = 3;
    private static final String ROTATE_SWITCH = "rotate_switch";
    private static final int ROTATION_DIRECTION_LEFT = 1;
    private static final int ROTATION_DIRECTION_RIGHT = 2;
    public static final int TOP_CLASS = 0;
    public static final int TOP_PACKAGE = 1;
    private static int mOldNavigationBarRotation;
    private static int mQClipHotkeyFlag;

    static {
        ENABLE_QUICK_CLIP_KEY = false;
        mQClipHotkeyFlag = QCLIP_HOTKEY_FLAG_UNKNOWN;
        mOldNavigationBarRotation = TOP_CLASS;
    }

    public static void setQuickClipScreenCaptureLimit(Activity activity) {
        CamLog.d(FaceDetector.TAG, "setQuickClipScreenCaptureLimit");
        if (!ModelProperties.isSamsungModel() && !ModelProperties.isReferenceModel()) {
            String shortPackageName = getHotkeyPackageName(activity.getApplicationContext());
            if (!HOTKEY_QMEMO_PACKAGE.equals(shortPackageName) && !HOTKEY_QCLIP_PACKAGE.equals(shortPackageName) && getQClipHotkeyFlag(activity.getApplicationContext()) != QCLIP_HOTKEY_FLAG_KEYUP_QMEMO) {
                try {
                    LayoutParams params = activity.getWindow().getAttributes();
                    Field field = Class.forName(LayoutParams.class.getName()).getField("privateFlags");
                    field.setAccessible(true);
                    if (field.isAccessible()) {
                        field.setInt(params, field.getInt(params) | 536870912);
                    }
                    activity.getWindow().setAttributes(params);
                } catch (Exception e) {
                }
                ENABLE_QUICK_CLIP_KEY = true;
                CamLog.v(FaceDetector.TAG, "setQuickClipScreenCaptureLimit, ENABLE_QUICK_CLIP_KEY(true)");
            }
        }
    }

    public static int getQuickClipRunType(Activity activity) {
        int nRet = TOP_CLASS;
        if (activity == null) {
            return TOP_CLASS;
        }
        String topPackageName = getTopActivity(activity, TOP_PACKAGE);
        if ("com.lge.camera".equals(topPackageName)) {
            if (ENABLE_QUICK_CLIP_KEY) {
                if (getIsTopOverlayForQSlide2(activity) || (getIsTopOverlay(activity) && "com.lge.QuickClip.QuickClipActivity".equals(getOverlayActivityName(activity)))) {
                    nRet = QUICKCLIP_RUNTYPE_RETURN_TO_QUICKMEMO;
                } else {
                    String topClassName = getTopActivity(activity, TOP_CLASS);
                    if ("com.lge.camera.CameraApp".equals(topClassName) || "com.lge.camera.Camcorder".equals(topClassName) || "com.lge.camera.CameraAppLauncher".equals(topClassName) || "com.lge.camera.SecureCameraApp".equals(topClassName) || "com.lge.camera.QuickWindowCameraApp".equals(topClassName)) {
                        nRet = TOP_PACKAGE;
                    } else {
                        nRet = ROTATION_DIRECTION_RIGHT;
                    }
                    if (topClassName != null) {
                        CamLog.v(FaceDetector.TAG, "topClassName = " + topClassName);
                    }
                }
            }
        } else if (topPackageName != null) {
            CamLog.v(FaceDetector.TAG, "topPackageName = " + topPackageName);
        }
        CamLog.v(FaceDetector.TAG, "ENABLE_QUICK_CLIP_KEY = " + ENABLE_QUICK_CLIP_KEY + " QUICKCLIP_RUNTYPE = " + nRet);
        return nRet;
    }

    private static boolean getIsTopOverlayForQSlide2(Activity activity) {
        boolean retval = false;
        for (RunningServiceInfo current : ((ActivityManager) activity.getSystemService("activity")).getRunningServices(100)) {
            if (current.service.getClassName().contains("FloatingWindowService") && current.service.getPackageName().contains(HOTKEY_QCLIP_PACKAGE)) {
                retval = true;
                break;
            }
        }
        CamLog.v(FaceDetector.TAG, "getIsTopOverlayForQSlide2 = " + retval);
        return retval;
    }

    private static String getTopActivity(Context context, int mode) {
        ActivityManager am = (ActivityManager) context.getSystemService("activity");
        if (mode == 0) {
            String className = "";
            if (am.getRunningTasks(TOP_PACKAGE) != null) {
                className = ((RunningTaskInfo) am.getRunningTasks(TOP_PACKAGE).get(TOP_CLASS)).topActivity.getClassName();
            }
            CamLog.v(FaceDetector.TAG, "@getTopAppName getClassName() : " + className);
            return className;
        }
        String packageName = "";
        if (am.getRunningTasks(TOP_PACKAGE) != null) {
            packageName = ((RunningTaskInfo) am.getRunningTasks(TOP_PACKAGE).get(TOP_CLASS)).topActivity.getPackageName();
        }
        CamLog.v(FaceDetector.TAG, "@getTopAppName getPackageName() : " + packageName);
        return packageName;
    }

    private static boolean getIsTopOverlay(Activity activity) {
        ActivityManager am = (ActivityManager) activity.getSystemService("activity");
        boolean retval = false;
        try {
            retval = ((Boolean) Class.forName(am.getClass().getName()).getMethod("isThereTopOverlay", new Class[TOP_CLASS]).invoke(am, new Object[TOP_CLASS])).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        CamLog.v(FaceDetector.TAG, "getIsTopOverlay() retval = " + retval);
        return retval;
    }

    private static String getOverlayActivityName(Activity activity) {
        String retval = null;
        ActivityManager am = (ActivityManager) activity.getSystemService("activity");
        try {
            retval = (String) Class.forName(am.getClass().getName()).getMethod("getOverlayActivityName", new Class[TOP_CLASS]).invoke(am, new Object[TOP_CLASS]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (retval != null) {
            CamLog.v(FaceDetector.TAG, "getOverlayActivityName() retval = " + retval);
        }
        return retval;
    }

    public static int getQClipHotkeyFlag(Context context) {
        if (mQClipHotkeyFlag == QCLIP_HOTKEY_FLAG_UNKNOWN) {
            setQClipHotkeyFlag(context);
        }
        return mQClipHotkeyFlag;
    }

    public static int setQClipHotkeyFlag(Context context) {
        int nFlag;
        String shortPackageName = getHotkeyPackageName(context);
        if (shortPackageName == null) {
            nFlag = TOP_PACKAGE;
        } else if (ROTATE_SWITCH.equals(shortPackageName)) {
            nFlag = QCLIP_HOTKEY_FLAG_KEYUP_ROTATE;
        } else if (ModelProperties.isSoftKeyNavigationBarModel()) {
            nFlag = QCLIP_HOTKEY_FLAG_KEYUP_QMEMO;
        } else {
            nFlag = QCLIP_HOTKEY_FLAG_KEYDOWN_SHUTTER;
        }
        mQClipHotkeyFlag = nFlag;
        CamLog.v(FaceDetector.TAG, "setQClipHotkeyFlag,  mQClipHotkeyFlag = " + mQClipHotkeyFlag);
        return mQClipHotkeyFlag;
    }

    public static void resetQClipHotkeyFlag() {
        mQClipHotkeyFlag = QCLIP_HOTKEY_FLAG_UNKNOWN;
    }

    public static void resetQuickClipScreenCaptureLimit(Activity activity) {
        CamLog.d(FaceDetector.TAG, "resetQuickClipScreenCaptureLimit");
        if (!ModelProperties.isSamsungModel() && !ModelProperties.isReferenceModel()) {
            String shortPackageName = getHotkeyPackageName(activity.getApplicationContext());
            if (!HOTKEY_QMEMO_PACKAGE.equals(shortPackageName) && !HOTKEY_QCLIP_PACKAGE.equals(shortPackageName) && getQClipHotkeyFlag(activity.getApplicationContext()) != QCLIP_HOTKEY_FLAG_KEYUP_QMEMO) {
                try {
                    LayoutParams params = activity.getWindow().getAttributes();
                    Field field = Class.forName(LayoutParams.class.getName()).getField("privateFlags");
                    field.setAccessible(true);
                    if (field.isAccessible()) {
                        field.setInt(params, field.getInt(params) & -536870913);
                    }
                    activity.getWindow().setAttributes(params);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e2) {
                    e2.printStackTrace();
                } catch (IllegalArgumentException e3) {
                    e3.printStackTrace();
                } catch (NoSuchFieldException e4) {
                    e4.printStackTrace();
                }
                ENABLE_QUICK_CLIP_KEY = false;
                CamLog.v(FaceDetector.TAG, "resetQuickClipScreenCaptureLimit, ENABLE_QUICK_CLIP_KEY(false)");
            }
        }
    }

    public static void setFmRadioOff(Context context) {
        Intent i = new Intent("com.lge.fmradio.command.fmradioservice");
        i.putExtra("request", "power_off");
        context.sendBroadcast(i);
    }

    public static void BlockAlarmInRecording(Activity activity, int appMode) {
        if (activity != null && appMode == TOP_PACKAGE) {
            CamLog.d(FaceDetector.TAG, "BlockAlarmInRecording");
            Intent recording_start = new Intent();
            recording_start.putExtra("packageName", "com.lge.camera");
            recording_start.setAction("voice_video_record_playing");
            activity.sendBroadcast(recording_start);
        }
    }

    public static void UnblockAlarmInRecording(Activity activity) {
        if (activity != null) {
            CamLog.d(FaceDetector.TAG, "UnblockAlarmInRecording");
            Intent recording_finish = new Intent();
            recording_finish.putExtra("packageName", "com.lge.camera");
            recording_finish.setAction("voice_video_record_finish");
            activity.sendBroadcast(recording_finish);
        }
    }

    public static void StopVoiceRec(Activity activity, int appMode) {
        if (activity != null && appMode == TOP_PACKAGE) {
            CamLog.d(FaceDetector.TAG, "StopVoiceRec");
            Intent StopVoiceRec = new Intent();
            StopVoiceRec.setAction("Stop_Voice_Rec");
            activity.sendBroadcast(StopVoiceRec);
        }
    }

    public static boolean getEnableSafetyCare(Context context) {
        boolean remoteCareEnabled = Boolean.parseBoolean(System.getString(context.getContentResolver(), CameraConstants.SETTINGS_VALUE_REMOVE_CARE_ENABLED));
        CamLog.d(FaceDetector.TAG, "getEnableSafetyCare=" + remoteCareEnabled);
        return remoteCareEnabled;
    }

    public static boolean isSecureCameraIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return false;
        }
        if (CameraConstants.INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE.equals(intent.getAction())) {
            return true;
        }
        if (CameraConstants.ACTION_IMAGE_CAPTURE_SECURE.equals(intent.getAction())) {
            return true;
        }
        if (intent.getBooleanExtra(CameraConstants.SECURE_CAMERA, false)) {
            return true;
        }
        if (CameraConstants.INTENT_ACTION_CAMERA_START_FROM_COVER.equals(intent.getAction())) {
            return true;
        }
        return intent.getBooleanExtra(CameraConstants.SECURE_CAMERA_EXTRA, false);
    }

    public static void setQuickWindowCameraFromIntent(Intent intent) {
        boolean mFromQuickWindowCase = false;
        if (!(intent == null || intent.getAction() == null)) {
            mFromQuickWindowCase = CameraConstants.LAUNCH_FROM_SMARTCOVER.equals(intent.getAction());
        }
        if (Common.isSmartCoverClosed() && (mFromQuickWindowCase || isSecureCameraIntent(intent))) {
            Common.setQuickWindowCameraMode(true);
        } else {
            Common.setQuickWindowCameraMode(false);
        }
    }

    public static void checkCurrentCoverStatus(Activity activity) {
        IntentFilter intentFilter = new IntentFilter(CameraConstants.ACTION_ACCESSORY_EVENT);
        intentFilter.addAction(CameraConstants.INTENT_ACTION_CAMERA_FINISH);
        Intent intent = activity.registerReceiver(null, intentFilter);
        if (intent != null && intent != null && intent.getAction().equals(CameraConstants.ACTION_ACCESSORY_EVENT)) {
            int coverState = intent.getIntExtra(CameraConstants.EXTRA_ACCESSORY_STATE, TOP_CLASS);
            CamLog.d(FaceDetector.TAG, "quick window case state:" + coverState);
            if (coverState == 0) {
                CamLog.d(FaceDetector.TAG, "cover EXTRA_ACCESSORY_COVER_OPENED!!");
                Common.setSmartCoverClosed(false);
                return;
            }
            CamLog.d(FaceDetector.TAG, "cover EXTRA_ACCESSORY_COVER_CLOSED!!");
            Common.setSmartCoverClosed(true);
        }
    }

    public static void setTranslucentStatusBar(Activity activity) {
        if (ModelProperties.isSoftKeyNavigationBarModel() && !ModelProperties.isSamsungModel() && !ModelProperties.isReferenceModel()) {
            LayoutParams winParams = activity.getWindow().getAttributes();
            if (winParams != null) {
                winParams.flags |= 67108864;
                activity.getWindow().setAttributes(winParams);
            }
        }
    }

    public static void setTransparentNavigationBar(Activity activity, boolean isTransparent) {
        if (ModelProperties.isSoftKeyNavigationBarModel() && !ModelProperties.isSamsungModel() && !ModelProperties.isReferenceModel() && isTransparent) {
            activity.getWindow().getDecorView().setSystemUiVisibility(1792);
            activity.getWindow().setNavigationBarColor(TOP_CLASS);
        }
    }

    public static void setEnableRotateNaviataionBar(Activity activity, boolean enable) {
        if (ModelProperties.isSoftKeyNavigationBarModel() && !ModelProperties.isSamsungModel() && !ModelProperties.isReferenceModel() && activity != null) {
            View v = activity.getWindow().getDecorView();
            if (v == null) {
                return;
            }
            if (enable) {
                v.setSystemUiVisibility(v.getSystemUiVisibility() | 32);
            } else {
                v.setSystemUiVisibility(v.getSystemUiVisibility() & -33);
            }
        }
    }

    public static void rotateNavigationBarIcon(Activity activity, int degree, int duration) {
        CamLog.d(FaceDetector.TAG, "rotate " + mOldNavigationBarRotation + " -> " + degree);
        int direction = mOldNavigationBarRotation < degree ? TOP_PACKAGE : ROTATION_DIRECTION_RIGHT;
        if (mOldNavigationBarRotation == Tag.IMAGE_DESCRIPTION && degree == 0) {
            direction = TOP_PACKAGE;
        } else if (mOldNavigationBarRotation == 0 && degree == Tag.IMAGE_DESCRIPTION) {
            direction = ROTATION_DIRECTION_RIGHT;
        } else if (mOldNavigationBarRotation == MediaProviderUtils.ROTATION_180 && degree == 0) {
            direction = TOP_PACKAGE;
            duration *= ROTATION_DIRECTION_RIGHT;
        } else if (mOldNavigationBarRotation == 0 && degree == MediaProviderUtils.ROTATION_180) {
            duration *= ROTATION_DIRECTION_RIGHT;
        } else if (mOldNavigationBarRotation == 90 && degree == Tag.IMAGE_DESCRIPTION) {
            duration *= ROTATION_DIRECTION_RIGHT;
        } else if (mOldNavigationBarRotation == Tag.IMAGE_DESCRIPTION && degree == 90) {
            direction = TOP_PACKAGE;
            duration *= ROTATION_DIRECTION_RIGHT;
        }
        setNavigationBarRotation(activity, Util.convertDegreeToSurfaceRotation(degree), direction, duration);
        mOldNavigationBarRotation = degree;
    }

    private static void setNavigationBarRotation(Activity activity, int rotation, int direction, int duration) {
        if (ModelProperties.isSoftKeyNavigationBarModel() && !ModelProperties.isSamsungModel() && !ModelProperties.isReferenceModel()) {
            CamLog.d(FaceDetector.TAG, "rotate to " + rotation + " with direction " + direction + " for duration " + duration);
            Intent intent = new Intent("com.lge.android.intent.action.NAVIGATION_KEY_ROTATION");
            intent.putExtra("com.lge.intent.extra.ROTATION", rotation);
            intent.putExtra("com.lge.intent.extra.ROTATION_DIRECTION", direction);
            intent.putExtra("com.lge.intent.extra.ROTATION_DURATION", duration);
            activity.getApplicationContext().sendBroadcast(intent);
        }
    }

    public static void disableNavigationButton(Activity activity) {
        ViewUtil.setLGSystemUiVisibility(activity.getWindow().getDecorView(), 393216);
    }

    public static boolean isGuestMode() {
        if ("kids".equals(SystemProperties.get("service.plushome.currenthome"))) {
            return true;
        }
        return false;
    }

    public static boolean checkGuestModeAndAppDisabled(ContentResolver contentResolver, boolean checkGuestMode, int appName) {
        if (checkGuestMode && !isGuestMode()) {
            return false;
        }
        String checkAppName;
        switch (appName) {
            case TOP_PACKAGE /*1*/:
                checkAppName = "com.android.gallery3d";
                break;
            default:
                checkAppName = "";
                break;
        }
        StringBuilder append = new StringBuilder().append("content://").append("com.lge.launcher2.settings").append("/").append("favorites").append("?");
        Cursor c = contentResolver.query(Uri.parse(append.append("notify").append("=true").toString()), null, "container=-103", null, null);
        try {
            int intentIndex = c.getColumnIndexOrThrow("intent");
            Intent intent = null;
            while (c.moveToNext()) {
                try {
                    intent = Intent.parseUri(c.getString(intentIndex), TOP_CLASS);
                    if (checkAppName.equals(intent.getComponent().getPackageName())) {
                        c.close();
                        return false;
                    }
                    CamLog.d(FaceDetector.TAG, "intent " + intent);
                } catch (URISyntaxException e) {
                }
            }
            c.close();
            return true;
        } catch (Exception e2) {
            CamLog.w(FaceDetector.TAG, "Desktop items loading interrupted:", e2);
        } catch (Throwable th) {
            c.close();
        }
    }

    public static void setFullScreen(Activity activity) {
        boolean z;
        AccessibilityManager am = (AccessibilityManager) activity.getSystemService("accessibility");
        if (Secure.getIntForUser(activity.getContentResolver(), "accessibility_touch_control_areas_service_enable", TOP_CLASS, -3) != 0) {
            z = true;
        } else {
            z = false;
        }
        Boolean mTouchControlServiceRunning = Boolean.valueOf(z);
        if (am.isTouchExplorationEnabled() || mTouchControlServiceRunning.booleanValue()) {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(dm);
            View v = ((ViewGroup) activity.getWindow().getDecorView()).getChildAt(TOP_CLASS);
            ViewGroup.LayoutParams lp = v.getLayoutParams();
            lp.width = dm.widthPixels;
            lp.height = dm.heightPixels;
            v.setLayoutParams(lp);
        }
    }

    public static String getHotkeyPackageName(Context context) {
        String shortPackageName = System.getString(context.getContentResolver(), HOTKEY_SHORT_PACKAGE);
        CamLog.v(FaceDetector.TAG, "shortPackageName = " + shortPackageName);
        return shortPackageName;
    }

    public static boolean isInLockTask(Activity activity) {
        return ((ActivityManager) activity.getSystemService("activity")).isInLockTaskMode();
    }
}
