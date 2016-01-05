package com.lge.camera.postview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PostViewScreenOffReceiver extends BroadcastReceiver {
    private ReceiverFunction mGet;

    public PostViewScreenOffReceiver(ReceiverFunction function) {
        this.mGet = null;
        this.mGet = function;
    }

    public void onReceive(Context context, Intent intent) {
        if (this.mGet != null) {
            this.mGet.finish();
        }
    }

    public void unbind() {
        if (this.mGet != null) {
            this.mGet = null;
        }
    }
}
