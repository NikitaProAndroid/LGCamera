package com.lge.camera.util;

import com.lge.olaworks.library.FaceDetector;

public class ThreadWorker extends Thread {
    private Runnable mJob;
    private boolean mNeedWaitByJoin;
    private OnWorkerListener mWorkerListener;
    private OnWorkingListListener mWorkingListListener;

    public interface OnWorkerListener {
        void onEndWork();

        void onStartWork();
    }

    public interface OnWorkingListListener {
        void onWorkingListRemove(ThreadWorker threadWorker);
    }

    public void setWorkerListener(OnWorkerListener listener) {
        this.mWorkerListener = listener;
    }

    public void setWorkingListListener(OnWorkingListListener listener) {
        this.mWorkingListListener = listener;
    }

    public ThreadWorker(String name, Runnable mainJob, boolean needWaitEnd) {
        this.mWorkerListener = null;
        this.mWorkingListListener = null;
        this.mNeedWaitByJoin = true;
        setName(name);
        this.mJob = mainJob;
        this.mNeedWaitByJoin = needWaitEnd;
    }

    public void run() {
        if (this.mWorkerListener != null) {
            this.mWorkerListener.onStartWork();
        }
        if (this.mJob != null) {
            this.mJob.run();
            this.mJob = null;
        }
        if (this.mWorkerListener != null) {
            this.mWorkerListener.onEndWork();
            this.mWorkerListener = null;
        }
        if (this.mWorkingListListener != null) {
            this.mWorkingListListener.onWorkingListRemove(this);
            this.mWorkingListListener = null;
        }
    }

    public boolean isWaitUntilJoin() {
        return this.mNeedWaitByJoin;
    }

    public void finish() {
        try {
            CamLog.d(FaceDetector.TAG, "ThreadWorker finish-join : " + getName());
            join();
        } catch (InterruptedException ex) {
            CamLog.e(FaceDetector.TAG, "InterruptedException : " + ex);
        }
    }
}
