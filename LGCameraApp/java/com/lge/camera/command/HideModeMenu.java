package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class HideModeMenu extends Command {
    public HideModeMenu(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        boolean immediately = ((Bundle) arg).getBoolean("immediately", false);
        CamLog.d(FaceDetector.TAG, "HideModeMenu is EXECUTE : immediately = " + immediately);
        if (immediately) {
            this.mGet.hideShotModeMenu(false);
            this.mGet.setSubMenuMode(0);
        } else {
            this.mGet.hideShotModeMenu(true);
            this.mGet.setSubMenuMode(0);
        }
        this.mGet.showControllerForHideSettingMenu(false, false);
        this.mGet.setQuickButtonVisible(100, 0, true);
        this.mGet.checkStorage(false);
    }
}
