package com.lge.camera.command;

import com.lge.camera.ControllerFunction;

public class ShowSettingBrightness extends Command {
    public ShowSettingBrightness(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        this.mGet.showSettingBrightnessControl(true);
    }
}
