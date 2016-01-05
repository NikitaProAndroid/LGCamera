package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;

public class DisplaySettingMenu extends Command {
    public DisplaySettingMenu(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        if (this.mGet.getApplicationMode() == 0) {
            if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
                this.mGet.removePanoramaView();
            }
            this.mGet.cancelAutoFocus();
            this.mGet.setFocusRectangleInitialize();
            if (this.mGet.isAttachMode()) {
                this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_AUTO_REVIEW, false, false);
                if (ModelProperties.isSupportShotModeModel() && this.mGet.getCameraId() != 1) {
                    this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_SHOT_MODE, false, false);
                }
            }
        }
        this.mGet.hideFocus();
        this.mGet.displaySettingView();
        if (this.mGet.getApplicationMode() == 1) {
            this.mGet.recordingControllerHide();
        }
    }
}
