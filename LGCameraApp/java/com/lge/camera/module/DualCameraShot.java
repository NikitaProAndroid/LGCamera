package com.lge.camera.module;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.net.Uri;
import com.lge.camera.ControllerFunction;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.ExifUtil;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.Util;
import com.lge.filterpacks.DualRecorderFilter;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DualCameraShot extends Module {
    private long[] mDateTaken;
    private String mSaveFileName;
    private Thread threadComplete;

    public DualCameraShot(ControllerFunction function) {
        super(function);
        this.threadComplete = null;
        this.mSaveFileName = null;
        this.mDateTaken = new long[2];
        CamLog.d(FaceDetector.TAG, "Dual Camera Module Create !!");
    }

    public boolean checkCurrentShotMode() {
        return this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA);
    }

    public void stopByUserAction() {
    }

    public boolean takePicture() {
        CamLog.d(FaceDetector.TAG, "Dual Camera Module takePicture....");
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                DualCameraShot.this.mGet.removePostRunnable(this);
                DualCameraShot.this.mGet.setMainButtonDisable();
                DualRecorderFilter.takePreviewFrame();
                DualCameraShot.this.mGet.playShutterSound();
                DualCameraShot.this.mGet.doCommandUi(Command.SNAPSHOT_EFFECT);
                DualCameraShot.this.mGet.clearFocusState();
                DualCameraShot.this.mGet.hideFocus();
            }
        });
        return true;
    }

    public void jpegCallbackOnPictureTaken(final byte[] jpegData, Camera camera) {
        FileNamer.get().markTakeTime(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), this.mGet.getSettingValue(Setting.KEY_SCENE_MODE));
        this.mSaveFileName = FileNamer.get().getFileNewName(this.mGet.getApplicationContext(), this.mGet.getApplicationMode(), this.mGet.getCurrentStorage(), this.mGet.getCurrentStorageDirectory(), true, this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), false);
        this.threadComplete = new Thread(new Runnable() {
            public void run() {
                if (DualCameraShot.this.saveResultImage(jpegData)) {
                    DualCameraShot.this.doComplete();
                }
            }
        }, "complete_Dual_Camera");
        this.threadComplete.start();
    }

    private void doComplete() {
        if (!this.mGet.isPausing() && this.mGet.checkAutoReviewOff(false)) {
            this.mGet.setEnable3ALocks(null, false);
            this.mGet.doCommandUi(Command.ON_DELAY_OFF);
            this.mGet.setInCaptureProgress(false);
            LGParameters lgParams = this.mGet.getLGParam();
            this.mGet.doCommand(Command.CAMERA_FOCUS_MODE, lgParams);
            try {
                lgParams.setParameters(lgParams.getParameters());
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "DualCameraShot: setParameters Exception");
                e.printStackTrace();
            }
        }
    }

    public boolean isRunning() {
        return false;
    }

    private boolean saveResultImage(byte[] jpegData) {
        if (this.mSaveFileName == null) {
            CamLog.w(FaceDetector.TAG, "File name is null! return.");
            return false;
        }
        boolean ret;
        long dateTaken = System.currentTimeMillis();
        String directory = this.mGet.getCurrentStorageDirectory();
        String bounding_file_name_with_extension = this.mSaveFileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
        String output_img_path = directory + bounding_file_name_with_extension;
        int currDegree = this.mGet.getDeviceDegree();
        this.mDateTaken[1] = System.currentTimeMillis();
        CamLog.d(FaceDetector.TAG, "file_name = " + this.mSaveFileName + ", directory = " + directory + ", bounding_file_name_with_extension = " + bounding_file_name_with_extension + ", output_img_path = " + output_img_path + ", mStartOrientation = " + currDegree);
        if (this.mGet.getCameraMode() == 1 && (currDegree == 90 || currDegree == Tag.IMAGE_DESCRIPTION)) {
            ret = saveOutputImage(rotateJpeg(jpegData, MediaProviderUtils.ROTATION_180), directory, bounding_file_name_with_extension, this.mDateTaken, currDegree);
        } else {
            ret = saveOutputImage(jpegData, directory, bounding_file_name_with_extension, this.mDateTaken, currDegree);
        }
        if (ret) {
            addImage(this.mSaveFileName, dateTaken, directory, output_img_path, currDegree);
            CamLog.d(FaceDetector.TAG, "The original dual camera image is saved.");
            this.mGet.setLastThumb(this.mGet.getSavedImageUri(), true);
            this.mGet.updateThumbnailButton();
            this.mSaveFileName = null;
            return true;
        }
        CamLog.d(FaceDetector.TAG, "Cannot save original dual camera image.");
        this.mSaveFileName = null;
        return false;
    }

    private boolean saveOutputImage(byte[] jpegData, String directory, String filename, long[] dateTaken, int orientation) {
        CamLog.d(FaceDetector.TAG, "orientation=" + orientation);
        boolean ret = false;
        if (!this.mGet.isPausing()) {
            ret = saveOutputJpeg(jpegData, directory, filename);
        }
        if (!ret) {
            CamLog.e(FaceDetector.TAG, "DualCameraShot - saveOutputImage error ret:" + ret);
        } else {
            CamLog.d(FaceDetector.TAG, "saveOutputImage=" + ret);
        }
        return ret;
    }

    public static boolean saveOutputJpeg(byte[] jpegData, String directory, String filename) {
        FileNotFoundException ex;
        Throwable th;
        IOException ex2;
        CamLog.i(FaceDetector.TAG, "saveOutputJpeg-start:" + filename);
        OutputStream outputStream = null;
        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            OutputStream outputStream2 = new FileOutputStream(new File(directory, filename));
            try {
                outputStream2.write(jpegData);
                if (outputStream2 != null) {
                    try {
                        outputStream2.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                CamLog.d(FaceDetector.TAG, "saveOutputJpeg-end");
                return true;
            } catch (FileNotFoundException e2) {
                ex = e2;
                outputStream = outputStream2;
                try {
                    CamLog.w(FaceDetector.TAG, ex.toString(), ex);
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                    return false;
                } catch (Throwable th2) {
                    th = th2;
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e32) {
                            e32.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (IOException e4) {
                ex2 = e4;
                outputStream = outputStream2;
                CamLog.w(FaceDetector.TAG, ex2.toString(), ex2);
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e322) {
                        e322.printStackTrace();
                    }
                }
                return false;
            } catch (Throwable th3) {
                th = th3;
                outputStream = outputStream2;
                if (outputStream != null) {
                    outputStream.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e5) {
            ex = e5;
            CamLog.w(FaceDetector.TAG, ex.toString(), ex);
            if (outputStream != null) {
                outputStream.close();
            }
            return false;
        } catch (IOException e6) {
            ex2 = e6;
            CamLog.w(FaceDetector.TAG, ex2.toString(), ex2);
            if (outputStream != null) {
                outputStream.close();
            }
            return false;
        }
    }

    private void addImage(String file_name, long dateTaken, String directory, String output_img_path, int degree) {
        int[] size = Util.SizeString2WidthHeight(this.mGet.getSettingValue(Setting.KEY_CAMERA_PICTURESIZE));
        if (output_img_path != null) {
            ExifUtil.setExif(output_img_path, this.mGet.getParameters().getFlashMode(), -1.0f, this.mGet.getCurrentLocation(), size[0], size[1], null, degree, null);
        }
        Uri ImageUri = this.mGet.getImageHandler(false).addImage(this.mGet.getActivity().getContentResolver(), file_name, dateTaken, this.mGet.getCurrentLocation(), directory, file_name + CameraConstants.TIME_MACHINE_TEMPFILE_EXT, degree, false);
        this.mGet.setSavedFileName(file_name);
        this.mGet.setSavedImageUri(ImageUri);
        Util.broadcastNewPicture(this.mGet.getActivity(), ImageUri);
        Util.requestUpBoxBackupPhoto(this.mGet.getActivity(), output_img_path, this.mGet.getSettingValue(Setting.KEY_UPLUS_BOX).equals(CameraConstants.SMART_MODE_ON));
        this.mGet.getImageListUri().add(ImageUri);
    }

    private byte[] rotateJpeg(byte[] jpegData, int degree) {
        ByteArrayOutputStream jpegRotated = new ByteArrayOutputStream();
        Bitmap bitmap = Util.rotate(Util.makeBitmap(jpegData, false), degree);
        bitmap.compress(CompressFormat.JPEG, 100, jpegRotated);
        bitmap.recycle();
        return jpegRotated.toByteArray();
    }
}
