package com.lge.camera.util;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.lge.camera.properties.ProjectVariables;
import com.lge.hardware.LGCamera;
import com.lge.olaworks.library.FaceDetector;
import java.util.concurrent.CountDownLatch;

public class CameraHolder {
    private static final int RELEASE_CAMERA = 1;
    private static CameraHolder sHolder;
    private CountDownLatch checkOneShotSetPreviewNull;
    private Camera mCameraDevice;
    private int mCameraId;
    private final Handler mHandler;
    private CameraInfo[] mInfo;
    private long mKeepBeforeTime;
    private LGCamera mLGCamera;
    private int mNumberOfCameras;
    private Parameters mParameters;
    private int mUsers;

    private class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CameraHolder.RELEASE_CAMERA /*1*/:
                    synchronized (CameraHolder.this) {
                        CamLog.d(FaceDetector.TAG, "CameraHolder-handleMessage:RELEASE_CAMERA [mKeepBeforeTime] = " + CameraHolder.this.mKeepBeforeTime);
                        if (CameraHolder.this.mKeepBeforeTime == 0) {
                            CamLog.d(FaceDetector.TAG, "we don't need to release, return");
                            return;
                        }
                        CameraHolder.this.releaseCamera();
                    }
                default:
            }
        }
    }

    public static synchronized CameraHolder instance() {
        CameraHolder cameraHolder;
        synchronized (CameraHolder.class) {
            if (sHolder == null) {
                sHolder = new CameraHolder();
            }
            cameraHolder = sHolder;
        }
        return cameraHolder;
    }

    private CameraHolder() {
        this.mKeepBeforeTime = 0;
        this.mUsers = 0;
        this.mCameraId = -1;
        this.checkOneShotSetPreviewNull = null;
        HandlerThread ht = new HandlerThread("CameraHolder");
        MyHandler myHandler = null;
        ht.start();
        try {
            Looper lp = ht.getLooper();
            if (lp == null) {
                throw new NullPointerException("pref is null at method CameraHolder::CameraHolder(): lp = ht.getLooper();");
            }
            myHandler = new MyHandler(lp);
            this.mHandler = myHandler;
            this.mNumberOfCameras = Camera.getNumberOfCameras();
            CamLog.d(FaceDetector.TAG, "CameraHolder() mNumberOfCameras = " + this.mNumberOfCameras);
            this.mInfo = new CameraInfo[this.mNumberOfCameras];
            for (int i = 0; i < this.mNumberOfCameras; i += RELEASE_CAMERA) {
                this.mInfo[i] = new CameraInfo();
                Camera.getCameraInfo(i, this.mInfo[i]);
            }
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "looper is null");
            e.printStackTrace();
        }
    }

    public CameraInfo[] getCameraInfo() {
        return this.mInfo;
    }

    public CountDownLatch getOneShotSetPreviewNull() {
        return this.checkOneShotSetPreviewNull;
    }

    public void setOneShotSetPreviewNullLatchCountDown() {
        if (this.checkOneShotSetPreviewNull != null) {
            this.checkOneShotSetPreviewNull.countDown();
            this.checkOneShotSetPreviewNull = null;
        }
    }

    public int getRealCameraId() {
        return this.mCameraId;
    }

    public synchronized android.hardware.Camera open(int r11) throws com.lge.camera.util.CameraHardwareException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:com.lge.camera.util.CameraHolder.open(int):android.hardware.Camera. bs: [B:13:0x005b, B:60:0x01e2]
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:86)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:57)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r10 = this;
        monitor-enter(r10);
        r6 = "CameraApp";	 Catch:{ all -> 0x01d0 }
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x01d0 }
        r7.<init>();	 Catch:{ all -> 0x01d0 }
        r8 = "KDH before open mUsers = [";	 Catch:{ all -> 0x01d0 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x01d0 }
        r8 = r10.mUsers;	 Catch:{ all -> 0x01d0 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x01d0 }
        r8 = "], mCameraDevice:";	 Catch:{ all -> 0x01d0 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x01d0 }
        r8 = r10.mCameraDevice;	 Catch:{ all -> 0x01d0 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x01d0 }
        r8 = ", mCameraId:";	 Catch:{ all -> 0x01d0 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x01d0 }
        r8 = r10.mCameraId;	 Catch:{ all -> 0x01d0 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x01d0 }
        r8 = ", cameraId:";	 Catch:{ all -> 0x01d0 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x01d0 }
        r7 = r7.append(r11);	 Catch:{ all -> 0x01d0 }
        r7 = r7.toString();	 Catch:{ all -> 0x01d0 }
        com.lge.camera.util.CamLog.i(r6, r7);	 Catch:{ all -> 0x01d0 }
        r5 = 0;	 Catch:{ all -> 0x01d0 }
        r1 = 0;	 Catch:{ all -> 0x01d0 }
        r6 = r10.mUsers;	 Catch:{ all -> 0x01d0 }
        if (r6 != 0) goto L_0x004b;	 Catch:{ all -> 0x01d0 }
    L_0x0043:
        r6 = r10.mCameraDevice;	 Catch:{ all -> 0x01d0 }
        if (r6 == 0) goto L_0x0057;	 Catch:{ all -> 0x01d0 }
    L_0x0047:
        r6 = r10.mCameraId;	 Catch:{ all -> 0x01d0 }
        if (r6 == r11) goto L_0x0057;	 Catch:{ all -> 0x01d0 }
    L_0x004b:
        r6 = r10.release();	 Catch:{ all -> 0x01d0 }
        if (r6 == 0) goto L_0x0057;	 Catch:{ all -> 0x01d0 }
    L_0x0051:
        r6 = 0;	 Catch:{ all -> 0x01d0 }
        r10.mCameraDevice = r6;	 Catch:{ all -> 0x01d0 }
        r6 = -1;	 Catch:{ all -> 0x01d0 }
        r10.mCameraId = r6;	 Catch:{ all -> 0x01d0 }
    L_0x0057:
        r6 = r10.mCameraDevice;	 Catch:{ all -> 0x01d0 }
        if (r6 != 0) goto L_0x01e2;
    L_0x005b:
        r6 = "CameraApp";	 Catch:{ RuntimeException -> 0x0145 }
        r7 = new java.lang.StringBuilder;	 Catch:{ RuntimeException -> 0x0145 }
        r7.<init>();	 Catch:{ RuntimeException -> 0x0145 }
        r8 = "#### android.hardware.Camera.open():cameraId = ";	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.append(r8);	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.append(r11);	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.toString();	 Catch:{ RuntimeException -> 0x0145 }
        com.lge.camera.util.CamLog.d(r6, r7);	 Catch:{ RuntimeException -> 0x0145 }
        r6 = "CameraApp";	 Catch:{ RuntimeException -> 0x0145 }
        r7 = new java.lang.StringBuilder;	 Catch:{ RuntimeException -> 0x0145 }
        r7.<init>();	 Catch:{ RuntimeException -> 0x0145 }
        r8 = "[Time Info][2] Camloading Activity End : Camera UI Initialization ";	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.append(r8);	 Catch:{ RuntimeException -> 0x0145 }
        r8 = 1;	 Catch:{ RuntimeException -> 0x0145 }
        r8 = com.lge.camera.util.Common.interimCheckTime(r8);	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.append(r8);	 Catch:{ RuntimeException -> 0x0145 }
        r8 = " ms";	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.append(r8);	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.toString();	 Catch:{ RuntimeException -> 0x0145 }
        android.util.Log.i(r6, r7);	 Catch:{ RuntimeException -> 0x0145 }
        r6 = "CameraApp";	 Catch:{ RuntimeException -> 0x0145 }
        r7 = new java.lang.StringBuilder;	 Catch:{ RuntimeException -> 0x0145 }
        r7.<init>();	 Catch:{ RuntimeException -> 0x0145 }
        r8 = "[Time Info][3] Camera Device Open Start : Camera Driver Initialization ";	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.append(r8);	 Catch:{ RuntimeException -> 0x0145 }
        r8 = 0;	 Catch:{ RuntimeException -> 0x0145 }
        r8 = com.lge.camera.util.Common.interimCheckTime(r8);	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.append(r8);	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.toString();	 Catch:{ RuntimeException -> 0x0145 }
        android.util.Log.i(r6, r7);	 Catch:{ RuntimeException -> 0x0145 }
        r6 = new com.lge.hardware.LGCamera;	 Catch:{ RuntimeException -> 0x0145 }
        r6.<init>(r11);	 Catch:{ RuntimeException -> 0x0145 }
        r10.mLGCamera = r6;	 Catch:{ RuntimeException -> 0x0145 }
        r6 = r10.mLGCamera;	 Catch:{ RuntimeException -> 0x0145 }
        r6 = r6.getCamera();	 Catch:{ RuntimeException -> 0x0145 }
        r10.mCameraDevice = r6;	 Catch:{ RuntimeException -> 0x0145 }
        r6 = "CameraApp";	 Catch:{ RuntimeException -> 0x0145 }
        r7 = new java.lang.StringBuilder;	 Catch:{ RuntimeException -> 0x0145 }
        r7.<init>();	 Catch:{ RuntimeException -> 0x0145 }
        r8 = "[Time Info][3] Camera Device Open End : Camera Driver Initialization ";	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.append(r8);	 Catch:{ RuntimeException -> 0x0145 }
        r8 = 1;	 Catch:{ RuntimeException -> 0x0145 }
        r8 = com.lge.camera.util.Common.interimCheckTime(r8);	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.append(r8);	 Catch:{ RuntimeException -> 0x0145 }
        r8 = " ms";	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.append(r8);	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.toString();	 Catch:{ RuntimeException -> 0x0145 }
        android.util.Log.i(r6, r7);	 Catch:{ RuntimeException -> 0x0145 }
        r6 = "CameraApp";	 Catch:{ RuntimeException -> 0x0145 }
        r7 = new java.lang.StringBuilder;	 Catch:{ RuntimeException -> 0x0145 }
        r7.<init>();	 Catch:{ RuntimeException -> 0x0145 }
        r8 = "[Time Info][4] App Camera Param setting Start : Camera Parameter setting ";	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.append(r8);	 Catch:{ RuntimeException -> 0x0145 }
        r8 = 0;	 Catch:{ RuntimeException -> 0x0145 }
        r8 = com.lge.camera.util.Common.interimCheckTime(r8);	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.append(r8);	 Catch:{ RuntimeException -> 0x0145 }
        r7 = r7.toString();	 Catch:{ RuntimeException -> 0x0145 }
        android.util.Log.i(r6, r7);	 Catch:{ RuntimeException -> 0x0145 }
        r10.mCameraId = r11;	 Catch:{ RuntimeException -> 0x0145 }
        r6 = new java.util.concurrent.CountDownLatch;	 Catch:{ RuntimeException -> 0x0145 }
        r7 = 1;	 Catch:{ RuntimeException -> 0x0145 }
        r6.<init>(r7);	 Catch:{ RuntimeException -> 0x0145 }
        r10.checkOneShotSetPreviewNull = r6;	 Catch:{ RuntimeException -> 0x0145 }
    L_0x010c:
        r6 = r10.mCameraDevice;	 Catch:{ all -> 0x01d0 }
        if (r6 == 0) goto L_0x0118;	 Catch:{ all -> 0x01d0 }
    L_0x0110:
        r6 = r10.mCameraDevice;	 Catch:{ all -> 0x01d0 }
        r6 = r6.getParameters();	 Catch:{ all -> 0x01d0 }
        r10.mParameters = r6;	 Catch:{ all -> 0x01d0 }
    L_0x0118:
        r6 = r10.mUsers;	 Catch:{ all -> 0x01d0 }
        r6 = r6 + 1;	 Catch:{ all -> 0x01d0 }
        r10.mUsers = r6;	 Catch:{ all -> 0x01d0 }
        r10.cancel();	 Catch:{ all -> 0x01d0 }
        r6 = "CameraApp";	 Catch:{ all -> 0x01d0 }
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x01d0 }
        r7.<init>();	 Catch:{ all -> 0x01d0 }
        r8 = "KDH after open mUsers = [";	 Catch:{ all -> 0x01d0 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x01d0 }
        r8 = r10.mUsers;	 Catch:{ all -> 0x01d0 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x01d0 }
        r8 = "]";	 Catch:{ all -> 0x01d0 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x01d0 }
        r7 = r7.toString();	 Catch:{ all -> 0x01d0 }
        com.lge.camera.util.CamLog.i(r6, r7);	 Catch:{ all -> 0x01d0 }
        r6 = r10.mCameraDevice;	 Catch:{ all -> 0x01d0 }
        monitor-exit(r10);
        return r6;
    L_0x0145:
        r2 = move-exception;
        r1 = 1;
        r6 = r10.checkOneShotSetPreviewNull;	 Catch:{ all -> 0x01d0 }
        if (r6 == 0) goto L_0x0153;	 Catch:{ all -> 0x01d0 }
    L_0x014b:
        r6 = r10.checkOneShotSetPreviewNull;	 Catch:{ all -> 0x01d0 }
        r6.countDown();	 Catch:{ all -> 0x01d0 }
        r6 = 0;	 Catch:{ all -> 0x01d0 }
        r10.checkOneShotSetPreviewNull = r6;	 Catch:{ all -> 0x01d0 }
    L_0x0153:
        if (r1 == 0) goto L_0x010c;	 Catch:{ all -> 0x01d0 }
    L_0x0155:
        r5 = r5 + 1;	 Catch:{ all -> 0x01d0 }
        r6 = "CameraApp";	 Catch:{ all -> 0x01d0 }
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x01d0 }
        r7.<init>();	 Catch:{ all -> 0x01d0 }
        r8 = "fail to retry connect Camera. retryCount = ";	 Catch:{ all -> 0x01d0 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x01d0 }
        r7 = r7.append(r5);	 Catch:{ all -> 0x01d0 }
        r7 = r7.toString();	 Catch:{ all -> 0x01d0 }
        com.lge.camera.util.CamLog.e(r6, r7);	 Catch:{ all -> 0x01d0 }
        r6 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        java.lang.Thread.sleep(r6);	 Catch:{ InterruptedException -> 0x01d3 }
    L_0x0174:
        r6 = "CameraApp";	 Catch:{ RuntimeException -> 0x01d8 }
        r7 = new java.lang.StringBuilder;	 Catch:{ RuntimeException -> 0x01d8 }
        r7.<init>();	 Catch:{ RuntimeException -> 0x01d8 }
        r8 = "#### android.hardware.Camera.open()";	 Catch:{ RuntimeException -> 0x01d8 }
        r7 = r7.append(r8);	 Catch:{ RuntimeException -> 0x01d8 }
        r7 = r7.append(r11);	 Catch:{ RuntimeException -> 0x01d8 }
        r7 = r7.toString();	 Catch:{ RuntimeException -> 0x01d8 }
        com.lge.camera.util.CamLog.e(r6, r7);	 Catch:{ RuntimeException -> 0x01d8 }
        r6 = new com.lge.hardware.LGCamera;	 Catch:{ RuntimeException -> 0x01d8 }
        r6.<init>(r11);	 Catch:{ RuntimeException -> 0x01d8 }
        r10.mLGCamera = r6;	 Catch:{ RuntimeException -> 0x01d8 }
        r6 = r10.mLGCamera;	 Catch:{ RuntimeException -> 0x01d8 }
        r6 = r6.getCamera();	 Catch:{ RuntimeException -> 0x01d8 }
        r10.mCameraDevice = r6;	 Catch:{ RuntimeException -> 0x01d8 }
        r10.mCameraId = r11;	 Catch:{ RuntimeException -> 0x01d8 }
        r6 = r10.checkOneShotSetPreviewNull;	 Catch:{ RuntimeException -> 0x01d8 }
        if (r6 != 0) goto L_0x01a9;	 Catch:{ RuntimeException -> 0x01d8 }
    L_0x01a1:
        r6 = new java.util.concurrent.CountDownLatch;	 Catch:{ RuntimeException -> 0x01d8 }
        r7 = 1;	 Catch:{ RuntimeException -> 0x01d8 }
        r6.<init>(r7);	 Catch:{ RuntimeException -> 0x01d8 }
        r10.checkOneShotSetPreviewNull = r6;	 Catch:{ RuntimeException -> 0x01d8 }
    L_0x01a9:
        r1 = 0;
    L_0x01aa:
        r0 = 30;
        if (r1 == 0) goto L_0x0153;
    L_0x01ae:
        if (r5 < r0) goto L_0x0153;
    L_0x01b0:
        r1 = 1;
        r6 = r10.mCameraDevice;	 Catch:{ all -> 0x01d0 }
        if (r6 == 0) goto L_0x01ca;	 Catch:{ all -> 0x01d0 }
    L_0x01b5:
        r6 = com.lge.camera.properties.ModelProperties.isOMAP4Chipset();	 Catch:{ all -> 0x01d0 }
        if (r6 != 0) goto L_0x01c7;	 Catch:{ all -> 0x01d0 }
    L_0x01bb:
        r6 = "CameraApp";	 Catch:{ all -> 0x01d0 }
        r7 = "#### mCameraDevice.stopPreview()";	 Catch:{ all -> 0x01d0 }
        com.lge.camera.util.CamLog.e(r6, r7);	 Catch:{ all -> 0x01d0 }
        r6 = r10.mCameraDevice;	 Catch:{ all -> 0x01d0 }
        r6.stopPreview();	 Catch:{ all -> 0x01d0 }
    L_0x01c7:
        r10.releaseCamera();	 Catch:{ all -> 0x01d0 }
    L_0x01ca:
        r6 = new com.lge.camera.util.CameraHardwareException;	 Catch:{ all -> 0x01d0 }
        r6.<init>(r2);	 Catch:{ all -> 0x01d0 }
        throw r6;	 Catch:{ all -> 0x01d0 }
    L_0x01d0:
        r6 = move-exception;
        monitor-exit(r10);
        throw r6;
    L_0x01d3:
        r4 = move-exception;
        r4.printStackTrace();	 Catch:{ all -> 0x01d0 }
        goto L_0x0174;	 Catch:{ all -> 0x01d0 }
    L_0x01d8:
        r3 = move-exception;	 Catch:{ all -> 0x01d0 }
        r6 = "CameraApp";	 Catch:{ all -> 0x01d0 }
        r7 = "RuntimeException : ";	 Catch:{ all -> 0x01d0 }
        com.lge.camera.util.CamLog.e(r6, r7, r3);	 Catch:{ all -> 0x01d0 }
        r1 = 1;
        goto L_0x01aa;
    L_0x01e2:
        r6 = "CameraApp";	 Catch:{ IOException -> 0x0212 }
        r7 = "#### mCameraDevice.reconnect()";	 Catch:{ IOException -> 0x0212 }
        com.lge.camera.util.CamLog.d(r6, r7);	 Catch:{ IOException -> 0x0212 }
        r6 = r10.mCameraDevice;	 Catch:{ IOException -> 0x0212 }
        r6.reconnect();	 Catch:{ IOException -> 0x0212 }
        r6 = r10.mCameraDevice;	 Catch:{ RuntimeException -> 0x01f7 }
        r7 = r10.mParameters;	 Catch:{ RuntimeException -> 0x01f7 }
        r6.setParameters(r7);	 Catch:{ RuntimeException -> 0x01f7 }
        goto L_0x0118;
    L_0x01f7:
        r2 = move-exception;
        r6 = "CameraApp";	 Catch:{ all -> 0x01d0 }
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x01d0 }
        r7.<init>();	 Catch:{ all -> 0x01d0 }
        r8 = "setParameters failed: ";	 Catch:{ all -> 0x01d0 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x01d0 }
        r7 = r7.append(r2);	 Catch:{ all -> 0x01d0 }
        r7 = r7.toString();	 Catch:{ all -> 0x01d0 }
        com.lge.camera.util.CamLog.e(r6, r7);	 Catch:{ all -> 0x01d0 }
        goto L_0x0118;	 Catch:{ all -> 0x01d0 }
    L_0x0212:
        r2 = move-exception;	 Catch:{ all -> 0x01d0 }
        r6 = "CameraApp";	 Catch:{ all -> 0x01d0 }
        r7 = "reconnect failed.";	 Catch:{ all -> 0x01d0 }
        com.lge.camera.util.CamLog.e(r6, r7);	 Catch:{ all -> 0x01d0 }
        r6 = new com.lge.camera.util.CameraHardwareException;	 Catch:{ all -> 0x01d0 }
        r6.<init>(r2);	 Catch:{ all -> 0x01d0 }
        throw r6;	 Catch:{ all -> 0x01d0 }
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.util.CameraHolder.open(int):android.hardware.Camera");
    }

    public LGCamera getLG() {
        return this.mLGCamera;
    }

    public int getUsers() {
        return this.mUsers;
    }

    public int getNumberOfCameras() {
        return this.mNumberOfCameras;
    }

    public static void Assert(boolean cond) {
        if (!cond) {
            throw new AssertionError();
        }
    }

    public synchronized Camera tryOpen() {
        Camera camera = null;
        synchronized (this) {
            try {
                if (this.mUsers == 0) {
                    camera = open(this.mCameraId);
                }
            } catch (CameraHardwareException e) {
                if ("eng".equals(Build.TYPE)) {
                    throw new RuntimeException(e);
                }
            }
        }
        return camera;
    }

    public synchronized Camera tryOpen(int cameraId) {
        Camera camera = null;
        synchronized (this) {
            try {
                if (this.mUsers == 0) {
                    camera = open(cameraId);
                }
            } catch (CameraHardwareException e) {
                if ("eng".equals(Build.TYPE)) {
                    throw new RuntimeException(e);
                }
            }
        }
        return camera;
    }

    public synchronized boolean release() {
        boolean ret_val;
        CamLog.i(FaceDetector.TAG, "KDH before release mUsers = [" + this.mUsers + "]");
        this.mUsers--;
        ret_val = false;
        if (this.mUsers < 0) {
            this.mUsers = 0;
        }
        if (this.mCameraDevice != null) {
            CamLog.d(FaceDetector.TAG, "### mCameraDevice.stopPreview()");
            this.mCameraDevice.stopPreview();
            ret_val = releaseCamera();
        }
        CamLog.i(FaceDetector.TAG, "KDH after release mUsers = [" + this.mUsers + "], ret_val:" + ret_val);
        return ret_val;
    }

    private synchronized boolean releaseCamera() {
        boolean z = true;
        synchronized (this) {
            long now = System.currentTimeMillis();
            if (now < this.mKeepBeforeTime) {
                if (this.mHandler != null) {
                    this.mHandler.sendEmptyMessageDelayed(RELEASE_CAMERA, this.mKeepBeforeTime - now);
                }
                z = false;
            } else {
                this.mKeepBeforeTime = 0;
                if (this.mCameraDevice != null) {
                    CamLog.d(FaceDetector.TAG, "#### mCameraDevice.release()-check");
                    this.mCameraDevice.release();
                    this.mCameraDevice = null;
                }
                if (this.checkOneShotSetPreviewNull != null) {
                    this.checkOneShotSetPreviewNull.countDown();
                    this.checkOneShotSetPreviewNull = null;
                }
            }
        }
        return z;
    }

    public synchronized void keep() {
        if (ProjectVariables.getUseDeviceKeepForChangeMode()) {
            if (Common.isFaceUnlock()) {
                CamLog.i(FaceDetector.TAG, "face unlock does not use to keep.");
            } else {
                CamLog.d(FaceDetector.TAG, "keep-check");
                this.mKeepBeforeTime = System.currentTimeMillis() + ProjectVariables.keepDuration;
            }
        }
    }

    public synchronized void cancel() {
        if (ProjectVariables.getUseDeviceKeepForChangeMode()) {
            CamLog.d(FaceDetector.TAG, "keep-cancel");
            this.mKeepBeforeTime = 0;
            if (this.mHandler != null) {
                this.mHandler.removeMessages(RELEASE_CAMERA);
            }
        }
    }
}
