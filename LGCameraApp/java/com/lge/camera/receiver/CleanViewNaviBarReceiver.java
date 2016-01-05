package com.lge.camera.receiver;

import android.content.Context;
import android.content.Intent;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class CleanViewNaviBarReceiver extends CameraBroadCastReceiver {
    public CleanViewNaviBarReceiver(ReceiverMediatorBridge bridge) {
        super(bridge);
    }

    public void onReceive(Context context, Intent intent) {
        CamLog.d(FaceDetector.TAG, "CleanViewNaviBarReceiver : onReceive()");
        if (checkOnReceive(intent) && CameraConstants.INTENT_ACTION_CLEAN_VIEW_RECEIVER.equals(intent.getAction()) && this.mGet.getActivity() != null) {
            this.mGet.getActivity().setCleanViewAndNavigationBar(true, false);
        }
    }
}
