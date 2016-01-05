package com.lge.camera.properties;

import android.os.SystemProperties;
import com.lge.camera.util.Common;
import com.lge.media.CamcorderProfileEx;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.BaseEngine;
import com.lge.voiceshutter.library.ISTAudioRecorder;
import com.lge.voiceshutter.library.LGKeyRec;

public final class MultimediaProperties {
    public static final int CAMCORDERPROFILE_QUALITY960P = 14;
    public static String DUALREC_DEFAULT_VIDEO_SIZE = null;
    public static String DUALREC_VIDEO_SIZE_LISTED_ON_MENU = null;
    public static final int DUAL_MODE_VIDEO_FRAMERATE_RANGE_MAX = 30000;
    public static final int DUAL_MODE_VIDEO_FRAMERATE_RANGE_MIN = 30000;
    public static final String EFFECTS_ENFORCED_SIZE_FOR_UVGA = "1280x960";
    public static final String IMAGE_MIME_TYPE = "image/jpeg";
    private static final String[] LIVEEFFECT_RESOLUTION_LIMITS_STARNDARD;
    private static final String[] LIVEEFFECT_RESOLUTION_LIMITS_VARIATION_2;
    private static final String[] LIVEEFFECT_RESOLUTION_LIMITS_VARIATION_3;
    public static final String MEDIA_EJECT = "eject";
    public static final int MEDIA_RECORDER_ERROR_RESOURCE = 2;
    public static final int MEDIA_RECORDER_INFO_PROGRESS_TIME_DURATION = 1003;
    public static final int MEDIA_RECORDER_INFO_PROGRESS_TIME_STATUS = 804;
    public static final int MEDIA_RECORDER_INFO_TOTAL_DURATION = 805;
    public static boolean PIP_MOVE_ALLOWED_ONLY_IN_EDIT_MODE = false;
    public static int PIP_SUBWINDOW_INIT_POSITION = 0;
    public static boolean PIP_SUPPORT_REALTIME_WINDOW_UPDATE = false;
    public static boolean PIP_TOGGLE_ALLOWED_IN_EDIT_MODE = false;
    public static final long SAFE_ATTACH_FILE_MIN_SIZE = 30720;
    public static String SMARTZOOM_DEFAULT_VIDEO_SIZE = null;
    public static int SMARTZOOM_FOCUS_MODE = 0;
    public static final String SMARTZOOM_PREVIEWSIZE_SET_ON_DEVICE = "2104x1184";
    public static final String SMARTZOOM_PREVIEWSIZE_SET_ON_DEVICE_FOR_UVGA = "2104x1560";
    public static String SMARTZOOM_VIDEO_SIZE_LISTED_ON_MENU = null;
    public static final int VALUE_VIDEO_FRAMERATE_FHD_FORNVIDIA = 24000;
    public static final int VALUE_VIDEO_FRAMERATE_FORMTK_MIN = 20000;
    public static final int VALUE_VIDEO_FRAMERATE_MMS_RANGE = 15000;
    public static final int VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX = 30000;
    public static final int VALUE_VIDEO_FRAMERATE_VARIABLE_RANGE_MIN = 10000;
    public static final String VIDEO_MIME_TYPE = "video/mp4";

    public static int getMinRecordingTime() {
        return Common.NO_BUTTON_POPUP_DISMISS_DELAY;
    }

    public static String getVideoMimeType(String postfix) {
        switch (ModelProperties.getCarrierCode()) {
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
            case MEDIA_RECORDER_ERROR_RESOURCE /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case ModelProperties.CODE_V7 /*31*/:
                return "video/3gpp";
            default:
                if (SystemProperties.get("ro.build.target_country").equals("AU")) {
                    return "video/3gpp";
                }
                return VIDEO_MIME_TYPE;
        }
    }

    public static int getMmsVideoEncodingType() {
        switch (ModelProperties.getCarrierCode()) {
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
                return MEDIA_RECORDER_ERROR_RESOURCE;
            default:
                return 3;
        }
    }

    public static int getMmsAudioEncodingType() {
        switch (ModelProperties.getCarrierCode()) {
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
                return 3;
            default:
                return 1;
        }
    }

