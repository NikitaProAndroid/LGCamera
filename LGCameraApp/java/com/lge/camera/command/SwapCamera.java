package com.lge.camera.command;

import android.util.Log;
import android.view.MotionEvent;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CameraHolder;
import com.lge.camera.util.Common;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.camera.util.TelephonyUtil;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;

public class SwapCamera extends Command {
    public SwapCamera(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        if (checkSwap()) {
            Log.d(FaceDetector.TAG, "TIME CHECK : Swap camera [START] - execute()");
            try {
                this.mGet.perfLockAcquire();
            } catch (NoSuchMethodError e) {
            }
            this.mGet.initSettingMenu();
            this.mGet.enableInput(false);
            this.mGet.setIsSwapCameraProcessing(true);
            this.mGet.clearSubMenu();
            this.mGet.setQuickFunctionAllMenuEnabled(false, false);
            this.mGet.setQuickButtonMenuEnable(false, false);
            this.mGet.setModeMenuVisibility(4);
            setCamcorderSettingForSwap();
            setCameraSettingForSwap();
            if (Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                this.mGet.initFaceDetectInfo();
            }
            if (this.mGet.getApplicationMode() == 0 && this.mGet.getCameraMode() == 0 && CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE))) {
                this.mGet.setSmartCameraMode(null, false);
            }
            doSwapAction();
            if (this.mGet.getApplicationMode() == 0) {
                if (this.mGet.getCameraMode() == 0) {
                    if (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE))) {
                        this.mGet.setQuickFunctionControllerVisible(false);
                        this.mGet.setQuickButtonMode(true);
                        this.mGet.setQuickButtonMenuEnable(true, false);
                    } else {
                        this.mGet.setQuickButtonMode(false);
                        this.mGet.setQuickFunctionControllerVisible(true);
                    }
                    this.mGet.setBackgroundColorBlack();
                    Common.setBacklightToSystemSetting(this.mGet.getActivity());
                } else {
                    this.mGet.setQuickButtonMode(false);
                    this.mGet.setQuickFunctionControllerVisible(true);
                    this.mGet.setTimemachineHasPictures(false);
                    this.mGet.setRefocusShotHasPictures(false);
                    this.mGet.setRefocusShotPreviewGuideVisibility(false);
                    if (this.mGet.checkSettingValue(Setting.KEY_LIGHT, CameraConstants.SMART_MODE_ON)) {
                        this.mGet.setBackgroundColorWhite();
                        Common.setBacklightToMax(this.mGet.getActivity());
                    }
                    if (FunctionProperties.isSupportedGestureShot() && CameraConstants.TYPE_SHOTMODE_NORMAL.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
                        this.mGet.showHelpGuidePopup(Setting.HELP_GESTURESHOT, DialogCreater.DIALOG_ID_HELP_GESTURESHOT, true);
                    }
                }
                this.mGet.showIndicatorController();
                this.mGet.updateSizeIndicator();
                this.mGet.updateSceneIndicator(false, null);
                this.mGet.updateFlashIndicator(false, null);
                this.mGet.clearFocusState();
                this.mGet.updateFocusIndicator();
            } else {
                this.mGet.setQuickButtonMode(false);
                this.mGet.showIndicatorController();
                this.mGet.recordingControllerShow();
                this.mGet.updateFlashIndicator(false, null);
                if (this.mGet.getCameraMode() == 1) {
                    if (this.mGet.checkSettingValue(Setting.KEY_LIGHT, CameraConstants.SMART_MODE_ON)) {
                        this.mGet.setBackgroundColorWhite();
                    } else {
                        this.mGet.setBackgroundColorBlack();
                    }
                }
            }
            this.mGet.setQuickButtonMenuEnable(false, false);
            this.mGet.refreshZoomController();
            this.mGet.refreshBrightnessController();
            this.mGet.refreshBeautyshotController();
            this.mGet.refreshManualFocusController();
            setFocusForSwap();
            if (this.mGet.getApplicationMode() == 0) {
                if (TelephonyUtil.phoneInCall(this.mGet.getApplicationContext())) {
                    this.mGet.setPreferenceMenuEnable(Setting.KEY_VOICESHUTTER, false, false);
                }
                this.mGet.setPreviousShotModeString(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE));
            }
            this.mGet.checkStorage(false);
            this.mGet.updateStorageIndicator();
            this.mGet.setIsSwapCameraProcessing(false);
            this.mGet.resetSwitcherLever();
            int delay = CameraConstants.TRANS_INTERVAL;
            if ((this.mGet.getApplicationMode() == 1 && MultimediaProperties.isLiveEffectSupported() && this.mGet.isEffectsCamcorderActive()) || this.mGet.isDualCameraActive()) {
                delay = LGT_Limit.IMAGE_SIZE_WALLPAPER_HEIGHT;
            }
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    SwapCamera.this.mGet.removePostRunnable(this);
                    SwapCamera.this.mGet.setQuickFunctionMenuForcedDisable(false);
                    SwapCamera.this.mGet.setQuickButtonForcedDisable(false);
                    SwapCamera.this.mGet.quickFunctionControllerRefresh(true);
                    if (!(SwapCamera.this.mGet.getApplicationMode() == 1 && SwapCamera.this.mGet.isLiveEffectActive() && SwapCamera.this.mGet.isLiveEffectDrawerOpened())) {
                        SwapCamera.this.mGet.setButtonRemainRefresh();
                    }
                    SwapCamera.this.mGet.updateModeMenuIndicator();
                }
            }, (long) delay);
            Log.d(FaceDetector.TAG, "TIME CHECK : Swap camera [END] - execute()");
            CamLog.d(FaceDetector.TAG, "SwapCamera EXECUTE -end");
        }
    }

    protected boolean checkSwap() {
        CamLog.d(FaceDetector.TAG, "SwapCamera");
        this.mGet.removeScheduledCommand(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION);
        if (!preExecuteSwapCamera()) {
            return false;
        }
        CamLog.d(FaceDetector.TAG, "SwapCamera EXECUTE -start !!!, current_app_mode:" + this.mGet.getApplicationMode() + " currentCameraMode:" + this.mGet.getCameraMode());
        return true;
    }

    protected void doSwapAction() {
        if (FunctionProperties.isFaceDetectionAuto()) {
            this.mGet.stopFaceDetection();
        }
        if (this.mGet.getCameraMode() == 0) {
            this.mGet.setCameraMode(1);
            if (Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                this.mGet.stopFaceDetection();
            }
        } else {
            this.mGet.setCameraMode(0);
        }
        this.mGet.applyCameraChange();
        this.mGet.initQuickFunctionEnabled();
        int numberOfCameras = CameraHolder.instance().getNumberOfCameras();
        if (numberOfCameras <= 0) {
            CamLog.w(FaceDetector.TAG, "numberOfCameras errors : " + numberOfCameras);
            numberOfCameras = 2;
        }
        if (ModelProperties.is3dSupportedModel()) {
            if (this.mGet.getCameraMode() != 0) {
                this.mGet.switchCameraId(1);
            } else if (this.mGet.getCameraDimension() == 0) {
                this.mGet.switchCameraId(0);
            } else {
                this.mGet.switchCameraId(0);
            }
            this.mGet.set3DSwitchImage();
            return;
        }
        this.mGet.switchCameraId((this.mGet.getCameraId() + 1) % numberOfCameras);
    }

    protected void setFocusForSwap() {
        if (this.mGet.getCameraMode() == 1) {
            this.mGet.hideFocus();
        } else if (CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
            this.mGet.hideFocus();
            this.mGet.showManualFocusController(true);
            this.mGet.setSubMenuMode(25);
        } else if (this.mGet.isCafSupported()) {
            this.mGet.hideFocus();
        } else {
            this.mGet.showFocus();
        }
    }

    protected void setCamcorderSettingForSwap() {
        if (this.mGet.getApplicationMode() == 1) {
            if (this.mGet.isLiveEffectActive()) {
                int effectIndex = this.mGet.getLiveEffectList().indexOf(this.mGet.getLiveEffect());
                if (this.mGet.getCameraMode() == 0) {
                    SharedPreferenceUtil.saveLiveEffectFaceIndex(this.mGet.getApplicationContext(), effectIndex);
                } else {
                    SharedPreferenceUtil.saveFrontLiveEffectFaceIndex(this.mGet.getApplicationContext(), effectIndex);
                }
            } else if (this.mGet.isDualRecordingActive()) {
                int dualRecordPipIndex = this.mGet.getCurrentPIPMask();
                if (this.mGet.getCameraMode() == 0) {
                    SharedPreferenceUtil.saveDualCamcorderPIPIndex(this.mGet.getApplicationContext(), dualRecordPipIndex);
                } else {
                    SharedPreferenceUtil.saveFrontDualCamcorderPIPIndex(this.mGet.getApplicationContext(), dualRecordPipIndex);
                }
            } else if (this.mGet.isSmartZoomRecordingActive()) {
                int smartZoomPipIndex = this.mGet.getCurrentPIPMask();
                if (this.mGet.getCameraMode() == 0) {
                    SharedPreferenceUtil.saveSmartZoomPIPIndex(this.mGet.getApplicationContext(), smartZoomPipIndex);
                } else {
                    SharedPreferenceUtil.saveFrontSmartZoomPIPIndex(this.mGet.getApplicationContext(), smartZoomPipIndex);
                }
                if (this.mGet.isObjectTrackingEnabledForSmartZoom()) {
                    this.mGet.disableObjectTrackingForSmartZoom();
                }
            } else if (this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE).equals(CameraConstants.TYPE_RECORDMODE_WDR)) {
                this.mGet.setSetting(Setting.KEY_VIDEO_RECORD_MODE, CameraConstants.TYPE_RECORDMODE_NORMAL);
            }
            this.mGet.storePreviousResolution(this.mGet.getPreviousResolution());
            this.mGet.setPrevResolutionWithStoredValue();
        }
    }

    protected void setCameraSettingForSwap() {
        if (this.mGet.getApplicationMode() == 0 && this.mGet.isDualCameraActive()) {
            int dualCameraPipIndex = this.mGet.getCurrentPIPMask();
            if (this.mGet.getCameraMode() == 0) {
                SharedPreferenceUtil.saveDualCameraPIPIndex(this.mGet.getApplicationContext(), dualCameraPipIndex);
            } else if (this.mGet.getCameraMode() == 1) {
                SharedPreferenceUtil.saveFrontDualCameraPIPIndex(this.mGet.getApplicationContext(), dualCameraPipIndex);
            }
        }
    }

    protected boolean preExecuteSwapCamera() {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                SwapCamera.this.mGet.removePostRunnable(this);
                SwapCamera.this.mGet.getActivity().dispatchTouchEvent(MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0));
            }
        });
        if (this.mGet.getVideoState() != 0) {
            CamLog.d(FaceDetector.TAG, "swapCamera return, not VIDEO_STATE_IDLE");
            return false;
        } else if (this.mGet.getInCaptureProgress()) {
            CamLog.d(FaceDetector.TAG, "swapCamera return, capturing now");
            return false;
        } else if (this.mGet.isPreviewOnGoing()) {
            CamLog.d(FaceDetector.TAG, "swapCamera return, not PreviewOnGoing");
            return false;
        } else {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    SwapCamera.this.mGet.removePostRunnable(this);
                    if (SwapCamera.this.mGet.isSettingControllerVisible()) {
                        SwapCamera.this.mGet.removeSettingView();
                    }
                }
            });
            return true;
        }
    }

    public void updateFocusIndicator() {
        if (this.mGet.isIndicatorControllerInitialized()) {
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    if (SwapCamera.this.checkMediator()) {
                        SwapCamera.this.mGet.removePostRunnable(this);
                        if (SwapCamera.this.mGet.getApplicationMode() == 0) {
                            SwapCamera.this.mGet.updateFocusIndicator();
                        }
                    }
                }
            });
        }
    }
}
