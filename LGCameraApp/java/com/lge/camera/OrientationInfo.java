package com.lge.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.view.OrientationEventListener;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.ImageManager;
import com.lge.camera.util.Util;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Limit;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import com.lge.systemservice.core.OsManager;
import com.lge.voiceshutter.library.LGKeyRec;

public class OrientationInfo {
    private OrientationRotateFunction mGet;
    private int mLastOrientation;
    private int mLastOrientationBackup;
    private int mOrientation;
    private boolean mOrientationChangeEnabled;
    private OrientationEventListener mOrientationListener;
    private OsManager mOsManager;

    public interface OrientationRotateFunction {
        Activity getActivity();

        boolean getAudiozoomStart();

        int getHeadsetstate();

        int getVideoState();

        boolean isCamcorderRotation(boolean z);

        boolean isConfigureLandscape();

        boolean isPausing();

        void setEffectCameraOrientationHint();

        void setEffectRecorderOrientationHint();

        void setOrientationListenerRotate(int i);

        void updateAudiozoom(boolean z, int i);
    }

    public void unbind() {
        this.mOsManager = null;
    }

    public OrientationInfo(OrientationRotateFunction function) {
        this.mOrientation = -1;
        this.mOrientationListener = null;
        this.mLastOrientation = -1;
        this.mLastOrientationBackup = -1;
        this.mGet = null;
        this.mOsManager = null;
        this.mOrientationChangeEnabled = false;
        this.mGet = function;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setOrientationForced(int orientation) {
        if (this.mOrientation != orientation) {
            changeOrientation(orientation);
            this.mLastOrientation = getOrientationDegree();
            if (this.mLastOrientation != this.mLastOrientationBackup) {
                setSlimPortDegree(this.mOsManager, this.mLastOrientation);
                this.mLastOrientationBackup = this.mLastOrientation;
            }
        }
    }

    public void initOsManager() {
        this.mOsManager = null;
    }

    public void initailizeOrientation() {
        setOrientation(1);
        this.mLastOrientation = getOrientationDegree();
        if (this.mLastOrientation != this.mLastOrientationBackup) {
            setSlimPortDegree(this.mOsManager, this.mLastOrientation);
            this.mLastOrientationBackup = this.mLastOrientation;
        }
    }

    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
    }

    public void setOrientationListener(OrientationEventListener listener) {
        this.mOrientationListener = listener;
    }

    public OrientationEventListener getOrientationListener() {
        return this.mOrientationListener;
    }

    public void setLastOrientation(int lastOrientation) {
        this.mLastOrientation = lastOrientation;
    }

    public int getLastOrientation() {
        return this.mLastOrientation;
    }

