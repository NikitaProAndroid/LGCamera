package com.lge.morpho.app.morphopanorama;

import android.content.Context;
import android.graphics.Rect;
import android.location.Location;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.util.CamLog;
import com.lge.morpho.app.quickpanorama.MorphoImageStitcher;
import com.lge.morpho.utils.multimedia.JpegHandler;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import java.io.File;

public class SavePanoramaImageRunnable implements Runnable {
    private static String LOG_TAG;
    private Context mContext;
    private long[] mDateTaken;
    private SavePanoramaImageListener mListner;
    private Location mLocation;
    private Object mLock;
    private MorphoImageStitcher mMorphoImageStitcher;
    private int mOutputType;
    private String mSaveDirPath;
    private String mSaveFileName;

    public static class SaveInfo {
        public long[] mDate;
        public Location mLocation;
        public int mOutputType;
        public String mSaveDir;
        public String mSaveFileName;
    }

    public interface SavePanoramaImageListener {
        void onSavePanoramaImage(String str);
    }

    public void setSavePanoramaImageListener(SavePanoramaImageListener listener) {
        synchronized (this.mLock) {
            this.mListner = listener;
        }
    }

    public SavePanoramaImageRunnable(Context context, MorphoImageStitcher stitcher, SaveInfo info) {
        LOG_TAG = getClass().getName();
        this.mMorphoImageStitcher = stitcher;
        this.mContext = context;
        this.mSaveDirPath = info.mSaveDir;
        this.mSaveFileName = info.mSaveFileName;
        this.mOutputType = info.mOutputType;
        this.mDateTaken = (long[]) info.mDate.clone();
        this.mLocation = info.mLocation;
        this.mLock = new Object();
    }

    public void run() {
        String directory = this.mSaveDirPath;
        String output_file_name = this.mSaveFileName;
        Rect output_rect = new Rect();
        int ret = this.mMorphoImageStitcher.getBoundingRect(output_rect);
        if (ret != 0) {
            CamLog.e(LOG_TAG, "mMorphoImageStitcher.getBoundingRect error ret:" + ret);
        }
        String output_file_path = directory + File.separator + output_file_name;
        int height = output_rect.bottom - output_rect.top;
        CamLog.d(LOG_TAG, "OutImageSize: w=" + (output_rect.right - output_rect.left) + " h=" + height);
        if (saveOutputImage(directory, output_file_name, this.mDateTaken, output_rect, this.mLocation, 1) == 0) {
            JpegHandler.setInExif(output_file_path, this.mLocation);
        } else {
            output_file_path = null;
        }
        if (this.mListner != null) {
            synchronized (this.mLock) {
                this.mListner.onSavePanoramaImage(output_file_path);
            }
        }
    }

    private int saveOutputImage(String directory, String filename, long[] dateTaken, Rect rect, Location location, int orientation) {
        int[] output_size = new int[1];
        String first_date = MediaProviderUtils.createDateStringForAppSeg(dateTaken[0]);
        String last_date = MediaProviderUtils.createDateStringForAppSeg(dateTaken[1]);
        String path = directory + File.separator + filename;
        int ret = this.mMorphoImageStitcher.saveOutputJpeg(path, rect, orientation, output_size, first_date, last_date, true);
        if (ret != 0) {
            CamLog.e(LOG_TAG, "mMorphoImageStitcher.saveOutputJpeg error ret:" + ret);
        } else {
            MediaProviderUtils.addImageExternal(this.mContext.getContentResolver(), path, MultimediaProperties.IMAGE_MIME_TYPE, 0, dateTaken[0], location);
        }
        return ret;
    }
}
