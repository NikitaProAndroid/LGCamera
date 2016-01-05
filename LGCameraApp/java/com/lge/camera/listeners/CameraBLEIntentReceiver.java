package com.lge.camera.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lge.camera.CameraApp;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CameraHolder;
import com.lge.camera.util.TelephonyUtil;
import com.lge.olaworks.library.FaceDetector;

public class CameraBLEIntentReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        CamLog.d(FaceDetector.TAG, "onReceive()");
        if (CameraConstants.BLE_ONEKEY_CHANGED.equals(intent.getAction()) && CameraConstants.BLE_ONEKEY_SERVICE.equals(intent.getStringExtra("_service")) && CameraConstants.ONEKEY_CONTROL_ENABLE_STRING.equals(intent.getStringExtra("_onekeyCamera")) && !TelephonyUtil.phoneInCall(context)) {
            String key = intent.getStringExtra("_key");
            CamLog.d(FaceDetector.TAG, "CameraBLEIntentReceiver Launch key:" + key);
            if (key != null && key.equals("LongKey")) {
                CameraHolder holder = CameraHolder.instance();
                if (holder != null && holder.tryOpen(0) != null) {
                    holder.keep();
                    holder.release();
                    Intent i = new Intent("android.intent.action.MAIN");
                    i.setClass(context, CameraApp.class);
                    i.addCategory("android.intent.category.LAUNCHER");
                    i.setFlags(269484032);
                    context.startActivity(i);
                }
            }
        }
    }
}
