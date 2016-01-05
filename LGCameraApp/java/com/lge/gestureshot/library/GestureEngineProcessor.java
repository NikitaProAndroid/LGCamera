package com.lge.gestureshot.library;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.lge.camera.util.CamLog;

public class GestureEngineProcessor {
    private static volatile int sEngineStatus = 0;
    private static final int sGESTURE_ACTIVE_SIGN = 1;
    private static final int sGESTURE_DETECTABLE_HAND_CNT = 1;
    private static final int sGESTURE_ENGINE_CAPTURE_MSG = 3;
    private static final int sGESTURE_ENGINE_CLEAN_MSG = 4;
    private static final int sGESTURE_ENGINE_DRAW_MSG = 2;
    private static final int sGESTURE_ENGINE_ERROR_MSG = 1;
    public static final int sGESTURE_ENGINE_STATUS_CREATED = 1;
    public static final int sGESTURE_ENGINE_STATUS_IDLE = 0;
    public static final int sGESTURE_ENGINE_STATUS_STARTED = 3;
    public static final int sGESTURE_ENGINE_STATUS_STOPPED = 4;
    public static final int sGESTURE_EVENT_CAPTURE = 3;
    public static final int sGESTURE_EVENT_CLEAN = 4;
    public static final int sGESTURE_EVENT_DRAW_GUIDEBOX = 2;
    public static final int sGESTURE_EVENT_ERROR = 1;
    public static final String sTAG = "CameraApp";
    private boolean bFrameUpdated;
    private Callback mCallback;
    private Thread mEngineThread;
    private long mEventCheckTime;
    private HandInfo mHandInfo;
    private Handler mHandler;
    private byte[] mInputbuf;
    private long mPreEventCheckTime;
    private boolean mStopThreadSign;
    private int nHeight;
    private int nImageType;
    private int nOrientation;
    private int nWidth;

    public interface Callback {
        void onGestureEngineErrorCallback(int i);

        void onGestureEngineResultCallback(int i, HandInfo handInfo);
    }

    static {
        sEngineStatus = sGESTURE_ENGINE_STATUS_IDLE;
    }

