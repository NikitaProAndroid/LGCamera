package com.lge.camera.command.setting;

import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.SystemProperties;
import android.view.View;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.MmsProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Util;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import com.lge.olaworks.library.FaceDetector;

public class SetVideoImageSize extends SettingCommand {
    private static final String FHD_RESOLUTION = "1920x1080";
    private static final String HD_RESOLUTION = "1280x720";
    private static final int W10M_HEIGHT = 2340;
    private static final int W10M_WIDTH = 4160;

    public SetVideoImageSize(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public boolean checkEnableLightFrame() {
        if (this.mGet.getCameraId() == 1 && this.mGet.checkSettingValue(Setting.KEY_VIDEO_RECORD_MODE, CameraConstants.TYPE_RECORDMODE_NORMAL)) {
            return true;
        }
        return false;
    }

    public void execute(final LGParameters lgParameters, Object arg) {
        CamLog.i(FaceDetector.TAG, "SetVideoImageSize-start");
        showFocusAndUpdateSizeIndicator();
        String sizeOnDeviceString = this.mGet.getPreviewSizeOnDevice();
        String sizeOnScreenString = this.mGet.getPreviewSizeOnScreen();
        if ("".equals(sizeOnScreenString)) {
            this.mGet.setSetting(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, this.mGet.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE).getDefaultValue());
            sizeOnDeviceString = this.mGet.getPreviewSizeOnDevice();
            sizeOnScreenString = this.mGet.getPreviewSizeOnScreen();
        }
        if (sizeOnDeviceString == null || sizeOnScreenString == null) {
            boolean z;
            String str = FaceDetector.TAG;
            StringBuilder append = new StringBuilder().append("error! sizeOnDeviceString or sizeOnScreenString is null:");
            if (sizeOnDeviceString == null) {
                z = true;
            } else {
                z = false;
            }
            CamLog.d(str, append.append(z).toString());
            return;
        }
        Size oldSizeOnDevice = lgParameters.getParameters().getPreviewSize();
        if (oldSizeOnDevice == null) {
            CamLog.d(FaceDetector.TAG, "oldSizeOnDevice is Null");
            return;
        }
        int[] previewSizeOnDevice = Util.SizeString2WidthHeight(sizeOnDeviceString);
        int[] previewSizeOnScreen = Util.SizeString2WidthHeight(sizeOnScreenString);
        int[] oldPreviewSizeOnScreen = new int[2];
        View preview = this.mGet.findViewById(R.id.preview_holder_surface);
        if (this.mGet.isConfigureLandscape()) {
            oldPreviewSizeOnScreen[0] = preview.getWidth();
            oldPreviewSizeOnScreen[1] = preview.getHeight();
        } else {
            oldPreviewSizeOnScreen[0] = preview.getHeight();
            oldPreviewSizeOnScreen[1] = preview.getWidth();
        }
        int[] oldVideoSize = new int[]{0, 0};
        if (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_LIGHT)) && checkEnableLightFrame()) {
            previewSizeOnScreen[0] = previewSizeOnScreen[0] / 2;
            previewSizeOnScreen[1] = previewSizeOnScreen[1] / 2;
            CamLog.d(FaceDetector.TAG, "MINA previewSize x : " + previewSizeOnScreen[0] + "previewSize y :" + previewSizeOnScreen[1]);
        }
        Float oldRatio = Float.valueOf(((float) oldPreviewSizeOnScreen[0]) / ((float) oldPreviewSizeOnScreen[1]));
        Float newRatio = Float.valueOf(((float) previewSizeOnScreen[0]) / ((float) previewSizeOnScreen[1]));
        setVideoStabilizationMenu(previewSizeOnDevice, lgParameters.getParameters());
        setPreviewFpsRange(lgParameters.getParameters(), previewSizeOnDevice);
        oldVideoSize = setVideoPreviewSize(lgParameters.getParameters(), previewSizeOnDevice, previewSizeOnScreen, oldVideoSize);
        CamLog.d(FaceDetector.TAG, "### set video-size " + sizeOnDeviceString);
        lgParameters.getParameters().set("video-size", sizeOnDeviceString);
        if (!setParameterForOMAP4(lgParameters.getParameters(), previewSizeOnDevice) && lgParameters.getParameters().isVideoSnapshotSupported() && FunctionProperties.isAvailableLiveShot()) {
            if (isSupportW10MSnapshot(sizeOnDeviceString)) {
                lgParameters.getParameters().setPictureSize(W10M_WIDTH, W10M_HEIGHT);
            } else {
                lgParameters.getParameters().setPictureSize(previewSizeOnDevice[0], previewSizeOnDevice[1]);
            }
        }
        lgParameters.getParameters().setRecordingHint(true);
        this.mGet.changePreviewModeOnUiThread(previewSizeOnScreen[0], previewSizeOnScreen[1]);
        if (this.mGet.isPreviewing() && !this.mGet.isLiveEffectActive()) {
            if (oldPreviewSizeOnScreen[0] == previewSizeOnScreen[0] && oldPreviewSizeOnScreen[1] == previewSizeOnScreen[1] && (oldSizeOnDevice.width != previewSizeOnDevice[0] || oldSizeOnDevice.height != previewSizeOnDevice[1])) {
                CamLog.d(FaceDetector.TAG, "Force restart preview. Preview size on device changed but view layout is same. ");
                this.mGet.getHandler().post(new Runnable() {
                    public void run() {
                        if (SetVideoImageSize.this.mGet.isDualRecordingActive()) {
                            SetVideoImageSize.this.mGet.startPreviewEffect();
                            return;
                        }
                        if (SetVideoImageSize.this.mGet.checkSlowMotionMode()) {
                            lgParameters.getParameters().setZoom(0);
                            SetVideoImageSize.this.mGet.resetZoomController();
                        }
                        SetVideoImageSize.this.mGet.restartPreview(lgParameters, false);
                    }
                });
            } else if (ModelProperties.isQCTChipset() && ModelProperties.isHDmodel() && oldRatio.compareTo(newRatio) == 0 && oldVideoSize[0] != previewSizeOnDevice[0]) {
                CamLog.d(FaceDetector.TAG, "Force restart preview. QCT 8k & full hd model have some limitation because of frame drop" + oldVideoSize[0] + "!=" + previewSizeOnDevice[0]);
                this.mGet.getHandler().post(new Runnable() {
                    public void run() {
                        SetVideoImageSize.this.mGet.restartPreview(lgParameters, false);
                    }
                });
            } else if (this.mGet.checkSlowMotionMode()) {
                lgParameters.getParameters().setZoom(0);
                this.mGet.resetZoomController();
                this.mGet.getHandler().post(new Runnable() {
                    public void run() {
                        SetVideoImageSize.this.mGet.restartPreview(lgParameters, false);
                    }
                });
            }
        }
        showMMSRequestSizeLimitPopup(sizeOnDeviceString);
        CamLog.i(FaceDetector.TAG, "SetVideoImageSize-end");
    }

    private boolean isSupportW10MSnapshot(String sizeOnDeviceString) {
        if (this.mGet.getCameraId() == 0 && ModelProperties.getProjectCode() == 23 && (HD_RESOLUTION.equals(sizeOnDeviceString) || FHD_RESOLUTION.equals(sizeOnDeviceString))) {
            return true;
        }
        return false;
    }

    private void changeVideoPreviewSize(Parameters parameters, int[] previewSizeOnDevice, int[] previewSizeOnScreen) {
        if (previewSizeOnDevice[0] > CameraConstants.LCD_SIZE_WIDTH && !ModelProperties.isLDPImodel() && !"3840x2160".equalsIgnoreCase(this.mGet.getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE))) {
            parameters.setPreviewSize(previewSizeOnScreen[0], previewSizeOnScreen[1]);
            CamLog.i(FaceDetector.TAG, "re-set Preview size :" + previewSizeOnScreen[0] + "x" + previewSizeOnScreen[1]);
        }
    }

    private int[] setVideoPreviewSize(Parameters parameters, int[] previewSizeOnDevice, int[] previewSizeOnScreen, int[] oldVideoSize) {
        CamLog.i(FaceDetector.TAG, "setPreviewSize :" + previewSizeOnDevice[0] + "x" + previewSizeOnDevice[1]);
        parameters.setPreviewSize(previewSizeOnDevice[0], previewSizeOnDevice[1]);
        if (!ModelProperties.isQCTChipset() && !ModelProperties.isNVIDIAChipset()) {
            return oldVideoSize;
        }
        changeVideoPreviewSize(parameters, previewSizeOnDevice, previewSizeOnScreen);
        String sOldVideoSize = parameters.get("video-size");
        if (sOldVideoSize == null || sOldVideoSize.equals("")) {
            return oldVideoSize;
        }
        return Util.SizeString2WidthHeight(sOldVideoSize);
    }

    private void showMMSRequestSizeLimitPopup(String sizeOnDeviceString) {
        boolean isMMS = MmsProperties.isAvailableMmsResolution(this.mGet.getContentResolver(), sizeOnDeviceString);
        this.mGet.doCommand(Command.SELECT_DURATION, Boolean.valueOf(isMMS));
        int mCarrierCode = ModelProperties.getCarrierCode();
        if (mCarrierCode != 5 && mCarrierCode != 21) {
            return;
        }
        if (this.mGet.isMMSIntent() || isMMS) {
            this.mGet.showRequestedSizeLimit();
        }
    }

    private boolean setParameterForOMAP4(Parameters parameters, int[] previewSizeOnDevice) {
        if (!ModelProperties.isOMAP4Chipset()) {
            return false;
        }
        if (FunctionProperties.isAvailableLiveShot()) {
            if (parameters.isVideoSnapshotSupported()) {
                parameters.setPictureSize(previewSizeOnDevice[0], previewSizeOnDevice[1]);
            }
            parameters.set(CameraConstants.HDR_MODE, "0");
        }
        if (!ModelProperties.isOMAP4Chipset()) {
            return true;
        }
        parameters.set("mode", "video-mode");
        CamLog.d(FaceDetector.TAG, "set mode: video-mode");
        return true;
    }

    private void showFocusAndUpdateSizeIndicator() {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                SetVideoImageSize.this.mGet.removePostRunnable(this);
                SetVideoImageSize.this.mGet.updateSizeIndicator();
                if (SetVideoImageSize.this.mGet.getSubMenuMode() != 5 && SetVideoImageSize.this.mGet.getSubMenuMode() != 16) {
                    SetVideoImageSize.this.mGet.showFocus();
                }
            }
        });
    }

    private void setPreviewFormat(Parameters parameters) {
        if ("3840x2160".equalsIgnoreCase(this.mGet.getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE))) {
            parameters.set("preview-format", "nv12-venus");
        } else {
            parameters.set("preview-format", "yuv420sp");
        }
    }

    private void setPreviewFpsRange(Parameters parameters, int[] previewSizeOnDevice) {
        if (!this.mGet.isEffectsCamcorderActive()) {
            if (this.mGet.isMMSIntent() || (!this.mGet.isAttachIntent() && this.mGet.isAttachMode())) {
                if (MultimediaProperties.isHighFramRateVideoSupported()) {
                    parameters.set("video-hfr", CameraConstants.SMART_MODE_OFF);
                    parameters.set("preview-format", "yuv420sp");
                }
                if (ModelProperties.isOMAP4Chipset()) {
                    parameters.setPreviewFpsRange(MultimediaProperties.VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX, MultimediaProperties.VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX);
                    return;
                } else {
                    parameters.setPreviewFpsRange(MultimediaProperties.VALUE_VIDEO_FRAMERATE_MMS_RANGE, MultimediaProperties.VALUE_VIDEO_FRAMERATE_MMS_RANGE);
                    return;
                }
            }
            String[] fpsRange = SystemProperties.get("hw.camcorder.fpsrange", "15000,30000").split(",");
            String sVideoFps = CameraConstants.SMART_MODE_OFF;
            ListPreference pref = this.mGet.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            if (pref != null) {
                sVideoFps = pref.getExtraInfo3();
            }
            if (MultimediaProperties.isHighFramRateVideoSupported() && this.mGet.getCameraId() == 0 && CameraConstants.TYPE_RECORDMODE_NORMAL.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE)) && !CameraConstants.SMART_MODE_OFF.equals(sVideoFps)) {
                parameters.set("video-hfr", sVideoFps);
                int iVideoFps = Integer.parseInt(sVideoFps) * PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME;
                if (this.mGet.checkSlowMotionMode()) {
                    parameters.set("preview-format", "yuv420sp");
                    CamLog.d(FaceDetector.TAG, "preview-format set to yuv420sp");
                    parameters.setPreviewFpsRange(iVideoFps, iVideoFps);
                    return;
                }
                parameters.set("preview-format", "nv12-venus");
                CamLog.d(FaceDetector.TAG, "preview-format set to nv12-venus");
                parameters.setPreviewFpsRange(Integer.parseInt(fpsRange[0]), iVideoFps);
                return;
            }
            if (MultimediaProperties.isHighFramRateVideoSupported()) {
                parameters.set("video-hfr", CameraConstants.SMART_MODE_OFF);
            }
            setPreviewFormat(parameters);
            CamLog.d(FaceDetector.TAG, "Min FPS is set to " + fpsRange[0] + " Max FPS is set to " + fpsRange[1]);
            parameters.setPreviewFpsRange(Integer.parseInt(fpsRange[0]), Integer.parseInt(fpsRange[1]));
        }
    }

    private void setVideoStabilizationMenu(int[] previewSizeOnDevice, Parameters parameters) {
        if (FunctionProperties.isVideoStabilizationSupported()) {
            if (previewSizeOnDevice[0] < 1280 || "1920x1080@60".equals(this.mGet.getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE)) || "3840x2160".equals(this.mGet.getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE))) {
                if (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_STABILIZATION))) {
                    this.mGet.setSetting(Setting.KEY_VIDEO_STABILIZATION, CameraConstants.SMART_MODE_OFF);
                    parameters.set("video-stabilization", CameraConstants.ONEKEY_CONTROL_DISABLE_STRING);
                }
                this.mGet.setPreferenceMenuEnable(Setting.KEY_VIDEO_STABILIZATION, false, false);
            } else if (!this.mGet.isDualRecordingActive()) {
                this.mGet.setPreferenceMenuEnable(Setting.KEY_VIDEO_STABILIZATION, true, false);
            }
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    SetVideoImageSize.this.mGet.removePostRunnable(this);
                    SetVideoImageSize.this.mGet.updateStabilizationIndicator();
                }
            });
        }
    }

    protected void onExecuteAlone() {
        int mCarrierCode = ModelProperties.getCarrierCode();
        if (mCarrierCode == 5 || mCarrierCode == 21) {
            String videoSizeString = this.mGet.getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            if (this.mGet.isMMSIntent() || MmsProperties.isAvailableMmsResolution(this.mGet.getContentResolver(), videoSizeString)) {
                this.mGet.showRequestedSizeLimit();
            }
        }
    }
}
