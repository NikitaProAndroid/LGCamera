package com.lge.camera.receiver;

import android.content.Context;
import android.content.Intent;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;

public class CameraScreenOffReceiver extends CameraBroadCastReceiver {
    public CameraScreenOffReceiver(ReceiverMediatorBridge bridge) {
        super(bridge);
    }

    public void onReceive(Context context, Intent intent) {
        if (this.mGet != null && this.mGet.getActivity() != null && !this.mGet.getActivity().isFinishing()) {
            if (CheckStatusManager.getCheckEnterOutSecure() == 0 || Common.isQuickWindowCameraMode()) {
                this.mGet.postOnUiThread(new Runnable() {
                    public void run() {
                        if (CameraScreenOffReceiver.this.mGet != null) {
                            CameraScreenOffReceiver.this.mGet.removePostRunnable(this);
                            if (CameraScreenOffReceiver.this.mGet.getActivity() != null) {
                                CameraScreenOffReceiver.this.mGet.getActivity().finish();
                            }
                        }
                    }
                });
            }
        }
    }
}
