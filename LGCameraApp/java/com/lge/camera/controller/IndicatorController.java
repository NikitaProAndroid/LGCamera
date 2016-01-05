package com.lge.camera.controller;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.components.RotateImageView;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.properties.StorageProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.Util;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.library.FaceDetector;

public abstract class IndicatorController extends Controller {
    protected static final int MAX_LEVEL = 100;
    private static int[] mBatteryIconResources;
    protected int mBattLevel;
    protected int mLeftMargin;

    protected abstract View getLayout();

    public abstract void hideIndicator();

    public abstract void showIndicator();

    public abstract void updateFlashIndicator(boolean z, String str);

    public abstract void updateSizeIndicator();

    public IndicatorController(ControllerFunction function) {
        super(function);
        this.mBattLevel = MAX_LEVEL;
        this.mLeftMargin = 0;
    }

    public void initController() {
        this.mInit = true;
        setMessageIndicatorReceived(this.mGet.getRecentMessageType(), false);
        setVoiceMailIndicator(getNewVoiceMailIcon());
        setBatteryVisibility(MAX_LEVEL);
        setBatteryIndicator(this.mGet.getBatteryLevel());
        showIndicator();
    }

    protected void invalidateParentLayout() {
    }

    public void setMessageIndicatorReceived(int msgReceived, boolean isReadAllMsg) {
        if (this.mInit && ProjectVariables.displayMessageIndicator() && !ModelProperties.isWifiOnlyModel(this.mGet.getApplicationContext())) {
            View icon = (RotateImageView) this.mGet.findViewById(R.id.icon_message);
            if (isReadAllMsg) {
                icon.setImageResource(R.drawable.cam_icon_empty);
                setContentDescription(icon);
            } else if (msgReceived == 0) {
                icon.setImageResource(R.drawable.cam_icon_empty);
                setContentDescription(icon);
            } else if (msgReceived == 1) {
                icon.setImageResource(R.drawable.cam_icon_line7_1);
                setContentDescription(icon, this.mGet.getString(R.string.camera_accessibility_indicator_message_remaining));
            } else if (msgReceived == 2) {
                icon.setImageResource(R.drawable.cam_icon_line7_3);
                setContentDescription(icon, this.mGet.getString(R.string.camera_accessibility_indicator_mms_remaining));
            }
            invalidateParentLayout();
        }
    }

    public void setVoiceMailIndicator(int msgReceived) {
        if (this.mInit && ProjectVariables.displayVisualVoiceMailIndicator()) {
            View icon = (RotateImageView) this.mGet.findViewById(R.id.icon_vvm_message);
            if (msgReceived == 1) {
                icon.setImageResource(R.drawable.cam_icon_indicator_vvm);
                setContentDescription(icon, this.mGet.getString(R.string.camera_accessibility_indicator_voice_mail_remaining));
            } else if (msgReceived == 0) {
                icon.setImageResource(R.drawable.cam_icon_empty);
                setContentDescription(icon);
            }
            invalidateParentLayout();
        }
    }

