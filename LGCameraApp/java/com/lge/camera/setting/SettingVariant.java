package com.lge.camera.setting;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.SystemProperties;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.properties.ShutterSoundProperties;
import com.lge.camera.properties.StorageProperties;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Util;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;

public class SettingVariant {
    private final int MINIMUM_FRONT_MODE_COUNT;
    private final int PREFERENCE_NOT_FOUND;
    private int SETTING_POSITION_INDEX;

    public SettingVariant() {
        this.SETTING_POSITION_INDEX = 5;
        this.PREFERENCE_NOT_FOUND = -1;
        this.MINIMUM_FRONT_MODE_COUNT = 2;
    }

    public void makePreferenceVariant(Context context, PreferenceGroup mPreferenceGroup) {
        if (mPreferenceGroup != null) {
            try {
                changePictureSizeList(context, mPreferenceGroup);
                changeVideoSizeList(context, mPreferenceGroup);
                addIsoMenu(context, mPreferenceGroup);
                changeSmartMode(context, mPreferenceGroup);
                changeDefaultImageSizeForPanorama(context, mPreferenceGroup);
                addFrontShotMode(context, mPreferenceGroup);
                addMainBeautyShotMenu(context, mPreferenceGroup);
                addPortraitPlusMenu(context, mPreferenceGroup);
                addTimerMenu(context, mPreferenceGroup);
                addDateStampMenu(context, mPreferenceGroup);
                addAutoReviewMenu(context, mPreferenceGroup);
                addTagLocationMenu(context, mPreferenceGroup);
                addSaveAsFlippedMenu(context, mPreferenceGroup);
                addShutterSoundMenu(context, mPreferenceGroup);
                addVolumeKeyMenu(context, mPreferenceGroup);
                addStorageMenu(context, mPreferenceGroup);
                addManualAntiBandingMenu(context, mPreferenceGroup);
                addHideZoomMenu(context, mPreferenceGroup);
                removeAudioZoomMenu(context, mPreferenceGroup);
                addHelpMenu(context, mPreferenceGroup);
                addRestoreMenu(context, mPreferenceGroup);
                changeFlashDefaultValue(context, mPreferenceGroup);
                addAuCloudMenu(context, mPreferenceGroup);
                addVideoStabilizationMenu(context, mPreferenceGroup);
                changeDefaultImageSize(context, mPreferenceGroup);
                addManualKeyFocusValue(context, mPreferenceGroup);
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "Preference add exception : ", e);
            }
        }
    }

    private void changePictureSizeList(Context context, PreferenceGroup mPreferenceGroup) {
        if (CameraConstants.PREF_NAME_MAIN_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName()) || CameraConstants.PREF_NAME_FRONT_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName())) {
            int[] menuIcons;
            int[] menuIconsExpand;
            int[] settingMenuIcons;
            int[] settingMenuIconsExpand;
            CharSequence[] entries;
            CharSequence[] entryValues;
            CharSequence[] extraInfo;
            CharSequence[] extraInfo2;
            CharSequence[] extraInfo3;
            int[] indicatorIcons;
            String defaultValue = "3264x2448";
            String modelName = ModelProperties.readModelName();
            if (CameraConstants.PREF_NAME_MAIN_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName())) {
                if (ModelProperties.getProjectCode() == 2 && (ModelProperties.getCarrierCode() == 5 || ModelProperties.getCarrierCode() == 20 || ModelProperties.getCarrierCode() == 15)) {
                    menuIcons = makeIconList(context, R.array.j1_8m_camera_pictureSize_menuIcons);
                    menuIconsExpand = makeIconList(context, R.array.j1_8m_camera_pictureSize_menuIcons_expand);
                    settingMenuIcons = makeIconList(context, R.array.camera_pictureSize_settingMenuIcons);
                    settingMenuIconsExpand = makeIconList(context, R.array.j1_8m_camera_pictureSize_settingMenuIcons_expand);
                    entries = context.getResources().getStringArray(R.array.j1_8m_camera_pictureSize_entries);
                    entryValues = context.getResources().getStringArray(R.array.j1_8m_camera_pictureSize_entryValues);
                    extraInfo = context.getResources().getStringArray(R.array.j1_8m_camera_pictureSize_extraInfos_postview_sample_size);
                    extraInfo2 = context.getResources().getStringArray(R.array.j1_8m_camera_pictureSize_extraInfos_preview_size);
                    extraInfo3 = context.getResources().getStringArray(R.array.j1_8m_camera_pictureSize_extraInfos_previewSizeOnScreen);
                    defaultValue = ModelProperties.getCarrierCode() == 5 ? "3264x2448" : "3200x1920";
                    indicatorIcons = makeIconList(context, R.array.j1_8m_camera_pictureSize_indicatorIcons);
                } else if (ModelProperties.getProjectCode() == 16 && (ModelProperties.NAME_W7_TMUS.equals(modelName) || ModelProperties.NAME_W7_TMUS_BK.equals(modelName) || ModelProperties.NAME_W7_MPCS.equals(modelName))) {
                    menuIcons = makeIconList(context, R.array.w7_5m_camera_pictureSize_menuIcons);
                    menuIconsExpand = makeIconList(context, R.array.w7_5m_camera_pictureSize_menuIcons_expand);
                    settingMenuIcons = makeIconList(context, R.array.camera_pictureSize_settingMenuIcons);
                    settingMenuIconsExpand = makeIconList(context, R.array.w7_5m_camera_pictureSize_settingMenuIcons_expand);
                    entries = context.getResources().getStringArray(R.array.w7_5m_camera_pictureSize_entries);
                    entryValues = context.getResources().getStringArray(R.array.w7_5m_camera_pictureSize_entryValues);
                    extraInfo = context.getResources().getStringArray(R.array.w7_5m_camera_pictureSize_extraInfos_postview_sample_size);
                    extraInfo2 = context.getResources().getStringArray(R.array.w7_5m_camera_pictureSize_extraInfos_preview_size);
                    extraInfo3 = context.getResources().getStringArray(R.array.w7_5m_camera_pictureSize_extraInfos_previewSizeOnScreen);
                    indicatorIcons = makeIconList(context, R.array.w7_5m_camera_pictureSize_settingMenuIcons_expand);
                    defaultValue = "2304x1296";
                } else if (ModelProperties.getProjectCode() == 22 && ModelProperties.NAME_X5_SPCS.equals(modelName)) {
                    menuIcons = makeIconList(context, R.array.x5_8m_camera_pictureSize_menuIcons);
                    menuIconsExpand = makeIconList(context, R.array.x5_8m_camera_pictureSize_menuIcons_expand);
                    settingMenuIcons = makeIconList(context, R.array.camera_pictureSize_settingMenuIcons);
                    settingMenuIconsExpand = makeIconList(context, R.array.x5_8m_camera_pictureSize_settingMenuIcons_expand);
                    entries = context.getResources().getStringArray(R.array.x5_8m_camera_pictureSize_entries);
                    entryValues = context.getResources().getStringArray(R.array.x5_8m_camera_pictureSize_entryValues);
                    extraInfo = context.getResources().getStringArray(R.array.x5_8m_camera_pictureSize_extraInfos_postview_sample_size);
                    extraInfo2 = context.getResources().getStringArray(R.array.x5_8m_camera_pictureSize_extraInfos_preview_size);
                    extraInfo3 = context.getResources().getStringArray(R.array.x5_8m_camera_pictureSize_extraInfos_previewSizeOnScreen);
                    indicatorIcons = makeIconList(context, R.array.x5_8m_camera_pictureSize_settingMenuIcons_expand);
                    defaultValue = "3264x1836";
                } else if (ModelProperties.NAME_W5_TCL.equals(modelName) || ModelProperties.NAME_W5_IUSACELL.equals(modelName) || ModelProperties.NAME_W5_ARGENTINA.equals(modelName) || ModelProperties.NAME_W5_ENTEL.equals(modelName) || ModelProperties.NAME_W5_PANAMA.equals(modelName) || ModelProperties.NAME_W5_BRAZIL1.equals(modelName) || ModelProperties.NAME_W5_BRAZIL2.equals(modelName)) {
                    menuIcons = makeIconList(context, R.array.w5_8m_camera_pictureSize_menuIcons);
                    menuIconsExpand = makeIconList(context, R.array.w5_8m_camera_pictureSize_menuIcons_expand);
                    settingMenuIcons = makeIconList(context, R.array.camera_pictureSize_settingMenuIcons);
                    settingMenuIconsExpand = makeIconList(context, R.array.w5_8m_camera_pictureSize_settingMenuIcons_expand);
                    entries = context.getResources().getStringArray(R.array.w5_8m_camera_pictureSize_entries);
                    entryValues = context.getResources().getStringArray(R.array.w5_8m_camera_pictureSize_entryValues);
                    extraInfo = context.getResources().getStringArray(R.array.w5_8m_camera_pictureSize_extraInfos_postview_sample_size);
                    extraInfo2 = context.getResources().getStringArray(R.array.w5_8m_camera_pictureSize_extraInfos_preview_size);
                    extraInfo3 = context.getResources().getStringArray(R.array.w5_8m_camera_pictureSize_extraInfos_previewSizeOnScreen);
                    indicatorIcons = makeIconList(context, R.array.w5_8m_camera_pictureSize_settingMenuIcons_expand);
                    defaultValue = "3200x1920";
                } else if (ModelProperties.NAME_W6_TIM.equals(modelName) || ModelProperties.NAME_W6_TCL.equals(modelName) || ModelProperties.NAME_W6_CIS.equals(modelName) || ModelProperties.NAME_W6_EU.equals(modelName) || ModelProperties.NAME_W6_AR.equals(modelName)) {
                    menuIcons = makeIconList(context, R.array.w6_8m_camera_pictureSize_menuIcons);
                    menuIconsExpand = makeIconList(context, R.array.w6_8m_camera_pictureSize_menuIcons_expand);
                    settingMenuIcons = makeIconList(context, R.array.camera_pictureSize_settingMenuIcons);
                    settingMenuIconsExpand = makeIconList(context, R.array.w6_8m_camera_pictureSize_settingMenuIcons_expand);
                    entries = context.getResources().getStringArray(R.array.w6_8m_camera_pictureSize_entries);
                    entryValues = context.getResources().getStringArray(R.array.w6_8m_camera_pictureSize_entryValues);
                    extraInfo = context.getResources().getStringArray(R.array.w6_8m_camera_pictureSize_extraInfos_postview_sample_size);
                    extraInfo2 = context.getResources().getStringArray(R.array.w6_8m_camera_pictureSize_extraInfos_preview_size);
                    extraInfo3 = context.getResources().getStringArray(R.array.w6_8m_camera_pictureSize_extraInfos_previewSizeOnScreen);
                    indicatorIcons = makeIconList(context, R.array.w6_8m_camera_pictureSize_settingMenuIcons_expand);
                    defaultValue = "3200x1920";
                } else if (ModelProperties.NAME_L50_VDF.equals(modelName) || ModelProperties.NAME_L50_TEL.equals(modelName) || ModelProperties.NAME_L50_3M.equals(modelName) || ModelProperties.NAME_L50_OPEN.equals(modelName) || ModelProperties.NAME_L50_FBD.equals(modelName) || ModelProperties.NAME_L50_TR.equals(modelName)) {
                    menuIcons = makeIconList(context, R.array.l50_3m_camera_pictureSize_menuIcons);
                    menuIconsExpand = makeIconList(context, R.array.l50_3m_camera_pictureSize_menuIcons_expand);
                    settingMenuIcons = makeIconList(context, R.array.camera_pictureSize_settingMenuIcons);
                    settingMenuIconsExpand = makeIconList(context, R.array.l50_3m_camera_pictureSize_settingMenuIcons_expand);
                    entries = context.getResources().getStringArray(R.array.l50_3m_camera_pictureSize_entries);
                    entryValues = context.getResources().getStringArray(R.array.l50_3m_camera_pictureSize_entryValues);
                    extraInfo = context.getResources().getStringArray(R.array.l50_3m_camera_pictureSize_extraInfos_postview_sample_size);
                    extraInfo2 = context.getResources().getStringArray(R.array.l50_3m_camera_pictureSize_extraInfos_preview_size);
                    extraInfo3 = context.getResources().getStringArray(R.array.l50_3m_camera_pictureSize_extraInfos_previewSizeOnScreen);
                    indicatorIcons = makeIconList(context, R.array.l50_3m_camera_pictureSize_indicatorIcons);
                    defaultValue = "1840x1104";
                } else if (ModelProperties.NAME_GK_ATT.equals(modelName)) {
                    menuIcons = makeIconList(context, R.array.gk_camera_pictureSize_menuIcons);
                    menuIconsExpand = makeIconList(context, R.array.gk_camera_pictureSize_menuIcons_expand);
                    settingMenuIcons = makeIconList(context, R.array.camera_pictureSize_settingMenuIcons);
                    settingMenuIconsExpand = makeIconList(context, R.array.gk_camera_pictureSize_settingMenuIcons_expand);
                    entries = context.getResources().getStringArray(R.array.gk_camera_pictureSize_entries);
                    entryValues = context.getResources().getStringArray(R.array.gk_camera_pictureSize_entryValues);
                    extraInfo = context.getResources().getStringArray(R.array.gk_camera_pictureSize_extraInfos_postview_sample_size);
                    extraInfo2 = context.getResources().getStringArray(R.array.gk_camera_pictureSize_extraInfos_preview_size);
                    extraInfo3 = context.getResources().getStringArray(R.array.gk_camera_pictureSize_extraInfos_previewSizeOnScreen);
                    indicatorIcons = makeIconList(context, R.array.gk_camera_pictureSize_settingMenuIcons_expand);
                    defaultValue = "4160x3120";
                } else if (ModelProperties.NAME_FX6_EU_OPEN.equals(modelName)) {
                    menuIcons = makeIconList(context, R.array.fx6_8m_camera_pictureSize_menuIcons);
                    menuIconsExpand = makeIconList(context, R.array.fx6_8m_camera_pictureSize_menuIcons_expand);
                    settingMenuIcons = makeIconList(context, R.array.camera_pictureSize_settingMenuIcons);
                    settingMenuIconsExpand = makeIconList(context, R.array.fx6_8m_camera_pictureSize_settingMenuIcons_expand);
                    entries = context.getResources().getStringArray(R.array.fx6_8m_camera_pictureSize_entries);
                    entryValues = context.getResources().getStringArray(R.array.fx6_8m_camera_pictureSize_entryValues);
                    extraInfo = context.getResources().getStringArray(R.array.fx6_8m_camera_pictureSize_extraInfos_postview_sample_size);
                    extraInfo2 = context.getResources().getStringArray(R.array.fx6_8m_camera_pictureSize_extraInfos_preview_size);
                    extraInfo3 = context.getResources().getStringArray(R.array.fx6_8m_camera_pictureSize_extraInfos_previewSizeOnScreen);
                    indicatorIcons = makeIconList(context, R.array.fx6_8m_camera_pictureSize_settingMenuIcons_expand);
                    defaultValue = "3200x1920";
                } else {
                    return;
                }
            } else if (!CameraConstants.PREF_NAME_FRONT_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName())) {
                return;
            } else {
                if (ModelProperties.getProjectCode() == 16 && (ModelProperties.NAME_W7_OPEN.equals(modelName) || ModelProperties.NAME_W7_TMUS.equals(modelName) || ModelProperties.NAME_W7_TMUS_BK.equals(modelName) || ModelProperties.NAME_W7_EU.equals(modelName) || ModelProperties.NAME_W7_TR.equals(modelName) || ModelProperties.NAME_W7_ISR.equals(modelName) || ModelProperties.NAME_W7_MPCS.equals(modelName) || ModelProperties.NAME_W7_CIS.equals(modelName))) {
                    menuIcons = makeIconList(context, R.array.w7_vga_front_camera_pictureSize_menuIcons);
                    menuIconsExpand = makeIconList(context, R.array.w7_vga_front_camera_pictureSize_menuIcons_expand);
                    settingMenuIcons = makeIconList(context, R.array.camera_pictureSize_settingMenuIcons);
                    settingMenuIconsExpand = makeIconList(context, R.array.w7_vga_front_camera_pictureSize_settingMenuIcons_expand);
                    entries = context.getResources().getStringArray(R.array.w7_vga_front_camera_pictureSize_entries);
                    entryValues = context.getResources().getStringArray(R.array.w7_vga_front_camera_pictureSize_entryValues);
                    extraInfo = context.getResources().getStringArray(R.array.w7_vga_front_camera_pictureSize_extraInfos_postview_sample_size);
                    extraInfo2 = context.getResources().getStringArray(R.array.w7_vga_front_camera_pictureSize_extraInfos_preview_size);
                    extraInfo3 = context.getResources().getStringArray(R.array.w7_vga_front_camera_pictureSize_extraInfos_previewSizeOnScreen);
                    indicatorIcons = makeIconList(context, R.array.w7_vga_front_camera_pictureSize_settingMenuIcons_expand);
                    defaultValue = "640x480";
                } else if (ModelProperties.getProjectCode() == 22 && ModelProperties.NAME_X5_SPCS.equals(modelName)) {
                    menuIcons = makeIconList(context, R.array.x5_8m_front_camera_pictureSize_menuIcons);
                    menuIconsExpand = makeIconList(context, R.array.x5_8m_front_camera_pictureSize_menuIcons_expand);
                    settingMenuIcons = makeIconList(context, R.array.camera_pictureSize_settingMenuIcons);
                    settingMenuIconsExpand = makeIconList(context, R.array.x5_8m_front_camera_pictureSize_settingMenuIcons_expand);
                    entries = context.getResources().getStringArray(R.array.x5_8m_front_camera_pictureSize_entries);
                    entryValues = context.getResources().getStringArray(R.array.x5_8m_front_camera_pictureSize_entryValues);
                    extraInfo = context.getResources().getStringArray(R.array.x5_8m_front_camera_pictureSize_extraInfos_postview_sample_size);
                    extraInfo2 = context.getResources().getStringArray(R.array.x5_8m_front_camera_pictureSize_extraInfos_preview_size);
                    extraInfo3 = context.getResources().getStringArray(R.array.x5_8m_front_camera_pictureSize_extraInfos_previewSizeOnScreen);
                    indicatorIcons = makeIconList(context, R.array.x5_8m_front_camera_pictureSize_settingMenuIcons_expand);
                    defaultValue = "1280x720";
                } else if (ModelProperties.NAME_W5_TCF_VZW.equals(modelName) || ModelProperties.NAME_W5_TCF.equals(modelName)) {
                    menuIcons = makeIconList(context, R.array.w5_w1m_front_camera_pictureSize_menuIcons);
                    menuIconsExpand = makeIconList(context, R.array.w5_w1m_front_camera_pictureSize_menuIcons_expand);
                    settingMenuIcons = makeIconList(context, R.array.camera_pictureSize_settingMenuIcons);
                    settingMenuIconsExpand = makeIconList(context, R.array.w5_w1m_front_camera_pictureSize_settingMenuIcons_expand);
                    entries = context.getResources().getStringArray(R.array.w5_w1m_front_camera_pictureSize_entries);
                    entryValues = context.getResources().getStringArray(R.array.w5_w1m_front_camera_pictureSize_entryValues);
                    extraInfo = context.getResources().getStringArray(R.array.w5_w1m_front_camera_pictureSize_extraInfos_postview_sample_size);
                    extraInfo2 = context.getResources().getStringArray(R.array.w5_w1m_front_camera_pictureSize_extraInfos_preview_size);
                    extraInfo3 = context.getResources().getStringArray(R.array.w5_w1m_front_camera_pictureSize_extraInfos_previewSizeOnScreen);
                    indicatorIcons = makeIconList(context, R.array.w5_w1m_front_camera_pictureSize_settingMenuIcons_expand);
                    defaultValue = "1280x768";
                } else {
                    return;
                }
            }
            if (!(menuIcons == null || settingMenuIcons == null)) {
                CamLog.d(FaceDetector.TAG, "menuIcons.length = " + menuIcons.length + ", settingMenuIcons.length = " + settingMenuIcons.length);
            }
            ListPreference listPref = mPreferenceGroup.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
            if (listPref != null) {
                listPref.setMenuIconResources(menuIconsExpand);
                listPref.setSettingMenuIconResources(settingMenuIconsExpand);
                listPref.setIndicatorIconResources(indicatorIcons);
                listPref.setEntries(entries);
                listPref.setEntryValues(entryValues);
                listPref.setExtraInfos(extraInfo);
                listPref.setExtraInfos2(extraInfo2);
                listPref.setExtraInfos3(extraInfo3);
                listPref.setDefaultValue(defaultValue);
            }
        }
    }

    private void changeVideoSizeList(Context context, PreferenceGroup mPreferenceGroup) {
        if (CameraConstants.PREF_NAME_MAIN_CAMCORDER.equals(mPreferenceGroup.getSharedPreferenceName()) || CameraConstants.PREF_NAME_FRONT_CAMCORDER.equals(mPreferenceGroup.getSharedPreferenceName())) {
            String defaultValue = "1280x720";
            String modelName = ModelProperties.readModelName();
            String strCountryIso = SystemProperties.get("ro.build.target_country");
            if (!CameraConstants.PREF_NAME_MAIN_CAMCORDER.equals(mPreferenceGroup.getSharedPreferenceName()) && CameraConstants.PREF_NAME_FRONT_CAMCORDER.equals(mPreferenceGroup.getSharedPreferenceName())) {
                int[] menuIconsExpand;
                int[] settingMenuIconsExpand;
                CharSequence[] entries;
                CharSequence[] entryValues;
                CharSequence[] extraInfo;
                CharSequence[] extraInfo2;
                CharSequence[] extraInfo3;
                int[] indicatorIcons;
                if (ModelProperties.getProjectCode() == 16 && (ModelProperties.NAME_W7_OPEN.equals(modelName) || ModelProperties.NAME_W7_TMUS.equals(modelName) || ModelProperties.NAME_W7_TMUS_BK.equals(modelName) || ModelProperties.NAME_W7_EU.equals(modelName) || ModelProperties.NAME_W7_TR.equals(modelName) || ModelProperties.NAME_W7_ISR.equals(modelName) || ModelProperties.NAME_W7_MPCS.equals(modelName) || (ModelProperties.NAME_W7_CIS.equals(modelName) && !"SCA".equals(strCountryIso)))) {
                    menuIconsExpand = makeIconList(context, R.array.w7_vga_front_video_size_menuIcons_expand);
                    settingMenuIconsExpand = makeIconList(context, R.array.w7_vga_front_video_size_settingMenuIcons_expand);
                    entries = context.getResources().getStringArray(R.array.w7_vga_front_video_size_entries);
                    entryValues = context.getResources().getStringArray(R.array.w7_vga_front_video_size_entryValues);
                    extraInfo = context.getResources().getStringArray(R.array.w7_vga_front_video_size_extraInfos_previewSizeOnScreen);
                    extraInfo2 = context.getResources().getStringArray(R.array.w7_vga_front_video_size_extraInfos_bitrate);
                    extraInfo3 = context.getResources().getStringArray(R.array.w7_vga_front_video_size_extraInfos_hfr);
                    indicatorIcons = makeIconList(context, R.array.w7_vga_front_video_size_indicatorIcons);
                    defaultValue = "640x480";
                } else if (ModelProperties.getProjectCode() == 22 && ModelProperties.NAME_X5_SPCS.equals(modelName)) {
                    menuIconsExpand = makeIconList(context, R.array.x5_8m_front_video_size_menuIcons_expand);
                    settingMenuIconsExpand = makeIconList(context, R.array.x5_8m_front_video_size_settingMenuIcons_expand);
                    entries = context.getResources().getStringArray(R.array.x5_8m_front_video_size_entries);
                    entryValues = context.getResources().getStringArray(R.array.x5_8m_front_video_size_entryValues);
                    extraInfo = context.getResources().getStringArray(R.array.x5_8m_front_video_size_extraInfos_previewSizeOnScreen);
                    extraInfo2 = context.getResources().getStringArray(R.array.x5_8m_front_video_size_extraInfos_bitrate);
                    extraInfo3 = context.getResources().getStringArray(R.array.x5_8m_front_video_size_extraInfos_hfr);
                    indicatorIcons = makeIconList(context, R.array.x5_8m_front_video_size_indicatorIcons);
                    defaultValue = "1280x720";
                } else {
                    return;
                }
                ListPreference listPref = mPreferenceGroup.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
                if (listPref != null) {
                    listPref.setMenuIconResources(menuIconsExpand);
                    listPref.setSettingMenuIconResources(settingMenuIconsExpand);
                    listPref.setIndicatorIconResources(indicatorIcons);
                    listPref.setEntries(entries);
                    listPref.setEntryValues(entryValues);
                    listPref.setExtraInfos(extraInfo);
                    listPref.setExtraInfos2(extraInfo2);
                    listPref.setExtraInfos3(extraInfo3);
                    listPref.setDefaultValue(defaultValue);
                }
            }
        }
    }

    private int[] makeIconList(Context context, int resourceID) {
        TypedArray tempTypedArray = context.getResources().obtainTypedArray(resourceID);
        int[] tempIconList = new int[tempTypedArray.length()];
        for (int i = 0; i < tempIconList.length; i++) {
            tempIconList[i] = tempTypedArray.getResourceId(i, 0);
        }
        return tempIconList;
    }

    private void changeSmartMode(Context context, PreferenceGroup mPreferenceGroup) {
        if (CameraConstants.PREF_NAME_MAIN_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName())) {
            ListPreference listPref = mPreferenceGroup.findPreference(Setting.KEY_SMART_MODE);
            if (listPref != null) {
                listPref.setPersist(false);
            }
        }
    }

    private void addFrontShotMode(Context context, PreferenceGroup mPreferenceGroup) {
        if (CameraConstants.PREF_NAME_FRONT_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName()) && ModelProperties.isSupportFrontCameraModel() && mPreferenceGroup.findPreference(Setting.KEY_CAMERA_SHOT_MODE) == null) {
            int[] menuIconResources = new int[]{R.drawable.camera_preview_quickfunction_shot_normal};
            int[] settingMenuResources = new int[]{R.drawable.camera_icon_shot_mode};
            CharSequence[] entries = new CharSequence[]{context.getResources().getString(R.string.normal)};
            CharSequence[] entriyValues = new CharSequence[]{CameraConstants.TYPE_SHOTMODE_NORMAL};
            CharSequence[] extraInfos = new CharSequence[]{"1920x1080"};
            CharSequence[] extraInfos2 = new CharSequence[]{"1920x1080"};
            CharSequence[] extraInfos3 = new CharSequence[]{"test"};
            if (FunctionProperties.getFunctionFrontCameraBeautyShot() == 1) {
                menuIconResources = Util.appendToIntArray(menuIconResources, R.drawable.camera_preview_quickfunction_scene_beauty);
                entries = Util.appendToCharSequenceArray(entries, context.getResources().getString(R.string.sp_shot_mode_beauty_NORMAL));
                entriyValues = Util.appendToCharSequenceArray(entriyValues, CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY);
                if (ModelProperties.isUVGAmodel()) {
                    extraInfos = Util.appendToCharSequenceArray(extraInfos, "1920x1080");
                } else {
                    extraInfos = Util.appendToCharSequenceArray(extraInfos, "1920x1080");
                }
                extraInfos2 = Util.appendToCharSequenceArray(extraInfos2, "1920x1080");
                extraInfos3 = Util.appendToCharSequenceArray(extraInfos3, "test");
            }
            if (MultimediaProperties.isDualCameraSupported()) {
                menuIconResources = Util.appendToIntArray(menuIconResources, R.drawable.camera_preview_quickfunction_dual_camera);
                entries = Util.appendToCharSequenceArray(entries, context.getResources().getString(R.string.dual_camera));
                entriyValues = Util.appendToCharSequenceArray(entriyValues, CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA);
                if (ModelProperties.isUVGAmodel()) {
                    extraInfos = Util.appendToCharSequenceArray(extraInfos, MultimediaProperties.EFFECTS_ENFORCED_SIZE_FOR_UVGA);
                } else {
                    extraInfos = Util.appendToCharSequenceArray(extraInfos, "1280x720");
                }
                extraInfos2 = Util.appendToCharSequenceArray(extraInfos2, getPreviewSizeOnScreenForDualCamera());
                extraInfos3 = Util.appendToCharSequenceArray(extraInfos3, "test");
            }
            if (entries.length >= 2) {
                ListPreference listPref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
                listPref.setKey(Setting.KEY_CAMERA_SHOT_MODE);
                listPref.setTitle(context.getResources().getString(R.string.shot_mode));
                listPref.setMenuIconResources(menuIconResources);
                listPref.setSettingMenuIconResources(settingMenuResources);
                listPref.setCommand(Command.SHOW_QUICK_FUNCTION_SETTING_MENU);
                listPref.setEntryCommand(Command.CAMERA_SHOT_MODE);
                listPref.setEntries(entries);
                listPref.setEntryValues(entriyValues);
                listPref.setExtraInfos(extraInfos);
                listPref.setExtraInfos2(extraInfos2);
                listPref.setExtraInfos3(extraInfos3);
                listPref.setIndicatorIconResources(null);
                listPref.setDefaultValue(CameraConstants.TYPE_SHOTMODE_NORMAL);
                listPref.setPersist(false);
                ListPreference beautyShotPref = mPreferenceGroup.findPreference(Setting.KEY_BEAUTYSHOT);
                int beautyShotIndex = mPreferenceGroup.findPreferenceIndex(Setting.KEY_BEAUTYSHOT);
                mPreferenceGroup.removePreference(beautyShotIndex);
                mPreferenceGroup.addChildAt(listPref, beautyShotIndex);
                if (beautyShotPref != null) {
                    mPreferenceGroup.addChildAt(beautyShotPref, this.SETTING_POSITION_INDEX + 1);
                }
            }
        }
    }

    private String getPreviewSizeOnScreenForDualCamera() {
        if (ModelProperties.getProjectCode() == 13) {
            return "1280x720";
        }
        if (ModelProperties.getProjectCode() == 15) {
            return MultimediaProperties.EFFECTS_ENFORCED_SIZE_FOR_UVGA;
        }
        return "1920x1080";
    }

    private void addMainBeautyShotMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if (CameraConstants.PREF_NAME_MAIN_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName())) {
            ListPreference shotModePref = mPreferenceGroup.findPreference(Setting.KEY_CAMERA_SHOT_MODE);
            if (shotModePref != null) {
                if (shotModePref.findIndexOfValue(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY) != -1) {
                    return;
                }
            }
            if (FunctionProperties.getFunctionFrontCameraBeautyShot() == 1 && shotModePref != null) {
                CharSequence entry = context.getResources().getString(R.string.sp_shot_mode_beauty_NORMAL);
                CharSequence entriyValue = CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY;
                CharSequence extraInfo = "640x480";
                CharSequence extraInfo2 = "640x480";
                CharSequence extraInfo3 = "test";
                int menuSize = shotModePref.getEntries().length;
                CharSequence[] readEntries = shotModePref.getEntries();
                CharSequence[] readEntriyValues = shotModePref.getEntryValues();
                CharSequence[] readExtraInfos = shotModePref.getExtraInfos();
                CharSequence[] readExtraInfos2 = shotModePref.getExtraInfos2();
                CharSequence[] readExtraInfos3 = shotModePref.getExtraInfos3();
                int[] writeMenuIconResources = new int[(menuSize + 1)];
                CharSequence[] writeEntries = new CharSequence[(menuSize + 1)];
                CharSequence[] writeEntriyValues = new CharSequence[(menuSize + 1)];
                CharSequence[] writeExtraInfos = new CharSequence[(menuSize + 1)];
                CharSequence[] writeExtraInfos2 = new CharSequence[(menuSize + 1)];
                CharSequence[] writeExtraInfos3 = new CharSequence[(menuSize + 1)];
                boolean isReadyToAdd = false;
                int i = 0;
                int j = 0;
                while (i < menuSize) {
                    if (isReadyToAdd) {
                        writeEntries[j] = entry;
                        writeEntriyValues[j] = entriyValue;
                        writeExtraInfos[j] = extraInfo;
                        writeExtraInfos2[j] = extraInfo2;
                        writeExtraInfos3[j] = extraInfo3;
                        isReadyToAdd = false;
                        j++;
                    }
                    writeEntries[j] = readEntries[i];
                    writeEntriyValues[j] = readEntriyValues[i];
                    writeExtraInfos[j] = readExtraInfos[i];
                    writeExtraInfos2[j] = readExtraInfos2[i];
                    writeExtraInfos3[j] = readExtraInfos3[i];
                    if (CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(writeEntriyValues[j])) {
                        isReadyToAdd = true;
                    }
                    i++;
                    j++;
                }
                shotModePref.setMenuIconResources(writeMenuIconResources);
                shotModePref.setEntries(writeEntries);
                shotModePref.setEntryValues(writeEntriyValues);
                shotModePref.setExtraInfos(writeExtraInfos);
                shotModePref.setExtraInfos2(writeExtraInfos2);
                shotModePref.setExtraInfos3(writeExtraInfos3);
            }
        }
    }

    private void addIsoMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if (CameraConstants.PREF_NAME_MAIN_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName())) {
            String modelName = ModelProperties.readModelName();
            if (ModelProperties.NAME_L50_VDF.equals(modelName) || ModelProperties.NAME_L50_TEL.equals(modelName) || ModelProperties.NAME_L50_3M.equals(modelName) || ModelProperties.NAME_L50_OPEN.equals(modelName) || ModelProperties.NAME_L50_FBD.equals(modelName) || ModelProperties.NAME_L50_TR.equals(modelName)) {
                ListPreference listPref = mPreferenceGroup.findPreference(Setting.KEY_ISO);
                if (listPref != null) {
                    int[] settingMenuIconResources = new int[]{R.drawable.camera_icon_sub_iso_auto_expand, R.drawable.camera_icon_sub_iso_100_expand, R.drawable.camera_icon_sub_iso_200_expand, R.drawable.camera_icon_sub_iso_400_expand};
                    CharSequence[] entries = new CharSequence[]{context.getResources().getString(R.string.auto), context.getResources().getString(R.string.iso_100), context.getResources().getString(R.string.iso_200), context.getResources().getString(R.string.iso_400)};
                    CharSequence[] entriyValues = new CharSequence[]{LGT_Limit.ISP_AUTOMODE_AUTO, "100", "200", "400"};
                    listPref.setMenuIconResources(new int[]{R.drawable.levellist_setting_expand_iso_auto, R.drawable.levellist_setting_expand_iso_100, R.drawable.levellist_setting_expand_iso_200, R.drawable.levellist_setting_expand_iso_400});
                    listPref.setSettingMenuIconResources(settingMenuIconResources);
                    listPref.setEntries(entries);
                    listPref.setEntryValues(entriyValues);
                }
            }
        }
    }

    private void addPortraitPlusMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if (ModelProperties.getCarrierCode() != 19) {
            return;
        }
        ListPreference listPref;
        if (CameraConstants.PREF_NAME_MAIN_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName())) {
            listPref = mPreferenceGroup.findPreference(Setting.KEY_CAMERA_SHOT_MODE);
            if (listPref != null) {
                CharSequence[] entries = listPref.getEntries();
                for (int i = 0; i < entries.length; i++) {
                    if (entries[i].equals(context.getResources().getString(R.string.sp_shot_mode_beauty_NORMAL))) {
                        entries[i] = context.getResources().getString(R.string.portrait_plus);
                    }
                }
                listPref.setEntries(entries);
            }
        } else if (CameraConstants.PREF_NAME_FRONT_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName())) {
            listPref = mPreferenceGroup.findPreference(Setting.KEY_BEAUTYSHOT);
            if (listPref != null) {
                listPref.setTitle(context.getResources().getString(R.string.portrait_plus));
            }
        }
    }

    private void addTimerMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if ((CameraConstants.PREF_NAME_MAIN_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName()) || CameraConstants.PREF_NAME_FRONT_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName())) && mPreferenceGroup.findPreference(Setting.KEY_CAMERA_TIMER) == null) {
            mPreferenceGroup.addChild(makeTimerListPreference(context, mPreferenceGroup));
        }
    }

    private ListPreference makeTimerListPreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIconResourcesExpand = new int[]{R.drawable.levellist_setting_expand_timer_none, R.drawable.levellist_setting_expand_timer_3, R.drawable.levellist_setting_expand_timer_5, R.drawable.levellist_setting_expand_timer_10};
        int[] settingMenuResourcesExpand = new int[]{R.drawable.camera_icon_sub_timer_none_expand, R.drawable.camera_icon_sub_timer_3_expand, R.drawable.camera_icon_sub_timer_5_expand, R.drawable.camera_icon_sub_timer_10_expand};
        CharSequence[] entries = new CharSequence[]{context.getResources().getString(R.string.off), context.getResources().getString(R.string.timer_3), context.getResources().getString(R.string.timer_5), context.getResources().getString(R.string.timer_10)};
        CharSequence[] entriyValues = new CharSequence[]{"0", "3", "5", "10"};
        int[] indicatorIconResources = new int[]{R.drawable.cam_icon_empty, R.drawable.cam_icon_line2_3, R.drawable.cam_icon_line2_2, R.drawable.cam_icon_line2_1};
        ListPreference timerPref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        timerPref.setKey(Setting.KEY_CAMERA_TIMER);
        timerPref.setTitle(context.getResources().getString(R.string.timer));
        timerPref.setMenuIconResources(menuIconResourcesExpand);
        timerPref.setSettingMenuIconResources(settingMenuResourcesExpand);
        timerPref.setCommand(Command.SHOW_QUICK_FUNCTION_SETTING_MENU);
        timerPref.setEntryCommand(Command.CAMERA_TIMER);
        timerPref.setEntries(entries);
        timerPref.setEntryValues(entriyValues);
        timerPref.setIndicatorIconResources(indicatorIconResources);
        timerPref.setDefaultValue("0");
        timerPref.setPersist(false);
        return timerPref;
    }

    private void addDateStampMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if (!FunctionProperties.isDateStampSupported()) {
            return;
        }
        if ((CameraConstants.PREF_NAME_MAIN_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName()) || CameraConstants.PREF_NAME_FRONT_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName())) && mPreferenceGroup.findPreference(Setting.KEY_DATE_STAMP) == null) {
            mPreferenceGroup.addChild(makeDateStampListPreference(context, mPreferenceGroup));
        }
    }

    private ListPreference makeDateStampListPreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIconResources = new int[]{R.drawable.camera_preview_quickfunction_date_stamp_off, R.drawable.camera_preview_quickfunction_date_stamp_on};
        int[] settingMenuResources = new int[]{R.drawable.camera_icon_date_stamp};
        CharSequence[] entries = new CharSequence[]{context.getResources().getString(R.string.off), context.getResources().getString(R.string.on)};
        CharSequence[] entriyValues = new CharSequence[]{CameraConstants.SMART_MODE_OFF, CameraConstants.SMART_MODE_ON};
        ListPreference dateStampPref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        dateStampPref.setKey(Setting.KEY_DATE_STAMP);
        dateStampPref.setTitle(context.getResources().getString(R.string.sp_date_stamp));
        dateStampPref.setMenuIconResources(menuIconResources);
        dateStampPref.setSettingMenuIconResources(settingMenuResources);
        dateStampPref.setCommand(Command.SHOW_QUICK_FUNCTION_SETTING_MENU);
        dateStampPref.setEntryCommand("com.lge.camera.command.setting.SetDateStamp");
        dateStampPref.setEntries(entries);
        dateStampPref.setEntryValues(entriyValues);
        dateStampPref.setIndicatorIconResources(null);
        dateStampPref.setDefaultValue(CameraConstants.SMART_MODE_OFF);
        dateStampPref.setPersist(true);
        return dateStampPref;
    }

    private void addAutoReviewMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if (!ProjectVariables.isSupportedAutoReview()) {
            return;
        }
        if (CameraConstants.PREF_NAME_MAIN_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName()) || CameraConstants.PREF_NAME_FRONT_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName())) {
            if (mPreferenceGroup.findPreference(Setting.KEY_CAMERA_AUTO_REVIEW) == null) {
                mPreferenceGroup.addChild(makeCameraAutoReviewListPreference(context, mPreferenceGroup));
            }
        } else if (mPreferenceGroup.findPreference(Setting.KEY_VIDEO_AUTO_REVIEW) == null) {
            mPreferenceGroup.addChild(makeVideoAutoReviewListPreference(context, mPreferenceGroup));
        }
    }

    private ListPreference makeCameraAutoReviewListPreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIconResourcesExpand = new int[]{R.drawable.levellist_setting_expand_show_captured_off, R.drawable.levellist_setting_expand_show_captured_on, R.drawable.levellist_setting_expand_show_captured_2, R.drawable.levellist_setting_expand_show_captured_5};
        int[] settingMenuResourcesExpand = new int[]{R.drawable.camera_icon_sub_show_captured_image_off, R.drawable.camera_icon_sub_show_captured_image_on, R.drawable.camera_icon_sub_show_captured_image_2, R.drawable.camera_icon_sub_show_captured_image_5};
        CharSequence[] entries = new CharSequence[]{context.getResources().getString(R.string.off), context.getResources().getString(R.string.on), context.getResources().getString(R.string.sp_review_timer_2_NORMAL), context.getResources().getString(R.string.timer_5)};
        CharSequence[] entriyValues = new CharSequence[]{CameraConstants.SMART_MODE_OFF, CameraConstants.SMART_MODE_ON, "on_delay_2sec", "on_delay_5sec"};
        ListPreference cameraAutoReviewPref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        cameraAutoReviewPref.setKey(Setting.KEY_CAMERA_AUTO_REVIEW);
        cameraAutoReviewPref.setTitle(context.getResources().getString(R.string.auto_review));
        cameraAutoReviewPref.setMenuIconResources(menuIconResourcesExpand);
        cameraAutoReviewPref.setSettingMenuIconResources(settingMenuResourcesExpand);
        cameraAutoReviewPref.setCommand(Command.SHOW_QUICK_FUNCTION_SETTING_MENU);
        cameraAutoReviewPref.setEntryCommand(Command.CAMERA_AUTO_REVIEW);
        cameraAutoReviewPref.setEntries(entries);
        cameraAutoReviewPref.setEntryValues(entriyValues);
        cameraAutoReviewPref.setIndicatorIconResources(null);
        cameraAutoReviewPref.setDefaultValue(CameraConstants.SMART_MODE_OFF);
        cameraAutoReviewPref.setPersist(true);
        return cameraAutoReviewPref;
    }

    private ListPreference makeVideoAutoReviewListPreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIconResourcesExpand = new int[]{R.drawable.levellist_setting_expand_show_recorded_off, R.drawable.levellist_setting_expand_show_recorded_on, R.drawable.levellist_setting_expand_show_recorded_2, R.drawable.levellist_setting_expand_show_recorded_5};
        int[] settingMenuResourcesExpand = new int[]{R.drawable.camera_icon_sub_show_recorded_video_off, R.drawable.camera_icon_sub_show_recorded_video_on, R.drawable.camera_icon_sub_show_recorded_video_2, R.drawable.camera_icon_sub_show_recorded_video_5};
        CharSequence[] entries = new CharSequence[]{context.getResources().getString(R.string.off), context.getResources().getString(R.string.on), context.getResources().getString(R.string.sp_review_timer_2_NORMAL), context.getResources().getString(R.string.timer_5)};
        CharSequence[] entriyValues = new CharSequence[]{CameraConstants.SMART_MODE_OFF, CameraConstants.SMART_MODE_ON, "on_delay_2sec", "on_delay_5sec"};
        ListPreference videoAutoReviewPref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        videoAutoReviewPref.setKey(Setting.KEY_VIDEO_AUTO_REVIEW);
        videoAutoReviewPref.setTitle(context.getResources().getString(R.string.show_recorded_video));
        videoAutoReviewPref.setMenuIconResources(menuIconResourcesExpand);
        videoAutoReviewPref.setSettingMenuIconResources(settingMenuResourcesExpand);
        videoAutoReviewPref.setCommand(Command.SHOW_QUICK_FUNCTION_SETTING_MENU);
        videoAutoReviewPref.setEntryCommand("com.lge.camera.command.setting.SetVideoShowRecordedVideo");
        videoAutoReviewPref.setEntries(entries);
        videoAutoReviewPref.setEntryValues(entriyValues);
        videoAutoReviewPref.setIndicatorIconResources(null);
        videoAutoReviewPref.setDefaultValue(CameraConstants.SMART_MODE_OFF);
        videoAutoReviewPref.setPersist(true);
        return videoAutoReviewPref;
    }

    private void addTagLocationMenu(Context context, PreferenceGroup mPreferenceGroup) {
        ListPreference listPref = mPreferenceGroup.findPreference(Setting.KEY_CAMERA_TAG_LOCATION);
        if (FunctionProperties.isSupportedTagLocation() && listPref == null) {
            mPreferenceGroup.addChild(makeTagLocationListPreference(context, mPreferenceGroup));
        }
    }

    private ListPreference makeTagLocationListPreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIconResourcesExpand = new int[]{R.drawable.levellist_setting_expand_geotagging_off, R.drawable.levellist_setting_expand_geotagging_on};
        int[] settingMenuResourcesExpand = new int[]{R.drawable.camera_icon_sub_geotagging_off_expand, R.drawable.camera_icon_sub_geotagging_on_expand};
        CharSequence[] entries = new CharSequence[]{context.getResources().getString(R.string.off), context.getResources().getString(R.string.on)};
        CharSequence[] entriyValues = new CharSequence[]{CameraConstants.SMART_MODE_OFF, CameraConstants.SMART_MODE_ON};
        int[] indicatorIconResources = new int[]{R.drawable.cam_icon_empty, R.drawable.cam_icon_line4_1};
        ListPreference tagLocationPref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        tagLocationPref.setKey(Setting.KEY_CAMERA_TAG_LOCATION);
        tagLocationPref.setTitle(context.getResources().getString(R.string.sp_geotagging_NORMAL));
        tagLocationPref.setMenuIconResources(menuIconResourcesExpand);
        tagLocationPref.setSettingMenuIconResources(settingMenuResourcesExpand);
        tagLocationPref.setCommand(Command.SHOW_QUICK_FUNCTION_SETTING_MENU);
        tagLocationPref.setEntryCommand(Command.CAMERA_GEO_TAG);
        tagLocationPref.setEntries(entries);
        tagLocationPref.setEntryValues(entriyValues);
        tagLocationPref.setIndicatorIconResources(indicatorIconResources);
        tagLocationPref.setDefaultValue(CameraConstants.SMART_MODE_OFF);
        tagLocationPref.setPersist(true);
        return tagLocationPref;
    }

    private void addSaveAsFlippedMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if (ModelProperties.isSupportFrontCameraModel() && CameraConstants.PREF_NAME_FRONT_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName()) && mPreferenceGroup.findPreference(Setting.KEY_SAVE_DIRECTION) == null) {
            mPreferenceGroup.addChild(makeSaveAsFlippedListPreference(context, mPreferenceGroup));
        }
    }

    private ListPreference makeSaveAsFlippedListPreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIconResourcesExpand = new int[]{R.drawable.levellist_setting_expand_save_as_flipped_on, R.drawable.levellist_setting_expand_save_as_flipped_off};
        int[] settingMenuResourcesExpand = new int[]{R.drawable.camera_icon_sub_save_on, R.drawable.camera_icon_sub_save_off};
        CharSequence[] entries = new CharSequence[]{context.getResources().getString(R.string.off), context.getResources().getString(R.string.on)};
        CharSequence[] entriyValues = new CharSequence[]{CameraConstants.SMART_MODE_OFF, CameraConstants.SMART_MODE_ON};
        int[] indicatorIconResources = new int[]{R.drawable.cam_icon_empty, R.drawable.cam_icon_line4_1};
        ListPreference saveDirectionPref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        saveDirectionPref.setKey(Setting.KEY_SAVE_DIRECTION);
        saveDirectionPref.setTitle(context.getResources().getString(R.string.sp_save_as_flipped_NORMAL));
        saveDirectionPref.setMenuIconResources(menuIconResourcesExpand);
        saveDirectionPref.setSettingMenuIconResources(settingMenuResourcesExpand);
        saveDirectionPref.setCommand(Command.SHOW_QUICK_FUNCTION_SETTING_MENU);
        saveDirectionPref.setEntryCommand("com.lge.camera.command.setting.SetSaveDirectionMode");
        saveDirectionPref.setEntries(entries);
        saveDirectionPref.setEntryValues(entriyValues);
        saveDirectionPref.setIndicatorIconResources(indicatorIconResources);
        saveDirectionPref.setDefaultValue(CameraConstants.SMART_MODE_ON);
        saveDirectionPref.setPersist(true);
        return saveDirectionPref;
    }

    private void addShutterSoundMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if ((CameraConstants.PREF_NAME_MAIN_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName()) || CameraConstants.PREF_NAME_FRONT_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName())) && mPreferenceGroup.findPreference(Setting.KEY_SHUTTER_SOUND) == null) {
            mPreferenceGroup.addChild(makeShutterSoundListPreference(context, mPreferenceGroup));
        }
    }

    private ListPreference makeShutterSoundListPreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIcon;
        int[] settingMenuIcon;
        CharSequence[] entery;
        CharSequence[] enteryValue;
        int[] menuIconResources_offExpand = new int[]{R.drawable.levellist_setting_expand_shutter_sound_off, R.drawable.levellist_setting_expand_shutter_sound_1, R.drawable.levellist_setting_expand_shutter_sound_2, R.drawable.levellist_setting_expand_shutter_sound_3, R.drawable.levellist_setting_expand_shutter_sound_4};
        int[] menuIconResourcesExpand = new int[]{R.drawable.levellist_setting_expand_shutter_sound_1, R.drawable.levellist_setting_expand_shutter_sound_2, R.drawable.levellist_setting_expand_shutter_sound_3, R.drawable.levellist_setting_expand_shutter_sound_4};
        int[] settingMenuResources_offExpand = new int[]{R.drawable.camera_icon_sub_shutter_sound_off_expand, R.drawable.camera_icon_sub_shutter_sound_1_expand, R.drawable.camera_icon_sub_shutter_sound_2_expand, R.drawable.camera_icon_sub_shutter_sound_3_expand, R.drawable.camera_icon_sub_shutter_sound_4_expand};
        int[] settingMenuResourcesExpand = new int[]{R.drawable.camera_icon_sub_shutter_sound_1_expand, R.drawable.camera_icon_sub_shutter_sound_2_expand, R.drawable.camera_icon_sub_shutter_sound_3_expand, R.drawable.camera_icon_sub_shutter_sound_4_expand};
        CharSequence[] entries_off = new CharSequence[]{context.getResources().getString(R.string.off), context.getResources().getString(R.string.shutter_sound_1), context.getResources().getString(R.string.shutter_sound_2), context.getResources().getString(R.string.shutter_sound_3), context.getResources().getString(R.string.shutter_sound_4)};
        CharSequence[] entries = new CharSequence[]{context.getResources().getString(R.string.shutter_sound_1), context.getResources().getString(R.string.shutter_sound_2), context.getResources().getString(R.string.shutter_sound_3), context.getResources().getString(R.string.shutter_sound_4)};
        CharSequence[] entriyValues_off = new CharSequence[]{CameraConstants.SMART_MODE_OFF, "0", "1", "2", "3"};
        CharSequence[] entriyValues = new CharSequence[]{"0", "1", "2", "3"};
        if (ShutterSoundProperties.isSupportShutterSoundOff()) {
            menuIcon = menuIconResources_offExpand;
            settingMenuIcon = settingMenuResources_offExpand;
        } else {
            menuIcon = menuIconResourcesExpand;
            settingMenuIcon = settingMenuResourcesExpand;
        }
        if (ShutterSoundProperties.isSupportShutterSoundOff()) {
            entery = entries_off;
        } else {
            entery = entries;
        }
        if (ShutterSoundProperties.isSupportShutterSoundOff()) {
            enteryValue = entriyValues_off;
        } else {
            enteryValue = entriyValues;
        }
        ListPreference shutterSoundPref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        shutterSoundPref.setKey(Setting.KEY_SHUTTER_SOUND);
        shutterSoundPref.setTitle(context.getResources().getString(R.string.shutter_sound));
        shutterSoundPref.setMenuIconResources(menuIcon);
        shutterSoundPref.setSettingMenuIconResources(settingMenuIcon);
        shutterSoundPref.setCommand(Command.SHOW_QUICK_FUNCTION_SETTING_MENU);
        shutterSoundPref.setEntryCommand(Command.CAMERA_SHUTTER_SOUND);
        shutterSoundPref.setEntries(entery);
        shutterSoundPref.setEntryValues(enteryValue);
        shutterSoundPref.setIndicatorIconResources(null);
        shutterSoundPref.setDefaultValue("0");
        shutterSoundPref.setPersist(true);
        return shutterSoundPref;
    }

    private void addStorageMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if (StorageProperties.isAllMemorySupported() && mPreferenceGroup.findPreference(Setting.KEY_STORAGE) == null) {
            mPreferenceGroup.addChild(makeStorageListPreference(context, mPreferenceGroup));
        }
    }

    private ListPreference makeStorageListPreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIconResourcesExpand = new int[]{R.drawable.levellist_setting_expand_memory_card, R.drawable.levellist_setting_expand_memory_card_internal};
        int[] settingMenuResourcesExpand = new int[]{R.drawable.camera_icon_sub_memory_card, R.drawable.camera_icon_sub_memory_card_internal_expand};
        CharSequence[] entries = new CharSequence[]{context.getResources().getString(R.string.sp_external_memory_NORMAL), context.getResources().getString(R.string.sp_internal_storage_NORMAL)};
        CharSequence[] entriyValues = new CharSequence[]{CameraConstants.NAME_EXTERNAL_MEMORY, CameraConstants.NAME_INTERNAL_STORAGE};
        int[] indicatorIconResources = new int[]{R.drawable.cam_icon_line5_1, R.drawable.cam_icon_empty};
        ListPreference storagePref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        storagePref.setKey(Setting.KEY_STORAGE);
        storagePref.setTitle(context.getResources().getString(R.string.sp_storage_NORMAL));
        storagePref.setMenuIconResources(menuIconResourcesExpand);
        storagePref.setSettingMenuIconResources(settingMenuResourcesExpand);
        storagePref.setCommand(Command.SHOW_QUICK_FUNCTION_SETTING_MENU);
        storagePref.setEntryCommand(Command.SET_STORAGE);
        storagePref.setEntries(entries);
        storagePref.setEntryValues(entriyValues);
        storagePref.setIndicatorIconResources(indicatorIconResources);
        storagePref.setDefaultValue(CameraConstants.NAME_EXTERNAL_MEMORY);
        storagePref.setPersist(true);
        return storagePref;
    }

    private void addManualAntiBandingMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if (CameraConstants.PREF_NAME_FRONT_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName()) && ProjectVariables.isSupportManualAntibanding() && mPreferenceGroup.findPreference(Setting.KEY_CAMERA_ANTI_BANDING) == null) {
            mPreferenceGroup.addChild(makeManualAntiBandingListPreference(context, mPreferenceGroup));
        }
    }

    private ListPreference makeManualAntiBandingListPreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIconResourcesExpand = new int[]{R.drawable.levellist_setting_expand_anti_banding_50hz, R.drawable.levellist_setting_expand_anti_banding_60hz};
        int[] settingMenuResourcesExpand = new int[]{R.drawable.camera_icon_sub_anti_banding_50hz, R.drawable.camera_icon_sub_anti_banding_60hz};
        CharSequence[] entries = new CharSequence[]{context.getResources().getString(R.string.sp_anti_banding_50hz_NORMAL), context.getResources().getString(R.string.sp_anti_banding_60hz_NORMAL)};
        CharSequence[] entriyValues = new CharSequence[]{"50", "60"};
        ListPreference antiBandingPref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        antiBandingPref.setKey(Setting.KEY_CAMERA_ANTI_BANDING);
        antiBandingPref.setTitle(context.getResources().getString(R.string.sp_anti_banding_NORMAL));
        antiBandingPref.setMenuIconResources(menuIconResourcesExpand);
        antiBandingPref.setSettingMenuIconResources(settingMenuResourcesExpand);
        antiBandingPref.setCommand(Command.SHOW_QUICK_FUNCTION_SETTING_MENU);
        antiBandingPref.setEntryCommand(Command.CAMERA_ANTI_BANDING);
        antiBandingPref.setEntries(entries);
        antiBandingPref.setEntryValues(entriyValues);
        antiBandingPref.setIndicatorIconResources(null);
        antiBandingPref.setDefaultValue("50");
        antiBandingPref.setPersist(true);
        return antiBandingPref;
    }

    private void addHideZoomMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if ((CameraConstants.PREF_NAME_MAIN_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName()) || CameraConstants.PREF_NAME_MAIN_CAMCORDER.equals(mPreferenceGroup.getSharedPreferenceName())) && mPreferenceGroup.findPreference(Setting.KEY_ZOOM) == null) {
            mPreferenceGroup.addChild(makeHideZoomListPreference(context, mPreferenceGroup));
        }
    }

    private ListPreference makeHideZoomListPreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIconResources = new int[]{R.drawable.camera_preview_quickfunction_zoom};
        int[] settingMenuResources = new int[]{R.drawable.camera_icon_zoom};
        ListPreference zoomPref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        zoomPref.setKey(Setting.KEY_ZOOM);
        zoomPref.setTitle(context.getResources().getString(R.string.zoom));
        zoomPref.setMenuIconResources(menuIconResources);
        zoomPref.setSettingMenuIconResources(settingMenuResources);
        zoomPref.setCommand(Command.SHOW_ZOOM);
        zoomPref.setEntryCommand(Command.SET_ZOOM);
        zoomPref.setSettingMenuCommand(Command.SHOW_SETTING_ZOOM);
        zoomPref.setEntries(null);
        zoomPref.setEntryValues(null);
        zoomPref.setIndicatorIconResources(null);
        zoomPref.setDefaultValue("0");
        zoomPref.setPersist(false);
        return zoomPref;
    }

    private void removeAudioZoomMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if (CameraConstants.PREF_NAME_MAIN_CAMCORDER.equals(mPreferenceGroup.getSharedPreferenceName()) && FunctionProperties.isHideAudiozoomMenu()) {
            ListPreference listPref = mPreferenceGroup.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            int audioZoomIndex = mPreferenceGroup.findPreferenceIndex(Setting.KEY_CAMCORDER_AUDIOZOOM);
            int videoSizeIndex = mPreferenceGroup.findPreferenceIndex(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            mPreferenceGroup.removePreference(audioZoomIndex);
            if (listPref != null) {
                mPreferenceGroup.addChildAt(listPref, audioZoomIndex);
            }
            mPreferenceGroup.removePreference(videoSizeIndex);
        }
    }

    private void addHelpMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if (mPreferenceGroup.findPreference(Setting.KEY_HELP_GUIDE) == null) {
            mPreferenceGroup.addChild(makeHelpListPreference(context, mPreferenceGroup));
        }
    }

    private ListPreference makeHelpListPreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIconResourcesExpand = new int[]{R.drawable.levellist_setting_expand_question};
        int[] settingMenuResources = new int[]{R.drawable.camera_preview_quickfunction_question};
        ListPreference helpPref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        helpPref.setKey(Setting.KEY_HELP_GUIDE);
        helpPref.setTitle(context.getResources().getString(R.string.help));
        helpPref.setMenuIconResources(menuIconResourcesExpand);
        helpPref.setSettingMenuIconResources(settingMenuResources);
        helpPref.setSettingMenuCommand(Command.SHOW_HELP_ACTIVITY);
        helpPref.setEntries(null);
        helpPref.setEntryValues(null);
        helpPref.setIndicatorIconResources(null);
        helpPref.setPersist(false);
        return helpPref;
    }

    private void addRestoreMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if (mPreferenceGroup.findPreference(Setting.KEY_RESTORE) == null) {
            mPreferenceGroup.addChild(makeRestoreListPreference(context, mPreferenceGroup));
        }
    }

    private ListPreference makeRestoreListPreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIconResourcesExpand = new int[]{R.drawable.levellist_setting_expand_restore};
        int[] settingMenuResources = new int[]{R.drawable.camera_preview_quickfunction_restore};
        ListPreference restorePref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        restorePref.setKey(Setting.KEY_RESTORE);
        restorePref.setTitle(context.getResources().getString(R.string.reset));
        restorePref.setMenuIconResources(menuIconResourcesExpand);
        restorePref.setSettingMenuIconResources(settingMenuResources);
        restorePref.setSettingMenuCommand(Command.SHOW_RESET_DIALOG);
        restorePref.setEntries(null);
        restorePref.setEntryValues(null);
        restorePref.setIndicatorIconResources(null);
        restorePref.setPersist(false);
        return restorePref;
    }

    private void changeFlashDefaultValue(Context context, PreferenceGroup mPreferenceGroup) {
        if (CameraConstants.PREF_NAME_MAIN_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName()) && !FunctionProperties.isNoneFlashModel()) {
            ListPreference listPref = mPreferenceGroup.findPreference(Setting.KEY_FLASH);
            if (listPref != null) {
                listPref.setDefaultValue(FunctionProperties.getFlashDefaultValue());
            }
        }
        if (mPreferenceGroup.findPreference(Setting.KEY_LIGHT) == null && FunctionProperties.isSupportLightFrame()) {
            mPreferenceGroup.addChild(makeLightFramePreference(context, mPreferenceGroup));
        }
    }

    private void addVolumeKeyMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if (mPreferenceGroup.findPreference(Setting.KEY_VOLUME) == null) {
            mPreferenceGroup.addChild(makeVolumeKeyListPreference(context, mPreferenceGroup));
        }
    }

    private ListPreference makeVolumeKeyListPreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIcon;
        int[] settingMenuIcon;
        CharSequence[] entries;
        boolean isCamcorder = CameraConstants.PREF_NAME_MAIN_CAMCORDER.equals(mPreferenceGroup.getSharedPreferenceName()) || CameraConstants.PREF_NAME_FRONT_CAMCORDER.equals(mPreferenceGroup.getSharedPreferenceName());
        int[] menuIconResourcesExpandCamera = new int[]{R.drawable.levellist_setting_expand_volume_shutter_camera, R.drawable.levellist_setting_expand_volume_zoom};
        int[] menuIconResourcesExpandVideo = new int[]{R.drawable.levellist_setting_expand_volume_shutter_video, R.drawable.levellist_setting_expand_volume_zoom};
        int[] settingMenuResourcesExpandCamera = new int[]{R.drawable.camera_icon_sub_volume_shutter_camera, R.drawable.camera_icon_sub_volume_zoom};
        int[] settingMenuResourcesExpandVideo = new int[]{R.drawable.camera_icon_sub_volume_shutter_video, R.drawable.camera_icon_sub_volume_zoom};
        CharSequence[] cameraEntries = new CharSequence[]{context.getResources().getString(R.string.volume_key_capture_image), context.getResources().getString(R.string.volume_key_zoom)};
        CharSequence[] videoEntries = new CharSequence[]{context.getResources().getString(R.string.volume_key_record_video), context.getResources().getString(R.string.volume_key_zoom)};
        CharSequence[] entriyValues = new CharSequence[]{CameraConstants.VOLUME_SHUTTER, LGT_Limit.ISP_ZOOM};
        if (isCamcorder) {
            menuIcon = menuIconResourcesExpandVideo;
        } else {
            menuIcon = menuIconResourcesExpandCamera;
        }
        if (isCamcorder) {
            settingMenuIcon = settingMenuResourcesExpandVideo;
        } else {
            settingMenuIcon = settingMenuResourcesExpandCamera;
        }
        if (isCamcorder) {
            entries = videoEntries;
        } else {
            entries = cameraEntries;
        }
        ListPreference volumePref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        volumePref.setKey(Setting.KEY_VOLUME);
        volumePref.setTitle(context.getResources().getString(R.string.volume_key));
        volumePref.setMenuIconResources(menuIcon);
        volumePref.setSettingMenuIconResources(settingMenuIcon);
        volumePref.setCommand(Command.SHOW_QUICK_FUNCTION_SETTING_MENU);
        volumePref.setEntryCommand(Command.SET_VOLUME_KEY);
        volumePref.setEntries(entries);
        volumePref.setEntryValues(entriyValues);
        volumePref.setIndicatorIconResources(null);
        if (FunctionProperties.isSupportVolumeHotKey()) {
            volumePref.setDefaultValue(CameraConstants.VOLUME_SHUTTER);
        } else {
            volumePref.setDefaultValue(LGT_Limit.ISP_ZOOM);
        }
        volumePref.setPersist(true);
        return volumePref;
    }

    private void addAuCloudMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if (ModelProperties.getCarrierCode() == 7 && mPreferenceGroup.findPreference(Setting.KEY_AU_CLOUD) == null) {
            mPreferenceGroup.addChild(makeAuCloudListPreference(context, mPreferenceGroup));
        }
    }

    private ListPreference makeAuCloudListPreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIconResourcesExpand = new int[]{R.drawable.levellist_setting_expand_aucloud};
        int[] settingMenuResources = new int[]{R.drawable.camera_icon_auto_upload};
        ListPreference auCloudPref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        auCloudPref.setKey(Setting.KEY_AU_CLOUD);
        auCloudPref.setTitle(context.getResources().getString(R.string.sp_auto_upload_NORMAL));
        auCloudPref.setMenuIconResources(menuIconResourcesExpand);
        auCloudPref.setSettingMenuIconResources(settingMenuResources);
        auCloudPref.setSettingMenuCommand(Command.GOTO_AUCLOUD);
        auCloudPref.setEntries(null);
        auCloudPref.setEntryValues(null);
        auCloudPref.setIndicatorIconResources(null);
        auCloudPref.setPersist(true);
        return auCloudPref;
    }

    private void addVideoStabilizationMenu(Context context, PreferenceGroup mPreferenceGroup) {
        if (FunctionProperties.isVideoStabilizationSupported() && mPreferenceGroup.findPreference(Setting.KEY_VIDEO_STABILIZATION) == null) {
            mPreferenceGroup.addChild(makeVideoStabilizationListPreference(context, mPreferenceGroup));
        }
    }

    private ListPreference makeVideoStabilizationListPreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIconResourcesExpand = new int[]{R.drawable.levellist_setting_expand_video_stabilization_off, R.drawable.levellist_setting_expand_video_stabilization_on};
        int[] settingMenuResourcesExpand = new int[]{R.drawable.camera_icon_sub_video_stabilization_off_expand, R.drawable.camera_icon_sub_video_stabilization_on_expand};
        CharSequence[] entries = new CharSequence[]{context.getResources().getString(R.string.off), context.getResources().getString(R.string.on)};
        CharSequence[] entriyValues = new CharSequence[]{CameraConstants.SMART_MODE_OFF, CameraConstants.SMART_MODE_ON};
        int[] indicatorIconResources = new int[]{R.drawable.cam_icon_empty, R.drawable.cam_icon_line6};
        ListPreference videoStabilizationPref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        videoStabilizationPref.setKey(Setting.KEY_VIDEO_STABILIZATION);
        videoStabilizationPref.setTitle(context.getResources().getString(R.string.anti_shaking));
        videoStabilizationPref.setMenuIconResources(menuIconResourcesExpand);
        videoStabilizationPref.setSettingMenuIconResources(settingMenuResourcesExpand);
        videoStabilizationPref.setCommand(Command.SHOW_QUICK_FUNCTION_SETTING_MENU);
        videoStabilizationPref.setEntryCommand(Command.SET_VIDEO_STABILIZATION);
        videoStabilizationPref.setEntries(entries);
        videoStabilizationPref.setEntryValues(entriyValues);
        videoStabilizationPref.setIndicatorIconResources(indicatorIconResources);
        videoStabilizationPref.setDefaultValue(CameraConstants.SMART_MODE_OFF);
        videoStabilizationPref.setPersist(true);
        return videoStabilizationPref;
    }

    private ListPreference makeLightFramePreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIconResourcesExpand = new int[]{R.drawable.camera_preview_quickfunction_flash_off, R.drawable.camera_preview_quickfunction_flash_on};
        int[] settingMenuResourcesExpand = new int[]{R.drawable.camera_icon_sub_flash_off_expand, R.drawable.camera_icon_sub_flash_on_expand};
        CharSequence[] entries = new CharSequence[]{context.getResources().getString(R.string.off), context.getResources().getString(R.string.on)};
        CharSequence[] entriyValues = new CharSequence[]{CameraConstants.SMART_MODE_OFF, CameraConstants.SMART_MODE_ON};
        ListPreference lightFramePref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        lightFramePref.setKey(Setting.KEY_LIGHT);
        lightFramePref.setTitle(context.getResources().getString(R.string.key_light));
        lightFramePref.setMenuIconResources(menuIconResourcesExpand);
        lightFramePref.setSettingMenuIconResources(settingMenuResourcesExpand);
        lightFramePref.setEntries(entries);
        lightFramePref.setEntryValues(entriyValues);
        lightFramePref.setDefaultValue(CameraConstants.SMART_MODE_OFF);
        lightFramePref.setPersist(true);
        return lightFramePref;
    }

    public void changeDefaultImageSizeForPanorama(Context context, PreferenceGroup mPreferenceGroup) {
        ListPreference listPref = mPreferenceGroup.findPreference(Setting.KEY_CAMERA_SHOT_MODE);
        if (listPref != null) {
            CharSequence[] extraInfos3 = listPref.getExtraInfos3();
            String modelName = Build.MODEL;
            if (ModelProperties.getProjectCode() == 16 && ModelProperties.NAME_W7_TMUS.equals(modelName)) {
                extraInfos3 = context.getResources().getStringArray(R.array.w7_5m_camera_shot_mode_extraInfos_change_size_message);
            } else if (ModelProperties.getProjectCode() == 22 && ModelProperties.NAME_X5_SPCS.equals(modelName)) {
                extraInfos3 = context.getResources().getStringArray(R.array.x5_8m_camera_shot_mode_extraInfos_change_size_message);
            } else if (ModelProperties.NAME_W5_TCL.equals(modelName) || ModelProperties.NAME_W5_IUSACELL.equals(modelName) || ModelProperties.NAME_W5_ARGENTINA.equals(modelName) || ModelProperties.NAME_W5_PANAMA.equals(modelName) || ModelProperties.NAME_W5_BRAZIL1.equals(modelName) || ModelProperties.NAME_W5_BRAZIL2.equals(modelName) || ModelProperties.NAME_W5_ENTEL.equals(modelName)) {
                extraInfos3 = context.getResources().getStringArray(R.array.w5_8m_camera_shot_mode_extraInfos_change_size_message);
            } else if (ModelProperties.NAME_W6_TIM.equals(modelName) || ModelProperties.NAME_W6_TCL.equals(modelName) || ModelProperties.NAME_W6_CIS.equals(modelName) || ModelProperties.NAME_W6_EU.equals(modelName) || ModelProperties.NAME_W6_AR.equals(modelName)) {
                extraInfos3 = context.getResources().getStringArray(R.array.w6_8m_camera_shot_mode_extraInfos_change_size_message);
            }
            listPref.setExtraInfos3(extraInfos3);
        }
    }

    private void changeKeyfocusValues(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIconsExpand = null;
        int[] settingMenuIconsExpand = null;
        int[] indicatorIcons = null;
        CharSequence[] entries = null;
        CharSequence[] entryValues = null;
        String defaultValue = "";
        String modelName = Build.MODEL;
        if (ModelProperties.getProjectCode() == 25) {
            if (CameraConstants.PREF_NAME_MAIN_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName())) {
                menuIconsExpand = makeIconList(context, R.array.camera_focus_manual_menuIcons_expand);
                settingMenuIconsExpand = makeIconList(context, R.array.camera_manual_settingMenuIcons_expand);
                entries = context.getResources().getStringArray(R.array.camera_focus_manual_entries);
                entryValues = context.getResources().getStringArray(R.array.camera_focus_manual_entryValues);
                defaultValue = LGT_Limit.ISP_AUTOMODE_AUTO;
                indicatorIcons = makeIconList(context, R.array.camera_focus_manual_menuIcons_expand);
            }
            ListPreference listPref = mPreferenceGroup.findPreference(Setting.KEY_FOCUS);
            if (listPref != null) {
                listPref.setMenuIconResources(menuIconsExpand);
                listPref.setSettingMenuIconResources(settingMenuIconsExpand);
                listPref.setIndicatorIconResources(indicatorIcons);
                listPref.setEntries(entries);
                listPref.setEntryValues(entryValues);
                listPref.setDefaultValue(defaultValue);
                CamLog.e("tag", "value: " + listPref.getDefaultValue());
            }
        }
    }

    private void changeDefaultImageSize(Context context, PreferenceGroup mPreferenceGroup) {
        ListPreference listPref = mPreferenceGroup.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
        if (listPref != null) {
            if ("TMO".equals(SystemProperties.get("ro.build.target_operator")) && ModelProperties.getProjectCode() == 17 && "TMA".equals(SystemProperties.get("ro.build.default_country")) && "COM".equals(SystemProperties.get("ro.build.target_country"))) {
                listPref.setDefaultValue("2048x1536");
            } else if (!"TMO".equals(SystemProperties.get("ro.build.target_operator")) || ModelProperties.getProjectCode() != 24) {
            } else {
                if (("TMA".equals(SystemProperties.get("ro.build.default_country")) || "TRA".equals(SystemProperties.get("ro.build.default_country"))) && "COM".equals(SystemProperties.get("ro.build.target_country"))) {
                    if (CameraConstants.PREF_NAME_MAIN_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName())) {
                        listPref.setDefaultValue("3264x2448");
                    }
                    if (CameraConstants.PREF_NAME_FRONT_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName())) {
                        listPref.setDefaultValue(MultimediaProperties.EFFECTS_ENFORCED_SIZE_FOR_UVGA);
                    }
                }
            }
        }
    }

    private void addManualKeyFocusValue(Context context, PreferenceGroup mPreferenceGroup) {
        if (CameraConstants.PREF_NAME_MAIN_CAMERA.equals(mPreferenceGroup.getSharedPreferenceName()) && mPreferenceGroup.findPreference(Setting.KEY_FOCUS) == null) {
            String modelName = Build.MODEL;
            if (ModelProperties.NAME_L50_TIM.equals(modelName) || ModelProperties.NAME_L50_OPEN_SCA.equals(modelName)) {
                mPreferenceGroup.addChild(makeManualFocusListPreference(context, mPreferenceGroup));
            }
        }
    }

    private ListPreference makeManualFocusListPreference(Context context, PreferenceGroup mPreferenceGroup) {
        int[] menuIconResourcesExpand = new int[]{R.drawable.levellist_setting_expand_focus_auto, R.drawable.levellist_setting_expand_focus_manual, R.drawable.levellist_setting_expand_focus_face};
        int[] settingMenuResourcesExpand = new int[]{R.drawable.camera_icon_sub_focus_auto_expand, R.drawable.camera_icon_sub_focus_manual_expand, R.drawable.camera_icon_sub_focus_face_expand};
        int[] indicatorIconResources = new int[]{R.drawable.cam_icon_indicator_focus_auto, R.drawable.cam_icon_indicator_focus_manual, R.drawable.cam_icon_indicator_focus_face};
        ListPreference keyfocusPref = new ListPreference(context, mPreferenceGroup.getSharedPreferenceName());
        keyfocusPref.setKey(Setting.KEY_FOCUS);
        keyfocusPref.setTitle(context.getResources().getString(R.string.focus));
        keyfocusPref.setMenuIconResources(menuIconResourcesExpand);
        keyfocusPref.setSettingMenuIconResources(settingMenuResourcesExpand);
        keyfocusPref.setCommand(Command.SHOW_QUICK_FUNCTION_SETTING_MENU);
        keyfocusPref.setEntryCommand(Command.CAMERA_FOCUS_MODE);
        keyfocusPref.setEntries(context.getResources().getTextArray(R.array.camera_focus_manual_entries));
        keyfocusPref.setEntryValues(context.getResources().getTextArray(R.array.camera_focus_manual_entryValues));
        keyfocusPref.setIndicatorIconResources(indicatorIconResources);
        keyfocusPref.setDefaultValue(LGT_Limit.ISP_AUTOMODE_AUTO);
        return keyfocusPref;
    }
}
