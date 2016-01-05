package com.lge.camera.properties;

import android.os.Environment;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.os.storage.IMountService.Stub;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import java.lang.reflect.Method;

public final class StorageProperties {
    public static String getEmmcName() {
        return CameraConstants.NAME_INTERNAL_STORAGE;
    }

    public static boolean isEMMCmemory() {
        if (Environment.isExternalStorageRemovable()) {
            return false;
        }
        return true;
    }

    public static boolean isInternalMemoryOnly() {
        if (getNoOfStorageVolumes() == 1 && isEMMCmemory()) {
            return true;
        }
        return false;
    }

    public static boolean isExternalMemoryOnly() {
        if (getNoOfStorageVolumes() != 1 || isEMMCmemory()) {
            return false;
        }
        return true;
    }

    public static boolean isAllMemorySupported() {
        return getNoOfStorageVolumes() > 1;
    }

    public static int getNoOfStorageVolumes() {
        int volumeCount = 1;
        try {
            IBinder svc = ServiceManager.getService("mount");
            if (svc != null) {
                IMountService mountService = Stub.asInterface(svc);
                if (mountService != null) {
                    Method md = mountService.getClass().getMethod("getVolumeList", (Class[]) null);
                    if (md != null) {
                        Object[] ob = (Object[]) md.invoke(mountService, new Object[0]);
                        if (ob != null) {
                            volumeCount = ob.length;
                        }
                    }
                }
            }
        } catch (Exception rex) {
            CamLog.e(FaceDetector.TAG, "Exception : ", rex);
        }
        if (volumeCount > 1 && System.getenv("EXTERNAL_ADD_STORAGE") == null) {
            return 1;
        }
        CamLog.d(FaceDetector.TAG, "Number of volumes = " + volumeCount);
        return volumeCount;
    }
}
