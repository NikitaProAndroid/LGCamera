package com.lge.camera.command;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.PostviewAttachActivity;
import com.lge.camera.PostviewClearShotActivity;
import com.lge.camera.PostviewRefocusActivity;
import com.lge.camera.PostviewTimeMachineActivity;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;
import java.util.Collections;

public class DisplayCameraPostview extends Command {
    public DisplayCameraPostview(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        CamLog.d(FaceDetector.TAG, "DisplayCameraPostview !");
        Bundle bundleFrom = (Bundle) arg;
        boolean useTimeMachinePostview = bundleFrom.getBoolean("useTimeMachinePostview", false);
        boolean useRefocusPostview = bundleFrom.getBoolean("useRefocusPostview", false);
        Intent intent = new Intent();
        if (this.mGet.isTimeMachineModeOn() && useTimeMachinePostview) {
            intent.setClassName(this.mGet.getActivity().getPackageName(), PostviewTimeMachineActivity.class.getName());
        } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_REFOCUS) && useRefocusPostview) {
            intent.setClassName(this.mGet.getActivity().getPackageName(), PostviewRefocusActivity.class.getName());
        } else if (this.mGet.isAttachIntent()) {
            intent.setClassName(this.mGet.getActivity().getPackageName(), PostviewAttachActivity.class.getName());
        } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT)) {
            intent.setClassName(this.mGet.getActivity().getPackageName(), PostviewClearShotActivity.class.getName());
        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean("useAsPostview", true);
            this.mGet.doCommand(Command.SHOW_GALLERY, bundle);
            return;
        }
        makePreviewParameters(intent);
    }

    private void makePreviewParameters(Intent intent) {
        try {
            ArrayList<String> uriStringList = new ArrayList();
            ArrayList<Uri> uriList = this.mGet.getImageListUri();
            int uriListSize = uriList.size();
            if (uriListSize == 0) {
                this.mGet.doCommand(Command.DISPLAY_PREVIEW);
                return;
            }
            if (this.mGet.isTimeMachineModeOn()) {
                Uri tempUri = this.mGet.getSavedImageUri();
                if (tempUri != null) {
                    intent.putExtra("timeMachineOneShotUri", tempUri.toString());
                }
            }
            if (FunctionProperties.isRefocusShotSupported() && this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_REFOCUS) && uriList.size() - 1 >= 0) {
                Uri allinfocusUri = this.mGet.getSavedImageUri();
                if (allinfocusUri != null) {
                    intent.putExtra("allinfocusUri", allinfocusUri.toString());
                }
                Collections.reverse(uriList);
            }
            for (int i = 0; i < uriListSize; i++) {
                uriStringList.add(((Uri) uriList.get(i)).toString());
                CamLog.d(FaceDetector.TAG, String.format("postview uri[%d] %s", new Object[]{Integer.valueOf(i), uriString}));
            }
            intent.putStringArrayListExtra("capturedUriList", uriStringList);
            intent.putExtra("app_mode", this.mGet.getApplicationMode());
            intent.putExtra("camera_dimension", this.mGet.getCameraDimension());
            intent.putExtra("cameraId", this.mGet.getCameraId());
            if (this.mGet.getApplicationMode() == 0 && this.mGet.getCameraId() == 0) {
                intent.putExtra("shotMode", this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE));
                intent.putExtra("shotModeIndex", this.mGet.getSettingIndex(Setting.KEY_CAMERA_SHOT_MODE));
            }
            intent.putExtra("currentStorage", this.mGet.getCurrentStorage());
            intent.putExtra("currentStorageDir", this.mGet.getCurrentStorageDirectory());
            intent.putExtra("timeMachineStorageDir", this.mGet.getTimeMachineStorageDirectory());
            intent.putExtra("saveFileName", this.mGet.getSavedFileName());
            intent.putExtra("isAttachMode", this.mGet.isAttachMode());
            intent.putExtra("isAttachIntent", this.mGet.isAttachIntent());
            intent.putExtra("autoReview", this.mGet.getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW));
            intent.putExtra("hasSavedUri", this.mGet.hasSaveURI());
            intent.putExtra("currentLang", this.mGet.getLanguageType());
            intent.putExtra("timeMachineMode", this.mGet.isTimeMachineModeOn());
            intent.putExtra("currentOrientation", this.mGet.getOrientation());
            intent.putExtra("volumeKey", this.mGet.getSettingValue(Setting.KEY_VOLUME));
            if (this.mGet.getCurrentLocation() != null) {
                intent.putExtra("locationLatitude", this.mGet.getCurrentLocation().getLatitude());
                intent.putExtra("locationLongitude", this.mGet.getCurrentLocation().getLongitude());
            }
            intent.putExtra("isFreePanorama", this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA));
            if (this.mGet.getApplicationMode() == 0 && this.mGet.getCameraId() == 1) {
                intent.putExtra("Flip", this.mGet.getSettingValue(Setting.KEY_SAVE_DIRECTION));
            }
            if (ProjectVariables.isSupportManualAntibanding() && this.mGet.getCameraId() == 0) {
                String[] curzoom = new String[]{this.mGet.getSettingValue(Setting.KEY_ZOOM), Integer.toString(this.mGet.getZoomCursorMaxStep()), Integer.toString(this.mGet.getZoomBarValue())};
                CamLog.d(FaceDetector.TAG, "===> current zoom : " + curzoom[0]);
                intent.putExtra("currentZoom", curzoom);
            }
            intent.putExtra(CameraConstants.SECURE_CAMERA, Common.isSecureCamera());
            intent.putExtra(CameraConstants.USE_SECURE_LOCK, Common.useSecureLockImage());
            this.mGet.setChangingToOtherActivity(true);
            this.mGet.startActivityForResult(intent, 100);
            this.mGet.getImageListUri().clear();
        } catch (Exception e) {
            CamLog.w(FaceDetector.TAG, "DisplayCameraPostview Exception:", e);
            this.mGet.doCommand(Command.DISPLAY_PREVIEW);
        }
    }
}
