package com.lge.camera.controller.camera;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera.Face;
import android.provider.Settings.System;
import android.util.TypedValue;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.components.CameraPreview;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.Util;
import com.lge.olaworks.library.AutoPanorama;
import com.lge.olaworks.library.FaceBeauty;
import com.lge.olaworks.library.FaceDetector;
import com.lge.olaworks.library.FaceDetector.Callback;
import com.lge.systemservice.core.LEDManager;
import com.lge.systemservice.core.LGContext;
import com.lge.systemservice.core.LGLedRecord;

public class FacePreviewController extends CameraController implements Callback {
    private static final int FACT_DISTANCE_DEFAULT = 35;
    private static final int MAX_FACE_NUM = 5;
    private static final int NONE_FACE_JUDGE_COUNT = 5;
    private int FACE_DISTANCE_TH;
    private int FACE_TIME_TH;
    private Point mAverageOfFacePoint;
    private float mCenter_x;
    private float mCenter_y;
    private int mCheckNoneFaceCount;
    private Rect[] mDetectedFaces;
    private CameraPreview mFaceDetectView;
    private int mFaceDetectedCount;
    private int mFaceLedEnabled;
    private int mFaceTimeCnt;
    private boolean mIsFaceFocusSuccessed;
    private boolean mIsReadyToInitilaizeFaceRect;
    private LEDManager mLEDManager;
    private int mLeftMargin;
    private FaceDetector mNewFaceDetector;
    private int mPreviewHeight;
    private int mPreviewWidth;
    private Rect mPreviousFaceRect;
    private Rect mPreviousFaceRectByFocusing;
    private int mPreviousFocusState;
    private int mPreviousLargestFaceIndex;
    private LGLedRecord mRecord;
    private Runnable mResetFace;
    private Point mSumOfFacePoint;
    private int mTimerLedEnabled;

    public FacePreviewController(ControllerFunction function) {
        super(function);
        this.mNewFaceDetector = null;
        this.mFaceDetectView = null;
        Rect[] rectArr = new Rect[NONE_FACE_JUDGE_COUNT];
        rectArr[0] = new Rect();
        rectArr[1] = new Rect();
        rectArr[2] = new Rect();
        rectArr[3] = new Rect();
        rectArr[4] = new Rect();
        this.mDetectedFaces = rectArr;
        this.mLeftMargin = 0;
        this.mPreviousFaceRectByFocusing = new Rect(-1, -1, 0, 0);
        this.mPreviousFaceRect = new Rect(-1, -1, 0, 0);
        this.mPreviousLargestFaceIndex = 0;
        this.FACE_DISTANCE_TH = FACT_DISTANCE_DEFAULT;
        this.FACE_TIME_TH = 10;
        this.mFaceTimeCnt = 0;
        this.mSumOfFacePoint = new Point(0, 0);
        this.mAverageOfFacePoint = new Point(0, 0);
        this.mPreviousFocusState = 0;
        this.mIsFaceFocusSuccessed = false;
        this.mFaceLedEnabled = 1;
        this.mTimerLedEnabled = 1;
        this.mPreviewWidth = 0;
        this.mPreviewHeight = 0;
        this.mCenter_x = 0.0f;
        this.mCenter_y = 0.0f;
        this.mIsReadyToInitilaizeFaceRect = false;
        this.mCheckNoneFaceCount = 0;
        this.mResetFace = new Runnable() {
            public void run() {
                if (FacePreviewController.this.mPreviousFaceRectByFocusing != null) {
                    FacePreviewController.this.mPreviousFaceRectByFocusing.set(-1, -1, 0, 0);
                }
                if (FacePreviewController.this.mPreviousFaceRect != null) {
                    FacePreviewController.this.mPreviousFaceRect.set(-1, -1, 0, 0);
                }
                if (FacePreviewController.this.mSumOfFacePoint != null) {
                    FacePreviewController.this.mSumOfFacePoint.set(0, 0);
                }
                FacePreviewController.this.mFaceDetectedCount = 0;
                if (FacePreviewController.this.mFaceDetectView != null) {
                    FacePreviewController.this.mFaceDetectView.setFaceRectangles(FacePreviewController.this.mDetectedFaces, FacePreviewController.this.mFaceDetectedCount);
                }
                FacePreviewController.this.mIsReadyToInitilaizeFaceRect = false;
            }
        };
    }

