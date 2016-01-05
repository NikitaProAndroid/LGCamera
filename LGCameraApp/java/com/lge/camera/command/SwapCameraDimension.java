package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class SwapCameraDimension extends SwapCamera {
    public SwapCameraDimension(ControllerFunction function) {
        super(function);
    }

    protected boolean checkSwap() {
        CamLog.d(FaceDetector.TAG, "SwapCameraDimension");
        if (this.mGet.getCameraMode() == 1) {
            return false;
        }
        this.mGet.removeScheduledCommand(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION);
        if (!preExecuteSwapCamera()) {
            return false;
        }
        CamLog.d(FaceDetector.TAG, "SwapCameraDimension EXECUTE -start !!!, current_app_mode:" + this.mGet.getApplicationMode() + " currentCameraMode:" + this.mGet.getCameraMode());
        return true;
    }

    protected void doSwapAction() {
        if (this.mGet.getCameraDimension() == 0) {
            this.mGet.setMainCameraDimension(1);
        } else {
            this.mGet.setMainCameraDimension(0);
        }
        this.mGet.applyCameraChange();
        this.mGet.set3DSwitchImage();
        this.mGet.initQuickFunctionEnabled();
        if (this.mGet.getCameraDimension() == 0) {
            this.mGet.switchCameraId(0);
        } else {
            this.mGet.switchCameraId(0);
        }
    }
}
