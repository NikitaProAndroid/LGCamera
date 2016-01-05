package com.lge.camera.util;

import android.app.Activity;
import android.net.Uri;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;
import java.util.Iterator;

public class SecureImageUtil {
    public static SecureImageUtil mSecureImageUtil;
    private Object mLock;
    private ArrayList<Uri> mSecureLockImageUriList;
    private ArrayList<Uri> mSecureLockVideoUriList;

    public SecureImageUtil() {
        this.mLock = new Object();
        this.mSecureLockImageUriList = new ArrayList();
        this.mSecureLockVideoUriList = new ArrayList();
    }

    static {
        mSecureImageUtil = null;
    }

    public static SecureImageUtil get() {
        if (mSecureImageUtil == null) {
            mSecureImageUtil = new SecureImageUtil();
        }
        return mSecureImageUtil;
    }

    public ArrayList<Uri> getSecureLockUriList(int cameraMode) {
        return cameraMode == 0 ? this.mSecureLockImageUriList : this.mSecureLockVideoUriList;
    }

    public boolean isSecureLockUriListEmpty(int cameraMode) {
        return getSecureLockUriListSize(cameraMode) == 0;
    }

    public int getSecureLockUriListSize(int cameraMode) {
        ArrayList<Uri> secureLockUriList = cameraMode == 0 ? this.mSecureLockImageUriList : this.mSecureLockVideoUriList;
        if (secureLockUriList == null) {
            return 0;
        }
        return secureLockUriList.size();
    }

    public void checkSecureLockUriList(Activity activity, int cameraMode) {
        synchronized (this.mLock) {
            if (activity != null) {
                if (!isSecureLockUriListEmpty(cameraMode)) {
                    CamLog.d(FaceDetector.TAG, "checkSecureLockUriList start = ");
                    ArrayList<Uri> deleteUriList = new ArrayList();
                    ArrayList<Uri> secureLockUriList = cameraMode == 0 ? this.mSecureLockImageUriList : this.mSecureLockVideoUriList;
                    Iterator i$ = secureLockUriList.iterator();
                    while (i$.hasNext()) {
                        Uri secureImageUri = (Uri) i$.next();
                        CamLog.d(FaceDetector.TAG, "secureLockUriList = " + secureLockUriList.size());
                        if (Util.getIdFromUri(activity, secureImageUri) == -1) {
                            deleteUriList.add(secureImageUri);
                        }
                    }
                    i$ = deleteUriList.iterator();
                    while (i$.hasNext()) {
                        Uri deleteUri = (Uri) i$.next();
                        removeSecureLockUri(deleteUri, cameraMode);
                        CamLog.d(FaceDetector.TAG, "deleteUri = " + deleteUri.toString());
                    }
                    deleteUriList.clear();
                    CamLog.d(FaceDetector.TAG, "checkSecureLockUriList end = ");
                }
            }
        }
    }

    public void addSecureLockImageUri(Uri addUri) {
        synchronized (this.mLock) {
            if (this.mSecureLockImageUriList != null) {
                CamLog.d(FaceDetector.TAG, "addSecureLockImageUri end = ");
                this.mSecureLockImageUriList.add(addUri);
            }
        }
    }

    public void addSecureLocVideokUri(Uri addUri) {
        synchronized (this.mLock) {
            if (this.mSecureLockVideoUriList != null) {
                this.mSecureLockVideoUriList.add(addUri);
            }
        }
    }

    public void removeSecureLockUri(Uri removeUri, int cameraMode) {
        synchronized (this.mLock) {
            ArrayList<Uri> secureLockUriList = cameraMode == 0 ? this.mSecureLockImageUriList : this.mSecureLockVideoUriList;
            if (secureLockUriList != null) {
                int index = secureLockUriList.indexOf(removeUri);
                if (index > -1) {
                    CamLog.d(FaceDetector.TAG, "removeSecureLockUri end = ");
                    secureLockUriList.remove(index);
                }
            }
        }
    }

    public void release() {
        CamLog.d(FaceDetector.TAG, "SecureImageUtil release.");
        if (this.mSecureLockImageUriList != null) {
            this.mSecureLockImageUriList.clear();
            this.mSecureLockImageUriList = null;
        }
        if (this.mSecureLockVideoUriList != null) {
            this.mSecureLockVideoUriList.clear();
            this.mSecureLockVideoUriList = null;
        }
        mSecureImageUtil = null;
    }
}
