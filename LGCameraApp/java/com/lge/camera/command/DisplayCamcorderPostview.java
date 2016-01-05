package com.lge.camera.command;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.PostviewAttachActivity;
import com.lge.camera.VideoFile;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;

public class DisplayCamcorderPostview extends Command {
    public DisplayCamcorderPostview(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "DisplayAfterCaptureView-start");
        Intent intent = new Intent();
        if (this.mGet.isAttachIntent()) {
            intent.setClassName(this.mGet.getActivity().getPackageName(), PostviewAttachActivity.class.getName());
            try {
                ArrayList<String> uriStringList = new ArrayList();
                VideoFile video = this.mGet.getVideoFile();
                ArrayList<Uri> uriList = new ArrayList();
                if (!(video == null || video.getUri() == null)) {
                    uriList.add(video.getUri());
                    int uriListSize = uriList.size();
                    if (uriListSize == 0) {
                        this.mGet.doCommand(Command.DISPLAY_PREVIEW);
                        return;
                    }
                    for (int i = 0; i < uriListSize; i++) {
                        uriStringList.add(((Uri) uriList.get(i)).toString());
                        CamLog.d(FaceDetector.TAG, String.format("uri[%d] %s", new Object[]{Integer.valueOf(i), uriString}));
                    }
                    intent.putStringArrayListExtra("capturedUriList", uriStringList);
                    intent.putExtra("app_mode", this.mGet.getApplicationMode());
                    intent.putExtra("camera_dimension", this.mGet.getCameraDimension());
                    intent.putExtra("cameraId", this.mGet.getCameraId());
                    intent.putExtra("currentStorage", this.mGet.getCurrentStorage());
                    intent.putExtra("currentStorageDir", this.mGet.getCurrentStorageDirectory());
                    intent.putExtra("saveFileName", video.getFileName().substring(0, video.getFileName().lastIndexOf(46)));
                    intent.putExtra("isAttachMode", this.mGet.isAttachMode());
                    intent.putExtra("isAttachIntent", this.mGet.isAttachIntent());
                    intent.putExtra("isMmsVideo", this.mGet.needProgressBar());
                    intent.putExtra("autoReview", this.mGet.getSettingValue(Setting.KEY_VIDEO_AUTO_REVIEW));
                    intent.putExtra("saveFilePath", video.getFilePath());
                    intent.putExtra("videoExtension", video.getFileExtension());
                    intent.putExtra("currentLang", this.mGet.getLanguageType());
                    intent.putExtra("currentOrientation", this.mGet.getOrientation());
                    intent.putExtra("effectsActive", this.mGet.isEffectsCamcorderActive());
                    intent.putExtra("effectsSizeOnScreen", this.mGet.getPreviewSizeOnScreen());
                    intent.putExtra("volumeKey", this.mGet.getSettingValue(Setting.KEY_VOLUME));
                }
                if (ProjectVariables.isSupportManualAntibanding() && this.mGet.getCameraId() == 0) {
                    String[] curzoom = new String[]{this.mGet.getSettingValue(Setting.KEY_ZOOM), Integer.toString(this.mGet.getZoomCursorMaxStep()), Integer.toString(this.mGet.getZoomBarValue())};
                    CamLog.d(FaceDetector.TAG, "===> current zoom : " + curzoom[0]);
                    intent.putExtra("currentZoom", curzoom);
                }
                intent.putExtra(CameraConstants.SECURE_CAMERA, Common.isSecureCamera());
                intent.putExtra(CameraConstants.USE_SECURE_LOCK, Common.useSecureLockImage());
                this.mGet.setChangingToOtherActivity(true);
                this.mGet.setVideoStateOnly(0);
                this.mGet.startActivityForResult(intent, 100);
            } catch (NullPointerException e) {
                CamLog.w(FaceDetector.TAG, "DisplayCamcorderPostview NullPointerException:", e);
                this.mGet.doCommand(Command.DISPLAY_PREVIEW);
            } catch (IndexOutOfBoundsException e2) {
                CamLog.w(FaceDetector.TAG, "DisplayCamcorderPostview IndexOutOfBoundsException:", e2);
                this.mGet.doCommand(Command.DISPLAY_PREVIEW);
            }
            CamLog.d(FaceDetector.TAG, "DisplayAfterCaptureView-end");
            return;
        }
        this.mGet.setVideoStateOnly(0);
        Bundle bundle = new Bundle();
        bundle.putBoolean("useAsPostview", true);
        this.mGet.doCommand(Command.SHOW_GALLERY, bundle);
        this.mGet.setChangingToOtherActivity(true);
    }
}
