package com.lge.hardware;

import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.lge.gles.GLESConfig;
import com.lge.util.ProxyUtil;
import com.lge.wfds.session.AspSessionProtoOpcode;
import java.lang.ref.WeakReference;
import java.util.List;

public class LGCamera {
    private static final int CAMERA_META_DATA_FLASH_INDICATOR = 8;
    private static final int CAMERA_META_DATA_HDR_INDICATOR = 4;
    private static final int CAMERA_META_DATA_LG_MANUAL_MODE_INDICATOR = 18;
    private static final int CAMERA_MSG_META_DATA = 8192;
    private static final int CAMERA_MSG_OBT_DATA = 20480;
    private static final int CAMERA_MSG_PROXY_DATA = 32768;
    private static final int CAMERA_MSG_STATS_DATA = 4096;
    private static final String TAG = "LGCamera";
    private static Object sSplitAreaMethod;
    private Camera mCamera;
    private CameraDataCallback mCameraDataCallback;
    private int mCameraId;
    private int mEnabledMetaData;
    private EventHandler mEventHandler;
    private CameraMetaDataCallback mFlashMetaDataCallback;
    private CameraMetaDataCallback mHdrMetaDataCallback;
    private CameraMetaDataCallback mLGManualModeMetaDataCallback;
    private Object mMetaDataCallbackLock;
    private ProxyDataListener mProxyDataListener;
    private boolean mProxyDataRunning;

    public interface CameraDataCallback {
        void onCameraData(int[] iArr, Camera camera);
    }

    public interface CameraMetaDataCallback {
        void onCameraMetaData(byte[] bArr, Camera camera);
    }

    private class EventHandler extends Handler {
        private LGCamera mLGCamera;

        public EventHandler(LGCamera c, Looper looper) {
            super(looper);
            this.mLGCamera = c;
        }