    static {
        mBatteryIconResources = new int[]{R.drawable.cam_icon_line8_01, R.drawable.cam_icon_line8_02, R.drawable.cam_icon_line8_03, R.drawable.cam_icon_line8_04, R.drawable.cam_icon_line8_05, R.drawable.cam_icon_line8_06, R.drawable.cam_icon_line8_07, R.drawable.cam_icon_line8_08, R.drawable.cam_icon_line8_09, R.drawable.cam_icon_line8_10, R.drawable.cam_icon_line8_11, R.drawable.cam_icon_line8_12, R.drawable.cam_icon_line8_13, R.drawable.cam_icon_line8_14, R.drawable.cam_icon_line8_15, R.drawable.cam_icon_line8_16, R.drawable.cam_icon_line8_17, R.drawable.cam_icon_line8_18, R.drawable.cam_icon_line8_19, R.drawable.cam_icon_line8_20, R.drawable.cam_icon_line8_21, R.drawable.cam_icon_line8_22, R.drawable.cam_icon_line8_23, R.drawable.cam_icon_line8_24, R.drawable.cam_icon_line8_25, R.drawable.cam_icon_line8_26, R.drawable.cam_icon_line8_27, R.drawable.cam_icon_line8_28, R.drawable.cam_icon_line8_29, R.drawable.cam_icon_line8_30, R.drawable.cam_icon_line8_31, R.drawable.cam_icon_line8_32, R.drawable.cam_icon_line8_33, R.drawable.cam_icon_line8_34, R.drawable.cam_icon_line8_35, R.drawable.cam_icon_line8_36, R.drawable.cam_icon_line8_37, R.drawable.cam_icon_line8_38, R.drawable.cam_icon_line8_39, R.drawable.cam_icon_line8_40, R.drawable.cam_icon_line8_41, R.drawable.cam_icon_line8_42, R.drawable.cam_icon_line8_43, R.drawable.cam_icon_line8_44, R.drawable.cam_icon_line8_45, R.drawable.cam_icon_line8_46, R.drawable.cam_icon_line8_47, R.drawable.cam_icon_line8_48, R.drawable.cam_icon_line8_49, R.drawable.cam_icon_line8_50, R.drawable.cam_icon_line8_51, R.drawable.cam_icon_line8_52, R.drawable.cam_icon_line8_53, R.drawable.cam_icon_line8_54, R.drawable.cam_icon_line8_55, R.drawable.cam_icon_line8_56, R.drawable.cam_icon_line8_57, R.drawable.cam_icon_line8_58, R.drawable.cam_icon_line8_59, R.drawable.cam_icon_line8_60, R.drawable.cam_icon_line8_61, R.drawable.cam_icon_line8_62, R.drawable.cam_icon_line8_63};
    }

    public void setBatteryIndicator(int batteryLevel) {
        if (this.mInit) {
            View icon = (RotateImageView) this.mGet.findViewById(R.id.icon_battery);
            if (batteryLevel >= mBatteryIconResources.length) {
                batteryLevel = mBatteryIconResources.length - 1;
            }
            icon.setImageResource(mBatteryIconResources[batteryLevel]);
            if (this.mBattLevel > 30 || this.mGet.isClearView()) {
                icon.setVisibility(8);
            }
            setContentDescription(icon, String.format(this.mGet.getString(R.string.camera_accessibility_indicator_battery), new Object[]{Integer.valueOf(this.mGet.getActualBatteryLevel())}));
        }
    }

    public void setBatteryVisibility(int batteryLevel) {
        if (this.mInit) {
            if (batteryLevel >= mBatteryIconResources.length) {
                batteryLevel = mBatteryIconResources.length - 1;
            }
            if (batteryLevel < 0) {
                batteryLevel = 0;
            }
            this.mBattLevel = batteryLevel;
            View icon = (RotateImageView) this.mGet.findViewById(R.id.icon_battery);
            if (batteryLevel > 30 || this.mGet.isClearView()) {
                icon.setVisibility(8);
            } else {
                icon.setVisibility(0);
                CamLog.d(FaceDetector.TAG, "batteryLevel = " + batteryLevel);
            }
            setContentDescription(icon, String.format(this.mGet.getString(R.string.camera_accessibility_indicator_battery), new Object[]{Integer.valueOf(this.mGet.getActualBatteryLevel())}));
        }
    }

