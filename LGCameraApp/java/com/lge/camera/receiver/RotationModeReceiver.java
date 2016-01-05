package com.lge.camera.receiver;

import android.content.Context;
import android.content.Intent;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class RotationModeReceiver extends CameraBroadCastReceiver {
    public RotationModeReceiver(ReceiverMediatorBridge bridge) {
        super(bridge);
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals("com.lge.android.intent.action.SWITCH_ROTATION_MODE")) {
            String extra = intent.getStringExtra("com.lge.intent.extra.ROTATION_MODE");
            if ("land".equals(extra)) {
                CamLog.d(FaceDetector.TAG, "EXTRA_ROTATION_MODE  extra : land");
                this.mGet.setOrientationForced(0);
            } else if ("port".equals(extra)) {
                CamLog.d(FaceDetector.TAG, "EXTRA_ROTATION_MODE  extra : port");
                this.mGet.setOrientationForced(1);
            }
        }
    }
}
