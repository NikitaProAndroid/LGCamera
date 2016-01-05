package com.lge.camera.command;

import android.os.Bundle;
import android.view.View;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.components.MultiDirectionSlidingDrawer;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class HidePIPFrameSubMenu extends Command {
    public HidePIPFrameSubMenu(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.v(FaceDetector.TAG, "HidePIPFrameSubMenu executed");
        MultiDirectionSlidingDrawer slidingDrawer = (MultiDirectionSlidingDrawer) this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu_slide);
        if (slidingDrawer != null) {
            slidingDrawer.close();
        }
        View pipFrameSlidingMenuView = this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu_view);
        if (pipFrameSlidingMenuView != null) {
            pipFrameSlidingMenuView.setVisibility(8);
        }
    }

    public void execute(Object arg) {
        CamLog.v(FaceDetector.TAG, "HidePIPFrameSubMenu executed arg exist");
        if (((Bundle) arg).getBoolean("only_handle_close", false)) {
            MultiDirectionSlidingDrawer slidingDrawer = (MultiDirectionSlidingDrawer) this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu_slide);
            if (slidingDrawer != null) {
                slidingDrawer.close();
                return;
            }
            return;
        }
        execute();
    }
}
