package com.lge.camera.util;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.R;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.hardware.LGCamera.LGParameters;

public class DialogCreater {
    public static final int DIALOG_ID_AU_CLOUD = 28;
    public static final int DIALOG_ID_CAMERA_DRIVER_NEED_RESET = 19;
    public static final int DIALOG_ID_CANNOT_CONNECT_CAMERA = 18;
    public static final int DIALOG_ID_DELETE_CONFIRM = 3;
    public static final int DIALOG_ID_DELETE_CONFIRM_MULTI = 8;
    public static final int DIALOG_ID_DELETE_CONFIRM_VIDEO = 9;
    public static final int DIALOG_ID_DELETE_DONE = 4;
    public static final int DIALOG_ID_ENABLE_GALLERY = 24;
    public static final int DIALOG_ID_EULA_POPUP = 23;
    public static final int DIALOG_ID_EXTERNAL_ADD_STORAGE_FULL = 11;
    public static final int DIALOG_ID_EXTERNAL_NOTIFICATION = 13;
    public static final int DIALOG_ID_EXTERNAL_STORAGE_FULL = 1;
    public static final int DIALOG_ID_GEO_TAG = 10;
    public static final int DIALOG_ID_HELP_AUDIOZOOM = 114;
    public static final int DIALOG_ID_HELP_BEAUTY_SHOT = 105;
    public static final int DIALOG_ID_HELP_BURST_SHOT = 109;
    public static final int DIALOG_ID_HELP_CLEAR_SHOT = 115;
    public static final int DIALOG_ID_HELP_CONTINUOUS_SHOT = 104;
    public static final int DIALOG_ID_HELP_DUAL_CAMERA = 116;
    public static final int DIALOG_ID_HELP_DUAL_RECORDING = 112;
    public static final int DIALOG_ID_HELP_END_INDEX = 126;
    public static final int DIALOG_ID_HELP_FACE_TRACKING_LED = 121;
    public static final int DIALOG_ID_HELP_FREE_PANORAMA = 108;
    public static final int DIALOG_ID_HELP_GESTURESHOT = 125;
    public static final int DIALOG_ID_HELP_HDR = 101;
    public static final int DIALOG_ID_HELP_HDR_MOVIE = 118;
    public static final int DIALOG_ID_HELP_INTELLIGENT_AUTO_MODE = 110;
    public static final int DIALOG_ID_HELP_LIGHT_FRAME = 123;
    public static final int DIALOG_ID_HELP_LIVE_EFFECT = 107;
    public static final int DIALOG_ID_HELP_NIGHT = 120;
    public static final int DIALOG_ID_HELP_PANORAMA = 102;
    public static final int DIALOG_ID_HELP_PLANE_PANORAMA = 122;
    public static final int DIALOG_ID_HELP_REFOCUS = 124;
    public static final int DIALOG_ID_HELP_SMART_ZOOM_RECORDING = 117;
    public static final int DIALOG_ID_HELP_SPORTS = 119;
    public static final int DIALOG_ID_HELP_START_INDEX = 100;
    public static final int DIALOG_ID_HELP_TIMEMACHINE = 103;
    public static final int DIALOG_ID_HELP_UPLUS_BOX = 113;
    public static final int DIALOG_ID_HELP_VOICE_PHOTO = 106;
    public static final int DIALOG_ID_HELP_WDR_MOVIE = 111;
    public static final int DIALOG_ID_INITIALIZE_CONFIG = 6;
    public static final int DIALOG_ID_INTERNAL_NOTIFICATION = 12;
    public static final int DIALOG_ID_NO_DELETE_SELECT = 15;
    public static final int DIALOG_ID_NO_EXTERNAL = 0;
    public static final int DIALOG_ID_NO_EXTERNAL_STORAGE = 7;
    public static final int DIALOG_ID_PROGRESS = 22;
    public static final int DIALOG_ID_RENAME = 5;
    public static final int DIALOG_ID_SAVING_PROGRESS = 27;
    public static final int DIALOG_ID_SELECT_MEMORY = 26;
    public static final int DIALOG_ID_SELECT_VIDEO_LENGTH = 16;
    public static final int DIALOG_ID_SETAS_LIST = 21;
    public static final int DIALOG_ID_SHARE_LIST = 20;
    public static final int DIALOG_ID_STORAGE_CONNECTED_PC = 17;
    public static final int DIALOG_ID_STORAGE_FULL = 14;
    public static final int DIALOG_ID_STORAGE_FULL_SAVE = 2;
    private static final int DIALOG_ID_TEST = 99;
    public static final int DIALOG_ID_TIMEMACHINE_WARNING = 25;
    private static String[][] mHelpDialogId;
    private static SparseIntArray mHelpPopupID;

