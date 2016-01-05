package com.lge.camera.listeners;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.R;
import com.lge.camera.components.RotateImageView;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.util.AnimationUtil;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.Util;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.ArrayList;
import java.util.Iterator;

public class PlanePanoramaCallbackBase implements PreviewCallback, PictureCallback, AutoFocusCallback, ShutterCallback {
    protected static boolean ADJUST_CURRENT_FRAME_ASPECT_RATIO = false;
    protected static final int ANI_SHOW_ARROW = 2130968589;
    public static final int APP_DIR_AUTO = 2;
    public static final int APP_DIR_HOR = 0;
    public static final int APP_DIR_LAND = 4;
    public static final int APP_DIR_LAND_AUTO = 6;
    public static final int APP_DIR_LAND_HOR = 4;
    public static final int APP_DIR_LAND_VER = 5;
    public static final int APP_DIR_POR = 0;
    public static final int APP_DIR_POR_AUTO = 2;
    public static final int APP_DIR_POR_HOR = 0;
    public static final int APP_DIR_POR_VER = 1;
    public static final int APP_DIR_VER = 1;
    protected static final int FULL_RATIO = 100;
    protected static final int GUIDE_SKIP_COUNT = 3;
    protected static final double HALF = 0.5d;
    protected static final int INDEX_X = 0;
    protected static final int INDEX_Y = 1;
    protected static final float LAST_WIDTH_RATE_FOR_AUTO_STOP = 0.2f;
    protected static final int PREVIEW_BAR_SCALE = 2;
    protected static final int PREVIEW_COUNT = -1;
    protected static final int PREVIEW_CROP_RATIO = 10;
    protected static final float PREVIEW_GUIDE_AREA_RATIO_HOR_HOR;
    protected static final float PREVIEW_GUIDE_AREA_RATIO_HOR_VER;
    protected static final float PREVIEW_GUIDE_AREA_RATIO_VER = 0.12f;
    protected static final float PREVIEW_GUIDE_RED_ZONE_RATIO = 0.3f;
    protected static final float PREVIEW_MAGNETIC_RATIO = 0.1f;
    protected static final int PREVIEW_SKIP_COUNT = 1;
    protected static final double QUARTER_ONE = 0.25d;
    protected static final double QUARTER_THREE = 0.75d;
    protected static final int RULE_TRUE = 1;
    protected static final int SHOOTING_SOUND_SKIP_COUNT = 1;
    protected static final int TIME_ANI = 300;
    protected static final int TWICE = 2;
    protected static final int TYPE_DOWN_FAR = 4;
    protected static final int TYPE_DOWN_NEAR = 5;
    protected static final int TYPE_LEFT_FAR = 6;
    protected static final int TYPE_LEFT_NEAR = 7;
    protected static final int TYPE_RIGHT_FAR = 2;
    protected static final int TYPE_RIGHT_NEAR = 3;
    protected static final int TYPE_UP_FAR = 0;
    protected static final int TYPE_UP_NEAR = 1;
    protected final int MOTION_DATA_LENGTH;
    protected boolean isOnMagneticVertical;
    protected boolean isShowHorizontalArrow;
    protected int mAppDeviceRotation;
    protected int mAppPanoramaDirection;
    protected int mAppPanoramaDirectionSettings;
    protected ImageView mArrowDown;
    protected ImageView mArrowLeft;
    protected ImageView mArrowRight;
    protected ImageView mArrowUp;
    protected RotateImageView mBarArrow;
    protected Canvas mBarCanvas;
    protected int mBarH;
    public Bitmap mBarImage;
    protected int mBarLayoutMargin;
    protected int mBarW;
    protected int mBoxAttachCurrentBgId;
    protected FrameLayout mBoxAttachFrameArrow;
    protected FrameLayout mBoxAttachFrameText;
    protected RelativeLayout mBoxAttachOutlineArrow;
    protected RelativeLayout mBoxAttachOutlineText;
    protected RotateLayout mBoxAttachRotateLayoutArrow;
    protected RotateLayout mBoxAttachRotateLayoutText;
    protected RelativeLayout mBoxGuide;
    protected int mBoxOutlineNinePatchMargin;
    protected int mCurDegree;
    protected int mDirectionArrowMargin;
    protected Bitmap mDisplayPreviewImageMini;
    protected float mDistGuideAttachPos;
    protected PlanePanoramaCallbackFunction mGet;
    protected int mGuideAreaLength;
    protected int mGuideBoxAdjustHor;
    protected int mGuideBoxAdjustVer;
    protected int mGuideBoxLineWidth;
    protected int mGuideDrawingSkipCount;
    protected int[] mImageID;
    protected ArrayList<Integer> mImageIDList;
    protected ArrayList<Integer> mImageStatusList;
    protected ArrayList<ImageView> mListArrow;
    protected byte[] mMotionData;
    protected boolean mNeedStop;
    protected NinePatchDrawable mOutline;
    protected int mPrevDirection;
    protected int mPreviewCount;
    protected int mPreviewCropAdjust;
    protected int mPreviewH;
    protected Bitmap mPreviewImageMini;
    protected Canvas mPreviewMiniCanvas;
    protected int mPreviewPanelBottmMargin;
    protected int mPreviewPanelWidth;
    protected int mPreviewSkipCount;
    protected int mPreviewW;
    public Bitmap mProgressImage;
    protected int mProgressImageH;
    protected int mProgressImageW;
    protected int mQuickButtonWidth;
    protected Rect mRectPreview;
    protected Rect mRectPreviewMini;
    protected Rect mRectProgressBar;
    protected int[] mStatus;
    protected int mStatusPre;
    protected String mStrDown;
    protected String mStrLeft;
    protected String mStrRight;
    protected String mStrUp;
    protected TextView mTextAny;
    protected int mUseImage;
    protected int offsetX;
    protected int offsetY;
    protected int[] outlineLocationArrow;
    protected int[] outlineLocationText;

