package com.lge.camera.controller;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.MmsProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.CameraFrontSettingMenu;
import com.lge.camera.setting.CameraMain3dSettingMenu;
import com.lge.camera.setting.CameraMainSettingMenu;
import com.lge.camera.setting.CameraSetting;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.PreferenceGroup;
import com.lge.camera.setting.Setting;
import com.lge.camera.setting.Setting.SettingFunction;
import com.lge.camera.setting.SettingMenu;
import com.lge.camera.setting.SettingMenu.SettingMenuFunction;
import com.lge.camera.setting.SettingMenuItem;
import com.lge.camera.setting.VideoFrontSettingMenu;
import com.lge.camera.setting.VideoMain3dSettingMenu;
import com.lge.camera.setting.VideoMainSettingMenu;
import com.lge.camera.setting.VideoSetting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;

public abstract class SettingController extends Controller implements SettingFunction, SettingMenuFunction {
    private static boolean needToChange;
    private static String needToChangeString;
    private ArrayList<BackUpSettingMenuItem> backup3DCameraMainMenu;
    private ArrayList<BackUpSettingMenuItem> backup3DVideoMainMenu;
    private ArrayList<BackUpSettingMenuItem> backupCameraFrontMenu;
    private ArrayList<BackUpSettingMenuItem> backupCameraMainMenu;
    private ArrayList<BackUpSettingMenuItem> backupMenu;
    private ArrayList<BackUpSettingMenuItem> backupVideoFrontMenu;
    private ArrayList<BackUpSettingMenuItem> backupVideoMainMenu;
    private int m3DCameraMainOriginalSize;
    private int m3DVideoMainOriginalSize;
    protected VideoSetting mCamcorderFrontSetting;
    protected VideoSetting mCamcorderMain3dSetting;
    protected VideoSetting mCamcorderMainSetting;
    private int mCameraFrontOriginalSize;
    protected CameraSetting mCameraFrontSetting;
    protected CameraFrontSettingMenu mCameraFrontSettingMenu;
    protected CameraSetting mCameraMain3dSetting;
    protected CameraMain3dSettingMenu mCameraMain3dSettingMenu;
    private int mCameraMainOriginalSize;
    protected CameraSetting mCameraMainSetting;
    protected CameraMainSettingMenu mCameraMainSettingMenu;
    private int mOriginalSize;
    protected Setting mSetting;
    protected boolean mSettingInit;
    protected SettingMenu mSettingMenu;
    private int mVideoFrontOriginalSize;
    protected VideoFrontSettingMenu mVideoFrontSettingMenu;
    protected VideoMain3dSettingMenu mVideoMain3dSettingMenu;
    private int mVideoMainOriginalSize;
    protected VideoMainSettingMenu mVideoMainSettingMenu;

    private class BackUpSettingMenuItem {
        private SettingMenuItem settingMenuItem;
        private int settingMenuItemIndex;

        private BackUpSettingMenuItem() {
            this.settingMenuItemIndex = 0;
            this.settingMenuItem = null;
        }

        public void setSettingMenuItem(SettingMenuItem item) {
            this.settingMenuItem = item;
        }

        public void setIndex(int index) {
            this.settingMenuItemIndex = index;
        }
    }

    public abstract void displaySettingView();

    public abstract boolean isNullSettingView();

    public abstract boolean isSettingViewRemoving();

    public abstract boolean isVisible();

    public abstract void removeChildSettingView(boolean z);

    public abstract void removeSettingView();

    public abstract void removeSettingViewAll();

    public SettingController(ControllerFunction function) {
        super(function);
        this.backupCameraMainMenu = new ArrayList();
        this.backupCameraFrontMenu = new ArrayList();
        this.backup3DCameraMainMenu = new ArrayList();
        this.backupVideoMainMenu = new ArrayList();
        this.backupVideoFrontMenu = new ArrayList();
        this.backup3DVideoMainMenu = new ArrayList();
        this.mSettingInit = false;
        this.mOriginalSize = 0;
        this.mCameraMainOriginalSize = 0;
        this.mCameraFrontOriginalSize = 0;
        this.m3DCameraMainOriginalSize = 0;
        this.mVideoMainOriginalSize = 0;
        this.mVideoFrontOriginalSize = 0;
        this.m3DVideoMainOriginalSize = 0;
    }

