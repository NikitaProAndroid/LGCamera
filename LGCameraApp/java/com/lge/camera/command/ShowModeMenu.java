package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ShowModeMenu extends Command {
    public ShowModeMenu(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ShowModeMenu is EXECUTE !!!");
        if (this.mGet.getApplicationMode() == 1) {
            this.mGet.recordingControllerHide();
        }
        this.mGet.clearSubMenu();
        this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        this.mGet.doCommandUi(Command.HIDE_LIVEEFFECT_SUBMENU_DRAWER);
        this.mGet.showBubblePopupVisibility(0, CameraConstants.TOAST_LENGTH_LONG, false);
        this.mGet.hideFocus();
        if (this.mGet.getApplicationMode() == 0) {
            if (this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE).equals(CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
                this.mGet.removePanoramaView();
            }
            this.mGet.cancelAutoFocus();
            this.mGet.setFocusRectangleInitialize();
        }
        this.mGet.checkStorage(false);
        this.mGet.showShotModeMenu();
    }
}
