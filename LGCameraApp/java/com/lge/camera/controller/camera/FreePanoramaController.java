package com.lge.camera.controller.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.ShutterButton;
import com.lge.camera.listeners.FreePanoramaPreviewCallback;
import com.lge.camera.listeners.FreePanoramaPreviewCallback.FreePanoramaPreviewCallbackFunction;
import com.lge.camera.listeners.FreePanoramaSensorEventListener;
import com.lge.camera.listeners.FreePanoramaSensorEventListener.FreePanoramaSensorEventListenerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.ExifUtil;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.SecureImageUtil;
import com.lge.camera.util.Util;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.morpho.app.morphopanorama.GLTextureView;
import com.lge.morpho.app.morphopanorama.PanoramaViewRenderer;
import com.lge.morpho.app.morphopanorama.PanoramaViewRenderer.ResultInfo;
import com.lge.morpho.app.morphopanorama.PanoramaViewRenderer.onRenderPreviewListener;
import com.lge.morpho.app.quickpanorama.MorphoImageStitcher;
import com.lge.morpho.core.Error;
import com.lge.morpho.core.MorphoSensorFusion;
import com.lge.morpho.util.ImageConverter.ImageConverterJNI;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_Exif.ThumbNailSize;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;

public class FreePanoramaController extends CameraController implements FreePanoramaPreviewCallbackFunction, FreePanoramaSensorEventListenerFunction {
    private Animation mAnimationTakingGuideHide;
    private Animation mAnimationTakingGuideShow;
    private byte[][] mCameraBuff;
    private int mCameraBuffID;
    private long[] mDateTaken;
    private boolean mFinishFlg;
    private RelativeLayout mFreePanoramaStopGuide;
    private RelativeLayout mFreePanoramaTakingGuide;
    private View mFreePanoramaView;
    private GLTextureView mGLPanoramaView;
    private Handler mHandler;
    private ImageConverterJNI mImageConverter;
    private boolean mIsRemoveFreePanoramaBlackBg;
    private MorphoImageStitcher mMorphoImageStitcher;
    private MorphoSensorFusion mMorphoSensorFusion;
    private int mPanoramaEngineState;
    private int mPanoramaState;
    private FreePanoramaPreviewCallback mPreviewCallback;
    private int[] mPreviewSize;
    private PanoramaViewRenderer mRenderer;
    private CountDownLatch mSaveOutputImageLatch;
    private SaveOutputImageTask mSaveOutputImageTask;
    private int mSensorFusionMode;
    private FreePanoramaSensorEventListener mSensorListener;
    private int mStartDegree;
    private Thread mStopModuleThread;
    private Location mTempLocation;
    private Parameters mTempParams;

    private class SaveOutputImageTask extends AsyncTask<Void, Integer, Integer> {
        private final int mLatchCount;
        private boolean mSaveImage;

        SaveOutputImageTask(Context context, boolean SaveImage) {
            this.mLatchCount = 1;
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask init START SaveImage=" + SaveImage);
            this.mSaveImage = SaveImage;
            FreePanoramaController.this.mSaveOutputImageLatch = new CountDownLatch(1);
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask init END");
        }

        protected Integer doInBackground(Void... params) {
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask doInBackground START");
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask mMorphoImageStitcher.end() START");
            FreePanoramaController.this.checkOK(FreePanoramaController.this.mMorphoImageStitcher.end(), "mMorphoImageStitcher.end error ret:", true);
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask mMorphoImageStitcher.end() END");
            if (this.mSaveImage) {
                if (FreePanoramaController.this.saveResultImage()) {
                    CamLog.d(FaceDetector.TAG, "success saving");
                    if (FreePanoramaController.this.isStopModuleThreadFinished()) {
                        FreePanoramaController.this.doComplete(true);
                    }
                } else {
                    CamLog.d(FaceDetector.TAG, "fail saving");
                    FreePanoramaController.this.mGet.runOnUiThread(new Runnable() {
                        public void run() {
                            FreePanoramaController.this.mGet.removePostRunnable(this);
                            Toast.makeText(FreePanoramaController.this.mGet.getApplicationContext(), R.string.error_write_file, 0).show();
                        }
                    });
                    if (!FreePanoramaController.this.mGet.isPausing()) {
                        FreePanoramaController.this.mGet.doCommand(Command.RESET_FREE_PANORAMA);
                    }
                }
            }
            FreePanoramaController.this.mSaveOutputImageLatch.countDown();
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask doInBackground END");
            return null;
        }

        protected void onPreExecute() {
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask onPreExecute");
            FreePanoramaController.this.mGet.showProgressDialog();
        }

        protected void onPostExecute(Integer result) {
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask onPostExecute");
            doFinish();
        }

        protected void onCancelled() {
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask onCancelled");
            doFinish();
        }

        private void doFinish() {
            if (FreePanoramaController.this.isStopModuleThreadFinished()) {
                FreePanoramaController.this.mGet.deleteProgressDialog();
                if (!FreePanoramaController.this.mGet.isPausing()) {
                    FreePanoramaController.this.startMode();
                }
            }
        }
    }

    public GLTextureView getGLPanoramaView() {
        return this.mGLPanoramaView;
    }

    public PanoramaViewRenderer getRenderer() {
        return this.mRenderer;
    }

    public FreePanoramaSensorEventListener getSensorListener() {
        return this.mSensorListener;
    }

    public int getPanoramaState() {
        return this.mPanoramaState;
    }

    public void setPanoramaState(int state) {
        this.mPanoramaState = state;
    }

    public int getPanoramaEngineState() {
        return this.mPanoramaEngineState;
    }

