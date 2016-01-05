package com.lge.camera.controller.camcorder;

import android.filterpacks.videosink.MediaRecorderStopException;
import android.hardware.Camera.Size;
import android.widget.Toast;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.controller.PreviewController;
import com.lge.camera.listeners.MediaRecorderListener;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.MmsProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.Util;
import com.lge.media.CamcorderProfileEx;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import com.lge.olaworks.library.FaceDetector;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class CamcorderPreviewController extends PreviewController {
    protected CountDownLatch mEffectRecordingDoneLatch;
    private Thread mStopRecordingEffectThread;
    private int tryCnt;

    public CamcorderPreviewController(ControllerFunction function) {
        super(function);
        this.mEffectRecordingDoneLatch = null;
        this.tryCnt = 0;
        makeEffectList();
    }

    public String getPreviewSizeOnScreen() {
        String previewSizeOnScreen;
        ListPreference pref;
        if (this.mGet.isMMSIntent()) {
            pref = this.mGet.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            if (pref == null) {
                CamLog.d(FaceDetector.TAG, " pref is null ");
                return null;
            }
            int prefVideoSizeIndex = pref.findIndexOfValue(getPreviewSizeOnDevice());
            if (prefVideoSizeIndex < 0) {
                prefVideoSizeIndex = 0;
            }
            previewSizeOnScreen = pref.getExtraInfos()[prefVideoSizeIndex].toString();
        } else {
            pref = this.mGet.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            if (pref == null) {
                CamLog.d(FaceDetector.TAG, " pref is null ");
                return null;
            } else if (MultimediaProperties.isLiveEffectSupported() && isLiveEffectActive()) {
                previewSizeOnScreen = MultimediaProperties.getLiveEffectPreviewOnDevice(this.mGet.getCameraMode());
            } else if (!isDualRecordingActive() && !isSmartZoomRecordingActive()) {
                previewSizeOnScreen = pref.getExtraInfo();
            } else if (ModelProperties.isUVGAmodel()) {
                previewSizeOnScreen = MultimediaProperties.EFFECTS_ENFORCED_SIZE_FOR_UVGA;
            } else {
                previewSizeOnScreen = pref.getExtraInfo();
            }
        }
        return previewSizeOnScreen;
    }

    public String getPreviewSizeOnDevice() {
        String previewSizeOnDevice;
        if (this.mGet.isMMSIntent()) {
            String prefVideoSize = this.mGet.getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            if (MmsProperties.isAvailableMmsResolution(this.mGet.getContentResolver(), prefVideoSize)) {
                previewSizeOnDevice = prefVideoSize;
            } else {
                String[] mmsResolutions = MmsProperties.getMmsResolutions(this.mGet.getContentResolver());
                if (mmsResolutions == null || mmsResolutions.length == 0) {
                    previewSizeOnDevice = prefVideoSize;
                } else {
                    previewSizeOnDevice = mmsResolutions[0];
                }
            }
        } else {
            ListPreference pref = this.mGet.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            if (pref == null) {
                CamLog.d(FaceDetector.TAG, " pref is null ");
                return null;
            }
            previewSizeOnDevice = pref.getValue();
        }
        return previewSizeOnDevice;
    }

    public void onResume() {
        super.onResume();
    }

    protected void makeEffectList() {
        String[] effectList = this.mGet.getResources().getStringArray(R.array.camcorder_liveeffect_entryValues);
        this.mEffectList = new ArrayList();
        for (String s : effectList) {
            this.mEffectList.add(s);
        }
    }

    public ArrayList<String> getLiveEffectList() {
        return this.mEffectList;
    }

    public void setLiveEffect(String effect) {
        this.mCurrentEffect = effect;
    }

    public String getLiveEffect() {
        return this.mCurrentEffect;
    }

    protected void initializeEffectsPreview() {
        int i = 4;
        CamLog.d(FaceDetector.TAG, "initializeEffectsPreview");
        if (this.mCameraDevice != null) {
            if (this.mEffectsRecorder == null) {
                CamLog.d(FaceDetector.TAG, "mEffectsRecorder is null");
                return;
            }
            String videoSize;
            int rotation;
            this.mEffectsRecorder.setCamera(this.mCameraDevice);
            this.mEffectsRecorder.setCameraFacing(this.mGet.getCameraId());
            if (this.mEffectType == 5 || this.mEffectType == 7) {
                videoSize = null;
                if (this.mGet.checkPreviewController()) {
                    if (ModelProperties.isUVGAmodel()) {
                        videoSize = MultimediaProperties.EFFECTS_ENFORCED_SIZE_FOR_UVGA;
                    } else {
                        videoSize = this.mGet.getPreviewSizeOnDevice();
                    }
                }
                if (videoSize != null) {
                    this.mProfileType = MultimediaProperties.getProfileQulity(this.mGet.getCameraId(), Util.SizeString2WidthHeight(videoSize));
                    initPIPSubWindow(videoSize);
                }
            } else if (!ModelProperties.isUVGAmodel()) {
                if (!CamcorderProfileEx.hasProfile(this.mGet.getCameraId(), 4)) {
                    i = 10;
                }
                this.mProfileType = i;
            } else if (CamcorderProfileEx.hasProfile(this.mGet.getCameraId(), 14)) {
                this.mProfileType = 14;
            } else {
                CamLog.e(FaceDetector.TAG, "Cannot find the profile; CamcorderProfile.QUALITY_960P");
            }
            int[] size = null;
            videoSize = getPreviewSizeOnDevice();
            if (videoSize != null) {
                size = Util.SizeString2WidthHeight(videoSize);
            }
            this.mEffectsRecorder.setVideoEncodingBitRate(MultimediaProperties.getBitrate(this.mGet.getCameraId(), MultimediaProperties.getProfileQulity(this.mGet.getCameraId(), size)));
            this.mProfile = CamcorderProfileEx.get(this.mGet.getCameraId(), this.mProfileType);
            CamLog.v(FaceDetector.TAG, "profile : " + this.mProfile.videoFrameWidth + "x" + this.mProfile.videoFrameHeight);
            this.mEffectsRecorder.setProfile(this.mProfile);
            this.mEffectsRecorder.setEffectsListener(this.mEffectRecorderListener);
            MediaRecorderListener listener = new MediaRecorderListener(this.mGet);
            this.mEffectsRecorder.setOnInfoListener(listener);
            this.mEffectsRecorder.setOnErrorListener(listener);
            if (this.mGet.isConfigureLandscape()) {
                rotation = this.mGet.getOrientationDegree();
            } else {
                rotation = (this.mGet.getOrientationDegree() + 90) % CameraConstants.DEGREE_360;
            }
            CamLog.v(FaceDetector.TAG, " mEffectsRecorder.setOrientationHint(" + rotation + ")");
            this.mEffectsRecorder.setOrientationHint(rotation);
            CamLog.v(FaceDetector.TAG, "mEffectsRecorder.setPreviewDisplay width:" + this.mSurfaceWidth + ",height:" + this.mSurfaceHeight);
            this.mEffectsRecorder.setPreviewDisplay(this.mSurfaceHolder, this.mSurfaceWidth, this.mSurfaceHeight);
            if (this.mEffectType == 7) {
                if (ModelProperties.isUVGAmodel()) {
                    this.mEffectsRecorder.setTextureSize(MultimediaProperties.SMARTZOOM_PREVIEWSIZE_SET_ON_DEVICE_FOR_UVGA);
                } else {
                    this.mEffectsRecorder.setTextureSize(MultimediaProperties.SMARTZOOM_PREVIEWSIZE_SET_ON_DEVICE);
                }
            }
            this.mEffectsRecorder.setEffect(this.mEffectType, this.mEffectParameter);
            if (this.mGet.isDualRecordingActive() || this.mGet.isSmartZoomRecordingActive()) {
                if (!this.mGet.setPIPMask(this.mGet.getCurrentPIPMask())) {
                    CamLog.d(FaceDetector.TAG, "setPIPMask returns FALSE");
                }
                this.mGet.setPIPRotate(this.mGet.getOrientationDegree());
            }
        }
    }

    protected void initPIPSubWindow(String videoSize) {
        int subWindowThick = (int) Util.dpToPx(this.mGet.getApplicationContext(), CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
        if (this.mPIPRecordingController != null) {
            int[] initPosition = this.mPIPRecordingController.getInitPIPSubWindowPosition(videoSize);
            this.mEffectsRecorder.setPIPSubWindowPosition(initPosition[0], initPosition[1], initPosition[2], initPosition[3]);
            this.mEffectsRecorder.setPIPSubWindowThick(subWindowThick);
        }
    }

    public boolean updateEffectSelection() {
        boolean z = true;
        if (!MultimediaProperties.isLiveEffectSupported()) {
            CamLog.d(FaceDetector.TAG, "Do not support liveeffect");
            return false;
        } else if (this.mGet.isPausing() || this.mGet.isFinishingActivity() || this.mEffectsRecorder == null || this.mCameraDevice == null) {
            r6 = FaceDetector.TAG;
            StringBuilder append = new StringBuilder().append("updateEffectSelection() return:").append(this.mGet.isPausing()).append(" ").append(this.mGet.getActivity().isFinishing()).append(" ").append(this.mEffectsRecorder == null).append(" ");
            if (this.mCameraDevice != null) {
                z = false;
            }
            CamLog.v(r6, append.append(z).toString());
            return false;
        } else {
            String str;
            CamLog.v(FaceDetector.TAG, "updateEffectSelection() [previous] mEffectType: " + this.mEffectType + ", mEffectParameter: " + (this.mEffectParameter == null ? "null" : this.mEffectParameter.toString()));
            this.previousEffectType = this.mEffectType;
            this.previousEffectParameter = this.mEffectParameter;
            this.mEffectType = readEffectType();
            this.mEffectParameter = readEffectParameter();
            r6 = FaceDetector.TAG;
            StringBuilder append2 = new StringBuilder().append("updateEffectSelection() [current] mEffectType: ").append(this.mEffectType).append(", mEffectParameter: ");
            if (this.mEffectParameter == null) {
                str = "null";
            } else {
                str = this.mEffectParameter.toString();
            }
            CamLog.v(r6, append2.append(str).toString());
            if (this.mEffectType == 7 && getPIPController().isObjectTrackingEnabledForSmartZoom()) {
                this.mGet.disableObjectTrackingForSmartZoom();
            }
            if (this.mEffectType == this.previousEffectType) {
                if (this.mEffectType == 0) {
                    CamLog.d(FaceDetector.TAG, "mEffectType is none");
                    return false;
                } else if (this.mEffectParameter == null) {
                    CamLog.d(FaceDetector.TAG, "effectparameter is null");
                    return false;
                } else if (this.mEffectParameter != null && this.mEffectParameter.equals(this.previousEffectParameter)) {
                    CamLog.d(FaceDetector.TAG, "effectparameter is same");
                    return false;
                }
            }
            if (this.previousEffectType == 0) {
                ListPreference videoSizePref = this.mGet.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
                if (videoSizePref != null) {
                    this.previousResolution = videoSizePref.getValue();
                    CamLog.v(FaceDetector.TAG, "backup previous resolution:" + this.previousResolution);
                } else {
                    CamLog.d(FaceDetector.TAG, "videoSizePref is null");
                    return true;
                }
            }
            this.mGet.enableInput(false);
            this.mGet.setQuickFunctionAllMenuEnabled(false, true);
            if (this.mEffectType == 0) {
                if (this.previousResolution != null) {
                    CamLog.i(FaceDetector.TAG, "set previous resolution :" + this.previousResolution);
                    this.mGet.setAllChildMenuEnabled(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, true);
                    this.mGet.setSelectedChild(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, this.previousResolution, true);
                    this.mGet.setPreferenceMenuEnable(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, true, false);
                    this.mGet.setLimitationToLiveeffect(true);
                }
                this.mEffectsRecorder.stopPreview(100);
                return true;
            }
            if ((this.previousEffectType != 0 && this.mEffectType == 5) || ((this.previousEffectType != 0 && this.mEffectType == 7) || this.previousEffectType == 5 || this.previousEffectType == 7)) {
                this.mEffectsRecorder.stopPreview(100);
            }
            CamLog.i(FaceDetector.TAG, "set to live effect limit");
            this.mGet.setLiveeffectLimit();
            String getSizeOnScreen = getPreviewSizeOnScreen();
            if (getSizeOnScreen != null) {
                int[] previewSizeOnScreen = Util.SizeString2WidthHeight(getSizeOnScreen);
                CamLog.i(FaceDetector.TAG, "getPreviewSizeOnScreen: " + getSizeOnScreen);
                changePreviewModeOnUiThread(previewSizeOnScreen[0], previewSizeOnScreen[1]);
            }
            if (this.previousEffectType == 0 || this.mEffectType == 0 || ((this.previousEffectType != 0 && this.mEffectType == 5) || (this.previousEffectType != 0 && this.mEffectType == 7))) {
                CamLog.d(FaceDetector.TAG, "#### mCameraDevice.stopPreview");
                this.mCameraDevice.stopPreview();
                checkQualityAndStartPreview();
            } else {
                this.mEffectsRecorder.setEffect(this.mEffectType, this.mEffectParameter);
                if (this.mEffectType == 1 && this.mEffectType == this.previousEffectType) {
                    this.mGet.enableInput(true);
                    CamLog.d(FaceDetector.TAG, "enableInput(true) in updateEffectSelection");
                }
                this.mGet.quickFunctionControllerRefresh(true);
            }
            return true;
        }
    }

    protected int readEffectType() {
        if (this.mGet.getApplicationMode() == 0) {
            return 0;
        }
        int retValue = 0;
        if (this.mGet.getPreferenceGroup() == null) {
            return 0;
        }
        String effectSelection = null;
        if (this.mGet.findPreference(Setting.KEY_LIVE_EFFECT) != null) {
            effectSelection = this.mGet.getSettingValue(Setting.KEY_LIVE_EFFECT);
        } else if (this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE) == null) {
            effectSelection = getLiveEffect();
        } else if (CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE))) {
            retValue = 1;
        }
        if (effectSelection != null && retValue == 0) {
            if (effectSelection.startsWith(CameraConstants.SMART_MODE_OFF)) {
                retValue = 0;
            } else {
                retValue = 1;
            }
        }
        if (retValue == 0) {
            if (this.mGet.findPreference(Setting.KEY_DUAL_RECORDING) != null) {
                String dualSelection = this.mGet.getSettingValue(Setting.KEY_DUAL_RECORDING);
                if (dualSelection != null) {
                    if (dualSelection.startsWith(CameraConstants.SMART_MODE_ON)) {
                        retValue = 5;
                    } else {
                        retValue = 0;
                    }
                }
            } else if (this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE) != null) {
                if (CameraConstants.TYPE_RECORDMODE_DUAL.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE))) {
                    retValue = 5;
                } else {
                    retValue = 0;
                }
            }
        }
        if (retValue != 0) {
            return retValue;
        }
        if (this.mGet.findPreference(Setting.KEY_SMART_ZOOM_RECORDING) != null) {
            String modeSelection = this.mGet.getSettingValue(Setting.KEY_SMART_ZOOM_RECORDING);
            if (modeSelection == null) {
                return retValue;
            }
            if (modeSelection.startsWith(CameraConstants.SMART_MODE_ON)) {
                return 7;
            }
            return 0;
        } else if (this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE) == null) {
            return retValue;
        } else {
            if (CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE))) {
                return 7;
            }
            return 0;
        }
    }

    protected Object readEffectParameter() {
        if (this.mGet.getApplicationMode() == 0) {
            return null;
        }
        if (this.mGet.getPreferenceGroup() != null) {
            String effectSelection;
            boolean flag;
            if (this.mGet.findPreference(Setting.KEY_LIVE_EFFECT) != null) {
                effectSelection = this.mGet.getSettingValue(Setting.KEY_LIVE_EFFECT);
                CamLog.v(FaceDetector.TAG, "current liveeffect type :" + effectSelection);
                if (effectSelection != null && !effectSelection.equals(CameraConstants.SMART_MODE_OFF)) {
                    return getEffectSelection(effectSelection);
                }
                flag = true;
            } else {
                effectSelection = getLiveEffect();
                CamLog.v(FaceDetector.TAG, "current liveeffect type (record mode exists) : " + effectSelection);
                if (effectSelection != null && !effectSelection.equals(CameraConstants.SMART_MODE_OFF)) {
                    return getEffectSelection(effectSelection);
                }
                flag = true;
            }
            if (this.mGet.findPreference(Setting.KEY_DUAL_RECORDING) != null) {
                effectSelection = this.mGet.getSettingValue(Setting.KEY_DUAL_RECORDING);
                CamLog.v(FaceDetector.TAG, "current dual recording setting value :" + effectSelection);
                if (effectSelection != null && !effectSelection.equals(CameraConstants.SMART_MODE_OFF)) {
                    return getEffectSelection(effectSelection);
                }
                flag = true;
            }
            if (this.mGet.findPreference(Setting.KEY_SMART_ZOOM_RECORDING) != null) {
                effectSelection = this.mGet.getSettingValue(Setting.KEY_SMART_ZOOM_RECORDING);
                CamLog.v(FaceDetector.TAG, "current smart zoom setting value :" + effectSelection);
                if (effectSelection != null && !effectSelection.equals(CameraConstants.SMART_MODE_OFF)) {
                    return getEffectSelection(effectSelection);
                }
                flag = true;
            }
            if (this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE) != null) {
                if (CameraConstants.TYPE_RECORDMODE_DUAL.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE))) {
                    if (CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(this.mGet.getPreviousRecordModeString()) || CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(this.mGet.getPreviousRecordModeString())) {
                        return getEffectSelection(CameraConstants.SMART_MODE_OFF);
                    }
                    CamLog.v(FaceDetector.TAG, "current dual recording setting value (record mode exists) : " + getEffectSelection(CameraConstants.SMART_MODE_ON));
                    return getEffectSelection(CameraConstants.SMART_MODE_ON);
                } else if (CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE))) {
                    if (CameraConstants.TYPE_RECORDMODE_DUAL.equals(this.mGet.getPreviousRecordModeString()) || CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(this.mGet.getPreviousRecordModeString())) {
                        return getEffectSelection(CameraConstants.SMART_MODE_OFF);
                    }
                    CamLog.v(FaceDetector.TAG, "current smart zoom setting value (record mode exists) : " + getEffectSelection(CameraConstants.SMART_MODE_ON));
                    return getEffectSelection(CameraConstants.SMART_MODE_ON);
                } else if (CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE))) {
                    if (CameraConstants.TYPE_RECORDMODE_DUAL.equals(this.mGet.getPreviousRecordModeString()) || CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(this.mGet.getPreviousRecordModeString())) {
                        return getEffectSelection(CameraConstants.SMART_MODE_OFF);
                    }
                    CamLog.v(FaceDetector.TAG, "current live effect setting value (record mode exists) : " + this.mCurrentEffect);
                    return getEffectSelection(this.mCurrentEffect);
                }
            }
            if (flag) {
                return null;
            }
            CamLog.d(FaceDetector.TAG, "Invalid effect selection, preference is null ");
            return null;
        }
        CamLog.d(FaceDetector.TAG, "Invalid effect selection, getPreferenceGroup is null ");
        return null;
    }

    private Object getEffectSelection(String effectSelection) {
        if (effectSelection.equals(CameraConstants.EFFECT_TYPE_SQUEEZE)) {
            return Integer.valueOf(0);
        }
        if (effectSelection.equals(CameraConstants.EFFECT_TYPE_BIG_NOSE)) {
            return Integer.valueOf(4);
        }
        if (effectSelection.equals(CameraConstants.EFFECT_TYPE_BIG_EYES)) {
            return Integer.valueOf(1);
        }
        if (effectSelection.equals(CameraConstants.EFFECT_TYPE_SMALL_EYES)) {
            return Integer.valueOf(5);
        }
        if (effectSelection.equals(CameraConstants.EFFECT_TYPE_BIG_MOUTH)) {
            return Integer.valueOf(2);
        }
        if (effectSelection.equals(CameraConstants.EFFECT_TYPE_SMALL_MOUTH)) {
            return Integer.valueOf(3);
        }
        if (effectSelection.equals(CameraConstants.SMART_MODE_ON)) {
            return CameraConstants.SMART_MODE_ON;
        }
        if (effectSelection.equals(CameraConstants.SMART_MODE_ON)) {
            return CameraConstants.SMART_MODE_ON;
        }
        CamLog.v(FaceDetector.TAG, "Invalid effect selection: " + effectSelection);
        return null;
    }

    public boolean effectsCamcorderActive() {
        boolean z = true;
        if ((!MultimediaProperties.isLiveEffectSupported() && !MultimediaProperties.isDualRecordingSupported() && !MultimediaProperties.isSmartZoomSupported()) || this.mGet.getApplicationMode() != 1) {
            return false;
        }
        if (readEffectType() == 0) {
            z = false;
        }
        return z;
    }

    public boolean isDualRecordingActive() {
        if (!MultimediaProperties.isDualRecordingSupported()) {
            CamLog.d(FaceDetector.TAG, "Do not support Dual Recording");
            return false;
        } else if (readEffectType() == 5) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLiveEffectActive() {
        if (MultimediaProperties.isLiveEffectSupported()) {
            int type = readEffectType();
            if (type == 0 || type == 5 || type == 7) {
                return false;
            }
            return true;
        }
        CamLog.d(FaceDetector.TAG, "Do not support Live Effect");
        return false;
    }

    public boolean isSmartZoomRecordingActive() {
        if (!MultimediaProperties.isSmartZoomSupported()) {
            CamLog.d(FaceDetector.TAG, "Do not support Smart Zoom Recording");
            return false;
        } else if (readEffectType() == 7) {
            return true;
        } else {
            return false;
        }
    }

    public void showSubWindowResizeHandler(float x, float y) {
        if ((isDualRecordingActive() || isSmartZoomRecordingActive()) && this.mPIPRecordingController != null) {
            int x0 = this.mPIPRecordingController.getLeftTopX();
            int y0 = this.mPIPRecordingController.getLeftTopY();
            int x1 = this.mPIPRecordingController.getRightBottomX();
            int y1 = this.mPIPRecordingController.getRightBottomY();
            if (x > ((float) x0) && y > ((float) y0) && x < ((float) x1) && y < ((float) y1) && !this.mPIPRecordingController.isResizeHandlerShown()) {
                this.mPIPRecordingController.showSubWindowResizeHandler();
            }
        }
    }

    public void hideSubWindowResizeHandler() {
        if (this.mPIPRecordingController != null && this.mPIPRecordingController.isResizeHandlerShown()) {
            this.mPIPRecordingController.hideSubWindowResizeHandler();
            CamLog.d(FaceDetector.TAG, "KMIN - Hide DualRec SubWindow Handler");
        }
    }

    public void showSmartZoomFocusView() {
        if (this.mPIPRecordingController != null && !this.mPIPRecordingController.isResizeHandlerShown()) {
            this.mPIPRecordingController.showSmartZoomFocusView();
            CamLog.d(FaceDetector.TAG, "Show smart zoom focus view");
        }
    }

    public void hideSmartZoomFocusView() {
        if (this.mPIPRecordingController != null && this.mPIPRecordingController.isSmartZoomFocusViewShown()) {
            this.mPIPRecordingController.hideSmartZoomFocusView();
            CamLog.d(FaceDetector.TAG, "Hide smart zoom focus view");
        }
    }

    public void setSmartZoomFocusViewPosition(int x, int y) {
        if (this.mPIPRecordingController != null && this.mPIPRecordingController.isSmartZoomFocusViewShown()) {
            this.mPIPRecordingController.setSmartZoomFocusViewPosition(x, y);
            CamLog.d(FaceDetector.TAG, "setSmartZoomFocusViewPosition: (" + x + " ," + y + ")");
        }
    }

    public void setPreviousResolution(String resolution) {
        this.previousResolution = resolution;
    }

    public void storePreviousResolution(String prevResolution) {
        CamLog.d(FaceDetector.TAG, "STORE: previousResolution = " + prevResolution);
        if (!this.mGet.isEffectsCamcorderActive()) {
            prevResolution = null;
        }
        if (this.mGet.getCameraMode() == 0) {
            this.mPreviousResolutionBack = prevResolution;
        } else {
            this.mPreviousResolutionFront = prevResolution;
        }
    }

    public void setPrevResolutionWithStoredValue() {
        if (this.mGet.getCameraMode() == 0) {
            this.previousResolution = this.mPreviousResolutionFront;
        } else {
            this.previousResolution = this.mPreviousResolutionBack;
        }
        CamLog.d(FaceDetector.TAG, "RESTORE: previousResolution = " + this.previousResolution);
    }

    public void initSmartZoomFocusView() {
        if (this.mPIPRecordingController != null) {
            this.mPIPRecordingController.initSmartZoomFocusView();
        }
    }

    public PIPRecordingController getPIPRecordingController() {
        return this.mPIPRecordingController;
    }

    protected void doOnEffectsUpdate(int effectId, int effectMsg) {
        if (!checkMediator()) {
            CamLog.d(FaceDetector.TAG, "checkMediator() is false");
        } else if (this.mGet.isPausing() || this.mGet.isFinishingActivity() || this.mGet.getEffectRecorderPausing()) {
            CamLog.d(FaceDetector.TAG, "ongoing finish effectMsg=" + effectMsg + "mEffectRecordingDoneLatch=" + this.mEffectRecordingDoneLatch);
            if (effectMsg == 4 || effectMsg == 6 || effectMsg == 7) {
                if (this.mEffectRecordingDoneLatch != null) {
                    this.mEffectRecordingDoneLatch.countDown();
                }
                if (effectMsg == 4) {
                    this.mGet.playRecordingSound(false);
                }
            }
        } else if (this.mEffectsRecorder == null) {
            CamLog.d(FaceDetector.TAG, "mEffectsRecorder is null");
        } else {
            CamLog.d(FaceDetector.TAG, "onEffectsUpdate: id:" + effectId + " msg:" + effectMsg);
            if (effectMsg == 3) {
                CamLog.d(FaceDetector.TAG, "-------EFFECT_MSG_EFFECTS_STOPPED");
                if (this.mEffectsRecorder.mCalledFrom == 100) {
                    checkQualityAndStartPreview();
                    this.mGet.quickFunctionControllerRefresh(true);
                }
            } else if (effectMsg == 7) {
                CamLog.d(FaceDetector.TAG, "-------EFFECT_MSG_SURFACE_TEARDOWN :" + this.mEffectsRecorder.mCalledFrom);
                if (this.mEffectsRecorder.mCalledFrom == DialogCreater.DIALOG_ID_HELP_HDR) {
                    this.mGet.doCommandUi(Command.RESET_MENU);
                    this.previousResolution = null;
                } else if (this.mEffectsRecorder.mCalledFrom == DialogCreater.DIALOG_ID_HELP_PANORAMA) {
                    if (this.mGet.isDualRecordingActive() || this.mGet.isSmartZoomRecordingActive()) {
                        this.mGet.doCommand(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
                    }
                    this.mGet.doCommandUi(Command.SWAP_CAMERA);
                }
                this.mEffectsRecorder.mCalledFrom = 100;
            } else if (effectMsg == 5) {
                CamLog.d(FaceDetector.TAG, "-------EFFECT_MSG_PREVIEW_DONE");
                if (this.mGet.getVideoState() == 3 || this.mGet.getVideoState() == 4) {
                    CamLog.d(FaceDetector.TAG, "return nPreview Done in Recording state");
                    return;
                }
                this.mStartPreviewOnGoing = false;
                this.mIsStartPreviewEffectOnGoing = false;
                this.mGet.enableInput(true);
                CamLog.d(FaceDetector.TAG, "enableInput(true) in onEffectsUpdate");
                this.mGet.quickFunctionControllerRefresh(true);
            } else if (effectMsg == 4) {
                CamLog.d(FaceDetector.TAG, "-------EFFECT_MSG_RECORDING_DONE");
                if (this.mEffectRecordingDoneLatch != null) {
                    this.mEffectRecordingDoneLatch.countDown();
                }
                this.mGet.playRecordingSound(false);
            } else if (effectMsg == 6) {
                CamLog.d(FaceDetector.TAG, "-------EFFECT_MSG_ERROR");
                closeCamera();
                Toast.makeText(this.mGet.getApplicationContext(), R.string.camera_error_occurred_try_again, 1).show();
                this.mGet.getActivity().finish();
            } else if (effectMsg == 1) {
                this.mGet.quickFunctionControllerRefresh(true);
                this.mGet.setQuickButtonForcedDisable(false);
                this.mGet.setButtonRemainRefresh();
            }
        }
    }

    protected void doOnEffectesError(Exception exception, String fileName) {
        if (!checkMediator()) {
            CamLog.d(FaceDetector.TAG, "checkMediator() is false");
        } else if (this.mGet.isPausing() || this.mGet.isFinishingActivity()) {
            CamLog.d(FaceDetector.TAG, "ongoing finish");
        } else if (exception instanceof MediaRecorderStopException) {
            CamLog.w(FaceDetector.TAG, "Problem recoding video file. Removing incomplete file.");
        } else {
            if (fileName != null) {
                File videoFile = new File(fileName);
                if (this.mGet.getCurrentRecordingTime() <= ((long) MultimediaProperties.getMinRecordingTime())) {
                    if (!videoFile.exists()) {
                        CamLog.d(FaceDetector.TAG, "Short recording time! Not Error!");
                        return;
                    } else if (this.mProfile != null) {
                        if (videoFile.length() < ((long) ((this.mProfile.videoFrameWidth * this.mProfile.videoFrameHeight) * (MultimediaProperties.getMinRecordingTime() / PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME)))) {
                            CamLog.d(FaceDetector.TAG, "Short recording time! Not Error!! file soon delete! -> " + fileName);
                            return;
                        }
                    }
                }
            }
            CamLog.e(FaceDetector.TAG, "Error during recording!", exception);
            closeCamera();
            this.mGet.showCameraStoppedAndFinish();
        }
    }

    public void startPreviewEffect() {
        startPreviewEffect(this.mEffectsRecorder, effectsCamcorderActive());
    }

    public void stopPreviewEffect() {
        stopPreviewEffect(this.mEffectsRecorder);
    }

    public void initializeRecordingEffect(String filepath_name, long mMaxFileSize, int mMaxDurationTime, long freeSpace) {
        if (effectsCamcorderActive() && this.mEffectsRecorder != null) {
            this.mEffectsRecorder.setProfile(this.mProfile);
            this.mEffectsRecorder.setCaptureRate(0.0d);
            this.mEffectsRecorder.setOutputFile(filepath_name);
            if (mMaxFileSize == 0) {
                mMaxFileSize = freeSpace - CameraConstants.VIDEO_LOW_STORAGE_THRESHOLD;
                if (mMaxFileSize > MultimediaProperties.getMediaRecoderLimitSize()) {
                    mMaxFileSize = MultimediaProperties.getMediaRecoderLimitSize();
                }
                if (mMaxFileSize < 0) {
                    mMaxFileSize = CameraConstants.VIDEO_LOW_STORAGE_THRESHOLD;
                }
            }
            this.mEffectsRecorder.setMaxFileSize(mMaxFileSize);
            this.mEffectsRecorder.setMaxDuration(mMaxDurationTime);
        }
    }

    public void startRecordingEffect() {
        if (!this.mGet.isPausing() && !this.mGet.isFinishingActivity() && this.mEffectsRecorder != null && this.mCameraDevice != null && effectsCamcorderActive()) {
            boolean beUnmute = true;
            ListPreference listPref = this.mGet.findPreference(Setting.KEY_VIDEO_AUDIO_RECORDING);
            if (listPref != null) {
                beUnmute = "unmute".equals(listPref.getValue());
            }
            CamLog.d(FaceDetector.TAG, "startRecordingEffect() unmute?" + beUnmute);
            this.mEffectsRecorder.startRecording(beUnmute);
        }
    }

    public void pauseAndResumeRecording(boolean pause) {
        if (this.mEffectsRecorder != null) {
            CamLog.d(FaceDetector.TAG, "EffectsRecorder: pause/resume : " + pause);
            this.mEffectsRecorder.pauseAndResumeRecording(null, pause);
        }
    }

    public void stopRecordingEffect() {
        if (effectsCamcorderActive()) {
            CamLog.d(FaceDetector.TAG, "stopRecordingEffect");
            this.tryCnt = 0;
            this.mStopRecordingEffectThread = new Thread(new Runnable() {
                public void run() {
                    CamLog.d(FaceDetector.TAG, "stopRecordingEffect in run()-start");
                    if (CamcorderPreviewController.this.mEffectsRecorder == null) {
                        CamLog.d(FaceDetector.TAG, "stopRecordingEffect in run()-end, effectsrecorder is null");
                        return;
                    }
                    if (CamcorderPreviewController.this.mEffectsRecorder.getState() == 4) {
                        CamcorderPreviewController.this.mEffectRecordingDoneLatch = new CountDownLatch(1);
                    }
                    CamcorderPreviewController.this.mEffectsRecorder.stopRecording();
                    long latchCount = 0;
                    try {
                        if (CamcorderPreviewController.this.mEffectRecordingDoneLatch != null) {
                            CamLog.d(FaceDetector.TAG, "START stopRecordingEffect await");
                            CamcorderPreviewController.this.mEffectRecordingDoneLatch.await();
                            CamLog.d(FaceDetector.TAG, "END stopRecordingEffect await");
                            latchCount = CamcorderPreviewController.this.mEffectRecordingDoneLatch.getCount();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    CamLog.d(FaceDetector.TAG, "stopRecordingEffect in run()-end, tryCnt:" + CamcorderPreviewController.this.tryCnt + " Latch:" + latchCount);
                }
            });
            this.mStopRecordingEffectThread.start();
        }
    }

    public void waitStopRecordingEffectThreadDone() {
        try {
            if (this.mStopRecordingEffectThread != null && this.mStopRecordingEffectThread.isAlive()) {
                CamLog.d(FaceDetector.TAG, String.format("Wait for stop effectRecording done..", new Object[0]));
                this.mStopRecordingEffectThread.join();
                this.mStopRecordingEffectThread = null;
                CamLog.d(FaceDetector.TAG, String.format("Stop effectRecording done..", new Object[0]));
            }
        } catch (InterruptedException e) {
            CamLog.e(FaceDetector.TAG, String.format("Failed to join stop recording thread!", new Object[0]));
            e.printStackTrace();
        }
    }

    protected void checkQualityAndStartPreview() {
        int i = 4;
        Size previewSize = this.mCameraDevice.getParameters().getPreviewSize();
        if (previewSize == null) {
            CamLog.d(FaceDetector.TAG, "size is null");
            return;
        }
        if (isDualRecordingActive() || isSmartZoomRecordingActive()) {
            int[] size = null;
            if (this.mGet.checkPreviewController()) {
                String videoSize;
                if (ModelProperties.isUVGAmodel()) {
                    videoSize = MultimediaProperties.EFFECTS_ENFORCED_SIZE_FOR_UVGA;
                } else {
                    videoSize = this.mGet.getPreviewSizeOnDevice();
                }
                size = Util.SizeString2WidthHeight(videoSize);
            }
            this.mProfileType = MultimediaProperties.getProfileQulity(this.mGet.getCameraId(), size);
            this.mProfile = CamcorderProfileEx.get(this.mGet.getCameraId(), this.mProfileType);
        } else if (isLiveEffectActive()) {
            if (!ModelProperties.isUVGAmodel()) {
                if (!CamcorderProfileEx.hasProfile(this.mGet.getCameraId(), 4)) {
                    i = 10;
                }
                this.mProfileType = i;
            } else if (CamcorderProfileEx.hasProfile(this.mGet.getCameraId(), 14)) {
                this.mProfileType = 14;
            } else {
                CamLog.e(FaceDetector.TAG, "Cannot find the profile; CamcorderProfile.QUALITY_960P");
            }
            this.mProfile = CamcorderProfileEx.get(this.mGet.getCameraId(), this.mProfileType);
            CamLog.v(FaceDetector.TAG, "reset profile : " + this.mProfile.videoFrameWidth + "x" + this.mProfile.videoFrameHeight);
            this.mEffectsRecorder.setProfile(this.mProfile);
        }
        CamLog.v(FaceDetector.TAG, "getPreviewSize:" + previewSize.width + "x" + previewSize.height);
        CamLog.v(FaceDetector.TAG, "profile mProfile size:" + this.mProfile.videoFrameWidth + "x" + this.mProfile.videoFrameHeight);
        if (previewSize.height == 1088) {
            previewSize.height = 1080;
        }
        Float profileRatio = Float.valueOf(((float) this.mProfile.videoFrameWidth) / ((float) this.mProfile.videoFrameHeight));
        Float previewRatio = Float.valueOf(((float) previewSize.width) / ((float) previewSize.height));
        if (effectsCamcorderActive() && profileRatio.compareTo(previewRatio) != 0 && ((this.mGet.getPreviousEffectType() != 7 || !this.mGet.isDualRecordingActive()) && (this.mGet.getPreviousEffectType() != 7 || !this.mGet.isLiveEffectActive() || !ModelProperties.isUVGAmodel()))) {
            CamLog.v(FaceDetector.TAG, " setAspectRatio: profileRatio:" + profileRatio + ", previewRatio:" + previewRatio);
            this.mCameraPreview.setAspectRatio(profileRatio.floatValue());
        } else if (this.mEffectType == 0) {
            startPreview(null, true);
        } else {
            CamLog.d(FaceDetector.TAG, "Call startPreviewEffect in checkQualityAndStartPreview ");
            startPreviewEffect();
        }
    }

    public int getPreviousEffectType() {
        return this.previousEffectType;
    }

    public void startPIPFrameSubMenuRotation(int degree) {
        this.mGet.startPIPFrameSubMenuRotation(degree);
    }
}
