package com.lge.camera.command;

import com.lge.camera.ControllerFunction;

public class ResetMenuPrepared extends Command {
    public ResetMenuPrepared(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        if (this.mGet.getPreferenceGroup() != null) {
            if (this.mGet.getApplicationMode() == 1) {
                this.mGet.effectRecorderStopPreview();
            } else {
                this.mGet.effectCameraStopPreview();
            }
        }
    }
}
