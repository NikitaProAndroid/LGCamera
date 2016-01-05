package com.lge.camera.receiver;

import android.content.Context;
import android.content.Intent;
import com.lge.camera.R;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;

public class HdmiReceiver extends CameraBroadCastReceiver {
    public final String DualDisplayConnectedEvent;
    public final String HDMICableConnectedEvent;
    public final String HDMICableConnectedEventForOMAP;
    public final String HDMICableConnectedEventFornVidia;
    public final String HDMICableDisconnectedEvent;

    public HdmiReceiver(ReceiverMediatorBridge bridge) {
        super(bridge);
        this.HDMICableConnectedEventForOMAP = "android.intent.action.HDMI_PLUG";
        this.HDMICableConnectedEvent = "HDMI_CABLE_CONNECTED";
        this.HDMICableDisconnectedEvent = "HDMI_CABLE_DISCONNECTED";
        this.HDMICableConnectedEventFornVidia = "android.intent.action.HDMI_AUDIO_PLUG";
        this.DualDisplayConnectedEvent = "android.intent.action.DUALDISPLAY";
    }

    public void onReceive(Context context, Intent intent) {
        CamLog.v(FaceDetector.TAG, "mHdmiReciever RECEIVER IN");
        String action = intent.getAction();
        if (action.equals("HDMI_CABLE_CONNECTED")) {
            CamLog.v(FaceDetector.TAG, "HDMICableConnectedEvent IN");
            HdmiConnectedAction();
        } else if (action.equals("HDMI_CABLE_DISCONNECTED")) {
            CamLog.v(FaceDetector.TAG, "HDMICable DisconnectedEvent IN");
            HdmiDisconnectedAction();
        } else if (action.equals("android.intent.action.HDMI_PLUG")) {
            CamLog.v(FaceDetector.TAG, "HDMICableConnectedEvent IN");
            if (intent.getIntExtra("state", -1) == 1) {
                HdmiConnectedAction();
            }
        } else if (action.equals("android.intent.action.HDMI_AUDIO_PLUG")) {
            CamLog.v(FaceDetector.TAG, "HDMICableConnectedEventFornVidia IN");
            int state = intent.getIntExtra("state", 0);
            if (state == 1) {
                HdmiConnectedAction();
            } else if (state == 0) {
                HdmiDisconnectedAction();
            }
        } else if (action.equals("android.intent.action.DUALDISPLAY")) {
            boolean state2 = intent.getBooleanExtra("state", false);
            CamLog.v(FaceDetector.TAG, "Dual Display Intent received, state: " + state2);
            if (state2) {
                DualDisplayConnectedAction();
            } else {
                DualDisplayDisconnectedAction();
            }
        } else {
            CamLog.v(FaceDetector.TAG, "other HDMI RCVR IN");
        }
    }

    private void HdmiConnectedAction() {
        if (ProjectVariables.isSupportHDMI_MHL()) {
            CamLog.d(FaceDetector.TAG, "It can support HDMI/MHL!!");
        } else if (CheckStatusManager.getCheckEnterOutSecure() == 0) {
            Common.toast(this.mGet.getApplicationContext(), this.mGet.getString(R.string.error_cannot_use_hdmi));
            this.mGet.getActivity().finish();
        }
    }

    private void HdmiDisconnectedAction() {
        if (ProjectVariables.isSupportHDMI_MHL()) {
            CamLog.d(FaceDetector.TAG, "It can support HDMI/MHL!!");
        }
    }

    private void DualDisplayConnectedAction() {
        CamLog.d(FaceDetector.TAG, "DualDisplayConnectedAction");
        if (!ProjectVariables.isSupportHDMI_MHL()) {
            CamLog.d(FaceDetector.TAG, "It can support HDMI/MHL!!");
        } else if (CheckStatusManager.getCheckEnterOutSecure() == 0) {
            Common.toast(this.mGet.getApplicationContext(), this.mGet.getString(R.string.sp_dual_display_status_NORMAL));
            this.mGet.getActivity().finish();
        }
    }

    private void DualDisplayDisconnectedAction() {
        CamLog.d(FaceDetector.TAG, "DualDisplayDisconnectedAction");
        if (!ProjectVariables.isSupportHDMI_MHL()) {
        }
    }
}
