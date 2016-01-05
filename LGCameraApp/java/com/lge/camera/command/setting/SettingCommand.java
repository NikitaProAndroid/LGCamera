package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.command.Command;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public abstract class SettingCommand extends Command {
    public SettingCommand(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        LGCamera lgCamera = this.mGet.getLG();
        if (lgCamera != null) {
            LGParameters lgParameters = lgCamera.getLGParameters();
            execute(lgParameters);
            try {
                lgParameters.setParameters(lgParameters.getParameters());
            } catch (RuntimeException e) {
                CamLog.e(FaceDetector.TAG, "setParameters failed: " + e);
            }
            onExecuteAlone();
            return;
        }
        CamLog.i(FaceDetector.TAG, String.format("Camera ref is null. Setting command return.", new Object[0]));
    }

    public void execute(Object arg) {
        if (arg == null || !(arg instanceof LGParameters)) {
            CamLog.w(FaceDetector.TAG, "arg is not Parameter !!");
        } else {
            execute((LGParameters) arg);
        }
    }

    public void execute(Object arg1, Object arg2) {
        if (arg1 == null || !(arg1 instanceof LGParameters)) {
            LGCamera lgCamera = this.mGet.getLG();
            if (lgCamera != null) {
                LGParameters lgParameters = lgCamera.getLGParameters();
                execute(lgParameters, arg2);
                try {
                    lgParameters.setParameters(lgParameters.getParameters());
                } catch (RuntimeException e) {
                    CamLog.e(FaceDetector.TAG, "setParameters failed: " + e);
                }
                onExecuteAlone();
                return;
            }
            CamLog.i(FaceDetector.TAG, String.format("Camera ref is null. Setting command return.", new Object[0]));
            return;
        }
        execute((LGParameters) arg1, arg2);
    }

    public void executeNoneParameter() {
        execute(null);
        onExecuteAlone();
    }

    public void executeNoneParameter(Object arg) {
        execute(null, arg);
        onExecuteAlone();
    }

    protected void execute(LGParameters lgParameters) {
    }

    protected void execute(LGParameters lgParameters, Object arg2) {
    }

    protected void onExecuteAlone() {
    }
}
