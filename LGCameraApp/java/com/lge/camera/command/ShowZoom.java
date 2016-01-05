package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ShowZoom extends Command {
    public ShowZoom(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ShowZoom is EXECUTE !!!");
        if (ProjectVariables.isSupportClearView()) {
            this.mGet.removeScheduledCommand(Command.CLEAR_SCREEN);
        }
        this.mGet.doCommandDelayed(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION, ProjectVariables.keepDuration);
        if (this.mGet.getApplicationMode() == 0 && this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
            this.mGet.removePanoramaView();
        }
        this.mGet.doCommand(Command.RELEASE_TOUCH_FOCUS);
        if (this.mGet.getSubMenuMode() == 6) {
            this.mGet.resetDisplayTimeoutZoom();
            return;
        }
        if (this.mGet.getSubMenuMode() != 0) {
            this.mGet.clearSubMenu();
        }
        int menuIndex = this.mGet.getQfIndex(Setting.KEY_ZOOM);
        if (this.mGet.isQuickFunctionList(menuIndex)) {
            this.mGet.setQFLMenuSelected(menuIndex, true);
        }
        this.mGet.setSubMenuMode(6);
        this.mGet.showBrightnessController(false);
        this.mGet.showBeautyshotController(false);
        this.mGet.showManualFocusController(false);
        this.mGet.showZoomController(true);
        if (ModelProperties.is3dSupportedModel()) {
            this.mGet.show3dDepthController(false);
        }
        this.mGet.setFocusRectangleInitialize();
        this.mGet.hideFocus();
    }
}
