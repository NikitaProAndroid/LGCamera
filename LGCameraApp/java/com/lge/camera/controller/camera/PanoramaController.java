package com.lge.camera.controller.camera;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.CameraPreview;
import com.lge.camera.components.PanoThumbView;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.ShutterButton;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.ExifUtil;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.camera.util.Util;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.datastruct.JOlaBitmap;
import com.lge.olaworks.datastruct.Ola_AutoPanoramaInfo;
import com.lge.olaworks.datastruct.Ola_AutoPanoramaThumbInfo;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.jni.OlaBitmapGraphicsJNI;
import com.lge.olaworks.library.AutoPanorama;
import com.lge.olaworks.library.AutoPanorama.Callback;
import com.lge.olaworks.library.EngineProcessor;
import com.lge.olaworks.library.FaceDetector;

public class PanoramaController extends CameraController {
    private AutoPanorama mAutoPanoramaEngine;
    private boolean mDirectInit;
    private int mDirection;
    private CameraPreview mGuideView;
    private boolean mInitMainButton;
    private boolean mIsReachProgressMax;
    private byte[] mJpegData;
    private int mModeDirection;
    private int mModeSetFrameImage;
    private int mModeThumbnail;
    private int mPanoOrientationDegree;
    private boolean mPanoramaStarted;
    private View mPanoramaView;
    private boolean mSynthesizeInProgress;
    private int mThumbnailHeight;
    private int mThumbnailWidth;

    private class AutoPanoramaCallback implements Callback {
        private boolean mSetStartSyncForceDelay;

        private AutoPanoramaCallback() {
            this.mSetStartSyncForceDelay = false;
        }

        public void onProcessFrame(Ola_AutoPanoramaInfo info) {
            if (PanoramaController.this.mDirectInit) {
                if (!PanoramaController.this.mInitMainButton) {
                    PanoramaController.this.mGet.setMainButtonEnable();
                }
                PanoramaController.this.mInitMainButton = true;
            } else {
                PanoramaController.this.mDirection = info.direction;
                PanoramaController.this.mPanoOrientationDegree = PanoramaController.this.mGet.getDeviceDegree();
                PanoramaController.this.mAutoPanoramaEngine.setOrientation(PanoramaController.this.mGet.getOrientationDegree());
                if (PanoramaController.this.initAutoPanoramaPlayUI(PanoramaController.this.mDirection, false)) {
                    PanoramaController.this.mDirectInit = true;
                }
                PanoramaController.this.mInitMainButton = false;
            }
            ((PanoThumbView) PanoramaController.this.mGet.findViewById(R.id.pano_thumb_view)).setMovingRect(info.hRealDisp, info.vRealDisp, PanoramaController.this.mDirection);
            if (info.status == 3) {
                PanoramaController.this.mIsReachProgressMax = true;
                PanoramaController.this.mGet.playRecordingSound(false);
            }
        }

        public void onComplete(byte[] jpegData) {
            CamLog.d(FaceDetector.TAG, "onComplete jpegData");
            PanoramaController.this.mJpegData = jpegData;
            PanoramaController.this.mGet.setInCaptureProgress(false);
            PanoramaController.this.mGet.setQuickFunctionMenuForcedDisable(false);
            PanoramaController.this.mGet.setQuickButtonForcedDisable(false);
            if (jpegData != null) {
                PanoramaController.this.onSynthesizeComplete();
            } else {
                CamLog.d(FaceDetector.TAG, "panorama shot jpegData is null -> MSG_GOTO_PREVIEW");
                PanoramaController.this.mGet.doCommandUi(Command.DISPLAY_PREVIEW);
            }
            PanoramaController.this.mSynthesizeInProgress = false;
            PanoramaController.this.mPanoramaStarted = false;
            if (PanoramaController.this.mGet.checkAutoReviewOff(false)) {
                PanoramaController.this.mGet.postOnUiThread(new Runnable() {
                    public void run() {
                        PanoramaController.this.mGet.removePostRunnable(this);
                        PanoramaController.this.mGet.setSwitcherVisible(true);
                        PanoramaController.this.mGet.setShutterButtonImage(true, PanoramaController.this.mGet.getOrientationDegree());
                        PanoramaController.this.mGet.updateThumbnailButtonVisibility();
                    }
                });
            }
        }

        public void onSynthesisProgressUpdate(int progress) {
            CamLog.d(FaceDetector.TAG, "onSynthesisProgressUpdate : progress = " + progress);
            if (progress == 0) {
                PanoramaController.this.stopPanorama();
                PanoramaController.this.mGet.showProgressDialog();
                return;
            }
            ((PanoThumbView) PanoramaController.this.mGet.findViewById(R.id.pano_thumb_view)).setThumbnail(null, false);
            PanoramaController.this.mGet.deleteProgressDialog();
        }

