package com.lge.camera.module;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.net.Uri;
import com.lge.camera.ControllerFunction;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.properties.StorageProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.ExifUtil;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.SecureImageUtil;
import com.lge.camera.util.Util;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ContinuousShot extends Module {
    private static final int CONTINUOUS_SHOT_TIME;
    private int deviceDegree;
    private long mCheckOneShotCallbackTime;
    private boolean mContiShotErrorOccur;
    private int mContinueShotCount;
    private Timer mContinuousShot;
    private Thread mContinuousShotSaveImageThread;
    private int mCount;
    private CountDownLatch mInProgressLatch;
    private boolean mIsContinuousShotSaveImageThreadRunning;
    private boolean mIsContinuousShotStopUserAction;
    private int mPushContineShotCount;
    private BlockingQueue<Integer> mQueueContinueOrientation;
    private BlockingQueue<byte[]> mQueueContinueShot;
    private boolean mSound_isPlayed;
    private int previewHeight;
    private int previewWidth;

    private class ContinuousShotSaveThread implements Runnable {
        private ContinuousShotSaveThread() {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r15 = this;
            r14 = 0;
            r2 = com.lge.camera.module.ContinuousShot.this;
            r2 = r2.mGet;
            if (r2 != 0) goto L_0x0008;
        L_0x0007:
            return;
        L_0x0008:
            r3 = 0;
            r8 = 0;
            r6 = 0;
            r10 = 0;
            r2 = "CameraApp";
            r4 = "[ContinuousShotSaveThread]-start";
            com.lge.camera.util.CamLog.d(r2, r4);
            r2 = com.lge.camera.module.ContinuousShot.this;
            r2 = r2.mGet;
            r2 = r2.getImageListUri();
            r2 = r2.size();
            if (r2 <= 0) goto L_0x0035;
        L_0x0022:
            r2 = com.lge.camera.module.ContinuousShot.this;
            r2 = r2.mGet;
            r2 = r2.getImageListUri();
            r4 = com.lge.camera.module.ContinuousShot.this;
            r4 = r4.mGet;
            r4 = r4.getImageListUri();
            r2.removeAll(r4);
        L_0x0035:
            r2 = com.lge.camera.module.ContinuousShot.this;
            r2 = r2.mIsContinuousShotSaveImageThreadRunning;
            if (r2 == 0) goto L_0x0055;
        L_0x003d:
            r10 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x00a7 }
            r2 = com.lge.camera.module.ContinuousShot.this;	 Catch:{ Exception -> 0x00a7 }
            r2 = r2.mQueueContinueShot;	 Catch:{ Exception -> 0x00a7 }
            if (r2 == 0) goto L_0x0055;
        L_0x0049:
            r2 = com.lge.camera.module.ContinuousShot.this;	 Catch:{ Exception -> 0x00a7 }
            r2 = r2.mQueueContinueShot;	 Catch:{ Exception -> 0x00a7 }
            r2 = r2.size();	 Catch:{ Exception -> 0x00a7 }
            if (r2 > 0) goto L_0x008b;
        L_0x0055:
            r2 = com.lge.camera.module.ContinuousShot.this;
            r2.mIsContinuousShotSaveImageThreadRunning = r14;
            r2 = com.lge.camera.module.ContinuousShot.this;
            r2.notifySaveDone();
            if (r8 == 0) goto L_0x007d;
        L_0x0061:
            r2 = com.lge.camera.module.ContinuousShot.this;
            r2 = r2.mGet;
            r2 = r2.getSavedImageUri();
            if (r2 == 0) goto L_0x007d;
        L_0x006b:
            r2 = com.lge.camera.module.ContinuousShot.this;
            r2 = r2.mGet;
            r2 = r2.isPausing();
            if (r2 != 0) goto L_0x0007;
        L_0x0075:
            r2 = com.lge.camera.module.ContinuousShot.this;
            r2 = r2.mContiShotErrorOccur;
            if (r2 != 0) goto L_0x0007;
        L_0x007d:
            r2 = com.lge.camera.module.ContinuousShot.this;
            r2.clearSaveImageQueue();
            r2 = "CameraApp";
            r4 = "[ContinuousShotSaveThread]-end";
            com.lge.camera.util.CamLog.d(r2, r4);
            goto L_0x0007;
        L_0x008b:
            r2 = com.lge.camera.module.ContinuousShot.this;	 Catch:{ Exception -> 0x00a7 }
            r2 = r2.mContiShotErrorOccur;	 Catch:{ Exception -> 0x00a7 }
            if (r2 == 0) goto L_0x00b9;
        L_0x0093:
            r2 = "CameraApp";
            r4 = "Quit save cont.shot thread";
            r5 = 0;
            r5 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x00a7 }
            r4 = java.lang.String.format(r4, r5);	 Catch:{ Exception -> 0x00a7 }
            com.lge.camera.util.CamLog.d(r2, r4);	 Catch:{ Exception -> 0x00a7 }
            r2 = com.lge.camera.module.ContinuousShot.this;	 Catch:{ Exception -> 0x00a7 }
            r2.clearSaveImageQueue();	 Catch:{ Exception -> 0x00a7 }
            goto L_0x0055;
        L_0x00a7:
            r9 = move-exception;
            r2 = "CameraApp";
            r4 = "Exception occured in ContinuousShotSaveThread";
            r5 = new java.lang.Object[r14];
            r4 = java.lang.String.format(r4, r5);
            com.lge.camera.util.CamLog.e(r2, r4);
            r9.printStackTrace();
            goto L_0x0055;
        L_0x00b9:
            r2 = com.lge.camera.module.ContinuousShot.this;	 Catch:{ Exception -> 0x00a7 }
            r2 = r2.mQueueContinueOrientation;	 Catch:{ Exception -> 0x00a7 }
            r2 = r2.take();	 Catch:{ Exception -> 0x00a7 }
            r2 = (java.lang.Integer) r2;	 Catch:{ Exception -> 0x00a7 }
            r6 = r2.intValue();	 Catch:{ Exception -> 0x00a7 }
            r2 = com.lge.camera.module.ContinuousShot.this;	 Catch:{ Exception -> 0x00a7 }
            r2 = r2.mQueueContinueShot;	 Catch:{ Exception -> 0x00a7 }
            r2 = r2.take();	 Catch:{ Exception -> 0x00a7 }
            r0 = r2;
            r0 = (byte[]) r0;	 Catch:{ Exception -> 0x00a7 }
            r3 = r0;
            r2 = com.lge.camera.module.ContinuousShot.this;	 Catch:{ Exception -> 0x00a7 }
            r2 = r2.mGet;	 Catch:{ Exception -> 0x00a7 }
            r2 = r2.getImageHandler();	 Catch:{ Exception -> 0x00a7 }
            r4 = com.lge.camera.module.ContinuousShot.this;	 Catch:{ Exception -> 0x00a7 }
            r4 = r4.previewWidth;	 Catch:{ Exception -> 0x00a7 }
            r5 = com.lge.camera.module.ContinuousShot.this;	 Catch:{ Exception -> 0x00a7 }
            r5 = r5.previewHeight;	 Catch:{ Exception -> 0x00a7 }
            r7 = 95;
            r8 = r2.convertYuvToJpeg(r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x00a7 }
            r2 = "CameraApp";
            r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00a7 }
            r4.<init>();	 Catch:{ Exception -> 0x00a7 }
            r5 = "converting YUV to JPEG time = ";
            r4 = r4.append(r5);	 Catch:{ Exception -> 0x00a7 }
            r12 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x00a7 }
            r12 = r12 - r10;
            r4 = r4.append(r12);	 Catch:{ Exception -> 0x00a7 }
            r5 = "ms";
            r4 = r4.append(r5);	 Catch:{ Exception -> 0x00a7 }
            r4 = r4.toString();	 Catch:{ Exception -> 0x00a7 }
            com.lge.camera.util.CamLog.d(r2, r4);	 Catch:{ Exception -> 0x00a7 }
            r2 = "CameraApp";
            r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00a7 }
            r4.<init>();	 Catch:{ Exception -> 0x00a7 }
            r5 = "dqueue orientation : ";
            r4 = r4.append(r5);	 Catch:{ Exception -> 0x00a7 }
            r4 = r4.append(r6);	 Catch:{ Exception -> 0x00a7 }
            r4 = r4.toString();	 Catch:{ Exception -> 0x00a7 }
            com.lge.camera.util.CamLog.d(r2, r4);	 Catch:{ Exception -> 0x00a7 }
            r2 = com.lge.camera.module.ContinuousShot.this;	 Catch:{ Exception -> 0x00a7 }
            r2 = r2.saveContiShotImage2(r8, r6);	 Catch:{ Exception -> 0x00a7 }
            if (r2 != 0) goto L_0x0035;
        L_0x0134:
            r2 = "CameraApp";
            r4 = "Error occured while saving cont.shot.";
            r5 = 0;
            r5 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x00a7 }
            r4 = java.lang.String.format(r4, r5);	 Catch:{ Exception -> 0x00a7 }
            com.lge.camera.util.CamLog.d(r2, r4);	 Catch:{ Exception -> 0x00a7 }
            r2 = com.lge.camera.module.ContinuousShot.this;	 Catch:{ Exception -> 0x00a7 }
            r4 = 1;
            r2.mContiShotErrorOccur = r4;	 Catch:{ Exception -> 0x00a7 }
            goto L_0x0055;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.module.ContinuousShot.ContinuousShotSaveThread.run():void");
        }
    }

    private class OneShotPreviewCallback implements PreviewCallback {
        private OneShotPreviewCallback() {
        }

        public void onPreviewFrame(byte[] data, Camera camera) {
            CamLog.d(FaceDetector.TAG, "receive a frame ");
            if (data == null) {
                CamLog.w(FaceDetector.TAG, String.format("data of onPreviewFrame is null", new Object[0]));
            } else if (ContinuousShot.this.mContinueShotCount < ContinuousShot.this.mPushContineShotCount || ContinuousShot.this.mQueueContinueShot == null) {
                CamLog.d(FaceDetector.TAG, String.format("mQueueContinueShot:%s", new Object[]{ContinuousShot.this.mQueueContinueShot}));
                CamLog.d(FaceDetector.TAG, String.format("Unknown error occured. stop conti. shot.", new Object[0]));
                ContinuousShot.this.stopByUserAction();
                ContinuousShot.this.mGet.setInCaptureProgress(false);
            } else {
                try {
                    if (ContinuousShot.this.mGet.isPausing() || ContinuousShot.this.mContiShotErrorOccur) {
                        CamLog.d(FaceDetector.TAG, String.format("Cont. shot onPreviewFrame error: pausing(%b) error flag(%b)", new Object[]{Boolean.valueOf(ContinuousShot.this.mGet.isPausing()), Boolean.valueOf(ContinuousShot.this.mContiShotErrorOccur)}));
                    } else if (data != null) {
                        ContinuousShot.this.mQueueContinueShot.put(data);
                        ContinuousShot.this.mQueueContinueOrientation.put(Integer.valueOf(ContinuousShot.this.deviceDegree));
                        ContinuousShot.this.mContinueShotCount = ContinuousShot.this.mContinueShotCount + 1;
                        CamLog.d(FaceDetector.TAG, "### TIME_CHECK : oneShotPreviewCallbackTime = " + (System.currentTimeMillis() - ContinuousShot.this.mCheckOneShotCallbackTime) + "ms");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static {
        CONTINUOUS_SHOT_TIME = ProjectVariables.getContinuousShotTime();
    }

    public ContinuousShot(ControllerFunction function) {
        super(function);
        this.mContinueShotCount = 0;
        this.mPushContineShotCount = 0;
        this.mQueueContinueShot = null;
        this.mIsContinuousShotSaveImageThreadRunning = false;
        this.mIsContinuousShotStopUserAction = false;
        this.mQueueContinueOrientation = null;
        this.mContinuousShotSaveImageThread = null;
        this.mContiShotErrorOccur = false;
        this.mCount = 0;
        this.mContinuousShot = null;
        this.deviceDegree = 0;
        this.mSound_isPlayed = false;
        this.mCheckOneShotCallbackTime = 0;
        this.mInProgressLatch = null;
    }

    public boolean checkCurrentShotMode() {
        return this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CONTINUOUS);
    }

    public boolean isRunning() {
        return this.mIsContinuousShotSaveImageThreadRunning;
    }

    public boolean takePicture() {
        CamLog.d(FaceDetector.TAG, "[Module]ContinuousShot::takePicture");
        this.mGet.getCameraDevice().setPreviewCallback(null);
        this.mGet.setSwitcherVisible(false);
        if (ModelProperties.is3dSupportedModel()) {
            this.mGet.set3DSwitchVisible(false);
        }
        this.mGet.setMainButtonDisable();
        this.mGet.setThumbnailButtonVisibility(4);
        this.mGet.clearFocusState();
        this.mGet.hideFocus();
        this.mCount = 0;
        this.mContinueShotCount = 0;
        if (this.mGet.getAvailablePictureCount() < 6) {
            this.mCount = 6 - ((int) this.mGet.getAvailablePictureCount());
            this.mContinueShotCount = 6 - ((int) this.mGet.getAvailablePictureCount());
        }
        this.mPushContineShotCount = 0;
        this.mContiShotErrorOccur = false;
        this.deviceDegree = this.mGet.getDeviceDegree();
        initSaveImageQueue();
        ListPreference shotModePref = this.mGet.getPreferenceGroup().findPreference(Setting.KEY_CAMERA_SHOT_MODE);
        if (shotModePref == null) {
            CamLog.d(FaceDetector.TAG, "shotModePref is null");
            return false;
        }
        int[] pictureSize = Util.SizeString2WidthHeight(shotModePref.getExtraInfo());
        this.previewWidth = pictureSize[0];
        this.previewHeight = pictureSize[1];
        this.mIsContinuousShotSaveImageThreadRunning = true;
        this.mIsContinuousShotStopUserAction = false;
        this.mContinuousShotSaveImageThread = null;
        this.mGet.setContinuousShotAlived(true);
        this.mGet.setImageRotationDegree(this.mGet.getDeviceDegree());
        try {
            if (FunctionProperties.isCafSupported(0, 0) && this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE).equals(CameraConstants.TYPE_SHOTMODE_CONTINUOUS)) {
                Parameters parameters = this.mGet.getParameters();
                parameters.setFocusMode(LGT_Limit.ISP_AUTOMODE_AUTO);
                this.mGet.setParameters(parameters);
                CamLog.d(FaceDetector.TAG, "### setFocusMode-auto");
            }
        } catch (RuntimeException e) {
            CamLog.e(FaceDetector.TAG, "RuntimeException : ", e);
        }
        this.mContinuousShot = new Timer(Module.CONTINUOUS_SHOT);
        this.mSound_isPlayed = false;
        TimerTask taskContinuous = new TimerTask() {
            public void run() {
                CamLog.d(FaceDetector.TAG, "taskContinuous run()-start");
                if (ContinuousShot.this.mGet == null || ContinuousShot.this.mContiShotErrorOccur) {
                    CamLog.d(FaceDetector.TAG, String.format("continuous shot thread return: error flag", new Object[0]));
                    ContinuousShot.this.stopByUserAction();
                    return;
                }
                if (ContinuousShot.this.mCount < 6 && !ContinuousShot.this.mIsContinuousShotStopUserAction) {
                    getTakeImagesForContinuousShot();
                }
                if (checkAvailablePictureCount()) {
                    if ((ContinuousShot.this.mContinueShotCount >= 6 && ContinuousShot.this.mCount >= 6) || ContinuousShot.this.mIsContinuousShotStopUserAction) {
                        CamLog.d(FaceDetector.TAG, String.format("Save cont. shot pictures.", new Object[0]));
                        ContinuousShot.this.stopContinuousShot();
                        ContinuousShot.this.mGet.runOnUiThread(new Runnable() {
                            public void run() {
                                ContinuousShot.this.mGet.removePostRunnable(this);
                                ContinuousShot.this.mGet.showProgressDialog();
                            }
                        });
                        try {
                            ContinuousShot.this.mContinuousShotSaveImageThread = new Thread(new ContinuousShotSaveThread());
                            ContinuousShot.this.mContinuousShotSaveImageThread.start();
                        } catch (Exception e) {
                            ContinuousShot.this.stopSaveImageThread();
                            e.printStackTrace();
                        }
                        if (!ContinuousShot.this.mContiShotErrorOccur) {
                            if (ContinuousShot.this.mContinuousShotSaveImageThread != null && ContinuousShot.this.mContinuousShotSaveImageThread.isAlive()) {
                                waitContinuousShotSave();
                            }
                            ContinuousShot.this.stopSaveImageThread();
                        }
                        ContinuousShot.this.mGet.runOnUiThread(new Runnable() {
                            public void run() {
                                ContinuousShot.this.mGet.removePostRunnable(this);
                                ContinuousShot.this.mGet.deleteProgressDialog();
                            }
                        });
                        finishContinuousShot();
                    }
                    CamLog.d(FaceDetector.TAG, "taskContinuous run()-end");
                }
            }

            private void waitContinuousShotSave() {
                while (ContinuousShot.this.mQueueContinueShot != null && ContinuousShot.this.mIsContinuousShotSaveImageThreadRunning) {
                    try {
                        CamLog.d(FaceDetector.TAG, "ContinuousShot Running :" + ContinuousShot.this.mIsContinuousShotSaveImageThreadRunning);
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }

            private void finishContinuousShot() {
                try {
                    ContinuousShot.this.mGet.setInCaptureProgress(false);
                    if (StorageProperties.isExternalMemoryOnly() && ContinuousShot.this.mGet.isExternalStorageRemoved()) {
                        ContinuousShot.this.mGet.getImageListUri().clear();
                        ContinuousShot.this.mGet.getImageListRotation().clear();
                    }
                    if (ContinuousShot.this.mGet.getImageListUri().size() <= 0 || ContinuousShot.this.mGet.isPausing()) {
                        ContinuousShot.this.stopContinuousShotThread();
                        if (ContinuousShot.this.mContinueShotCount > 0) {
                            ContinuousShot.this.mGet.runOnUiThread(new Runnable() {
                                public void run() {
                                    if (ContinuousShot.this.mGet != null) {
                                        ContinuousShot.this.mGet.removePostRunnable(this);
                                        ContinuousShot.this.mGet.restartPreview(null, true);
                                    }
                                }
                            });
                        }
                        ContinuousShot.this.mGet.doCommandUi(Command.DISPLAY_PREVIEW);
                        return;
                    }
                    ContinuousShot.this.stopContinuousShotThread();
                    if (ContinuousShot.this.mGet.checkAutoReviewOff(false)) {
                        ContinuousShot.this.mGet.doCommandUi(Command.ON_DELAY_OFF);
                    } else {
                        ContinuousShot.this.mGet.runOnUiThread(new Runnable() {
                            public void run() {
                                if (ContinuousShot.this.mGet != null) {
                                    ContinuousShot.this.mGet.removePostRunnable(this);
                                    if (!ContinuousShot.this.mGet.checkAutoReviewForQuickView()) {
                                        ContinuousShot.this.mGet.doCommandUi(Command.DISPLAY_CAMERA_POSTVIEW);
                                    }
                                }
                            }
                        });
                    }
                } catch (NullPointerException e) {
                    CamLog.e(FaceDetector.TAG, "taskContinuous run() - failed : " + e);
                }
            }

            private void getTakeImagesForContinuousShot() {
                if (ContinuousShot.this.mGet.getCameraDevice() != null) {
                    try {
                        if (ProjectVariables.useContinuousSound()) {
                            if (!ContinuousShot.this.mSound_isPlayed) {
                                ContinuousShot.this.mGet.playContinuousShutterSound();
                            }
                            ContinuousShot.this.mSound_isPlayed = true;
                        } else {
                            ContinuousShot.this.mGet.playContinuousShutterSound();
                        }
                        if (ContinuousShot.this.mCount != ContinuousShot.this.mContinueShotCount) {
                            int waitcnt = 0;
                            while (ContinuousShot.this.mCount != ContinuousShot.this.mContinueShotCount && !ContinuousShot.this.mIsContinuousShotStopUserAction) {
                                try {
                                    Thread.sleep(10);
                                    waitcnt++;
                                    if (waitcnt >= 100) {
                                        ContinuousShot.this.mIsContinuousShotStopUserAction = true;
                                    }
                                    CamLog.d(FaceDetector.TAG, "continuous waitcnt = " + waitcnt);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        ContinuousShot.this.mGet.getCameraDevice().setOneShotPreviewCallback(new OneShotPreviewCallback());
                        ContinuousShot.this.mCount = ContinuousShot.this.mCount + 1;
                        CamLog.d(FaceDetector.TAG, "setOneShotPreviewCallback : " + ContinuousShot.this.mCount);
                        ContinuousShot.this.mCheckOneShotCallbackTime = System.currentTimeMillis();
                        return;
                    } catch (Exception e2) {
                        CamLog.d(FaceDetector.TAG, "Camera Device is null...", e2);
                        return;
                    }
                }
                CamLog.d(FaceDetector.TAG, String.format("Camera device is null. One shot callback is not set.", new Object[0]));
            }

            private boolean checkAvailablePictureCount() {
                if (ContinuousShot.this.mGet.getAvailablePictureCount() >= 1) {
                    return true;
                }
                ContinuousShot.this.mGet.stopSoundContinuous();
                ContinuousShot.this.stopContinuousShot();
                CamLog.d(FaceDetector.TAG, String.format("Available picture count:%d", new Object[]{Long.valueOf(ContinuousShot.this.mGet.getAvailablePictureCount())}));
                FileNamer.get().setErrorFeedback(ContinuousShot.this.mGet.getApplicationMode());
                if (ContinuousShot.this.mGet.getImageListUri().size() > 0) {
                    ContinuousShot.this.mGet.getImageListUri().clear();
                }
                ContinuousShot.this.mGet.setInCaptureProgress(false);
                if (!ContinuousShot.this.mGet.isPausing()) {
                    ContinuousShot.this.mGet.restartPreview(null, true);
                }
                ContinuousShot.this.mContiShotErrorOccur = false;
                return false;
            }
        };
        initLatch();
        this.mContinuousShot.scheduleAtFixedRate(taskContinuous, (long) CONTINUOUS_SHOT_TIME, (long) CONTINUOUS_SHOT_TIME);
        this.mGet.getHandler().sendEmptyMessageDelayed(3, 500);
        return true;
    }

    public void stopContinuousShot() {
        CamLog.d(FaceDetector.TAG, String.format("stopContinuousShot()", new Object[0]));
        if (this.mContinuousShot != null) {
            this.mContinuousShot.purge();
            this.mContinuousShot.cancel();
            this.mContinuousShot = null;
        }
    }

    private boolean saveContiShotImage2(byte[] data, int rotation) {
        CamLog.d(FaceDetector.TAG, "[saveContiShotImage2] mPreviewing [" + this.mGet.isPreviewing() + "]");
        CamLog.d(FaceDetector.TAG, "[saveContiShotImage2] mPausing [" + this.mGet.isPausing() + "] mContiShotErrorOccur [" + this.mContiShotErrorOccur + "]");
        String fileName = checkErrorAndGetFileName();
        if (fileName == null) {
            return false;
        }
        Uri tUri;
        String filePath = this.mGet.getCurrentStorageDirectory();
        byte[] exifData = ExifUtil.setNewExifInformation(data, 0, this.mGet.getCurrentLocation(), this.mGet.getParameters().getFocalLength());
        long startTime = System.currentTimeMillis();
        long dateTaken = System.currentTimeMillis();
        boolean ret = false;
        if (exifData != null) {
            try {
                tUri = this.mGet.getImageHandler().addJpegImage(this.mGet.getContentResolver(), fileName, dateTaken, exifData, this.mGet.getCurrentLocation(), filePath, fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT, 0, this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE).equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS));
            } catch (Throwable ise) {
                CamLog.e(FaceDetector.TAG, "IllegalStateException while compressing image.", ise);
                stopContinuousShotThread();
                ret = false;
            } catch (Exception ex) {
                CamLog.e(FaceDetector.TAG, "Exception while compressing image.", ex);
                ret = false;
            }
        } else {
            tUri = this.mGet.getImageHandler().addJpegImage(this.mGet.getContentResolver(), fileName, dateTaken, data, this.mGet.getCurrentLocation(), filePath, fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT, 0, this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE).equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS));
        }
        CamLog.d(FaceDetector.TAG, String.format("saved cont. shot uri:%s", new Object[]{tUri}));
        if (tUri == null) {
            return false;
        }
        this.mGet.setSavedImageUri(tUri);
        Uri savedImageUri = this.mGet.getSavedImageUri();
        if (this.mGet.getImageListUri() == null) {
            return false;
        }
        if (savedImageUri != null) {
            this.mGet.getImageListUri().add(savedImageUri);
            CamLog.d(FaceDetector.TAG, String.format("Set last thumbnail uri:%s", new Object[]{savedImageUri}));
            this.mGet.setLastThumb(this.mGet.getSavedImageUri(), true);
            this.mGet.updateThumbnailButton();
            this.mGet.getImageListRotation().add(Integer.valueOf(rotation));
            this.mPushContineShotCount++;
            CamLog.d(FaceDetector.TAG, "SAVE IMAGE pushContineShotCount[" + this.mPushContineShotCount + "] " + "mImageListUri.SIZE()[" + this.mGet.getImageListUri().size() + "]");
            ret = true;
        } else {
            CamLog.d(FaceDetector.TAG, String.format("saveContiShotImage2() error: savedImageUri is null", new Object[0]));
        }
        if (this.mGet.isPausing()) {
            return false;
        }
        CamLog.d(FaceDetector.TAG, "ret = " + ret);
        if (ret) {
            Util.broadcastNewPicture(this.mGet.getActivity(), this.mGet.getSaveURI());
            if (Common.useSecureLockImage() || AppControlUtil.checkGuestModeAndAppDisabled(this.mGet.getContentResolver(), true, 1)) {
                SecureImageUtil.get().addSecureLockImageUri(this.mGet.getSaveURI());
            }
            this.mGet.setSavedFileName(fileName);
            Util.requestUpBoxBackupPhoto(this.mGet.getActivity(), filePath + fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT, this.mGet.getSettingValue(Setting.KEY_UPLUS_BOX).equals(CameraConstants.SMART_MODE_ON));
        }
        CamLog.d(FaceDetector.TAG, "save continuous shot image elapse time = " + (System.currentTimeMillis() - startTime) + "ms");
        return ret;
    }

    private String checkErrorAndGetFileName() {
        if (this.mContiShotErrorOccur) {
            return null;
        }
        return FileNamer.get().getFileNewName(this.mGet.getApplicationContext(), this.mGet.getApplicationMode(), this.mGet.getCurrentStorage(), this.mGet.getCurrentStorageDirectory(), false, this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), false);
    }

    public void stopContinuousShotThread() {
        CamLog.d(FaceDetector.TAG, "stopContinuousShotThread");
        this.mGet.setContinuousShotAlived(false);
        stopContinuousShot();
        stopSaveImageThread();
        if (!this.mGet.isPausing()) {
            if (this.mGet.getCameraDevice() != null) {
                this.mGet.getCameraDevice().setOneShotPreviewCallback(null);
            }
            if (this.mGet.getCameraDevice() != null) {
                this.mGet.getCameraDevice().setPreviewCallback(null);
            }
        }
        if (FunctionProperties.isVoiceShutter()) {
            String mVoiceShutterValue = this.mGet.getSettingValue(Setting.KEY_VOICESHUTTER);
            if (mVoiceShutterValue != null && mVoiceShutterValue.equals(CameraConstants.SMART_MODE_ON)) {
                this.mGet.doCommandUi(Command.SET_VOICE_SHUTTER);
            }
        }
        if (FunctionProperties.isCafSupported(0, 0) && this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CONTINUOUS)) {
            try {
                if (!(this.mGet.getCameraDevice() == null || this.mGet.getLGParam() == null)) {
                    if (ModelProperties.isMTKChipset()) {
                        this.mGet.initFocusAreas();
                        this.mGet.getCameraDevice().cancelAutoFocus();
                    }
                    LGParameters lgParameters = this.mGet.getLGParam();
                    String defaultFocusMode = this.mGet.getDefaultFocusModeParameterForMultiWindowAF(lgParameters);
                    lgParameters.getParameters().setFocusMode(defaultFocusMode);
                    this.mGet.setParameters(lgParameters.getParameters());
                    CamLog.d(FaceDetector.TAG, "### setFocusMode-" + defaultFocusMode);
                }
            } catch (RuntimeException e) {
                CamLog.e(FaceDetector.TAG, "RuntimeException : ", e);
            }
            this.mGet.setShutterButtonClicked(false);
        }
    }

    private void stopSaveImageThread() {
        CamLog.d(FaceDetector.TAG, "stopSaveImageThread");
        this.mIsContinuousShotSaveImageThreadRunning = false;
        notifySaveDone();
        clearSaveImageQueue();
        if (this.mContinuousShotSaveImageThread != null) {
            this.mContinuousShotSaveImageThread.interrupt();
        }
        this.mContinuousShotSaveImageThread = null;
    }

    public void stopByUserAction() {
        if (this.mGet != null) {
            this.mGet.stopSoundContinuous();
        }
        this.mIsContinuousShotStopUserAction = true;
    }

    private void initSaveImageQueue() {
        if (this.mQueueContinueShot == null) {
            this.mQueueContinueShot = new LinkedBlockingQueue();
            this.mQueueContinueOrientation = new LinkedBlockingQueue();
            return;
        }
        this.mQueueContinueShot.clear();
        this.mQueueContinueShot = null;
        this.mQueueContinueShot = new LinkedBlockingQueue();
        this.mQueueContinueOrientation.clear();
        this.mQueueContinueOrientation = null;
        this.mQueueContinueOrientation = new LinkedBlockingQueue();
    }

    private void clearSaveImageQueue() {
        CamLog.d(FaceDetector.TAG, "clearSaveImageQueue");
        if (this.mQueueContinueShot != null) {
            this.mQueueContinueShot.clear();
            this.mQueueContinueShot = null;
        }
        if (this.mQueueContinueOrientation != null) {
            this.mQueueContinueOrientation.clear();
            this.mQueueContinueOrientation = null;
        }
    }

    private void initLatch() {
        CamLog.d(FaceDetector.TAG, String.format("Init cont. shot latch", new Object[0]));
        this.mInProgressLatch = new CountDownLatch(1);
    }

    private void notifySaveDone() {
        if (this.mInProgressLatch != null) {
            this.mInProgressLatch.countDown();
        } else {
            CamLog.w(FaceDetector.TAG, String.format("Cont. shot latch null! Ignore notifySaveDone().", new Object[0]));
        }
    }

    public void waitForSaveDone() {
        if (this.mInProgressLatch != null) {
            boolean saveDone = false;
            try {
                saveDone = this.mInProgressLatch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                CamLog.e(FaceDetector.TAG, String.format("Wait for cont. shot done has interrupted!", new Object[0]));
                e.printStackTrace();
            }
            if (saveDone) {
                CamLog.d(FaceDetector.TAG, String.format("Cont. shot save done.", new Object[0]));
                return;
            } else {
                CamLog.w(FaceDetector.TAG, String.format("Waiting for cont. shot done timeout!", new Object[0]));
                return;
            }
        }
        CamLog.w(FaceDetector.TAG, String.format("Cont. shot latch null! Don't wait.", new Object[0]));
    }
}
