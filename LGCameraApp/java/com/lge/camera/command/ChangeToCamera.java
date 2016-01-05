package com.lge.camera.command;

import android.content.Intent;
import com.lge.camera.CameraApp;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CameraHolder;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;

public class ChangeToCamera extends Command {
    public ChangeToCamera(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ChangeToCamera-start");
        try {
            this.mGet.perfLockAcquire();
        } catch (NoSuchMethodError e) {
        }
        this.mGet.removeScheduledAllCommand();
        this.mGet.clearSubMenu();
        Intent intent = new Intent("android.media.action.STILL_IMAGE_CAMERA");
        intent.putExtra("keepLastValue", true);
        intent.putExtra("isFirst", false);
        intent.putExtra("orientation", this.mGet.getOrientation());
        intent.putExtra("mainCameraDimension", this.mGet.getMainCameraDimension());
        intent.putExtra(CameraConstants.INTENT_EXTRA_SHOW_LOADING, true);
        intent.addFlags(67108864);
        intent.addFlags(65536);
        intent.setClassName(this.mGet.getActivity().getPackageName(), CameraApp.class.getName());
        this.mGet.setIsSendBroadcastIntent(false);
        CameraConstants.IS_CHANGE_MODE_STATUS = true;
        intent.putExtra(CameraConstants.SECURE_CAMERA, Common.isSecureCamera());
        CameraHolder.instance().keep();
        this.mGet.setChangeMode();
        CheckStatusManager.setTelephonyStateCheckSkip(true);
        this.mGet.getActivity().startActivity(intent);
        this.mGet.activityFinish();
        this.mGet.getActivity().overridePendingTransition(0, 0);
        Common.checkEnteringTime(false);
        CamLog.d(FaceDetector.TAG, "ChangeToCamera-end");
    }
}
