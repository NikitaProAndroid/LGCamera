package com.lge.camera.controller.camera;

import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.components.RotateImageView;
import com.lge.camera.controller.Controller;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.Util;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.BaseEngine;
import com.lge.voiceshutter.library.LGKeyRec;

public class SmartModeIndicatorController extends Controller {
    public SmartModeIndicatorController(ControllerFunction function) {
        super(function);
    }

    public void initController() {
        if (FunctionProperties.isSupportSmartMode()) {
            if (!this.mInit) {
                this.mGet.inflateStub(R.id.stub_smartmode_indicator);
            }
            this.mInit = true;
            if (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE))) {
                this.mGet.findViewById(R.id.smart_mode_indicator).setVisibility(0);
            } else {
                this.mGet.findViewById(R.id.smart_mode_indicator).setVisibility(4);
            }
            setIndicatorResources();
            setSmartModeIndicator(15, false, false);
            if (this.mGet.getApplicationMode() == 0) {
                startRotation(this.mGet.getOrientationDegree(), false);
            }
        }
    }

    public void setVisibleOfSmartModeIndicator(boolean show) {
        if (this.mInit) {
            this.mGet.findViewById(R.id.smart_mode_indicator).setVisibility(show ? 0 : 4);
        }
    }

    public void updateSmartModeIndicator(int sceneMode, boolean standUpdate, boolean stabilizeUpdate) {
        if (this.mInit) {
            Util.startAlphaAnimation(this.mGet.findViewById(R.id.smart_mode_indicator), 1, 0, CameraConstants.GALLERY_QUICKVIEW_ANI_INTERVAL, null);
            setSmartModeIndicator(sceneMode, standUpdate, stabilizeUpdate);
            Util.startAlphaAnimation(this.mGet.findViewById(R.id.smart_mode_indicator), 0, 1, CameraConstants.GALLERY_QUICKVIEW_ANI_INTERVAL, null);
        }
    }

    public void setSmartModeIndicator(int sceneMode, boolean standUpdate, boolean stabilizeUpdate) {
        if (this.mInit) {
            updateSceneIndicator(sceneMode);
            updateTriPODIndicator(standUpdate);
            updateStabilizationByHightISOIndicator(stabilizeUpdate);
        }
    }

    private void updateSceneIndicator(int sceneMode) {
        if (this.mInit) {
            RotateImageView iconView = (RotateImageView) this.mGet.findViewById(R.id.icon_smart_scene_mode);
            if (iconView != null) {
                switch (sceneMode) {
                    case LGKeyRec.EVENT_INVALID /*0*/:
                        iconView.setImageResource(R.drawable.icon_smart_scenery);
                    case LGKeyRec.EVENT_NO_MATCH /*1*/:
                        iconView.setImageResource(R.drawable.icon_smart_portrait);
                    case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                        iconView.setImageResource(R.drawable.icon_smart_night_scenery);
                    case LGKeyRec.EVENT_STARTED /*3*/:
                        iconView.setImageResource(R.drawable.icon_smart_night_portrait);
                    case LGKeyRec.EVENT_STOPPED /*4*/:
                        iconView.setImageResource(R.drawable.icon_smart_macro);
                    case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                        iconView.setImageResource(R.drawable.icon_smart_portrait_backlight);
                    case LGKeyRec.EVENT_NEED_MORE_AUDIO /*8*/:
                        iconView.setImageResource(R.drawable.icon_smart_twilight);
                    case BaseEngine.DEFAULT_PRIORITY /*10*/:
                        iconView.setImageResource(R.drawable.icon_smart_indoor);
                    case Ola_ShotParam.ImageEffect_Vivid /*14*/:
                        iconView.setImageResource(R.drawable.icon_smart_scenery_back_light);
                    case Ola_ShotParam.ImageEffect_AutoWB /*15*/:
                        iconView.setImageResource(R.drawable.icon_smart_scenery);
                    default:
                }
            }
        }
    }

    private void updateTriPODIndicator(boolean show) {
        if (this.mInit) {
            RotateImageView iconView = (RotateImageView) this.mGet.findViewById(R.id.icon_smart_stand_mode);
            if (iconView == null) {
                return;
            }
            if (show) {
                iconView.setVisibility(0);
            } else {
                iconView.setVisibility(4);
            }
        }
    }

    private void updateStabilizationByHightISOIndicator(boolean show) {
        if (this.mInit) {
            RotateImageView iconView = (RotateImageView) this.mGet.findViewById(R.id.icon_smart_stabilization_mode);
            if (iconView == null) {
                return;
            }
            if (show) {
                iconView.setVisibility(0);
            } else {
                iconView.setVisibility(4);
            }
        }
    }

    private void setIndicatorResources() {
        if (this.mInit) {
            RotateImageView iconView = (RotateImageView) this.mGet.findViewById(R.id.icon_smart_stand_mode);
            if (iconView != null) {
                iconView.setImageResource(R.drawable.icon_smart_stand_tripod);
            }
            iconView = (RotateImageView) this.mGet.findViewById(R.id.icon_smart_stabilization_mode);
            if (iconView != null) {
                iconView.setImageResource(R.drawable.icon_smart_stabilization);
            }
        }
    }

    public void startRotation(int degree, boolean animation) {
        if (this.mInit) {
            this.mGet.setDegree(R.id.icon_smart_scene_mode, degree, R.id.smart_mode_indicator, animation);
            this.mGet.setDegree(R.id.icon_smart_stand_mode, degree, R.id.smart_mode_indicator, animation);
            this.mGet.setDegree(R.id.icon_smart_stabilization_mode, degree, R.id.smart_mode_indicator, animation);
        }
    }
}
