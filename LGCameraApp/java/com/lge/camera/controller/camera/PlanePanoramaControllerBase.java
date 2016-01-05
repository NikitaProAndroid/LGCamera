package com.lge.camera.controller.camera;

import android.app.Activity;
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.location.Location;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.RotateView;
import com.lge.camera.listeners.PlanePanoramaCallback;
import com.lge.camera.listeners.PlanePanoramaCallbackFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.Util;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.morpho.core.MorphoPanoramaGP;
import com.lge.morpho.core.MorphoPanoramaGP.InitParam;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.morpho.utils.multimedia.StillImageData;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;

public class PlanePanoramaControllerBase extends CameraController implements PlanePanoramaCallbackFunction {
    protected static final int ANIMATION_TIME = 300;
    public static final String DIR_SAVE_INPUT = "input";
    public static final String DIR_SAVE_PREVIEW = "preview_img";
    public static final String DIR_SAVE_STILL = "still_img";
    protected static final int FAR_THRESHOLD_HOR = 5;
    protected static final int FAR_THRESHOLD_VER = 5;
    public static final String FORMAT = "YVU420_SEMIPLANAR";
    public static final boolean IS_LOCK_AE = false;
    public static final boolean IS_LOCK_AW = false;
    public static final boolean IS_PREVIEW_INPUT;
    public static final boolean IS_SAVE_INPUT = false;
    public static final boolean IS_USE_AF = false;
    protected static final int MAX_DST_IMG_WIDTH = 30000;
    protected static final double MAX_IMAGE_ANGLE = 360.0d;
    protected static final int MOTIONLESS_THRESHOLD = 500;
    protected static final int PARAM_MULTIPLY_WIDTH = 10;
    public static final int PROGREE_ALPHA_BACK = 132;
    protected static final int PROGREE_ALPHA_FORE = 255;
    protected static final int USE_DISPLAY_CURRENT_PREVIEW = 0;
    public static final boolean USE_MULTI_THREAD = true;
    protected static final int USE_SENSOR_CORRECTION = 0;
    protected static final int USE_THRESHOLD = 10;
    protected final boolean COPY_EXIF_FROM_1ST_SHOOT;
    protected final int SINGLE_ARRAY;
    protected RelativeLayout mBackgroundPreviewLayout;
    protected ImageView mBar;
    protected RelativeLayout mBarLayout;
    protected View mBaseView;
    protected PlanePanoramaCallback mCallback;
    protected int mCntProcessd;
    protected int mCntReqShoot;
    protected int mCurOrientaionDegree;
    protected int[] mDirection;
    protected InitParam mInitParam;
    protected boolean mIsShooting;
    protected MorphoPanoramaGP mPanoramaGP;
    protected int mPictureH;
    protected int mPictureW;
    protected byte[] mPreviewBuff;
    protected int mPreviewH;
    protected ImageView mPreviewMini;
    protected RotateLayout mPreviewMiniLayout;
    protected RelativeLayout mPreviewMiniLayoutArrow;
    protected RelativeLayout mPreviewMiniLayoutOutline;
    protected int mPreviewW;
    protected ArrayList<Size> mPreview_size_list;
    protected boolean mRequestTakePicture;
    protected int mRoratePreview;
    protected int mRotateOutput;
    protected int mRotateUI;
    protected String mSaveInputDirPath;
    protected String mShootingDate;
    protected RelativeLayout mStartAndStopGuideTextLayout;
    protected int mStatusShot;
    protected ArrayList<Size> mStill_size_list;
    protected Object mSyncObj;
    protected Location mTempLocation;
    protected Parameters mTempParams;
    protected int mUseSensorThres;

    static {
        IS_PREVIEW_INPUT = FunctionProperties.isPlanePanoramaPreviewInput();
    }

    public PlanePanoramaControllerBase(ControllerFunction function) {
        super(function);
        this.COPY_EXIF_FROM_1ST_SHOOT = USE_MULTI_THREAD;
        this.mUseSensorThres = USE_SENSOR_CORRECTION;
        this.mStatusShot = USE_SENSOR_CORRECTION;
        this.mSyncObj = new Object();
        this.mIsShooting = IS_USE_AF;
        this.mRequestTakePicture = IS_USE_AF;
        this.mCntReqShoot = USE_SENSOR_CORRECTION;
        this.mCntProcessd = USE_SENSOR_CORRECTION;
        this.SINGLE_ARRAY = 1;
        this.mDirection = new int[1];
        this.mCurOrientaionDegree = USE_SENSOR_CORRECTION;
        this.mTempParams = null;
        this.mTempLocation = null;
        this.mCallback = null;
    }

