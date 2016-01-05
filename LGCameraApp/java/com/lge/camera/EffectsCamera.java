package com.lge.camera;

import android.content.Context;
import com.lge.camera.EffectsBase.EffectBaseInterface;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class EffectsCamera extends EffectsBase {
    public EffectsCamera(Context context, EffectBaseInterface inf) {
        super(context, inf);
        CamLog.v(FaceDetector.TAG, "EffectsCamera created (" + this + ")");
    }
}
