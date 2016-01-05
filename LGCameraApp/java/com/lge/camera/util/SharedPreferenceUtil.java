package com.lge.camera.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import com.lge.camera.setting.Setting;
import com.lge.olaworks.library.FaceDetector;

public class SharedPreferenceUtil {
    private static final int ERROR_VALUE = -1;
    private static final String KEY_PREVIOUS_PICTURE_SIZE = "previous_picture_size";

    public static void saveAccumulatedPictureCount(Context c, int storage, long count) {
        String storageString;
        if (storage == 1) {
            storageString = "internal";
        } else {
            storageString = "external";
        }
        Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
        editor.putLong(String.format("picture_number_%s", new Object[]{storageString}), count);
        editor.apply();
        CamLog.i(FaceDetector.TAG, "saved picture counter = " + count);
    }

    public static long getAccumulatedPictureCount(Context c, int storage) {
        String storageString;
        if (storage == 1) {
            storageString = "internal";
        } else {
            storageString = "external";
        }
        return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getLong(String.format("picture_number_%s", new Object[]{storageString}), 0);
    }

    public static void saveAccumulatedVideoCount(Context c, int storage, long count) {
        String storageString;
        if (storage == 1) {
            storageString = "internal";
        } else {
            storageString = "external";
        }
        Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
        editor.putLong(String.format("video_number_%s", new Object[]{storageString}), count);
        editor.apply();
        CamLog.i(FaceDetector.TAG, "saved video counter = " + count);
    }

    public static void saveAccumulatedDCFCount(Context c, long count) {
        Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
        editor.putLong(String.format("dcf_count", new Object[0]), count);
        editor.apply();
        CamLog.i(FaceDetector.TAG, "saved counter = " + count);
    }

    public static void saveAccumulatedDCFFirstCount(Context c, int firstCount) {
        Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
        editor.putInt(String.format("dcf_first_number", new Object[0]), firstCount);
        editor.apply();
        CamLog.i(FaceDetector.TAG, "saved counter = " + firstCount);
    }

    public static void saveAccumulatedDCFDigit(Context c, int digit) {
        Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
        editor.putInt(String.format("dcf_digit", new Object[0]), digit);
        editor.apply();
        CamLog.i(FaceDetector.TAG, "saved counter = " + digit);
    }

    public static long getAccumulatedDCFCount(Context c) {
        return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getLong(String.format("dcf_count", new Object[0]), 0);
    }

    public static int getAccumulatedDCFFirstCount(Context c) {
        return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getInt(String.format("dcf_first_number", new Object[0]), ERROR_VALUE);
    }

    public static int getAccumulatedDCFDigit(Context c) {
        return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getInt(String.format("dcf_digit", new Object[0]), 0);
    }

    public static long getAccumulatedVideoCount(Context c, int storage) {
        String storageString;
        if (storage == 1) {
            storageString = "internal";
        } else {
            storageString = "external";
        }
        return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getLong(String.format("video_number_%s", new Object[]{storageString}), 0);
    }

    public static int getLastCameraMode(Context c) {
        return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getInt("entermode", 0);
    }

    public static int getLastSecondaryCameraMode(Context c) {
        return c.getSharedPreferences(Setting.SETTING_SECONDARY, 0).getInt("entermode", 0);
    }

    public static void saveLastCameraMode(Context c, int m) {
        Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
        editor.putInt("entermode", m);
        editor.apply();
    }

    public static void saveLastSecondaryCameraMode(Context c, int m) {
        Editor editor = c.getSharedPreferences(Setting.SETTING_SECONDARY, 0).edit();
        editor.putInt("entermode", m);
        editor.apply();
    }

    public static void saveLastPicture(Activity activity, Uri uri) {
        if (activity != null && uri != null) {
            Context context = activity.getApplicationContext();
            saveLastPictureUri(context, uri);
            saveLastPicturePath(context, BitmapManager.getRealPathFromURI(activity, uri));
        }
    }

    public static void saveLastVideo(Activity activity, Uri uri) {
        if (activity != null && uri != null) {
            Context context = activity.getApplicationContext();
            saveLastVideoUri(context, uri);
            saveLastVideoPath(context, BitmapManager.getRealPathFromURI(activity, uri));
        }
    }

    public static void saveLastPictureUri(Context c, Uri uri) {
        Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
        if (uri != null) {
            editor.putString("thumbnail_uri_camera", uri.toString());
        } else {
            editor.remove("thumbnail_uri_camera");
        }
        editor.apply();
    }

    public static void saveLastVideoUri(Context c, Uri uri) {
        Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
        if (uri != null) {
            editor.putString("thumbnail_uri_camcorder", uri.toString());
        } else {
            editor.remove("thumbnail_uri_camcorder");
        }
        editor.apply();
    }

    public static void saveLastPicturePath(Context c, String path) {
        Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
        if (path != null) {
            editor.putString("thumbnail_path_camera", path);
        } else {
            editor.remove("thumbnail_path_camera");
        }
        editor.apply();
    }

    public static void saveLastVideoPath(Context c, String path) {
        Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
        if (path != null) {
            editor.putString("thumbnail_path_camcorder", path);
        } else {
            editor.remove("thumbnail_path_camcorder");
        }
        editor.apply();
    }

    public static String getLastPictureUri(Context c) {
        return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getString("thumbnail_uri_camera", null);
    }

