package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AudioUtil;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.TelephonyUtil;
import com.lge.olaworks.library.FaceDetector;

public class SetVoiceShutterMode extends Command {
    private static int checkCount;
    private boolean mVoiceShutterValueOn;

    public SetVoiceShutterMode(ControllerFunction function) {
        super(function);
        this.mVoiceShutterValueOn = true;
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        Bundle bundle = (Bundle) arg;
        boolean subMenuClicked = bundle.getBoolean("subMenuClicked", false);
        boolean allSetting = bundle.getBoolean("allSetting", false);
        if (checkMediator()) {
            String value = this.mGet.getSettingValue(Setting.KEY_VOICESHUTTER);
            CamLog.d(FaceDetector.TAG, "## SetVoiceShutterMode : " + value);
            if (FunctionProperties.isVoiceShutter()) {
                if (this.mGet.getApplicationMode() == 0) {
                    if (!value.equals(CameraConstants.SMART_MODE_ON) || TelephonyUtil.phoneInCall(this.mGet.getApplicationContext())) {
                        if (!value.equals(CameraConstants.TYPE_PREFERENCE_NOT_FOUND)) {
                            if (!(AudioUtil.requestAudioFocusCount == 0 || this.mGet.isCameraKeyLongPressed() || this.mGet.isShutterButtonLongKey())) {
                                AudioUtil.setAudioFocus(this.mGet.getApplicationContext(), false);
                            }
                            this.mVoiceShutterValueOn = false;
                            this.mGet.setAudioRecogEngineStop();
                        }
                    } else if (checkAudioManagerCallStatus(true)) {
                        this.mGet.doCommandDelayed(Command.SET_VOICE_SHUTTER, bundle, 100);
                        return;
                    } else {
                        AudioUtil.setAudioFocus(this.mGet.getApplicationContext(), true);
                        this.mVoiceShutterValueOn = true;
                        if (allSetting) {
                            this.mGet.postOnUiThread(new Runnable() {
                                public void run() {
                                    if (SetVoiceShutterMode.this.mGet != null && !SetVoiceShutterMode.this.mGet.isPausing()) {
                                        SetVoiceShutterMode.this.mGet.removePostRunnable(this);
                                        SetVoiceShutterMode.this.mGet.setAudioRecogEngineStart();
                                    }
                                }
                            });
                        } else {
                            this.mGet.setAudioRecogEngineStart();
                        }
                    }
                    this.mGet.setSetting(Setting.KEY_VOICESHUTTER, value);
                }
                checkAudioManagerCallStatus(false);
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        if (SetVoiceShutterMode.this.checkMediator()) {
                            SetVoiceShutterMode.this.mGet.removePostRunnable(this);
                            if (SetVoiceShutterMode.this.mGet.getApplicationMode() == 0) {
                                SetVoiceShutterMode.this.mGet.updateVoiceShutterIndicator(false);
                                if (TelephonyUtil.phoneInCall(SetVoiceShutterMode.this.mGet.getApplicationContext()) || !(SetVoiceShutterMode.this.mGet.isTimeMachineModeOn() || CheckStatusManager.checkVoiceShutterEnable(SetVoiceShutterMode.this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)))) {
                                    SetVoiceShutterMode.this.mGet.setPreferenceMenuEnable(Setting.KEY_VOICESHUTTER, false, false);
                                } else {
                                    SetVoiceShutterMode.this.mGet.setPreferenceMenuEnable(Setting.KEY_VOICESHUTTER, true, true);
                                }
                            }
                        }
                    }
                });
                if (subMenuClicked) {
                    onExecuteAlone();
                    return;
                }
                return;
            }
            this.mVoiceShutterValueOn = false;
            CamLog.i(FaceDetector.TAG, "SetVoiceShutterMode : model is not supported.");
        }
    }

    protected void onExecuteAlone() {
        CamLog.d(FaceDetector.TAG, "SetVoiceShutterMode - Show Toast Message : mVoiceShutterValueOn is = " + this.mVoiceShutterValueOn);
        if (FunctionProperties.isVoiceShutter()) {
            if (this.mVoiceShutterValueOn && !this.mGet.showHelpGuidePopup(Setting.HELP_VOICE_PHOTO, DialogCreater.DIALOG_ID_HELP_VOICE_PHOTO, true)) {
                String toastMsg;
                String word_cheese = this.mGet.getString(R.string.sp_voiceshutter_sound_cheese_NORMAL);
                String word_smile = this.mGet.getString(R.string.sp_voiceshutter_sound_smile_NORMAL);
                String word_whisky = this.mGet.getString(R.string.sp_voiceshutter_sound_whisky_NORMAL);
                String word_kimchi = this.mGet.getString(R.string.sp_voiceshutter_sound_kimchi_NORMAL);
                String word_lg = this.mGet.getString(R.string.sp_voiceshutter_sound_LG_NORMAL);
                String word_torimasu = this.mGet.getString(R.string.sp_voiceshutter_sound_torimasu_NORMAL);
                if (FunctionProperties.isSupportVoiceShutterJapanese()) {
                    toastMsg = String.format(this.mGet.getString(R.string.sp_voiceshutter_multi_keyword_say_4items), new Object[]{word_cheese, word_smile, word_lg, word_torimasu});
                } else if (FunctionProperties.isSupportVoiceShutterAME()) {
                    toastMsg = String.format(this.mGet.getString(R.string.sp_voiceshutter_multi_keyword_say_4items), new Object[]{word_cheese, word_smile, word_kimchi, word_lg});
                } else {
                    toastMsg = String.format(this.mGet.getString(R.string.sp_voiceshutter_multi_keyword_say_NORMAL), new Object[]{word_cheese, word_smile, word_whisky, word_kimchi, word_lg});
                }
                this.mGet.toastMiddleLong(toastMsg);
            }
            this.mGet.quickFunctionControllerInitMenu();
            this.mGet.quickFunctionControllerRefresh(true);
        }
    }

    static {
        checkCount = 30;
    }

    private boolean checkAudioManagerCallStatus(boolean check) {
        if (check && checkCount > 0 && AudioUtil.isAudioManagerCallStatus(this.mGet.getApplicationContext())) {
            checkCount--;
            CamLog.d(FaceDetector.TAG, "isAudioManagerCallStatus() == true, checkAudioManagerCallStatusCount = " + checkCount);
            return true;
        }
        checkCount = 30;
        return false;
    }
}
