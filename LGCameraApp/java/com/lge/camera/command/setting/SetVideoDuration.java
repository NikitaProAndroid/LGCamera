package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.hardware.LGCamera.LGParameters;

public class SetVideoDuration extends SettingCommand {
    public SetVideoDuration(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
    }

    protected void onExecuteAlone() {
    }
}
