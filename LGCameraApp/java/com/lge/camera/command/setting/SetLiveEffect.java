package com.lge.camera.command.setting;

import android.os.SystemProperties;
import com.lge.camera.ControllerFunction;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;

public class SetLiveEffect extends SettingCommand {
    String currentResolution;

    public SetLiveEffect(ControllerFunction function) {
        super(function);
        this.currentResolution = null;
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        if (this.mGet.getApplicationMode() != 0 && this.mGet.isPreviewing()) {
            CamLog.d(FaceDetector.TAG, "SetLiveeffectMode");
            if (MultimediaProperties.isLiveEffectSupported()) {
                CamLog.i(FaceDetector.TAG, "#### set coloreffect none because of liveeffect ");
                lgParameters.getParameters().setColorEffect(Setting.HELP_OTHER);
                if (!(CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT)) || Setting.HELP_OTHER.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT)))) {
                    this.mGet.setSetting(Setting.KEY_CAMERA_COLOREFFECT, Setting.HELP_OTHER);
                }
                CamLog.i(FaceDetector.TAG, "#### set gps off because of liveeffect ");
                this.mGet.setSetting(Setting.KEY_CAMERA_TAG_LOCATION, CameraConstants.SMART_MODE_OFF);
                this.mGet.setRecordLocation(false);
                this.mGet.stopReceivingLocationUpdates();
                CamLog.i(FaceDetector.TAG, "#### set whitebalance auto because of liveeffect ");
                lgParameters.getParameters().setWhiteBalance(LGT_Limit.ISP_AUTOMODE_AUTO);
                this.mGet.setSetting(Setting.KEY_CAMERA_WHITEBALANCE, LGT_Limit.ISP_AUTOMODE_AUTO);
                CamLog.i(FaceDetector.TAG, "#### set exposure zero because of liveeffect ");
                lgParameters.getParameters().setExposureCompensation(0);
                this.mGet.resetBrightnessController();
                if (this.mGet.getCameraId() == 0) {
                    CamLog.i(FaceDetector.TAG, "#### set zoom zero because of liveeffect ");
                    lgParameters.getParameters().setZoom(0);
                    this.mGet.resetZoomController();
                }
                CamLog.i(FaceDetector.TAG, "#### set video_stabilization off because of liveeffect ");
                this.mGet.setSetting(Setting.KEY_VIDEO_STABILIZATION, CameraConstants.SMART_MODE_OFF);
                lgParameters.getParameters().set("video-stabilization", CameraConstants.ONEKEY_CONTROL_DISABLE_STRING);
                CamLog.i(FaceDetector.TAG, "#### set video_wdr off because of liveeffect ");
                if (FunctionProperties.isHDRRecordingNameUsed()) {
                    lgParameters.getParameters().set("video-hdr", CameraConstants.SMART_MODE_OFF);
                } else {
                    lgParameters.getParameters().set("video-wdr", CameraConstants.SMART_MODE_OFF);
                }
                lgParameters.getParameters().set(CameraConstants.PARAM_KEY_DUAL_RECORDER, 0);
                if (MultimediaProperties.isHighFramRateVideoSupported()) {
                    String[] fpsRange = SystemProperties.get("hw.camcorder.fpsrange", "15000,30000").split(",");
                    lgParameters.getParameters().set("video-hfr", CameraConstants.SMART_MODE_OFF);
                    lgParameters.getParameters().set("preview-format", "yuv420sp");
                    lgParameters.getParameters().setPreviewFpsRange(Integer.parseInt(fpsRange[0]), Integer.parseInt(fpsRange[1]));
                }
                this.mGet.getHandler().post(new Runnable() {
                    public void run() {
                        int mMenuIndex = SetLiveEffect.this.mGet.getPreferenceGroup().findPreferenceIndex(Setting.KEY_LIVE_EFFECT);
                        if (SetLiveEffect.this.mGet.isPreviewing()) {
                            int video_state = SetLiveEffect.this.mGet.getVideoState();
                            if (video_state != 0) {
                                SetLiveEffect.this.mGet.setSetting(Setting.KEY_LIVE_EFFECT, CameraConstants.SMART_MODE_OFF);
                                SetLiveEffect.this.mGet.setLiveEffect(CameraConstants.SMART_MODE_OFF);
                                CamLog.d(FaceDetector.TAG, "setting rollback to off. video is not idle:" + video_state);
                                if (SetLiveEffect.this.mGet.isQuickFunctionList(mMenuIndex)) {
                                    SetLiveEffect.this.mGet.setQuickFunctionControllerMenuIcon(mMenuIndex, 0);
                                    return;
                                }
                                return;
                            }
                            CamLog.d(FaceDetector.TAG, "resetting EffectParameter in previewing!!!! ");
                            SetLiveEffect.this.mGet.updateEffectSelection();
                            if (CameraConstants.SMART_MODE_OFF.equals(SetLiveEffect.this.mGet.getSettingValue(Setting.KEY_LIVE_EFFECT))) {
                                SetLiveEffect.this.mGet.doCommandUi(Command.HIDE_LIVEEFFECT_SUBMENU_DRAWER);
                            }
                            if (MultimediaProperties.isSmartZoomSupported()) {
                                SetLiveEffect.this.mGet.hideSmartZoomFocusView();
                            }
                            if (!SetLiveEffect.this.mGet.isPausing()) {
                                SetLiveEffect.this.mGet.updateThumbnailButtonVisibility();
                                return;
                            }
                            return;
                        }
                        SetLiveEffect.this.mGet.setSetting(Setting.KEY_LIVE_EFFECT, CameraConstants.SMART_MODE_OFF);
                        SetLiveEffect.this.mGet.setLiveEffect(CameraConstants.SMART_MODE_OFF);
                        if (SetLiveEffect.this.mGet.isQuickFunctionList(mMenuIndex)) {
                            SetLiveEffect.this.mGet.setQuickFunctionControllerMenuIcon(mMenuIndex, 0);
                        }
                        CamLog.d(FaceDetector.TAG, "setting rollback to off. no Previewing now");
                    }
                });
                return;
            }
            CamLog.d(FaceDetector.TAG, "Dont support liveeffect");
        }
    }
}
