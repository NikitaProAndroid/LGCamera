package com.lge.camera.command;

import com.lge.camera.ControllerFunction;

public class RestartPreview extends Command {
    public RestartPreview(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        this.mGet.restartPreview(null, true);
    }
}
