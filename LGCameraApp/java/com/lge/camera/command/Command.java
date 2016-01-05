package com.lge.camera.command;

import android.os.SystemClock;
import com.lge.camera.ControllerFunction;
import com.lge.camera.util.Common;

public abstract class Command implements Runnable {
    public static final String APPLY_ALL_SETTINGS = "com.lge.camera.command.setting.ApplyAllSettings";
    public static final String BEAUTY_SHOT = "com.lge.camera.command.setting.SetOlaBeautyShot";
    public static final String CAMERA_ANTI_BANDING = "com.lge.camera.command.setting.SetCameraAntibanding";
    public static final String CAMERA_AUTO_REVIEW = "com.lge.camera.command.setting.SetCameraShowCapturedImage";
    public static final String CAMERA_COLOR_EFFECT = "com.lge.camera.command.setting.SetCameraColorEffect";
    public static final String CAMERA_FLASH_MODE = "com.lge.camera.command.setting.SetFlashMode";
    public static final String CAMERA_FOCUS_MODE = "com.lge.camera.command.setting.SetCameraFocusMode";
    public static final String CAMERA_GEO_TAG = "com.lge.camera.command.setting.SetCameraGeoTag";
    public static final String CAMERA_IA_FLASH_MODE = "com.lge.camera.command.setting.SetSmartCameraFlashMode";
    public static final String CAMERA_IMAGE_SIZE = "com.lge.camera.command.setting.SetCameraImageSize";
    public static final String CAMERA_ISO = "com.lge.camera.command.setting.SetCameraIso";
    public static final String CAMERA_SCENE_MODE = "com.lge.camera.command.setting.SetSceneMode";
    public static final String CAMERA_SHOT_MODE = "com.lge.camera.command.setting.SetCameraShotMode";
    public static final String CAMERA_SHUTTER_SOUND = "com.lge.camera.command.setting.SetCameraShutterSound";
    public static final String CAMERA_TIMER = "com.lge.camera.command.setting.SetCameraTimer";
    public static final String CAMERA_WHITE_BALANCE = "com.lge.camera.command.setting.SetCameraWhiteBalance";
    public static final String CHANGE_TO_CAMCORDER = "com.lge.camera.command.ChangeToCamcorder";
    public static final String CHANGE_TO_CAMERA = "com.lge.camera.command.ChangeToCamera";
    public static final String CLEAR_SCREEN = "com.lge.camera.command.ClearScreen";
    public static final String CLEAR_SHOT = "com.lge.camera.command.setting.SetClearShot";
    public static final String CLICK_QUICK_FUNCTION_BUTTON1 = "com.lge.camera.command.ClickQuickFunctionButton1";
    public static final String CLICK_QUICK_FUNCTION_BUTTON2 = "com.lge.camera.command.ClickQuickFunctionButton2";
    public static final String CLICK_QUICK_FUNCTION_BUTTON3 = "com.lge.camera.command.ClickQuickFunctionButton3";
    public static final String CLICK_QUICK_FUNCTION_BUTTON4 = "com.lge.camera.command.ClickQuickFunctionButton4";
    public static final String CLICK_QUICK_FUNCTION_BUTTON5 = "com.lge.camera.command.ClickQuickFunctionButton5";
    public static final String CONTINUOUS_SHOT = "com.lge.camera.command.setting.SetOlaContinuousShot";
    public static final String DELETE_PROGRESS_DIALOG = "com.lge.camera.command.DeleteProgressDialog";
    public static final String DISPLAY_CAMCORDER_POSTVIEW = "com.lge.camera.command.DisplayCamcorderPostview";
    public static final String DISPLAY_CAMERA_POSTVIEW = "com.lge.camera.command.DisplayCameraPostview";
    public static final String DISPLAY_PREVIEW = "com.lge.camera.command.DisplayPreview";
    public static final String DISPLAY_SETTING_MENU = "com.lge.camera.command.DisplaySettingMenu";
    public static final String DO_AFTER_FULLFRAME_CONTINUOUS = "com.lge.camera.command.DoAfterFullFrameContinuous";
    public static final String DO_CAPTURE = "com.lge.camera.command.DoCapture";
    public static final String DUAL_CAMERA_SHOT = "com.lge.camera.command.setting.SetDualCameraShot";
    public static final String EDIT_ALL_PREF_GEOTAG_OFF = "com.lge.camera.command.EditAllPrefGeoTagOff";
    public static final String EDIT_ALL_PREF_GEOTAG_ON = "com.lge.camera.command.EditAllPrefGeoTagOn";
    public static final String EXIT_INTERACTION = "com.lge.camera.command.ExitInteraction";
    public static final String EXIT_ZOOM_BRIGHTNESS_INTERACTION = "com.lge.camera.command.ExitZoomBrightnessInteraction";
    public static final String FREE_PANORAMA_SHOT = "com.lge.camera.command.setting.SetFreePanoramaShot";
    public static final String FULL_FRAME_CONTINUOUS_SHOT = "com.lge.camera.command.setting.SetFullFrameContinuousShot";
    public static final String GOTO_AUCLOUD = "com.lge.camera.command.GotoAUCloud";
    public static final String HDR_SHOT = "com.lge.camera.command.setting.SetOlaHDRShot";
    public static final String HIDE_FREE_PANORAMA_GUIDE = "com.lge.camera.command.HideFreePanoramaGuide";
    public static final String HIDE_LIVEEFFECT_SUBMENU_DRAWER = "com.lge.camera.command.HideLiveEffectSubMenuDrawer";
    public static final String HIDE_LIVE_SNAPSHOT_BUTTON = "com.lge.camera.command.HideLiveSnapshotButton";
    public static final String HIDE_MODE_MENU = "com.lge.camera.command.HideModeMenu";
    public static final String HIDE_PIP_FRAME_SUB_MENU = "com.lge.camera.command.HidePIPFrameSubMenu";
    public static final String HIDE_PIP_SUBWINDOW_RESIZE_HANDLER = "com.lge.camera.command.HidePIPResizeHandler";
    public static final String HIDE_QUICK_FUNCTION_DRAG_DROP = "com.lge.camera.command.HideQuickFunctionDragDrop";
    public static final String HIDE_QUICK_FUNCTION_SETTING_MENU = "com.lge.camera.command.HideQuickFunctionSettingMenu";
    public static final String HIDE_SETTING_EXPAND_PARENT_SCROLLBAR = "com.lge.camera.command.HideSettingExpandParentScrollbar";
    public static final String MAIN_BEAUTY_SHOT = "com.lge.camera.command.setting.SetOlaMainBeautyShot";
    public static final String NORMAL_SHOT = "com.lge.camera.command.setting.SetOlaNormalShot";
    public static final String ON_DELAY_OFF = "com.lge.camera.command.OnDelayOff";
    public static final String PANORAMA_SHOT = "com.lge.camera.command.setting.SetOlaPanoramaShot";
    public static final String PAUSE_RECORDING = "com.lge.camera.command.PauseRecording";
    public static final String PLANE_PANORAMA_SHOT = "com.lge.camera.command.setting.SetPlanePanoramaShot";
    public static final String REFOCUS_SHOT = "com.lge.camera.command.setting.SetRefocusShot";
    public static final String RELEASE_TOUCH_FOCUS = "com.lge.camera.command.ReleaseTouchFocus";
    public static final String REMOVE_SETTING_MENU = "com.lge.camera.command.RemoveSettingMenu";
    public static final String RESET_FREE_PANORAMA = "com.lge.camera.command.ResetFreePanorama";
    public static final String RESET_MENU = "com.lge.camera.command.ResetMenu";
    public static final String RESET_MENU_PREPARED = "com.lge.camera.command.ResetMenuPrepared";
    public static final String RESTART_PREVIEW = "com.lge.camera.command.RestartPreview";
    public static final String RESTORE_OPTIONAL_PARAMETERS = "com.lge.camera.command.setting.RestoreOptionalParameters";
    public static final String RESUME_RECORDING = "com.lge.camera.command.ResumeRecording";
    public static final String ROTATE = "com.lge.camera.command.Rotate";
    public static final String RUN_PANORAMA_START_SYC_TASK = "com.lge.camera.command.RunPanoramaStartSyncTask";
    public static final String SELECT_DURATION = "com.lge.camera.command.SelectDuration";
    public static final String SELECT_VIDEO_LENGTH = "com.lge.camera.command.SelectVideoLength";
    public static final String SET_AUDIO_ZOOM = "com.lge.camera.command.setting.SetAudiozoom";
    public static final String SET_BEAUTYSHOT = "com.lge.camera.command.setting.SetBeautyshot";
    public static final String SET_BRIGHTNESS = "com.lge.camera.command.setting.SetBrightness";
    public static final String SET_CAMERA_3D_DEPTH = "com.lge.camera.command.setting.setCamera3dDepth";
    public static final String SET_CAMERA_ID_BEFORE_START_INIT = "com.lge.camera.command.SetCameraIdBeforeStartInit";
    public static final String SET_CAMERA_MODE = "com.lge.camera.command.setting.SetCameraMode";
    public static final String SET_DUAL_RECORDING = "com.lge.camera.command.setting.SetDualRecording";
    public static final String SET_LIVE_EFFECT = "com.lge.camera.command.setting.SetLiveEffect";
    public static final String SET_MANUAL_FOCUS = "com.lge.camera.command.setting.SetManualFocus";
    public static final String SET_OPTIONAL_PARAMETERS = "com.lge.camera.command.setting.SetOptionalParameters";
    public static final String SET_SMART_MODE = "com.lge.camera.command.setting.SetSmartCameraMode";
    public static final String SET_SMART_ZOOM_RECORDING = "com.lge.camera.command.setting.SetSmartZoomRecording";
    public static final String SET_STORAGE = "com.lge.camera.command.setting.SetStorage";
    public static final String SET_SUPER_ZOOM = "com.lge.camera.command.setting.SetSuperZoom";
    public static final String SET_TIMEMACHINE_MODE = "com.lge.camera.command.SetTimeMachineMode";
    public static final String SET_UPLUS_BOX = "com.lge.camera.command.SetUplusBoxMode";
    public static final String SET_VIDEO_MODE = "com.lge.camera.command.setting.SetVideoRecordMode";
    public static final String SET_VIDEO_STABILIZATION = "com.lge.camera.command.setting.SetVideoStabilization";
    public static final String SET_VOICE_SHUTTER = "com.lge.camera.command.SetVoiceShutterMode";
    public static final String SET_VOLUME_KEY = "com.lge.camera.command.setting.SetVolumeKey";
    public static final String SET_WDR_RECORDING = "com.lge.camera.command.setting.SetWDRRecording";
    public static final String SET_ZOOM = "com.lge.camera.command.setting.SetZoom";
    public static final String SHOW_BEAUTYSHOT = "com.lge.camera.command.ShowBeautyshot";
    public static final String SHOW_BRIGHTNESS = "com.lge.camera.command.ShowBrightness";
    public static final String SHOW_CAMERA_3D_DEPTH = "com.lge.camera.command.showCamera3dDepth";
    public static final String SHOW_GALLERY = "com.lge.camera.command.ShowGallery";
    public static final String SHOW_HELP_ACTIVITY = "com.lge.camera.command.ShowHelpActivity";
    public static final String SHOW_HELP_GUIDE_POPUP = "com.lge.camera.command.ShowHelpGuidePopup";
    public static final String SHOW_LIVEEFFECT_SUBMENU = "com.lge.camera.command.ShowLiveEffectSubMenu";
    public static final String SHOW_LIVEEFFECT_SUBMENU_DRAWER = "com.lge.camera.command.ShowLiveEffectSubMenuDrawer";
    public static final String SHOW_LIVE_SNAPSHOT_BUTTON = "com.lge.camera.command.ShowLiveSnapshotButton";
    public static final String SHOW_MODE_MENU = "com.lge.camera.command.ShowModeMenu";
    public static final String SHOW_PIP_FRAME_SUB_MENU = "com.lge.camera.command.ShowPIPFrameSubMenu";
    public static final String SHOW_PIP_SUBWINDOW_RESIZE_HANDLER = "com.lge.camera.command.ShowPIPResizeHandler";
    public static final String SHOW_PROGRESS_DIALOG = "com.lge.camera.command.ShowProgressDialog";
    public static final String SHOW_QUICK_FUNCTION_DRAG_DROP = "com.lge.camera.command.ShowQuickFunctionDragDrop";
    public static final String SHOW_QUICK_FUNCTION_SETTING_MENU = "com.lge.camera.command.ShowQuickFunctionSettingMenu";
    public static final String SHOW_RESET_DIALOG = "com.lge.camera.command.ShowResetDialog";
    public static final String SHOW_SETTING_BEAUTYSHOT = "com.lge.camera.command.ShowSettingBeautyShot";
    public static final String SHOW_SETTING_BRIGHTNESS = "com.lge.camera.command.ShowSettingBrightness";
    public static final String SHOW_SETTING_CAMERA_3D_DEPTH = "com.lge.camera.command.showSettingCamera3dDepth";
    public static final String SHOW_SETTING_EXPAND_CHILD = "com.lge.camera.command.ShowSettingExpandChild";
    public static final String SHOW_SETTING_MENU = "com.lge.camera.command.ShowSettingMenu";
    public static final String SHOW_SETTING_ZOOM = "com.lge.camera.command.ShowSettingZoom";
    public static final String SHOW_ZOOM = "com.lge.camera.command.ShowZoom";
    public static final String SNAPSHOT_EFFECT = "com.lge.camera.command.SnapshotEffect";
    public static final String START_INIT = "com.lge.camera.command.StartInit";
    public static final String START_PREVIEW = "com.lge.camera.command.StartPreview";
    public static final String START_RECORDING = "com.lge.camera.command.StartRecording";
    public static final String STOP_FREE_PANORAMA = "com.lge.camera.command.StopFreePanorama";
    public static final String STOP_PREVIEW = "com.lge.camera.command.StopPreview";
    public static final String STOP_RECORDING = "com.lge.camera.command.StopRecording";
    public static final String SWAP_CAMERA = "com.lge.camera.command.SwapCamera";
    public static final String SWAP_CAMERA_DIMENSION = "com.lge.camera.command.SwapCameraDimension";
    public static final String TAG = "CameraApp";
    public static final String TAKE_PICTURE = "com.lge.camera.command.TakePicture";
    public static final String TAKE_PICTURE_IN_RECORDING = "com.lge.camera.command.TakePictureInRecording";
    public static final String TIME_MACHINE_SHOT = "com.lge.camera.command.setting.SetOlaTimeMachineShot";
    public static final String UPDATE_CAPTURE_BUTTON = "com.lge.camera.command.UpdateCaptureButton";
    public static final String UPDATE_RECORDING_TIME = "com.lge.camera.command.UpdateRecordingTime";
    public static final String UPDATE_REC_INDICATOR = "com.lge.camera.command.UpdateRecIndicator";
    public static final String UPDATE_THUMBNAIL_BUTTON = "com.lge.camera.command.UpdateThumbnailButton";
    public static final String VIDEO_COLOR_EFFECT = "com.lge.camera.command.setting.SetVideoColorEffect";
    public static final String VIDEO_DURATION = "com.lge.camera.command.setting.SetVideoDuration";
    public static final String VIDEO_IMAGE_SIZE = "com.lge.camera.command.setting.SetVideoImageSize";
    public static final String VIDEO_SCENE_MODE = "com.lge.camera.command.setting.SetVideoSceneMode";
    public static final String VIDEO_VOICE = "com.lge.camera.command.setting.SetVideoVoice";
    public static final String VIDEO_WHITE_BALANCE = "com.lge.camera.command.setting.SetVideoWhiteBalance";
    private Object mArgment;
    private Object mArgment2;
    public ControllerFunction mGet;
    private long mPeriod;
    private boolean mPosted;
    private long mStartTime;

