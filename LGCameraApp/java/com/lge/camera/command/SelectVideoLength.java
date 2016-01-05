package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class SelectVideoLength extends Command {
    public SelectVideoLength(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "SelectVideoLength");
        this.mGet.showDialogPopup(16);
    }
}