    protected double getAngleOfViewDegree() {
        if (this.mGet.getCameraDevice() == null) {
            CamLog.w(FaceDetector.TAG, "Camera device is null.");
            return 0.0d;
        }
        Parameters parameters = this.mGet.getCameraDevice().getParameters();
        if (parameters == null) {
            CamLog.w(FaceDetector.TAG, "Parameters is null.");
            return 0.0d;
        }
        Size picSize = parameters.getPictureSize();
        double vaH = (double) parameters.getHorizontalViewAngle();
        double vaV = (((double) picSize.height) * vaH) / ((double) picSize.width);
        double value = Math.sqrt((vaH * vaH) + (vaV * vaV));
        CamLog.d(FaceDetector.TAG, "camera view angle (hor,ver)=" + vaH + "," + vaV + " angle of view degree = " + value);
        return value;
    }

    protected void setCafSetting() {
        if (FunctionProperties.isCafSupported(this.mGet.getApplicationMode(), this.mGet.getCameraId()) && this.mGet.getCameraDevice() != null) {
            LGParameters lgParameters = this.mGet.getLGParam();
            lgParameters.getParameters().setFocusMode("continuous-picture");
            lgParameters.setParameters(lgParameters.getParameters());
            CamLog.d(FaceDetector.TAG, "### setFocusMode-conti");
            if (ModelProperties.isRenesasISP()) {
                this.mGet.getCameraDevice().autoFocus(null);
            }
            if (ModelProperties.getProjectCode() == 8) {
                this.mGet.getCameraDevice().cancelAutoFocus();
            }
        }
    }

    protected String getDateString(long dateTaken) {
        return DateFormat.format("yyyy-MM-dd_kk-mm-ss", dateTaken).toString();
    }

    public int getStatus() {
        return this.mStatusShot;
    }

