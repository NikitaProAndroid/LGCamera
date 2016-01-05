package com.lge.camera.properties;

import android.os.SystemProperties;
import android.util.Log;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public final class ShutterSoundProperties {
    private static boolean mForcedShutterSound;
    private static boolean mShutterSoundOff;

    static {
        mForcedShutterSound = setShutterSound();
        mShutterSoundOff = setShutterSoundOff();
    }

    public static boolean isForcedShutterSound() {
        Log.d(FaceDetector.TAG, "IsForcedShutterSound : " + mForcedShutterSound);
        return mForcedShutterSound;
    }

    public static boolean isSupportShutterSoundOff() {
        return mShutterSoundOff;
    }

    public static int getShutterStreamType() {
        return 7;
    }

    public static boolean setShutterSound() {
        Boolean result = Boolean.valueOf(true);
        String strCountryIso = SystemProperties.get("ro.build.target_country");
        if ("GB".equals(strCountryIso) && "TSC".equals(SystemProperties.get("ro.build.target_operator"))) {
            mForcedShutterSound = true;
            return true;
        } else if ("ESA".equals(strCountryIso) && "IN".equals(SystemProperties.get("ro.build.default_country")) && "ESA".equals(SystemProperties.get("ro.build.target_region"))) {
            mForcedShutterSound = true;
            return true;
        } else if ("AU".equals(SystemProperties.get("ro.build.target_country")) && "ESA".equals(SystemProperties.get("ro.build.target_region"))) {
            mForcedShutterSound = true;
            return true;
        } else if ("SCA".equals(SystemProperties.get("ro.build.target_region")) || SystemProperties.getBoolean("persist.sys.cust.shuttersndoff", false)) {
            return false;
        } else {
            if ("TRF".equals(SystemProperties.get("ro.build.target_operator"))) {
                return false;
            }
            if ("KR".equals(strCountryIso) || "JP".equals(strCountryIso)) {
                result = Boolean.valueOf(true);
            } else if ("CA".equals(strCountryIso) && (ModelProperties.getCarrierCode() == 20 || ModelProperties.getCarrierCode() == 26 || ModelProperties.getCarrierCode() == 27)) {
                result = Boolean.valueOf(true);
            } else if ("IT".equals(strCountryIso) && ModelProperties.getCarrierCode() == 24) {
                result = Boolean.valueOf(true);
            } else if ("PR".equals(strCountryIso)) {
                result = Boolean.valueOf(true);
            } else if ("EU".equals(strCountryIso) || "COM".equals(strCountryIso)) {
                String strNetworkCountryIso = SystemProperties.get("gsm.operator.iso-country", "au");
                String strSimCountryIso = SystemProperties.get("gsm.sim.operator.iso-country", "au");
                if ("kr".equals(strNetworkCountryIso) || "kr".equals(strSimCountryIso)) {
                    result = Boolean.valueOf(true);
                    CamLog.d(FaceDetector.TAG, "setShutterSound KR strCountryIso : " + strCountryIso + " strNetworkCountryIso : " + strNetworkCountryIso + " strSimCountryIso : " + strSimCountryIso);
                } else {
                    result = Boolean.valueOf(false);
                }
            } else {
                result = Boolean.valueOf(false);
            }
            mForcedShutterSound = result.booleanValue();
            return result.booleanValue();
        }
    }

    public static boolean setShutterSoundOff() {
        Boolean result = Boolean.valueOf(true);
        if ("SCA".equals(SystemProperties.get("ro.build.target_region")) || SystemProperties.getBoolean("persist.sys.cust.shuttersndoff", false)) {
            return true;
        }
        if ("ESA".equals(SystemProperties.get("ro.build.target_region")) && "IN".equals(SystemProperties.get("ro.build.default_country")) && "ESA".equals(SystemProperties.get("ro.build.target_country"))) {
            mShutterSoundOff = false;
            return false;
        } else if ("ESA".equals(SystemProperties.get("ro.build.target_region")) && "AU".equals(SystemProperties.get("ro.build.target_country"))) {
            mShutterSoundOff = true;
            return true;
        } else if ("TRF".equals(SystemProperties.get("ro.build.target_operator"))) {
            return true;
        } else {
            String strCountryIso = SystemProperties.get("ro.build.target_country");
            Log.d(FaceDetector.TAG, "strCountryIso : " + strCountryIso);
            if (!isForcedShutterSound() || ModelProperties.getCarrierCode() == 20) {
                if ("AT".equals(strCountryIso) || "BE".equals(strCountryIso) || "BA".equals(strCountryIso) || "BG".equals(strCountryIso) || "HR".equals(strCountryIso) || "CZ".equals(strCountryIso) || "DK".equals(strCountryIso) || "EE".equals(strCountryIso) || "FI".equals(strCountryIso) || "MK".equals(strCountryIso) || "FR".equals(strCountryIso) || "DE".equals(strCountryIso) || "GR".equals(strCountryIso) || "GL".equals(strCountryIso) || "HU".equals(strCountryIso) || "IS".equals(strCountryIso) || "IE".equals(strCountryIso) || "IT".equals(strCountryIso) || "LV".equals(strCountryIso) || "LT".equals(strCountryIso) || "LU".equals(strCountryIso) || "MT".equals(strCountryIso) || "NL".equals(strCountryIso) || "NO".equals(strCountryIso) || "PL".equals(strCountryIso) || "PT".equals(strCountryIso) || "RO".equals(strCountryIso) || "RS".equals(strCountryIso) || "SK".equals(strCountryIso) || "SI".equals(strCountryIso) || "ES".equals(strCountryIso) || "SE".equals(strCountryIso) || "CH".equals(strCountryIso) || "GB".equals(strCountryIso)) {
                    result = Boolean.valueOf(false);
                } else if ("EU".equals(strCountryIso) || "COM".equals(strCountryIso)) {
                    result = Boolean.valueOf(false);
                } else {
                    result = Boolean.valueOf(true);
                }
                mShutterSoundOff = result.booleanValue();
                return result.booleanValue();
            }
            result = Boolean.valueOf(false);
            mShutterSoundOff = result.booleanValue();
            return result.booleanValue();
        }
    }

    public static boolean isDisableAudioFuction() {
        return false;
    }
}
