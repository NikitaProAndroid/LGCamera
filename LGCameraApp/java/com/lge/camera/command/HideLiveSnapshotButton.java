package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class HideLiveSnapshotButton extends Command {
    public HideLiveSnapshotButton(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "HideLiveSnapshotButton executed");
        if (this.mGet.getVideoState() != 3 && this.mGet.getVideoState() != 4) {
            this.mGet.hidePreviewPanelLiveSnapshotButton();
        }
    }
}
