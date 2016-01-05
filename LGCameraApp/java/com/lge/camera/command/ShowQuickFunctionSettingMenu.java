package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ShowQuickFunctionSettingMenu extends Command {
    public ShowQuickFunctionSettingMenu(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ShowQuickFunctionSettingMenu is EXECUTE !!!");
        execute(null);
    }

    public void execute(Object obj) {
        String key = (String) obj;
        CamLog.d(FaceDetector.TAG, "ShowQuickFunctionSettingMenu is EXECUTE !!! + key = " + key);
        if (this.mGet.getApplicationMode() == 1) {
            this.mGet.recordingControllerHide();
        }
        this.mGet.clearSubMenu();
        this.mGet.setQuickButtonVisible(100, 8, true);
        this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        this.mGet.doCommandUi(Command.HIDE_LIVEEFFECT_SUBMENU_DRAWER);
        this.mGet.hideFocus();
        if (this.mGet.getApplicationMode() == 0) {
            if (this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE).equals(CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
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
        this.mGet.checkStorage(false);
        if (key != null) {
            this.mGet.setQFLMenuSelected(this.mGet.getQfIndex(key), true);
            this.mGet.displayQuickFunctionSettingView(key);
        }
    }
}