    static {
        String[][] strArr = new String[DIALOG_ID_TIMEMACHINE_WARNING][];
        String[] strArr2 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr2[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_HDR;
        strArr2[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_HDR);
        strArr[DIALOG_ID_NO_EXTERNAL] = strArr2;
        strArr2 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr2[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_PANORAMA;
        strArr2[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_PANORAMA);
        strArr[DIALOG_ID_EXTERNAL_STORAGE_FULL] = strArr2;
        strArr2 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr2[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_FREE_PANORAMA;
        strArr2[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_FREE_PANORAMA);
        strArr[DIALOG_ID_STORAGE_FULL_SAVE] = strArr2;
        String[] strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_TIMEMACHINE;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_TIMEMACHINE);
        strArr[DIALOG_ID_DELETE_CONFIRM] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_CONTINUOUS_SHOT;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_CONTINUOUS_SHOT);
        strArr[DIALOG_ID_DELETE_DONE] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_BEAUTY_SHOT;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_BEAUTY_SHOT);
        strArr[DIALOG_ID_RENAME] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_VOICE_PHOTO;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_VOICE_PHOTO);
        strArr[DIALOG_ID_INITIALIZE_CONFIG] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_LIVE_EFFECT;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_LIVE_EFFECT);
        strArr[DIALOG_ID_NO_EXTERNAL_STORAGE] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_UPLUS_BOX;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_UPLUS_BOX);
        strArr[DIALOG_ID_DELETE_CONFIRM_MULTI] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_BURST_SHOT;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_BURST_SHOT);
        strArr[DIALOG_ID_DELETE_CONFIRM_VIDEO] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_INTELLIGENT_AUTO_MODE;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_INTELLIGENT_AUTO_MODE);
        strArr[DIALOG_ID_GEO_TAG] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_WDR_MOVIE;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_WDR_MOVIE);
        strArr[DIALOG_ID_EXTERNAL_ADD_STORAGE_FULL] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_DUAL_RECORDING;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_DUAL_RECORDING);
        strArr[DIALOG_ID_INTERNAL_NOTIFICATION] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_AUDIOZOOM;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_AUDIOZOOM);
        strArr[DIALOG_ID_EXTERNAL_NOTIFICATION] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_CLEAR_SHOT;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_CLEAR_SHOT);
        strArr[DIALOG_ID_STORAGE_FULL] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_DUAL_CAMERA;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_DUAL_CAMERA);
        strArr[DIALOG_ID_NO_DELETE_SELECT] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_SMART_ZOOM_RECORDING;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_SMART_ZOOM_RECORDING);
        strArr[DIALOG_ID_SELECT_VIDEO_LENGTH] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_HDR_MOVIE;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_HDR_MOVIE);
        strArr[DIALOG_ID_STORAGE_CONNECTED_PC] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_SPORTS;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_SPORTS);
        strArr[DIALOG_ID_CANNOT_CONNECT_CAMERA] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = LGParameters.SCENE_MODE_NIGHT;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_NIGHT);
        strArr[DIALOG_ID_CAMERA_DRIVER_NEED_RESET] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_FACE_TRACKING_LED;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_FACE_TRACKING_LED);
        strArr[DIALOG_ID_SHARE_LIST] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_PLANE_PANORAMA;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_PLANE_PANORAMA);
        strArr[DIALOG_ID_SETAS_LIST] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_LIGHT_FRAME;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_LIGHT_FRAME);
        strArr[DIALOG_ID_PROGRESS] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_REFOCUS;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_REFOCUS);
        strArr[DIALOG_ID_EULA_POPUP] = strArr3;
        strArr3 = new String[DIALOG_ID_STORAGE_FULL_SAVE];
        strArr3[DIALOG_ID_NO_EXTERNAL] = Setting.HELP_GESTURESHOT;
        strArr3[DIALOG_ID_EXTERNAL_STORAGE_FULL] = String.valueOf(DIALOG_ID_HELP_GESTURESHOT);
        strArr[DIALOG_ID_ENABLE_GALLERY] = strArr3;
        mHelpDialogId = strArr;
        mHelpPopupID = null;
    }

    public static int getHelpDialogId(String key) {
        String[][] arr$ = mHelpDialogId;
        int len$ = arr$.length;
        for (int i$ = DIALOG_ID_NO_EXTERNAL; i$ < len$; i$ += DIALOG_ID_EXTERNAL_STORAGE_FULL) {
            String[] dialog = arr$[i$];
            if (dialog[DIALOG_ID_NO_EXTERNAL].equals(key)) {
                return Integer.parseInt(dialog[DIALOG_ID_EXTERNAL_STORAGE_FULL]);
            }
        }
        return DIALOG_ID_NO_EXTERNAL;
    }

    public static String getHelpDialogKeyValue(int dialogId) {
        String[][] arr$ = mHelpDialogId;
        int len$ = arr$.length;
        for (int i$ = DIALOG_ID_NO_EXTERNAL; i$ < len$; i$ += DIALOG_ID_EXTERNAL_STORAGE_FULL) {
            String[] dialog = arr$[i$];
            if (Integer.parseInt(dialog[DIALOG_ID_EXTERNAL_STORAGE_FULL]) == dialogId) {
                return dialog[DIALOG_ID_NO_EXTERNAL];
            }
        }
        return CameraConstants.TYPE_PREFERENCE_NOT_FOUND;
    }

    public static void makeHelpDialog() {
        mHelpPopupID = new SparseIntArray();
        mHelpPopupID.put(R.string.sp_shot_mode_new_hdr, DIALOG_ID_HELP_HDR);
        mHelpPopupID.put(R.string.shot_mode_panorama, FunctionProperties.isPlanePanoramaSupported() ? DIALOG_ID_HELP_PLANE_PANORAMA : DIALOG_ID_HELP_PANORAMA);
        if (FunctionProperties.isTimeMachinShotSupported()) {
            if (FunctionProperties.useTimeCatchShotTitle()) {
                mHelpPopupID.put(R.string.sp_shot_mode_time_catch_NORMAL, DIALOG_ID_HELP_TIMEMACHINE);
            } else {
                mHelpPopupID.put(R.string.sp_shot_mode_time_machine_NORMAL, DIALOG_ID_HELP_TIMEMACHINE);
            }
        }
        mHelpPopupID.put(R.string.shot_mode_continuous, DIALOG_ID_HELP_CONTINUOUS_SHOT);
        if (ModelProperties.getCarrierCode() == DIALOG_ID_CAMERA_DRIVER_NEED_RESET) {
            mHelpPopupID.put(R.string.portrait_plus, DIALOG_ID_HELP_BEAUTY_SHOT);
        } else {
            mHelpPopupID.put(R.string.sp_shot_mode_beauty_NORMAL, DIALOG_ID_HELP_BEAUTY_SHOT);
        }
        mHelpPopupID.put(R.string.sp_live_effect_NORMAL, DIALOG_ID_HELP_LIVE_EFFECT);
        mHelpPopupID.put(R.string.sp_uplusbox_NORMAL, DIALOG_ID_HELP_UPLUS_BOX);
        mHelpPopupID.put(R.string.sp_shot_mode_burst, DIALOG_ID_HELP_BURST_SHOT);
        mHelpPopupID.put(R.string.intelligent_auto, DIALOG_ID_HELP_INTELLIGENT_AUTO_MODE);
        mHelpPopupID.put(R.string.record_mode_WDR, DIALOG_ID_HELP_WDR_MOVIE);
        mHelpPopupID.put(R.string.sp_shot_mode_shot_and_clear, DIALOG_ID_HELP_CLEAR_SHOT);
        mHelpPopupID.put(R.string.record_mode_HDR, DIALOG_ID_HELP_HDR_MOVIE);
        mHelpPopupID.put(R.string.scene_mode_sports, DIALOG_ID_HELP_SPORTS);
        mHelpPopupID.put(R.string.scene_mode_night, DIALOG_ID_HELP_NIGHT);
        mHelpPopupID.put(R.string.shot_mode_vr_panorama, DIALOG_ID_HELP_FREE_PANORAMA);
        if (FunctionProperties.useCheeseShutterTitle()) {
            mHelpPopupID.put(R.string.sp_cheeseshutter_NORMAL, DIALOG_ID_HELP_VOICE_PHOTO);
        } else {
            mHelpPopupID.put(R.string.sp_voiceshutter_NORMAL, DIALOG_ID_HELP_VOICE_PHOTO);
        }
        mHelpPopupID.put(R.string.dual_recording, DIALOG_ID_HELP_DUAL_RECORDING);
        mHelpPopupID.put(R.string.sp_audio_zoom_NORMAL, DIALOG_ID_HELP_AUDIOZOOM);
        mHelpPopupID.put(R.string.dual_camera, DIALOG_ID_HELP_DUAL_CAMERA);
        mHelpPopupID.put(R.string.tracking_zoom, DIALOG_ID_HELP_SMART_ZOOM_RECORDING);
        mHelpPopupID.put(R.string.focus_face_tracking, DIALOG_ID_HELP_FACE_TRACKING_LED);
        mHelpPopupID.put(R.string.flash, DIALOG_ID_HELP_LIGHT_FRAME);
        mHelpPopupID.put(R.string.shot_mode_magic_focus, DIALOG_ID_HELP_REFOCUS);
        mHelpPopupID.put(R.string.camera_help_activity_gesture_shot, DIALOG_ID_HELP_GESTURESHOT);
    }

    public static int getHelpPopupID(int menuItem) {
        return mHelpPopupID == null ? DIALOG_ID_NO_EXTERNAL : mHelpPopupID.get(menuItem);
    }

    public static int[] getHelpItemResources(int popupId, int cameraId) {
        int titleId;
        int messageTextId;
        int messageImageId;
        switch (popupId) {
            case DIALOG_ID_HELP_HDR /*101*/:
                titleId = R.string.sp_new_ux_help_guide_title_hdr;
                messageTextId = R.string.sp_new_ux_help_guide_desc_hdr;
                messageImageId = R.drawable.levellist_camera_help_image_hdr;
                break;
            case DIALOG_ID_HELP_PANORAMA /*102*/:
                titleId = R.string.sp_new_ux_help_guide_title_panorama;
                messageTextId = R.string.sp_new_ux_help_guide_desc_panorama;
                messageImageId = R.drawable.levellist_camera_help_image_panorama;
                break;
            case DIALOG_ID_HELP_TIMEMACHINE /*103*/:
                if (FunctionProperties.useTimeCatchShotTitle()) {
                    titleId = R.string.sp_new_ux_help_guide_title_time_catch_shot;
                    messageTextId = R.string.sp_new_ux_help_guide_desc_time_catch_shot;
                } else {
                    titleId = R.string.sp_new_ux_help_guide_title_time_machine_camera;
                    messageTextId = R.string.sp_new_ux_help_guide_desc_time_machine_camera;
                }
                messageImageId = R.drawable.levellist_camera_help_image_timemachine;
                break;
            case DIALOG_ID_HELP_CONTINUOUS_SHOT /*104*/:
                titleId = R.string.sp_new_ux_help_guide_title_continuous_shot;
                messageTextId = R.string.sp_help_guide_continuous_shot_desc_NORMAL_v2;
                messageImageId = R.drawable.levellist_camera_help_image_continuous;
                break;
            case DIALOG_ID_HELP_BEAUTY_SHOT /*105*/:
                if (ModelProperties.getCarrierCode() == DIALOG_ID_CAMERA_DRIVER_NEED_RESET) {
                    titleId = R.string.sp_new_ux_help_guide_title_portrait_plus;
                    messageTextId = R.string.help_guide_portrait_plus_shot_desc;
                } else {
                    titleId = R.string.sp_new_ux_help_guide_title_beauty_shot;
                    messageTextId = R.string.sp_new_ux_help_guide_desc_beauty_shot;
                }
                if (cameraId != DIALOG_ID_EXTERNAL_STORAGE_FULL) {
                    messageImageId = R.drawable.levellist_camera_help_image_main_beautyshot;
                    break;
                }
                messageImageId = R.drawable.levellist_camera_help_image_beautyshot;
                break;
            case DIALOG_ID_HELP_VOICE_PHOTO /*106*/:
                if (FunctionProperties.useCheeseShutterTitle()) {
                    titleId = R.string.sp_new_ux_help_guide_title_cheese_shutter;
                    messageTextId = R.string.sp_note_voiceshutter_multi_sound_vzw_NORMAL;
                } else {
                    titleId = R.string.sp_new_ux_help_guide_title_voice_shutter;
                    messageTextId = R.string.sp_note_voiceshutter_multi_sound_NORMAL;
                }
                messageImageId = R.drawable.levellist_camera_help_image_voice_shutter;
                break;
            case DIALOG_ID_HELP_LIVE_EFFECT /*107*/:
                titleId = R.string.sp_new_ux_help_guide_title_live_effect;
                messageTextId = R.string.sp_new_ux_help_guide_desc_live_effect;
                messageImageId = R.drawable.levellist_camera_help_image_liveeffect;
                break;
            case DIALOG_ID_HELP_FREE_PANORAMA /*108*/:
                titleId = R.string.sp_new_ux_help_guide_title_vr_panorama;
                messageTextId = R.string.sp_new_ux_help_guide_desc_vr_panorama_new_specified;
                messageImageId = R.drawable.levellist_camera_help_image_general_panorama;
                break;
            case DIALOG_ID_HELP_BURST_SHOT /*109*/:
                titleId = R.string.sp_new_ux_help_guide_title_burst_shot;
                messageTextId = R.string.sp_new_ux_help_guide_desc_burst_shot;
                messageImageId = R.drawable.levellist_camera_help_image_burstshot;
                break;
            case DIALOG_ID_HELP_INTELLIGENT_AUTO_MODE /*110*/:
                titleId = R.string.sp_new_ux_help_guide_title_intelligent_auto;
                messageTextId = R.string.sp_new_ux_help_guide_desc_intelligent_auto;
                messageImageId = R.drawable.levellist_camera_help_image_intelligentauto;
                break;
            case DIALOG_ID_HELP_WDR_MOVIE /*111*/:
                titleId = R.string.sp_new_ux_help_guide_title_wdr_recording;
                messageTextId = R.string.sp_new_ux_help_guide_desc_wdr_recording;
                messageImageId = R.drawable.levellist_camera_help_image_wdr;
                break;
            case DIALOG_ID_HELP_DUAL_RECORDING /*112*/:
                titleId = R.string.sp_new_ux_help_guide_title_dual_recording;
                messageTextId = R.string.sp_new_ux_help_guide_desc_dual_recording;
                messageImageId = R.drawable.levellist_camera_help_image_dualrecording;
                break;
            case DIALOG_ID_HELP_UPLUS_BOX /*113*/:
                titleId = R.string.sp_new_ux_help_guide_title_uplus_box;
                messageTextId = R.string.sp_help_guide_uplusbox_desc_NORMAL;
                messageImageId = R.drawable.levellist_camera_help_image_uplusbox;
                break;
            case DIALOG_ID_HELP_AUDIOZOOM /*114*/:
                titleId = R.string.sp_new_ux_help_guide_title_audio_zoom;
                messageTextId = R.string.sp_audio_zoom_help_desc_v2;
                messageImageId = R.drawable.levellist_camera_help_image_audiozoom;
                break;
            case DIALOG_ID_HELP_CLEAR_SHOT /*115*/:
                titleId = R.string.sp_new_ux_help_guide_title_clear_shot;
                messageTextId = R.string.sp_new_ux_help_guide_desc_clear_shot;
                messageImageId = R.drawable.levellist_camera_help_image_clearshot;
                break;
            case DIALOG_ID_HELP_DUAL_CAMERA /*116*/:
                titleId = R.string.sp_new_ux_help_guide_title_dual_camera;
                messageTextId = R.string.sp_new_ux_help_guide_desc_dual_recording;
                messageImageId = R.drawable.levellist_camera_help_image_dualrecording;
                break;
            case DIALOG_ID_HELP_SMART_ZOOM_RECORDING /*117*/:
                titleId = R.string.sp_new_ux_help_guide_title_tracking_zoom;
                messageTextId = R.string.sp_smart_zoom_help_desc_v2;
                messageImageId = R.drawable.levellist_camera_help_image_smartzoom;
                break;
            case DIALOG_ID_HELP_HDR_MOVIE /*118*/:
                titleId = R.string.sp_new_ux_help_guide_title_hdr_recording;
                messageTextId = R.string.sp_new_ux_help_guide_desc_hdr_recording;
                messageImageId = R.drawable.levellist_camera_help_image_hdr_recording;
                break;
            case DIALOG_ID_HELP_SPORTS /*119*/:
                titleId = R.string.sp_new_ux_help_guide_title_sports;
                messageTextId = R.string.sp_help_scene_mode_menu_sports_desc_v2_NORMAL;
                messageImageId = R.drawable.levellist_camera_help_image_sports;
                break;
            case DIALOG_ID_HELP_NIGHT /*120*/:
                titleId = R.string.sp_new_ux_help_guide_title_night;
                messageTextId = R.string.help_scene_mode_menu_night_desc_new;
                messageImageId = R.drawable.levellist_camera_help_image_night;
                break;
            case DIALOG_ID_HELP_FACE_TRACKING_LED /*121*/:
                titleId = R.string.sp_help_guide_title_face_tracking_led;
                messageTextId = R.string.sp_help_guide_face_tracking_led;
                messageImageId = R.drawable.levellist_camera_help_image_face_tracking_led;
                break;
            case DIALOG_ID_HELP_PLANE_PANORAMA /*122*/:
                titleId = R.string.sp_new_ux_help_guide_title_panorama;
                messageTextId = R.string.sp_new_ux_help_guide_desc_plane_panorama;
                messageImageId = R.drawable.levellist_camera_help_image_plane_panorama;
                break;
            case DIALOG_ID_HELP_LIGHT_FRAME /*123*/:
                titleId = R.string.flash;
                messageTextId = R.string.sp_new_ux_help_guide_desc_light_frame;
                messageImageId = R.drawable.levellist_camera_help_image_lightframe;
                break;
            case DIALOG_ID_HELP_REFOCUS /*124*/:
                titleId = R.string.sp_new_ux_help_guide_title_refocus;
                messageTextId = R.string.sp_new_ux_help_guide_desc_refocus_new;
                messageImageId = R.drawable.levellist_camera_help_image_refocus;
                break;
            case DIALOG_ID_HELP_GESTURESHOT /*125*/:
                titleId = R.string.sp_new_ux_help_guide_title_gestureshot;
                messageTextId = R.string.sp_new_ux_gesture_shot_help_activity_desc;
                messageImageId = R.drawable.levellist_camera_help_image_gestureshot;
                break;
            default:
                titleId = DIALOG_ID_NO_EXTERNAL;
                messageTextId = DIALOG_ID_NO_EXTERNAL;
                messageImageId = DIALOG_ID_NO_EXTERNAL;
                break;
        }
        int[] iArr = new int[DIALOG_ID_DELETE_CONFIRM];
        iArr[DIALOG_ID_NO_EXTERNAL] = titleId;
        iArr[DIALOG_ID_EXTERNAL_STORAGE_FULL] = messageTextId;
        iArr[DIALOG_ID_STORAGE_FULL_SAVE] = messageImageId;
        return iArr;
    }

    public static void setCommandLayout(Context context, View parentView, boolean isLand) {
        if (context != null && parentView != null) {
            int viewWidth = isLand ? Common.getPixelFromDimens(context, R.dimen.voice_command_text_view_width_landscape) : Common.getPixelFromDimens(context, R.dimen.voice_command_text_view_width_portrait);
            int[][] iArr;
            if (FunctionProperties.isSupportVoiceShutterAME()) {
                viewWidth = Math.round(Util.dpToPx(context, isLand ? 140.0f : 156.0f));
                setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_cheese_command, DIALOG_ID_NO_EXTERNAL, (int[][]) null);
                iArr = new int[DIALOG_ID_EXTERNAL_STORAGE_FULL][];
                iArr[DIALOG_ID_NO_EXTERNAL] = new int[]{DIALOG_ID_STORAGE_CONNECTED_PC, R.id.voice_command_cheese_command};
                setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_smile_command, DIALOG_ID_NO_EXTERNAL, iArr);
                iArr = new int[DIALOG_ID_STORAGE_FULL_SAVE][];
                iArr[DIALOG_ID_NO_EXTERNAL] = new int[]{DIALOG_ID_STORAGE_CONNECTED_PC, DIALOG_ID_NO_EXTERNAL};
                iArr[DIALOG_ID_EXTERNAL_STORAGE_FULL] = new int[]{DIALOG_ID_DELETE_CONFIRM, R.id.voice_command_cheese_command};
                setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_kimchi_command, DIALOG_ID_NO_EXTERNAL, iArr);
                iArr = new int[DIALOG_ID_STORAGE_FULL_SAVE][];
                iArr[DIALOG_ID_NO_EXTERNAL] = new int[]{DIALOG_ID_STORAGE_CONNECTED_PC, R.id.voice_command_kimchi_command};
                iArr[DIALOG_ID_EXTERNAL_STORAGE_FULL] = new int[]{DIALOG_ID_DELETE_CONFIRM, R.id.voice_command_smile_command};
                setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_LG_command, DIALOG_ID_NO_EXTERNAL, iArr);
                setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_whisky_command, DIALOG_ID_DELETE_CONFIRM_MULTI, (int[][]) null);
                setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_torimasu_command, DIALOG_ID_DELETE_CONFIRM_MULTI, (int[][]) null);
            } else if (FunctionProperties.isSupportVoiceShutterJapanese()) {
                viewWidth = Math.round(Util.dpToPx(context, isLand ? 110.0f : 156.0f));
                setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_cheese_command, DIALOG_ID_NO_EXTERNAL, (int[][]) null);
                iArr = new int[DIALOG_ID_EXTERNAL_STORAGE_FULL][];
                iArr[DIALOG_ID_NO_EXTERNAL] = new int[]{DIALOG_ID_STORAGE_CONNECTED_PC, R.id.voice_command_cheese_command};
                setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_smile_command, DIALOG_ID_NO_EXTERNAL, iArr);
                iArr = new int[DIALOG_ID_STORAGE_FULL_SAVE][];
                iArr[DIALOG_ID_NO_EXTERNAL] = new int[]{DIALOG_ID_STORAGE_CONNECTED_PC, DIALOG_ID_NO_EXTERNAL};
                iArr[DIALOG_ID_EXTERNAL_STORAGE_FULL] = new int[]{DIALOG_ID_DELETE_CONFIRM, R.id.voice_command_cheese_command};
                setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_LG_command, DIALOG_ID_NO_EXTERNAL, iArr);
                iArr = new int[DIALOG_ID_STORAGE_FULL_SAVE][];
                iArr[DIALOG_ID_NO_EXTERNAL] = new int[]{DIALOG_ID_STORAGE_CONNECTED_PC, R.id.voice_command_LG_command};
                iArr[DIALOG_ID_EXTERNAL_STORAGE_FULL] = new int[]{DIALOG_ID_DELETE_CONFIRM, R.id.voice_command_smile_command};
                setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_torimasu_command, DIALOG_ID_NO_EXTERNAL, iArr);
                setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_whisky_command, DIALOG_ID_DELETE_CONFIRM_MULTI, (int[][]) null);
                setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_kimchi_command, DIALOG_ID_DELETE_CONFIRM_MULTI, (int[][]) null);
            } else {
                setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_cheese_command, DIALOG_ID_NO_EXTERNAL, (int[][]) null);
                if (isLand) {
                    iArr = new int[DIALOG_ID_EXTERNAL_STORAGE_FULL][];
                    iArr[DIALOG_ID_NO_EXTERNAL] = new int[]{DIALOG_ID_STORAGE_CONNECTED_PC, R.id.voice_command_cheese_command};
                    setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_smile_command, DIALOG_ID_NO_EXTERNAL, iArr);
                    iArr = new int[DIALOG_ID_STORAGE_FULL_SAVE][];
                    iArr[DIALOG_ID_NO_EXTERNAL] = new int[]{DIALOG_ID_STORAGE_CONNECTED_PC, DIALOG_ID_NO_EXTERNAL};
                    iArr[DIALOG_ID_EXTERNAL_STORAGE_FULL] = new int[]{DIALOG_ID_DELETE_CONFIRM, R.id.voice_command_cheese_command};
                    setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_whisky_command, DIALOG_ID_NO_EXTERNAL, iArr);
                    iArr = new int[DIALOG_ID_STORAGE_FULL_SAVE][];
                    iArr[DIALOG_ID_NO_EXTERNAL] = new int[]{DIALOG_ID_STORAGE_CONNECTED_PC, R.id.voice_command_whisky_command};
                    iArr[DIALOG_ID_EXTERNAL_STORAGE_FULL] = new int[]{DIALOG_ID_DELETE_CONFIRM, R.id.voice_command_smile_command};
                    setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_kimchi_command, DIALOG_ID_NO_EXTERNAL, iArr);
                    iArr = new int[DIALOG_ID_STORAGE_FULL_SAVE][];
                    iArr[DIALOG_ID_NO_EXTERNAL] = new int[]{DIALOG_ID_STORAGE_CONNECTED_PC, DIALOG_ID_NO_EXTERNAL};
                    iArr[DIALOG_ID_EXTERNAL_STORAGE_FULL] = new int[]{DIALOG_ID_DELETE_CONFIRM, R.id.voice_command_whisky_command};
                    setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_LG_command, DIALOG_ID_NO_EXTERNAL, iArr);
                } else {
                    iArr = new int[DIALOG_ID_EXTERNAL_STORAGE_FULL][];
                    iArr[DIALOG_ID_NO_EXTERNAL] = new int[]{DIALOG_ID_STORAGE_CONNECTED_PC, R.id.voice_command_cheese_command};
                    setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_smile_command, DIALOG_ID_NO_EXTERNAL, iArr);
                    iArr = new int[DIALOG_ID_STORAGE_FULL_SAVE][];
                    iArr[DIALOG_ID_NO_EXTERNAL] = new int[]{DIALOG_ID_DELETE_CONFIRM, DIALOG_ID_NO_EXTERNAL};
                    iArr[DIALOG_ID_EXTERNAL_STORAGE_FULL] = new int[]{DIALOG_ID_STORAGE_CONNECTED_PC, R.id.voice_command_smile_command};
                    setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_whisky_command, DIALOG_ID_NO_EXTERNAL, iArr);
                    iArr = new int[DIALOG_ID_STORAGE_FULL_SAVE][];
                    iArr[DIALOG_ID_NO_EXTERNAL] = new int[]{DIALOG_ID_STORAGE_CONNECTED_PC, DIALOG_ID_NO_EXTERNAL};
                    iArr[DIALOG_ID_EXTERNAL_STORAGE_FULL] = new int[]{DIALOG_ID_DELETE_CONFIRM, R.id.voice_command_cheese_command};
                    setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_kimchi_command, DIALOG_ID_NO_EXTERNAL, iArr);
                    iArr = new int[DIALOG_ID_STORAGE_FULL_SAVE][];
                    iArr[DIALOG_ID_NO_EXTERNAL] = new int[]{DIALOG_ID_STORAGE_CONNECTED_PC, R.id.voice_command_kimchi_command};
                    iArr[DIALOG_ID_EXTERNAL_STORAGE_FULL] = new int[]{DIALOG_ID_DELETE_CONFIRM, R.id.voice_command_smile_command};
                    setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_LG_command, DIALOG_ID_NO_EXTERNAL, iArr);
                }
                setVoiceCommandLayoutParams(context, parentView, viewWidth, R.id.voice_command_torimasu_command, DIALOG_ID_DELETE_DONE, (int[][]) null);
            }
        }
    }

    public static void setVoiceCommandLayoutParams(Context context, View parentView, int viewWidth, int commandLayoutId, int visible, int[][] addRule) {
        if (context != null && parentView != null && commandLayoutId > 0) {
            View commandView = parentView.findViewById(commandLayoutId);
            if (commandView != null) {
                commandView.setVisibility(visible);
                LayoutParams commandParams = (LayoutParams) commandView.getLayoutParams();
                if (commandParams != null) {
                    commandParams.width = viewWidth;
                    Common.resetLayoutParameter(commandParams);
                    if (visible == 0 && addRule != null) {
                        for (int i = DIALOG_ID_NO_EXTERNAL; i < addRule.length; i += DIALOG_ID_EXTERNAL_STORAGE_FULL) {
                            commandParams.addRule(addRule[i][DIALOG_ID_NO_EXTERNAL], addRule[i][DIALOG_ID_EXTERNAL_STORAGE_FULL]);
                        }
                    }
                    commandView.setLayoutParams(commandParams);
                }
            }
        }
    }

    public static int getHorizontalHelpDialogWidth(Context context, boolean useVoice) {
        if (context == null) {
            return DIALOG_ID_NO_EXTERNAL;
        }
        if (useVoice) {
        }
        return Math.round(Util.dpToPx(context, 460.0f));
    }

    public static Dialog create(Context context, int id) {
        return create(context, id, null, null, null, null);
    }

    public static Dialog create(Context context, int id, OnClickListener listener1) {
        return create(context, id, listener1, null, null, null);
    }

    public static Dialog create(Context context, int id, OnClickListener listener1, OnClickListener listener2) {
        return create(context, id, listener1, listener2, null, null);
    }

    public static Dialog create(Context context, int id, OnClickListener listener1, OnClickListener listener2, OnClickListener listener3) {
        return create(context, id, listener1, listener2, listener3, null);
    }

    public static Dialog create(Context context, int id, OnClickListener listener1, OnClickListener listener2, OnClickListener listener3, Object arg1) {
        Builder builder = new Builder(context);
        switch (id) {
            case DIALOG_ID_EXTERNAL_STORAGE_FULL /*1*/:
                builder.setTitle(R.string.title_not_enough_space).setIconAttribute(16843605).setMessage(R.string.not_enough_space).setPositiveButton(R.string.sp_ok_NORMAL, listener1);
                break;
            case DIALOG_ID_STORAGE_FULL_SAVE /*2*/:
                builder.setMessage(R.string.popup_storage_full_save);
                break;
            case DIALOG_ID_DELETE_CONFIRM /*3*/:
                if (context != null) {
                    builder.setTitle(R.string.dlg_title_delete).setMessage(R.string.sp_photo_will_be_deleted_NORMAL).setPositiveButton(R.string.yes, listener1).setNegativeButton(R.string.no, listener2);
                    break;
                }
                break;
            case DIALOG_ID_DELETE_DONE /*4*/:
                builder.setMessage(R.string.popup_delete_done);
                break;
            case DIALOG_ID_RENAME /*5*/:
                builder.setTitle(R.string.dlg_title_rename).setView((View) arg1).setPositiveButton(R.string.sp_ok_NORMAL, listener1).setNegativeButton(R.string.cancel, listener2);
                break;
            case DIALOG_ID_INITIALIZE_CONFIG /*6*/:
                builder.setTitle(R.string.sp_reset_message_NORMAL).setView((View) arg1).setPositiveButton(R.string.reset, listener1).setNegativeButton(R.string.cancel, listener2);
                break;
            case DIALOG_ID_NO_EXTERNAL_STORAGE /*7*/:
                builder.setMessage(R.string.insert_sd_card);
                break;
            case DIALOG_ID_DELETE_CONFIRM_MULTI /*8*/:
                builder.setTitle(R.string.dlg_title_delete).setMessage(R.string.sp_deleted_photos_NORMAL).setPositiveButton(R.string.yes, listener1).setNegativeButton(R.string.no, listener2);
                break;
            case DIALOG_ID_DELETE_CONFIRM_VIDEO /*9*/:
                if (context != null) {
                    builder.setTitle(R.string.dlg_title_delete).setMessage(R.string.sp_video_will_be_deleted_NORMAL).setPositiveButton(R.string.sp_ok_NORMAL, listener1).setNegativeButton(R.string.cancel, listener2);
                    break;
                }
                break;
            case DIALOG_ID_GEO_TAG /*10*/:
                builder.setTitle(R.string.sp_gps_settings_title_NORMAL).setMessage(R.string.sp_gps_setting_msg_NORMAL).setPositiveButton(R.string.geo_tag_dialog_confirm, listener1).setNegativeButton(R.string.cancel, listener2);
                break;
            case DIALOG_ID_EXTERNAL_ADD_STORAGE_FULL /*11*/:
            case DIALOG_ID_STORAGE_FULL /*14*/:
                builder.setTitle(R.string.title_not_enough_space).setIconAttribute(16843605).setMessage(R.string.sp_addspace_NORMAL).setPositiveButton(R.string.sp_ok_NORMAL, listener1);
                break;
            case DIALOG_ID_INTERNAL_NOTIFICATION /*12*/:
            case DIALOG_ID_EXTERNAL_NOTIFICATION /*13*/:
                builder.setTitle(R.string.sp_storage_noti_title_NORMAL).setIconAttribute(16843605).setPositiveButton(R.string.sp_ok_NORMAL, null).setView((View) arg1);
                break;
            case DIALOG_ID_NO_DELETE_SELECT /*15*/:
                builder.setTitle(R.string.sp_storage_noti_title_NORMAL).setIconAttribute(16843605).setPositiveButton(R.string.sp_ok_NORMAL, null);
                break;
            case DIALOG_ID_ENABLE_GALLERY /*24*/:
                builder.setTitle(R.string.sp_note_dialog_title_NORMAL).setMessage(R.string.sp_enable_app_msg_NORMAL).setPositiveButton(R.string.sp_ok_NORMAL, listener1).setNegativeButton(R.string.cancel, listener2);
                break;
            case DIALOG_ID_SELECT_MEMORY /*26*/:
                builder.setTitle(R.string.sp_storage_NORMAL).setMessage(R.string.sp_sdcard_inserted_noti).setPositiveButton(R.string.sp_ok_NORMAL, listener1).setNegativeButton(R.string.cancel, listener2);
                break;
            case DIALOG_ID_TEST /*99*/:
                builder.setTitle("Pick a color").setView(new EditText(context)).setPositiveButton(R.string.yes, listener1).setNegativeButton(R.string.no, listener2);
                break;
            default:
                return null;
        }
        return builder.create();
    }
}