        public void handleMessage(Message msg) {
            int i;
            int ptr;
            int ptr2;
            byte[] byteData;
            switch (msg.what) {
                case LGCamera.CAMERA_MSG_STATS_DATA /*4096*/:
                    int[] statsdata = new int[257];
                    for (i = 0; i < 257; i++) {
                        statsdata[i] = LGCamera.byteToInt((byte[]) msg.obj, i * LGCamera.CAMERA_META_DATA_HDR_INDICATOR);
                    }
                    if (LGCamera.this.mCameraDataCallback != null) {
                        LGCamera.this.mCameraDataCallback.onCameraData(statsdata, LGCamera.this.mCamera);
                    }
                case LGCamera.CAMERA_MSG_META_DATA /*8192*/:
                    byte[] buf = (byte[]) msg.obj;
                    if (LGCamera.this.mEnabledMetaData > 0 && buf != null) {
                        if (LGCamera.this.mCamera != null) {
                            CameraMetaDataCallback cb1;
                            CameraMetaDataCallback cb2;
                            CameraMetaDataCallback cb3;
                            synchronized (LGCamera.this.mMetaDataCallbackLock) {
                                cb1 = LGCamera.this.mHdrMetaDataCallback;
                                cb2 = LGCamera.this.mFlashMetaDataCallback;
                                cb3 = LGCamera.this.mLGManualModeMetaDataCallback;
                            }
                            if ((buf[0] & LGCamera.CAMERA_META_DATA_HDR_INDICATOR) != 0) {
                                if (!((LGCamera.this.mEnabledMetaData & LGCamera.CAMERA_META_DATA_HDR_INDICATOR) == 0 || cb1 == null)) {
                                    cb1.onCameraMetaData(new byte[]{buf[LGCamera.CAMERA_META_DATA_HDR_INDICATOR]}, LGCamera.this.mCamera);
                                }
                            }
                            if ((buf[0] & LGCamera.CAMERA_META_DATA_FLASH_INDICATOR) != 0) {
                                if (!((LGCamera.this.mEnabledMetaData & LGCamera.CAMERA_META_DATA_FLASH_INDICATOR) == 0 || cb2 == null)) {
                                    cb2.onCameraMetaData(new byte[]{buf[LGCamera.CAMERA_META_DATA_FLASH_INDICATOR]}, LGCamera.this.mCamera);
                                }
                            }
                            if ((buf[0] & LGCamera.CAMERA_META_DATA_LG_MANUAL_MODE_INDICATOR) != 0) {
                                if ((LGCamera.this.mEnabledMetaData & LGCamera.CAMERA_META_DATA_LG_MANUAL_MODE_INDICATOR) != 0 && cb3 != null) {
                                    byte[] lg_manual_data = new byte[16];
                                    int length = buf.length;
                                    if (r0 >= 28) {
                                        i = 0;
                                        ptr = 12;
                                        while (i < 16) {
                                            ptr2 = ptr + 1;
                                            lg_manual_data[i] = buf[ptr];
                                            i++;
                                            ptr = ptr2;
                                        }
                                    } else {
                                        for (i = 0; i < 16; i++) {
                                            lg_manual_data[i] = (byte) 0;
                                        }
                                        Log.e(LGCamera.TAG, "error! Manual mode was set but data was not matched.");
                                    }
                                    ptr = 0 + 1;
                                    ptr2 = ptr + 1;
                                    ptr = ptr2 + 1;
                                    ptr2 = ptr + 1;
                                    int ia = (((lg_manual_data[0] & AspSessionProtoOpcode.NACK) | ((lg_manual_data[ptr] & AspSessionProtoOpcode.NACK) << LGCamera.CAMERA_META_DATA_FLASH_INDICATOR)) | ((lg_manual_data[ptr2] & AspSessionProtoOpcode.NACK) << 16)) | ((lg_manual_data[ptr] & AspSessionProtoOpcode.NACK) << 24);
                                    ptr = ptr2 + 1;
                                    ptr2 = ptr + 1;
                                    ptr = ptr2 + 1;
                                    ptr2 = ptr + 1;
                                    int ib = (((lg_manual_data[ptr2] & AspSessionProtoOpcode.NACK) | ((lg_manual_data[ptr] & AspSessionProtoOpcode.NACK) << LGCamera.CAMERA_META_DATA_FLASH_INDICATOR)) | ((lg_manual_data[ptr2] & AspSessionProtoOpcode.NACK) << 16)) | ((lg_manual_data[ptr] & AspSessionProtoOpcode.NACK) << 24);
                                    ptr = ptr2 + 1;
                                    ptr2 = ptr + 1;
                                    ptr = ptr2 + 1;
                                    ptr2 = ptr + 1;
                                    int ic = (((lg_manual_data[ptr2] & AspSessionProtoOpcode.NACK) | ((lg_manual_data[ptr] & AspSessionProtoOpcode.NACK) << LGCamera.CAMERA_META_DATA_FLASH_INDICATOR)) | ((lg_manual_data[ptr2] & AspSessionProtoOpcode.NACK) << 16)) | ((lg_manual_data[ptr] & AspSessionProtoOpcode.NACK) << 24);
                                    ptr = ptr2 + 1;
                                    ptr2 = ptr + 1;
                                    ptr = ptr2 + 1;
                                    ptr2 = ptr + 1;
                                    int id = (((lg_manual_data[ptr2] & AspSessionProtoOpcode.NACK) | ((lg_manual_data[ptr] & AspSessionProtoOpcode.NACK) << LGCamera.CAMERA_META_DATA_FLASH_INDICATOR)) | ((lg_manual_data[ptr2] & AspSessionProtoOpcode.NACK) << 16)) | ((lg_manual_data[ptr] & AspSessionProtoOpcode.NACK) << 24);
                                    float a = Float.intBitsToFloat(ia);
                                    float b = Float.intBitsToFloat(ib);
                                    float c = Float.intBitsToFloat(ic);
                                    float d = Float.intBitsToFloat(id);
                                    String str = LGCamera.TAG;
                                    r28 = new Object[6];
                                    r28[LGCamera.CAMERA_META_DATA_HDR_INDICATOR] = Integer.valueOf(lg_manual_data.length);
                                    r28[5] = Integer.valueOf(buf.length);
                                    Log.e(str, String.format("dennis: %f %f %f %f length=%d buf.length=%d", r28));
                                    cb3.onCameraMetaData(lg_manual_data, LGCamera.this.mCamera);
                                }
                            }
                        }
                    }
                case LGCamera.CAMERA_MSG_OBT_DATA /*20480*/:
                    if (LGCamera.this.mCameraDataCallback != null) {
                        short[] obt_data = new short[5];
                        byteData = (byte[]) msg.obj;
                        int i2 = byteData[1] & AspSessionProtoOpcode.NACK;
                        obt_data[0] = (short) ((r0 << LGCamera.CAMERA_META_DATA_FLASH_INDICATOR) | (byteData[0] & AspSessionProtoOpcode.NACK));
                        i2 = byteData[3] & AspSessionProtoOpcode.NACK;
                        obt_data[1] = (short) ((r0 << LGCamera.CAMERA_META_DATA_FLASH_INDICATOR) | (byteData[2] & AspSessionProtoOpcode.NACK));
                        i2 = byteData[5] & AspSessionProtoOpcode.NACK;
                        obt_data[2] = (short) ((r0 << LGCamera.CAMERA_META_DATA_FLASH_INDICATOR) | (byteData[LGCamera.CAMERA_META_DATA_HDR_INDICATOR] & AspSessionProtoOpcode.NACK));
                        i2 = byteData[7] & AspSessionProtoOpcode.NACK;
                        obt_data[3] = (short) ((r0 << LGCamera.CAMERA_META_DATA_FLASH_INDICATOR) | (byteData[6] & AspSessionProtoOpcode.NACK));
                        i2 = byteData[9] & AspSessionProtoOpcode.NACK;
                        obt_data[LGCamera.CAMERA_META_DATA_HDR_INDICATOR] = (short) ((r0 << LGCamera.CAMERA_META_DATA_FLASH_INDICATOR) | (byteData[LGCamera.CAMERA_META_DATA_FLASH_INDICATOR] & AspSessionProtoOpcode.NACK));
                        int[] obt_data_i = new int[5];
                        for (i = 0; i < 5; i++) {
                            obt_data_i[i] = obt_data[i];
                        }
                        LGCamera.this.mCameraDataCallback.onCameraData(obt_data_i, LGCamera.this.mCamera);
                    }
                case LGCamera.CAMERA_MSG_PROXY_DATA /*32768*/:
                    if (LGCamera.this.mProxyDataListener != null) {
                        ProxyData data = new ProxyData();
                        byteData = (byte[]) msg.obj;
                        if (byteData != null) {
                            ptr = 0 + 1;
                            ptr2 = ptr + 1;
                            ptr = ptr2 + 1;
                            ptr2 = ptr + 1;
                            data.val = (((byteData[0] & AspSessionProtoOpcode.NACK) | ((byteData[ptr] & AspSessionProtoOpcode.NACK) << LGCamera.CAMERA_META_DATA_FLASH_INDICATOR)) | ((byteData[ptr2] & AspSessionProtoOpcode.NACK) << 16)) | ((byteData[ptr] & AspSessionProtoOpcode.NACK) << 24);
                            ptr = ptr2 + 1;
                            ptr2 = ptr + 1;
                            ptr = ptr2 + 1;
                            ptr2 = ptr + 1;
                            data.conv = (((byteData[ptr2] & AspSessionProtoOpcode.NACK) | ((byteData[ptr] & AspSessionProtoOpcode.NACK) << LGCamera.CAMERA_META_DATA_FLASH_INDICATOR)) | ((byteData[ptr2] & AspSessionProtoOpcode.NACK) << 16)) | ((byteData[ptr] & AspSessionProtoOpcode.NACK) << 24);
                            ptr = ptr2 + 1;
                            ptr2 = ptr + 1;
                            ptr = ptr2 + 1;
                            ptr2 = ptr + 1;
                            data.sig = (((byteData[ptr2] & AspSessionProtoOpcode.NACK) | ((byteData[ptr] & AspSessionProtoOpcode.NACK) << LGCamera.CAMERA_META_DATA_FLASH_INDICATOR)) | ((byteData[ptr2] & AspSessionProtoOpcode.NACK) << 16)) | ((byteData[ptr] & AspSessionProtoOpcode.NACK) << 24);
                            ptr = ptr2 + 1;
                            ptr2 = ptr + 1;
                            ptr = ptr2 + 1;
                            ptr2 = ptr + 1;
                            data.amb = (((byteData[ptr2] & AspSessionProtoOpcode.NACK) | ((byteData[ptr] & AspSessionProtoOpcode.NACK) << LGCamera.CAMERA_META_DATA_FLASH_INDICATOR)) | ((byteData[ptr2] & AspSessionProtoOpcode.NACK) << 16)) | ((byteData[ptr] & AspSessionProtoOpcode.NACK) << 24);
                            ptr = ptr2 + 1;
                            ptr2 = ptr + 1;
                            ptr = ptr2 + 1;
                            ptr2 = ptr + 1;
                            data.raw = (((byteData[ptr2] & AspSessionProtoOpcode.NACK) | ((byteData[ptr] & AspSessionProtoOpcode.NACK) << LGCamera.CAMERA_META_DATA_FLASH_INDICATOR)) | ((byteData[ptr2] & AspSessionProtoOpcode.NACK) << 16)) | ((byteData[ptr] & AspSessionProtoOpcode.NACK) << 24);
                        } else {
                            data.val = -1;
                        }
                        LGCamera.this.mProxyDataListener.onDataListen(data, LGCamera.this.mCamera);
                    }
                default:
                    Log.e(LGCamera.TAG, "Unknown message type " + msg.what);
            }
        }
    }

