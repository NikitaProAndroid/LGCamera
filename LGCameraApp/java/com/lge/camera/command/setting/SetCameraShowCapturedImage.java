package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetCameraShowCapturedImage extends SettingCommand {
    public SetCameraShowCapturedImage(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters parameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetCameraShowCapturedImage executed");
        String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
        if (AppControlUtil.checkGuestModeAndAppDisabled(this.mGet.getContentResolver(), true, 1) || this.mGet.isAttachIntent()) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, false);
        } else if (CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(shotMode)) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, false);
        } else {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true);
        }
    }

    protected void onExecuteAlone() {
        this.mGet.allSettingMenuSelectedChild(Setting.KEY_CAMERA_AUTO_REVIEW, true);
        this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true, true);
    }
}
