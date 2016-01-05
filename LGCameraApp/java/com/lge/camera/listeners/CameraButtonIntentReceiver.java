package com.lge.camera.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lge.camera.CameraApp;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CameraHolder;
import com.lge.olaworks.library.FaceDetector;

public class CameraButtonIntentReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        CamLog.d(FaceDetector.TAG, "onReceive()");
        CameraHolder holder = CameraHolder.instance();
        if (holder != null && holder.tryOpen(0) != null) {
            holder.keep();
            holder.release();
            Intent i = new Intent("android.intent.action.MAIN");
            i.setClass(context, CameraApp.class);
            i.addCategory("android.intent.category.LAUNCHER");
            i.setFlags(268435456);
            context.startActivity(i);
        }
    }
}