    public int getOrientationDegree() {
        int i = 90;
        int i2 = Tag.IMAGE_DESCRIPTION;
        boolean windowLand = this.mGet.isConfigureLandscape();
        switch (this.mOrientation) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                if (windowLand) {
                    return 0;
                }
                return Tag.IMAGE_DESCRIPTION;
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                if (windowLand) {
                    return 90;
                }
                return 0;
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                if (windowLand) {
                    i = 180;
                }
                return i;
            case LGKeyRec.EVENT_STARTED /*3*/:
                if (!windowLand) {
                    i2 = 180;
                }
                return i2;
            default:
                if (ModelProperties.getProjectCode() == 33 || windowLand) {
                    return 0;
                }
                return Tag.IMAGE_DESCRIPTION;
        }
    }

    public int getOrientationDegreeForLandDevice() {
        switch (this.mOrientation) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                return MediaProviderUtils.ROTATION_180;
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                return Tag.IMAGE_DESCRIPTION;
            case LGKeyRec.EVENT_STARTED /*3*/:
                return 90;
            default:
                return 0;
        }
    }

    public int getDeviceDegree(int cameraId) {
        int orientation = this.mLastOrientation;
        int mDevicedegree = getOrientationDegree();
        if (this.mGet.isConfigureLandscape()) {
            mDevicedegree = (getOrientationDegree() + (360 - Util.getDisplayRotation(this.mGet.getActivity()))) % CameraConstants.DEGREE_360;
        }
        if (this.mLastOrientation == -1) {
            return 0;
        }
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        if (info.facing == 1) {
            return ((info.orientation - mDevicedegree) + CameraConstants.DEGREE_360) % CameraConstants.DEGREE_360;
        }
        return (info.orientation + mDevicedegree) % CameraConstants.DEGREE_360;
    }

    public boolean getOrientationListenerEnable() {
        return this.mOrientationChangeEnabled;
    }

    public void setOrientationListenerEnable(boolean enable) {
        this.mOrientationChangeEnabled = enable;
        if (this.mOrientationListener != null) {
            if (enable) {
                this.mOrientationListener.enable();
            } else {
                this.mOrientationListener.disable();
            }
            CamLog.d(FaceDetector.TAG, "setOrientationListener enable : " + enable + " done!");
        }
    }

    public void releaseOrientationListener() {
        this.mOrientationListener = null;
    }

    public void setSlimPortDegree(int degree) {
        setSlimPortDegree(this.mOsManager, degree);
    }

    private void setSlimPortDegree(OsManager osManager, int degree) {
        if (osManager != null) {
            String strOrientation = String.valueOf(degree);
            try {
                osManager.setSystemProperty("sys.camera_orientation", strOrientation);
            } catch (SecurityException e) {
                CamLog.w(FaceDetector.TAG, "setSlimPortDegree : SecurityException.");
            }
            CamLog.d(FaceDetector.TAG, "setSlimPortDegree = " + strOrientation);
        }
    }

    public void setOrientationListener() {
        CamLog.v(FaceDetector.TAG, "setOrientationListener");
        if (this.mOrientationListener != null) {
            this.mOrientationListener.disable();
            this.mOrientationListener = null;
        }
        this.mLastOrientationBackup = -1;
        this.mOrientationListener = new OrientationEventListener(this.mGet.getActivity(), 2) {
            public void onOrientationChanged(int orientation) {
                if (orientation != -1 && OrientationInfo.this.mOrientationChangeEnabled) {
                    OrientationInfo.this.mLastOrientation = ImageManager.roundOrientation(orientation);
                    OrientationInfo.this.mLastOrientation = OrientationInfo.this.mLastOrientation % CameraConstants.DEGREE_360;
                    if (!(OrientationInfo.this.mLastOrientationBackup == OrientationInfo.this.mLastOrientation || Common.isScreenLocked())) {
                        OrientationInfo.this.setSlimPortDegree(OrientationInfo.this.mOsManager, OrientationInfo.this.mLastOrientation);
                        OrientationInfo.this.mLastOrientationBackup = OrientationInfo.this.mLastOrientation;
                    }
                    if (orientation > CameraConstants.ORIENTATION_LANDSCAPE_DEGREE_FROM && orientation < CameraConstants.ORIENTATION_LANDSCAPE_DEGREE_TO && OrientationInfo.this.mOrientation != 0) {
                        OrientationInfo.this.changeOrientation(0);
                    } else if ((orientation > CameraConstants.ORIENTATION_PORTRAIT_DEGREE_FROM || orientation < 20) && OrientationInfo.this.mOrientation != 1) {
                        OrientationInfo.this.changeOrientation(1);
                    } else if (orientation > CameraConstants.ORIENTATION_PORTRAIT_OPPOSITE_DEGREE_FROM && orientation < Limit.OLA_FIFO_DATA_MAX_SIZE_BYTE && OrientationInfo.this.mOrientation != 3) {
                        OrientationInfo.this.changeOrientation(3);
                    } else if (orientation <= 70 || orientation >= DialogCreater.DIALOG_ID_HELP_INTELLIGENT_AUTO_MODE || OrientationInfo.this.mOrientation == 2) {
                        OrientationInfo.this.setOrientationDefault();
                    } else {
                        OrientationInfo.this.changeOrientation(2);
                    }
                }
            }
        };
    }

    private void changeOrientation(int orientation) {
        if (this.mGet != null) {
            setOrientation(orientation);
            if (FunctionProperties.isSupportAudiozoom() && (this.mGet.getVideoState() == 3 || this.mGet.getVideoState() == 4)) {
                this.mGet.updateAudiozoom(false, 0);
            }
            this.mGet.setOrientationListenerRotate(orientation);
            this.mGet.setEffectRecorderOrientationHint();
            this.mGet.setEffectCameraOrientationHint();
        }
    }

    private void setOrientationDefault() {
        if (getOrientation() != -1) {
            return;
        }
        if (this.mLastOrientation == Tag.IMAGE_DESCRIPTION) {
            changeOrientation(0);
        } else if (this.mLastOrientation == 90) {
            changeOrientation(2);
        } else if (this.mLastOrientation == 0) {
            changeOrientation(1);
        } else if (this.mLastOrientation == MediaProviderUtils.ROTATION_180) {
            changeOrientation(3);
        }
    }
}