    public void setPanoramaEngineState(int state) {
        this.mPanoramaEngineState = state;
    }

    public int[] getPreviewSize() {
        return this.mPreviewSize;
    }

    public int getCameraBuffID() {
        return this.mCameraBuffID;
    }

    public void setCameraBuffID(int id) {
        this.mCameraBuffID = id;
    }

    public byte[] getCameraBuff() {
        return this.mCameraBuff[this.mCameraBuffID];
    }

    public boolean getFinishFlg() {
        return this.mFinishFlg;
    }

    public MorphoImageStitcher getMorphoImageStitcher() {
        return this.mMorphoImageStitcher;
    }

    public MorphoSensorFusion getMorphoSensorFusion() {
        return this.mMorphoSensorFusion;
    }

    public void setRemoveFreePanoramaBlackBg(boolean remove) {
        this.mIsRemoveFreePanoramaBlackBg = remove;
    }

    public int getSensorFusionMode() {
        return this.mSensorFusionMode;
    }

    public FreePanoramaController(ControllerFunction function) {
        super(function);
        this.mPanoramaState = 0;
        this.mPanoramaEngineState = 0;
        this.mPreviewSize = new int[]{0, 0};
        this.mFinishFlg = false;
        this.mImageConverter = null;
        this.mMorphoImageStitcher = null;
        this.mMorphoSensorFusion = null;
        this.mStartDegree = 0;
        this.mTempParams = null;
        this.mTempLocation = null;
        this.mSaveOutputImageLatch = null;
        this.mSaveOutputImageTask = null;
        this.mPreviewCallback = null;
        this.mIsRemoveFreePanoramaBlackBg = true;
        this.mSensorFusionMode = ModelProperties.getFreePanoramaSensorMode();
        this.mDateTaken = new long[2];
        this.mStopModuleThread = null;
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case LGKeyRec.EVENT_INVALID /*0*/:
                        if (!FreePanoramaController.this.mGet.isPausing() && FreePanoramaController.this.mGet.getCameraDevice() != null && msg.obj != null) {
                            if (!FreePanoramaController.this.moveToNextStateByAttachStatus(msg.obj.mAttachStatus)) {
                            }
                        }
                    default:
                }
            }
        };
    }

    private void createModules() {
        CamLog.d(FaceDetector.TAG, "createModules start");
        if (this.mImageConverter == null) {
            this.mImageConverter = new ImageConverterJNI();
        }
        if (this.mSensorListener == null) {
            this.mSensorListener = new FreePanoramaSensorEventListener(this);
        }
        CamLog.d(FaceDetector.TAG, "mPanoramaEngineState=" + this.mPanoramaEngineState);
        if (this.mPreviewCallback == null) {
            this.mPreviewCallback = new FreePanoramaPreviewCallback(this);
        }
        if (this.mMorphoImageStitcher == null) {
            this.mMorphoImageStitcher = new MorphoImageStitcher();
        }
        if (this.mMorphoSensorFusion == null) {
            this.mMorphoSensorFusion = new MorphoSensorFusion();
        }
        CamLog.d(FaceDetector.TAG, "createModules end");
    }

    public void reInitialize() {
        CamLog.d(FaceDetector.TAG, "reInitialize");
        this.mFreePanoramaView = null;
        this.mGLPanoramaView = null;
    }

    private void initEngine() {
        CamLog.d(FaceDetector.TAG, "initEngine");
        createModules();
        this.mSensorListener.initSensorManager(this.mGet.getActivity());
        this.mMorphoSensorFusion.initialize();
        this.mMorphoSensorFusion.setOffsetMode(0);
        this.mMorphoSensorFusion.setMode(this.mSensorFusionMode);
        this.mMorphoSensorFusion.setAppState(1);
        int buff_size = (int) (((double) (this.mPreviewSize[0] * this.mPreviewSize[1])) * 1.5d);
        if (this.mRenderer == null) {
            this.mRenderer = new PanoramaViewRenderer(this.mGet.getActivity(), this.mHandler, this.mMorphoImageStitcher, buff_size);
            if (this.mSensorFusionMode == 4) {
                this.mRenderer.setInputGyroscopeType(0);
            } else {
                this.mRenderer.setInputGyroscopeType(2);
            }
            this.mRenderer.setOnRenderPreviewListener(new onRenderPreviewListener() {
                public void onRenderPreview(ResultInfo result_info) {
                    FreePanoramaController.this.mPreviewCallback.setStatus(result_info.mAttachStatus);
                    if (result_info.mImageID >= 0) {
                        FreePanoramaController.this.mGet.playFreePanoramaShutterSound();
                    } else if (result_info.mImageID >= 1) {
                        FreePanoramaController.this.setVisibleTakingGuide(false);
                        FreePanoramaController.this.showGuideText(true);
                    }
                    Message msg = Message.obtain();
                    msg.what = 0;
                    msg.obj = result_info;
                    FreePanoramaController.this.mHandler.sendMessage(msg);
                }
            });
        } else {
            this.mRenderer.setStitcher(this.mMorphoImageStitcher);
        }
        if (this.mRenderer != null) {
            this.mRenderer.setDispType(2);
        }
        if (this.mFreePanoramaView == null) {
            this.mFreePanoramaView = this.mGet.inflateStub(R.id.stub_free_panorama);
        }
        if (this.mGLPanoramaView == null) {
            this.mGLPanoramaView = (GLTextureView) this.mGet.findViewById(R.id.free_panorama_view_GL);
            if (this.mGLPanoramaView != null) {
                this.mGLPanoramaView.setRenderer(this.mRenderer);
                this.mGLPanoramaView.setRenderMode(0);
            }
        }
    }

    private void runEngine() {
        CamLog.d(FaceDetector.TAG, "runEngine");
        this.mFinishFlg = false;
        if (this.mSensorListener != null) {
            this.mSensorListener.registSensorManager();
        }
        if (this.mGet.getCameraDevice() != null) {
            if (this.mCameraBuff != null) {
                this.mGet.getCameraDevice().addCallbackBuffer(this.mCameraBuff[this.mCameraBuffID]);
            }
            if (this.mPreviewCallback != null) {
                this.mGet.getCameraDevice().setPreviewCallbackWithBuffer(this.mPreviewCallback);
            }
        }
        if (!(this.mGLPanoramaView == null || this.mGLPanoramaView.isActivated())) {
            this.mGLPanoramaView.onResume();
        }
        this.mRenderer.setRenderEnable(true);
    }

    public void startEngine(Parameters parameters) {
        CamLog.d(FaceDetector.TAG, "startEngine");
        if (!this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            CamLog.d(FaceDetector.TAG, "exit startEngine");
        } else if (!isSaveOutputImageTaskFinished()) {
            CamLog.d(FaceDetector.TAG, "exit SaveOutputImageTask is working");
        } else if (this.mGet.getPreferenceGroup() != null) {
            ListPreference shotModePref = this.mGet.getPreferenceGroup().findPreference(Setting.KEY_CAMERA_SHOT_MODE);
            if (shotModePref != null) {
                int[] previewSize = Util.SizeString2WidthHeight(shotModePref.getExtraInfo());
                this.mPreviewSize[0] = previewSize[0];
                this.mPreviewSize[1] = previewSize[1];
                this.mCameraBuffID = 0;
                CamLog.d(FaceDetector.TAG, "preview size (w,h)=" + this.mPreviewSize[0] + "," + this.mPreviewSize[1]);
                this.mCameraBuff = (byte[][]) Array.newInstance(Byte.TYPE, new int[]{2, ((this.mPreviewSize[0] * this.mPreviewSize[1]) * 3) / 2});
                initEngine();
                if (this.mMorphoImageStitcher != null) {
                    String str = FaceDetector.TAG;
                    StringBuilder append = new StringBuilder().append("free panorama engine version=");
                    MorphoImageStitcher morphoImageStitcher = this.mMorphoImageStitcher;
                    CamLog.d(str, append.append(MorphoImageStitcher.getVersion()).toString());
                }
                this.mPreviewCallback.setUseStillImage(false);
                this.mPreviewCallback.resetCount();
                this.mPreviewCallback.setAngleOfViewDegree(parameters.getHorizontalViewAngle(), parameters.getVerticalViewAngle());
                if (this.mFreePanoramaTakingGuide == null) {
                    this.mFreePanoramaTakingGuide = (RelativeLayout) this.mGet.getActivity().findViewById(R.id.free_panorama_view_guide);
                }
                if (this.mFreePanoramaStopGuide == null) {
                    this.mFreePanoramaStopGuide = (RelativeLayout) this.mGet.getActivity().findViewById(R.id.guide_text_layout);
                }
                if (this.mAnimationTakingGuideShow == null) {
                    this.mAnimationTakingGuideShow = AnimationUtils.loadAnimation(this.mGet.getApplicationContext(), R.anim.free_panorama_arrow_show);
                }
                if (this.mAnimationTakingGuideHide == null) {
                    this.mAnimationTakingGuideHide = AnimationUtils.loadAnimation(this.mGet.getApplicationContext(), R.anim.free_panorama_arrow_hide);
                }
            }
        }
    }

    private void stopModules() {
        CamLog.d(FaceDetector.TAG, "stopModules START");
        if (this.mRenderer != null) {
            this.mRenderer.setRenderEnable(false);
        }
        if (this.mMorphoImageStitcher != null) {
            if (this.mPanoramaEngineState == 1) {
                this.mPanoramaEngineState = 4;
            }
            if (this.mMorphoImageStitcher.isInitialized() && !this.mMorphoImageStitcher.isFinished()) {
                CamLog.d(FaceDetector.TAG, "mMorphoImageStitcher.finish() START");
                this.mMorphoImageStitcher.finish();
                CamLog.d(FaceDetector.TAG, "mMorphoImageStitcher.finish() END");
            }
            this.mMorphoImageStitcher = null;
            this.mPanoramaEngineState = 0;
        }
        if (this.mMorphoSensorFusion != null) {
            this.mMorphoSensorFusion.finish();
            this.mMorphoSensorFusion = null;
        }
        if (this.mGLPanoramaView != null) {
            this.mGLPanoramaView.onPause();
        }
        if (this.mSensorListener != null) {
            this.mSensorListener.unRegistSensorManager();
        }
        this.mPanoramaState = 0;
        this.mFinishFlg = true;
        this.mFreePanoramaTakingGuide = null;
        this.mFreePanoramaStopGuide = null;
        this.mAnimationTakingGuideShow = null;
        this.mAnimationTakingGuideHide = null;
        CamLog.d(FaceDetector.TAG, "stopModules END");
    }

    public void stopEngine(Parameters parameters) {
        CamLog.d(FaceDetector.TAG, "stopEngine");
        if (!(this.mGet.isPausing() || this.mGet.getCameraDevice() == null)) {
            this.mGet.getCameraDevice().addCallbackBuffer(null);
            this.mGet.getCameraDevice().setPreviewCallbackWithBuffer(null);
        }
        if (isStopModuleThreadFinished()) {
            this.mStopModuleThread = createStopModuleThread();
            this.mStopModuleThread.start();
        }
    }

    private Thread createStopModuleThread() {
        return new Thread(new Runnable() {
            public void run() {
                CamLog.d(FaceDetector.TAG, "mStopModuleThread START");
                if (FreePanoramaController.this.isSaveOutputImageTaskFinished() && FreePanoramaController.this.mGet.getCommandManager() != null) {
                    FreePanoramaController.this.mGet.doCommandUi(Command.SHOW_PROGRESS_DIALOG);
                }
                try {
                    if (FreePanoramaController.this.mSaveOutputImageLatch != null) {
                        FreePanoramaController.this.mSaveOutputImageLatch.await();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                FreePanoramaController.this.stopModules();
                if (FreePanoramaController.this.mGet.getCommandManager() != null) {
                    FreePanoramaController.this.mGet.removeScheduledCommand(Command.SHOW_PROGRESS_DIALOG);
                    FreePanoramaController.this.mGet.doCommandUi(Command.DELETE_PROGRESS_DIALOG);
                }
                if (!FreePanoramaController.this.mGet.isPausing()) {
                    FreePanoramaController.this.startMode();
                    FreePanoramaController.this.doComplete(false);
                }
                CamLog.d(FaceDetector.TAG, "mStopModuleThread END");
            }
        });
    }

    public boolean isPanoramaUIShown() {
        if (this.mFreePanoramaView != null && this.mFreePanoramaView.getVisibility() == 0) {
            return true;
        }
        return false;
    }

    public void showPanoramaView() {
        CamLog.d(FaceDetector.TAG, "showPanoramaView");
        if (!this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            CamLog.d(FaceDetector.TAG, "exit showPanoramaView because not free panorama mode");
        } else if (isSaveOutputImageTaskFinished()) {
            if (this.mFreePanoramaView != null) {
                this.mFreePanoramaView.setVisibility(0);
            }
            if (this.mGLPanoramaView != null) {
                this.mGLPanoramaView.setVisibility(0);
            }
            runEngine();
            updateScreenRotation();
            this.mGet.setMainBarAlpha(30);
            this.mGet.doCommandUi(Command.ROTATE);
            ShutterButton mShutterButton = (ShutterButton) this.mGet.findViewById(R.id.main_button_bg);
            if (mShutterButton != null) {
                this.mGet.setShutterButtonImage(mShutterButton.isEnabled(), this.mGet.getOrientationDegree());
            }
        } else {
            CamLog.d(FaceDetector.TAG, "exit SaveOutputImageTask is working");
        }
    }

    private void updateSensorFusionRotation(int orientation) {
        if (this.mMorphoSensorFusion != null) {
            int rotation;
            switch (orientation) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    rotation = 0;
                    break;
                case MediaProviderUtils.ROTATION_90 /*90*/:
                    rotation = 1;
                    break;
                case MediaProviderUtils.ROTATION_180 /*180*/:
                    rotation = 2;
                    break;
                case Tag.IMAGE_DESCRIPTION /*270*/:
                    rotation = 3;
                    break;
                default:
                    rotation = 0;
                    break;
            }
            this.mMorphoSensorFusion.setRotation(rotation);
        }
    }

    private void updateScreenRotation() {
        int degrees;
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(0, info);
        this.mSensorListener.setCameraOrientation(info.orientation);
        updateSensorFusionRotation(info.orientation);
        switch (this.mGet.getActivity().getWindowManager().getDefaultDisplay().getRotation()) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                degrees = 0;
                break;
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                degrees = 90;
                break;
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                degrees = MediaProviderUtils.ROTATION_180;
                break;
            case LGKeyRec.EVENT_STARTED /*3*/:
                degrees = Tag.IMAGE_DESCRIPTION;
                break;
            default:
                degrees = 0;
                break;
        }
        int preview_rotation = ((degrees - this.mSensorListener.getCameraOrientation()) + CameraConstants.DEGREE_360) % CameraConstants.DEGREE_360;
        CamLog.d(FaceDetector.TAG, "camera:" + this.mSensorListener.getCameraOrientation() + " disp:" + degrees + " preview:" + preview_rotation);
        this.mRenderer.setPreviewRotation(preview_rotation);
    }

    public void removePanoramaView() {
        CamLog.d(FaceDetector.TAG, "removePanoramaView");
        if (this.mGLPanoramaView != null) {
            this.mGLPanoramaView.setVisibility(8);
        }
        if (this.mFreePanoramaView != null) {
            this.mFreePanoramaView.setVisibility(8);
        }
        if (this.mIsRemoveFreePanoramaBlackBg) {
            this.mGet.removeFreePanoramaBlackBg();
        } else {
            this.mIsRemoveFreePanoramaBlackBg = true;
        }
        if (this.mFreePanoramaStopGuide != null) {
            this.mFreePanoramaStopGuide.setVisibility(8);
        }
    }

    public void startPanorama() {
        CamLog.d(FaceDetector.TAG, "startPanorama()");
        if (this.mPanoramaEngineState != 1) {
            CamLog.d(FaceDetector.TAG, "fail startPanorama() mPanoramaEngineState=" + this.mPanoramaEngineState);
            return;
        }
        this.mGet.setLockChangeConfiguration(true);
        this.mPanoramaState = 1;
        this.mPreviewCallback.resetCount();
        this.mStartDegree = 0;
        this.mGet.setQuickFunctionControllerVisible(false);
        this.mGet.setQuickButtonVisible(100, 8, false);
        this.mGet.setSwitcherVisible(false);
        if (ModelProperties.is3dSupportedModel()) {
            this.mGet.set3DSwitchVisible(false);
        }
        this.mGet.setThumbnailButtonVisibility(8);
        this.mGet.hideFocus();
        this.mGet.hideOptionMenu();
        this.mGet.setKeepScreenOn();
        this.mGet.playRecordingSound(true);
        this.mGet.getHandler().sendEmptyMessageDelayed(1, 500);
        this.mGet.getHandler().sendEmptyMessageDelayed(3, 500);
        this.mSensorListener.setWaitTime(0);
        setSensorCorrectionGuideCounter(0);
        setVisibleSensorCorrectionGuide(true);
        this.mPanoramaState = 2;
        this.mMorphoSensorFusion.setAppState(0);
        this.mGet.setShutterButtonImage(false, this.mGet.getOrientationDegree());
        setVisibleResetButton(true);
    }

    public void stopPanorama() {
        CamLog.d(FaceDetector.TAG, String.format("stopPanorama() mPanoramaState = " + this.mPanoramaState, new Object[0]));
        if (this.mPanoramaState == 2) {
            restartToStartupPreview();
            return;
        }
        this.mGet.setSubButton(2, 0);
        if (this.mPanoramaState == 3 && this.mPanoramaEngineState == 3) {
            boolean isNeedSaving;
            if (this.mGet.isPausing()) {
                isNeedSaving = false;
            } else {
                isNeedSaving = true;
            }
            this.mPanoramaState = 4;
            this.mPanoramaEngineState = 4;
            this.mFinishFlg = true;
            setVisibleTakingGuide(false);
            showGuideText(false);
            this.mGet.showProgressDialog();
            this.mGet.setMainButtonDisable();
            this.mGet.clearFocusState();
            this.mGet.hideFocus();
            this.mGet.keepScreenOnAwhile();
            this.mGet.setInCaptureProgress(false);
            if (isNeedSaving) {
                this.mGet.playRecordingSound(false);
            }
            this.mGet.showQuickFunctionController();
            this.mGet.setQuickButtonVisible(100, 0, false);
            setCafSetting();
            this.mSaveOutputImageTask = new SaveOutputImageTask(this.mGet.getApplicationContext(), isNeedSaving);
            if (isNeedSaving) {
                this.mTempParams = this.mGet.getParameters();
                this.mTempLocation = this.mGet.getCurrentLocation();
            }
            this.mSaveOutputImageTask.execute(new Void[0]);
            return;
        }
        CamLog.d(FaceDetector.TAG, String.format("stopPanorama() do nothing; not started", new Object[0]));
    }

    private void doComplete(final boolean needRestart) {
        CamLog.d(FaceDetector.TAG, "doComplete START");
        if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            this.mPanoramaState = 0;
            if (!this.mGet.isPausing()) {
                if (this.mGet.checkAutoReviewOff(false)) {
                    this.mGet.doCommandUi(Command.ON_DELAY_OFF);
                    if (needRestart) {
                        restartPanorama();
                    }
                } else {
                    this.mGet.runOnUiThread(new Runnable() {
                        public void run() {
                            if (FreePanoramaController.this.mGet != null) {
                                FreePanoramaController.this.mGet.removePostRunnable(this);
                                if (!FreePanoramaController.this.mGet.checkAutoReviewForQuickView()) {
                                    FreePanoramaController.this.mGet.stopPreview();
                                    FreePanoramaController.this.mGet.doCommandUi(Command.DISPLAY_CAMERA_POSTVIEW);
                                } else if (needRestart) {
                                    FreePanoramaController.this.restartPanorama();
                                }
                            }
                        }
                    });
                }
            }
            setVoiceShutterSetting();
            this.mGet.setLockChangeConfiguration(false);
            CamLog.d(FaceDetector.TAG, "doComplete END");
            return;
        }
        CamLog.i(FaceDetector.TAG, "exit doComplete because not free panorama");
    }

    public void setVisibleSensorCorrectionGuide(boolean isVisible) {
        View view = this.mGet.findViewById(R.id.free_panorama_sensor_correction_guide_layout);
        if (view == null) {
            return;
        }
        if (isVisible) {
            view.setVisibility(0);
        } else {
            view.setVisibility(8);
        }
    }

    public void setSensorCorrectionGuideCounter(int num) {
        ImageView counter_view = (ImageView) this.mGet.findViewById(R.id.free_panorama_sensor_correction_guide_view);
        if (counter_view != null) {
            switch (num) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    counter_view.setImageResource(R.drawable.panorama_ready_03);
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    counter_view.setImageResource(R.drawable.panorama_ready_02);
                case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                    counter_view.setImageResource(R.drawable.panorama_ready_01);
                default:
                    counter_view.setImageDrawable(null);
            }
        }
    }

    public void setVisibleTakingGuide(boolean isVisible) {
        if (isVisible) {
            this.mSensorListener.setWaitTime(0);
            setSensorCorrectionGuideCounter(0);
            if (this.mFreePanoramaTakingGuide != null) {
                this.mFreePanoramaTakingGuide.startAnimation(this.mAnimationTakingGuideShow);
                this.mFreePanoramaTakingGuide.setVisibility(0);
                this.mGet.doCommandDelayed(Command.HIDE_FREE_PANORAMA_GUIDE, CameraConstants.FREE_PANO_TIME_REMOVE_GUIDE);
            }
            showGuideText(true);
        } else if (this.mFreePanoramaTakingGuide != null && this.mFreePanoramaTakingGuide.getVisibility() == 0) {
            this.mFreePanoramaTakingGuide.startAnimation(this.mAnimationTakingGuideHide);
            this.mFreePanoramaTakingGuide.setVisibility(8);
        }
    }

    private void showGuideText(final boolean isVisible) {
        if (checkMediator()) {
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    FreePanoramaController.this.mGet.removePostRunnable(this);
                    if (FreePanoramaController.this.mFreePanoramaStopGuide != null) {
                        if (isVisible) {
                            FreePanoramaController.this.startRotateGuideText(FreePanoramaController.this.mGet.getOrientationDegree());
                        }
                        FreePanoramaController.this.mFreePanoramaStopGuide.setVisibility(isVisible ? 0 : 8);
                    }
                }
            });
        }
    }

    private void startRotateGuideText(int degree) {
        if (this.mFreePanoramaStopGuide != null) {
            int audioZoomGuideStringMarginBottom = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.free_panorama_guide_string_marginBottom);
            int previewPanelWidth = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_panel_width);
            int previewPanelMarginBottom = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_panel_marginBottom);
            int guideSideMargin = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.free_panorama_guide_string_marginLeft);
            int indicatorHeight = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.layout_preview_indicators_height);
            int[] previewSizeOnScreen = new int[]{0, 0};
            String sizeOnScreenString = this.mGet.getPreviewSizeOnScreen();
            if (sizeOnScreenString != null) {
                previewSizeOnScreen = Util.SizeString2WidthHeight(sizeOnScreenString);
            }
            LayoutParams lp = (LayoutParams) this.mFreePanoramaStopGuide.getLayoutParams();
            Common.resetLayoutParameter(lp);
            this.mFreePanoramaStopGuide.setLayoutDirection(0);
            lp.topMargin = 0;
            lp.leftMargin = 0;
            lp.rightMargin = 0;
            lp.bottomMargin = 0;
            RelativeLayout textInnerLayout = (RelativeLayout) this.mFreePanoramaStopGuide.findViewById(R.id.guide_text_inner_layout);
            LayoutParams lpInnerLayout = (LayoutParams) textInnerLayout.getLayoutParams();
            Common.resetLayoutParameter(lpInnerLayout);
            textInnerLayout.setLayoutDirection(0);
            lpInnerLayout.width = -2;
            textInnerLayout.setGravity(17);
            if (Util.isEqualDegree(this.mGet.getResources(), degree, 0) || Util.isEqualDegree(this.mGet.getResources(), degree, MediaProviderUtils.ROTATION_180)) {
                lp.addRule(20, 1);
                lp.addRule(12, 1);
                lp.leftMargin = audioZoomGuideStringMarginBottom;
                lp.topMargin = guideSideMargin;
                lp.bottomMargin = (guideSideMargin + previewPanelWidth) + previewPanelMarginBottom;
                lpInnerLayout.width = previewSizeOnScreen[0] - (((guideSideMargin + previewPanelWidth) + previewPanelMarginBottom) * 2);
            } else if (Util.isEqualDegree(this.mGet.getResources(), degree, 90) || Util.isEqualDegree(this.mGet.getResources(), degree, Tag.IMAGE_DESCRIPTION)) {
                lp.addRule(12, 1);
                lp.addRule(14, 1);
                lp.leftMargin = guideSideMargin;
                lp.rightMargin = indicatorHeight;
                lp.bottomMargin = (audioZoomGuideStringMarginBottom + previewPanelWidth) + previewPanelMarginBottom;
                lpInnerLayout.width = previewSizeOnScreen[1] - ((guideSideMargin + indicatorHeight) * 2);
            }
            ((RotateLayout) this.mFreePanoramaStopGuide.findViewById(R.id.guide_text_rotate_layout)).setAngle(degree);
            textInnerLayout.setLayoutParams(lpInnerLayout);
            this.mFreePanoramaStopGuide.setLayoutParams(lp);
        }
    }

    public void startRotation(int degree, boolean animation) {
        startRotateGuideText(degree);
    }

    public void setVisibleResetButton(boolean isVisible) {
        if (isVisible) {
            this.mGet.setSubButton(2, R.drawable.selector_sub_btn_reset_free_panorama);
            this.mGet.findViewById(R.id.sub_button3).setEnabled(true);
            this.mGet.findViewById(R.id.sub_touch_button3).setEnabled(true);
            return;
        }
        this.mGet.setSubButton(2, 0);
    }

    public void restartToStartupPreview() {
        if (this.mPanoramaState >= 2 || this.mPanoramaState <= 4) {
            this.mHandler.removeMessages(0);
            this.mRenderer.setRenderEnable(false);
            setVisibleResetButton(false);
            setVisibleTakingGuide(false);
            showGuideText(false);
            this.mGet.setSwitcherVisible(true);
            if (ModelProperties.is3dSupportedModel()) {
                this.mGet.set3DSwitchVisible(true);
            }
            this.mGet.setThumbnailButtonVisibility(0);
            this.mGet.enableCommand(true);
            this.mPanoramaEngineState = 1;
            checkOK(this.mMorphoImageStitcher.releaseRegisteredImage(), "mMorphoImageStitcher.releaseAllInputImage error int panorama_restart_button onclicked ret:", true);
            checkOK(this.mMorphoImageStitcher.end(), "mMorphoImageStitcher.end error int panorama_restart_button onclicked ret:", true);
            this.mPreviewCallback.setUseImage(0);
            checkOK(this.mMorphoImageStitcher.start(1), "mMorphoImageStitcher.start(1) error int panorama_restart_button onclicked ret:", true);
            this.mRenderer.setRenderEnable(true);
            setVisibleSensorCorrectionGuide(false);
            this.mGet.setInCaptureProgress(false);
            setCafSetting();
            setVoiceShutterSetting();
            this.mPanoramaState = 0;
            this.mGet.setShutterButtonImage(true, this.mGet.getOrientationDegree());
            this.mGet.showQuickFunctionController();
            this.mGet.quickFunctionControllerRefresh(true);
            this.mGet.setQuickButtonVisible(100, 0, false);
            this.mGet.setQuickButtonMenuEnable(true, false);
            this.mGet.setLockChangeConfiguration(false);
            if (!this.mGet.isPausing()) {
                runEngine();
                return;
            }
            return;
        }
        CamLog.d(FaceDetector.TAG, String.format("stopPanorama() do nothing; not started", new Object[0]));
    }

    private void restartPanorama() {
        this.mHandler.removeMessages(0);
        this.mRenderer.setRenderEnable(false);
        checkOK(this.mMorphoImageStitcher.releaseRegisteredImage(), "mMorphoImageStitcher.releaseAllInputImage error int panorama_restart_button onclicked ret:", true);
        checkOK(this.mMorphoImageStitcher.end(), "mMorphoImageStitcher.end error int panorama_restart_button onclicked ret:", true);
        checkOK(this.mMorphoImageStitcher.start(1), "mMorphoImageStitcher.start(1) error int panorama_restart_button onclicked ret:", true);
        this.mPanoramaEngineState = 1;
        runEngine();
    }

    public void onPause() {
        CamLog.d(FaceDetector.TAG, "Panorama Controller onPause - start");
        if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            this.mFinishFlg = true;
            stopPanorama();
            CamLog.d(FaceDetector.TAG, "getImageListUri().clear() call");
            this.mGet.getImageListUri().clear();
            removePanoramaView();
            setVisibleSensorCorrectionGuide(false);
            Parameters parameters = null;
            if (this.mGet.getCameraDevice() != null) {
                parameters = this.mGet.getParameters();
            }
            stopEngine(parameters);
            CamLog.d(FaceDetector.TAG, "Panorama Controller onPause -end");
            return;
        }
        CamLog.i(FaceDetector.TAG, "Pano Panorama Controller onPause - return");
    }

    public void onDestroy() {
        this.mImageConverter = null;
        this.mFreePanoramaView = null;
        this.mFreePanoramaTakingGuide = null;
        this.mFreePanoramaStopGuide = null;
        this.mGLPanoramaView = null;
        this.mHandler = null;
        this.mRenderer = null;
        this.mSensorListener = null;
        if (this.mPreviewCallback != null) {
            this.mPreviewCallback.unbind();
            this.mPreviewCallback = null;
        }
        if (this.mCameraBuff != null) {
            this.mCameraBuff = (byte[][]) null;
        }
        super.onDestroy();
    }

    private int saveOutputImage(String directory, String filename, long[] dateTaken, Rect rect, int orientation) {
        CamLog.d(FaceDetector.TAG, "orientation=" + orientation);
        int[] output_size = new int[1];
        String first_date = createAppSegDateString(dateTaken[0]);
        String last_date = createAppSegDateString(dateTaken[1]);
        int ret = Error.ERROR_STATE;
        if (this.mMorphoImageStitcher != null) {
            ret = this.mMorphoImageStitcher.saveOutputJpeg(directory + "/" + filename, rect, orientation, output_size, first_date, last_date, true);
        }
        if (ret != 0) {
            CamLog.e(FaceDetector.TAG, "mMorphoImageStitcher.saveOutputJpeg error ret:" + ret);
        } else {
            CamLog.d(FaceDetector.TAG, "saveOutputImage=" + ret);
        }
        return ret;
    }

    private static String createAppSegDateString(long dateTaken) {
        Date date = new Date(dateTaken);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    private boolean moveToNextStateByAttachStatus(int attach_status) {
        switch (attach_status) {
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
            case Ola_ShotParam.ImageEffect_Solarize /*12*/:
                stopPanorama();
                return true;
            default:
                return false;
        }
    }

    private boolean saveResultImage() {
        Rect bounding_rect = new Rect();
        Rect clipping_rect = new Rect();
        int height;
        if (checkOK(this.mMorphoImageStitcher.getClippingRect(clipping_rect), "mMorphoImageStitcher.getClippingRect error ret:", false)) {
            height = clipping_rect.bottom - clipping_rect.top;
            CamLog.d(FaceDetector.TAG, "OutImageSize[clipping]: w=" + (clipping_rect.right - clipping_rect.left) + " h=" + height);
        } else {
            checkOK(this.mMorphoImageStitcher.getBoundingRect(bounding_rect), "mMorphoImageStitcher.getBoundingRect error ret:", true);
            height = bounding_rect.bottom - bounding_rect.top;
            CamLog.d(FaceDetector.TAG, "OutImageSize[bounding]: w=" + (bounding_rect.right - bounding_rect.left) + " h=" + height);
        }
        long dateTaken = System.currentTimeMillis();
        FileNamer.get().markTakeTime(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), this.mGet.getSettingValue(Setting.KEY_SCENE_MODE));
        String fileName = FileNamer.get().getFileNewName(this.mGet.getApplicationContext(), this.mGet.getApplicationMode(), this.mGet.getCurrentStorage(), this.mGet.getCurrentStorageDirectory(), true, this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), null);
        String directory = this.mGet.getCurrentStorageDirectory();
        String bounding_file_name_with_extension = fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
        String output_img_path = directory + bounding_file_name_with_extension;
        Rect rect = new Rect();
        rect = clipping_rect;
        this.mDateTaken[1] = System.currentTimeMillis();
        if (saveOutputImage(directory, bounding_file_name_with_extension, this.mDateTaken, rect, 1) == 0) {
            addImage(fileName, dateTaken, directory, output_img_path, this.mStartDegree, rect);
            CamLog.d(FaceDetector.TAG, "The original free panorama image is saved.");
            this.mGet.setLastThumb(this.mGet.getSavedImageUri(), true);
            this.mGet.updateThumbnailButton();
            return true;
        }
        CamLog.d(FaceDetector.TAG, "Cannot save original free panorama image.");
        return false;
    }

    private void addImage(String fileName, long dateTaken, String directory, String output_img_path, int degree, Rect imageRect) {
        if (output_img_path != null) {
            ExifUtil.setExif(output_img_path, this.mTempParams.getFlashMode(), this.mTempParams.getFocalLength(), this.mTempLocation, imageRect.width(), imageRect.height(), this.mTempParams.get(LGT_Limit.ISP_ISO), degree, this.mTempParams.getWhiteBalance());
        }
        Uri imageUri = this.mGet.getImageHandler(false).addImage(this.mGet.getActivity().getContentResolver(), fileName, dateTaken, this.mTempLocation, directory, fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT, degree, false);
        this.mGet.setSavedFileName(fileName);
        this.mGet.setSavedImageUri(imageUri);
        Util.broadcastNewPicture(this.mGet.getActivity(), imageUri);
        if (Common.useSecureLockImage() || AppControlUtil.checkGuestModeAndAppDisabled(this.mGet.getContentResolver(), true, 1)) {
            SecureImageUtil.get().addSecureLockImageUri(imageUri);
        }
        Util.requestUpBoxBackupPhoto(this.mGet.getActivity(), output_img_path, this.mGet.getSettingValue(Setting.KEY_UPLUS_BOX).equals(CameraConstants.SMART_MODE_ON));
        this.mGet.getImageListUri().add(imageUri);
    }

    public int[] getResultSize() {
        int[] size = new int[]{7542, 3104};
        if (this.mPreviewSize[0] == 1280 && this.mPreviewSize[1] == 960) {
            size[0] = 7542;
            size[1] = 3104;
        } else if (this.mPreviewSize[0] == Ola_ImageFormat.YUVPLANAR_LABEL && this.mPreviewSize[1] == 768) {
            size[0] = 5824;
            size[1] = 2944;
        } else if (this.mPreviewSize[0] == 960 && this.mPreviewSize[1] == 720) {
            size[0] = 3040;
            size[1] = 2848;
        } else if (this.mPreviewSize[0] == LGT_Limit.PREVIEW_SIZE_WIDTH && this.mPreviewSize[1] == LGT_Limit.PREVIEW_SIZE_HEIGHT) {
            size[0] = 2016;
            size[1] = 1920;
        } else if (this.mPreviewSize[0] == ThumbNailSize.width && this.mPreviewSize[1] == Ola_ShotParam.Sampler_Complete) {
            size[0] = 1056;
            size[1] = 928;
        }
        return size;
    }

    private void setCafSetting() {
        if (FunctionProperties.isCafSupported(this.mGet.getApplicationMode(), this.mGet.getCameraId()) && this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA) && this.mGet.getCameraDevice() != null) {
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

    private void setVoiceShutterSetting() {
        if (FunctionProperties.isVoiceShutter()) {
            String mVoiceShutterValue = this.mGet.getSettingValue(Setting.KEY_VOICESHUTTER);
            if (mVoiceShutterValue != null && mVoiceShutterValue.equals(CameraConstants.SMART_MODE_ON)) {
                this.mGet.doCommandUi(Command.SET_VOICE_SHUTTER);
            }
        }
    }

    public boolean checkOK(int ret, String errorLog, boolean isFinish) {
        if (ret == 0) {
            return true;
        }
        CamLog.d(FaceDetector.TAG, errorLog + "(" + ret + ")");
        if (isFinish) {
            Toast.makeText(this.mGet.getApplicationContext(), R.string.camera_error_occurred_try_again, 1).show();
            this.mGet.getActivity().finish();
        }
        return false;
    }

    public Camera getCameraDevice() {
        return this.mGet.getCameraDevice();
    }

    public void setShutterButtonImage(boolean b) {
        this.mGet.setShutterButtonImage(b, this.mGet.getOrientationDegree());
    }

    public Activity getActivity() {
        return this.mGet.getActivity();
    }

    private void startMode() {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                FreePanoramaController.this.mGet.removePostRunnable(this);
                if (FreePanoramaController.this.mGet.getCameraDevice() != null && FreePanoramaController.this.mGet.getLGParam() != null) {
                    FreePanoramaController.this.startEngine(FreePanoramaController.this.mGet.getParameters());
                    FreePanoramaController.this.showPanoramaView();
                    FreePanoramaController.this.mGet.enableInput(true);
                }
            }
        });
    }

    private boolean isStopModuleThreadFinished() {
        if (this.mStopModuleThread == null || this.mStopModuleThread == null || !this.mStopModuleThread.isAlive()) {
            return true;
        }
        return false;
    }

    private boolean isSaveOutputImageTaskFinished() {
        if (this.mSaveOutputImageTask == null) {
            return true;
        }
        if (this.mSaveOutputImageTask == null || this.mSaveOutputImageTask.getStatus() != Status.FINISHED) {
            return false;
        }
        return true;
    }

    public void onResume() {
        if (!(isStopModuleThreadFinished() && isSaveOutputImageTaskFinished())) {
            CamLog.d(FaceDetector.TAG, "show progress mStopModuleThread.isAlive()=" + this.mStopModuleThread.isAlive() + " isSaveOutputImageTaskFinished=" + isSaveOutputImageTaskFinished());
            this.mGet.showProgressDialog();
        }
        super.onResume();
    }
}
