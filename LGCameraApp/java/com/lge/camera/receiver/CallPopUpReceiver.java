package com.lge.camera.receiver;

import android.content.Context;
import android.content.Intent;
import com.lge.camera.command.Command;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.olaworks.library.FaceDetector;

public class CallPopUpReceiver extends CameraBroadCastReceiver {
    public CallPopUpReceiver(ReceiverMediatorBridge bridge) {
        super(bridge);
    }

    public void onReceive(Context context, Intent intent) {
        if (checkOnReceive()) {
            CamLog.d(FaceDetector.TAG, "BroadCastReceiver action = " + intent.getAction());
            String action = intent.getAction();
            if (action.equals("com.lge.action.CALLALERTING_SHOW")) {
                this.mGet.setBlockTouchByCallPopUp(true);
                if (CheckStatusManager.checkVoiceShutterEnable(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
                    this.mGet.doCommandUi(Command.SET_VOICE_SHUTTER);
                    this.mGet.setPreferenceMenuEnable(Setting.KEY_VOICESHUTTER, false, false);
                }
            } else if (action.equals("com.lge.action.CALLALERTING_HIDE")) {
                this.mGet.setBlockTouchByCallPopUp(false);
            } else if (action.equals("com.lge.action.CALLALERTING_ANSWER") && this.mGet.getVideoState() == 3) {
                this.mGet.stopRecordingByPausing();
            }
            if (this.mGet.isOptionMenuShowing()) {
                this.mGet.hideOptionMenu();
            }
        }
    }

    private boolean checkOnReceive() {
        if (this.mGet != null && this.mGet.getActivity() != null) {
            return true;
        }
        CamLog.d(FaceDetector.TAG, String.format("activity is null", new Object[0]));
        return false;
    }
}
