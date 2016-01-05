package com.lge.camera.receiver;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;

public class CameraDayDreamReceiver extends CameraBroadCastReceiver {
    public CameraDayDreamReceiver(ReceiverMediatorBridge bridge) {
        super(bridge);
    }

    public void onReceive(Context context, Intent intent) {
        boolean screenLock;
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService("keyguard");
        if (ModelProperties.getProjectCode() == 8) {
            screenLock = keyguardManager.isKeyguardLocked();
        } else {
            screenLock = Common.getScreenLock();
        }
        CamLog.d(FaceDetector.TAG, "CameraDayDreamReceiver : screenLock = " + screenLock);
        ActivityManager am = (ActivityManager) context.getSystemService("activity");
        String className = null;
        if (am.getRunningTasks(1) != null) {
            className = ((RunningTaskInfo) am.getRunningTasks(1).get(0)).topActivity.getClassName();
        }
        if (screenLock) {
            if ("com.lge.camera.SecureCameraApp".equals(className)) {
                CamLog.d(FaceDetector.TAG, "CameraDayDreamReceiver : getClassName = " + className);
                finishActivity();
            }
        } else if ("com.lge.camera.CameraAppLauncher".equals(className) || "com.lge.camera.CameraApp".equals(className) || "com.lge.camera.Camcorder".equals(className)) {
            CamLog.d(FaceDetector.TAG, "CameraDayDreamReceiver : getClassName = " + className);
            finishActivity();
        }
    }

    private void finishActivity() {
        if (this.mGet != null && this.mGet.getActivity() != null && !this.mGet.getActivity().isFinishing() && CheckStatusManager.getCheckEnterOutSecure() == 0) {
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    if (CameraDayDreamReceiver.this.mGet != null) {
                        CameraDayDreamReceiver.this.mGet.removePostRunnable(this);
                        if (CameraDayDreamReceiver.this.mGet.getActivity() != null) {
                            CameraDayDreamReceiver.this.mGet.getActivity().finish();
                        }
                    }
                }
            }, 1000);
        }
    }
}
