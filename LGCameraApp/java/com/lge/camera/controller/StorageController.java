package com.lge.camera.controller;

import android.os.Environment;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.StatFs;
import android.os.storage.IMountService;
import android.os.storage.IMountService.Stub;
import android.os.storage.StorageVolume;
import android.widget.Toast;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.properties.StorageProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.io.File;
import java.lang.reflect.Method;

public abstract class StorageController extends Controller {
    private static IMountService mMntSvc;
    public static String sDCF_DIRECTORY;
    public static String sGUEST_DIRECTORY;
    public static String sNORMAL_DIRECTORY;
    public static String sTIMEMACHINE_DIRECTORY;
    public String EXTERNAL_STORAGE_DIR;
    public String INTERNAL_STORAGE_DIR;
    protected int mCurrentStorage;
    private String mCurrentStorageDCFDirectory;
    private String mCurrentStorageDirectory;
    private String mCurrentStorageState;
    private boolean mMediaScanning;
    protected int mStorageState;
    private Toast mToast;
    private int messageId;

    public abstract void checkStorage(boolean z);

    public abstract void initController();

    public abstract boolean isEnoughWorkingStorage(int i);

    static {
        mMntSvc = null;
        sDCF_DIRECTORY = "/DCIM/100LGDSC/";
        sNORMAL_DIRECTORY = "/DCIM/Camera/";
        sTIMEMACHINE_DIRECTORY = "/DCIM/.thumbnails/";
        sGUEST_DIRECTORY = "/DCIM/Guest album/";
    }

    public StorageController(ControllerFunction function) {
        super(function);
        this.mCurrentStorage = 0;
        this.EXTERNAL_STORAGE_DIR = GetStoragePath(true);
        this.INTERNAL_STORAGE_DIR = GetStoragePath(false);
        this.mCurrentStorageDirectory = this.EXTERNAL_STORAGE_DIR + sNORMAL_DIRECTORY;
        this.mCurrentStorageDCFDirectory = this.INTERNAL_STORAGE_DIR + sDCF_DIRECTORY;
        this.mCurrentStorageState = Environment.getExternalStorageState();
        this.mToast = null;
        this.mMediaScanning = false;
        this.messageId = 1;
        if (!StorageProperties.isExternalMemoryOnly()) {
            setCurrentStorage(1);
        }
    }

    public long getAvailablePictureCount() {
        return 0;
    }

    public void checkStorage() {
        if (!this.mGet.isPausing()) {
            this.mGet.doCommandDelayed(Command.UPDATE_THUMBNAIL_BUTTON, 200);
        }
        checkStorage(true);
    }

    public int getStorageState() {
        return this.mStorageState;
    }