        public void onPanningSpeedWarning(final boolean warningFlag) {
            if (PanoramaController.this.checkMediator()) {
                PanoramaController.this.mGet.postOnUiThread(new Runnable() {
                    public void run() {
                        PanoramaController.this.mGet.removePostRunnable(this);
                        int visible = warningFlag ? 0 : 4;
                        RotateLayout warning = (RotateLayout) PanoramaController.this.mGet.findViewById(R.id.warning_fast_layout);
                        if (warning != null) {
                            TextView tv = (TextView) PanoramaController.this.mGet.findViewById(R.id.warning_fast_text);
                            if (tv != null) {
                                tv.setText(R.string.sp_pano_too_fast_prompt_NORMAL);
                                int degree = PanoramaController.this.mPanoOrientationDegree;
                                if (!PanoramaController.this.mGet.isConfigureLandscape()) {
                                    degree = (degree + Tag.IMAGE_DESCRIPTION) % CameraConstants.DEGREE_360;
                                }
                                warning.setAngle(degree);
                            }
                            if (PanoramaController.this.mDirectInit) {
                                PanoramaController.this.initAutoPanoramaPlayUI(PanoramaController.this.mDirection, warningFlag);
                            }
                            warning.setVisibility(visible);
                        }
                    }
                });
            }
        }

        public void onTakePicture(int frameCount) {
            LGParameters lgParams = PanoramaController.this.mGet.getLGParam();
            if (frameCount == 0) {
                PanoramaController.this.mGet.setParameteredRotation(PanoramaController.this.mGet.getDeviceDegree());
                PanoramaController.this.mPanoOrientationDegree = PanoramaController.this.mGet.getParameteredRotation();
                PanoramaController.this.mGet.setImageRotationDegree(0);
            }
            lgParams.setParameters(lgParams.getParameters());
            PanoramaController.this.mGet.getCameraDevice().takePicture(null, null, null, new PanoJpegPictureCallback());
        }

        public void onGetThumbnailImage(Bitmap thumbnail, boolean needGuideBox) {
            if (thumbnail != null) {
                ((PanoThumbView) PanoramaController.this.mGet.findViewById(R.id.pano_thumb_view)).setThumbnail(thumbnail, needGuideBox);
            }
            if (this.mSetStartSyncForceDelay) {
                PanoramaController.this.mGet.removeScheduledCommand(Command.RUN_PANORAMA_START_SYC_TASK);
                PanoramaController.this.mGet.doCommandDelayed(Command.RUN_PANORAMA_START_SYC_TASK, PanoramaController.this.mAutoPanoramaEngine, 0);
                this.mSetStartSyncForceDelay = false;
            }
        }

        public void onSetStartSyncForceDelay() {
            this.mSetStartSyncForceDelay = true;
        }

        public void onAlarmStartSync() {
            CamLog.d(FaceDetector.TAG, "height / width is too short, so synthesis begins with a forced !!!");
            PanoramaController.this.mGet.toast((int) R.string.error_panorama_during_taking);
        }
    }

    private class GuideParameters {
        public int mDegree;
        public int mIndicatorHeight;
        public int mLcdHeight;
        public int mLcdWidth;
        public LayoutParams mLpGuideLayout;
        public LayoutParams mLpTextLayout;
        public int mPanoramaThumbHeight;
        public int mPanoramaThumbMarginBottom;
        public int mPanoramaThumbMarginLeft;
        public int mPanoramaThumbWidth;
        public int mPreviewPanelMarginBottom;
        public int mPreviewPanelWidth;
        public RelativeLayout mTextLayout;
        public int mTextMarginBottom;
        public int mTextMarginLeft;

        public GuideParameters(int textMarginLeft, int textMarginBottom, int panoramaThumbWidth, int panoramaThumbHeight, int panoramaThumbMarginLeft, int panoramaThumbMarginBottom, int indicatorHeight, int previewPanelWidth, int previewPanelMarginBottom, int lcdWidth, int lcdHeight, int degree, RelativeLayout textLayout, LayoutParams lpGuideLayout, LayoutParams lpTextLayout) {
            this.mTextMarginLeft = textMarginLeft;
            this.mTextMarginBottom = textMarginBottom;
            this.mPanoramaThumbWidth = panoramaThumbWidth;
            this.mPanoramaThumbHeight = panoramaThumbHeight;
            this.mPanoramaThumbMarginLeft = panoramaThumbMarginLeft;
            this.mPanoramaThumbMarginBottom = panoramaThumbMarginBottom;
            this.mIndicatorHeight = indicatorHeight;
            this.mPreviewPanelWidth = previewPanelWidth;
            this.mPreviewPanelMarginBottom = previewPanelMarginBottom;
            this.mLcdWidth = lcdWidth;
            this.mLcdHeight = lcdHeight;
            this.mDegree = degree;
            this.mTextLayout = textLayout;
            this.mLpGuideLayout = lpGuideLayout;
            this.mLpTextLayout = lpTextLayout;
        }
    }

