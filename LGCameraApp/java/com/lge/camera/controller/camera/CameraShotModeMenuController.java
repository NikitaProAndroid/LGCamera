package com.lge.camera.controller.camera;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.controller.ShotModeMenuController;
import com.lge.camera.controller.ShotModeMenuController.ModeItem;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;

public class CameraShotModeMenuController extends ShotModeMenuController {
    public CameraShotModeMenuController(ControllerFunction function) {
        super(function);
    }

    protected void makeItemList() {
        makeShotModeItemList();
        makeIntelligentAutoItemList();
        makeSceneModeItemList();
    }

    private void makeIntelligentAutoItemList() {
        if (this.mGet.getCameraId() == 0 && this.mModeItemList != null) {
            ListPreference listPref = this.mGet.getSettingListPreference(Setting.KEY_SMART_MODE);
            if (listPref != null) {
                String menuCommand = listPref.getEntryCommand();
                this.mModeItemList.add(new ModeItem(Setting.KEY_SMART_MODE, CameraConstants.SMART_MODE_ON, this.mGet.getString(R.string.intelligent_auto), this.mGet.getString(R.string.sp_intelligent_auto_help_desc_popup), R.drawable.levellist_camera_mode_image_intelligentauto, menuCommand));
            }
        }
    }

    private void makeShotModeItemList() {
        if (this.mModeItemList != null) {
            ListPreference listPref = this.mGet.getSettingListPreference(Setting.KEY_CAMERA_SHOT_MODE);
            if (listPref != null) {
                CharSequence[] entryValues = listPref.getEntryValues();
                String menuCommand = listPref.getEntryCommand();
                for (int i = 0; i < entryValues.length; i++) {
                    int[] itemRes = getShotModeItemResources(String.valueOf(entryValues[i]));
                    String title = this.mGet.getString(itemRes[0]);
                    String desc = this.mGet.getString(itemRes[1]);
                    int imgResId = itemRes[2];
                    this.mModeItemList.add(new ModeItem(Setting.KEY_CAMERA_SHOT_MODE, String.valueOf(entryValues[i]), title, desc, imgResId, menuCommand));
                }
            }
        }
    }

    private int[] getShotModeItemResources(String modeString) {
        int titleId;
        int messageTextId;
        int messageImageId;
        if (CameraConstants.TYPE_SHOTMODE_CONTINUOUS.equals(modeString)) {
            titleId = R.string.shot_mode_continuous;
            messageTextId = R.string.sp_help_shot_mode_continuous_shot_desc_v2_NORMAL;
            messageImageId = R.drawable.levellist_camera_mode_image_continuous;
        } else if (CameraConstants.TYPE_SHOTMODE_PANORAMA.equals(modeString)) {
            titleId = R.string.shot_mode_panorama;
            messageTextId = R.string.sp_help_guide_panorama_shot_desc_NORMAL;
            messageImageId = R.drawable.levellist_camera_mode_image_panorama;
        } else if (CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA.equals(modeString)) {
            titleId = R.string.shot_mode_panorama;
            messageTextId = R.string.sp_help_guide_panorama_shot_desc_NORMAL;
            messageImageId = R.drawable.levellist_camera_mode_image_panorama;
        } else if (CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA.equals(modeString)) {
            titleId = R.string.shot_mode_vr_panorama;
            messageTextId = R.string.sp_vrpanorama_help_desc;
            messageImageId = R.drawable.levellist_camera_mode_image_general_panorama;
        } else if (CameraConstants.TYPE_SHOTMODE_HDR.equals(modeString)) {
            titleId = R.string.sp_shot_mode_new_hdr;
            messageTextId = R.string.sp_help_guide_new_hdr_desc_list;
            messageImageId = R.drawable.levellist_camera_mode_image_hdr;
        } else if (CameraConstants.TYPE_SHOTMODE_TIMEMACHINE.equals(modeString)) {
            if (!FunctionProperties.useTimeCatchShotTitle()) {
                titleId = R.string.sp_shot_mode_time_machine_NORMAL;
                messageTextId = R.string.sp_help_guide_time_machine_desc_new_vzw_NORMAL;
            } else if (ModelProperties.getCarrierCode() == 4) {
                titleId = R.string.sp_shot_mode_time_catch_NORMAL;
                messageTextId = R.string.sp_help_guide_time_catch_new_desc_NORMAL;
            } else {
                titleId = R.string.sp_shot_mode_time_catch_NORMAL;
                messageTextId = R.string.sp_help_guide_time_machine_desc_new_vzw_NORMAL;
            }
            messageImageId = R.drawable.levellist_camera_mode_image_timemachine;
        } else if (CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(modeString)) {
            if (FunctionProperties.isSupportBurstShot()) {
                titleId = R.string.sp_shot_mode_burst;
                messageTextId = R.string.sp_burst_shot_help_desc_list;
                messageImageId = R.drawable.levellist_camera_mode_image_burstshot;
            } else {
                titleId = R.string.shot_mode_continuous;
                messageTextId = R.string.sp_help_shot_mode_continuous_shot_desc_v2_NORMAL;
                messageImageId = R.drawable.levellist_camera_mode_image_continuous;
            }
        } else if (CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(modeString) || CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(modeString)) {
            if (ModelProperties.getCarrierCode() == 19) {
                titleId = R.string.portrait_plus;
                messageTextId = R.string.sp_help_guide_beauty_shot_desc_list;
            } else {
                titleId = R.string.sp_shot_mode_beauty_NORMAL;
                messageTextId = R.string.sp_help_guide_beauty_shot_desc_list;
            }
            messageImageId = R.drawable.levellist_camera_mode_image_main_beautyshot;
        } else if (CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT.equals(modeString)) {
            titleId = R.string.sp_shot_mode_shot_and_clear;
            messageTextId = R.string.sp_clear_shot_help_desc;
            messageImageId = R.drawable.levellist_camera_mode_image_clearshot;
        } else if (CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(modeString)) {
            titleId = R.string.dual_camera;
            messageTextId = R.string.sp_dual_camera_help_desc;
            messageImageId = R.drawable.levellist_camera_mode_image_dualcamera;
        } else if (CameraConstants.TYPE_SHOTMODE_REFOCUS.equals(modeString)) {
            titleId = R.string.shot_mode_magic_focus;
            messageTextId = R.string.mode_menu_camera_refocus_new;
            messageImageId = R.drawable.levellist_camera_mode_image_refocus;
        } else {
            titleId = R.string.normal;
            messageTextId = R.string.mode_menu_camera_normal;
            messageImageId = R.drawable.levellist_camera_mode_image_normal;
        }
        return new int[]{titleId, messageTextId, messageImageId};
    }

