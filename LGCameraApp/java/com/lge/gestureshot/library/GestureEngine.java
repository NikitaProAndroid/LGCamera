package com.lge.gestureshot.library;

import android.os.Handler;
import android.os.Message;
import java.util.ArrayList;
import java.util.List;

public class GestureEngine {
    public static final int GESTURE_ENGINE_CAPTURE_EVENT = 2;
    private static final int GESTURE_ENGINE_CAPTURE_MSG = 3;
    public static final int GESTURE_ENGINE_CLEAN_EVENT = 3;
    private static final int GESTURE_ENGINE_CLEAN_MSG = 5;
    private static final int GESTURE_ENGINE_DRAW_MSG = 2;
    private static final int GESTURE_ENGINE_ERROR_MSG = 1;
    public static final int GESTURE_ENGINE_READY_EVENT = 1;
    public static final int GESTURE_ENGINE_STATUS_CREATED = 1;
    public static final int GESTURE_ENGINE_STATUS_INITIALIZED = 2;
    public static final int GESTURE_ENGINE_STATUS_NULL = 0;
    public static final int GESTURE_ENGINE_STATUS_STARTED = 3;
    public static final int GESTURE_ENGINE_STATUS_STOPPED = 4;
    private static List<GestureCallBack> sGestureCallBackList;
    private static Handler sHandler;

    public interface GestureCallBack {
        void onGestureEngineDrawCallback(HandInfo handInfo);

        void onGestureEngineErrorCallback(int i);

        void onGestureEngineEventCallback(int i);
    }

    public static native int GestureCreate();

    public static native int GesturePutPreviewFrame(byte[] bArr, int i, int i2, int i3, int i4);

    public static native int GestureRelease();

    public static native int GestureStart();

    public static native int GestureStop();

    public static native int GetEngineStatus();

    public static native int create(int i, int i2);

    public static native int find(byte[] bArr, int i, int i2, int i3, int i4);

    public static native HandInfo[] getHandInfo();

    public static native int getSensorDelay();

    public static native int motionInitialize(int i, int i2);

    public static native int motionRelease();

    public static native int motionUpdate(float[] fArr);

    public static native int release();

    public static native int resetHandInfo();

    static {
        sGestureCallBackList = new ArrayList();
        sHandler = new Handler() {
            public void handleMessage(Message msg) {
                HandInfo handInfo = msg.obj;
                switch (msg.what) {
                    case GestureEngine.GESTURE_ENGINE_STATUS_INITIALIZED /*2*/:
                        for (GestureCallBack gestureCallBack : GestureEngine.sGestureCallBackList) {
                            gestureCallBack.onGestureEngineDrawCallback(handInfo);
                        }
                    case GestureEngine.GESTURE_ENGINE_STATUS_STARTED /*3*/:
                        for (GestureCallBack gestureCallBack2 : GestureEngine.sGestureCallBackList) {
                            gestureCallBack2.onGestureEngineEventCallback(GestureEngine.GESTURE_ENGINE_STATUS_INITIALIZED);
                        }
                    case GestureEngine.GESTURE_ENGINE_CLEAN_MSG /*5*/:
                        for (GestureCallBack gestureCallBack22 : GestureEngine.sGestureCallBackList) {
                            gestureCallBack22.onGestureEngineEventCallback(GestureEngine.GESTURE_ENGINE_STATUS_STARTED);
                        }
                    default:
                }
            }
        };
        System.loadLibrary("gesture-jni");
    }

    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }

    public static void AddCallBack(GestureCallBack gestureCallBack) {
        sGestureCallBackList.add(gestureCallBack);
    }

    public static void ClearCallBack() {
        sGestureCallBackList.clear();
    }

    public static void GestureCallBack(HandInfo handInfo) {
        if (sHandler != null) {
            switch (handInfo.mEvent) {
                case GESTURE_ENGINE_STATUS_NULL /*0*/:
                    Message msg0 = sHandler.obtainMessage();
                    msg0.obj = handInfo;
                    msg0.what = GESTURE_ENGINE_STATUS_INITIALIZED;
                    sHandler.sendMessage(msg0);
                case GESTURE_ENGINE_STATUS_CREATED /*1*/:
                    Message msg1 = sHandler.obtainMessage();
                    msg1.obj = handInfo;
                    msg1.what = GESTURE_ENGINE_STATUS_STARTED;
                    sHandler.sendMessage(msg1);
                case GESTURE_ENGINE_STATUS_INITIALIZED /*2*/:
                    Message msg2 = sHandler.obtainMessage();
                    msg2.obj = handInfo;
                    msg2.what = GESTURE_ENGINE_CLEAN_MSG;
                    sHandler.sendMessage(msg2);
                default:
            }
        }
    }
}
