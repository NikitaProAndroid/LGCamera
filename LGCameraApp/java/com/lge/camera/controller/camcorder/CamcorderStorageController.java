package com.lge.camera.controller.camcorder;

import com.lge.camera.ControllerFunction;
import com.lge.camera.controller.StorageController;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.Util;
import com.lge.olaworks.library.FaceDetector;

public class CamcorderStorageController extends StorageController {
    public CamcorderStorageController(ControllerFunction function) {
        super(function);
    }

    public void initController() {
        if (!this.mInit) {
            checkStorage(true);
            this.mInit = true;
        }
    }

    public void checkStorage(boolean showToast) {
        CamLog.d(FaceDetector.TAG, String.format("checkstorage(%b)", new Object[]{Boolean.valueOf(showToast)}));
        boolean mIsHaveEnoughFreeSpace = false;
        long mExternalFreeSpace = -1;
        String state = getCurrentStorageState();
        int oldStorageState = this.mStorageState;
        CamLog.d(FaceDetector.TAG, String.format("external storage state: %s", new Object[]{state}));
        if (isStorageReady(state) && checkFsWritable()) {
            mExternalFreeSpace = getFreeSpace();
            if (mExternalFreeSpace > CameraConstants.VIDEO_LOW_STORAGE_THRESHOLD + CameraConstants.VIDEO_SAFE_MAX_FILE_SIZE_DAMPER) {
                mIsHaveEnoughFreeSpace = true;
            }
            CamLog.d(FaceDetector.TAG, "mExternalFreeSpace=" + mExternalFreeSpace + " mIsHaveEnoughFreeSpace=" + mIsHaveEnoughFreeSpace);
        }
        setStorageState(mIsHaveEnoughFreeSpace, mExternalFreeSpace, state);
        FileNamer.get().setStorageState(this.mGet.getApplicationContext(), this.mGet.getApplicationMode(), this.mGet.getCurrentStorage(), this.mGet.getCurrentStorageDirectory(), this.mStorageState);
        if (this.mInit && !this.mGet.isPausing()) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    CamcorderStorageController.this.mGet.removePostRunnable(this);
                    if (CamcorderStorageController.this.mStorageState == 3 || CamcorderStorageController.this.mStorageState == 1) {
                        CamcorderStorageController.this.mGet.setMainButtonDisable();
                    } else {
                        CamcorderStorageController.this.mGet.setMainButtonEnable(CameraConstants.STORAGECONTROLLER_LOCKKEY);
                    }
                }
            });
            CamLog.d(FaceDetector.TAG, String.format("mStorageState: %d", new Object[]{Integer.valueOf(this.mStorageState)}));
            CamLog.d(FaceDetector.TAG, String.format("show hint:%b, state %d -> %d", new Object[]{Boolean.valueOf(showToast), Integer.valueOf(oldStorageState), Integer.valueOf(this.mStorageState)}));
            if (showToast || this.mStorageState == 1) {
                showStorageHint(this.mStorageState);
            }
        }
    }

    private void setStorageState(boolean mIsHaveEnoughFreeSpace, long mExternalFreeSpace, String state) {
        if (mIsHaveEnoughFreeSpace) {
            if (isMediaScanning()) {
                this.mStorageState = 3;
                return;
            }
            int[] size = Util.SizeString2WidthHeight(this.mGet.getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE));
            if (mExternalFreeSpace > (getAverageSpace(size[0], size[1]) + CameraConstants.VIDEO_LOW_STORAGE_THRESHOLD) + CameraConstants.VIDEO_SAFE_MAX_FILE_SIZE_DAMPER) {
                this.mStorageState = 0;
            } else {
                this.mStorageState = 2;
            }
        } else if (mExternalFreeSpace != -1) {
            this.mStorageState = 2;
        } else if (isStorageReadOnly(state)) {
            this.mStorageState = 1;
        } else {
            this.mStorageState = 1;
        }
    }

    private long getAverageSpace(int width, int height) {
        long resolution = (long) (width * height);
        if (resolution >= 2088960) {
            return 3 * resolution;
        }
        if (resolution >= 307200) {
            return 4 * resolution;
        }
        if (resolution >= 76800) {
            return 13 * resolution;
        }
        return 35 * resolution;
    }

    public void checkStorage() {
        checkStorage(true);
    }

    public void onResume() {
        if (this.mInit) {
            checkStorage(false);
        }
        super.onResume();
    }

    public boolean isEnoughWorkingStorage(int storageType) {
        long freeSpace;
        CamLog.i(FaceDetector.TAG, String.format("isEnoughWorkingStorage(%d)", new Object[]{Integer.valueOf(storageType)}));
        boolean isHaveEnoughFreeSpace = false;
        String state = getStorageState(storageType);
        CamLog.i(FaceDetector.TAG, String.format("storage(%d) state: %s", new Object[]{Integer.valueOf(storageType), state}));
        if (isStorageReady(state)) {
            if (checkFsWritable(storageType)) {
                freeSpace = getFreeSpace(storageType);
                if (freeSpace > CameraConstants.VIDEO_LOW_STORAGE_THRESHOLD + CameraConstants.VIDEO_SAFE_MAX_FILE_SIZE_DAMPER) {
                    isHaveEnoughFreeSpace = true;
                }
            } else {
                freeSpace = -1;
            }
            CamLog.d(FaceDetector.TAG, "freeSpace=" + freeSpace + " isHaveEnoughFreeSpace=" + isHaveEnoughFreeSpace);
        } else {
            freeSpace = -1;
        }
        if (!isHaveEnoughFreeSpace) {
            return false;
        }
        int[] size = Util.SizeString2WidthHeight(this.mGet.getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE));
        if (freeSpace > (getAverageSpace(size[0], size[1]) + CameraConstants.VIDEO_LOW_STORAGE_THRESHOLD) + CameraConstants.VIDEO_SAFE_MAX_FILE_SIZE_DAMPER) {
            return true;
        }
        return false;
    }
}
