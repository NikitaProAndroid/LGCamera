package com.lge.camera.command;

import com.lge.camera.ControllerFunction;

public class StopPreview extends Command {
    public StopPreview(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        this.mGet.stopPreview();
    }
}
