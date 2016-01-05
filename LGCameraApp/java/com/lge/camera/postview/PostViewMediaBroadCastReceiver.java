package com.lge.camera.postview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class PostViewMediaBroadCastReceiver extends BroadcastReceiver {
    private ReceiverFunction mGet;

    public PostViewMediaBroadCastReceiver(ReceiverFunction function) {
        this.mGet = null;
        this.mGet = function;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        try {
            if (action.equals("android.intent.action.MEDIA_BAD_REMOVAL") || action.equals("android.intent.action.MEDIA_REMOVED") || action.equals("android.intent.action.MEDIA_EJECT")) {
                CameraConstants.MEDIA_RECEIVER_FINISHED = false;
                if (this.mGet != null) {
                    this.mGet.finish();
                }
            } else if (action.equals("android.intent.action.MEDIA_MOUNTED")) {
                doMediaMounted();
            }
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "PostViewMediaBroadCastReceiver Exception : ", e);
        }
    }

    private void doMediaMounted() {
        if (this.mGet != null) {
            this.mGet.setSDCardSetting(true);
        }
    }

    public void unbind() {
        if (this.mGet != null) {
            this.mGet = null;
        }
    }
}
