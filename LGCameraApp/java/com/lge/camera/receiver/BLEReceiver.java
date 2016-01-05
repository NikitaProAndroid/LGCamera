package com.lge.camera.receiver;

import android.content.Context;
import android.content.Intent;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceDetector;

public class BLEReceiver extends CameraBroadCastReceiver {
    public BLEReceiver(ReceiverMediatorBridge bridge) {
        super(bridge);
    }

    public void onReceive(Context context, Intent intent) {
        if (CameraConstants.BLE_ONEKEY_CHANGED.equals(intent.getAction()) && this.mGet.isPreviewing() && CameraConstants.BLE_ONEKEY_SERVICE.equals(intent.getStringExtra("_service"))) {
            String key = intent.getStringExtra("_key");
            CamLog.d(FaceDetector.TAG, "key:" + key);
            if (CameraConstants.ONEKEY_CONTROL_ENABLE_STRING.equals(intent.getStringExtra("_onekeyCamera"))) {
                if (key == null || !key.equals("ShortKey") || !checkCleanViewForShutterBLEKey()) {
                    return;
                }
                if (this.mGet.getApplicationMode() == 0) {
                    this.mGet.getHandler().post(this.mGet.getSnapshotRunnable());
                } else if (this.mGet.getVideoState() == 3 || this.mGet.getVideoState() == 4) {
                    CamLog.d(FaceDetector.TAG, "VIDEO_STATE_RECORDING");
                    if (this.mGet.isRecordedLengthTooShort()) {
                        CamLog.d(FaceDetector.TAG, String.format("Ignore stop recording request. It's too short.", new Object[0]));
                        return;
                    }
                    this.mGet.clearSettingMenuAndSubMenu();
                    this.mGet.doCommandUi(Command.STOP_RECORDING);
                } else {
                    this.mGet.getHandler().post(this.mGet.getSnapshotRunnable());
                }
            } else if (CameraConstants.ONEKEY_CONTROL_DISABLE_STRING.equals(intent.getStringExtra("_onekeyCamera"))) {
                CamLog.d(FaceDetector.TAG, "LG Smart Onekey Camera key Skipped !");
            }
        }
    }

    private boolean checkCleanViewForShutterBLEKey() {
        if (this.mGet.getSubMenuMode() == 5 || this.mGet.getSubMenuMode() == 16) {
            this.mGet.setSubMenuMode(0);
            this.mGet.doCommandUi(Command.REMOVE_SETTING_MENU);
            if (!this.mGet.isRotateDialogVisible() || this.mGet.getDialogID() == 22 || this.mGet.getDialogID() == 27) {
                return false;
            }
            this.mGet.onDismissRotateDialog();
            return false;
        } else if (this.mGet.getSubMenuMode() == 22) {
            this.mGet.setSubMenuMode(0);
            this.mGet.doCommandUi(Command.HIDE_QUICK_FUNCTION_DRAG_DROP);
            return false;
        } else if (this.mGet.getSubMenuMode() == 21) {
            this.mGet.setSubMenuMode(0);
            this.mGet.doCommandUi(Command.HIDE_QUICK_FUNCTION_SETTING_MENU);
            return false;
        } else if (this.mGet.getSubMenuMode() != 0) {
            this.mGet.setSubMenuMode(0);
            this.mGet.clearSubMenu();
            if (!(Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)) || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)))) {
                this.mGet.showFocus();
            }
            if (!this.mGet.isRotateDialogVisible() || this.mGet.getDialogID() == 22) {
                return false;
            }
            this.mGet.onDismissRotateDialog();
            return false;
        } else if (!this.mGet.isRotateDialogVisible()) {
            return true;
        } else {
            switch (this.mGet.getDialogID()) {
                case Ola_ShotParam.ImageEffect_Vivid /*14*/:
                    return false;
                case DialogCreater.DIALOG_ID_HELP_HDR /*101*/:
                case DialogCreater.DIALOG_ID_HELP_PANORAMA /*102*/:
                case DialogCreater.DIALOG_ID_HELP_TIMEMACHINE /*103*/:
                case DialogCreater.DIALOG_ID_HELP_CONTINUOUS_SHOT /*104*/:
                case DialogCreater.DIALOG_ID_HELP_BEAUTY_SHOT /*105*/:
                case DialogCreater.DIALOG_ID_HELP_VOICE_PHOTO /*106*/:
                case DialogCreater.DIALOG_ID_HELP_LIVE_EFFECT /*107*/:
                case DialogCreater.DIALOG_ID_HELP_BURST_SHOT /*109*/:
                case DialogCreater.DIALOG_ID_HELP_INTELLIGENT_AUTO_MODE /*110*/:
                case DialogCreater.DIALOG_ID_HELP_WDR_MOVIE /*111*/:
                case DialogCreater.DIALOG_ID_HELP_DUAL_RECORDING /*112*/:
                case DialogCreater.DIALOG_ID_HELP_UPLUS_BOX /*113*/:
                case DialogCreater.DIALOG_ID_HELP_CLEAR_SHOT /*115*/:
                case DialogCreater.DIALOG_ID_HELP_DUAL_CAMERA /*116*/:
                case DialogCreater.DIALOG_ID_HELP_SMART_ZOOM_RECORDING /*117*/:
                case DialogCreater.DIALOG_ID_HELP_HDR_MOVIE /*118*/:
                    this.mGet.onDismissRotateDialog();
                    return false;
                default:
                    return false;
            }
        }
    }
}
