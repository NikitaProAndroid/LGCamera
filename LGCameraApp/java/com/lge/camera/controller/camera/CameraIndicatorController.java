package com.lge.camera.controller.camera;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.components.RotateImageView;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.controller.IndicatorController;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.TelephonyUtil;
import com.lge.camera.util.Util;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.library.FaceDetector;

public class CameraIndicatorController extends IndicatorController {
    private long mPicturesRemaining;

    public CameraIndicatorController(ControllerFunction function) {
        super(function);
        this.mPicturesRemaining = 0;
    }

    public void initController() {
        this.mGet.inflateStub(R.id.stub_camera_indicator);
        this.mGet.findViewById(R.id.camera_indicator).setVisibility(4);
        this.mInit = true;
        updateRemainIndicator();
        updateGpsIndicator();
        updateFlashIndicator(false, null);
        updateStorageIndicator();
        updateVoiceIndicator(false);
        updateTimerIndicator();
        super.initController();
        if (this.mGet.getApplicationMode() == 0) {
            int degree = this.mGet.getOrientationDegree();
            this.mGet.setDegree(R.id.icon_flash, degree, R.id.camera_indicator, false);
            this.mGet.setDegree(R.id.icon_geo_tag, degree, R.id.camera_indicator, false);
            this.mGet.setDegree(R.id.icon_storage, degree, R.id.camera_indicator, false);
            this.mGet.setDegree(R.id.icon_battery, degree, R.id.camera_indicator, false);
            this.mGet.setDegree(R.id.icon_voice_shutter, degree, R.id.camera_indicator, false);
            this.mGet.setDegree(R.id.icon_message, degree, R.id.camera_indicator, false);
            this.mGet.setDegree(R.id.icon_timer, degree, R.id.camera_indicator, false);
            this.mGet.setDegree(R.id.icon_vvm_message, degree, R.id.camera_indicator, false);
            startRotation(degree, false);
        }
    }

    public void showIndicator() {
        if (Common.isQuickWindowCameraMode()) {
            this.mGet.findViewById(R.id.camera_indicator).setVisibility(4);
        } else if ((!ProjectVariables.isSupportClearView() && !ProjectVariables.isSupportKDDICleanView()) || !this.mGet.isClearView()) {
            this.mGet.findViewById(R.id.camera_indicator).setVisibility(0);
        }
    }

    public void hideIndicator() {
        if ((!ProjectVariables.isSupportClearView() && !ProjectVariables.isSupportKDDICleanView()) || !this.mGet.isClearView()) {
            this.mGet.findViewById(R.id.camera_indicator).setVisibility(4);
        }
    }

