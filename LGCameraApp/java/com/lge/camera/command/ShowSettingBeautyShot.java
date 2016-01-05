package com.lge.camera.command;

import com.lge.camera.ControllerFunction;

public class ShowSettingBeautyShot extends Command {
    public ShowSettingBeautyShot(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        this.mGet.showSettingBeautyShotControl(true);
    }
}
