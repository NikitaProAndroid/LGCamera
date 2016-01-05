package com.lge.camera.util;

import android.graphics.Bitmap;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;

public class ImageSavers extends Thread {
    private ImageSaverCallback mCb;
    private int mCount;
    private ArrayList<SaveRequest> mQueue;
    private int mQueueLimit;
    private boolean mStop;

    public interface ImageSaverCallback {
        void doAfterSaveImageSavers();

        boolean isStorageFull();

        void saveAndAddImageForImageSavers(SaveRequest saveRequest);

        void setSaveRequest(SaveRequest saveRequest, byte[] bArr, Bitmap bitmap, int i, boolean z, boolean z2);
    }

    public ImageSavers(ImageSaverCallback callback, int queueCount) {
        this.mQueueLimit = 40;
        this.mCount = 0;
        this.mCb = null;
        this.mQueueLimit = queueCount;
        this.mQueue = new ArrayList();
        this.mCb = callback;
        start();
    }

    public int getQueueCount() {
        return this.mQueue.size();
    }

    public int getCount() {
        return this.mCount;
    }

    public boolean addImage(byte[] data, Bitmap bitmap, int imageRotationDegree, boolean isSetLastThumb, boolean isBurstFirst) {
        if (this.mCb == null || this.mCb.isStorageFull()) {
            return false;
        }
        this.mCount++;
        SaveRequest sr = new SaveRequest();
        this.mCb.setSaveRequest(sr, data, bitmap, imageRotationDegree, isSetLastThumb, isBurstFirst);
        synchronized (this) {
            while (this.mQueue.size() >= this.mQueueLimit) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    CamLog.e(FaceDetector.TAG, "InterruptedException : " + ex);
                }
            }
            this.mQueue.add(sr);
            notifyAll();
        }
        return true;
    }

    public void run() {
        while (true) {
            synchronized (this) {
                if (this.mQueue.isEmpty()) {
                    notifyAll();
                    if (this.mStop) {
                        return;
                    }
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        CamLog.e(FaceDetector.TAG, "InterruptedException : " + ex);
                    }
                } else {
                    SaveRequest sr = (SaveRequest) this.mQueue.get(0);
                    if (this.mCb != null) {
                        this.mCb.saveAndAddImageForImageSavers(sr);
                    }
                    synchronized (this) {
                        this.mCount--;
                        this.mQueue.remove(0);
                        notifyAll();
                        if (this.mCb != null) {
                            this.mCb.doAfterSaveImageSavers();
                        }
                    }
                }
            }
        }
    }

    public void waitDone() {
        CamLog.d(FaceDetector.TAG, "waitDone start : Qsize" + this.mQueue.size());
        synchronized (this) {
            while (this.mQueue != null && !this.mQueue.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    CamLog.e(FaceDetector.TAG, "InterruptedException : " + ex);
                }
            }
        }
    }

    public void waitAvailableQueueCount(int availableCount) {
        synchronized (this) {
            if (availableCount > this.mQueueLimit) {
                CamLog.e(FaceDetector.TAG, "Error! availableCount must be less than Limit!");
                return;
            }
            while (this.mQueueLimit - this.mQueue.size() < availableCount) {
                CamLog.i(FaceDetector.TAG, "Imagesaver available Que Count is " + (this.mQueueLimit - this.mQueue.size()) + ", Wait...");
                try {
                    wait();
                } catch (InterruptedException ex) {
                    CamLog.e(FaceDetector.TAG, "InterruptedException : " + ex);
                }
            }
        }
    }

    public void finish() {
        waitDone();
        synchronized (this) {
            this.mStop = true;
            notifyAll();
        }
        try {
            join();
        } catch (InterruptedException ex) {
            CamLog.e(FaceDetector.TAG, "InterruptedException : " + ex);
        }
        this.mCb = null;
        this.mQueue = null;
    }
}
