package com.lge.morpho.app.morphopanorama;

import android.app.Application;
import com.lge.morpho.app.morphopanorama.SavePanoramaImageRunnable.SaveInfo;
import com.lge.morpho.app.quickpanorama.MorphoImageStitcher;
import com.lge.morpho.app.quickpanorama.MorphoImageStitcher.ViewParam;

public class PanoramaApplication extends Application {
    public static final int SENSOR_CORRECTION_EXTRA_TIME = 1000;
    public static final int SENSOR_CORRECTION_TIME_BEFORE_HAND = 10000;
    public static final int SENSOR_CORRECTION_TIME_EVERYTIME = 3000;
    private MorphoImageStitcher mMorphoImageStitcher;
    private ViewParam mPostviewDefaultParam;
    private ViewParam mPostviewParam;
    private SaveInfo mSaveInfo;
    private SavePanoramaImageRunnable mSavePanoramaImageRunnable;
    private Thread mSavePanoramaImageThread;
    private String mSaveStillImageDir;
    private String mTmpStillImageDir;

    public void onCreate() {
    }

    public MorphoImageStitcher getMorphoImageStitcher() {
        if (this.mMorphoImageStitcher == null || this.mMorphoImageStitcher.isFinished()) {
            this.mMorphoImageStitcher = new MorphoImageStitcher();
        }
        return this.mMorphoImageStitcher;
    }

    public String getSaveStillImageDir() {
        return this.mSaveStillImageDir;
    }

    public String getTmpStillImageDir() {
        return this.mTmpStillImageDir;
    }

    public Thread getSavePanoramaImageThread() {
        return this.mSavePanoramaImageThread;
    }

    public SavePanoramaImageRunnable getSavePanoramaImageRunnable() {
        return this.mSavePanoramaImageRunnable;
    }

    public SaveInfo getSaveInfo() {
        return this.mSaveInfo;
    }

    public ViewParam getPostviewParam() {
        return this.mPostviewParam;
    }

    public ViewParam getPostviewDefaultParam() {
        return this.mPostviewDefaultParam;
    }

    public void setMorphoImageStitcher(MorphoImageStitcher image_stitcher) {
        this.mMorphoImageStitcher = image_stitcher;
    }

    public void setSaveStillImageDir(String path) {
        this.mSaveStillImageDir = path;
    }

    public void setTmpStillImageDir(String path) {
        this.mTmpStillImageDir = path;
    }

    public void setSavePanoramaImageThread(Thread thread) {
        this.mSavePanoramaImageThread = thread;
    }

    public void setSavePanoramaImageRunnable(SavePanoramaImageRunnable runnable) {
        this.mSavePanoramaImageRunnable = runnable;
    }

    public void setSaveInfo(SaveInfo info) {
        this.mSaveInfo = info;
    }

    public void setPostviewParam(ViewParam param) {
        this.mPostviewParam = param;
    }

    public void setPostviewDefaultParam(ViewParam param) {
        this.mPostviewDefaultParam = param;
    }
}