    public GestureEngineProcessor(Callback callback) {
        this.mHandInfo = new HandInfo();
        this.mCallback = null;
        this.mEngineThread = null;
        this.mStopThreadSign = false;
        this.mInputbuf = null;
        this.nOrientation = sGESTURE_ENGINE_STATUS_IDLE;
        this.nWidth = sGESTURE_ENGINE_STATUS_IDLE;
        this.nHeight = sGESTURE_ENGINE_STATUS_IDLE;
        this.nImageType = sGESTURE_ENGINE_STATUS_IDLE;
        this.bFrameUpdated = false;
        this.mEventCheckTime = 0;
        this.mPreEventCheckTime = 0;
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GestureEngineProcessor.sGESTURE_EVENT_DRAW_GUIDEBOX /*2*/:
                        if (GestureEngineProcessor.this.mCallback != null) {
                            GestureEngineProcessor.this.mCallback.onGestureEngineResultCallback(GestureEngineProcessor.sGESTURE_EVENT_DRAW_GUIDEBOX, GestureEngineProcessor.this.mHandInfo);
                        }
                        if (GestureEngineProcessor.this.mHandInfo == null) {
                        }
                    case GestureEngineProcessor.sGESTURE_EVENT_CAPTURE /*3*/:
                        GestureEngineProcessor.this.mCallback.onGestureEngineResultCallback(GestureEngineProcessor.sGESTURE_EVENT_CAPTURE, null);
                    case GestureEngineProcessor.sGESTURE_EVENT_CLEAN /*4*/:
                        GestureEngineProcessor.this.mCallback.onGestureEngineResultCallback(GestureEngineProcessor.sGESTURE_EVENT_CLEAN, null);
                    default:
                }
            }
        };
        this.mCallback = callback;
    }

    public int create() {
        int bSuccessed = sGESTURE_ENGINE_STATUS_IDLE;
        if (sEngineStatus == 0) {
            bSuccessed = GestureEngine.create(sGESTURE_EVENT_ERROR, sGESTURE_EVENT_ERROR);
            sEngineStatus = sGESTURE_EVENT_ERROR;
        }
        CamLog.d(sTAG, "Gesture Engine create()!!   " + bSuccessed);
        return bSuccessed;
    }

    public void start() {
        if (sEngineStatus != sGESTURE_EVENT_CAPTURE) {
            try {
                if (this.mEngineThread != null) {
                    this.mStopThreadSign = true;
                    this.mEngineThread.join();
                    this.mEngineThread = null;
                }
            } catch (InterruptedException e) {
                CamLog.e(sTAG, "InterruptedException in Gesture Engine Thread join()");
            }
            initialize();
            if (this.mEngineThread != null) {
                this.mStopThreadSign = false;
                this.mEngineThread.start();
                sEngineStatus = sGESTURE_EVENT_CAPTURE;
                CamLog.d(sTAG, "Gesture Engine Thread start()!!");
            }
        }
    }

    public void putPreviewFrame(byte[] tbuf, int orientation, int width, int height, int imagetype) {
        this.bFrameUpdated = true;
        this.mInputbuf = tbuf;
        this.nOrientation = orientation;
        this.nWidth = width;
        this.nHeight = height;
        this.nImageType = imagetype;
    }

    public void stop() {
        if (sEngineStatus == sGESTURE_EVENT_CAPTURE) {
            if (!this.mStopThreadSign) {
                this.mStopThreadSign = true;
                try {
                    if (this.mEngineThread != null) {
                        this.mEngineThread.join();
                        this.mEngineThread = null;
                        sEngineStatus = sGESTURE_EVENT_CLEAN;
                    }
                } catch (InterruptedException e) {
                    CamLog.e(sTAG, "InterruptedException in Gesture Engine Thread stop()");
                }
            }
            CamLog.d(sTAG, "Gesture Engine is stopped");
            GestureEngine.resetHandInfo();
        }
    }

    public int release() {
        int bSuccessed = sGESTURE_ENGINE_STATUS_IDLE;
        if (sEngineStatus == sGESTURE_EVENT_CAPTURE) {
            stop();
        }
        this.mHandInfo = null;
        this.mInputbuf = null;
        if (sEngineStatus != 0) {
            bSuccessed = GestureEngine.release();
            sEngineStatus = sGESTURE_ENGINE_STATUS_IDLE;
        }
        CamLog.d(sTAG, "Gesture Engine is released   " + bSuccessed);
        return bSuccessed;
    }

    public int getGestureEngineState() {
        return sEngineStatus;
    }

    public HandInfo getHandInfo() {
        return this.mHandInfo;
    }

    private boolean initialize() {
        this.mStopThreadSign = false;
        this.mEngineThread = new Thread(new Runnable() {
            public void run() {
                while (!GestureEngineProcessor.this.mStopThreadSign) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        GestureEngineProcessor.this.stop();
                        CamLog.d(GestureEngineProcessor.sTAG, "InterruptedException GestureEngine Stopped");
                        e.printStackTrace();
                    }
                    if (GestureEngineProcessor.this.mInputbuf != null && GestureEngineProcessor.this.bFrameUpdated) {
                        GestureEngine.find(GestureEngineProcessor.this.mInputbuf, GestureEngineProcessor.this.nOrientation, GestureEngineProcessor.this.nWidth, GestureEngineProcessor.this.nHeight, GestureEngineProcessor.this.nImageType);
                        HandInfo[] tempInfo = GestureEngine.getHandInfo();
                        if (tempInfo == null) {
                            continue;
                        } else if (tempInfo.length == 0) {
                            GestureEngineProcessor.this.mPreEventCheckTime = SystemClock.uptimeMillis();
                            if (GestureEngineProcessor.this.mPreEventCheckTime - GestureEngineProcessor.this.mEventCheckTime > 10 && GestureEngineProcessor.this.mHandler != null) {
                                GestureEngineProcessor.this.mHandler.sendEmptyMessage(GestureEngineProcessor.sGESTURE_EVENT_CLEAN);
                                GestureEngineProcessor.this.mEventCheckTime = GestureEngineProcessor.this.mPreEventCheckTime;
                            }
                            GestureEngineProcessor.this.bFrameUpdated = false;
                        } else if (GestureEngineProcessor.this.mHandInfo == null) {
                            continue;
                        } else if (GestureEngineProcessor.this.mHandInfo.compareHandInfo(tempInfo[GestureEngineProcessor.sGESTURE_ENGINE_STATUS_IDLE])) {
                            GestureEngineProcessor.this.bFrameUpdated = false;
                        } else {
                            GestureEngineProcessor.this.mHandInfo.setHandInfo(tempInfo[GestureEngineProcessor.sGESTURE_ENGINE_STATUS_IDLE]);
                            if (GestureEngineProcessor.this.mHandler != null) {
                                GestureEngineProcessor.this.mHandler.sendEmptyMessage(GestureEngineProcessor.sGESTURE_EVENT_DRAW_GUIDEBOX);
                                GestureEngineProcessor.this.mEventCheckTime = SystemClock.uptimeMillis();
                                if (GestureEngineProcessor.this.mHandInfo.mEvent == GestureEngineProcessor.sGESTURE_EVENT_ERROR) {
                                    GestureEngineProcessor.this.mHandler.sendEmptyMessage(GestureEngineProcessor.sGESTURE_EVENT_CAPTURE);
                                    GestureEngineProcessor.this.bFrameUpdated = false;
                                    return;
                                }
                                GestureEngineProcessor.this.bFrameUpdated = false;
                            } else {
                                continue;
                            }
                        }
                    }
                }
            }
        });
        return true;
    }
}
