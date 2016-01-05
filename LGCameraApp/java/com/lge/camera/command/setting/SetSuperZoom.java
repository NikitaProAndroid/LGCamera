package com.lge.camera.command.setting;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetSuperZoom extends SettingCommand {
    public SetSuperZoom(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        execute((Object) this.mGet.getParameters(), null);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetSuperZoom starts");
        if (this.mGet.getApplicationMode() != 0 || this.mGet.getCameraMode() != 0) {
            CamLog.d(FaceDetector.TAG, "Super zoom : Not main camera mode. Return.");
        } else if (!lgParameters.getParameters().isZoomSupported()) {
            CamLog.d(FaceDetector.TAG, "Super zoom : Zoom's not supported. Return.");
        } else if (!FunctionProperties.isSuperZoomSupported()) {
            CamLog.d(FaceDetector.TAG, "Super zoom : Super zoom's not supported. Return.");
        } else if (!FunctionProperties.isZslSupported()) {
            CamLog.d(FaceDetector.TAG, "Super zoom : ZSL's not supported. Return.");
        } else if (!checkShotModeForSuperZoom() || !CameraConstants.SMART_MODE_OFF.equals(this.mGet.getSettingValue(Setting.KEY_FLASH)) || LGParameters.SCENE_MODE_NIGHT.equals(this.mGet.getSettingValue(Setting.KEY_SCENE_MODE))) {
            CamLog.d(FaceDetector.TAG, "Super zoom : Shot mode's wrong or flash's on or scene mode is 'night'.");
            if (CameraConstants.SMART_MODE_ON.equals(lgParameters.getParameters().get(CameraConstants.PARAMETER_SUPERZOOM))) {
                CamLog.d(FaceDetector.TAG, "Set super zoom off and return. Super zoom set to OFF.");
                lgParameters.getParameters().set(CameraConstants.PARAMETER_SUPERZOOM, CameraConstants.SMART_MODE_OFF);
            }
        } else if (this.mGet.isPreviewing() && CameraConstants.SMART_MODE_OFF.equals(lgParameters.getParameters().get("zsl"))) {
            CamLog.d(FaceDetector.TAG, "Super zoom : Preview started but zsl's off. Return");
        } else {
            float zoomRatio = (float) ((Integer) lgParameters.getParameters().getZoomRatios().get(lgParameters.getParameters().getZoom())).intValue();
            try {
                if (CameraConstants.SMART_MODE_OFF.equals(lgParameters.getParameters().get(CameraConstants.PARAMETER_SUPERZOOM))) {
                    if (zoomRatio < 200.0f) {
                        CamLog.d(FaceDetector.TAG, "Super zoom is off and zoom ratio is " + zoomRatio + ", smaller than 200. Return.");
                        return;
                    } else {
                        CamLog.d(FaceDetector.TAG, "Super zoom is off and zoom ratio is " + zoomRatio + ", larger than or equal with 200. Super zoom set to ON");
                        lgParameters.getParameters().set(CameraConstants.PARAMETER_SUPERZOOM, CameraConstants.SMART_MODE_ON);
                    }
                } else if (!CameraConstants.SMART_MODE_ON.equals(lgParameters.getParameters().get(CameraConstants.PARAMETER_SUPERZOOM))) {
                    CamLog.d(FaceDetector.TAG, "superzoom is unsupported parameter for this model. Zoom value = " + lgParameters.getParameters().getZoom());
                } else if (zoomRatio >= 200.0f) {
                    CamLog.d(FaceDetector.TAG, "Super zoom is on and zoom raio is " + zoomRatio + ", larger than or equal with 200. Return.");
                    return;
                } else {
                    CamLog.d(FaceDetector.TAG, "Super zoom is on and zoom ratio is " + zoomRatio + ", smaller than 200. Super zoom set to OFF.");
                    lgParameters.getParameters().set(CameraConstants.PARAMETER_SUPERZOOM, CameraConstants.SMART_MODE_OFF);
                }
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "Exception occured during setParameters()/getParameters()");
                e.printStackTrace();
            }
            CamLog.d(FaceDetector.TAG, "SetSuperZoom ends");
        }
    }

    private boolean checkShotModeForSuperZoom() {
        if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CONTINUOUS) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_HDR) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_TIMEMACHINE) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_REFOCUS) || this.mGet.isTimeMachineModeOn() || (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE)) && !ModelProperties.isRenesasISP())) {
            return false;
        }
        return true;
    }
}
