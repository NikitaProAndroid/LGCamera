package com.lge.camera.controller;

import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.Camera3dDepthBar;
import com.lge.camera.components.SettingCamera3dDepthBar;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class Camera3dDepthController extends BarController {
    public static final int MAX_3D_STEP = 15;
    private Camera3dDepthBar mCamera3dDepthBar;
    private SettingCamera3dDepthBar mSettingCamera3dDepthBar;

    public Camera3dDepthController(ControllerFunction function) {
        super(function);
    }

    public void initController() {
        CamLog.d(FaceDetector.TAG, "Camera3dDepthController init()");
        if (!this.mInit) {
            this.mGet.inflateStub(R.id.stub_camera_3d_depth);
            this.mCamera3dDepthBar = (Camera3dDepthBar) findViewById(R.id.camera_3d_depth_bar);
            super.setNormalBarView(this.mCamera3dDepthBar);
            this.mCamera3dDepthBar.setBarSettingCommand(Setting.KEY_CAMERA_3D_DEPTH, Command.SET_CAMERA_3D_DEPTH);
            this.mCamera3dDepthBar.setBarValueInitialization(1000, 200, MAX_3D_STEP, 0, -1, 1);
            this.mCamera3dDepthBar.setBarResources(3, R.id.depth_cursor, R.id.depth_cursor_bg, R.id.depth_minus_button, R.id.depth_minus_button_view, R.id.depth_plus_button, R.id.depth_plus_button_view);
            this.mCamera3dDepthBar.initBar(this);
            this.mCamera3dDepthBar.setListener(true);
            this.mGet.inflateStub(R.id.stub_setting_camera_3d_depth);
            this.mSettingCamera3dDepthBar = (SettingCamera3dDepthBar) findViewById(R.id.setting_camera_3d_depth_bar);
            super.setSettingBarView(this.mSettingCamera3dDepthBar);
            this.mSettingCamera3dDepthBar.setBarSettingCommand(Setting.KEY_CAMERA_3D_DEPTH, Command.SET_CAMERA_3D_DEPTH);
            this.mSettingCamera3dDepthBar.setBarValueInitialization(1000, 200, MAX_3D_STEP, 0, -1, 1);
            this.mSettingCamera3dDepthBar.setBarResources(7, R.id.setting_adj_cursor, R.id.setting_adj_cursor_bg, R.id.setting_adj_minus_button, R.id.setting_adj_minus_button_view, R.id.setting_adj_plus_button, R.id.setting_adj_plus_button_view);
            this.mSettingCamera3dDepthBar.initBar(this);
            this.mSettingCamera3dDepthBar.setListener(true);
            this.mInit = true;
        }
    }

    public void clearSettingDepth3DBar() {
        if (this.mSettingCamera3dDepthBar.getVisibility() == 0) {
            hideSettingDepth3DControl(true);
        }
    }

    public void showSetting3dDepthControl(boolean useAnim) {
        if (this.mInit) {
            super.showSettingBarControl(R.id.setting_camera_3d_depth, useAnim);
        }
    }

    public void hideSettingDepth3DControl(boolean useAnim) {
        if (this.mInit) {
            super.hideSettingBarControl(R.id.setting_camera_3d_depth, useAnim);
        }
    }

    public void reset() {
        ListPreference preference = this.mGet.findPreference(Setting.KEY_CAMERA_3D_DEPTH);
        if (preference != null) {
            setSetting(Setting.KEY_CAMERA_3D_DEPTH, preference.getDefaultValue());
            int value = Integer.parseInt(preference.getDefaultValue());
            this.mCamera3dDepthBar.resetValue(value);
            this.mCamera3dDepthBar.resetCursor(value);
            this.mSettingCamera3dDepthBar.resetValue(value);
            this.mSettingCamera3dDepthBar.resetCursor(value);
        }
    }
}
