package com.lge.camera.command;

import android.graphics.Typeface;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;

public class SelectDuration extends Command {
    TextView recTimeText;

    public SelectDuration(ControllerFunction function) {
        super(function);
        this.recTimeText = null;
    }

    public void execute() {
        execute(Boolean.valueOf(true));
    }

    public void execute(Object obj) {
        final boolean con = ((Boolean) obj).booleanValue();
        final int videoSizePrefIndex = this.mGet.getQfIndex(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
        if (this.recTimeText == null && this.mGet.getApplicationMode() == 1 && this.mGet.isControllerInitialized()) {
            this.recTimeText = (TextView) this.mGet.findViewById(R.id.text_rec_time);
            if (this.recTimeText != null) {
                this.recTimeText.setTypeface(Typeface.DEFAULT);
            }
        }
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                SelectDuration.this.mGet.removePostRunnable(this);
                if (SelectDuration.this.mGet.isQuickFunctionList(videoSizePrefIndex)) {
                    SelectDuration.this.mGet.setQuickFunctionControllerMmsLimit(con);
                }
                if (con) {
                    int currentCarrierCode = ModelProperties.getCarrierCode();
                    if (SelectDuration.this.mGet.isMMSRecording() && currentCarrierCode == 6) {
                        SelectDuration.this.mGet.setScaleWidthHeight(0.3f);
                        return;
                    } else {
                        SelectDuration.this.mGet.setScaleWidthHeight(0.2f);
                        return;
                    }
                }
                SelectDuration.this.mGet.setScaleWidthHeight(0.2f);
            }
        });
        this.mGet.setSettingControllerMmsLimit(con);
    }
}