    private void makeSceneModeItemList() {
        if (this.mModeItemList != null) {
            ListPreference listPref = this.mGet.getSettingListPreference(Setting.KEY_SCENE_MODE);
            if (listPref != null) {
                CharSequence[] entryValues = listPref.getEntryValues();
                String menuCommand = listPref.getEntryCommand();
                for (int i = 0; i < entryValues.length; i++) {
                    if (!LGT_Limit.ISP_AUTOMODE_AUTO.equals(String.valueOf(entryValues[i]))) {
                        int[] itemRes = getSceneModeItemResources(String.valueOf(entryValues[i]));
                        if (itemRes != null) {
                            String title = this.mGet.getString(itemRes[0]);
                            String desc = this.mGet.getString(itemRes[1]);
                            int imgResId = itemRes[2];
                            this.mModeItemList.add(new ModeItem(Setting.KEY_SCENE_MODE, String.valueOf(entryValues[i]), title, desc, imgResId, menuCommand));
                        }
                    }
                }
            }
        }
    }

    private int[] getSceneModeItemResources(String sceneMode) {
        int titleId = 0;
        int messageTextId = 0;
        int messageImageId = 0;
        if (LGT_Limit.ISP_ORIENTATION_PORTRAIT.equals(sceneMode) || LGT_Limit.ISP_ORIENTATION_LANDSCAPE.equals(sceneMode) || CameraConstants.SCENE_MODE_SUNSET.equals(sceneMode) || CameraConstants.SCENE_MODE_SMART_SHUTTER.equals(sceneMode) || ((!FunctionProperties.isSupportNightShotModeMenu(this.mGet.getCameraId()) && LGParameters.SCENE_MODE_NIGHT.equals(sceneMode)) || (!FunctionProperties.isSupportSportShot() && Setting.HELP_SPORTS.equals(sceneMode)))) {
            return null;
        }
        if (Setting.HELP_SPORTS.equals(sceneMode)) {
            titleId = R.string.scene_mode_sports;
            messageTextId = R.string.sp_help_scene_mode_menu_sports_desc_v2_NORMAL;
            messageImageId = R.drawable.levellist_camera_mode_image_scene_sports;
        } else if (LGParameters.SCENE_MODE_NIGHT.equals(sceneMode)) {
            titleId = R.string.scene_mode_night;
            messageTextId = R.string.help_scene_mode_menu_night_desc_new;
            messageImageId = R.drawable.levellist_camera_mode_image_scene_night;
        }
        return new int[]{titleId, messageTextId, messageImageId};
    }

