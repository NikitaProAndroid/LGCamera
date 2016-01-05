package com.lge.camera.controller;

import android.view.View;
import android.view.View.OnClickListener;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.RotateImageButton;
import com.lge.camera.properties.ModelProperties;
import com.lge.olaworks.define.Ola_Exif.Tag;

public class PreviewPanel3DController extends PreviewPanelController {
    private RotateImageButton m3DSwitchButton;
    private OnClickListener m3DSwitchButtonListener;

    public PreviewPanel3DController(ControllerFunction function) {
        super(function);
        this.m3DSwitchButton = null;
        this.m3DSwitchButtonListener = new OnClickListener() {
            public void onClick(View v) {
                PreviewPanel3DController.this.mGet.doCommand(Command.SWAP_CAMERA_DIMENSION);
            }
        };
    }

    public void initController() {
        this.mGet.inflateStub(R.id.stub_preview_3d_panel);
        this.m3DSwitchButton = (RotateImageButton) this.mGet.findViewById(R.id.mode_switch);
        this.m3DSwitchButton.setOnClickListener(this.m3DSwitchButtonListener);
        this.m3DSwitchButton.setFocusable(false);
        enableCommand(false);
        set3DSwitchImage();
        startRotation(this.mGet.getOrientationDegree(), false);
        super.initController();
    }

    public void initReleaseArea() {
        super.initReleaseArea();
    }

    public void showCommand() {
        if (this.mGet.isPreviewing()) {
            setSwitcherVisible(true);
            setMainButtonEnable();
            showSubButtonInit(false);
        }
        if (!this.mGet.isAttachIntent()) {
            this.m3DSwitchButton.setVisibility(0);
        }
    }

    public void enableCommand(boolean enable) {
        super.enableCommand(enable);
        if (this.mInit) {
            if (this.m3DSwitchButton == null) {
                this.m3DSwitchButton = (RotateImageButton) this.mGet.findViewById(R.id.mode_switch);
            }
            this.m3DSwitchButton.setEnabled(enable);
        }
    }

    public void startRotation(int degree, boolean animation) {
        if (this.mGet.getCameraDimension() != 1 || (degree != 90 && degree != Tag.IMAGE_DESCRIPTION)) {
            super.startRotation(degree, animation);
            this.mGet.setDegree(R.id.mode_switch, degree, animation);
        }
    }

    public void set3DSwitchVisible(boolean visible) {
        if (visible) {
            this.m3DSwitchButton.setVisibility(0);
        } else {
            this.m3DSwitchButton.setVisibility(8);
        }
    }

    public void set3DSwitchImage() {
        if (this.m3DSwitchButton != null) {
            if (this.mGet.getCameraDimension() == 0) {
                this.m3DSwitchButton.setImageResource(R.drawable.selector_switch_2d_btn);
            } else {
                this.m3DSwitchButton.setImageResource(R.drawable.selector_switch_3d_btn);
            }
        }
    }

    public void onResume() {
        if (!(this.mGet.getSubMenuMode() == 5 || this.mGet.getSubMenuMode() == 16 || !ModelProperties.is3dSupportedModel())) {
            set3DSwitchVisible(true);
        }
        super.onResume();
    }
}