    public class LGParameters {
        private static final String KEY_BACKLIGHT_CONDITION = "backlight-condition";
        private static final String KEY_BEAUTY = "beautyshot";
        private static final String KEY_FLASH_MODE = "flash-mode";
        private static final String KEY_FLASH_STATUS = "flash-status";
        private static final String KEY_FOCUS_MODE_OBJECT_TRACKING = "object-tracking";
        private static final String KEY_HDR_MODE = "hdr-mode";
        private static final String KEY_LG_MULTI_WINDOW_FOCUS_AREA = "multi-window-focus-area";
        private static final String KEY_LUMINANCE_CONDITION = "luminance-condition";
        private static final String KEY_PANORAMA = "panorama-shot";
        private static final String KEY_QC_SCENE_DETECT = "scene-detect";
        private static final String KEY_SUPERZOOM = "superzoom";
        private static final String KEY_ZOOM = "zoom";
        public static final String SCENE_MODE_AUTO = "auto";
        public static final String SCENE_MODE_NIGHT = "night";
        String backlightCondition;
        String luminanceCondition;
        String mCurrentFlash;
        String mFlashStatus;
        String mHDRstatus;
        String mIsBeauty;
        boolean mIsCurrentFlash;
        boolean mIsFlashAuto;
        boolean mIsFlashOff;
        boolean mIsFlashOn;
        boolean mIsHDRAuto;
        boolean mIsHDROff;
        boolean mIsHDROn;
        boolean mIsHighBackLight;
        boolean mIsLuminanceEis;
        boolean mIsLuminanceHigh;
        boolean mIsSuperZoomEnabled;
        private Parameters mParameters;
        int mSuperZoomStatus;
        String mshotMode;

