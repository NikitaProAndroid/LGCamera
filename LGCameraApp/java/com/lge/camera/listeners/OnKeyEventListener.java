package com.lge.camera.listeners;

import android.view.KeyEvent;
import com.lge.camera.Mediator;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.BarView;
import com.lge.camera.components.CameraCoverView;
import com.lge.camera.components.ShutterButton;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.AudioUtil;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.TelephonyUtil;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.ISTAudioRecorder;
import com.lge.voiceshutter.library.LGKeyRec;

public class OnKeyEventListener {
    private static int mQClipHotkeyFlag;
    boolean needFocusFalse;

    public OnKeyEventListener() {
        this.needFocusFalse = false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event, Mediator mediator) {
        if (event != null && event.getRepeatCount() != 0 && keyCode != 24 && keyCode != 25 && keyCode != 79 && keyCode != 27 && keyCode != ProjectVariables.KEYCODE_QCLIP_HOT_KEY && keyCode != 66) {
            return false;
        }
        switch (keyCode) {
            case LGKeyRec.EVENT_STOPPED /*4*/:
                CamLog.d(FaceDetector.TAG, "KEYCODE_BACK");
                return doBackKey(mediator);
            case Tag.GPS_DEST_LAT_REF /*19*/:
            case Tag.GPS_DEST_LAT /*20*/:
            case Tag.GPS_DEST_LON_REF /*21*/:
            case Tag.GPS_DEST_LON /*22*/:
            case ISTAudioRecorder.NUM_JAVA_BUFFER /*80*/:
                return true;
            case Tag.GPS_DEST_BEAR_REF /*23*/:
                return true;
            case Tag.GPS_DEST_BEAR /*24*/:
                CamLog.d(FaceDetector.TAG, "&&KEYCODE_VOLUME_UP");
                return doVolumeKeyUpDown(keyCode, event, true, mediator);
            case Tag.GPS_DEST_DIST_REF /*25*/:
                CamLog.d(FaceDetector.TAG, "&&KEYCODE_VOLUME_DOWN");
                return doVolumeKeyUpDown(keyCode, event, false, mediator);
            case Tag.GPS_PROCESS_METHOD /*27*/:
                CamLog.d(FaceDetector.TAG, "KEYCODE_CAMERA");
                if (!checkMediator(mediator)) {
                    return true;
                }
                if (event == null || !shutterHotKey(keyCode, event.getRepeatCount(), mediator)) {
                    return true;
                }
                return true;
            case 66:
                CamLog.d(FaceDetector.TAG, "&&KEYCODE_ENTER");
                return doKeyEnter(keyCode, event, mediator);
            case 79:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case MediaProviderUtils.ROTATION_90 /*90*/:
            case DialogCreater.DIALOG_ID_HELP_END_INDEX /*126*/:
            case 127:
                CamLog.d(FaceDetector.TAG, "MEDIA_KEY or HEADSETHOOK DOWN " + mediator.getVideoState());
                return doHeadSetHookAndMediaKey(mediator, event);
            case 82:
                CamLog.d(FaceDetector.TAG, "KEYCODE_MENU");
                if (CameraCoverView.isCoverOpen()) {
                    return doMenuKey(mediator);
                }
                return true;
            case 84:
                CamLog.d(FaceDetector.TAG, "KEYCODE_SEARCH");
                return true;
            default:
                return doHotKey(keyCode, event, true, mediator);
        }
    }

