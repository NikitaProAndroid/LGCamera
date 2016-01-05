package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.controller.SettingRotatableExpandableController;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ShowSettingExpandChild extends Command {
    public ShowSettingExpandChild(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        Bundle bundle = (Bundle) arg;
        int column = bundle.getInt("column", 0);
        int row = bundle.getInt("row", 0);
        String key = bundle.getString("key", null);
        CamLog.d(FaceDetector.TAG, "ShowSettingExpandChild  !!! key=" + key + " column=" + column + " row=" + row);
        ((SettingRotatableExpandableController) this.mGet.getSettingController()).showChildSetting(column, row, key);
    }
}
