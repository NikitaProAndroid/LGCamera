package com.lge.camera.util;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ServiceManager;
import android.os.StatFs;
import android.os.SystemProperties;
import android.util.Log;
import com.lge.camera.R;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.MmsProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.fmccall.IFmcCallInterface;
import com.lge.fmccall.IFmcCallInterface.Stub;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.library.FaceDetector;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CheckStatusManager {
    public static final int CHECK_ENTER_BATTERY = 5;
    public static final int CHECK_ENTER_CALL = 1;
    public static final int CHECK_ENTER_CALL_CAMCORDER = 2;
    public static final int CHECK_ENTER_DATA_STORAGE = 13;
    public static final int CHECK_ENTER_DEV_POLOCY = 8;
    public static final int CHECK_ENTER_EXT_MEDIA_SCANNING = 6;
    public static final int CHECK_ENTER_FMC = 4;
    public static final int CHECK_ENTER_HDMI = 9;
    public static final int CHECK_ENTER_INT_MEDIA_SCANNING = 7;
    public static final int CHECK_ENTER_MMS_REC_SIZE = 11;
    public static final int CHECK_ENTER_OK = 0;
    public static final int CHECK_ENTER_SAFETY_CARE = 14;
    public static final int CHECK_ENTER_SDM = 10;
    public static final int CHECK_ENTER_TEMPERATURE = 12;
    public static final int CHECK_ENTER_VT_CALL = 3;
    public static final int CHECK_OUT_FINISH = 2;
    public static final int CHECK_OUT_IDLE = 0;
    public static final int CHECK_OUT_SECURE = 1;
    public static final String PROPERTY_TEMPERATURE = "/sys/devices/platform/msm_ssbi.0/pm8921-core/pm8xxx-adc/xo_therm";
    public static final double TEMPERATURE_ENTERING_DUAL_RECORDING;
    public static final double TEMPERATURE_ENTERING_STANDARD;
    public static final double TEMPERATURE_FLASH_RECORDING_STANDARD;
    public static final long TEMPERATURE_GUARANTEE_RECORDING_TIME;
    public static final float TEMPERATURE_LCD_CONTROL_RATIO;
    public static final long TEMPERATURE_LCD_CONTROL_SECOND;
    private static final String TEMPERATURE_PROP_BLOCK = "ro.lge.heat_block";
    private static final String TEMPERATURE_PROP_DUAL_RECORDING = "ro.lge.heat_dual_recording";
    private static final String TEMPERATURE_PROP_FINISH = "ro.lge.heat_finish";
    private static final String TEMPERATURE_PROP_FLASH_RECORDING_FINISH = "ro.lge.heat_flash_recorder";
    private static final String TEMPERATURE_PROP_GUARANTEE_TIME = "ro.lge.heat_guarantee_time";
    private static final String TEMPERATURE_PROP_LCD_CONTROL_PERCENT = "ro.lge.heat_lcd_percent";
    private static final String TEMPERATURE_PROP_LCD_CONTROL_SECOND = "ro.lge.heat_lcd_second";
    public static final double TEMPERATURE_STANDARD;
    private static int[] cameraOutStringId;
    private static boolean isEnterDuringCall;
    private static boolean isTelephonyStateCheckSkip;
    private static int mCheckEnterKind;
    private static int mCheckEnterOutSecure;
    private static boolean mEnterCheckComplete;

    static {
        mCheckEnterKind = CHECK_OUT_IDLE;
        mEnterCheckComplete = false;
        isTelephonyStateCheckSkip = false;
        mCheckEnterOutSecure = CHECK_OUT_IDLE;
        cameraOutStringId = new int[]{-1, R.string.sp_error_call_camera_NORMAL, R.string.error_video_recording_during_call, R.string.error_camera_during_video_call, R.string.sp_error_call_camera_NORMAL, R.string.sp_lowbattery, R.string.sp_media_scanning_NORMAL, R.string.sp_media_scanning_NORMAL, R.string.sp_block_camera_NORMAL, R.string.error_cannot_use_hdmi, R.string.sp_block_camera_NORMAL, R.string.sp_message_recording_limit_NORMAL, R.string.sp_high_temp_action_on_enter_NORMAL, R.string.sp_storage_full_popup_ics_title_NORMAL, R.string.not_available_during_remote_care};
        isEnterDuringCall = false;
        TEMPERATURE_STANDARD = setTemperatureCondition(TEMPERATURE_PROP_FINISH, 53.0d);
        TEMPERATURE_ENTERING_STANDARD = setTemperatureCondition(TEMPERATURE_PROP_BLOCK, 48.0d);
        TEMPERATURE_ENTERING_DUAL_RECORDING = setTemperatureCondition(TEMPERATURE_PROP_DUAL_RECORDING, 42.0d);
        TEMPERATURE_GUARANTEE_RECORDING_TIME = setTemperatureCondition(TEMPERATURE_PROP_GUARANTEE_TIME, (long) TEMPERATURE_LCD_CONTROL_SECOND);
        TEMPERATURE_FLASH_RECORDING_STANDARD = setTemperatureCondition(TEMPERATURE_PROP_FLASH_RECORDING_FINISH, 45.0d);
        TEMPERATURE_LCD_CONTROL_SECOND = setSecondForBacklightInRecording();
        TEMPERATURE_LCD_CONTROL_RATIO = setRatioForBacklightInRecording();
    }

    public static boolean isEnterCheckComplete() {
        return mEnterCheckComplete;
    }

    public static void setEnterCheckComplete(boolean complete) {
        mEnterCheckComplete = complete;
    }

    public static int getCheckEnterKind() {
        return mCheckEnterKind;
    }

    public static int getCheckEnterOutSecure() {
        return mCheckEnterOutSecure;
    }

    public static void setCheckEnterOutSecure(int status) {
        mCheckEnterOutSecure = status;
    }

    public static boolean isEnterDuringCall() {
        return isEnterDuringCall;
    }

    private static void setIsEnterDuringCall(boolean duringCall) {
        CamLog.d(FaceDetector.TAG, "Enter camera during call : " + duringCall);
        isEnterDuringCall = duringCall;
    }

    private static boolean checkCurrentBatteryStatus(Activity activity) {
        CamLog.d(FaceDetector.TAG, "check enter by battery status");
        IntentFilter intentFilter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("android.intent.action.BATTERY_LOW");
        Intent intent = activity.registerReceiver(null, intentFilter);
        if (intent != null && intent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
            int level = intent.getIntExtra("level", CHECK_OUT_IDLE);
            if (intent.getIntExtra("level", CHECK_OUT_IDLE) <= CHECK_ENTER_BATTERY) {
                CamLog.w(FaceDetector.TAG, "Battery level is low : " + level);
                mCheckEnterKind = CHECK_ENTER_BATTERY;
                return false;
            }
            CamLog.i(FaceDetector.TAG, "Current battery level is " + level);
        }
        return true;
    }

    public static boolean checkCurrentTemperature(Activity activity) {
        Log.d(FaceDetector.TAG, "check enter by Temperature");
        double mTemp = TEMPERATURE_FLASH_RECORDING_STANDARD;
        if (ProjectVariables.temperatureCheckMethod() == CHECK_OUT_SECURE) {
            mTemp = GetXo_thermal();
            CamLog.d(FaceDetector.TAG, "xo_termal Termperature : " + mTemp);
        } else if (ProjectVariables.temperatureCheckMethod() == CHECK_OUT_FINISH) {
            intent = activity.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            if (intent != null) {
                mTemp = ((double) intent.getIntExtra("temperature", CHECK_OUT_IDLE)) / 10.0d;
            }
        } else if (ProjectVariables.temperatureCheckMethod() == CHECK_ENTER_VT_CALL) {
            intent = activity.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            if (intent == null) {
                return true;
            }
            boolean camera_available = intent.getBooleanExtra("CameraAvailable", true);
            if (camera_available) {
                return true;
            }
            CamLog.d(FaceDetector.TAG, "Cannot exeute camera because it's too hot. Cool state : " + camera_available);
            mCheckEnterKind = CHECK_ENTER_TEMPERATURE;
            return false;
        }
        if (mTemp < TEMPERATURE_ENTERING_STANDARD) {
            return true;
        }
        CamLog.d(FaceDetector.TAG, "Cannot exeute camera because it's too hot. *-_-* : " + mTemp + " degrees.");
        mCheckEnterKind = CHECK_ENTER_TEMPERATURE;
        return false;
    }

    public static double GetXo_thermal() {
        IOException e;
        Throwable th;
        BufferedReader mReader = null;
        String mLine = null;
        double mTemp = TEMPERATURE_FLASH_RECORDING_STANDARD;
        try {
            BufferedReader mReader2 = new BufferedReader(new FileReader(ProjectVariables.getThermFile()));
            if (mReader2 != null) {
                try {
                    mLine = mReader2.readLine();
                } catch (IOException e2) {
                    e = e2;
                    mReader = mReader2;
                    try {
                        CamLog.e(FaceDetector.TAG, "IOException " + e);
                        if (mReader != null) {
                            try {
                                mReader.close();
                                if (mLine != null) {
                                    mTemp = Double.parseDouble(mLine.split("[: ]")[CHECK_OUT_SECURE]);
                                }
                            } catch (IOException e3) {
                                CamLog.e(FaceDetector.TAG, "IOException " + e3);
                            } catch (Exception e4) {
                                CamLog.d(FaceDetector.TAG, "Exception occured.");
                            }
                        }
                        return mTemp;
                    } catch (Throwable th2) {
                        th = th2;
                        if (mReader != null) {
                            try {
                                mReader.close();
                                if (mLine != null) {
                                    mTemp = Double.parseDouble(mLine.split("[: ]")[CHECK_OUT_SECURE]);
                                }
                            } catch (IOException e32) {
                                CamLog.e(FaceDetector.TAG, "IOException " + e32);
                            } catch (Exception e5) {
                                CamLog.d(FaceDetector.TAG, "Exception occured.");
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    mReader = mReader2;
                    if (mReader != null) {
                        mReader.close();
                        if (mLine != null) {
                            mTemp = Double.parseDouble(mLine.split("[: ]")[CHECK_OUT_SECURE]);
                        }
                    }
                    throw th;
                }
            }
            if (mReader2 != null) {
                try {
                    mReader2.close();
                    if (mLine != null) {
                        mTemp = Double.parseDouble(mLine.split("[: ]")[CHECK_OUT_SECURE]);
                    }
                } catch (IOException e322) {
                    CamLog.e(FaceDetector.TAG, "IOException " + e322);
                    mReader = mReader2;
                } catch (Exception e6) {
                    CamLog.d(FaceDetector.TAG, "Exception occured.");
                    mReader = mReader2;
                }
            }
            mReader = mReader2;
        } catch (IOException e7) {
            e322 = e7;
            CamLog.e(FaceDetector.TAG, "IOException " + e322);
            if (mReader != null) {
                mReader.close();
                if (mLine != null) {
                    mTemp = Double.parseDouble(mLine.split("[: ]")[CHECK_OUT_SECURE]);
                }
            }
            return mTemp;
        }
        return mTemp;
    }

    public static boolean checkDataStorageEnough() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long availBlocks = statFs.getAvailableBlocksLong();
        long blockSize = statFs.getBlockSizeLong();
        CamLog.d(FaceDetector.TAG, "DATA STORAGE = " + (availBlocks * blockSize));
        if (availBlocks * blockSize > 10) {
            return true;
        }
        CamLog.d(FaceDetector.TAG, "Data storage(/data/) is full!!!!");
        mCheckEnterKind = CHECK_ENTER_DATA_STORAGE;
        return false;
    }

    public static void setTelephonyStateCheckSkip(boolean callCheck) {
        isTelephonyStateCheckSkip = callCheck;
        CamLog.d(FaceDetector.TAG, "TelephonyStateCheck = " + isTelephonyStateCheckSkip);
    }

    private static boolean checkCallStatus(Activity activity) {
        CamLog.d(FaceDetector.TAG, "check enter by call status");
        if (isTelephonyStateCheckSkip) {
            return true;
        }
        CamLog.d(FaceDetector.TAG, "checkEnterApplication activity : " + activity.getClass().getName());
        if (!TelephonyUtil.phoneInCall(activity.getApplicationContext())) {
            setIsEnterDuringCall(false);
            return true;
        } else if (activity.getClass().getName().equals("com.lge.camera.Camcorder") || activity.getClass().getName().equals("com.lge.camera.CamcorderLoading")) {
            mCheckEnterKind = CHECK_OUT_FINISH;
            return false;
        } else if (ProjectVariables.isEnterCameraDuringCall() || isRCSeWorkingOn(activity)) {
            setIsEnterDuringCall(true);
            return true;
        } else if (ProjectVariables.isUPlusRMSCall() && isRmsConnected(activity)) {
            setIsEnterDuringCall(true);
            return true;
        } else {
            mCheckEnterKind = CHECK_OUT_SECURE;
            return false;
        }
    }

    private static boolean isRCSeWorkingOn(Activity activity) {
        CamLog.d(FaceDetector.TAG, "check enter by RCS status()");
        int nISWorking = CHECK_OUT_IDLE;
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = activity.getApplicationContext().getContentResolver();
            if (contentResolver != null) {
                cursor = contentResolver.query(Uri.parse("content://com.lge.ims.provisioning/workings"), null, null, null, null);
            }
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    nISWorking = cursor.getInt(cursor.getColumnIndex("rcs_e_is_working"));
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (SQLiteException e) {
            CamLog.e(FaceDetector.TAG, "cursor error ", e);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (nISWorking == CHECK_OUT_SECURE) {
            return true;
        }
        return false;
    }

    private static boolean isRmsConnected(Activity activity) {
        int connected = CHECK_OUT_IDLE;
        Cursor cursor = null;
        try {
            ContentResolver cr = activity.getContentResolver();
            if (cr != null) {
                cursor = cr.query(Uri.parse("content://com.lguplus.rms/service"), null, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        connected = cursor.getInt(cursor.getColumnIndex("connected"));
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "cursor error ", e);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (connected == CHECK_OUT_SECURE) {
            return true;
        }
        return false;
    }

    public static boolean checkVTCallStatus(Activity activity) {
        if (!TelephonyUtil.phoneInVTCall(activity.getApplicationContext())) {
            return true;
        }
        mCheckEnterKind = CHECK_ENTER_VT_CALL;
        return false;
    }

    private static boolean checkFMCStatus(Activity activity) {
        CamLog.d(FaceDetector.TAG, "check enter by FMC status");
        if (isTelephonyStateCheckSkip) {
            CamLog.d(FaceDetector.TAG, "Telephony state check skip - FMC");
            return true;
        } else if (!ModelProperties.isFMCmodel()) {
            return true;
        } else {
            int fmcState = CHECK_OUT_IDLE;
            try {
                IFmcCallInterface iFmcCallInterfac = Stub.asInterface(ServiceManager.getService("FmcCall"));
                if (iFmcCallInterfac == null) {
                    CamLog.i(FaceDetector.TAG, "Not use FMC Call service");
                    return true;
                }
                fmcState = iFmcCallInterfac.getFmcCallState();
                if (fmcState == 0) {
                    return true;
                }
                mCheckEnterKind = CHECK_ENTER_FMC;
                return false;
            } catch (Exception ex) {
                CamLog.e(FaceDetector.TAG, "RemoteException from getFmcCallInterface()", ex);
            }
        }
    }

    private static boolean checkDevicePolicy(Activity activity) {
        CamLog.d(FaceDetector.TAG, "check enter by Device Policy status");
        boolean allowCamera = true;
        DevicePolicyManager dpm = (DevicePolicyManager) activity.getApplicationContext().getSystemService("device_policy");
        if (dpm != null) {
            allowCamera = dpm.getCameraDisabled(null);
            CamLog.d(FaceDetector.TAG, "allowCamera = " + allowCamera);
            if (allowCamera) {
                mCheckEnterKind = CHECK_ENTER_DEV_POLOCY;
            }
        }
        return !allowCamera;
    }

    private static boolean getCameraStateInSDM(Activity activity) {
        if (ModelProperties.getCarrierCode() != CHECK_ENTER_EXT_MEDIA_SCANNING) {
            return true;
        }
        CamLog.d(FaceDetector.TAG, "check enter In SDM");
        int cameraEnableStatus = CHECK_OUT_SECURE;
        Cursor cursor = null;
        try {
            cursor = activity.getContentResolver().query(CameraConstants.SDM_CONTENT_URI, CameraConstants.CAMERA_PROJECTION, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                CamLog.w(FaceDetector.TAG, "*** cannot access to SDM server DB, cursor = " + cursor);
                if (cursor != null) {
                    cursor.close();
                }
                return true;
            }
            cameraEnableStatus = cursor.getInt(CHECK_OUT_IDLE);
            CamLog.w(FaceDetector.TAG, "*** cameraEnableStatus = " + cameraEnableStatus);
            if (cursor != null) {
                cursor.close();
            }
            if (cameraEnableStatus == CHECK_OUT_SECURE) {
                return true;
            }
            mCheckEnterKind = CHECK_ENTER_SDM;
            return false;
        } catch (SQLiteException e) {
            CamLog.e(FaceDetector.TAG, "Could not load photo from database", e);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static boolean checkHdmiStatus(Activity activity) {
        Throwable th;
        boolean enterCamera = true;
        int mHDMIState = CHECK_OUT_IDLE;
        char[] buffer = new char[Ola_ImageFormat.YUVPLANAR_LABEL];
        String HDMI_STATE_PATH = "/sys/class/switch/hdmi/state";
        if (ProjectVariables.isSupportHDMI_MHL()) {
            CamLog.d(FaceDetector.TAG, "It can support HDMI/MHL!!");
            return true;
        }
        FileReader file = null;
        try {
            FileReader file2 = new FileReader(HDMI_STATE_PATH);
            try {
                mHDMIState = Integer.valueOf(new String(buffer, CHECK_OUT_IDLE, file2.read(buffer, CHECK_OUT_IDLE, Ola_ImageFormat.YUVPLANAR_LABEL)).trim()).intValue();
                if (file2 != null) {
                    try {
                        file2.close();
                        file = file2;
                    } catch (IOException e) {
                        CamLog.w(FaceDetector.TAG, "IOException for HDMI_STATE_PATH");
                        file = file2;
                    }
                }
            } catch (FileNotFoundException e2) {
                file = file2;
                try {
                    CamLog.w(FaceDetector.TAG, "This kernel does not have dock station support");
                    if (file != null) {
                        try {
                            file.close();
                        } catch (IOException e3) {
                            CamLog.w(FaceDetector.TAG, "IOException for HDMI_STATE_PATH");
                        }
                    }
                    if (mHDMIState == CHECK_OUT_SECURE) {
                        enterCamera = false;
                        mCheckEnterKind = CHECK_ENTER_HDMI;
                    }
                    CamLog.i(FaceDetector.TAG, "checkHdmiStatus: is disconnected?" + enterCamera);
                    return enterCamera;
                } catch (Throwable th2) {
                    th = th2;
                    if (file != null) {
                        try {
                            file.close();
                        } catch (IOException e4) {
                            CamLog.w(FaceDetector.TAG, "IOException for HDMI_STATE_PATH");
                        }
                    }
                    throw th;
                }
            } catch (Exception e5) {
                file = file2;
                CamLog.w(FaceDetector.TAG, "Cannot check the HDMI status");
                if (file != null) {
                    try {
                        file.close();
                    } catch (IOException e6) {
                        CamLog.w(FaceDetector.TAG, "IOException for HDMI_STATE_PATH");
                    }
                }
                if (mHDMIState == CHECK_OUT_SECURE) {
                    enterCamera = false;
                    mCheckEnterKind = CHECK_ENTER_HDMI;
                }
                CamLog.i(FaceDetector.TAG, "checkHdmiStatus: is disconnected?" + enterCamera);
                return enterCamera;
            } catch (Throwable th3) {
                th = th3;
                file = file2;
                if (file != null) {
                    file.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e7) {
            CamLog.w(FaceDetector.TAG, "This kernel does not have dock station support");
            if (file != null) {
                file.close();
            }
            if (mHDMIState == CHECK_OUT_SECURE) {
                enterCamera = false;
                mCheckEnterKind = CHECK_ENTER_HDMI;
            }
            CamLog.i(FaceDetector.TAG, "checkHdmiStatus: is disconnected?" + enterCamera);
            return enterCamera;
        } catch (Exception e8) {
            CamLog.w(FaceDetector.TAG, "Cannot check the HDMI status");
            if (file != null) {
                file.close();
            }
            if (mHDMIState == CHECK_OUT_SECURE) {
                enterCamera = false;
                mCheckEnterKind = CHECK_ENTER_HDMI;
            }
            CamLog.i(FaceDetector.TAG, "checkHdmiStatus: is disconnected?" + enterCamera);
            return enterCamera;
        }
        if (mHDMIState == CHECK_OUT_SECURE) {
            enterCamera = false;
            mCheckEnterKind = CHECK_ENTER_HDMI;
        }
        CamLog.i(FaceDetector.TAG, "checkHdmiStatus: is disconnected?" + enterCamera);
        return enterCamera;
    }

    public static boolean isHDMIConnected() {
        FileNotFoundException e;
        Throwable th;
        Exception e2;
        int mHDMIState = CHECK_OUT_IDLE;
        char[] buffer = new char[Ola_ImageFormat.YUVPLANAR_LABEL];
        FileReader file = null;
        try {
            FileReader file2 = new FileReader("/sys/class/switch/hdmi/state");
            try {
                mHDMIState = Integer.valueOf(new String(buffer, CHECK_OUT_IDLE, file2.read(buffer, CHECK_OUT_IDLE, Ola_ImageFormat.YUVPLANAR_LABEL)).trim()).intValue();
                if (file2 != null) {
                    try {
                        file2.close();
                        file = file2;
                    } catch (IOException e3) {
                        CamLog.e(FaceDetector.TAG, "", e3);
                        file = file2;
                    }
                }
            } catch (FileNotFoundException e4) {
                e = e4;
                file = file2;
                try {
                    CamLog.w(FaceDetector.TAG, "This kernel does not have dock station support", e);
                    if (file != null) {
                        try {
                            file.close();
                        } catch (IOException e32) {
                            CamLog.e(FaceDetector.TAG, "", e32);
                        }
                    }
                    CamLog.d(FaceDetector.TAG, "mHDMIState = " + mHDMIState);
                    if (mHDMIState == CHECK_OUT_SECURE) {
                        return false;
                    }
                    return true;
                } catch (Throwable th2) {
                    th = th2;
                    if (file != null) {
                        try {
                            file.close();
                        } catch (IOException e322) {
                            CamLog.e(FaceDetector.TAG, "", e322);
                        }
                    }
                    throw th;
                }
            } catch (Exception e5) {
                e2 = e5;
                file = file2;
                CamLog.e(FaceDetector.TAG, "", e2);
                if (file != null) {
                    try {
                        file.close();
                    } catch (IOException e3222) {
                        CamLog.e(FaceDetector.TAG, "", e3222);
                    }
                }
                CamLog.d(FaceDetector.TAG, "mHDMIState = " + mHDMIState);
                if (mHDMIState == CHECK_OUT_SECURE) {
                    return true;
                }
                return false;
            } catch (Throwable th3) {
                th = th3;
                file = file2;
                if (file != null) {
                    file.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e6) {
            e = e6;
            CamLog.w(FaceDetector.TAG, "This kernel does not have dock station support", e);
            if (file != null) {
                file.close();
            }
            CamLog.d(FaceDetector.TAG, "mHDMIState = " + mHDMIState);
            if (mHDMIState == CHECK_OUT_SECURE) {
                return false;
            }
            return true;
        } catch (Exception e7) {
            e2 = e7;
            CamLog.e(FaceDetector.TAG, "", e2);
            if (file != null) {
                file.close();
            }
            CamLog.d(FaceDetector.TAG, "mHDMIState = " + mHDMIState);
            if (mHDMIState == CHECK_OUT_SECURE) {
                return true;
            }
            return false;
        }
        CamLog.d(FaceDetector.TAG, "mHDMIState = " + mHDMIState);
        if (mHDMIState == CHECK_OUT_SECURE) {
            return true;
        }
        return false;
    }

    private static boolean checkMinimumMMSRecordingSize(Activity activity) {
        if ("android.media.action.VIDEO_CAPTURE".equals(activity.getIntent().getAction())) {
            CamLog.d(FaceDetector.TAG, "check enter by minimum mms recording size");
            Bundle getExBundle = activity.getIntent().getExtras();
            if (getExBundle != null) {
                long mRequestedSizeLimit = getExBundle.getLong("android.intent.extra.sizeLimit", TEMPERATURE_LCD_CONTROL_SECOND);
                CamLog.d(FaceDetector.TAG, "requested size :" + mRequestedSizeLimit);
                if (mRequestedSizeLimit != TEMPERATURE_LCD_CONTROL_SECOND && mRequestedSizeLimit < MmsProperties.getMmsVideoMinimumSize(activity.getContentResolver())) {
                    mCheckEnterKind = CHECK_ENTER_MMS_REC_SIZE;
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkSafetyCareStatus(Activity activity) {
        boolean enterCamera = !AppControlUtil.getEnableSafetyCare(activity.getApplicationContext());
        if (!enterCamera) {
            mCheckEnterKind = CHECK_ENTER_SAFETY_CARE;
        }
        return enterCamera;
    }

    public static boolean checkEnterApplication(Activity activity, boolean bResume) {
        CamLog.d(FaceDetector.TAG, "checkEnterApplication : mEnterCheckComplete = " + mEnterCheckComplete);
        Common.IS_ENTER_CONDITION = true;
        if (mEnterCheckComplete) {
            return true;
        }
        if (bResume && Common.getScreenLock()) {
            setTelephonyStateCheckSkip(true);
        }
        if (checkVTCallStatus(activity) && checkCallStatus(activity) && checkFMCStatus(activity) && checkCurrentBatteryStatus(activity) && checkDevicePolicy(activity) && checkHdmiStatus(activity) && getCameraStateInSDM(activity) && checkMinimumMMSRecordingSize(activity) && checkCurrentTemperature(activity) && checkDataStorageEnough() && checkSafetyCareStatus(activity)) {
            setTelephonyStateCheckSkip(false);
            mEnterCheckComplete = true;
            return true;
        }
        setTelephonyStateCheckSkip(false);
        Common.IS_ENTER_CONDITION = false;
        return false;
    }

    public static void checkCameraOut(final Activity activity, Handler handler) {
        if (activity != null) {
            String sMsg = null;
            if (mCheckEnterKind >= 0 && mCheckEnterKind < cameraOutStringId.length && cameraOutStringId[mCheckEnterKind] >= 0) {
                sMsg = activity.getString(cameraOutStringId[mCheckEnterKind]);
            }
            if (sMsg != null) {
                Common.toast(activity.getApplicationContext(), sMsg);
                if (handler != null) {
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            activity.finish();
                        }
                    }, ProjectVariables.keepDuration);
                } else {
                    activity.finish();
                }
                mCheckEnterKind = CHECK_OUT_IDLE;
            }
        }
    }

    public static boolean checkVoiceShutterEnable(String shotMode) {
        if (CameraConstants.TYPE_SHOTMODE_CONTINUOUS.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_PANORAMA.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(shotMode)) {
            return false;
        }
        return true;
    }

    public static final double setTemperatureCondition(String prop, double defaultValue) {
        double result = defaultValue;
        boolean isNeedForcedSetting = false;
        String value = SystemProperties.get(prop);
        if (value == null) {
            CamLog.d(FaceDetector.TAG, "Temperature condition is null (" + prop + "). So use default value = " + result);
            isNeedForcedSetting = true;
        } else if ("".equals(value)) {
            CamLog.d(FaceDetector.TAG, "Temperature condition is empty (" + prop + "). So use default value = " + result);
            isNeedForcedSetting = true;
        } else {
            result = Double.parseDouble(value);
        }
        CamLog.d(FaceDetector.TAG, "isNeedForcedSetting : " + isNeedForcedSetting);
        if (isNeedForcedSetting && ModelProperties.getProjectCode() == CHECK_ENTER_VT_CALL) {
            if (TEMPERATURE_PROP_FINISH.equals(prop)) {
                result = 44.0d;
            } else if (TEMPERATURE_PROP_BLOCK.equals(prop)) {
                result = 42.5d;
            }
        }
        CamLog.d(FaceDetector.TAG, prop + " value =" + result);
        return result;
    }

    public static final long setTemperatureCondition(String prop, long defaultValue) {
        long result = defaultValue;
        String value = SystemProperties.get(prop);
        if (value == null) {
            CamLog.d(FaceDetector.TAG, "Temperature condition is null (" + prop + "). So use default value =" + result);
        } else if ("".equals(value)) {
            CamLog.d(FaceDetector.TAG, "Temperature condition is empty (" + prop + "). So use default value =" + result);
        } else {
            result = Long.parseLong(value);
        }
        CamLog.d(FaceDetector.TAG, "value =" + result);
        return result;
    }

    public static final long setSecondForBacklightInRecording() {
        CamLog.d(FaceDetector.TAG, "back setSecondForBacklight");
        long result = setTemperatureCondition(TEMPERATURE_PROP_LCD_CONTROL_SECOND, -1);
        if (result < TEMPERATURE_LCD_CONTROL_SECOND) {
            result = -1;
        }
        CamLog.d(FaceDetector.TAG, "back setSecondForBacklight end : " + result);
        return result;
    }

    public static final float setRatioForBacklightInRecording() {
        CamLog.d(FaceDetector.TAG, "back setRatioForBacklightInRecording");
        long result = setTemperatureCondition(TEMPERATURE_PROP_LCD_CONTROL_PERCENT, -1);
        if (result > 100 || result < 50) {
            return RotateView.DEFAULT_TEXT_SCALE_X;
        }
        CamLog.d(FaceDetector.TAG, "back setRatioForBacklightInRecording end : " + result);
        return ((float) result) * CameraConstants.AUDIOZOOM_BAR_RATIO;
    }

    public static final boolean useBackLightControlInRecording() {
        CamLog.d(FaceDetector.TAG, "useBackLightControlInRecording");
        return TEMPERATURE_LCD_CONTROL_SECOND >= TEMPERATURE_LCD_CONTROL_SECOND;
    }
}
