package com.lge.camera.command;

import com.lge.camera.ControllerFunction;

public class DeleteProgressDialog extends Command {
    public DeleteProgressDialog(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        this.mGet.deleteProgressDialog();
    }
}
