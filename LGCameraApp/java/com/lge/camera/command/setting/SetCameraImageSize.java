package com.lge.camera.command.setting;

import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.SystemProperties;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;

public class SetCameraImageSize extends SettingCommand {
    public SetCameraImageSize(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public boolean checkEnableLightFrame() {
        if (this.mGet.getCameraId() != 1 || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
            return false;
        }
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void execute(com.lge.hardware.LGCamera.LGParameters r22, java.lang.Object r23) {
        /*
        r21 = this;
        r18 = "CameraApp";
        r19 = "SetCameraImageSize-start";
        com.lge.camera.util.CamLog.d(r18, r19);
        r3 = r23;
        r3 = (android.os.Bundle) r3;
        r18 = "doNotRestartPreview";
        r19 = 0;
        r0 = r18;
        r1 = r19;
        r5 = r3.getBoolean(r0, r1);
        r18 = "doChangePrevieMode";
        r19 = 0;
        r0 = r18;
        r1 = r19;
        r4 = r3.getBoolean(r0, r1);
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r16 = r18.getPreviewSizeOnDevice();
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r17 = r18.getPreviewSizeOnScreen();
        r18 = "";
        r0 = r18;
        r1 = r17;
        r18 = r0.equals(r1);
        if (r18 == 0) goto L_0x0072;
    L_0x0043:
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r19 = "key_camera_picturesize";
        r12 = r18.findPreference(r19);
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r19 = "key_camera_picturesize";
        r20 = r12.getDefaultValue();
        r18.setSetting(r19, r20);
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r16 = r18.getPreviewSizeOnDevice();
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r17 = r18.getPreviewSizeOnScreen();
    L_0x0072:
        r18 = r22.getParameters();
        r9 = r18.getPreviewSize();
        r18 = r22.getParameters();
        r8 = r18.getPictureSize();
        if (r16 == 0) goto L_0x008a;
    L_0x0084:
        if (r17 == 0) goto L_0x008a;
    L_0x0086:
        if (r9 == 0) goto L_0x008a;
    L_0x0088:
        if (r8 != 0) goto L_0x0138;
    L_0x008a:
        r19 = "CameraApp";
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r20 = "error! sizeOnDeviceString is NULL: ";
        r0 = r18;
        r1 = r20;
        r20 = r0.append(r1);
        if (r16 != 0) goto L_0x012b;
    L_0x009d:
        r18 = 1;
    L_0x009f:
        r0 = r20;
        r1 = r18;
        r18 = r0.append(r1);
        r18 = r18.toString();
        r0 = r19;
        r1 = r18;
        com.lge.camera.util.CamLog.d(r0, r1);
        r19 = "CameraApp";
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r20 = "error! sizeOnScreenString is Null: ";
        r0 = r18;
        r1 = r20;
        r20 = r0.append(r1);
        if (r17 != 0) goto L_0x012f;
    L_0x00c5:
        r18 = 1;
    L_0x00c7:
        r0 = r20;
        r1 = r18;
        r18 = r0.append(r1);
        r18 = r18.toString();
        r0 = r19;
        r1 = r18;
        com.lge.camera.util.CamLog.d(r0, r1);
        r19 = "CameraApp";
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r20 = "error! oldPreviewSizeOnDevice is Null: ";
        r0 = r18;
        r1 = r20;
        r20 = r0.append(r1);
        if (r9 != 0) goto L_0x0132;
    L_0x00ed:
        r18 = 1;
    L_0x00ef:
        r0 = r20;
        r1 = r18;
        r18 = r0.append(r1);
        r18 = r18.toString();
        r0 = r19;
        r1 = r18;
        com.lge.camera.util.CamLog.d(r0, r1);
        r19 = "CameraApp";
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r20 = "error! oldPictureSize is Null: ";
        r0 = r18;
        r1 = r20;
        r20 = r0.append(r1);
        if (r8 != 0) goto L_0x0135;
    L_0x0115:
        r18 = 1;
    L_0x0117:
        r0 = r20;
        r1 = r18;
        r18 = r0.append(r1);
        r18 = r18.toString();
        r0 = r19;
        r1 = r18;
        com.lge.camera.util.CamLog.d(r0, r1);
    L_0x012a:
        return;
    L_0x012b:
        r18 = 0;
        goto L_0x009f;
    L_0x012f:
        r18 = 0;
        goto L_0x00c7;
    L_0x0132:
        r18 = 0;
        goto L_0x00ef;
    L_0x0135:
        r18 = 0;
        goto L_0x0117;
    L_0x0138:
        r14 = com.lge.camera.util.Util.SizeString2WidthHeight(r16);
        r15 = com.lge.camera.util.Util.SizeString2WidthHeight(r17);
        r18 = "on";
        r0 = r21;
        r0 = r0.mGet;
        r19 = r0;
        r20 = "key_light";
        r19 = r19.getSettingValue(r20);
        r18 = r18.equals(r19);
        if (r18 == 0) goto L_0x0194;
    L_0x0154:
        r18 = r21.checkEnableLightFrame();
        if (r18 == 0) goto L_0x0194;
    L_0x015a:
        r18 = 0;
        r19 = r15[r18];
        r19 = r19 / 2;
        r15[r18] = r19;
        r18 = 1;
        r19 = r15[r18];
        r19 = r19 / 2;
        r15[r18] = r19;
        r18 = "CameraApp";
        r19 = new java.lang.StringBuilder;
        r19.<init>();
        r20 = "MINA previewSize x : ";
        r19 = r19.append(r20);
        r20 = 0;
        r20 = r15[r20];
        r19 = r19.append(r20);
        r20 = "previewSize y :";
        r19 = r19.append(r20);
        r20 = 1;
        r20 = r15[r20];
        r19 = r19.append(r20);
        r19 = r19.toString();
        com.lge.camera.util.CamLog.d(r18, r19);
    L_0x0194:
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r19 = "key_camera_picturesize";
        r12 = r18.findPreference(r19);
        r18 = r12.getValue();
        r11 = com.lge.camera.util.Util.SizeString2WidthHeight(r18);
        r18 = r22.getParameters();
        r0 = r21;
        r1 = r18;
        r0.setParameterPictureSize(r1, r14, r11);
        r18 = r22.getParameters();
        r19 = 0;
        r19 = r14[r19];
        r20 = 1;
        r20 = r14[r20];
        r18.setPreviewSize(r19, r20);
        r18 = "CameraApp";
        r19 = new java.lang.StringBuilder;
        r19.<init>();
        r20 = "setPreviewSize ";
        r19 = r19.append(r20);
        r20 = 0;
        r20 = r14[r20];
        r19 = r19.append(r20);
        r20 = "x";
        r19 = r19.append(r20);
        r20 = 1;
        r20 = r14[r20];
        r19 = r19.append(r20);
        r19 = r19.toString();
        com.lge.camera.util.CamLog.i(r18, r19);
        r18 = r22.getParameters();
        r0 = r21;
        r1 = r18;
        r0.setPreviewFpsRange(r1);
        r18 = r22.getParameters();
        r19 = 0;
        r18.setRecordingHint(r19);
        r18 = "face_tracking";
        r0 = r21;
        r0 = r0.mGet;
        r19 = r0;
        r20 = "key_focus";
        r19 = r19.getSettingValue(r20);
        r18 = r18.equals(r19);
        if (r18 == 0) goto L_0x021d;
    L_0x0214:
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r18.initFaceDetectInfo();
    L_0x021d:
        r18 = 2;
        r0 = r18;
        r10 = new int[r0];
        r7 = 0;
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r18 = r18.isPreviewRendered();
        if (r18 == 0) goto L_0x041c;
    L_0x0230:
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r19 = 2131558775; // 0x7f0d0177 float:1.8742875E38 double:1.053129963E-314;
        r13 = r18.findViewById(r19);
        r13 = (com.lge.camera.components.OpenGLSurfaceView) r13;
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r18 = r18.isConfigureLandscape();
        if (r18 == 0) goto L_0x03ca;
    L_0x024b:
        r18 = 0;
        r19 = r13.getWidth();
        r10[r18] = r19;
        r18 = 1;
        r19 = r13.getHeight();
        r10[r18] = r19;
    L_0x025b:
        r6 = 1;
        r18 = 0;
        r18 = r10[r18];
        if (r18 <= 0) goto L_0x0284;
    L_0x0262:
        r18 = 1;
        r18 = r10[r18];
        if (r18 <= 0) goto L_0x0284;
    L_0x0268:
        r18 = 0;
        r18 = r10[r18];
        r19 = 0;
        r19 = r15[r19];
        r0 = r18;
        r1 = r19;
        if (r0 != r1) goto L_0x0284;
    L_0x0276:
        r18 = 0;
        r18 = r10[r18];
        r19 = 0;
        r19 = r15[r19];
        r0 = r18;
        r1 = r19;
        if (r0 == r1) goto L_0x03dc;
    L_0x0284:
        r6 = 1;
    L_0x0285:
        if (r4 == 0) goto L_0x0288;
    L_0x0287:
        r6 = 1;
    L_0x0288:
        if (r6 == 0) goto L_0x02a8;
    L_0x028a:
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r18 = r18.isPreviewOnGoing();
        if (r18 == 0) goto L_0x0419;
    L_0x0296:
        r7 = 0;
    L_0x0297:
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r19 = 0;
        r19 = r15[r19];
        r20 = 1;
        r20 = r15[r20];
        r18.changePreviewModeOnUiThread(r19, r20);
    L_0x02a8:
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r18 = r18.isPreviewing();
        if (r18 == 0) goto L_0x02b5;
    L_0x02b4:
        r7 = 1;
    L_0x02b5:
        if (r5 == 0) goto L_0x02b8;
    L_0x02b7:
        r7 = 0;
    L_0x02b8:
        r18 = "CameraApp";
        r19 = new java.lang.StringBuilder;
        r19.<init>();
        r20 = "PreviewOnScreen Old : ";
        r19 = r19.append(r20);
        r20 = 0;
        r20 = r10[r20];
        r19 = r19.append(r20);
        r20 = "x";
        r19 = r19.append(r20);
        r20 = 1;
        r20 = r10[r20];
        r19 = r19.append(r20);
        r20 = " -> New : ";
        r19 = r19.append(r20);
        r20 = 0;
        r20 = r15[r20];
        r19 = r19.append(r20);
        r20 = "x";
        r19 = r19.append(r20);
        r20 = 1;
        r20 = r15[r20];
        r19 = r19.append(r20);
        r19 = r19.toString();
        com.lge.camera.util.CamLog.i(r18, r19);
        r18 = "CameraApp";
        r19 = new java.lang.StringBuilder;
        r19.<init>();
        r20 = "PreviewOnDevice Old : ";
        r19 = r19.append(r20);
        r0 = r9.width;
        r20 = r0;
        r19 = r19.append(r20);
        r20 = "x";
        r19 = r19.append(r20);
        r0 = r9.height;
        r20 = r0;
        r19 = r19.append(r20);
        r20 = " -> New : ";
        r19 = r19.append(r20);
        r20 = 0;
        r20 = r14[r20];
        r19 = r19.append(r20);
        r20 = "x";
        r19 = r19.append(r20);
        r20 = 1;
        r20 = r14[r20];
        r19 = r19.append(r20);
        r19 = r19.toString();
        com.lge.camera.util.CamLog.i(r18, r19);
        if (r7 == 0) goto L_0x03c1;
    L_0x0346:
        r18 = 0;
        r18 = r10[r18];
        r19 = 0;
        r19 = r15[r19];
        r0 = r18;
        r1 = r19;
        if (r0 != r1) goto L_0x03c1;
    L_0x0354:
        r18 = 1;
        r18 = r10[r18];
        r19 = 1;
        r19 = r15[r19];
        r0 = r18;
        r1 = r19;
        if (r0 != r1) goto L_0x03c1;
    L_0x0362:
        r0 = r9.width;
        r18 = r0;
        r19 = 0;
        r19 = r14[r19];
        r0 = r18;
        r1 = r19;
        if (r0 != r1) goto L_0x038c;
    L_0x0370:
        r0 = r8.width;
        r18 = r0;
        r19 = 0;
        r19 = r11[r19];
        r0 = r18;
        r1 = r19;
        if (r0 != r1) goto L_0x038c;
    L_0x037e:
        r0 = r8.height;
        r18 = r0;
        r19 = 1;
        r19 = r11[r19];
        r0 = r18;
        r1 = r19;
        if (r0 == r1) goto L_0x03c1;
    L_0x038c:
        r18 = "CameraApp";
        r19 = "SetCameraImageSize:Preview size on device changed but view layout is same.";
        com.lge.camera.util.CamLog.d(r18, r19);
        r18 = "CameraApp";
        r19 = new java.lang.StringBuilder;
        r19.<init>();
        r20 = "SetCameraImageSize:Force restart preview-[doNotRestartPreview value] : ";
        r19 = r19.append(r20);
        r0 = r19;
        r19 = r0.append(r5);
        r19 = r19.toString();
        com.lge.camera.util.CamLog.d(r18, r19);
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r19 = new com.lge.camera.command.setting.SetCameraImageSize$1;
        r0 = r19;
        r1 = r21;
        r2 = r22;
        r0.<init>(r2);
        r18.postOnUiThread(r19);
    L_0x03c1:
        r18 = "CameraApp";
        r19 = "SetCameraImageSize-end";
        com.lge.camera.util.CamLog.d(r18, r19);
        goto L_0x012a;
    L_0x03ca:
        r18 = 0;
        r19 = r13.getHeight();
        r10[r18] = r19;
        r18 = 1;
        r19 = r13.getWidth();
        r10[r18] = r19;
        goto L_0x025b;
    L_0x03dc:
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r18 = r18.getInCaptureProgress();
        if (r18 != 0) goto L_0x0416;
    L_0x03e8:
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r18 = r18.isPreviewing();
        if (r18 != 0) goto L_0x0416;
    L_0x03f4:
        r18 = com.lge.camera.properties.ModelProperties.isHVGAmodel();
        if (r18 == 0) goto L_0x0285;
    L_0x03fa:
        r18 = 0;
        r18 = r10[r18];
        r19 = 0;
        r19 = r14[r19];
        r0 = r18;
        r1 = r19;
        if (r0 > r1) goto L_0x0285;
    L_0x0408:
        r18 = 1;
        r18 = r10[r18];
        r19 = 1;
        r19 = r14[r19];
        r0 = r18;
        r1 = r19;
        if (r0 > r1) goto L_0x0285;
    L_0x0416:
        r6 = 0;
        goto L_0x0285;
    L_0x0419:
        r7 = 1;
        goto L_0x0297;
    L_0x041c:
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r19 = 2131558774; // 0x7f0d0176 float:1.8742873E38 double:1.0531299623E-314;
        r13 = r18.findViewById(r19);
        r13 = (com.lge.camera.components.CameraPreview) r13;
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r18 = r18.isConfigureLandscape();
        if (r18 == 0) goto L_0x0476;
    L_0x0437:
        r18 = 0;
        r19 = r13.getWidth();
        r10[r18] = r19;
        r18 = 1;
        r19 = r13.getHeight();
        r10[r18] = r19;
    L_0x0447:
        r18 = 0;
        r18 = r14[r18];
        r19 = 1;
        r19 = r14[r19];
        r0 = r18;
        r1 = r19;
        r13.setSrcImageSize(r0, r1);
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r19 = 0;
        r19 = r15[r19];
        r20 = 1;
        r20 = r15[r20];
        r18.changePreviewModeOnUiThread(r19, r20);
        r0 = r21;
        r0 = r0.mGet;
        r18 = r0;
        r18 = r18.isPreviewing();
        if (r18 == 0) goto L_0x02b5;
    L_0x0473:
        r7 = 1;
        goto L_0x02b5;
    L_0x0476:
        r18 = 0;
        r19 = r13.getHeight();
        r10[r18] = r19;
        r18 = 1;
        r19 = r13.getWidth();
        r10[r18] = r19;
        goto L_0x0447;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.command.setting.SetCameraImageSize.execute(com.lge.hardware.LGCamera$LGParameters, java.lang.Object):void");
    }

    private void setParameterPictureSize(Parameters parameters, int[] previewSizeOnDevice, int[] pictureSize) {
        if (!this.mGet.isEffectsCamcorderActive() && !this.mGet.isEffectsCameraActive()) {
            if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
                parameters.setPictureSize(1280, LGT_Limit.IMAGE_SIZE_WALLPAPER_WIDTH);
                CamLog.i(FaceDetector.TAG, "panorama setPictureSize 1280x960");
                return;
            }
            parameters.setPictureSize(pictureSize[0], pictureSize[1]);
            CamLog.i(FaceDetector.TAG, "setPictureSize " + pictureSize[0] + "x" + pictureSize[1]);
        }
    }

    private void setPreviewFpsRange(Parameters parameters) {
        if (MultimediaProperties.isHighFramRateVideoSupported()) {
            parameters.set("video-hfr", CameraConstants.SMART_MODE_OFF);
            parameters.set("preview-format", "yuv420sp");
        }
        if (!this.mGet.isEffectsCamcorderActive() && !this.mGet.isEffectsCameraActive()) {
            if (ModelProperties.isOMAP4Chipset()) {
                if (this.mGet.getCameraId() == 1) {
                    parameters.setPreviewFpsRange(MultimediaProperties.VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX, MultimediaProperties.VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX);
                } else {
                    parameters.setPreviewFpsRange(MultimediaProperties.getCameraFrameRateNormalRangeMin(), MultimediaProperties.getCameraFrameRateNormalRangeMax());
                }
            } else if (this.mGet.getCameraId() == 1) {
                fpsValues = SystemProperties.get("persist.data.front.minfps", MultimediaProperties.getFrontCameraFrameRateNormalRangeMin() + "," + MultimediaProperties.getFrontCameraFrameRateNightModeRangeMin()).split(",");
                if (LGParameters.SCENE_MODE_NIGHT.equals(this.mGet.getSettingValue(Setting.KEY_SCENE_MODE))) {
                    parameters.setPreviewFpsRange(Integer.parseInt(fpsValues[1]), MultimediaProperties.getCameraFrameRateNormalRangeMax());
                } else {
                    parameters.setPreviewFpsRange(Integer.parseInt(fpsValues[0]), MultimediaProperties.getCameraFrameRateNormalRangeMax());
                }
            } else if (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE))) {
                parameters.setPreviewFpsRange(MultimediaProperties.getCameraFrameRateIAModeRangeMin(), MultimediaProperties.getCameraFrameRateNormalRangeMax());
            } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS)) {
                parameters.setPreviewFpsRange(MultimediaProperties.getCameraFrameRateBurstShotModeRangeMin(), MultimediaProperties.getCameraFrameRateNormalRangeMax());
            } else {
                fpsValues = SystemProperties.get("persist.data.rear.minfps", MultimediaProperties.getCameraFrameRateNormalRangeMin() + "," + MultimediaProperties.getCameraFrameRateNightModeRangeMin()).split(",");
                if (LGParameters.SCENE_MODE_NIGHT.equals(this.mGet.getSettingValue(Setting.KEY_SCENE_MODE))) {
                    parameters.setPreviewFpsRange(Integer.parseInt(fpsValues[1]), MultimediaProperties.getCameraFrameRateNormalRangeMax());
                } else {
                    parameters.setPreviewFpsRange(Integer.parseInt(fpsValues[0]), MultimediaProperties.getCameraFrameRateNormalRangeMax());
                }
            }
        }
    }

    protected void onExecuteAlone() {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                SetCameraImageSize.this.mGet.removePostRunnable(this);
                if (SetCameraImageSize.this.mGet.getCameraId() == 0) {
                    if (SetCameraImageSize.this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || SetCameraImageSize.this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) || SetCameraImageSize.this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
                        SetCameraImageSize.this.mGet.hideFocus();
                    } else {
                        SetCameraImageSize.this.mGet.showFocus();
                    }
                }
                SetCameraImageSize.this.mGet.checkStorage(false);
                SetCameraImageSize.this.mGet.updateSizeIndicator();
            }
        });
    }
}
