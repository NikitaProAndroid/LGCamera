package com.lge.camera.controller.camera;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.controller.PreviewController;
import com.lge.camera.listeners.MediaRecorderListener;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.Util;
import com.lge.media.CamcorderProfileEx;
import com.lge.olaworks.library.EngineProcessor;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;

public class CameraPreviewController extends PreviewController {
    public CameraPreviewController(ControllerFunction function) {
        super(function);
    }

    public void onResume() {
        if (this.mEngineProcessor == null) {
            this.mEngineProcessor = new EngineProcessor();
        }
        super.onResume();
    }

    public String getPreviewSizeOnScreen() {
        String previewSizeOnScreen;
        String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
        if (CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_HDR.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_REFOCUS.equals(shotMode) || this.mGet.isTimeMachineModeOn()) {
            ListPreference pref = this.mGet.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
            if (pref == null) {
                CamLog.d(FaceDetector.TAG, " pref is null ");
                return null;
            }
            previewSizeOnScreen = pref.getExtraInfo3();
        } else {
            ListPreference listPref = this.mGet.findPreference(Setting.KEY_CAMERA_SHOT_MODE);
            if (listPref == null) {
                CamLog.d(FaceDetector.TAG, " pref is null ");
                return null;
            }
            previewSizeOnScreen = listPref.getExtraInfo2();
        }
        return previewSizeOnScreen;
    }