    public void onPause() {
        super.onPause();
        hideView();
        this.mGet.getHandler().removeCallbacks(this.mResetFace);
    }

    public void onDestroy() {
        this.mFaceDetectView = null;
        this.mNewFaceDetector = null;
        this.mDetectedFaces = null;
        super.onDestroy();
    }

    public void initController() {
        if (Util.SizeString2WidthHeight(this.mGet.getPreviewSizeOnScreen())[0] > getPixelFromDimens(R.dimen.layout_preview_width)) {
            this.mLeftMargin = 0;
        } else {
            this.mLeftMargin = getPixelFromDimens(R.dimen.layout_preview_marginLeft);
        }
        this.mFaceDetectView = (CameraPreview) this.mGet.findViewById(R.id.preview_holder_surface);
        if (FunctionProperties.useFaceDetectionFromHal()) {
            this.mDetectedFaces = new Rect[NONE_FACE_JUDGE_COUNT];
            for (int i = 0; i < NONE_FACE_JUDGE_COUNT; i++) {
                this.mDetectedFaces[i] = new Rect();
                this.mDetectedFaces[i].setEmpty();
            }
        } else {
            this.mNewFaceDetector = new FaceDetector(this);
            this.mGet.getEngineProcessor().setEngine(this.mNewFaceDetector);
        }
        this.FACE_DISTANCE_TH = Math.round(TypedValue.applyDimension(1, 35.0f, this.mGet.getResources().getDisplayMetrics()));
        this.mInit = true;
    }

    public void startFaceDetection(boolean bHasUI) {
        if (this.mGet.getCameraMode() == 0) {
            initFaceDetectInfo();
            initEmotionalLEDForFaceTracking();
        }
        if (FunctionProperties.useFaceDetectionFromHal()) {
            this.mGet.startFaceDetectionFromHal(bHasUI);
        } else {
            this.mGet.getEngineProcessor().start();
        }
        if (this.mGet.getCameraMode() == 0) {
            showView();
        }
    }

    public void stopFaceDetection() {
        CamLog.d(FaceDetector.TAG, "Face dectection stop!");
        if (FunctionProperties.useFaceDetectionFromHal()) {
            this.mGet.stopFaceDetectionFromHal();
        } else if (!(this.mGet.getEngineProcessor() == null || this.mGet.getEngineProcessor().isEmptyEngine() || this.mGet.getEngineProcessor().checkEngineTag(FaceBeauty.ENGINE_TAG) || this.mGet.getEngineProcessor().checkEngineTag(AutoPanorama.ENGINE_TAG))) {
            this.mGet.getEngineProcessor().stop();
        }
        if (this.mGet.getCameraMode() == 0) {
            stopLEDForFaceTracking();
            initFaceDetectInfo();
            hideView();
        }
    }

    public void showView() {
        if (this.mFaceDetectView != null) {
            this.mFaceDetectView.setDrawMode(1);
        }
    }

    public void hideView() {
        if (this.mFaceDetectView != null) {
            this.mFaceDetectView.setDrawMode(0);
        }
    }

    public Rect[] getDetectedFaces() {
        return this.mDetectedFaces;
    }

