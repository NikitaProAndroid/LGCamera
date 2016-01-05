package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ShowResetDialog extends Command {
    public ShowResetDialog(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ShowResetDialog is EXECUTE !!!");
        this.mGet.showDialogPopup(6);
    }
}
