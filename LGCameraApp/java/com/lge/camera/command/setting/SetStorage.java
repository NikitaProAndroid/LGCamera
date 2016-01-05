package com.lge.camera.command.setting;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.command.Command;
import com.lge.camera.properties.StorageProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.FileNamer;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetStorage extends SettingCommand {
    public SetStorage(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        boolean allSetting = ((Bundle) arg).getBoolean("allSetting", false);
        CamLog.d(FaceDetector.TAG, "set storage");
        if (checkMediator()) {
            this.mGet.storageToasthide(true);
            if (this.mGet.isExternalStorageRemoved()) {
                this.mGet.setCurrentStorage(1);
                if (StorageProperties.isAllMemorySupported()) {
                    this.mGet.setSetting(Setting.KEY_STORAGE, StorageProperties.getEmmcName());
                }
                this.mGet.setPreferenceMenuEnable(Setting.KEY_STORAGE, false, false);
            } else {
                if (StorageProperties.getEmmcName().equals(this.mGet.getSettingValue(Setting.KEY_STORAGE)) || StorageProperties.isInternalMemoryOnly()) {
                    CamLog.d(FaceDetector.TAG, "storage is internal storage");
                    this.mGet.setCurrentStorage(1);
                } else {
                    CamLog.d(FaceDetector.TAG, "storage is external storage");
                    this.mGet.setCurrentStorage(0);
                }
                this.mGet.setPreferenceMenuEnable(Setting.KEY_STORAGE, true, false);
            }
            updateIndicator();
            if (!allSetting) {
                this.mGet.doCommandDelayed(Command.UPDATE_THUMBNAIL_BUTTON, 200);
                FileNamer.get().reload(this.mGet.getApplicationContext(), this.mGet.getApplicationMode(), this.mGet.getCurrentStorage(), this.mGet.getCurrentStorageDirectory(), true);
                return;
            }
            return;
        }
        CamLog.d(FaceDetector.TAG, "return : getMediator() is null.");
    }

    private void updateIndicator() {
        if (checkMediator() && this.mGet.isIndicatorControllerInitialized()) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    SetStorage.this.mGet.removePostRunnable(this);
                    SetStorage.this.mGet.checkStorage(false);
                    SetStorage.this.mGet.updateStorageIndicator();
                }
            });
        }
    }

    protected void onExecuteAlone() {
        if (checkMediator()) {
            this.mGet.allSettingMenuSelectedChild(Setting.KEY_STORAGE, true);
        }
    }
}
