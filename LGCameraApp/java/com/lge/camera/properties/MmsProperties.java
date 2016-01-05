package com.lge.camera.properties;

import android.content.ContentResolver;
import android.provider.Settings.System;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;

public final class MmsProperties {
    private static final String[][] MMS_RESOLUTION_LIMITS;
    private static final String[] MMS_RESOLUTION_LIMITS_QCIF;
    private static final String[] MMS_RESOLUTION_LIMITS_QVGA;
    private static final String[] MMS_RESOLUTION_LIMITS_VGA;
    private static final String[] MMS_RESOLUTION_NOT_SUPPORT;
    private static final long[] MmsVideoMinimumSize;
    private static final long[] MmsVideoSizeLimit;
    private static String[] mMmsResolutions;

    static {
        MmsVideoMinimumSize = new long[]{MultimediaProperties.SAFE_ATTACH_FILE_MIN_SIZE, 51200, 51200, 51200, 51200, 51200, 51200, 51200, 51200, 51200, 51200, MultimediaProperties.SAFE_ATTACH_FILE_MIN_SIZE, MultimediaProperties.SAFE_ATTACH_FILE_MIN_SIZE, MultimediaProperties.SAFE_ATTACH_FILE_MIN_SIZE, 51200, 51200, 51200, 51200, 51200, 51200, 51200, MultimediaProperties.SAFE_ATTACH_FILE_MIN_SIZE, 51200, 51200, 51200, 51200, 51200, 51200, MultimediaProperties.SAFE_ATTACH_FILE_MIN_SIZE, MultimediaProperties.SAFE_ATTACH_FILE_MIN_SIZE, MultimediaProperties.SAFE_ATTACH_FILE_MIN_SIZE, 51200, 51200, 51200};
        String[][] strArr = new String[34][];
        strArr[0] = new String[]{"320x240", "176x144"};
        strArr[1] = new String[]{"320x240", "176x144"};
        strArr[2] = new String[]{"320x240", "176x144"};
        strArr[3] = new String[]{"320x240", "176x144"};
        strArr[4] = new String[0];
        strArr[5] = new String[]{"176x144"};
        strArr[6] = new String[]{"320x240", "176x144"};
        strArr[7] = new String[0];
        strArr[8] = new String[]{"320x240", "176x144"};
        strArr[9] = new String[]{"320x240", "176x144"};
        strArr[10] = new String[]{"320x240", "176x144"};
        strArr[11] = new String[]{"176x144"};
        strArr[12] = new String[]{"320x240", "176x144"};
        strArr[13] = new String[]{"176x144"};
        strArr[14] = new String[]{"320x240", "176x144"};
        strArr[15] = new String[]{"320x240", "176x144"};
        strArr[16] = new String[]{"320x240", "176x144"};
        strArr[17] = new String[]{"320x240", "176x144"};
        strArr[18] = new String[]{"320x240", "176x144"};
        strArr[19] = new String[]{"320x240", "176x144"};
        strArr[20] = new String[]{"320x240", "176x144"};
        strArr[21] = new String[]{"176x144"};
        strArr[22] = new String[]{"320x240", "176x144"};
        strArr[23] = new String[]{"320x240", "176x144"};
        strArr[24] = new String[]{"320x240", "176x144"};
        strArr[25] = new String[]{"320x240", "176x144"};
        strArr[26] = new String[]{"320x240", "176x144"};
        strArr[27] = new String[]{"320x240", "176x144"};
        strArr[28] = new String[]{"320x240", "176x144"};
        strArr[29] = new String[]{"320x240", "176x144"};
        strArr[30] = new String[]{"320x240", "176x144"};
        strArr[31] = new String[]{"320x240", "176x144"};
        strArr[32] = new String[0];
        strArr[33] = new String[0];
        MMS_RESOLUTION_LIMITS = strArr;
        mMmsResolutions = null;
        MMS_RESOLUTION_LIMITS_QCIF = new String[]{"176x144"};
        MMS_RESOLUTION_LIMITS_QVGA = new String[]{"320x240", "176x144"};
        MMS_RESOLUTION_LIMITS_VGA = new String[]{"640x480", "320x240", "176x144"};
        MMS_RESOLUTION_NOT_SUPPORT = new String[0];
        MmsVideoSizeLimit = new long[]{307200, 1024000, 972800, 1000000, 307200, 1024000, 1228800, 0, 512000, 307200, 1024000, 307200, 1024000, 307200, 614400, 1024000, 307200, 1024000, 1024000, 1024000, 1024000, 614400, 307200, 307200, 307200, 1024000, 307200, 307200, 307200, 307200, 307200, 1024000, 307200, 307200};
    }

