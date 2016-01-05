package com.lge.camera.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Camera.Size;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public abstract class Setting extends Observable {
    public static final int APP_PREFERENCE_VERSION = 0;
    public static final String HELP_AUDIOZOOM = "audiozoom";
    public static final String HELP_BEAUTY_SHOT = "beauty_shot";
    public static final String HELP_BURST_SHOT = "burst";
    public static final String HELP_CLEAR_SHOT = "clear_shot";
    public static final String HELP_CONTINUOUS_SHOT = "continuous";
    public static final String HELP_DUAL_CAMERA = "dual_camera";
    public static final String HELP_DUAL_RECORDING = "dual_recording";
    public static final String HELP_FACE_TRACKING_LED = "face_tracking";
    public static final String HELP_FREE_PANORAMA = "free_panorama";
    public static final String HELP_GESTURESHOT = "gestureshot";
    public static final String HELP_HDR = "hdr";
    public static final String HELP_HDR_MOVIE = "hdr_movie";
    public static final String HELP_INTELLIGENT_AUTO_MODE = "smart_camera_mode";
    public static final String HELP_LIGHT_FRAME = "light_frame";
    public static final String HELP_LIVE_EFFECT = "live_effect";
    public static final String HELP_NIGHT = "night";
    public static final String HELP_OTHER = "none";
    public static final String HELP_PANORAMA = "panorama";
    public static final String HELP_PLANE_PANORAMA = "plane_panorama";
    public static final String HELP_REFOCUS = "refocus";
    public static final String HELP_SMART_ZOOM_RECORDING = "smart_zoom";
    public static final String HELP_SPORTS = "sports";
    public static final String HELP_TIMEMACHINE = "timemachine";
    public static final String HELP_UPLUS_BOX = "uplus_box";
    public static final String HELP_VOICE_PHOTO = "voice_photo";
    public static final String HELP_WDR_MOVIE = "wdr";
    public static final String KEY_AU_CLOUD = "key_au_cloud";
    public static final String KEY_BEAUTYSHOT = "key_beautyshot";
    public static final String KEY_BRIGHTNESS = "key_brightness";
    public static final String KEY_CAMCORDER_AUDIOZOOM = "key_audiozoom";
    public static final String KEY_CAMERA_3D_DEPTH = "key_camera_3d_depth";
    public static final String KEY_CAMERA_ANTI_BANDING = "key_camera_anti_banding";
    public static final String KEY_CAMERA_AUTO_REVIEW = "key_camera_auto_review";
    public static final String KEY_CAMERA_COLOREFFECT = "key_camera_coloreffect";
    public static final String KEY_CAMERA_ID = "pref_camera_id_key";
    public static final String KEY_CAMERA_PICTURESIZE = "key_camera_picturesize";
    public static final String KEY_CAMERA_SHOT_MODE = "key_camera_shot_mode";
    public static final String KEY_CAMERA_TAG_LOCATION = "key_camera_tag_location";
    public static final String KEY_CAMERA_TIMER = "key_camera_timer";
    public static final String KEY_CAMERA_WHITEBALANCE = "key_camera_whitebalance";
    public static final String KEY_DATE_STAMP = "key_date_stamp";
    public static final String KEY_DUAL_CAMERA = "key_dual_camera";
    public static final String KEY_DUAL_RECORDING = "key_dual_recording";
    public static final String KEY_EDIT_SHORTCUT = "key_edit_shortcut";
    public static final String KEY_FLASH = "key_flash";
    public static final String KEY_FOCUS = "key_focus";
    public static final String KEY_HDR = "key_hdr";
    public static final String KEY_HELP_GUIDE = "key_help_guide";
    public static final String KEY_ISO = "key_iso";
    public static final String KEY_LIGHT = "key_light";
    public static final String KEY_LIVE_EFFECT = "key_live_effect";
    public static final String KEY_NONE = "key_none";
    public static final String KEY_PREFERENCE_VERSION = "key_preference_version";
    public static final String KEY_PREVIEW_SIZE_ON_DEVICE = "key_preview_size_on_device";
    public static final String KEY_QF_INDEX1 = "key_qf_index1";
    public static final String KEY_QF_INDEX2 = "key_qf_index2";
    public static final String KEY_QF_INDEX3 = "key_qf_index3";
    public static final String KEY_QF_INDEX4 = "key_qf_index4";
    public static final String KEY_QF_INDEX5 = "key_qf_index5";
    public static final String KEY_RECORD_LOCATION = "pref_camera_recordlocation_key";
    public static final String KEY_RECORD_MODE = "key_record_mode";
    public static final String KEY_RESTORE = "key_restore";
    public static final String KEY_SAVE_DIRECTION = "key_save_direction";
    public static final String KEY_SCENE_MODE = "key_scene_mode";
    public static final String KEY_SETTING = "key_setting";
    public static final String KEY_SHOT_MODE = "key_shot_mode";
    public static final String KEY_SHUTTER_SOUND = "key_camera_shutter_sound";
    public static final String KEY_SMART_MODE = "key_smart_mode";
    public static final String KEY_SMART_ZOOM_RECORDING = "key_smart_zoom";
    public static final String KEY_STORAGE = "key_storage";
    public static final String KEY_SWAP = "key_swap";
    public static final String KEY_TIME_MACHINE = "key_time_machine";
    public static final String KEY_UPLUS_BOX = "key_uplus_box";
    public static final String KEY_VIDEO_AUDIO_RECORDING = "key_video_audio_recording";
    public static final String KEY_VIDEO_AUTO_REVIEW = "key_video_auto_review";
    public static final String KEY_VIDEO_DURATION = "key_video_duration";
    public static final String KEY_VIDEO_RECORD_MODE = "key_video_record_mode";
    public static final String KEY_VIDEO_STABILIZATION = "key_video_stabilization";
    public static final String KEY_VOICESHUTTER = "key_voiceshutter";
    public static final String KEY_VOLUME = "key_volume";
    public static final String KEY_ZOOM = "key_zoom";
    public static final String SETTING_3D_PRIMARY = "Main_3d_CameraAppConfig";
    public static final String SETTING_PRIMARY = "Main_CameraAppConfig";
    public static final String SETTING_SECONDARY = "Secondary_CameraAppConfig";
    public static final String VIDEO_QUALITY_MMS = "mms";
    protected String mConfigName;
    public SettingFunction mGet;
    protected PreferenceGroup mPreferenceGroup;
    private boolean mQfIndexInit;
    protected ArrayList<String> mQfIndexList;
    protected int[] values;

    public interface SettingFunction {
        boolean isAttachIntent();
    }

    public abstract void loadSetting(Context context);

    public abstract void saveSetting(Context context);

    public Setting(SettingFunction function, Context context, String configName, PreferenceGroup prefGroup) {
        this.mQfIndexList = new ArrayList();
        this.mQfIndexInit = true;
        this.mGet = null;
        this.mGet = function;
        this.mConfigName = configName;
        this.mPreferenceGroup = prefGroup;
    }

    public PreferenceGroup getPreferenceGroup() {
        return this.mPreferenceGroup;
    }

    public void setPreferenceGroup(PreferenceGroup prefGroup) {
        this.mPreferenceGroup = prefGroup;
    }

    public void close() {
        this.values = null;
        deleteObservers();
    }

    public int getCount() {
        return this.mPreferenceGroup.size();
    }

    public void initializeSetting(Context context) {
        CamLog.d(FaceDetector.TAG, "Restore setting to default.");
        if (this.values != null) {
            for (int i = APP_PREFERENCE_VERSION; i < getCount(); i++) {
                this.values[i] = APP_PREFERENCE_VERSION;
            }
        }
        saveSetting(context);
        setChanged();
        notifyObservers();
    }

    public boolean setSetting(int index, int value) {
        ListPreference pref = this.mPreferenceGroup.getListPreference(index);
        if (pref == null) {
            return false;
        }
        if (this.mGet.isAttachIntent()) {
            pref.setSaveSettingEnabled(false);
        } else {
            pref.setSaveSettingEnabled(true);
        }
        if (pref.findIndexOfValue(pref.getValue()) == value) {
            return false;
        }
        pref.setValueIndex(value);
        setChanged();
        notifyObservers();
        return true;
    }

    public boolean setSetting(String key, int value) {
        ListPreference pref = this.mPreferenceGroup.findPreference(key);
        if (pref == null) {
            return false;
        }
        if (this.mGet.isAttachIntent()) {
            pref.setSaveSettingEnabled(false);
        } else {
            pref.setSaveSettingEnabled(true);
        }
        if (pref.findIndexOfValue(pref.getValue()) == value) {
            return false;
        }
        pref.setValueIndex(value);
        setChanged();
        notifyObservers();
        return true;
    }

    public boolean setSetting(String key, String value) {
        return setSetting(key, value, true);
    }

    public boolean setSetting(String key, String value, boolean needSave) {
        ListPreference pref = this.mPreferenceGroup.findPreference(key);
        if (pref == null) {
            CamLog.d(FaceDetector.TAG, "ListPreference is null!!!");
            return false;
        }
        if (!needSave || this.mGet.isAttachIntent()) {
            pref.setSaveSettingEnabled(false);
        } else {
            pref.setSaveSettingEnabled(true);
        }
        if (pref.getValue().equals(value)) {
            return false;
        }
        pref.setValue(value);
        setChanged();
        notifyObservers();
        return true;
    }

    public int getSetting(int index) {
        int valueIndex = APP_PREFERENCE_VERSION;
        try {
            ListPreference pref = this.mPreferenceGroup.getListPreference(index);
            if (pref == null) {
                throw new NullPointerException("pref is null at method Setting::getSetting(" + index + ")");
            }
            valueIndex = pref.findIndexOfValue(pref.getValue());
            return valueIndex;
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "pref is Null");
            e.printStackTrace();
        }
    }

    public int getSettingIndex(String key) {
        int valueIndex = APP_PREFERENCE_VERSION;
        try {
            if (this.mPreferenceGroup != null) {
                ListPreference pref = this.mPreferenceGroup.findPreference(key);
                if (pref != null) {
                    valueIndex = pref.findIndexOfValue(pref.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            CamLog.e(FaceDetector.TAG, "pref  null error");
        }
        return valueIndex;
    }

    public String getSettingValue(String key) {
        ListPreference pref = this.mPreferenceGroup.findPreference(key);
        if (pref != null) {
            return pref.getValue();
        }
        return CameraConstants.TYPE_PREFERENCE_NOT_FOUND;
    }

    public ListPreference getSettingListPreference(String key) {
        if (this.mPreferenceGroup == null) {
            return null;
        }
        return this.mPreferenceGroup.findPreference(key);
    }

    protected static List<String> sizeListToStringList(List<Size> sizes) {
        ArrayList<String> list = new ArrayList();
        for (Size size : sizes) {
            list.add(String.format("%dx%d", new Object[]{Integer.valueOf(size.width), Integer.valueOf(size.height)}));
        }
        return list;
    }

    public static int readPreferredCameraId(SharedPreferences pref) {
        return Integer.parseInt(pref.getString(KEY_CAMERA_ID, "0"));
    }

    public static void writePreferredCameraId(SharedPreferences pref, int cameraId) {
        Editor editor = pref.edit();
        editor.putString(KEY_CAMERA_ID, Integer.toString(cameraId));
        editor.apply();
    }

    public void clearQFIndex() {
        Editor editor = this.mPreferenceGroup.getSharedPreferences().edit();
        editor.putString(KEY_QF_INDEX1, null);
        editor.putString(KEY_QF_INDEX2, null);
        editor.putString(KEY_QF_INDEX3, null);
        editor.putString(KEY_QF_INDEX4, null);
        editor.putString(KEY_QF_INDEX5, null);
        editor.apply();
        this.mQfIndexList.clear();
        this.mQfIndexInit = true;
    }

    public void loadQfIndex() {
        int i;
        SharedPreferences pref = this.mPreferenceGroup.getSharedPreferences();
        this.mQfIndexList.clear();
        this.mQfIndexList.add(pref.getString(KEY_QF_INDEX1, null));
        this.mQfIndexList.add(pref.getString(KEY_QF_INDEX2, null));
        this.mQfIndexList.add(pref.getString(KEY_QF_INDEX3, null));
        this.mQfIndexList.add(pref.getString(KEY_QF_INDEX4, null));
        this.mQfIndexList.add(pref.getString(KEY_QF_INDEX5, null));
        int qflSize = this.mQfIndexList.size();
        for (i = APP_PREFERENCE_VERSION; i < qflSize; i++) {
            if (this.mQfIndexList.get(i) != null) {
                this.mQfIndexInit = false;
                break;
            }
        }
        if (this.mQfIndexInit) {
            for (i = APP_PREFERENCE_VERSION; i < 5; i++) {
                ListPreference preferenceGroup = this.mPreferenceGroup.getListPreference(i + 1);
                if (preferenceGroup != null) {
                    this.mQfIndexList.set(i, preferenceGroup.getKey());
                }
            }
        }
    }

    public void saveQFLIndex() {
        Editor editor = this.mPreferenceGroup.getSharedPreferences().edit();
        editor.putString(KEY_QF_INDEX1, (String) this.mQfIndexList.get(APP_PREFERENCE_VERSION));
        editor.putString(KEY_QF_INDEX2, (String) this.mQfIndexList.get(1));
        editor.putString(KEY_QF_INDEX3, (String) this.mQfIndexList.get(2));
        editor.putString(KEY_QF_INDEX4, (String) this.mQfIndexList.get(3));
        editor.putString(KEY_QF_INDEX5, (String) this.mQfIndexList.get(4));
        editor.apply();
    }

    public ArrayList<String> getQfIndexList() {
        return this.mQfIndexList;
    }

    public int getQfIndex(String key) {
        int qflSize = this.mQfIndexList.size();
        for (int i = APP_PREFERENCE_VERSION; i < qflSize; i++) {
            String listItem = (String) this.mQfIndexList.get(i);
            if (listItem != null && listItem.equals(key)) {
                return i;
            }
        }
        return -1;
    }

    public String getConfigName() {
        return this.mConfigName;
    }
}
