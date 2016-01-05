package com.lge.camera.command;

import android.hardware.Camera.Parameters;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.GpsLocation;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class TakePicture extends Command {
    private GpsLocation mGpsLocation;

    public TakePicture(ControllerFunction function) {
        super(function);
        this.mGpsLocation = null;
        this.mGpsLocation = new GpsLocation(false);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void execute() {
        /*
        r9 = this;
        r8 = 1;
        r4 = "CameraApp";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "takePicture()-start,isPausing()[";
        r5 = r5.append(r6);
        r6 = r9.mGet;
        r6 = r6.isPausing();
        r5 = r5.append(r6);
        r6 = "] getInCaptureProgress()[";
        r5 = r5.append(r6);
        r6 = r9.mGet;
        r6 = r6.getInCaptureProgress();
        r5 = r5.append(r6);
        r6 = "]";
        r5 = r5.append(r6);
        r5 = r5.toString();
        com.lge.camera.util.CamLog.d(r4, r5);
        r4 = r9.checkTakePicture();
        if (r4 != 0) goto L_0x003c;
    L_0x003b:
        return;
    L_0x003c:
        r4 = r9.mGet;
        r4.setInCaptureProgress(r8);
        r4 = r9.mGet;
        r5 = "com.lge.camera.command.ReleaseTouchFocus";
        r4.removeScheduledCommand(r5);
        r4 = r9.mGet;
        r4.hideGestureGuide();
        r4 = r9.mGet;
        r2 = r4.getParameters();
        r4 = r9.mGpsLocation;
        r5 = r9.mGet;
        r5 = r5.getRecordLocation();
        r6 = r9.mGet;
        r6 = r6.getCurrentLocation();
        r0 = r4.setGPSlocation(r2, r5, r6);
        r4 = java.lang.System.currentTimeMillis();	 Catch:{ NoSuchMethodError -> 0x017c }
        r3 = com.lge.camera.util.Common.getCurrentDateTime(r4);	 Catch:{ NoSuchMethodError -> 0x017c }
        r4 = "exif-datetime";
        r2.set(r4, r3);	 Catch:{ NoSuchMethodError -> 0x017c }
        r4 = com.lge.camera.util.FileNamer.get();	 Catch:{ NoSuchMethodError -> 0x017c }
        r5 = r9.mGet;	 Catch:{ NoSuchMethodError -> 0x017c }
        r6 = "key_camera_shot_mode";
        r5 = r5.getSettingValue(r6);	 Catch:{ NoSuchMethodError -> 0x017c }
        r6 = r9.mGet;	 Catch:{ NoSuchMethodError -> 0x017c }
        r7 = "key_scene_mode";
        r6 = r6.getSettingValue(r7);	 Catch:{ NoSuchMethodError -> 0x017c }
        r4.markTakeTime(r5, r6);	 Catch:{ NoSuchMethodError -> 0x017c }
    L_0x0089:
        r4 = r9.mGet;
        r5 = r9.mGet;
        r5 = r5.getDeviceDegree();
        r4.setParameteredRotation(r5);
        r4 = com.lge.camera.properties.ProjectVariables.isDisableCheckModifyParameters();
        if (r4 == 0) goto L_0x00a3;
    L_0x009a:
        r4 = r9.mGet;
        r4 = r4.getImageHandler();
        r4.resetRotation();
    L_0x00a3:
        r4 = r9.mGet;
        r4 = r4.getImageHandler();
        r5 = r9.mGet;
        r5 = r5.getParameteredRotation();
        r4 = r4.setRotation(r2, r5);
        r0 = r0 | r4;
        r4 = r9.mGet;
        r4 = r4.getApplicationMode();
        r5 = r9.mGet;
        r5 = r5.getCameraId();
        r4 = com.lge.camera.properties.FunctionProperties.isCafSupported(r4, r5);
        if (r4 == 0) goto L_0x0119;
    L_0x00c6:
        r4 = r9.mGet;
        r5 = "key_camera_shot_mode";
        r6 = "shotmode_panorama";
        r4 = r4.checkSettingValue(r5, r6);
        if (r4 != 0) goto L_0x0102;
    L_0x00d2:
        r4 = r9.mGet;
        r5 = "key_camera_shot_mode";
        r6 = "shotmode_plane_panorama";
        r4 = r4.checkSettingValue(r5, r6);
        if (r4 != 0) goto L_0x0102;
    L_0x00de:
        r4 = r9.mGet;
        r5 = "key_camera_shot_mode";
        r6 = "shotmode_free_panorama";
        r4 = r4.checkSettingValue(r5, r6);
        if (r4 != 0) goto L_0x0102;
    L_0x00ea:
        r4 = r9.mGet;
        r5 = "key_camera_shot_mode";
        r6 = "shotmode_full_continuous";
        r4 = r4.checkSettingValue(r5, r6);
        if (r4 != 0) goto L_0x0102;
    L_0x00f6:
        r4 = r9.mGet;
        r5 = "key_camera_shot_mode";
        r6 = "shotmode_clear_shot";
        r4 = r4.checkSettingValue(r5, r6);
        if (r4 == 0) goto L_0x0119;
    L_0x0102:
        r4 = com.lge.camera.properties.ModelProperties.isRenesasISP();
        if (r4 == 0) goto L_0x0190;
    L_0x0108:
        r4 = "CameraApp";
        r5 = "### cancelAutoFocus";
        com.lge.camera.util.CamLog.d(r4, r5);
        r4 = r9.mGet;
        r4 = r4.getCameraDevice();
        r4.cancelAutoFocus();
    L_0x0118:
        r0 = 1;
    L_0x0119:
        r4 = com.lge.camera.properties.ModelProperties.isRenesasISP();
        if (r4 != 0) goto L_0x019e;
    L_0x011f:
        r9.setNightModeAndHDRMode(r2);
    L_0x0122:
        if (r0 != 0) goto L_0x012a;
    L_0x0124:
        r4 = com.lge.camera.properties.ProjectVariables.isDisableCheckModifyParameters();	 Catch:{ RuntimeException -> 0x01c1 }
        if (r4 == 0) goto L_0x012f;
    L_0x012a:
        r4 = r9.mGet;	 Catch:{ RuntimeException -> 0x01c1 }
        r4.setParameters(r2);	 Catch:{ RuntimeException -> 0x01c1 }
    L_0x012f:
        r2 = 0;
    L_0x0130:
        r4 = com.lge.camera.util.AudioUtil.isWiredHeadsetOn();
        if (r4 != 0) goto L_0x016b;
    L_0x0136:
        r4 = com.lge.camera.util.AudioUtil.isBluetoothA2dpOn();
        if (r4 != 0) goto L_0x016b;
    L_0x013c:
        r4 = com.lge.camera.properties.ShutterSoundProperties.isSupportShutterSoundOff();
        if (r4 == 0) goto L_0x0152;
    L_0x0142:
        r4 = r9.mGet;
        r5 = "key_camera_shutter_sound";
        r4 = r4.getSettingValue(r5);
        r5 = "off";
        r4 = r4.equals(r5);
        if (r4 != 0) goto L_0x016b;
    L_0x0152:
        r4 = r9.mGet;
        r4 = r4.isCameraKeyLongPressed();
        if (r4 != 0) goto L_0x016b;
    L_0x015a:
        r4 = r9.mGet;
        r4 = r4.isShutterButtonLongKey();
        if (r4 != 0) goto L_0x016b;
    L_0x0162:
        r4 = r9.mGet;
        r4 = r4.getApplicationContext();
        com.lge.camera.util.AudioUtil.setAudioFocus(r4, r8);
    L_0x016b:
        r4 = r9.mGet;
        r4 = r4.doTakePictureCommand();
        if (r4 != 0) goto L_0x01ce;
    L_0x0173:
        r4 = "CameraApp";
        r5 = " error!!!! takepicture fail!!!!";
        com.lge.camera.util.CamLog.e(r4, r5);
        goto L_0x003b;
    L_0x017c:
        r1 = move-exception;
        r1.printStackTrace();
        r4 = "CameraApp";
        r5 = "setExifDateTime() NOT SUPPORTED!";
        r6 = 0;
        r6 = new java.lang.Object[r6];
        r5 = java.lang.String.format(r5, r6);
        com.lge.camera.util.CamLog.e(r4, r5);
        goto L_0x0089;
    L_0x0190:
        r4 = "auto";
        r2.setFocusMode(r4);
        r4 = "CameraApp";
        r5 = "### setFocusMode-auto";
        com.lge.camera.util.CamLog.d(r4, r5);
        goto L_0x0118;
    L_0x019e:
        r4 = "night";
        r5 = r9.mGet;
        r6 = "key_scene_mode";
        r5 = r5.getSettingValue(r6);
        r4 = r4.equals(r5);
        if (r4 != 0) goto L_0x01ba;
    L_0x01ae:
        r4 = r9.mGet;
        r5 = "key_camera_shot_mode";
        r6 = "shotmode_hdr";
        r4 = r4.checkSettingValue(r5, r6);
        if (r4 == 0) goto L_0x0122;
    L_0x01ba:
        r4 = r9.mGet;
        r4.setNeedProgressDuringCapture(r8);
        goto L_0x0122;
    L_0x01c1:
        r1 = move-exception;
        r4 = "CameraApp";
        r5 = "setParameters failed";
        com.lge.camera.util.CamLog.e(r4, r5);
        r1.printStackTrace();
        goto L_0x0130;
    L_0x01ce:
        r4 = "CameraApp";
        r5 = "takePicture-end";
        com.lge.camera.util.CamLog.d(r4, r5);
        goto L_0x003b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.command.TakePicture.execute():void");
    }

    private boolean checkTakePicture() {
        if (this.mGet.getCameraDevice() == null) {
            CamLog.d(FaceDetector.TAG, "takePicture-end return, CamDevice is null");
            return false;
        } else if (this.mGet.isPausing() || this.mGet.getInCaptureProgress() || this.mGet.getAvailablePictureCount() < 1) {
            CamLog.d(FaceDetector.TAG, "takePicture-end return " + this.mGet.isPausing() + " / " + this.mGet.getInCaptureProgress());
            if (this.mGet.getAvailablePictureCount() >= 1 || !Common.isQuickWindowCameraMode()) {
                return false;
            }
            this.mGet.showStorageHint(2);
            return false;
        } else {
            CamLog.d(FaceDetector.TAG, "getPictureRemaining()[" + this.mGet.getAvailablePictureCount() + "] ");
            if (FileNamer.get().getFileStatus(this.mGet.getApplicationMode())) {
                CamLog.w(FaceDetector.TAG, "File Naming Helper status is NOT READY! so reload");
                FileNamer.get().reload(this.mGet.getApplicationContext(), this.mGet.getApplicationMode(), this.mGet.getCurrentStorage(), this.mGet.getCurrentStorageDirectory(), false);
                return true;
            }
            CamLog.w(FaceDetector.TAG, "file naming helper is null!");
            this.mGet.quickFunctionControllerRefresh(true);
            this.mGet.enableCommand(true);
            return false;
        }
    }

    private void setNightModeAndHDRMode(Parameters parameters) {
        String superZoomStatus;
        if (FunctionProperties.isSupportSmartMode()) {
            if (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE))) {
                if (!CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_FLASH))) {
                    switch (this.mGet.getCurrentIAMode()) {
                        case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                        case LGKeyRec.EVENT_STARTED /*3*/:
                            parameters.setSceneMode(Setting.HELP_NIGHT);
                            parameters.set(CameraConstants.HDR_MODE, "0");
                            break;
                        case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                        case Ola_ShotParam.ImageEffect_Vivid /*14*/:
                            parameters.set(CameraConstants.HDR_MODE, "1");
                            break;
                        default:
                            parameters.set(CameraConstants.HDR_MODE, "0");
                            parameters.setSceneMode(LGT_Limit.ISP_AUTOMODE_AUTO);
                            break;
                    }
                }
                parameters.set(CameraConstants.HDR_MODE, "0");
                parameters.setSceneMode(LGT_Limit.ISP_AUTOMODE_AUTO);
            } else if (!CameraConstants.SMART_MODE_OFF.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE)) || CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_FLASH)) || Setting.HELP_SPORTS.equals(this.mGet.getSettingValue(Setting.KEY_SCENE_MODE)) || !this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_NORMAL)) {
                if (this.mGet.getCameraId() == 1 && this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_NORMAL) && Common.isLowLuminance(this.mGet.getParameters(), true)) {
                    CamLog.d(FaceDetector.TAG, "Auto night mode.");
                    parameters.setSceneMode(Setting.HELP_NIGHT);
                }
            } else if (FunctionProperties.isSupportMorphoNightShot()) {
                superZoomStatus = this.mGet.getParameters().get(CameraConstants.PARAMETER_SUPERZOOM);
                if (Common.isLowLuminance(this.mGet.getParameters(), true) && ((superZoomStatus != null && CameraConstants.SMART_MODE_OFF.equals(superZoomStatus)) || superZoomStatus == null)) {
                    CamLog.d(FaceDetector.TAG, "Auto night mode.");
                    parameters.setSceneMode(Setting.HELP_NIGHT);
                }
            }
        } else if (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_FLASH)) || Setting.HELP_SPORTS.equals(this.mGet.getSettingValue(Setting.KEY_SCENE_MODE)) || !this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_NORMAL)) {
            if (this.mGet.getCameraId() == 1 && ((this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_NORMAL) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_PREFERENCE_NOT_FOUND)) && FunctionProperties.isSupportMorphoNightShot() && Common.isLowLuminance(this.mGet.getParameters(), true))) {
                CamLog.d(FaceDetector.TAG, "Auto night mode.");
                parameters.setSceneMode(Setting.HELP_NIGHT);
            }
        } else if (FunctionProperties.isSupportMorphoNightShot()) {
            superZoomStatus = this.mGet.getParameters().get(CameraConstants.PARAMETER_SUPERZOOM);
            if (Common.isLowLuminance(this.mGet.getParameters(), true) && ((superZoomStatus != null && CameraConstants.SMART_MODE_OFF.equals(superZoomStatus)) || superZoomStatus == null)) {
                CamLog.d(FaceDetector.TAG, "Auto night mode.");
                parameters.setSceneMode(Setting.HELP_NIGHT);
            }
        }
        if (Setting.HELP_NIGHT.equals(this.mGet.getSettingValue(Setting.KEY_SCENE_MODE)) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_HDR)) {
            this.mGet.setNeedProgressDuringCapture(true);
        }
    }
}