    public void initFaceDetectInfo() {
        if (!FunctionProperties.useFaceDetectionFromHal()) {
            this.mDetectedFaces = null;
        } else if (this.mDetectedFaces == null) {
            this.mDetectedFaces = new Rect[NONE_FACE_JUDGE_COUNT];
            for (i = 0; i < NONE_FACE_JUDGE_COUNT; i++) {
                this.mDetectedFaces[i] = new Rect();
                this.mDetectedFaces[i].setEmpty();
            }
        } else {
            for (i = 0; i < NONE_FACE_JUDGE_COUNT; i++) {
                if (this.mDetectedFaces[i] != null) {
                    this.mDetectedFaces[i].setEmpty();
                }
            }
        }
        this.mFaceDetectedCount = 0;
        if (this.mFaceDetectView != null) {
            this.mFaceDetectView.resetFaceRectangles();
        }
        int[] previewSizeOnScreen = Util.SizeString2WidthHeight(this.mGet.getPreviewSizeOnDevice());
        this.mPreviewWidth = previewSizeOnScreen[0];
        this.mPreviewHeight = previewSizeOnScreen[1];
        this.mCenter_x = ((float) previewSizeOnScreen[0]) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
        this.mCenter_y = ((float) previewSizeOnScreen[1]) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
    }

    public void getSumPosition(Rect r) {
        Point point = this.mSumOfFacePoint;
        point.x += (r.left + r.right) / 2;
        point = this.mSumOfFacePoint;
        point.y += (r.top + r.bottom) / 2;
    }

    public void outSet(Rect dst_r, Rect src_r, int dx, int dy) {
        dst_r.left = src_r.left - dx;
        dst_r.right = src_r.right + dx;
        dst_r.top = src_r.top - dy;
        dst_r.bottom = src_r.bottom + dy;
        if (dst_r.left < 0) {
            dst_r.left = 0;
        }
        if (dst_r.top < 0) {
            dst_r.top = 0;
        }
    }

    public void onFaceDetected(int numDetectedFaces, Rect[] detectedFaces) {
        if (numDetectedFaces <= 0) {
            this.mResetFace.run();
            stopLEDForFaceTracking();
            this.mPreviousFocusState = 0;
            this.mIsFaceFocusSuccessed = false;
        } else if (this.mGet.getInCaptureProgress() || this.mGet.isPausing()) {
            CamLog.v(FaceDetector.TAG, " captureing or pausing..don't need to detect faces");
        } else {
            this.mFaceDetectedCount = numDetectedFaces;
            this.mDetectedFaces = detectedFaces;
            if (this.mGet.getCameraMode() == 1) {
                flipHorizontal(this.mDetectedFaces, this.mFaceDetectedCount);
            }
            this.mFaceDetectView.setFaceRectangles(this.mDetectedFaces, this.mFaceDetectedCount);
            int largestFaceIndex = getIndexLargestFace(detectedFaces, this.mFaceDetectedCount);
            for (int i = 0; i < this.mFaceDetectedCount; i++) {
                if (largestFaceIndex == i) {
                    if (this.mGet.isBlockingFaceTrFocusing()) {
                        CamLog.v(FaceDetector.TAG, "face blocking return:yellow:" + i);
                        this.mFaceDetectView.setFaceRectangleColor(i, -256);
                    } else if (!FunctionProperties.isTouchAfSupported(this.mGet.getApplicationMode()) || ModelProperties.isFixedFocusModel()) {
                        CamLog.v(FaceDetector.TAG, "not supported face focus return:" + i);
                        this.mFaceDetectView.setFaceRectangleColor(i, -16711936);
                    } else {
                        if (this.mGet.getFocusState() == 0) {
                            this.mPreviousFaceRectByFocusing.set(-1, -1, 0, 0);
                        }
                        int center_y = (detectedFaces[largestFaceIndex].top + detectedFaces[largestFaceIndex].bottom) / 2;
                        int center_x = ((detectedFaces[largestFaceIndex].right + detectedFaces[largestFaceIndex].left) / 2) + this.mLeftMargin;
                        setFaceDetecedRectColor(detectedFaces, largestFaceIndex);
                        onFaceDetecedStartFocus(detectedFaces, largestFaceIndex, center_x, center_y);
                        this.mPreviousFaceRect = detectedFaces[largestFaceIndex];
                    }
                }
            }
            if (this.mFaceDetectView.getVisibility() == 4) {
                this.mFaceDetectView.setVisibility(0);
            }
            if (!(this.mGet.getFocusState() == this.mPreviousFocusState || (this.mGet.isTimerShotCountdown() && this.mTimerLedEnabled == 1))) {
                if (this.mGet.getFocusState() == 6 && this.mIsFaceFocusSuccessed) {
                    setLEDPatternId(CameraConstants.ID_CAMERA_SHOT_BEST_GUIDE2);
                    startLEDForFaceTracking();
                } else if (ModelProperties.getProjectCode() == 23) {
                    stopLEDForFaceTracking();
                } else {
                    setLEDPatternId(CameraConstants.ID_CAMERA_SHOT_BEST_GUIDE);
                    startLEDForFaceTracking();
                }
            }
            this.mPreviousFocusState = this.mGet.getFocusState();
            this.mIsFaceFocusSuccessed = false;
        }
    }

