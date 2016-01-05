package com.lge.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera.Parameters;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import com.lge.camera.command.Command;
import com.lge.camera.components.CameraPreview;
import com.lge.camera.controller.camcorder.AudiozoomController;
import com.lge.camera.controller.camcorder.CamcorderFocusController;
import com.lge.camera.controller.camcorder.CamcorderIndicatorController;
import com.lge.camera.controller.camcorder.CamcorderPreviewController;
import com.lge.camera.controller.camcorder.CamcorderQuickFunctionController;
import com.lge.camera.controller.camcorder.CamcorderShotModeMenuController;
import com.lge.camera.controller.camcorder.CamcorderStorageController;
import com.lge.camera.controller.camcorder.RecordingController;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.MmsProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.PreferenceGroup;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.BitmapManager;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.ExifUtil;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.ImageSavers;
import com.lge.camera.util.ImageSavers.ImageSaverCallback;
import com.lge.camera.util.SaveRequest;
import com.lge.camera.util.SecureImageUtil;
import com.lge.camera.util.Util;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.io.File;
import java.util.Random;

public class CamcorderMediator extends Mediator implements ImageSaverCallback {
    private final long HeatDelay1;
    private final long HeatDelay2;
    protected AudiozoomController mAudiozoomController;
    private boolean mBackKeyRecStop;
    private int mConnectHeadset;
    public int mHealDelayCount;
    private ImageSavers mLiveSnapshotSaver;
    private int mMaxVideoDurationInMs;
    private String mPrevieousVideoAutoReviewValue;
    private String mPreviousRecordMode;
    protected RecordingController mRecordingController;
    private long mRequestedSizeLimit;
    private boolean mediaUsbConnected;

    public CamcorderMediator(Camcorder activity) {
        super(activity);
        this.mBackKeyRecStop = false;
        this.HeatDelay1 = 600000;
        this.HeatDelay2 = 180000;
        this.mHealDelayCount = 0;
        this.mPreviousRecordMode = CameraConstants.TYPE_RECORDMODE_NORMAL;
        this.mPrevieousVideoAutoReviewValue = null;
        this.mConnectHeadset = 0;
        this.mRequestedSizeLimit = 0;
        this.mMaxVideoDurationInMs = 0;
        this.mediaUsbConnected = false;
        setApplicationMode(1);
    }

    public RecordingController getRecordingController() {
        return this.mRecordingController;
    }

    public AudiozoomController getAudiozoomController() {
        return this.mAudiozoomController;
    }

    public CamcorderFocusController getFocusController() {
        return (CamcorderFocusController) this.mFocusController;
    }

    public CamcorderIndicatorController getIndicatorController() {
        return (CamcorderIndicatorController) this.mIndicatorController;
    }

    public CamcorderPreviewController getPreviewController() {
        return (CamcorderPreviewController) this.mPreviewController;
    }

    public CamcorderQuickFunctionController getQuickFunctionController() {
        return (CamcorderQuickFunctionController) this.mQuickFunctionController;
    }

    public String getSelectedQuickFunctionMenuKey() {
        return this.mQuickFunctionController.getSelectedMenuKey();
    }

    public CamcorderShotModeMenuController getShotModeMenuController() {
        return (CamcorderShotModeMenuController) this.mShotModeMenuController;
    }

    protected void createControllers() {
        if (this.mPreviewController == null) {
            this.mPreviewController = new CamcorderPreviewController(this);
        }
        this.mControllers.add(this.mPreviewController);
        this.mQuickFunctionController = new CamcorderQuickFunctionController(this);
        this.mControllers.add(this.mQuickFunctionController);
        this.mIndicatorController = new CamcorderIndicatorController(this);
        this.mControllers.add(this.mIndicatorController);
        this.mFocusController = new CamcorderFocusController(this);
        this.mControllers.add(this.mFocusController);
        this.mStorageController = new CamcorderStorageController(this);
        this.mControllers.add(this.mStorageController);
        this.mRecordingController = new RecordingController(this);
        this.mControllers.add(this.mRecordingController);
        this.mAudiozoomController = new AudiozoomController(this);
        this.mControllers.add(this.mAudiozoomController);
        this.mShotModeMenuController = new CamcorderShotModeMenuController(this);
        this.mControllers.add(this.mShotModeMenuController);
        super.createControllers();
    }

    public void initControllers() {
        super.initControllers();
        this.mAudiozoomController.initController();
        this.mRecordingController.initController();
    }

    public void showOsd() {
        showQuickFunctionController();
        showIndicatorController();
        showFocus();
    }

    public void hideOsd() {
        this.mQuickFunctionController.hide();
        hideFocus();
    }

    public void hideOsdByForce() {
        this.mQuickFunctionController.qflHide();
        hideFocus();
    }

    protected void createPreviewController() {
        this.mPreviewController = new CamcorderPreviewController(this);
    }

    public void onCreate() {
        createControllers();
        super.onCreate();
    }

