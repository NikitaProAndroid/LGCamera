package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.controller.SettingRotatableExpandableController;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class UpdateSettingExpand extends Command {
    public UpdateSettingExpand(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        Bundle bundle = (Bundle) arg;
        int column = bundle.getInt("column", 0);
        CamLog.d(FaceDetector.TAG, "ShowSettingExpandChild  !!! key=" + bundle.getString("key", null) + " column=" + column + " row=" + bundle.getInt("row", 0));
        ((SettingRotatableExpandableController) this.mGet.getSettingController()).updateSetting();
    }
}
