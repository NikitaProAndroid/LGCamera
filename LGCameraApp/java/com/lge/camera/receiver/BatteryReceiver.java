package com.lge.camera.receiver;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.lge.camera.R;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;

public class BatteryReceiver extends CameraBroadCastReceiver {
    public static final int BATTERY_CHARGING_CURRENT_INCOMPATIBLE_CHARGING = 2;
    public static final int BATTERY_CHARGING_CURRENT_NORMAL_CHARGING = 1;
    public static final int BATTERY_CHARGING_CURRENT_USB_DRIVER_UNINSTALLED = 4;
    public static final String BATTERY_EXTRA_CHARGING_CURRENT = "charging_current";
    public static final int UNCHARGE_LEVEL = 2;
    private Toast mToast;

    public BatteryReceiver(ReceiverMediatorBridge bridge) {
        super(bridge);
        this.mToast = null;
    }

    public void onReceive(Context context, Intent intent) {
        if (checkOnReceive(intent)) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.BATTERY_CHANGED")) {
                checkLowBattery(intent);
                actionBatteryChanged(intent);
            } else if (action.equals("android.intent.action.BATTERY_LOW")) {
                checkLowBattery(intent);
            }
            heatWarningByPowerConnection(action);
        }
    }

    private void actionBatteryChanged(Intent intent) {
        int charged = intent.getIntExtra("level", -1);
        int levelMax = intent.getIntExtra("scale", -1);
        int battThermal = intent.getIntExtra("temperature", 0);
        this.mGet.setBatteryTemper(battThermal);
        if (charged == -1 || charged > 5) {
            if (ProjectVariables.temperatureCheckMethod() == UNCHARGE_LEVEL && this.mGet.getVideoState() == 3) {
                double threshold = (this.mGet.isDualRecordingActive() ? CheckStatusManager.TEMPERATURE_ENTERING_DUAL_RECORDING : CheckStatusManager.TEMPERATURE_STANDARD) * 10.0d;
                if (this.mGet.isDualRecordingActive()) {
                    if (((double) battThermal) > threshold && !this.mGet.getActivity().isFinishing() && this.mGet.getCurrentRecordingTime() >= CheckStatusManager.TEMPERATURE_GUARANTEE_RECORDING_TIME) {
                        CamLog.d(FaceDetector.TAG, "Camera is finishing due to high temperature during recording. It's not the error.");
                        Toast.makeText(this.mGet.getApplicationContext(), this.mGet.getString(R.string.high_temp_action_on_recording), BATTERY_CHARGING_CURRENT_NORMAL_CHARGING).show();
                        this.mGet.getActivity().finish();
                    }
                } else if (((double) battThermal) > threshold && !this.mGet.getActivity().isFinishing()) {
                    CamLog.d(FaceDetector.TAG, "Camera is finishing due to high temperature during recording. It's not the error.");
                    Toast.makeText(this.mGet.getApplicationContext(), this.mGet.getString(R.string.high_temp_action_on_recording), BATTERY_CHARGING_CURRENT_NORMAL_CHARGING).show();
                    this.mGet.getActivity().finish();
                }
            }
        } else if (!this.mGet.getActivity().isFinishing() && CheckStatusManager.getCheckEnterOutSecure() == 0) {
            Common.toast(this.mGet.getApplicationContext(), this.mGet.getString(R.string.sp_lowbattery_MLINE));
            this.mGet.getActivity().finish();
        }
        this.mGet.setBatteryVisibility(charged);
        if (charged == -1 || levelMax == -1) {
            CamLog.d(FaceDetector.TAG, "Fail to receive battery level!");
            return;
        }
        int level = calculateBatteryLevel(charged);
        int status = intent.getIntExtra("status", -1);
        int pluged = intent.getIntExtra("plugged", 0);
        if (ModelProperties.getCarrierCode() == 6) {
            level = setIsCharging(21, level, pluged, status, intent.getIntExtra(BATTERY_EXTRA_CHARGING_CURRENT, BATTERY_CHARGING_CURRENT_NORMAL_CHARGING));
        } else {
            level = setIsCharging(21, level, pluged, status);
        }
        if (this.mGet.getActualBatteryLevel() != charged) {
            this.mGet.setActualBatteryLevel(charged);
        }
        if (this.mGet.getBatteryLevel() != level) {
            this.mGet.setBatteryLevel(level);
            this.mGet.setBatteryIndicator(this.mGet.getBatteryLevel());
        }
    }

    private int setIsCharging(int tempTotalBatteryLevel, int level, int pluged, int status) {
        if (status == UNCHARGE_LEVEL) {
            level += tempTotalBatteryLevel;
            if (ProjectVariables.isSupportHeat_detection()) {
                this.mGet.setIsCharging(true);
            }
        } else if (status == 5 && (pluged == BATTERY_CHARGING_CURRENT_NORMAL_CHARGING || pluged == UNCHARGE_LEVEL)) {
            level += tempTotalBatteryLevel;
            if (ProjectVariables.isSupportHeat_detection()) {
                this.mGet.setIsCharging(true);
            }
        }
        return level;
    }

    private int setIsCharging(int tempTotalBatteryLevel, int level, int pluged, int status, int currentChargeStatus) {
        if (currentChargeStatus == UNCHARGE_LEVEL || (currentChargeStatus == BATTERY_CHARGING_CURRENT_USB_DRIVER_UNINSTALLED && pluged == UNCHARGE_LEVEL)) {
            return level + (tempTotalBatteryLevel * UNCHARGE_LEVEL);
        }
        if (status == UNCHARGE_LEVEL) {
            level += tempTotalBatteryLevel;
            if (!ProjectVariables.isSupportHeat_detection()) {
                return level;
            }
            this.mGet.setIsCharging(true);
            return level;
        } else if (status != 5) {
            return level;
        } else {
            if (pluged != BATTERY_CHARGING_CURRENT_NORMAL_CHARGING && pluged != UNCHARGE_LEVEL) {
                return level;
            }
            level += tempTotalBatteryLevel;
            if (!ProjectVariables.isSupportHeat_detection()) {
                return level;
            }
            this.mGet.setIsCharging(true);
            return level;
        }
    }

    private void checkLowBattery(Intent intent) {
        int level = intent.getIntExtra("level", -1);
        if (level != -1 && level <= 5 && !this.mGet.getActivity().isFinishing() && CheckStatusManager.getCheckEnterOutSecure() == 0) {
            CamLog.d(FaceDetector.TAG, "battery level is too low!! go to finish!");
            if (Common.isQuickWindowCameraMode()) {
                if (this.mToast != null) {
                    this.mToast.cancel();
                }
                this.mToast = Toast.makeText(this.mGet.getApplicationContext(), this.mGet.getString(R.string.sp_lowbattery_MLINE), 0);
                this.mToast.setGravity(49, 0, Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.smart_cover_toast_marginTop));
                this.mToast.show();
            } else {
                Common.toast(this.mGet.getApplicationContext(), this.mGet.getString(R.string.sp_lowbattery_MLINE));
            }
            this.mGet.getActivity().finish();
        }
    }

    private void heatWarningByPowerConnection(String action) {
        if (!ProjectVariables.isSupportHeat_detection()) {
            return;
        }
        if (action.equals("android.intent.action.ACTION_POWER_CONNECTED")) {
            this.mGet.setIsCharging(true);
            CamLog.d(FaceDetector.TAG, "===>ACTION_POWER_CONNECTED");
            actionPowerConnected();
        } else if (action.equals("android.intent.action.ACTION_POWER_DISCONNECTED")) {
            this.mGet.setIsCharging(false);
            CamLog.d(FaceDetector.TAG, "===>ACTION_POWER_DISCONNECTED");
            actionPowerDisconnected();
        }
    }

    private void actionPowerDisconnected() {
        if (this.mGet.getApplicationMode() == BATTERY_CHARGING_CURRENT_NORMAL_CHARGING) {
            String RecordingSize = this.mGet.getPreviewSizeOnDevice();
            CamLog.d(FaceDetector.TAG, "===>RecordingSize_2: " + RecordingSize);
            if (Common.IsHeatingVideoSize(RecordingSize)) {
                this.mGet.stopHeatingwarning();
            }
        }
    }

    private void actionPowerConnected() {
        if (this.mGet.getApplicationMode() != BATTERY_CHARGING_CURRENT_NORMAL_CHARGING) {
            return;
        }
        if (this.mGet.getVideoState() == 3 || this.mGet.getVideoState() == BATTERY_CHARGING_CURRENT_USB_DRIVER_UNINSTALLED) {
            String RecordingSize = this.mGet.getPreviewSizeOnDevice();
            CamLog.d(FaceDetector.TAG, "===>RecordingSize_1: " + RecordingSize);
            if (Common.IsHeatingVideoSize(RecordingSize)) {
                this.mGet.startHeatingwarning();
            }
        }
    }

    private int calculateBatteryLevel(int charged) {
        int currentCarrierCode = ModelProperties.getCarrierCode();
        if (charged < 0) {
            charged = 0;
        } else if (charged > 100) {
            charged = 100;
        }
        if (currentCarrierCode == 6) {
            if ((charged >= 21 && charged <= 22) || (charged >= 16 && charged <= 17)) {
                return (charged + BATTERY_CHARGING_CURRENT_USB_DRIVER_UNINSTALLED) / 5;
            }
            if ((charged < 8 || charged > 10) && (charged < 3 || charged > 5)) {
                return (charged + UNCHARGE_LEVEL) / 5;
            }
            return (charged - 1) / 5;
        } else if (currentCarrierCode == 5) {
            if ((charged < 21 || charged > 22) && ((charged < 16 || charged > 17) && (charged < 11 || charged > 12))) {
                return (charged + UNCHARGE_LEVEL) / 5;
            }
            return (charged + BATTERY_CHARGING_CURRENT_USB_DRIVER_UNINSTALLED) / 5;
        } else if (charged < 16 || charged > 17) {
            return (charged + UNCHARGE_LEVEL) / 5;
        } else {
            return (charged + BATTERY_CHARGING_CURRENT_USB_DRIVER_UNINSTALLED) / 5;
        }
    }
}
