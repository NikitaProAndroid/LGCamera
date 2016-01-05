package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.hardware.LGCamera.LGParameters;

public class SetVideoSelectMemory extends SettingCommand {
    public SetVideoSelectMemory(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
    }
}