    static {
        mQClipHotkeyFlag = 0;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event, Mediator mediator) {
        if (event != null && event.getRepeatCount() != 0 && keyCode != 24 && keyCode != 25 && keyCode != 79) {
            return false;
        }
        switch (keyCode) {
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case 82:
                CamLog.d(FaceDetector.TAG, "KEYCODE_MENU or KEYCODE_BACK keyup");
                return false;
            case Tag.GPS_DEST_BEAR /*24*/:
            case Tag.GPS_DEST_DIST_REF /*25*/:
            case 66:
                CamLog.d(FaceDetector.TAG, "KEYCODE_VOLUME keyUp.");
                if (mediator == null) {
                    return true;
                }
                if (mediator.isRotateDialogVisible() && mediator.getDialogID() == DialogCreater.DIALOG_ID_HELP_VOICE_PHOTO) {
                    return false;
                }
                if (!LGT_Limit.ISP_ZOOM.equals(mediator.getSettingValue(Setting.KEY_VOLUME)) || keyCode == 66 || Common.isQuickWindowCameraMode()) {
                    releaseBurstShotPressed(mediator);
                    refreshMenusForShutterHotKey(mediator);
                    if (mediator.isCameraKeyLongPressed()) {
                        mediator.setCameraKeyLongPressed(false);
                        if (!CameraConstants.SMART_MODE_ON.equals(mediator.getSettingValue(Setting.KEY_VOICESHUTTER))) {
                            AudioUtil.setAudioFocus(mediator.getApplicationContext(), false);
                        }
                    }
                } else if (mediator.getZoomController().getZoomBar() != null) {
                    mediator.getZoomController().getZoomBar().updateBarWithTimer(null, BarView.CURSOR_ONE_STEP_MINUS, null, true, false, true);
                    mediator.getZoomController().getZoomBar().stopTimerTask();
                }
                return true;
            case 79:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case MediaProviderUtils.ROTATION_90 /*90*/:
            case DialogCreater.DIALOG_ID_HELP_END_INDEX /*126*/:
            case 127:
                return doHeadSetHookAndMediaKey(mediator, event);
            default:
                return doHotKey(keyCode, event, false, mediator);
        }
    }

    private boolean doHotKey(int keyCode, KeyEvent event, boolean isKeyDown, Mediator mediator) {
        if (event == null || keyCode != ProjectVariables.KEYCODE_QCLIP_HOT_KEY) {
            return false;
        }
        if (TelephonyUtil.isRinging(mediator.getApplicationContext())) {
            CamLog.v(FaceDetector.TAG, "Do not support photo story in call popup state");
            mediator.toast(mediator.getString(R.string.not_available_during_incoming_call));
            return false;
        }
        int nRepeatCount = event.getRepeatCount();
        if (FunctionProperties.isSupportQClipCustomization()) {
            CamLog.v(FaceDetector.TAG, "KEYCODE_QCLIP_HOT_KEY, isKeyDown = " + isKeyDown + " nRepeatCount = " + nRepeatCount);
            if (nRepeatCount == 0 && isKeyDown) {
                mQClipHotkeyFlag = AppControlUtil.getQClipHotkeyFlag(mediator.getApplicationContext());
                CamLog.v(FaceDetector.TAG, "mQClipHotkeyFlag = " + mQClipHotkeyFlag);
            }
            switch (mQClipHotkeyFlag) {
                case Ola_ShotParam.Panorama_Begin /*16*/:
                    if (!isKeyDown) {
                        releaseBurstShotPressed(mediator);
                        refreshMenusForShutterHotKey(mediator);
                        break;
                    }
                    CamLog.v(FaceDetector.TAG, "Common.QCLIP_HOTKEY_FLAG_KEYDOWN_SHUTTER");
                    doShutterKey(keyCode, nRepeatCount, mediator);
                    break;
                case Ola_ImageFormat.YUVPLANAR_LABEL /*1024*/:
                    if (!isKeyDown) {
                        CamLog.v(FaceDetector.TAG, "QCLIP_HOTKEY_FLAG_KEYUP_ROTATE");
                        mediator.setOrientationForced(-1);
                        break;
                    }
                    break;
                default:
                    CamLog.v(FaceDetector.TAG, "Common.QCLIP_HOTKEY_FLAG_NONE");
                    break;
            }
            if (!isKeyDown && mediator.isCameraKeyLongPressed()) {
                mediator.setCameraKeyLongPressed(false);
                if (!CameraConstants.SMART_MODE_ON.equals(mediator.getSettingValue(Setting.KEY_VOICESHUTTER))) {
                    AudioUtil.setAudioFocus(mediator.getApplicationContext(), false);
                }
            }
        } else if (isKeyDown) {
            CamLog.v(FaceDetector.TAG, "Common.QCLIP_HOTKEY - shutter");
            doShutterKey(keyCode, nRepeatCount, mediator);
        } else {
            releaseBurstShotPressed(mediator);
            refreshMenusForShutterHotKey(mediator);
        }
        return true;
    }

