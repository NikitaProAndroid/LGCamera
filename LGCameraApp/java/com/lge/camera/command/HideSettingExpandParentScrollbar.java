package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.controller.SettingRotatableExpandableController;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class HideSettingExpandParentScrollbar extends Command {
    public HideSettingExpandParentScrollbar(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        CamLog.d(FaceDetector.TAG, "HideSettingExpandParentScrollbar  !!!");
        ((SettingRotatableExpandableController) this.mGet.getSettingController()).hideParentScrollbar();
    }
}
