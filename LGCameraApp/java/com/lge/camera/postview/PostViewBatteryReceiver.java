package com.lge.camera.postview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lge.camera.util.CheckStatusManager;

public class PostViewBatteryReceiver extends BroadcastReceiver {
    private ReceiverFunction mGet;

    public PostViewBatteryReceiver(ReceiverFunction function) {
        this.mGet = null;
        this.mGet = function;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.intent.action.BATTERY_CHANGED")) {
            int charged = intent.getIntExtra("level", -1);
            if (charged != -1 && charged <= 5) {
                CheckStatusManager.setEnterCheckComplete(false);
                if (this.mGet != null) {
                    this.mGet.finish();
                }
            }
        } else if (action.equals("android.intent.action.BATTERY_LOW")) {
            int level = intent.getIntExtra("level", -1);
            if (level != -1 && level <= 5) {
                CheckStatusManager.setEnterCheckComplete(false);
                if (this.mGet != null) {
                    this.mGet.finish();
                }
            }
        }
    }

    public void unbind() {
        if (this.mGet != null) {
            this.mGet = null;
        }
    }
}
