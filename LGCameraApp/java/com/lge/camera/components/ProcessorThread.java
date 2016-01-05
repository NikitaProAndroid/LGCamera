package com.lge.camera.components;

import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProcessorThread extends Thread {
    private static final int QUEUE_SIZE = 3;
    private static final String THREAD = "ProcessorThread";
    int fcount;
    int fcount1;
    private FrameCallback mCb;
    private boolean mCheckFps;
    private boolean mCompleteFrame;
    private BlockingQueue<byte[]> mFrameQueue;
    Date start;
    Date start1;

    public interface FrameCallback {
        void processFrameOnThread(byte[] bArr);
    }

    public ProcessorThread(FrameCallback callback, int priority) {
        this.mFrameQueue = new LinkedBlockingQueue();
        this.mCompleteFrame = false;
        this.mCb = null;
        this.mCheckFps = false;
        this.start = new Date();
        this.fcount = 0;
        this.start1 = new Date();
        this.fcount1 = 0;
        this.mCb = callback;
        setPriority(priority);
    }

    public synchronized void run() {
        setName(THREAD);
        while (!isInterrupted()) {
            try {
                if (this.mCb != null) {
                    this.mCb.processFrameOnThread((byte[]) this.mFrameQueue.take());
                }
                yield();
                this.mCompleteFrame = true;
                if (this.mCheckFps) {
                    printFps();
                }
            } catch (InterruptedException e) {
                CamLog.i(FaceDetector.TAG, "ProcessorThread stop:", e);
                clearThread();
                this.mCompleteFrame = true;
            }
        }
        CamLog.i(FaceDetector.TAG, "Thread is interrupted.");
        clearThread();
        this.mCompleteFrame = true;
    }

    public boolean nextFrame(byte[] frame) {
        if (this.mFrameQueue.size() >= QUEUE_SIZE) {
            return false;
        }
        try {
            this.mFrameQueue.put(frame);
            return true;
        } catch (InterruptedException e) {
            CamLog.i(FaceDetector.TAG, "ProcessorThread stop:", e);
            return true;
        }
    }

    public void clearThread() {
        if (this.mFrameQueue != null && this.mFrameQueue.size() > 0) {
            this.mFrameQueue.clear();
            this.mFrameQueue = null;
        }
        if (this.mCb != null) {
            this.mCb = null;
        }
    }

    public void setCompleteFrame(boolean set) {
        this.mCompleteFrame = set;
    }

    public boolean getcompleteFrame() {
        return this.mCompleteFrame;
    }

    public void setCheckFPS(boolean set) {
        this.mCheckFps = set;
    }

    public void finish() {
        clearThread();
        try {
            join();
        } catch (InterruptedException ex) {
            CamLog.e(FaceDetector.TAG, "InterruptedException : " + ex);
        }
    }

    private void printFps() {
        this.fcount++;
        if (this.fcount % 100 == 0) {
            CamLog.i(FaceDetector.TAG, "fps:" + (((double) this.fcount) / (((double) (new Date().getTime() - this.start.getTime())) / 1000.0d)));
            this.start = new Date();
            this.fcount = 0;
        }
    }
}