    public void slideIndicatorIn(boolean useAnimation) {
        if (ProjectVariables.isSupportClearView() || ProjectVariables.isSupportKDDICleanView()) {
            setVisibleIndicatorView(R.id.icon_geo_tag, true, useAnimation);
            setVisibleIndicatorView(R.id.icon_storage, true, useAnimation);
            setVisibleIndicatorView(R.id.icon_timer, true, true);
            setVisibleIndicatorView(R.id.camera_mode_indicator_text, true, useAnimation);
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
            setVisibleIndicatorView(R.id.icon_geo_tag, false, useAnimation);
            setVisibleIndicatorView(R.id.icon_storage, false, useAnimation);
            setVisibleIndicatorView(R.id.icon_timer, false, true);
            setVisibleIndicatorView(R.id.camera_mode_indicator_text, false, useAnimation);
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
            RotateImageView icon = (RotateImageView) this.mGet.findViewById(R.id.icon_resolution);
            if (icon != null) {
                ListPreference pref = this.mGet.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
                if (pref == null) {
                    CamLog.d(FaceDetector.TAG, "pref is Null");
                    return;
                }
                if (this.mGet.getCameraId() == 1) {
                    icon.setImageResource(pref.getIndicatorIconResource());
                } else {
                    String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
                    if (CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_HDR.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_REFOCUS.equals(shotMode) || this.mGet.isTimeMachineModeOn()) {
                        icon.setImageResource(pref.getIndicatorIconResource());
                    } else if (shotMode.equals(CameraConstants.TYPE_SHOTMODE_CONTINUOUS) || shotMode.equals(CameraConstants.TYPE_SHOTMODE_PANORAMA) || shotMode.equals(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) || shotMode.equals(CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
                        setContinuousAndPanoramaSizeIcon(icon, pref);
                    }
                }
            }
        }
    }

    private void setContinuousAndPanoramaSizeIcon(RotateImageView icon, ListPreference pref) {
        if (pref.getIndicatorIconResource() == 0) {
            return;
        }
        if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
            icon.setImageResource(R.drawable.cam_icon_line3_1);
        } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            icon.setImageResource(R.drawable.cam_icon_line3_12);
        } else {
            ListPreference shotModePref = this.mGet.findPreference(Setting.KEY_CAMERA_SHOT_MODE);
            if (shotModePref != null) {
                String extraInfo = shotModePref.getExtraInfo();
                if ("1536x864".equals(extraInfo) || "1296x864".equals(extraInfo) || "1280x768".equals(extraInfo)) {
                    icon.setImageResource(R.drawable.cam_icon_line0_17);
                    return;
                } else if (MultimediaProperties.EFFECTS_ENFORCED_SIZE_FOR_UVGA.equals(extraInfo)) {
                    icon.setImageResource(R.drawable.cam_icon_line0_05);
                    return;
                } else if ("800x480".equals(extraInfo)) {
                    icon.setImageResource(R.drawable.cam_icon_line0_22);
                    return;
                } else if ("640x480".equals(extraInfo)) {
                    icon.setImageResource(R.drawable.cam_icon_line0_06);
                    return;
                } else {
                    icon.setImageResource(R.drawable.cam_icon_line3_6);
                    return;
                }
            }
            icon.setImageResource(R.drawable.cam_icon_line3_6);
        }
    }

    public void updateFocusIndicator() {
        if (this.mInit) {
            RotateImageView icon = (RotateImageView) this.mGet.findViewById(R.id.icon_focus);
            if (icon != null) {
                if (this.mGet.getCameraMode() == 1) {
                    icon.setVisibility(8);
                } else {
                    setVisibleIndicatorView(R.id.icon_focus, true, false);
                }
                ListPreference pref = this.mGet.findPreference(Setting.KEY_FOCUS);
                if (pref != null) {
                    icon.setImageResource(pref.getIndicatorIconResource());
                }
            }
        }
    }

    public void updateSceneIndicator(boolean useLocalSetting, String value) {
        if (this.mInit) {
            RotateImageView icon = (RotateImageView) this.mGet.findViewById(R.id.icon_scene_mode);
            if (icon != null) {
                ListPreference pref = this.mGet.findPreference(Setting.KEY_SCENE_MODE);
                if (pref != null) {
                    if (!useLocalSetting || value == null) {
                        icon.setImageResource(pref.getIndicatorIconResource());
                    } else {
                        icon.setImageResource(pref.getIndicatorIconResources()[pref.findIndexOfValue(value)]);
                    }
                    setVisibleIndicatorView(R.id.icon_scene_mode, true, false);
                    return;
                }
                icon.setVisibility(4);
            }
        }
    }

    public void updateTimerIndicator() {
        if (this.mInit) {
            RotateImageView icon = (RotateImageView) this.mGet.findViewById(R.id.icon_timer);
            if (icon != null) {
                ListPreference pref = this.mGet.findPreference(Setting.KEY_CAMERA_TIMER);
                if (pref != null) {
                    icon.setImageResource(pref.getIndicatorIconResource());
                    setVisibleIndicatorView(R.id.icon_timer, true, false);
                    setContentDescription((View) icon, pref);
                    return;
                }
                icon.setVisibility(4);
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

    public void startRotation(int degree, boolean animation) {
        super.startRotation(degree, animation);
        if (ModelProperties.isUS()) {
            rotateRemainTextIndicator(degree);
        }
    }

    public void rotateRemainTextIndicator(int degree) {
        View remainTextLayout = this.mGet.findViewById(R.id.camera_text_remain_layout);
        RotateLayout remainTextLayoutRotate = (RotateLayout) this.mGet.findViewById(R.id.camera_text_remain_layout_rotate);
        if (remainTextLayout != null && remainTextLayoutRotate != null) {
            remainTextLayoutRotate.setLayoutDirection(0);
            remainTextLayoutRotate.rotateLayout(degree);
            int marginTop = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.layout_preview_indicators_sub_marginTop);
            int previewMargin = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.layout_preview_marginLeft);
            LayoutParams params = (LayoutParams) remainTextLayout.getLayoutParams();
            Common.resetLayoutParameter(params);
            if (this.mGet.isConfigureLandscape()) {
                params.addRule(10, 1);
                params.addRule(20, 1);
                params.topMargin = marginTop;
                params.rightMargin = 0;
                if (Util.isEqualDegree(this.mGet.getResources(), this.mGet.getOrientationDegree(), 0) || Util.isEqualDegree(this.mGet.getResources(), this.mGet.getOrientationDegree(), MediaProviderUtils.ROTATION_180)) {
                    params.leftMargin = 0;
                } else {
                    if (this.mLeftMargin > 0) {
                        previewMargin = 0;
                    }
                    params.leftMargin = previewMargin;
                }
            } else {
                params.addRule(10, 1);
                params.addRule(21, 1);
                params.leftMargin = 0;
                params.rightMargin = marginTop;
                if (Util.isEqualDegree(this.mGet.getResources(), this.mGet.getOrientationDegree(), 0) || Util.isEqualDegree(this.mGet.getResources(), this.mGet.getOrientationDegree(), MediaProviderUtils.ROTATION_180)) {
                    params.topMargin = 0;
                } else {
                    if (this.mLeftMargin > 0) {
                        previewMargin = 0;
                    }
                    params.topMargin = previewMargin;
                }
            }
            remainTextLayout.setLayoutDirection(0);
            remainTextLayout.setLayoutParams(params);
        }
    }

    public void updateRemainIndicator() {
        if (this.mInit && this.mGet.isStorageControllerInitialized()) {
            TextView remainIndicator = (TextView) this.mGet.findViewById(R.id.icon_text_remain);
            View icon = this.mGet.findViewById(R.id.icon_text_remain_camera);
            if (remainIndicator == null) {
                return;
            }
            if (!ModelProperties.isUS() || isSmartModeOn()) {
                remainIndicator.setVisibility(8);
                icon.setVisibility(8);
                return;
            }
            String remainString;
            if (this.mPicturesRemaining > 99999999999L) {
                String str = "99999999999+";
            }
            if (this.mPicturesRemaining < 0) {
                remainString = "";
                icon.setVisibility(4);
            } else {
                remainString = String.format("%d", new Object[]{Long.valueOf(this.mPicturesRemaining)});
                if (ProjectVariables.showCapturedImageCountInRemainIndicator()) {
                    remainString = calculateTakenPictures() + "/" + remainString;
                }
                remainIndicator.setVisibility(0);
                icon.setVisibility(0);
            }
            CharSequence contentDescripton = String.format(this.mGet.getString(R.string.camera_accessibility_indicator_remaining_picture_count), new Object[]{remainString});
            remainIndicator.setText(remainString);
            setContentDescription((View) remainIndicator, contentDescripton);
            return;
        }
        CamLog.d(FaceDetector.TAG, String.format("updateRemainIndicator return: not initialized", new Object[0]));
    }

    public void updateVoiceIndicator(boolean recog) {
        if (this.mInit) {
            RotateImageView icon = (RotateImageView) this.mGet.findViewById(R.id.icon_voice_shutter);
            if (icon == null) {
                return;
            }
            if (FunctionProperties.isVoiceShutter()) {
                ListPreference pref = this.mGet.findPreference(Setting.KEY_VOICESHUTTER);
                if (pref != null) {
                    if (!pref.getValue().equals(CameraConstants.SMART_MODE_ON) || TelephonyUtil.phoneInCall(this.mGet.getApplicationContext())) {
                        icon.setImageResource(R.drawable.cam_icon_empty);
                        icon.setVisibility(4);
                    } else {
                        if (recog) {
                            icon.setImageResource(R.drawable.cam_icon_voice_shutter_recog);
                        } else {
                            icon.setImageResource(R.drawable.cam_icon_voice_shutter_normal);
                        }
                        icon.setVisibility(0);
                    }
                    String string = this.mGet.getString(R.string.camera_accessibility_indicator_voice_shutter_on);
                    Object[] objArr = new Object[1];
                    objArr[0] = FunctionProperties.useCheeseShutterTitle() ? this.mGet.getString(R.string.sp_cheeseshutter_NORMAL) : pref.getTitle();
                    setContentDescription((View) icon, String.format(string, objArr));
                    return;
                }
                icon.setImageResource(R.drawable.cam_icon_voice_shutter_normal);
                icon.setVisibility(8);
                return;
            }
            icon.setVisibility(8);
        }
    }

    private int calculateTakenPictures() {
        int result = 0;
        Cursor cursor = null;
        try {
            cursor = this.mGet.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "bucket_id=" + this.mGet.getStorageBucketId(), null, null);
            if (cursor != null) {
                result = cursor.getCount();
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (SQLiteException e) {
            CamLog.e(FaceDetector.TAG, "cursor error ", e);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public void setIndicatorForSmartMode(boolean show, boolean animation) {
        boolean z;
        boolean z2 = true;
        if (isSmartModeOn()) {
            show = true;
        }
        setVisibleIndicatorView(R.id.icon_flash, !show, animation);
        if (show) {
            z = false;
        } else {
            z = true;
        }
        setVisibleIndicatorView(R.id.icon_geo_tag, z, animation);
        if (ModelProperties.isUS()) {
            if (show) {
                z = false;
            } else {
                z = true;
            }
            setVisibleIndicatorView(R.id.icon_text_remain_camera, z, animation);
            if (show) {
                z = false;
            } else {
                z = true;
            }
            setVisibleIndicatorView(R.id.icon_text_remain, z, animation);
        }
        if (show) {
            z2 = false;
        }
        setVisibleIndicatorView(R.id.icon_timer, z2, animation);
    }

    public void setPicturesRemaining(long remain) {
        this.mPicturesRemaining = remain;
    }

    public long getPicturesRemaining() {
        return this.mPicturesRemaining;
    }

    protected boolean isSmartModeOn() {
        if (FunctionProperties.isSupportSmartMode() && this.mGet.getApplicationMode() == 0 && this.mGet.getCameraId() == 0 && CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE))) {
            return true;
        }
        return false;
    }

    protected View getLayout() {
        return this.mGet.findViewById(R.id.camera_indicator);
    }

    protected void invalidateParentLayout() {
        this.mGet.findViewById(R.id.camera_indicator).invalidate();
    }

    public void updateModeMenuIndicator() {
        if (this.mGet.findPreference(Setting.KEY_CAMERA_SHOT_MODE) == null) {
            super.updateModeMenuIndicator("");
        } else {
            super.updateModeMenuIndicator();
        }
    }
}
