package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ShowSettingMenu extends Command {
    public ShowSettingMenu(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ClickQuickFunctionButton5  !!!");
        if (this.mGet.getInCaptureProgress()) {
            CamLog.d(FaceDetector.TAG, "While capturing the photos, Does not display the settings menu.");
            this.mGet.removeScheduledCommand(Command.SHOW_SETTING_MENU);
            return;
        }
        if (this.mGet.getApplicationMode() == 1) {
            this.mGet.recordingControllerHide();
        }
        this.mGet.clearSubMenu();
        this.mGet.showManualFocusController(false);
        this.mGet.doCommandUi(Command.DISPLAY_SETTING_MENU);
        this.mGet.setSubMenuMode(5);
        this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        this.mGet.doCommandUi(Command.HIDE_LIVEEFFECT_SUBMENU_DRAWER);
        if (ProjectVariables.isSupportClearView()) {
            this.mGet.removeScheduledCommand(Command.CLEAR_SCREEN);
        }
        if (ProjectVariables.useHideQFLWhenSettingMenuDisplay()) {
            int menuIndex = this.mGet.getQfIndex(Setting.KEY_SETTING);
            if (this.mGet.isQuickFunctionList(menuIndex)) {
                this.mGet.setQFLMenuSelected(menuIndex, true);
            }
        }
        this.mGet.showIndicatorController();
        this.mGet.removeScheduledCommand(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION);
        if (ModelProperties.is3dSupportedModel()) {
            this.mGet.set3DSwitchVisible(false);
        }
    }
}