        public LGParameters() {
            if (LGCamera.this.mCamera == null) {
                Log.e(LGCamera.TAG, "Camera hardware is not opened!. open camera first.");
                return;
            }
            this.mParameters = LGCamera.this.mCamera.getParameters();
            if (this.mParameters == null) {
                Log.e(LGCamera.TAG, "didn't get native parameters.");
            }
        }

        public Parameters getParameters() {
            return this.mParameters;
        }

        public void setParameters(Parameters param) {
            this.mParameters = param;
            LGCamera.this.mCamera.setParameters(this.mParameters);
        }

        public void setSceneDetectMode(String value) {
            this.mParameters.set(KEY_QC_SCENE_DETECT, value);
        }

        public boolean getParamStatus(String Param, String Status) {
            if (Param == null || Status == null || !Status.equals(Param)) {
                return false;
            }
            return true;
        }

        private void setDefaultParam() {
            this.mParameters.set(KEY_SUPERZOOM, "off");
            this.mParameters.set(KEY_HDR_MODE, "0");
            this.mParameters.setSceneMode(SCENE_MODE_AUTO);
            this.mParameters.set(KEY_PANORAMA, "0");
        }

        private void setHDROnParam() {
            this.mParameters.set(KEY_HDR_MODE, GLESConfig.MAJOR_NUMBER);
            this.mParameters.set(KEY_SUPERZOOM, "off");
            this.mParameters.setSceneMode(SCENE_MODE_AUTO);
        }