    protected int getCurrentItem() {
        String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
        String intelliAuto = this.mGet.getSettingValue(Setting.KEY_SMART_MODE);
        String sceneMode = this.mGet.getSettingValue(Setting.KEY_SCENE_MODE);
        if (this.mListAdapter == null || this.mGridAdapter == null || this.mTitleText == null || this.mDescText == null) {
            return 0;
        }
        int i;
        ModeItem item;
        if (CameraConstants.SMART_MODE_ON.equals(intelliAuto)) {
            for (i = 0; i < this.mModeItemList.size(); i++) {
                item = (ModeItem) this.mModeItemList.get(i);
                if (Setting.KEY_SMART_MODE.equals(item.getKey())) {
                    this.mTitleText.setText(item.getTitle());
                    this.mDescText.setText(item.getDescription());
                    this.mListAdapter.setSelectedItem(i);
                    this.mGridAdapter.setSelectedItem(i);
                    return i;
                }
            }
        } else if (CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) && LGT_Limit.ISP_AUTOMODE_AUTO.equals(sceneMode)) {
            item = (ModeItem) this.mModeItemList.get(0);
            this.mTitleText.setText(item.getTitle());
            this.mDescText.setText(item.getDescription());
            this.mListAdapter.setSelectedItem(0);
            this.mGridAdapter.setSelectedItem(0);
            return 0;
        } else if (CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode)) {
            for (i = 0; i < this.mModeItemList.size(); i++) {
                item = (ModeItem) this.mModeItemList.get(i);
                if (sceneMode.equals(item.getValue())) {
                    this.mTitleText.setText(item.getTitle());
                    this.mDescText.setText(item.getDescription());
                    this.mListAdapter.setSelectedItem(i);
                    this.mGridAdapter.setSelectedItem(i);
                    return i;
                }
            }
        } else {
            for (i = 0; i < this.mModeItemList.size(); i++) {
                item = (ModeItem) this.mModeItemList.get(i);
                if (shotMode.equals(item.getValue())) {
                    this.mTitleText.setText(item.getTitle());
                    this.mDescText.setText(item.getDescription());
                    this.mListAdapter.setSelectedItem(i);
                    this.mGridAdapter.setSelectedItem(i);
                    return i;
                }
            }
        }
        return 0;
    }

    protected String getCurrentItemTitle() {
        if (this.mModeItemList != null) {
            String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
            String intelliAuto = this.mGet.getSettingValue(Setting.KEY_SMART_MODE);
            String sceneMode = this.mGet.getSettingValue(Setting.KEY_SCENE_MODE);
            int i;
            ModeItem item;
            if (CameraConstants.SMART_MODE_ON.equals(intelliAuto)) {
                for (i = 0; i < this.mModeItemList.size(); i++) {
                    item = (ModeItem) this.mModeItemList.get(i);
                    if (item != null && Setting.KEY_SMART_MODE.equals(item.getKey())) {
                        return item.getTitle();
                    }
                }
            } else if (CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) && LGT_Limit.ISP_AUTOMODE_AUTO.equals(sceneMode)) {
                item = (ModeItem) this.mModeItemList.get(0);
                if (item != null) {
                    return item.getTitle();
                }
            } else if (CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode)) {
                for (i = 0; i < this.mModeItemList.size(); i++) {
                    item = (ModeItem) this.mModeItemList.get(i);
                    if (item != null && sceneMode.equals(item.getValue())) {
                        return item.getTitle();
                    }
                }
            } else {
                for (i = 0; i < this.mModeItemList.size(); i++) {
                    item = (ModeItem) this.mModeItemList.get(i);
                    if (item != null && shotMode.equals(item.getValue())) {
                        return item.getTitle();
                    }
                }
            }
        }
        return this.mGet.getString(R.string.normal);
    }

    protected void setDefaultMode() {
        boolean needChange = false;
        hide();
        if (this.mViewMode == 0) {
            if (!this.mListAdapter.isSelectedItem(0)) {
                needChange = true;
            }
        } else if (!this.mGridAdapter.isSelectedItem(0)) {
            needChange = true;
        }
        if (needChange) {
            this.mGet.setSetting(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_NORMAL);
            Bundle bundle = new Bundle();
            bundle.putBoolean(CameraConstants.MODE_MENU_COMMAND, true);
            this.mGet.doCommand(Command.CAMERA_SHOT_MODE, null, bundle);
        }
        final String defaultString = this.mGet.getString(R.string.normal);
        this.mGet.postOnUiThread(new Runnable() {
            public void run() {
                CameraShotModeMenuController.this.mGet.removePostRunnable(this);
                CameraShotModeMenuController.this.mGet.updateModeMenuIndicator(defaultString);
            }
        });
    }
}