    public void initController() {
        PreferenceGroup backPreferenceGroup = this.mGet.getBackPreferenceGroup();
        PreferenceGroup frontPreferenceGroup = this.mGet.getFrontPreferenceGroup();
        if (this.mGet.getApplicationMode() == 0) {
            this.mCameraMainSetting = new CameraSetting(this, this.mGet.getApplicationContext(), Setting.SETTING_PRIMARY, backPreferenceGroup);
            this.mCameraFrontSetting = new CameraSetting(this, this.mGet.getApplicationContext(), Setting.SETTING_SECONDARY, frontPreferenceGroup);
            this.mCameraMainSettingMenu = new CameraMainSettingMenu(this, this.mGet.getApplicationContext(), this.mCameraMainSetting);
            this.mCameraFrontSettingMenu = new CameraFrontSettingMenu(this, this.mGet.getApplicationContext(), this.mCameraFrontSetting);
            this.mCameraMainOriginalSize = this.mCameraMainSettingMenu.getMenuList().size();
            this.mCameraFrontOriginalSize = this.mCameraFrontSettingMenu.getMenuList().size();
        } else {
            this.mCamcorderMainSetting = new VideoSetting(this, this.mGet.getApplicationContext(), Setting.SETTING_PRIMARY, backPreferenceGroup);
            this.mCamcorderFrontSetting = new VideoSetting(this, this.mGet.getApplicationContext(), Setting.SETTING_SECONDARY, frontPreferenceGroup);
            this.mVideoMainSettingMenu = new VideoMainSettingMenu(this, this.mGet.getApplicationContext(), this.mCamcorderMainSetting);
            this.mVideoFrontSettingMenu = new VideoFrontSettingMenu(this, this.mGet.getApplicationContext(), this.mCamcorderFrontSetting);
            this.mVideoMainOriginalSize = this.mVideoMainSettingMenu.getMenuList().size();
            this.mVideoFrontOriginalSize = this.mVideoFrontSettingMenu.getMenuList().size();
        }
        if (ModelProperties.is3dSupportedModel()) {
            PreferenceGroup back3DPreferenceGroup = this.mGet.getBack3dPreferenceGroup();
            if (this.mGet.getApplicationMode() == 0) {
                this.mCameraMain3dSetting = new CameraSetting(this, this.mGet.getApplicationContext(), Setting.SETTING_3D_PRIMARY, back3DPreferenceGroup);
                this.mCameraMain3dSettingMenu = new CameraMain3dSettingMenu(this, this.mGet.getApplicationContext(), this.mCameraMain3dSetting);
                this.m3DCameraMainOriginalSize = this.mCameraMain3dSettingMenu.getMenuList().size();
            } else {
                this.mCamcorderMain3dSetting = new VideoSetting(this, this.mGet.getApplicationContext(), Setting.SETTING_3D_PRIMARY, back3DPreferenceGroup);
                this.mVideoMain3dSettingMenu = new VideoMain3dSettingMenu(this, this.mGet.getApplicationContext(), this.mCamcorderMain3dSetting);
                this.m3DVideoMainOriginalSize = this.mVideoMain3dSettingMenu.getMenuList().size();
            }
        }
        applyCameraChange();
        this.mSettingInit = true;
    }

    public SettingMenu getCurrentSettingMenu() {
        return this.mSettingMenu;
    }

    public void restoreSettingItem() {
        if (this.backupMenu != null) {
            for (int i = this.backupMenu.size() - 1; i >= 0; i--) {
                BackUpSettingMenuItem backupMenuItem = (BackUpSettingMenuItem) this.backupMenu.get(i);
                ListPreference listPref = this.mSetting.getSettingListPreference(backupMenuItem.settingMenuItem.getKey());
                if (listPref != null) {
                    String settingValue = listPref.getValue();
                    if (settingValue != null) {
                        backupMenuItem.settingMenuItem.selectedChildPosition = listPref.findIndexOfValue(settingValue);
                    }
                }
                this.mSettingMenu.insertMenuItem(backupMenuItem.settingMenuItemIndex, backupMenuItem.settingMenuItem);
            }
            this.backupMenu.clear();
        }
    }

