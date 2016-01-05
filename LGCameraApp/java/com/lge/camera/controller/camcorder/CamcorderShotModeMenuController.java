package com.lge.camera.controller.camcorder;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.controller.ShotModeMenuController;
import com.lge.camera.controller.ShotModeMenuController.ModeItem;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;

public class CamcorderShotModeMenuController extends ShotModeMenuController {
    public CamcorderShotModeMenuController(ControllerFunction function) {
        super(function);
    }

    protected void makeItemList() {
        makeRecordModeItemList();
    }

    private void makeRecordModeItemList() {
        if (this.mModeItemList != null) {
            ListPreference listPref = this.mGet.getSettingListPreference(Setting.KEY_VIDEO_RECORD_MODE);
            if (listPref != null) {
                CharSequence[] entryValues = listPref.getEntryValues();
                String menuCommand = listPref.getEntryCommand();
                for (int i = 0; i < entryValues.length; i++) {
                    int[] itemRes = getRecordModeItemResources(String.valueOf(entryValues[i]));
                    String title = this.mGet.getString(itemRes[0]);
                    String desc = this.mGet.getString(itemRes[1]);
                    int imgResId = itemRes[2];
                    this.mModeItemList.add(new ModeItem(Setting.KEY_VIDEO_RECORD_MODE, String.valueOf(entryValues[i]), title, desc, imgResId, menuCommand));
                }
            }
        }
    }

    private int[] getRecordModeItemResources(String modeString) {
        int titleId = 0;
        int messageTextId = 0;
        int messageImageId = 0;
        if (CameraConstants.TYPE_RECORDMODE_NORMAL.equals(modeString)) {
            titleId = R.string.normal;
            messageTextId = R.string.mode_menu_video_normal;
            messageImageId = R.drawable.levellist_camcorder_mode_image_normal;
        } else if (CameraConstants.TYPE_RECORDMODE_WDR.equals(modeString)) {
            if (FunctionProperties.isHDRRecordingNameUsed()) {
                titleId = R.string.record_mode_HDR;
                messageImageId = R.drawable.levellist_camcorder_mode_image_hdr;
            } else {
                titleId = R.string.record_mode_WDR;
                messageImageId = R.drawable.levellist_camcorder_mode_image_wdr;
            }
            messageTextId = R.string.sp_wdr_recording_help_desc_list;
        } else if (CameraConstants.TYPE_RECORDMODE_DUAL.equals(modeString)) {
            titleId = R.string.dual_recording;
            messageTextId = R.string.sp_dual_recording_help_desc;
            messageImageId = R.drawable.levellist_camcorder_mode_image_dual;
        } else if (CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(modeString)) {
            titleId = R.string.sp_live_effect_NORMAL;
            messageTextId = R.string.sp_live_effect_help_desc;
            messageImageId = R.drawable.levellist_camcorder_mode_image_liveeffect;
        } else if (CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(modeString)) {
            titleId = R.string.tracking_zoom;
            messageTextId = R.string.mode_menu_video_tracking_zoom;
            messageImageId = R.drawable.levellist_camcorder_mode_image_smartzoom;
        }
        return new int[]{titleId, messageTextId, messageImageId};
    }

    protected int getCurrentItem() {
        String recordMode = this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE);
        if (this.mListAdapter == null || this.mTitleText == null || this.mDescText == null) {
            return 0;
        }
        if (CameraConstants.TYPE_RECORDMODE_NORMAL.equals(recordMode)) {
            ModeItem item = (ModeItem) this.mModeItemList.get(0);
            this.mTitleText.setText(item.getTitle());
            this.mDescText.setText(item.getDescription());
            this.mListAdapter.setSelectedItem(0);
            return 0;
        }
        for (int i = 0; i < this.mModeItemList.size(); i++) {
            if (recordMode.equals(((ModeItem) this.mModeItemList.get(i)).getValue())) {
                item = (ModeItem) this.mModeItemList.get(i);
                this.mTitleText.setText(item.getTitle());
                this.mDescText.setText(item.getDescription());
                this.mListAdapter.setSelectedItem(i);
                return i;
            }
        }
        return 0;
    }

    protected String getCurrentItemTitle() {
        if (this.mModeItemList != null) {
            String recordMode = this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE);
            ModeItem item;
            if (CameraConstants.TYPE_RECORDMODE_NORMAL.equals(recordMode)) {
                item = (ModeItem) this.mModeItemList.get(0);
                if (item != null) {
                    return item.getTitle();
                }
            }
            for (int i = 0; i < this.mModeItemList.size(); i++) {
                item = (ModeItem) this.mModeItemList.get(i);
                if (item != null && recordMode.equals(item.getValue())) {
                    return item.getTitle();
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
            this.mGet.setSetting(Setting.KEY_VIDEO_RECORD_MODE, CameraConstants.TYPE_RECORDMODE_NORMAL);
            Bundle bundle = new Bundle();
            bundle.putBoolean(CameraConstants.MODE_MENU_COMMAND, true);
            this.mGet.doCommand(Command.SET_VIDEO_MODE, null, bundle);
        }
        final String defaultString = this.mGet.getString(R.string.normal);
        this.mGet.postOnUiThread(new Runnable() {
            public void run() {
                CamcorderShotModeMenuController.this.mGet.removePostRunnable(this);
                CamcorderShotModeMenuController.this.mGet.updateModeMenuIndicator(defaultString);
            }
        });
    }
}
