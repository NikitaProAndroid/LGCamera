package com.lge.camera.receiver;

import android.content.Context;
import android.content.Intent;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class VoiceMailReceiver extends CameraBroadCastReceiver {
    public VoiceMailReceiver(ReceiverMediatorBridge bridge) {
        super(bridge);
    }

    public void onReceive(Context context, Intent intent) {
        CamLog.d(FaceDetector.TAG, "BroadCastReceiver action = " + intent.getAction());
        if (intent.getAction().equals("com.lge.vvm.NEW_VVM_NOTIFICATION_RECEIVED")) {
            int number = intent.getIntExtra("vvm_unreadcount", 0);
            CamLog.d(FaceDetector.TAG, "vvm_unreadcount = " + number);
            if (number == 0) {
                try {
                    this.mGet.setVoiceMailIndicator(0);
                    return;
                } catch (NumberFormatException e) {
                    CamLog.e(FaceDetector.TAG, "failure to read ", e);
                    return;
                }
            }
            this.mGet.setVoiceMailIndicator(1);
        }
    }
}
