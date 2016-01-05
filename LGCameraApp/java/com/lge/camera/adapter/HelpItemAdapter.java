package com.lge.camera.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.R;
import com.lge.camera.components.RotateView;
import com.lge.camera.dialog.HelpActivityDialog;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.PreferenceProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.PreferenceGroup;
import com.lge.camera.setting.PreferenceInflater;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.olaworks.library.FaceDetector;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class HelpItemAdapter extends BaseAdapter {
    private static final int PREFERENCE_NOT_FOUND = -1;
    private WeakReference<Context> mContext;
    private HelpActivityDialog mDialog;
    private int mOrientation;
    private ArrayList<Integer> mStringDesc;
    private ArrayList<Integer> mStringMainMenu;
    private ArrayList<Integer> mStringMainMenuIcon;
    private ArrayList<Integer> mStringMenu;

    public HelpItemAdapter(Context context, Activity activity, int mode, int cameraId, int orientation) {
        this.mStringMenu = new ArrayList();
        this.mStringDesc = new ArrayList();
        this.mStringMainMenu = new ArrayList();
        this.mStringMainMenuIcon = new ArrayList();
        this.mOrientation = 0;
        this.mContext = new WeakReference(context);
        this.mOrientation = orientation;
        this.mDialog = new HelpActivityDialog(activity, cameraId);
        makeHelpGuideItem(mode, cameraId);
    }

    private void makeHelpGuideItem(int mode, int cameraId) {
        if (mode == 0) {
            if (cameraId == 0) {
                makeMainCameraHelpItem();
            } else {
                makeFrontCameraHelpItem();
            }
        } else if (cameraId == 0) {
            makeMainVideoHelpItem();
        } else {
            makeFrontVideoHelpItem();
        }
    }

    private void makeMainCameraHelpItem() {
        this.mStringMainMenu.add(Integer.valueOf(R.string.sp_camera_helpdesc_generalsetting));
        this.mStringMainMenuIcon.add(Integer.valueOf(R.drawable.camera_icon_general_c));
        this.mStringMenu.add(Integer.valueOf(R.string.sp_camera_helpdesc_generalsetting));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_swap_camera_menu_desc_v2_NORMAL));
        if (ModelProperties.isSupportFrontCameraModel()) {
            this.mStringMenu.add(Integer.valueOf(R.string.dual_camera_select));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_swap_camera_menu_desc_v2_NORMAL));
        }
        if (ModelProperties.getCarrierCode() == 5 || ModelProperties.getCarrierCode() == 10) {
            this.mStringMenu.add(Integer.valueOf(R.string.auto_review));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_show_captured_image_menu_desc_v2_NORMAL));
        }
        if (FunctionProperties.isSupportedTagLocation()) {
            this.mStringMenu.add(Integer.valueOf(R.string.sp_geotagging_NORMAL));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_geo_tagging_menu_desc_v2_NORMAL));
        }
        addShotModeItemsForMainCamera();
        if (FunctionProperties.isSupportSmartMode()) {
            this.mStringMenu.add(Integer.valueOf(R.string.intelligent_auto));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_intelligent_auto_help_desc));
        }
        if (FunctionProperties.isSupportSportShot()) {
            this.mStringMenu.add(Integer.valueOf(R.string.scene_mode_sports));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_scene_mode_menu_sports_desc_v2_NORMAL));
        }
        if (FunctionProperties.isSupportNightShotModeMenu(0)) {
            this.mStringMenu.add(Integer.valueOf(R.string.scene_mode_night));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_scene_mode_menu_night_desc_v2_NORMAL));
        }
        if (FunctionProperties.useCheeseShutterTitle()) {
            this.mStringMenu.add(Integer.valueOf(R.string.sp_cheeseshutter_NORMAL));
        } else {
            this.mStringMenu.add(Integer.valueOf(R.string.sp_voiceshutter_NORMAL));
        }
        this.mStringDesc.add(Integer.valueOf(R.string.sp_voice_shutter_help_desc));
        this.mStringMainMenu.add(Integer.valueOf(R.string.iso));
        this.mStringMainMenuIcon.add(Integer.valueOf(R.drawable.camera_icon_iso_c));
        this.mStringMenu.add(Integer.valueOf(R.string.iso));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_iso_menu_desc_v2_NORMAL));
        addWhiteBalanceItemsForCamera();
        addColorEffectItemsForCamera();
    }

    private void makeFrontCameraHelpItem() {
        this.mStringMainMenu.add(Integer.valueOf(R.string.sp_camera_helpdesc_generalsetting));
        this.mStringMainMenuIcon.add(Integer.valueOf(R.drawable.camera_icon_general_c));
        this.mStringMenu.add(Integer.valueOf(R.string.sp_camera_helpdesc_generalsetting));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_swap_camera_menu_desc_v2_NORMAL));
        if (ModelProperties.isSupportFrontCameraModel()) {
            this.mStringMenu.add(Integer.valueOf(R.string.dual_camera_select));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_swap_camera_menu_desc_v2_NORMAL));
        }
        if (FunctionProperties.isSupportLightFrame()) {
            this.mStringMenu.add(Integer.valueOf(R.string.flash));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_light_frame_desc_v2_NORMAL));
        }
        if (ModelProperties.getCarrierCode() == 5 || ModelProperties.getCarrierCode() == 10) {
            this.mStringMenu.add(Integer.valueOf(R.string.auto_review));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_show_captured_image_menu_desc_v2_NORMAL));
        }
        if (FunctionProperties.isSupportedTagLocation()) {
            this.mStringMenu.add(Integer.valueOf(R.string.sp_geotagging_NORMAL));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_geo_tagging_menu_desc_v2_NORMAL));
        }
        this.mStringMenu.add(Integer.valueOf(R.string.sp_save_as_flipped_NORMAL));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_flipped_img_desc_v3_NORMAL));
        if (FunctionProperties.isSupportedGestureShot()) {
            this.mStringMenu.add(Integer.valueOf(R.string.camera_help_activity_gesture_shot));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_gestureshot_desc));
        }
        this.mStringMainMenu.add(Integer.valueOf(R.string.shot_mode));
        this.mStringMainMenuIcon.add(Integer.valueOf(R.drawable.camera_icon_shot_mode_c));
        this.mStringMenu.add(Integer.valueOf(R.string.shot_mode));
        this.mStringDesc.add(Integer.valueOf(R.string.mode_menu_camera_normal));
        this.mStringMenu.add(Integer.valueOf(R.string.normal));
        this.mStringDesc.add(Integer.valueOf(R.string.mode_menu_camera_normal));
        if (FunctionProperties.getFunctionFrontCameraBeautyShot() == 1) {
            if (ModelProperties.getCarrierCode() == 19) {
                this.mStringMenu.add(Integer.valueOf(R.string.portrait_plus));
                this.mStringDesc.add(Integer.valueOf(R.string.help_guide_portrait_plus_shot_desc));
            } else {
                this.mStringMenu.add(Integer.valueOf(R.string.sp_shot_mode_beauty_NORMAL));
                this.mStringDesc.add(Integer.valueOf(R.string.sp_help_guide_beauty_shot_desc_list));
            }
        }
        if (MultimediaProperties.isDualCameraSupported()) {
            this.mStringMenu.add(Integer.valueOf(R.string.dual_camera));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_dual_camera_help_desc));
        }
        if (FunctionProperties.isSupportNightShotModeMenu(1)) {
            this.mStringMenu.add(Integer.valueOf(R.string.scene_mode_night));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_scene_mode_menu_night_desc_v2_NORMAL));
        }
        if (FunctionProperties.useCheeseShutterTitle()) {
            this.mStringMenu.add(Integer.valueOf(R.string.sp_cheeseshutter_NORMAL));
        } else {
            this.mStringMenu.add(Integer.valueOf(R.string.sp_voiceshutter_NORMAL));
        }
        this.mStringDesc.add(Integer.valueOf(R.string.sp_voice_shutter_help_desc));
        addWhiteBalanceItemsForCamera();
        addColorEffectItemsForCamera();
    }

    private void makeMainVideoHelpItem() {
        this.mStringMainMenu.add(Integer.valueOf(R.string.sp_camera_helpdesc_generalsetting));
        this.mStringMainMenuIcon.add(Integer.valueOf(R.drawable.camera_icon_general_c));
        this.mStringMenu.add(Integer.valueOf(R.string.sp_camera_helpdesc_generalsetting));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_swap_camera_menu_desc_v2_NORMAL));
        if (ModelProperties.isSupportFrontCameraModel()) {
            this.mStringMenu.add(Integer.valueOf(R.string.dual_camera_select));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_swap_camera_menu_desc_v2_NORMAL));
        }
        if (ModelProperties.getCarrierCode() == 5 || ModelProperties.getCarrierCode() == 10) {
            this.mStringMenu.add(Integer.valueOf(R.string.show_recorded_video));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_video_show_desc_v2_NORMAL));
        }
        if (FunctionProperties.isSupportedTagLocation()) {
            this.mStringMenu.add(Integer.valueOf(R.string.sp_geotagging_NORMAL));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_video_geo_tagging_menu_desc_v2_NORMAL));
        }
        addRecordingModeForMainVideo();
        addWhiteBalanceItemsForCamcorder();
        addColorEffectItemsForCamcorder();
    }

    private void makeFrontVideoHelpItem() {
        this.mStringMainMenu.add(Integer.valueOf(R.string.sp_camera_helpdesc_generalsetting));
        this.mStringMainMenuIcon.add(Integer.valueOf(R.drawable.camera_icon_general_c));
        this.mStringMenu.add(Integer.valueOf(R.string.sp_camera_helpdesc_generalsetting));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_swap_camera_menu_desc_v2_NORMAL));
        if (ModelProperties.isSupportFrontCameraModel()) {
            this.mStringMenu.add(Integer.valueOf(R.string.dual_camera_select));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_swap_camera_menu_desc_v2_NORMAL));
        }
        if (ModelProperties.getCarrierCode() == 5 || ModelProperties.getCarrierCode() == 10) {
            this.mStringMenu.add(Integer.valueOf(R.string.show_recorded_video));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_video_show_desc_v2_NORMAL));
        }
        if (FunctionProperties.isSupportedTagLocation()) {
            this.mStringMenu.add(Integer.valueOf(R.string.sp_geotagging_NORMAL));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_video_geo_tagging_menu_desc_v2_NORMAL));
        }
        addRecordingModeForFrontVideo();
        addWhiteBalanceItemsForCamcorder();
        addColorEffectItemsForCamcorder();
    }

    private void addShotModeItemsForMainCamera() {
        ListPreference listPref = ((PreferenceGroup) new PreferenceInflater((Context) this.mContext.get()).inflate(PreferenceProperties.getBackCameraPreferenceResource())).findPreference(Setting.KEY_CAMERA_SHOT_MODE);
        this.mStringMainMenu.add(Integer.valueOf(R.string.shot_mode));
        this.mStringMainMenuIcon.add(Integer.valueOf(R.drawable.camera_icon_shot_mode_c));
        this.mStringMenu.add(Integer.valueOf(R.string.shot_mode));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_shot_mode_desc_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.normal));
        this.mStringDesc.add(Integer.valueOf(R.string.mode_menu_camera_normal));
        if (FunctionProperties.isRefocusShotSupported()) {
            this.mStringMenu.add(Integer.valueOf(R.string.shot_mode_magic_focus));
            this.mStringDesc.add(Integer.valueOf(R.string.mode_menu_camera_refocus_new));
        } else if (FunctionProperties.isClearShotSupported()) {
            this.mStringMenu.add(Integer.valueOf(R.string.sp_shot_mode_shot_and_clear));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_clear_shot_help_desc));
        }
        if (!(listPref == null || listPref.findIndexOfValue(CameraConstants.TYPE_SHOTMODE_HDR) == PREFERENCE_NOT_FOUND)) {
            this.mStringMenu.add(Integer.valueOf(R.string.sp_shot_mode_new_hdr));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_guide_hdr_desc_list));
        }
        if (!(listPref == null || listPref.findIndexOfValue(CameraConstants.TYPE_SHOTMODE_PANORAMA) == PREFERENCE_NOT_FOUND)) {
            this.mStringMenu.add(Integer.valueOf(R.string.shot_mode_panorama));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_shot_mode_panorama_shot_desc_v2_NORMAL));
        }
        if (!(listPref == null || listPref.findIndexOfValue(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) == PREFERENCE_NOT_FOUND)) {
            this.mStringMenu.add(Integer.valueOf(R.string.shot_mode_panorama));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_shot_mode_panorama_shot_desc_v2_NORMAL));
        }
        if (FunctionProperties.isFreePanoramaSupported()) {
            this.mStringMenu.add(Integer.valueOf(R.string.shot_mode_vr_panorama));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_vrpanorama_help_desc));
        }
        if (FunctionProperties.isSupportBurstShot()) {
            this.mStringMenu.add(Integer.valueOf(R.string.sp_shot_mode_burst));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_burst_shot_help_desc_list));
        } else if (!(listPref == null || (listPref.findIndexOfValue(CameraConstants.TYPE_SHOTMODE_CONTINUOUS) == PREFERENCE_NOT_FOUND && listPref.findIndexOfValue(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS) == PREFERENCE_NOT_FOUND))) {
            this.mStringMenu.add(Integer.valueOf(R.string.shot_mode_continuous));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_shot_mode_continuous_shot_desc_v2_NORMAL));
        }
        if (FunctionProperties.getFunctionFrontCameraBeautyShot() == 1) {
            if (ModelProperties.getCarrierCode() == 19) {
                this.mStringMenu.add(Integer.valueOf(R.string.portrait_plus));
                this.mStringDesc.add(Integer.valueOf(R.string.help_guide_portrait_plus_shot_desc));
            } else {
                this.mStringMenu.add(Integer.valueOf(R.string.sp_shot_mode_beauty_NORMAL));
                this.mStringDesc.add(Integer.valueOf(R.string.sp_help_guide_beauty_shot_desc_list));
            }
        }
        if (MultimediaProperties.isDualCameraSupported()) {
            this.mStringMenu.add(Integer.valueOf(R.string.dual_camera));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_dual_camera_help_desc));
        }
        if (FunctionProperties.isTimeMachinShotSupported()) {
            if (FunctionProperties.useTimeCatchShotTitle()) {
                this.mStringMenu.add(Integer.valueOf(R.string.sp_shot_mode_time_catch_NORMAL));
                this.mStringDesc.add(Integer.valueOf(R.string.sp_help_guide_time_machine_desc_new_vzw_NORMAL));
            } else {
                this.mStringMenu.add(Integer.valueOf(R.string.sp_shot_mode_time_machine_NORMAL));
                this.mStringDesc.add(Integer.valueOf(R.string.sp_help_guide_time_machine_desc_new_NORMAL));
            }
        }
        if (FunctionProperties.isRefocusShotSupported() && FunctionProperties.isClearShotSupported()) {
            this.mStringMenu.add(Integer.valueOf(R.string.sp_shot_mode_shot_and_clear));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_clear_shot_help_desc));
        }
    }

    private void addWhiteBalanceItemsForCamera() {
        this.mStringMainMenu.add(Integer.valueOf(R.string.white_balance));
        this.mStringMainMenuIcon.add(Integer.valueOf(R.drawable.camera_icon_white_balance_c));
        this.mStringMenu.add(Integer.valueOf(R.string.white_balance));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_withe_balance_desc_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.sp_whitebalance_default_NORMAL));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_white_balance_auto_desc_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.white_balance_incandescent));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_withe_balance_incandescent_desc_camera_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.white_balance_sunny));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_white_balance_sunny_desc_camera_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.white_balance_fluorescent));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_white_balance_flourescent_desc_camera_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.white_balance_cloudy));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_white_balance_cloudy_desc_camera_v2_NORMAL));
    }

    private void addColorEffectItemsForCamera() {
        this.mStringMainMenu.add(Integer.valueOf(R.string.color_effect));
        this.mStringMainMenuIcon.add(Integer.valueOf(R.drawable.camera_icon_color_effect_c));
        this.mStringMenu.add(Integer.valueOf(R.string.color_effect));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_video_color_effect_desc_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.sp_color_effect_none_cameraapp_NORMAL));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_color_effect_off_desc_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.color_effect_mono));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_color_effect_mono_desc_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.color_effect_sepia));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_color_effect_sepia_desc_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.color_effect_negative));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_color_effect_negative_desc_v2_NORMAL));
    }

    private void addWhiteBalanceItemsForCamcorder() {
        this.mStringMainMenu.add(Integer.valueOf(R.string.white_balance));
        this.mStringMainMenuIcon.add(Integer.valueOf(R.drawable.camera_icon_white_balance_c));
        this.mStringMenu.add(Integer.valueOf(R.string.white_balance));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_withe_balance_desc_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.sp_whitebalance_default_NORMAL));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_white_balance_auto_desc_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.white_balance_incandescent));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_withe_balance_incandescent_desc_camera_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.white_balance_sunny));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_white_balance_sunny_desc_camera_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.white_balance_fluorescent));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_white_balance_flourescent_desc_camera_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.white_balance_cloudy));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_white_balance_cloudy_desc_camera_v2_NORMAL));
    }

    private void addColorEffectItemsForCamcorder() {
        this.mStringMainMenu.add(Integer.valueOf(R.string.color_effect));
        this.mStringMainMenuIcon.add(Integer.valueOf(R.drawable.camera_icon_color_effect_c));
        this.mStringMenu.add(Integer.valueOf(R.string.color_effect));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_video_color_effect_desc_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.sp_color_effect_none_cameraapp_NORMAL));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_color_effect_off_desc_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.color_effect_mono));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_video_color_effect_mono_desc_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.color_effect_sepia));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_video_color_effect_sepia_desc_v2_NORMAL));
        this.mStringMenu.add(Integer.valueOf(R.string.color_effect_negative));
        this.mStringDesc.add(Integer.valueOf(R.string.sp_help_video_color_effect_negative_desc_v2_NORMAL));
    }

    private void addRecordingModeForMainVideo() {
        if (isRecordingModeHelpNeeded(true)) {
            this.mStringMainMenu.add(Integer.valueOf(R.string.record_mode));
            this.mStringMainMenuIcon.add(Integer.valueOf(R.drawable.camera_icon_recording_mode_c));
            this.mStringMenu.add(Integer.valueOf(R.string.record_mode));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_swap_camera_menu_desc_v2_NORMAL));
            this.mStringMenu.add(Integer.valueOf(R.string.normal));
            this.mStringDesc.add(Integer.valueOf(R.string.mode_menu_video_normal));
            if (FunctionProperties.isWDRSupported()) {
                if (FunctionProperties.isHDRRecordingNameUsed()) {
                    this.mStringMenu.add(Integer.valueOf(R.string.record_mode_HDR));
                } else {
                    this.mStringMenu.add(Integer.valueOf(R.string.record_mode_WDR));
                }
                this.mStringDesc.add(Integer.valueOf(R.string.sp_wdr_recording_help_desc_list));
            }
            if (MultimediaProperties.isDualRecordingSupported()) {
                this.mStringMenu.add(Integer.valueOf(R.string.dual_recording));
                this.mStringDesc.add(Integer.valueOf(R.string.sp_dual_recording_help_desc));
            }
            if (MultimediaProperties.isSmartZoomSupported()) {
                this.mStringMenu.add(Integer.valueOf(R.string.tracking_zoom));
                this.mStringDesc.add(Integer.valueOf(R.string.sp_smart_zoom_help_desc));
            }
            if (FunctionProperties.isVideoStabilizationSupported()) {
                this.mStringMenu.add(Integer.valueOf(R.string.anti_shaking));
                this.mStringDesc.add(Integer.valueOf(R.string.sp_anti_shaking_help_desc));
            }
            if (FunctionProperties.isSupportAudiozoom()) {
                this.mStringMenu.add(Integer.valueOf(R.string.sp_audio_zoom_NORMAL));
                this.mStringDesc.add(Integer.valueOf(R.string.sp_audio_zoom_help_desc));
            }
            if (FunctionProperties.isSupportObjectTracking()) {
                this.mStringMenu.add(Integer.valueOf(R.string.sp_tracking_focus_NORMAL));
                this.mStringDesc.add(Integer.valueOf(R.string.sp_tracking_focus_help_desc));
            }
        }
    }

    private void addRecordingModeForFrontVideo() {
        if (isRecordingModeHelpNeeded(false)) {
            this.mStringMainMenu.add(Integer.valueOf(R.string.record_mode));
            this.mStringMainMenuIcon.add(Integer.valueOf(R.drawable.camera_icon_recording_mode_c));
            this.mStringMenu.add(Integer.valueOf(R.string.record_mode));
            this.mStringDesc.add(Integer.valueOf(R.string.sp_help_swap_camera_menu_desc_v2_NORMAL));
            this.mStringMenu.add(Integer.valueOf(R.string.normal));
            this.mStringDesc.add(Integer.valueOf(R.string.mode_menu_video_normal));
            if (FunctionProperties.isVideoStabilizationSupported()) {
                this.mStringMenu.add(Integer.valueOf(R.string.anti_shaking));
                this.mStringDesc.add(Integer.valueOf(R.string.sp_anti_shaking_help_desc));
            }
            if (MultimediaProperties.isDualRecordingSupported()) {
                this.mStringMenu.add(Integer.valueOf(R.string.dual_recording));
                this.mStringDesc.add(Integer.valueOf(R.string.sp_dual_recording_help_desc));
            }
        }
    }

    private boolean isRecordingModeHelpNeeded(boolean isMainCamera) {
        int numOfSupportingRecordingMode = 0;
        if (FunctionProperties.isVideoStabilizationSupported()) {
            numOfSupportingRecordingMode = 0 + 1;
        }
        if (MultimediaProperties.isDualRecordingSupported()) {
            numOfSupportingRecordingMode++;
        }
        if (isMainCamera) {
            if (FunctionProperties.isWDRSupported()) {
                numOfSupportingRecordingMode++;
            }
            if (FunctionProperties.isSupportObjectTracking()) {
                numOfSupportingRecordingMode++;
            }
            if (FunctionProperties.isSupportAudiozoom()) {
                numOfSupportingRecordingMode++;
            }
        }
        if (numOfSupportingRecordingMode >= 1) {
            return true;
        }
        return false;
    }

    public int getCount() {
        if (this.mStringMenu == null) {
            return 0;
        }
        CamLog.d(FaceDetector.TAG, "HelpAdapter --- getCount() :" + this.mStringMenu.size());
        return this.mStringMenu.size();
    }

    public String getItem(int position) {
        CamLog.d(FaceDetector.TAG, "HelpAdapter --- getItem()" + position + " mStringMenu[postion] :" + ((Context) this.mContext.get()).getString(((Integer) this.mStringMenu.get(position)).intValue()));
        return ((Context) this.mContext.get()).getString(((Integer) this.mStringMenu.get(position)).intValue());
    }

    public long getItemId(int position) {
        CamLog.d(FaceDetector.TAG, "HelpAdapter --- getItemId()" + position);
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        CamLog.d(FaceDetector.TAG, "HelpAdapter --- getView() : " + position);
        Context tempContext = (Context) this.mContext.get();
        if (tempContext == null) {
            CamLog.d(FaceDetector.TAG, "Cannot getView because tempContext is null");
            return null;
        }
        View view = convertView;
        if (view == null) {
            view = ((LayoutInflater) tempContext.getSystemService("layout_inflater")).inflate(R.layout.help_item, null);
        }
        if (view != null) {
            return setHelpItemLayout(position, tempContext, view);
        }
        return null;
    }

    private View setHelpItemLayout(int position, Context tempContext, View view) {
        LinearLayout mainItemLayout = (LinearLayout) view.findViewById(R.id.help_item_view);
        LinearLayout subItemLayout = (LinearLayout) view.findViewById(R.id.help_item_view_sub);
        LinearLayout divider = (LinearLayout) view.findViewById(R.id.main_divider);
        LinearLayout subDivider = (LinearLayout) view.findViewById(R.id.sub_divider);
        TextView tvMainMenu = (TextView) view.findViewById(R.id.help_item_menu);
        TextView tvSubMenu = (TextView) view.findViewById(R.id.help_item_sub_menu);
        TextView tvDesc = (TextView) view.findViewById(R.id.help_item_desc);
        ImageView ivMainIcon = (ImageView) view.findViewById(R.id.help_item_menu7_icon);
        ImageView ivHelpPopupIcon = (ImageView) view.findViewById(R.id.help_popup_icon);
        View helpItemText = view.findViewById(R.id.help_item_view_text);
        View helpItemIcon = view.findViewById(R.id.help_item_view_icon);
        if (!(mainItemLayout == null || subItemLayout == null || divider == null || subDivider == null || tvMainMenu == null || tvSubMenu == null || tvDesc == null || ivMainIcon == null || helpItemText == null || helpItemIcon == null)) {
            CamLog.d(FaceDetector.TAG, "mStringMenu[position] = " + tempContext.getString(((Integer) this.mStringMenu.get(position)).intValue()));
            LayoutParams helpItemTextLp = (LayoutParams) helpItemText.getLayoutParams();
            LayoutParams helpItemIconLp = (LayoutParams) helpItemIcon.getLayoutParams();
            final int popupID = ((Integer) this.mStringMenu.get(position)).intValue();
            ivHelpPopupIcon.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    HelpItemAdapter.this.createDialog(popupID);
                }
            });
            int mainMenuSize = this.mStringMainMenu.size();
            int i = 0;
            while (i < mainMenuSize) {
                if (tempContext.getString(((Integer) this.mStringMenu.get(position)).intValue()).equals(tempContext.getString(((Integer) this.mStringMainMenu.get(i)).intValue()))) {
                    mainItemLayout.setVisibility(0);
                    tvMainMenu.setText(tempContext.getString(((Integer) this.mStringMainMenu.get(i)).intValue()));
                    tvMainMenu.setTypeface(Typeface.DEFAULT, 1);
                    if (this.mStringMainMenuIcon != null) {
                        ivMainIcon.setBackgroundResource(((Integer) this.mStringMainMenuIcon.get(i)).intValue());
                    }
                    if (tempContext.getString(R.string.iso).equals(tempContext.getString(((Integer) this.mStringMenu.get(position)).intValue()))) {
                        subItemLayout.setVisibility(0);
                        tvSubMenu.setVisibility(8);
                        subDivider.setVisibility(0);
                        tvDesc.setText(tempContext.getString(((Integer) this.mStringDesc.get(position)).intValue()));
                        tvDesc.setTypeface(Typeface.DEFAULT);
                    } else {
                        subItemLayout.setVisibility(8);
                        subDivider.setVisibility(8);
                    }
                    divider.setVisibility(0);
                } else {
                    mainItemLayout.setVisibility(8);
                    subItemLayout.setVisibility(0);
                    tvSubMenu.setVisibility(0);
                    divider.setVisibility(8);
                    subDivider.setVisibility(0);
                    if (DialogCreater.getHelpPopupID(((Integer) this.mStringMenu.get(position)).intValue()) != 0) {
                        ivHelpPopupIcon.setVisibility(0);
                        int i2 = this.mOrientation;
                        helpItemTextLp.weight = r0 == 2 ? 0.9f : 0.84f;
                        i2 = this.mOrientation;
                        helpItemIconLp.weight = r0 == 2 ? RotateView.BASE_TEXT_SCALE_X_RATE : 0.16f;
                    } else {
                        ivHelpPopupIcon.setVisibility(8);
                        helpItemTextLp.weight = RotateView.DEFAULT_TEXT_SCALE_X;
                        helpItemIconLp.weight = 0.0f;
                    }
                    helpItemText.setLayoutParams(helpItemTextLp);
                    helpItemIcon.setLayoutParams(helpItemIconLp);
                    tvSubMenu.setText(tempContext.getString(((Integer) this.mStringMenu.get(position)).intValue()));
                    tvSubMenu.setTypeface(Typeface.DEFAULT, 1);
                    tvDesc.setText(tempContext.getString(((Integer) this.mStringDesc.get(position)).intValue()));
                    tvDesc.setTypeface(Typeface.DEFAULT);
                    i++;
                }
            }
        }
        return view;
    }

    public int getItemPosition(String key) {
        if (this.mStringMenu == null) {
            return 0;
        }
        int menuSize = this.mStringMenu.size();
        for (int i = 0; i < menuSize; i++) {
            if (key.equals(((Context) this.mContext.get()).getString(((Integer) this.mStringMenu.get(0)).intValue()))) {
                return i;
            }
        }
        return 0;
    }

    public boolean isEnabled(int position) {
        return false;
    }

    public void createDialog(int whichPopup) {
        CamLog.d(FaceDetector.TAG, "Which popup =" + whichPopup);
        if (this.mDialog != null) {
            this.mDialog.dismissHelpDialog();
            this.mDialog.create(whichPopup, this.mOrientation);
        }
    }

    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
    }

    public void onPause() {
        if (this.mDialog != null) {
            this.mDialog.dismissHelpDialog();
        }
    }

    public void refreshDialog() {
        if (this.mDialog != null) {
            this.mDialog.refreshDialog(this.mOrientation);
        }
    }

    public void unbind() {
        if (this.mStringMenu != null) {
            this.mStringMenu.clear();
            this.mStringMenu = null;
        }
        if (this.mStringDesc != null) {
            this.mStringDesc.clear();
            this.mStringDesc = null;
        }
        if (this.mStringMainMenu != null) {
            this.mStringMainMenu.clear();
            this.mStringMainMenu = null;
        }
        if (this.mStringMainMenu != null) {
            this.mStringMainMenuIcon.clear();
            this.mStringMainMenu = null;
        }
        if (this.mDialog != null) {
            this.mDialog.unbind();
            this.mDialog = null;
        }
        this.mContext = null;
    }
}