    public void onFaceDetectionFromHal(Face[] faces) {
        if (this.mGet.getInCaptureProgress() || this.mGet.isPausing()) {
            CamLog.v(FaceDetector.TAG, " captureing or pausing..don't need to detect faces");
            return;
        }
        this.mFaceDetectedCount = faces.length;
        CamLog.d(FaceDetector.TAG, "[HAL FACE] mFaceDetectedCount = " + this.mFaceDetectedCount);
        if (this.mGet.getCameraId() == 1) {
            return;
        }
        if (faces.length > 0) {
            int i;
            this.mGet.getHandler().removeCallbacks(this.mResetFace);
            this.mCheckNoneFaceCount = 0;
            for (i = 0; i < this.mFaceDetectedCount; i++) {
                this.mDetectedFaces[i].left = Math.round(((float) ((faces[i].rect.left * this.mPreviewWidth) / Common.NO_BUTTON_POPUP_DISMISS_DELAY)) + this.mCenter_x);
                this.mDetectedFaces[i].right = Math.round(((float) ((faces[i].rect.right * this.mPreviewWidth) / Common.NO_BUTTON_POPUP_DISMISS_DELAY)) + this.mCenter_x);
                this.mDetectedFaces[i].top = Math.round(((float) ((faces[i].rect.top * this.mPreviewHeight) / Common.NO_BUTTON_POPUP_DISMISS_DELAY)) + this.mCenter_y);
                this.mDetectedFaces[i].bottom = Math.round(((float) ((faces[i].rect.bottom * this.mPreviewHeight) / Common.NO_BUTTON_POPUP_DISMISS_DELAY)) + this.mCenter_y);
            }
            if (this.mGet.getCameraMode() == 1) {
                flipHorizontal(this.mDetectedFaces, this.mFaceDetectedCount);
            }
            this.mFaceDetectView.setFaceRectangles(this.mDetectedFaces, this.mFaceDetectedCount);
            int largestFaceIndex = getIndexLargestFace(this.mDetectedFaces, this.mFaceDetectedCount);
            if ((this.mGet.getFocusState() == 3 || this.mGet.getFocusState() == 7) && !this.mIsReadyToInitilaizeFaceRect) {
                this.mIsReadyToInitilaizeFaceRect = true;
            }
            for (i = 0; i < this.mFaceDetectedCount; i++) {
                if (largestFaceIndex == i) {
                    if (this.mGet.isBlockingFaceTrFocusing()) {
                        CamLog.v(FaceDetector.TAG, "face blocking return:yellow:" + i);
                        this.mFaceDetectView.setFaceRectangleColor(i, -256);
                    } else if (!FunctionProperties.isTouchAfSupported(this.mGet.getApplicationMode()) || ModelProperties.isFixedFocusModel()) {
                        CamLog.v(FaceDetector.TAG, "not supported face focus return:" + i);
                        this.mFaceDetectView.setFaceRectangleColor(i, -16711936);
                    } else {
                        if (this.mGet.getFocusState() == 0 && this.mIsReadyToInitilaizeFaceRect) {
                            this.mPreviousFaceRectByFocusing.set(-1, -1, 0, 0);
                            this.mIsReadyToInitilaizeFaceRect = false;
                        }
                        int center_y = (this.mDetectedFaces[largestFaceIndex].top + this.mDetectedFaces[largestFaceIndex].bottom) / 2;
                        int center_x = ((this.mDetectedFaces[largestFaceIndex].right + this.mDetectedFaces[largestFaceIndex].left) / 2) + this.mLeftMargin;
                        setFaceDetecedRectColor(this.mDetectedFaces, largestFaceIndex);
                        onFaceDetecedStartFocus(this.mDetectedFaces, largestFaceIndex, center_x, center_y);
                        this.mPreviousFaceRect = this.mDetectedFaces[largestFaceIndex];
                    }
                }
            }
            if (this.mFaceDetectView.getVisibility() == 4) {
                this.mFaceDetectView.setVisibility(0);
            }
            if (!(this.mGet.getFocusState() == this.mPreviousFocusState || (this.mGet.isTimerShotCountdown() && this.mTimerLedEnabled == 1))) {
                if (this.mGet.getFocusState() == 6 && this.mIsFaceFocusSuccessed) {
                    setLEDPatternId(CameraConstants.ID_CAMERA_SHOT_BEST_GUIDE2);
                    startLEDForFaceTracking();
                } else if (ModelProperties.getProjectCode() == 23) {
                    stopLEDForFaceTracking();
                } else {
                    setLEDPatternId(CameraConstants.ID_CAMERA_SHOT_BEST_GUIDE);
                    startLEDForFaceTracking();
                }
            }
            this.mPreviousFocusState = this.mGet.getFocusState();
            this.mIsFaceFocusSuccessed = false;
        } else if (this.mCheckNoneFaceCount >= NONE_FACE_JUDGE_COUNT) {
            this.mGet.getHandler().removeCallbacks(this.mResetFace);
            this.mResetFace.run();
        } else {
            this.mGet.getHandler().removeCallbacks(this.mResetFace);
            this.mGet.getHandler().postDelayed(this.mResetFace, 500);
            this.mCheckNoneFaceCount++;
            stopLEDForFaceTracking();
            this.mPreviousFocusState = 0;
            this.mIsFaceFocusSuccessed = false;
        }
    }

