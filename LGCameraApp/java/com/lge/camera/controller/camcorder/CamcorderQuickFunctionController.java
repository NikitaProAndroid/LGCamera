package com.lge.camera.controller.camcorder;

import com.lge.camera.ControllerFunction;
import com.lge.camera.command.Command;
import com.lge.camera.controller.QuickFunctionController;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.MmsProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.Util;

public class CamcorderQuickFunctionController extends QuickFunctionController {
    public CamcorderQuickFunctionController(ControllerFunction function) {
        super(function);
    }

    public void setMmsLimit(final boolean mmsVideo) {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                CamcorderQuickFunctionController.this.mGet.removePostRunnable(this);
                CamcorderQuickFunctionController.this.setMmsLimitUiQfl(mmsVideo);
            }
        });
    }

    public void setMmsLimitUiQfl(final boolean mmsVideo) {
        if (this.mInit) {
            ListPreference videoSizePref = this.mGet.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            if (videoSizePref != null) {
                CharSequence[] values = videoSizePref.getEntryValues();
                int videoSizePrefIndex = this.mGet.getQfIndex(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
                String selectedResolution = videoSizePref.getValue();
                int selectedIndex = videoSizePref.findIndexOfValue(selectedResolution);
                if (selectedIndex < 0) {
                    selectedIndex = 0;
                }
                if (mmsVideo && this.mGet.isMMSIntent()) {
                    boolean needChange = true;
                    for (CharSequence charSequence : values) {
                        String size = charSequence.toString();
                        if (MmsProperties.isAvailableMmsResolution(this.mGet.getContentResolver(), size) && selectedResolution.equalsIgnoreCase(size)) {
                            needChange = false;
                        }
                    }
                    if (needChange) {
                        this.mGet.setSetting(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, MmsProperties.getMmsResolutions(this.mGet.getContentResolver())[0]);
                    }
                }
                if (this.mGet.isQuickFunctionList(videoSizePrefIndex)) {
                    setMenuIcon(videoSizePrefIndex, selectedIndex);
                    return;
                }
                return;
            }
            return;
        }
        this.mGet.postOnUiThread(new Runnable() {
            public void run() {
                CamcorderQuickFunctionController.this.mGet.removePostRunnable(this);
                CamcorderQuickFunctionController.this.setMmsLimitUiQfl(mmsVideo);
            }
        }, 200);
    }

    public void restoreLiveEffectSubMenu() {
        if (this.mGet.getSubMenuMode() == 18 && this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE) != null) {
            this.mGet.setSetting(Setting.KEY_VIDEO_RECORD_MODE, CameraConstants.TYPE_RECORDMODE_NORMAL);
            this.mGet.doCommand(Command.SET_VIDEO_MODE);
            int menuIndex = this.mGet.getQfIndex(Setting.KEY_VIDEO_RECORD_MODE);
            if (this.mGet.isQuickFunctionList(menuIndex)) {
                this.mGet.setQuickFunctionControllerMenuIcon(menuIndex, this.mGet.getSettingIndex(Setting.KEY_VIDEO_RECORD_MODE));
            }
        }
    }

    public void setLimitationToLiveeffect(boolean beSet) {
        if (beSet) {
            this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_COLOREFFECT, true, false);
            this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_TAG_LOCATION, true, false);
            this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_WHITEBALANCE, true, false);
            if (this.mGet.getCameraId() == 0) {
                this.mGet.setPreferenceMenuEnable(Setting.KEY_ZOOM, true, false);
            }
            this.mGet.setPreferenceMenuEnable(Setting.KEY_BRIGHTNESS, true, false);
            this.mGet.setPreferenceMenuEnable(Setting.KEY_LIVE_EFFECT, true, false);
            this.mGet.setPreferenceMenuEnable(Setting.KEY_DUAL_RECORDING, true, false);
            this.mGet.setPreferenceMenuEnable(Setting.KEY_FLASH, true, false);
            this.mGet.setPreferenceMenuEnable(Setting.KEY_SWAP, true, false);
            int[] previewSizeOnDevice = Util.SizeString2WidthHeight(this.mGet.getPreviewSizeOnDevice());
            if (!(this.mGet.isMMSRecording() || previewSizeOnDevice[0] < 1280 || "1920x1080@60".equals(this.mGet.getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE)) || "3840x2160".equals(this.mGet.getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE)))) {
                this.mGet.setPreferenceMenuEnable(Setting.KEY_VIDEO_STABILIZATION, true, false);
            }
            this.mGet.setForced_audiozoom(true);
            this.mGet.recordingControllerShow();
        } else {
            this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_COLOREFFECT, false, false);
            this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_TAG_LOCATION, false, false);
            this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_WHITEBALANCE, false, false);
            this.mGet.setPreferenceMenuEnable(Setting.KEY_ZOOM, false, false);
            this.mGet.setPreferenceMenuEnable(Setting.KEY_BRIGHTNESS, false, false);
            this.mGet.setPreferenceMenuEnable(Setting.KEY_VIDEO_STABILIZATION, false, false);
            this.mGet.setForced_audiozoom(false);
            if (this.mGet.isDualRecordingActive() || this.mGet.isSmartZoomRecordingActive()) {
                this.mGet.setPreferenceMenuEnable(Setting.KEY_DUAL_RECORDING, true, false);
                this.mGet.setForced_audiozoom(false);
                this.mGet.setPreferenceMenuEnable(Setting.KEY_LIVE_EFFECT, false, false);
            } else {
                this.mGet.setPreferenceMenuEnable(Setting.KEY_LIVE_EFFECT, true, false);
                this.mGet.setPreferenceMenuEnable(Setting.KEY_FLASH, true, false);
                this.mGet.setPreferenceMenuEnable(Setting.KEY_DUAL_RECORDING, false, false);
            }
            this.mGet.recordingControllerShow();
        }
        if (this.mGet.isLiveEffectDrawerOpened()) {
            this.mGet.setQuickFunctionAllMenuEnabled(false, true);
        }
    }
}
