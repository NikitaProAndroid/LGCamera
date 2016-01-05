package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ShowQuickFunctionDragDrop extends Command {
    public ShowQuickFunctionDragDrop(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ShowQuickFunctionDragDrop executed");
        execute(Integer.valueOf(0));
    }

    public void execute(Object arg) {
        int index = ((Integer) arg).intValue();
        CamLog.d(FaceDetector.TAG, "ShowQuickFunctionDragDrop executed select index = " + index);
        if (this.mGet.getInCaptureProgress()) {
            CamLog.d(FaceDetector.TAG, "While capturing the photos, Does not display the edit menu.");
            this.mGet.removeScheduledCommand(Command.SHOW_QUICK_FUNCTION_DRAG_DROP);
        } else if (this.mGet.isTimerShotCountdown()) {
            CamLog.d(FaceDetector.TAG, "While timer shot, Does not display the edit menu.");
            this.mGet.removeScheduledCommand(Command.SHOW_QUICK_FUNCTION_DRAG_DROP);
        } else {
            if (this.mGet.isSettingControllerVisible()) {
                this.mGet.removeSettingViewAll();
            }
            if (this.mGet.isQuickFunctionSettingControllerShowing()) {
                this.mGet.removeQuickFunctionSettingView();
            }
            if (this.mGet.getApplicationMode() == 1) {
                this.mGet.recordingControllerHide();
            }
            this.mGet.setMainButtonVisible(false);
            this.mGet.clearSubMenu();
            this.mGet.hideOsd();
            this.mGet.showManualFocusController(false);
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
            this.mGet.doCommandUi(Command.HIDE_LIVEEFFECT_SUBMENU_DRAWER);
            this.mGet.showIndicatorController();
            this.mGet.removeScheduledCommand(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION);
            this.mGet.setSwitcherVisible(false);
            this.mGet.setSubButton(0, 0);
            this.mGet.setSubButton(1, 0);
            this.mGet.setSubButton(2, 0);
            if (ModelProperties.is3dSupportedModel()) {
                this.mGet.set3DSwitchVisible(false);
            }
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
            this.mGet.setQuickButtonVisible(100, 8, true);
            this.mGet.setThumbnailButtonVisibility(8);
            this.mGet.setQuickFunctionControllerVisible(false);
            this.mGet.setQuickFunctionDragControllerSelectIndex(index);
            this.mGet.showQuickFunctionDragController();
            this.mGet.setSubMenuMode(22);
        }
    }
}