    private int getIndexLargestFace(Rect[] detectedFaces, int length) {
        int largestFaceIndex = length == 0 ? 0 : this.mPreviousLargestFaceIndex;
        if (this.mGet.getFocusState() == 0) {
            for (int i = 0; i < length; i++) {
                int newWidth = detectedFaces[i].right - detectedFaces[i].left;
                if (detectedFaces[largestFaceIndex].right - detectedFaces[largestFaceIndex].left < newWidth) {
                    largestFaceIndex = i;
                    int largestFaceWidth = newWidth;
                }
            }
            this.mPreviousLargestFaceIndex = largestFaceIndex;
        }
        return largestFaceIndex;
    }

    private void onFaceDetecedStartFocus(Rect[] detectedFaces, int largestFaceIndex, int center_x, int center_y) {
        try {
            if (this.mGet.getFocusState() == 0) {
                boolean startFocus = false;
                if (this.mPreviousFaceRect.left == -1) {
                    startFocus = true;
                } else if (this.mPreviousFaceRectByFocusing.left == -1) {
                    this.mFaceTimeCnt++;
                    if (this.mFaceTimeCnt == this.FACE_TIME_TH) {
                        getSumPosition(detectedFaces[largestFaceIndex]);
                        this.mAverageOfFacePoint.x = this.mSumOfFacePoint.x / this.mFaceTimeCnt;
                        this.mAverageOfFacePoint.y = this.mSumOfFacePoint.y / this.mFaceTimeCnt;
                        this.mFaceTimeCnt = 0;
                    } else if (this.mFaceTimeCnt < this.FACE_TIME_TH) {
                        getSumPosition(detectedFaces[largestFaceIndex]);
                    }
                    if (this.mFaceTimeCnt == 0) {
                        startFocus = true;
                        this.mSumOfFacePoint.set(0, 0);
                    }
                } else if (!this.mPreviousFaceRectByFocusing.contains(detectedFaces[largestFaceIndex])) {
                    this.mPreviousFaceRectByFocusing.set(-1, -1, 0, 0);
                }
                if (startFocus) {
                    CamLog.d(FaceDetector.TAG, "startFocus : center_x = " + center_x + ", center_y = " + center_y);
                    this.mGet.startFocusByTouchPress(center_x, center_y, true);
                    outSet(this.mPreviousFaceRectByFocusing, detectedFaces[largestFaceIndex], this.FACE_DISTANCE_TH, this.FACE_DISTANCE_TH);
                }
            }
        } catch (Exception e) {
            CamLog.w(FaceDetector.TAG, "Exception:", e);
        }
    }

