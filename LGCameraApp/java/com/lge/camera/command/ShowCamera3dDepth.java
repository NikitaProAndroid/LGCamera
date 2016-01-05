package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ShowCamera3dDepth extends Command {
    public ShowCamera3dDepth(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "showCamera3dDepth is EXECUTE !!!");
        this.mGet.doCommandDelayed(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION, ProjectVariables.keepDuration);
        if (ProjectVariables.isSupportClearView()) {
            this.mGet.removeScheduledCommand(Command.CLEAR_SCREEN);
        }
        if (this.mGet.getSubMenuMode() == 23) {
            this.mGet.resetDisplayTimeout3dDepth();
            return;
        }
        if (this.mGet.getSubMenuMode() != 0) {
            this.mGet.clearSubMenu();
        }
        this.mGet.setSubMenuMode(23);
        this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_3D_DEPTH, true, false);
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
        this.mGet.show3dDepthController(true);
        if (this.mGet.getApplicationMode() == 0) {
            this.mGet.hideFocus();
        }
    }
}
