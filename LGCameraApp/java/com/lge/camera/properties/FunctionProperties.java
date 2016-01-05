package com.lge.camera.properties;

import android.os.SystemProperties;
import com.lge.camera.components.RotateView;
import com.lge.config.ConfigBuildFlags;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.BaseEngine;
import com.lge.voiceshutter.library.LGKeyRec;

public final class FunctionProperties {
    public static final int CONTINUOUS_AUTO_FOCUS_NOT_SUPPORTED = 2;
    public static final int CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION = 1;
    public static final int CONTINUOUS_AUTO_FOCUS_WITH_ANIMATION = 0;
    public static boolean isSupportVRPanoramaForSameProjectcode;

    public static boolean isTouchAfSupported(int appMode) {
        String currentModel = ModelProperties.readModelName();
        switch (ModelProperties.getProjectCode()) {
            case Ola_ShotParam.ImageEffect__Max /*17*/:
            case Tag.GPS_PROCESS_METHOD /*27*/:
            case Tag.GPS_AREA_INFO /*28*/:
            case Tag.GPS_DIFF /*30*/:
            case ModelProperties.CODE_L20 /*33*/:
                if (ModelProperties.NAME_L50_TIM.equals(currentModel) || ModelProperties.NAME_L50_OPEN_SCA.equals(currentModel)) {
                    return true;
                }
                return false;
            default:
                return true;
        }
    }

