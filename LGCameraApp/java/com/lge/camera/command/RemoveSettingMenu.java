package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class RemoveSettingMenu extends Command {
    public RemoveSettingMenu(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        CamLog.d(FaceDetector.TAG, "RemoveSettingMenu");
        boolean showAll = ((Bundle) arg).getBoolean("showAll", true);
        this.mGet.setMainButtonVisible(true);
        if (showAll) {
            if (!this.mGet.getSubCameraModeRunning()) {
                this.mGet.quickFunctionControllerRefresh(true);
                this.mGet.setQuickButtonMenuEnable(true, false);
            }
            this.mGet.removeSettingView();
        } else {
            if (!ProjectVariables.useHideQFLWhenSettingMenuDisplay()) {
                this.mGet.quickFunctionControllerRefresh(true);
                this.mGet.setQuickButtonMenuEnable(true, false);
            }
            this.mGet.removeSettingViewAll();
        }
        this.mGet.showIndicatorController();
        this.mGet.showSubButtonInit(false);
        if (this.mGet.isAttachIntent()) {
            this.mGet.setSwitcherVisible(false);
        } else {
            this.mGet.setSwitcherVisible(true);
        }
        if (ModelProperties.is3dSupportedModel()) {
            this.mGet.set3DSwitchVisible(true);
        }
        this.mGet.showControllerForHideSettingMenu(true, showAll);
        this.mGet.setQuickButtonVisible(100, 0, true);
        this.mGet.checkStorage(false);
    }
}
