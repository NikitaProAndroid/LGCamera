package com.lge.camera.receiver;

import android.content.Context;
import android.content.Intent;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.StorageProperties;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;

public class CameraMediaBroadcastReceiver extends CameraBroadCastReceiver {
    public CameraMediaBroadcastReceiver(ReceiverMediatorBridge bridge) {
        super(bridge);
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        CamLog.i(FaceDetector.TAG, action + "data : " + intent.getDataString());
        if (checkOnReceive(intent.getDataString())) {
            try {
                if (action.equals("android.intent.action.MEDIA_EJECT") || action.equals("android.intent.action.MEDIA_REMOVED") || action.equals("android.intent.action.MEDIA_BAD_REMOVAL")) {
                    doMediaBadRemoval();
                } else if (action.equals("android.intent.action.MEDIA_MOUNTED")) {
                    doMediaMounted(intent);
                } else if (action.equals("android.intent.action.MEDIA_UNMOUNTED")) {
                    doMediaUnMounted();
                }
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "CameraMediaBroadcastReceiver Exception : ", e);
            }
        }
    }

    private void doMediaUnMounted() {
        if (!this.mGet.isPausing()) {
            this.mGet.doCommandDelayed(Command.UPDATE_THUMBNAIL_BUTTON, 200);
        }
    }

    private void doMediaMounted(Intent intent) {
        CamLog.i(FaceDetector.TAG, "doMediaMounted");
        if (!this.mGet.isPausing()) {
            if (FunctionProperties.isSupportBurstShot() && this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS) && this.mGet.getInCaptureProgress()) {
                this.mGet.stopByUserAction();
            }
            this.mGet.toastControllerHide(false);
            this.mGet.storageToasthide(false);
            this.mGet.checkStorage(false);
            this.mGet.doCommandDelayed(Command.UPDATE_THUMBNAIL_BUTTON, 1000);
            if (StorageProperties.isAllMemorySupported() && this.mGet.getExternalStorageDir() != null && intent.getDataString() != null && intent.getDataString().endsWith(this.mGet.getExternalStorageDir())) {
                if (this.mGet.isRotateDialogVisible()) {
                    this.mGet.onDismissRotateDialog();
                }
                this.mGet.showDialogPopup(26);
                this.mGet.setPreferenceMenuEnable(Setting.KEY_STORAGE, true, false);
            }
        }
    }

    private void doMediaBadRemoval() {
        if (!this.mFinished) {
            if (!this.mGet.isPausing()) {
                if (StorageProperties.isAllMemorySupported()) {
                    this.mGet.setSetting(Setting.KEY_STORAGE, StorageProperties.getEmmcName());
                    this.mGet.doCommand(Command.SET_STORAGE);
                }
                this.mGet.toastControllerHide(true);
                if (StorageProperties.isInternalMemoryOnly()) {
                    Common.toastLong(this.mGet.getApplicationContext(), this.mGet.getString(R.string.sp_not_available_connected_mass_storage_NORMAL));
                } else {
                    Common.toastLong(this.mGet.getApplicationContext(), this.mGet.getString(R.string.sp_sd_card_removed_NORMAL));
                }
                this.mGet.doCommandDelayed(Command.UPDATE_THUMBNAIL_BUTTON, 1000);
                CameraConstants.MEDIA_RECEIVER_FINISHED = true;
            }
            this.mGet.getActivity().finish();
            this.mFinished = true;
        }
    }

    private boolean checkOnReceive(String dataSting) {
        if (this.mGet == null) {
            CamLog.w(FaceDetector.TAG, "mGet is null");
            return false;
        } else if (!this.mGet.checkStorageController()) {
            CamLog.w(FaceDetector.TAG, "storageController is null");
            return false;
        } else if (!"file:///storage/USBstorage1".equals(dataSting)) {
            return true;
        } else {
            CamLog.w(FaceDetector.TAG, "file:///storage/USBstrorage1");
            return false;
        }
    }
}
