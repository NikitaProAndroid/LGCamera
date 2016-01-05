package com.lge.camera.controller;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.lge.camera.R;
import com.lge.camera.VideoRecorder;
import com.lge.camera.command.Command;
import com.lge.camera.components.MultiDirectionSlidingDrawer;
import com.lge.camera.components.MultiDirectionSlidingDrawer.OnDrawerCloseListener;
import com.lge.camera.components.MultiDirectionSlidingDrawer.OnDrawerOpenListener;
import com.lge.camera.components.PIPResizeHandlerView;
import com.lge.camera.components.RotateView;
import com.lge.camera.components.SmartZoomFocusView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.Util;
import com.lge.filterpacks.DualRecorderFilter;
import com.lge.filterpacks.ObjectFilter;
import com.lge.filterpacks.SmartZoomFilter;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.library.FaceDetector;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PIPController {
    public static final int DEFAULT_PIP_FRAME_EFFECT = 1;
    public static final int PIP_MASK_FISHEYE_EFFECT = 7;
    public static final int PIP_MASK_INSTANTPIC_EFFECT = 4;
    public static final int PIP_MASK_OFF_EFFECT = 0;
    public static final int PIP_MASK_SPLITVIEW1_EFFECT = 8;
    public static final int PIP_MASK_SPLITVIEW2_EFFECT = 9;
    public static final int PIP_MASK_WINDOW_EFFECT = 1;
    public static int SMARTZOOM_DEFAULT_X = 0;
    public static int SMARTZOOM_DEFAULT_Y = 0;
    private static final int VIDEO_SIZE_FHD = 1;
    private static final int VIDEO_SIZE_HD = 2;
    private static final int VIDEO_SIZE_QCIF = 6;
    private static final int VIDEO_SIZE_QVGA = 5;
    private static final int VIDEO_SIZE_TV = 3;
    private static final int VIDEO_SIZE_UVGA = 7;
    private static final int VIDEO_SIZE_VGA = 4;
    public static int mCurrentMaskMenu;
    private PIPControllerFunction mGet;
    private float mInitPIPHeight;
    private float mInitPIPWidth;
    private boolean mIsResizeHandlerShown;
    private boolean mIsSmartZoomFocusViewShown;
    private int mMarginForFrameEffect;
    private int mMarginHeightForFrameEffectWindow;
    private int mMarginWidthForFrameEffectWindow;
    private Bitmap mMaskBitmapOutlineFinal;
    private Bitmap mMaskBitmapShapeFinal;
    private int mMaxSmallScreenHeight;
    private int mMaxSmallScreenWidth;
    private int mMaxX;
    private int mMaxY;
    private int mMinSmallScreenHeight;
    private int mMinSmallScreenWidth;
    private int mMinX;
    private int mMinY;
    private int mMode;
    private ArrayList<Integer> mPIPFrameMaskImage;
    private ArrayList<Integer> mPIPFrameMaskOutlineImage;
    private PIPResizeHandlerView mPIPResizeHandlerView;
    private int mPreviewSize;
    private float mScaleX;
    private float mScaleY;
    private int mScreenSizeX;
    private int mScreenSizeY;
    private boolean mSetBitmapDone;
    protected SmartZoomFocusView mSmartZoomFocusView;
    private RectInfo mSmartZoomFocusViewPositon;
    private RectInfo mSubWindowPosition;
    private float mSubWindowRatio;
    int previewTopMargin;
    private RectInfo tmpRect;
    private ViewGroup vg;

    public interface PIPControllerFunction {
        void doCommandDelayed(String str, long j);

        View findViewById(int i);

        Activity getActivity();

        Context getApplicationContext();

        int getFocusAreaHeight();

        int getOrientationDegree();

        Resources getResources();

        int getVideoState();

        View inflateStub(int i);

        boolean isCamcorderRotation(boolean z);

        boolean isConfigureLandscape();

        boolean isDualCameraActive();

        boolean isDualRecordingActive();

        boolean isSmartZoomRecordingActive();

        void quickFunctionControllerRefresh(boolean z);

        void setButtonRemainRefresh();

        void setMainButtonVisible(boolean z);

        void setQuickButtonForcedDisable(boolean z);

        void setQuickButtonMenuEnable(boolean z, boolean z2);

        void setQuickFunctionAllMenuEnabled(boolean z, boolean z2);

        void setSmartZoomFocusViewPosition(int i, int i2);

        void setSwitcherVisible(boolean z);

        void setThumbnailButtonVisibility(int i);

        void startPIPFrameSubMenuRotation(int i);
    }

    class RectInfo {
        private int mHeight;
        private int mPrevRectX0;
        private int mPrevRectX1;
        private int mPrevRectY0;
        private int mPrevRectY1;
        private int mRectX0;
        private int mRectX1;
        private int mRectY0;
        private int mRectY1;
        private int mWidth;

        static /* synthetic */ int access$112(RectInfo x0, int x1) {
            int i = x0.mRectX0 + x1;
            x0.mRectX0 = i;
            return i;
        }

        static /* synthetic */ int access$120(RectInfo x0, int x1) {
            int i = x0.mRectX0 - x1;
            x0.mRectX0 = i;
            return i;
        }

        static /* synthetic */ int access$212(RectInfo x0, int x1) {
            int i = x0.mRectX1 + x1;
            x0.mRectX1 = i;
            return i;
        }

        static /* synthetic */ int access$220(RectInfo x0, int x1) {
            int i = x0.mRectX1 - x1;
            x0.mRectX1 = i;
            return i;
        }

        static /* synthetic */ int access$312(RectInfo x0, int x1) {
            int i = x0.mRectY0 + x1;
            x0.mRectY0 = i;
            return i;
        }

        static /* synthetic */ int access$320(RectInfo x0, int x1) {
            int i = x0.mRectY0 - x1;
            x0.mRectY0 = i;
            return i;
        }

        static /* synthetic */ int access$412(RectInfo x0, int x1) {
            int i = x0.mRectY1 + x1;
            x0.mRectY1 = i;
            return i;
        }

        static /* synthetic */ int access$420(RectInfo x0, int x1) {
            int i = x0.mRectY1 - x1;
            x0.mRectY1 = i;
            return i;
        }

        public RectInfo() {
            setPosition(PIPController.PIP_MASK_OFF_EFFECT, PIPController.PIP_MASK_OFF_EFFECT, PIPController.PIP_MASK_OFF_EFFECT, PIPController.PIP_MASK_OFF_EFFECT);
            setPrevPositionUpdate();
        }

        public RectInfo(int x0, int y0, int x1, int y1) {
            setPosition(x0, y0, x1, y1);
        }

        public void setPosition(int x0, int y0, int x1, int y1) {
            this.mRectX0 = x0;
            this.mRectY0 = y0;
            this.mRectX1 = x1;
            this.mRectY1 = y1;
            this.mWidth = this.mRectX1 - this.mRectX0;
            this.mHeight = this.mRectY1 - this.mRectY0;
        }

        public void setPosition(RectInfo rect) {
            setPosition(rect.mRectX0, rect.mRectY0, rect.mRectX1, rect.mRectY1);
        }

        public void setPrevPositionUpdate() {
            this.mPrevRectX0 = this.mRectX0;
            this.mPrevRectY0 = this.mRectY0;
            this.mPrevRectX1 = this.mRectX1;
            this.mPrevRectY1 = this.mRectY1;
        }

        public void restorePosition() {
            setPosition(this.mPrevRectX0, this.mPrevRectY0, this.mPrevRectX1, this.mPrevRectY1);
        }
    }

    static {
        SMARTZOOM_DEFAULT_X = CameraConstants.LCD_SIZE_WIDTH / VIDEO_SIZE_HD;
        SMARTZOOM_DEFAULT_Y = CameraConstants.LCD_SIZE_HEIGHT / VIDEO_SIZE_HD;
        mCurrentMaskMenu = PIP_MASK_OFF_EFFECT;
    }

    public PIPController(PIPControllerFunction function) {
        this.mSubWindowPosition = new RectInfo();
        this.mSmartZoomFocusViewPositon = new RectInfo();
        this.mPIPResizeHandlerView = null;
        this.mSmartZoomFocusView = null;
        this.mIsResizeHandlerShown = false;
        this.mIsSmartZoomFocusViewShown = false;
        this.vg = null;
        this.mMode = PIP_MASK_OFF_EFFECT;
        this.mSubWindowRatio = 1.5f;
        this.mPreviewSize = VIDEO_SIZE_HD;
        this.mInitPIPWidth = 0.0f;
        this.mInitPIPHeight = 0.0f;
        this.mMinSmallScreenWidth = PIP_MASK_OFF_EFFECT;
        this.mMinSmallScreenHeight = PIP_MASK_OFF_EFFECT;
        this.mMaxSmallScreenWidth = PIP_MASK_OFF_EFFECT;
        this.mMaxSmallScreenHeight = PIP_MASK_OFF_EFFECT;
        this.mMarginForFrameEffect = PIP_MASK_OFF_EFFECT;
        this.mMarginHeightForFrameEffectWindow = PIP_MASK_OFF_EFFECT;
        this.mMarginWidthForFrameEffectWindow = PIP_MASK_OFF_EFFECT;
        this.mMaskBitmapOutlineFinal = null;
        this.mSetBitmapDone = true;
        this.mGet = null;
        this.tmpRect = new RectInfo();
        this.mGet = function;
        prepare();
        this.mSubWindowPosition.setPosition(PIP_MASK_OFF_EFFECT, PIP_MASK_OFF_EFFECT, PIP_MASK_OFF_EFFECT, PIP_MASK_OFF_EFFECT);
        this.mPIPResizeHandlerView = new PIPResizeHandlerView(this.mGet.getApplicationContext());
        if (MultimediaProperties.isSmartZoomSupported()) {
            this.mSmartZoomFocusView = new SmartZoomFocusView(this.mGet.getApplicationContext());
        }
    }

    public PIPController(PIPControllerFunction function, int x0, int y0, int x1, int y1) {
        this.mSubWindowPosition = new RectInfo();
        this.mSmartZoomFocusViewPositon = new RectInfo();
        this.mPIPResizeHandlerView = null;
        this.mSmartZoomFocusView = null;
        this.mIsResizeHandlerShown = false;
        this.mIsSmartZoomFocusViewShown = false;
        this.vg = null;
        this.mMode = PIP_MASK_OFF_EFFECT;
        this.mSubWindowRatio = 1.5f;
        this.mPreviewSize = VIDEO_SIZE_HD;
        this.mInitPIPWidth = 0.0f;
        this.mInitPIPHeight = 0.0f;
        this.mMinSmallScreenWidth = PIP_MASK_OFF_EFFECT;
        this.mMinSmallScreenHeight = PIP_MASK_OFF_EFFECT;
        this.mMaxSmallScreenWidth = PIP_MASK_OFF_EFFECT;
        this.mMaxSmallScreenHeight = PIP_MASK_OFF_EFFECT;
        this.mMarginForFrameEffect = PIP_MASK_OFF_EFFECT;
        this.mMarginHeightForFrameEffectWindow = PIP_MASK_OFF_EFFECT;
        this.mMarginWidthForFrameEffectWindow = PIP_MASK_OFF_EFFECT;
        this.mMaskBitmapOutlineFinal = null;
        this.mSetBitmapDone = true;
        this.mGet = null;
        this.tmpRect = new RectInfo();
        this.mGet = function;
        prepare();
        this.mSubWindowPosition.setPosition(x0, y0, x1, y1);
        this.mPIPResizeHandlerView = new PIPResizeHandlerView(this.mGet.getApplicationContext());
        if (MultimediaProperties.isSmartZoomSupported()) {
            this.mSmartZoomFocusView = new SmartZoomFocusView(this.mGet.getApplicationContext());
        }
    }

    public void setSubWindowPosition(int x0, int y0, int x1, int y1) {
        this.mSubWindowPosition.setPosition(x0, y0, x1, y1);
        this.mSubWindowPosition.setPrevPositionUpdate();
    }

    public void setVideoSize(String videoSize) {
        if ("1920x1088".equals(videoSize) || "1920x1080".equals(videoSize)) {
            this.mPreviewSize = VIDEO_SIZE_FHD;
            setSubWindowRatio(1.7777778f);
        } else if ("1280x720".equals(videoSize)) {
            this.mPreviewSize = VIDEO_SIZE_HD;
            setSubWindowRatio(1.7777778f);
        } else if (MultimediaProperties.EFFECTS_ENFORCED_SIZE_FOR_UVGA.equals(videoSize)) {
            this.mPreviewSize = VIDEO_SIZE_UVGA;
            setSubWindowRatio(1.3333334f);
        } else if ("720x480".equals(videoSize)) {
            this.mPreviewSize = VIDEO_SIZE_TV;
            setSubWindowRatio(1.5f);
        } else if ("640x480".equals(videoSize)) {
            this.mPreviewSize = VIDEO_SIZE_VGA;
            setSubWindowRatio(1.3333334f);
        } else if ("320x240".equals(videoSize)) {
            this.mPreviewSize = VIDEO_SIZE_QVGA;
            setSubWindowRatio(1.3333334f);
        } else if ("176x144".equals(videoSize)) {
            this.mPreviewSize = VIDEO_SIZE_QCIF;
            setSubWindowRatio(1.2222222f);
        } else {
            this.mPreviewSize = PIP_MASK_OFF_EFFECT;
        }
        CamLog.d(FaceDetector.TAG, "setVideoSize = " + this.mPreviewSize);
        setMinMaxSmallScreenSize();
    }

    public void setSubWindowRatio(float ratio) {
        if (!this.mGet.isConfigureLandscape()) {
            ratio = RotateView.DEFAULT_TEXT_SCALE_X / ratio;
        }
        this.mSubWindowRatio = ratio;
    }

    private void prepare() {
        this.mScaleX = RotateView.DEFAULT_TEXT_SCALE_X;
        this.mScaleY = RotateView.DEFAULT_TEXT_SCALE_X;
        this.mScreenSizeX = CameraConstants.LCD_SIZE_WIDTH;
        this.mScreenSizeY = CameraConstants.LCD_SIZE_HEIGHT;
        setMinMaxPreviewSize();
        this.vg = (ViewGroup) this.mGet.findViewById(R.id.preview);
        addPIPFrameMaskImageToArray();
        addPIPFrameMaskOutlineImageToArray();
        if (MultimediaProperties.isDualRecordingSupported() || MultimediaProperties.isDualCameraSupported() || MultimediaProperties.isSmartZoomSupported()) {
            this.mGet.inflateStub(R.id.stub_pip_frame_sliding_drawable_menu);
            this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu_view).setVisibility(PIP_MASK_SPLITVIEW1_EFFECT);
            MultiDirectionSlidingDrawer slidingDrawer = (MultiDirectionSlidingDrawer) this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu_slide);
            slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
                public void onDrawerOpened() {
                    if (PIPController.this.mGet.isConfigureLandscape()) {
                        ((ImageView) PIPController.this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu_handle)).setImageResource(R.drawable.dual_camera_handler_open_land);
                    } else {
                        ((ImageView) PIPController.this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu_handle)).setImageResource(R.drawable.dual_camera_handler_open);
                    }
                    CamLog.d(FaceDetector.TAG, "onDrawerOpened");
                    if (PIPController.this.mGet.getVideoState() == 0) {
                        PIPController.this.mGet.setQuickFunctionAllMenuEnabled(false, true);
                        PIPController.this.mGet.startPIPFrameSubMenuRotation(PIPController.this.mGet.getOrientationDegree());
                    }
                    PIPController.this.mGet.setQuickButtonForcedDisable(true);
                    PIPController.this.mGet.setQuickButtonMenuEnable(false, false);
                }
            });
            slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
                public void onDrawerClosed() {
                    if (PIPController.this.mGet.isConfigureLandscape()) {
                        ((ImageView) PIPController.this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu_handle)).setImageResource(R.drawable.dual_camera_handler_close_land);
                    } else {
                        ((ImageView) PIPController.this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu_handle)).setImageResource(R.drawable.dual_camera_handler_close);
                    }
                    CamLog.d(FaceDetector.TAG, "onDrawerClosed");
                    if (PIPController.this.mGet.getVideoState() == 0) {
                        PIPController.this.mGet.quickFunctionControllerRefresh(true);
                        PIPController.this.mGet.startPIPFrameSubMenuRotation(PIPController.this.mGet.getOrientationDegree());
                    }
                    PIPController.this.mGet.setQuickButtonForcedDisable(false);
                    PIPController.this.mGet.setButtonRemainRefresh();
                }
            });
        }
    }

    private void setMinMaxPreviewSize() {
        this.mMinX = PIP_MASK_OFF_EFFECT;
        this.mMinY = PIP_MASK_OFF_EFFECT;
        int panel_width = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_panel_width);
        if (this.mPreviewSize == VIDEO_SIZE_FHD || this.mPreviewSize == VIDEO_SIZE_HD) {
            this.mMaxX = this.mScreenSizeX;
        } else {
            this.mMaxX = this.mScreenSizeX - panel_width;
        }
        this.mMaxY = this.mScreenSizeY;
        if (!this.mGet.isConfigureLandscape()) {
            int tmp = this.mMaxX;
            this.mMaxX = this.mMaxY;
            this.mMaxY = tmp;
        }
        setMinMaxSmallScreenSize();
    }

    private void setMinMaxSmallScreenSize() {
        int minWidth = (int) Util.dpToPx(this.mGet.getApplicationContext(), this.mInitPIPWidth * CameraConstants.PIP_SUBWINDOW_MINSIZE_RATIO_TO_DEFAULT_SIZE);
        this.mMinSmallScreenWidth = this.mGet.isConfigureLandscape() ? minWidth : Math.round(((float) minWidth) * this.mSubWindowRatio);
        if (this.mGet.isConfigureLandscape()) {
            minWidth = Math.round(((float) minWidth) / this.mSubWindowRatio);
        }
        this.mMinSmallScreenHeight = minWidth;
        if (ModelProperties.isUVGAmodel()) {
            this.mMaxSmallScreenWidth = (int) (((float) this.mMaxX) * CameraConstants.PIP_SUBWINDOW_MAXSIZE_RATIO_TO_PREVIEW_SIZE_FOR_UVGA);
            this.mMaxSmallScreenHeight = (int) (((float) this.mMaxY) * CameraConstants.PIP_SUBWINDOW_MAXSIZE_RATIO_TO_PREVIEW_SIZE_FOR_UVGA);
            return;
        }
        this.mMaxSmallScreenWidth = (int) (((float) this.mMaxX) * CameraConstants.PIP_SUBWINDOW_MAXSIZE_RATIO_TO_PREVIEW_SIZE);
        this.mMaxSmallScreenHeight = (int) (((float) this.mMaxY) * CameraConstants.PIP_SUBWINDOW_MAXSIZE_RATIO_TO_PREVIEW_SIZE);
    }

    public int[] getInitPIPSubWindowPosition(String videoSize) {
        int maxScreenSizeX;
        int maxScreenSizeY;
        PIPControllerFunction pIPControllerFunction;
        int subWindowWidth;
        int subWindowHeight;
        int panelWidth;
        int degree;
        int subWindowMargin_land_top;
        int subWindowMargin_land_left;
        int subWindowMargin_port_top;
        int subWindowMargin_port_right;
        int subWindowMarginX;
        int subWindowMarginY;
        int[] retPosition;
        int[][] initPositionLand;
        int[] iArr;
        int[][] initPositionPort;
        int idxPosition;
        int[][] initPosition;
        if (ModelProperties.getProjectCode() == PIP_MASK_SPLITVIEW2_EFFECT) {
            maxScreenSizeX = CameraConstants.LCD_SIZE_WIDTH;
            maxScreenSizeY = (maxScreenSizeX * PIP_MASK_SPLITVIEW2_EFFECT) / 16;
        } else {
            maxScreenSizeX = CameraConstants.LCD_SIZE_WIDTH;
            maxScreenSizeY = CameraConstants.LCD_SIZE_HEIGHT;
        }
        if (!this.mGet.isConfigureLandscape()) {
            int temp = maxScreenSizeX;
            maxScreenSizeX = maxScreenSizeY;
            maxScreenSizeY = temp;
        }
        if (!"1920x1088".equals(videoSize)) {
            if (!"1920x1080".equals(videoSize)) {
                if (MultimediaProperties.EFFECTS_ENFORCED_SIZE_FOR_UVGA.equals(videoSize)) {
                    this.mInitPIPWidth = CameraConstants.PIP_SUBWINDOW_INIT_WIDTH_UVGA;
                    this.mInitPIPHeight = CameraConstants.PIP_SUBWINDOW_INIT_HEIGHT_UVGA;
                } else {
                    if ("1280x720".equals(videoSize)) {
                        this.mInitPIPWidth = CameraConstants.PIP_SUBWINDOW_INIT_WIDTH_HD;
                        this.mInitPIPHeight = CameraConstants.PIP_SUBWINDOW_INIT_HEIGHT_UVGA;
                    } else {
                        if ("720x480".equals(videoSize)) {
                            this.mInitPIPWidth = CameraConstants.PIP_SUBWINDOW_INIT_WIDTH_TV;
                            this.mInitPIPHeight = CameraConstants.PIP_SUBWINDOW_INIT_HEIGHT_TV;
                        }
                    }
                }
                pIPControllerFunction = this.mGet;
                subWindowWidth = (int) Util.dpToPx(r0.getApplicationContext(), this.mInitPIPWidth);
                pIPControllerFunction = this.mGet;
                subWindowHeight = (int) Util.dpToPx(r0.getApplicationContext(), this.mInitPIPHeight);
                if (!this.mGet.isConfigureLandscape()) {
                    pIPControllerFunction = this.mGet;
                    subWindowWidth = (int) Util.dpToPx(r0.getApplicationContext(), this.mInitPIPHeight);
                    pIPControllerFunction = this.mGet;
                    subWindowHeight = (int) Util.dpToPx(r0.getApplicationContext(), this.mInitPIPWidth);
                }
                panelWidth = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_panel_width);
                degree = this.mGet.getOrientationDegree();
                if (!this.mGet.isConfigureLandscape()) {
                    degree = (degree + 90) % CameraConstants.DEGREE_360;
                }
                subWindowMargin_land_top = (int) Util.dpToPx(this.mGet.getApplicationContext(), CameraConstants.PIP_SUBWINDOW_OUTER_MARGIN_LAND_TOP);
                subWindowMargin_land_left = (int) Util.dpToPx(this.mGet.getApplicationContext(), CameraConstants.PIP_SUBWINDOW_OUTER_MARGIN_LAND_LEFT);
                subWindowMargin_port_top = (int) Util.dpToPx(this.mGet.getApplicationContext(), CameraConstants.PIP_SUBWINDOW_OUTER_MARGIN_PORT_TOP);
                subWindowMargin_port_right = (int) Util.dpToPx(this.mGet.getApplicationContext(), CameraConstants.PIP_SUBWINDOW_OUTER_MARGIN_PORT_RIGHT);
                if (degree != 0 || degree == 180) {
                    subWindowMarginX = subWindowMargin_land_left;
                    subWindowMarginY = subWindowMargin_land_top;
                } else {
                    subWindowMarginX = subWindowMargin_port_top;
                    subWindowMarginY = subWindowMargin_port_right;
                }
                retPosition = new int[VIDEO_SIZE_VGA];
                initPositionLand = new int[VIDEO_SIZE_VGA][];
                iArr = new int[VIDEO_SIZE_HD];
                iArr[PIP_MASK_OFF_EFFECT] = subWindowMarginX;
                iArr[VIDEO_SIZE_FHD] = (maxScreenSizeY - subWindowMarginY) - subWindowHeight;
                initPositionLand[PIP_MASK_OFF_EFFECT] = iArr;
                iArr = new int[VIDEO_SIZE_HD];
                iArr[PIP_MASK_OFF_EFFECT] = ((maxScreenSizeX - subWindowMarginX) - panelWidth) - subWindowWidth;
                iArr[VIDEO_SIZE_FHD] = (maxScreenSizeY - subWindowMarginY) - subWindowHeight;
                initPositionLand[VIDEO_SIZE_FHD] = iArr;
                iArr = new int[VIDEO_SIZE_HD];
                iArr[PIP_MASK_OFF_EFFECT] = ((maxScreenSizeX - subWindowMarginX) - panelWidth) - subWindowWidth;
                iArr[VIDEO_SIZE_FHD] = subWindowMarginY;
                initPositionLand[VIDEO_SIZE_HD] = iArr;
                iArr = new int[VIDEO_SIZE_HD];
                iArr[PIP_MASK_OFF_EFFECT] = subWindowMarginX;
                iArr[VIDEO_SIZE_FHD] = subWindowMarginY;
                initPositionLand[VIDEO_SIZE_TV] = iArr;
                initPositionPort = new int[VIDEO_SIZE_VGA][];
                iArr = new int[VIDEO_SIZE_HD];
                iArr[PIP_MASK_OFF_EFFECT] = subWindowMarginY;
                iArr[VIDEO_SIZE_FHD] = subWindowMarginX;
                initPositionPort[PIP_MASK_OFF_EFFECT] = iArr;
                iArr = new int[VIDEO_SIZE_HD];
                iArr[PIP_MASK_OFF_EFFECT] = subWindowMarginY;
                iArr[VIDEO_SIZE_FHD] = ((maxScreenSizeY - panelWidth) - subWindowMarginX) - subWindowHeight;
                initPositionPort[VIDEO_SIZE_FHD] = iArr;
                iArr = new int[VIDEO_SIZE_HD];
                iArr[PIP_MASK_OFF_EFFECT] = (maxScreenSizeX - subWindowMarginY) - subWindowWidth;
                iArr[VIDEO_SIZE_FHD] = ((maxScreenSizeY - panelWidth) - subWindowMarginX) - subWindowHeight;
                initPositionPort[VIDEO_SIZE_HD] = iArr;
                iArr = new int[VIDEO_SIZE_HD];
                iArr[PIP_MASK_OFF_EFFECT] = (maxScreenSizeX - subWindowMarginY) - subWindowWidth;
                iArr[VIDEO_SIZE_FHD] = subWindowMarginX;
                initPositionPort[VIDEO_SIZE_TV] = iArr;
                idxPosition = MultimediaProperties.PIP_SUBWINDOW_INIT_POSITION;
                if (this.mGet.isConfigureLandscape()) {
                    initPosition = initPositionPort;
                } else {
                    initPosition = initPositionLand;
                }
                retPosition[PIP_MASK_OFF_EFFECT] = initPosition[idxPosition][PIP_MASK_OFF_EFFECT];
                retPosition[VIDEO_SIZE_FHD] = initPosition[idxPosition][VIDEO_SIZE_FHD];
                retPosition[VIDEO_SIZE_HD] = retPosition[PIP_MASK_OFF_EFFECT] + subWindowWidth;
                retPosition[VIDEO_SIZE_TV] = retPosition[VIDEO_SIZE_FHD] + subWindowHeight;
                setInnerSpaceForFrameEffect(retPosition);
                if (this.mGet.isConfigureLandscape()) {
                    retPosition[VIDEO_SIZE_FHD] = retPosition[VIDEO_SIZE_FHD] - this.mMarginForFrameEffect;
                } else {
                    retPosition[PIP_MASK_OFF_EFFECT] = retPosition[PIP_MASK_OFF_EFFECT] - this.mMarginForFrameEffect;
                }
                retPosition[VIDEO_SIZE_HD] = retPosition[PIP_MASK_OFF_EFFECT] + subWindowWidth;
                retPosition[VIDEO_SIZE_TV] = retPosition[VIDEO_SIZE_FHD] + subWindowHeight;
                setVideoSize(videoSize);
                setSubWindowPosition(retPosition[PIP_MASK_OFF_EFFECT], retPosition[VIDEO_SIZE_FHD], retPosition[VIDEO_SIZE_HD], retPosition[VIDEO_SIZE_TV]);
                return retPosition;
            }
        }
        this.mInitPIPWidth = CameraConstants.PIP_SUBWINDOW_INIT_WIDTH_HD;
        this.mInitPIPHeight = CameraConstants.PIP_SUBWINDOW_INIT_HEIGHT_UVGA;
        pIPControllerFunction = this.mGet;
        subWindowWidth = (int) Util.dpToPx(r0.getApplicationContext(), this.mInitPIPWidth);
        pIPControllerFunction = this.mGet;
        subWindowHeight = (int) Util.dpToPx(r0.getApplicationContext(), this.mInitPIPHeight);
        if (this.mGet.isConfigureLandscape()) {
            pIPControllerFunction = this.mGet;
            subWindowWidth = (int) Util.dpToPx(r0.getApplicationContext(), this.mInitPIPHeight);
            pIPControllerFunction = this.mGet;
            subWindowHeight = (int) Util.dpToPx(r0.getApplicationContext(), this.mInitPIPWidth);
        }
        panelWidth = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_panel_width);
        degree = this.mGet.getOrientationDegree();
        if (this.mGet.isConfigureLandscape()) {
            degree = (degree + 90) % CameraConstants.DEGREE_360;
        }
        subWindowMargin_land_top = (int) Util.dpToPx(this.mGet.getApplicationContext(), CameraConstants.PIP_SUBWINDOW_OUTER_MARGIN_LAND_TOP);
        subWindowMargin_land_left = (int) Util.dpToPx(this.mGet.getApplicationContext(), CameraConstants.PIP_SUBWINDOW_OUTER_MARGIN_LAND_LEFT);
        subWindowMargin_port_top = (int) Util.dpToPx(this.mGet.getApplicationContext(), CameraConstants.PIP_SUBWINDOW_OUTER_MARGIN_PORT_TOP);
        subWindowMargin_port_right = (int) Util.dpToPx(this.mGet.getApplicationContext(), CameraConstants.PIP_SUBWINDOW_OUTER_MARGIN_PORT_RIGHT);
        if (degree != 0) {
        }
        subWindowMarginX = subWindowMargin_land_left;
        subWindowMarginY = subWindowMargin_land_top;
        retPosition = new int[VIDEO_SIZE_VGA];
        initPositionLand = new int[VIDEO_SIZE_VGA][];
        iArr = new int[VIDEO_SIZE_HD];
        iArr[PIP_MASK_OFF_EFFECT] = subWindowMarginX;
        iArr[VIDEO_SIZE_FHD] = (maxScreenSizeY - subWindowMarginY) - subWindowHeight;
        initPositionLand[PIP_MASK_OFF_EFFECT] = iArr;
        iArr = new int[VIDEO_SIZE_HD];
        iArr[PIP_MASK_OFF_EFFECT] = ((maxScreenSizeX - subWindowMarginX) - panelWidth) - subWindowWidth;
        iArr[VIDEO_SIZE_FHD] = (maxScreenSizeY - subWindowMarginY) - subWindowHeight;
        initPositionLand[VIDEO_SIZE_FHD] = iArr;
        iArr = new int[VIDEO_SIZE_HD];
        iArr[PIP_MASK_OFF_EFFECT] = ((maxScreenSizeX - subWindowMarginX) - panelWidth) - subWindowWidth;
        iArr[VIDEO_SIZE_FHD] = subWindowMarginY;
        initPositionLand[VIDEO_SIZE_HD] = iArr;
        iArr = new int[VIDEO_SIZE_HD];
        iArr[PIP_MASK_OFF_EFFECT] = subWindowMarginX;
        iArr[VIDEO_SIZE_FHD] = subWindowMarginY;
        initPositionLand[VIDEO_SIZE_TV] = iArr;
        initPositionPort = new int[VIDEO_SIZE_VGA][];
        iArr = new int[VIDEO_SIZE_HD];
        iArr[PIP_MASK_OFF_EFFECT] = subWindowMarginY;
        iArr[VIDEO_SIZE_FHD] = subWindowMarginX;
        initPositionPort[PIP_MASK_OFF_EFFECT] = iArr;
        iArr = new int[VIDEO_SIZE_HD];
        iArr[PIP_MASK_OFF_EFFECT] = subWindowMarginY;
        iArr[VIDEO_SIZE_FHD] = ((maxScreenSizeY - panelWidth) - subWindowMarginX) - subWindowHeight;
        initPositionPort[VIDEO_SIZE_FHD] = iArr;
        iArr = new int[VIDEO_SIZE_HD];
        iArr[PIP_MASK_OFF_EFFECT] = (maxScreenSizeX - subWindowMarginY) - subWindowWidth;
        iArr[VIDEO_SIZE_FHD] = ((maxScreenSizeY - panelWidth) - subWindowMarginX) - subWindowHeight;
        initPositionPort[VIDEO_SIZE_HD] = iArr;
        iArr = new int[VIDEO_SIZE_HD];
        iArr[PIP_MASK_OFF_EFFECT] = (maxScreenSizeX - subWindowMarginY) - subWindowWidth;
        iArr[VIDEO_SIZE_FHD] = subWindowMarginX;
        initPositionPort[VIDEO_SIZE_TV] = iArr;
        idxPosition = MultimediaProperties.PIP_SUBWINDOW_INIT_POSITION;
        if (this.mGet.isConfigureLandscape()) {
            initPosition = initPositionPort;
        } else {
            initPosition = initPositionLand;
        }
        retPosition[PIP_MASK_OFF_EFFECT] = initPosition[idxPosition][PIP_MASK_OFF_EFFECT];
        retPosition[VIDEO_SIZE_FHD] = initPosition[idxPosition][VIDEO_SIZE_FHD];
        retPosition[VIDEO_SIZE_HD] = retPosition[PIP_MASK_OFF_EFFECT] + subWindowWidth;
        retPosition[VIDEO_SIZE_TV] = retPosition[VIDEO_SIZE_FHD] + subWindowHeight;
        setInnerSpaceForFrameEffect(retPosition);
        if (this.mGet.isConfigureLandscape()) {
            retPosition[PIP_MASK_OFF_EFFECT] = retPosition[PIP_MASK_OFF_EFFECT] - this.mMarginForFrameEffect;
        } else {
            retPosition[VIDEO_SIZE_FHD] = retPosition[VIDEO_SIZE_FHD] - this.mMarginForFrameEffect;
        }
        retPosition[VIDEO_SIZE_HD] = retPosition[PIP_MASK_OFF_EFFECT] + subWindowWidth;
        retPosition[VIDEO_SIZE_TV] = retPosition[VIDEO_SIZE_FHD] + subWindowHeight;
        setVideoSize(videoSize);
        setSubWindowPosition(retPosition[PIP_MASK_OFF_EFFECT], retPosition[VIDEO_SIZE_FHD], retPosition[VIDEO_SIZE_HD], retPosition[VIDEO_SIZE_TV]);
        return retPosition;
    }

    public PIPResizeHandlerView getDualRecSubWindowResizeHandler() {
        return this.mPIPResizeHandlerView;
    }

    public void resizeSubWindowResizeHandler(int direction) {
        this.mPIPResizeHandlerView.updatePosition(direction);
    }

    public void showSubWindowResizeHandler() {
        if (this.vg != null && this.vg.indexOfChild(this.mPIPResizeHandlerView) == -1) {
            setSubWindowResizeHandlerPosition();
            this.vg.invalidate();
            this.vg.addView(this.mPIPResizeHandlerView);
            this.mIsResizeHandlerShown = true;
            this.mGet.doCommandDelayed(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER, CameraConstants.TOAST_LENGTH_LONG);
        }
    }

    public void hideSubWindowResizeHandler() {
        if (this.vg != null && this.vg.indexOfChild(this.mPIPResizeHandlerView) != -1) {
            this.vg.removeView(this.mPIPResizeHandlerView);
            this.mIsResizeHandlerShown = false;
            setMode(PIP_MASK_OFF_EFFECT);
        }
    }

    public void showSmartZoomFocusView() {
        if (this.vg != null && this.vg.indexOfChild(this.mSmartZoomFocusView) == -1) {
            this.vg.invalidate();
            this.vg.addView(this.mSmartZoomFocusView);
            this.mIsSmartZoomFocusViewShown = true;
        }
    }

    public void hideSmartZoomFocusView() {
        if (this.vg != null && this.vg.indexOfChild(this.mSmartZoomFocusView) != -1) {
            this.vg.removeView(this.mSmartZoomFocusView);
            this.mIsSmartZoomFocusViewShown = false;
        }
    }

    public void setSmartZoomLayoutSize(int width, int height, int leftMargin) {
        int initX = (width / VIDEO_SIZE_HD) + leftMargin;
        int initY = height / VIDEO_SIZE_HD;
        this.previewTopMargin = (this.mScreenSizeY - height) / VIDEO_SIZE_HD;
        SMARTZOOM_DEFAULT_X = this.mGet.isConfigureLandscape() ? this.previewTopMargin + initX : this.previewTopMargin + initY;
        if (!this.mGet.isConfigureLandscape()) {
            initY = initX;
        }
        SMARTZOOM_DEFAULT_Y = initY;
    }

    public void initSmartZoomFocusView() {
        setSmartZoomFocusViewPosition(SMARTZOOM_DEFAULT_X, SMARTZOOM_DEFAULT_Y);
    }

    public boolean isResizeHandlerShown() {
        return this.mIsResizeHandlerShown;
    }

    public boolean isSmartZoomFocusViewShown() {
        return this.mIsSmartZoomFocusViewShown;
    }

    public void moveSubWindow(int diffX, int diffY) {
        if (setPositionByDiff(diffX, diffY)) {
            if (MultimediaProperties.PIP_SUPPORT_REALTIME_WINDOW_UPDATE || getMode() != VIDEO_SIZE_FHD) {
                drawSubWindow();
            }
            setSubWindowResizeHandlerPosition();
        }
    }

    public void resizeSubWindowByDiff(int diffX, int diffY, int direction) {
        if (setSizeByDiff(diffX, diffY, direction)) {
            if (MultimediaProperties.PIP_SUPPORT_REALTIME_WINDOW_UPDATE) {
                drawSubWindow();
            }
            setSubWindowResizeHandlerPosition();
        }
    }

    public void confirmSubWindow() {
        this.mSubWindowPosition.setPrevPositionUpdate();
    }

    public void restoreSubWindow() {
        if (!MultimediaProperties.PIP_SUPPORT_REALTIME_WINDOW_UPDATE) {
            this.mSubWindowPosition.restorePosition();
            setSubWindowResizeHandlerPosition();
        }
    }

    public void drawSubWindow() {
        drawSubWindowWithScale(this.mScaleX, this.mScaleY);
    }

    public int setDirectionForRatio(int direction, int initialX, int initialY, int orientation) {
        int halfWidth = this.mSubWindowPosition.mRectX0 + ((this.mSubWindowPosition.mRectX1 - this.mSubWindowPosition.mRectX0) / VIDEO_SIZE_HD);
        int halfHeight = this.mSubWindowPosition.mRectY0 + ((this.mSubWindowPosition.mRectY1 - this.mSubWindowPosition.mRectY0) / VIDEO_SIZE_HD);
        if (direction == 15 || direction == 0) {
            return direction;
        }
        if ((direction & VIDEO_SIZE_HD) == 0 && (direction & PIP_MASK_SPLITVIEW1_EFFECT) == 0) {
            if (initialY < halfHeight) {
                return direction | VIDEO_SIZE_HD;
            }
            return direction | PIP_MASK_SPLITVIEW1_EFFECT;
        } else if (initialX < halfWidth) {
            return direction | VIDEO_SIZE_FHD;
        } else {
            return direction | VIDEO_SIZE_VGA;
        }
    }

    public int checkResizeDirection(int x, int y) {
        return checkResizeDirectionWithMargin(x, y, 80, 80);
    }

    private int checkResizeDirectionWithMargin(int x, int y, int innerMargin, int outerMargin) {
        int[] position = new int[VIDEO_SIZE_VGA];
        position[PIP_MASK_OFF_EFFECT] = this.mSubWindowPosition.mRectX0;
        position[VIDEO_SIZE_FHD] = this.mSubWindowPosition.mRectY0;
        position[VIDEO_SIZE_HD] = this.mSubWindowPosition.mRectX1;
        position[VIDEO_SIZE_TV] = this.mSubWindowPosition.mRectY1;
        resizeHandlerForFrameEffect(position);
        int x0 = position[PIP_MASK_OFF_EFFECT];
        int y0 = position[VIDEO_SIZE_FHD];
        int x1 = position[VIDEO_SIZE_HD];
        int y1 = position[VIDEO_SIZE_TV];
        if (x < x0 - outerMargin || x > x1 + outerMargin || y < y0 - outerMargin || y > y1 + outerMargin) {
            return PIP_MASK_OFF_EFFECT;
        }
        if (x < x0 + innerMargin && x > x0 - outerMargin) {
            return VIDEO_SIZE_FHD;
        }
        if (x > x1 - innerMargin && x < x1 + outerMargin) {
            return VIDEO_SIZE_VGA;
        }
        if (y < y0 + innerMargin && y > y0 - outerMargin) {
            return VIDEO_SIZE_HD;
        }
        if (y <= y1 - innerMargin || y >= y1 + outerMargin) {
            return 15;
        }
        return PIP_MASK_SPLITVIEW1_EFFECT;
    }

    private void setSubWindowResizeHandlerPosition() {
        CamLog.d(FaceDetector.TAG, "setSubWindowResizeHandlerPosition: (" + this.mSubWindowPosition.mRectX0 + " ," + this.mSubWindowPosition.mRectY0 + ") (" + this.mSubWindowPosition.mRectX1 + " ," + this.mSubWindowPosition.mRectY1 + ")");
        if (!this.mGet.isConfigureLandscape()) {
            int paddingX = PIP_MASK_OFF_EFFECT;
        }
        if (this.mGet.isConfigureLandscape()) {
            int paddingY = PIP_MASK_OFF_EFFECT;
        }
        int[] position = new int[VIDEO_SIZE_VGA];
        position[PIP_MASK_OFF_EFFECT] = (this.mSubWindowPosition.mRectX0 + VIDEO_SIZE_TV) + PIP_MASK_OFF_EFFECT;
        position[VIDEO_SIZE_FHD] = (this.mSubWindowPosition.mRectY0 + VIDEO_SIZE_TV) + PIP_MASK_OFF_EFFECT;
        position[VIDEO_SIZE_HD] = (this.mSubWindowPosition.mRectX1 - VIDEO_SIZE_TV) + PIP_MASK_OFF_EFFECT;
        position[VIDEO_SIZE_TV] = (this.mSubWindowPosition.mRectY1 - VIDEO_SIZE_TV) + PIP_MASK_OFF_EFFECT;
        resizeHandlerForFrameEffect(position, getDegree());
        this.mPIPResizeHandlerView.setPosition(position[PIP_MASK_OFF_EFFECT], position[VIDEO_SIZE_FHD], position[VIDEO_SIZE_HD], position[VIDEO_SIZE_TV]);
    }

    public void setSmartZoomFocusViewPosition(int x, int y) {
        int dimenWidth;
        int dimenHeight;
        int mTargetZoomAreaWidth;
        int mTargetZoomAreaHeight;
        if (ModelProperties.isUVGAmodel()) {
            dimenWidth = (int) Util.dpToPx(this.mGet.getApplicationContext(), CameraConstants.SMARTZOOM_ZOOM_AREA_WIDTH_UVGA);
            dimenHeight = (int) Util.dpToPx(this.mGet.getApplicationContext(), CameraConstants.SMARTZOOM_ZOOM_AREA_HEIGHT_UVGA);
        } else {
            dimenWidth = (int) Util.dpToPx(this.mGet.getApplicationContext(), CameraConstants.SMARTZOOM_ZOOM_AREA_WIDTH);
            dimenHeight = (int) Util.dpToPx(this.mGet.getApplicationContext(), CameraConstants.SMARTZOOM_ZOOM_AREA_HEIGHT_UVGA);
        }
        if (this.mGet.isConfigureLandscape()) {
            mTargetZoomAreaWidth = dimenWidth;
        } else {
            mTargetZoomAreaWidth = dimenHeight;
        }
        if (this.mGet.isConfigureLandscape()) {
            mTargetZoomAreaHeight = dimenHeight;
        } else {
            mTargetZoomAreaHeight = dimenWidth;
        }
        x -= this.previewTopMargin;
        int x0 = x - (mTargetZoomAreaWidth / VIDEO_SIZE_HD);
        int y0 = y - (mTargetZoomAreaHeight / VIDEO_SIZE_HD);
        int x1 = x + (mTargetZoomAreaWidth / VIDEO_SIZE_HD);
        int y1 = y + (mTargetZoomAreaHeight / VIDEO_SIZE_HD);
        CamLog.d(FaceDetector.TAG, "setSmartZoomFocusViewPosition: (" + x0 + " ," + y0 + ") (" + x1 + " ," + y1 + ")");
        ObjectFilter.updateObjPosition(x, y);
        this.mSmartZoomFocusViewPositon.setPosition(x0, y0, x1, y1);
        this.mSmartZoomFocusView.setPositionAndUpdate(x0, y0, x1, y1, getDegree());
    }

    public void updateOrientation() {
        this.mSmartZoomFocusView.setPositionAndUpdate(this.mSmartZoomFocusViewPositon.mRectX0, this.mSmartZoomFocusViewPositon.mRectY0, this.mSmartZoomFocusViewPositon.mRectX1, this.mSmartZoomFocusViewPositon.mRectY1, getDegree());
    }

    private boolean setPosition(RectInfo rect) {
        setToValidPosition(rect, getDegree());
        if (!isValidSize(rect)) {
            return false;
        }
        this.mSubWindowPosition.setPosition(rect);
        return true;
    }

    private boolean setPositionByDiff(int diffX, int diffY) {
        this.tmpRect.setPosition(this.mSubWindowPosition.mRectX0 + diffX, this.mSubWindowPosition.mRectY0 + diffY, this.mSubWindowPosition.mRectX1 + diffX, this.mSubWindowPosition.mRectY1 + diffY);
        if (setPosition(this.tmpRect)) {
            return true;
        }
        return false;
    }

    private boolean setSizeByDiff(int diffX, int diffY, int direction) {
        int i;
        int offsetWidth;
        int offsetHeight;
        RectInfo rectInfo = this.tmpRect;
        int access$100 = this.mSubWindowPosition.mRectX0;
        if ((direction & VIDEO_SIZE_FHD) != 0) {
            i = diffX;
        } else {
            i = PIP_MASK_OFF_EFFECT;
        }
        access$100 += i;
        int access$300 = this.mSubWindowPosition.mRectY0;
        if ((direction & VIDEO_SIZE_HD) != 0) {
            i = diffY;
        } else {
            i = PIP_MASK_OFF_EFFECT;
        }
        access$300 += i;
        int access$200 = this.mSubWindowPosition.mRectX1;
        if ((direction & VIDEO_SIZE_VGA) != 0) {
            i = diffX;
        } else {
            i = PIP_MASK_OFF_EFFECT;
        }
        access$200 += i;
        int access$400 = this.mSubWindowPosition.mRectY1;
        if ((direction & PIP_MASK_SPLITVIEW1_EFFECT) != 0) {
            i = diffY;
        } else {
            i = PIP_MASK_OFF_EFFECT;
        }
        rectInfo.setPosition(access$100, access$300, access$200, i + access$400);
        fitToRatio(this.tmpRect, diffX, diffY, direction);
        int marginBefore = getInnerSpaceOfResizeHandler(this.mSubWindowPosition);
        int marginAfter = getInnerSpaceOfResizeHandler(this.tmpRect);
        int marginWindowHeightBefore = getInnerSpaceHeightOfResizeHandlerForWindowEffect(this.mSubWindowPosition.mRectX0, this.mSubWindowPosition.mRectY0, this.mSubWindowPosition.mRectX1, this.mSubWindowPosition.mRectY1);
        int marginWindowWidthBefore = getInnerSpaceWidthOfResizeHandlerForWindowEffect(this.mSubWindowPosition.mRectX0, this.mSubWindowPosition.mRectY0, this.mSubWindowPosition.mRectX1, this.mSubWindowPosition.mRectY1);
        int marginWindowHeightAfter = getInnerSpaceHeightOfResizeHandlerForWindowEffect(this.tmpRect.mRectX0, this.tmpRect.mRectY0, this.tmpRect.mRectX1, this.tmpRect.mRectY1);
        int offset = marginAfter - marginBefore;
        int offsetWidthForWindow = getInnerSpaceWidthOfResizeHandlerForWindowEffect(this.tmpRect.mRectX0, this.tmpRect.mRectY0, this.tmpRect.mRectX1, this.tmpRect.mRectY1) - marginWindowWidthBefore;
        int offsetHeightForWindow = marginWindowHeightAfter - marginWindowHeightBefore;
        int degree = getDegree();
        int offsetW = PIP_MASK_OFF_EFFECT;
        int offsetH = PIP_MASK_OFF_EFFECT;
        if (this.mGet.isConfigureLandscape()) {
            offsetW = offset;
        } else {
            offsetH = offset;
        }
        if (degree == 0 || degree == 180) {
            offsetWidth = offsetW + offsetWidthForWindow;
            offsetHeight = offsetH + offsetHeightForWindow;
        } else {
            offsetWidth = offsetW + offsetHeightForWindow;
            offsetHeight = offsetH + offsetWidthForWindow;
        }
        if ((direction & VIDEO_SIZE_VGA) != 0) {
            offsetWidth = -offsetWidth;
        }
        if ((direction & PIP_MASK_SPLITVIEW1_EFFECT) != 0) {
            offsetHeight = -offsetHeight;
        }
        this.tmpRect.setPosition(this.tmpRect.mRectX0 + offsetWidth, this.tmpRect.mRectY0 + offsetHeight, this.tmpRect.mRectX1 + offsetWidth, this.tmpRect.mRectY1 + offsetHeight);
        if (setPosition(this.tmpRect)) {
            return true;
        }
        return false;
    }

    private void fitToRatio(RectInfo mRect, int diffX, int diffY, int direction) {
        if (diffY * diffY > diffX * diffX) {
            if ((direction & VIDEO_SIZE_FHD) != 0) {
                mRect.mRectX0 = mRect.mRectX1 - ((int) (((float) mRect.mHeight) * this.mSubWindowRatio));
            } else {
                mRect.mRectX1 = mRect.mRectX0 + ((int) (((float) mRect.mHeight) * this.mSubWindowRatio));
            }
        } else if ((direction & VIDEO_SIZE_HD) != 0) {
            mRect.mRectY0 = mRect.mRectY1 - ((int) (((float) mRect.mWidth) / this.mSubWindowRatio));
        } else {
            mRect.mRectY1 = mRect.mRectY0 + ((int) (((float) mRect.mWidth) / this.mSubWindowRatio));
        }
    }

    private void setToValidPosition(RectInfo rect, int degree) {
        int minX = this.mMinX;
        int minY = this.mMinY;
        int maxX = this.mMaxX;
        int maxY = this.mMaxY;
        if (this.mGet.isConfigureLandscape()) {
            minX -= this.mMarginForFrameEffect;
            maxX += this.mMarginForFrameEffect;
        } else {
            minY -= this.mMarginForFrameEffect;
            maxY += this.mMarginForFrameEffect;
        }
        if (degree == 0 || degree == MediaProviderUtils.ROTATION_180) {
            minX -= this.mMarginWidthForFrameEffectWindow;
            maxX += this.mMarginWidthForFrameEffectWindow;
            minY -= this.mMarginHeightForFrameEffectWindow;
            maxY += this.mMarginHeightForFrameEffectWindow;
        } else {
            minX -= this.mMarginHeightForFrameEffectWindow;
            maxX += this.mMarginHeightForFrameEffectWindow;
            minY -= this.mMarginWidthForFrameEffectWindow;
            maxY += this.mMarginWidthForFrameEffectWindow;
        }
        if (rect.mRectX0 <= minX) {
            RectInfo.access$212(rect, minX - rect.mRectX0);
            rect.mRectX0 = minX;
        }
        if (rect.mRectY0 <= minY) {
            RectInfo.access$412(rect, minY - rect.mRectY0);
            rect.mRectY0 = minY;
        }
        if (rect.mRectX1 >= maxX) {
            RectInfo.access$112(rect, maxX - rect.mRectX1);
            rect.mRectX1 = maxX;
        }
        if (rect.mRectY1 >= maxY) {
            RectInfo.access$312(rect, maxY - rect.mRectY1);
            rect.mRectY1 = maxY;
        }
    }

    private boolean isValidSize(RectInfo rect) {
        if (rect.mRectX1 - rect.mRectX0 < this.mMinSmallScreenWidth || rect.mRectX1 - rect.mRectX0 > this.mMaxSmallScreenWidth || rect.mRectY1 - rect.mRectY0 < this.mMinSmallScreenHeight || rect.mRectY1 - rect.mRectY0 > this.mMaxSmallScreenHeight) {
            return false;
        }
        return true;
    }

    private void drawSubWindowWithScale(float scaleX, float scaleY) {
        if (this.mGet.isDualRecordingActive() || this.mGet.isDualCameraActive()) {
            DualRecorderFilter.setPIPPosition((int) (((float) this.mSubWindowPosition.mRectX0) * scaleX), (int) (((float) this.mSubWindowPosition.mRectY0) * scaleY), (int) (((float) this.mSubWindowPosition.mWidth) * scaleX), (int) (((float) this.mSubWindowPosition.mHeight) * scaleY));
        } else {
            SmartZoomFilter.setPIPPosition((int) (((float) this.mSubWindowPosition.mRectX0) * scaleX), (int) (((float) this.mSubWindowPosition.mRectY0) * scaleY), (int) (((float) this.mSubWindowPosition.mWidth) * scaleX), (int) (((float) this.mSubWindowPosition.mHeight) * scaleY));
        }
    }

    public boolean isInSubWindow(int x, int y) {
        int[] position = new int[VIDEO_SIZE_VGA];
        position[PIP_MASK_OFF_EFFECT] = this.mSubWindowPosition.mRectX0;
        position[VIDEO_SIZE_FHD] = this.mSubWindowPosition.mRectY0;
        position[VIDEO_SIZE_HD] = this.mSubWindowPosition.mRectX1;
        position[VIDEO_SIZE_TV] = this.mSubWindowPosition.mRectY1;
        resizeHandlerForFrameEffect(position);
        int x0 = position[PIP_MASK_OFF_EFFECT];
        int y0 = position[VIDEO_SIZE_FHD];
        int x1 = position[VIDEO_SIZE_HD];
        int y1 = position[VIDEO_SIZE_TV];
        if (x <= x0 || y <= y0 || x >= x1 || y >= y1) {
            return false;
        }
        return true;
    }

    public boolean isInSmartZoomFocusViewArea(int x, int y) {
        if (!this.mGet.isSmartZoomRecordingActive()) {
            return false;
        }
        int marginWidth;
        int[] position = new int[VIDEO_SIZE_VGA];
        position[PIP_MASK_OFF_EFFECT] = this.mSmartZoomFocusViewPositon.mRectX0;
        position[VIDEO_SIZE_FHD] = this.mSmartZoomFocusViewPositon.mRectY0;
        position[VIDEO_SIZE_HD] = this.mSmartZoomFocusViewPositon.mRectX1;
        position[VIDEO_SIZE_TV] = this.mSmartZoomFocusViewPositon.mRectY1;
        int x0 = position[PIP_MASK_OFF_EFFECT];
        int y0 = position[VIDEO_SIZE_FHD];
        int x1 = position[VIDEO_SIZE_HD];
        int y1 = position[VIDEO_SIZE_TV];
        int marginHeight;
        if (getSmartZoomFocusViewMode() == VIDEO_SIZE_HD) {
            marginWidth = ((x1 - x0) - CameraConstants.SMARTZOOM_AUTO_ZOOM_AREA_MARGIN_WIDTH) / VIDEO_SIZE_HD;
            marginHeight = ((y1 - y0) - CameraConstants.SMARTZOOM_AUTO_ZOOM_AREA_MARGIN_HEIGHT) / VIDEO_SIZE_HD;
        } else {
            marginWidth = ((x1 - x0) - CameraConstants.SMARTZOOM_MANUAL_ZOOM_AREA_MARGIN_WIDTH) / VIDEO_SIZE_HD;
            marginHeight = ((y1 - y0) - CameraConstants.SMARTZOOM_MANUAL_ZOOM_AREA_MARGIN_HEIGHT) / VIDEO_SIZE_HD;
        }
        if (x <= x0 + marginWidth || y <= y0 + marginHeight || x >= x1 - marginWidth || y >= y1 - marginHeight) {
            return false;
        }
        return true;
    }

    private void addPIPFrameMaskImageToArray() {
        this.mPIPFrameMaskImage = new ArrayList();
        this.mPIPFrameMaskImage.add(Integer.valueOf(R.drawable.dual_camera_effect_off));
        this.mPIPFrameMaskImage.add(Integer.valueOf(R.drawable.dual_camera_effect_window));
        this.mPIPFrameMaskImage.add(Integer.valueOf(R.drawable.dual_camera_effect_stamp));
        this.mPIPFrameMaskImage.add(Integer.valueOf(R.drawable.dual_camera_effect_ovalblur));
        this.mPIPFrameMaskImage.add(Integer.valueOf(R.drawable.dual_camera_effect_instantpic));
        this.mPIPFrameMaskImage.add(Integer.valueOf(R.drawable.dual_camera_effect_heart));
        this.mPIPFrameMaskImage.add(Integer.valueOf(R.drawable.dual_camera_effect_star));
        this.mPIPFrameMaskImage.add(Integer.valueOf(R.drawable.dual_camera_effect_fisheye));
        this.mPIPFrameMaskImage.add(Integer.valueOf(R.drawable.dual_camera_effect_splitview1));
        this.mPIPFrameMaskImage.add(Integer.valueOf(R.drawable.dual_camera_effect_splitview2));
    }

    private void addPIPFrameMaskOutlineImageToArray() {
        this.mPIPFrameMaskOutlineImage = new ArrayList();
        this.mPIPFrameMaskOutlineImage.add(Integer.valueOf(R.drawable.dual_camera_effect_empty));
        this.mPIPFrameMaskOutlineImage.add(Integer.valueOf(R.drawable.dual_camera_effect_window_outline));
        this.mPIPFrameMaskOutlineImage.add(Integer.valueOf(R.drawable.dual_camera_effect_stamp_outline));
        this.mPIPFrameMaskOutlineImage.add(null);
        this.mPIPFrameMaskOutlineImage.add(Integer.valueOf(R.drawable.dual_camera_effect_instantpic_outline));
        this.mPIPFrameMaskOutlineImage.add(Integer.valueOf(R.drawable.dual_camera_effect_heart_outline));
        this.mPIPFrameMaskOutlineImage.add(Integer.valueOf(R.drawable.dual_camera_effect_star_outline));
        this.mPIPFrameMaskOutlineImage.add(Integer.valueOf(R.drawable.dual_camera_effect_fisheye_outline));
        this.mPIPFrameMaskOutlineImage.add(Integer.valueOf(R.drawable.dual_camera_effect_empty));
        this.mPIPFrameMaskOutlineImage.add(Integer.valueOf(R.drawable.dual_camera_effect_empty));
    }

    public boolean setPIPMask(int pipFrameSelectedMenu) {
        if (!this.mSetBitmapDone) {
            CamLog.d(FaceDetector.TAG, "Previous setPIPMask is ongoing. Do nothing and just return");
            return false;
        }
        Bitmap maskBitmapOutline;
        int effectType;
        this.mSetBitmapDone = false;
        Options option = new Options();
        option.inSampleSize = VIDEO_SIZE_FHD;
        option.inPurgeable = true;
        int degreeOffset = PIP_MASK_OFF_EFFECT;
        if (this.mMaskBitmapShapeFinal != null) {
            this.mMaskBitmapShapeFinal.recycle();
            this.mMaskBitmapShapeFinal = null;
        }
        if (this.mMaskBitmapOutlineFinal != null) {
            this.mMaskBitmapOutlineFinal.recycle();
            this.mMaskBitmapOutlineFinal = null;
        }
        mCurrentMaskMenu = pipFrameSelectedMenu;
        CamLog.d(FaceDetector.TAG, "setPIPMask : pipFrameSelectedMenu = " + pipFrameSelectedMenu);
        Bitmap maskBitmapShape = BitmapFactory.decodeResource(this.mGet.getResources(), ((Integer) this.mPIPFrameMaskImage.get(pipFrameSelectedMenu)).intValue(), option);
        if (this.mPIPFrameMaskOutlineImage.get(pipFrameSelectedMenu) != null) {
            maskBitmapOutline = BitmapFactory.decodeResource(this.mGet.getResources(), ((Integer) this.mPIPFrameMaskOutlineImage.get(pipFrameSelectedMenu)).intValue(), option);
        } else {
            maskBitmapOutline = null;
        }
        if (pipFrameSelectedMenu == PIP_MASK_SPLITVIEW1_EFFECT || pipFrameSelectedMenu == PIP_MASK_SPLITVIEW2_EFFECT) {
            this.mMaskBitmapShapeFinal = maskBitmapShape;
            this.mMaskBitmapOutlineFinal = maskBitmapOutline;
        } else {
            if (pipFrameSelectedMenu == VIDEO_SIZE_VGA && maskBitmapOutline != null) {
                maskBitmapOutline = Util.drawTextToBitmap(this.mGet.getActivity(), maskBitmapOutline, 183, 456, new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(new Date()), 42, Color.rgb(PIP_MASK_OFF_EFFECT, PIP_MASK_OFF_EFFECT, PIP_MASK_OFF_EFFECT), "HYRPostM_13_0624.ttf");
                degreeOffset = -6;
            }
            if (PIP_MASK_OFF_EFFECT == null || PIP_MASK_OFF_EFFECT == 180) {
                maskBitmapShape = Util.rotate(maskBitmapShape, degreeOffset + Tag.IMAGE_DESCRIPTION);
            } else {
                maskBitmapShape = Util.rotate(maskBitmapShape, degreeOffset + 90);
            }
            this.mMaskBitmapShapeFinal = maskBitmapShape;
            if (maskBitmapOutline != null) {
                if (PIP_MASK_OFF_EFFECT == null || PIP_MASK_OFF_EFFECT == 180) {
                    maskBitmapOutline = Util.rotate(maskBitmapOutline, degreeOffset + Tag.IMAGE_DESCRIPTION);
                } else {
                    maskBitmapOutline = Util.rotate(maskBitmapOutline, degreeOffset + 90);
                }
            }
            this.mMaskBitmapOutlineFinal = maskBitmapOutline;
        }
        if (pipFrameSelectedMenu == VIDEO_SIZE_UVGA) {
            effectType = VIDEO_SIZE_FHD;
        } else if (pipFrameSelectedMenu == PIP_MASK_SPLITVIEW1_EFFECT || pipFrameSelectedMenu == PIP_MASK_SPLITVIEW2_EFFECT) {
            effectType = VIDEO_SIZE_HD;
        } else {
            effectType = PIP_MASK_OFF_EFFECT;
        }
        if (this.mGet.isSmartZoomRecordingActive()) {
            SmartZoomFilter.setPIPMask(this.mMaskBitmapShapeFinal, this.mMaskBitmapOutlineFinal, effectType);
        } else {
            DualRecorderFilter.setPIPMask(this.mMaskBitmapShapeFinal, this.mMaskBitmapOutlineFinal, effectType);
        }
        adjustPIPPositionToBeValid(this.mGet.getOrientationDegree());
        return true;
    }

    public void setDefaultPIPMask() {
        mCurrentMaskMenu = VIDEO_SIZE_FHD;
    }

    public int getInnerSpaceOfResizeHandler(int x0, int y0, int x1, int y1) {
        if (this.mGet.isConfigureLandscape()) {
            return (int) (((float) ((x1 - x0) - (y1 - y0))) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
        }
        return (int) (((float) ((y1 - y0) - (x1 - x0))) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
    }

    public int getInnerSpaceOfResizeHandler(RectInfo rect) {
        return getInnerSpaceOfResizeHandler(rect.mRectX0, rect.mRectY0, rect.mRectX1, rect.mRectY1);
    }

    public int getInnerSpaceHeightOfResizeHandlerForWindowEffect(int x0, int y0, int x1, int y1) {
        int retSpace;
        if (this.mGet.isConfigureLandscape()) {
            retSpace = ((y1 - y0) * 74) / Ola_ImageFormat.YUVPACKED_LABEL;
        } else {
            retSpace = ((x1 - x0) * 74) / Ola_ImageFormat.YUVPACKED_LABEL;
        }
        return mCurrentMaskMenu == VIDEO_SIZE_FHD ? retSpace : PIP_MASK_OFF_EFFECT;
    }

    public int getInnerSpaceWidthOfResizeHandlerForWindowEffect(int x0, int y0, int x1, int y1) {
        int retSpace;
        if (this.mGet.isConfigureLandscape()) {
            retSpace = ((y1 - y0) * 18) / Ola_ImageFormat.YUVPACKED_LABEL;
        } else {
            retSpace = ((x1 - x0) * 18) / Ola_ImageFormat.YUVPACKED_LABEL;
        }
        return mCurrentMaskMenu == VIDEO_SIZE_FHD ? retSpace : PIP_MASK_OFF_EFFECT;
    }

    public void resizeHandlerForFrameEffect(int[] position, int degree) {
        setInnerSpaceForFrameEffect(position);
        if (this.mGet.isConfigureLandscape()) {
            position[PIP_MASK_OFF_EFFECT] = position[PIP_MASK_OFF_EFFECT] + this.mMarginForFrameEffect;
            position[VIDEO_SIZE_HD] = position[VIDEO_SIZE_HD] - this.mMarginForFrameEffect;
            if (degree == 0 || degree == MediaProviderUtils.ROTATION_180) {
                position[PIP_MASK_OFF_EFFECT] = position[PIP_MASK_OFF_EFFECT] + this.mMarginWidthForFrameEffectWindow;
                position[VIDEO_SIZE_HD] = position[VIDEO_SIZE_HD] - this.mMarginWidthForFrameEffectWindow;
                position[VIDEO_SIZE_FHD] = position[VIDEO_SIZE_FHD] + this.mMarginHeightForFrameEffectWindow;
                position[VIDEO_SIZE_TV] = position[VIDEO_SIZE_TV] - this.mMarginHeightForFrameEffectWindow;
                return;
            }
            position[VIDEO_SIZE_FHD] = position[VIDEO_SIZE_FHD] + this.mMarginWidthForFrameEffectWindow;
            position[VIDEO_SIZE_TV] = position[VIDEO_SIZE_TV] - this.mMarginWidthForFrameEffectWindow;
            position[PIP_MASK_OFF_EFFECT] = position[PIP_MASK_OFF_EFFECT] + this.mMarginHeightForFrameEffectWindow;
            position[VIDEO_SIZE_HD] = position[VIDEO_SIZE_HD] - this.mMarginHeightForFrameEffectWindow;
            return;
        }
        position[VIDEO_SIZE_FHD] = position[VIDEO_SIZE_FHD] + this.mMarginForFrameEffect;
        position[VIDEO_SIZE_TV] = position[VIDEO_SIZE_TV] - this.mMarginForFrameEffect;
        if (degree == 0 || degree == MediaProviderUtils.ROTATION_180) {
            position[VIDEO_SIZE_FHD] = position[VIDEO_SIZE_FHD] + this.mMarginHeightForFrameEffectWindow;
            position[VIDEO_SIZE_TV] = position[VIDEO_SIZE_TV] - this.mMarginHeightForFrameEffectWindow;
            position[PIP_MASK_OFF_EFFECT] = position[PIP_MASK_OFF_EFFECT] + this.mMarginWidthForFrameEffectWindow;
            position[VIDEO_SIZE_HD] = position[VIDEO_SIZE_HD] - this.mMarginWidthForFrameEffectWindow;
            return;
        }
        position[PIP_MASK_OFF_EFFECT] = position[PIP_MASK_OFF_EFFECT] + this.mMarginHeightForFrameEffectWindow;
        position[VIDEO_SIZE_HD] = position[VIDEO_SIZE_HD] - this.mMarginHeightForFrameEffectWindow;
        position[VIDEO_SIZE_FHD] = position[VIDEO_SIZE_FHD] + this.mMarginWidthForFrameEffectWindow;
        position[VIDEO_SIZE_TV] = position[VIDEO_SIZE_TV] - this.mMarginWidthForFrameEffectWindow;
    }

    public void resizeHandlerForFrameEffect(int[] position) {
        resizeHandlerForFrameEffect(position, this.mGet.getOrientationDegree());
    }

    public void setInnerSpaceForFrameEffect(int[] position) {
        this.mMarginForFrameEffect = getInnerSpaceOfResizeHandler(position[PIP_MASK_OFF_EFFECT], position[VIDEO_SIZE_FHD], position[VIDEO_SIZE_HD], position[VIDEO_SIZE_TV]);
        this.mMarginHeightForFrameEffectWindow = getInnerSpaceHeightOfResizeHandlerForWindowEffect(position[PIP_MASK_OFF_EFFECT], position[VIDEO_SIZE_FHD], position[VIDEO_SIZE_HD], position[VIDEO_SIZE_TV]);
        this.mMarginWidthForFrameEffectWindow = getInnerSpaceWidthOfResizeHandlerForWindowEffect(position[PIP_MASK_OFF_EFFECT], position[VIDEO_SIZE_FHD], position[VIDEO_SIZE_HD], position[VIDEO_SIZE_TV]);
    }

    public void setPIPRotate(int degree) {
        if (this.mGet.isSmartZoomRecordingActive()) {
            SmartZoomFilter.setPIPRotate(degree);
        } else {
            DualRecorderFilter.setPIPRotate(degree);
        }
        adjustPIPPositionToBeValid(degree);
    }

    private void adjustPIPPositionToBeValid(int degree) {
        if (mCurrentMaskMenu != 0) {
            int diff;
            int[] position = new int[VIDEO_SIZE_VGA];
            position[PIP_MASK_OFF_EFFECT] = this.mSubWindowPosition.mRectX0;
            position[VIDEO_SIZE_FHD] = this.mSubWindowPosition.mRectY0;
            position[VIDEO_SIZE_HD] = this.mSubWindowPosition.mRectX1;
            position[VIDEO_SIZE_TV] = this.mSubWindowPosition.mRectY1;
            setInnerSpaceForFrameEffect(position);
            int marginWidth = this.mMarginWidthForFrameEffectWindow;
            int marginHeight = this.mMarginHeightForFrameEffectWindow;
            if (degree == 90 || degree == Tag.IMAGE_DESCRIPTION) {
                marginWidth = this.mMarginHeightForFrameEffectWindow;
                marginHeight = this.mMarginWidthForFrameEffectWindow;
            }
            int marginWidthForFrameEffect = PIP_MASK_OFF_EFFECT;
            int margintHeightForFrameEffect = PIP_MASK_OFF_EFFECT;
            if (this.mGet.isConfigureLandscape()) {
                marginWidthForFrameEffect = this.mMarginForFrameEffect;
            } else {
                margintHeightForFrameEffect = this.mMarginForFrameEffect;
            }
            if ((this.mSubWindowPosition.mRectX1 - marginWidth) - marginWidthForFrameEffect > this.mMaxX) {
                diff = ((this.mSubWindowPosition.mRectX1 - marginWidth) - marginWidthForFrameEffect) - this.mMaxX;
                RectInfo.access$220(this.mSubWindowPosition, diff);
                RectInfo.access$120(this.mSubWindowPosition, diff);
            } else if ((this.mSubWindowPosition.mRectX0 + marginWidth) + marginWidthForFrameEffect < this.mMinX) {
                diff = this.mMinX - ((this.mSubWindowPosition.mRectX0 + marginWidth) + marginWidthForFrameEffect);
                RectInfo.access$212(this.mSubWindowPosition, diff);
                RectInfo.access$112(this.mSubWindowPosition, diff);
            }
            if ((this.mSubWindowPosition.mRectY1 - marginHeight) - margintHeightForFrameEffect > this.mMaxY) {
                diff = ((this.mSubWindowPosition.mRectY1 - marginHeight) - margintHeightForFrameEffect) - this.mMaxY;
                RectInfo.access$420(this.mSubWindowPosition, diff);
                RectInfo.access$320(this.mSubWindowPosition, diff);
            } else if ((this.mSubWindowPosition.mRectY0 + marginHeight) + margintHeightForFrameEffect < this.mMinY) {
                diff = this.mMinY - ((this.mSubWindowPosition.mRectY0 + marginHeight) + margintHeightForFrameEffect);
                RectInfo.access$412(this.mSubWindowPosition, diff);
                RectInfo.access$312(this.mSubWindowPosition, diff);
            }
            this.mSubWindowPosition.setPrevPositionUpdate();
            drawSubWindow();
            setSubWindowResizeHandlerPosition();
        }
    }

    public void setBitmapDone(boolean value) {
        this.mSetBitmapDone = value;
    }

    public int getSmartZoomFocusViewMode() {
        return this.mSmartZoomFocusView.getSmartZoomFocusViewMode();
    }

    private int getDegree() {
        int recorderOrientation = VideoRecorder.getOrientationHint();
        if (this.mGet.isCamcorderRotation(true)) {
            return this.mGet.getOrientationDegree();
        }
        return this.mGet.isConfigureLandscape() ? recorderOrientation : (recorderOrientation + Tag.IMAGE_DESCRIPTION) % CameraConstants.DEGREE_360;
    }

    public void setMode(int mode) {
        CamLog.d(FaceDetector.TAG, "SubWindow Mode is set to " + (mode == 0 ? "NORMAL mode" : "RESIZE mode"));
        this.mMode = mode;
    }

    public int getMode() {
        return this.mMode;
    }

    public int getLeftTopX() {
        return this.mSubWindowPosition.mRectX0;
    }

    public int getLeftTopY() {
        return this.mSubWindowPosition.mRectY0;
    }

    public int getRightBottomX() {
        return this.mSubWindowPosition.mRectX1;
    }

    public int getRightBottomY() {
        return this.mSubWindowPosition.mRectY1;
    }

    public int getWidth() {
        return this.mSubWindowPosition.mWidth;
    }

    public int getHeight() {
        return this.mSubWindowPosition.mHeight;
    }

    public void unbind() {
        this.vg = null;
        this.mSubWindowPosition = null;
        this.mSmartZoomFocusView = null;
        if (this.mPIPResizeHandlerView != null) {
            this.mPIPResizeHandlerView.unbind();
            this.mPIPResizeHandlerView = null;
        }
    }

    public boolean isObjectTrackingEnabledForSmartZoom() {
        return false;
    }

    public void enableObjectTrackingForSmartZoom() {
    }

    public void disableObjectTrackingForSmartZoom() {
    }
}