    static {
        PREVIEW_GUIDE_AREA_RATIO_HOR_HOR = FunctionProperties.getPlanePanoramaGuideAreaRatioHorHor();
        PREVIEW_GUIDE_AREA_RATIO_HOR_VER = FunctionProperties.getPlanePanoramaGuideAreaRatioHorVer();
        ADJUST_CURRENT_FRAME_ASPECT_RATIO = false;
    }

    public PlanePanoramaCallbackBase(PlanePanoramaCallbackFunction function) {
        this.outlineLocationArrow = new int[]{TYPE_UP_FAR, TYPE_UP_FAR};
        this.outlineLocationText = new int[]{TYPE_UP_FAR, TYPE_UP_FAR};
        this.mListArrow = new ArrayList();
        this.mStatusPre = TYPE_UP_FAR;
        this.mStatus = new int[TYPE_UP_NEAR];
        this.mImageID = new int[TYPE_UP_NEAR];
        this.mUseImage = TYPE_UP_FAR;
        this.mPreviewCount = PREVIEW_COUNT;
        this.mPreviewSkipCount = TYPE_UP_NEAR;
        this.mGuideDrawingSkipCount = TYPE_UP_FAR;
        this.MOTION_DATA_LENGTH = Ola_ImageFormat.RGB_LABEL;
        this.mMotionData = new byte[Ola_ImageFormat.RGB_LABEL];
        this.mPreviewCropAdjust = TYPE_UP_FAR;
        this.mAppDeviceRotation = TYPE_UP_FAR;
        this.mAppPanoramaDirection = TYPE_RIGHT_FAR;
        this.mAppPanoramaDirectionSettings = TYPE_RIGHT_FAR;
        this.mImageIDList = new ArrayList();
        this.mImageStatusList = new ArrayList();
        this.isShowHorizontalArrow = false;
        this.isOnMagneticVertical = false;
        this.mBoxAttachCurrentBgId = R.drawable.panorama_white_box;
        this.mNeedStop = false;
        this.mGet = function;
    }

    public void resetImageIdAndStatusList() {
        this.mImageIDList.clear();
        this.mImageStatusList.clear();
    }

    public void resetPreviewSkipCount() {
        this.mPreviewSkipCount = TYPE_UP_NEAR;
    }

    public void setPrevDirection(int direction) {
        this.mPrevDirection = direction;
    }

    public void setPreviewCroppingAdjustByAuto(int value) {
        this.mPreviewCropAdjust = value;
    }

    public int getAppPanoramaDirectionSettings() {
        return this.mAppPanoramaDirectionSettings;
    }

    public int getAppPanoramaDirection() {
        return this.mAppPanoramaDirection;
    }

    public void setAppPanoramaDirection(int value) {
        this.mAppPanoramaDirection = value;
    }

    public int getAppDeviceRotation() {
        return this.mAppDeviceRotation;
    }

    public Bitmap getPreviewImage() {
        return this.mProgressImage;
    }

    public void onShutter() {
        if (this.mGet.getNumOfShoot() > TYPE_UP_NEAR) {
            this.mGet.playPanoramaShutterSound();
        }
    }

