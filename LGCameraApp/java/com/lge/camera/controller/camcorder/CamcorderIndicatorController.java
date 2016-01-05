package com.lge.camera.controller.camcorder;

import android.view.View;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.components.RotateImageView;
import com.lge.camera.controller.IndicatorController;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class CamcorderIndicatorController extends IndicatorController {
    public CamcorderIndicatorController(ControllerFunction function) {
        super(function);
    }

    public void initController() {
        this.mGet.inflateStub(R.id.stub_camcorder_indicator);
        this.mGet.findViewById(R.id.camcorder_indicator).setVisibility(4);
        this.mInit = true;
        updateGpsIndicator();
        updateStabilizationIndicator();
        updateFlashIndicator(false, null);
        updateStorageIndicator();
        super.initController();
        if (this.mGet.isCamcorderRotation(true)) {
            int degree = this.mGet.getOrientationDegree();
            this.mGet.setDegree(R.id.icon_flash, degree, R.id.camcorder_indicator, false);
            this.mGet.setDegree(R.id.icon_geo_tag, degree, R.id.camcorder_indicator, false);
            this.mGet.setDegree(R.id.icon_stabilization, degree, R.id.camcorder_indicator, false);
            this.mGet.setDegree(R.id.icon_storage, degree, R.id.camcorder_indicator, false);
            this.mGet.setDegree(R.id.icon_voice_mail, degree, R.id.camcorder_indicator, false);
            this.mGet.setDegree(R.id.icon_battery, degree, R.id.camcorder_indicator, false);
            this.mGet.setDegree(R.id.icon_message, degree, R.id.camcorder_indicator, false);
            this.mGet.setDegree(R.id.icon_vvm_message, degree, R.id.camcorder_indicator, false);
        }
    }

    public void showIndicator() {
        if ((!ProjectVariables.isSupportClearView() && !ProjectVariables.isSupportKDDICleanView()) || !this.mGet.isClearView()) {
            this.mGet.findViewById(R.id.camcorder_indicator).setVisibility(0);
        }
    }

    public void hideIndicator() {
        if ((!ProjectVariables.isSupportClearView() && !ProjectVariables.isSupportKDDICleanView()) || !this.mGet.isClearView()) {
            this.mGet.findViewById(R.id.camcorder_indicator).setVisibility(4);
        }
    }

    public void slideIndicatorIn(boolean useAnimation) {
        if (ProjectVariables.isSupportClearView() || ProjectVariables.isSupportKDDICleanView()) {
            setVisibleIndicatorView(R.id.camera_mode_indicator_text, true, useAnimation);
            setVisibleIndicatorView(R.id.icon_storage, true, useAnimation);
            this.mGet.findViewById(R.id.icon_voice_mail).setVisibility(0);
            if (this.mBattLevel > 30) {
                this.mGet.findViewById(R.id.icon_battery).setVisibility(8);
            } else if (ProjectVariables.isSupportKDDICleanView()) {
                setVisibleIndicatorView(R.id.icon_battery, true, useAnimation);
            } else {
                this.mGet.findViewById(R.id.icon_battery).setVisibility(0);
            }
        }
    }

    public void slideIndicatorOut(boolean useAnimation) {
        if (ProjectVariables.isSupportClearView() || ProjectVariables.isSupportKDDICleanView()) {
            setVisibleIndicatorView(R.id.camera_mode_indicator_text, false, useAnimation);
            setVisibleIndicatorView(R.id.icon_storage, false, useAnimation);
            this.mGet.findViewById(R.id.icon_voice_mail).setVisibility(0);
            if (this.mBattLevel > 30) {
                this.mGet.findViewById(R.id.icon_battery).setVisibility(8);
            } else if (ProjectVariables.isSupportKDDICleanView()) {
                setVisibleIndicatorView(R.id.icon_battery, false, useAnimation);
            } else {
                this.mGet.findViewById(R.id.icon_battery).setVisibility(0);
            }
        }
    }

    public void updateSizeIndicator() {
        if (this.mInit) {
            RotateImageView icon = (RotateImageView) this.mGet.findViewById(R.id.icon_video_size);
            ListPreference pref = this.mGet.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            if (pref != null) {
                icon.setImageResource(pref.getIndicatorIconResource());
                setContentDescription((View) icon, pref);
            }
        }
    }

    public void updateAudioIndicator() {
        CamLog.d(FaceDetector.TAG, "updateAudioIndicator : " + this.mInit);
        if (this.mInit) {
            RotateImageView icon = (RotateImageView) this.mGet.findViewById(R.id.icon_voice);
            ListPreference pref = this.mGet.findPreference(Setting.KEY_VIDEO_AUDIO_RECORDING);
            if (pref != null) {
                icon.setImageResource(pref.getIndicatorIconResource());
                setContentDescription((View) icon, pref);
            }
        }
    }

    public void updateFlashIndicator(boolean useLocalSetting, String value) {
        if (this.mInit) {
            RotateImageView icon = (RotateImageView) this.mGet.findViewById(R.id.icon_flash);
            if (icon != null) {
                icon.setVisibility(8);
            }
        }
    }

    public void updateStabilizationIndicator() {
        if (this.mInit) {
            RotateImageView icon = (RotateImageView) this.mGet.findViewById(R.id.icon_stabilization);
            if (icon != null) {
                ListPreference pref = this.mGet.findPreference(Setting.KEY_VIDEO_STABILIZATION);
                if (pref != null) {
                    icon.setImageResource(pref.getIndicatorIconResource());
                    CharSequence contentDescription = null;
                    if (pref.getIndicatorIconResource() != R.drawable.cam_icon_empty) {
                        contentDescription = this.mGet.getString(R.string.camera_accessibility_indicator_anti_shaking_on);
                    }
                    setContentDescription((View) icon, contentDescription);
                }
            }
        }
    }

    protected View getLayout() {
        return this.mGet.findViewById(R.id.camcorder_indicator);
    }

    protected void invalidateParentLayout() {
        this.mGet.findViewById(R.id.camcorder_indicator).invalidate();
    }

    public void updateModeMenuIndicator() {
        if (this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE) == null) {
            super.updateModeMenuIndicator("");
        } else {
            super.updateModeMenuIndicator();
        }
    }
}
