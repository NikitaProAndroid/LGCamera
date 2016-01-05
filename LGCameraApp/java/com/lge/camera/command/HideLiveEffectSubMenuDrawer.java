package com.lge.camera.command;

import android.view.View;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.components.MultiDirectionSlidingDrawer;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class HideLiveEffectSubMenuDrawer extends Command {
    public HideLiveEffectSubMenuDrawer(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.v(FaceDetector.TAG, "HideLiveEffectSubMenuDrawer executed");
        MultiDirectionSlidingDrawer slidingDrawer = (MultiDirectionSlidingDrawer) this.mGet.findViewById(R.id.live_effect_sliding_drawer_menu_slide);
        if (slidingDrawer != null) {
            slidingDrawer.close();
        }
        View liveEffectSlidingMenuView = this.mGet.findViewById(R.id.live_effect_sliding_drawer_menu_view);
        if (liveEffectSlidingMenuView != null) {
            liveEffectSlidingMenuView.setVisibility(8);
        }
    }
}
