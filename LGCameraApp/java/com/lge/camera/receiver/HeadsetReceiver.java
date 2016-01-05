package com.lge.camera.receiver;

import android.content.Context;
import android.content.Intent;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.util.AudioUtil;

public class HeadsetReceiver extends CameraBroadCastReceiver {
    public HeadsetReceiver(ReceiverMediatorBridge bridge) {
        super(bridge);
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.HEADSET_PLUG")) {
            int state = intent.getIntExtra("state", -1);
            String nm = intent.getStringExtra("name");
            int mic = intent.getIntExtra("microphone", 0);
            if (nm == null || state != 1) {
                this.mGet.setHeadsetstate(0);
                this.mGet.setForced_audiozoom(true);
            } else if (AudioUtil.getHasMic(this.mGet.getApplicationContext()) && mic == 1) {
                this.mGet.setHeadsetstate(2);
                this.mGet.setForced_audiozoom(false);
            } else {
                this.mGet.setHeadsetstate(1);
                this.mGet.setForced_audiozoom(true);
            }
        }
    }
}
