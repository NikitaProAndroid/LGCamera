package com.lge.camera.command.setting;

import android.os.SystemProperties;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;

public class SetSmartZoomRecording extends SettingCommand {
    public SetSmartZoomRecording(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        if (this.mGet.getApplicationMode() != 0 && this.mGet.isPreviewing()) {
            CamLog.d(FaceDetector.TAG, "SetSmartZoomMode");
            if (MultimediaProperties.isSmartZoomSupported()) {
                CamLog.i(FaceDetector.TAG, "#### set video stabilization false because of smart zoom");
                lgParameters.getParameters().set("video-stabilization", CameraConstants.ONEKEY_CONTROL_DISABLE_STRING);
                this.mGet.setSetting(Setting.KEY_VIDEO_STABILIZATION, CameraConstants.SMART_MODE_OFF);
                CamLog.i(FaceDetector.TAG, "#### set coloreffect none because of smart zoom");
                lgParameters.getParameters().setColorEffect(Setting.HELP_OTHER);
                if (!(CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT)) || Setting.HELP_OTHER.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT)))) {
                    this.mGet.setSetting(Setting.KEY_CAMERA_COLOREFFECT, Setting.HELP_OTHER);
                }
                lgParameters.getParameters().set(CameraConstants.PARAM_KEY_DUAL_RECORDER, 0);
                if (MultimediaProperties.isHighFramRateVideoSupported()) {
                    String[] fpsRange = SystemProperties.get("hw.camcorder.fpsrange", "15000,30000").split(",");
                    lgParameters.getParameters().set("video-hfr", CameraConstants.SMART_MODE_OFF);
                    lgParameters.getParameters().set("preview-format", "yuv420sp");
                    lgParameters.getParameters().setPreviewFpsRange(Integer.parseInt(fpsRange[0]), Integer.parseInt(fpsRange[1]));
                }
                CamLog.i(FaceDetector.TAG, "#### set gps off because of smart zoom");
                this.mGet.setSetting(Setting.KEY_CAMERA_TAG_LOCATION, CameraConstants.SMART_MODE_OFF);
                this.mGet.setRecordLocation(false);
                this.mGet.stopReceivingLocationUpdates();
                CamLog.i(FaceDetector.TAG, "#### set whiltebalance auto because of smart zoom");
                lgParameters.getParameters().setWhiteBalance(LGT_Limit.ISP_AUTOMODE_AUTO);
                this.mGet.setSetting(Setting.KEY_CAMERA_WHITEBALANCE, LGT_Limit.ISP_AUTOMODE_AUTO);
                CamLog.i(FaceDetector.TAG, "#### set exposure zero because of smart zoom");
                lgParameters.getParameters().setExposureCompensation(0);
                this.mGet.resetBrightnessController();
                if (this.mGet.getCameraId() == 0) {
                    CamLog.i(FaceDetector.TAG, "#### set zoom zero because of smart zoom");
                    lgParameters.getParameters().setZoom(0);
                    this.mGet.resetZoomController();
                }
                this.mGet.getHandler().post(new Runnable() {
                    public void run() {
                        int mMenuIndex = SetSmartZoomRecording.this.mGet.getPreferenceGroup().findPreferenceIndex(Setting.KEY_SMART_ZOOM_RECORDING);
                        if (SetSmartZoomRecording.this.mGet.isPreviewing()) {
                            int video_state = SetSmartZoomRecording.this.mGet.getVideoState();
                            if (video_state != 0) {
                                SetSmartZoomRecording.this.mGet.setSetting(Setting.KEY_SMART_ZOOM_RECORDING, CameraConstants.SMART_MODE_OFF);
                                CamLog.e(FaceDetector.TAG, "setting rollback to off. video is not idle:" + video_state);
                                SetSmartZoomRecording.this.mGet.setQuickFunctionControllerMenuIcon(mMenuIndex, 0);
                                return;
                            }
                            CamLog.d(FaceDetector.TAG, "resetting Smart Zoom Recording Parameter in previewing!!!! ");
                            SetSmartZoomRecording.this.mGet.updateSmartZoomRecordingSelection();
                            if (!SetSmartZoomRecording.this.mGet.isPausing()) {
                                SetSmartZoomRecording.this.mGet.updateThumbnailButtonVisibility();
                                return;
                            }
                            return;
                        }
                        SetSmartZoomRecording.this.mGet.setSetting(Setting.KEY_SMART_ZOOM_RECORDING, CameraConstants.SMART_MODE_OFF);
                        SetSmartZoomRecording.this.mGet.setQuickFunctionControllerMenuIcon(mMenuIndex, 0);
                        CamLog.e(FaceDetector.TAG, "setting rollback to off. no Previewing now");
                    }
                });
                return;
            }
            CamLog.d(FaceDetector.TAG, "Do not support smart zoom recording mode");
        }
    }
}
