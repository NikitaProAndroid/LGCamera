package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetDateStamp extends SettingCommand {
    private boolean mDateStampValueOn;

    public SetDateStamp(ControllerFunction function) {
        super(function);
        this.mDateStampValueOn = true;
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        if (!checkMediator()) {
            return;
        }
        if (FunctionProperties.isDateStampSupported()) {
            String value = this.mGet.getSettingValue(Setting.KEY_DATE_STAMP);
            CamLog.d(FaceDetector.TAG, "## SetDateStampMode : " + value);
            if (this.mGet.getApplicationMode() == 0) {
                if (value.equals(CameraConstants.SMART_MODE_ON)) {
                    this.mDateStampValueOn = true;
                } else {
                    this.mDateStampValueOn = true;
                }
                if (lgParameters != null) {
                    try {
                        lgParameters.getParameters().set("time_stamp", value);
                    } catch (RuntimeException e) {
                        CamLog.i(FaceDetector.TAG, "Time stamp SetParam-RuntimeException : ", e);
                    }
                }
                this.mGet.setSetting(Setting.KEY_DATE_STAMP, value);
                return;
            }
            return;
        }
        CamLog.i(FaceDetector.TAG, "SetDateStampMode : model is not supported.");
    }

    protected void onExecuteAlone() {
        if (FunctionProperties.isDateStampSupported()) {
            CamLog.d(FaceDetector.TAG, "SetDateStampMode - Show Toast Message : SetDateStampMode is = ");
            if (this.mDateStampValueOn) {
                this.mGet.quickFunctionControllerInitMenu();
                this.mGet.quickFunctionControllerRefresh(true);
            } else {
                this.mGet.quickFunctionControllerInitMenu();
                this.mGet.quickFunctionControllerRefresh(true);
            }
            return;
        }
        CamLog.i(FaceDetector.TAG, "SetDateStampMode : model is not supported.");
    }
}