    public static long getMmsVideoMinimumSize(ContentResolver cr) {
        long returnSize = MmsVideoMinimumSize[ModelProperties.getCarrierCode()] * ((long) (MultimediaProperties.getMinRecordingTime() / PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME));
        if (returnSize >= getMmsVideoSizeLimit(cr)) {
            return getMmsVideoSizeLimit(cr) - 1024;
        }
        return returnSize;
    }

    public static long getAttachVideoMinimumSize(String resolution, ContentResolver cr) {
        if (resolution.equalsIgnoreCase("1920x1080@60")) {
            return 4710400;
        }
        if (resolution.equalsIgnoreCase("1920x1088") || resolution.equalsIgnoreCase("1920x1080")) {
            return 4710400;
        }
        if (resolution.equalsIgnoreCase("1440x1088")) {
            return 4505000;
        }
        if (resolution.equalsIgnoreCase("1280x720")) {
            return 3276800;
        }
        if (resolution.equalsIgnoreCase("720x480")) {
            return 1228800;
        }
        if (resolution.equalsIgnoreCase("640x480")) {
            return 1024000;
        }
        return getMmsVideoMinimumSize(cr);
    }

    public static String[] getMmsResolutions(ContentResolver cr) {
        if (mMmsResolutions == null) {
            if (!(cr == null || ModelProperties.getCarrierCode() == 4 || ModelProperties.getCarrierCode() == 7 || ModelProperties.getCarrierCode() == 32 || ModelProperties.isWifiOnlyModel(null))) {
                String mmsResolution = System.getString(cr, "android.msg.camera.max.video.resolution");
                if (!"640x480".equals(mmsResolution) && !"320x240".equals(mmsResolution) && !"176x144".equals(mmsResolution)) {
                    mMmsResolutions = MMS_RESOLUTION_LIMITS[ModelProperties.getCarrierCode()];
                } else if ("640x480".equals(mmsResolution)) {
                    mMmsResolutions = MMS_RESOLUTION_LIMITS_VGA;
                } else if ("320x240".equals(mmsResolution)) {
                    mMmsResolutions = MMS_RESOLUTION_LIMITS_QVGA;
                } else {
                    mMmsResolutions = MMS_RESOLUTION_LIMITS_QCIF;
                }
            }
            if (mMmsResolutions == null || ModelProperties.getCarrierCode() == 4 || ModelProperties.getCarrierCode() == 7 || ModelProperties.isWifiOnlyModel(null)) {
                try {
                    if (ModelProperties.isWifiOnlyModel(null)) {
                        mMmsResolutions = MMS_RESOLUTION_NOT_SUPPORT;
                    } else {
                        mMmsResolutions = MMS_RESOLUTION_LIMITS[ModelProperties.getCarrierCode()];
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    mMmsResolutions = MMS_RESOLUTION_LIMITS_QCIF;
                }
            }
        }
        return mMmsResolutions;
    }

    public static int getMmsResolutionsLength(ContentResolver cr) {
        return getMmsResolutions(cr).length;
    }

    public static boolean isAvailableMmsResolution(ContentResolver cr, String sizeValue) {
        String[] mmsResolutions = getMmsResolutions(cr);
        if (mmsResolutions.length == 0) {
            return false;
        }
        boolean available = false;
        for (String equalsIgnoreCase : mmsResolutions) {
            if (sizeValue.equalsIgnoreCase(equalsIgnoreCase)) {
                available = true;
            }
        }
        return available;
    }

    public static long getMmsVideoSizeLimit(ContentResolver cr) {
        String mmsLimit = null;
        if (cr != null) {
            mmsLimit = System.getString(cr, "android.msg.attachment.max.size");
        }
        if (ModelProperties.getCarrierCode() == 1) {
            mmsLimit = "972800";
        }
        if (mmsLimit != null) {
            try {
                return (long) Integer.parseInt(mmsLimit);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return MmsVideoSizeLimit[ModelProperties.getCarrierCode()];
    }
}
