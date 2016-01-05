package com.lge.camera.command;

import com.lge.camera.ControllerFunction;

public class ShowSettingZoom extends Command {
    public ShowSettingZoom(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        this.mGet.showSettingZoomControl(true);
    }
}