    private void setFaceDetecedRectColor(Rect[] detectedFaces, int largestFaceIndex) {
        int index = 0;
        while (index < detectedFaces.length) {
            try {
                if (this.mPreviousFaceRectByFocusing.contains(detectedFaces[index])) {
                    if (this.mGet.getFocusState() == 7) {
                        this.mFaceDetectView.setFaceRectangleColor(index, -65536);
                    } else if (this.mGet.getFocusState() == 6) {
                        this.mFaceDetectView.setFaceRectangleColor(index, -16711936);
                        this.mIsFaceFocusSuccessed = true;
                    } else if (this.mGet.getFocusState() == 0) {
                        this.mFaceDetectView.setFaceRectangleColor(index, -16711936);
                    } else {
                        this.mFaceDetectView.setFaceRectangleColor(index, -256);
                    }
                } else if (this.mGet.getFocusState() == 0 && largestFaceIndex == index) {
                    this.mFaceDetectView.setFaceRectangleColor(index, -256);
                } else {
                    this.mFaceDetectView.setFaceRectangleColor(index, -1);
                }
                index++;
            } catch (Exception e) {
                CamLog.w(FaceDetector.TAG, "Exception:", e);
                return;
            }
        }
    }

    private void flipHorizontal(Rect[] rects, int count) {
        int previewWidth = this.mFaceDetectView.getWidth();
        for (int i = 0; i < count; i++) {
            int width = rects[i].width();
            rects[i].left = previewWidth - rects[i].right;
            rects[i].right = rects[i].left + width;
        }
    }

    public int getFaceDetectedCount() {
        return this.mFaceDetectedCount;
    }

    private void initEmotionalLEDForFaceTracking() {
        if (FunctionProperties.isSupportEmotionalLED()) {
            CamLog.d(FaceDetector.TAG, "Initialize Emotional LED");
            this.mFaceLedEnabled = System.getInt(this.mGet.getContentResolver(), "emotional_led_back_camera_face_detecting_noti", 1);
            this.mTimerLedEnabled = System.getInt(this.mGet.getContentResolver(), "emotional_led_back_camera_timer_noti", 1);
            if (this.mFaceLedEnabled != 1) {
                CamLog.d(FaceDetector.TAG, "FaceTracking LED Setting is disabled");
                return;
            }
            this.mLEDManager = (LEDManager) new LGContext(this.mGet.getApplicationContext()).getLGSystemService("emotionled");
            this.mRecord = new LGLedRecord();
            this.mRecord.priority = 0;
            this.mRecord.flags = 1;
            this.mRecord.whichLedPlay = 2;
        }
    }

    private void startLEDForFaceTracking() {
        if (FunctionProperties.isSupportEmotionalLED() && this.mFaceLedEnabled == 1 && this.mGet.getActivity() != null && this.mLEDManager != null) {
            CamLog.d(FaceDetector.TAG, "Emotioinal LED is started");
            this.mLEDManager.startPattern(this.mGet.getActivity().getPackageName(), 1, this.mRecord);
        }
    }

    private void stopLEDForFaceTracking() {
        if (FunctionProperties.isSupportEmotionalLED() && this.mFaceLedEnabled == 1 && this.mGet.getActivity() != null && this.mLEDManager != null) {
            CamLog.d(FaceDetector.TAG, "Emotioinal LED is stopped");
            this.mLEDManager.stopPattern(this.mGet.getActivity().getPackageName(), 1);
        }
    }

    private void setLEDPatternId(String path) {
        if (FunctionProperties.isSupportEmotionalLED() && this.mFaceLedEnabled == 1) {
            this.mRecord.patternFilePath = path;
        }
    }
}
