package com.lge.camera.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.olaworks.library.FaceDetector;

public class TemperatureReceiver extends CameraBroadCastReceiver {
    public TemperatureReceiver(ReceiverMediatorBridge bridge) {
        super(bridge);
    }

    public void onReceive(Context context, Intent intent) {
        if (ProjectVariables.temperatureCheckMethod() == 1 && ((this.mGet.getVideoState() == 3 || this.mGet.getVideoState() == 4) && intent.getAction().equals(ProjectVariables.ACTION_CAMERA_HIGH_TEMP_WARN))) {
            double mTemp = CheckStatusManager.GetXo_thermal();
            CamLog.d(FaceDetector.TAG, "TemperatureReceiver received intent : " + mTemp);
            String flashState = null;
            if (!(this.mGet.getCameraDevice() == null || this.mGet.getParameters() == null)) {
                flashState = this.mGet.getParameters().getFlashMode();
            }
            if (mTemp < CheckStatusManager.TEMPERATURE_STANDARD) {
                if (ProjectVariables.isUseFlashTemperature() && this.mGet.getVideoState() == 3 && CameraConstants.FLASH_TORCH.equals(flashState) && mTemp >= CheckStatusManager.TEMPERATURE_FLASH_RECORDING_STANDARD && !this.mGet.getActivity().isFinishing()) {
                    this.mGet.setSetting(Setting.KEY_FLASH, CameraConstants.SMART_MODE_OFF);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("fromQuickButton", true);
                    this.mGet.doCommand(Command.CAMERA_FLASH_MODE, null, bundle);
                    this.mGet.setButtonRemainEnabled(10, false, true);
                    this.mGet.toast(R.string.warning_high_temp_action_flash_off);
                    this.mGet.setFlashOffByHighTemperature(true);
                    CamLog.v(FaceDetector.TAG, "flash off by TemperatureReceiver");
                }
                if (this.mGet.isFlashOffByHighTemperature() && mTemp < CheckStatusManager.TEMPERATURE_FLASH_RECORDING_STANDARD) {
                    this.mGet.setButtonRemainEnabled(10, true, true);
                    this.mGet.setFlashOffByHighTemperature(false);
                }
            } else if (CheckStatusManager.getCheckEnterOutSecure() == 0) {
                CamLog.d(FaceDetector.TAG, "finsish CameraApp by high Temperature");
                this.mGet.getActivity().finish();
                this.mGet.postOnUiThread(new Runnable() {
                    public void run() {
                        TemperatureReceiver.this.mGet.removePostRunnable(this);
                        Toast.makeText(TemperatureReceiver.this.mGet.getApplicationContext(), TemperatureReceiver.this.mGet.getApplicationContext().getString(R.string.high_temp_action_on_recording), 1).show();
                    }
                }, 300);
            }
        } else if (ProjectVariables.temperatureCheckMethod() != 3) {
        } else {
            if ((this.mGet.getVideoState() == 3 || this.mGet.getVideoState() == 4) && intent.getAction().equals(ProjectVariables.ACTION_CAMERA_HIGH_TEMP_WARN)) {
                CamLog.d(FaceDetector.TAG, "finsish CameraApp by high Temperature");
                if (CheckStatusManager.getCheckEnterOutSecure() == 0) {
                    this.mGet.getActivity().finish();
                    this.mGet.postOnUiThread(new Runnable() {
                        public void run() {
                            TemperatureReceiver.this.mGet.removePostRunnable(this);
                            Toast.makeText(TemperatureReceiver.this.mGet.getApplicationContext(), TemperatureReceiver.this.mGet.getApplicationContext().getString(R.string.high_temp_action_on_recording), 1).show();
                        }
                    }, 300);
                }
            }
        }
    }
}
