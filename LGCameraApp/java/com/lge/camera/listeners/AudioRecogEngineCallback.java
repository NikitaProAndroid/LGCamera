package com.lge.camera.listeners;

import android.os.SystemClock;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.AudioRecogEngine.Callback;
import com.lge.voiceshutter.library.LGKeyRec;

public class AudioRecogEngineCallback implements Callback {
    private AudioCallbackFunction mGet;

    public interface AudioCallbackFunction {
        void audioCallbackRestartEngine();

        void audioCallbackTakePicture();
    }

    public AudioRecogEngineCallback(AudioCallbackFunction function) {
        this.mGet = null;
        this.mGet = function;
    }

    public void onAudioEngineStartCallback(int mode) {
        CamLog.d(FaceDetector.TAG, "onAudioEngineStartCallback(), " + mode);
        switch (mode) {
        }
    }

    public void onAudioEngineStopCallback(int mode) {
        CamLog.d(FaceDetector.TAG, "onAudioEngineStopCallback(), " + mode);
        switch (mode) {
        }
    }

    public void onAudioRecogErrorCallback(int error_type) {
        CamLog.d(FaceDetector.TAG, "onAudioRecogStateCallback(), ERROR:" + error_type);
        switch (error_type) {
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                SystemClock.sleep(25);
                restartEngine();
            default:
        }
    }

    public void restartEngine() {
        CamLog.d(FaceDetector.TAG, "restartEngine");
        if (this.mGet != null) {
            this.mGet.audioCallbackRestartEngine();
        }
    }

    public void onAudioRecogResultCallback(int type) {
        CamLog.d(FaceDetector.TAG, "onAudioRecogResultCallback() " + type);
        switch (type) {
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                this.mGet.audioCallbackTakePicture();
            default:
        }
    }
}
