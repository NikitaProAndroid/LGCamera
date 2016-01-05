package com.lge.voiceshutter.library;

import android.util.Log;
import com.lge.camera.properties.FunctionProperties;
import java.io.File;

public final class LGKeyRec {
    public static final int EVENT_INCOMPLETE = 2;
    public static final int EVENT_INVALID = 0;
    public static final int EVENT_MAX_SPEECH = 9;
    public static final int EVENT_NEED_MORE_AUDIO = 8;
    public static final int EVENT_NO_MATCH = 1;
    public static final int EVENT_RECOGNITION_RESULT = 6;
    public static final int EVENT_RECOGNITION_TIMEOUT = 7;
    public static final int EVENT_STARTED = 3;
    public static final int EVENT_START_OF_VOICING = 5;
    public static final int EVENT_STOPPED = 4;
    private static int LGKhandle = 0;
    private static final int STATUS_CREATED = 1;
    private static final int STATUS_INITIALIZED = 2;
    private static final int STATUS_NULL = 0;
    private static final int STATUS_STARTED = 3;
    private static final int STATUS_STOPPED = 4;
    public static final String TAG = "LGKeyRec";
    private int LGK_status;

    private native int LGKAdvance(int i);

    private native int LGKCreate(int i, int i2);

    private native void LGKDestroy(int i);

    private native void LGKInitialize(int i);

    private native String LGKLibraryVersion();

    private native void LGKPutAudio(int i, byte[] bArr, int i2, int i3, boolean z);

    private native void LGKStart(int i);

    private native void LGKStop(int i);

    static {
        LGKhandle = STATUS_NULL;
        File path = new File("/sdcard/mvoice/pcm");
        if (!path.isDirectory()) {
            path.mkdirs();
        }
        System.loadLibrary("kwr_mvoice-jni_4");
        Log.d(TAG, "loading: libkwr_mvoice-jni.so");
    }

    public static String eventToString(int event) {
        switch (event) {
            case STATUS_NULL /*0*/:
                return "EVENT_INVALID";
            case STATUS_CREATED /*1*/:
                return "EVENT_NO_MATCH";
            case STATUS_INITIALIZED /*2*/:
                return "EVENT_INCOMPLETE";
            case STATUS_STARTED /*3*/:
                return "EVENT_STARTED";
            case STATUS_STOPPED /*4*/:
                return "EVENT_STOPPED";
            case EVENT_START_OF_VOICING /*5*/:
                return "EVENT_START_OF_VOICING";
            case EVENT_RECOGNITION_RESULT /*6*/:
                return "EVENT_RECOGNITION_RESULT";
            case EVENT_RECOGNITION_TIMEOUT /*7*/:
                return "EVENT_RECOGNITION_TIMEOUT";
            case EVENT_NEED_MORE_AUDIO /*8*/:
                return "EVENT_NEED_MORE_AUDIO";
            case EVENT_MAX_SPEECH /*9*/:
                return "EVENT_MAX_SPEECH";
            default:
                return "EVENT_" + event;
        }
    }

    public LGKeyRec(int language) {
        this.LGK_status = STATUS_NULL;
        LGKhandle = LGKCreate(language, FunctionProperties.getVoiceShutterSensitivity());
        this.LGK_status = STATUS_CREATED;
    }

    public void Initialize() {
        if (this.LGK_status != 0) {
            LGKInitialize(LGKhandle);
            this.LGK_status = STATUS_INITIALIZED;
        }
    }

    public void Start() {
        if (this.LGK_status == STATUS_STOPPED) {
            Initialize();
        }
        if (this.LGK_status == STATUS_INITIALIZED) {
            LGKStart(LGKhandle);
            this.LGK_status = STATUS_STARTED;
        }
    }

    public int Advance() {
        if (this.LGK_status == STATUS_STARTED) {
            return LGKAdvance(LGKhandle);
        }
        return STATUS_NULL;
    }

    public void PutAudio(byte[] buf, int offset, int length, boolean isLast) {
        if (this.LGK_status == STATUS_STARTED) {
            LGKPutAudio(LGKhandle, buf, offset, length, isLast);
        }
    }

    public void Stop() {
        if (LGKhandle != 0 && this.LGK_status == STATUS_STARTED) {
            LGKStop(LGKhandle);
        }
        this.LGK_status = STATUS_STOPPED;
    }

    public void DestroyRecognizer() {
        Stop();
        if (LGKhandle != 0) {
            LGKDestroy(LGKhandle);
            LGKhandle = STATUS_NULL;
            Log.d(TAG, "LGKDestroy is called !");
        }
        this.LGK_status = STATUS_NULL;
    }

    public String LibraryVersion() {
        return LGKLibraryVersion();
    }
}
