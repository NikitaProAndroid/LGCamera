package com.lge.camera.command.setting;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetBrightness extends SettingCommand {
    private static final int VALUE_ERROR = Integer.MIN_VALUE;

    public SetBrightness(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetBrightness");
        int value = ((Bundle) arg).getInt("mValue", VALUE_ERROR);
        if (value == VALUE_ERROR) {
            if (this.mGet.getSettingValue(Setting.KEY_BRIGHTNESS).equals(CameraConstants.TYPE_PREFERENCE_NOT_FOUND)) {
                CamLog.d(FaceDetector.TAG, "Need to check string whether it is convertable or not.");
                return;
            }
            value = Integer.parseInt(this.mGet.getSettingValue(Setting.KEY_BRIGHTNESS));
        }
        if (value < 0) {
            value = 0;
        }
        int valueForParameter = Common.scaleParameter(value, lgParameters.getParameters());
        CamLog.i(FaceDetector.TAG, "brightness value to parameter: " + valueForParameter);
        lgParameters.getParameters().setExposureCompensation(valueForParameter);
    }
}