    public void removeSettingItem() {
        if (this.mSettingMenu != null) {
            int i = 0;
            while (i < this.mSettingMenu.getMenuList().size()) {
                SettingMenuItem menuItem = this.mSettingMenu.getMenu(i);
                if (this.mGet.getQfIndex(menuItem.getKey()) != -1) {
                    BackUpSettingMenuItem backupItem = new BackUpSettingMenuItem();
                    backupItem.setIndex(i);
                    backupItem.setSettingMenuItem(menuItem);
                    this.backupMenu.add(backupItem);
                    this.mSettingMenu.removeMenuItem(i);
                    i = 0;
                }
                i++;
            }
        }
    }

    public void restoreQFLItemEnableValue() {
        if (this.mSettingMenu != null) {
            int menuListSize = this.mSettingMenu.getMenuList().size();
            for (int i = 0; i < menuListSize; i++) {
                int index = this.mGet.getQfIndex(this.mSettingMenu.getMenu(i).getKey());
                if (index != -1) {
                    this.mSettingMenu.getMenu(i).enable = this.mGet.getQFLMenuEnable()[index];
                }
            }
        }
    }

    public void initSettingMenu() {
        if (this.mSettingMenu != null && this.mOriginalSize != this.mSettingMenu.getMenuList().size()) {
            restoreSettingItem();
            restoreQFLItemEnableValue();
        }
    }

    public void applyCameraChange() {
        if (this.mGet.getApplicationMode() == 0) {
            if (this.mGet.getCameraMode() != 0) {
                this.mSetting = this.mCameraFrontSetting;
                this.mSettingMenu = this.mCameraFrontSettingMenu;
                this.mOriginalSize = this.mCameraFrontOriginalSize;
                this.backupMenu = this.backupCameraFrontMenu;
            } else if (this.mGet.getCameraDimension() == 0) {
                this.mSetting = this.mCameraMainSetting;
                this.mSettingMenu = this.mCameraMainSettingMenu;
                this.mOriginalSize = this.mCameraMainOriginalSize;
                this.backupMenu = this.backupCameraMainMenu;
            } else {
                this.mSetting = this.mCameraMain3dSetting;
                this.mSettingMenu = this.mCameraMain3dSettingMenu;
                this.mOriginalSize = this.m3DCameraMainOriginalSize;
                this.backupMenu = this.backup3DCameraMainMenu;
            }
        } else if (this.mGet.getCameraMode() != 0) {
            this.mSetting = this.mCamcorderFrontSetting;
            this.mSettingMenu = this.mVideoFrontSettingMenu;
            this.mOriginalSize = this.mVideoFrontOriginalSize;
            this.backupMenu = this.backupVideoFrontMenu;
        } else if (this.mGet.getCameraDimension() == 0) {
            this.mSetting = this.mCamcorderMainSetting;
            this.mSettingMenu = this.mVideoMainSettingMenu;
            this.mOriginalSize = this.mVideoMainOriginalSize;
            this.backupMenu = this.backupVideoMainMenu;
        } else {
            this.mSetting = this.mCamcorderMain3dSetting;
            this.mSettingMenu = this.mVideoMain3dSettingMenu;
            this.mOriginalSize = this.m3DVideoMainOriginalSize;
            this.backupMenu = this.backup3DVideoMainMenu;
        }
    }

    public Setting getSetting() {
        return this.mSetting;
    }

    public String getSettingValue(String key) {
        return this.mSetting.getSettingValue(key);
    }

    public int getSetting(String key) {
        return this.mSetting.getSettingIndex(key);
    }

    public int getSetting(int settingIndex) {
        return this.mSetting.getSetting(settingIndex);
    }

