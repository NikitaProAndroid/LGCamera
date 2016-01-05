package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.olaworks.library.FaceDetector;

public class ShowBeautyshot extends Command {
    public ShowBeautyshot(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ShowBeautyshot is EXECUTE !!!");
        if (ProjectVariables.isSupportClearView()) {
            this.mGet.removeScheduledCommand(Command.CLEAR_SCREEN);
        }
        this.mGet.doCommandDelayed(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION, ProjectVariables.keepDuration);
        if (this.mGet.getSubMenuMode() == 15) {
            this.mGet.resetDisplayTimeoutBeautyshot();
            return;
        }
        if (this.mGet.getSubMenuMode() != 0) {
            this.mGet.clearSubMenu();
        }
        this.mGet.setSubMenuMode(15);
        this.mGet.setPreferenceMenuEnable(Setting.KEY_BEAUTYSHOT, true, false);
        handleQuickFunction();
        this.mGet.setFocusRectangleInitialize();
        this.mGet.hideFocus();
    }

    private void handleQuickFunction() {
        if (this.mGet.getApplicationMode() == 0 && this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
            this.mGet.removePanoramaView();
        }
        this.mGet.showZoomController(false);
        this.mGet.showBrightnessController(false);
        this.mGet.showManualFocusController(false);
        if (ModelProperties.is3dSupportedModel()) {
            this.mGet.show3dDepthController(false);
        }
        this.mGet.showBeautyshotController(true);
        if (this.mGet.getApplicationMode() == 0) {
            this.mGet.hideFocus();
        }
        this.mGet.showHelpGuidePopup(Setting.HELP_BEAUTY_SHOT, DialogCreater.DIALOG_ID_HELP_BEAUTY_SHOT, true);
    }
}
