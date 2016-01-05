package com.lge.camera.controller;

import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.BeautyshotBar;
import com.lge.camera.components.SettingBeautyshotBar;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class BeautyshotController extends BarController {
    public static final int MAX_BEAUTYSHOT_STEP = 8;
    private BeautyshotBar mBeautyshotBar;
    private SettingBeautyshotBar mSettingBeautyshotBar;

    public BeautyshotController(ControllerFunction function) {
        super(function);
    }

    public void initController() {
        CamLog.d(FaceDetector.TAG, "BeautyshotController init()");
        if (!this.mInit) {
            this.mGet.inflateStub(R.id.stub_beautyshot);
            this.mBeautyshotBar = (BeautyshotBar) findViewById(R.id.beautyshot_bar);
            super.setNormalBarView(this.mBeautyshotBar);
            this.mBeautyshotBar.setBarSettingCommand(Setting.KEY_BEAUTYSHOT, Command.SET_BEAUTYSHOT);
            this.mBeautyshotBar.setBarValueInitialization(1000, 200, MAX_BEAUTYSHOT_STEP, 0, -1, 1);
            this.mBeautyshotBar.setBarResources(2, R.id.face_cursor, R.id.face_cursor_bg, R.id.face_minus_button, R.id.face_minus_button_view, R.id.face_plus_button, R.id.face_plus_button_view);
            this.mBeautyshotBar.initBar(this);
            this.mBeautyshotBar.setListener(true);
            this.mGet.inflateStub(R.id.stub_setting_beautyshot);
            this.mSettingBeautyshotBar = (SettingBeautyshotBar) findViewById(R.id.setting_beautyshot_bar);
            super.setSettingBarView(this.mSettingBeautyshotBar);
            this.mSettingBeautyshotBar.setBarSettingCommand(Setting.KEY_BEAUTYSHOT, Command.SET_BEAUTYSHOT);
            this.mSettingBeautyshotBar.setBarValueInitialization(1000, 200, MAX_BEAUTYSHOT_STEP, 0, -1, 1);
            this.mSettingBeautyshotBar.setBarResources(6, R.id.setting_adj_cursor, R.id.setting_adj_cursor_bg, R.id.setting_adj_minus_button, R.id.setting_adj_minus_button_view, R.id.setting_adj_plus_button, R.id.setting_adj_plus_button_view);
            this.mSettingBeautyshotBar.initBar(this);
            this.mSettingBeautyshotBar.setListener(true);
            this.mInit = true;
        }
    }

    public int getCursorMaxStep() {
        if (!this.mInit || this.mBeautyshotBar == null) {
            return MAX_BEAUTYSHOT_STEP;
        }
        return this.mBeautyshotBar.getCursorMaxStep();
    }

    public void clearSettingBeautyshotBar() {
        if (this.mSettingBeautyshotBar.getVisibility() == 0) {
            hideSettingBeautyShotControl(true);
        }
    }

    public void showSettingBeautyShotControl(boolean useAnim) {
        if (this.mInit) {
            super.showSettingBarControl(R.id.setting_beautyshot_rotate, useAnim);
        }
    }

    public void hideSettingBeautyShotControl(boolean useAnim) {
        if (this.mInit) {
            super.hideSettingBarControl(R.id.setting_beautyshot_rotate, useAnim);
        }
    }

    public void updateAllBars(int value) {
        if (getApplicationMode() == 0) {
            super.updateAllBars(value);
        }
    }

    public void reset() {
        if (this.mInit && getApplicationMode() == 0) {
            if (this.mBeautyshotBar != null) {
                this.mBeautyshotBar.resetValue(4);
                this.mBeautyshotBar.resetCursor(4);
            }
            this.mSettingBeautyshotBar.resetValue(4);
            this.mSettingBeautyshotBar.resetCursor(4);
            setSetting(Setting.KEY_BEAUTYSHOT, Integer.toString(4));
            doCommandNoneParameter(Command.SET_BEAUTYSHOT);
        }
    }
}