    private class PanoJpegPictureCallback implements PictureCallback {
        private PanoJpegPictureCallback() {
        }

        public void onPictureTaken(byte[] data, Camera camera) {
            PanoramaController.this.mGet.getCameraDevice().startPreview();
            if (data != null) {
                setFrameProcess(data);
            }
        }

        private void setFrameProcess(byte[] data) {
            JOlaBitmap jolabitmap;
            Options options = new Options();
            options.inSampleSize = 1;
            options.inPreferredConfig = Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            int size = (int) (((double) (bitmap.getWidth() * bitmap.getHeight())) * 1.5d);
            byte[] yuvBuf = new byte[size];
            OlaBitmapGraphicsJNI.rgbBitmapToYuv(bitmap, yuvBuf);
            if (ExifUtil.getOrientation(data) != 0) {
                jolabitmap = new JOlaBitmap(bitmap.getWidth(), bitmap.getHeight(), Ola_ImageFormat.YUVPLANAR_NV21, getOlaRotation(PanoramaController.this.mPanoOrientationDegree), null);
                jolabitmap.imageData = yuvBuf;
            } else if (PanoramaController.this.mPanoOrientationDegree == 90 || PanoramaController.this.mPanoOrientationDegree == Tag.IMAGE_DESCRIPTION) {
                jolabitmap = new JOlaBitmap(bitmap.getHeight(), bitmap.getWidth(), Ola_ImageFormat.YUVPLANAR_NV21, getOlaRotation(PanoramaController.this.mPanoOrientationDegree), null);
                rotateBuf = new byte[size];
                jolabitmap.imageData = OlaBitmapGraphicsJNI.rotateYuv(yuvBuf, bitmap.getWidth(), bitmap.getHeight(), getOlaRotation(PanoramaController.this.mPanoOrientationDegree));
            } else {
                jolabitmap = new JOlaBitmap(bitmap.getWidth(), bitmap.getHeight(), Ola_ImageFormat.YUVPLANAR_NV21, getOlaRotation(PanoramaController.this.mPanoOrientationDegree), null);
                if (PanoramaController.this.mPanoOrientationDegree == MediaProviderUtils.ROTATION_180) {
                    rotateBuf = new byte[size];
                    jolabitmap.imageData = OlaBitmapGraphicsJNI.rotateYuv(yuvBuf, bitmap.getWidth(), bitmap.getHeight(), getOlaRotation(PanoramaController.this.mPanoOrientationDegree));
                } else {
                    jolabitmap.imageData = yuvBuf;
                }
            }
            PanoramaController.this.mAutoPanoramaEngine.setFrameProcess(jolabitmap);
        }

        private int getOlaRotation(int degree) {
            if (degree == 90) {
                return 3;
            }
            if (degree == MediaProviderUtils.ROTATION_180) {
                return 2;
            }
            if (degree == Tag.IMAGE_DESCRIPTION) {
                return 1;
            }
            return 0;
        }
    }

    public PanoramaController(ControllerFunction function) {
        super(function);
        this.mAutoPanoramaEngine = null;
        this.mJpegData = null;
        this.mGuideView = null;
        this.mPanoramaStarted = false;
        this.mSynthesizeInProgress = false;
        this.mIsReachProgressMax = false;
        this.mDirection = 0;
        this.mPanoOrientationDegree = 0;
        this.mDirectInit = false;
        this.mInitMainButton = false;
        this.mModeSetFrameImage = 0;
        this.mModeDirection = 0;
        this.mModeThumbnail = 1;
    }

    public void reInitialize() {
        this.mInit = false;
        this.mPanoramaView = null;
    }