    private boolean doShutterKey(int keyCode, int repeatCount, Mediator mediator) {
        if (checkMediator(mediator) && shutterHotKey(keyCode, repeatCount, mediator)) {
            return true;
        }
        return false;
    }

    private boolean doHeadSetHookAndMediaKey(Mediator mediator, KeyEvent event) {
        if (!checkMediator(mediator) || event == null) {
            return true;
        }
        CamLog.d(FaceDetector.TAG, "MEDIA_KEY or HEADSETHOOK UP" + mediator.getVideoState());
        if (TelephonyUtil.phoneInCall(mediator.getApplicationContext())) {
            CamLog.d(FaceDetector.TAG, "go to incomming call");
            return false;
        } else if (mediator.getApplicationMode() == 1) {
            int video_state = mediator.getVideoState();
            if (TelephonyUtil.phoneInCall(mediator.getApplicationContext())) {
                return false;
            }
            if (video_state == 3 || video_state == 4 || video_state == 1 || video_state == 2) {
                return true;
            }
            return false;
        } else if (isTimerSettingActive(mediator)) {
            return true;
        } else {
            if (FunctionProperties.isVoiceShutter() && CameraConstants.SMART_MODE_ON.equals(mediator.getSettingValue(Setting.KEY_VOICESHUTTER))) {
                return true;
            }
            return false;
        }
    }

    private boolean doVolumeKeyUpDown(int keyCode, KeyEvent event, boolean upKey, Mediator mediator) {
        if (!checkMediator(mediator) || mediator.getShowCameraErrorPopup() || !CameraCoverView.isCoverOpen()) {
            return true;
        }
        if (mediator.isRotateDialogVisible() && mediator.getDialogID() == DialogCreater.DIALOG_ID_HELP_VOICE_PHOTO) {
            return false;
        }
        if ((!ProjectVariables.isSupportCameraKey() && event != null && shutterHotKey(keyCode, event.getRepeatCount(), mediator)) || checkKeyAction(mediator) || checkZoomOnRecord(event, upKey, mediator) || mediator.getVideoState() != 0) {
            return true;
        }
        updateZoom(event, upKey, mediator);
        return true;
    }