    public void onResume() {
        this.mLiveSnapshotSaver = new ImageSavers(this, 10);
        super.onResume();
        if (checkSettingValue(Setting.KEY_LIGHT, CameraConstants.SMART_MODE_ON)) {
            setBackgroundColorWhite();
        } else if (checkSettingValue(Setting.KEY_LIGHT, CameraConstants.SMART_MODE_OFF)) {
            setBackgroundColorBlack();
        }
        if (getVideoState() == 5) {
            if (isAttachIntent() || isMMSIntent()) {
                doCommandUi(Command.DISPLAY_CAMCORDER_POSTVIEW);
            } else if (CameraConstants.SMART_MODE_OFF.equals(getSettingValue(Setting.KEY_VIDEO_AUTO_REVIEW)) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(getSettingValue(Setting.KEY_VIDEO_AUTO_REVIEW))) {
                doCommand(Command.DISPLAY_PREVIEW);
            } else {
                doCommandUi(Command.DISPLAY_CAMCORDER_POSTVIEW);
            }
        } else if (this.mActivity.getPostviewRequestCode() == 2) {
            doAttach();
            this.mActivity.setPostviewRequestInitCode();
        } else {
            postOnUiThread(new Runnable() {
                public void run() {
                    CamcorderMediator.this.removePostRunnable(this);
                    if (!CamcorderMediator.this.isPausing()) {
                        int requestCode = CamcorderMediator.this.mActivity.getPostviewRequestCode();
                        CamcorderMediator.this.mActivity.setPostviewRequestInitCode();
                        switch (requestCode) {
                            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                                CamcorderMediator.this.toast(CamcorderMediator.this.getString(R.string.popup_delete_done));
                            default:
                        }
                    }
                }
            });
        }
    }

    public void onPause() {
        CamLog.d(FaceDetector.TAG, "onPause()-start");
        this.mPreviewController.setEffectRecorderPausing(true);
        if (getRecordingController() != null) {
            getRecordingController().onPause();
        }
        if (getFocusController() != null) {
            getFocusController().onPause();
        }
        if (checkSettingValue(Setting.KEY_LIGHT, CameraConstants.SMART_MODE_ON) && getCameraId() == 1) {
            setBackgroundColorBlack();
        }
        finishLiveSnapshotSaver();
        stopReceivingLocationUpdates();
        stopHeatingwarning();
        super.onPause();
        CamLog.d(FaceDetector.TAG, "onPause()-end");
    }

    public void onDestroy() {
        CamLog.d(FaceDetector.TAG, "onDestroy()-start");
        this.mRecordingController = null;
        this.mAudiozoomController = null;
        super.onDestroy();
        CamLog.d(FaceDetector.TAG, "onDestroy()-end");
    }

    public void readVideoIntentExtras() {
        Intent intent = getActivity().getIntent();
        if (intent.hasExtra("android.intent.extra.videoQuality")) {
            int extraVideoQuality = intent.getIntExtra("android.intent.extra.videoQuality", 0);
            CamLog.d(FaceDetector.TAG, String.format("extra video quality request: %d", new Object[]{Integer.valueOf(extraVideoQuality)}));
        }
        Bundle getExBundle = intent.getExtras();
        if (getExBundle == null) {
            CamLog.d(FaceDetector.TAG, "intent.getExtras() is null. assume no limit.");
            this.mRequestedSizeLimit = 0;
        } else {
            this.mRequestedSizeLimit = getExBundle.getLong("android.intent.extra.sizeLimit", 0);
            CamLog.d(FaceDetector.TAG, String.format("requested file size limit: %d", new Object[]{Long.valueOf(this.mRequestedSizeLimit)}));
            this.mSaveUri = (Uri) getExBundle.getParcelable("output");
            CamLog.d(FaceDetector.TAG, "requested file uri: " + this.mSaveUri);
        }
        CamLog.d(FaceDetector.TAG, String.format("reduced safe filesize limit: %d", new Object[]{Long.valueOf(this.mRequestedSizeLimit)}));
        if (!isMMSIntent() && intent.hasExtra("android.intent.extra.durationLimit")) {
            int seconds = intent.getIntExtra("android.intent.extra.durationLimit", 0);
            CamLog.d(FaceDetector.TAG, String.format("duration limit: %d", new Object[]{Integer.valueOf(seconds)}));
            if ("kr.co.tictocplus".equals(getActivity().getCallingPackage())) {
                seconds = 0;
            }
            this.mMaxVideoDurationInMs = seconds * PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME;
        }
    }

    public boolean hasSaveURI() {
        return this.mSaveUri != null;
    }

