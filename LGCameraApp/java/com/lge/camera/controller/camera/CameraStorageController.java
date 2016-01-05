package com.lge.camera.controller.camera;

import com.lge.camera.ControllerFunction;
import com.lge.camera.controller.StorageController;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.Util;
import com.lge.olaworks.library.FaceDetector;

public class CameraStorageController extends StorageController {
    public CameraStorageController(ControllerFunction function) {
        super(function);
    }

    public void initController() {
        if (!this.mInit) {
            checkStorage(true);
            this.mInit = true;
        }
    }

    public void checkStorage(final boolean showToast) {
        CamLog.i(FaceDetector.TAG, String.format("checkstorage(%b)", new Object[]{Boolean.valueOf(showToast)}));
        boolean mIsHaveEnoughFreeSpace = false;
        long mExternalFreeSpace = -1;
        String state = getCurrentStorageState();
        CamLog.i(FaceDetector.TAG, String.format("external storage state: %s", new Object[]{state}));
        if (isStorageReady(state) && checkFsWritable()) {
            mExternalFreeSpace = getFreeSpace();
            if (mExternalFreeSpace > 1048576) {
                mIsHaveEnoughFreeSpace = true;
            }
            CamLog.d(FaceDetector.TAG, "mExternalFreeSpace=" + mExternalFreeSpace + " mIsHaveEnoughFreeSpace=" + mIsHaveEnoughFreeSpace);
        }
        setStorageState(mIsHaveEnoughFreeSpace, mExternalFreeSpace, state);
        FileNamer.get().setStorageState(this.mGet.getApplicationContext(), this.mGet.getApplicationMode(), this.mGet.getCurrentStorage(), this.mGet.getCurrentStorageDirectory(), this.mStorageState);
        if (this.mInit && !this.mGet.isPausing()) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    CameraStorageController.this.mGet.removePostRunnable(this);
                    if (CameraStorageController.this.mStorageState == 3 || CameraStorageController.this.mStorageState == 1) {
                        CameraStorageController.this.mGet.setMainButtonDisable();
                    } else if (!(CameraStorageController.this.mGet.getInCaptureProgress() || CameraStorageController.this.mGet.isTimerShotCountdown())) {
                        CameraStorageController.this.mGet.setMainButtonEnable(CameraConstants.STORAGECONTROLLER_LOCKKEY);
                    }
                    CameraStorageController.this.mGet.updateRemainIndicator();
                    if (showToast || CameraStorageController.this.mStorageState == 1) {
                        CameraStorageController.this.showStorageHint(CameraStorageController.this.mStorageState);
                    }
                }
            });
        }
    }

    private void setStorageState(boolean mIsHaveEnoughFreeSpace, long mExternalFreeSpace, String state) {
        long pictureRemaining = 0;
        if (mIsHaveEnoughFreeSpace) {
            String sizeString = this.mGet.getSettingValue(Setting.KEY_CAMERA_PICTURESIZE);
            int[] size = null;
            if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CONTINUOUS) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
                size = Util.SizeString2WidthHeight(this.mGet.findPreference(Setting.KEY_CAMERA_SHOT_MODE).getExtraInfo());
            } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
                size = this.mGet.getPlanePanoramaResultSize();
            } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
                size = this.mGet.getFreePanoramaResultSize();
            } else if (!this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
                size = Util.SizeString2WidthHeight(sizeString);
            } else if (this.mGet.getDualCameraPictureSize() != null) {
                size = this.mGet.getDualCameraPictureSize();
            }
            if (size != null) {
                pictureRemaining = calculateRemainPictureCount(size[0], size[1], mExternalFreeSpace);
            }
            if (isMediaScanning()) {
                this.mStorageState = 3;
            } else if (pictureRemaining > 0) {
                this.mStorageState = 0;
            } else {
                pictureRemaining = 0;
                this.mStorageState = 2;
            }
        } else if (mExternalFreeSpace == -1) {
            pictureRemaining = -1;
            if (isStorageReadOnly(state)) {
                this.mStorageState = 1;
            } else {
                this.mStorageState = 1;
            }
        } else {
            pictureRemaining = 0;
            this.mStorageState = 2;
        }
        this.mGet.setPicturesRemaining(pictureRemaining);
    }

    private long calculateAvailablePictureCountInTargetStorage(int storageType) {
        long freeSpace;
        CamLog.i(FaceDetector.TAG, String.format("calculate Available PictureCount In TargetStorage(%d)", new Object[]{Integer.valueOf(storageType)}));
        boolean isHaveEnoughFreeSpace = false;
        String state = getStorageState(storageType);
        CamLog.i(FaceDetector.TAG, String.format("storage(%d) state: %s", new Object[]{Integer.valueOf(storageType), state}));
        if (isStorageReady(state)) {
            if (checkFsWritable(storageType)) {
                freeSpace = getFreeSpace(storageType);
                if (freeSpace > 1048576) {
                    isHaveEnoughFreeSpace = true;
                }
            } else {
                freeSpace = -1;
            }
            CamLog.d(FaceDetector.TAG, "freeSpace=" + freeSpace + " isHaveEnoughFreeSpace=" + isHaveEnoughFreeSpace);
        } else {
            freeSpace = -1;
        }
        if (isHaveEnoughFreeSpace) {
            int[] size;
            String sizeString = this.mGet.getSettingValue(Setting.KEY_CAMERA_PICTURESIZE);
            if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CONTINUOUS) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
                size = Util.SizeString2WidthHeight(this.mGet.findPreference(Setting.KEY_CAMERA_SHOT_MODE).getExtraInfo());
            } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
                size = this.mGet.getFreePanoramaResultSize();
            } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
                size = this.mGet.getPlanePanoramaResultSize();
            } else {
                size = Util.SizeString2WidthHeight(sizeString);
            }
            long pictureRemaining = calculateRemainPictureCount(size[0], size[1], freeSpace);
            if (pictureRemaining < 0) {
                return 0;
            }
            return pictureRemaining;
        } else if (freeSpace == -1) {
            return -1;
        } else {
            return 0;
        }
    }

    private double getAverageSpace(int width, int height) {
        return (((double) (width * height)) * 0.3d) * Util.getPictureSizeScale(this.mGet.getCameraId(), this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), ModelProperties.getProjectCode(), this.mGet.getSettingValue(Setting.KEY_CAMERA_PICTURESIZE));
    }

    public long getAvailablePictureCount() {
        return this.mGet.getPicturesRemaining();
    }

    public long getAvailablePictureCount(int storageType) {
        if (storageType == -1) {
            return this.mGet.getPicturesRemaining();
        }
        return calculateAvailablePictureCountInTargetStorage(storageType);
    }

    public void onResume() {
        if (this.mInit) {
            checkStorage(false);
        }
        super.onResume();
    }

    public long calculateRemainPictureCount(int width, int height, long freeSpace) {
        long remain = (long) Math.floor(((double) (freeSpace - 1048576)) / getAverageSpace(width, height));
        CamLog.d(FaceDetector.TAG, String.format("picture count remained : %s", new Object[]{Long.valueOf(remain)}));
        return remain;
    }

    public boolean isEnoughWorkingStorage(int storageType) {
        return getAvailablePictureCount(storageType) > 0;
    }
}