    public String getPreviewSizeOnDevice() {
        String previewSizeOnDevice;
        String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
        ListPreference pref;
        if (CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_HDR.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_REFOCUS.equals(shotMode) || this.mGet.isTimeMachineModeOn()) {
            pref = this.mGet.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
            if (pref == null) {
                CamLog.d(FaceDetector.TAG, " pref is null ");
                return null;
            }
            previewSizeOnDevice = pref.getExtraInfo2();
        } else if (CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(shotMode)) {
            pref = this.mGet.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
            if (pref == null) {
                CamLog.d(FaceDetector.TAG, " pref is null ");
                return null;
            } else if ("".equals(pref.getExtraInfo4())) {
                CamLog.d(FaceDetector.TAG, "extra info4 is null, so normal preview return");
                previewSizeOnDevice = pref.getExtraInfo2();
            } else {
                CamLog.d(FaceDetector.TAG, "extra info4 is not null, so beauty shot preview return");
                previewSizeOnDevice = pref.getExtraInfo4();
            }
        } else {
            ListPreference listPref = this.mGet.findPreference(Setting.KEY_CAMERA_SHOT_MODE);
            if (listPref == null) {
                CamLog.d(FaceDetector.TAG, " pref is null ");
                return null;
            }
            previewSizeOnDevice = listPref.getExtraInfo();
        }
        return previewSizeOnDevice;
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
            if (this.mEffectsCamera == null) {
                CamLog.d(FaceDetector.TAG, "mEffectsCamera is null");
                return;
            }
            this.mEffectsCamera.setCamera(this.mCameraDevice);
            this.mEffectsCamera.setCameraFacing(this.mGet.getCameraId());
            if (this.mEffectType == 6) {
                String videoSize = null;
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
            } else {
                if (!CamcorderProfileEx.hasProfile(this.mGet.getCameraId(), 4)) {
                    i = 10;
                }
                this.mProfileType = i;
            }
            ListPreference videoSizePref = this.mGet.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            if (videoSizePref != null) {
                String sVideoBitrate = videoSizePref.getExtraInfo2();
                CamLog.d(FaceDetector.TAG, "sVideoBitrate: " + sVideoBitrate);
                if (!(sVideoBitrate == null || sVideoBitrate.equals(""))) {
                    this.mEffectsCamera.setVideoEncodingBitRate(Integer.parseInt(sVideoBitrate));
                }
            } else {
                CamLog.d(FaceDetector.TAG, "videoSizePref is null");
            }
            this.mProfile = CamcorderProfileEx.get(this.mGet.getCameraId(), this.mProfileType);
            CamLog.v(FaceDetector.TAG, "profile : " + this.mProfile.videoFrameWidth + "x" + this.mProfile.videoFrameHeight);
            this.mEffectsCamera.setProfile(this.mProfile);
            this.mEffectsCamera.setEffectsListener(this.mEffectCameraListener);
            MediaRecorderListener listener = new MediaRecorderListener(this.mGet);
            this.mEffectsCamera.setOnInfoListener(listener);
            this.mEffectsCamera.setOnErrorListener(listener);
            int rotation = this.mGet.isConfigureLandscape() ? this.mGet.getOrientationDegree() : (this.mGet.getOrientationDegree() + 90) % CameraConstants.DEGREE_360;
            CamLog.v(FaceDetector.TAG, " mEffectsCamera.setOrientationHint(" + rotation + ")");
            this.mEffectsCamera.setOrientationHint(rotation);
            CamLog.v(FaceDetector.TAG, "mEffectsCamera.setPreviewDisplay width:" + this.mSurfaceWidth + ",height:" + this.mSurfaceHeight);
            this.mEffectsCamera.setPreviewDisplay(this.mSurfaceHolder, this.mSurfaceWidth, this.mSurfaceHeight);
            this.mEffectsCamera.setEffect(this.mEffectType, this.mEffectParameter);
            if (this.mGet.isDualCameraActive()) {
                if (!this.mGet.setPIPMask(this.mGet.getCurrentPIPMask())) {
                    CamLog.d(FaceDetector.TAG, "setPIPMask returns FALSE");
                }
                this.mGet.setPIPRotate(this.mGet.getOrientationDegree());
            }
        }
    }

    protected void initPIPSubWindow(String videoSize) {
        int subWindowThick = (int) Util.dpToPx(this.mGet.getApplicationContext(), CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
        if (this.mPIPCameraController != null) {
            int[] initPosition = this.mPIPCameraController.getInitPIPSubWindowPosition(videoSize);
            this.mEffectsCamera.setPIPSubWindowPosition(initPosition[0], initPosition[1], initPosition[2], initPosition[3]);
            this.mEffectsCamera.setPIPSubWindowThick(subWindowThick);
        }
    }

    public boolean updateEffectSelection() {
        boolean z = true;
        if (this.mGet.isPausing() || this.mGet.isFinishingActivity() || this.mEffectsCamera == null || this.mCameraDevice == null) {
            boolean z2;
            String str = FaceDetector.TAG;
            StringBuilder append = new StringBuilder().append("updateEffectSelection() return:").append(this.mGet.isPausing()).append(" ").append(this.mGet.getActivity().isFinishing()).append(" ");
            if (this.mEffectsCamera == null) {
                z2 = true;
            } else {
                z2 = false;
            }
            StringBuilder append2 = append.append(z2).append(" ");
            if (this.mCameraDevice != null) {
                z = false;
            }
            CamLog.v(str, append2.append(z).toString());
            return false;
        }
        String str2;
        this.previousEffectType = this.mEffectType;
        this.previousEffectParameter = this.mEffectParameter;
        this.mEffectType = readEffectType();
        this.mEffectParameter = readEffectParameter();
        CamLog.v(FaceDetector.TAG, "updateEffectSelection() [previous] previousEffectType: " + this.previousEffectType + ", previousEffectParameter: " + (this.previousEffectParameter == null ? "null" : this.previousEffectParameter.toString()));
        str = FaceDetector.TAG;
        append = new StringBuilder().append("updateEffectSelection() [current] mEffectType: ").append(this.mEffectType).append(", mEffectParameter: ");
        if (this.mEffectParameter == null) {
            str2 = "null";
        } else {
            str2 = this.mEffectParameter.toString();
        }
        CamLog.v(str, append.append(str2).toString());
        if (this.mEffectType == this.previousEffectType) {
            if (this.mEffectType == 0 || this.mEffectParameter == null) {
                return false;
            }
            if (this.mEffectParameter != null && this.mEffectParameter.equals(this.previousEffectParameter)) {
                CamLog.d(FaceDetector.TAG, "effectparameter is same");
                return false;
            }
        }
        if (this.previousEffectType == 0) {
            ListPreference imageSizePref = this.mGet.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            if (imageSizePref != null) {
                this.previousResolution = imageSizePref.getValue();
                CamLog.v(FaceDetector.TAG, "backup previous resolution:" + this.previousResolution);
            } else {
                CamLog.d(FaceDetector.TAG, "imageSizePref is null");
                return true;
            }
        }
        this.mGet.enableInput(false);
        this.mGet.setQuickFunctionAllMenuEnabled(false, false);
        if (this.mEffectType == 0) {
            if (this.previousResolution != null) {
                CamLog.i(FaceDetector.TAG, "set previous resolution :" + this.previousResolution);
                this.mGet.setAllChildMenuEnabled(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, true);
                this.mGet.setSelectedChild(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, this.previousResolution, true);
                this.mGet.setPreferenceMenuEnable(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, true, false);
            }
            this.mEffectsCamera.stopPreview(100);
            return true;
        }
        if ((this.previousEffectType != 0 && this.mEffectType == 6) || (this.previousEffectType == 6 && this.mEffectType != 0)) {
            this.mEffectsCamera.stopPreview(100);
        }
        CamLog.i(FaceDetector.TAG, "set to live effect limit");
        this.mGet.setLiveeffectLimit();
        String getSizeOnScreen = getPreviewSizeOnScreen();
        if (getSizeOnScreen != null) {
            int[] previewSizeOnScreen = Util.SizeString2WidthHeight(getSizeOnScreen);
            CamLog.i(FaceDetector.TAG, "getPreviewSizeOnScreen: " + getSizeOnScreen);
            changePreviewModeOnUiThread(previewSizeOnScreen[0], previewSizeOnScreen[1]);
        }
        if (this.previousEffectType == 0 || this.mEffectType == 0 || (this.previousEffectType != 0 && this.mEffectType == 6)) {
            CamLog.d(FaceDetector.TAG, "#### mCameraDevice.stopPreview");
            this.mCameraDevice.stopPreview();
            checkQualityAndStartPreview();
        } else {
            this.mEffectsCamera.setEffect(this.mEffectType, this.mEffectParameter);
            this.mGet.quickFunctionControllerRefresh(true);
        }
        return true;
    }

    protected int readEffectType() {
        if (this.mGet.getApplicationMode() != 0 || this.mGet.getPreferenceGroup() == null) {
            if (this.mGet.getPreferenceGroup() == null || null != null) {
                return 0;
            }
            if (this.mGet.findPreference(Setting.KEY_DUAL_RECORDING) != null) {
                String dualSelection = this.mGet.getSettingValue(Setting.KEY_DUAL_RECORDING);
                if (dualSelection == null) {
                    return 0;
                }
                if (dualSelection.startsWith(CameraConstants.SMART_MODE_ON)) {
                    return 6;
                }
                return 0;
            } else if (this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE) == null) {
                return 0;
            } else {
                if (CameraConstants.TYPE_RECORDMODE_DUAL.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE))) {
                    return 6;
                }
                return 0;
            }
        } else if (this.mGet.findPreference(Setting.KEY_CAMERA_SHOT_MODE) == null) {
            return 0;
        } else {
            if (CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
                return 6;
            }
            return 0;
        }
    }

    protected Object readEffectParameter() {
        if (this.mGet.getApplicationMode() != 0) {
            return null;
        }
        if (this.mGet.getPreferenceGroup() == null || !CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
            CamLog.d(FaceDetector.TAG, "Invalid effect selection");
            return null;
        }
        CamLog.v(FaceDetector.TAG, "current dual camera setting value : " + getEffectSelection(CameraConstants.SMART_MODE_ON));
        return getEffectSelection(CameraConstants.SMART_MODE_ON);
    }

    private Object getEffectSelection(String effectSelection) {
        if (effectSelection.equals(CameraConstants.SMART_MODE_ON)) {
            return CameraConstants.SMART_MODE_ON;
        }
        CamLog.v(FaceDetector.TAG, "Invalid effect selection: " + effectSelection);
        return null;
    }

    public boolean effectsCameraActive() {
        if (!MultimediaProperties.isDualCameraSupported()) {
            CamLog.d(FaceDetector.TAG, "Do not support Dual Camera");
            return false;
        } else if (this.mGet.getApplicationMode() != 0 || readEffectType() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isDualCameraActive() {
        if (!MultimediaProperties.isDualRecordingSupported()) {
            CamLog.d(FaceDetector.TAG, "Do not support Dual Recording");
            return false;
        } else if (readEffectType() == 6) {
            return true;
        } else {
            return false;
        }
    }

    public void showSubWindowResizeHandler(float x, float y) {
        if (this.mPIPCameraController == null) {
            return;
        }
        if (isDualCameraActive() || CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
            int x0 = this.mPIPCameraController.getLeftTopX();
            int y0 = this.mPIPCameraController.getLeftTopY();
            int x1 = this.mPIPCameraController.getRightBottomX();
            int y1 = this.mPIPCameraController.getRightBottomY();
            if (x > ((float) x0) && y > ((float) y0) && x < ((float) x1) && y < ((float) y1) && !this.mPIPCameraController.isResizeHandlerShown()) {
                this.mPIPCameraController.showSubWindowResizeHandler();
            }
        }
    }

    public void hideSubWindowResizeHandler() {
        if (this.mPIPCameraController != null && this.mPIPCameraController.isResizeHandlerShown()) {
            this.mPIPCameraController.hideSubWindowResizeHandler();
            CamLog.d(FaceDetector.TAG, "KMIN - Hide DualRec SubWindow Handler");
        }
    }

    public PIPCameraController getPIPCameraController() {
        return this.mPIPCameraController;
    }

    protected void doOnEffectsUpdate(int effectId, int effectMsg) {
        if (!checkMediator()) {
            CamLog.d(FaceDetector.TAG, "checkMediator() is false");
        } else if (this.mEffectsCamera == null) {
            CamLog.d(FaceDetector.TAG, "mEffectsCamera is null");
        } else {
            CamLog.d(FaceDetector.TAG, "onEffectsUpdate: id:" + effectId + " msg:" + effectMsg);
            if (effectMsg == 3) {
                CamLog.d(FaceDetector.TAG, "-------EFFECT_MSG_EFFECTS_STOPPED");
                if (this.mEffectsCamera.mCalledFrom == 100) {
                    checkQualityAndStartPreview();
                    this.mGet.quickFunctionControllerRefresh(true);
                }
            } else if (effectMsg == 7) {
                CamLog.d(FaceDetector.TAG, "-------EFFECT_MSG_SURFACE_TEARDOWN :" + this.mEffectsCamera.mCalledFrom);
                if (this.mEffectsCamera.mCalledFrom == DialogCreater.DIALOG_ID_HELP_HDR) {
                    this.mGet.doCommandUi(Command.RESET_MENU);
                } else if (this.mEffectsCamera.mCalledFrom == DialogCreater.DIALOG_ID_HELP_PANORAMA) {
                    if (this.mGet.isDualCameraActive()) {
                        this.mGet.doCommand(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
                    }
                    this.mGet.doCommandUi(Command.SWAP_CAMERA);
                }
                this.mEffectsCamera.mCalledFrom = 100;
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
            } else if (effectMsg == 6) {
                CamLog.d(FaceDetector.TAG, "-------EFFECT_MSG_ERROR");
                closeCamera();
                this.mGet.showCameraErrorAndFinish();
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
        } else {
            CamLog.e(FaceDetector.TAG, "Error!", exception);
            closeCamera();
            this.mGet.showCameraStoppedAndFinish();
        }
    }

    public void startPreviewEffect() {
        startPreviewEffect(this.mEffectsCamera, CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)));
    }

    public void stopPreviewEffect() {
        stopPreviewEffect(this.mEffectsCamera);
    }

    protected void checkQualityAndStartPreview() {
        Size previewSize = this.mCameraDevice.getParameters().getPreviewSize();
        if (previewSize == null) {
            CamLog.d(FaceDetector.TAG, "size is null");
            return;
        }
        if (isDualCameraActive()) {
            String videoSize = null;
            if (this.mGet.checkPreviewController()) {
                videoSize = this.mGet.getPreviewSizeOnDevice();
            }
            if (videoSize != null) {
                this.mProfileType = MultimediaProperties.getProfileQulity(this.mGet.getCameraId(), Util.SizeString2WidthHeight(videoSize));
            }
            this.mProfile = CamcorderProfileEx.get(this.mGet.getCameraId(), this.mProfileType);
        }
        CamLog.v(FaceDetector.TAG, "getPreviewSize:" + previewSize.width + "x" + previewSize.height);
        CamLog.v(FaceDetector.TAG, "profile mProfile size:" + this.mProfile.videoFrameWidth + "x" + this.mProfile.videoFrameHeight);
        if (previewSize.height == 1088) {
            previewSize.height = 1080;
        }
        Float profileRatio = Float.valueOf(((float) this.mProfile.videoFrameWidth) / ((float) this.mProfile.videoFrameHeight));
        Float previewRatio = Float.valueOf(((float) previewSize.width) / ((float) previewSize.height));
        if (this.mEffectType != 0) {
            CamLog.d(FaceDetector.TAG, "Call startPreviewEffect in checkQualityAndStartPreview ");
            startPreviewEffect();
        } else if (this.mGet.isPreviewRendered()) {
            getSizeOnScreen = getPreviewSizeOnScreen();
            if (getSizeOnScreen != null) {
                previewSizeOnScreen = Util.SizeString2WidthHeight(getSizeOnScreen);
                CamLog.i(FaceDetector.TAG, "getPreviewSizeOnScreen: " + getSizeOnScreen);
                changePreviewModeOnUiThread(previewSizeOnScreen[0], previewSizeOnScreen[1]);
            }
            this.mGet.setPreviewEffectForBeautyShotMode(null, true);
        } else if (profileRatio.compareTo(previewRatio) != 0) {
            getSizeOnScreen = getPreviewSizeOnScreen();
            if (getSizeOnScreen != null) {
                previewSizeOnScreen = Util.SizeString2WidthHeight(getSizeOnScreen);
                CamLog.i(FaceDetector.TAG, "getPreviewSizeOnScreen: " + getSizeOnScreen);
                changePreviewModeOnUiThread(previewSizeOnScreen[0], previewSizeOnScreen[1]);
            }
        } else {
            startPreview(null, true);
        }
    }

    public void startPIPFrameSubMenuRotation(int degree) {
        this.mGet.startPIPFrameSubMenuRotation(degree);
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        if (this.mGet != null) {
            this.mGet.putPreviewFrameForGesture(data, camera);
        }
    }
}