    protected int getNewVoiceMailIcon() {
        if (ModelProperties.getCarrierCode() != 6) {
            return 0;
        }
        if (!this.mInit) {
            return 0;
        }
        int vvmCount = 0;
        Cursor cursor = null;
        try {
            Uri VvmInfoURI = Uri.parse("content://com.lge.provider.vvm/vvmInfo");
            String[] projection = new String[]{"_id", "date_time", "urgency", "private", "from_addrs", "heard_status", "mark_as_read"};
            ContentResolver cr = this.mGet.getContentResolver();
            if (cr == null) {
                if (cursor != null) {
                    cursor.close();
                }
                return 0;
            }
            CamLog.i("DataBaseApp", "Accessing data from VvmInfo Table");
            cursor = cr.query(VvmInfoURI, projection, "heard_status='N' AND mark_as_read='n'", null, null);
            if (cursor != null) {
                vvmCount = cursor.getCount();
                CamLog.i(FaceDetector.TAG, "unread vvm message = " + vvmCount);
            }
            if (cursor != null) {
                cursor.close();
            }
            if (vvmCount != 0) {
                CamLog.d(FaceDetector.TAG, "getNewVoiceMailIcon() return = 1");
                return 1;
            }
            CamLog.d(FaceDetector.TAG, "getNewVoiceMailIcon() return = 0");
            return 0;
        } catch (SQLiteException e) {
            e.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void updateStorageIndicator() {
        if (this.mInit) {
            View icon = (RotateImageView) this.mGet.findViewById(R.id.icon_storage);
            if (icon != null) {
                ListPreference pref = this.mGet.getSettingListPreference(Setting.KEY_STORAGE);
                if (pref != null) {
                    int iconIndex;
                    if (this.mGet.getCurrentStorage() == 1) {
                        iconIndex = pref.findIndexOfValue(StorageProperties.getEmmcName());
                    } else {
                        iconIndex = pref.findIndexOfValue(CameraConstants.NAME_EXTERNAL_MEMORY);
                    }
                    CharSequence contentDescription = null;
                    if (pref.getIndicatorIconResource() != R.drawable.cam_icon_empty) {
                        contentDescription = String.format(this.mGet.getString(R.string.camera_accessibility_indicator_storage), new Object[]{pref.getEntry()});
                    }
                    icon.setImageResource(pref.getIndicatorIconResources()[iconIndex]);
                    setContentDescription(icon, contentDescription);
                }
            }
        }
    }

    public void setModeMenuVisibility(int visible) {
        if (this.mInit) {
            View modeMenuLayout = this.mGet.findViewById(R.id.camera_mode_indicator_text_layout);
            if (modeMenuLayout != null) {
                modeMenuLayout.setVisibility(visible);
            }
        }
    }

    public void updateModeMenuIndicator(String title) {
        if (this.mInit && !Common.isQuickWindowCameraMode()) {
            rotateModeMenuIndicator(this.mGet.getOrientationDegree());
            setModeMenuVisibility(0);
            TextView curModeView = (TextView) this.mGet.findViewById(R.id.camera_mode_indicator_text);
            if (curModeView != null) {
                curModeView.setText(title);
            }
        }
    }

    public void updateModeMenuIndicator() {
        if (this.mInit && !Common.isQuickWindowCameraMode()) {
            rotateModeMenuIndicator(this.mGet.getOrientationDegree());
            setModeMenuVisibility(0);
            String title = (ModelProperties.getProjectCode() == 17 || ModelProperties.getProjectCode() == 33 || ModelProperties.getProjectCode() == 27 || ModelProperties.getProjectCode() == 30) ? "" : this.mGet.getCurrentSelectedTitle();
            TextView curModeView = (TextView) this.mGet.findViewById(R.id.camera_mode_indicator_text);
            if (curModeView != null) {
                curModeView.setText(title);
            }
        }
    }

    public void updateGpsIndicator() {
        if (this.mInit) {
            View icon = (RotateImageView) this.mGet.findViewById(R.id.icon_geo_tag);
            if (icon != null) {
                ListPreference pref = this.mGet.findPreference(Setting.KEY_CAMERA_TAG_LOCATION);
                if (pref != null) {
                    CharSequence contentDescription;
                    if (this.mGet.getCurrentLocation() == null) {
                        CamLog.d(FaceDetector.TAG, "Location info not available");
                        icon.setImageResource(pref.getIndicatorIconResource());
                        if (pref.getIndicatorIconResource() == R.drawable.cam_icon_empty) {
                            contentDescription = null;
                        } else {
                            contentDescription = this.mGet.getString(R.string.camera_accessibility_indicator_gps_connecting);
                        }
                    } else {
                        icon.setImageResource(R.drawable.cam_icon_line4_2);
                        contentDescription = this.mGet.getString(R.string.camera_accessibility_indicator_geotagging_on);
                    }
                    setContentDescription(icon, contentDescription);
                }
            }
        }
    }

    protected void setVisibleIndicatorView(int resId, boolean show, boolean animation) {
        int visible = 0;
        if (checkMediator()) {
            final View view = this.mGet.findViewById(resId);
            if (view != null) {
                view.clearAnimation();
                if (!animation) {
                    if (!show || isSmartModeOn()) {
                        visible = 4;
                    }
                    if ((ProjectVariables.isSupportClearView() || ProjectVariables.isSupportKDDICleanView()) && this.mGet.isClearView()) {
                        visible = 4;
                    }
                    view.setVisibility(visible);
                } else if (show) {
                    view.setVisibility(4);
                    Util.startAlphaAnimation(view, 0, 1, CameraConstants.GALLERY_QUICKVIEW_ANI_INTERVAL, new AnimationListener() {
                        public void onAnimationStart(Animation animation) {
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }

                        public void onAnimationEnd(Animation animation) {
                            if (view != null) {
                                view.setVisibility(0);
                                view.clearAnimation();
                            }
                        }
                    });
                } else {
                    view.setVisibility(4);
                    Util.startAlphaAnimation(view, 1, 0, CameraConstants.GALLERY_QUICKVIEW_ANI_INTERVAL, null);
                }
            }
        }
    }

    public void onResume() {
        if (this.mInit) {
            View indiLayout = getLayout();
            if (indiLayout != null) {
                indiLayout.setVisibility(0);
            }
            setMessageIndicatorReceived(this.mGet.getRecentMessageType(), false);
            setVoiceMailIndicator(getNewVoiceMailIcon());
            setBatteryIndicator(this.mGet.getBatteryLevel());
        }
        super.onResume();
    }

    public void onPause() {
        if (this.mInit) {
            View indiLayout = getLayout();
            if (indiLayout != null) {
                indiLayout.setVisibility(4);
            }
        }
    }

    protected boolean isSmartModeOn() {
        return false;
    }

    public long getPicturesRemaining() {
        return 0;
    }

    public void startRotation(int degree, boolean animation) {
        rotateModeMenuIndicator(degree);
    }

    protected void rotateModeMenuIndicator(int degree) {
        RotateLayout modeMenuLayoutRotate = (RotateLayout) this.mGet.findViewById(R.id.camera_mode_indicator_text_rotate_layout);
        View modeMenuLayout = this.mGet.findViewById(R.id.camera_mode_indicator_text_layout);
        TextView modeMenuText = (TextView) this.mGet.findViewById(R.id.camera_mode_indicator_text);
        if (modeMenuText != null && modeMenuLayout != null && modeMenuLayoutRotate != null && !Common.isQuickWindowCameraMode()) {
            modeMenuLayoutRotate.setLayoutDirection(0);
            modeMenuLayoutRotate.rotateLayout(degree);
            int marginLeft = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.quickfunction_layout_width);
            int marginTop = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.layout_preview_indicators_height);
            int paddingHorizon = Math.round(Util.dpToPx(this.mGet.getApplicationContext(), 6.0f));
            int paddingVertical = Math.round(Util.dpToPx(this.mGet.getApplicationContext(), CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK));
            View indiLayout = getLayout();
            if (indiLayout != null) {
                LayoutParams indiLayoutParams = (LayoutParams) indiLayout.getLayoutParams();
                if (Util.isConfigureLandscape(this.mGet.getResources())) {
                    if (indiLayoutParams.leftMargin > 0) {
                        marginLeft = 0;
                    }
                } else if (indiLayoutParams.topMargin > 0) {
                    marginLeft = 0;
                }
            }
            LayoutParams params = (LayoutParams) modeMenuLayout.getLayoutParams();
            Common.resetLayoutParameter(params);
            if (Util.isEqualDegree(this.mGet.getResources(), this.mGet.getOrientationDegree(), 0) || Util.isEqualDegree(this.mGet.getResources(), this.mGet.getOrientationDegree(), MediaProviderUtils.ROTATION_180)) {
                if (this.mGet.isConfigureLandscape()) {
                    params.addRule(10, 1);
                    params.addRule(20, 1);
                    params.leftMargin = 0;
                    params.topMargin = marginTop;
                    params.rightMargin = 0;
                    modeMenuLayout.setPaddingRelative(paddingHorizon, 0, paddingHorizon, 0);
                } else {
                    params.addRule(10, 1);
                    params.addRule(21, 1);
                    params.leftMargin = 0;
                    params.topMargin = 0;
                    params.rightMargin = marginTop;
                    modeMenuLayout.setPaddingRelative(0, paddingHorizon, 0, paddingHorizon);
                }
                if (Util.isEqualDegree(this.mGet.getResources(), this.mGet.getOrientationDegree(), MediaProviderUtils.ROTATION_180)) {
                    modeMenuText.setGravity(8388613);
                } else {
                    modeMenuText.setGravity(8388611);
                }
            } else {
                if (this.mGet.isConfigureLandscape()) {
                    params.addRule(15, 1);
                    params.addRule(20, 1);
                    params.leftMargin = marginLeft;
                    params.topMargin = 0;
                    params.rightMargin = 0;
                    modeMenuLayout.setPaddingRelative(0, paddingVertical, 0, paddingVertical);
                } else {
                    params.addRule(14, 1);
                    params.addRule(10, 1);
                    params.leftMargin = 0;
                    params.topMargin = marginLeft;
                    params.rightMargin = 0;
                    modeMenuLayout.setPaddingRelative(paddingVertical, 0, paddingVertical, 0);
                }
                modeMenuText.setGravity(1);
            }
            modeMenuLayout.setLayoutDirection(0);
            modeMenuLayout.setLayoutParams(params);
        }
    }