    public void showRequestedSizeLimit() {
        String notifyMsg = "";
        if (!isAttachIntent()) {
            try {
                toastLong(String.format(getString(R.string.sp_MMS_REC_limit_NORMAL), new Object[]{Integer.valueOf(60)}));
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "Not provided this language in AT&T :" + e);
            }
        } else if (this.mRequestedSizeLimit > 0) {
            try {
                toastLong(String.format(getString(R.string.sp_MMS_REC_limit_NORMAL), new Object[]{Integer.valueOf(60)}));
            } catch (Exception e2) {
                CamLog.e(FaceDetector.TAG, "Not provided this language in AT&T :" + e2);
            }
        }
    }

    public void addMMSTexture(PreferenceGroup pg) {
        if (pg != null) {
            ListPreference listPref = pg.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            if (listPref != null) {
                CharSequence[] entries = listPref.getEntries();
                CharSequence[] entryValues = listPref.getEntryValues();
                for (int i = 0; i < entryValues.length; i++) {
                    if (MmsProperties.isAvailableMmsResolution(getContentResolver(), entryValues[i].toString())) {
                        if (entries[i].toString().equals(getString(R.string.sp_video_size_qvga_qfl_NORMAL))) {
                            entries[i] = getString(R.string.sp_video_size_qvga_EXPAND);
                        } else if (entries[i].toString().equals(getString(R.string.sp_video_size_qcif_qfl_NORMAL))) {
                            entries[i] = getString(R.string.sp_video_size_qcif_EXPAND);
                        }
                    }
                }
                listPref.setEntries(entries);
            }
        }
    }

    public long getRequestedVideoSizeLimit() {
        return this.mRequestedSizeLimit;
    }

    public int getMaxVideoDurationInMs() {
        return this.mMaxVideoDurationInMs;
    }

    public void doAttach() {
        int resultCode;
        Intent resultIntent = new Intent();
        Uri savedVideoUri = this.mActivity.getPostviewUri();
        this.mSavedVideoUri = savedVideoUri;
        if (savedVideoUri == null) {
            CamLog.d(FaceDetector.TAG, "attached file uri is null");
            resultCode = 0;
        } else if (!hasSaveURI() || isMMSIntent()) {
            CamLog.d(FaceDetector.TAG, "attached file uri:" + savedVideoUri);
            resultCode = -1;
            resultIntent.setData(savedVideoUri);
        } else {
            doAttachSaveUri();
            resultCode = -1;
        }
        getActivity().setResult(resultCode, resultIntent);
        getActivity().finish();
    }

    public boolean isAttachMode() {
        PreferenceGroup pg = getPreferenceGroup();
        if (pg == null) {
            return false;
        }
        ListPreference listPref = pg.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
        if (listPref == null) {
            return false;
        }
        String videoSize = listPref.getValue();
        boolean isMmsVideoSetting;
        if (MmsProperties.getMmsResolutionsLength(getContentResolver()) == 0) {
            isMmsVideoSetting = false;
        } else if (isLiveEffectActive()) {
            isMmsVideoSetting = false;
        } else {
            isMmsVideoSetting = MmsProperties.isAvailableMmsResolution(getContentResolver(), videoSize);
        }
        if (isAttachIntent() || isMmsVideoSetting) {
            return true;
        }
        return false;
    }

    public boolean isAttachIntent() {
        return "android.media.action.VIDEO_CAPTURE".equals(getActivity().getIntent().getAction());
    }

    public boolean isMMSIntent() {
        boolean isMMScalling = false;
        Intent intent = getActivity().getIntent();
        String callingPackage = getActivity().getCallingPackage();
        if (intent != null) {
            if (intent.getIntExtra("MMSAttach", 0) == 1) {
                isMMScalling = true;
            }
            String intentFrom = intent.getStringExtra("intentFrom");
            if (intentFrom != null && intentFrom.equals("MMSAttach")) {
                isMMScalling = true;
            }
        }
        if ("com.android.mms".equals(callingPackage)) {
            return true;
        }
        return isMMScalling;
    }

    public boolean isPlayRingMode() {
        boolean z;
        Intent intent = getActivity().getIntent();
        String str = FaceDetector.TAG;
        StringBuilder append = new StringBuilder().append("isCallPlayRingMode? = ");
        if (intent.getIntExtra("playRing", 0) == 1) {
            z = true;
        } else {
            z = false;
        }
        CamLog.d(str, append.append(z).toString());
        if (intent.getIntExtra("playRing", 0) == 1) {
            return true;
        }
        return false;
    }

    public boolean isMMSRecording() {
        return isMMSIntent() || (!isAttachIntent() && isAttachMode());
    }

    public void setBackKeyRecStop(boolean con) {
        this.mBackKeyRecStop = con;
    }

    public boolean getBackKeyRecStop() {
        return this.mBackKeyRecStop;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean checkSupportVideoSize(android.net.Uri r14) {
        /*
        r13 = this;
        r12 = 1;
        r3 = 0;
        r11 = 0;
        r2 = new java.lang.String[r12];
        r0 = "resolution";
        r2[r11] = r0;
        r6 = 0;
        r9 = "";
        r0 = r13.getContentResolver();
        r1 = r14;
        r4 = r3;
        r5 = r3;
        r6 = r0.query(r1, r2, r3, r4, r5);
        if (r6 != 0) goto L_0x0021;
    L_0x0019:
        r0 = new java.lang.RuntimeException;
        r1 = "cursor is null";
        r0.<init>(r1);
        throw r0;
    L_0x0021:
        r0 = r6.moveToNext();	 Catch:{ Throwable -> 0x006c, all -> 0x0071 }
        if (r0 == 0) goto L_0x0067;
    L_0x0027:
        r0 = 0;
        r9 = r6.getString(r0);	 Catch:{ Throwable -> 0x006c, all -> 0x0071 }
        if (r9 != 0) goto L_0x0033;
    L_0x002e:
        r6.close();
        r0 = r11;
    L_0x0032:
        return r0;
    L_0x0033:
        r0 = 0;
        r1 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        r1 = r9.indexOf(r1);	 Catch:{ Throwable -> 0x005f, all -> 0x0071 }
        r0 = r9.substring(r0, r1);	 Catch:{ Throwable -> 0x005f, all -> 0x0071 }
        r10 = java.lang.Integer.parseInt(r0);	 Catch:{ Throwable -> 0x005f, all -> 0x0071 }
        r0 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        r0 = r9.indexOf(r0);	 Catch:{ Throwable -> 0x005f, all -> 0x0071 }
        r0 = r0 + 1;
        r0 = r9.substring(r0);	 Catch:{ Throwable -> 0x005f, all -> 0x0071 }
        r8 = java.lang.Integer.parseInt(r0);	 Catch:{ Throwable -> 0x005f, all -> 0x0071 }
        r0 = 1280; // 0x500 float:1.794E-42 double:6.324E-321;
        if (r10 > r0) goto L_0x005a;
    L_0x0056:
        r0 = 720; // 0x2d0 float:1.009E-42 double:3.557E-321;
        if (r8 <= r0) goto L_0x0067;
    L_0x005a:
        r6.close();
        r0 = r11;
        goto L_0x0032;
    L_0x005f:
        r7 = move-exception;
        r0 = "CameraApp";
        r1 = "Exception ";
        com.lge.camera.util.CamLog.e(r0, r1, r7);	 Catch:{ Throwable -> 0x006c, all -> 0x0071 }
    L_0x0067:
        r6.close();
    L_0x006a:
        r0 = r12;
        goto L_0x0032;
    L_0x006c:
        r0 = move-exception;
        r6.close();
        goto L_0x006a;
    L_0x0071:
        r0 = move-exception;
        r6.close();
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.CamcorderMediator.checkSupportVideoSize(android.net.Uri):boolean");
    }

    public Bitmap getLastThumbnail(Uri uri) {
        if (this.mActivity == null) {
            return null;
        }
        long id = -1;
        if (uri != null) {
            id = Util.getIdFromUri(getActivity(), uri);
            CamLog.d(FaceDetector.TAG, String.format("GET VIDEO THUMB start id is %d, and uri is %s", new Object[]{Long.valueOf(id), uri.toString()}));
        }
        if (id == -1) {
            CamLog.w(FaceDetector.TAG, String.format("GET VIDEO THUMB end: uri not valid", new Object[0]));
            return null;
        }
        Bitmap thumbnail = null;
        if (ModelProperties.isOMAP4Chipset()) {
            String filePath = BitmapManager.getRealPathFromURI(getActivity(), uri);
            if (filePath != null) {
                thumbnail = ThumbnailUtils.createVideoThumbnail(filePath, 1);
            }
        } else if (getVideoState() == 3 && FunctionProperties.isAvailableLiveShot() && !MultimediaProperties.isPauseAndResumeSupported()) {
            int degree = ExifUtil.getExifOrientationDegree(BitmapManager.getRealPathFromURI(getActivity(), uri));
            thumbnail = this.imageHandler.getImage(BitmapManager.instance().getThumbnail(getContentResolver(), id, 1, null, false), degree, false);
        } else {
            thumbnail = BitmapManager.instance().getThumbnail(getContentResolver(), id, 1, null, true);
        }
        CamLog.d(FaceDetector.TAG, String.format("GET VIDEO THUMB stop", new Object[0]));
        return thumbnail;
    }

    public void showRecoridngStopButton() {
        if (!MultimediaProperties.isPauseAndResumeSupported()) {
            return;
        }
        if (getVideoState() == 3 || getVideoState() == 4) {
            postOnUiThread(new Runnable() {
                public void run() {
                    CamcorderMediator.this.removePostRunnable(this);
                    CamcorderMediator.this.getPreviewPanelController().showRecoridngStopButton();
                }
            });
        }
    }

    public void hideRecoridngStopButton() {
        if (MultimediaProperties.isPauseAndResumeSupported()) {
            getPreviewPanelController().hideRecoridngStopButton();
        }
    }

    public boolean savePicture(byte[] data, Bitmap bitmap) {
        boolean ret = false;
        long dateTaken = System.currentTimeMillis();
        CamLog.d(FaceDetector.TAG, "Camcorder savePicture()-start ");
        String path = this.mStorageController.getCurrentStorageDirectory();
        this.mImageRotationDegree = getDeviceDegree();
        CamLog.d(FaceDetector.TAG, "Camcorder savePicture()-CarrierCode: " + ModelProperties.getCarrierCode());
        FileNamer.get().markTakeTime(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), getSettingValue(Setting.KEY_SCENE_MODE));
        String fileName = FileNamer.get().getFileNewName(getApplicationContext(), 0, getCurrentStorage(), getCurrentStorageDirectory(), false, getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), false);
        CamLog.i(FaceDetector.TAG, "savePicture >  fileName :" + fileName);
        if (fileName == null) {
            CamLog.d(FaceDetector.TAG, "error get file name!");
            return false;
        }
        try {
            CamLog.d(FaceDetector.TAG, "imageHandler.addImage-start ");
            this.mSavedImageUri = this.imageHandler.addImage(getContentResolver(), fileName, dateTaken, getCurrentLocation(), path, fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT, bitmap, data, this.mImageRotationDegree, getSettingValue(Setting.KEY_CAMERA_SHOT_MODE).equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS));
            if (this.mSavedImageUri != null) {
                ret = true;
            }
        } catch (Throwable ex) {
            CamLog.e(FaceDetector.TAG, "Exception while compressing liveSnapshot image.", ex);
            ret = false;
        }
        CamLog.d(FaceDetector.TAG, "imageHandler.addImage-end " + this.mSavedImageUri);
        if (ret) {
            this.mSavedFileName = fileName;
            Util.broadcastNewPicture(getActivity(), this.mSavedImageUri);
            if (Common.useSecureLockImage() || AppControlUtil.checkGuestModeAndAppDisabled(getContentResolver(), true, 1)) {
                SecureImageUtil.get().addSecureLockImageUri(this.mSavedImageUri);
            }
        }
        getStorageController().checkStorage(false);
        CamLog.d(FaceDetector.TAG, "Camcorder savePicture()-end, return " + ret);
        return ret;
    }

    public void finishLiveSnapshotSaver() {
        if (this.mLiveSnapshotSaver != null) {
            this.mLiveSnapshotSaver.finish();
            this.mLiveSnapshotSaver = null;
        }
    }

    public void waitSaveImageThreadDone() {
        if (this.mLiveSnapshotSaver != null) {
            this.mLiveSnapshotSaver.waitDone();
        }
    }

    public boolean saveImageSavers(byte[] jpegData, Bitmap bitmap, int degree, boolean isSetLastThumb, boolean isBurstFirst) {
        if (this.mLiveSnapshotSaver != null) {
            return this.mLiveSnapshotSaver.addImage(jpegData, bitmap, 0, isSetLastThumb, isBurstFirst);
        }
        CamLog.w(FaceDetector.TAG, "ImageSave is null!");
        return false;
    }

    public int getQueueCount() {
        if (this.mLiveSnapshotSaver != null) {
            return this.mLiveSnapshotSaver.getCount();
        }
        return 0;
    }

    public void setSaveRequest(SaveRequest sr, byte[] data, Bitmap bitmap, int degree, boolean isSetLastThumb, boolean isBurstFirst) {
        sr.exifData = null;
        sr.data = data;
        sr.bitmap = bitmap;
        sr.dateTaken = System.currentTimeMillis();
        sr.degree = degree;
        sr.isSetLastThumb = isSetLastThumb;
        sr.isBurstFirst = isBurstFirst;
    }

    public void saveAndAddImageForImageSavers(SaveRequest sr) {
        String fileName;
        Throwable ex;
        File file;
        long savedFileSize;
        boolean ret = false;
        String fileName2 = "";
        String path = null;
        try {
            CamLog.d(FaceDetector.TAG, "imageHandler.addImage-start ");
            path = getStorageController().getCurrentStorageDirectory();
            FileNamer.get().markTakeTime(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), getSettingValue(Setting.KEY_SCENE_MODE));
            fileName = FileNamer.get().getFileNewName(getApplicationContext(), 0, getCurrentStorage(), getCurrentStorageDirectory(), false, getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), sr.isBurstFirst);
            try {
                CamLog.i(FaceDetector.TAG, "savePicture >  sr.fileName :" + fileName);
                if (fileName == null) {
                    CamLog.d(FaceDetector.TAG, "error get file name!");
                    ret = false;
                } else {
                    this.mSavedImageUri = this.imageHandler.addImage(getContentResolver(), fileName, sr.dateTaken, getCurrentLocation(), path, fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT, sr.bitmap, sr.data, sr.degree, getSettingValue(Setting.KEY_CAMERA_SHOT_MODE).equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS));
                    if (this.mSavedImageUri != null) {
                        ret = true;
                    }
                }
            } catch (Exception e) {
                ex = e;
                CamLog.e(FaceDetector.TAG, "Exception while compressing liveSnapshot image.", ex);
                ret = false;
                CamLog.d(FaceDetector.TAG, "imageHandler.addImage-end " + this.mSavedImageUri);
                if (ret) {
                    this.mSavedFileName = fileName;
                    Util.broadcastNewPicture(getActivity(), this.mSavedImageUri);
                    SecureImageUtil.get().addSecureLockImageUri(this.mSavedImageUri);
                    file = new File(path + fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT);
                    if (file != null) {
                        savedFileSize = file.length();
                        CamLog.d(FaceDetector.TAG, "changeMaxFileSize = " + savedFileSize);
                        changeLiveSnapshotMaxFileSize(savedFileSize);
                    }
                }
                getPreviewPanelController().setLastPictureThumb(sr.data, this.mSavedImageUri, true);
                getPreviewPanelController().updateThumbnailButton();
                getStorageController().checkStorage(false);
            }
        } catch (Exception e2) {
            ex = e2;
            fileName = fileName2;
            CamLog.e(FaceDetector.TAG, "Exception while compressing liveSnapshot image.", ex);
            ret = false;
            CamLog.d(FaceDetector.TAG, "imageHandler.addImage-end " + this.mSavedImageUri);
            if (ret) {
                this.mSavedFileName = fileName;
                Util.broadcastNewPicture(getActivity(), this.mSavedImageUri);
                SecureImageUtil.get().addSecureLockImageUri(this.mSavedImageUri);
                file = new File(path + fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT);
                if (file != null) {
                    savedFileSize = file.length();
                    CamLog.d(FaceDetector.TAG, "changeMaxFileSize = " + savedFileSize);
                    changeLiveSnapshotMaxFileSize(savedFileSize);
                }
            }
            getPreviewPanelController().setLastPictureThumb(sr.data, this.mSavedImageUri, true);
            getPreviewPanelController().updateThumbnailButton();
            getStorageController().checkStorage(false);
        }
        CamLog.d(FaceDetector.TAG, "imageHandler.addImage-end " + this.mSavedImageUri);
        if (ret) {
            this.mSavedFileName = fileName;
            Util.broadcastNewPicture(getActivity(), this.mSavedImageUri);
            if (Common.useSecureLockImage() || AppControlUtil.checkGuestModeAndAppDisabled(getContentResolver(), true, 1)) {
                SecureImageUtil.get().addSecureLockImageUri(this.mSavedImageUri);
            }
            file = new File(path + fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT);
            if (file != null) {
                savedFileSize = file.length();
                CamLog.d(FaceDetector.TAG, "changeMaxFileSize = " + savedFileSize);
                changeLiveSnapshotMaxFileSize(savedFileSize);
            }
        }
        getPreviewPanelController().setLastPictureThumb(sr.data, this.mSavedImageUri, true);
        getPreviewPanelController().updateThumbnailButton();
        getStorageController().checkStorage(false);
    }

    public void startHeatingwarning() {
        long delay;
        CamLog.v(FaceDetector.TAG, "===> StartHeatingwarning");
        Message msg = Message.obtain();
        msg.what = 7;
        if (this.mHealDelayCount == 1) {
            delay = 180000;
        } else if (this.mHealDelayCount == 0) {
            delay = 600000;
        } else {
            msg.what = 6;
            delay = 0;
        }
        this.mHealDelayCount++;
        CamLog.v(FaceDetector.TAG, "===> delay :" + delay);
        this.mHandler.sendMessageDelayed(msg, delay);
    }

    public void stopHeatingwarning() {
        CamLog.v(FaceDetector.TAG, "===> StopHeatingwarning");
        if (this.mHandler.hasMessages(7)) {
            this.mHandler.removeMessages(7);
        }
        if (this.mHealDelayCount != 0) {
            this.mHealDelayCount = 0;
        }
    }

    public void showHeatingwarning() {
        CamLog.v(FaceDetector.TAG, "===> ShowHeatingwarning");
        if (getIsCharging()) {
            getToastController().show(String.format(getString(R.string.sp_warning_high_temperature_on_recording_NORMAL), new Object[0]), (long) ProjectVariables.keepDuration);
            Message msg = Message.obtain();
            msg.what = 5;
            this.mHandler.sendMessage(msg);
        }
    }

    public void doTouchbyAF(int x, int y) {
        if (this.mApplicationMode == 1 && this.mRecordingController != null) {
            recordingControllerShow();
            if ((this.mCameraMode == 0 && (getVideoState() == 3 || getVideoState() == 4)) || (isSmartZoomRecordingActive() && getPIPController().isSmartZoomFocusViewShown())) {
                this.mFocusController.startFocusByTouchPress(x, y);
            }
        }
    }

    public boolean getMediaUSBConnectAtStartRecord() {
        return this.mediaUsbConnected;
    }

    public void setHeadsetstate(int Connect) {
        if (FunctionProperties.isSupportAudiozoom()) {
            this.mConnectHeadset = Connect;
            if (this.mConnectHeadset == 2 && getVideoFile() != null) {
                getVideoFile().setAudiozoomExection_state(true);
            }
            if (getAudiozoomStart() && this.mConnectHeadset == 2) {
                stopAudiozoom();
            } else {
                setAudiozoombuttonstate();
            }
        }
    }

    public int getHeadsetstate() {
        return this.mConnectHeadset;
    }

    public void switchCameraId(int cameraId) {
        if (!this.mPausing) {
            CamLog.d(FaceDetector.TAG, "switchCameraId()-start, " + cameraId);
            setCameraId(cameraId);
            Setting.writePreferredCameraId(getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, 0), cameraId);
            CameraPreview sPreview = this.mPreviewController.getCameraPreview();
            sPreview.setVisibility(4);
            stopPreview();
            getPreviewController().closeCamera();
            if (ModelProperties.isOMAP4Chipset()) {
                this.imageHandler.resetRotation();
            }
            enableInput(false);
            sPreview.setVisibility(0);
            this.mPreviewController.startPreview(null, true);
            CamLog.d(FaceDetector.TAG, "switchCameraId()-end, " + cameraId);
        }
    }

    public void showControllerForHideSettingMenu(boolean checkShowAll, boolean showAll) {
        if (this.mRecordingController != null && this.mFocusController != null) {
            if (!checkShowAll || showAll) {
                recordingControllerShow();
                showFocus();
            }
            if (needProgressBar()) {
                recordingControllerShow();
            }
            if (isDualRecordingActive() || isDualCameraActive() || isSmartZoomRecordingActive()) {
                doCommandUi(Command.SHOW_PIP_FRAME_SUB_MENU);
            } else if (isLiveEffectActive()) {
                Bundle isOpen = new Bundle();
                isOpen.putBoolean("menu_open", false);
                doCommandUi(Command.SHOW_LIVEEFFECT_SUBMENU_DRAWER, isOpen);
            }
        }
    }

    public boolean postviewRequestInit() {
        if (this.mActivity.getPostviewRequestCode() != 3 || (getVideoState() == 3 && FunctionProperties.isAvailableLiveShot())) {
            return false;
        }
        this.mActivity.setPostviewRequestInitCode();
        return true;
    }

    public void doCamcorderContinuousFocusCallback(boolean focusedState) {
        if (!isPausing() && isPreviewing() && getCameraDevice() != null) {
            CamLog.d(FaceDetector.TAG, "### onCotinuousFocus(): " + focusedState);
            if (focusedState) {
                setFocusState(9);
            } else {
                setFocusState(8);
            }
            updateFocusStateIndicator();
            Random rand = new Random();
            startObjectTrackingFocus(Math.abs(rand.nextInt() % LGT_Limit.PREVIEW_SIZE_WIDTH), Math.abs(rand.nextInt() % CameraConstants.DEGREE_360), Ola_ShotParam.Sampler_Complete, Ola_ShotParam.Sampler_Complete, Math.abs(rand.nextInt() % 3));
        }
    }

    public boolean checkCamcorderStop(int repeatCount, boolean useBackKey) {
        if (getVideoState() == 3 || getVideoState() == 4) {
            EffectsRecorder effectsRecorder = getPreviewController().mEffectsRecorder;
            if (VideoRecorder.isRecording() || (MultimediaProperties.isLiveEffectSupported() && effectsRecorder != null && effectsRecorder.getState() == 4)) {
                if (repeatCount != 0) {
                    return true;
                }
                if (getRecordingController().isRecordedLengthTooShort()) {
                    CamLog.d(FaceDetector.TAG, "Ignore stop recording request. It's too short.");
                    return true;
                }
                doCommandUi(Command.STOP_RECORDING);
                return true;
            }
        } else if (getVideoState() != 0) {
            CamLog.d(FaceDetector.TAG, "VideoRecorder is not in idle state. Ignore key:useBackKey = " + useBackKey + ", video state : " + getVideoState());
            return true;
        }
        return false;
    }

    public VideoFile getVideoFile() {
        return getRecordingController().getVideoFile();
    }

    public void recordingControllerShow() {
        getRecordingController().show();
    }

    public void recordingControllerHide() {
        getRecordingController().hide();
    }

    public void setScaleWidthHeight(float scaleWidthHeight) {
        getRecordingController().setScaleWidthHeight(scaleWidthHeight);
    }

    public void startRecording() {
        getRecordingController().startRecording();
    }

    public boolean isStopRecordingByMountedAction() {
        return getRecordingController().isStopRecordingByMountedAction();
    }

    public void stopRecording(boolean isFromMountedAction) {
        getRecordingController().stopRecording(isFromMountedAction);
    }

    public void pauseRecording() {
        getRecordingController().pauseRecording();
    }

    public void resumeRecording() {
        getRecordingController().resumeRecording();
    }

    public void startRecordingControllerRotation(int degree) {
        getRecordingController().startRotation(degree);
    }

    public void startAudioZoomContollerRotation(int degree) {
        getAudiozoomController().startRotation(degree);
    }

    public long getStartTime() {
        return getRecordingController().getStartTime();
    }

    public void setEndTime(long endTime) {
        getRecordingController().setEndTime(endTime);
    }

    public void resumeUpdateReordingTime() {
        getRecordingController().resumeUpdateReordingTime();
    }

    public boolean isAvailableResumeVideo() {
        return getRecordingController().isAvailableResumeVideo();
    }

    public boolean getIsFileSizeLimitReached() {
        return getRecordingController().getIsFileSizeLimitReached();
    }

    public void setIsFileSizeLimitReached(boolean set) {
        getRecordingController().setIsFileSizeLimitReached(set);
    }

    public long getRecordingSizeLimit() {
        return getRecordingController().getRecordingSizeLimit();
    }

    public long getRecordingDurationLimit() {
        return getRecordingController().getRecordingDurationLimit();
    }

    public long getVideoFileSize() {
        return getRecordingController().getVideoFileSize();
    }

    public boolean needProgressBar() {
        return getRecordingController().needProgressBar();
    }

    public boolean isRecordingControllerInit() {
        return getRecordingController().isRecordingControllerInit();
    }

    public void setRecIndicatorLayout(int width, int height, int leftMargin) {
        getRecordingController().setRecIndicatorLayout(width, height, leftMargin);
    }

    public void stopRecordingByPausing() {
        getRecordingController().stopRecordingByPausing();
    }

    public boolean isRecordedLengthTooShort() {
        return getRecordingController().isRecordedLengthTooShort();
    }

    public boolean checkUpdateThumbnail() {
        if (this.mActivity.getPostviewRequestCode() != 3 || ((getVideoState() == 3 && FunctionProperties.isAvailableLiveShot()) || (getVideoState() == 4 && MultimediaProperties.isPauseAndResumeSupported()))) {
            return true;
        }
        CamLog.d(FaceDetector.TAG, "UpdateThumbnailButton() return");
        return false;
    }

    public String getPreviousRecordModeString() {
        return this.mPreviousRecordMode;
    }

    public void setPreviousRecordModeString(String strRecordMode) {
        this.mPreviousRecordMode = strRecordMode;
    }

    public boolean getAudiozoomStart() {
        return this.mAudiozoomController.getAudiozoomStart();
    }

    public void setAudiozoomStart(boolean mAudiozoomStart) {
        this.mAudiozoomController.setAudiozoomStart(mAudiozoomStart);
    }

    public boolean getAudiozoomStartInRecording() {
        return this.mAudiozoomController.getAudioZoomStartInRecording();
    }

    public void setAudiozoomStartInRecording(boolean start) {
        this.mAudiozoomController.setAudioZoomStartInRecording(start);
    }

    public void startAudiozoom() {
        this.mAudiozoomController.startAudiozoom();
        getRecordingController().getVideoFile().setAudiozoomcontent(1);
    }

    public void updateAudiozoom(boolean updateangle, int zoomvalue) {
        if (FunctionProperties.isSupportAudiozoom()) {
            if (!updateangle) {
                int degree = getOrientationDegree();
                setAudiozoombuttonstate();
                if (Util.isEqualDegree(getResources(), degree, 90) || Util.isEqualDegree(getResources(), degree, Tag.IMAGE_DESCRIPTION)) {
                    if (getVideoFile() != null) {
                        getVideoFile().setAudiozoomExection_state(true);
                    }
                    getQuickButtonController().setMenuEnable(2, false, true);
                    changeButtonResource(2, R.drawable.selector_btn_mode_audio_zoom_off_button);
                    if (getAudiozoomStart()) {
                        getZoomController().hideSettingZoomControl(true);
                        stopAudiozoom();
                        toast(String.format(getString(R.string.audio_zoom_warning), new Object[0]));
                    }
                } else if (!isEffectsCamcorderActive() && getAudiozoomStartInRecording() && getVideoState() == 3 && ((getStartrecordingdegree() == 0 && Util.isEqualDegree(getResources(), degree, 0)) || (getStartrecordingdegree() == MediaProviderUtils.ROTATION_180 && Util.isEqualDegree(getResources(), degree, MediaProviderUtils.ROTATION_180)))) {
                    if (getVideoFile() != null) {
                        getVideoFile().setAudiozoomExection_state(false);
                    }
                    if (FunctionProperties.isSupportObjectTracking() && getObjectTrackingState() != 0) {
                        startObjectTrackingFocus(0, 0, 0, 0, 0);
                    }
                    startAudiozoom();
                    changeButtonResource(2, R.drawable.selector_btn_mode_audio_zoom_on_button);
                }
            }
            if (getHeadsetstate() != 2) {
                this.mAudiozoomController.updateAudiozoomvalue(updateangle, zoomvalue);
            }
        }
    }

    public void stopAudiozoom() {
        this.mAudiozoomController.stopAudiozoom();
    }

    public void setAudiozoombuttonstate() {
        this.mAudiozoomController.setAudiozoombuttonstate();
    }

    public void setStartrecordingdegree(int degree) {
        this.mAudiozoomController.setStartrecordingdegree(degree);
    }

    public int getStartrecordingdegree() {
        return this.mAudiozoomController.getStartrecordingdegree();
    }

    public String getAudiozoomvalue() {
        return this.mAudiozoomController.getmAudiozoomvalue();
    }

    public void setAudiozoomvalue(String value) {
        this.mAudiozoomController.setmAudiozoomvalue(value);
    }

    public void setAudioZoomGuideViewLayout(int width, int height, int marginLeft) {
        if (this.mAudiozoomController != null) {
            this.mAudiozoomController.setAudioZoomGuideViewLayout(width, height, marginLeft);
        }
    }

    public void hideAudiozoomready() {
        CamLog.v(FaceDetector.TAG, "===> Hide Audiozoom ready image");
        if (this.mHandler.hasMessages(8)) {
            this.mHandler.removeMessages(8);
        }
    }

    public void setForced_audiozoom(boolean isEnable) {
        if (getApplicationMode() == 1 && getCameraMode() == 0) {
            this.mAudiozoomController.setForced_audiozoom(isEnable);
        }
    }

    public boolean isAudiozoom_ExceptionCase(boolean checkRotation) {
        if (!FunctionProperties.isSupportAudiozoom()) {
            return true;
        }
        int headsetState = getHeadsetstate();
        int nStartdegree = this.mAudiozoomController.getStartrecordingdegree();
        boolean isMMS = MmsProperties.isAvailableMmsResolution(getContentResolver(), getPreviewSizeOnDevice());
        String currentVideoSize = getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
        if (!isLiveEffectActive() && !isDualRecordingActive() && !isSmartZoomRecordingActive() && !"176x144".equalsIgnoreCase(currentVideoSize) && !"320x240".equalsIgnoreCase(currentVideoSize) && !isMMS && !isAudiozoomExceptionOrientation(checkRotation) && headsetState != 2 && nStartdegree != 90 && nStartdegree != Tag.IMAGE_DESCRIPTION && !checkSlowMotionMode()) {
            return false;
        }
        if (getVideoFile() == null) {
            return true;
        }
        getVideoFile().setAudiozoomExection_state(true);
        return true;
    }

    private boolean isAudiozoomExceptionOrientation(boolean checkRotation) {
        CamLog.d(FaceDetector.TAG, "isAudiozoomExceptionOrientation : video state = " + getVideoState());
        if ((getSubMenuMode() != 0 && getVideoState() == 0) || !checkRotation) {
            return false;
        }
        if (Util.isEqualDegree(getResources(), getOrientationDegree(), 90) || Util.isEqualDegree(getResources(), getOrientationDegree(), Tag.IMAGE_DESCRIPTION)) {
            return true;
        }
        return false;
    }

    public void setVideoFlash(boolean on) {
        if (getCameraDevice() != null) {
            Parameters parameters = getParameters();
            String flashMode = getSettingValue(Setting.KEY_FLASH);
            if (!isPausing() && !isFinishingActivity() && parameters != null && Common.isSupported(parameters, flashMode)) {
                if (!on || (!CameraConstants.SMART_MODE_ON.equals(flashMode) && !LGT_Limit.ISP_AUTOMODE_AUTO.equals(flashMode))) {
                    parameters.setFlashMode(CameraConstants.SMART_MODE_OFF);
                    setParameters(parameters);
                } else if (CameraConstants.SMART_MODE_OFF.equals(parameters.getFlashMode())) {
                    if (!FunctionProperties.isSupportedVideoFlashAuto() || !LGT_Limit.ISP_AUTOMODE_AUTO.equals(flashMode)) {
                        parameters.setFlashMode(CameraConstants.FLASH_TORCH);
                    } else if (Common.isLowLuminance(parameters, false)) {
                        parameters.setFlashMode(CameraConstants.FLASH_TORCH);
                    } else {
                        parameters.setFlashMode(CameraConstants.SMART_MODE_OFF);
                    }
                    setParameters(parameters);
                }
            }
        }
    }

    public boolean checkSlowMotionMode() {
        ListPreference pref = findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
        String sVideoFps = CameraConstants.SMART_MODE_OFF;
        if (pref != null) {
            sVideoFps = pref.getExtraInfo3();
            if (!CameraConstants.SMART_MODE_OFF.equals(sVideoFps) && Integer.parseInt(sVideoFps) >= DialogCreater.DIALOG_ID_HELP_NIGHT && checkShotModeForZoomInOut()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkShotModeForZoomInOut() {
        String videoRecordMode = getSettingValue(Setting.KEY_VIDEO_RECORD_MODE);
        if (CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(videoRecordMode) || CameraConstants.TYPE_RECORDMODE_DUAL.equals(videoRecordMode) || CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(videoRecordMode)) {
            return false;
        }
        return true;
    }

    public void setPreviousAutoReviewValue() {
        if (ProjectVariables.isSupportedAutoReview() && AppControlUtil.checkGuestModeAndAppDisabled(getContentResolver(), true, 1)) {
            this.mPrevieousVideoAutoReviewValue = getSettingValue(Setting.KEY_VIDEO_AUTO_REVIEW);
            setSetting(Setting.KEY_VIDEO_AUTO_REVIEW, CameraConstants.SMART_MODE_OFF);
            CamLog.d(FaceDetector.TAG, "Because of guest mode and gallery not exist, so previous AutoReview value is saved. Saved value : " + this.mPrevieousVideoAutoReviewValue);
        }
    }

    public void restoreAutoReviewValue() {
        if (ProjectVariables.isSupportedAutoReview() && AppControlUtil.checkGuestModeAndAppDisabled(getContentResolver(), true, 1)) {
            CamLog.d(FaceDetector.TAG, "Restore previous AutoReviewValue : " + this.mPrevieousVideoAutoReviewValue);
            if (this.mPrevieousVideoAutoReviewValue != null) {
                this.mSettingController.setSetting(Setting.KEY_VIDEO_AUTO_REVIEW, this.mPrevieousVideoAutoReviewValue);
            }
        }
    }

    public boolean getEffectRecorderPausing() {
        return this.mPreviewController.getEffectRecorderPausing();
    }

    public boolean checkFaceDetectionNoUI() {
        return false;
    }

    public void changeLiveSnapshotMaxFileSize(long size) {
        if (this.mRecordingController != null) {
            this.mRecordingController.changeMaxFileSize(size);
        }
    }
}
