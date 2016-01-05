package com.lge.olaworks.library;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.datastruct.JOlaBitmap;
import com.lge.olaworks.datastruct.Ola_AutoPanoramaInfo;
import com.lge.olaworks.datastruct.Ola_AutoPanoramaThumbInfo;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.jni.OlaAutoPanoramaJNI;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class AutoPanorama extends BaseEngine {
    private static final int DEFAULT_PANNING_SPEED_THRESHOLD = 100;
    public static final String ENGINE_TAG = "AutoPanorama";
    public static final int MODE_DIRECITON_ALL = 0;
    public static final int MODE_DIRECTION_HORIZONTAL_ONLY = 1;
    public static final int MODE_IMAGE_JPEG = 1;
    public static final int MODE_IMAGE_PREVIEW = 0;
    public static final int MODE_THUMBANIL_DISABLE = 0;
    public static final int MODE_THUMBANIL_ENABLE = 1;
    private static final int NEED_TO_SETFRAME = 1;
    public static final String TAG = "CameraApp";
    private boolean isConfigureLandscape;
    Callback mCallback;
    Ola_AutoPanoramaInfo mInfo;
    private int mInitOrientation;
    private int mLlogLevel;
    private int mModeDirection;
    private int mModeSetFrameImage;
    private int mModeThumbnail;
    private int mOneShotOrientation;
    private int mPanningSpeedThreshold;
    private int mSetFrameCount;
    private ArrayList<JOlaBitmap> mSetFrameData;
    private ArrayList<Thread> mSetFrameThreads;
    private StartSynthesisTask mStartSynthesisTask;
    private CountDownLatch mSynthesisCountDownLatch;
    Ola_AutoPanoramaThumbInfo mThumbInfo;
    private int[] mThumbnailExpectedSize;
    private boolean mWarningStatus;

    public interface Callback {
        void onAlarmStartSync();

        void onComplete(byte[] bArr);

        void onGetThumbnailImage(Bitmap bitmap, boolean z);

        void onPanningSpeedWarning(boolean z);

        void onProcessFrame(Ola_AutoPanoramaInfo ola_AutoPanoramaInfo);

        void onSetStartSyncForceDelay();

        void onSynthesisProgressUpdate(int i);

        void onTakePicture(int i);
    }

    private class SetFrameRunnable implements Runnable {
        int mFrameId;

        public SetFrameRunnable(int frameId) {
            this.mFrameId = frameId;
        }

        public void run() {
            int retVal = OlaAutoPanoramaJNI.setFrame((JOlaBitmap) AutoPanorama.this.mSetFrameData.get(this.mFrameId - 1), this.mFrameId - 1);
            if (retVal < 0) {
                CamLog.e(AutoPanorama.TAG, "error setFrame Id = " + this.mFrameId + " / retVal = " + retVal);
            }
        }
    }

    private class StartSynthesisTask extends AsyncTask<Void, Void, Integer> {
        private StartSynthesisTask() {
        }

        protected void onPreExecute() {
            CamLog.d(AutoPanorama.TAG, "onPreExecute");
            AutoPanorama.this.mSynthesisCountDownLatch = new CountDownLatch(AutoPanorama.NEED_TO_SETFRAME);
            AutoPanorama.this.mCallback.onSynthesisProgressUpdate(AutoPanorama.MODE_THUMBANIL_DISABLE);
            super.onPreExecute();
        }

        protected Integer doInBackground(Void... params) {
            int rotDeg;
            CamLog.d(AutoPanorama.TAG, "doInBackground");
            JOlaBitmap output = new JOlaBitmap();
            if (AutoPanorama.this.mInitOrientation == 3) {
                rotDeg = 90;
            } else if (AutoPanorama.this.mInitOrientation == 2) {
                rotDeg = MediaProviderUtils.ROTATION_180;
            } else if (AutoPanorama.this.mInitOrientation == AutoPanorama.NEED_TO_SETFRAME) {
                rotDeg = Tag.IMAGE_DESCRIPTION;
            } else {
                rotDeg = AutoPanorama.this.mOneShotOrientation;
            }
            if (!AutoPanorama.this.isConfigureLandscape) {
                rotDeg = (rotDeg + 90) % CameraConstants.DEGREE_360;
            }
            int retVal = OlaAutoPanoramaJNI.processSynthesis(output, rotDeg);
            CamLog.d(AutoPanorama.TAG, "processSynthesis ret = " + retVal);
            if (retVal >= 0) {
                AutoPanorama.this.prepareOutput(output);
            }
            AutoPanorama.this.mSynthesisCountDownLatch.countDown();
            AutoPanorama.this.mSynthesisCountDownLatch = null;
            return Integer.valueOf(retVal);
        }

        protected void onPostExecute(Integer result) {
            CamLog.d(AutoPanorama.TAG, "onPostExecute result = " + result.intValue());
            if (result.intValue() < 0) {
                CamLog.e(AutoPanorama.TAG, "processSynthesis fail! - retVal  = " + result.intValue());
            }
            AutoPanorama.this.mCallback.onSynthesisProgressUpdate(-1);
            OlaAutoPanoramaJNI.initialize();
            if (AutoPanorama.this.mModeThumbnail == AutoPanorama.NEED_TO_SETFRAME) {
                AutoPanorama.this.mThumbInfo.clear();
                OlaAutoPanoramaJNI.setThumbnailSize(AutoPanorama.this.mThumbInfo, AutoPanorama.this.mThumbnailExpectedSize[AutoPanorama.MODE_THUMBANIL_DISABLE], AutoPanorama.this.mThumbnailExpectedSize[AutoPanorama.NEED_TO_SETFRAME]);
            }
            for (int i = AutoPanorama.MODE_THUMBANIL_DISABLE; i < AutoPanorama.this.mSetFrameData.size(); i += AutoPanorama.NEED_TO_SETFRAME) {
                ((JOlaBitmap) AutoPanorama.this.mSetFrameData.get(i)).imageData = null;
            }
            AutoPanorama.this.mSetFrameData.clear();
            AutoPanorama.this.mSetFrameCount = AutoPanorama.MODE_THUMBANIL_DISABLE;
        }

        protected void onCancelled() {
            super.onCancelled();
        }
    }

    public void setConfigureLandscape(boolean set) {
        this.isConfigureLandscape = set;
    }

    public void setOrientation(int degree) {
        this.mOneShotOrientation = degree;
    }

    public Ola_AutoPanoramaThumbInfo getThumbnailInfo() {
        return this.mThumbInfo;
    }

    public int getModeThumbnail() {
        return this.mModeThumbnail;
    }

    public void setModeThumbnail(int mode) {
        this.mModeThumbnail = mode;
    }

    public int getModeDirection() {
        return this.mModeDirection;
    }

    public void setModeDirection(int mode) {
        this.mModeDirection = mode;
    }

    public int[] getThumbnailExpectedSize() {
        return this.mThumbnailExpectedSize;
    }

    public void setThumbnailExpectedSize(int width, int height) {
        if (this.mThumbnailExpectedSize == null) {
            this.mThumbnailExpectedSize = new int[2];
        }
        this.mThumbnailExpectedSize[MODE_THUMBANIL_DISABLE] = width;
        this.mThumbnailExpectedSize[NEED_TO_SETFRAME] = height;
    }

    private int prepareOutput(JOlaBitmap output) {
        YuvImage data = new YuvImage(output.imageData, 17, output.width, output.height, null);
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        int w = output.width % 16;
        int h = output.height % 16;
        if (w != 0) {
            output.width -= w;
        }
        if (h != 0) {
            output.height -= h;
        }
        Rect rect = new Rect(MODE_THUMBANIL_DISABLE, MODE_THUMBANIL_DISABLE, output.width, output.height);
        if (!(data == null || data.getStrides() == null)) {
            data.compressToJpeg(rect, 95, ostream);
        }
        this.mCallback.onComplete(ostream.toByteArray());
        return MODE_THUMBANIL_DISABLE;
    }

    public AutoPanorama(int modeImage, int modeDirect, int modeThumb, Callback callback) {
        this.mInfo = new Ola_AutoPanoramaInfo();
        this.mThumbInfo = new Ola_AutoPanoramaThumbInfo();
        this.mCallback = null;
        this.mPanningSpeedThreshold = DEFAULT_PANNING_SPEED_THRESHOLD;
        this.mWarningStatus = false;
        this.mSetFrameCount = MODE_THUMBANIL_DISABLE;
        this.mModeSetFrameImage = MODE_THUMBANIL_DISABLE;
        this.mModeThumbnail = NEED_TO_SETFRAME;
        this.mModeDirection = MODE_THUMBANIL_DISABLE;
        this.mSetFrameData = new ArrayList();
        this.mSetFrameThreads = new ArrayList();
        this.isConfigureLandscape = true;
        this.mLlogLevel = MODE_THUMBANIL_DISABLE;
        this.mModeSetFrameImage = modeImage;
        this.mModeDirection = modeDirect;
        this.mModeThumbnail = modeThumb;
        this.mCallback = callback;
    }

    public String getTag() {
        return ENGINE_TAG;
    }

    public boolean needRenderMode() {
        return false;
    }

    public int create() {
        int retVal = OlaAutoPanoramaJNI.create();
        if (retVal < 0) {
            return retVal;
        }
        retVal = OlaAutoPanoramaJNI.initialize();
        if (retVal < 0) {
            return retVal;
        }
        OlaAutoPanoramaJNI.setParam(MODE_THUMBANIL_DISABLE, this.mModeDirection);
        OlaAutoPanoramaJNI.setParam(NEED_TO_SETFRAME, this.mModeThumbnail);
        if (this.mModeThumbnail == NEED_TO_SETFRAME) {
            this.mThumbInfo.clear();
            retVal = OlaAutoPanoramaJNI.setThumbnailSize(this.mThumbInfo, this.mThumbnailExpectedSize[MODE_THUMBANIL_DISABLE], this.mThumbnailExpectedSize[NEED_TO_SETFRAME]);
        }
        if (retVal <= 0) {
            return retVal;
        }
        return MODE_THUMBANIL_DISABLE;
    }

    private int cancel() {
        CamLog.d(TAG, "cancel()");
        int ret = OlaAutoPanoramaJNI.cancel();
        CamLog.d(TAG, "ret = " + ret);
        int i = MODE_THUMBANIL_DISABLE;
        while (i < this.mSetFrameThreads.size()) {
            if (this.mSetFrameThreads.get(i) != null && ((Thread) this.mSetFrameThreads.get(i)).isAlive()) {
                try {
                    ((Thread) this.mSetFrameThreads.get(i)).join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            i += NEED_TO_SETFRAME;
        }
        this.mSetFrameThreads.clear();
        this.mSetFrameThreads = null;
        if (this.mStartSynthesisTask != null && this.mStartSynthesisTask.getStatus() == Status.RUNNING) {
            try {
                if (this.mSynthesisCountDownLatch != null) {
                    this.mSynthesisCountDownLatch.await();
                }
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
        return ret;
    }

    public int destroy() {
        CamLog.d(TAG, "destroy()");
        cancel();
        OlaAutoPanoramaJNI.destroy();
        return MODE_THUMBANIL_DISABLE;
    }

    private void addFrameDataList(JOlaBitmap olaBitmap) {
        this.mSetFrameData.add(new JOlaBitmap(olaBitmap.width, olaBitmap.height, olaBitmap.imageFormat, olaBitmap.orientation, olaBitmap.imageData, olaBitmap.imageData.length));
    }

    public void setFrameProcess(JOlaBitmap olaBitmap) {
        addFrameDataList(olaBitmap);
        if (this.mSetFrameCount == 0) {
            this.mInitOrientation = olaBitmap.orientation;
        }
        this.mSetFrameCount += NEED_TO_SETFRAME;
        boolean drawGuideBox = this.mSetFrameCount < 10;
        CamLog.e(TAG, "mSetFrameCount = " + this.mSetFrameCount);
        if (this.mModeThumbnail == NEED_TO_SETFRAME) {
            Bitmap thumbnail = makeThumbnail();
            if (thumbnail != null) {
                this.mCallback.onGetThumbnailImage(thumbnail, drawGuideBox);
            }
        }
        Thread setFrameThread = new Thread(new SetFrameRunnable(this.mSetFrameCount));
        setFrameThread.setPriority(NEED_TO_SETFRAME);
        setFrameThread.start();
        this.mSetFrameThreads.add(setFrameThread);
    }

    private Bitmap makeThumbnail() {
        JOlaBitmap olabitmap = new JOlaBitmap();
        if (OlaAutoPanoramaJNI.makeThumbnail(olabitmap) < 0) {
            return null;
        }
        Bitmap thumbnail = Bitmap.createBitmap(olabitmap.width, olabitmap.height, Config.ARGB_8888);
        olabitmap.getBitmap(thumbnail);
        return thumbnail;
    }

    public int processPreview(JOlaBitmap olaBitmap) {
        this.mInfo.clear();
        int retVal = OlaAutoPanoramaJNI.processFrame(olaBitmap, this.mInfo);
        if (retVal < 0) {
            return retVal;
        }
        if (this.mCallback != null) {
            this.mCallback.onProcessFrame(this.mInfo);
            if (retVal == NEED_TO_SETFRAME) {
                if (this.mModeSetFrameImage == 0) {
                    if (this.mModeThumbnail == NEED_TO_SETFRAME && this.mInfo.status == 3) {
                        this.mCallback.onSetStartSyncForceDelay();
                    }
                    setFrameProcess(olaBitmap);
                } else {
                    this.mCallback.onTakePicture(this.mSetFrameCount);
                }
            }
            if (this.mInfo.displacement > this.mPanningSpeedThreshold) {
                if (!this.mWarningStatus) {
                    this.mWarningStatus = true;
                    this.mCallback.onPanningSpeedWarning(this.mWarningStatus);
                }
            } else if (this.mWarningStatus) {
                this.mWarningStatus = false;
                this.mCallback.onPanningSpeedWarning(this.mWarningStatus);
            }
            if (this.mInfo.status == 3) {
                if (retVal != NEED_TO_SETFRAME) {
                    this.mCallback.onAlarmStartSync();
                }
                if (retVal != NEED_TO_SETFRAME) {
                    runStartSynthesisTask();
                } else if (this.mModeSetFrameImage == NEED_TO_SETFRAME) {
                    this.mCallback.onSetStartSyncForceDelay();
                }
            }
        }
        return MODE_THUMBANIL_DISABLE;
    }

    public int processImage(Bitmap bitmap, int orientation) {
        CamLog.d(TAG, "Auto Panorama not support function processImage");
        return MODE_THUMBANIL_DISABLE;
    }

    public int stopProcess() {
        CamLog.d(TAG, "stopProcess()");
        int retVal = OlaAutoPanoramaJNI.stopProcess();
        if (retVal < 0) {
            CamLog.e(TAG, "auto panorama stop process fail!");
            this.mCallback.onComplete(null);
            return retVal;
        }
        runStartSynthesisTask();
        return MODE_THUMBANIL_DISABLE;
    }

    public void runStartSynthesisTask() {
        this.mStartSynthesisTask = new StartSynthesisTask();
        this.mStartSynthesisTask.execute(new Void[MODE_THUMBANIL_DISABLE]);
    }
}
