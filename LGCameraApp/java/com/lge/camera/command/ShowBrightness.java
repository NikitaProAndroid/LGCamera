package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ShowBrightness extends Command {
    public ShowBrightness(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ShowBrightness is EXECUTE !!!");
        if (ProjectVariables.isSupportClearView()) {
            this.mGet.removeScheduledCommand(Command.CLEAR_SCREEN);
        }
        this.mGet.doCommandDelayed(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION, ProjectVariables.keepDuration);
        if (this.mGet.getSubMenuMode() == 7) {
            this.mGet.resetDisplayTimeoutBrightness();
            return;
        }
        if (this.mGet.getSubMenuMode() != 0) {
            this.mGet.clearSubMenu();
        }
        this.mGet.doCommand(Command.RELEASE_TOUCH_FOCUS);
        this.mGet.setSubMenuMode(7);
        int menuIndex = this.mGet.getQfIndex(Setting.KEY_BRIGHTNESS);
        if (this.mGet.isQuickFunctionList(menuIndex)) {
            this.mGet.setQFLMenuSelected(menuIndex, true);
        }
        handleQuickFunction();
        this.mGet.setFocusRectangleInitialize();
        this.mGet.hideFocus();
    }

    private void handleQuickFunction() {
        if (this.mGet.getApplicationMode() == 0 && this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
            this.mGet.removePanoramaView();
        }
        this.mGet.showZoomController(false);
        this.mGet.showManualFocusController(false);
        this.mGet.showBeautyshotController(false);
        this.mGet.showBrightnessController(true);
        if (ModelProperties.is3dSupportedModel()) {
            this.mGet.show3dDepthController(false);
        }
        if (this.mGet.getApplicationMode() == 0) {
            this.mGet.hideFocus();
        }
    }
}
