package com.lge.camera.controller;

import android.view.View;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.SettingZoomBar;
import com.lge.camera.components.ZoomBar;
import com.lge.camera.components.ZoomProgressBar;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ZoomController extends BarController {
    private static float mZoomMaxValue;
    private static float mZoomRatio;
    private SettingZoomBar mSettingZoomBar;
    private ZoomBar mZoomBar;

    static {
        mZoomMaxValue = 0.0f;
        mZoomRatio = 0.0f;
    }

    public ZoomController(ControllerFunction function) {
        super(function);
    }

    public void initController() {
        CamLog.d(FaceDetector.TAG, "ZoomController init()");
        if (!this.mInit) {
            this.mGet.inflateStub(R.id.stub_zoom);
            this.mZoomBar = (ZoomBar) findViewById(R.id.zoom_control);
            super.setNormalBarView(this.mZoomBar);
            this.mZoomBar.setBarSettingCommand(Setting.KEY_ZOOM, Command.SET_ZOOM);
            this.mZoomBar.setBarValueInitialization(500, 50, 90, 0, -2, 2);
            this.mZoomBar.setBarResources(0, R.id.zoom_bar, R.id.zoom_bar_bg_view, R.id.zoom_out_button, R.id.zoom_out_button_view, R.id.zoom_in_button, R.id.zoom_in_button_view);
            this.mZoomBar.initBar(this);
            this.mZoomBar.setListener(true);
            this.mZoomBar.updateZoomText();
            this.mGet.inflateStub(R.id.stub_setting_zoom);
            this.mSettingZoomBar = (SettingZoomBar) findViewById(R.id.setting_zoom_control);
            super.setSettingBarView(this.mSettingZoomBar);
            this.mSettingZoomBar.setBarSettingCommand(Setting.KEY_ZOOM, Command.SET_ZOOM);
            this.mSettingZoomBar.setBarValueInitialization(500, 50, 90, 0, -2, 2);
            this.mSettingZoomBar.setBarResources(4, R.id.setting_zoom_bar, R.id.setting_zoom_bar_bg_view, R.id.setting_zoom_out_button, R.id.setting_zoom_out_button_view, R.id.setting_zoom_in_button, R.id.setting_zoom_in_button_view);
            this.mSettingZoomBar.initBar(this);
            this.mSettingZoomBar.setListener(true);
            this.mInit = true;
        }
    }

    public void setZoomMaxValue(float max) {
        mZoomMaxValue = max;
    }

    public float getZoomMaxValue() {
        return mZoomMaxValue;
    }

    public void setZoomRatio(float ratio) {
        mZoomRatio = ratio;
    }

    public float getZoomRatio() {
        return mZoomRatio / CameraConstants.PIP_VIEW_ALLOWABLE_MOVEMENT_EXTENT_FOR_TOGGLE;
    }

    public void setZoomCursorMaxStep(int step) {
        if (this.mInit) {
            this.mZoomBar.setCursorMaxStep(step);
            this.mSettingZoomBar.setCursorMaxStep(step);
        }
    }

    public int getCursorMaxStep() {
        if (this.mGet.getCameraDevice() == null || this.mGet.getParameters() == null) {
            CamLog.d(FaceDetector.TAG, "device is not ready.");
            return 90;
        }
        try {
            int ret = this.mGet.getParameters().getMaxZoom();
            CamLog.d(FaceDetector.TAG, "device getMaxZoom = " + ret);
            return ret;
        } catch (RuntimeException e) {
            CamLog.e(FaceDetector.TAG, "RuntimeException : getParameters failed.", e);
            return 90;
        }
    }

    public void showControl(boolean visible) {
        if (this.mInit && this.mZoomBar != null && findViewById(R.id.zoom_text_view) != null) {
            this.mZoomBar.showControl(visible);
            View zoomText = findViewById(R.id.zoom_text_view);
            if (zoomText != null) {
                zoomText.setVisibility(visible ? 0 : 4);
            }
            if (!visible) {
                endZoomInOut();
            }
            View zoomProgress = findViewById(R.id.zoom_bar_bg);
            if (zoomProgress != null) {
                ((ZoomProgressBar) zoomProgress).initZoomProgressBar(getCursorMaxStep());
            }
        }
    }

    public void clearSettingZoom() {
        if (this.mSettingZoomBar != null && this.mSettingZoomBar.getVisibility() == 0) {
            hideSettingZoomControl(true);
        }
    }

    public void showSettingZoomControl(boolean useAnim) {
        if (this.mInit) {
            super.showSettingBarControl(R.id.setting_zoom_rotate, useAnim);
        }
    }

    public void hideSettingZoomControl(boolean useAnim) {
        if (this.mInit) {
            super.hideSettingBarControl(R.id.setting_zoom_rotate, useAnim);
        }
    }

    public ZoomBar getZoomBar() {
        return this.mZoomBar;
    }

    public boolean endZoomInOut() {
        if (!this.mInit) {
            return false;
        }
        this.mZoomBar.stopTimerTask();
        this.mSettingZoomBar.stopTimerTask();
        return true;
    }

    public void reset() {
        ListPreference preference = this.mGet.findPreference(Setting.KEY_ZOOM);
        if (preference != null && this.mZoomBar != null && this.mSettingZoomBar != null) {
            setSetting(Setting.KEY_ZOOM, preference.getDefaultValue());
            reset(Integer.parseInt(preference.getDefaultValue()));
        }
    }

    public void reset(int value) {
        if (this.mZoomBar != null && this.mSettingZoomBar != null) {
            this.mZoomBar.resetValue(value);
            this.mZoomBar.resetCursor(value);
            this.mSettingZoomBar.resetValue(value);
            this.mSettingZoomBar.resetCursor(value);
        }
    }

    public void updateAllBars(int value) {
        int maxValue = getCursorMaxStep();
        CamLog.d(FaceDetector.TAG, "maxValue = " + maxValue + ", value = " + value);
        if (this.mZoomBar != null) {
            this.mZoomBar.setCursorMaxStep(maxValue);
            this.mZoomBar.setBarValue(value);
        }
        if (this.mSettingZoomBar != null) {
            this.mSettingZoomBar.setCursorMaxStep(maxValue);
            this.mSettingZoomBar.setBarValue(value);
        }
    }

    public int getZoomBarValue() {
        if (!this.mInit || this.mZoomBar == null) {
            return 0;
        }
        return this.mZoomBar.getValue();
    }

    public void onPause() {
        if (this.mInit) {
            int zoomMaxValue = (int) getZoomMaxValue();
            int zoomValue = this.mZoomBar.getValue();
            if (this.mZoomBar.getCursorMaxStep() == 90) {
                zoomValue = (zoomValue * zoomMaxValue) / 90;
            }
            CamLog.d(FaceDetector.TAG, "zoombar : mValue = " + zoomValue);
            setSetting(Setting.KEY_ZOOM, Integer.toString(zoomValue));
            this.mZoomBar.stopTimerTask();
            this.mSettingZoomBar.stopTimerTask();
        }
    }
}