        private void checkSuperZoomStatus() {
            if (this.mIsSuperZoomEnabled) {
                this.mParameters.set(KEY_SUPERZOOM, "on");
                this.mParameters.setSceneMode(SCENE_MODE_AUTO);
                Log.i(LGCamera.TAG, "[LGSF] lumi_low : SZ_on Scene_Auto");
                return;
            }
            this.mParameters.set(KEY_SUPERZOOM, "off");
            this.mParameters.setSceneMode(SCENE_MODE_NIGHT);
            Log.i(LGCamera.TAG, "[LGSF] lumi_low : SZ_off Scene_Night");
        }

        private void checkSceneStatus() {
            if (this.mIsLuminanceEis) {
                checkSuperZoomStatus();
                Log.i(LGCamera.TAG, "[LGSF] EIS Scene_Night");
                return;
            }
            this.mParameters.setSceneMode(SCENE_MODE_AUTO);
            Log.i(LGCamera.TAG, "[LGSF] Scene_Auto");
        }

        private void checkBacklightStatus() {
            if (this.mIsHighBackLight) {
                Log.i(LGCamera.TAG, "[LGSF] HDR_auto BL_high SZ_off");
                setHDROnParam();
                return;
            }
            Log.i(LGCamera.TAG, "[LGSF] BL_low HDR_off");
            this.mParameters.set(KEY_HDR_MODE, "0");
            checkSceneStatus();
        }

        private void checkHDRStatus() {
            if (this.mIsHDRAuto) {
                checkBacklightStatus();
            } else if (this.mIsHDROn) {
                Log.i(LGCamera.TAG, "[LGSF] HDR_on SZ_off");
                setHDROnParam();
            } else {
                Log.i(LGCamera.TAG, "[LGSF] HDR_off");
                this.mParameters.set(KEY_HDR_MODE, "0");
                checkSceneStatus();
            }
        }

        private void checkLuminanceStatus() {
            if (this.mIsLuminanceHigh || this.mIsLuminanceEis) {
                checkHDRStatus();
                return;
            }
            this.mParameters.set(KEY_HDR_MODE, "0");
            checkSuperZoomStatus();
        }

        private void checkFlashStatus() {
            if (this.mIsFlashOn || (this.mIsFlashAuto && this.mIsCurrentFlash)) {
                Log.i(LGCamera.TAG, "[LGSF] flash_on");
                setDefaultParam();
                return;
            }
            Log.i(LGCamera.TAG, "[LGSF] flash_off");
            checkLuminanceStatus();
        }

