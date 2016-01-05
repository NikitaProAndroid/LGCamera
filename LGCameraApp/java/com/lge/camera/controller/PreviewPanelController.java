package com.lge.camera.controller;

import android.content.ContentUris;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.RotateImageButton;
import com.lge.camera.components.RotateImageView;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.RotateView;
import com.lge.camera.components.ShutterButton;
import com.lge.camera.components.ShutterButton.OnShutterButtonListener;
import com.lge.camera.components.ShutterButton.OnShutterButtonLongPressListener;
import com.lge.camera.components.Switcher;
import com.lge.camera.components.Switcher.OnSwitchListener;
import com.lge.camera.components.SwitcherLever;
import com.lge.camera.components.SwitcherLever.OnSwitchLeverListener;
import com.lge.camera.components.SwitcherLeverVertical;
import com.lge.camera.components.ThumbnailController;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.MmsProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.properties.StorageProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.AudioUtil;
import com.lge.camera.util.BitmapManager;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;
import com.lge.camera.util.ExifUtil;
import com.lge.camera.util.ImageManager;
import com.lge.camera.util.SecureImageUtil;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.camera.util.TelephonyUtil;
import com.lge.camera.util.Util;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Limit;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

public abstract class PreviewPanelController extends Controller implements OnShutterButtonListener, OnShutterButtonLongPressListener, OnTouchListener, OnSwitchListener, OnSwitchLeverListener {
    private static final int ANI_CLOSE_STARTED = 2;
    private static final int ANI_FINISHED = 3;
    private static final int ANI_NONE = 0;
    private static final int ANI_OPEN_STARTED = 1;
    private static int PAUSE_RESUME_CHECK_DURATION = 0;
    public static final int SUB_BUTTON_BOTTOM = 2;
    public static final int SUB_BUTTON_MIDDLE = 1;
    public static final int SUB_BUTTON_TOP = 0;
    protected int NAVI_MARGIN;
    private boolean checkAreaOnTouch;
    private int mAlpha_value;
    public boolean mAutoReviewBlockTouch;
    private Timer mButtonCheckTimer;
    private Thread mDeleteThumbnailThread;
    public Runnable mDoSnapRunnable;
    private boolean mGalleryLaunching;
    private int mGalleryWindowAniState;
    private HashMap<String, Boolean> mHashMap;
    public Runnable mHideQuickViewRunable;
    private ImageView mLastGalleryImage;
    protected RotateImageView mLastPictureButton;
    private long mPrevTime;
    private boolean mProcessInitDone;
    private RotateImageView mQuickViewThumbImage;
    private View mQuickViewThumbLayout;
    protected ArrayList<ReleaseArea> mReleaseArea;
    private OnClickListener mReviewThumbnailClickListener;
    private OnLongClickListener mReviewThumbnailLongClickListener;
    private OnTouchListener mReviewThumbnailTouchListener;
    private boolean mShutterBurstShot;
    protected ShutterButton mShutterButton;
    private boolean mShutterButtonLongKey;
    private Runnable mShutterButtonRunnable;
    private boolean mShutterFocusLongKey;
    private boolean mSliding;
    private boolean mSnapshotOnContinuousFocus;
    private boolean mSnapshotOnIdle;
    protected RotateImageButton mSubButtonBottom;
    protected RotateImageButton mSubButtonMiddle;
    protected RotateImageButton mSubButtonTop;
    protected ImageView mSubTouchButtonBottom;
    protected ImageView mSubTouchButtonMiddle;
    protected ImageView mSubTouchButtonTop;
    protected Switcher mSwitcher;
    protected SwitcherLeverVertical mSwitcherLever;
    private ThumbnailController mThumbController;
    private Bitmap mThumbImage;
    private LayoutParams mThumbParams;
    private Uri mThumbUri;
    private Object mThumbnailLock;
    private Thread mThumbnailThread;
    private RotateImageButton mTrashView;
    private int mainBarAlpha_value;

    private class ReleaseArea {
        public int mBottom;
        public int mLeft;
        public int mRight;
        public int mTop;

        public ReleaseArea(int left, int top, int right, int bottom) {
            this.mLeft = left;
            this.mTop = top;
            this.mRight = right;
            this.mBottom = bottom;
        }
    }

