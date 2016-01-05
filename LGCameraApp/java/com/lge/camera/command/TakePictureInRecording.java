package com.lge.camera.command;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.GpsLocation;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.olaworks.library.FaceDetector;

public class TakePictureInRecording extends Command {
    private GpsLocation mGpsLocation;
    private ShutterCallbackInRecording mShutterCallback;

    class JpegPictureCallbackInRecording implements PictureCallback {
        public void onPictureTaken(byte[] jpegData, Camera camera) {
            CamLog.d(FaceDetector.TAG, "#### TIME_CHECK JpegPictureCallbackInRecording()-start");
            if (!TakePictureInRecording.this.mGet.isPausing()) {
                TakePictureInRecording.this.mGet.setImageRotationDegree(TakePictureInRecording.this.mGet.getDeviceDegree());
                if (jpegData == null) {
                    CamLog.e(FaceDetector.TAG, "error!! onPictureTaken (jpegData == null)");
                    return;
                }
                CamLog.i(FaceDetector.TAG, "jpegData size [" + jpegData.length + "]");
                boolean isSuccessSave = TakePictureInRecording.this.mGet.saveImageSavers(jpegData, null, 0, true, false);
                CamLog.d(FaceDetector.TAG, "saveLiveSnapshot Queue count is : " + TakePictureInRecording.this.mGet.getQueueCount());
                if (isSuccessSave) {
                    TakePictureInRecording.this.mGet.doCommandUi(Command.SNAPSHOT_EFFECT);
                    SharedPreferenceUtil.saveLastPicture(TakePictureInRecording.this.mGet.getActivity(), TakePictureInRecording.this.mGet.getSavedImageUri());
                    TakePictureInRecording.this.mGet.setInCaptureProgress(false);
                    CamLog.d(FaceDetector.TAG, "JpegPictureCallbackInRecording()-end");
                    return;
                }
                CamLog.e(FaceDetector.TAG, "savePicture() fail!");
                TakePictureInRecording.this.mGet.toast((int) R.string.error_write_file);
                TakePictureInRecording.this.mGet.setInCaptureProgress(false);
                FileNamer.get().setErrorFeedback(0);
            }
        }
    }

    class ShutterCallbackInRecording implements ShutterCallback {
        public void onShutter() {
            CamLog.d(FaceDetector.TAG, "SHOT ShutterCallback() in LiveSnapShot");
            TakePictureInRecording.this.mGet.playShutterSound();
        }
    }

