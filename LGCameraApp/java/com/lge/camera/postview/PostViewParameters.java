package com.lge.camera.postview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.lge.camera.VideoFile;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;

public class PostViewParameters {
    private boolean bChangeFileName;
    private boolean bConnectedDevice;
    private boolean bFromCreateProcess;
    private int mApplicationMode;
    private String mAutoReview;
    private int mCameraDimension;
    private int mCameraId;
    private String mCurrentLang;
    private int mCurrentStorage;
    private String mCurrentStorageDirectory;
    private String[] mCurrentZoom;
    private int mDisplayOrientationSetting;
    private String mFlip;
    private boolean mIsAttachIntent;
    private boolean mIsAttachMode;
    private boolean mIsMmsVideo;
    private double mLocationLatitude;
    private double mLocationLongitude;
    private int mPreviewOrientation;
    private String mSaveFileName;
    private Uri mSavedUri;
    private boolean mSecureCamera;
    private String mShotMode;
    private int mShotModeIndex;
    private boolean mTimeMachineDeleteDone;
    private String mTimeMachineStorageDirectory;
    private ArrayList<Uri> mUriList;
    private boolean mUseSecureLockImage;
    private String mVideoEffectSizeOnScreen;
    private boolean mVideoEffectsActive;
    private String mVideoExtension;
    private String mVolumeKey;

    public PostViewParameters() {
        this.mApplicationMode = 0;
        this.mCameraDimension = 0;
        this.mCameraId = 0;
        this.mSaveFileName = "";
        this.mSavedUri = null;
        this.mCurrentStorage = 1;
        this.mCurrentStorageDirectory = "";
        this.mTimeMachineStorageDirectory = "";
        this.mIsAttachMode = false;
        this.mIsAttachIntent = false;
        this.mIsMmsVideo = false;
        this.mShotMode = "";
        this.mShotModeIndex = 0;
        this.mAutoReview = CameraConstants.SMART_MODE_ON;
        this.mVideoExtension = VideoFile.VIDEO_EXTENSION_MP4;
        this.mCurrentLang = "en";
        this.mPreviewOrientation = 0;
        this.mDisplayOrientationSetting = 0;
        this.bChangeFileName = false;
        this.bFromCreateProcess = false;
        this.bConnectedDevice = false;
        this.mTimeMachineDeleteDone = false;
        this.mVideoEffectsActive = false;
        this.mVideoEffectSizeOnScreen = "";
        this.mCurrentZoom = null;
        this.mFlip = CameraConstants.SMART_MODE_OFF;
        this.mLocationLatitude = 0.0d;
        this.mLocationLongitude = 0.0d;
        this.mSecureCamera = false;
        this.mUseSecureLockImage = false;
        this.mVolumeKey = CameraConstants.VOLUME_SHUTTER;
        this.mUriList = new ArrayList();
    }

    public ArrayList<Uri> getUriList() {
        return this.mUriList;
    }

    public void setUriList(ArrayList<Uri> mUriList) {
        this.mUriList = mUriList;
    }

    public String getTimeMachineStorageDirectory() {
        return this.mTimeMachineStorageDirectory;
    }

    public int getApplicationMode() {
        return this.mApplicationMode;
    }

    public int getCameraDimension() {
        return this.mCameraDimension;
    }

    public int getCameraId() {
        return this.mCameraId;
    }

    public void setCameraId(int mCameraId) {
        this.mCameraId = mCameraId;
    }

    public String getSaveFileName() {
        return this.mSaveFileName;
    }

    public void setSaveFileName(String mSaveFileName) {
        this.mSaveFileName = mSaveFileName;
    }

    public Uri getSavedUri() {
        return this.mSavedUri;
    }

    public void setSavedUri(Uri mSavedUri) {
        this.mSavedUri = mSavedUri;
    }

    public int getCurrentStorage() {
        return this.mCurrentStorage;
    }

    public String getCurrentStorageDirectory() {
        return this.mCurrentStorageDirectory;
    }

    public void setCurrentStorageDirectory(String mCurrentStorageDirectory) {
        this.mCurrentStorageDirectory = mCurrentStorageDirectory;
    }

    public boolean isIsAttachMode() {
        return this.mIsAttachMode;
    }

    public boolean isIsAttachIntent() {
        return this.mIsAttachIntent;
    }

    public boolean isIsMmsVideo() {
        return this.mIsMmsVideo;
    }

    public String getShotMode() {
        return this.mShotMode;
    }

    public int getShotModeIndex() {
        return this.mShotModeIndex;
    }

    public String getAutoReview() {
        return this.mAutoReview;
    }

    public String getVideoExtension() {
        return this.mVideoExtension;
    }

    public String getCurrentLang() {
        return this.mCurrentLang;
    }

    public int getPreviewOrientation() {
        return this.mPreviewOrientation;
    }

    public void setPreviewOrientation(int mPreviewOrientation) {
        this.mPreviewOrientation = mPreviewOrientation;
    }

    public int getDisplayOrientationSetting() {
        return this.mDisplayOrientationSetting;
    }

    public boolean isChangeFileName() {
        return this.bChangeFileName;
    }

    public boolean isFromCreateProcess() {
        return this.bFromCreateProcess;
    }

    public boolean isConnectedDevice() {
        return this.bConnectedDevice;
    }

    public boolean isTimeMachineDeleteDone() {
        return this.mTimeMachineDeleteDone;
    }

    public boolean isVideoEffectsActive() {
        return this.mVideoEffectsActive;
    }

    public String getVideoEffectSizeOnScreen() {
        return this.mVideoEffectSizeOnScreen;
    }

