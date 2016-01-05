package com.lge.camera.command;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.System;
import com.lge.camera.ControllerFunction;
import com.lge.camera.VideoFile;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.util.BitmapManager;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.SecureImageUtil;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class ShowGallery extends Command {
    private boolean mCheckingLastThumbnail;
    private Uri mThumbnailUri;
    private String mUriPath;

    public ShowGallery(ControllerFunction function) {
        super(function);
        this.mCheckingLastThumbnail = false;
        this.mThumbnailUri = null;
        this.mUriPath = "";
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        CamLog.d(FaceDetector.TAG, "ShowGallery - start");
        if (((Bundle) arg).getBoolean("useAsPostview", false)) {
            if (this.mGet.getApplicationMode() == 0) {
                this.mThumbnailUri = this.mGet.getSavedImageUri();
                this.mUriPath = BitmapManager.getRealPathFromURI(this.mGet.getActivity(), this.mThumbnailUri);
            } else {
                VideoFile video = this.mGet.getVideoFile();
                if (video != null) {
                    this.mThumbnailUri = video.getUri();
                    this.mUriPath = video.getFilePath();
                }
            }
        } else if (!Common.useSecureLockImage()) {
            this.mThumbnailUri = this.mGet.getThumbnailControllerUri();
            this.mUriPath = BitmapManager.getRealPathFromURI(this.mGet.getActivity(), this.mThumbnailUri);
        } else if (!this.mCheckingLastThumbnail) {
            this.mCheckingLastThumbnail = true;
            new Thread() {
                public void run() {
                    int firstInclusion;
                    int secondInclusion;
                    if (ShowGallery.this.mGet.getApplicationMode() == 0) {
                        firstInclusion = 1;
                    } else {
                        firstInclusion = 4;
                    }
                    if (ShowGallery.this.mGet.getApplicationMode() == 0) {
                        secondInclusion = 4;
                    } else {
                        secondInclusion = 1;
                    }
                    if (!ShowGallery.this.mGet.isPausing()) {
                        ShowGallery.this.mThumbnailUri = ShowGallery.this.mGet.getMostRecentThumbnailUri(true, firstInclusion);
                        if (ShowGallery.this.mThumbnailUri == null) {
                            ShowGallery.this.mThumbnailUri = ShowGallery.this.mGet.getMostRecentThumbnailUri(true, secondInclusion);
                        }
                        ShowGallery.this.mUriPath = BitmapManager.getRealPathFromURI(ShowGallery.this.mGet.getActivity(), ShowGallery.this.mThumbnailUri);
                        ShowGallery.this.mGet.postOnUiThread(new Runnable() {
                            public void run() {
                                ShowGallery.this.mGet.removePostRunnable(this);
                                ShowGallery.this.checkUriAndShowGallery();
                            }
                        });
                    }
                    ShowGallery.this.mCheckingLastThumbnail = false;
                }
            }.start();
            SecureImageUtil.get().release();
            return;
        } else {
            return;
        }
        checkUriAndShowGallery();
    }

    private void checkUriAndShowGallery() {
        if (this.mUriPath == null || this.mThumbnailUri == null) {
            this.mGet.setGalleryLaunching(false);
            this.mGet.getThumbnailAndUpdateButton();
            CamLog.d(FaceDetector.TAG, "Uri is not valid");
            CamLog.d(FaceDetector.TAG, "show gallery return - thumbnailUri:" + this.mThumbnailUri);
        } else if (this.mGet.isPreviewOnGoing()) {
            this.mGet.setGalleryLaunching(false);
        } else {
            if (this.mGet.getApplicationMode() == 0 && this.mGet.getFocusState() != 0) {
                this.mGet.cancelAutoFocus();
                this.mGet.setFocusRectangleInitialize();
            }
            ApplicationInfo info = null;
            try {
                info = this.mGet.getActivity().getPackageManager().getApplicationInfo("com.android.gallery3d", 128);
            } catch (NameNotFoundException e) {
                CamLog.d(FaceDetector.TAG, "Gallery is not founded:", e);
                if (ModelProperties.isSamsungModel() || ModelProperties.getProjectCode() == 8) {
                    launchingGallery(this.mThumbnailUri, null);
                } else {
                    this.mGet.setGalleryLaunching(false);
                    return;
                }
            }
            launchingGallery(this.mThumbnailUri, info);
        }
    }

    private void launchingGallery(Uri thumbnailUri, ApplicationInfo info) {
        if (info == null || info.enabled) {
            Intent intent = new Intent("com.android.camera.action.REVIEW");
            if (this.mGet.getApplicationMode() == 0) {
                intent.setDataAndType(thumbnailUri, "image/*");
            } else {
                intent.setDataAndType(thumbnailUri, "video/*");
            }
            setIntentRotationExtra(intent);
            intent.addFlags(67108864);
            try {
                if (this.mGet.getStorageState() == 2 || this.mGet.getStorageState() == 0) {
                    this.mGet.getActivity().startActivity(intent);
                    return;
                }
                return;
            } catch (ActivityNotFoundException ex) {
                CamLog.e(FaceDetector.TAG, "review fail", ex);
                this.mGet.setGalleryLaunching(false);
                return;
            }
        }
        this.mGet.setGalleryLaunching(false);
        this.mGet.restoreLiveEffectSubMenu();
        this.mGet.clearQuickFunctionSubMenu();
        this.mGet.doCommand(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION);
        this.mGet.showDialogPopup(24);
        this.mGet.enableCommand(true);
        this.mGet.setQuickFunctionAllMenuEnabled(true, false);
        this.mGet.setQuickButtonMenuEnable(true, false);
    }

    public void setIntentRotationExtra(Intent intent) {
        boolean isRotationAutoOff = false;
        if (System.getInt(this.mGet.getActivity().getContentResolver(), "accelerometer_rotation", 0) == 0) {
            isRotationAutoOff = true;
        }
        if (!isRotationAutoOff) {
            intent.putExtra("screen-orientation", getOrientaionString());
            CamLog.i(FaceDetector.TAG, "set scerre-orientation " + getOrientaionString());
        } else if (setExtraForKDDI()) {
            CamLog.i(FaceDetector.TAG, "set extra to reversePortrait for KDDI");
            intent.putExtra("screen-orientation", "reversePortrait");
        }
    }

    protected String getOrientaionString() {
        String strOrientation = null;
        switch (this.mGet.getOrientationDegree()) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                strOrientation = LGT_Limit.ISP_ORIENTATION_PORTRAIT;
                break;
            case MediaProviderUtils.ROTATION_90 /*90*/:
                strOrientation = "reverseLandscape";
                break;
            case MediaProviderUtils.ROTATION_180 /*180*/:
                strOrientation = "reversePortrait";
                break;
            case Tag.IMAGE_DESCRIPTION /*270*/:
                strOrientation = LGT_Limit.ISP_ORIENTATION_LANDSCAPE;
                break;
        }
        CamLog.i(FaceDetector.TAG, "set extra orientation :" + strOrientation);
        return strOrientation;
    }

    protected boolean setExtraForKDDI() {
        if (ModelProperties.getCarrierCode() != 7) {
            return false;
        }
        boolean mIsReverse;
        if (this.mGet.getOrientationDegree() == MediaProviderUtils.ROTATION_180) {
            mIsReverse = true;
        } else {
            mIsReverse = false;
        }
        boolean mIsFrontCam;
        if (this.mGet.getCameraId() == 1) {
            mIsFrontCam = true;
        } else {
            mIsFrontCam = false;
        }
        if (mIsReverse && mIsFrontCam) {
            return true;
        }
        return false;
    }
}
