package com.lge.camera.controller;

import android.view.View;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.ManualFocusBar;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;

public class ManualFocusController extends BarController {
    public static final int MANUAL_FOCUS_DEFAULT_VALUE = 0;
    public static final int MAX_MANUAL_FOCUS_STEP = 60;
    private ManualFocusBar mManualFocusBar;

    public ManualFocusController(ControllerFunction function) {
        super(function);
    }

    public void initController() {
        CamLog.d(FaceDetector.TAG, "BrightnessController init()");
        if (!this.mInit) {
            this.mGet.inflateStub(R.id.stub_manual_focus);
            this.mManualFocusBar = (ManualFocusBar) findViewById(R.id.manual_focusbar);
            super.setNormalBarView(this.mManualFocusBar);
            this.mManualFocusBar.setBarSettingCommand(null, Command.SET_MANUAL_FOCUS);
            this.mManualFocusBar.setBarValueInitialization(1000, 200, MAX_MANUAL_FOCUS_STEP, MANUAL_FOCUS_DEFAULT_VALUE, -1, 1);
            this.mManualFocusBar.setBarResources(8, R.id.manual_focus_cursor, R.id.manual_focus_cursor_bg, R.id.manual_focus_minus_button, R.id.manual_focus_minus_button_view, R.id.manual_focus_plus_button, R.id.manual_focus_plus_button_view);
            this.mManualFocusBar.initBar(this);
            this.mManualFocusBar.setListener(true);
            this.mInit = true;
        }
    }

    public int getCursorMaxStep() {
        if (!this.mInit || this.mManualFocusBar == null) {
            return MAX_MANUAL_FOCUS_STEP;
        }
        return this.mManualFocusBar.getCursorMaxStep();
    }

    public void resetDisplayTimeout() {
        if (this.mInit && this.mManualFocusBar != null) {
            this.mManualFocusBar.resetDisplayTimeout();
        }
    }

    public void reset() {
        if (this.mManualFocusBar != null) {
            this.mManualFocusBar.resetValue(MANUAL_FOCUS_DEFAULT_VALUE);
            this.mManualFocusBar.resetCursor(MANUAL_FOCUS_DEFAULT_VALUE);
        }
    }

    public void setManualFocusValue(int value) {
        if (this.mManualFocusBar != null) {
            this.mManualFocusBar.setBarSettingValue(Setting.HELP_OTHER, value);
        }
    }

    public int getManualFocusValue() {
        if (this.mManualFocusBar != null) {
            return this.mManualFocusBar.getCurrentManualFocusValue();
        }
        return MANUAL_FOCUS_DEFAULT_VALUE;
    }

    public void showControl(boolean visible) {
        if (this.mInit && this.mManualFocusBar != null && !Common.isQuickWindowCameraMode()) {
            this.mManualFocusBar.showControl(visible);
            View indexView = findViewById(R.id.manual_focus_index_view);
            if (indexView != null) {
                indexView.setVisibility(visible ? MANUAL_FOCUS_DEFAULT_VALUE : 4);
            }
        }
    }
}