    public void onAutoFocus(boolean success, Camera camera) {
    }

    public void onPictureTaken(byte[] data, Camera camera) {
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
    }

    protected void initView(View base) {
        CamLog.d(FaceDetector.TAG, "initView START ");
        if (this.mGet == null || this.mGet.getActivity() == null || base == null) {
            CamLog.d(FaceDetector.TAG, "exit initView mGet=" + this.mGet + " base=" + base);
            return;
        }
        this.mGuideBoxLineWidth = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.plane_panorama_guide_box_line_width);
        this.mStrUp = this.mGet.getResources().getString(R.string.sp_up_NORMAL);
        this.mStrDown = this.mGet.getResources().getString(R.string.sp_down_NORMAL);
        this.mStrLeft = this.mGet.getResources().getString(R.string.sp_left_NORMAL);
        this.mStrRight = this.mGet.getResources().getString(R.string.sp_right_NORMAL);
        this.mBoxGuide = (RelativeLayout) base.findViewById(R.id.plane_panorama_box_guide);
        this.mBoxAttachFrameArrow = (FrameLayout) base.findViewById(R.id.plane_panorama_box_attach_layout_arrow);
        this.mBoxAttachRotateLayoutArrow = (RotateLayout) base.findViewById(R.id.plane_panorama_box_attach_rotate_layout_arrow);
        this.mBoxAttachOutlineArrow = (RelativeLayout) base.findViewById(R.id.plane_panorama_box_attach_arrow);
        this.mArrowUp = (ImageView) base.findViewById(R.id.plane_panorama_box_arrow_up);
        this.mArrowDown = (ImageView) base.findViewById(R.id.plane_panorama_box_arrow_down);
        this.mArrowLeft = (ImageView) base.findViewById(R.id.plane_panorama_box_arrow_left);
        this.mArrowRight = (ImageView) base.findViewById(R.id.plane_panorama_box_arrow_right);
        this.mBoxAttachFrameText = (FrameLayout) base.findViewById(R.id.plane_panorama_box_attach_layout_text);
        this.mBoxAttachRotateLayoutText = (RotateLayout) base.findViewById(R.id.plane_panorama_box_attach_rotate_layout_text);
        this.mBoxAttachOutlineText = (RelativeLayout) base.findViewById(R.id.plane_panorama_box_attach_text_outline);
        this.mTextAny = (TextView) base.findViewById(R.id.plane_panorama_box_attach_text);
        this.mBarArrow = (RotateImageView) base.findViewById(R.id.plane_panorama_preview_bar_arrow);
        this.mListArrow.add(this.mArrowUp);
        this.mListArrow.add(this.mArrowRight);
        this.mListArrow.add(this.mArrowDown);
        this.mListArrow.add(this.mArrowLeft);
        this.mBoxGuide.setVisibility(TYPE_DOWN_FAR);
        this.mBoxAttachFrameArrow.setVisibility(TYPE_DOWN_FAR);
        this.mBoxAttachFrameText.setVisibility(TYPE_DOWN_FAR);
        this.mBarLayoutMargin = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.plane_panorama_bar_margin);
        this.mPreviewPanelWidth = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.preview_panel_width);
        this.mPreviewPanelBottmMargin = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.preview_panel_marginBottom);
        this.mBoxOutlineNinePatchMargin = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.plane_panorama_guide_box_nine_patch_margin);
        this.mDirectionArrowMargin = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.plane_panorama_direction_arrow_margin);
        this.mQuickButtonWidth = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.quickfunction_layout_width);
        setGuideBoxAdjustDimens();
        hideGuideCase();
        CamLog.d(FaceDetector.TAG, "initView END");
    }

    private void setGuideBoxAdjustDimens() {
        this.mGuideBoxAdjustHor = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.plane_panorama_guide_box_adjust_hor_margin);
        this.mGuideBoxAdjustVer = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.plane_panorama_guide_box_adjust_ver_margin);
        if (ModelProperties.NAME_G2M_NV_TCL.equals(ModelProperties.readModelName())) {
            this.mGuideBoxAdjustHor = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.plane_panorama_guide_box_adjust_hor_2_margin);
            this.mGuideBoxAdjustVer = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.plane_panorama_guide_box_adjust_ver_2_margin);
        }
    }

    public void setCameraPreviewSize(int width, int height) {
        this.mPreviewW = width;
        this.mPreviewH = height;
        this.mPreviewImageMini = Bitmap.createBitmap((int) (((double) this.mPreviewW) * HALF), (int) (((double) this.mPreviewH) * HALF), Config.ARGB_8888);
        this.mDisplayPreviewImageMini = Bitmap.createBitmap(Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.plane_panorama_preview_mini_width), Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.plane_panorama_preview_mini_heigh), Config.ARGB_8888);
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(TYPE_UP_FAR, info);
        this.mGet.getPreviewMiniLayout().setRotation((float) (((info.orientation - Util.getDisplayRotation(this.mGet.getActivity())) + CameraConstants.DEGREE_360) % CameraConstants.DEGREE_360));
        this.mPreviewMiniCanvas = new Canvas(this.mDisplayPreviewImageMini);
        this.mRectPreviewMini = new Rect(TYPE_UP_FAR, TYPE_UP_FAR, this.mPreviewMiniCanvas.getWidth(), this.mPreviewMiniCanvas.getHeight());
    }

    protected void rotatePreviewPoints(Point pt, int rotate) {
        int x = pt.x;
        int y = pt.y;
        switch (rotate) {
            case MediaProviderUtils.ROTATION_90 /*90*/:
                x = (this.mGet.getInitParam().preview_img_height - pt.y) + PREVIEW_COUNT;
                y = pt.x;
                break;
            case MediaProviderUtils.ROTATION_180 /*180*/:
                x = (this.mGet.getInitParam().preview_img_width - pt.x) + PREVIEW_COUNT;
                y = (this.mGet.getInitParam().preview_img_height - pt.y) + PREVIEW_COUNT;
                break;
            case Tag.IMAGE_DESCRIPTION /*270*/:
                x = pt.y;
                y = (this.mGet.getInitParam().preview_img_width - pt.x) + PREVIEW_COUNT;
                break;
        }
        pt.x = x;
        pt.y = y;
    }

    public void unbind() {
        if (this.mProgressImage != null) {
            this.mProgressImage.recycle();
            this.mProgressImage = null;
        }
        if (this.mBarImage != null) {
            this.mBarImage.recycle();
            this.mBarImage = null;
        }
    }

    public void hideViews() {
        hideGuideCase();
        this.mBoxAttachOutlineArrow.setVisibility(TYPE_DOWN_FAR);
        this.mBoxAttachOutlineText.setVisibility(TYPE_DOWN_FAR);
        this.mBoxAttachFrameArrow.setVisibility(TYPE_DOWN_FAR);
        this.mBoxAttachFrameText.setVisibility(TYPE_DOWN_FAR);
        this.mBoxGuide.setVisibility(TYPE_DOWN_FAR);
    }

    protected void hideGuideCase() {
        Iterator i$ = this.mListArrow.iterator();
        while (i$.hasNext()) {
            ImageView iv = (ImageView) i$.next();
            if (iv != null) {
                iv.clearAnimation();
                iv.setVisibility(TYPE_DOWN_FAR);
            }
        }
        if (this.mTextAny != null && this.mTextAny.getVisibility() == 0) {
            this.mTextAny.clearAnimation();
            this.mTextAny.setText("");
            this.mTextAny.setVisibility(TYPE_DOWN_FAR);
        }
    }

    public void releaseResources() {
    }

    public void resetParamsBeforeTaking() {
        this.mPreviewCount = PREVIEW_COUNT;
        this.mGuideDrawingSkipCount = TYPE_UP_FAR;
        this.isShowHorizontalArrow = false;
        this.isOnMagneticVertical = false;
        this.mGuideAreaLength = TYPE_UP_FAR;
        this.mBoxAttachCurrentBgId = R.drawable.panorama_white_box;
        this.mBoxAttachOutlineArrow.setBackgroundResource(R.drawable.panorama_white_box);
        showAllGuideArrowAndText(false);
        this.mNeedStop = false;
        this.mDistGuideAttachPos = PREVIEW_GUIDE_AREA_RATIO_HOR_VER;
        this.mRectProgressBar = new Rect(TYPE_UP_FAR, TYPE_UP_FAR, TYPE_UP_FAR, TYPE_UP_FAR);
    }

    protected void showAllGuideArrowAndText(boolean show) {
        int visibility = show ? TYPE_UP_FAR : TYPE_DOWN_FAR;
        this.mArrowUp.setVisibility(visibility);
        this.mArrowDown.setVisibility(visibility);
        this.mArrowLeft.setVisibility(visibility);
        this.mArrowRight.setVisibility(visibility);
        this.mTextAny.setVisibility(visibility);
        this.mBarArrow.setVisibility(visibility);
    }

    public void rotateGuide(int degree) {
        if (this.mBoxAttachRotateLayoutArrow != null) {
            this.mBoxAttachRotateLayoutArrow.setRotation((float) (-degree));
        }
        if (this.mBoxAttachRotateLayoutText != null) {
            this.mBoxAttachRotateLayoutText.setRotation((float) (-degree));
        }
    }

    protected void startBlinkingAnimation(View view, boolean isVisible, boolean isForced, int startAniId) {
        int visibility;
        int i = TYPE_UP_FAR;
        if (isVisible) {
            visibility = TYPE_UP_FAR;
        } else {
            visibility = TYPE_DOWN_FAR;
        }
        if (!isForced) {
            int visibility2 = view.getVisibility();
            if (!isVisible) {
                i = TYPE_DOWN_FAR;
            }
            if (visibility2 == i) {
                return;
            }
        }
        view.clearAnimation();
        view.setVisibility(visibility);
        if (!isVisible) {
            startAniId = R.anim.plane_panorama_arrow_hide;
        }
        view.startAnimation(AnimationUtils.loadAnimation(this.mGet.getActivity().getApplicationContext(), startAniId));
    }

    protected void showGuideCase(ImageView visibleArrow, String directionText, int textType, boolean isShow) {
        Iterator i$ = this.mListArrow.iterator();
        while (i$.hasNext()) {
            ImageView iv = (ImageView) i$.next();
            if (!(iv == null || iv == visibleArrow)) {
                iv.clearAnimation();
                iv.setVisibility(TYPE_DOWN_FAR);
            }
        }
        startBlinkingAnimation(visibleArrow, isShow, false, ANI_SHOW_ARROW);
        if (isShow) {
            if (this.mTextAny.getText() != directionText) {
                this.mTextAny.setVisibility(TYPE_DOWN_FAR);
                this.mTextAny.setText(directionText);
            }
            setGuideTextLayout(textType);
        }
        AnimationUtil.startShowingAnimation(this.mTextAny, isShow, 300, null, false);
    }

    protected void setGuideTextLayout(int type) {
        if (this.mTextAny != null) {
            LayoutParams lpText = (LayoutParams) this.mTextAny.getLayoutParams();
            LayoutParams lpOutline = (LayoutParams) this.mBoxAttachOutlineText.getLayoutParams();
            Common.resetLayoutParameter(lpText);
            Common.resetLayoutParameter(lpOutline);
            this.mTextAny.setGravity(17);
            lpOutline.setLayoutDirection(TYPE_UP_FAR);
            lpOutline.width = this.mBoxAttachOutlineArrow.getWidth();
            lpOutline.height = this.mBoxAttachOutlineArrow.getHeight();
            setGuideTextLayoutByType(lpText, lpOutline, type);
            this.mTextAny.setLayoutParams(lpText);
            this.mBoxAttachOutlineText.setLayoutParams(lpOutline);
        }
    }

    public void allocateDisplayBuffers(int direction) {
        int dispW = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.lcd_height);
        int dispH = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.lcd_width);
        if (this.mProgressImage != null) {
            this.mProgressImage.recycle();
            this.mProgressImage = null;
        }
        if (this.mBarImage != null) {
            this.mBarImage.recycle();
            this.mBarImage = null;
        }
        if (this.mProgressImage == null) {
            int dpW;
            int dpH;
            int pdw = this.mGet.getInitParam().preview_img_width;
            int pdh = this.mGet.getInitParam().preview_img_height;
            if (this.mGet.getRoratePreview() == 90 || this.mGet.getRoratePreview() == Tag.IMAGE_DESCRIPTION) {
                pdw = this.mGet.getInitParam().preview_img_height;
                pdh = this.mGet.getInitParam().preview_img_width;
            }
            this.mProgressImage = Bitmap.createBitmap(pdw, pdh, Config.ARGB_8888);
            this.mProgressImageW = this.mProgressImage.getWidth();
            this.mProgressImageH = this.mProgressImage.getHeight();
            switch (direction) {
                case TYPE_UP_FAR /*0*/:
                case TYPE_DOWN_FAR /*4*/:
                    dispW -= this.mBarLayoutMargin + this.mBarLayoutMargin;
                    dpW = dispW;
                    dpH = ((((int) (((float) dispW) * (((float) pdh) / ((float) pdw)))) * 80) / FULL_RATIO) * TYPE_RIGHT_FAR;
                    break;
                default:
                    dispH -= ((this.mPreviewPanelWidth + this.mPreviewPanelBottmMargin) + this.mQuickButtonWidth) + (this.mBarLayoutMargin + this.mBarLayoutMargin);
                    dpW = ((((int) (((float) dispH) * (((float) (pdw - (this.mPreviewCropAdjust * TYPE_RIGHT_FAR))) / ((float) pdh)))) * 80) / FULL_RATIO) * TYPE_RIGHT_FAR;
                    dpH = dispH;
                    break;
            }
            this.mBarImage = Bitmap.createBitmap(dpW, dpH, Config.ARGB_8888);
            this.mBarCanvas = new Canvas(this.mBarImage);
            this.mBarW = this.mBarImage.getWidth();
            this.mBarH = this.mBarImage.getHeight();
        }
    }

    protected void checkAttachBoxRedZone(int gap, int margin) {
        if (this.mStatus[TYPE_UP_FAR] != TYPE_DOWN_FAR) {
            if (Math.abs(gap) < margin) {
                if (this.mBoxAttachCurrentBgId != R.drawable.panorama_white_box) {
                    this.mBoxAttachOutlineArrow.setBackgroundResource(R.drawable.panorama_white_box);
                    this.mBoxAttachCurrentBgId = R.drawable.panorama_white_box;
                }
            } else if (this.mBoxAttachCurrentBgId != R.drawable.panorama_red_box) {
                this.mBoxAttachOutlineArrow.setBackgroundResource(R.drawable.panorama_red_box);
                this.mBoxAttachCurrentBgId = R.drawable.panorama_red_box;
            }
        }
    }

    protected boolean showTakingTextGuide() {
        if (this.mStatusPre != this.mStatus[TYPE_UP_FAR]) {
            CamLog.d(FaceDetector.TAG, "engine status = " + this.mStatus[TYPE_UP_FAR]);
            switch (this.mStatus[TYPE_UP_FAR]) {
                case TYPE_UP_NEAR /*1*/:
                case TYPE_RIGHT_FAR /*2*/:
                case TYPE_RIGHT_NEAR /*3*/:
                case TYPE_LEFT_NEAR /*7*/:
                case LGKeyRec.EVENT_NEED_MORE_AUDIO /*8*/:
                case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
                case PREVIEW_CROP_RATIO /*10*/:
                    this.mGet.toastLong(this.mGet.getResources().getString(R.string.error_panorama_during_taking));
                    this.mGet.stopPanorama();
                    this.mGet.getCameraDevice().startPreview();
                    return false;
                case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
                    this.mGet.stopPanorama();
                    this.mGet.getCameraDevice().startPreview();
                    return false;
                default:
                    this.mStatusPre = this.mStatus[TYPE_UP_FAR];
                    break;
            }
        }
        return true;
    }

    protected void setGuideTextLayoutByType(LayoutParams lpText, LayoutParams lpOutline, int type) {
        switch (type) {
            case TYPE_UP_FAR /*0*/:
            case TYPE_UP_NEAR /*1*/:
                setGuideTextLayoutToUp(lpText, lpOutline, type);
            case TYPE_RIGHT_FAR /*2*/:
            case TYPE_RIGHT_NEAR /*3*/:
                setGuideTextLayoutToRight(lpText, lpOutline, type);
            case TYPE_DOWN_FAR /*4*/:
            case TYPE_DOWN_NEAR /*5*/:
                setGuideTextLayoutToDown(lpText, lpOutline, type);
            case TYPE_LEFT_FAR /*6*/:
            case TYPE_LEFT_NEAR /*7*/:
                setGuideTextLayoutToLeft(lpText, lpOutline, type);
            default:
        }
    }

    protected void setGuideTextLayoutToUp(LayoutParams lpText, LayoutParams lpOutline, int type) {
        lpText.addRule(14, TYPE_UP_NEAR);
        lpOutline.addRule(14, TYPE_UP_NEAR);
        lpOutline.addRule(TYPE_RIGHT_NEAR, this.mTextAny.getId());
        lpText.topMargin = TYPE_UP_FAR;
        lpText.rightMargin = TYPE_UP_FAR;
        if (type == 0) {
            lpText.bottomMargin = this.mArrowUp.getHeight();
        } else {
            lpText.bottomMargin = this.mGuideBoxLineWidth;
        }
        lpText.leftMargin = TYPE_UP_FAR;
    }

    protected void setGuideTextLayoutToRight(LayoutParams lpText, LayoutParams lpOutline, int type) {
        lpText.addRule(15, TYPE_UP_NEAR);
        lpOutline.addRule(15, TYPE_UP_NEAR);
        lpText.addRule(17, this.mBoxAttachOutlineText.getId());
        lpText.topMargin = TYPE_UP_FAR;
        lpText.rightMargin = TYPE_UP_FAR;
        lpText.bottomMargin = TYPE_UP_FAR;
        if (type == TYPE_RIGHT_FAR) {
            lpText.leftMargin = this.mArrowRight.getHeight();
        } else {
            lpText.leftMargin = this.mGuideBoxLineWidth;
        }
    }

    protected void setGuideTextLayoutToDown(LayoutParams lpText, LayoutParams lpOutline, int type) {
        lpText.addRule(14, TYPE_UP_NEAR);
        lpOutline.addRule(14, TYPE_UP_NEAR);
        lpText.addRule(TYPE_RIGHT_NEAR, this.mBoxAttachOutlineText.getId());
        if (type == TYPE_DOWN_FAR) {
            lpText.topMargin = this.mArrowDown.getHeight();
        } else {
            lpText.topMargin = this.mGuideBoxLineWidth;
        }
        lpText.rightMargin = TYPE_UP_FAR;
        lpText.bottomMargin = TYPE_UP_FAR;
        lpText.leftMargin = TYPE_UP_FAR;
    }

    protected void setGuideTextLayoutToLeft(LayoutParams lpText, LayoutParams lpOutline, int type) {
        lpText.addRule(15, TYPE_UP_NEAR);
        lpOutline.addRule(15, TYPE_UP_NEAR);
        lpOutline.addRule(17, this.mTextAny.getId());
        lpText.topMargin = TYPE_UP_FAR;
        if (type == TYPE_LEFT_FAR) {
            lpText.rightMargin = this.mArrowLeft.getHeight();
        } else {
            lpText.rightMargin = this.mGuideBoxLineWidth;
        }
        lpText.bottomMargin = TYPE_UP_FAR;
        lpText.leftMargin = TYPE_UP_FAR;
    }

    protected void drawAttachGuideVerticalToHorUp() {
        switch (this.mCurDegree) {
            case TYPE_UP_FAR /*0*/:
                showGuideCase(this.mArrowUp, this.mStrUp, TYPE_UP_FAR, true);
            case MediaProviderUtils.ROTATION_90 /*90*/:
                showGuideCase(this.mArrowLeft, this.mStrLeft, TYPE_LEFT_FAR, true);
            case MediaProviderUtils.ROTATION_180 /*180*/:
                showGuideCase(this.mArrowDown, this.mStrDown, TYPE_UP_NEAR, true);
            case Tag.IMAGE_DESCRIPTION /*270*/:
                showGuideCase(this.mArrowRight, this.mStrRight, TYPE_RIGHT_FAR, true);
            default:
        }
    }

    protected void drawAttachGuideVerticalToHorDown() {
        switch (this.mCurDegree) {
            case TYPE_UP_FAR /*0*/:
                showGuideCase(this.mArrowDown, this.mStrDown, TYPE_DOWN_FAR, true);
            case MediaProviderUtils.ROTATION_90 /*90*/:
                showGuideCase(this.mArrowRight, this.mStrRight, TYPE_LEFT_NEAR, true);
            case MediaProviderUtils.ROTATION_180 /*180*/:
                showGuideCase(this.mArrowUp, this.mStrUp, TYPE_DOWN_NEAR, true);
            case Tag.IMAGE_DESCRIPTION /*270*/:
                showGuideCase(this.mArrowLeft, this.mStrLeft, TYPE_RIGHT_NEAR, true);
            default:
        }
    }

    protected void drawAttachGuideVerticalToVerUp() {
        switch (this.mCurDegree) {
            case TYPE_UP_FAR /*0*/:
                showGuideCase(this.mArrowRight, this.mStrRight, TYPE_RIGHT_FAR, true);
            case MediaProviderUtils.ROTATION_90 /*90*/:
                showGuideCase(this.mArrowUp, this.mStrUp, TYPE_UP_FAR, true);
            case MediaProviderUtils.ROTATION_180 /*180*/:
                showGuideCase(this.mArrowLeft, this.mStrLeft, TYPE_LEFT_FAR, true);
            case Tag.IMAGE_DESCRIPTION /*270*/:
                showGuideCase(this.mArrowDown, this.mStrDown, TYPE_DOWN_FAR, true);
            default:
        }
    }

    protected void drawAttachGuideVerticalToVerDown() {
        switch (this.mCurDegree) {
            case TYPE_UP_FAR /*0*/:
                showGuideCase(this.mArrowLeft, this.mStrLeft, TYPE_RIGHT_NEAR, true);
            case MediaProviderUtils.ROTATION_90 /*90*/:
                showGuideCase(this.mArrowDown, this.mStrDown, TYPE_UP_NEAR, true);
            case MediaProviderUtils.ROTATION_180 /*180*/:
                showGuideCase(this.mArrowRight, this.mStrRight, TYPE_LEFT_NEAR, true);
            case Tag.IMAGE_DESCRIPTION /*270*/:
                showGuideCase(this.mArrowUp, this.mStrUp, TYPE_DOWN_NEAR, true);
            default:
        }
    }

    protected void drawAttachGuideHorizontalToHorLeft() {
        switch (this.mCurDegree) {
            case TYPE_UP_FAR /*0*/:
                showGuideCase(this.mArrowLeft, this.mStrLeft, TYPE_UP_NEAR, this.isShowHorizontalArrow);
            case MediaProviderUtils.ROTATION_90 /*90*/:
                showGuideCase(this.mArrowDown, this.mStrDown, TYPE_LEFT_NEAR, this.isShowHorizontalArrow);
            case MediaProviderUtils.ROTATION_180 /*180*/:
                showGuideCase(this.mArrowRight, this.mStrRight, TYPE_DOWN_NEAR, this.isShowHorizontalArrow);
            case Tag.IMAGE_DESCRIPTION /*270*/:
                showGuideCase(this.mArrowUp, this.mStrUp, TYPE_RIGHT_NEAR, this.isShowHorizontalArrow);
            default:
        }
    }

    protected void drawAttachGuideHorizontalToHorRight() {
        switch (this.mCurDegree) {
            case TYPE_UP_FAR /*0*/:
                showGuideCase(this.mArrowRight, this.mStrRight, TYPE_UP_NEAR, this.isShowHorizontalArrow);
            case MediaProviderUtils.ROTATION_90 /*90*/:
                showGuideCase(this.mArrowUp, this.mStrUp, TYPE_LEFT_NEAR, this.isShowHorizontalArrow);
            case MediaProviderUtils.ROTATION_180 /*180*/:
                showGuideCase(this.mArrowLeft, this.mStrLeft, TYPE_DOWN_NEAR, this.isShowHorizontalArrow);
            case Tag.IMAGE_DESCRIPTION /*270*/:
                showGuideCase(this.mArrowDown, this.mStrDown, TYPE_RIGHT_NEAR, this.isShowHorizontalArrow);
            default:
        }
    }

    protected void drawAttachGuideHorizontalToVerLeft() {
        switch (this.mCurDegree) {
            case TYPE_UP_FAR /*0*/:
                showGuideCase(this.mArrowUp, this.mStrUp, TYPE_RIGHT_NEAR, this.isShowHorizontalArrow);
            case MediaProviderUtils.ROTATION_90 /*90*/:
                showGuideCase(this.mArrowLeft, this.mStrLeft, TYPE_UP_NEAR, this.isShowHorizontalArrow);
            case MediaProviderUtils.ROTATION_180 /*180*/:
                showGuideCase(this.mArrowDown, this.mStrDown, TYPE_LEFT_NEAR, this.isShowHorizontalArrow);
            case Tag.IMAGE_DESCRIPTION /*270*/:
                showGuideCase(this.mArrowRight, this.mStrRight, TYPE_DOWN_NEAR, this.isShowHorizontalArrow);
            default:
        }
    }

    protected void drawAttachGuideHorizontalToVerRight() {
        switch (this.mCurDegree) {
            case TYPE_UP_FAR /*0*/:
                showGuideCase(this.mArrowDown, this.mStrDown, TYPE_RIGHT_NEAR, this.isShowHorizontalArrow);
            case MediaProviderUtils.ROTATION_90 /*90*/:
                showGuideCase(this.mArrowRight, this.mStrRight, TYPE_UP_NEAR, this.isShowHorizontalArrow);
            case MediaProviderUtils.ROTATION_180 /*180*/:
                showGuideCase(this.mArrowUp, this.mStrUp, TYPE_LEFT_NEAR, this.isShowHorizontalArrow);
            case Tag.IMAGE_DESCRIPTION /*270*/:
                showGuideCase(this.mArrowLeft, this.mStrLeft, TYPE_DOWN_NEAR, this.isShowHorizontalArrow);
            default:
        }
    }
}