    public boolean setSetting(int settingIndex, int settingValue) {
        if (settingIndex == this.mSetting.getSettingIndex(Setting.KEY_CAMERA_AUTO_REVIEW) || settingIndex == this.mSetting.getSettingIndex(Setting.KEY_CAMERA_TAG_LOCATION) || settingIndex == this.mSetting.getSettingIndex(Setting.KEY_VIDEO_AUDIO_RECORDING) || settingIndex == this.mSetting.getSettingIndex(Setting.KEY_VIDEO_AUTO_REVIEW) || settingIndex == this.mSetting.getSettingIndex(Setting.KEY_STORAGE) || settingIndex == this.mSetting.getSettingIndex(Setting.KEY_SHUTTER_SOUND) || settingIndex == this.mSetting.getSettingIndex(Setting.KEY_VOICESHUTTER) || settingIndex == this.mSetting.getSettingIndex(Setting.KEY_UPLUS_BOX) || settingIndex == this.mSetting.getSettingIndex(Setting.KEY_VIDEO_STABILIZATION) || settingIndex == this.mSetting.getSettingIndex(Setting.KEY_VOLUME) || settingIndex == this.mSetting.getSettingIndex(Setting.KEY_BEAUTYSHOT)) {
            return setSettingAllPreferences(settingIndex, settingValue);
        }
        if (ProjectVariables.isSupportManualAntibanding() && settingIndex == this.mSetting.getSettingIndex(Setting.KEY_CAMERA_ANTI_BANDING)) {
            return setSettingAllPreferences(settingIndex, settingValue);
        }
        if (settingIndex == this.mSetting.getSettingIndex(Setting.KEY_RESTORE) || settingIndex == this.mSetting.getSettingIndex(Setting.KEY_HELP_GUIDE)) {
            return setSettingAllPreferences(settingIndex, settingValue);
        }
        return this.mSetting.setSetting(settingIndex, settingValue);
    }

    public boolean setSetting(String key, String value) {
        return setSetting(key, value, true);
    }

    public boolean setSetting(String key, String value, boolean needSave) {
        if (Setting.KEY_CAMERA_AUTO_REVIEW.equals(key) || Setting.KEY_CAMERA_TAG_LOCATION.equals(key) || Setting.KEY_VIDEO_AUDIO_RECORDING.equals(key) || Setting.KEY_VIDEO_AUTO_REVIEW.equals(key) || Setting.KEY_STORAGE.equals(key) || Setting.KEY_SHUTTER_SOUND.equals(key) || Setting.KEY_VOICESHUTTER.equals(key) || Setting.KEY_UPLUS_BOX.equals(key) || Setting.KEY_VIDEO_STABILIZATION.equals(key) || Setting.KEY_VOLUME.equals(key) || Setting.KEY_BEAUTYSHOT.equals(key)) {
            return setSettingAllPreferences(key, value);
        }
        if (ProjectVariables.isSupportManualAntibanding() && Setting.KEY_CAMERA_ANTI_BANDING.equals(key)) {
            return setSettingAllPreferences(key, value);
        }
        if (Setting.KEY_RESTORE.equals(key) || Setting.KEY_HELP_GUIDE.equals(key)) {
            return setSettingAllPreferences(key, value);
        }
        return this.mSetting.setSetting(key, value, needSave);
    }

    protected boolean setSettingAllPreferences(String key, String value) {
        boolean mainSetting;
        boolean frontSetting;
        if (this.mGet.getApplicationMode() == 0) {
            mainSetting = this.mCameraMainSetting.setSetting(key, value);
            frontSetting = this.mCameraFrontSetting.setSetting(key, value);
        } else {
            mainSetting = this.mCamcorderMainSetting.setSetting(key, value);
            frontSetting = this.mCamcorderFrontSetting.setSetting(key, value);
        }
        return mainSetting && frontSetting;
    }

    public void setAttatchModeDefaultVideoSize(String value) {
        this.mCamcorderMainSetting.setSetting(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, value);
        this.mCamcorderFrontSetting.setSetting(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, value);
    }

