package com.lge.camera.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Global;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;

public class QuickCamCaseIntentReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        CamLog.d(FaceDetector.TAG, "Smart cover IntentReceiver : onReceive()");
        if (intent != null) {
            if (Global.getInt(context.getContentResolver(), "quick_view_enable", 1) == 0) {
                CamLog.d(FaceDetector.TAG, "Quick Window Case setting disable.");
            } else if (intent.getAction().equals(CameraConstants.ACTION_ACCESSORY_EVENT)) {
                int coverState = intent.getIntExtra(CameraConstants.EXTRA_ACCESSORY_STATE, 0);
                CamLog.d(FaceDetector.TAG, "Cover IntentReceiver : coverState = " + coverState);
                if (coverState == 1) {
                    Common.setSmartCoverClosed(true);
                    if (Common.isSecureCamera()) {
                        context.sendBroadcast(new Intent(CameraConstants.INTENT_ACTION_CAMERA_FINISH));
                    }
                } else if (coverState == 0) {
                    Common.setSmartCoverClosed(false);
                }
            }
        }
    }
}
