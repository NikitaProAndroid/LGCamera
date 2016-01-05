package com.lge.camera.dialog;

import android.view.View;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;

public class ProgressRotatableDialog extends RotateDialog {
    public ProgressRotatableDialog(ControllerFunction function) {
        super(function);
    }

    public void create(int rId) {
        View v = this.mGet.inflateView(R.layout.progress_rotate_dialog);
        ((TextView) v.findViewById(R.id.message_text)).setText(rId);
        super.create(v, false);
    }
}