    public TakePictureInRecording(ControllerFunction function) {
        super(function);
        this.mGpsLocation = null;
        this.mShutterCallback = null;
        this.mGpsLocation = new GpsLocation(false);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void execute() {
        /*
        r11 = this;
        r5 = 0;
        r10 = 0;
        r0 = "CameraApp";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "TakePictureInRecording-start,isPausing()[";
        r1 = r1.append(r2);
        r2 = r11.mGet;
        r2 = r2.isPausing();
        r1 = r1.append(r2);
        r2 = "] getInCaptureProgress()[";
        r1 = r1.append(r2);
        r2 = r11.mGet;
        r2 = r2.getInCaptureProgress();
        r1 = r1.append(r2);
        r2 = "]";
        r1 = r1.append(r2);
        r1 = r1.toString();
        com.lge.camera.util.CamLog.d(r0, r1);
        r0 = r11.mGet;
        r0 = r0.getCameraDevice();
        if (r0 != 0) goto L_0x0046;
    L_0x003e:
        r0 = "CameraApp";
        r1 = "TakePictureInRecording-end return, CamDevice is null";
        com.lge.camera.util.CamLog.d(r0, r1);
    L_0x0045:
        return;
    L_0x0046:
        r0 = r11.mGet;
        r0 = r0.isPausing();
        if (r0 != 0) goto L_0x0056;
    L_0x004e:
        r0 = r11.mGet;
        r0 = r0.getInCaptureProgress();
        if (r0 == 0) goto L_0x0085;
    L_0x0056:
        r0 = "CameraApp";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "TakePictureInRecording-end return ";
        r1 = r1.append(r2);
        r2 = r11.mGet;
        r2 = r2.isPausing();
        r1 = r1.append(r2);
        r2 = " / ";
        r1 = r1.append(r2);
        r2 = r11.mGet;
        r2 = r2.getInCaptureProgress();
        r1 = r1.append(r2);
        r1 = r1.toString();
        com.lge.camera.util.CamLog.d(r0, r1);
        goto L_0x0045;
    L_0x0085:
        r0 = com.lge.camera.util.FileNamer.get();
        r1 = r11.mGet;
        r1 = r1.getApplicationMode();
        r0 = r0.getFileStatus(r1);
        if (r0 == 0) goto L_0x016e;
    L_0x0095:
        r0 = "CameraApp";
        r1 = "File Naming Helper status is NOT READY! so reload";
        com.lge.camera.util.CamLog.w(r0, r1);
        r0 = com.lge.camera.util.FileNamer.get();
        r1 = r11.mGet;
        r1 = r1.getApplicationContext();
        r2 = r11.mGet;
        r2 = r2.getApplicationMode();
        r3 = r11.mGet;
        r3 = r3.getCurrentStorage();
        r4 = r11.mGet;
        r4 = r4.getCurrentStorageDirectory();
        r0.reload(r1, r2, r3, r4, r5);
        r0 = r11.mGet;
        r1 = 1;
        r0.setInCaptureProgress(r1);
        r0 = r11.mGet;
        r8 = r0.getParameters();
        r0 = r11.mGpsLocation;
        r1 = r11.mGet;
        r1 = r1.getRecordLocation();
        r2 = r11.mGet;
        r2 = r2.getCurrentLocation();
        r6 = r0.setGPSlocation(r8, r1, r2);
        r0 = java.lang.System.currentTimeMillis();	 Catch:{ NoSuchMethodError -> 0x0177 }
        r9 = com.lge.camera.util.Common.getCurrentDateTime(r0);	 Catch:{ NoSuchMethodError -> 0x0177 }
        r0 = "exif-datetime";
        r8.set(r0, r9);	 Catch:{ NoSuchMethodError -> 0x0177 }
        r0 = com.lge.camera.util.FileNamer.get();	 Catch:{ NoSuchMethodError -> 0x0177 }
        r1 = r11.mGet;	 Catch:{ NoSuchMethodError -> 0x0177 }
        r2 = "key_camera_shot_mode";
        r1 = r1.getSettingValue(r2);	 Catch:{ NoSuchMethodError -> 0x0177 }
        r2 = r11.mGet;	 Catch:{ NoSuchMethodError -> 0x0177 }
        r3 = "key_scene_mode";
        r2 = r2.getSettingValue(r3);	 Catch:{ NoSuchMethodError -> 0x0177 }
        r0.markTakeTime(r1, r2);	 Catch:{ NoSuchMethodError -> 0x0177 }
    L_0x00fd:
        r0 = r11.mGet;
        r1 = r11.mGet;
        r1 = r1.getDeviceDegree();
        r0.setParameteredRotation(r1);
        r0 = com.lge.camera.properties.ProjectVariables.isDisableCheckModifyParameters();
        if (r0 == 0) goto L_0x0117;
    L_0x010e:
        r0 = r11.mGet;
        r0 = r0.getImageHandler();
        r0.resetRotation();
    L_0x0117:
        r0 = r11.mGet;
        r0 = r0.getImageHandler();
        r1 = r11.mGet;
        r1 = r1.getParameteredRotation();
        r0 = r0.setRotation(r8, r1);
        r6 = r6 | r0;
        if (r6 != 0) goto L_0x0130;
    L_0x012a:
        r0 = com.lge.camera.properties.ProjectVariables.isDisableCheckModifyParameters();	 Catch:{ RuntimeException -> 0x018a }
        if (r0 == 0) goto L_0x0135;
    L_0x0130:
        r0 = r11.mGet;	 Catch:{ RuntimeException -> 0x018a }
        r0.setParameters(r8);	 Catch:{ RuntimeException -> 0x018a }
    L_0x0135:
        r8 = 0;
    L_0x0136:
        r0 = com.lge.camera.properties.FunctionProperties.useShutterSoundInLiveShot();
        if (r0 == 0) goto L_0x0193;
    L_0x013c:
        r0 = r11.mGet;
        r0 = r0.getVideoState();
        r1 = 4;
        if (r0 != r1) goto L_0x0193;
    L_0x0145:
        r0 = new com.lge.camera.command.TakePictureInRecording$ShutterCallbackInRecording;
        r0.<init>();
    L_0x014a:
        r11.mShutterCallback = r0;
        r0 = "CameraApp";
        r1 = "#### getCameraDevice().takePicture()";
        com.lge.camera.util.CamLog.d(r0, r1);
        r0 = r11.mGet;	 Catch:{ RuntimeException -> 0x0195 }
        r0 = r0.getCameraDevice();	 Catch:{ RuntimeException -> 0x0195 }
        r1 = r11.mShutterCallback;	 Catch:{ RuntimeException -> 0x0195 }
        r2 = 0;
        r3 = 0;
        r4 = new com.lge.camera.command.TakePictureInRecording$JpegPictureCallbackInRecording;	 Catch:{ RuntimeException -> 0x0195 }
        r4.<init>();	 Catch:{ RuntimeException -> 0x0195 }
        r0.takePicture(r1, r2, r3, r4);	 Catch:{ RuntimeException -> 0x0195 }
    L_0x0165:
        r0 = "CameraApp";
        r1 = "TakePictureInRecording-end";
        com.lge.camera.util.CamLog.d(r0, r1);
        goto L_0x0045;
    L_0x016e:
        r0 = "CameraApp";
        r1 = "file naming helper is null!";
        com.lge.camera.util.CamLog.w(r0, r1);
        goto L_0x0045;
    L_0x0177:
        r7 = move-exception;
        r7.printStackTrace();
        r0 = "CameraApp";
        r1 = "setExifDateTime() NOT SUPPORTED!";
        r2 = new java.lang.Object[r5];
        r1 = java.lang.String.format(r1, r2);
        com.lge.camera.util.CamLog.e(r0, r1);
        goto L_0x00fd;
    L_0x018a:
        r7 = move-exception;
        r0 = "CameraApp";
        r1 = "setParameters failed";
        com.lge.camera.util.CamLog.e(r0, r1, r7);
        goto L_0x0136;
    L_0x0193:
        r0 = r10;
        goto L_0x014a;
    L_0x0195:
        r7 = move-exception;
        r0 = "CameraApp";
        r1 = "TakePictureInRecording failed";
        com.lge.camera.util.CamLog.e(r0, r1, r7);
        goto L_0x0165;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.command.TakePictureInRecording.execute():void");
    }
}
