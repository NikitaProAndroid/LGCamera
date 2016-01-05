package com.lge.camera.util;

import android.os.Handler;
import android.os.Message;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class MainHandler extends Handler {
    private HandlerFunction mGet;

    public interface HandlerFunction {
        void doSettingFlashHandler();

        void doVoiceShutterIndicatorUpdateHandler();

        void hideAudiozoomready();

        void setMainButtonEnable();

        void showHeatingwarning();

        void showRequestedSizeLimit();

        void startHeatingwarning();

        void stopHeatingwarning();
    }

    public MainHandler() {
        this.mGet = null;
    }

    public MainHandler(HandlerFunction function) {
        this.mGet = null;
        this.mGet = function;
    }

    public void unbind() {
        this.mGet = null;
    }

    public void handleMessage(Message msg) {
        CamLog.d(FaceDetector.TAG, "handle msg: " + msg.what);
        if (this.mGet != null) {
            switch (msg.what) {
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    this.mGet.setMainButtonEnable();
                    break;
                case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                    this.mGet.doSettingFlashHandler();
                    break;
                case LGKeyRec.EVENT_STARTED /*3*/:
                    this.mGet.doVoiceShutterIndicatorUpdateHandler();
                    break;
                case LGKeyRec.EVENT_STOPPED /*4*/:
                    this.mGet.showRequestedSizeLimit();
                    break;
                case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                    CamLog.d(FaceDetector.TAG, "MSG_HEATING_START");
                    this.mGet.startHeatingwarning();
                    break;
                case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                    CamLog.d(FaceDetector.TAG, "MSG_HEATING_STOP");
                    this.mGet.stopHeatingwarning();
                    break;
                case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
                    CamLog.d(FaceDetector.TAG, "MSG_HEATING_SHOW ");
                    this.mGet.showHeatingwarning();
                    break;
                case LGKeyRec.EVENT_NEED_MORE_AUDIO /*8*/:
                    CamLog.d(FaceDetector.TAG, "MSG_HEATING_SHOW ");
                    this.mGet.hideAudiozoomready();
                    break;
            }
        }
        super.handleMessage(msg);
    }
}
