package com.lge.camera.controller;

import android.view.View;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.BrightnessBar;
import com.lge.camera.components.SettingBrightnessBar;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class BrightnessController extends BarController {
    private BrightnessBar mBrightnessBar;
    private SettingBrightnessBar mSettingBrightnessBar;

    public BrightnessController(ControllerFunction function) {
        super(function);
    }

    public void initController() {
        CamLog.d(FaceDetector.TAG, "BrightnessController init()");
        if (!this.mInit) {
            this.mGet.inflateStub(R.id.stub_brightness);
            this.mBrightnessBar = (BrightnessBar) findViewById(R.id.brightness_bar);
            super.setNormalBarView(this.mBrightnessBar);
            this.mBrightnessBar.setBarSettingCommand(Setting.KEY_BRIGHTNESS, Command.SET_BRIGHTNESS);
            this.mBrightnessBar.setBarValueInitialization(1000, 200, CameraConstants.MAX_BRIGHTNESS_STEP, 0, -1, 1);
            this.mBrightnessBar.setBarResources(1, R.id.adj_cursor, R.id.adj_cursor_bg, R.id.adj_minus_button, R.id.adj_minus_button_view, R.id.adj_plus_button, R.id.adj_plus_button_view);
            this.mBrightnessBar.initBar(this);
            this.mBrightnessBar.setListener(true);
            this.mGet.inflateStub(R.id.stub_setting_brightness);
            this.mSettingBrightnessBar = (SettingBrightnessBar) findViewById(R.id.setting_brightness_bar);
            super.setSettingBarView(this.mSettingBrightnessBar);
            this.mSettingBrightnessBar.setBarSettingCommand(Setting.KEY_BRIGHTNESS, Command.SET_BRIGHTNESS);
            this.mSettingBrightnessBar.setBarValueInitialization(1000, 200, CameraConstants.MAX_BRIGHTNESS_STEP, 0, -1, 1);
            this.mSettingBrightnessBar.setBarResources(5, R.id.setting_adj_cursor, R.id.setting_adj_cursor_bg, R.id.setting_adj_minus_button, R.id.setting_adj_minus_button_view, R.id.setting_adj_plus_button, R.id.setting_adj_plus_button_view);
            this.mSettingBrightnessBar.initBar(this);
            this.mSettingBrightnessBar.setListener(true);
            this.mInit = true;
        }
    }

    public int getCursorMaxStep() {
        if (!this.mInit || this.mBrightnessBar == null) {
            return CameraConstants.MAX_BRIGHTNESS_STEP;
        }
        return this.mBrightnessBar.getCursorMaxStep();
    }

    public void clearSettingBrightnessBar() {
        if (this.mSettingBrightnessBar.getVisibility() == 0) {
            hideSettingBrightnessControl(true);
        }
    }

    public void showSettingBrightnessControl(boolean useAnim) {
        if (this.mInit) {
            super.showSettingBarControl(R.id.setting_brightness_rotate, useAnim);
        }
    }

    public void showControl(boolean visible) {
        if (this.mInit && this.mBrightnessBar != null) {
            this.mBrightnessBar.showControl(visible);
            View indexView = findViewById(R.id.brightness_index_view);
            if (indexView != null) {
                indexView.setVisibility(visible ? 0 : 4);
            }
        }
    }

    public void hideSettingBrightnessControl(boolean useAnim) {
        if (this.mInit) {
            super.hideSettingBarControl(R.id.setting_brightness_rotate, useAnim);
        }
    }

    public void reset() {
        ListPreference preference = this.mGet.findPreference(Setting.KEY_BRIGHTNESS);
        if (preference != null) {
            setSetting(Setting.KEY_BRIGHTNESS, preference.getDefaultValue());
            int value = Integer.parseInt(preference.getDefaultValue());
            if (this.mBrightnessBar != null) {
                this.mBrightnessBar.resetValue(value);
                this.mBrightnessBar.resetCursor(value);
            }
            if (this.mSettingBrightnessBar != null) {
                this.mSettingBrightnessBar.resetValue(value);
                this.mSettingBrightnessBar.resetCursor(value);
            }
        }
    }
}