    public void setIndicatorLayout(int leftMargin) {
        View indiLayout = getLayout();
        if (indiLayout != null) {
            LayoutParams params = (LayoutParams) indiLayout.getLayoutParams();
            if (params != null) {
                int panelWidth = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_panel_width);
                if (ModelProperties.isSoftKeyNavigationBarModel()) {
                    panelWidth += Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_panel_marginBottom);
                }
                this.mLeftMargin = leftMargin;
                if (Util.isConfigureLandscape(this.mGet.getResources())) {
                    params.leftMargin = leftMargin;
                    params.rightMargin = panelWidth;
                    params.topMargin = 0;
                    params.bottomMargin = 0;
                } else {
                    params.leftMargin = 0;
                    params.rightMargin = 0;
                    params.topMargin = leftMargin;
                    params.bottomMargin = panelWidth;
                }
                indiLayout.setLayoutParams(params);
            }
        }
        rotateModeMenuIndicator(this.mGet.getOrientationDegree());
        rotateRemainTextIndicator(this.mGet.getOrientationDegree());
    }

    public void setContentDescription(View icon) {
        if (icon != null) {
            icon.setContentDescription(null);
        }
    }

    public void setContentDescription(View icon, ListPreference pref) {
        if (icon != null && pref != null) {
            if (icon.getVisibility() != 0 || pref.getIndicatorIconResource() == R.drawable.cam_icon_empty) {
                icon.setContentDescription(null);
            } else {
                icon.setContentDescription(pref.getTitle() + pref.getEntry());
            }
        }
    }

    public void setContentDescription(View icon, CharSequence description) {
        if (icon != null) {
            if (icon.getVisibility() != 0) {
                icon.setContentDescription(null);
            } else {
                icon.setContentDescription(description);
            }
        }
    }

    public void rotateRemainTextIndicator(int degree) {
    }

    public void setPicturesRemaining(long remain) {
    }

    public void updateRemainIndicator() {
    }

    public void updateVoiceIndicator(boolean recog) {
    }

    public void updateSceneIndicator(boolean useLocalSetting, String value) {
    }

    public void updateFocusIndicator() {
    }

    public void updateTimerIndicator() {
    }

    public void updateAudioIndicator() {
    }

    public void updateStabilizationIndicator() {
    }

    public void slideIndicatorIn(boolean useAnimation) {
    }

    public void slideIndicatorOut(boolean useAnimation) {
    }
}