    public String[] getCurrentZoom() {
        return this.mCurrentZoom;
    }

    public String getFlip() {
        return this.mFlip;
    }

    public double getLocationLatitude() {
        return this.mLocationLatitude;
    }

    public double getLocationLongitude() {
        return this.mLocationLongitude;
    }

    public boolean isSecureCamera() {
        return this.mSecureCamera;
    }

    public boolean useSecureLockImage() {
        return this.mUseSecureLockImage;
    }

    public String getVolumeKey() {
        return this.mVolumeKey;
    }

    public boolean setPostViewParameters(Context context, Intent intent) {
        CamLog.d(FaceDetector.TAG, "setupCaptureParams");
        try {
            String fullfilename;
            ArrayList<String> capturedUriList = intent.getStringArrayListExtra("capturedUriList");
            if (capturedUriList != null) {
                int uriListSize = capturedUriList.size();
                if (uriListSize == 0) {
                    CamLog.e(FaceDetector.TAG, "Image List size is 0 !! return to preview.");
                    return false;
                }
                for (int i = 0; i < uriListSize; i++) {
                    String uriString = (String) capturedUriList.get(i);
                    if (uriString != null) {
                        this.mUriList.add(Uri.parse(uriString));
                    }
                }
            }
            this.mApplicationMode = intent.getIntExtra("app_mode", 0);
            this.mCameraDimension = intent.getIntExtra("camera_dimension", 0);
            this.mCameraId = intent.getIntExtra("cameraId", 0);
            this.mSavedUri = (Uri) this.mUriList.get(0);
            this.mSaveFileName = intent.getStringExtra("saveFileName");
            setupShotModeCaptureParam(intent);
            if (this.mSavedUri != null) {
                String mRealSavedName = Common.getNameWithoutExtension(context.getContentResolver(), this.mSavedUri);
                if (!(this.mSaveFileName == null || this.mSaveFileName.equals(mRealSavedName))) {
                    this.mSaveFileName = mRealSavedName;
                }
            }
            this.mCurrentStorage = intent.getIntExtra("currentStorage", 1);
            this.mCurrentStorageDirectory = intent.getStringExtra("currentStorageDir");
            this.mTimeMachineStorageDirectory = intent.getStringExtra("timeMachineStorageDir");
            this.mIsAttachMode = intent.getBooleanExtra("isAttachMode", false);
            this.mIsAttachIntent = intent.getBooleanExtra("isAttachIntent", false);
            this.mIsMmsVideo = intent.getBooleanExtra("isMmsVideo", false);
            this.mAutoReview = intent.getStringExtra("autoReview");
            this.mVideoExtension = intent.getStringExtra("videoExtension");
            this.mPreviewOrientation = intent.getIntExtra("currentOrientation", 0);
            this.mVideoEffectsActive = intent.getBooleanExtra("effectsActive", false);
            this.mVideoEffectSizeOnScreen = intent.getStringExtra("effectsSizeOnScreen");
            this.mLocationLatitude = intent.getDoubleExtra("locationLatitude", 0.0d);
            this.mLocationLongitude = intent.getDoubleExtra("locationLongitude", 0.0d);
            this.mTimeMachineDeleteDone = false;
            this.mSecureCamera = intent.getBooleanExtra(CameraConstants.SECURE_CAMERA, false);
            this.mUseSecureLockImage = intent.getBooleanExtra(CameraConstants.USE_SECURE_LOCK, false);
            if (ProjectVariables.isSupportManualAntibanding() && this.mCameraId == 0) {
                this.mCurrentZoom = new String[3];
                this.mCurrentZoom = intent.getStringArrayExtra("currentZoom");
            }
            if (this.mApplicationMode == 0) {
                fullfilename = this.mCurrentStorageDirectory + this.mSaveFileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
            } else {
                fullfilename = this.mCurrentStorageDirectory + this.mSaveFileName + this.mVideoExtension;
            }
            if (Common.isFileExist(fullfilename)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "Exception!", e);
            return false;
        }
    }

    private void setupShotModeCaptureParam(Intent intent) {
        if (this.mApplicationMode != 0) {
            this.mFlip = intent.getStringExtra("Flip");
        } else if (this.mCameraId == 0) {
            this.mShotMode = intent.getStringExtra("shotMode");
            this.mShotModeIndex = intent.getIntExtra("shotModeIndex", 0);
            String oneShotUri;
            if (CameraConstants.TYPE_SHOTMODE_REFOCUS.equals(this.mShotMode)) {
                oneShotUri = intent.getStringExtra("allinfocusUri");
                if (oneShotUri != null) {
                    this.mSavedUri = Uri.parse(oneShotUri);
                }
                CamLog.d(FaceDetector.TAG, "mSavedUri = " + this.mSavedUri);
            } else if (!FunctionProperties.isTimeMachinShotSupported() && !FunctionProperties.isClearShotSupported()) {
            } else {
                if (intent.getBooleanExtra("timeMachineMode", false)) {
                    this.mShotMode = CameraConstants.TYPE_SHOTMODE_TIMEMACHINE;
                    this.mShotModeIndex = 3;
                    oneShotUri = intent.getStringExtra("timeMachineOneShotUri");
                    if (oneShotUri != null) {
                        this.mSavedUri = Uri.parse(oneShotUri);
                        return;
                    }
                    return;
                }
                this.mSavedUri = (Uri) this.mUriList.get(0);
            }
        } else {
            this.mSavedUri = (Uri) this.mUriList.get(0);
        }
    }

    public void clearParameters() {
        if (this.mUriList != null) {
            this.mUriList.clear();
            this.mUriList = null;
        }
    }
}
