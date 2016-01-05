package com.lge.camera.command.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings.Secure;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.hardware.LGCamera.LGParameters;

public class SetCameraGeoTag extends SettingCommand {
    public SetCameraGeoTag(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        boolean locationOn;
        boolean allSetting = ((Bundle) arg).getBoolean("allSetting", false);
        boolean locationOnGps = Secure.isLocationProviderEnabled(this.mGet.getContentResolver(), "gps");
        boolean locationOnNetwork = Secure.isLocationProviderEnabled(this.mGet.getContentResolver(), "network");
        if (locationOnGps || locationOnNetwork) {
            locationOn = true;
        } else {
            locationOn = false;
        }
        this.mGet.setLocationOn(locationOn);
        String onOff = this.mGet.getSettingValue(Setting.KEY_CAMERA_TAG_LOCATION);
        if (this.mGet.getLocationOn() || !onOff.equals(CameraConstants.SMART_MODE_ON) || allSetting) {
            reSetting(locationOn);
            if (this.mGet.getSettingValue(Setting.KEY_CAMERA_TAG_LOCATION).equals(CameraConstants.SMART_MODE_OFF)) {
                this.mGet.setRecordLocation(false);
            } else {
                this.mGet.setRecordLocation(true);
            }
            this.mGet.stopReceivingLocationUpdates();
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    SetCameraGeoTag.this.mGet.removePostRunnable(this);
                    if (SetCameraGeoTag.this.mGet.getRecordLocation() && !SetCameraGeoTag.this.mGet.isPausing() && !SetCameraGeoTag.this.mGet.isFinishingActivity()) {
                        SetCameraGeoTag.this.mGet.startReceivingLocationUpdates();
                    }
                }
            });
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    SetCameraGeoTag.this.mGet.removePostRunnable(this);
                    SetCameraGeoTag.this.mGet.updateGpsIndicator();
                }
            });
            return;
        }
        reSetting(locationOn);
        if (ProjectVariables.beSupportEulaPopup()) {
            SharedPreferences pref = this.mGet.getActivity().getSharedPreferences(CameraConstants.EULA_PREFERENCE_NAME, 0);
            if (!(pref == null || pref.getBoolean(CameraConstants.EULA_PREFERENCE_VALUE, false))) {
                this.mGet.showDialogPopup(23);
                return;
            }
        }
        this.mGet.showDialogPopup(10);
    }

    private void reSetting(boolean locationOn) {
        if (this.mGet.getOpenLBSSetting()) {
            if (locationOn) {
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        SetCameraGeoTag.this.mGet.removePostRunnable(this);
                        SetCameraGeoTag.this.mGet.setSetting(Setting.KEY_CAMERA_TAG_LOCATION, CameraConstants.SMART_MODE_ON);
                        SetCameraGeoTag.this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_TAG_LOCATION, true, true);
                    }
                });
            } else {
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        SetCameraGeoTag.this.mGet.removePostRunnable(this);
                        SetCameraGeoTag.this.mGet.setSetting(Setting.KEY_CAMERA_TAG_LOCATION, CameraConstants.SMART_MODE_OFF);
                        SetCameraGeoTag.this.mGet.updateGpsIndicator();
                        SetCameraGeoTag.this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_TAG_LOCATION, true, true);
                    }
                });
            }
            this.mGet.setOpenLBSSetting(false);
        } else if (locationOn) {
            this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_TAG_LOCATION, true, true);
        } else if (!this.mGet.getSettingValue(Setting.KEY_CAMERA_TAG_LOCATION).equals(CameraConstants.SMART_MODE_OFF)) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    SetCameraGeoTag.this.mGet.removePostRunnable(this);
                    SetCameraGeoTag.this.mGet.setSetting(Setting.KEY_CAMERA_TAG_LOCATION, CameraConstants.SMART_MODE_OFF);
                    SetCameraGeoTag.this.mGet.updateGpsIndicator();
                    SetCameraGeoTag.this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_TAG_LOCATION, true, true);
                }
            });
        }
    }

    protected void onExecuteAlone() {
        this.mGet.allSettingMenuSelectedChild(Setting.KEY_CAMERA_TAG_LOCATION, false);
    }
}