        public Parameters setNightandHDRorAuto(Parameters Param, String modeType, boolean recording_flag) {
            this.mParameters = Param;
            this.mshotMode = modeType;
            String beautyShot = "mode_beauty";
            if (recording_flag) {
                setDefaultParam();
                LGCamera.this.mCamera.setParameters(this.mParameters);
                return this.mParameters;
            }
            boolean z;
            if (this.mParameters.get(KEY_ZOOM) == null) {
                this.mSuperZoomStatus = 0;
            } else {
                this.mSuperZoomStatus = this.mParameters.getInt(KEY_ZOOM);
            }
            if (this.mSuperZoomStatus > 54) {
                z = true;
            } else {
                z = false;
            }
            this.mIsSuperZoomEnabled = z;
            this.luminanceCondition = this.mParameters.get(KEY_LUMINANCE_CONDITION);
            this.mIsLuminanceHigh = getParamStatus(this.luminanceCondition, "high");
            this.mIsLuminanceEis = getParamStatus(this.luminanceCondition, "eis");
            this.backlightCondition = this.mParameters.get(KEY_BACKLIGHT_CONDITION);
            this.mIsHighBackLight = getParamStatus(this.backlightCondition, "high");
            this.mFlashStatus = this.mParameters.get(KEY_FLASH_MODE);
            this.mIsFlashOff = getParamStatus(this.mFlashStatus, "off");
            if (!this.mIsHighBackLight && this.mIsLuminanceHigh && !this.mIsSuperZoomEnabled && this.mIsFlashOff && this.mshotMode.equals("mode_normal")) {
                Log.e(LGCamera.TAG, "[LGSF] return1");
                LGCamera.this.mCamera.setParameters(this.mParameters);
                return this.mParameters;
            }
            this.mHDRstatus = this.mParameters.get(KEY_HDR_MODE);
            this.mCurrentFlash = this.mParameters.get(KEY_FLASH_STATUS);
            if (this.mshotMode.length() > beautyShot.length()) {
                this.mIsBeauty = this.mshotMode.substring(0, beautyShot.length());
            }
            this.mIsFlashOn = getParamStatus(this.mFlashStatus, "on");
            this.mIsFlashAuto = getParamStatus(this.mFlashStatus, SCENE_MODE_AUTO);
            this.mIsHDROff = getParamStatus(this.mHDRstatus, "0");
            this.mIsHDROn = getParamStatus(this.mHDRstatus, GLESConfig.MAJOR_NUMBER);
            this.mIsHDRAuto = getParamStatus(this.mHDRstatus, "2");
            this.mIsCurrentFlash = getParamStatus(this.mCurrentFlash, "on");
            if (!this.mIsHighBackLight && this.mIsLuminanceHigh && !this.mIsSuperZoomEnabled && this.mIsFlashOff && this.mshotMode.equals("mode_normal")) {
                Log.e(LGCamera.TAG, "[LGSF] return2");
            } else {
                setLGParameters();
            }
            LGCamera.this.mCamera.setParameters(this.mParameters);
            return this.mParameters;
        }

        private void setLGParameters() {
            if (this.mshotMode.equals("mode_normal")) {
                if (this.mshotMode.equals("mode_normal")) {
                    checkFlashStatus();
                }
            } else if (this.mshotMode.equals("mode_burst")) {
                setDefaultParam();
            } else if (this.mIsBeauty.equals("mode_beauty")) {
                if (this.mshotMode.equals("mode_beauty=0")) {
                    this.mParameters.set(KEY_BEAUTY, "off");
                    Log.i(LGCamera.TAG, "[LGSF]Beautyshot : level is 0 and normal mode");
                } else {
                    this.mParameters.set(KEY_BEAUTY, "on");
                    Log.i(LGCamera.TAG, "[LGSF]Beautyshot : level is higher than 0 and  not normal mode");
                }
                checkLuminanceStatus();
            } else if (this.mshotMode.equals("mode_panorama")) {
                this.mParameters.set(KEY_PANORAMA, GLESConfig.MAJOR_NUMBER);
                Log.i(LGCamera.TAG, "[LGSF]Panorama shot mode");
            }
        }

