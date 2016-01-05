package com.lge.camera.controller;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.SettingBrightnessBarExpand;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class BrightnessControllerExpand extends BarController {
    private SettingBrightnessBarExpand mSettingBrightnessBar;
    RelativeLayout mSettingBrigtnessBarLayout;

    public BrightnessControllerExpand(ControllerFunction function) {
        super(function);
    }

    public void initController() {
        CamLog.d(FaceDetector.TAG, "BrightnessController init()");
        if (!this.mInit) {
            this.mInit = true;
        }
    }

    public RelativeLayout initSettingBrightnessBar() {
        this.mSettingBrigtnessBarLayout = (RelativeLayout) ((LayoutInflater) this.mGet.getApplicationContext().getSystemService("layout_inflater")).inflate(R.layout.setting_expand_brightness, null);
        this.mSettingBrightnessBar = (SettingBrightnessBarExpand) this.mSettingBrigtnessBarLayout.findViewById(R.id.setting_brightness_bar);
        super.setSettingBarView(this.mSettingBrightnessBar);
        this.mSettingBrightnessBar.setBarSettingCommand(Setting.KEY_BRIGHTNESS, Command.SET_BRIGHTNESS);
        this.mSettingBrightnessBar.setBarValueInitialization(1000, 200, CameraConstants.MAX_BRIGHTNESS_STEP, 0, -1, 1);
        this.mSettingBrightnessBar.setBarResources(9, R.id.setting_adj_cursor, R.id.setting_adj_cursor_bg, R.id.setting_adj_minus_button, R.id.setting_adj_minus_button_view, R.id.setting_adj_plus_button, R.id.setting_adj_plus_button_view);
        this.mSettingBrightnessBar.initBar(this);
        this.mSettingBrightnessBar.setListener(true);
        this.mSettingBrigtnessBarLayout.setVisibility(0);
        this.mSettingBrigtnessBarLayout.setSoundEffectsEnabled(false);
        this.mSettingBrigtnessBarLayout.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.mSettingBrightnessBar.setVisibility(0);
        return this.mSettingBrigtnessBarLayout;
    }

    public void showSettingBrightnessControl(boolean useAnim) {
        if (this.mInit) {
            super.showSettingBarControl(R.id.setting_brightness_rotate, useAnim);
        }
    }

    public void reset() {
        ListPreference preference = this.mGet.findPreference(Setting.KEY_BRIGHTNESS);
        if (preference != null) {
            setSetting(Setting.KEY_BRIGHTNESS, preference.getDefaultValue());
            int value = Integer.parseInt(preference.getDefaultValue());
            if (this.mSettingBrightnessBar != null) {
                this.mSettingBrightnessBar.resetValue(value);
                this.mSettingBrightnessBar.resetCursor(value);
            }
        }
    }

    public void updateAllBars(int value) {
    }
}
