package com.lge.camera.controller;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.components.GestureGuideBox;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.Util;
import com.lge.gestureshot.library.GestureEngineProcessor;
import com.lge.gestureshot.library.GestureEngineProcessor.Callback;
import com.lge.gestureshot.library.HandInfo;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class GestureShutterController extends Controller implements PreviewCallback {
    public static final int GESTURETYPE_FIST = 1;
    public static final int GESTURETYPE_HAND = 0;
    private int mDegree;
    private HandInfo mDetectedHandInfo;
    private GestureEngineProcessor mGestureEngine;
    private View mGestureGuideView;
    private OnGestureRecogListener mGestureRecogListener;
    private onGestureUIListener mGestureUIListener;
    private ImageView mGuideImage;
    private TextView mGuideString;
    private View mGuideStringLayout;
    private GestureGuideBox mHandGuideBox;
    private byte[] mPreviewBuff;
    private int mTranHeight;
    private int mTranMinX;
    private int mTranMinY;
    private int mTranWidth;
    private boolean mUseCallback;

    public interface OnGestureRecogListener {
        void doTimershotByGestureRecog();
    }

    private class GestureRegEngineCallback implements Callback {
        private GestureRegEngineCallback() {
        }

        public void onGestureEngineErrorCallback(int errorType) {
            CamLog.d(FaceDetector.TAG, "onGestureEngineErrorCallback()");
        }

        public void onGestureEngineResultCallback(int eventType, HandInfo handInfo) {
            if (GestureShutterController.this.mDetectedHandInfo != null) {
                GestureShutterController.this.mDetectedHandInfo.setHandInfo(handInfo);
                switch (eventType) {
                    case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                        CamLog.d(FaceDetector.TAG, "GESTURE_EVENT_DRAW_GUIDEBOX");
                        GestureShutterController.this.showGestureGuide();
                        if (GestureShutterController.this.mGestureUIListener != null) {
                            GestureShutterController.this.mGestureUIListener.onShowGestureGuide();
                        }
                        GestureShutterController.this.mGet.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                GestureShutterController.this.mGet.removePostRunnable(this);
                                GestureShutterController.this.drawHandGuideRect(GestureShutterController.this.mDetectedHandInfo);
                            }
                        });
                    case LGKeyRec.EVENT_STARTED /*3*/:
                        CamLog.d(FaceDetector.TAG, "GESTURE_EVENT_CAPTURE");
                        if (GestureShutterController.this.mHandGuideBox != null) {
                            GestureShutterController.this.hideGestureGuide();
                            if (GestureShutterController.this.mGestureUIListener != null) {
                                GestureShutterController.this.mGestureUIListener.onHideGestureGuide();
                            }
                            GestureShutterController.this.mHandGuideBox.invalidate();
                            GestureShutterController.this.stopGestureEngine();
                            GestureShutterController.this.executeTimershot();
                        }
                    case LGKeyRec.EVENT_STOPPED /*4*/:
                        if (GestureShutterController.this.mHandGuideBox != null && GestureShutterController.this.mHandGuideBox.getVisibility() == 0) {
                            GestureShutterController.this.hideGestureGuide();
                            if (GestureShutterController.this.mGestureUIListener != null) {
                                GestureShutterController.this.mGestureUIListener.onHideGestureGuide();
                            }
                        }
                    default:
                }
            }
        }
    }

    public interface onGestureUIListener {
        void onHideGestureGuide();

        void onShowGestureGuide();
    }

    public GestureShutterController(ControllerFunction function) {
        super(function);
        this.mGestureEngine = null;
        this.mHandGuideBox = null;
        this.mGestureGuideView = null;
        this.mGuideStringLayout = null;
        this.mGuideString = null;
        this.mGuideImage = null;
        this.mPreviewBuff = null;
        this.mDetectedHandInfo = new HandInfo();
        this.mTranMinX = 0;
        this.mTranMinY = 0;
        this.mTranWidth = 0;
        this.mTranHeight = 0;
        this.mDegree = 0;
        this.mUseCallback = false;
        this.mGestureRecogListener = null;
        this.mGestureUIListener = null;
    }

    public void setGestureRecogEngineListener(OnGestureRecogListener listener) {
        this.mGestureRecogListener = listener;
    }

    public void initController() {
        initLayout();
        super.initController();
    }

    public void reInitialize() {
        super.reInitialize();
    }

    public void initLayout() {
        CamLog.d(FaceDetector.TAG, "initLayout");
        ViewGroup vg = (ViewGroup) this.mGet.findViewById(R.id.init);
        this.mGet.inflateStub(R.id.stub_focus);
        this.mDegree = this.mGet.getOrientationDegree();
        this.mHandGuideBox = (GestureGuideBox) this.mGet.findViewById(R.id.hand_shutter_view);
        this.mGestureGuideView = this.mGet.inflateView(R.layout.gestureshutter);
        this.mGuideStringLayout = this.mGestureGuideView.findViewById(R.id.gestureshutter_guide_layout);
        this.mGuideString = (TextView) this.mGestureGuideView.findViewById(R.id.gestureshutter_guide_string);
        this.mGuideImage = (ImageView) this.mGestureGuideView.findViewById(R.id.gestureshutter_guide_image);
        this.mGuideString.setText(R.string.gesture_guide);
        if (!(vg == null || this.mGestureGuideView == null || this.mGestureGuideView.getParent() != null)) {
            vg.addView(this.mGestureGuideView, new LayoutParams(-1, -1));
        }
        if (this.mHandGuideBox != null) {
            this.mHandGuideBox.setVisibility(4);
            this.mHandGuideBox.init();
            this.mHandGuideBox.setInitialDegree(this.mDegree);
        }
        setRotateDegree(this.mDegree, false);
    }

    public void releaseLayout() {
        CamLog.d(FaceDetector.TAG, "releaseLayout");
        ViewGroup vg = (ViewGroup) this.mGet.findViewById(R.id.init);
        if (vg != null) {
            vg.removeView(this.mGestureGuideView);
            this.mGestureGuideView = null;
        }
        if (this.mHandGuideBox != null) {
            this.mHandGuideBox.unbind();
            this.mHandGuideBox = null;
        }
        this.mGuideImage = null;
        this.mGuideString = null;
        this.mGuideStringLayout = null;
    }

    public void runGestureEngine(boolean useCallback) {
        this.mUseCallback = useCallback;
        if (!isAvailableGestureShutterStarted()) {
            stopGestureEngine();
            releaseGestureEngine();
        } else if (this.mGet.getCameraDevice() != null) {
            if (this.mPreviewBuff == null) {
                Parameters parameters = this.mGet.getCameraDevice().getParameters();
                if (parameters == null) {
                    CamLog.d(FaceDetector.TAG, "parameter is null. can not run GestureEngine!! ");
                    return;
                }
                Size previewSize = parameters.getPreviewSize();
                if (previewSize == null) {
                    CamLog.d(FaceDetector.TAG, "previewSize is null. can not run GestureEngine!! ");
                    return;
                }
                this.mPreviewBuff = new byte[((int) (((double) (previewSize.width * previewSize.height)) * 1.5d))];
            }
            if (this.mGet.getCameraDevice() != null && useCallback) {
                if (this.mPreviewBuff != null) {
                    this.mGet.getCameraDevice().addCallbackBuffer(this.mPreviewBuff);
                }
                this.mGet.getCameraDevice().setPreviewCallbackWithBuffer(this);
            }
            initGestureEngine();
            startGestureEngine();
        }
    }

    public void startGestureEngine() {
        if (this.mGestureEngine != null) {
            hideGestureGuide();
            this.mGestureEngine.start();
            CamLog.d(FaceDetector.TAG, "startGestureEngine");
        }
    }

    public void stopGestureEngine() {
        if (this.mGestureEngine != null) {
            this.mGestureEngine.stop();
            CamLog.d(FaceDetector.TAG, "stopGestureEngine");
        }
        hideGestureGuide();
    }

    public void releaseGestureEngine() {
        hideGestureGuide();
        if (this.mGestureEngine != null) {
            this.mGestureEngine.release();
            this.mGestureEngine = null;
            if (this.mGet.getCameraDevice() != null && this.mUseCallback) {
                this.mGet.getCameraDevice().addCallbackBuffer(null);
                this.mGet.getCameraDevice().setPreviewCallbackWithBuffer(null);
            }
            this.mPreviewBuff = null;
            this.mUseCallback = false;
            CamLog.d(FaceDetector.TAG, "releaseGestureEngine");
        }
    }

    public void onResume() {
        initLayout();
        super.onResume();
    }

    public void onPause() {
        hideGestureGuide();
        releaseLayout();
        super.onPause();
    }

    public void initGestureEngine() {
        if (this.mGestureEngine == null) {
            this.mGestureEngine = new GestureEngineProcessor(new GestureRegEngineCallback());
        }
        this.mGestureEngine.create();
        CamLog.d(FaceDetector.TAG, "initGestureEngine");
    }

    public void showGestureGuide() {
        CamLog.d(FaceDetector.TAG, "showGestureGuide");
        if (this.mHandGuideBox != null && this.mGestureEngine != null && this.mGet != null && this.mGuideStringLayout != null && this.mGuideString != null && this.mGuideImage != null && this.mHandGuideBox != null && this.mGestureEngine.getGestureEngineState() == 3) {
            setRotateDegree(this.mGet.getOrientationDegree(), false);
            if (this.mDetectedHandInfo != null) {
                if (this.mDetectedHandInfo.mGestureType == GESTURETYPE_FIST) {
                    this.mGuideString.setText(R.string.sp_gesture_guide_msg_fist);
                    this.mGuideImage.setImageResource(R.drawable.shutter_hand_gesture_icon_02);
                } else {
                    this.mGuideString.setText(R.string.gesture_guide);
                    this.mGuideImage.setImageResource(R.drawable.shutter_hand_gesture_icon);
                }
            }
            this.mGuideStringLayout.setVisibility(0);
            this.mHandGuideBox.setVisibility(0);
        }
    }

    public void hideGestureGuide() {
        CamLog.d(FaceDetector.TAG, "hideGestureGuide");
        if (this.mGuideStringLayout != null && this.mHandGuideBox != null && this.mGuideString != null && this.mGuideImage != null) {
            this.mGuideStringLayout.setVisibility(8);
            this.mHandGuideBox.setVisibility(8);
        }
    }

    private void convertCoordinate(HandInfo handInfo) {
        int previewWidth = 0;
        int previewHeight = 0;
        int tmpOrientation = getOrientation();
        this.mTranMinX = handInfo.mMinX;
        this.mTranMinY = handInfo.mMinY;
        this.mTranWidth = handInfo.mWidth;
        this.mTranHeight = handInfo.mHeight;
        Parameters parameters = this.mGet.getCameraDevice().getParameters();
        if (parameters != null) {
            Size previewSize = parameters.getPreviewSize();
            if (previewSize != null) {
                if (tmpOrientation == GESTURETYPE_FIST || tmpOrientation == 3) {
                    previewWidth = previewSize.width;
                    previewHeight = previewSize.height;
                } else {
                    previewHeight = previewSize.width;
                    previewWidth = previewSize.height;
                }
            }
            switch (tmpOrientation) {
                case GESTURETYPE_FIST /*1*/:
                    this.mTranMinX = (previewHeight - (handInfo.mMinY + handInfo.mHeight)) - 1;
                    this.mTranMinY = handInfo.mMinX;
                    this.mTranWidth = handInfo.mHeight;
                    this.mTranHeight = handInfo.mWidth;
                    break;
                case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                    this.mTranMinX = (previewWidth - (handInfo.mMinX + handInfo.mWidth)) - 1;
                    this.mTranMinY = (previewHeight - (handInfo.mMinY + handInfo.mHeight)) - 1;
                    this.mTranWidth = handInfo.mWidth;
                    this.mTranHeight = handInfo.mHeight;
                    break;
                case LGKeyRec.EVENT_STARTED /*3*/:
                    this.mTranMinX = handInfo.mMinY;
                    this.mTranMinY = (previewWidth - (handInfo.mMinX + handInfo.mWidth)) - 1;
                    this.mTranWidth = handInfo.mHeight;
                    this.mTranHeight = handInfo.mWidth;
                    break;
            }
            if (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_LIGHT))) {
                calculateCoordinatesforHalfPreview();
            }
        }
    }

    private void calculateCoordinatesforHalfPreview() {
        Parameters parameters = this.mGet.getCameraDevice().getParameters();
        if (parameters != null) {
            Size previewSize = parameters.getPreviewSize();
            int naviHeight = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_panel_marginBottom);
            int transValue_W = previewSize.height / 4;
            int transValue_H = previewSize.width / 4;
            if (Util.isConfigureLandscape(this.mGet.getApplicationContext().getResources())) {
                transValue_W = (previewSize.height - naviHeight) / 4;
            } else {
                transValue_H = (previewSize.width - naviHeight) / 4;
            }
            int x = this.mTranMinX / 2;
            int y = this.mTranMinY / 2;
            int h = this.mTranHeight / 2;
            this.mTranWidth /= 2;
            this.mTranHeight = h;
            this.mTranMinX = x + transValue_W;
            this.mTranMinY = y + transValue_H;
        }
    }

    private void drawHandGuideRect(HandInfo handInfo) {
        int quickBtnW = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.quickfunction_layout_width);
        int naviHeight = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_panel_marginBottom);
        int pannelW = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_panel_width);
        if (this.mHandGuideBox != null && handInfo != null) {
            convertCoordinate(handInfo);
            Parameters parameters = this.mGet.getCameraDevice().getParameters();
            if (parameters != null) {
                Size previewSize = parameters.getPreviewSize();
                if (previewSize != null) {
                    this.mHandGuideBox.setCoorinate(quickBtnW, pannelW, naviHeight);
                    this.mHandGuideBox.setPreviewSize(this.mGet.getApplicationContext(), previewSize.width, previewSize.height);
                    this.mHandGuideBox.setRectangleArea(this.mTranMinX, this.mTranMinY, this.mTranWidth, this.mTranHeight);
                }
            }
        }
    }

    private void executeTimershot() {
        CamLog.d(FaceDetector.TAG, "executeTimershot");
        if (this.mHandGuideBox != null) {
            this.mHandGuideBox.setVisibility(4);
        }
        if (this.mGestureRecogListener != null) {
            this.mGestureRecogListener.doTimershotByGestureRecog();
        }
    }

    public boolean isAvailableGestureShutterStarted() {
        return this.mGet.getCameraId() == GESTURETYPE_FIST && FunctionProperties.isSupportedGestureShot() && !this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE).equals(CameraConstants.TYPE_RECORDMODE_DUAL);
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        if (this.mGet != null && this.mGet.getCameraDevice() != null && this.mGestureEngine != null && data != null) {
            int nOrientation = getOrientation();
            Parameters parameters = this.mGet.getCameraDevice().getParameters();
            if (parameters != null) {
                Size previewSize = parameters.getPreviewSize();
                if (previewSize.width != 0 && previewSize.height != 0) {
                    if (this.mUseCallback) {
                        this.mGet.getCameraDevice().addCallbackBuffer(this.mPreviewBuff);
                    } else {
                        this.mPreviewBuff = data;
                    }
                    int gestureEngineState = this.mGestureEngine.getGestureEngineState();
                    GestureEngineProcessor gestureEngineProcessor = this.mGestureEngine;
                    if (gestureEngineState == 3) {
                        this.mGestureEngine.putPreviewFrame(this.mPreviewBuff, nOrientation, previewSize.width, previewSize.height, 0);
                    }
                }
            }
        }
    }

    private int getOrientation() {
        switch (this.mGet.getOrientationDegree()) {
            case MediaProviderUtils.ROTATION_90 /*90*/:
                return 3;
            case MediaProviderUtils.ROTATION_180 /*180*/:
                return 2;
            case Tag.IMAGE_DESCRIPTION /*270*/:
                return GESTURETYPE_FIST;
            default:
                return 0;
        }
    }

    public void startRotation(int degree, boolean animation) {
        if (this.mHandGuideBox != null && this.mDegree != degree) {
            hideGestureGuide();
            CamLog.d(FaceDetector.TAG, "setDegree " + degree);
            this.mHandGuideBox.setDegree(degree);
            this.mDegree = degree;
            setRotateDegree(this.mDegree, false);
        }
    }

    public void setRotateDegree(int degree, boolean animation) {
        if (this.mGestureGuideView != null) {
            int bottomMargin = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.gestureshutter_guide_bottom_margin);
            int panelMargin = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.gestureshutter_guide_panel_margin);
            LayoutParams guideParams = (LayoutParams) this.mGuideStringLayout.getLayoutParams();
            Common.resetLayoutParameter(guideParams);
            guideParams.setMarginStart(0);
            guideParams.setMarginEnd(0);
            guideParams.topMargin = 0;
            guideParams.bottomMargin = 0;
            if (!Util.isConfigureLandscape(this.mGet.getApplicationContext().getResources())) {
                switch (degree) {
                    case LGKeyRec.EVENT_INVALID /*0*/:
                        guideParams.addRule(14, GESTURETYPE_FIST);
                        guideParams.addRule(12, GESTURETYPE_FIST);
                        guideParams.bottomMargin = panelMargin;
                        break;
                    case MediaProviderUtils.ROTATION_90 /*90*/:
                        guideParams.addRule(10, GESTURETYPE_FIST);
                        guideParams.addRule(14, GESTURETYPE_FIST);
                        guideParams.topMargin = bottomMargin;
                        break;
                    case MediaProviderUtils.ROTATION_180 /*180*/:
                        guideParams.addRule(14, GESTURETYPE_FIST);
                        guideParams.addRule(10, GESTURETYPE_FIST);
                        guideParams.topMargin = panelMargin;
                        break;
                    case Tag.IMAGE_DESCRIPTION /*270*/:
                        guideParams.addRule(14, GESTURETYPE_FIST);
                        guideParams.addRule(12, GESTURETYPE_FIST);
                        guideParams.bottomMargin = bottomMargin;
                        break;
                    default:
                        break;
                }
            }
            switch (degree) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    guideParams.addRule(14, GESTURETYPE_FIST);
                    guideParams.addRule(12, GESTURETYPE_FIST);
                    guideParams.bottomMargin = bottomMargin;
                    break;
                case MediaProviderUtils.ROTATION_90 /*90*/:
                    guideParams.addRule(14, GESTURETYPE_FIST);
                    guideParams.addRule(12, GESTURETYPE_FIST);
                    guideParams.bottomMargin = panelMargin;
                    break;
                case MediaProviderUtils.ROTATION_180 /*180*/:
                    guideParams.addRule(14, GESTURETYPE_FIST);
                    guideParams.addRule(10, GESTURETYPE_FIST);
                    guideParams.topMargin = bottomMargin;
                    break;
                case Tag.IMAGE_DESCRIPTION /*270*/:
                    guideParams.addRule(14, GESTURETYPE_FIST);
                    guideParams.addRule(10, GESTURETYPE_FIST);
                    guideParams.topMargin = panelMargin;
                    break;
            }
            this.mGuideStringLayout.setLayoutParams(guideParams);
            ((RotateLayout) this.mGestureGuideView).rotateLayout(degree);
        }
    }
}
