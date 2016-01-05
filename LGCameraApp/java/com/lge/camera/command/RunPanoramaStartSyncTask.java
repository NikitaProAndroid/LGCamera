package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.olaworks.library.AutoPanorama;

public class RunPanoramaStartSyncTask extends Command {
    public RunPanoramaStartSyncTask(ControllerFunction function) {
        super(function);
    }

    public void execute() {
    }

    public void execute(Object obj) {
        AutoPanorama panorama = (AutoPanorama) obj;
        if (panorama != null) {
            panorama.runStartSynthesisTask();
        }
    }
}