    protected boolean setSettingAllPreferences(int index, int value) {
        boolean mainSetting;
        boolean frontSetting;
        if (this.mGet.getApplicationMode() == 0) {
            mainSetting = this.mCameraMainSetting.setSetting(index, value);
            frontSetting = this.mCameraFrontSetting.setSetting(index, value);
        } else {
            mainSetting = this.mCamcorderMainSetting.setSetting(index, value);
            frontSetting = this.mCamcorderFrontSetting.setSetting(index, value);
        }
        return mainSetting && frontSetting;
    }

    public void setMmsLimit(boolean mmsVideo) {
        ListPreference videoSizePref = this.mGet.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
        if (videoSizePref == null) {
            CamLog.d(FaceDetector.TAG, "videoSizePref is null");
            return;
        }
        CharSequence[] values = videoSizePref.getEntryValues();
        int videoSizePrefIndex = this.mGet.getQfIndex(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
        String selectedResolution = videoSizePref.getValue();
        if (MmsProperties.isAvailableMmsResolution(this.mGet.getContentResolver(), this.mGet.getPreviewSizeOnDevice()) || "320x240".equalsIgnoreCase(selectedResolution) || "176x144".equalsIgnoreCase(selectedResolution) || this.mGet.checkSlowMotionMode()) {
            this.mGet.setForced_audiozoom(false);
        } else {
            this.mGet.setForced_audiozoom(true);
        }
        int i;
        int len;
        if (mmsVideo && isMMSIntent()) {
            boolean needChange = true;
            for (CharSequence charSequence : values) {
                String size = charSequence.toString();
                boolean available = MmsProperties.isAvailableMmsResolution(this.mGet.getContentResolver(), size);
                if (available && selectedResolution.equalsIgnoreCase(size)) {
                    needChange = false;
                }
                this.mGet.setCurrentSettingMenuEnable(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, size, available);
            }
            if (needChange) {
                selectedResolution = MmsProperties.getMmsResolutions(this.mGet.getContentResolver())[0];
            }
        } else if (this.mGet.isEffectsCamcorderActive() || this.mGet.isEffectsCameraActive()) {
            CamLog.w(FaceDetector.TAG, "skip! resolution is already setted by effectActive ");
            return;
        } else if (!this.mGet.isQuickFunctionList(videoSizePrefIndex)) {
            len = values.length;
            for (i = 0; i < len; i++) {
                setAllChildMenuEnabled(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, true);
            }
        }
        if (isAttachIntent()) {
            this.mGet.setPreferenceMenuEnable(Setting.KEY_VIDEO_AUTO_REVIEW, false, false);
        } else {
            this.mGet.setPreferenceMenuEnable(Setting.KEY_VIDEO_AUTO_REVIEW, true, false);
        }
        this.mGet.setSelectedChild(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, selectedResolution, true);
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                SettingController.this.mGet.removePostRunnable(this);
                SettingController.this.mGet.updateSizeIndicator();
            }
        });
    }

    public String setLiveeffectLimit() {
        if (!this.mGet.isEffectsCamcorderActive()) {
            return null;
        }
        ListPreference videoSizePref = this.mGet.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
        if (videoSizePref == null) {
            CamLog.d(FaceDetector.TAG, "videoSizePref is null");
            return null;
        }
        String size;
        CamLog.v(FaceDetector.TAG, "setLiveeffectLimit");
        CharSequence[] values = videoSizePref.getEntryValues();
        needToChangeString = videoSizePref.getValue();
        needToChange = true;
        if (this.mGet.isLiveEffectActive()) {
            for (CharSequence charSequence : values) {
                size = charSequence.toString();
                if (MultimediaProperties.isAvailableLiveeffectResolution(size, this.mGet.getCameraMode()) && needToChangeString.equalsIgnoreCase(size)) {
                    needToChange = false;
                }
            }
        } else if (this.mGet.isDualRecordingActive() || this.mGet.isSmartZoomRecordingActive()) {
            needToChange = true;
        }
        CamLog.w(FaceDetector.TAG, "needToChange = " + needToChange);
        if (needToChange) {
            if (this.mGet.isDualRecordingActive()) {
                if (MultimediaProperties.DUALREC_DEFAULT_VIDEO_SIZE == null) {
                    needToChangeString = MultimediaProperties.getDualRecordingResolution(needToChangeString);
                    CamLog.v(FaceDetector.TAG, "DualRec Video Size = Current Video Size = " + needToChangeString);
                } else if (this.mGet.getPreviousResolution() == null || this.mGet.getPreviousEffectType() == 0 || this.mGet.getPreviousEffectType() == 1) {
                    needToChangeString = MultimediaProperties.getDualRecordingResolution(MultimediaProperties.DUALREC_DEFAULT_VIDEO_SIZE);
                    CamLog.v(FaceDetector.TAG, "DualRec Video Size = Default dualrec video size = " + needToChangeString);
                } else {
                    needToChangeString = MultimediaProperties.getDualRecordingResolution(needToChangeString);
                    CamLog.v(FaceDetector.TAG, "DualRec Video Size = Selected video size = " + needToChangeString);
                }
                CamLog.d(FaceDetector.TAG, "Video size is set for Dual Recording : " + needToChangeString);
            } else if (this.mGet.isSmartZoomRecordingActive()) {
                if (MultimediaProperties.SMARTZOOM_DEFAULT_VIDEO_SIZE == null) {
                    needToChangeString = MultimediaProperties.getSmartZoomResolution(needToChangeString);
                    CamLog.v(FaceDetector.TAG, "SmartZoom Video Size = Current Video Size = " + needToChangeString);
                } else if (this.mGet.getPreviousResolution() == null || this.mGet.getPreviousEffectType() == 0 || this.mGet.getPreviousEffectType() == 1) {
                    needToChangeString = MultimediaProperties.getSmartZoomResolution(MultimediaProperties.SMARTZOOM_DEFAULT_VIDEO_SIZE);
                    CamLog.v(FaceDetector.TAG, "SmartZoom Video Size = Default SmartZoom video size = " + needToChangeString);
                } else {
                    needToChangeString = MultimediaProperties.getSmartZoomResolution(needToChangeString);
                    CamLog.v(FaceDetector.TAG, "SmartZoom Video Size = Selected video size = " + needToChangeString);
                }
                CamLog.d(FaceDetector.TAG, "Video size is set for SmartZoom Recording : " + needToChangeString);
            } else {
                needToChangeString = MultimediaProperties.getLiveeffectResolutions(this.mGet.getCameraMode());
                CamLog.d(FaceDetector.TAG, "Video size is set for Live Effect : " + needToChangeString);
            }
        }
        if (this.mGet.isDualRecordingActive() || this.mGet.isSmartZoomRecordingActive()) {
            String[] videoSizeOnMenu;
            if (this.mGet.isDualRecordingActive()) {
                videoSizeOnMenu = MultimediaProperties.DUALREC_VIDEO_SIZE_LISTED_ON_MENU.split(",");
            } else {
                videoSizeOnMenu = MultimediaProperties.SMARTZOOM_VIDEO_SIZE_LISTED_ON_MENU.split(",");
            }
            for (CharSequence charSequence2 : values) {
                this.mGet.setCurrentSettingMenuEnable(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, charSequence2.toString(), false);
            }
            for (String videoSize : videoSizeOnMenu) {
                String videoSize2;
                if ("1920x1080".equals(videoSize2)) {
                    videoSize2 = "1920x1088";
                }
                for (CharSequence charSequence22 : values) {
                    size = charSequence22.toString();
                    if (videoSize2.equalsIgnoreCase(size)) {
                        this.mGet.setCurrentSettingMenuEnable(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, size, true);
                    }
                }
            }
        } else {
            for (CharSequence charSequence222 : values) {
                size = charSequence222.toString();
                if (needToChangeString.equalsIgnoreCase(size)) {
                    this.mGet.setCurrentSettingMenuEnable(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, size, true);
                } else {
                    this.mGet.setCurrentSettingMenuEnable(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, size, false);
                }
            }
        }
        this.mGet.setSelectedChild(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, needToChangeString, true);
        this.mGet.setPreferenceMenuEnable(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, true, true);
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                SettingController.this.mGet.removePostRunnable(this);
                CamLog.v(FaceDetector.TAG, "setLiveeffectLimit updateSizeIndicator");
                SettingController.this.mGet.updateSizeIndicator();
                SettingController.this.mGet.updateGpsIndicator();
                SettingController.this.mGet.updateStabilizationIndicator();
                SettingController.this.mGet.setLimitationToLiveeffect(false);
            }
        });
        return needToChangeString;
    }

    public void setSettingForDualCamera(boolean value) {
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SWAP, value);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, value);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_FLASH, value);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_ZOOM, value);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_FOCUS, value);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_PICTURESIZE, value);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SMART_MODE, value);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_TIME_MACHINE, value);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_BRIGHTNESS, value);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SCENE_MODE, value);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SAVE_DIRECTION, value);
        this.mGet.setMenuEnableForSceneMode(7);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TIMER, value);
        if (ProjectVariables.isSupportedAutoReview()) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, value);
        }
        if (!value) {
            this.mGet.setSetting(Setting.KEY_VOICESHUTTER, CameraConstants.SMART_MODE_OFF);
            this.mGet.resetBrightnessController();
            ListPreference pref = this.mGet.findPreference(Setting.KEY_FOCUS);
            if (pref != null) {
                this.mGet.setSetting(Setting.KEY_FOCUS, pref.getDefaultValue());
            }
            this.mGet.setSetting(Setting.KEY_SCENE_MODE, this.mGet.findPreference(Setting.KEY_SCENE_MODE).getDefaultValue());
            if (this.mGet.getCameraMode() == 0) {
                this.mGet.setSetting(Setting.KEY_ISO, this.mGet.findPreference(Setting.KEY_ISO).getDefaultValue());
            }
            this.mGet.setSetting(Setting.KEY_CAMERA_WHITEBALANCE, this.mGet.findPreference(Setting.KEY_CAMERA_WHITEBALANCE).getDefaultValue());
            this.mGet.setSetting(Setting.KEY_CAMERA_COLOREFFECT, this.mGet.findPreference(Setting.KEY_CAMERA_COLOREFFECT).getDefaultValue());
            this.mGet.setSetting(Setting.KEY_CAMERA_TIMER, this.mGet.findPreference(Setting.KEY_CAMERA_TIMER).getDefaultValue());
            if (ProjectVariables.isSupportedAutoReview()) {
                this.mGet.setSetting(Setting.KEY_CAMERA_AUTO_REVIEW, this.mGet.findPreference(Setting.KEY_CAMERA_AUTO_REVIEW).getDefaultValue());
            }
        }
    }

    public void setTimeMachineLimit(boolean set) {
        if (FunctionProperties.isTimeMachineShotSizeLimit()) {
            ListPreference pictureSizePref = this.mGet.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
            ListPreference pref = this.mGet.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
            if (pictureSizePref == null || pref == null) {
                CamLog.d(FaceDetector.TAG, "pictureSizePref or pref is null");
                return;
            }
            CamLog.v(FaceDetector.TAG, "setTimeMachineLimit");
            CharSequence[] values = pictureSizePref.getEntryValues();
            boolean limitSize = false;
            String mSelectedSize = pref.getValue();
            for (CharSequence charSequence : values) {
                String size = charSequence.toString();
                boolean available = true;
                if (!FunctionProperties.isTimeMachineShotSizeAvailable(size) && set) {
                    limitSize = true;
                    available = false;
                }
                this.mGet.setCurrentSettingMenuEnable(Setting.KEY_CAMERA_PICTURESIZE, size, available);
                if (available && limitSize) {
                    this.mGet.setSelectedChild(Setting.KEY_CAMERA_PICTURESIZE, size, available);
                    break;
                }
            }
            if (FunctionProperties.isTimeMachineShotSizeAvailable(mSelectedSize)) {
                this.mGet.setSelectedChild(Setting.KEY_CAMERA_PICTURESIZE, mSelectedSize, true);
            }
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    CamLog.v(FaceDetector.TAG, "setTimeMachineLimit updateSizeIndicator");
                    SettingController.this.mGet.removePostRunnable(this);
                    SettingController.this.mGet.updateSizeIndicator();
                }
            });
        }
    }

    public CameraMainSettingMenu getCameraMainSettingMenu() {
        return this.mCameraMainSettingMenu;
    }

    public CameraFrontSettingMenu getCameraFrontSettingMenu() {
        return this.mCameraFrontSettingMenu;
    }

    public VideoMainSettingMenu getVideoMainSettingMenu() {
        return this.mVideoMainSettingMenu;
    }

    public VideoFrontSettingMenu getVideoFrontSettingMenu() {
        return this.mVideoFrontSettingMenu;
    }

    public CameraMain3dSettingMenu getCameraMain3dSettingMenu() {
        return this.mCameraMain3dSettingMenu;
    }

    public VideoMain3dSettingMenu getVideoMain3dSettingMenu() {
        return this.mVideoMain3dSettingMenu;
    }

    public String getString(int resId) {
        return this.mGet.getString(resId);
    }

    public boolean isAttachIntent() {
        return this.mGet.isAttachIntent();
    }

    public boolean isMMSIntent() {
        return this.mGet.isMMSIntent();
    }

    public void addMMSTexture(PreferenceGroup pg) {
        this.mGet.addMMSTexture(pg);
    }

    public void setAllChildMenuEnabled(String key, boolean enable) {
        for (CharSequence charSequence : this.mGet.findPreference(key).getEntryValues()) {
            this.mGet.setCurrentSettingMenuEnable(key, charSequence.toString(), enable);
        }
    }

    public void runOnUiThread(Runnable action) {
        this.mGet.runOnUiThread(action);
    }

    public void removePostRunnable(Runnable action) {
        this.mGet.removePostRunnable(action);
    }

    public ContentResolver getContentResolver() {
        return this.mGet.getContentResolver();
    }

    public void setAllPreferenceApply(int which, String key, String value) {
        if ((which & 1) != 0) {
            editPrefValue(this.mGet.getActivity().getSharedPreferences(CameraConstants.PREF_NAME_MAIN_CAMERA, 0), key, value);
            if (this.mCameraMainSetting != null) {
                this.mCameraMainSetting.setSetting(key, value);
            }
        }
        if ((which & 2) != 0) {
            editPrefValue(this.mGet.getActivity().getSharedPreferences(CameraConstants.PREF_NAME_MAIN_CAMCORDER, 0), key, value);
            if (this.mCamcorderMainSetting != null) {
                this.mCamcorderMainSetting.setSetting(key, value);
            }
        }
        if (ModelProperties.isSupportFrontCameraModel() && (which & 4) != 0) {
            editPrefValue(this.mGet.getActivity().getSharedPreferences(CameraConstants.PREF_NAME_FRONT_CAMERA, 0), key, value);
            if (this.mCameraFrontSetting != null) {
                this.mCameraFrontSetting.setSetting(key, value);
            }
        }
        if (ModelProperties.isSupportFrontCameraModel() && (which & 8) != 0) {
            editPrefValue(this.mGet.getActivity().getSharedPreferences(CameraConstants.PREF_NAME_FRONT_CAMCORDER, 0), key, value);
            if (this.mCamcorderFrontSetting != null) {
                this.mCamcorderFrontSetting.setSetting(key, value);
            }
        }
    }

    private void editPrefValue(SharedPreferences pref, String key, String value) {
        if (pref != null) {
            Editor editor = pref.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    public void startRotation(int degree) {
    }

    public void rotateSettingZoom(int degree) {
    }

    public void rotateSettingBrightness(int degree) {
    }

    public void rotateSettingBeautyShot(int degree) {
    }

    public void rotateSettingCamera3dDepth(int degree) {
    }

    public boolean checkAndCloseChildView() {
        return false;
    }
}
