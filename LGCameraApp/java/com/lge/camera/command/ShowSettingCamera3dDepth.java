package com.lge.camera.command;

import com.lge.camera.ControllerFunction;

public class ShowSettingCamera3dDepth extends Command {
    public ShowSettingCamera3dDepth(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        this.mGet.showSetting3dDepthControl(true);
    }
}