    public String getStorageState(int storageType) {
        switch (storageType) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                return getExternalAddtionalStorageState();
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                return Environment.getExternalStorageState();
            default:
                return getCurrentStorageState();
        }
    }

    public boolean isStorageReady(String storageState) {
        if (storageState.equals("bad_removal") || storageState.equals("removed") || storageState.equals("unmounted") || storageState.equals("shared") || storageState.equals("unmountable") || storageState.equals("mounted_ro") || storageState.equals(MultimediaProperties.MEDIA_EJECT)) {
            return false;
        }
        return true;
    }

    public void showStorageHint(int storageState) {
        CamLog.d(FaceDetector.TAG, "**showStorageHint" + storageState);
        int oldStorageState = this.mStorageState;
        this.mStorageState = storageState;
        if (this.mGet.getCurrentDialog() != null && this.mGet.getDialogID() == 1) {
            this.mGet.getCurrentDialog().dismiss();
        }
        switch (storageState) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                CamLog.d(FaceDetector.TAG, "***** storage available");
                showStorageHintForAvailable();
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                CamLog.d(FaceDetector.TAG, "***** storage not found");
                showStorageHintForNotFound(oldStorageState);
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                CamLog.d(FaceDetector.TAG, "***** storage full");
                showStorageHintForFull();
            case LGKeyRec.EVENT_STARTED /*3*/:
                CamLog.d(FaceDetector.TAG, "***** storage scanning");
                showStorageHintForScanning();
            default:
                this.mGet.storageToastShow(this.mGet.getString(R.string.sp_external_sd_prepared_NORMAL), false, true);
                this.mGet.setMainButtonEnable();
        }
    }

    private void showStorageHintForAvailable() {
        this.mGet.storageToasthide(false);
        if (!this.mGet.isStorageToastShowing()) {
            switch (this.mCurrentStorage) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    this.mGet.storageToastShow(this.mGet.getString(R.string.sp_external_sd_prepared_NORMAL), false, true);
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    this.mGet.storageToastShow(this.mGet.getString(R.string.sp_internal_sd_prepared_NORMAL), false, true);
                default:
            }
        }
    }

    private void showStorageHintForScanning() {
        this.mGet.storageToasthide(false);
        if (!this.mGet.isStorageToastShowing()) {
            this.mGet.storageToastShow(this.mGet.getString(R.string.preparing_sd), false, false);
        }
    }

    private void showStorageHintForFull() {
        this.mGet.storageToasthide(false);
        this.mGet.deleteProgressDialog();
        if (Common.isQuickWindowCameraMode()) {
            if (this.mToast != null) {
                this.mToast.cancel();
            }
            this.mToast = Toast.makeText(this.mGet.getActivity(), R.string.sp_storage_full_popup_ics_title_NORMAL, 0);
            this.mToast.setGravity(49, 0, getPixelFromDimens(R.dimen.smart_cover_toast_marginTop));
            this.mToast.show();
        } else {
            this.mGet.showDialogPopup(14);
        }
        this.mGet.enableCommand(true);
    }

    private void showStorageHintForNotFound(int oldStorageState) {
        if (oldStorageState != this.mStorageState && this.mGet.isStorageToastShowing()) {
            this.mGet.storageToasthide(false);
        }
        if (this.mGet.isAttachIntent()) {
            this.mGet.findViewById(R.id.main_button).setEnabled(true);
        } else {
            this.mGet.enableCommand(true);
        }
        if (!this.mGet.isStorageToastShowing()) {
            if (getCurrentStorageState().equals("shared")) {
                if (this.mGet.getDialogID() != 17) {
                    if (this.mGet.getCurrentDialog() != null) {
                        this.mGet.getCurrentDialog().dismiss();
                    }
                    this.mGet.showDialogPopup(17);
                }
            } else if (isStorageReadOnly(getCurrentStorageState())) {
                this.mGet.storageToastShow(this.mGet.getString(R.string.sp_sd_card_read_only_NORMAL), false, false);
            } else {
                this.mGet.storageToastShow(this.mGet.getString(R.string.insert_add_sd_card), false, false);
            }
        }
    }

    public boolean isMediaScanning() {
        return this.mMediaScanning;
    }

    public void setMediaScanning(boolean scanning) {
        this.mMediaScanning = scanning;
    }

    public long getFreeSpace() {
        return getFreeSpace(-1);
    }

    public long getFreeSpace(int storageType) {
        try {
            String strStorageDirectory = getTargetStorageDirectory(storageType);
            File file = new File(strStorageDirectory);
            if (!file.exists()) {
                file.mkdirs();
            }
            StatFs stat = new StatFs(strStorageDirectory);
            long freeSpace = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
            if (getCurrentStorage() == 1 || StorageProperties.isInternalMemoryOnly()) {
                freeSpace -= CameraConstants.INTERNAL_MEMORY_SAFE_FREE_SPACE;
                if (freeSpace < 0) {
                    freeSpace = 0;
                }
            }
            return freeSpace;
        } catch (RuntimeException ex) {
            CamLog.e(FaceDetector.TAG, " error :" + ex.getMessage());
            return -2;
        }
    }

    public boolean checkFsWritable() {
        File directory = new File(getCurrentStorageDirectory().split(getSaveDirectory())[0]);
        CamLog.d(FaceDetector.TAG, "checkFsWritable : " + directory.canWrite());
        return directory.canWrite();
    }

    public boolean checkFsWritable(int storageType) {
        File directory = new File(getTargetStorageDirectory(storageType).split(getSaveDirectory())[0]);
        CamLog.d(FaceDetector.TAG, "checkFsWritable : " + directory.canWrite() + " storageType : " + storageType);
        return directory.canWrite();
    }

    public void setMenuEnable(boolean enable) {
        this.mGet.setCurrentSettingMenuEnable(Setting.KEY_STORAGE, enable);
    }

    public boolean isExternalStorageRemoved() {
        String state = "removed";
        if (StorageProperties.isAllMemorySupported()) {
            state = getExternalAddtionalStorageState();
        } else {
            state = Environment.getExternalStorageState();
        }
        CamLog.d(FaceDetector.TAG, String.format("SD card state:%s", new Object[]{state}));
        if (state.equals("mounted")) {
            return false;
        }
        return true;
    }

    public boolean isStorageAvailable() {
        checkStorage(false);
        if (this.mStorageState == 0) {
            return true;
        }
        return false;
    }

    public boolean isStorageFull() {
        checkStorage(false);
        if (this.mStorageState == 2) {
            return true;
        }
        return false;
    }

    public boolean isStorageFull(int storageType) {
        if (this.mStorageState == 2) {
            return true;
        }
        return false;
    }

    public static String getSaveDirectory() {
        String dir = sNORMAL_DIRECTORY;
        if (ProjectVariables.getUseDCFRule()) {
            dir = sDCF_DIRECTORY;
        }
        if (AppControlUtil.isGuestMode()) {
            return sGUEST_DIRECTORY;
        }
        return dir;
    }

    public String getCurrentStorageDirectory() {
        if (!StorageProperties.isAllMemorySupported()) {
            this.mCurrentStorageDirectory = this.EXTERNAL_STORAGE_DIR + getSaveDirectory();
            return this.mCurrentStorageDirectory;
        } else if (ProjectVariables.getUseDCFRule()) {
            return this.mCurrentStorageDCFDirectory;
        } else {
            return this.mCurrentStorageDirectory;
        }
    }

    public String getTimeMachineStorageDirectory() {
        if (!StorageProperties.isAllMemorySupported()) {
            return this.EXTERNAL_STORAGE_DIR + sTIMEMACHINE_DIRECTORY;
        }
        if (this.mCurrentStorage == 1) {
            return this.INTERNAL_STORAGE_DIR + sTIMEMACHINE_DIRECTORY;
        }
        return this.EXTERNAL_STORAGE_DIR + sTIMEMACHINE_DIRECTORY;
    }

    public String getTargetStorageDirectory(int storageType) {
        if (!StorageProperties.isAllMemorySupported()) {
            return getCurrentStorageDirectory();
        }
        switch (storageType) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                return this.EXTERNAL_STORAGE_DIR + getSaveDirectory();
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                return this.INTERNAL_STORAGE_DIR + getSaveDirectory();
            default:
                return getCurrentStorageDirectory();
        }
    }

    public int getCurrentStorage() {
        if (StorageProperties.isExternalMemoryOnly()) {
            return 0;
        }
        return this.mCurrentStorage;
    }

    private String getExternalAddtionalStorageState() {
        String state = "removed";
        try {
            if (mMntSvc == null) {
                mMntSvc = Stub.asInterface(ServiceManager.getService("mount"));
            }
            if (mMntSvc != null) {
                return mMntSvc.getVolumeState(this.EXTERNAL_STORAGE_DIR);
            }
            return state;
        } catch (Exception rex) {
            state = "removed";
            CamLog.e(FaceDetector.TAG, "Exception : ", rex);
            return state;
        }
    }

    public void setCurrentStorage(int storageType) {
        this.mCurrentStorage = storageType;
        if (!StorageProperties.isAllMemorySupported()) {
            this.mCurrentStorageDirectory = this.EXTERNAL_STORAGE_DIR + getSaveDirectory();
            this.mCurrentStorageDCFDirectory = this.EXTERNAL_STORAGE_DIR + getSaveDirectory();
            this.mCurrentStorageState = Environment.getExternalStorageState();
        } else if (storageType == 1) {
            this.mCurrentStorageDirectory = this.INTERNAL_STORAGE_DIR + getSaveDirectory();
            this.mCurrentStorageDCFDirectory = this.INTERNAL_STORAGE_DIR + getSaveDirectory();
            this.mCurrentStorageState = Environment.getExternalStorageState();
        } else {
            this.mCurrentStorageDirectory = this.EXTERNAL_STORAGE_DIR + getSaveDirectory();
            this.mCurrentStorageDCFDirectory = this.EXTERNAL_STORAGE_DIR + getSaveDirectory();
            this.mCurrentStorageState = getExternalAddtionalStorageState();
        }
    }

    public String getCurrentStorageState() {
        if (StorageProperties.isAllMemorySupported()) {
            return this.mCurrentStorageState;
        }
        this.mCurrentStorageState = Environment.getExternalStorageState();
        return this.mCurrentStorageState;
    }

    public String getBucketId() {
        String bucket;
        if (ProjectVariables.getUseDCFRule()) {
            bucket = this.mCurrentStorageDCFDirectory.substring(0, this.mCurrentStorageDCFDirectory.length() - 1);
        } else {
            bucket = this.mCurrentStorageDirectory.substring(0, this.mCurrentStorageDirectory.length() - 1);
        }
        return String.valueOf(bucket.toLowerCase().hashCode());
    }

    public String getBucketId(int storageType) {
        String storageDirectory;
        String storageDcfDirectory;
        String bucket;
        if (StorageProperties.isExternalMemoryOnly()) {
            storageDirectory = this.EXTERNAL_STORAGE_DIR + getSaveDirectory();
            storageDcfDirectory = this.EXTERNAL_STORAGE_DIR + getSaveDirectory();
        } else if (storageType == 1) {
            storageDirectory = this.INTERNAL_STORAGE_DIR + getSaveDirectory();
            storageDcfDirectory = this.INTERNAL_STORAGE_DIR + getSaveDirectory();
        } else {
            storageDirectory = this.EXTERNAL_STORAGE_DIR + getSaveDirectory();
            storageDcfDirectory = this.EXTERNAL_STORAGE_DIR + getSaveDirectory();
        }
        if (ProjectVariables.getUseDCFRule()) {
            bucket = storageDcfDirectory.substring(0, storageDcfDirectory.length() - 1);
        } else {
            bucket = storageDirectory.substring(0, storageDirectory.length() - 1);
        }
        return String.valueOf(bucket.toLowerCase().hashCode());
    }

    public boolean isStorageReadOnly(String state) {
        return state.equals("mounted_ro") || (state.equals("mounted") && !checkFsWritable());
    }

    public String setStorageInitForFileNamingHelper() {
        int mStorage;
        if (StorageProperties.isAllMemorySupported() && isExternalStorageRemoved()) {
            mStorage = 1;
        } else if (StorageProperties.getEmmcName().equals(this.mGet.getSettingValue(Setting.KEY_STORAGE)) || StorageProperties.isInternalMemoryOnly()) {
            mStorage = 1;
        } else {
            mStorage = 0;
        }
        setCurrentStorage(mStorage);
        return getCurrentStorageDirectory();
    }

    public String getExternalStorageDir() {
        return this.EXTERNAL_STORAGE_DIR;
    }

    public String GetStoragePath(boolean storagetype) {
        if (!checkMediator()) {
            return null;
        }
        try {
            IBinder service = ServiceManager.getService("mount");
            if (service != null) {
                IMountService mountService = Stub.asInterface(service);
                if (mountService != null) {
                    Method md = mountService.getClass().getMethod("getVolumeList", (Class[]) null);
                    if (md != null) {
                        StorageVolume[] volumeList = (StorageVolume[]) md.invoke(mountService, new Object[0]);
                        if (volumeList == null || volumeList.length < 1) {
                            return "/mnt/sdcard";
                        }
                        String path = volumeList[0].getPath();
                        if (!StorageProperties.isAllMemorySupported()) {
                            return path;
                        }
                        for (int i = 0; i < volumeList.length; i++) {
                            CamLog.d(FaceDetector.TAG, "Storage info : " + volumeList[i].getPath() + " R :  " + volumeList[i].isRemovable());
                            if (volumeList[i].isRemovable() == storagetype) {
                                return volumeList[i].getPath();
                            }
                        }
                        return path;
                    }
                }
            }
        } catch (Exception rex) {
            CamLog.e(FaceDetector.TAG, "Exception : ", rex);
        }
        CamLog.d(FaceDetector.TAG, "Storage Path is Null");
        return null;
    }

    public int getMessageId() {
        return this.messageId;
    }

    public String getMessage() {
        CamLog.i(FaceDetector.TAG, "getMessage");
        if (StorageProperties.isAllMemorySupported()) {
            int anotherStorage;
            int currentStorage = getCurrentStorage();
            CamLog.i(FaceDetector.TAG, "getMessage currentStorage=" + currentStorage);
            if (currentStorage == 1) {
                anotherStorage = 0;
            } else {
                anotherStorage = 1;
            }
            if (!isStorageReady(getStorageState(anotherStorage))) {
                this.messageId = 1;
                return getMessageType1(currentStorage, anotherStorage);
            } else if (isEnoughWorkingStorage(anotherStorage)) {
                this.messageId = 2;
                return getMessageType2(currentStorage, anotherStorage);
            } else {
                this.messageId = 3;
                return this.mGet.getString(R.string.sp_storage_full_popup_ics_3_NORMAL);
            }
        }
        this.messageId = 1;
        return getMessageType1(getCurrentStorage(), -1);
    }

    private String getMessageType1(int currentStorage, int anotherStorage) {
        if (currentStorage != 1) {
            return this.mGet.getString(R.string.sp_full_popup_ics_1_not_sd_NORMAL);
        }
        if (StorageProperties.getEmmcName().equals(CameraConstants.NAME_INTERNAL_MEMORY)) {
            return this.mGet.getString(R.string.sp_full_popup_ics_1_not_in_memory_NORMAL);
        }
        return this.mGet.getString(R.string.sp_full_popup_ics_1_not_in_storage_NORMAL);
    }

    private String getMessageType2(int currentStorage, int anotherStorage) {
        if (currentStorage == 1) {
            if (StorageProperties.getEmmcName().equals(CameraConstants.NAME_INTERNAL_MEMORY)) {
                return this.mGet.getString(R.string.sp_full_popup_ics_2_not_in_memory_to_sd_NORMAL);
            }
            return this.mGet.getString(R.string.sp_full_popup_ics_2_not_in_storage_to_sd_NORMAL);
        } else if (StorageProperties.getEmmcName().equals(CameraConstants.NAME_INTERNAL_MEMORY)) {
            return this.mGet.getString(R.string.sp_full_popup_ics_2_not_sd_to_in_memory_NORMAL);
        } else {
            return this.mGet.getString(R.string.sp_full_popup_ics_2_not_sd_to_in_storage_NORMAL);
        }
    }
}
