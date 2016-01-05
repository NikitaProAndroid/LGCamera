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

public class SetDualRecording extends SettingCommand {
    public SetDualRecording(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        if (this.mGet.getApplicationMode() != 0) {
            CamLog.d(FaceDetector.TAG, "SetDualRecordingMode");
            if (MultimediaProperties.isDualRecordingSupported()) {
                CamLog.i(FaceDetector.TAG, "#### set video stabilization false because of dual recording ");
                lgParameters.getParameters().set("video-stabilization", CameraConstants.ONEKEY_CONTROL_DISABLE_STRING);
                this.mGet.setSetting(Setting.KEY_VIDEO_STABILIZATION, CameraConstants.SMART_MODE_OFF);
                CamLog.i(FaceDetector.TAG, "#### set coloreffect none because of dual recording ");
                lgParameters.getParameters().setColorEffect(Setting.HELP_OTHER);
                if (!(CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT)) || Setting.HELP_OTHER.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT)))) {
                    this.mGet.setSetting(Setting.KEY_CAMERA_COLOREFFECT, Setting.HELP_OTHER);
                }
                lgParameters.getParameters().set(CameraConstants.PARAM_KEY_DUAL_RECORDER, 1);
                if (MultimediaProperties.isHighFramRateVideoSupported()) {
                    String[] fpsRange = SystemProperties.get("hw.camcorder.fpsrange", "15000,30000").split(",");
                    lgParameters.getParameters().set("video-hfr", CameraConstants.SMART_MODE_OFF);
                    lgParameters.getParameters().set("preview-format", "yuv420sp");
                    lgParameters.getParameters().setPreviewFpsRange(Integer.parseInt(fpsRange[0]), Integer.parseInt(fpsRange[1]));
                }
                CamLog.i(FaceDetector.TAG, "#### set gps off because of dual recording ");
                this.mGet.setSetting(Setting.KEY_CAMERA_TAG_LOCATION, CameraConstants.SMART_MODE_OFF);
                this.mGet.setRecordLocation(false);
                this.mGet.stopReceivingLocationUpdates();
                CamLog.i(FaceDetector.TAG, "#### set whiltebalance auto because of dual recording ");
                lgParameters.getParameters().setWhiteBalance(LGT_Limit.ISP_AUTOMODE_AUTO);
                this.mGet.setSetting(Setting.KEY_CAMERA_WHITEBALANCE, LGT_Limit.ISP_AUTOMODE_AUTO);
                CamLog.i(FaceDetector.TAG, "#### set exposure zero because of dual recording ");
                lgParameters.getParameters().setExposureCompensation(0);
                this.mGet.resetBrightnessController();
                if (this.mGet.getCameraId() == 0) {
                    CamLog.i(FaceDetector.TAG, "#### set zoom zero because of dual recording ");
                    lgParameters.getParameters().setZoom(0);
                    this.mGet.resetZoomController();
                }
                this.mGet.setPreferenceMenuEnable(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, false, false);
                this.mGet.getHandler().post(new Runnable() {
                    public void run() {
                        int mMenuIndex = SetDualRecording.this.mGet.getPreferenceGroup().findPreferenceIndex(Setting.KEY_DUAL_RECORDING);
                        if (SetDualRecording.this.mGet.isPreviewing()) {
                            int video_state = SetDualRecording.this.mGet.getVideoState();
                            if (video_state != 0) {
                                SetDualRecording.this.mGet.setSetting(Setting.KEY_DUAL_RECORDING, CameraConstants.SMART_MODE_OFF);
                                CamLog.e(FaceDetector.TAG, "setting rollback to off. video is not idle:" + video_state);
                                SetDualRecording.this.mGet.setQuickFunctionControllerMenuIcon(mMenuIndex, 0);
                                return;
                            }
                            CamLog.d(FaceDetector.TAG, "resetting Dual Recording Parameter in previewing!!!! ");
                            SetDualRecording.this.mGet.updateDualRecordingSelection();
                            if (!SetDualRecording.this.mGet.isPausing()) {
                                SetDualRecording.this.mGet.updateThumbnailButtonVisibility();
                                return;
                            }
                            return;
                        }
                        SetDualRecording.this.mGet.setSetting(Setting.KEY_DUAL_RECORDING, CameraConstants.SMART_MODE_OFF);
                        SetDualRecording.this.mGet.setQuickFunctionControllerMenuIcon(mMenuIndex, 0);
                        CamLog.e(FaceDetector.TAG, "setting rollback to off. no Previewing now");
                    }
                });
                return;
            }
            CamLog.d(FaceDetector.TAG, "Do not support dual recording");
        }
    }
}
