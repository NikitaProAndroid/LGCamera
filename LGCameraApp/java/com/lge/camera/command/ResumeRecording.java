package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ResumeRecording extends Command {
    public ResumeRecording(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        execute(null);
    }

    public void execute(Object obj) {
        CamLog.d(FaceDetector.TAG, "ResumeRecording");
        if (this.mGet.isDualRecordingActive() || this.mGet.isSmartZoomRecordingActive()) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("only_handle_close", true);
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU, bundle);
        }
        this.mGet.resumeRecording();
    }
}