    public static boolean isFrontTouchAESupported() {
        if (!ModelProperties.isJBModel()) {
            return false;
        }
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
            case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
            case Ola_ShotParam.ImageEffect_Solarize /*12*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Ola_ShotParam.Panorama_Begin /*16*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case Tag.GPS_DEST_LAT_REF /*19*/:
            case Tag.GPS_DEST_LAT /*20*/:
            case Tag.GPS_DEST_LON_REF /*21*/:
            case Tag.GPS_DEST_LON /*22*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
            case Tag.GPS_DEST_BEAR /*24*/:
            case Tag.GPS_DEST_DIST_REF /*25*/:
            case Tag.GPS_DEST_DIST /*26*/:
            case Tag.GPS_AREA_INFO /*28*/:
            case Tag.GPS_DATESTAMP /*29*/:
            case ModelProperties.CODE_V7 /*31*/:
            case ModelProperties.CODE_GXR /*32*/:
                return true;
            default:
                return false;
        }
    }

    public static int getCaf(int appMode) {
        if (appMode == 0) {
            switch (ModelProperties.getProjectCode()) {
                case Ola_ShotParam.ImageEffect__Max /*17*/:
                case Tag.GPS_PROCESS_METHOD /*27*/:
                case Tag.GPS_AREA_INFO /*28*/:
                case Tag.GPS_DIFF /*30*/:
                case ModelProperties.CODE_L20 /*33*/:
                case ModelProperties.CODE_V5 /*34*/:
                    return CONTINUOUS_AUTO_FOCUS_NOT_SUPPORTED;
                default:
                    return 0;
            }
        }
        switch (ModelProperties.getProjectCode()) {
            case Ola_ShotParam.ImageEffect__Max /*17*/:
            case Tag.GPS_PROCESS_METHOD /*27*/:
            case Tag.GPS_AREA_INFO /*28*/:
            case Tag.GPS_DIFF /*30*/:
            case ModelProperties.CODE_L20 /*33*/:
                return CONTINUOUS_AUTO_FOCUS_NOT_SUPPORTED;
            default:
                return CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION;
        }
    }

    public static boolean isCafSupported(int appMode, int cameraID) {
        boolean z = true;
        if (cameraID == CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION) {
            return false;
        }
        if (getCaf(appMode) == CONTINUOUS_AUTO_FOCUS_NOT_SUPPORTED) {
            z = false;
        }
        return z;
    }

    public static boolean isCafAnimationSupported(int appMode, int cameraID) {
        boolean z = true;
        if (cameraID == CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION) {
            return false;
        }
        if (getCaf(appMode) != 0) {
            z = false;
        }
        return z;
    }

    public static boolean beSupportCafCallbackFromSensor() {
        ModelProperties.getProjectCode();
        return true;
    }

    public static boolean beSupportMoveCallbackFromSensor() {
        if (ModelProperties.isRenesasISP() || ModelProperties.isFixedFocusModel()) {
            return false;
        }
        ModelProperties.getProjectCode();
        return true;
    }

    public static boolean isSupportAFonCAF() {
        if (!beSupportMoveCallbackFromSensor()) {
            return false;
        }
        switch (ModelProperties.getProjectCode()) {
            case Tag.GPS_DEST_LAT_REF /*19*/:
            case Tag.GPS_PROCESS_METHOD /*27*/:
            case Tag.GPS_AREA_INFO /*28*/:
            case ModelProperties.CODE_L20 /*33*/:
                return false;
            default:
                return true;
        }
    }

    public static int getFunctionFrontCameraBeautyShot() {
        switch (ModelProperties.getProjectCode()) {
            case Ola_ShotParam.Panorama_Begin /*16*/:
            case Ola_ShotParam.ImageEffect__Max /*17*/:
            case Tag.GPS_DEST_LAT /*20*/:
            case Tag.GPS_DEST_LON /*22*/:
            case Tag.GPS_DEST_DIST /*26*/:
            case Tag.GPS_PROCESS_METHOD /*27*/:
            case Tag.GPS_AREA_INFO /*28*/:
            case Tag.GPS_DATESTAMP /*29*/:
            case Tag.GPS_DIFF /*30*/:
            case ModelProperties.CODE_L20 /*33*/:
            case ModelProperties.CODE_V5 /*34*/:
                return 0;
            case Tag.GPS_DEST_LON_REF /*21*/:
                if (!ModelProperties.isDomesticModel()) {
                    return 0;
                }
                break;
        }
        return CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION;
    }

    public static boolean isZslSupported() {
        switch (ModelProperties.getProjectCode()) {
            case CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION /*1*/:
            case CONTINUOUS_AUTO_FOCUS_NOT_SUPPORTED /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case LGKeyRec.EVENT_NEED_MORE_AUDIO /*8*/:
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
            case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
            case Ola_ShotParam.ImageEffect_Solarize /*12*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Ola_ShotParam.Panorama_Begin /*16*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case Tag.GPS_DEST_LAT /*20*/:
            case Tag.GPS_DEST_LON_REF /*21*/:
            case Tag.GPS_DEST_LON /*22*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
            case Tag.GPS_DEST_BEAR /*24*/:
            case Tag.GPS_DEST_DIST_REF /*25*/:
            case Tag.GPS_DEST_DIST /*26*/:
            case Tag.GPS_DATESTAMP /*29*/:
            case ModelProperties.CODE_GXR /*32*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isNonZSLMode() {
        String currentModel = ModelProperties.readModelName();
        switch (ModelProperties.getProjectCode()) {
            case CONTINUOUS_AUTO_FOCUS_NOT_SUPPORTED /*2*/:
            case ModelProperties.CODE_V7 /*31*/:
                if (ModelProperties.NAME_J1_ATT.equals(currentModel) || ModelProperties.NAME_J1_ROGES.equals(currentModel) || ModelProperties.NAME_J1_TELUS.equals(currentModel)) {
                    return false;
                }
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case LGKeyRec.EVENT_NEED_MORE_AUDIO /*8*/:
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case Tag.GPS_PROCESS_METHOD /*27*/:
            case Tag.GPS_AREA_INFO /*28*/:
            case ModelProperties.CODE_GXR /*32*/:
            case ModelProperties.CODE_L20 /*33*/:
                break;
            case Tag.GPS_DEST_DIST_REF /*25*/:
                return true;
            default:
                return false;
        }
        return true;
    }

    public static boolean isSupportVolumeHotKey() {
        switch (ModelProperties.getCarrierCode()) {
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
                return false;
            default:
                return true;
        }
    }

    public static boolean isVoiceShutter() {
        return true;
    }

    public static int voiceShutterKind() {
        switch (ModelProperties.getCarrierCode()) {
            case CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION /*1*/:
            case CONTINUOUS_AUTO_FOCUS_NOT_SUPPORTED /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case ModelProperties.CODE_V7 /*31*/:
                return 0;
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case ModelProperties.CODE_GXR /*32*/:
                return CONTINUOUS_AUTO_FOCUS_NOT_SUPPORTED;
            default:
                return CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION;
        }
    }

    public static int getVoiceShutterSensitivity() {
        switch (ModelProperties.getCarrierCode()) {
            case CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION /*1*/:
            case CONTINUOUS_AUTO_FOCUS_NOT_SUPPORTED /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case ModelProperties.CODE_V7 /*31*/:
                return 0;
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case ModelProperties.CODE_GXR /*32*/:
                return 5;
            default:
                return 20;
        }
    }

    public static boolean useCheeseShutterTitle() {
        switch (ModelProperties.getCarrierCode()) {
            case CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION /*1*/:
            case CONTINUOUS_AUTO_FOCUS_NOT_SUPPORTED /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case Tag.GPS_DEST_LAT_REF /*19*/:
            case ModelProperties.CODE_V7 /*31*/:
            case ModelProperties.CODE_GXR /*32*/:
                return false;
            default:
                return true;
        }
    }

    public static boolean isSupportVoiceShutterJapanese() {
        switch (ModelProperties.getCarrierCode()) {
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case ModelProperties.CODE_GXR /*32*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSupportVoiceShutterAME() {
        if ("AME".equals(SystemProperties.get("ro.build.target_region")) && "AME".equals(SystemProperties.get("ro.build.target_country"))) {
            return true;
        }
        return false;
    }

    public static boolean isAvailableLiveShot() {
        switch (ModelProperties.getProjectCode()) {
            case Tag.GPS_DEST_LAT_REF /*19*/:
            case Tag.GPS_PROCESS_METHOD /*27*/:
            case Tag.GPS_AREA_INFO /*28*/:
            case ModelProperties.CODE_L20 /*33*/:
            case ModelProperties.CODE_V5 /*34*/:
                return false;
            default:
                return true;
        }
    }

    public static boolean useShutterSoundInLiveShot() {
        if (!isAvailableLiveShot()) {
            return false;
        }
        switch (ModelProperties.getCarrierCode()) {
            case LGKeyRec.EVENT_STOPPED /*4*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isVideoStabilizationSupported() {
        switch (ModelProperties.getProjectCode()) {
            case CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION /*1*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case LGKeyRec.EVENT_NEED_MORE_AUDIO /*8*/:
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case ModelProperties.CODE_GXR /*32*/:
                return true;
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
                if (ModelProperties.getCarrierCode() != 7) {
                    return false;
                }
                return true;
            default:
                return false;
        }
    }

    public static boolean isTimeMachinShotSupported() {
        switch (ModelProperties.getProjectCode()) {
            case CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION /*1*/:
            case CONTINUOUS_AUTO_FOCUS_NOT_SUPPORTED /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
            case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
            case Ola_ShotParam.ImageEffect_Solarize /*12*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Ola_ShotParam.Panorama_Begin /*16*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case Tag.GPS_DEST_LON_REF /*21*/:
            case Tag.GPS_DEST_LON /*22*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
            case Tag.GPS_DEST_BEAR /*24*/:
            case ModelProperties.CODE_GXR /*32*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isRefocusShotSupported() {
        switch (ModelProperties.getProjectCode()) {
            case Tag.GPS_DEST_BEAR_REF /*23*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isFullFrameContinuousShotSupported() {
        switch (ModelProperties.getProjectCode()) {
            case CONTINUOUS_AUTO_FOCUS_NOT_SUPPORTED /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
            case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
            case Ola_ShotParam.ImageEffect_Solarize /*12*/:
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
            case Ola_ShotParam.Panorama_Begin /*16*/:
            case Tag.GPS_DEST_LAT /*20*/:
            case Tag.GPS_DEST_LON_REF /*21*/:
            case Tag.GPS_DEST_LON /*22*/:
            case Tag.GPS_DEST_BEAR /*24*/:
            case Tag.GPS_DEST_DIST_REF /*25*/:
            case Tag.GPS_DEST_DIST /*26*/:
            case Tag.GPS_AREA_INFO /*28*/:
            case Tag.GPS_DATESTAMP /*29*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isTimeMachineShotSizeLimit() {
        ModelProperties.getProjectCode();
        return false;
    }

    public static boolean isTimeMachineShotSizeAvailable(String size) {
        if (size.equals("3264x1836") || size.equals("3264x2448") || size.equals("3200x1920")) {
            return false;
        }
        return true;
    }

    public static boolean useTimeCatchShotTitle() {
        return !ModelProperties.isDomesticModel();
    }

    public static boolean isSupportShutterButtonBurst() {
        return true;
    }

    public static boolean isSupportZoomOnRecord() {
        return true;
    }

    public static boolean isUPlusBox() {
        switch (ModelProperties.getCarrierCode()) {
            case CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION /*1*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSupportObjectTracking() {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
            case ModelProperties.CODE_GXR /*32*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSupportSmartMode() {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
            case ModelProperties.CODE_GXR /*32*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSupportBurstShot() {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
            case ModelProperties.CODE_GXR /*32*/:
                return true;
            default:
                return ConfigBuildFlags.CAPP_CAMERA_BURSTSHOT;
        }
    }

    public static boolean isSupportQClipCustomization() {
        ModelProperties.getProjectCode();
        return true;
    }

    public static boolean isSupportCameraCleanGuide() {
        switch (ModelProperties.getCarrierCode()) {
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                return false;
            default:
                return true;
        }
    }

    public static boolean isSupportRotateSaveImage() {
        ModelProperties.getProjectCode();
        return true;
    }

    public static boolean isWDRSupported() {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case ModelProperties.CODE_GXR /*32*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isHDRRecordingNameUsed() {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
                return true;
            default:
                return false;
        }
    }

    static {
        isSupportVRPanoramaForSameProjectcode = false;
    }

    public static boolean isFreePanoramaSupported() {
        if ("yes".equals(SystemProperties.get("ro.build.new_function_disabled"))) {
            return false;
        }
        if (isSupportVRPanoramaForSameProjectcode) {
            return true;
        }
        switch (ModelProperties.getProjectCode()) {
            case CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION /*1*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
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

    public static boolean isSmartShutterSupported() {
        ModelProperties.getProjectCode();
        return false;
    }

    public static boolean isDateStampSupported() {
        ModelProperties.getProjectCode();
        return false;
    }

    public static boolean isSupportAudiozoom() {
        String currentModel = ModelProperties.readModelName();
        if ("yes".equals(SystemProperties.get("ro.build.new_function_disabled"))) {
            return false;
        }
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
            case ModelProperties.CODE_GXR /*32*/:
                return true;
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
                if (ModelProperties.NAME_G2_KDDI.equals(currentModel)) {
                    return false;
                }
                return true;
            default:
                return false;
        }
    }

    public static boolean isSupportGuideFlash() {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case ModelProperties.CODE_GXR /*32*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isNoneFlashModel() {
        switch (ModelProperties.getProjectCode()) {
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
            case Ola_ShotParam.ImageEffect__Max /*17*/:
            case Tag.GPS_PROCESS_METHOD /*27*/:
            case Tag.GPS_DIFF /*30*/:
            case ModelProperties.CODE_L20 /*33*/:
                return true;
            default:
                return false;
        }
    }

    public static String getFlashDefaultValue() {
        switch (ModelProperties.getCarrierCode()) {
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
                return LGT_Limit.ISP_AUTOMODE_AUTO;
            default:
                return CameraConstants.SMART_MODE_OFF;
        }
    }

    public static boolean isAutoFocusNullSettingNeededInStartPreview() {
        if (!isSupportAFonCAF()) {
            return false;
        }
        switch (ModelProperties.getProjectCode()) {
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean useFaceDetectionFromHal() {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Ola_ShotParam.Panorama_Begin /*16*/:
            case Tag.GPS_DEST_LAT /*20*/:
            case Tag.GPS_DEST_LON_REF /*21*/:
            case Tag.GPS_DEST_LON /*22*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
            case Tag.GPS_DEST_BEAR /*24*/:
            case Tag.GPS_DEST_DIST_REF /*25*/:
            case Tag.GPS_DEST_DIST /*26*/:
            case Tag.GPS_DATESTAMP /*29*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isFaceDetectionAuto() {
        if (!useFaceDetectionFromHal()) {
            return false;
        }
        switch (ModelProperties.getProjectCode()) {
            case CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION /*1*/:
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect__Max /*17*/:
                return false;
            default:
                return true;
        }
    }

    public static boolean isClearShotSupported() {
        switch (ModelProperties.getProjectCode()) {
            case CONTINUOUS_AUTO_FOCUS_NOT_SUPPORTED /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
            case Ola_ShotParam.ImageEffect_Solarize /*12*/:
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
            case Ola_ShotParam.Panorama_Begin /*16*/:
            case Ola_ShotParam.ImageEffect__Max /*17*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case Tag.GPS_DEST_LAT_REF /*19*/:
            case Tag.GPS_DEST_LAT /*20*/:
            case Tag.GPS_DEST_LON_REF /*21*/:
            case Tag.GPS_DEST_LON /*22*/:
            case Tag.GPS_DEST_BEAR /*24*/:
            case Tag.GPS_DEST_DIST_REF /*25*/:
            case Tag.GPS_DEST_DIST /*26*/:
            case Tag.GPS_PROCESS_METHOD /*27*/:
            case Tag.GPS_AREA_INFO /*28*/:
            case Tag.GPS_DATESTAMP /*29*/:
            case Tag.GPS_DIFF /*30*/:
            case ModelProperties.CODE_V7 /*31*/:
            case ModelProperties.CODE_GXR /*32*/:
            case ModelProperties.CODE_L20 /*33*/:
            case ModelProperties.CODE_V5 /*34*/:
                return false;
            default:
                return true;
        }
    }

    public static boolean isSupportHelpSetting() {
        String currentModel = ModelProperties.readModelName();
        switch (ModelProperties.getCarrierCode()) {
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                if (ModelProperties.NAME_X3_TRF_VZW.equals(currentModel) || ModelProperties.NAME_W3_TRF_VZW.equals(currentModel) || ModelProperties.NAME_W5_TCF_VZW.equals(currentModel)) {
                    return false;
                }
                return true;
            default:
                return false;
        }
    }

    public static boolean isSupportCameraHandGuide() {
        switch (ModelProperties.getCarrierCode()) {
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                return false;
            default:
                return true;
        }
    }

    public static boolean isSuperZoomSupported() {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isHideAudiozoomMenu() {
        String currentModel = ModelProperties.readModelName();
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
                if (ModelProperties.NAME_G2_KDDI.equals(currentModel)) {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    public static boolean isSupportedVideoFlashAuto() {
        switch (ModelProperties.getProjectCode()) {
            case CONTINUOUS_AUTO_FOCUS_NOT_SUPPORTED /*2*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
            case Ola_ShotParam.ImageEffect__Max /*17*/:
            case Tag.GPS_PROCESS_METHOD /*27*/:
            case Tag.GPS_AREA_INFO /*28*/:
            case Tag.GPS_DIFF /*30*/:
            case ModelProperties.CODE_V7 /*31*/:
            case ModelProperties.CODE_L20 /*33*/:
            case ModelProperties.CODE_V5 /*34*/:
                return false;
            default:
                return true;
        }
    }

    public static boolean isSupportEmotionalLED() {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSupportMorphoNightShot() {
        ModelProperties.getProjectCode();
        return true;
    }

    public static boolean isSupportNightShotModeMenu(int cameraId) {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
            case Ola_ShotParam.Panorama_Begin /*16*/:
            case Ola_ShotParam.ImageEffect__Max /*17*/:
            case Tag.GPS_DEST_LAT /*20*/:
            case Tag.GPS_DEST_LON_REF /*21*/:
            case Tag.GPS_DEST_LON /*22*/:
            case Tag.GPS_DEST_BEAR /*24*/:
            case Tag.GPS_DEST_DIST_REF /*25*/:
            case Tag.GPS_DEST_DIST /*26*/:
            case Tag.GPS_PROCESS_METHOD /*27*/:
            case Tag.GPS_AREA_INFO /*28*/:
            case Tag.GPS_DATESTAMP /*29*/:
            case Tag.GPS_DIFF /*30*/:
            case ModelProperties.CODE_L20 /*33*/:
            case ModelProperties.CODE_V5 /*34*/:
                return false;
            case Tag.GPS_DEST_BEAR_REF /*23*/:
                return cameraId == CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION;
            default:
                return true;
        }
    }

    public static boolean isSupportSportShot() {
        switch (ModelProperties.getProjectCode()) {
            case Ola_ShotParam.ImageEffect__Max /*17*/:
            case Tag.GPS_DEST_DIST_REF /*25*/:
            case Tag.GPS_PROCESS_METHOD /*27*/:
            case Tag.GPS_AREA_INFO /*28*/:
            case Tag.GPS_DIFF /*30*/:
            case ModelProperties.CODE_V7 /*31*/:
            case ModelProperties.CODE_L20 /*33*/:
            case ModelProperties.CODE_V5 /*34*/:
                return false;
            default:
                return true;
        }
    }

    public static boolean isPlanePanoramaPreviewInput() {
        ModelProperties.getProjectCode();
        if (isZslSupported()) {
            return false;
        }
        return true;
    }

    public static boolean isSupportLightFrame() {
        String currentModel = ModelProperties.readModelName();
        if (!ModelProperties.isSupportFrontCameraModel()) {
            return false;
        }
        switch (ModelProperties.getProjectCode()) {
            case CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION /*1*/:
            case CONTINUOUS_AUTO_FOCUS_NOT_SUPPORTED /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
            case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
            case Ola_ShotParam.ImageEffect_Solarize /*12*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case ModelProperties.CODE_V7 /*31*/:
                return false;
            case Tag.GPS_DEST_LON /*22*/:
                if (ModelProperties.NAME_X5_VZW.equals(currentModel)) {
                    return false;
                }
                return true;
            default:
                return true;
        }
    }

    public static float getPlanePanoramaGuideAreaRatioHorHor() {
        switch (ModelProperties.getProjectCode()) {
            case Ola_ShotParam.Panorama_Begin /*16*/:
                return RotateView.DEFAULT_TEXT_SCALE_X;
            default:
                return 0.9f;
        }
    }

    public static float getPlanePanoramaGuideAreaRatioHorVer() {
        ModelProperties.getProjectCode();
        return 0.8f;
    }

    public static boolean isPlanePanoramaSupported() {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_INVALID /*0*/:
            case CONTINUOUS_AUTO_FOCUS_WITHOUT_ANIMATION /*1*/:
            case CONTINUOUS_AUTO_FOCUS_NOT_SUPPORTED /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
            case LGKeyRec.EVENT_NEED_MORE_AUDIO /*8*/:
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
            case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
            case Ola_ShotParam.ImageEffect_Solarize /*12*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
            case Ola_ShotParam.ImageEffect__Max /*17*/:
            case Tag.GPS_MAP_DATUM /*18*/:
            case Tag.GPS_DEST_LAT_REF /*19*/:
            case Tag.GPS_PROCESS_METHOD /*27*/:
            case Tag.GPS_DIFF /*30*/:
            case ModelProperties.CODE_V7 /*31*/:
            case ModelProperties.CODE_GXR /*32*/:
            case ModelProperties.CODE_L20 /*33*/:
            case ModelProperties.CODE_V5 /*34*/:
                return false;
            default:
                return true;
        }
    }

    public static boolean isSupportedTagLocation() {
        if ("CN".equals(SystemProperties.get("ro.build.target_country"))) {
            return false;
        }
        return true;
    }

    public static boolean isSupportedRotationWithoutAccelerometer() {
        switch (ModelProperties.getProjectCode()) {
            case ModelProperties.CODE_L20 /*33*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSupportedGestureShot() {
        return true;
    }
}
