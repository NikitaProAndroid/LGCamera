package com.lge.camera.command;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class GotoAUCloud extends Command {
    public GotoAUCloud(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        ApplicationInfo info = null;
        try {
            info = this.mGet.getActivity().getPackageManager().getApplicationInfo("com.kddi.android.auclouduploader", 128);
        } catch (NameNotFoundException e) {
            CamLog.d(FaceDetector.TAG, "Au Cloud cannot be founded:", e);
        }
        if (info == null || info.enabled) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.kddi.android.auclouduploader", "com.kddi.android.auclouduploader.activity.AutoUploadSettingActivity"));
            intent.addFlags(67108864);
            try {
                this.mGet.getActivity().startActivity(intent);
                return;
            } catch (ActivityNotFoundException ex) {
                CamLog.e(FaceDetector.TAG, "AU Cloud App is not exist", ex);
                return;
            }
        }
        this.mGet.showDialogPopup(28);
    }
}