        public Parameters setSuperZoom(Parameters Param) {
            boolean z = false;
            this.mParameters = Param;
            if (this.mParameters.get(KEY_ZOOM) == null) {
                this.mSuperZoomStatus = 0;
            } else {
                this.mSuperZoomStatus = this.mParameters.getInt(KEY_ZOOM);
            }
            if (this.mSuperZoomStatus > 54) {
                z = true;
            }
            this.mIsSuperZoomEnabled = z;
            if (this.mIsSuperZoomEnabled) {
                this.mParameters.set(KEY_SUPERZOOM, "on");
            } else {
                this.mParameters.set(KEY_SUPERZOOM, "off");
            }
            LGCamera.this.mCamera.setParameters(this.mParameters);
            return this.mParameters;
        }

        public List<Area> getMultiWindowFocusAreas() throws UnsupportedOperationException {
            String area = this.mParameters.get(KEY_LG_MULTI_WINDOW_FOCUS_AREA);
            try {
                return (List) ProxyUtil.invokeMethod(LGCamera.sSplitAreaMethod, this.mParameters, area);
            } catch (Exception e) {
                Log.e(LGCamera.TAG, e.toString());
                return null;
            }
        }

        public void setObjectTracking(String value) {
            this.mParameters.set(KEY_FOCUS_MODE_OBJECT_TRACKING, value);
        }
    }

    public static class ProxyData {
        public int amb;
        public int conv;
        public int raw;
        public int sig;
        public int val;
    }

    public interface ProxyDataListener {
        void onDataListen(ProxyData proxyData, Camera camera);
    }

    private final native void _enableProxyDataListen(Camera camera, boolean z);

    private final native void native_cancelPicture(Camera camera);

    private final native void native_change_listener(Object obj, Camera camera);

    private final native void native_sendObjectTrackingCmd(Camera camera);

    private final native void native_setISPDataCallbackMode(Camera camera, boolean z);

    private final native void native_setMetadataCb(Camera camera, boolean z);

    private final native void native_setOBTDataCallbackMode(Camera camera, boolean z);

    static {
        System.loadLibrary("hook_jni");
        sSplitAreaMethod = ProxyUtil.loadMethod(Parameters.class, "splitArea", String.class);
    }

    public LGCamera(int cameraId) {
        this.mProxyDataRunning = false;
        this.mMetaDataCallbackLock = new Object();
        this.mCamera = Camera.open(cameraId);
        cameraInit(cameraId, this.mCamera);
    }

    public LGCamera(int cameraId, int halVersion) {
        this.mProxyDataRunning = false;
        this.mMetaDataCallbackLock = new Object();
        this.mCamera = Camera.openLegacy(cameraId, halVersion);
        cameraInit(cameraId, this.mCamera);
    }

    private void cameraInit(int cameraId, Camera camera) {
        native_change_listener(new WeakReference(this), camera);
        this.mCameraDataCallback = null;
        this.mProxyDataListener = null;
        this.mHdrMetaDataCallback = null;
        this.mFlashMetaDataCallback = null;
        this.mLGManualModeMetaDataCallback = null;
        this.mEnabledMetaData = 0;
        this.mCameraId = cameraId;
        Looper looper = Looper.myLooper();
        if (looper != null) {
            this.mEventHandler = new EventHandler(this, looper);
            return;
        }
        looper = Looper.getMainLooper();
        if (looper != null) {
            this.mEventHandler = new EventHandler(this, looper);
        } else {
            this.mEventHandler = null;
        }
    }

    public Camera getCamera() {
        return this.mCamera;
    }

    protected void finalize() {
        if (this.mCamera != null) {
            this.mCamera.release();
            this.mCamera = null;
        }
    }

