package com.lge.camera.command.setting;

import android.hardware.Camera.Parameters;
import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetZoom extends SettingCommand {
    public SetZoom(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        int mZoomValue = ((Bundle) arg).getInt("mValue", 0);
        if (checkMediator() && this.mGet.getCameraId() != 1) {
            if (mZoomValue == 0) {
                if (this.mGet.getSettingValue(Setting.KEY_ZOOM).equals(CameraConstants.TYPE_PREFERENCE_NOT_FOUND)) {
                    CamLog.d(FaceDetector.TAG, "Need to check string whether it is convertable or not.");
                    return;
                }
                mZoomValue = Integer.parseInt(this.mGet.getSettingValue(Setting.KEY_ZOOM));
                int zoomCursorMaxStep = this.mGet.getZoomCursorMaxStep();
                int max = lgParameters.getParameters().getMaxZoom();
                if (zoomCursorMaxStep == 90 && max > 0) {
                    mZoomValue = Math.round(((float) (mZoomValue * 90)) / ((float) max));
                }
            }
            if (mZoomValue < 0) {
                mZoomValue = 0;
            }
            int valueOfParameter = scaleParameter(mZoomValue, lgParameters.getParameters());
            CamLog.i(FaceDetector.TAG, "zoom value to parameter: " + valueOfParameter);
            lgParameters.getParameters().setZoom(valueOfParameter);
            if (this.mGet.getApplicationMode() == 1 && this.mGet.getAudiozoomStart()) {
                this.mGet.updateAudiozoom(true, valueOfParameter);
            }
            this.mGet.doCommand(Command.SET_SUPER_ZOOM, lgParameters);
        }
    }

    private int scaleParameter(int value, Parameters params) {
        float max = (float) params.getMaxZoom();
        if (max > 0.0f) {
            this.mGet.setZoomMaxValue(max);
        } else {
            max = this.mGet.getZoomMaxValue();
        }
        float compensationFactor = max / ((float) this.mGet.getZoomCursorMaxStep());
        CamLog.d(FaceDetector.TAG, "compensationFactor = " + compensationFactor + ", max = " + max + ", value = " + value);
        int scaledValue = Math.round(((float) value) * compensationFactor);
        if (((float) scaledValue) > max) {
            scaledValue = (int) max;
        }
        if (params.isZoomSupported()) {
            this.mGet.setZoomRatio((float) ((Integer) params.getZoomRatios().get(scaledValue)).intValue());
        }
        return scaledValue;
    }
}