    public static String getLastVideoUri(Context c) {
        return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getString("thumbnail_uri_camcorder", null);
    }

    public static String getLastPicturePath(Context c) {
        return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getString("thumbnail_path_camera", null);
    }

    public static String getLastVideoPath(Context c) {
        return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getString("thumbnail_path_camcorder", null);
    }

    public static int getShutterSoundIndex(Context c) {
        if (c != null) {
            return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getInt("shutter_sound_index", 0);
        }
        return ERROR_VALUE;
    }

    public static void saveShutterSoundIndex(Context c, int index) {
        if (c != null) {
            Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
            editor.putInt("shutter_sound_index", index);
            editor.apply();
        }
    }

    public static void saveVideoSizeIndexAtPrimaryNormalMode(Context c, int index) {
        if (c != null) {
            Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
            editor.putInt("video_size_at_normal", index);
            editor.apply();
        }
    }

    public static int getVideoSizeIndexAtPrimaryNormalMode(Context c) {
        if (c != null) {
            return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getInt("video_size_at_normal", 0);
        }
        return ERROR_VALUE;
    }

    public static void saveVideoSizeIndexAtSecondaryNormalMode(Context c, int index) {
        if (c != null) {
            Editor editor = c.getSharedPreferences(Setting.SETTING_SECONDARY, 0).edit();
            editor.putInt("video_size_at_normal", index);
            editor.apply();
        }
    }

    public static int getVideoSizeIndexAtSecondaryNormalMode(Context c) {
        if (c != null) {
            return c.getSharedPreferences(Setting.SETTING_SECONDARY, 0).getInt("video_size_at_normal", 0);
        }
        return ERROR_VALUE;
    }

    public static void saveLiveEffectFaceIndex(Context c, int index) {
        if (c != null) {
            Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
            editor.putInt("live_effect_face_index", index);
            editor.apply();
        }
    }

    public static int getLiveEffectFaceIndex(Context c) {
        if (c != null) {
            return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getInt("live_effect_face_index", 1);
        }
        return ERROR_VALUE;
    }

    public static void saveFrontLiveEffectFaceIndex(Context c, int index) {
        if (c != null) {
            Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
            editor.putInt("front_live_effect_face_index", index);
            editor.apply();
        }
    }

    public static int getFrontLiveEffectFaceIndex(Context c) {
        if (c != null) {
            return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getInt("front_live_effect_face_index", 1);
        }
        return ERROR_VALUE;
    }

    public static void saveDualCameraPIPIndex(Context c, int index) {
        if (c != null) {
            Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
            editor.putInt("dual_camera_pip_index", index);
            editor.apply();
        }
    }

    public static int getDualCameraPIPIndex(Context c) {
        if (c != null) {
            return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getInt("dual_camera_pip_index", 0);
        }
        return ERROR_VALUE;
    }

    public static void saveFrontDualCameraPIPIndex(Context c, int index) {
        if (c != null) {
            Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
            editor.putInt("front_dual_camera_pip_index", index);
            editor.apply();
        }
    }

    public static int getFrontDualCameraPIPIndex(Context c) {
        if (c != null) {
            return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getInt("front_dual_camera_pip_index", 0);
        }
        return ERROR_VALUE;
    }

    public static void saveDualCamcorderPIPIndex(Context c, int index) {
        if (c != null) {
            Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
            editor.putInt("dual_camcorder_pip_index", index);
            editor.apply();
        }
    }

    public static int getDualCamcorderPIPIndex(Context c) {
        if (c != null) {
            return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getInt("dual_camcorder_pip_index", 0);
        }
        return ERROR_VALUE;
    }

    public static void saveFrontDualCamcorderPIPIndex(Context c, int index) {
        if (c != null) {
            Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
            editor.putInt("front_dual_camcorder_pip_index", index);
            editor.apply();
        }
    }

    public static int getFrontDualCamcorderPIPIndex(Context c) {
        if (c != null) {
            return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getInt("front_dual_camcorder_pip_index", 0);
        }
        return ERROR_VALUE;
    }

    public static void saveSmartZoomPIPIndex(Context c, int index) {
        if (c != null) {
            Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
            editor.putInt("smartZoom_pip_index", index);
            editor.apply();
        }
    }

    public static int getSmartZoomPIPIndex(Context c) {
        if (c != null) {
            return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getInt("smartZoom_pip_index", 0);
        }
        return ERROR_VALUE;
    }

    public static void saveFrontSmartZoomPIPIndex(Context c, int index) {
        if (c != null) {
            Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
            editor.putInt("front_smartZoom_pip_index", index);
            editor.apply();
        }
    }

    public static int getFrontSmartZoomPIPIndex(Context c) {
        if (c != null) {
            return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getInt("front_smartZoom_pip_index", 0);
        }
        return ERROR_VALUE;
    }

    public static void saveMainPreviousPictureSize(Context c, String size) {
        if (c != null) {
            Editor editor = c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
            editor.putString(KEY_PREVIOUS_PICTURE_SIZE, size);
            editor.apply();
        }
    }

    public static String getMainPreviousPictureSize(Context c) {
        return c.getSharedPreferences(Setting.SETTING_PRIMARY, 0).getString(KEY_PREVIOUS_PICTURE_SIZE, null);
    }
}
