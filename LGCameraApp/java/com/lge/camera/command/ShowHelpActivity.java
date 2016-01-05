package com.lge.camera.command;

import com.lge.camera.ControllerFunction;

public class ShowHelpActivity extends Command {
    public ShowHelpActivity(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        this.mGet.postOnUiThread(new Runnable() {
            public void run() {
                if (ShowHelpActivity.this.checkMediator()) {
                    ShowHelpActivity.this.mGet.removePostRunnable(this);
                    ShowHelpActivity.this.mGet.gotoHelpActivity(ShowHelpActivity.this.mGet.getApplicationMode() == 0 ? "camera help" : "camcorder help");
                }
            }
        }, 300);
    }
}
