package com.lge.camera.receiver;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Global;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;

public class SmartCoverReceiver extends CameraBroadCastReceiver {
    public SmartCoverReceiver(ReceiverMediatorBridge bridge) {
        super(bridge);
    }

    public void onReceive(Context context, Intent intent) {
        CamLog.d(FaceDetector.TAG, "QuickWindowCaseReceiver : onReceive()");
        if (!checkOnReceive(intent)) {
            return;
        }
        if (Global.getInt(context.getContentResolver(), "quick_view_enable", 1) == 0) {
            CamLog.d(FaceDetector.TAG, "Quick Window view setting disable.");
            return;
        }
        String action = intent.getAction();
        if (action.equals(CameraConstants.INTENT_ACTION_CAMERA_FINISH)) {
            if (!this.mGet.getActivity().isFinishing() && CheckStatusManager.getCheckEnterOutSecure() == 0) {
                this.mGet.getActivity().finish();
            }
        } else if (action.equals(CameraConstants.ACTION_ACCESSORY_EVENT)) {
            int coverState = intent.getIntExtra(CameraConstants.EXTRA_ACCESSORY_STATE, 0);
            CamLog.e(FaceDetector.TAG, "quick window case state:" + coverState);
            if (coverState == 0) {
                CamLog.e(FaceDetector.TAG, "cover EXTRA_ACCESSORY_COVER_OPENED!!");
                Common.setSmartCoverClosed(false);
                if (Common.isQuickWindowCameraMode() && !this.mGet.isPausing()) {
                    Common.setQuickWindowCameraMode(false);
                    if (!this.mGet.getInCaptureProgress()) {
                        this.mGet.doCommand(Command.STOP_PREVIEW);
                        this.mGet.doCommand(Command.START_PREVIEW);
                    }
                }
            } else if (coverState == 1) {
                CamLog.e(FaceDetector.TAG, "cover EXTRA_ACCESSORY_COVER_CLOSED!!");
                Common.setSmartCoverClosed(true);
                if (Common.isQuickWindowCameraMode() && this.mGet.isPreviewing()) {
                    this.mGet.getActivity().finish();
                }
            }
        }
    }
}
