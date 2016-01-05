package com.lge.camera.util;

import android.content.Context;
import android.text.format.Time;
import com.lge.camera.VideoFile;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FileNamer {
    private static final int BURSTID_LENGTH_DCM = 8;
    private static final int BURSTID_LENGTH_NEWNAMING = 15;
    private static final int BURSTID_LENGTH_VZW = 10;
    public static final int STATUS_NOT_READY = 0;
    public static final int STATUS_READY = 1;
    private static FileNamer mFileNamer;
    private static String sBurstFirstTime;
    private int mBurstCount;
    private Thread mCheckAVIThread;
    private Thread mCheckJPEGThread;
    private Thread mCheckThread;
    private String mCurrFileName;
    private String mDCFFileName;
    private int mDCFFileStatus;
    private int mDCFFirstNumber;
    private long mDCFNumber;
    private int mDigitnum;
    private String mImageFileName;
    private long mImageFileNumber;
    private int mImageFileStatus;
    private boolean mInCheckDCF;
    private boolean mInCheckImage;
    private boolean mInCheckVideo;
    private int mLastMode;
    private String mScenemode;
    private boolean mStopThread;
    private int mStorageOldState;
    private int mStorageState;
    private int mTMCount;
    private String mTMFirstTime;
    private int mTMsaveCount;
    private String mTakeTime;
    private String mTempBurstFirstTime;
    private String mVideoFileName;
    private long mVideoFileNumber;
    private int mVideoFileStatus;
    private long temp;

    public FileNamer() {
        this.mStorageOldState = STATUS_READY;
        this.mLastMode = -1;
        this.mImageFileStatus = STATUS_NOT_READY;
        this.mVideoFileStatus = STATUS_NOT_READY;
        this.mDCFFileStatus = STATUS_NOT_READY;
        this.mStopThread = false;
        this.mCheckJPEGThread = null;
        this.mCheckAVIThread = null;
        this.mCheckThread = null;
        this.mBurstCount = STATUS_NOT_READY;
        this.mTempBurstFirstTime = "";
        this.mTMCount = STATUS_NOT_READY;
        this.mTMsaveCount = STATUS_NOT_READY;
        this.mTMFirstTime = "";
        this.mTakeTime = "";
        this.mScenemode = "";
        this.temp = 0;
    }

    static {
        sBurstFirstTime = "";
        mFileNamer = null;
    }

    public static FileNamer get() {
        if (mFileNamer == null) {
            mFileNamer = new FileNamer();
        }
        return mFileNamer;
    }

    public void startFileNamer(Context context, int mode, int storage, String dir, boolean useThread) {
        CamLog.i(FaceDetector.TAG, "create()-start");
        if (!ProjectVariables.isUseNewNamingRule()) {
            initializeFileNumber(context, storage);
            if (ProjectVariables.getUseDCFRule()) {
                startCheckFileName_DCF(context, mode, dir, useThread);
            } else {
                startCheckFileName(context, mode, storage, dir, useThread);
                if (mode == STATUS_READY && ModelProperties.getCarrierCode() == 4) {
                    startCheckFileName(context, mode, storage, dir, useThread);
                }
            }
            CamLog.i(FaceDetector.TAG, "create()-end");
        }
    }

    private void initializeFileNumber(Context context, int storage) {
        if (context == null) {
            CamLog.d(FaceDetector.TAG, "Cannot initialize file number because context is null");
            return;
        }
        this.mImageFileNumber = SharedPreferenceUtil.getAccumulatedPictureCount(context, storage);
        if (this.mImageFileNumber == 0) {
            this.mImageFileNumber = 1;
        }
        this.mImageFileStatus = STATUS_NOT_READY;
        this.mVideoFileNumber = SharedPreferenceUtil.getAccumulatedVideoCount(context, storage);
        if (this.mVideoFileNumber == 0) {
            this.mVideoFileNumber = 1;
        }
        this.mVideoFileStatus = STATUS_NOT_READY;
        this.mDCFFirstNumber = SharedPreferenceUtil.getAccumulatedDCFFirstCount(context);
        this.mDCFNumber = SharedPreferenceUtil.getAccumulatedDCFCount(context);
        if (this.mDCFFirstNumber == -1 && this.mDCFNumber == 0) {
            this.mDCFNumber = 1;
        }
        if (this.mDCFFirstNumber == -1 || this.mDCFFirstNumber == 0) {
            this.mDCFFirstNumber = 48;
        }
        this.mDigitnum = SharedPreferenceUtil.getAccumulatedDCFDigit(context);
        if (this.mDigitnum == 0) {
            this.mDigitnum = 4;
        }
        this.mDCFFileStatus = STATUS_NOT_READY;
    }

    public void close(Context context, int storage) {
        CamLog.i(FaceDetector.TAG, "FileNamingHelper close 1/4 " + this.mInCheckImage + " " + this.mInCheckVideo);
        if (mFileNamer == null) {
            CamLog.d(FaceDetector.TAG, "Already close().");
            return;
        }
        if (this.mInCheckImage || this.mInCheckVideo || this.mInCheckDCF) {
            stopThread();
        }
        while (true) {
            if (!this.mInCheckImage && !this.mInCheckVideo && !this.mInCheckDCF) {
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (context != null) {
            SharedPreferenceUtil.saveAccumulatedPictureCount(context, storage, this.mImageFileNumber);
            CamLog.i(FaceDetector.TAG, "FileNamingHelper close 2/4 mImageFileNumber:" + this.mImageFileNumber);
            SharedPreferenceUtil.saveAccumulatedVideoCount(context, storage, this.mVideoFileNumber);
            CamLog.i(FaceDetector.TAG, "FileNamingHelper close 3/4 mVideoFileNumber:" + this.mVideoFileNumber);
            SharedPreferenceUtil.saveAccumulatedDCFCount(context, this.mDCFNumber);
            SharedPreferenceUtil.saveAccumulatedDCFFirstCount(context, this.mDCFFirstNumber);
            SharedPreferenceUtil.saveAccumulatedDCFDigit(context, this.mDigitnum);
            CamLog.i(FaceDetector.TAG, "FileNamingHelper close 4/4 mDCFNumber:" + this.mDCFFirstNumber + "/" + this.mDCFNumber + "/" + this.mDigitnum);
        } else {
            CamLog.d(FaceDetector.TAG, "Cannot accumulate DCF because context is null");
        }
        this.mImageFileName = null;
        this.mVideoFileName = null;
        this.mDCFFileName = null;
        this.mCurrFileName = null;
        mFileNamer = null;
    }

    private static String makePictureFileName(long count) {
        Object[] objArr;
        if (ModelProperties.getCarrierCode() == 4) {
            if (count > 99999) {
                objArr = new Object[STATUS_READY];
                objArr[STATUS_NOT_READY] = Long.valueOf(count);
                return String.format("IMG%d", objArr);
            }
            objArr = new Object[STATUS_READY];
            objArr[STATUS_NOT_READY] = Long.valueOf(count);
            return String.format("IMG%05d", objArr);
        } else if (count > 999) {
            objArr = new Object[STATUS_READY];
            objArr[STATUS_NOT_READY] = Long.valueOf(count);
            return String.format("IMG%d", objArr);
        } else {
            objArr = new Object[STATUS_READY];
            objArr[STATUS_NOT_READY] = Long.valueOf(count);
            return String.format("IMG%03d", objArr);
        }
    }

    private static String makeVideoFileName(long count) {
        Object[] objArr;
        if (ModelProperties.getCarrierCode() == 4) {
            if (count > 99999) {
                objArr = new Object[STATUS_READY];
                objArr[STATUS_NOT_READY] = Long.valueOf(count);
                return String.format("MOV%d", objArr);
            }
            objArr = new Object[STATUS_READY];
            objArr[STATUS_NOT_READY] = Long.valueOf(count);
            return String.format("MOV%05d", objArr);
        } else if (count > 999) {
            objArr = new Object[STATUS_READY];
            objArr[STATUS_NOT_READY] = Long.valueOf(count);
            return String.format("MOV%d", objArr);
        } else {
            objArr = new Object[STATUS_READY];
            objArr[STATUS_NOT_READY] = Long.valueOf(count);
            return String.format("MOV%03d", objArr);
        }
    }

    private static String makeFileNameByDCFRule(int firstNumber, int digit, long count) {
        Object[] objArr = new Object[STATUS_READY];
        objArr[STATUS_NOT_READY] = Character.valueOf((char) firstNumber);
        String fileName = String.format("CAM%s", objArr);
        String fileNum = String.valueOf(count);
        int tmpNum = digit - fileNum.length();
        for (int i = STATUS_NOT_READY; i < tmpNum; i += STATUS_READY) {
            fileName = fileName + "0";
        }
        return fileName + fileNum;
    }

    public void updateNextFileIndex(Context context, int mode, int storage, String dir, boolean useThread, String fileName) {
        if (ProjectVariables.getUseDCFRule()) {
            this.mDCFFileStatus = STATUS_NOT_READY;
            startCheckFileName_DCF(context, mode, dir, useThread);
            return;
        }
        this.mImageFileStatus = STATUS_NOT_READY;
        if (!(fileName == null || fileName.equals(makePictureFileName(this.mImageFileNumber - 1)))) {
            this.mImageFileNumber--;
        }
        if (ModelProperties.getCarrierCode() == 4) {
            startCheckFileName(context, STATUS_NOT_READY, storage, dir, useThread);
        } else {
            startCheckFileName(context, mode, storage, dir, useThread);
        }
    }

    public String getFileName(Context context, int mode, int storage, String dir, boolean useThread) {
        String ret;
        switch (ModelProperties.getCarrierCode()) {
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                if (mode != 0) {
                    if (mode != STATUS_READY) {
                        CamLog.i(FaceDetector.TAG, "error! get file name fail!");
                        break;
                    }
                    ret = startCheckFileNameCDMA(mode, dir, "");
                    CamLog.i(FaceDetector.TAG, "get new file name = " + ret);
                    return ret;
                }
                CamLog.i(FaceDetector.TAG, "getFileName for CDMA");
                ret = startCheckFileNameCDMA(mode, dir, "");
                CamLog.i(FaceDetector.TAG, "get file name = " + ret);
                return ret;
            default:
                if (ProjectVariables.getUseDCFRule() && this.mDCFFileStatus == STATUS_READY) {
                    addDCFCount();
                    this.mDCFFileStatus = STATUS_NOT_READY;
                    ret = this.mDCFFileName;
                    startCheckFileName_DCF(context, mode, dir, useThread);
                    CamLog.i(FaceDetector.TAG, "get file name = " + ret);
                    return ret;
                } else if (mode == 0 && this.mImageFileStatus == STATUS_READY) {
                    this.mImageFileNumber++;
                    this.mImageFileStatus = STATUS_NOT_READY;
                    ret = this.mImageFileName;
                    startCheckFileName(context, mode, storage, dir, useThread);
                    CamLog.i(FaceDetector.TAG, "get file name = " + ret);
                    return ret;
                } else if (mode != STATUS_READY || this.mVideoFileStatus != STATUS_READY) {
                    CamLog.i(FaceDetector.TAG, "error! get file name fail!");
                    break;
                } else {
                    this.mVideoFileNumber++;
                    this.mVideoFileStatus = STATUS_NOT_READY;
                    ret = this.mVideoFileName;
                    startCheckFileName(context, mode, storage, dir, useThread);
                    CamLog.i(FaceDetector.TAG, "get new file name = " + ret);
                    return ret;
                }
        }
        return null;
    }

    private synchronized void startCheckFileName(Context context, int mode, int storage, String dir, boolean useThread) {
        CamLog.i(FaceDetector.TAG, "startCheckFileName (mode : " + mode + ", useThread : " + useThread + ")");
        if (this.mStorageState != 0) {
            CamLog.w(FaceDetector.TAG, "storage State = NOT AVAILABLE, " + this.mStorageState);
        } else {
            CamLog.i(FaceDetector.TAG, "startCheckFileName stopThread.");
            stopThread();
            this.mLastMode = mode;
            this.mStopThread = false;
            if (mode == 0) {
                startCheckFileNameForCamera(context, storage, dir, useThread);
            } else {
                startCheckFileNameForCamcorder(context, storage, dir, useThread);
            }
        }
    }

    private void startCheckFileNameForCamcorder(final Context context, final int storage, final String dir, boolean useThread) {
        if (this.mVideoFileStatus == STATUS_READY) {
            CamLog.w(FaceDetector.TAG, "mVideoFileStatus == STATUS_READY");
        } else if (useThread) {
            this.mCheckAVIThread = new Thread(new Runnable() {
                public void run() {
                    FileNamer.this.mInCheckVideo = true;
                    CamLog.i(FaceDetector.TAG, "startCheckFileName video with thread");
                    long startTime = System.currentTimeMillis();
                    String fileName = FileNamer.makeVideoFileName(FileNamer.this.mVideoFileNumber);
                    do {
                        if (!Common.isFileExist(dir + fileName + VideoFile.VIDEO_EXTENSION_3GP) && !Common.isFileExist(dir + fileName + VideoFile.VIDEO_EXTENSION_MP4)) {
                            break;
                        }
                        FileNamer.this.mVideoFileNumber = 1 + FileNamer.this.mVideoFileNumber;
                        fileName = FileNamer.makeVideoFileName(FileNamer.this.mVideoFileNumber);
                        if (Thread.interrupted()) {
                            break;
                        }
                    } while (!FileNamer.this.mStopThread);
                    FileNamer.this.mStopThread = true;
                    if (FileNamer.this.mStopThread) {
                        CamLog.w(FaceDetector.TAG, "startCheckFileName thread interrupted!");
                        FileNamer.this.mInCheckVideo = false;
                        FileNamer.this.mStopThread = false;
                        return;
                    }
                    FileNamer.this.mVideoFileName = fileName;
                    FileNamer.this.mVideoFileStatus = FileNamer.STATUS_READY;
                    CamLog.d(FaceDetector.TAG, "video file is ready " + FileNamer.this.mVideoFileName);
                    CamLog.i(FaceDetector.TAG, "startCheckFileName is finished with thread (elapse time = " + (System.currentTimeMillis() - startTime) + "ms)");
                    FileNamer.this.mInCheckVideo = false;
                    if (context != null) {
                        SharedPreferenceUtil.saveAccumulatedVideoCount(context, storage, FileNamer.this.mVideoFileNumber);
                    }
                }
            });
            this.mCheckAVIThread.start();
        } else {
            this.mInCheckVideo = true;
            CamLog.i(FaceDetector.TAG, "startCheckFileName video without thread");
            long startTime = System.currentTimeMillis();
            String fileName = makeVideoFileName(this.mVideoFileNumber);
            do {
                if (!Common.isFileExist(dir + fileName + VideoFile.VIDEO_EXTENSION_3GP) && !Common.isFileExist(dir + fileName + VideoFile.VIDEO_EXTENSION_MP4)) {
                    break;
                }
                this.mVideoFileNumber++;
                fileName = makeVideoFileName(this.mVideoFileNumber);
            } while (!this.mStopThread);
            if (this.mStopThread) {
                CamLog.w(FaceDetector.TAG, "startCheckFileName is stop without thread in Video!");
                this.mInCheckVideo = false;
                this.mStopThread = false;
                return;
            }
            this.mVideoFileName = fileName;
            this.mVideoFileStatus = STATUS_READY;
            CamLog.i(FaceDetector.TAG, "video file is ready " + this.mVideoFileName);
            CamLog.i(FaceDetector.TAG, "startCheckFileName is finished without thread (elapse time = " + (System.currentTimeMillis() - startTime) + "ms)");
            this.mInCheckVideo = false;
            if (context != null) {
                SharedPreferenceUtil.saveAccumulatedVideoCount(context, storage, this.mVideoFileNumber);
            }
        }
    }

    private void startCheckFileNameForCamera(final Context context, final int storage, final String dir, boolean useThread) {
        if (this.mImageFileStatus == STATUS_READY) {
            CamLog.w(FaceDetector.TAG, "mImageFileStatus == STATUS_READY");
        } else if (useThread) {
            this.mCheckJPEGThread = new Thread(new Runnable() {
                public void run() {
                    FileNamer.this.mInCheckImage = true;
                    CamLog.i(FaceDetector.TAG, "startCheckFileName image with thread : file count (" + FileNamer.this.mImageFileNumber + ")");
                    long startTime = System.currentTimeMillis();
                    long imageFileNumber = FileNamer.this.mImageFileNumber;
                    String fileName = FileNamer.makePictureFileName(imageFileNumber);
                    while (Common.isFileExist(dir + fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT)) {
                        imageFileNumber++;
                        fileName = FileNamer.makePictureFileName(imageFileNumber);
                        if (!Thread.interrupted()) {
                            if (FileNamer.this.mStopThread) {
                            }
                        }
                        FileNamer.this.mStopThread = true;
                        break;
                    }
                    FileNamer.this.mImageFileNumber = imageFileNumber;
                    if (FileNamer.this.mStopThread) {
                        CamLog.w(FaceDetector.TAG, "startCheckFileName thread interrupted!");
                        FileNamer.this.mInCheckImage = false;
                        FileNamer.this.mStopThread = false;
                        return;
                    }
                    FileNamer.this.mImageFileName = fileName;
                    FileNamer.this.mImageFileStatus = FileNamer.STATUS_READY;
                    CamLog.i(FaceDetector.TAG, "image file is ready " + FileNamer.this.mImageFileName);
                    CamLog.i(FaceDetector.TAG, "startCheckFileName is finished with thread (elapse time = " + (System.currentTimeMillis() - startTime) + "ms)");
                    FileNamer.this.mInCheckImage = false;
                    if (context != null) {
                        SharedPreferenceUtil.saveAccumulatedPictureCount(context, storage, FileNamer.this.mImageFileNumber);
                    }
                }
            });
            this.mCheckJPEGThread.start();
        } else {
            this.mInCheckImage = true;
            CamLog.i(FaceDetector.TAG, "startCheckFileName image without thread: " + this.mImageFileNumber);
            long startTime = System.currentTimeMillis();
            long imageFileNumber = this.mImageFileNumber;
            String fileName = makePictureFileName(imageFileNumber);
            while (Common.isFileExist(dir + fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT)) {
                imageFileNumber++;
                fileName = makePictureFileName(imageFileNumber);
                if (this.mStopThread) {
                    break;
                }
            }
            this.mImageFileNumber = imageFileNumber;
            if (this.mStopThread) {
                CamLog.w(FaceDetector.TAG, "startCheckFileName is stop in Camera!");
                this.mInCheckImage = false;
                this.mStopThread = false;
                return;
            }
            this.mImageFileName = fileName;
            this.mImageFileStatus = STATUS_READY;
            CamLog.i(FaceDetector.TAG, "image file is ready " + this.mImageFileName);
            CamLog.i(FaceDetector.TAG, "startCheckFileName is finished without thread (elapse time = " + (System.currentTimeMillis() - startTime) + "ms)");
            this.mInCheckImage = false;
            if (context != null) {
                SharedPreferenceUtil.saveAccumulatedPictureCount(context, storage, this.mImageFileNumber);
            }
        }
    }

    private synchronized void startCheckFileName_DCF(final Context context, int mode, final String dir, boolean useThread) {
        CamLog.i(FaceDetector.TAG, "startCheckFileName_DCF (mode : " + mode + ", useThread : " + useThread + ")");
        if (this.mStorageState != 0) {
            CamLog.w(FaceDetector.TAG, "storage State = NOT AVAILABLE," + this.mStorageState);
        } else if (this.mDCFFileStatus == STATUS_READY) {
            CamLog.w(FaceDetector.TAG, "mDCFFileStatus == STATUS_READY");
        } else {
            CamLog.i(FaceDetector.TAG, "startCheckFileName stopThread.");
            stopThread();
            this.mLastMode = mode;
            this.mStopThread = false;
            if (useThread) {
                this.mCheckThread = new Thread(new Runnable() {
                    public void run() {
                        FileNamer.this.mInCheckDCF = true;
                        CamLog.i(FaceDetector.TAG, "startCheckFileName_DCF with thread : file count (" + FileNamer.this.mDCFFirstNumber + ", " + FileNamer.this.mDCFNumber + ")");
                        long startTime = System.currentTimeMillis();
                        String fileName = FileNamer.makeFileNameByDCFRule(FileNamer.this.mDCFFirstNumber, FileNamer.this.mDigitnum, FileNamer.this.mDCFNumber);
                        do {
                            if (!Common.isFileExist(dir + fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT) && !Common.isFileExist(dir + fileName + VideoFile.VIDEO_EXTENSION_3GP) && !Common.isFileExist(dir + fileName + VideoFile.VIDEO_EXTENSION_MP4)) {
                                break;
                            }
                            FileNamer.this.addDCFCount();
                            fileName = FileNamer.makeFileNameByDCFRule(FileNamer.this.mDCFFirstNumber, FileNamer.this.mDigitnum, FileNamer.this.mDCFNumber);
                            if (Thread.interrupted()) {
                                break;
                            }
                        } while (!FileNamer.this.mStopThread);
                        FileNamer.this.mStopThread = true;
                        if (FileNamer.this.mStopThread) {
                            CamLog.w(FaceDetector.TAG, "startCheckFileName_DCF thread interrupted!");
                            FileNamer.this.mInCheckDCF = false;
                            FileNamer.this.mStopThread = false;
                            return;
                        }
                        FileNamer.this.mCurrFileName = FileNamer.this.mDCFFileName;
                        FileNamer.this.mDCFFileName = fileName;
                        FileNamer.this.mDCFFileStatus = FileNamer.STATUS_READY;
                        CamLog.i(FaceDetector.TAG, "next dcf file is ready " + FileNamer.this.mCurrFileName + " / " + FileNamer.this.mDCFFileName);
                        CamLog.i(FaceDetector.TAG, "startCheckFileName_DCF is finished with thread (elapse time = " + (System.currentTimeMillis() - startTime) + "ms)");
                        FileNamer.this.mInCheckDCF = false;
                        if (context != null) {
                            SharedPreferenceUtil.saveAccumulatedDCFCount(context, FileNamer.this.mDCFNumber);
                            SharedPreferenceUtil.saveAccumulatedDCFFirstCount(context, FileNamer.this.mDCFFirstNumber);
                            SharedPreferenceUtil.saveAccumulatedDCFDigit(context, FileNamer.this.mDigitnum);
                        }
                    }
                });
                this.mCheckThread.start();
            } else {
                this.mInCheckDCF = true;
                CamLog.i(FaceDetector.TAG, "startCheckFileName_DCF without thread : " + this.mDCFFirstNumber + ", " + this.mDCFNumber);
                long startTime = System.currentTimeMillis();
                String fileName = makeFileNameByDCFRule(this.mDCFFirstNumber, this.mDigitnum, this.mDCFNumber);
                do {
                    if (!Common.isFileExist(dir + fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT) && !Common.isFileExist(dir + fileName + VideoFile.VIDEO_EXTENSION_3GP) && !Common.isFileExist(dir + fileName + VideoFile.VIDEO_EXTENSION_MP4)) {
                        break;
                    }
                    addDCFCount();
                    fileName = makeFileNameByDCFRule(this.mDCFFirstNumber, this.mDigitnum, this.mDCFNumber);
                } while (!this.mStopThread);
                if (this.mStopThread) {
                    CamLog.w(FaceDetector.TAG, "startCheckFileName_DCF is stop without Thread by DCF rules!");
                    this.mInCheckDCF = false;
                    this.mStopThread = false;
                } else {
                    this.mDCFFileName = fileName;
                    this.mDCFFileStatus = STATUS_READY;
                    CamLog.i(FaceDetector.TAG, "dcf file is ready " + this.mDCFFileName);
                    CamLog.i(FaceDetector.TAG, "startCheckFileName_DCF is finished without thread (elapse time = " + (System.currentTimeMillis() - startTime) + "ms)");
                    this.mInCheckDCF = false;
                    if (context != null) {
                        SharedPreferenceUtil.saveAccumulatedDCFCount(context, this.mDCFNumber);
                        SharedPreferenceUtil.saveAccumulatedDCFFirstCount(context, this.mDCFFirstNumber);
                        SharedPreferenceUtil.saveAccumulatedDCFDigit(context, this.mDigitnum);
                    }
                }
            }
        }
    }

    public boolean getFileStatus(int mode) {
        if (ProjectVariables.getUseDCFRule()) {
            if (this.mDCFFileStatus == STATUS_READY) {
                return true;
            }
            return false;
        } else if (mode == 0) {
            if (this.mImageFileStatus != STATUS_READY) {
                return false;
            }
            return true;
        } else if (this.mVideoFileStatus != STATUS_READY) {
            return false;
        } else {
            return true;
        }
    }

    public void setStorageState(Context context, int mode, int storage, String dir, int state) {
        CamLog.d(FaceDetector.TAG, "setStorageState " + state + "/" + mode);
        if (this.mStorageOldState == state && this.mLastMode == mode) {
            CamLog.d(FaceDetector.TAG, "setStorageState: status same");
            return;
        }
        this.mStorageOldState = state;
        if (this.mStorageState != 0) {
            CamLog.w(FaceDetector.TAG, "storage state : NOT AVAILABLE, " + this.mStorageState);
            stopThread();
            this.mImageFileStatus = STATUS_NOT_READY;
            this.mVideoFileStatus = STATUS_NOT_READY;
            this.mDCFFileStatus = STATUS_NOT_READY;
            return;
        }
        CamLog.w(FaceDetector.TAG, "storage state :  AVAILABLE ");
        if (!getFileStatus(mode)) {
            CamLog.w(FaceDetector.TAG, "setStorageState : startCheckFileName with thread");
            if (ProjectVariables.getUseDCFRule()) {
                startCheckFileName_DCF(context, mode, dir, true);
            } else {
                startCheckFileName(context, mode, storage, dir, true);
            }
        }
    }

    public void stopThread() {
        if (this.mCheckThread != null && this.mCheckThread.isAlive()) {
            this.mCheckThread.interrupt();
            try {
                this.mCheckThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.mCheckThread = null;
        if (this.mCheckJPEGThread != null && this.mCheckJPEGThread.isAlive()) {
            this.mCheckJPEGThread.interrupt();
            try {
                this.mCheckJPEGThread.join();
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
        this.mCheckJPEGThread = null;
        if (this.mCheckAVIThread != null && this.mCheckAVIThread.isAlive()) {
            this.mCheckAVIThread.interrupt();
            try {
                this.mCheckAVIThread.join();
            } catch (InterruptedException e22) {
                e22.printStackTrace();
            }
        }
        this.mCheckAVIThread = null;
        this.mStopThread = true;
    }

    public void setErrorFeedback(int mode) {
        if (mode == 0) {
            this.mImageFileNumber--;
        } else {
            this.mVideoFileNumber--;
        }
        if (ProjectVariables.getUseDCFRule()) {
            subtractDCFCount();
            CamLog.w(FaceDetector.TAG, "error feedback dcf = " + this.mDCFFirstNumber + ", " + this.mDCFNumber);
        }
        CamLog.w(FaceDetector.TAG, "error feedback image = " + this.mImageFileNumber + ", video = " + this.mVideoFileNumber);
    }

    private String makePictureFileNameForCDMA(String fileName, long AscCode) {
        if (fileName.length() >= 11) {
            fileName = fileName.substring(STATUS_NOT_READY, BURSTID_LENGTH_VZW);
        }
        if (AscCode <= 122) {
            this.temp = 0;
            return fileName + String.valueOf((char) ((int) AscCode));
        }
        StringBuilder append = new StringBuilder().append(fileName).append(String.valueOf((char) ((int) 122))).append("[");
        long j = this.temp;
        this.temp = 1 + j;
        return append.append(j).append("]").toString();
    }

    private String makeCurrentDateToString() {
        Time time = new Time();
        time.setToNow();
        String CurrentTime = time.toString();
        CamLog.i(FaceDetector.TAG, "YYYYMMDDTHHDDSS : " + time.toString());
        String month = CurrentTime.substring(4, 6);
        String monthDay = CurrentTime.substring(6, BURSTID_LENGTH_DCM);
        String year = CurrentTime.substring(2, 4);
        String hour = CurrentTime.substring(9, 11);
        String minute = CurrentTime.substring(11, 13);
        String fileName = month + "" + monthDay + "" + year + "" + hour + "" + minute;
        CamLog.i(FaceDetector.TAG, "fileName : " + month + "." + monthDay + "." + year + "." + hour + "." + minute);
        return fileName;
    }

    private String startCheckFileNameCDMA(int mode, String dir, String shotMode) {
        CamLog.i(FaceDetector.TAG, "startCheckFileNameCDMA mode is " + mode);
        long start;
        String fileName;
        if (mode == 0) {
            this.mInCheckImage = true;
            start = 96;
            if (shotMode.equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS)) {
                fileName = sBurstFirstTime + getModename(shotMode);
                int samecount = STATUS_NOT_READY;
                String fullfileName = dir + fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
                while (Common.isFileExist(fullfileName)) {
                    samecount += STATUS_READY;
                    fullfileName = dir + fileName + "(" + Integer.toString(samecount) + ")" + CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
                }
                if (samecount > 0) {
                    fileName = fileName + "(" + Integer.toString(samecount) + ")";
                }
            } else {
                fileName = makeCurrentDateToString();
                while (Common.isFileExist(dir + fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT)) {
                    start++;
                    fileName = makePictureFileNameForCDMA(fileName, start);
                }
            }
            this.mImageFileName = fileName;
            this.mInCheckImage = false;
            return this.mImageFileName;
        }
        this.mInCheckVideo = true;
        fileName = makeCurrentDateToString();
        start = 96;
        while (true) {
            if (Common.isFileExist(dir + fileName + VideoFile.VIDEO_EXTENSION_MP4) || Common.isFileExist(dir + fileName + VideoFile.VIDEO_EXTENSION_3GP)) {
                start++;
                fileName = makePictureFileNameForCDMA(fileName, start);
            } else {
                this.mVideoFileName = fileName;
                this.mInCheckVideo = false;
                return this.mVideoFileName;
            }
        }
    }

    public void reload(Context context, int mode, int storage, String dir, boolean useThread) {
        if (!ProjectVariables.isUseNewNamingRule()) {
            CamLog.i(FaceDetector.TAG, "&&&&&&&&&&&&& reload call");
            if (ProjectVariables.getUseDCFRule()) {
                this.mDCFFileStatus = STATUS_NOT_READY;
                startCheckFileName_DCF(context, mode, dir, useThread);
                return;
            }
            initializeFileNumber(context, storage);
            startCheckFileName(context, mode, storage, dir, useThread);
            if (mode == STATUS_READY && ModelProperties.getCarrierCode() == 4) {
                startCheckFileName(context, STATUS_NOT_READY, storage, dir, useThread);
            }
        }
    }

    private void addDCFCount() {
        int digitNum = this.mDigitnum;
        int dcfFirstNumber = this.mDCFFirstNumber;
        long dcfNumber = this.mDCFNumber + 1;
        if (Math.pow(10.0d, (double) digitNum) <= ((double) dcfNumber)) {
            dcfNumber = 0;
            if ((dcfFirstNumber > 47 && dcfFirstNumber < 57) || (dcfFirstNumber > 65 && dcfFirstNumber < 90)) {
                dcfFirstNumber += STATUS_READY;
            } else if (dcfFirstNumber == 57) {
                dcfFirstNumber = 65;
            } else if (dcfFirstNumber == 90) {
                dcfFirstNumber = 48;
                digitNum += STATUS_READY;
                dcfNumber = 1;
            }
        }
        this.mDigitnum = digitNum;
        this.mDCFFirstNumber = dcfFirstNumber;
        this.mDCFNumber = dcfNumber;
    }

    private void subtractDCFCount() {
        this.mDCFNumber--;
        double tmpNum = Math.pow(10.0d, (double) this.mDigitnum);
        if (this.mDCFFirstNumber == 48 && this.mDCFNumber == 0) {
            this.mDCFNumber = ((long) tmpNum) - 1;
            this.mDCFFirstNumber = 90;
            this.mDigitnum--;
        } else if (this.mDCFNumber == -1) {
            this.mDCFNumber = ((long) tmpNum) - 1;
            if ((this.mDCFFirstNumber > 48 && this.mDCFFirstNumber < 58) || (this.mDCFFirstNumber > 66 && this.mDCFFirstNumber < 91)) {
                this.mDCFFirstNumber--;
            } else if (this.mDCFFirstNumber == 65) {
                this.mDCFFirstNumber = 57;
            } else if (this.mDCFFirstNumber == 48) {
                this.mDCFFirstNumber = 90;
                this.mDigitnum--;
            }
        }
        CamLog.i(FaceDetector.TAG, "subtractDCFCount " + this.mDCFFirstNumber + "/" + this.mDCFNumber + "/" + this.mDigitnum);
    }

    public String getFileNewName(Context context, int mode, int storage, String dir, boolean useThread, String shotMode, boolean isBurstFirst) {
        String ret = null;
        CamLog.i(FaceDetector.TAG, "getFileNewName   isBurstFirst  : " + isBurstFirst);
        processBurstCount(isBurstFirst);
        if (ProjectVariables.isUseNewNamingRule()) {
            ret = startCheckFileNamebyTime(mode, dir, shotMode);
        } else if (ModelProperties.getCarrierCode() != 6) {
            if (ProjectVariables.getUseDCFRule() && this.mDCFFileStatus == STATUS_READY) {
                addDCFCount();
                this.mDCFFileStatus = STATUS_NOT_READY;
                ret = this.mDCFFileName;
                startCheckFileName_DCF(context, mode, dir, useThread);
                CamLog.i(FaceDetector.TAG, "get file name = " + ret);
            } else if (mode == 0 && this.mImageFileStatus == STATUS_READY) {
                this.mImageFileNumber++;
                this.mImageFileStatus = STATUS_NOT_READY;
                ret = this.mImageFileName;
                startCheckFileName(context, mode, storage, dir, useThread);
                CamLog.i(FaceDetector.TAG, "get file name = " + ret);
            } else if (mode == STATUS_READY && this.mVideoFileStatus == STATUS_READY) {
                this.mVideoFileNumber++;
                this.mVideoFileStatus = STATUS_NOT_READY;
                ret = this.mVideoFileName;
                startCheckFileName(context, mode, storage, dir, useThread);
                CamLog.i(FaceDetector.TAG, "get new file name = " + ret);
            }
            CamLog.i(FaceDetector.TAG, "error! get file name fail!");
        } else if (mode == 0) {
            CamLog.i(FaceDetector.TAG, "getFileName for CDMA");
            ret = startCheckFileNameCDMA(mode, dir, shotMode);
            CamLog.i(FaceDetector.TAG, "get file name = " + ret);
        } else if (mode == STATUS_READY) {
            ret = startCheckFileNameCDMA(mode, dir, shotMode);
            CamLog.i(FaceDetector.TAG, "get new file name = " + ret);
        }
        CamLog.i(FaceDetector.TAG, "getFileNewName : " + ret);
        return ret;
    }

    public void setBurstFirstTime(String takeTime) {
        if (ModelProperties.getCarrierCode() == 6) {
            String sYMD = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String sYY = sYMD.substring(STATUS_NOT_READY, 4).substring(2, 4);
            String sMM = sYMD.substring(4, 6);
            this.mTempBurstFirstTime = sMM + sYMD.substring(6, BURSTID_LENGTH_DCM) + sYY + takeTime.substring(STATUS_NOT_READY, 4);
        } else {
            this.mTempBurstFirstTime = getNamebyDate() + "_" + getNamebyTime();
        }
        CamLog.d(FaceDetector.TAG, "setBurstFirstTime  temp : " + this.mTempBurstFirstTime + "  : burstTime  :" + sBurstFirstTime);
    }

    public void setTMSaveCount(int saveCount) {
        this.mTMCount = saveCount;
        this.mTMsaveCount = saveCount;
        CamLog.d(FaceDetector.TAG, "setTMSaveCount :   " + saveCount);
    }

    private String startCheckFileNamebyTime(int cameraMode, String dir, String shotmode) {
        CamLog.i(FaceDetector.TAG, "startCheckFileNameCDMA mode is " + cameraMode);
        String fileName = "";
        String fullfileName;
        int samecount;
        if (cameraMode == 0) {
            this.mInCheckImage = true;
            fileName = makeFilenamebyTime(shotmode, cameraMode);
            fullfileName = dir + fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
            samecount = STATUS_NOT_READY;
            while (Common.isFileExist(fullfileName)) {
                samecount += STATUS_READY;
                fullfileName = dir + fileName + "(" + Integer.toString(samecount) + ")" + CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
                CamLog.d(FaceDetector.TAG, "filename exist :  " + fullfileName);
            }
            if (samecount > 0) {
                fileName = fileName + "(" + Integer.toString(samecount) + ")";
            }
            this.mImageFileName = fileName;
            this.mInCheckImage = false;
            CamLog.d(FaceDetector.TAG, "startCheckFileNamebyTime mImageFileName:  " + this.mImageFileName);
            return this.mImageFileName;
        }
        this.mInCheckVideo = true;
        fileName = makeFilenamebyTime(shotmode, cameraMode);
        fullfileName = dir + fileName;
        samecount = STATUS_NOT_READY;
        while (true) {
            if (!Common.isFileExist(fullfileName + VideoFile.VIDEO_EXTENSION_MP4) && !Common.isFileExist(fullfileName + VideoFile.VIDEO_EXTENSION_3GP)) {
                break;
            }
            samecount += STATUS_READY;
            fullfileName = fileName + "(" + Integer.toString(samecount) + ")";
        }
        if (samecount > 0) {
            fileName = fileName + "(" + Integer.toString(samecount) + ")";
        }
        this.mVideoFileName = fileName;
        this.mInCheckVideo = false;
        CamLog.d(FaceDetector.TAG, "startCheckFileNamebyTime mVideoFileName:  " + this.mVideoFileName);
        return this.mVideoFileName;
    }

    public void markTakeTime(String shotmode, String scenemode) {
        String takeTime = getNamebyTime();
        if (shotmode.equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS)) {
            setBurstFirstTime(takeTime);
            CamLog.d(FaceDetector.TAG, "markTakeTime mBurstFirstTime   : " + shotmode + "    :   " + sBurstFirstTime);
        } else if (shotmode.equals(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE)) {
            String filename = getNamebyDate() + "_" + takeTime;
            this.mTMFirstTime = filename;
            CamLog.d(FaceDetector.TAG, "markTakeTime    mTMFirstTime: " + shotmode + "    :   " + filename);
        } else {
            this.mTakeTime = getNamebyDate() + "_" + takeTime;
            if (shotmode.equals(CameraConstants.TYPE_SHOTMODE_NORMAL)) {
                this.mScenemode = scenemode;
            }
            CamLog.d(FaceDetector.TAG, "markTakeTime    shotmode: " + shotmode + "   mTakeTime   :   " + this.mTakeTime);
        }
    }

    public String makeFilenamebyTime(String shotmode, int cameramode) {
        if (cameramode != 0) {
            return getNamebyDate() + "_" + getNamebyTime() + getModename(shotmode);
        }
        if (shotmode.equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS)) {
            return sBurstFirstTime + getModename(shotmode);
        }
        if (shotmode.equals(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE)) {
            return this.mTMFirstTime + getModename(shotmode);
        }
        if (shotmode.equals(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE) && this.mTMCount > 0) {
            String filename_final;
            int index = this.mTMsaveCount - this.mTMCount;
            if (index > 0) {
                filename_final = this.mTMFirstTime + getModename(shotmode) + "(" + Integer.toString(index) + ")";
            } else {
                filename_final = this.mTMFirstTime + getModename(shotmode);
            }
            this.mTMCount--;
            return filename_final;
        } else if (this.mTakeTime.length() > 0) {
            return this.mTakeTime + getModename(shotmode);
        } else {
            return getNamebyDate() + "_" + getNamebyTime() + getModename(shotmode);
        }
    }

    private String getModename(String shotmode) {
        if (shotmode.equals(CameraConstants.TYPE_SHOTMODE_PANORAMA) || shotmode.equals(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
            return "_Pano";
        }
        if (shotmode.equals(CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            return "_VRpano";
        }
        if (shotmode.equals(CameraConstants.TYPE_SHOTMODE_HDR)) {
            return "_HDR";
        }
        if (shotmode.equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS) && FunctionProperties.isSupportBurstShot()) {
            this.mBurstCount += STATUS_READY;
            String sCount = Integer.toString(this.mBurstCount);
            if (sCount.length() == STATUS_READY) {
                sCount = "0" + sCount;
            }
            return "_Burst" + sCount;
        } else if (!shotmode.equals(CameraConstants.TYPE_SHOTMODE_NORMAL) || !this.mScenemode.equals(Setting.HELP_NIGHT)) {
            return "";
        } else {
            String sMode = "_Night";
            this.mScenemode = "";
            return sMode;
        }
    }

    private String getNamebyDate() {
        Time time = new Time();
        time.setToNow();
        String sYMD = time.toString().substring(STATUS_NOT_READY, BURSTID_LENGTH_DCM);
        CamLog.i(FaceDetector.TAG, "getNamebyDate  sYMD: " + sYMD);
        return sYMD;
    }

    private String getNamebyTime() {
        Time time = new Time();
        time.setToNow();
        String sHMS = time.toString().substring(9, BURSTID_LENGTH_NEWNAMING);
        CamLog.i(FaceDetector.TAG, "getNamebyTime  sHMS: " + sHMS);
        return sHMS;
    }

    private void processBurstCount(boolean isBurstFirst) {
        if (this.mBurstCount == 20 || isBurstFirst) {
            int cmpLength;
            this.mBurstCount = STATUS_NOT_READY;
            if (ModelProperties.getCarrierCode() == 4) {
                cmpLength = BURSTID_LENGTH_DCM;
            } else if (ModelProperties.getCarrierCode() == 6) {
                cmpLength = BURSTID_LENGTH_VZW;
            } else {
                cmpLength = BURSTID_LENGTH_NEWNAMING;
            }
            if (sBurstFirstTime.length() <= 0) {
                sBurstFirstTime = this.mTempBurstFirstTime;
            } else if (!sBurstFirstTime.substring(STATUS_NOT_READY, cmpLength).equals(this.mTempBurstFirstTime.substring(STATUS_NOT_READY, cmpLength))) {
                sBurstFirstTime = this.mTempBurstFirstTime;
            } else if (sBurstFirstTime.matches(".*\\(.*")) {
                sBurstFirstTime = this.mTempBurstFirstTime + "(" + Integer.toString(Integer.valueOf(sBurstFirstTime.split("\\(")[STATUS_READY].replace(")", "")).intValue() + STATUS_READY) + ")";
            } else {
                sBurstFirstTime = this.mTempBurstFirstTime + "(1)";
            }
        }
    }
}
