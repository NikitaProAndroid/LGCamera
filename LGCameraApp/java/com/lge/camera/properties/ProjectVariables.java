package com.lge.camera.properties;

import android.os.SystemProperties;
import android.util.Log;
import com.lge.camera.util.CheckStatusManager;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.BaseEngine;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public final class ProjectVariables {
    public static final String ACTION_CAMERA_HIGH_TEMP_WARN = "com.lge.intent.action.ACTION_THERMALDAEMON_TEMP";
    public static final int FUNCTION_FRONT_CAMERA_FACE_BEAUTY_SHOT = 1;
    public static final int FUNCTION_FRONT_CAMERA_NORMAL = 0;
    public static final int INITIAL_UI_DELAY = 100;
    public static final int KEYCODE_QCLIP_HOT_KEY;
    public static final int QUICK_FUNCTION_COUNT = 5;
    public static boolean bEnterSetting = false;
    public static final long keepDuration = 3000;
    public static long mCaptureStartTime;

    private ProjectVariables() {
    }

    static {
        mCaptureStartTime = 0;
        bEnterSetting = false;
        KEYCODE_QCLIP_HOT_KEY = getQClipHotkey();
    }

    public static boolean showCapturedImageCountInRemainIndicator() {
        return false;
    }

    public static boolean isHwTuning() {
        ModelProperties.getProjectCode();
        return false;
    }

    public static boolean displayVisualVoiceMailIndicator() {
        switch (ModelProperties.getCarrierCode()) {
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean displayMessageIndicator() {
        switch (ModelProperties.getCarrierCode()) {
            case LGKeyRec.EVENT_STOPPED /*4*/:
                if (ModelProperties.getProjectCode() == 7) {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    public static int getContinuousShotTime() {
        switch (ModelProperties.getProjectCode()) {
            case KEYCODE_QCLIP_HOT_KEY:
                return CameraConstants.GALLERY_QUICKVIEW_ANI_INTERVAL;
            default:
                return INITIAL_UI_DELAY;
        }
    }

    public static boolean useContinuousSound() {
        return true;
    }

    public static boolean getUseDeviceKeepForChangeMode() {
        return ModelProperties.isQCTChipset();
    }

    public static boolean isStopPreviewAfterRecordStop() {
        ModelProperties.getProjectCode();
        return false;
    }

    public static boolean hasWrongPreviewWhilePauseBug() {
        return ModelProperties.isQCTChipset() || ModelProperties.isOMAP4Chipset() || ModelProperties.isMTKChipset();
    }

    public static boolean hasWrongPreviewWhileChangingFullHD() {
        switch (ModelProperties.getProjectCode()) {
            case KEYCODE_QCLIP_HOT_KEY:
            case FUNCTION_FRONT_CAMERA_FACE_BEAUTY_SHOT /*1*/:
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case QUICK_FUNCTION_COUNT /*5*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
            case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
            case Ola_ShotParam.ImageEffect_Solarize /*12*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
            case ModelProperties.CODE_GXR /*32*/:
                return true;
            default:
                return false;
        }
    }

    public static int getQClipHotkey() {
        return 165;
    }

    public static int getSettingListHeight() {
        return (ModelProperties.isXGAmodel() || ModelProperties.isUVGAmodel() || ModelProperties.isUWXGAmodel()) ? 8 : QUICK_FUNCTION_COUNT;
    }

    public static boolean isSupportHDMI_MHL() {
        switch (ModelProperties.getProjectCode()) {
            case FUNCTION_FRONT_CAMERA_FACE_BEAUTY_SHOT /*1*/:
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case QUICK_FUNCTION_COUNT /*5*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
            case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
            case Ola_ShotParam.ImageEffect_Solarize /*12*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
            case ModelProperties.CODE_GXR /*32*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSupportManualAntibanding() {
        switch (ModelProperties.getCarrierCode()) {
            case LGKeyRec.EVENT_STOPPED /*4*/:
                if (ModelProperties.getProjectCode() == 0) {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    public static boolean useHideQFLWhenSettingMenuDisplay() {
        return (ModelProperties.isLDPImodel() || ModelProperties.isHVGAmodel() || ModelProperties.isXGAmodel()) ? false : true;
    }

    public static boolean useJpegPictureCallbackError() {
        switch (ModelProperties.getProjectCode()) {
            case KEYCODE_QCLIP_HOT_KEY:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isDisableCheckModifyParameters() {
        return !ModelProperties.isOMAP4Chipset();
    }

    public static boolean stopGalleryCacheduringCameraApp() {
        ModelProperties.getProjectCode();
        return false;
    }

    public static boolean getUseDCFRule() {
        if (isUseNewNamingRule()) {
            return false;
        }
        switch (ModelProperties.getCarrierCode()) {
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                return false;
            default:
                return true;
        }
    }

    public static int getSettingMenuBoarderHeight() {
        if (ModelProperties.isLDPImodel() || ModelProperties.isHVGAmodel()) {
            return KEYCODE_QCLIP_HOT_KEY;
        }
        if (ModelProperties.isWVGAmodel()) {
            return 2;
        }
        return 8;
    }

    public static boolean isDebugNotSupported() {
        switch (ModelProperties.getProjectCode()) {
            case KEYCODE_QCLIP_HOT_KEY:
            case FUNCTION_FRONT_CAMERA_FACE_BEAUTY_SHOT /*1*/:
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
            case Ola_ShotParam.ImageEffect_Solarize /*12*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case Tag.GPS_DEST_LAT_REF /*19*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
            case ModelProperties.CODE_GXR /*32*/:
                if (SystemProperties.getInt("persist.service.main.enable", KEYCODE_QCLIP_HOT_KEY) == 0) {
                    Log.d(FaceDetector.TAG, "####### logServiceEnable = 0 : Log service is disable. Please set log service to enable for debug. ");
                    return true;
                }
                Log.d(FaceDetector.TAG, "####### logServiceEnable = 1 : Log service is enable. You can debug log messages. ");
                return true;
            default:
                return true;
        }
    }

    public static boolean isSupportCameraKey() {
        ModelProperties.getProjectCode();
        return false;
    }

    public static boolean isSupportHardKeyborad() {
        ModelProperties.getProjectCode();
        return false;
    }

    public static int getGestureZoomFactor() {
        return (ModelProperties.isLDPImodel() || ModelProperties.isHVGAmodel()) ? 2 : FUNCTION_FRONT_CAMERA_FACE_BEAUTY_SHOT;
    }

    public static boolean beSupportEulaPopup() {
        switch (ModelProperties.getCarrierCode()) {
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSupportPushContorl() {
        switch (ModelProperties.getProjectCode()) {
            case KEYCODE_QCLIP_HOT_KEY:
            case FUNCTION_FRONT_CAMERA_FACE_BEAUTY_SHOT /*1*/:
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case QUICK_FUNCTION_COUNT /*5*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
            case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
            case Ola_ShotParam.ImageEffect_Solarize /*12*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case Tag.GPS_DEST_LAT_REF /*19*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
            case ModelProperties.CODE_GXR /*32*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean useTurnOffAnimation() {
        return true;
    }

    public static boolean reduceBrightnessCamcorderMode() {
        return false;
    }

    public static boolean isSupportHeat_detection() {
        return true;
    }

    public static int temperatureCheckMethod() {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
            case ModelProperties.CODE_GXR /*32*/:
                break;
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
                if (ModelProperties.getCarrierCode() == 4) {
                    return FUNCTION_FRONT_CAMERA_FACE_BEAUTY_SHOT;
                }
                if (ModelProperties.getCarrierCode() == 7) {
                    return 2;
                }
                break;
        }
        if (ModelProperties.getCarrierCode() == 4) {
            return 2;
        }
        if (ModelProperties.getCarrierCode() == 7) {
            return 2;
        }
        return KEYCODE_QCLIP_HOT_KEY;
    }

    public static final String getThermFile() {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
                if (ModelProperties.getCarrierCode() == 4 || ModelProperties.getCarrierCode() == 7) {
                    return "/sys/class/hwmon/hwmon1/device/xo_therm_pu2";
                }
        }
        return CheckStatusManager.PROPERTY_TEMPERATURE;
    }

    public static boolean isUseFlashTemperature() {
        if (ModelProperties.getCarrierCode() != 4) {
            return false;
        }
        ModelProperties.getProjectCode();
        return true;
    }

    public static boolean useBackLightControl() {
        ModelProperties.getProjectCode();
        return false;
    }

    public static boolean isEnterCameraDuringCall() {
        ModelProperties.getProjectCode();
        return true;
    }

    public static boolean isPostviewDeviceOpenModel() {
        ModelProperties.getProjectCode();
        return true;
    }

    public static boolean isSupportRecordingModePopUp() {
        if (ModelProperties.getCarrierCode() != 10) {
            return false;
        }
        ModelProperties.getProjectCode();
        return true;
    }

    public static boolean isRemoveOrgFile() {
        if (ModelProperties.getCarrierCode() == 4) {
            return true;
        }
        return false;
    }

    public static boolean useToggleSwitcher() {
        return false;
    }

    public static boolean isCheckUSBConfig() {
        ModelProperties.getProjectCode();
        return true;
    }

    public static boolean isUPlusRMSCall() {
        if (ModelProperties.getCarrierCode() != FUNCTION_FRONT_CAMERA_FACE_BEAUTY_SHOT) {
            return false;
        }
        switch (ModelProperties.getProjectCode()) {
            case FUNCTION_FRONT_CAMERA_FACE_BEAUTY_SHOT /*1*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSupportedAutoReview() {
        switch (ModelProperties.getCarrierCode()) {
            case QUICK_FUNCTION_COUNT /*5*/:
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSupportClearView() {
        ModelProperties.getProjectCode();
        return false;
    }

    public static boolean isSupportKDDICleanView() {
        if (ModelProperties.getCarrierCode() != 7) {
            return false;
        }
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isUseNewNamingRule() {
        switch (ModelProperties.getCarrierCode()) {
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                return false;
            default:
                return true;
        }
    }

    public static boolean isAppliedBurstPlayer() {
        switch (ModelProperties.getProjectCode()) {
            case Tag.GPS_DEST_BEAR_REF /*23*/:
                return true;
            default:
                return false;
        }
    }
}