    public static long getMediaRecoderLimitSize() {
        return 4294967295L;
    }

    public static int getMMSMaxDuration() {
        switch (ModelProperties.getCarrierCode()) {
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                return 60000;
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
                return VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX;
            default:
                return -1;
        }
    }

    public static final int getCameraFrameRateNormalRangeMin() {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
                return 6000;
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
                return 8000;
            case Tag.GPS_DEST_LAT_REF /*19*/:
                return CameraConstants.PIP_SUBWINDOW_RESIZE_HANDLER_DURATION;
            case Tag.GPS_DEST_BEAR_REF /*23*/:
                return 9000;
            case ModelProperties.CODE_V5 /*34*/:
                return VALUE_VIDEO_FRAMERATE_MMS_RANGE;
            default:
                return VALUE_VIDEO_FRAMERATE_VARIABLE_RANGE_MIN;
        }
    }

    public static final int getFrontCameraFrameRateNormalRangeMin() {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
                return ISTAudioRecorder.JAVA_BUFFER_INTERVAL;
            case Tag.GPS_DEST_LAT_REF /*19*/:
                return CameraConstants.PIP_SUBWINDOW_RESIZE_HANDLER_DURATION;
            case Tag.GPS_DEST_BEAR_REF /*23*/:
                return 12000;
            default:
                return VALUE_VIDEO_FRAMERATE_VARIABLE_RANGE_MIN;
        }
    }

    public static final int getCameraFrameRateNormalRangeMax() {
        return VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX;
    }

    public static final int getCameraFrameRateNightModeRangeMin() {
        switch (ModelProperties.getProjectCode()) {
            case Tag.GPS_DEST_BEAR_REF /*23*/:
                return 7000;
            default:
                return 6000;
        }
    }

    public static final int getFrontCameraFrameRateNightModeRangeMin() {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
            case Ola_ShotParam.ImageEffect_Glow /*13*/:
            case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
                return ISTAudioRecorder.JAVA_BUFFER_INTERVAL;
            case Tag.GPS_DEST_BEAR_REF /*23*/:
                return VALUE_VIDEO_FRAMERATE_VARIABLE_RANGE_MIN;
            default:
                return CameraConstants.PIP_SUBWINDOW_RESIZE_HANDLER_DURATION;
        }
    }

    public static final int getCameraFrameRateIAModeRangeMin() {
        return 6000;
    }

    public static final int getCameraFrameRateBurstShotModeRangeMin() {
        return VALUE_VIDEO_FRAMERATE_MMS_RANGE;
    }

    public static int getStartRecordingSoundDelay() {
        return CameraConstants.TIME_MACHINE_ANI_INTERVAL;
    }

    public static boolean isLiveEffectSupported() {
        switch (ModelProperties.getProjectCode()) {
            case LGKeyRec.EVENT_INVALID /*0*/:
            case Ola_ShotParam.ImageEffect__Max /*17*/:
            case Tag.GPS_DEST_LAT_REF /*19*/:
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

    static {
        String[] strArr = new String[MEDIA_RECORDER_ERROR_RESOURCE];
        strArr[0] = "720x480";
        strArr[1] = "720x480";
        LIVEEFFECT_RESOLUTION_LIMITS_STARNDARD = strArr;
        strArr = new String[MEDIA_RECORDER_ERROR_RESOURCE];
        strArr[0] = "720x480";
        strArr[1] = "640x480";
        LIVEEFFECT_RESOLUTION_LIMITS_VARIATION_2 = strArr;
        strArr = new String[MEDIA_RECORDER_ERROR_RESOURCE];
        strArr[0] = EFFECTS_ENFORCED_SIZE_FOR_UVGA;
        strArr[1] = EFFECTS_ENFORCED_SIZE_FOR_UVGA;
        LIVEEFFECT_RESOLUTION_LIMITS_VARIATION_3 = strArr;
        PIP_SUPPORT_REALTIME_WINDOW_UPDATE = false;
        PIP_SUBWINDOW_INIT_POSITION = 3;
        PIP_MOVE_ALLOWED_ONLY_IN_EDIT_MODE = false;
        PIP_TOGGLE_ALLOWED_IN_EDIT_MODE = true;
        DUALREC_VIDEO_SIZE_LISTED_ON_MENU = "1280x720,720x480";
        DUALREC_DEFAULT_VIDEO_SIZE = "1280x720";
        SMARTZOOM_VIDEO_SIZE_LISTED_ON_MENU = "1280x720,720x480";
        SMARTZOOM_DEFAULT_VIDEO_SIZE = "1280x720";
        SMARTZOOM_FOCUS_MODE = 3;
    }

    public static String getLiveEffectPreviewOnDevice(int cam_mode) {
        if (ModelProperties.isLDPImodel()) {
            return "320x214";
        }
        if (ModelProperties.isHVGAmodel()) {
            return "480x320";
        }
        if (ModelProperties.isWVGAmodel()) {
            if (isLiveEffectResolutionLimitVariation2(ModelProperties.readModelName())) {
                return cam_mode == 0 ? "720x480" : "640x480";
            } else {
                return "720x480";
            }
        } else if (ModelProperties.isQHDmodel()) {
            if (isLiveEffectResolutionLimitVariation2(ModelProperties.readModelName())) {
                return cam_mode == 0 ? "810x540" : "720x540";
            } else {
                return "810x540";
            }
        } else if (ModelProperties.isXGAmodel()) {
            return "1024x682";
        } else {
            if (ModelProperties.isHDmodel()) {
                if (ModelProperties.isSoftKeyNavigationBarModel()) {
                    return "1072x714";
                }
                return "1080x720";
            } else if (ModelProperties.isWXGAmodel()) {
                return "1080x720";
            } else {
                if (ModelProperties.isUVGAmodel()) {
                    return EFFECTS_ENFORCED_SIZE_FOR_UVGA;
                }
                if (ModelProperties.isFHDmodel()) {
                    if (ModelProperties.isSoftKeyNavigationBarModel()) {
                        return "1608x1072";
                    }
                    return "1620x1080";
                } else if (!ModelProperties.isUWXGAmodel()) {
                    return "1620x1080";
                } else {
                    if (ModelProperties.isSoftKeyNavigationBarModel()) {
                        return "1686x1124";
                    }
                    return "1800x1200";
                }
            }
        }
    }

    public static String getLiveeffectResolutions(int cam_mode) {
        if (isLiveEffectResolutionLimitVariation2(ModelProperties.readModelName())) {
            return LIVEEFFECT_RESOLUTION_LIMITS_VARIATION_2[cam_mode];
        }
        if (ModelProperties.isUVGAmodel()) {
            return LIVEEFFECT_RESOLUTION_LIMITS_VARIATION_3[cam_mode];
        }
        return LIVEEFFECT_RESOLUTION_LIMITS_STARNDARD[cam_mode];
    }

    public static boolean isAvailableLiveeffectResolution(String sizeValue, int cam_mode) {
        String liveeffectResolutions;
        if (isLiveEffectResolutionLimitVariation2(ModelProperties.readModelName())) {
            liveeffectResolutions = LIVEEFFECT_RESOLUTION_LIMITS_VARIATION_2[cam_mode];
        } else if (ModelProperties.isUVGAmodel()) {
            liveeffectResolutions = LIVEEFFECT_RESOLUTION_LIMITS_VARIATION_3[cam_mode];
        } else {
            liveeffectResolutions = LIVEEFFECT_RESOLUTION_LIMITS_STARNDARD[cam_mode];
        }
        if (sizeValue.equalsIgnoreCase(liveeffectResolutions)) {
            return true;
        }
        return false;
    }

    private static boolean isLiveEffectResolutionLimitVariation2(String model) {
        if (ModelProperties.getProjectCode() == 6 || ModelProperties.getProjectCode() == 21 || ((ModelProperties.getProjectCode() == 22 && (ModelProperties.NAME_X5_VZW.equals(model) || ModelProperties.NAME_X5_LRA.equals(model))) || ModelProperties.getProjectCode() == 20 || ModelProperties.getProjectCode() == 29 || ModelProperties.getProjectCode() == 26 || (ModelProperties.getProjectCode() == 16 && (ModelProperties.NAME_W7_OPEN.equals(model) || ModelProperties.NAME_W7_TMUS.equals(model) || ModelProperties.NAME_W7_TMUS_BK.equals(model) || ModelProperties.NAME_W7_EU.equals(model) || ModelProperties.NAME_W7_TR.equals(model) || ModelProperties.NAME_W7_ISR.equals(model) || ModelProperties.NAME_W7_MPCS.equals(model) || ModelProperties.NAME_W7_CIS.equals(model))))) {
            return true;
        }
        return false;
    }

    public static String getLiveEffectInSpacePath() {
        ModelProperties.getProjectCode();
        return "file:///system/media/video/AndroidInSpace.480p.mp4";
    }

    public static String getLiveEffectSunSetPath() {
        ModelProperties.getProjectCode();
        return "file:///system/media/video/Sunset.480p.mp4";
    }

    public static String getLiveEffectDiscoPath() {
        ModelProperties.getProjectCode();
        return "file:///system/media/video/Disco.480p.mp4";
    }

    public static String getDualRecordingResolution(String profileVideoSize) {
        String retSize = profileVideoSize;
        if ("1920x1080".equals(retSize)) {
            return "1920x1088";
        }
        return retSize;
    }

    public static boolean isDualRecordingSupported() {
        if ("yes".equals(SystemProperties.get("ro.build.new_function_disabled"))) {
            return false;
        }
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

    public static boolean isDualCameraSupported() {
        if ("yes".equals(SystemProperties.get("ro.build.new_function_disabled"))) {
            return false;
        }
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

    public static boolean isPauseAndResumeSupported() {
        return true;
    }

    public static int getProfileQulity(int cameraId, int[] size) {
        if (size == null) {
            return 7;
        }
        if (size[1] == 2160 && CamcorderProfileEx.hasProfile(cameraId, 8)) {
            return 8;
        }
        if ((size[1] == 1080 || size[1] == 1088) && CamcorderProfileEx.hasProfile(cameraId, 6)) {
            return 6;
        }
        if (size[1] == LGT_Limit.IMAGE_SIZE_WALLPAPER_WIDTH && CamcorderProfileEx.hasProfile(cameraId, CAMCORDERPROFILE_QUALITY960P)) {
            return CAMCORDERPROFILE_QUALITY960P;
        }
        if (size[1] == 720 && CamcorderProfileEx.hasProfile(cameraId, 5)) {
            return 5;
        }
        if (size[1] == LGT_Limit.PREVIEW_SIZE_HEIGHT && CamcorderProfileEx.hasProfile(cameraId, 4)) {
            return 4;
        }
        if (size[1] == Ola_ShotParam.Sampler_Complete && CamcorderProfileEx.hasProfile(cameraId, 7)) {
            return 7;
        }
        if (size[1] == 144 && CamcorderProfileEx.hasProfile(cameraId, MEDIA_RECORDER_ERROR_RESOURCE)) {
            return MEDIA_RECORDER_ERROR_RESOURCE;
        }
        return 7;
    }

    public static int getBitrate(int cameraId, int quality) {
        CamcorderProfileEx profile = CamcorderProfileEx.get(cameraId, quality);
        if (profile != null) {
            return profile.videoBitRate;
        }
        return -1;
    }

    public static int getHFRBitrate(int cameraId) {
        if (!CamcorderProfileEx.hasProfile(cameraId, 2004)) {
            return 30000000;
        }
        CamcorderProfileEx profile = CamcorderProfileEx.get(cameraId, 2004);
        if (profile != null) {
            return profile.videoBitRate;
        }
        return 30000000;
    }

    public static String getSmartZoomResolution(String profileVideoSize) {
        String retSize = profileVideoSize;
        if ("1920x1080".equals(retSize)) {
            return "1920x1088";
        }
        return retSize;
    }

    public static boolean isSmartZoomSupported() {
        if ("yes".equals(SystemProperties.get("ro.build.new_function_disabled"))) {
            return false;
        }
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

    public static boolean isHighFramRateVideoSupported() {
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
}
