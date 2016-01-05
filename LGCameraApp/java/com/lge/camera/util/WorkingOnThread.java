package com.lge.camera.util;

import com.lge.camera.util.ThreadWorker.OnWorkingListListener;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;

public class WorkingOnThread extends Thread {
    private static WorkingOnThread mWorkingOnThread;
    private ArrayList<ThreadWorker> mWorkingArrayList;

    public WorkingOnThread() {
        this.mWorkingArrayList = new ArrayList();
    }

    static {
        mWorkingOnThread = null;
    }

    public static WorkingOnThread getInstance() {
        if (mWorkingOnThread == null) {
            mWorkingOnThread = new WorkingOnThread();
        }
        return mWorkingOnThread;
    }

    public void startWorker(ThreadWorker worker) {
        if (worker != null) {
            if (this.mWorkingArrayList != null) {
                this.mWorkingArrayList.add(worker);
            }
            worker.setWorkingListListener(new OnWorkingListListener() {
                public void onWorkingListRemove(ThreadWorker worker) {
                    WorkingOnThread.this.removeWorker(worker);
                }
            });
            worker.start();
        }
    }

    public void removeWorker(ThreadWorker worker) {
        if (worker != null && this.mWorkingArrayList != null) {
            this.mWorkingArrayList.remove(worker);
        }
    }

    public void waitAllWorkers() {
        while (this.mWorkingArrayList.size() > 0) {
            ThreadWorker worker = (ThreadWorker) this.mWorkingArrayList.get(0);
            if (worker != null) {
                try {
                    CamLog.d(FaceDetector.TAG, "join worker : " + worker.getName());
                    if (worker.isWaitUntilJoin()) {
                        worker.join();
                    }
                    this.mWorkingArrayList.remove(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