    public void inflatePanoramaView() {
        if (this.mPanoramaView == null) {
            try {
                this.mPanoramaView = this.mGet.inflateStub(R.id.stub_panorama);
                this.mGuideView = (CameraPreview) this.mGet.findViewById(R.id.preview_holder_surface);
                this.mGuideView.initializePanorama(this.mGet.getOrientation());
                removePanoramaView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setEngine() {
        if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
            this.mAutoPanoramaEngine = new AutoPanorama(this.mModeSetFrameImage, this.mModeDirection, this.mModeThumbnail, new AutoPanoramaCallback());
            if (this.mAutoPanoramaEngine.getModeThumbnail() == 1) {
                int margin = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.panorama_thumb_view_marginLeft);
                this.mThumbnailWidth = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.panorama_view_layout_width) - (margin * 2);
                this.mThumbnailHeight = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.panorama_view_layout_height) - (margin * 2);
                this.mAutoPanoramaEngine.setThumbnailExpectedSize(this.mThumbnailWidth, this.mThumbnailHeight);
            }
            this.mAutoPanoramaEngine.setConfigureLandscape(this.mGet.isConfigureLandscape());
            this.mGet.getEngineProcessor().setEngine(this.mAutoPanoramaEngine, false);
        }
    }

    public boolean isPanoramaUIShown() {
        if (this.mPanoramaView != null && this.mPanoramaView.getVisibility() == 0) {
            return true;
        }
        return false;
    }

    public boolean isSynthesisInProgress() {
        return this.mSynthesizeInProgress;
    }

    public void showPanoramaView() {
        CamLog.d(FaceDetector.TAG, "showPanoramaView");
        if (!this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
            return;
        }
        if (this.mGet.getSubMenuMode() == 0 || this.mGet.getSubMenuMode() == 21) {
            if (this.mPanoramaView == null) {
                inflatePanoramaView();
            }
            this.mGet.doCommand(Command.ROTATE);
            this.mGet.setSwitcherVisible(true);
            if (ModelProperties.is3dSupportedModel()) {
                this.mGet.set3DSwitchVisible(true);
            }
            this.mGet.setMainButtonVisible(true);
            ShutterButton mShutterButton = (ShutterButton) this.mGet.findViewById(R.id.main_button_bg);
            if (mShutterButton != null) {
                this.mGet.setShutterButtonImage(mShutterButton.isEnabled(), this.mGet.getOrientationDegree());
            }
            if (this.mPanoramaView != null) {
                this.mPanoramaView.setVisibility(0);
                this.mGuideView.resetAutoPanorama();
                this.mGet.findViewById(R.id.warning_fast_layout).setVisibility(8);
                this.mGet.findViewById(R.id.panorama_arrow_left).setVisibility(0);
                this.mGet.findViewById(R.id.panorama_arrow_right).setVisibility(0);
                this.mGet.findViewById(R.id.panorama_arrow_up).setVisibility(0);
                this.mGet.findViewById(R.id.panorama_arrow_down).setVisibility(0);
                showGuideCenterText(true, R.string.panorama_guide_move_in_one_direction);
                this.mGet.findViewById(R.id.guide_around_thumb_layout).setVisibility(4);
                this.mGuideView.setDrawMode(3);
            }
        }
    }

    public void removePanoramaView() {
        CamLog.d(FaceDetector.TAG, "removePanoramaView");
        if (this.mPanoramaView != null) {
            this.mPanoramaView.setVisibility(8);
            if (this.mGuideView.getDrawMode() == 3) {
                this.mGuideView.setDrawMode(0);
            }
        }
    }

    public void startPanorama() {
        CamLog.d(FaceDetector.TAG, "startPanorama()");
        this.mPanoramaStarted = true;
        this.mSynthesizeInProgress = false;
        this.mGet.hideQuickFunctionController();
        this.mGet.setSwitcherVisible(false);
        if (ModelProperties.is3dSupportedModel()) {
            this.mGet.set3DSwitchVisible(false);
        }
        this.mGet.setThumbnailButtonVisibility(8);
        EngineProcessor engine = this.mGet.getEngineProcessor();
        if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
            CamLog.d(FaceDetector.TAG, "### awb/ae lock");
            tryEnable3ALocks(null, true);
        }
        if (engine != null) {
            CamLog.d(FaceDetector.TAG, "getEngineProcessor().checkEngineTag(AutoPanorama.TAG) [" + engine.checkEngineTag(AutoPanorama.ENGINE_TAG) + "]");
            this.mGet.playRecordingSound(true);
            if (engine.checkEngineTag(AutoPanorama.ENGINE_TAG)) {
                engine.start();
                CamLog.d(FaceDetector.TAG, "startPanorama() start AutoPanorama Engine...............");
            }
        }
        View view = this.mGet.findViewById(R.id.warning_fast_layout);
        if (view != null) {
            view.setVisibility(8);
        }
        view = this.mGet.findViewById(R.id.guide_around_thumb_layout);
        if (view != null) {
            view.setVisibility(4);
        }
        this.mGet.hideFocus();
        this.mGet.hideOptionMenu();
        this.mGet.getHandler().sendEmptyMessageDelayed(1, 500);
        this.mGet.getHandler().sendEmptyMessageDelayed(3, 500);
    }

    public void stopPanorama() {
        CamLog.d(FaceDetector.TAG, String.format("stopPanorama() isPanoramaStarted() = " + isPanoramaStarted(), new Object[0]));
        if (isPanoramaStarted()) {
            this.mSynthesizeInProgress = true;
            this.mPanoramaStarted = false;
            this.mGet.setMainButtonDisable();
            this.mGet.clearFocusState();
            this.mGet.hideFocus();
            this.mGet.findViewById(R.id.warning_fast_layout).setVisibility(8);
            this.mGet.findViewById(R.id.guide_center_layout).setVisibility(8);
            this.mGet.findViewById(R.id.guide_around_thumb_layout).setVisibility(8);
            this.mDirectInit = false;
            EngineProcessor engine = this.mGet.getEngineProcessor();
            if (engine != null && engine.checkEngineTag(AutoPanorama.ENGINE_TAG)) {
                engine.stop();
                CamLog.d(FaceDetector.TAG, "stopPanorama : engineProcessor Stop");
            }
            if (this.mPanoramaView != null) {
                try {
                    this.mPanoramaView.setVisibility(8);
                    this.mGuideView.resetAutoPanorama();
                    this.mGuideView.setDrawMode(0);
                    this.mGuideView.stopPanoramaDrawing();
                } catch (Exception ex) {
                    CamLog.w(FaceDetector.TAG, "exception occur during remove the panorama view : " + ex.toString());
                }
            }
            if (this.mIsReachProgressMax) {
                this.mIsReachProgressMax = false;
            } else {
                this.mAutoPanoramaEngine.stopProcess();
            }
            this.mGet.setInCaptureProgress(false);
            this.mGet.setQuickFunctionMenuForcedDisable(false);
            this.mGet.setQuickButtonForcedDisable(false);
            if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) && this.mGet.getCameraDevice() != null && this.mGet.getLGParam() != null) {
                CamLog.d(FaceDetector.TAG, "### awb/ae unlock");
                LGParameters lgParameters = this.mGet.getLGParam();
                tryEnable3ALocks(lgParameters, false);
                if (lgParameters != null && lgParameters.getParameters() != null && FunctionProperties.isCafSupported(this.mGet.getApplicationMode(), this.mGet.getCameraId())) {
                    lgParameters.getParameters().setFocusMode("continuous-picture");
                    lgParameters.setParameters(lgParameters.getParameters());
                    CamLog.d(FaceDetector.TAG, "### setFocusMode-conti");
                    if (ModelProperties.isRenesasISP()) {
                        this.mGet.getCameraDevice().autoFocus(null);
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        CamLog.d(FaceDetector.TAG, String.format("stopPanorama() do nothing; not started", new Object[0]));
    }

    private boolean tryEnable3ALocks(LGParameters lgParameters, boolean toggle) {
        if (this.mGet.getCameraDevice() == null || !this.mGet.checkPreviewController()) {
            return false;
        }
        return this.mGet.setEnable3ALocks(lgParameters, toggle);
    }

    public boolean initAutoPanoramaPlayUI(int direction, boolean warning) {
        if (direction == 0) {
            return false;
        }
        this.mGet.findViewById(R.id.panorama_arrow_left).setVisibility(4);
        this.mGet.findViewById(R.id.panorama_arrow_right).setVisibility(4);
        this.mGet.findViewById(R.id.panorama_arrow_up).setVisibility(4);
        this.mGet.findViewById(R.id.panorama_arrow_down).setVisibility(4);
        this.mGet.findViewById(R.id.guide_center_layout).setVisibility(4);
        Ola_AutoPanoramaThumbInfo info = this.mAutoPanoramaEngine.getThumbnailInfo();
        int[] previewSizeOnDevice = Util.SizeString2WidthHeight(this.mGet.getPreviewSizeOnDevice());
        ((PanoThumbView) this.mGet.findViewById(R.id.pano_thumb_view)).init(info.tWidth, info.tHeight, info.width, info.height, info.dW, info.dH, previewSizeOnDevice[0], previewSizeOnDevice[1]);
        ((PanoThumbView) this.mGet.findViewById(R.id.pano_thumb_view)).setDirection(direction, this.mGet.getOrientation());
        RotateLayout rl = (RotateLayout) this.mGet.findViewById(R.id.pano_thumb_rotate);
        if (rl != null) {
            rl.rotateLayout(this.mGet.isConfigureLandscape() ? 0 : Tag.IMAGE_DESCRIPTION);
        }
        showGuideAroundThumbText(true);
        return true;
    }

    private void showGuideCenterText(final boolean isShowing, final int resId) {
        if (checkMediator()) {
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    int i = 0;
                    PanoramaController.this.mGet.removePostRunnable(this);
                    RotateLayout guide = (RotateLayout) PanoramaController.this.mGet.findViewById(R.id.guide_center_layout);
                    if (guide != null) {
                        if (isShowing) {
                            TextView tv = (TextView) PanoramaController.this.mGet.findViewById(R.id.guide_center_text);
                            if (tv != null) {
                                int i2;
                                tv.setText(resId);
                                int[] previewSizeOnScreen = new int[]{0, 0};
                                String sizeOnScreenString = PanoramaController.this.mGet.getPreviewSizeOnScreen();
                                if (sizeOnScreenString != null) {
                                    previewSizeOnScreen = Util.SizeString2WidthHeight(sizeOnScreenString);
                                }
                                int degree = PanoramaController.this.mPanoOrientationDegree;
                                if (!PanoramaController.this.mGet.isConfigureLandscape()) {
                                    degree = (degree + Tag.IMAGE_DESCRIPTION) % CameraConstants.DEGREE_360;
                                }
                                RelativeLayout textLayout = (RelativeLayout) PanoramaController.this.mGet.findViewById(R.id.guide_center_text_layout);
                                LayoutParams lpTextLayout = (LayoutParams) textLayout.getLayoutParams();
                                Common.resetLayoutParameter(lpTextLayout);
                                if (degree == 0 || degree == MediaProviderUtils.ROTATION_180) {
                                    i2 = 1;
                                } else {
                                    i2 = 0;
                                }
                                lpTextLayout.width = previewSizeOnScreen[i2] - ((PanoramaController.this.mGet.findViewById(R.id.panorama_arrow_left).getMeasuredWidth() + Common.getPixelFromDimens(PanoramaController.this.mGet.getApplicationContext(), R.dimen.panorama_arrow_outside_margin)) * 2);
                                lpTextLayout.height = -2;
                                lpTextLayout.addRule(13, 1);
                                textLayout.setGravity(17);
                                textLayout.setLayoutDirection(0);
                                textLayout.setLayoutParams(lpTextLayout);
                                guide.setAngle(degree);
                            }
                        }
                        if (!isShowing) {
                            i = 4;
                        }
                        guide.setVisibility(i);
                    }
                }
            });
        }
    }

    private void showGuideAroundThumbText(final boolean isShowing) {
        if (checkMediator()) {
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    PanoramaController.this.mGet.removePostRunnable(this);
                    RelativeLayout guideLayout = (RelativeLayout) PanoramaController.this.mGet.findViewById(R.id.guide_around_thumb_layout);
                    if (guideLayout == null) {
                        return;
                    }
                    if (isShowing) {
                        int degree = PanoramaController.this.mPanoOrientationDegree;
                        if (!PanoramaController.this.mGet.isConfigureLandscape()) {
                            degree = (degree + Tag.IMAGE_DESCRIPTION) % CameraConstants.DEGREE_360;
                        }
                        RotateLayout guide = (RotateLayout) guideLayout.findViewById(R.id.guide_around_thumb_rotate_layout);
                        if (guide != null) {
                            guide.setAngle(degree);
                        }
                        ViewGroup.LayoutParams lpGuideLayout = (LayoutParams) guideLayout.getLayoutParams();
                        Common.resetLayoutParameter(lpGuideLayout);
                        guideLayout.setLayoutDirection(0);
                        lpGuideLayout.topMargin = 0;
                        lpGuideLayout.leftMargin = 0;
                        lpGuideLayout.rightMargin = 0;
                        lpGuideLayout.bottomMargin = 0;
                        RelativeLayout textLayout = (RelativeLayout) guideLayout.findViewById(R.id.guide_around_thumb_text_layout);
                        ViewGroup.LayoutParams lpTextLayout = (LayoutParams) textLayout.getLayoutParams();
                        Common.resetLayoutParameter(lpTextLayout);
                        textLayout.setLayoutDirection(0);
                        lpTextLayout.height = -2;
                        GuideParameters gp = new GuideParameters(Common.getPixelFromDimens(PanoramaController.this.mGet.getApplicationContext(), R.dimen.panorama_stop_guide_marginLeft), Common.getPixelFromDimens(PanoramaController.this.mGet.getApplicationContext(), R.dimen.panorama_stop_guide_marginBottom), PanoramaController.this.mAutoPanoramaEngine.getThumbnailInfo().width, PanoramaController.this.mAutoPanoramaEngine.getThumbnailInfo().height, Common.getPixelFromDimens(PanoramaController.this.mGet.getApplicationContext(), R.dimen.panorama_thumb_view_marginLeft), Common.getPixelFromDimens(PanoramaController.this.mGet.getApplicationContext(), R.dimen.panorama_thumb_view_marginBottom), Common.getPixelFromDimens(PanoramaController.this.mGet.getApplicationContext(), R.dimen.layout_preview_indicators_height), Common.getPixelFromDimens(PanoramaController.this.mGet.getApplicationContext(), R.dimen.preview_panel_width), Common.getPixelFromDimens(PanoramaController.this.mGet.getApplicationContext(), R.dimen.preview_panel_marginBottom), Common.getPixelFromDimens(PanoramaController.this.mGet.getApplicationContext(), R.dimen.lcd_width), Common.getPixelFromDimens(PanoramaController.this.mGet.getApplicationContext(), R.dimen.lcd_height), degree, textLayout, lpGuideLayout, lpTextLayout);
                        if (PanoramaController.this.mDirection == 1 || PanoramaController.this.mDirection == 2) {
                            PanoramaController.this.setGuideAroundThumbTextLayoutRightLeft(gp);
                        } else {
                            PanoramaController.this.setGuideAroundThumbTextLayoutUpDown(gp);
                        }
                        textLayout.setLayoutParams(lpTextLayout);
                        guideLayout.setLayoutParams(lpGuideLayout);
                        guideLayout.setVisibility(0);
                        return;
                    }
                    guideLayout.setVisibility(4);
                }
            });
        }
    }

    private void setGuideAroundThumbTextLayoutRightLeft(GuideParameters gp) {
        if (gp.mDegree == 0) {
            gp.mLpGuideLayout.addRule(21, 1);
            gp.mLpGuideLayout.addRule(12, 1);
            gp.mLpGuideLayout.rightMargin = gp.mIndicatorHeight;
            gp.mLpGuideLayout.bottomMargin = gp.mPanoramaThumbMarginLeft;
            gp.mLpTextLayout.width = (((gp.mLcdHeight - gp.mIndicatorHeight) - gp.mTextMarginBottom) - gp.mPanoramaThumbHeight) - gp.mPanoramaThumbMarginBottom;
            gp.mTextLayout.setGravity(17);
        } else if (gp.mDegree == MediaProviderUtils.ROTATION_180) {
            gp.mLpGuideLayout.addRule(21, 1);
            gp.mLpGuideLayout.addRule(10, 1);
            gp.mLpGuideLayout.topMargin = gp.mPanoramaThumbMarginLeft;
            gp.mLpGuideLayout.rightMargin = gp.mIndicatorHeight;
            gp.mLpTextLayout.width = (((gp.mLcdHeight - gp.mIndicatorHeight) - gp.mTextMarginBottom) - gp.mPanoramaThumbHeight) - gp.mPanoramaThumbMarginBottom;
            gp.mTextLayout.setGravity(17);
        } else if (gp.mDegree == 90 || gp.mDegree == Tag.IMAGE_DESCRIPTION) {
            gp.mLpGuideLayout.addRule(15, 1);
            gp.mLpGuideLayout.addRule(20, 1);
            gp.mLpGuideLayout.leftMargin = (gp.mTextMarginBottom + gp.mPanoramaThumbHeight) + gp.mPanoramaThumbMarginBottom;
            gp.mLpTextLayout.width = (((gp.mLcdWidth - gp.mTextMarginLeft) - gp.mTextMarginLeft) - gp.mPreviewPanelWidth) - gp.mPreviewPanelMarginBottom;
            gp.mTextLayout.setGravity(17);
        }
    }

    private void setGuideAroundThumbTextLayoutUpDown(GuideParameters gp) {
        if (gp.mDegree == 0 || gp.mDegree == MediaProviderUtils.ROTATION_180) {
            gp.mLpGuideLayout.addRule(14, 1);
            gp.mLpGuideLayout.addRule(12, 1);
            gp.mLpGuideLayout.bottomMargin = (gp.mTextMarginBottom + gp.mPanoramaThumbWidth) + gp.mPanoramaThumbMarginBottom;
            gp.mLpTextLayout.width = (gp.mLcdHeight - gp.mTextMarginLeft) - gp.mTextMarginLeft;
            gp.mTextLayout.setGravity(17);
        } else if (gp.mDegree == 90) {
            gp.mLpGuideLayout.addRule(20, 1);
            gp.mLpGuideLayout.addRule(12, 1);
            gp.mLpGuideLayout.leftMargin = gp.mPanoramaThumbMarginLeft;
            gp.mLpGuideLayout.bottomMargin = (gp.mTextMarginBottom + gp.mPanoramaThumbWidth) + gp.mPanoramaThumbMarginBottom;
            gp.mLpTextLayout.width = (((((gp.mLcdWidth - gp.mTextMarginLeft) - gp.mTextMarginBottom) - gp.mPanoramaThumbWidth) - gp.mPanoramaThumbMarginBottom) - gp.mPreviewPanelWidth) - gp.mPreviewPanelMarginBottom;
            gp.mTextLayout.setGravity(17);
        } else if (gp.mDegree == Tag.IMAGE_DESCRIPTION) {
            gp.mLpGuideLayout.addRule(20, 1);
            gp.mLpGuideLayout.addRule(12, 1);
            gp.mLpGuideLayout.leftMargin = gp.mPanoramaThumbMarginLeft;
            gp.mLpGuideLayout.bottomMargin = (gp.mTextMarginBottom + gp.mPanoramaThumbWidth) + gp.mPanoramaThumbMarginBottom;
            gp.mLpTextLayout.width = (((((gp.mLcdWidth - gp.mTextMarginLeft) - gp.mTextMarginBottom) - gp.mPanoramaThumbWidth) - gp.mPanoramaThumbMarginBottom) - gp.mPreviewPanelWidth) - gp.mPreviewPanelMarginBottom;
            gp.mTextLayout.setGravity(17);
        }
    }

    private void onSynthesizeComplete() {
        if (!this.mGet.isPausing()) {
            if (this.mJpegData == null) {
                this.mGet.startPreview(null, true);
                this.mGet.doCommandUi(Command.DISPLAY_PREVIEW);
            } else if (this.mGet.savePicture(this.mJpegData, null)) {
                ExifUtil.setExifMakeModel(this.mGet.getCurrentStorageDirectory() + this.mGet.getSavedFileName() + CameraConstants.TIME_MACHINE_TEMPFILE_EXT);
                this.mGet.setLastThumb(this.mGet.getSavedImageUri(), true);
                this.mGet.updateThumbnailButton();
                if (!this.mGet.isPausing()) {
                    if (this.mGet.checkAutoReviewOff(false)) {
                        this.mGet.doCommandUi(Command.ON_DELAY_OFF);
                    } else if (!this.mGet.checkAutoReviewForQuickView()) {
                        this.mGet.stopPreview();
                        this.mGet.doCommandUi(Command.DISPLAY_CAMERA_POSTVIEW);
                    }
                }
                this.mJpegData = null;
            } else {
                CamLog.d(FaceDetector.TAG, "panorama save fail");
                this.mGet.startPreview(null, true);
                this.mGet.doCommandUi(Command.DISPLAY_PREVIEW);
                return;
            }
            if (FunctionProperties.isVoiceShutter()) {
                String mVoiceShutterValue = this.mGet.getSettingValue(Setting.KEY_VOICESHUTTER);
                if (mVoiceShutterValue != null && mVoiceShutterValue.equals(CameraConstants.SMART_MODE_ON)) {
                    this.mGet.doCommandUi(Command.SET_VOICE_SHUTTER);
                }
            }
        }
    }

    public boolean isPanoramaStarted() {
        if (this.mPanoramaStarted) {
            return true;
        }
        return false;
    }

    public boolean isPanoramaUpdatebutton() {
        return this.mPanoramaStarted;
    }

    public void onResume() {
        CamLog.d(FaceDetector.TAG, "Panorama Controller onResume !");
        if (this.mInit && this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
            setEngine();
        }
    }

    public void onPause() {
        CamLog.d(FaceDetector.TAG, "Panorama Controller onPause - start");
        if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
            boolean stopByPausing = this.mSynthesizeInProgress;
            stopPanorama();
            CamLog.d(FaceDetector.TAG, String.format("Wait for synthesis done in pause()", new Object[0]));
            CamLog.d(FaceDetector.TAG, String.format("synth done in pause()", new Object[0]));
            if (stopByPausing && this.mGet.getSavedImageUri() != null) {
                SharedPreferenceUtil.saveLastPicture(this.mGet.getActivity(), this.mGet.getSavedImageUri());
            }
            EngineProcessor engine = this.mGet.getEngineProcessor();
            if (engine != null) {
                engine.stop();
                engine.releaseAllEngine();
            }
            this.mGet.setShutterButtonImage(true, this.mGet.getOrientationDegree());
            this.mGet.updateThumbnailButtonVisibility();
            CamLog.d(FaceDetector.TAG, "getImageListUri().clear() call");
            this.mGet.getImageListUri().clear();
            this.mSynthesizeInProgress = false;
            this.mPanoramaStarted = false;
            CamLog.d(FaceDetector.TAG, "Panorama Controller onPause -end");
            return;
        }
        CamLog.i(FaceDetector.TAG, "Pano Panorama Controller onPause - return");
    }

    public void onDestroy() {
        this.mGuideView = null;
        this.mPanoramaView = null;
        this.mAutoPanoramaEngine = null;
        this.mJpegData = null;
        super.onDestroy();
    }
}