    private boolean checkKeyAction(Mediator mediator) {
        if ((ProjectVariables.useHideQFLWhenSettingMenuDisplay() && (mediator.getSubMenuMode() == 5 || mediator.getSubMenuMode() == 16)) || mediator.isShotModeMenuVisible() || mediator.isShotModeMenuVisible() || !mediator.isControllerInitialized() || mediator.isPressedShutterButton() || mediator.getInCaptureProgress() || mediator.getBeautyshotProgress()) {
            return true;
        }
        if (LGT_Limit.ISP_ZOOM.equals(mediator.getSettingValue(Setting.KEY_VOLUME)) && (mediator.getCameraId() == 1 || !mediator.checkShotModeForZoomInOut() || mediator.checkSlowMotionMode())) {
            if (mediator.checkSlowMotionMode()) {
                mediator.toastConstant(mediator.getString(R.string.volume_key_zoom_disable_resolution));
                return true;
            }
            mediator.toastConstant(mediator.getString(R.string.volume_key_zoom_disable));
            return true;
        } else if (mediator.getApplicationMode() == 0 && mediator.isSnapOnFinish()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkZoomOnRecord(KeyEvent event, boolean upKey, Mediator mediator) {
        if (FunctionProperties.isSupportZoomOnRecord() && LGT_Limit.ISP_ZOOM.equals(mediator.getSettingValue(Setting.KEY_VOLUME))) {
            if (Common.isQuickWindowCameraMode()) {
                mediator.doCommandUi(Command.TAKE_PICTURE);
                return true;
            } else if (mediator.getCameraId() == 0 && (mediator.getVideoState() == 3 || mediator.getVideoState() == 4)) {
                updateZoom(event, upKey, mediator);
            }
        }
        return false;
    }

    private boolean updateZoom(KeyEvent event, boolean upKey, Mediator mediator) {
        if (!((event != null && event.getRepeatCount() != 0) || mediator.getZoomController().getZoomBar() == null || mediator.getCameraDevice() == null || mediator.getParameters() == null)) {
            if (!(mediator.getSubMenuMode() == 6 || mediator.getSubMenuMode() == 5 || mediator.getSubMenuMode() == 16)) {
                mediator.doCommand(Command.SHOW_ZOOM);
            }
            mediator.getZoomController().getZoomBar().updateBarWithTimer(null, upKey ? BarView.CURSOR_ONE_STEP_PLUS : BarView.CURSOR_ONE_STEP_MINUS, null, true, false, false);
        }
        return true;
    }

    private boolean doKeyEnter(int keyCode, KeyEvent event, Mediator mediator) {
        if (checkMediator(mediator) && ((event == null || !shutterHotKey(keyCode, event.getRepeatCount(), mediator)) && ProjectVariables.isSupportHDMI_MHL() && CheckStatusManager.isHDMIConnected() && checkKeyAction(mediator))) {
        }
        return true;
    }

    private boolean doBackKey(Mediator mediator) {
        if (!checkMediator(mediator)) {
            return true;
        }
        mediator.doCommand(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
        if (mediator.getPreviewPanelController().isGalleryLaunching()) {
            mediator.getPreviewPanelController().setGalleryLaunching(false);
        }
        if (checkQFLmenuAndSubMenuForBackKey(mediator)) {
            return true;
        }
        if (checkQuickViewForBackKey(mediator)) {
            return true;
        }
        if (mediator.isOptionMenuShowing()) {
            mediator.hideOptionMenu();
            return true;
        } else if (mediator.getApplicationMode() == 0) {
            return doBackKeyForCamera(mediator);
        } else {
            if (mediator.getApplicationMode() != 1) {
                return false;
            }
            boolean checkCamcorderStop = mediator.checkCamcorderStop(0, true);
            if (checkCamcorderStop) {
                return checkCamcorderStop;
            }
            mediator.hideSmartZoomFocusView();
            mediator.setBackKeyPressed(true);
            return checkCamcorderStop;
        }
    }

    private boolean doBackKeyForCamera(Mediator mediator) {
        if (isTimerSettingActive(mediator) || mediator.isGestureShotActivated()) {
            mediator.stopTimerShot();
            mediator.getPreviewPanelController().enableCommand(true);
            mediator.getPreviewPanelController().setSwitcherVisible(true);
            mediator.showQuickFunctionController();
            mediator.showIndicatorController();
            mediator.doCommandUi(Command.CAMERA_FOCUS_MODE);
            mediator.showFocus();
            if (FunctionProperties.isVoiceShutter() && CameraConstants.SMART_MODE_ON.equals(mediator.getSettingValue(Setting.KEY_VOICESHUTTER))) {
                mediator.doCommandUi(Command.SET_VOICE_SHUTTER);
            }
            if (mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY) || mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) {
                mediator.showBeautyshotController(true);
            }
            mediator.startGestureEngine();
            return true;
        } else if (mediator.isEnteringViewShowing()) {
            mediator.showQuickMenuEnteringGuide(false);
            return true;
        } else {
            mediator.setResultCancelForAttachMode();
            if (mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
                if (mediator.isPanoramaStarted()) {
                    mediator.stopPanorama();
                    mediator.getSoundController().playRecordingSound(false);
                    return true;
                }
            } else if (mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
                if (mediator.getPlanePanoramaStatus() >= 1 && mediator.getPlanePanoramaStatus() <= 3) {
                    mediator.stopPlanePanorama();
                    return true;
                }
            } else if (mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
                if (mediator.getFreePanoramaStatus() >= 2 && mediator.getFreePanoramaStatus() <= 3) {
                    mediator.stopFreePanorama();
                    return true;
                }
            } else if (mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CONTINUOUS) && mediator.stopByUserAction()) {
                return true;
            } else {
                if ((mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT) && mediator.stopByUserAction()) || mediator.getInCaptureProgress() || mediator.isExitIgnoreDuringSaving()) {
                    return true;
                }
                if (mediator.getQueueCount() > 10) {
                    mediator.setExitIgnoreDuringSaving(true);
                    mediator.showSavingProgressDialog();
                    return true;
                }
            }
            return false;
        }
    }