    public abstract void execute();

    public Command(ControllerFunction function) {
        this.mPeriod = 0;
        this.mStartTime = 0;
        this.mArgment = null;
        this.mArgment2 = null;
        this.mPosted = false;
        this.mGet = null;
        this.mGet = function;
    }

    protected boolean checkMediator() {
        return this.mGet != null;
    }

    protected int getPixelFromDimens(int id) {
        return Common.getPixelFromDimens(this.mGet.getApplicationContext(), id);
    }

    public void executeNoneParameter() {
        if (this.mPosted) {
            this.mPosted = false;
        }
        execute();
    }

    public void executeNoneParameter(Object arg) {
        if (this.mPosted) {
            this.mPosted = false;
        }
        execute();
    }

    public void execute(Object arg) {
        if (this.mPosted) {
            this.mPosted = false;
        }
        execute();
    }

    public void execute(Object arg1, Object arg2) {
        if (this.mPosted) {
            this.mPosted = false;
        }
        execute();
    }

    public void resetStartTime() {
        this.mStartTime = 0;
    }

    public void setRepeat(long period) {
        this.mPeriod = period;
    }

    public void run() {
        this.mGet.removePostRunnable(this);
        if (this.mStartTime == 0) {
            this.mStartTime = SystemClock.uptimeMillis();
        }
        this.mStartTime += this.mPeriod;
        if (this.mArgment != null && this.mArgment2 == null) {
            execute(this.mArgment);
        } else if (this.mArgment == null || this.mArgment2 == null) {
            execute();
        } else {
            execute(this.mArgment, this.mArgment2);
        }
        if (this.mPeriod > 0) {
            this.mGet.getHandler().postAtTime(this, this.mStartTime);
        }
    }

    public void stop() {
        this.mGet.getHandler().removeCallbacks(this);
    }

    public void setArgument(Object arg) {
        this.mArgment = arg;
    }

    public void setArgument(Object arg1, Object arg2) {
        this.mArgment = arg1;
        this.mArgment2 = arg2;
    }

    public Object getArgument() {
        return this.mArgment;
    }

    public boolean getPosted() {
        return this.mPosted;
    }

    public void setPosted(boolean con) {
        this.mPosted = con;
    }
}