    protected void startAnimationAlphaShowing(final View view, boolean isVisible, boolean isForced) {
        final int visibility = isVisible ? USE_SENSOR_CORRECTION : 4;
        if (isForced || view.getVisibility() != visibility) {
            Animation anim;
            if (isVisible) {
                anim = new AlphaAnimation(0.0f, RotateView.DEFAULT_TEXT_SCALE_X);
            } else {
                anim = new AlphaAnimation(RotateView.DEFAULT_TEXT_SCALE_X, 0.0f);
            }
            anim.setDuration(300);
            anim.setAnimationListener(new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(visibility);
                }
            });
            if (view.getAnimation() != null && view.getAnimation().hasStarted()) {
                anim.setStartOffset(300);
            }
            view.startAnimation(anim);
        }
    }

    protected void startAnimationGuideArrowShowing(final View view, boolean isVisible, boolean isForced) {
        Animation anim;
        final int visibility = isVisible ? USE_SENSOR_CORRECTION : 4;
        if (!isForced) {
            if (view.getVisibility() == visibility) {
                return;
            }
            if (view.getAnimation() != null && view.getAnimation().hasStarted()) {
                return;
            }
        }
        if (isVisible) {
            anim = AnimationUtils.loadAnimation(this.mGet.getApplicationContext(), R.anim.free_panorama_arrow_show);
        } else {
            anim = AnimationUtils.loadAnimation(this.mGet.getApplicationContext(), R.anim.free_panorama_arrow_hide);
        }
        anim.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                view.setVisibility(visibility);
            }
        });
        view.startAnimation(anim);
    }

    public void hide() {
        if (this.mBarLayout != null) {
            this.mBarLayout.setVisibility(4);
        }
        if (this.mPreviewMini != null) {
            this.mPreviewMini.setImageDrawable(null);
        }
        if (this.mPreviewMiniLayoutOutline != null) {
            this.mPreviewMiniLayoutOutline.setVisibility(4);
        }
        if (this.mPreviewMiniLayout != null) {
            this.mPreviewMiniLayout.setVisibility(4);
        }
        if (this.mStartAndStopGuideTextLayout != null) {
            this.mStartAndStopGuideTextLayout.setVisibility(4);
        }
        if (this.mPreviewMiniLayoutArrow != null) {
            this.mPreviewMiniLayoutArrow.setVisibility(4);
        }
        if (this.mCallback != null) {
            this.mCallback.hideViews();
        }
        if (this.mBaseView != null) {
            this.mBaseView.setVisibility(4);
        }
    }

    public int[] getResultSize() {
        if (this.mPreviewW <= 0 || this.mPictureW <= 0) {
            ListPreference shotModePref = this.mGet.findPreference(Setting.KEY_CAMERA_SHOT_MODE);
            if (shotModePref != null) {
                int[] previewSize = Util.SizeString2WidthHeight(shotModePref.getExtraInfo());
                int[] pictureSize = Util.SizeString2WidthHeight(shotModePref.getExtraInfo3());
                this.mPreviewW = previewSize[USE_SENSOR_CORRECTION];
                this.mPreviewH = previewSize[1];
                this.mPictureW = pictureSize[USE_SENSOR_CORRECTION];
                this.mPictureH = pictureSize[1];
            }
        }
        CamLog.d(FaceDetector.TAG, "size (w,h) = " + ((IS_PREVIEW_INPUT ? this.mPreviewW : this.mPictureW) * USE_THRESHOLD) + ", " + (IS_PREVIEW_INPUT ? this.mPreviewH : this.mPictureH));
        return new int[]{(IS_PREVIEW_INPUT ? this.mPreviewW : this.mPictureW) * USE_THRESHOLD, IS_PREVIEW_INPUT ? this.mPreviewH : this.mPictureH};
    }

    public void startRotation(int degree, boolean animation) {
        if (this.mInit) {
            this.mCurOrientaionDegree = (((degree + 45) / 90) * 90) % CameraConstants.DEGREE_360;
            startRotateGuideText(this.mStartAndStopGuideTextLayout, this.mCurOrientaionDegree);
            if (this.mCallback != null) {
                this.mCallback.rotateGuide(degree);
            }
        }
    }

    public void reInitialize() {
        CamLog.d(FaceDetector.TAG, "reInitialize");
        this.mBaseView = null;
    }

    public void onDestroy() {
        this.mBaseView = null;
        super.onDestroy();
    }

    public Camera getCameraDevice() {
        return this.mGet.getCameraDevice();
    }

    public MorphoPanoramaGP getMorphoPanoramaGP() {
        return this.mPanoramaGP;
    }

    public int getState() {
        return this.mStatusShot;
    }

    public Activity getActivity() {
        return this.mGet.getActivity();
    }

    public Object getSyncObj() {
        return this.mSyncObj;
    }

    public void stopPanorama() {
    }

    public byte[] getPreviewBuff() {
        return this.mPreviewBuff;
    }

    public boolean isProcessingFinishTask() {
        return IS_USE_AF;
    }

    public void toastLong(String strString) {
        this.mGet.toastLong(strString);
    }

    public InitParam getInitParam() {
        return this.mInitParam;
    }

    public int getRoratePreview() {
        return this.mRoratePreview;
    }

    public int getRotateUI() {
        return this.mRotateUI;
    }

    public void setVisiblePreviewBar(boolean isVisible, boolean isForced) {
        startAnimationAlphaShowing(this.mBarLayout, isVisible, isForced);
    }

    public void setVisiblePreviewMini(boolean isVisible, boolean isForced) {
        startAnimationAlphaShowing(this.mPreviewMiniLayout, isVisible, isForced);
        startAnimationAlphaShowing(this.mPreviewMiniLayoutOutline, isVisible, isForced);
    }

    public void setVisibleArrowGuide(boolean isVisible, boolean isForced, boolean isBlicking) {
        if (this.mPreviewMiniLayoutArrow == null) {
            return;
        }
        if (isBlicking) {
            startAnimationGuideArrowShowing(this.mPreviewMiniLayoutArrow, isVisible, isForced);
        } else {
            startAnimationAlphaShowing(this.mPreviewMiniLayoutArrow, isVisible, isForced);
        }
    }

    public void setVisibleTakingGuide(boolean isVisible, boolean isForced) {
        if (isVisible) {
            showGuideText(USE_MULTI_THREAD, R.string.panorama_guide_move_in_one_direction);
        } else {
            showGuideText(IS_USE_AF, USE_SENSOR_CORRECTION);
        }
    }

    protected void showGuideText(final boolean isVisible, final int stringId) {
        if (checkMediator()) {
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    PlanePanoramaControllerBase.this.mGet.removePostRunnable(this);
                    if (PlanePanoramaControllerBase.this.mStartAndStopGuideTextLayout != null) {
                        if (stringId != 0) {
                            ((TextView) PlanePanoramaControllerBase.this.mStartAndStopGuideTextLayout.findViewById(R.id.guide_text)).setText(stringId);
                        }
                        if (isVisible) {
                            PlanePanoramaControllerBase.this.startRotateGuideText(PlanePanoramaControllerBase.this.mStartAndStopGuideTextLayout, PlanePanoramaControllerBase.this.mGet.getOrientationDegree());
                        }
                        PlanePanoramaControllerBase.this.mStartAndStopGuideTextLayout.setVisibility(isVisible ? PlanePanoramaControllerBase.USE_SENSOR_CORRECTION : 4);
                    }
                }
            });
        }
    }

    protected void startRotateGuideText(RelativeLayout layout, int degree) {
        if (layout != null) {
            int marginBottom = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.plane_panorama_guide_string_marginBottom);
            int sideMargin = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.plane_panorama_guide_string_marginStart);
            int indicatorHeight = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.layout_preview_indicators_height);
            LayoutParams lp = (LayoutParams) layout.getLayoutParams();
            Common.resetLayoutParameter(lp);
            layout.setLayoutDirection(USE_SENSOR_CORRECTION);
            lp.topMargin = USE_SENSOR_CORRECTION;
            lp.setMarginStart(USE_SENSOR_CORRECTION);
            lp.setMarginEnd(USE_SENSOR_CORRECTION);
            lp.bottomMargin = USE_SENSOR_CORRECTION;
            RelativeLayout textInnerLayout = (RelativeLayout) layout.findViewById(R.id.guide_text_inner_layout);
            LayoutParams lpInnerLayout = (LayoutParams) textInnerLayout.getLayoutParams();
            Common.resetLayoutParameter(lpInnerLayout);
            textInnerLayout.setLayoutDirection(USE_SENSOR_CORRECTION);
            lpInnerLayout.width = -2;
            textInnerLayout.setGravity(17);
            ((RotateLayout) layout.findViewById(R.id.guide_text_rotate_layout)).setAngle(degree);
            if (Util.isEqualDegree(this.mGet.getResources(), degree, USE_SENSOR_CORRECTION) || Util.isEqualDegree(this.mGet.getResources(), degree, MediaProviderUtils.ROTATION_180)) {
                lp.addRule(20, 1);
                lp.addRule(USE_THRESHOLD, 1);
                lp.setMarginStart(marginBottom);
                lp.topMargin = sideMargin;
                lp.bottomMargin = sideMargin;
                lpInnerLayout.width = this.mBackgroundPreviewLayout.getHeight() - (sideMargin * 2);
            } else if (Util.isEqualDegree(this.mGet.getResources(), degree, 90) || Util.isEqualDegree(this.mGet.getResources(), degree, Tag.IMAGE_DESCRIPTION)) {
                lp.addRule(12, 1);
                lp.addRule(14, 1);
                lp.setMarginStart(sideMargin);
                lp.setMarginEnd(indicatorHeight);
                lp.bottomMargin = marginBottom;
                lpInnerLayout.width = this.mBackgroundPreviewLayout.getWidth() - ((sideMargin + indicatorHeight) * 2);
            }
            textInnerLayout.setLayoutParams(lpInnerLayout);
            layout.setLayoutParams(lp);
        }
    }

    public RotateLayout getPreviewMiniLayout() {
        return this.mPreviewMiniLayout;
    }

    public ImageView getPreviewMini() {
        return this.mPreviewMini;
    }

    public ImageView getBar() {
        return this.mBar;
    }

    public void addStillImage(StillImageData dat) {
    }

    public boolean isShooting() {
        return this.mIsShooting;
    }

    public void increseCntReqShoot() {
        this.mCntReqShoot++;
    }

    public void setStatus(int status) {
        this.mStatusShot = status;
    }

    public int getOrientationDegree() {
        return this.mGet.getOrientationDegree();
    }

    public void setShutterButtonImage(boolean buttonEnable, int degree) {
        this.mGet.setShutterButtonImage(buttonEnable, degree);
    }

    public int[] getDirection() {
        return this.mDirection;
    }

    public void playPanoramaShutterSound() {
        if (this.mGet.isPausing()) {
            CamLog.d(FaceDetector.TAG, "mMediator.isPausing() == true");
        } else {
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    PlanePanoramaControllerBase.this.mGet.removePostRunnable(this);
                    PlanePanoramaControllerBase.this.mGet.playFreePanoramaShutterSound();
                }
            });
        }
    }

    public View getBarLayout() {
        return this.mBarLayout;
    }

    public String getSaveInputDirPath() {
        return this.mSaveInputDirPath;
    }

    public String getShootingDate() {
        return this.mShootingDate;
    }

    public int getNumOfShoot() {
        return this.mCntReqShoot;
    }

    public void playRecordingSound(boolean start) {
        this.mGet.playRecordingSound(start);
    }

    public void setRequestTakePicture(boolean need) {
        this.mRequestTakePicture = need;
    }

    public View findViewById(int id) {
        return this.mGet.findViewById(id);
    }

    public Resources getResources() {
        return this.mGet.getResources();
    }

    public void perfLockAcquire() {
        try {
            this.mGet.perfLockAcquire();
        } catch (NoSuchMethodError e) {
            CamLog.d(FaceDetector.TAG, "no perfLockAcquire API");
        }
    }

    public boolean checkOK(int ret, String errorLog, boolean isFinish) {
        if (ret == 0) {
            return USE_MULTI_THREAD;
        }
        CamLog.d(FaceDetector.TAG, errorLog + "(" + ret + ")");
        if (isFinish) {
            Toast.makeText(this.mGet.getApplicationContext(), R.string.camera_error_occurred_try_again, 1).show();
            this.mGet.getActivity().finish();
        }
        return IS_USE_AF;
    }
}
