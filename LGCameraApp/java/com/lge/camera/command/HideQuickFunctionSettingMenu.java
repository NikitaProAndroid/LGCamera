package com.lge.camera.command;

import android.os.Bundle;
import android.widget.ListView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class HideQuickFunctionSettingMenu extends Command {
    public HideQuickFunctionSettingMenu(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        boolean immediately = ((Bundle) arg).getBoolean("immediately", false);
        CamLog.d(FaceDetector.TAG, "HideQuickFunctionSettingMenu is EXECUTE : immediately = " + immediately);
        this.mGet.quickFunctionAllMenuSelected(false);
        if (immediately) {
            this.mGet.removeQuickFunctionSettingView();
        } else {
            this.mGet.qflSettingAnimation((ListView) this.mGet.findViewById(R.id.quick_function_settingview), false);
            this.mGet.setSubMenuMode(0);
        }
        showAllControl();
        this.mGet.setQuickButtonVisible(100, 0, true);
        this.mGet.checkStorage(false);
    }

    private void showAllControl() {
        if (checkMediator()) {
            this.mGet.showControllerForHideSettingMenu(false, false);
        }
    }
}