    private boolean checkQFLmenuAndSubMenuForBackKey(Mediator mediator) {
        if (mediator.isQuickFunctionSettingControllerShowing()) {
            mediator.doCommand(Command.HIDE_QUICK_FUNCTION_SETTING_MENU);
            return true;
        } else if (mediator.isQuickFunctionDragControllerVisible()) {
            mediator.doCommand(Command.HIDE_QUICK_FUNCTION_DRAG_DROP);
            return true;
        } else if (mediator.isRotateDialogVisible()) {
            dismissRotateDialog(mediator);
            return true;
        } else if (mediator.isShotModeMenuVisible()) {
            mediator.doCommandUi(Command.HIDE_MODE_MENU);
            return true;
        } else if (mediator.getSubMenuMode() == 16) {
            mediator.getSettingController().removeChildSettingView(true);
            mediator.clearSettingBarControll();
            return true;
        } else if (mediator.checkAndCloseChildView()) {
            return true;
        } else {
            if (mediator.getSubMenuMode() == 5 || mediator.getSubMenuMode() == 16) {
                mediator.setSubMenuMode(0);
                mediator.doCommandUi(Command.REMOVE_SETTING_MENU);
                return true;
            } else if (mediator.getSubMenuMode() == 18) {
                mediator.restoreLiveEffectSubMenu();
                mediator.setSubMenuMode(0);
                mediator.clearSubMenu();
                return true;
            } else if (mediator.getSubMenuMode() == 15) {
                return false;
            } else {
                if (mediator.getSubMenuMode() == 0) {
                    return false;
                }
                mediator.setSubMenuMode(0);
                mediator.clearSubMenu();
                if (CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(mediator.getSettingValue(Setting.KEY_FOCUS))) {
                    return true;
                }
                mediator.showFocus();
                return true;
            }
        }
    }

    private boolean checkQuickViewForBackKey(Mediator mediator) {
        if (mediator.isShowingQuickView()) {
            return true;
        }
        return false;
    }

    private boolean doMenuKey(Mediator mediator) {
        return false;
    }

    public boolean checkMediator(Mediator mediator) {
        if (mediator == null) {
            CamLog.d(FaceDetector.TAG, "checkKeyOperation : return, mediator is not initialized...");
            return false;
        } else if (!mediator.isControllerInitialized()) {
            CamLog.d(FaceDetector.TAG, "checkKeyOperation : return, mediator is not initialized...");
            return false;
        } else if (!mediator.isPausing()) {
            return true;
        } else {
            CamLog.d(FaceDetector.TAG, "checkKeyOperation : return, mediator is pausing...");
            return false;
        }
    }

