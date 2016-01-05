package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ExitZoomBrightnessInteraction extends Command {
    public ExitZoomBrightnessInteraction(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ExitZoomInteraction : " + this.mGet.getSubMenuMode());
        if (this.mGet.getSubMenuMode() == 6 || this.mGet.getSubMenuMode() == 7 || this.mGet.getSubMenuMode() == 15 || this.mGet.getSubMenuMode() == 23 || this.mGet.getSubMenuMode() == 25) {
            this.mGet.clearSubMenu();
            this.mGet.setSubMenuMode(0);
            this.mGet.showIndicatorController();
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    ExitZoomBrightnessInteraction.this.mGet.removePostRunnable(this);
                    if (ExitZoomBrightnessInteraction.this.mGet.checkFocusController()) {
                        String focus = ExitZoomBrightnessInteraction.this.mGet.getSettingValue(Setting.KEY_FOCUS);
                        if (!(Setting.HELP_FACE_TRACKING_LED.equals(focus) || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(focus)) || (ExitZoomBrightnessInteraction.this.mGet.getApplicationMode() == 1 && ExitZoomBrightnessInteraction.this.mGet.isCamcorderRotation(true))) {
                            ExitZoomBrightnessInteraction.this.mGet.showFocus();
                        }
                    }
                    ExitZoomBrightnessInteraction.this.mGet.showBeautyShotBarForNewUx(true);
                }
            });
        }
    }
}
