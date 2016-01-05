package com.lge.camera.receiver;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import com.lge.camera.R;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;

public class CameraSettingReceiverBySDM extends CameraBroadCastReceiver {
    public CameraSettingReceiverBySDM(ReceiverMediatorBridge bridge) {
        super(bridge);
    }

    public void onReceive(Context context, Intent intent) {
        CamLog.v(FaceDetector.TAG, "BroadCastReceiver action = " + intent.getAction());
        if (!getCameraStateInSDM() && CheckStatusManager.getCheckEnterOutSecure() == 0) {
            Common.toastLong(this.mGet.getApplicationContext(), this.mGet.getString(R.string.error_sdm_server_setting));
            this.mGet.getActivity().finish();
        }
    }

    private boolean getCameraStateInSDM() {
        CamLog.i(FaceDetector.TAG, "check enter In SDM");
        int cameraEnableStatus = 1;
        Cursor cursor = null;
        try {
            cursor = this.mGet.getActivity().getContentResolver().query(CameraConstants.SDM_CONTENT_URI, CameraConstants.CAMERA_PROJECTION, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                CamLog.w(FaceDetector.TAG, "*** cannot access to SDM server DB, cursor = " + cursor);
                if (cursor != null) {
                    cursor.close();
                }
                return true;
            }
            cameraEnableStatus = cursor.getInt(0);
            CamLog.w(FaceDetector.TAG, "*** cameraEnableStatus = " + cameraEnableStatus);
            if (cursor != null) {
                cursor.close();
            }
            if (cameraEnableStatus == 1) {
                return true;
            }
            return false;
        } catch (SQLiteException e) {
            CamLog.e(FaceDetector.TAG, "Could not load photo from database", e);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