    public boolean shutterHotKey(int keycode, int repeatCount, Mediator mediator) {
        if (!CameraCoverView.isCoverOpen()) {
            return true;
        }
        if (!CameraConstants.VOLUME_SHUTTER.equals(mediator.getSettingValue(Setting.KEY_VOLUME)) && !ProjectVariables.isSupportCameraKey() && keycode != ProjectVariables.KEYCODE_QCLIP_HOT_KEY && keycode != 66) {
            return false;
        }
        CamLog.d(FaceDetector.TAG, "shutterHotKey : repeatCount = " + repeatCount);
        if (mediator.isOptionMenuShowing()) {
            mediator.hideOptionMenu();
            return true;
        } else if (checkQFLmenuAndSubMenu(repeatCount, mediator, true) || checkHelpDialogForShutterHotKey(mediator)) {
            return true;
        } else {
            if (mediator.getApplicationMode() == 0 && checkShutterHotKeyInCamera(repeatCount, mediator)) {
                return true;
            }
            if (mediator.getApplicationMode() == 1 && mediator.checkCamcorderStop(repeatCount, false)) {
                return false;
            }
            if (mediator.isPreviewing()) {
                ShutterButton button = (ShutterButton) mediator.findViewById(R.id.main_button_bg);
                if (mediator.getPreviewPanelController() == null || button.getVisibility() != 0 || !button.isEnabled()) {
                    return true;
                }
                CamLog.d(FaceDetector.TAG, "KeyEvent.KEYCODE_HOT_KEY");
                if (repeatCount > 0) {
                    mediator.setCameraKeyLongPressed(true);
                    if (!CameraConstants.SMART_MODE_ON.equals(mediator.getSettingValue(Setting.KEY_VOICESHUTTER))) {
                        AudioUtil.setAudioFocus(mediator.getApplicationContext(), true);
                    }
                }
                mediator.setQuickFunctionMenuForcedDisable(true);
                mediator.setQuickButtonForcedDisable(true);
                this.needFocusFalse = false;
                mediator.getPreviewPanelController().onShutterButtonFocus(button, true);
                mediator.getPreviewPanelController().onShutterButtonClick(button);
                if (FunctionProperties.isSupportBurstShot() && mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS) && (mediator.getInCaptureProgress() || mediator.getFocusState() == 2)) {
                    this.needFocusFalse = true;
                    return true;
                }
                mediator.getPreviewPanelController().onShutterButtonFocus(button, false);
                return true;
            }
            CamLog.v(FaceDetector.TAG, "KeyEvent.KEYCODE_HOT_KEY return, not previewing");
            return true;
        }
    }

    private boolean checkQFLmenuAndSubMenu(int repeatCount, Mediator mediator, boolean isShutterKey) {
        if (mediator.getSubMenuMode() == 5 || mediator.getSubMenuMode() == 16) {
            doSubmenuOffAndHideMenu(Command.REMOVE_SETTING_MENU, repeatCount, mediator);
            dismissRotateDialog(mediator);
            return true;
        } else if (mediator.getSubMenuMode() == 22) {
            doSubmenuOffAndHideMenu(Command.HIDE_QUICK_FUNCTION_DRAG_DROP, repeatCount, mediator);
            return true;
        } else if (mediator.getSubMenuMode() == 21) {
            doSubmenuOffAndHideMenu(Command.HIDE_QUICK_FUNCTION_SETTING_MENU, repeatCount, mediator);
            return true;
        } else if (mediator.getSubMenuMode() == 18) {
            mediator.restoreLiveEffectSubMenu();
            mediator.clearSubMenu();
            return true;
        } else if (mediator.isShotModeMenuVisible()) {
            mediator.doCommandUi(Command.HIDE_MODE_MENU);
            return true;
        } else if (mediator.getSubMenuMode() == 15) {
            if (isShutterKey) {
                return false;
            }
            return true;
        } else if (mediator.getSubMenuMode() == 0) {
            return false;
        } else {
            if (repeatCount == 0) {
                mediator.setSubMenuMode(0);
                mediator.clearSubMenu();
                if (!(Setting.HELP_FACE_TRACKING_LED.equals(mediator.getSettingValue(Setting.KEY_FOCUS)) || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(mediator.getSettingValue(Setting.KEY_FOCUS)))) {
                    mediator.showFocus();
                }
            }
            dismissRotateDialog(mediator);
            return true;
        }
    }

    private void doSubmenuOffAndHideMenu(String command, int repeatCount, Mediator mediator) {
        if (repeatCount == 0) {
            mediator.setSubMenuMode(0);
            mediator.doCommandUi(command);
        }
    }

    private void dismissRotateDialog(Mediator mediator) {
        if (mediator.isRotateDialogVisible() && mediator.getDialogID() != 22 && mediator.getDialogID() != 27) {
            mediator.onDismissRotateDialog();
            if (mediator.getSubMenuMode() == 0) {
                mediator.quickFunctionAllMenuSelected(false);
            }
        }
    }

    private boolean isTimerSettingActive(Mediator mediator) {
        if (mediator.getSettingValue(Setting.KEY_CAMERA_TIMER).equals("0") || !mediator.isTimerShotCountdown()) {
            return false;
        }
        return true;
    }

    private boolean checkShutterHotKeyInCamera(int repeatCount, Mediator mediator) {
        if (isTimerSettingActive(mediator)) {
            return true;
        }
        String shotmode = mediator.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
        if (!(mediator.isTimeMachineModeOn() || CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotmode) || CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(shotmode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(shotmode) || CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(shotmode))) {
            if (repeatCount > 0) {
                return true;
            }
            if (CameraConstants.TYPE_SHOTMODE_PANORAMA.equals(shotmode)) {
                if (mediator.isSynthesisInProgress()) {
                    return true;
                }
                if (mediator.isPanoramaStarted()) {
                    mediator.stopPanorama();
                    mediator.getSoundController().playRecordingSound(false);
                    return true;
                }
            } else if (CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA.equals(shotmode)) {
                if (mediator.getPlanePanoramaStatus() == 1 || mediator.getPlanePanoramaStatus() == 2 || mediator.getPlanePanoramaStatus() == 4) {
                    return true;
                }
                if (mediator.getPlanePanoramaStatus() == 3) {
                    mediator.stopPlanePanorama();
                    return true;
                }
            } else if (CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA.equals(shotmode)) {
                if (mediator.getFreePanoramaStatus() == 2 || mediator.getFreePanoramaStatus() == 4) {
                    return true;
                }
                if (mediator.getFreePanoramaStatus() == 3) {
                    mediator.stopFreePanorama();
                    return true;
                }
            } else if (CameraConstants.TYPE_SHOTMODE_CONTINUOUS.equals(shotmode) && mediator.stopByUserAction()) {
                return true;
            } else {
                if (CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT.equals(shotmode) && mediator.isCurrnetModuleRunning()) {
                    return true;
                }
            }
        }
        String autoReview = mediator.getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW);
        if ((("on_delay_2sec".equals(autoReview) || "on_delay_5sec".equals(autoReview)) && repeatCount > 0) || mediator.getInCaptureProgress()) {
            return true;
        }
        if (!CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(mediator.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)) || mediator.getPreviewController().getCameraGLPreview().isCompleteProcessFrame()) {
            return false;
        }
        return true;
    }

    private boolean checkHelpDialogForShutterHotKey(Mediator mediator) {
        if (!mediator.isRotateDialogVisible()) {
            return false;
        }
        int dialogId = mediator.getDialogID();
        if (dialogId >= 100 && dialogId <= DialogCreater.DIALOG_ID_HELP_END_INDEX) {
            mediator.onDismissRotateDialog();
        }
        return true;
    }

    private void refreshMenusForShutterHotKey(Mediator mediator) {
        if ((!mediator.getInCaptureProgress() && (!FunctionProperties.isSupportAFonCAF() || !mediator.snapshotOnContinuousFocus())) || (!mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT) && !mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA))) {
            mediator.setQuickFunctionMenuForcedDisable(false);
            mediator.setQuickButtonForcedDisable(false);
            mediator.refreshMenuForVolumeShutterPress();
        }
    }

    private void releaseBurstShotPressed(Mediator mediator) {
        if (FunctionProperties.isSupportBurstShot() && mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS) && this.needFocusFalse) {
            mediator.getPreviewPanelController().onShutterButtonFocus((ShutterButton) mediator.findViewById(R.id.main_button_bg), false);
            this.needFocusFalse = false;
        }
    }
}
