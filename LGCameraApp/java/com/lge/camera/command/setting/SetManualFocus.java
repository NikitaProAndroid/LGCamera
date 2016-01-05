package com.lge.camera.command.setting;

import android.hardware.Camera.Parameters;
import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetManualFocus extends SettingCommand {
    public SetManualFocus(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetManualFocus");
        if (checkMediator()) {
            int value = ((Bundle) arg).getInt("mValue", 0);
            if (value == 0) {
                value = this.mGet.getManualFocusValue();
            }
            if (value < 0) {
                value = 0;
            }
            this.mGet.setManualFocusValue(value);
            int valueForParameter = scaleParameter(value, lgParameters.getParameters());
            CamLog.i(FaceDetector.TAG, "SetManualFocus value to parameter: " + valueForParameter);
            lgParameters.getParameters().set("manualfocus_step", valueForParameter);
            return;
        }
        CamLog.w(FaceDetector.TAG, "Mediator is null.");
    }

    public int scaleParameter(int value, Parameters params) {
        if (params == null) {
            return 0;
        }
        return Math.round(((float) value) * (60.0f / 60.0f));
    }
}