    private static int byteToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < CAMERA_META_DATA_HDR_INDICATOR; i++) {
            value += (b[(3 - i) + offset] & AspSessionProtoOpcode.NACK) << ((3 - i) * CAMERA_META_DATA_FLASH_INDICATOR);
        }
        return value;
    }

    public final void setMetadataCb(CameraMetaDataCallback cb) {
        synchronized (this.mMetaDataCallbackLock) {
            this.mHdrMetaDataCallback = cb;
        }
        if (cb == null) {
            try {
                this.mEnabledMetaData &= -5;
                if (this.mEnabledMetaData == 0) {
                    native_setMetadataCb(this.mCamera, false);
                    return;
                }
                return;
            } catch (RuntimeException e) {
                Log.e(TAG, "setMetadataCb failed");
                this.mEnabledMetaData &= -5;
                return;
            }
        }
        this.mEnabledMetaData |= CAMERA_META_DATA_HDR_INDICATOR;
        native_setMetadataCb(this.mCamera, true);
    }

    public final void setLGManualModedataCb(CameraMetaDataCallback cb) {
        synchronized (this.mMetaDataCallbackLock) {
            this.mLGManualModeMetaDataCallback = cb;
        }
        if (cb == null) {
            try {
                this.mEnabledMetaData &= CAMERA_META_DATA_LG_MANUAL_MODE_INDICATOR;
                if (this.mEnabledMetaData == 0) {
                    native_setMetadataCb(this.mCamera, false);
                    return;
                }
                return;
            } catch (RuntimeException e) {
                Log.e(TAG, "setLGManualModedataCb failed");
                this.mEnabledMetaData &= CAMERA_META_DATA_LG_MANUAL_MODE_INDICATOR;
                return;
            }
        }
        this.mEnabledMetaData |= CAMERA_META_DATA_LG_MANUAL_MODE_INDICATOR;
        native_setMetadataCb(this.mCamera, true);
    }

    public final void setFlashdataCb(CameraMetaDataCallback cb) {
        synchronized (this.mMetaDataCallbackLock) {
            this.mFlashMetaDataCallback = cb;
        }
        if (cb == null) {
            try {
                this.mEnabledMetaData &= -9;
                if (this.mEnabledMetaData == 0) {
                    native_setMetadataCb(this.mCamera, false);
                    return;
                }
                return;
            } catch (RuntimeException e) {
                Log.e(TAG, "setFlashdataCb failed");
                this.mEnabledMetaData &= -9;
                return;
            }
        }
        this.mEnabledMetaData |= CAMERA_META_DATA_FLASH_INDICATOR;
        native_setMetadataCb(this.mCamera, true);
    }

    public final void runObjectTracking() {
        native_sendObjectTrackingCmd(this.mCamera);
    }

    public final void setISPDataCallbackMode(CameraDataCallback cb) {
        this.mCameraDataCallback = cb;
        native_setISPDataCallbackMode(this.mCamera, cb != null);
    }

    public final void setOBTDataCallbackMode(CameraDataCallback cb) {
        this.mCameraDataCallback = cb;
        native_setOBTDataCallbackMode(this.mCamera, cb != null);
    }

    public final void cancelPicture() {
        native_cancelPicture(this.mCamera);
    }

    public LGParameters getLGParameters() {
        return new LGParameters();
    }

    private static void postEventFromNative(Object camera_ref, int what, int arg1, int arg2, Object obj) {
        LGCamera c = (LGCamera) ((WeakReference) camera_ref).get();
        if (c != null && c.mEventHandler != null) {
            c.mEventHandler.sendMessage(c.mEventHandler.obtainMessage(what, arg1, arg2, obj));
        }
    }

    public final void setProxyDataListener(ProxyDataListener listener) {
        this.mProxyDataListener = listener;
        if (listener != null && !this.mProxyDataRunning) {
            this.mProxyDataRunning = true;
            _enableProxyDataListen(this.mCamera, true);
        } else if (listener == null && this.mProxyDataRunning) {
            this.mProxyDataRunning = false;
            _enableProxyDataListen(this.mCamera, false);
        }
    }
}