    public PreviewPanelController(ControllerFunction function) {
        super(function);
        this.mShutterButton = null;
        this.mSwitcher = null;
        this.mSwitcherLever = null;
        this.mSubButtonTop = null;
        this.mSubButtonMiddle = null;
        this.mSubButtonBottom = null;
        this.mSubTouchButtonTop = null;
        this.mSubTouchButtonMiddle = null;
        this.mSubTouchButtonBottom = null;
        this.mThumbnailThread = null;
        this.mDeleteThumbnailThread = null;
        this.mHashMap = new HashMap();
        this.mLastPictureButton = null;
        this.mThumbImage = null;
        this.mThumbUri = null;
        this.mThumbController = null;
        this.mainBarAlpha_value = Ola_ShotParam.AnimalMask_Random;
        this.mAlpha_value = Ola_ShotParam.AnimalMask_Random;
        this.mSnapshotOnIdle = false;
        this.mSnapshotOnContinuousFocus = false;
        this.mProcessInitDone = false;
        this.NAVI_MARGIN = ANI_NONE;
        this.mThumbnailLock = new Object();
        this.mDoSnapRunnable = new Runnable() {
            public void run() {
                if (PreviewPanelController.this.checkMediator()) {
                    ShutterButton button = (ShutterButton) PreviewPanelController.this.mGet.findViewById(R.id.main_button_bg);
                    if (button != null && button.getVisibility() == 0) {
                        CamLog.d(FaceDetector.TAG, "mDoSnapRunnable");
                        PreviewPanelController.this.onShutterButtonClick(button);
                    }
                }
            }
        };
        this.mPrevTime = 0;
        this.checkAreaOnTouch = true;
        this.mGalleryLaunching = false;
        this.mReviewThumbnailClickListener = new OnClickListener() {
            public void onClick(View v) {
                PreviewPanelController.this.reviewThumbnailDoClickAction(v, false);
            }
        };
        this.mReviewThumbnailLongClickListener = new OnLongClickListener() {
            public boolean onLongClick(View v) {
                if (PreviewPanelController.this.reviewThumbnailDoClickAction(v, true)) {
                    return true;
                }
                return false;
            }
        };
        this.mTrashView = null;
        this.mLastGalleryImage = null;
        this.mQuickViewThumbLayout = null;
        this.mQuickViewThumbImage = null;
        this.mThumbParams = null;
        this.mReviewThumbnailTouchListener = new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionevent) {
                switch (motionevent.getAction()) {
                    case PreviewPanelController.SUB_BUTTON_MIDDLE /*1*/:
                        CamLog.d(FaceDetector.TAG, "Gallery key touch up");
                        PreviewPanelController.this.mGet.doCommand(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
                        boolean result = false;
                        View galleryWindowViewImage = PreviewPanelController.this.mGet.findViewById(R.id.gallery_quick_window_rotate);
                        if (!(PreviewPanelController.this.mAutoReviewBlockTouch || galleryWindowViewImage == null || galleryWindowViewImage.getVisibility() == 8 || PreviewPanelController.this.mGalleryWindowAniState == 0)) {
                            if (PreviewPanelController.this.mThumbUri == null || !PreviewPanelController.this.checkTrashLocation(motionevent.getX(), motionevent.getY())) {
                                PreviewPanelController.this.showGalleryQuickViewAnimation(false, false);
                            } else if (ModelProperties.getCarrierCode() == 6) {
                                PreviewPanelController.this.mGet.showDialogPopup(PreviewPanelController.ANI_FINISHED);
                                return true;
                            } else {
                                PreviewPanelController.this.showGalleryQuickViewAnimation(false, true);
                            }
                            result = true;
                        }
                        PreviewPanelController.this.reviewThumbnailTouchActionUp();
                        if (!PreviewPanelController.this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY) && !PreviewPanelController.this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) {
                            return result;
                        }
                        PreviewPanelController.this.mGet.showBeautyShotBarForNewUx(true);
                        return result;
                    case PreviewPanelController.SUB_BUTTON_BOTTOM /*2*/:
                        if (PreviewPanelController.this.mTrashView != null && PreviewPanelController.this.mLastGalleryImage != null && PreviewPanelController.this.mQuickViewThumbLayout != null && PreviewPanelController.this.mQuickViewThumbImage != null && PreviewPanelController.this.mThumbParams != null) {
                            if (!PreviewPanelController.this.mAutoReviewBlockTouch) {
                                int[] startPos = new int[PreviewPanelController.SUB_BUTTON_BOTTOM];
                                PreviewPanelController.this.mLastPictureButton.getLocationOnScreen(startPos);
                                int buttonWidth = Common.getPixelFromDimens(PreviewPanelController.this.mGet.getApplicationContext(), R.dimen.review_thumbnail_width);
                                int buttonHeight = Common.getPixelFromDimens(PreviewPanelController.this.mGet.getApplicationContext(), R.dimen.review_thumbnail_height);
                                int lcdWidth = Common.getPixelFromDimens(PreviewPanelController.this.mGet.getApplicationContext(), R.dimen.lcd_width);
                                int lcdHeight = Common.getPixelFromDimens(PreviewPanelController.this.mGet.getApplicationContext(), R.dimen.lcd_height);
                                PreviewPanelController.this.mThumbParams.leftMargin = (startPos[PreviewPanelController.ANI_NONE] + ((int) motionevent.getX())) - (buttonWidth / PreviewPanelController.SUB_BUTTON_BOTTOM);
                                PreviewPanelController.this.mThumbParams.topMargin = (startPos[PreviewPanelController.SUB_BUTTON_MIDDLE] + ((int) motionevent.getY())) - (buttonHeight / PreviewPanelController.SUB_BUTTON_BOTTOM);
                                if (PreviewPanelController.this.mThumbParams.leftMargin < 0) {
                                    PreviewPanelController.this.mThumbParams.leftMargin = PreviewPanelController.ANI_NONE;
                                }
                                if (PreviewPanelController.this.mGet.isConfigureLandscape()) {
                                    if (PreviewPanelController.this.mThumbParams.leftMargin > lcdWidth - buttonWidth) {
                                        PreviewPanelController.this.mThumbParams.leftMargin = lcdWidth - buttonWidth;
                                    }
                                } else if (PreviewPanelController.this.mThumbParams.leftMargin > lcdHeight - buttonWidth) {
                                    PreviewPanelController.this.mThumbParams.leftMargin = lcdHeight - buttonWidth;
                                }
                                if (PreviewPanelController.this.mThumbParams.topMargin < 0) {
                                    PreviewPanelController.this.mThumbParams.topMargin = PreviewPanelController.ANI_NONE;
                                }
                                if (PreviewPanelController.this.mGet.isConfigureLandscape()) {
                                    if (PreviewPanelController.this.mThumbParams.topMargin > lcdHeight - buttonHeight) {
                                        PreviewPanelController.this.mThumbParams.topMargin = lcdHeight - buttonHeight;
                                    }
                                } else if (PreviewPanelController.this.mThumbParams.topMargin > lcdWidth - buttonHeight) {
                                    PreviewPanelController.this.mThumbParams.topMargin = lcdWidth - buttonHeight;
                                }
                                PreviewPanelController.this.mQuickViewThumbLayout.setLayoutParams(PreviewPanelController.this.mThumbParams);
                                if (PreviewPanelController.this.mQuickViewThumbLayout.getVisibility() != 0) {
                                    PreviewPanelController.this.mQuickViewThumbLayout.setVisibility(PreviewPanelController.ANI_NONE);
                                }
                                if (!PreviewPanelController.this.checkTrashLocation(motionevent.getX(), motionevent.getY())) {
                                    PreviewPanelController.this.mTrashView.setPressed(false);
                                    PreviewPanelController.this.mTrashView.setImageResource(R.drawable.camera_gallery_quickview_trash_close);
                                    PreviewPanelController.this.mLastGalleryImage.setBackgroundResource(R.drawable.camera_gallery_quickview_bg);
                                    PreviewPanelController.this.mTrashView.sendAccessibilityEvent(65536);
                                    break;
                                }
                                if (!PreviewPanelController.this.mTrashView.isPressed()) {
                                    PreviewPanelController.this.mTrashView.sendAccessibilityEvent(32768);
                                }
                                PreviewPanelController.this.mTrashView.setPressed(true);
                                PreviewPanelController.this.mTrashView.setImageResource(R.drawable.camera_gallery_quickview_trash_open);
                                PreviewPanelController.this.mLastGalleryImage.setBackgroundResource(R.drawable.camera_gallery_quickview_bg_on);
                                break;
                            }
                            if (PreviewPanelController.this.mQuickViewThumbLayout.getVisibility() == 0) {
                                PreviewPanelController.this.mQuickViewThumbLayout.setVisibility(4);
                            }
                            return true;
                        }
                        return true;
                        break;
                }
                return false;
            }
        };
        this.mAutoReviewBlockTouch = false;
        this.mHideQuickViewRunable = new Runnable() {
            public void run() {
                if (PreviewPanelController.this.mGet != null) {
                    long duration;
                    long autoReviewDuration;
                    PreviewPanelController.this.mGet.removePostRunnable(this);
                    PreviewPanelController.this.mAutoReviewBlockTouch = false;
                    PreviewPanelController.this.showGalleryQuickViewAnimation(false, false);
                    String autoReview = PreviewPanelController.this.mGet.getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW);
                    if ("on_delay_2sec".equals(autoReview)) {
                        duration = CameraConstants.TOAST_LENGTH_SHORT;
                    } else {
                        duration = 0;
                    }
                    if ("on_delay_5sec".equals(autoReview)) {
                        autoReviewDuration = CameraConstants.TOAST_LENGTH_LONG;
                    } else {
                        autoReviewDuration = duration;
                    }
                    if (autoReviewDuration != 0 && PreviewPanelController.this.mGet.isTimemachineHasPictures()) {
                        PreviewPanelController.this.mGet.showBubblePopupVisibility(PreviewPanelController.ANI_NONE, CameraConstants.TOAST_LENGTH_LONG, true);
                    }
                }
            }
        };
        this.mGalleryWindowAniState = ANI_NONE;
        this.mShutterButtonLongKey = false;
        this.mShutterFocusLongKey = false;
        this.mShutterBurstShot = false;
        this.mShutterButtonRunnable = new Runnable() {
            public void run() {
                if (PreviewPanelController.this.checkMediator()) {
                    PreviewPanelController.this.mGet.removePostRunnable(this);
                    if (PreviewPanelController.this.mShutterButton != null) {
                        CamLog.d(FaceDetector.TAG, "mShutterButtonRunnable : click");
                        PreviewPanelController.this.onShutterButtonClick(PreviewPanelController.this.mShutterButton);
                    }
                }
            }
        };
        this.mSliding = false;
        setLockConditionForMainButton(true);
    }

    public void setPreviewPanelVisibility(boolean show) {
        if (!show) {
            this.mGet.findViewById(R.id.main_bar).setVisibility(4);
            this.mGet.findViewById(R.id.main_bar_without_shutter).setVisibility(4);
        } else if (ProjectVariables.isSupportClearView() && this.mGet.isClearView()) {
            this.mGet.findViewById(R.id.main_bar).setVisibility(ANI_NONE);
        } else {
            this.mGet.findViewById(R.id.main_bar).setVisibility(ANI_NONE);
            this.mGet.findViewById(R.id.main_bar_without_shutter).setVisibility(ANI_NONE);
        }
    }

    public void initController() {
        this.NAVI_MARGIN = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_panel_marginBottom);
        this.mGet.inflateStub(R.id.stub_preview_panel_without_shutter);
        setSubButtonVisibilityWithTouchBotton(R.id.sub_button1, 4);
        setSubButtonVisibilityWithTouchBotton(R.id.sub_button2, 4);
        setSubButtonVisibilityWithTouchBotton(R.id.sub_button3, 4);
        boolean z;
        if (ProjectVariables.useToggleSwitcher()) {
            this.mGet.findViewById(R.id.switcher).setVisibility(ANI_NONE);
            this.mGet.findViewById(R.id.switcher_lever_vertical).setVisibility(8);
            this.mSwitcher = (Switcher) this.mGet.findViewById(R.id.camera_switch);
            if (this.mGet.isAttachIntent()) {
                setSwitcherVisible(false);
            } else if (this.mSwitcher != null) {
                Switcher switcher = this.mSwitcher;
                if (this.mGet.getApplicationMode() == SUB_BUTTON_MIDDLE) {
                    z = true;
                } else {
                    z = false;
                }
                switcher.setSwitch(z);
                this.mSwitcher.setSwitcherImage(this.mGet.getOrientation(), this.mGet.getApplicationMode());
                this.mSwitcher.setOnSwitchListener(this);
            }
        } else {
            this.mGet.findViewById(R.id.switcher).setVisibility(8);
            this.mGet.findViewById(R.id.switcher_lever_vertical).setVisibility(ANI_NONE);
            this.mSwitcherLever = (SwitcherLeverVertical) this.mGet.findViewById(R.id.camera_switcher_vertical);
            if (this.mGet.isAttachIntent()) {
                setSwitcherVisible(false);
            } else if (this.mSwitcherLever != null) {
                SwitcherLeverVertical switcherLeverVertical = this.mSwitcherLever;
                if (this.mGet.getApplicationMode() == SUB_BUTTON_MIDDLE) {
                    z = true;
                } else {
                    z = false;
                }
                switcherLeverVertical.setSwitch(z);
                this.mSwitcherLever.setSwitcherImage(this.mGet.getOrientation(), this.mGet.getApplicationMode());
                this.mSwitcherLever.setOnSwitchLeverListener(this);
                this.mSwitcherLever.setSwitchEnable(true);
            }
        }
        this.mShutterButton = (ShutterButton) this.mGet.findViewById(R.id.main_button_bg);
        this.mShutterButton.setOnShutterButtonListener(this);
        this.mShutterButton.setOnShutterButtonLongPressListener(this);
        this.mShutterButton.setFocusable(false);
        setMainButtonContentDescription();
        setMainButtonVisible(true);
        this.mGet.findViewById(R.id.main_button).setFocusable(false);
        this.mGet.findViewById(R.id.mode_bg_camera).setFocusable(false);
        this.mGet.findViewById(R.id.mode_bg_video).setFocusable(false);
        CamLog.d(FaceDetector.TAG, "[PreviewPanelController] initController");
        this.mSubButtonTop = (RotateImageButton) this.mGet.findViewById(R.id.sub_button1);
        this.mSubButtonMiddle = (RotateImageButton) this.mGet.findViewById(R.id.sub_button2);
        this.mSubButtonBottom = (RotateImageButton) this.mGet.findViewById(R.id.sub_button3);
        this.mSubTouchButtonTop = (ImageView) this.mGet.findViewById(R.id.sub_touch_button1);
        this.mSubTouchButtonMiddle = (ImageView) this.mGet.findViewById(R.id.sub_touch_button2);
        this.mSubTouchButtonBottom = (ImageView) this.mGet.findViewById(R.id.sub_touch_button3);
        showSubButtonInit(false);
        if (!this.mGet.isAttachIntent() && this.mThumbController == null) {
            this.mLastPictureButton = (RotateImageView) this.mGet.findViewById(R.id.review_thumbnail);
            this.mLastPictureButton.setOnClickListener(this.mReviewThumbnailClickListener);
            this.mLastPictureButton.setOnLongClickListener(this.mReviewThumbnailLongClickListener);
            this.mLastPictureButton.setOnTouchListener(this.mReviewThumbnailTouchListener);
            this.mThumbController = new ThumbnailController(this.mGet.getResources(), this.mLastPictureButton, this.mGet.getContentResolver(), Common.useSecureLockImage());
        }
        initReleaseArea();
        this.mInit = true;
        enableCommand(false);
        setMainBarAlpha(this.mainBarAlpha_value);
        startRotation(this.mGet.getOrientationDegree(), false);
        this.mProcessInitDone = true;
    }

    public int getMainBarAlphaValue() {
        return this.mainBarAlpha_value;
    }

    public void setMainBarAlpha(int value) {
        boolean isOverPreviewSize = false;
        View mainBar = this.mGet.findViewById(R.id.main_bar_without_shutter);
        if (mainBar != null) {
            mainBar.setBackgroundResource(R.drawable.btn_right_bg);
            if (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_LIGHT))) {
                value = ANI_NONE;
            } else {
                if (ProjectVariables.isSupportClearView() || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
                    value = 30;
                }
                if (Util.SizeString2WidthHeight(this.mGet.getPreviewSizeOnScreen())[ANI_NONE] > Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.layout_preview_width)) {
                    isOverPreviewSize = true;
                }
                if (isOverPreviewSize) {
                    value = ANI_NONE;
                }
            }
            CamLog.v(FaceDetector.TAG, "call setAlpha value =" + value);
            mainBar.getBackground().setAlpha(value);
        }
        this.mainBarAlpha_value = value;
        this.mAlpha_value = Ola_ShotParam.AnimalMask_Random;
        if (!(this.mSubButtonTop == null || this.mSubButtonTop.getBackground() == null)) {
            this.mSubButtonTop.getBackground().setAlpha(this.mAlpha_value);
        }
        if (!(this.mSubButtonMiddle == null || this.mSubButtonMiddle.getBackground() == null)) {
            this.mSubButtonMiddle.getBackground().setAlpha(this.mAlpha_value);
        }
        if (!(this.mSubButtonBottom == null || this.mSubButtonBottom.getBackground() == null)) {
            this.mSubButtonBottom.getBackground().setAlpha(this.mAlpha_value);
        }
        if (ProjectVariables.useToggleSwitcher()) {
            if (!(this.mSwitcher == null || this.mSwitcher.getBackground() == null)) {
                this.mSwitcher.getBackground().setAlpha(this.mAlpha_value);
            }
        } else if (!(this.mSwitcherLever == null || this.mSwitcherLever.getBackground() == null)) {
            this.mSwitcherLever.getBackground().setAlpha(this.mAlpha_value);
            this.mGet.findViewById(R.id.switcher_lever_vertical).getBackground().setAlpha(this.mAlpha_value);
            this.mGet.findViewById(R.id.mode_bg_camera).getBackground().setAlpha(this.mAlpha_value / SUB_BUTTON_BOTTOM);
            this.mGet.findViewById(R.id.mode_bg_video).getBackground().setAlpha(this.mAlpha_value / SUB_BUTTON_BOTTOM);
        }
        if (this.mShutterButton != null && this.mShutterButton.getBackground() != null) {
            this.mShutterButton.getBackground().setAlpha(this.mAlpha_value);
            this.mGet.findViewById(R.id.main_button).getBackground().setAlpha(this.mAlpha_value);
        }
    }

    public void initReleaseArea() {
        this.mReleaseArea = new ArrayList();
        this.mReleaseArea.add(ANI_NONE, new ReleaseArea(getPixelFromDimens(R.dimen.panel_touch_button1_releaseLeft), getPixelFromDimens(R.dimen.panel_touch_button1_releaseTop), getPixelFromDimens(R.dimen.panel_touch_button1_releaseRight), getPixelFromDimens(R.dimen.panel_touch_button1_releaseBottom)));
        this.mReleaseArea.add(SUB_BUTTON_MIDDLE, new ReleaseArea(getPixelFromDimens(R.dimen.panel_touch_button2_releaseLeft), getPixelFromDimens(R.dimen.panel_touch_button2_releaseTop), getPixelFromDimens(R.dimen.panel_touch_button2_releaseRight), getPixelFromDimens(R.dimen.panel_touch_button2_releaseBottom)));
        this.mReleaseArea.add(SUB_BUTTON_BOTTOM, new ReleaseArea(getPixelFromDimens(R.dimen.panel_touch_button3_releaseLeft), getPixelFromDimens(R.dimen.panel_touch_button3_releaseTop), getPixelFromDimens(R.dimen.panel_touch_button3_releaseRight), getPixelFromDimens(R.dimen.panel_touch_button3_releaseBottom)));
    }

    private void setLockConditionForMainButton(boolean bSoundLock) {
        registerLockConditionForMainButton(CameraConstants.STORAGECONTROLLER_LOCKKEY);
        if (bSoundLock) {
            registerLockConditionForMainButton(CameraConstants.SOUNDCONTROLLER_LOCKKEY);
        }
    }

    public void registerLockConditionForMainButton(String lockKey) {
        if (lockKey == null) {
            CamLog.d(FaceDetector.TAG, String.format("return by lockKey == null", new Object[ANI_NONE]));
            return;
        }
        String str = FaceDetector.TAG;
        Object[] objArr = new Object[SUB_BUTTON_MIDDLE];
        objArr[ANI_NONE] = lockKey;
        CamLog.d(str, String.format("lock key: %s", objArr));
        this.mHashMap.put(lockKey, Boolean.valueOf(true));
    }

    public void setUnlockConditionForMainButton(String lockKey) {
        if (lockKey == null) {
            CamLog.d(FaceDetector.TAG, String.format("return by lockKey == null", new Object[ANI_NONE]));
            return;
        }
        String str = FaceDetector.TAG;
        Object[] objArr = new Object[SUB_BUTTON_MIDDLE];
        objArr[ANI_NONE] = lockKey;
        CamLog.d(str, String.format("unlock key: %s", objArr));
        if (this.mHashMap.containsKey(lockKey)) {
            this.mHashMap.put(lockKey, Boolean.valueOf(false));
        }
    }

    private boolean checkLockConditionForMainButton() {
        for (String keyItem : this.mHashMap.keySet()) {
            if (((Boolean) this.mHashMap.get(keyItem)).booleanValue()) {
                String str = FaceDetector.TAG;
                Object[] objArr = new Object[SUB_BUTTON_MIDDLE];
                objArr[ANI_NONE] = keyItem;
                CamLog.d(str, String.format("Locked by key:%s", objArr));
                return true;
            }
        }
        return false;
    }

    public void setMainButtonEnable() {
        setMainButtonEnable(null);
    }

    public void setMainButtonEnable(String lockKey) {
        CamLog.d(FaceDetector.TAG, "setMainButtonEnable, " + lockKey);
        Log.d(FaceDetector.TAG, "TIME CHECK : Shot to Shot [END] - setMainButtonEnable()");
        setUnlockConditionForMainButton(lockKey);
        if (checkLockConditionForMainButton()) {
            CamLog.d(FaceDetector.TAG, String.format("return by checkLockConditionForMainButton() == true", new Object[ANI_NONE]));
            return;
        }
        this.mShutterButton.setEnabled(true);
        if (!(this.mGet.getApplicationMode() == SUB_BUTTON_MIDDLE && this.mGet.getVideoState() == ANI_FINISHED)) {
            setShutterButtonImage(true, this.mGet.getOrientationDegree());
        }
        this.mGet.findViewById(R.id.main_button_view).bringToFront();
    }

    public void setMainButtonDisable() {
        CamLog.d(FaceDetector.TAG, "setMainButtonDisable");
        if (this.mShutterButtonLongKey || this.mShutterBurstShot) {
            CamLog.d(FaceDetector.TAG, "setMainButtonDisable return");
            return;
        }
        setShutterButtonImage(false, this.mGet.getOrientationDegree());
        if (this.mShutterButton != null && !this.mShutterButtonLongKey) {
            this.mShutterButton.setPressed(false);
            this.mShutterButton.setEnabled(false);
            this.mGet.findViewById(R.id.main_button).setPressed(false);
        }
    }

    public void setShutterButtonImage(boolean buttonEnable, int degree) {
        CamLog.d(FaceDetector.TAG, "setShutterButtonImage : status = " + buttonEnable + ", degree = " + degree);
        if (!this.mInit || this.mShutterButton == null) {
            CamLog.d(FaceDetector.TAG, String.format("return by !mInit", new Object[ANI_NONE]));
            return;
        }
        int bgResId;
        this.mGet.findViewById(R.id.main_button).setBackgroundResource(R.drawable.selector_main_cam_btn);
        if (this.mGet.getApplicationMode() == 0) {
            bgResId = setCameraShutterButtonImage(buttonEnable, degree);
        } else {
            bgResId = setCamcorderShutterButtonImage(buttonEnable, degree);
        }
        this.mGet.findViewById(R.id.main_button_bg).setBackgroundResource(bgResId);
        this.mGet.findViewById(R.id.main_button_bg).getBackground().setAlpha(this.mAlpha_value);
        this.mGet.findViewById(R.id.main_button).getBackground().setAlpha(this.mAlpha_value);
        setMainButtonContentDescription();
    }

    private int setCameraShutterButtonImage(boolean buttonEnable, int degree) {
        View mainButton = this.mGet.findViewById(R.id.main_button);
        View mainButtonBg = this.mGet.findViewById(R.id.main_button_bg);
        if (mainButton == null || mainButtonBg == null) {
            return ANI_NONE;
        }
        int mainButtonResId = R.drawable.selector_main_cam_btn;
        int shutterDesId = R.string.accessibility_shutter_button;
        if (buttonEnable) {
            if (this.mGet.isPanoramaUpdatebutton()) {
                mainButtonResId = R.drawable.selector_main_stop_btn;
                shutterDesId = R.string.camera_accessibility_stop_button;
            } else if (this.mGet.isSynthesisInProgress()) {
                mainButtonResId = R.drawable.btn_stop_dim;
                shutterDesId = R.string.camera_accessibility_stop_button;
            } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
                if (this.mGet.getPlanePanoramaStatus() == ANI_FINISHED) {
                    mainButtonResId = R.drawable.selector_main_stop_btn;
                    shutterDesId = R.string.camera_accessibility_stop_button;
                    mainButtonBg.setEnabled(true);
                } else if (this.mGet.getPlanePanoramaStatus() == SUB_BUTTON_BOTTOM || this.mGet.getPlanePanoramaStatus() == SUB_BUTTON_MIDDLE || this.mGet.getPlanePanoramaStatus() == 4) {
                    mainButtonResId = R.drawable.btn_stop_dim;
                    shutterDesId = R.string.camera_accessibility_stop_button;
                    mainButtonBg.setEnabled(false);
                } else {
                    mainButtonResId = R.drawable.selector_main_cam_btn;
                    shutterDesId = R.string.accessibility_shutter_button;
                    mainButtonBg.setEnabled(true);
                }
            } else if (!this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
                mainButtonResId = R.drawable.selector_main_cam_btn;
                shutterDesId = R.string.accessibility_shutter_button;
            } else if (this.mGet.getFreePanoramaStatus() == SUB_BUTTON_MIDDLE || this.mGet.getFreePanoramaStatus() == SUB_BUTTON_BOTTOM || this.mGet.getFreePanoramaStatus() == 4) {
                mainButtonResId = R.drawable.btn_stop_dim;
                shutterDesId = R.string.camera_accessibility_stop_button;
                mainButtonBg.setEnabled(false);
            } else if (this.mGet.getFreePanoramaStatus() == ANI_FINISHED) {
                mainButtonResId = R.drawable.selector_main_stop_btn;
                shutterDesId = R.string.camera_accessibility_stop_button;
                mainButtonBg.setEnabled(true);
            } else {
                mainButtonResId = R.drawable.selector_main_cam_btn;
                shutterDesId = R.string.accessibility_shutter_button;
                mainButtonBg.setEnabled(true);
            }
        } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
            if (this.mGet.isPanoramaUpdatebutton() || this.mGet.isSynthesisInProgress()) {
                mainButtonResId = R.drawable.btn_stop_dim;
                shutterDesId = R.string.camera_accessibility_stop_button;
            }
        } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
            if (this.mGet.getPlanePanoramaStatus() >= 4) {
                mainButtonResId = R.drawable.btn_stop_dim;
                shutterDesId = R.string.camera_accessibility_stop_button;
            }
        } else if (!this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA) || this.mGet.getFreePanoramaStatus() < SUB_BUTTON_BOTTOM) {
            mainButtonResId = R.drawable.btn_shutter_dim;
            shutterDesId = R.string.accessibility_shutter_button;
        } else {
            mainButtonResId = R.drawable.btn_stop_dim;
            shutterDesId = R.string.camera_accessibility_stop_button;
        }
        mainButton.setBackgroundResource(mainButtonResId);
        this.mShutterButton.setContentDescription(this.mGet.getString(shutterDesId));
        return setShutterButtonBgOrientation(degree, true);
    }

    private int setCamcorderShutterButtonImage(boolean buttonEnable, int degree) {
        int videoState = this.mGet.getVideoState();
        boolean enable = true;
        if (!buttonEnable) {
            this.mGet.findViewById(R.id.main_button).setBackgroundResource(R.drawable.btn_rec_dim);
            enable = false;
        } else if (videoState == ANI_FINISHED) {
            if (MultimediaProperties.isPauseAndResumeSupported()) {
                this.mGet.findViewById(R.id.main_button).setBackgroundResource(R.drawable.selector_main_pause_btn);
            } else {
                this.mGet.findViewById(R.id.main_button).setBackgroundResource(R.drawable.selector_main_stop_btn);
            }
        } else if (videoState == 0 || videoState == 5) {
            this.mGet.findViewById(R.id.main_button).setBackgroundResource(R.drawable.selector_main_rec_btn);
            this.mGet.findViewById(R.id.main_button_animation).setVisibility(8);
        } else if (videoState == 4 && MultimediaProperties.isPauseAndResumeSupported()) {
            this.mGet.findViewById(R.id.main_button).setBackgroundResource(R.drawable.selector_main_rec_btn);
            this.mGet.findViewById(R.id.main_button_animation).setVisibility(8);
        } else {
            this.mGet.findViewById(R.id.main_button).setBackgroundResource(R.drawable.btn_rec_dim);
            if (videoState == SUB_BUTTON_BOTTOM) {
                this.mGet.findViewById(R.id.main_button_animation).setVisibility(8);
            }
            enable = false;
        }
        return setShutterButtonBgOrientation(degree, enable);
    }

    private int setShutterButtonBgOrientation(int degree, boolean enable) {
        RotateLayout rl = (RotateLayout) this.mGet.findViewById(R.id.main_button_layout);
        if (rl == null) {
            return ANI_NONE;
        }
        boolean isHorizontal;
        int bgResId;
        int convDegree = Util.convertDegree(this.mGet.getResources(), degree);
        if (convDegree % MediaProviderUtils.ROTATION_180 == 0) {
            isHorizontal = true;
        } else {
            isHorizontal = false;
        }
        switch (convDegree) {
            case ANI_NONE /*0*/:
            case MediaProviderUtils.ROTATION_90 /*90*/:
                if (this.mGet.isConfigureLandscape()) {
                    rl.rotateLayout(ANI_NONE);
                } else {
                    rl.rotateLayout(Tag.IMAGE_DESCRIPTION);
                }
                this.mGet.setDegree(R.id.main_button_bg, ANI_NONE, false);
                break;
            case MediaProviderUtils.ROTATION_180 /*180*/:
            case Tag.IMAGE_DESCRIPTION /*270*/:
                if (this.mGet.isConfigureLandscape()) {
                    rl.rotateLayout(ANI_NONE);
                } else {
                    rl.rotateLayout(Tag.IMAGE_DESCRIPTION);
                }
                this.mGet.setDegree(R.id.main_button_bg, MediaProviderUtils.ROTATION_180, false);
                break;
        }
        if (isHorizontal) {
            if (this.mGet.getApplicationMode() == 0 && CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE))) {
                bgResId = R.drawable.selector_main_smart_bg;
            } else {
                bgResId = R.drawable.selector_main_bg;
            }
        } else if (this.mGet.getApplicationMode() == 0 && CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE))) {
            bgResId = R.drawable.selector_main_smart_bg_ver;
        } else {
            bgResId = R.drawable.selector_main_bg_ver;
        }
        if (this.mGet.getApplicationMode() == SUB_BUTTON_MIDDLE && !enable) {
            if (isHorizontal) {
                bgResId = R.drawable.btn_bg;
            } else {
                bgResId = R.drawable.btn_bg_ver;
            }
        }
        return bgResId;
    }

    public boolean isShutterButtonEnable() {
        return this.mShutterButton != null && this.mShutterButton.isEnabled();
    }

    public void setMainButtonVisible(boolean visible) {
        CamLog.i(FaceDetector.TAG, "setMainbuttonVisible = " + visible);
        if (this.mGet.findViewById(R.id.main_button_view) == null) {
            CamLog.i(FaceDetector.TAG, "setMainbuttonVisible : main_button_view did not inflate!!");
        } else if (visible) {
            this.mGet.findViewById(R.id.main_button_view).setVisibility(ANI_NONE);
            this.mGet.findViewById(R.id.main_button_bg).setVisibility(ANI_NONE);
            this.mGet.findViewById(R.id.main_button).setVisibility(ANI_NONE);
        } else {
            this.mGet.findViewById(R.id.main_button_view).setVisibility(4);
            this.mGet.findViewById(R.id.main_button_bg).setVisibility(4);
            this.mGet.findViewById(R.id.main_button).setVisibility(4);
        }
    }

    public void setSwitcherVisible(boolean visible) {
        boolean z = true;
        CamLog.d(FaceDetector.TAG, "setSwitcherVisible = " + visible);
        int visibility = ANI_NONE;
        if (!visible) {
            visibility = 4;
        } else if (this.mGet.isAttachIntent()) {
            visibility = 4;
        }
        if (ProjectVariables.useToggleSwitcher()) {
            if (this.mSwitcher != null) {
                this.mGet.findViewById(R.id.switcher).setVisibility(visibility);
            }
        } else if (this.mSwitcherLever != null) {
            this.mGet.findViewById(R.id.switcher_lever_vertical).setVisibility(visibility);
            SwitcherLeverVertical switcherLeverVertical = this.mSwitcherLever;
            if (this.mGet.getApplicationMode() != SUB_BUTTON_MIDDLE) {
                z = false;
            }
            switcherLeverVertical.resetSwitcherLever(z);
        }
        setSwithcerEnable(visible);
    }

    public void enableCommand(boolean enable) {
        CamLog.i(FaceDetector.TAG, "enableCommand : " + enable);
        if (this.mInit) {
            setSwithcerEnable(enable);
            if (this.mGet.getApplicationMode() == 0) {
                if (enable) {
                    setMainButtonEnable();
                } else {
                    setMainButtonDisable();
                }
            }
            this.mGet.findViewById(R.id.sub_button1).setEnabled(enable);
            this.mGet.findViewById(R.id.sub_touch_button1).setEnabled(enable);
            if (this.mGet.getApplicationMode() == 0 && this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
                this.mGet.findViewById(R.id.sub_button2).setEnabled(true);
                this.mGet.findViewById(R.id.sub_touch_button2).setEnabled(true);
            } else {
                this.mGet.findViewById(R.id.sub_button2).setEnabled(enable);
                this.mGet.findViewById(R.id.sub_touch_button2).setEnabled(enable);
            }
            this.mGet.findViewById(R.id.sub_button3).setEnabled(enable);
            this.mGet.findViewById(R.id.sub_touch_button3).setEnabled(enable);
            if (this.mThumbUri != null && this.mThumbImage != null) {
                this.mGet.findViewById(R.id.review_thumbnail).setEnabled(enable);
            }
        }
    }

    public boolean isSwitcherLeverEnable() {
        if (this.mSwitcherLever == null) {
            this.mSwitcherLever = (SwitcherLeverVertical) this.mGet.findViewById(R.id.camera_switcher_vertical);
        }
        if (this.mSwitcherLever != null) {
            return this.mSwitcherLever.isEnabled();
        }
        return false;
    }

    public void setSwithcerEnable(boolean enable) {
        if (ProjectVariables.useToggleSwitcher()) {
            if (this.mSwitcher == null) {
                this.mSwitcher = (Switcher) this.mGet.findViewById(R.id.camera_switch);
            }
            if (this.mSwitcher != null) {
                this.mSwitcher.setEnabled(enable);
                return;
            }
            return;
        }
        if (this.mSwitcherLever == null) {
            this.mSwitcherLever = (SwitcherLeverVertical) this.mGet.findViewById(R.id.camera_switcher_vertical);
        }
        if (this.mSwitcherLever != null) {
            this.mSwitcherLever.setEnabled(enable);
        }
    }

    public void resetSwitcherLever() {
        boolean z = true;
        if (this.mSwitcherLever != null) {
            SwitcherLeverVertical switcherLeverVertical = this.mSwitcherLever;
            if (this.mGet.getApplicationMode() != SUB_BUTTON_MIDDLE) {
                z = false;
            }
            switcherLeverVertical.resetSwitcherLever(z);
        }
    }

    public void setSubButton(int loc, int resId) {
        RotateImageButton ib;
        ImageView iv;
        CamLog.i(FaceDetector.TAG, "setSubButton " + loc + " to " + resId);
        switch (loc) {
            case ANI_NONE /*0*/:
                ib = this.mSubButtonTop;
                iv = this.mSubTouchButtonTop;
                break;
            case SUB_BUTTON_MIDDLE /*1*/:
                ib = this.mSubButtonMiddle;
                iv = this.mSubTouchButtonMiddle;
                break;
            case SUB_BUTTON_BOTTOM /*2*/:
                ib = this.mSubButtonBottom;
                iv = this.mSubTouchButtonBottom;
                break;
            default:
                return;
        }
        if (ib != null) {
            ib.setBackgroundResource(resId);
            if (resId == 0) {
                iv.setOnTouchListener(null);
                iv.setVisibility(4);
            } else {
                setSubButtonVisibilityWithTouchBotton(ib.getId(), ANI_NONE);
                iv.setOnTouchListener(this);
                ib.getBackground().setAlpha(this.mAlpha_value);
                setSubButtonContentDescription(iv, resId);
            }
            ib.setFocusable(false);
            ib.setPressed(false);
        }
    }

    public void showSubButtonInit(boolean updateThumb) {
        setSubButton(ANI_NONE, ANI_NONE);
        setSubButton(SUB_BUTTON_MIDDLE, ANI_NONE);
        setSubButton(SUB_BUTTON_BOTTOM, ANI_NONE);
        updateThumbnailButtonVisibility();
        if (updateThumb) {
            updateThumbnailButton();
        }
    }

    public void showLiveSnapshotButton() {
        setSubButton(ANI_NONE, R.drawable.selector_livesnapshot_btn);
        this.mGet.findViewById(R.id.sub_button1).setEnabled(true);
        this.mGet.findViewById(R.id.sub_touch_button1).setEnabled(true);
    }

    public void hideLiveSnapshotButton() {
        setSubButton(ANI_NONE, ANI_NONE);
    }

    public void showRecoridngStopButton() {
        if (this.mInit && !this.mGet.isPausing()) {
            setSubButton(SUB_BUTTON_BOTTOM, R.drawable.selector_recording_stop_btn);
            this.mSubButtonBottom.setEnabled(true);
            this.mGet.findViewById(R.id.sub_touch_button3).setEnabled(true);
        }
    }

    public void hideRecoridngStopButton() {
        setSubButton(SUB_BUTTON_BOTTOM, ANI_NONE);
    }

    public void performFocusOnShutterButton(boolean state) {
        this.mShutterButton.performFocus(state);
    }

    public boolean isPressedShutterButton() {
        if (this.mShutterButton == null || !this.mShutterButton.isPressed()) {
            return false;
        }
        return true;
    }

    public void getThumbnailAndUpdateButton() {
        if (this.mThumbController == null || this.mGet.isPausing()) {
            String str = FaceDetector.TAG;
            Object[] objArr = new Object[SUB_BUTTON_MIDDLE];
            objArr[ANI_NONE] = this.mThumbController;
            CamLog.w(str, String.format("getThumbnailAndUpdateButton() return mThumbController:%s", objArr));
        } else if (Common.useSecureLockImage() || AppControlUtil.checkGuestModeAndAppDisabled(this.mGet.getContentResolver(), true, SUB_BUTTON_MIDDLE)) {
            CamLog.d(FaceDetector.TAG, "SecureImageUtil = " + SecureImageUtil.get().getSecureLockUriList(this.mGet.getApplicationMode()).size());
            SecureImageUtil.get().checkSecureLockUriList(this.mGet.getActivity(), this.mGet.getApplicationMode());
            if (SecureImageUtil.get().isSecureLockUriListEmpty(this.mGet.getApplicationMode())) {
                this.mThumbUri = null;
                this.mThumbImage = null;
                if (this.mThumbController != null) {
                    this.mThumbController.setSecureDefaultImage(Common.useSecureLockImage());
                }
            } else {
                this.mThumbUri = (Uri) SecureImageUtil.get().getSecureLockUriList(this.mGet.getApplicationMode()).get(SecureImageUtil.get().getSecureLockUriListSize(this.mGet.getApplicationMode()) - 1);
                Bitmap temp = this.mGet.getLastThumbnail(this.mThumbUri);
                if (this.mThumbImage != null) {
                    this.mThumbImage.recycle();
                }
                this.mThumbImage = temp;
            }
            updateThumbnailButton();
        } else {
            if (this.mThumbController != null) {
                this.mThumbController.setSecureDefaultImage(Common.useSecureLockImage());
            }
            if (this.mGet.getApplicationMode() == 0 || (this.mGet.getVideoState() == ANI_FINISHED && FunctionProperties.isAvailableLiveShot())) {
                getThumbnailAndUpdateButton(SUB_BUTTON_MIDDLE);
            } else {
                getThumbnailAndUpdateButton(4);
            }
        }
    }

    public void getThumbnailAndUpdateButton(final int inclusion) {
        this.mThumbnailThread = new Thread() {
            public void run() {
                PreviewPanelController.this.mThumbUri = null;
                if (PreviewPanelController.this.mGet.checkActivity()) {
                    String strUri;
                    String savedPath;
                    if (PreviewPanelController.this.mGet.getApplicationMode() == 0 || (FunctionProperties.isAvailableLiveShot() && (PreviewPanelController.this.mGet.getVideoState() == PreviewPanelController.ANI_FINISHED || PreviewPanelController.this.mGet.getVideoState() == 4))) {
                        strUri = SharedPreferenceUtil.getLastPictureUri(PreviewPanelController.this.mGet.getApplicationContext());
                        savedPath = SharedPreferenceUtil.getLastPicturePath(PreviewPanelController.this.mGet.getApplicationContext());
                    } else {
                        strUri = SharedPreferenceUtil.getLastVideoUri(PreviewPanelController.this.mGet.getApplicationContext());
                        savedPath = SharedPreferenceUtil.getLastVideoPath(PreviewPanelController.this.mGet.getApplicationContext());
                    }
                    if (AppControlUtil.isGuestMode()) {
                        strUri = null;
                        savedPath = null;
                    } else if (!(savedPath == null || savedPath.contains(PreviewPanelController.this.mGet.getCurrentStorageDirectory()))) {
                        strUri = null;
                        savedPath = null;
                    }
                    if (isInterrupted()) {
                        CamLog.d(FaceDetector.TAG, "mThumbnailThread is isInterrupted()");
                        return;
                    }
                    String str = FaceDetector.TAG;
                    Object[] objArr = new Object[PreviewPanelController.SUB_BUTTON_MIDDLE];
                    objArr[PreviewPanelController.ANI_NONE] = strUri;
                    CamLog.d(str, String.format("Last uri:%s", objArr));
                    if (strUri != null) {
                        String pathFromUri = BitmapManager.getRealPathFromURI(PreviewPanelController.this.mGet.getActivity(), Uri.parse(strUri));
                        if (pathFromUri == null || !pathFromUri.equals(savedPath)) {
                            CamLog.d(FaceDetector.TAG, String.format("Saved uri is not valid. Find most recent uri.", new Object[PreviewPanelController.ANI_NONE]));
                            strUri = null;
                        }
                    }
                    if (isInterrupted()) {
                        CamLog.d(FaceDetector.TAG, "mThumbnailThread is isInterrupted()");
                        return;
                    }
                    synchronized (PreviewPanelController.this.mThumbnailLock) {
                        if (PreviewPanelController.this.checkMediator() && PreviewPanelController.this.mGet.checkActivity()) {
                            Bitmap temp;
                            if (strUri == null) {
                                PreviewPanelController.this.mThumbUri = PreviewPanelController.this.getMostRecentThumbnailUri(true, inclusion);
                                str = FaceDetector.TAG;
                                Object[] objArr2 = new Object[PreviewPanelController.SUB_BUTTON_MIDDLE];
                                objArr2[PreviewPanelController.ANI_NONE] = PreviewPanelController.this.mThumbUri;
                                CamLog.d(str, String.format("Found most recent uri:%s", objArr2));
                                temp = PreviewPanelController.this.mGet.getLastThumbnail(PreviewPanelController.this.mThumbUri);
                            } else {
                                PreviewPanelController.this.mThumbUri = Uri.parse(strUri);
                                temp = PreviewPanelController.this.mGet.getLastThumbnail(PreviewPanelController.this.mThumbUri);
                                if (temp == null) {
                                    CamLog.d(FaceDetector.TAG, String.format("Couldn't get thumbnail from Last uri", new Object[PreviewPanelController.ANI_NONE]));
                                    PreviewPanelController.this.mThumbUri = PreviewPanelController.this.getMostRecentThumbnailUri(true, inclusion);
                                    temp = PreviewPanelController.this.mGet.getLastThumbnail(PreviewPanelController.this.mThumbUri);
                                }
                            }
                            if (PreviewPanelController.this.mThumbImage != null) {
                                PreviewPanelController.this.mThumbImage.recycle();
                            }
                            PreviewPanelController.this.mThumbImage = temp;
                            if (isInterrupted()) {
                                CamLog.d(FaceDetector.TAG, "mThumbnailThread is isInterrupted()");
                                return;
                            }
                            if (PreviewPanelController.this.mThumbImage == null) {
                                CamLog.d(FaceDetector.TAG, String.format("Couldn't get any thumbnail. Leave it empty.", new Object[PreviewPanelController.ANI_NONE]));
                            }
                            PreviewPanelController.this.updateThumbnailButton();
                            return;
                        }
                    }
                }
            }
        };
        this.mThumbnailThread.start();
    }

    public Uri getMostRecentThumbnailUri(boolean isUseLinkedThumbnailList, int inclusion) {
        String bucketId = this.mGet.getStorageBucketId();
        if (!isUseLinkedThumbnailList || !StorageProperties.isAllMemorySupported()) {
            return getMostRecentThumbnailUri(bucketId, inclusion);
        }
        if (!this.mGet.checkActivity()) {
            return null;
        }
        Uri integratedUri = getMostRecentThumbnailUri(bucketId, inclusion);
        if (integratedUri != null) {
            return integratedUri;
        }
        if (StorageProperties.getEmmcName().equals(this.mGet.getSettingValue(Setting.KEY_STORAGE)) || StorageProperties.isInternalMemoryOnly()) {
            return getMostRecentThumbnailUri(this.mGet.getStorageBucketId(SUB_BUTTON_MIDDLE), inclusion);
        }
        return getMostRecentThumbnailUri(this.mGet.getStorageBucketId(ANI_NONE), inclusion);
    }

    public Uri getMostRecentThumbnailUri(String bucketId, int inclusion) {
        Uri uri;
        if (inclusion == SUB_BUTTON_MIDDLE) {
            uri = getLastImageThumbnail(bucketId);
            if (uri != null) {
                SharedPreferenceUtil.saveLastPicture(this.mGet.getActivity(), uri);
            }
        } else {
            uri = getLastVideoThumbnail(bucketId);
            if (uri != null) {
                SharedPreferenceUtil.saveLastVideo(this.mGet.getActivity(), uri);
            }
        }
        CamLog.i(FaceDetector.TAG, "getMostRecentThumbnailUri = " + uri);
        return uri;
    }

    public Uri getLastImageThumbnail(String bucketId) {
        Uri baseUri = Media.EXTERNAL_CONTENT_URI;
        Uri query = baseUri.buildUpon().appendQueryParameter("limit", "1").build();
        String[] projection = new String[ANI_FINISHED];
        projection[ANI_NONE] = "_id";
        projection[SUB_BUTTON_MIDDLE] = "orientation";
        projection[SUB_BUTTON_BOTTOM] = "datetaken";
        String selection = "mime_type='image/jpeg' AND bucket_id=" + bucketId;
        String order = "datetaken DESC,_id DESC";
        Cursor cursor = null;
        if (!this.mGet.checkActivity()) {
            return null;
        }
        try {
            CamLog.d(FaceDetector.TAG, "getContentResolver start");
            cursor = this.mGet.getContentResolver().query(query, projection, selection, null, order);
            CamLog.d(FaceDetector.TAG, "getContentResolver end");
            if (cursor == null || !cursor.moveToFirst()) {
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            }
            Uri withAppendedId = ContentUris.withAppendedId(baseUri, cursor.getLong(ANI_NONE));
            if (cursor == null) {
                return withAppendedId;
            }
            cursor.close();
            return withAppendedId;
        } catch (SQLiteException e) {
            CamLog.e(FaceDetector.TAG, "cursor error ", e);
            if (cursor != null) {
                cursor.close();
            }
        } catch (IllegalStateException e2) {
            CamLog.e(FaceDetector.TAG, "cursor error ", e2);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private Uri getLastVideoThumbnail(String bucketId) {
        Uri baseUri = Video.Media.EXTERNAL_CONTENT_URI;
        Uri query = baseUri.buildUpon().appendQueryParameter("limit", "1").build();
        String[] projection = new String[ANI_FINISHED];
        projection[ANI_NONE] = "_id";
        projection[SUB_BUTTON_MIDDLE] = "_data";
        projection[SUB_BUTTON_BOTTOM] = "datetaken";
        String selection = "bucket_id=" + bucketId;
        String order = "datetaken DESC,_id DESC";
        Cursor cursor = null;
        if (!this.mGet.checkActivity()) {
            return null;
        }
        try {
            cursor = this.mGet.getContentResolver().query(query, projection, selection, null, order);
            if (cursor == null || !cursor.moveToFirst()) {
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            }
            Log.d(FaceDetector.TAG, "getLastVideoThumbnail: " + cursor.getString(SUB_BUTTON_MIDDLE));
            Uri withAppendedId = ContentUris.withAppendedId(baseUri, cursor.getLong(ANI_NONE));
            if (cursor == null) {
                return withAppendedId;
            }
            cursor.close();
            return withAppendedId;
        } catch (SQLiteException e) {
            CamLog.e(FaceDetector.TAG, "cursor error ", e);
            if (cursor != null) {
                cursor.close();
            }
        } catch (IllegalStateException e2) {
            CamLog.e(FaceDetector.TAG, "cursor error ", e2);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean checkNeedUpdateLastThumb() {
        boolean result = false;
        String mode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
        if (CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA.equals(mode)) {
            result = this.mGet.getFreePanoramaStatus() == 4;
        } else if (CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA.equals(mode)) {
            result = this.mGet.getPlanePanoramaStatus() == 4;
        }
        CamLog.d(FaceDetector.TAG, "checkNeedUpdateLastThumb mode=" + mode + " result=" + result);
        return result;
    }

    public void setLastThumb(Uri uri, boolean isPicture) {
        boolean z = true;
        if (this.mThumbController == null || uri == null || (this.mGet.isPausing() && !checkNeedUpdateLastThumb())) {
            String str = FaceDetector.TAG;
            Object[] objArr = new Object[ANI_FINISHED];
            objArr[ANI_NONE] = this.mThumbController;
            objArr[SUB_BUTTON_MIDDLE] = Boolean.valueOf(this.mGet.isPausing());
            objArr[SUB_BUTTON_BOTTOM] = uri;
            CamLog.w(str, String.format("mThumbController:%s, isPausing():%b, uri:%s", objArr));
            CamLog.w(FaceDetector.TAG, String.format("setLastThumb() return", new Object[ANI_NONE]));
            return;
        }
        this.mThumbUri = uri;
        CamLog.d(FaceDetector.TAG, "Thumbnail Start");
        synchronized (this.mThumbnailLock) {
            Bitmap temp = this.mGet.getLastThumbnail(this.mThumbUri);
            if (this.mThumbImage != null) {
                this.mThumbImage.recycle();
            }
            this.mThumbImage = temp;
        }
        str = FaceDetector.TAG;
        StringBuilder append = new StringBuilder().append("Thumbnail End. mThumbImage is null?");
        if (this.mThumbImage != null) {
            z = false;
        }
        CamLog.d(str, append.append(z).toString());
        if (isPicture) {
            try {
                SharedPreferenceUtil.saveLastPicture(this.mGet.getActivity(), uri);
                return;
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "saveLastPicture error ", e);
                return;
            }
        }
        try {
            SharedPreferenceUtil.saveLastVideo(this.mGet.getActivity(), uri);
        } catch (Exception e2) {
            CamLog.e(FaceDetector.TAG, "saveLastVideo error ", e2);
        }
    }

    public void setThumbBitmapAndUpdate(Bitmap bmp, Uri uri) {
        CamLog.d(FaceDetector.TAG, "setThumbBitmapAndUpdate : bmp = " + bmp + ", uri = " + uri);
        if (this.mThumbController == null || bmp == null || uri == null) {
            this.mGet.doCommandDelayed(Command.UPDATE_THUMBNAIL_BUTTON, 100);
            return;
        }
        this.mGet.removeScheduledCommand(Command.UPDATE_THUMBNAIL_BUTTON);
        synchronized (this.mThumbnailLock) {
            this.mThumbUri = uri;
            if (this.mThumbImage != null) {
                this.mThumbImage.recycle();
            }
            this.mThumbImage = bmp;
        }
        updateThumbnailButtonVisibility();
        if (this.mThumbUri == null || this.mThumbImage == null) {
            this.mGet.doCommandDelayed(Command.UPDATE_THUMBNAIL_BUTTON, 100);
            return;
        }
        this.mLastPictureButton.setEnabled(true);
        this.mLastPictureButton.setImageBitmap(this.mThumbImage);
        this.mThumbController.setData(this.mThumbUri, this.mThumbImage, false);
    }

    public void setLastPictureThumb(byte[] data, Uri uri, boolean isPicture) {
        synchronized (this.mThumbnailLock) {
            if (this.mThumbController == null || uri == null || data == null) {
                String str = FaceDetector.TAG;
                Object[] objArr = new Object[ANI_FINISHED];
                objArr[ANI_NONE] = this.mThumbController;
                objArr[SUB_BUTTON_MIDDLE] = Boolean.valueOf(this.mGet.isPausing());
                objArr[SUB_BUTTON_BOTTOM] = uri;
                CamLog.w(str, String.format("mThumbController:%s, isPausing():%b, uri:%s", objArr));
                CamLog.w(FaceDetector.TAG, String.format("setLastThumb() return", new Object[ANI_NONE]));
                return;
            }
            this.mThumbUri = uri;
            if (this.mGet.isPausing()) {
                if (isPicture) {
                    SharedPreferenceUtil.saveLastPicture(this.mGet.getActivity(), uri);
                } else {
                    SharedPreferenceUtil.saveLastVideo(this.mGet.getActivity(), uri);
                }
                return;
            }
            CamLog.d(FaceDetector.TAG, "Thumbnail Start");
            if (isPicture) {
                SharedPreferenceUtil.saveLastPicture(this.mGet.getActivity(), uri);
                Options options = new Options();
                options.inSampleSize = Util.getSampleSize(data, null, null, options, this.mThumbController.getThumbnailWidth(), this.mThumbController.getThumbnailHeight());
                int degree = ExifUtil.getExifOrientationDegree(BitmapManager.getRealPathFromURI(this.mGet.getActivity(), uri));
                Bitmap temp = this.mGet.getImage(BitmapFactory.decodeByteArray(data, ANI_NONE, data.length, options), degree, false);
                if (this.mThumbImage != null) {
                    this.mThumbImage.recycle();
                }
                this.mThumbImage = temp;
            } else {
                SharedPreferenceUtil.saveLastVideo(this.mGet.getActivity(), uri);
                if (this.mThumbImage != null) {
                    this.mThumbImage.recycle();
                }
                this.mThumbImage = this.mGet.getLastThumbnail(this.mThumbUri);
            }
            CamLog.d(FaceDetector.TAG, "Thumbnail End");
        }
    }

    public void setThumbnailButtonVisibility(int visible) {
        if (this.mGet.findViewById(R.id.review_thumbnail) != null) {
            this.mGet.findViewById(R.id.review_thumbnail).setVisibility(visible);
        }
        if (this.mGet.getApplicationMode() != 0 || this.mGet.getCameraId() != 0 || visible != 0) {
            setTimeMachineReviewIconVisible(false);
            setRefocusReviewIconVisible(false);
            this.mGet.showBubblePopupVisibility(ANI_NONE, CameraConstants.TOAST_LENGTH_LONG, false);
            this.mGet.showBubblePopupVisibility(SUB_BUTTON_MIDDLE, CameraConstants.TOAST_LENGTH_LONG, false);
        } else if (this.mGet.isTimemachineHasPictures()) {
            setTimeMachineReviewIconVisible(true);
            autoReview = this.mGet.getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW);
            if ("on_delay_2sec".equals(autoReview)) {
                duration = CameraConstants.TOAST_LENGTH_SHORT;
            } else {
                duration = 0;
            }
            if ("on_delay_5sec".equals(autoReview)) {
                autoReviewDuration = CameraConstants.TOAST_LENGTH_LONG;
            } else {
                autoReviewDuration = duration;
            }
            if (autoReviewDuration == 0) {
                this.mGet.showBubblePopupVisibility(ANI_NONE, CameraConstants.TOAST_LENGTH_LONG, true);
            }
        } else if (this.mGet.isRefocusShotHasPictures()) {
            setRefocusReviewIconVisible(true);
            autoReview = this.mGet.getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW);
            if ("on_delay_2sec".equals(autoReview)) {
                duration = CameraConstants.TOAST_LENGTH_SHORT;
            } else {
                duration = 0;
            }
            if ("on_delay_5sec".equals(autoReview)) {
                autoReviewDuration = CameraConstants.TOAST_LENGTH_LONG;
            } else {
                autoReviewDuration = duration;
            }
            if (autoReviewDuration == 0) {
                this.mGet.showBubblePopupVisibility(SUB_BUTTON_MIDDLE, ProjectVariables.keepDuration, true);
            }
        } else {
            setTimeMachineReviewIconVisible(false);
            setRefocusReviewIconVisible(false);
            this.mGet.showBubblePopupVisibility(ANI_NONE, CameraConstants.TOAST_LENGTH_LONG, false);
            this.mGet.showBubblePopupVisibility(SUB_BUTTON_MIDDLE, CameraConstants.TOAST_LENGTH_LONG, false);
        }
    }

    public void updateThumbnailButtonVisibility() {
        int visible = ANI_NONE;
        if (this.mGet.isAttachIntent()) {
            visible = 8;
        }
        if (this.mGet.getApplicationMode() == SUB_BUTTON_MIDDLE) {
            if (MultimediaProperties.isPauseAndResumeSupported() && (this.mGet.getVideoState() == ANI_FINISHED || this.mGet.getVideoState() == 4)) {
                visible = 8;
            }
            if (this.mGet.getVideoState() == ANI_FINISHED) {
                if (!FunctionProperties.isAvailableLiveShot()) {
                    visible = 8;
                }
                String liveEffect = this.mGet.getSettingValue(Setting.KEY_LIVE_EFFECT);
                String recordMode = this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE);
                if (!(!MultimediaProperties.isLiveEffectSupported() || CameraConstants.SMART_MODE_OFF.equals(liveEffect) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(liveEffect)) || CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(recordMode) || CameraConstants.TYPE_RECORDMODE_DUAL.equals(recordMode) || MmsProperties.isAvailableMmsResolution(this.mGet.getContentResolver(), this.mGet.getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE))) {
                    visible = 8;
                }
            }
        }
        setThumbnailButtonVisibility(visible);
    }

    public void updateThumbnailButton() {
        this.mGet.postOnUiThread(new Runnable() {
            public void run() {
                PreviewPanelController.this.mGet.removePostRunnable(this);
                synchronized (PreviewPanelController.this.mThumbnailLock) {
                    if (PreviewPanelController.this.mThumbController != null) {
                        if (PreviewPanelController.this.mThumbUri == null || PreviewPanelController.this.mThumbImage == null) {
                            CamLog.d(FaceDetector.TAG, "Thumbnail : mThumbUri = " + PreviewPanelController.this.mThumbUri + ", mThumbImage = " + PreviewPanelController.this.mThumbImage);
                            PreviewPanelController.this.mThumbController.setData(null, null, true);
                            if (!Common.useSecureLockImage()) {
                                PreviewPanelController.this.mLastPictureButton.setEnabled(false);
                            }
                        } else {
                            PreviewPanelController.this.mThumbController.setData(PreviewPanelController.this.mThumbUri, PreviewPanelController.this.mThumbImage, true);
                            if (PreviewPanelController.this.mGet.isCamcorderRotation(true)) {
                                PreviewPanelController.this.mLastPictureButton.setEnabled(true);
                            } else {
                                PreviewPanelController.this.mLastPictureButton.setEnabled(false);
                            }
                        }
                        PreviewPanelController.this.mThumbController.startTransition(CameraConstants.GALLERY_QUICKVIEW_ANI_INTERVAL);
                    }
                }
            }
        });
    }

    public void setTimeMachineReviewIconVisible(final boolean show) {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                if (PreviewPanelController.this.mGet != null) {
                    PreviewPanelController.this.mGet.removePostRunnable(this);
                    int visible = (show && PreviewPanelController.this.mGet.getCameraId() == 0) ? PreviewPanelController.ANI_NONE : 4;
                    PreviewPanelController.this.mGet.findViewById(R.id.review_thumbnail_timemachine_icon).setVisibility(visible);
                }
            }
        });
    }

    public void setRefocusReviewIconVisible(final boolean show) {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                if (PreviewPanelController.this.mGet != null) {
                    PreviewPanelController.this.mGet.removePostRunnable(this);
                    int visible = (show && PreviewPanelController.this.mGet.getCameraId() == 0) ? PreviewPanelController.ANI_NONE : 4;
                    PreviewPanelController.this.mGet.findViewById(R.id.review_thumbnail_refocus_icon).setVisibility(visible);
                }
            }
        });
    }

    public void startRotation(int degree, boolean animation) {
        if (this.mGet.isCamcorderRotation(true)) {
            int convDegree = Util.convertDegree(this.mGet.getResources(), degree);
            this.mGet.setDegree(R.id.sub_button1, degree, ANI_NONE, animation);
            this.mGet.setDegree(R.id.sub_button2, degree, ANI_NONE, animation);
            this.mGet.setDegree(R.id.sub_button3, degree, ANI_NONE, animation);
            this.mGet.setDegree(R.id.review_thumbnail, degree, ANI_NONE, animation);
            this.mGet.setDegree(R.id.review_thumbnail_timemachine_icon, degree, ANI_NONE, animation);
            this.mGet.setDegree(R.id.review_thumbnail_refocus_icon, degree, ANI_NONE, animation);
            this.mGet.setDegree(R.id.gallery_quick_thumb_image, degree, ANI_NONE, animation);
            this.mGet.setDegree(R.id.camera_switch, degree, animation);
            this.mGet.setDegree(R.id.mode_bg_camera, convDegree, animation);
            this.mGet.setDegree(R.id.mode_bg_video, convDegree, animation);
            this.mGet.setDegree(R.id.camera_switcher_vertical, convDegree, animation);
            this.mGet.setDegree(R.id.smart_cover_window_exit_button, degree, ANI_NONE, animation);
            this.mGet.setDegree(R.id.smart_cover_window_shutter_button, degree, ANI_NONE, animation);
            if (degree == 0 || degree == MediaProviderUtils.ROTATION_180) {
                this.mGet.findViewById(R.id.switcher_lever_vertical).setBackgroundResource(R.drawable.mode_bg_new);
            } else {
                this.mGet.findViewById(R.id.switcher_lever_vertical).setBackgroundResource(R.drawable.mode_bg_new_ver);
            }
            this.mGet.setDegree(R.id.main_button, convDegree, animation);
            setShutterButtonImage(isShutterButtonEnable(), degree);
            if (this.mGalleryWindowAniState != SUB_BUTTON_BOTTOM) {
                try {
                    ((RotateLayout) this.mGet.findViewById(R.id.gallery_quick_window_rotate)).rotateLayout(degree);
                    ((RotateLayout) this.mGet.findViewById(R.id.switcher_lever_vertical_rotate_layout)).rotateLayout(this.mGet.isConfigureLandscape() ? ANI_NONE : Tag.IMAGE_DESCRIPTION);
                } catch (ClassCastException e) {
                    CamLog.w(FaceDetector.TAG, "ClassCastException:", e);
                }
            }
            this.mGet.setDegree(R.id.gallery_quick_window_trash, degree, animation);
        }
    }

    public ThumbnailController getThumbController() {
        return this.mThumbController;
    }

    public void onShutterButtonFocus(ShutterButton button, boolean pressed) {
        CamLog.d(FaceDetector.TAG, "onShutterButtonFocus pressed : " + pressed);
        this.mShutterBurstShot = false;
        if (checkForShutterButton(button, false, pressed)) {
            if (this.mGet.getApplicationMode() == 0) {
                if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) && (this.mGet.isPanoramaStarted() || this.mGet.isSynthesisInProgress())) {
                    this.mGet.findViewById(R.id.main_button).setPressed(pressed);
                    return;
                } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) && this.mGet.getPlanePanoramaStatus() >= ANI_FINISHED) {
                    this.mGet.findViewById(R.id.main_button).setPressed(pressed);
                    return;
                } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA) && this.mGet.getFreePanoramaStatus() >= ANI_FINISHED) {
                    this.mGet.findViewById(R.id.main_button).setPressed(pressed);
                    return;
                } else if (!this.mGet.checkCurrentShotModeForModule()) {
                    this.mGet.findViewById(R.id.main_button).setPressed(pressed);
                    return;
                } else if (pressed) {
                    if (this.mGet.checkAutoReviewOff(true)) {
                        this.mGet.setQuickFunctionAllMenuEnabled(false, true);
                        this.mGet.setQuickButtonMenuEnable(false, true);
                    } else {
                        this.mGet.setQuickFunctionAllMenuEnabled(false, true);
                        this.mGet.setQuickButtonMenuEnable(false, true);
                    }
                }
            } else if (this.mGet.getVideoState() == 0) {
                if (pressed) {
                    this.mGet.setQuickFunctionAllMenuEnabled(false, true);
                    this.mGet.setQuickButtonMenuEnable(false, true);
                } else {
                    this.mGet.quickFunctionControllerRefresh(true);
                    this.mGet.setQuickButtonMenuEnable(true, false);
                }
            }
            switch (button.getId()) {
                case R.id.main_button_bg /*2131558784*/:
                    if (StorageProperties.isExternalMemoryOnly() && this.mGet.isExternalStorageRemoved()) {
                        setMainButtonDisable();
                        this.mGet.setQuickFunctionAllMenuEnabled(true, true);
                        this.mGet.setQuickButtonMenuEnable(true, true);
                    }
                    this.mGet.findViewById(R.id.main_button).setPressed(pressed);
                    doCameraShutterButtonFocus(pressed);
                default:
            }
        }
    }

    public void doCameraShutterButtonFocus(boolean pressed) {
        if (checkAvailableCountForShutterButtonFocus(pressed) && isShutterButtonEnable()) {
            CamLog.i(FaceDetector.TAG, "onShutterButtonFocus : doFocus, pressed = " + pressed);
            if (pressed) {
                if (this.mGet.getSubMenuMode() != 0) {
                    if (this.mGet.checkAutoReviewOff(true)) {
                        this.mGet.clearSubMenu();
                    } else {
                        this.mGet.clearScreen();
                    }
                }
                if (FunctionProperties.isSupportBurstShot() && this.mGet.getCameraMode() == 0 && this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS)) {
                    if (this.mGet.getAvailablePictureCount() < 1) {
                        this.mGet.clearFocusState();
                        this.mGet.checkStorage(true);
                        return;
                    }
                    this.mShutterBurstShot = true;
                    this.mShutterButton.setShutterButtonReleaseImmediately(true);
                    this.mGet.doCommand(Command.DO_CAPTURE);
                    return;
                }
            } else if (FunctionProperties.isSupportBurstShot() && this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS) && this.mGet.getInCaptureProgress()) {
                this.mGet.stopByUserAction();
            } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) && this.mGet.isPanoramaStarted()) {
                if (!this.mShutterButton.isShutterButtonClicked()) {
                    this.mGet.quickFunctionControllerRefresh(true);
                    this.mGet.setQuickButtonMenuEnable(true, false);
                    this.mGet.showOsd();
                }
            } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) && this.mGet.getPlanePanoramaStatus() == ANI_FINISHED) {
                this.mGet.quickFunctionControllerRefresh(true);
                this.mGet.setQuickButtonMenuEnable(true, false);
                this.mGet.showOsd();
            } else if (!this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA) || this.mGet.getFreePanoramaStatus() < SUB_BUTTON_BOTTOM) {
                this.mGet.quickFunctionControllerRefresh(true);
                this.mGet.setQuickButtonMenuEnable(true, false);
                this.mGet.showOsd();
                this.mGet.doCommand(Command.RELEASE_TOUCH_FOCUS);
                if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) {
                    this.mGet.showBeautyShotBarForNewUx(true);
                }
            } else if (this.mGet.getFreePanoramaStatus() != SUB_BUTTON_BOTTOM) {
                this.mGet.quickFunctionControllerRefresh(true);
                this.mGet.setQuickButtonMenuEnable(true, false);
                this.mGet.showOsd();
            }
            if (!(this.mGet.getCameraMode() == SUB_BUTTON_MIDDLE || this.mGet.isCafSupported() || this.mGet.getFocusState() != 0 || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)))) {
                this.mGet.cancelAutoFocus();
                this.mGet.doFocus(pressed);
                this.mGet.setShutterButtonClicked(false);
            }
            if (!pressed && this.mGet.getCameraMode() == SUB_BUTTON_MIDDLE) {
                this.mGet.quickFunctionControllerRefresh(true);
                this.mGet.showOsd();
                return;
            }
            return;
        }
        this.mGet.hideFocus();
    }

    private boolean checkAvailableCountForShutterButtonFocus(boolean pressed) {
        if (this.mGet.getApplicationMode() != 0) {
            return false;
        }
        if (this.mGet.getAvailablePictureCount() >= 1) {
            return true;
        }
        this.mGet.doCommand(Command.RELEASE_TOUCH_FOCUS);
        if (!pressed) {
            return false;
        }
        this.mGet.quickFunctionControllerRefresh(true);
        this.mGet.setQuickButtonMenuEnable(true, false);
        this.mGet.showOsd();
        return false;
    }

    public void onShutterButtonClick(ShutterButton button) {
        boolean z = true;
        CamLog.d(FaceDetector.TAG, "TIME_CHECK onShutterButtonClick");
        Log.d(FaceDetector.TAG, "TIME CHECK : Shot to Shot [START] - onShutterButtonClick");
        String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
        if (checkForShutterButton(button, true, false)) {
            switch (button.getId()) {
                case R.id.main_button_bg /*2131558784*/:
                    ShutterButton shutterButton;
                    if (this.mGet.getApplicationMode() == 0) {
                        if (!checkShotModeForShutterButtonClick(button)) {
                            if (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_VOICESHUTTER)) && CheckStatusManager.checkVoiceShutterEnable(shotMode)) {
                                this.mGet.audioCallbackRestartEngine();
                            }
                            this.mGet.startGestureEngine();
                            return;
                        } else if (checkFocusStateForShutterButtonClick(button)) {
                            this.mSnapshotOnIdle = false;
                            this.mSnapshotOnContinuousFocus = false;
                            shutterButton = this.mShutterButton;
                            if (this.mSnapshotOnContinuousFocus) {
                                z = false;
                            }
                            shutterButton.setShutterButtonFocusEnable(z);
                            doCameraShutterButtonClick(button);
                        } else {
                            this.mGet.startGestureEngine();
                            return;
                        }
                    } else if (this.mGet.isRecordingControllerInit()) {
                        this.mSnapshotOnIdle = false;
                        this.mSnapshotOnContinuousFocus = false;
                        shutterButton = this.mShutterButton;
                        if (this.mSnapshotOnContinuousFocus) {
                            z = false;
                        }
                        shutterButton.setShutterButtonFocusEnable(z);
                        doCamcorderShutterButtonClick(button);
                    } else {
                        CamLog.d(FaceDetector.TAG, "RecordingController not ready");
                        return;
                    }
                    setMainButtonContentDescription();
                    return;
                default:
                    return;
            }
        }
        if (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_VOICESHUTTER)) && CheckStatusManager.checkVoiceShutterEnable(shotMode)) {
            this.mGet.audioCallbackRestartEngine();
        }
        this.mGet.startGestureEngine();
    }

    private boolean checkShotModeForShutterButtonClick(ShutterButton button) {
        if (CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)) && !this.mGet.isCompleteProcessFrame() && !this.mGet.isDualCameraActive()) {
            return false;
        }
        if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS) && FunctionProperties.isSupportBurstShot()) {
            if (this.mGet.getAvailablePictureCount() >= 1) {
                return false;
            }
            this.mGet.clearFocusState();
            this.mGet.checkStorage(true);
            return false;
        } else if (!this.mGet.checkCurrentShotModeForModule()) {
            return false;
        } else {
            if (!this.mGet.getInCaptureProgress()) {
                return true;
            }
            if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) && this.mGet.isPanoramaStarted()) {
                this.mGet.stopPanorama();
                this.mGet.playRecordingSound(false);
                return false;
            } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) && this.mGet.getPlanePanoramaStatus() == ANI_FINISHED) {
                this.mGet.stopPlanePanorama();
                return false;
            } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA) && this.mGet.getFreePanoramaStatus() == ANI_FINISHED) {
                this.mGet.stopFreePanorama();
                return false;
            } else if (this.mShutterButtonLongKey) {
                return false;
            } else {
                button.setPressed(false);
                this.mGet.findViewById(R.id.main_button).setPressed(false);
                CamLog.d(FaceDetector.TAG, "mSnapshotOnIdle = true");
                this.mSnapshotOnIdle = true;
                return false;
            }
        }
    }

    private boolean checkFocusStateForShutterButtonClick(ShutterButton button) {
        boolean z = true;
        ShutterButton shutterButton;
        if (this.mGet.getCameraMode() == 0 && this.mGet.isContinuousFocusActivating() && !FunctionProperties.isSupportAFonCAF() && !this.mShutterButtonLongKey) {
            button.setPressed(false);
            this.mGet.findViewById(R.id.main_button).setPressed(false);
            CamLog.d(FaceDetector.TAG, "mSnapshotOnContinuousFocus = true");
            this.mSnapshotOnContinuousFocus = true;
            shutterButton = this.mShutterButton;
            if (this.mSnapshotOnContinuousFocus) {
                z = false;
            }
            shutterButton.setShutterButtonFocusEnable(z);
            return false;
        } else if (this.mGet.getCameraMode() != 0 || !FunctionProperties.isSupportAFonCAF() || !checkAfOnCafContition() || this.mGet.getAvailablePictureCount() < 1) {
            return true;
        } else {
            CamLog.d(FaceDetector.TAG, "mSnapshotOnContinuousFocus = true");
            this.mSnapshotOnContinuousFocus = true;
            shutterButton = this.mShutterButton;
            if (this.mSnapshotOnContinuousFocus) {
                z = false;
            }
            shutterButton.setShutterButtonFocusEnable(z);
            this.mGet.doFocusOnCaf();
            return false;
        }
    }

    private boolean checkForShutterButton(ShutterButton button, boolean useClick, boolean pressed) {
        if (checkMediator()) {
            if (!useClick) {
                this.mGet.removeScheduledCommand(Command.RELEASE_TOUCH_FOCUS);
                if (!pressed) {
                    Log.d(FaceDetector.TAG, "stopTimerTask");
                    stopTimerTask();
                    releaseShutterFocus();
                    button.setPressed(false);
                    this.mGet.findViewById(R.id.main_button).setPressed(false);
                }
            }
            if ((!this.mGet.isPreviewing() && !this.mGet.getInCaptureProgress()) || this.mGet.isPausing() || this.mGet.getDialogID() != -1 || this.mAutoReviewBlockTouch || this.mGet.isEnteringViewShowing()) {
                CamLog.d(FaceDetector.TAG, "onShutterButtonClick return.");
                return false;
            }
            if (this.mGet.isOptionMenuShowing()) {
                this.mGet.hideOptionMenu();
            }
            if (this.mGet.getEnableInput() && isShutterButtonEnable() && this.mShutterButton.getVisibility() == 0 && this.mGet.getStatus() != SUB_BUTTON_BOTTOM) {
                return true;
            }
            if ((useClick && !this.mShutterButtonLongKey) || (!useClick && pressed)) {
                button.setPressed(false);
                this.mGet.findViewById(R.id.main_button).setPressed(false);
            } else if (!(useClick || pressed || this.mGalleryWindowAniState == 0)) {
                this.mGet.quickFunctionControllerRefresh(true);
                this.mGet.setQuickButtonMenuEnable(true, false);
            }
            CamLog.d(FaceDetector.TAG, "return by enable false");
            return false;
        }
        CamLog.d(FaceDetector.TAG, "return by !checkMediator()");
        return false;
    }

    private void doCameraShutterButtonClick(ShutterButton button) {
        this.mGet.setShutterButtonClicked(true);
        if (this.mGet.getAvailablePictureCount() < 1) {
            this.mGet.clearFocusState();
            this.mGet.checkStorage(true);
            if (this.mShutterButtonLongKey) {
                onShutterButtonFocus(button, false);
            }
            this.mGet.audioCallbackRestartEngine();
            this.mGet.startGestureEngine();
            return;
        }
        if (this.mGet.getSubMenuMode() != 0) {
            if (this.mGet.checkAutoReviewOff(true)) {
                this.mGet.clearSubMenu();
            } else {
                this.mGet.clearScreen();
            }
        }
        if ("0".equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_TIMER)) || !this.mGet.isTimerShotCountdown()) {
            if (this.mGet.checkAutoReviewOff(true)) {
                this.mGet.setQuickFunctionAllMenuEnabled(false, true);
                this.mGet.setQuickButtonMenuEnable(false, true);
            } else {
                this.mGet.setQuickFunctionAllMenuEnabled(false, true);
                this.mGet.setQuickButtonMenuEnable(false, true);
            }
            if (this.mGet.isDualCameraActive()) {
                this.mGet.restoreSubWindow();
                this.mGet.doCommand(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
            }
            String flashMode = this.mGet.getSettingValue(Setting.KEY_FLASH);
            if (FunctionProperties.isSupportGuideFlash() && this.mGet.getCameraMode() == 0 && CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE))) {
                if (!(!this.mGet.getIAFlashStatus() || this.mGet.getFocusState() == SUB_BUTTON_MIDDLE || this.mGet.getFocusState() == ANI_FINISHED)) {
                    CamLog.d(FaceDetector.TAG, "doFocus IA mode: Flash on or auto");
                    this.mGet.cancelAutoFocus();
                    this.mGet.doFocus(true);
                }
            } else if (!(!FunctionProperties.isSupportGuideFlash() || this.mGet.getCameraMode() != 0 || CameraConstants.SMART_MODE_OFF.equals(flashMode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(flashMode) || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)) || !"0".equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_TIMER)) || this.mGet.getFocusState() == 5 || this.mGet.getFocusState() == 6 || this.mGet.getFocusState() == SUB_BUTTON_MIDDLE || this.mGet.getFocusState() == ANI_FINISHED)) {
                if (!ModelProperties.isRenesasISP() || !LGT_Limit.ISP_AUTOMODE_AUTO.equals(flashMode)) {
                    CamLog.d(FaceDetector.TAG, "doFocus : Flash on or auto");
                    this.mGet.cancelAutoFocus();
                    this.mGet.doFocus(true);
                } else if (this.mGet.getParameters() != null) {
                    if ("1".equals(this.mGet.getParameters().get("is-lowlight"))) {
                        CamLog.d(FaceDetector.TAG, "doFocus : Flash auto and is-lowlight = 1");
                        this.mGet.cancelAutoFocus();
                        this.mGet.doFocus(true);
                    }
                }
            }
            setShutterFocusLongKey(false);
            this.mGet.doCommand(Command.DO_CAPTURE);
            return;
        }
        this.mGet.audioCallbackRestartEngine();
        this.mGet.startGestureEngine();
    }

    private void doCamcorderShutterButtonClick(ShutterButton button) {
        clearSettingMenuAndSubMenu();
        switch (this.mGet.getVideoState()) {
            case ANI_NONE /*0*/:
                CamLog.d(FaceDetector.TAG, "VideoState = VIDEO_STATE_IDLE");
                if (TelephonyUtil.phoneInCall(this.mGet.getApplicationContext())) {
                    this.mGet.toast((int) R.string.error_video_recording_during_call);
                    return;
                }
                this.mGet.setVideoState(SUB_BUTTON_MIDDLE);
                this.mGet.setQuickFunctionAllMenuEnabled(false, true);
                if (this.mGet.isDualRecordingActive() || this.mGet.isSmartZoomRecordingActive()) {
                    this.mGet.restoreSubWindow();
                    this.mGet.doCommand(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
                }
                if (this.mGet.isAttachMode() && this.mGet.getRequestedVideoSizeLimit() != 0) {
                    String videoResolution = this.mGet.getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
                    if (CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE))) {
                        videoResolution = MultimediaProperties.getLiveEffectPreviewOnDevice(this.mGet.getCameraId());
                    }
                    if (MmsProperties.getAttachVideoMinimumSize(videoResolution, this.mGet.getActivity().getContentResolver()) > this.mGet.getRequestedVideoSizeLimit()) {
                        CamLog.d(FaceDetector.TAG, "#########  aileen minRequireSpace : " + MmsProperties.getAttachVideoMinimumSize(videoResolution, this.mGet.getActivity().getContentResolver()) + "getRequestedVideoSizeLimit : " + this.mGet.getRequestedVideoSizeLimit());
                        this.mGet.toast((int) R.string.sp_message_recording_limit_NORMAL);
                        this.mGet.setVideoState(ANI_NONE);
                        this.mGet.setQuickButtonForcedDisable(false);
                        this.mGet.setQuickFunctionMenuForcedDisable(false);
                        this.mGet.quickFunctionControllerRefresh(true);
                        this.mGet.setQuickButtonMenuEnable(true, true);
                        return;
                    }
                }
                CamLog.d(FaceDetector.TAG, "Here is shutterclick : storage is = " + this.mGet.getStorageState());
                if (this.mGet.getStorageState() != 0) {
                    this.mGet.showStorageHint(this.mGet.getStorageState());
                    this.mGet.setVideoState(ANI_NONE);
                    this.mGet.setQuickButtonForcedDisable(false);
                    this.mGet.setQuickFunctionMenuForcedDisable(false);
                    this.mGet.quickFunctionControllerRefresh(true);
                    this.mGet.setQuickButtonMenuEnable(true, true);
                    return;
                }
                this.mGet.removeScheduledCommand(Command.START_RECORDING);
                this.mGet.setVideoFlash(true);
                AudioUtil.setAudioFocus(this.mGet.getApplicationContext(), true, false);
                this.mGet.playRecordingSound(true);
                AppControlUtil.BlockAlarmInRecording(this.mGet.getActivity(), this.mGet.getApplicationMode());
                this.mGet.doCommandDelayed(Command.START_RECORDING, (long) MultimediaProperties.getStartRecordingSoundDelay());
            case SUB_BUTTON_MIDDLE /*1*/:
            case SUB_BUTTON_BOTTOM /*2*/:
                CamLog.d(FaceDetector.TAG, "VIDEO_STATE_NO_REACTION");
            case ANI_FINISHED /*3*/:
                CamLog.d(FaceDetector.TAG, "VIDEO_STATE_RECORDING");
                if (MultimediaProperties.isPauseAndResumeSupported()) {
                    if (!checkRecordingPauseAndResumePressTime() || !this.mGet.isAvailableResumeVideo()) {
                        return;
                    }
                    if (this.mGet.isRecordedLengthTooShort()) {
                        CamLog.d(FaceDetector.TAG, String.format("Ignore stop recording request. It's too short.", new Object[ANI_NONE]));
                    } else {
                        this.mGet.doCommandUi(Command.PAUSE_RECORDING);
                    }
                } else if (this.mGet.isRecordedLengthTooShort()) {
                    CamLog.d(FaceDetector.TAG, String.format("Ignore stop recording request. It's too short.", new Object[ANI_NONE]));
                } else {
                    this.mGet.doCommandUi(Command.STOP_RECORDING);
                }
            case LGKeyRec.EVENT_STOPPED /*4*/:
                CamLog.d(FaceDetector.TAG, "VIDEO_STATE_PAUSE");
                if (checkRecordingPauseAndResumePressTime()) {
                    this.mGet.doCommandUi(Command.RESUME_RECORDING);
                }
            default:
        }
    }

    static {
        PAUSE_RESUME_CHECK_DURATION = PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME;
    }

    private boolean checkRecordingPauseAndResumePressTime() {
        long now = System.currentTimeMillis();
        if (now - this.mPrevTime < 0) {
            CamLog.d(FaceDetector.TAG, "Time is somthing wrong! now = " + now + "mPrevTime = " + this.mPrevTime);
            this.mPrevTime = System.currentTimeMillis();
        }
        if (now - this.mPrevTime <= ((long) PAUSE_RESUME_CHECK_DURATION)) {
            return false;
        }
        this.mPrevTime = System.currentTimeMillis();
        return true;
    }

    public void clearSettingMenuAndSubMenu() {
        if (this.mGet.getSubMenuMode() == 0) {
            return;
        }
        if (this.mGet.getSubMenuMode() == 5 || this.mGet.getSubMenuMode() == 16) {
            this.mGet.setSubMenuMode(ANI_NONE);
            Bundle bundle = new Bundle();
            bundle.putBoolean("showAll", false);
            this.mGet.doCommand(Command.REMOVE_SETTING_MENU, bundle);
        } else if (this.mGet.getSubMenuMode() != 0) {
            if (this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE) == null) {
                this.mGet.restoreLiveEffectSubMenu();
            }
            this.mGet.clearQuickFunctionSubMenu();
            this.mGet.clearSubMenu();
        }
    }

    private void clearSettingMenuAndSubMenuForReviewButton() {
        if (this.mGet.getSubMenuMode() == 5 || this.mGet.getSubMenuMode() == 16) {
            this.mGet.setSubMenuMode(ANI_NONE);
            this.mGet.doCommandUi(Command.REMOVE_SETTING_MENU);
        } else if (this.mGet.getSubMenuMode() == 22) {
            this.mGet.setSubMenuMode(ANI_NONE);
            this.mGet.doCommandUi(Command.HIDE_QUICK_FUNCTION_DRAG_DROP);
        } else if (this.mGet.getSubMenuMode() == 21) {
            this.mGet.setSubMenuMode(ANI_NONE);
            this.mGet.doCommandUi(Command.HIDE_QUICK_FUNCTION_SETTING_MENU);
        } else if (this.mGet.getSubMenuMode() != 0) {
            this.mGet.clearSubMenu();
        }
    }

    private boolean checkOutRange(View v, int index, float x, float y) {
        CamLog.d(FaceDetector.TAG, "x = " + x + " / y = " + y);
        ReleaseArea releaseArea = (ReleaseArea) this.mReleaseArea.get(index);
        if (y < ((float) (-releaseArea.mTop)) || y > ((float) (v.getHeight() + releaseArea.mBottom)) || x < ((float) (-releaseArea.mLeft)) || x > ((float) (v.getWidth() + releaseArea.mRight))) {
            return true;
        }
        return false;
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (checkMediator()) {
            if (!this.mGet.isPausing()) {
                if (!this.mGet.isEnteringViewShowing()) {
                    switch (event.getAction()) {
                        case ANI_NONE /*0*/:
                            doTouchActionDown(v, event);
                            break;
                        case SUB_BUTTON_MIDDLE /*1*/:
                            doTouchActionUp(v, event);
                            break;
                        case SUB_BUTTON_BOTTOM /*2*/:
                            doTouchActionMove(v, event);
                            break;
                        default:
                            break;
                    }
                }
                CamLog.d(FaceDetector.TAG, "PreviewPanelController : Entering view is visible.");
            } else {
                CamLog.d(FaceDetector.TAG, "PreviewPanelController : Mediator is pausing.");
            }
        } else {
            CamLog.d(FaceDetector.TAG, "PreviewPanelController : Mediator is null.");
        }
        return true;
    }

    private boolean doTouchActionDown(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.sub_touch_button1 /*2131558789*/:
                switch (this.mSubButtonTop.getBackgroundResource()) {
                    case R.drawable.selector_livesnapshot_btn /*2130838669*/:
                        this.mSubButtonTop.setPressed(true);
                        break;
                    default:
                        CamLog.d(FaceDetector.TAG, "press no button");
                        break;
                }
            case R.id.sub_touch_button3 /*2131558793*/:
                switch (this.mSubButtonBottom.getBackgroundResource()) {
                    case R.drawable.selector_recording_stop_btn /*2130838686*/:
                    case R.drawable.selector_sub_btn_reset_free_panorama /*2130838693*/:
                        this.mSubButtonBottom.setPressed(true);
                        break;
                    default:
                        break;
                }
        }
        return true;
    }

    private boolean doTouchActionMove(View v, MotionEvent event) {
        if (this.checkAreaOnTouch) {
            switch (v.getId()) {
                case R.id.sub_touch_button1 /*2131558789*/:
                    if (checkOutRange(v, ANI_NONE, event.getX(), event.getY()) && this.mSubButtonTop.getBackgroundResource() == R.drawable.selector_livesnapshot_btn) {
                        CamLog.d(FaceDetector.TAG, "out range!!!");
                        this.mSubButtonTop.setPressed(false);
                        this.checkAreaOnTouch = false;
                        break;
                    }
                case R.id.sub_touch_button3 /*2131558793*/:
                    if (checkOutRange(v, SUB_BUTTON_BOTTOM, event.getX(), event.getY())) {
                        switch (this.mSubButtonBottom.getBackgroundResource()) {
                            case R.drawable.selector_recording_stop_btn /*2130838686*/:
                            case R.drawable.selector_sub_btn_reset_free_panorama /*2130838693*/:
                                this.mSubButtonBottom.setPressed(false);
                                this.checkAreaOnTouch = false;
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    private boolean doTouchActionUp(View v, MotionEvent event) {
        if (this.checkAreaOnTouch) {
            switch (v.getId()) {
                case R.id.sub_touch_button1 /*2131558789*/:
                    switch (this.mSubButtonTop.getBackgroundResource()) {
                        case R.drawable.selector_livesnapshot_btn /*2130838669*/:
                            this.mSubButtonTop.setPressed(false);
                            this.mGet.doCommand(Command.TAKE_PICTURE_IN_RECORDING);
                            break;
                        default:
                            break;
                    }
                case R.id.sub_touch_button3 /*2131558793*/:
                    switch (this.mSubButtonBottom.getBackgroundResource()) {
                        case R.drawable.selector_recording_stop_btn /*2130838686*/:
                            this.mSubButtonBottom.setPressed(false);
                            if (this.mGet.getVideoState() == ANI_FINISHED || this.mGet.getVideoState() == 4) {
                                CamLog.d(FaceDetector.TAG, "VIDEO_STATE_RECORDING");
                                if (this.mGet.isRecordedLengthTooShort()) {
                                    CamLog.d(FaceDetector.TAG, String.format("Ignore stop recording request. It's too short.", new Object[ANI_NONE]));
                                } else {
                                    clearSettingMenuAndSubMenu();
                                    this.mGet.doCommandUi(Command.STOP_RECORDING);
                                }
                            }
                            setMainButtonContentDescription();
                            break;
                        case R.drawable.selector_sub_btn_reset_free_panorama /*2130838693*/:
                            this.mSubButtonBottom.setPressed(false);
                            this.mSubButtonBottom.playSoundEffect(ANI_NONE);
                            this.mGet.doCommand(Command.RESET_FREE_PANORAMA);
                            break;
                        default:
                            break;
                    }
                default:
                    break;
            }
        }
        this.checkAreaOnTouch = true;
        return true;
    }

    public void setGalleryLaunching(boolean isLaunching) {
        this.mGalleryLaunching = isLaunching;
    }

    public boolean isGalleryLaunching() {
        return this.mGalleryLaunching;
    }

    public void closeGalleryQuickView(boolean deleteImage) {
        if (!(deleteImage || this.mTrashView == null || this.mLastGalleryImage == null)) {
            this.mTrashView.setPressed(false);
            this.mTrashView.setImageResource(R.drawable.camera_gallery_quickview_trash_close);
            this.mLastGalleryImage.setBackgroundResource(R.drawable.camera_gallery_quickview_bg);
        }
        showGalleryQuickViewAnimation(false, deleteImage);
        reviewThumbnailTouchActionUp();
    }

    public void reviewThumbnailTouchActionUp() {
        this.mTrashView = null;
        this.mLastGalleryImage = null;
        if (this.mQuickViewThumbLayout != null) {
            this.mQuickViewThumbLayout.setVisibility(4);
            this.mQuickViewThumbLayout = null;
        }
        if (this.mQuickViewThumbImage != null) {
            if (this.mQuickViewThumbImage.getDrawable() != null) {
                this.mQuickViewThumbImage.getDrawable().setCallback(null);
            }
            this.mQuickViewThumbImage.setImageDrawable(null);
            this.mQuickViewThumbImage = null;
        }
        this.mThumbParams = null;
    }

    private boolean checkTrashLocation(float inputX, float inputY) {
        if (this.mTrashView == null || this.mLastPictureButton == null) {
            return false;
        }
        int[] startPos = new int[SUB_BUTTON_BOTTOM];
        int[] trashPos = new int[SUB_BUTTON_BOTTOM];
        this.mLastPictureButton.getLocationOnScreen(startPos);
        this.mTrashView.getLocationOnScreen(trashPos);
        float trashWidth = (float) this.mTrashView.getMeasuredWidth();
        float trashHeight = (float) this.mTrashView.getMeasuredHeight();
        if (((float) startPos[ANI_NONE]) + inputX < ((float) trashPos[ANI_NONE]) || ((float) startPos[ANI_NONE]) + inputX > ((float) trashPos[ANI_NONE]) + trashWidth || ((float) startPos[SUB_BUTTON_MIDDLE]) + inputY < ((float) trashPos[SUB_BUTTON_MIDDLE]) || ((float) startPos[SUB_BUTTON_MIDDLE]) + inputY > ((float) trashPos[SUB_BUTTON_MIDDLE]) + trashHeight) {
            return false;
        }
        return true;
    }

    private boolean reviewThumbnailDoClickAction(View v, boolean longKey) {
        if (!checkMediator() || this.mGet.getVideoState() == SUB_BUTTON_MIDDLE || this.mGet.getVideoState() == SUB_BUTTON_BOTTOM || this.mGet.getVideoState() == ANI_FINISHED || this.mGet.getVideoState() == 4 || this.mShutterButtonLongKey || isLockDuringMediaScanning() || this.mGet.getInCaptureProgress() || this.mGet.getBeautyshotProgress()) {
            return false;
        }
        switch (v.getId()) {
            case R.id.review_thumbnail /*2131558802*/:
                if (this.mGet.isEnteringViewShowing() || this.mAutoReviewBlockTouch) {
                    return false;
                }
                if (this.mGalleryLaunching) {
                    CamLog.w(FaceDetector.TAG, String.format("Gallery is launching already.", new Object[ANI_NONE]));
                    return false;
                } else if (longKey) {
                    CamLog.d(FaceDetector.TAG, "Gallery key long key");
                    return showGalleryQuickViewWindow(true, 0);
                } else {
                    CamLog.d(FaceDetector.TAG, "goto gallery");
                    if (this.mGet.getTimeMachinePictures() || this.mGet.getRefocusPictures()) {
                        return true;
                    }
                    if (this.mGet.getQueueCount() > 10) {
                        this.mGet.setExitIgnoreDuringSaving(true);
                        this.mGet.showSavingProgressDialog();
                        return false;
                    }
                    if (AppControlUtil.checkGuestModeAndAppDisabled(this.mGet.getContentResolver(), true, SUB_BUTTON_MIDDLE)) {
                        this.mGalleryLaunching = false;
                    } else {
                        this.mGalleryLaunching = true;
                    }
                    galleryWindowViewClose();
                    this.mGet.doCommand(Command.SHOW_GALLERY);
                    return true;
                }
            default:
                return false;
        }
    }

    public boolean showGalleryQuickViewWindow(boolean useLongKey, long duration) {
        clearSettingMenuAndSubMenuForReviewButton();
        View galleryWindowView = this.mGet.findViewById(R.id.gallery_quick_window);
        View galleryWindowViewImage = this.mGet.findViewById(R.id.gallery_quick_window_rotate);
        if (this.mThumbUri == null || this.mThumbImage == null || galleryWindowView == null) {
            this.mGalleryWindowAniState = ANI_NONE;
            return false;
        }
        this.mQuickViewThumbLayout = this.mGet.findViewById(R.id.gallery_quick_thumb_layout);
        this.mQuickViewThumbImage = (RotateImageView) this.mGet.findViewById(R.id.gallery_quick_thumb_image);
        this.mThumbParams = (LayoutParams) this.mQuickViewThumbLayout.getLayoutParams();
        this.mTrashView = (RotateImageButton) this.mGet.findViewById(R.id.gallery_quick_window_trash);
        this.mTrashView.setImageResource(R.drawable.camera_gallery_quickview_trash_close);
        if (useLongKey) {
            this.mTrashView.setVisibility(ANI_NONE);
            synchronized (this.mThumbnailLock) {
                if (this.mThumbImage != null) {
                    int circleRadius = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.review_thumbnail_circle_radius);
                    int thmbSize = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.review_thumbnail_height);
                    Bitmap thumbBmp = Util.getRoundedImage(this.mThumbImage.copy(Config.ARGB_8888, true), thmbSize, thmbSize, circleRadius);
                    Util.clearImageViewDrawable(this.mQuickViewThumbImage);
                    this.mQuickViewThumbImage.setImageBitmap(thumbBmp);
                }
            }
            this.mQuickViewThumbImage.setAlpha(0.475f);
            if (ModelProperties.isSoftKeyNavigationBarModel()) {
                View galleryWindowLayout = this.mGet.findViewById(R.id.gallery_quick_window_layout);
                LayoutParams marginParams = (LayoutParams) galleryWindowLayout.getLayoutParams();
                Common.resetLayoutParameter(marginParams);
                if (Util.isConfigureLandscape(this.mGet.getResources())) {
                    marginParams.addRule(20, SUB_BUTTON_MIDDLE);
                    marginParams.rightMargin = this.NAVI_MARGIN;
                } else {
                    marginParams.addRule(10, SUB_BUTTON_MIDDLE);
                    marginParams.bottomMargin = this.NAVI_MARGIN;
                }
                galleryWindowLayout.setLayoutParams(marginParams);
            }
        } else {
            this.mAutoReviewBlockTouch = true;
            this.mTrashView.setVisibility(4);
            this.mQuickViewThumbLayout.setVisibility(4);
            this.mQuickViewThumbLayout = null;
            if (this.mQuickViewThumbImage != null) {
                Util.clearImageViewDrawable(this.mQuickViewThumbImage);
                this.mQuickViewThumbImage.setImageDrawable(null);
                this.mQuickViewThumbImage = null;
            }
        }
        this.mLastGalleryImage = (ImageView) this.mGet.findViewById(R.id.gallery_quick_window_last_image);
        this.mLastGalleryImage.setBackgroundResource(R.drawable.camera_gallery_quickview_bg);
        galleryWindowView.setVisibility(4);
        galleryWindowViewImage.setVisibility(4);
        setGalleryWindowImage(this.mThumbUri, ANI_NONE);
        this.mGalleryWindowAniState = SUB_BUTTON_MIDDLE;
        this.mGet.enableInput(false);
        this.mGet.postOnUiThread(new Runnable() {
            public void run() {
                PreviewPanelController.this.mGet.removePostRunnable(this);
                if (PreviewPanelController.this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) && PreviewPanelController.this.mGet.getApplicationMode() == 0) {
                    PreviewPanelController.this.mGet.removePanoramaView();
                }
                PreviewPanelController.this.showGalleryQuickViewAnimation(true, false);
            }
        });
        if (!useLongKey) {
            this.mGet.postOnUiThread(this.mHideQuickViewRunable, duration);
        }
        return true;
    }

    private void setGalleryWindowImage(final Uri uri, int degrees) {
        String str = FaceDetector.TAG;
        Object[] objArr = new Object[SUB_BUTTON_BOTTOM];
        objArr[ANI_NONE] = uri;
        objArr[SUB_BUTTON_MIDDLE] = Integer.valueOf(degrees);
        CamLog.d(str, String.format("Load captured image:%s, degrees:%d", objArr));
        if (this.mThumbImage != null) {
            synchronized (this.mThumbnailLock) {
                ImageView lastGalleryImage = (ImageView) this.mGet.findViewById(R.id.gallery_quick_window_last_image);
                int[] dstSize = new int[SUB_BUTTON_BOTTOM];
                dstSize = Util.getFitSizeOfBitmapForLCD(this.mGet.getActivity(), this.mThumbImage.getWidth(), this.mThumbImage.getHeight());
                try {
                    if (!this.mThumbImage.isRecycled()) {
                        Bitmap thumbBmp = this.mThumbImage.copy(Config.ARGB_8888, true);
                        Bitmap resizeThumbBmp = Bitmap.createScaledBitmap(thumbBmp, dstSize[ANI_NONE], dstSize[SUB_BUTTON_MIDDLE], true);
                        Util.clearImageViewDrawable(lastGalleryImage);
                        lastGalleryImage.setImageBitmap(resizeThumbBmp);
                        if (!(thumbBmp == null || resizeThumbBmp == null || thumbBmp.hashCode() == resizeThumbBmp.hashCode())) {
                            thumbBmp.recycle();
                        }
                    }
                } catch (Exception e) {
                    CamLog.w(FaceDetector.TAG, "setGalleryWindowImage exception : ", e);
                }
            }
            new Thread(new Runnable() {
                public void run() {
                    if (PreviewPanelController.this.mGet.checkActivity() && PreviewPanelController.this.mGet.getActivity() != null) {
                        Bitmap resizeFullBmp = null;
                        int[] dstSize = new int[PreviewPanelController.SUB_BUTTON_BOTTOM];
                        CamLog.d(FaceDetector.TAG, "Current uri = " + uri + ", mThumbUri = " + PreviewPanelController.this.mThumbUri);
                        if (PreviewPanelController.this.mGet.getApplicationMode() == 0) {
                            dstSize = Util.getFitSizeOfBitmapForLCD(PreviewPanelController.this.mGet.getActivity(), ExifUtil.getExifWidth(BitmapManager.getRealPathFromURI(PreviewPanelController.this.mGet.getActivity(), uri)), ExifUtil.getExifHeight(BitmapManager.getRealPathFromURI(PreviewPanelController.this.mGet.getActivity(), uri)));
                            resizeFullBmp = ImageManager.loadScaledBitmap(PreviewPanelController.this.mGet.getContentResolver(), uri.toString(), dstSize[PreviewPanelController.ANI_NONE], dstSize[PreviewPanelController.SUB_BUTTON_MIDDLE]);
                        } else {
                            Bitmap tempBmp = PreviewPanelController.this.mGet.getLastThumbnail(uri);
                            if (tempBmp != null) {
                                dstSize = Util.getFitSizeOfBitmapForLCD(PreviewPanelController.this.mGet.getActivity(), tempBmp.getWidth(), tempBmp.getHeight());
                                resizeFullBmp = Bitmap.createScaledBitmap(tempBmp, dstSize[PreviewPanelController.ANI_NONE], dstSize[PreviewPanelController.SUB_BUTTON_MIDDLE], true);
                            }
                        }
                        if (resizeFullBmp == null || PreviewPanelController.this.mGalleryWindowAniState == 0 || !PreviewPanelController.this.mGet.checkActivity() || PreviewPanelController.this.mGet.getActivity() == null) {
                            CamLog.e(FaceDetector.TAG, "LoadBitmap fail!");
                            return;
                        }
                        final Bitmap imageBmp = resizeFullBmp;
                        final int degree = ExifUtil.getExifOrientationDegree(BitmapManager.getRealPathFromURI(PreviewPanelController.this.mGet.getActivity(), uri));
                        PreviewPanelController.this.mGet.postOnUiThread(new Runnable() {
                            public void run() {
                                PreviewPanelController.this.mGet.removePostRunnable(this);
                                ImageView lastGalleryImage = (ImageView) PreviewPanelController.this.mGet.findViewById(R.id.gallery_quick_window_last_image);
                                if (lastGalleryImage != null) {
                                    Util.clearImageViewDrawable(lastGalleryImage);
                                    lastGalleryImage.setImageBitmap(PreviewPanelController.this.mGet.getImage(imageBmp, degree, false));
                                }
                            }
                        });
                    }
                }
            }).start();
        }
    }

    public void showGalleryQuickViewAnimation(boolean open, boolean deleteImage) {
        AlphaAnimation aa;
        View galleryWindowView = this.mGet.findViewById(R.id.gallery_quick_window);
        final View galleryWindowViewImage = this.mGet.findViewById(R.id.gallery_quick_window_rotate);
        final View blackCover = this.mGet.findViewById(R.id.gallery_quick_window_backcover);
        View trash = this.mGet.findViewById(R.id.gallery_quick_window_trash);
        try {
            ((RotateLayout) galleryWindowViewImage).rotateLayout(this.mGet.getOrientationDegree());
        } catch (ClassCastException e) {
            CamLog.w(FaceDetector.TAG, "ClassCastException:", e);
        }
        blackCover.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        galleryWindowViewImage.clearAnimation();
        int[] startPos = new int[SUB_BUTTON_BOTTOM];
        int[] targetPos = new int[SUB_BUTTON_BOTTOM];
        int[] adjustPos = new int[SUB_BUTTON_BOTTOM];
        int[] trashPos = new int[SUB_BUTTON_BOTTOM];
        this.mLastPictureButton.getLocationOnScreen(startPos);
        galleryWindowView.getLocationOnScreen(targetPos);
        galleryWindowViewImage.getLocationOnScreen(adjustPos);
        trash.getLocationOnScreen(trashPos);
        float scaleX = ((float) this.mLastPictureButton.getMeasuredWidth()) / ((float) galleryWindowViewImage.getMeasuredWidth());
        float scaleY = ((float) this.mLastPictureButton.getMeasuredHeight()) / ((float) galleryWindowViewImage.getMeasuredHeight());
        float srcX = (float) (startPos[ANI_NONE] + (targetPos[ANI_NONE] - adjustPos[ANI_NONE]));
        float srcY = (float) (startPos[SUB_BUTTON_MIDDLE] + (targetPos[SUB_BUTTON_MIDDLE] - adjustPos[SUB_BUTTON_MIDDLE]));
        float destX = (float) targetPos[ANI_NONE];
        float destY = (float) targetPos[SUB_BUTTON_MIDDLE];
        if (!open && deleteImage) {
            scaleX = 0.0f;
            scaleY = 0.0f;
            srcX = (float) ((trashPos[ANI_NONE] + (targetPos[ANI_NONE] - adjustPos[ANI_NONE])) + (trash.getMeasuredWidth() / SUB_BUTTON_BOTTOM));
            srcY = (float) ((trashPos[SUB_BUTTON_MIDDLE] + (targetPos[SUB_BUTTON_MIDDLE] - adjustPos[SUB_BUTTON_MIDDLE])) + (trash.getMeasuredHeight() / SUB_BUTTON_BOTTOM));
            ((RotateImageButton) trash).setImageResource(R.drawable.camera_gallery_quickview_trash_open);
        }
        Animation scaleAnimation;
        if (open) {
            if (this.mGalleryWindowAniState != SUB_BUTTON_MIDDLE) {
                galleryWindowViewClose();
                return;
            }
            scaleAnimation = new ScaleAnimation(scaleX, RotateView.DEFAULT_TEXT_SCALE_X, scaleY, RotateView.DEFAULT_TEXT_SCALE_X);
            scaleAnimation = new TranslateAnimation(srcX, destX, srcY, destY);
            aa = new AlphaAnimation(0.5f, RotateView.DEFAULT_TEXT_SCALE_X);
            galleryWindowView.setVisibility(ANI_NONE);
            galleryWindowViewImage.setVisibility(ANI_NONE);
        } else if (this.mGalleryWindowAniState == SUB_BUTTON_MIDDLE || this.mGalleryWindowAniState == SUB_BUTTON_BOTTOM) {
            galleryWindowViewClose();
            return;
        } else {
            this.mLastPictureButton.setPressed(false);
            blackCover.setVisibility(8);
            scaleAnimation = new ScaleAnimation(RotateView.DEFAULT_TEXT_SCALE_X, scaleX, RotateView.DEFAULT_TEXT_SCALE_X, scaleY);
            scaleAnimation = new TranslateAnimation(destX, srcX, destY, srcY);
            aa = new AlphaAnimation(RotateView.DEFAULT_TEXT_SCALE_X, 0.5f);
        }
        AnimationSet aniSet = new AnimationSet(true);
        final boolean z = open;
        final boolean z2 = deleteImage;
        aniSet.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
                if (!z) {
                    PreviewPanelController.this.mGalleryWindowAniState = PreviewPanelController.SUB_BUTTON_BOTTOM;
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                PreviewPanelController.this.mGet.postOnUiThread(new Runnable() {
                    public void run() {
                        PreviewPanelController.this.mGet.removePostRunnable(this);
                        if (galleryWindowViewImage != null && blackCover != null) {
                            if (z) {
                                if (PreviewPanelController.this.mGalleryWindowAniState == PreviewPanelController.SUB_BUTTON_MIDDLE) {
                                    blackCover.setVisibility(PreviewPanelController.ANI_NONE);
                                }
                                if (PreviewPanelController.this.mGalleryWindowAniState != 0) {
                                    PreviewPanelController.this.mGalleryWindowAniState = PreviewPanelController.ANI_FINISHED;
                                }
                            } else if (z2) {
                                RotateImageButton trash = (RotateImageButton) PreviewPanelController.this.mGet.findViewById(R.id.gallery_quick_window_trash);
                                trash.setPressed(false);
                                trash.setImageResource(R.drawable.camera_gallery_quickview_trash_close);
                                PreviewPanelController.this.deleteImageAndUpdateThumbnail();
                            } else {
                                PreviewPanelController.this.galleryWindowViewClose();
                            }
                        }
                    }
                });
            }
        });
        aniSet.addAnimation(sa);
        aniSet.addAnimation(ta);
        aniSet.addAnimation(aa);
        aniSet.setDuration(300);
        aniSet.setInterpolator(new DecelerateInterpolator(1.5f));
        galleryWindowViewImage.startAnimation(aniSet);
    }

    private void deleteImageAndUpdateThumbnail() {
        galleryWindowViewClose();
        this.mDeleteThumbnailThread = new Thread() {
            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                r9 = this;
                r5 = com.lge.camera.controller.PreviewPanelController.this;
                r5 = r5.mGet;
                r5 = r5.getActivity();
                r6 = com.lge.camera.controller.PreviewPanelController.this;
                r6 = r6.mThumbUri;
                r2 = com.lge.camera.util.BitmapManager.getRealPathFromURI(r5, r6);
                if (r2 == 0) goto L_0x004d;
            L_0x0014:
                r1 = new java.io.File;	 Catch:{ Exception -> 0x005b }
                r1.<init>(r2);	 Catch:{ Exception -> 0x005b }
                r5 = r1.exists();	 Catch:{ Exception -> 0x005b }
                if (r5 == 0) goto L_0x004d;
            L_0x001f:
                r5 = r1.delete();	 Catch:{ Exception -> 0x005b }
                if (r5 == 0) goto L_0x004d;
            L_0x0025:
                r5 = com.lge.camera.controller.PreviewPanelController.this;	 Catch:{ Exception -> 0x005b }
                r5 = r5.mGet;	 Catch:{ Exception -> 0x005b }
                r5 = r5.getContentResolver();	 Catch:{ Exception -> 0x005b }
                r6 = com.lge.camera.controller.PreviewPanelController.this;	 Catch:{ Exception -> 0x005b }
                r6 = r6.mThumbUri;	 Catch:{ Exception -> 0x005b }
                r7 = 0;
                r8 = 0;
                r5.delete(r6, r7, r8);	 Catch:{ Exception -> 0x005b }
                r5 = com.lge.camera.util.SecureImageUtil.get();	 Catch:{ Exception -> 0x005b }
                r6 = com.lge.camera.controller.PreviewPanelController.this;	 Catch:{ Exception -> 0x005b }
                r6 = r6.mThumbUri;	 Catch:{ Exception -> 0x005b }
                r7 = com.lge.camera.controller.PreviewPanelController.this;	 Catch:{ Exception -> 0x005b }
                r7 = r7.mGet;	 Catch:{ Exception -> 0x005b }
                r7 = r7.getApplicationMode();	 Catch:{ Exception -> 0x005b }
                r5.removeSecureLockUri(r6, r7);	 Catch:{ Exception -> 0x005b }
            L_0x004d:
                r5 = r9.isInterrupted();
                if (r5 == 0) goto L_0x0064;
            L_0x0053:
                r5 = "CameraApp";
                r6 = "mDeleteThumbnailThread is isInterrupted()";
                com.lge.camera.util.CamLog.d(r5, r6);
            L_0x005a:
                return;
            L_0x005b:
                r0 = move-exception;
                r5 = "CameraApp";
                r6 = "delete Thumbnail fail : ";
                com.lge.camera.util.CamLog.e(r5, r6, r0);
                goto L_0x004d;
            L_0x0064:
                r5 = com.lge.camera.properties.FunctionProperties.isTimeMachinShotSupported();
                if (r5 == 0) goto L_0x00a7;
            L_0x006a:
                r4 = 0;
                r5 = com.lge.camera.controller.PreviewPanelController.this;	 Catch:{ Exception -> 0x00fd }
                r5 = r5.mGet;	 Catch:{ Exception -> 0x00fd }
                r5 = r5.isTimeMachineModeOn();	 Catch:{ Exception -> 0x00fd }
                if (r5 == 0) goto L_0x008f;
            L_0x0075:
                r5 = com.lge.camera.controller.PreviewPanelController.this;	 Catch:{ Exception -> 0x00fd }
                r5 = r5.mGet;	 Catch:{ Exception -> 0x00fd }
                r5 = r5.isTimemachineHasPictures();	 Catch:{ Exception -> 0x00fd }
                if (r5 == 0) goto L_0x008f;
            L_0x007f:
                r5 = com.lge.camera.controller.PreviewPanelController.this;	 Catch:{ Exception -> 0x00fd }
                r5 = r5.mGet;	 Catch:{ Exception -> 0x00fd }
                r4 = r5.deleteTimeMachineImages();	 Catch:{ Exception -> 0x00fd }
                r5 = com.lge.camera.controller.PreviewPanelController.this;	 Catch:{ Exception -> 0x00fd }
                r5 = r5.mGet;	 Catch:{ Exception -> 0x00fd }
                r6 = 0;
                r5.setTimemachineHasPictures(r6);	 Catch:{ Exception -> 0x00fd }
            L_0x008f:
                r5 = "CameraApp";
                r6 = new java.lang.StringBuilder;
                r6.<init>();
                r7 = "timeMachineTempFileDeleted ? = ";
                r6 = r6.append(r7);
                r6 = r6.append(r4);
                r6 = r6.toString();
                com.lge.camera.util.CamLog.i(r5, r6);
            L_0x00a7:
                r5 = com.lge.camera.properties.FunctionProperties.isRefocusShotSupported();
                if (r5 == 0) goto L_0x00ee;
            L_0x00ad:
                r3 = 0;
                r5 = com.lge.camera.controller.PreviewPanelController.this;	 Catch:{ Exception -> 0x0138 }
                r5 = r5.mGet;	 Catch:{ Exception -> 0x0138 }
                r6 = "key_camera_shot_mode";
                r7 = "shotmode_refocus";
                r5 = r5.checkSettingValue(r6, r7);	 Catch:{ Exception -> 0x0138 }
                if (r5 == 0) goto L_0x00d6;
            L_0x00bc:
                r5 = com.lge.camera.controller.PreviewPanelController.this;	 Catch:{ Exception -> 0x0138 }
                r5 = r5.mGet;	 Catch:{ Exception -> 0x0138 }
                r5 = r5.isRefocusShotHasPictures();	 Catch:{ Exception -> 0x0138 }
                if (r5 == 0) goto L_0x00d6;
            L_0x00c6:
                r5 = com.lge.camera.controller.PreviewPanelController.this;	 Catch:{ Exception -> 0x0138 }
                r5 = r5.mGet;	 Catch:{ Exception -> 0x0138 }
                r3 = r5.deleteRefocusShotImages();	 Catch:{ Exception -> 0x0138 }
                r5 = com.lge.camera.controller.PreviewPanelController.this;	 Catch:{ Exception -> 0x0138 }
                r5 = r5.mGet;	 Catch:{ Exception -> 0x0138 }
                r6 = 0;
                r5.setRefocusShotHasPictures(r6);	 Catch:{ Exception -> 0x0138 }
            L_0x00d6:
                r5 = "CameraApp";
                r6 = new java.lang.StringBuilder;
                r6.<init>();
                r7 = "refocusShotTempFileDeleted ? = ";
                r6 = r6.append(r7);
                r6 = r6.append(r3);
                r6 = r6.toString();
                com.lge.camera.util.CamLog.i(r5, r6);
            L_0x00ee:
                r5 = r9.isInterrupted();
                if (r5 == 0) goto L_0x0173;
            L_0x00f4:
                r5 = "CameraApp";
                r6 = "mDeleteThumbnailThread is isInterrupted()";
                com.lge.camera.util.CamLog.d(r5, r6);
                goto L_0x005a;
            L_0x00fd:
                r0 = move-exception;
                r5 = "CameraApp";
                r6 = "Exception:";
                com.lge.camera.util.CamLog.w(r5, r6, r0);	 Catch:{ all -> 0x011e }
                r5 = "CameraApp";
                r6 = new java.lang.StringBuilder;
                r6.<init>();
                r7 = "timeMachineTempFileDeleted ? = ";
                r6 = r6.append(r7);
                r6 = r6.append(r4);
                r6 = r6.toString();
                com.lge.camera.util.CamLog.i(r5, r6);
                goto L_0x00a7;
            L_0x011e:
                r5 = move-exception;
                r6 = "CameraApp";
                r7 = new java.lang.StringBuilder;
                r7.<init>();
                r8 = "timeMachineTempFileDeleted ? = ";
                r7 = r7.append(r8);
                r7 = r7.append(r4);
                r7 = r7.toString();
                com.lge.camera.util.CamLog.i(r6, r7);
                throw r5;
            L_0x0138:
                r0 = move-exception;
                r5 = "CameraApp";
                r6 = "Exception:";
                com.lge.camera.util.CamLog.w(r5, r6, r0);	 Catch:{ all -> 0x0159 }
                r5 = "CameraApp";
                r6 = new java.lang.StringBuilder;
                r6.<init>();
                r7 = "refocusShotTempFileDeleted ? = ";
                r6 = r6.append(r7);
                r6 = r6.append(r3);
                r6 = r6.toString();
                com.lge.camera.util.CamLog.i(r5, r6);
                goto L_0x00ee;
            L_0x0159:
                r5 = move-exception;
                r6 = "CameraApp";
                r7 = new java.lang.StringBuilder;
                r7.<init>();
                r8 = "refocusShotTempFileDeleted ? = ";
                r7 = r7.append(r8);
                r7 = r7.append(r3);
                r7 = r7.toString();
                com.lge.camera.util.CamLog.i(r6, r7);
                throw r5;
            L_0x0173:
                r5 = com.lge.camera.controller.PreviewPanelController.this;
                r5 = r5.mGet;
                r6 = new com.lge.camera.controller.PreviewPanelController$14$1;
                r6.<init>();
                r5.postOnUiThread(r6);
                goto L_0x005a;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.controller.PreviewPanelController.14.run():void");
            }
        };
        this.mDeleteThumbnailThread.start();
    }

    private void galleryWindowViewClose() {
        View galleryWindowViewImage = this.mGet.findViewById(R.id.gallery_quick_window_rotate);
        galleryWindowViewImage.clearAnimation();
        galleryWindowViewImage.setVisibility(8);
        this.mGet.findViewById(R.id.gallery_quick_window).setVisibility(8);
        this.mGet.findViewById(R.id.gallery_quick_window_backcover).setVisibility(8);
        Util.clearImageViewDrawableOnly((ImageView) this.mGet.findViewById(R.id.gallery_quick_window_last_image));
        this.mGalleryWindowAniState = ANI_NONE;
        this.mGet.enableInput(true);
        if (this.mLastPictureButton != null) {
            this.mLastPictureButton.setPressed(false);
        }
        if (!this.mGet.isPausing()) {
            String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
            if (CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(shotMode)) {
                this.mGet.showBeautyshotController(true);
            }
        }
    }

    public void onResume() {
        this.mGalleryLaunching = false;
        CamLog.d(FaceDetector.TAG, "onResume mInit[" + this.mInit + "]");
        if (this.mInit) {
            setPreviewPanelVisibility(true);
            enableCommand(true);
            setSwitcherVisible(true);
            if (this.mSwitcherLever != null) {
                this.mSwitcherLever.setSwitchEnable(true);
            }
            this.mGet.doCommandDelayed(Command.UPDATE_THUMBNAIL_BUTTON, 1000);
            if (!this.mProcessInitDone) {
                startRotation(this.mGet.getOrientationDegree(), false);
                this.mProcessInitDone = false;
            }
        }
    }

    public void onPause() {
        if (checkMediator()) {
            setMainButtonDisable();
        }
        stopTimerTask();
        this.mSnapshotOnIdle = false;
        this.mSnapshotOnContinuousFocus = false;
        this.mShutterButtonLongKey = false;
        this.mShutterFocusLongKey = false;
        if (this.mShutterButton != null) {
            this.mShutterButton.setShutterButtonFocusEnable(!this.mSnapshotOnContinuousFocus);
        }
        if (this.mThumbnailThread != null) {
            this.mThumbnailThread.interrupt();
            this.mThumbnailThread = null;
        }
        if (this.mDeleteThumbnailThread != null) {
            this.mDeleteThumbnailThread.interrupt();
            this.mDeleteThumbnailThread = null;
        }
        galleryWindowViewClose();
        setLockConditionForMainButton(false);
        if (this.mGet.isTimeMachineModeOn()) {
            this.mGet.setTimemachineHasPictures(false);
        }
        if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_REFOCUS)) {
            this.mGet.setRefocusShotHasPictures(false);
        }
        if (this.mGet.isConfigurationChanging()) {
            this.mThumbUri = null;
            if (this.mThumbImage != null) {
                this.mThumbImage.recycle();
                this.mThumbImage = null;
            }
            if (this.mLastPictureButton != null) {
                this.mLastPictureButton.setOnClickListener(null);
                this.mLastPictureButton.setOnLongClickListener(null);
                this.mLastPictureButton.setOnTouchListener(null);
            }
            this.mLastPictureButton = null;
            if (this.mThumbController != null) {
                this.mThumbController.close();
            }
            this.mThumbController = null;
        }
    }

    public void onDestroy() {
        Drawable d;
        CamLog.d(FaceDetector.TAG, "onDestory-start");
        this.mThumbUri = null;
        if (this.mThumbImage != null) {
            this.mThumbImage.recycle();
            this.mThumbImage = null;
        }
        if (this.mLastPictureButton != null) {
            this.mLastPictureButton.setOnClickListener(null);
            this.mLastPictureButton.setOnLongClickListener(null);
            this.mLastPictureButton.setOnTouchListener(null);
        }
        this.mLastPictureButton = null;
        if (this.mThumbController != null) {
            this.mThumbController.close();
        }
        this.mThumbController = null;
        if (this.mReleaseArea != null) {
            this.mReleaseArea.clear();
            this.mReleaseArea = null;
        }
        if (this.mHashMap != null) {
            this.mHashMap.clear();
            this.mHashMap = null;
        }
        if (this.mShutterButton != null) {
            this.mShutterButton.setOnShutterButtonListener(null);
            this.mShutterButton.setOnShutterButtonLongPressListener(null);
            this.mShutterButton.unbind();
            this.mShutterButton = null;
        }
        if (this.mSwitcher != null) {
            this.mSwitcher.setOnSwitchListener(null);
            this.mSwitcher = null;
        }
        if (this.mSwitcherLever != null) {
            this.mSwitcherLever.setOnSwitchLeverListener(null);
            this.mSwitcherLever = null;
        }
        if (this.mSubButtonTop != null) {
            d = this.mSubButtonTop.getBackground();
            if (d != null) {
                d.setCallback(null);
            }
            this.mSubButtonTop.setBackgroundDrawable(null);
            this.mSubButtonTop = null;
        }
        if (this.mSubButtonMiddle != null) {
            d = this.mSubButtonMiddle.getBackground();
            if (d != null) {
                d.setCallback(null);
            }
            this.mSubButtonMiddle.setBackgroundDrawable(null);
            this.mSubButtonMiddle = null;
        }
        if (this.mSubButtonBottom != null) {
            d = this.mSubButtonBottom.getBackground();
            if (d != null) {
                d.setCallback(null);
            }
            this.mSubButtonBottom.setBackgroundDrawable(null);
            this.mSubButtonBottom = null;
        }
        if (this.mSubTouchButtonTop != null) {
            this.mSubTouchButtonTop.setOnTouchListener(null);
            this.mSubTouchButtonTop = null;
        }
        if (this.mSubTouchButtonMiddle != null) {
            this.mSubTouchButtonMiddle.setOnTouchListener(null);
            this.mSubTouchButtonMiddle = null;
        }
        if (this.mSubTouchButtonBottom != null) {
            this.mSubTouchButtonBottom.setOnTouchListener(null);
            this.mSubTouchButtonBottom = null;
        }
        super.onDestroy();
        CamLog.d(FaceDetector.TAG, "onDestory-end");
    }

    public void onSwitcherClick(Switcher button) {
        if (this.mGet.isEnteringViewShowing() || this.mGet.getInCaptureProgress() || this.mGet.isPausing() || this.mShutterButtonLongKey || isLockDuringMediaScanning() || !this.mGet.getEnableInput()) {
            CamLog.d(FaceDetector.TAG, "onSwitcherClick return: is capturing..");
            return;
        }
        switch (button.getId()) {
            case R.id.camera_switch /*2131558795*/:
            case R.id.camera_switcher_vertical /*2131558800*/:
                if (TelephonyUtil.phoneInCall(this.mGet.getApplicationContext())) {
                    this.mGet.toast((int) R.string.error_video_recording_during_call);
                    return;
                }
                if (this.mGet.isQuickFunctionSettingControllerShowing()) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("immediately", true);
                    this.mGet.doCommandUi(Command.HIDE_QUICK_FUNCTION_SETTING_MENU, bundle);
                }
                String command = this.mGet.getApplicationMode() == 0 ? Command.CHANGE_TO_CAMCORDER : Command.CHANGE_TO_CAMERA;
                if (this.mGet.getApplicationMode() == 0 || (this.mGet.getApplicationMode() == SUB_BUTTON_MIDDLE && this.mGet.getVideoState() == 0)) {
                    setMainButtonDisable();
                    this.mGet.setQuickFunctionAllMenuEnabled(false, true);
                    this.mGet.setQuickButtonMenuEnable(false, true);
                    this.mGet.setQuickFunctionMenuForcedDisable(true);
                    this.mGet.enableCommand(false);
                    this.mGet.toastControllerHide(true);
                    this.mGet.doCommandDelayed(command, 0);
                    this.mGet.hideOsd();
                }
            default:
        }
    }

    public boolean snapshotOnIdle() {
        return this.mSnapshotOnIdle;
    }

    public boolean snapshotOnContinuousFocus() {
        return this.mSnapshotOnContinuousFocus;
    }

    public boolean isShutterButtonLongKey() {
        return this.mShutterButtonLongKey;
    }

    public boolean isShutterFocusLongKey() {
        return this.mShutterFocusLongKey;
    }

    public void setShutterFocusLongKey(boolean set) {
        this.mShutterFocusLongKey = set;
    }

    public void releaseShutterFocus() {
        if (checkMediator() && this.mGet.getApplicationMode() == 0 && this.mShutterFocusLongKey) {
            this.mShutterFocusLongKey = false;
            this.mGet.cancelAutoFocus();
            this.mGet.setFocusRectangleInitialize();
            if (this.mGet.isCafSupported()) {
                this.mGet.hideFocus();
            }
        }
    }

    public void onShutterButtonLongPressed(ShutterButton button) {
        CamLog.d(FaceDetector.TAG, "onShutterButtonLongPressed");
        if (checkMediator() && this.mGet.getApplicationMode() != SUB_BUTTON_MIDDLE) {
            if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS) && FunctionProperties.isSupportBurstShot()) {
                CamLog.d(FaceDetector.TAG, "onShutterButtonLongPressed return");
                if (this.mGet.getAvailablePictureCount() < 1) {
                    this.mGet.clearFocusState();
                    this.mGet.checkStorage(true);
                }
            } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) && this.mGet.isPanoramaStarted()) {
                CamLog.d(FaceDetector.TAG, "return because panorama started");
            } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) && this.mGet.getInCaptureProgress()) {
                CamLog.d(FaceDetector.TAG, "return because plane panorama started");
            } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
                CamLog.d(FaceDetector.TAG, "return because free panorama don't use long key");
            } else {
                this.mShutterFocusLongKey = true;
                if (this.mGet.getCameraId() == SUB_BUTTON_MIDDLE) {
                    this.mGet.doCommand(Command.RELEASE_TOUCH_FOCUS);
                } else if (!CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                    this.mGet.cancelAutoFocus();
                    this.mShutterButton.setShutterButtonFocusEnable(true);
                    this.mGet.setFocusRectangleInitialize();
                    if (this.mGet.getParameters() != null) {
                        String focusMode = LGT_Limit.ISP_AUTOMODE_AUTO;
                        LGParameters lgParameters = this.mGet.getLGParam();
                        lgParameters.getParameters().setFocusMode(focusMode);
                        CamLog.d(FaceDetector.TAG, "### setFocusMode-" + focusMode);
                        lgParameters.setParameters(lgParameters.getParameters());
                    }
                    this.mGet.postOnUiThread(new Runnable() {
                        public void run() {
                            if (PreviewPanelController.this.checkMediator()) {
                                PreviewPanelController.this.mGet.removePostRunnable(this);
                                PreviewPanelController.this.mGet.showFocus(false);
                                PreviewPanelController.this.doCameraShutterButtonFocus(true);
                            }
                        }
                    }, 100);
                }
            }
        }
    }

    public void stopTimerTask() {
        if (checkMediator()) {
            if (this.mGet.getApplicationMode() == 0 && this.mGet.getSettingValue(Setting.KEY_VOICESHUTTER).equals(CameraConstants.SMART_MODE_OFF) && this.mShutterButtonLongKey) {
                AudioUtil.setAudioFocus(this.mGet.getApplicationContext(), false);
            }
            this.mShutterButtonLongKey = false;
            if (this.mButtonCheckTimer != null) {
                this.mButtonCheckTimer.cancel();
                this.mButtonCheckTimer.purge();
                this.mButtonCheckTimer = null;
            }
            this.mGet.removePostRunnable(this.mShutterButtonRunnable);
        }
    }

    public boolean onSwitchChanged(SwitcherLever source, boolean onOff) {
        CamLog.d(FaceDetector.TAG, "Switch changed to " + onOff);
        this.mGet.doCommand(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
        this.mGet.hideSmartZoomFocusView();
        if (isLockDuringMediaScanning()) {
            return false;
        }
        if (this.mGet.isEnteringViewShowing() || this.mGet.getInCaptureProgress() || this.mGet.isPausing() || this.mShutterButtonLongKey || isLockDuringMediaScanning() || !this.mGet.getEnableInput()) {
            CamLog.d(FaceDetector.TAG, "onSwitcherClick return: is capturing..");
            return false;
        } else if (!onOff) {
            CamLog.d(FaceDetector.TAG, "Video State: " + this.mGet.getVideoState());
            if (this.mGet.getVideoState() != 0) {
                return false;
            }
            this.mGet.toastControllerHide(true);
            this.mSwitcherLever.setSwitchEnable(false);
            this.mSwitcherLever.setEnabled(false);
            this.mGet.setQuickFunctionAllMenuEnabled(false, true);
            this.mGet.setQuickButtonMenuEnable(false, true);
            this.mGet.setQuickFunctionMenuForcedDisable(true);
            this.mGet.doCommandDelayed(Command.CHANGE_TO_CAMERA, 0);
            this.mGet.hideOsd();
            return true;
        } else if (TelephonyUtil.phoneInCall(this.mGet.getApplicationContext())) {
            this.mGet.toast((int) R.string.error_video_recording_during_call);
            return false;
        } else if (this.mGet.isExitIgnoreDuringSaving()) {
            return false;
        } else {
            if (this.mGet.getQueueCount() > 10) {
                this.mGet.setExitIgnoreDuringSaving(true);
                this.mGet.showSavingProgressDialog();
                return false;
            }
            this.mGet.toastControllerHide(true);
            this.mSwitcherLever.setSwitchEnable(false);
            this.mSwitcherLever.setEnabled(false);
            this.mGet.setQuickFunctionAllMenuEnabled(false, true);
            this.mGet.setQuickButtonMenuEnable(false, true);
            this.mGet.setQuickFunctionMenuForcedDisable(true);
            this.mGet.doCommandDelayed(Command.CHANGE_TO_CAMCORDER, 0);
            this.mGet.hideOsd();
            return true;
        }
    }

    private boolean isLockDuringMediaScanning() {
        if (!this.mGet.isMediaScanning()) {
            return false;
        }
        this.mGet.toast((int) R.string.sp_media_scanning_ics_NORMAL);
        return true;
    }

    public void audioCallbackTakePicture() {
        if (this.mGet == null || this.mGet.getApplicationMode() != 0 || !this.mGet.isPreviewing()) {
            return;
        }
        if (((!this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY) && !this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) || this.mGet.getSubMenuMode() != 15) && this.mGet.getSubMenuMode() != 0) {
            CamLog.d(FaceDetector.TAG, "onAudioRecogResultCallback-return : Submenu state is not off!!");
            this.mGet.audioCallbackRestartEngine();
        } else if (this.mGet.isRotateDialogVisible() || this.mGet.isTimerShotCountdown()) {
            this.mGet.audioCallbackRestartEngine();
        } else if (this.mGet.getStorageState() != 0) {
            if (this.mGet.getAvailablePictureCount() < 1) {
                this.mGet.checkStorage(true);
            }
            this.mGet.audioCallbackRestartEngine();
        } else {
            final ShutterButton button = (ShutterButton) this.mGet.findViewById(R.id.main_button_bg);
            if (button == null || button.getVisibility() != 0) {
                this.mGet.audioCallbackRestartEngine();
                return;
            }
            CamLog.d(FaceDetector.TAG, "sound recognize : take a picture!!!");
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    PreviewPanelController.this.mGet.removePostRunnable(this);
                    if (PreviewPanelController.this.mGet.getApplicationMode() == 0) {
                        PreviewPanelController.this.mGet.updateVoiceShutterIndicator(true);
                    }
                }
            });
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    PreviewPanelController.this.mGet.removePostRunnable(this);
                    PreviewPanelController.this.onShutterButtonFocus(button, true);
                    PreviewPanelController.this.onShutterButtonClick(button);
                    PreviewPanelController.this.onShutterButtonFocus(button, false);
                }
            }, 400);
        }
    }

    public void gestureCallbackTakePicture() {
        if (this.mGet != null && this.mGet.getApplicationMode() == 0 && this.mGet.getCameraMode() == SUB_BUTTON_MIDDLE && !this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE).equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
            final ShutterButton button = (ShutterButton) this.mGet.findViewById(R.id.main_button_bg);
            if (button != null && button.getVisibility() == 0) {
                CamLog.d(FaceDetector.TAG, "hand gesture recognize : take a picture!!!");
                this.mGet.postOnUiThread(new Runnable() {
                    public void run() {
                        PreviewPanelController.this.mGet.removePostRunnable(this);
                        PreviewPanelController.this.onShutterButtonFocus(button, true);
                        PreviewPanelController.this.onShutterButtonClick(button);
                        PreviewPanelController.this.onShutterButtonFocus(button, false);
                    }
                });
            } else if (this.mGet.isTimerShotCountdown()) {
                this.mGet.startGestureEngine();
            }
        }
    }

    public boolean isSliding() {
        return this.mSliding;
    }

    public void slidePanelIn() {
        if (ProjectVariables.isSupportClearView() && this.mInit) {
            panelMenuAnimation(true, Limit.OLA_FIFO_DATA_MAX_SIZE_BYTE, new AnimationListener() {
                public void onAnimationStart(Animation arg0) {
                    PreviewPanelController.this.mSliding = true;
                    PreviewPanelController.this.setPreviewPanelVisibility(true);
                }

                public void onAnimationRepeat(Animation arg0) {
                }

                public void onAnimationEnd(Animation arg0) {
                    if (PreviewPanelController.this.mLastPictureButton != null) {
                        PreviewPanelController.this.mLastPictureButton.setEnabled(true);
                        PreviewPanelController.this.mLastPictureButton.setClickable(true);
                    }
                    PreviewPanelController.this.mSliding = false;
                    PreviewPanelController.this.mGet.findViewById(R.id.main_bar_without_shutter).setVisibility(PreviewPanelController.ANI_NONE);
                }
            });
        }
    }

    public void slidePanelOut() {
        if (ProjectVariables.isSupportClearView() && this.mInit) {
            panelMenuAnimation(false, Limit.OLA_FIFO_DATA_MAX_SIZE_BYTE, new AnimationListener() {
                public void onAnimationStart(Animation arg0) {
                    PreviewPanelController.this.mSliding = true;
                    if (PreviewPanelController.this.mLastPictureButton != null) {
                        PreviewPanelController.this.mLastPictureButton.setEnabled(false);
                        PreviewPanelController.this.mLastPictureButton.setClickable(false);
                    }
                }

                public void onAnimationRepeat(Animation arg0) {
                }

                public void onAnimationEnd(Animation arg0) {
                    PreviewPanelController.this.mSliding = false;
                    PreviewPanelController.this.mGet.findViewById(R.id.main_bar_without_shutter).setVisibility(4);
                }
            });
        }
    }

    public void panelMenuAnimation(boolean show, int duration, AnimationListener listener) {
        View panelView = this.mGet.findViewById(R.id.main_bar_without_shutter);
        if (panelView != null) {
            panelView.clearAnimation();
            int direction = this.mGet.isConfigureLandscape() ? SUB_BUTTON_MIDDLE : ANI_FINISHED;
            if (show) {
                Util.slideIn(panelView, direction, duration, listener);
            } else {
                Util.slideOut(panelView, direction, duration, listener);
            }
        }
    }

    private void setSubButtonContentDescription(View v, int resId) {
        switch (resId) {
            case R.drawable.selector_livesnapshot_btn /*2130838669*/:
                v.setContentDescription(this.mGet.getString(R.string.sp_live_shot_NORMAL));
            case R.drawable.selector_recording_stop_btn /*2130838686*/:
                v.setContentDescription(this.mGet.getString(R.string.accessiblity_stop_recording));
            case R.drawable.selector_sub_btn_reset_free_panorama /*2130838693*/:
                v.setContentDescription(this.mGet.getString(R.string.accessiblity_free_panorama_retake));
            default:
        }
    }

    private void setMainButtonContentDescription() {
        if (this.mGet.getApplicationMode() == 0) {
            this.mShutterButton.setContentDescription(this.mGet.getString(R.string.accessibility_shutter_button));
            return;
        }
        int videoState = this.mGet.getVideoState();
        if (videoState == ANI_FINISHED || videoState == SUB_BUTTON_MIDDLE) {
            this.mShutterButton.setContentDescription(this.mGet.getString(R.string.accessiblity_pause_recording));
        } else if (videoState == 0 || videoState == SUB_BUTTON_BOTTOM) {
            this.mShutterButton.setContentDescription(this.mGet.getString(R.string.accessiblity_start_recording));
        } else if (videoState == 4) {
            this.mShutterButton.setContentDescription(this.mGet.getString(R.string.accessiblity_start_recording));
        }
    }

    private void setSubButtonVisibilityWithTouchBotton(int resId, int visibility) {
        this.mGet.findViewById(resId).setVisibility(visibility);
        switch (resId) {
            case R.id.sub_button1 /*2131558788*/:
                this.mGet.findViewById(R.id.sub_touch_button1).setVisibility(visibility);
            case R.id.sub_button2 /*2131558790*/:
                this.mGet.findViewById(R.id.sub_touch_button2).setVisibility(visibility);
            case R.id.sub_button3 /*2131558792*/:
                this.mGet.findViewById(R.id.sub_touch_button3).setVisibility(visibility);
            default:
        }
    }

    private boolean checkAfOnCafContition() {
        if (!checkMediator() || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)) || this.mGet.getFocusState() == SUB_BUTTON_MIDDLE || this.mGet.getFocusState() == ANI_FINISHED || this.mGet.getFocusState() == 4 || this.mGet.getFocusState() == SUB_BUTTON_BOTTOM || this.mGet.getFocusState() == 6 || this.mGet.getFocusState() == 7 || this.mGet.getFocusState() == 5 || this.mSnapshotOnContinuousFocus || this.mShutterButtonLongKey || this.mShutterFocusLongKey) {
            return false;
        }
        return true;
    }

    public void set3DSwitchVisible(boolean visible) {
    }

    public void set3DSwitchImage() {
    }

    public boolean isSwitcherLeverPressed() {
        return this.mSwitcherLever.isSwitcherPressed();
    }
}
