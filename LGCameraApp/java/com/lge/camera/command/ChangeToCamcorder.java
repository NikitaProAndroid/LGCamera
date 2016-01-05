package com.lge.camera.command;

import android.content.Intent;
import com.lge.camera.Camcorder;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CameraHolder;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;

public class ChangeToCamcorder extends Command {
    public ChangeToCamcorder(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ChangeToCamcorder");
        try {
            this.mGet.perfLockAcquire();
        } catch (NoSuchMethodError e) {
        }
        this.mGet.removeScheduledAllCommand();
        if (this.mGet.getApplicationMode() == 0) {
            this.mGet.cancelAutoFocus();
        }
        Intent intent = new Intent("android.media.action.VIDEO_CAMERA");
        intent.putExtra("keepLastValue", true);
        intent.putExtra("isFirst", this.mGet.getActivity().getIntent().getBooleanExtra("isFirst", true));
        intent.putExtra("orientation", this.mGet.getOrientation());
        intent.putExtra("mainCameraDimension", this.mGet.getMainCameraDimension());
        intent.putExtra(CameraConstants.INTENT_EXTRA_SHOW_LOADING, true);
        intent.addFlags(67108864);
        intent.addFlags(65536);
        intent.setClassName(this.mGet.getActivity().getPackageName(), Camcorder.class.getName());
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
    }
}
