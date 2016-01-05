package com.lge.camera.controller.camera;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.listeners.PlanePanoramaCallback;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.ExifUtil;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.SecureImageUtil;
import com.lge.camera.util.Util;
import com.lge.morpho.core.MorphoPanoramaGP;
import com.lge.morpho.core.MorphoPanoramaGP.InitParam;
import com.lge.morpho.utils.NativeMemoryAllocator;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.morpho.utils.multimedia.StillImageData;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class PlanePanoramaController extends PlanePanoramaControllerBase {
    private static final int STILL_PROC_TASK_DELAY_TIME = 100;
    private SaveOutputImageTask mSaveOutputImageTask;
    private CountDownLatch mSaveOutputLatch;
    private long mSaveTimeStart;
    private ArrayList<StillImageData> mStillProcList;
    private StillProcTask mStillProcTask;
    private WaitSaveOutputThread mWaitSaveOutputThread;

    private class SaveOutputImageTask extends AsyncTask<Void, Integer, Integer> {
        private final int LATCH_COUNT;
        String bounding_file_name_with_extension;
        String directory;
        String fileName;
        boolean mSaveImage;
        String output_img_path;

        SaveOutputImageTask(Context context, boolean SaveImage) {
            this.LATCH_COUNT = 1;
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask init SaveImage=" + SaveImage);
            this.mSaveImage = SaveImage;
        }

        protected Integer doInBackground(Void... params) {
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask doInBackground START");
            PlanePanoramaController.this.finishAttachStillImageTask();
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask mPanoramaGP.end() START");
            PlanePanoramaController.this.mSaveOutputLatch = new CountDownLatch(1);
            if (PlanePanoramaController.this.mWaitSaveOutputThread == null) {
                PlanePanoramaController.this.mWaitSaveOutputThread = new WaitSaveOutputThread();
                PlanePanoramaController.this.mWaitSaveOutputThread.start();
                CamLog.d(FaceDetector.TAG, "mWaitSaveOutputTask started");
            }
            PlanePanoramaController.this.checkOK(PlanePanoramaController.this.mPanoramaGP.end(), "end() -> ", false);
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask mPanoramaGP.end() END");
            if (this.mSaveImage) {
                FileNamer.get().markTakeTime(PlanePanoramaController.this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), PlanePanoramaController.this.mGet.getSettingValue(Setting.KEY_SCENE_MODE));
                this.fileName = FileNamer.get().getFileNewName(PlanePanoramaController.this.mGet.getApplicationContext(), PlanePanoramaController.this.mGet.getApplicationMode(), PlanePanoramaController.this.mGet.getCurrentStorage(), PlanePanoramaController.this.mGet.getCurrentStorageDirectory(), true, PlanePanoramaController.this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), false);
                this.directory = PlanePanoramaController.this.mGet.getCurrentStorageDirectory();
                this.bounding_file_name_with_extension = this.fileName + CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
                this.output_img_path = this.directory + this.bounding_file_name_with_extension;
                Rect c_rect = new Rect();
                PlanePanoramaController.this.checkOK(PlanePanoramaController.this.mPanoramaGP.getClippingRect(c_rect), "getClippingRect() -> ", false);
                CamLog.d(FaceDetector.TAG, "save.end() END");
                saveOutputJpeg(this.output_img_path, c_rect);
            }
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask doInBackground END");
            if (PlanePanoramaController.this.mPanoramaGP != null) {
                PlanePanoramaController.this.mPanoramaGP.finish();
                PlanePanoramaController.this.mPanoramaGP = null;
            }
            PlanePanoramaController.this.mIsShooting = false;
            PlanePanoramaController.this.mSaveOutputLatch.countDown();
            return null;
        }

        protected void onPreExecute() {
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask onPreExecute");
            PlanePanoramaController.this.mGet.showProgressDialog();
            PlanePanoramaController.this.mSaveTimeStart = System.currentTimeMillis();
        }

        protected void onPostExecute(Integer result) {
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask onPostExecute");
            PlanePanoramaController.this.mGet.deleteProgressDialog();
            if (!PlanePanoramaController.this.mGet.isPausing()) {
                if (PlanePanoramaController.this.mGet.checkAutoReviewOff(false)) {
                    PlanePanoramaController.this.mGet.doCommandUi(Command.ON_DELAY_OFF);
                    PlanePanoramaController.this.resetToPreviewState();
                    return;
                }
                PlanePanoramaController.this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        if (PlanePanoramaController.this.mGet != null) {
                            PlanePanoramaController.this.mGet.removePostRunnable(this);
                            if (PlanePanoramaController.this.mGet.checkAutoReviewForQuickView()) {
                                PlanePanoramaController.this.resetToPreviewState();
                                return;
                            }
                            PlanePanoramaController.this.mGet.stopPreview();
                            PlanePanoramaController.this.mGet.doCommandUi(Command.DISPLAY_CAMERA_POSTVIEW);
                        }
                    }
                });
            }
        }

        protected void onCancelled() {
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask onCancelled");
            PlanePanoramaController.this.mIsShooting = false;
            if (PlanePanoramaController.this.mSaveOutputLatch != null) {
                PlanePanoramaController.this.mSaveOutputLatch.countDown();
                PlanePanoramaController.this.mSaveOutputLatch = null;
            }
            if (PlanePanoramaController.this.mPanoramaGP != null) {
                PlanePanoramaController.this.mPanoramaGP = null;
            }
            PlanePanoramaController.this.mGet.deleteProgressDialog();
            PlanePanoramaController.this.resetToPreviewState();
        }

        private void saveOutputJpeg(String path, Rect rect) {
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask saveOutputJpeg");
            if (PlanePanoramaController.this.mPanoramaGP.saveOutputJpeg(path, rect, 1) == 0) {
                addImageAsApplication(path, 0, rect);
                return;
            }
            CamLog.e(FaceDetector.TAG, String.format("saveOutputJpeg() -> 0x%x", new Object[]{Integer.valueOf(ret)}));
        }

        private void addImageAsApplication(String file_path, int degree, Rect rect) {
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask addImageAsApplicationo");
            addImage(this.fileName, System.currentTimeMillis(), this.directory, this.output_img_path, degree, rect);
            CamLog.d(FaceDetector.TAG, "The original plane panorama image is saved.");
            PlanePanoramaController.this.mGet.setLastThumb(PlanePanoramaController.this.mGet.getSavedImageUri(), true);
            PlanePanoramaController.this.mGet.updateThumbnailButton();
            PlanePanoramaController.this.mGet.checkStorage(false);
        }

        private void addImage(String file_name, long dateTaken, String directory, String output_img_path, int degree, Rect imageRect) {
            CamLog.d(FaceDetector.TAG, "SaveOutputImageTask addImage");
            if (output_img_path != null) {
                ExifUtil.setExif(output_img_path, PlanePanoramaController.this.mTempParams.getFlashMode(), PlanePanoramaController.this.mTempParams.getFocalLength(), PlanePanoramaController.this.mTempLocation, imageRect.width(), imageRect.height(), PlanePanoramaController.this.mTempParams.get(LGT_Limit.ISP_ISO), degree, PlanePanoramaController.this.mTempParams.getWhiteBalance());
            }
            Uri imageUri = PlanePanoramaController.this.mGet.getImageHandler(false).addImage(PlanePanoramaController.this.mGet.getActivity().getContentResolver(), file_name, dateTaken, PlanePanoramaController.this.mTempLocation, directory, file_name + CameraConstants.TIME_MACHINE_TEMPFILE_EXT, degree, false);
            CamLog.d(FaceDetector.TAG, "imageUri=" + imageUri);
            PlanePanoramaController.this.mGet.setSavedFileName(file_name);
            PlanePanoramaController.this.mGet.setSavedImageUri(imageUri);
            Util.broadcastNewPicture(PlanePanoramaController.this.mGet.getActivity(), imageUri);
            if (Common.useSecureLockImage() || AppControlUtil.checkGuestModeAndAppDisabled(PlanePanoramaController.this.mGet.getContentResolver(), true, 1)) {
                SecureImageUtil.get().addSecureLockImageUri(imageUri);
            }
            Util.requestUpBoxBackupPhoto(PlanePanoramaController.this.mGet.getActivity(), output_img_path, PlanePanoramaController.this.mGet.getSettingValue(Setting.KEY_UPLUS_BOX).equals(CameraConstants.SMART_MODE_ON));
            PlanePanoramaController.this.mGet.getImageListUri().add(imageUri);
        }
    }

    public class StillProcTask extends Thread {
        private int shootCount;

        public StillProcTask() {
            this.shootCount = 0;
        }

        public void run() {
            while (PlanePanoramaController.this.mIsShooting) {
                sendImageToEngine();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            CamLog.d(FaceDetector.TAG, "remove un-processed picture : mCntReqShoot=" + PlanePanoramaController.this.mCntReqShoot + " mCntProcessd=" + PlanePanoramaController.this.mCntProcessd + " mStillProcList.size()=" + PlanePanoramaController.this.mStillProcList.size());
            while (PlanePanoramaController.this.mCntReqShoot > PlanePanoramaController.this.mCntProcessd) {
                if (PlanePanoramaController.this.mStillProcList.size() > 0) {
                    CamLog.d(FaceDetector.TAG, "remove StillImageData");
                    StillImageData dat = (StillImageData) PlanePanoramaController.this.mStillProcList.remove(0);
                    NativeMemoryAllocator.freeBuffer(dat.mImage);
                    NativeMemoryAllocator.freeBuffer(dat.mMotionData);
                    PlanePanoramaController planePanoramaController = PlanePanoramaController.this;
                    planePanoramaController.mCntProcessd++;
                }
            }
        }

        private void sendImageToEngine() {
            if (PlanePanoramaController.this.mStillProcList.size() > 0) {
                StillImageData data = (StillImageData) PlanePanoramaController.this.mStillProcList.remove(0);
                if (PlanePanoramaControllerBase.IS_PREVIEW_INPUT) {
                    CamLog.d(FaceDetector.TAG, "run attachStillImageRaw() start :" + data.mId);
                    PlanePanoramaController.this.checkOK(PlanePanoramaController.this.mPanoramaGP.attachStillImageRaw(data.mImage, data.mId, data.mMotionData), "attachStillImageRaw() -> ", false);
                } else {
                    CamLog.d(FaceDetector.TAG, "run attachStillImage() start :" + data.mId);
                    int ret = PlanePanoramaController.this.mPanoramaGP.attachStillImageExt(data.mImage, data.mId, data.mMotionData);
                    if (ret != 0) {
                        CamLog.d(FaceDetector.TAG, "attachStillImageExt() ->(" + ret + ")");
                        PlanePanoramaController.this.mGet.runOnUiThread(new Runnable() {
                            public void run() {
                                if (PlanePanoramaController.this.mGet != null) {
                                    PlanePanoramaController.this.mGet.removePostRunnable(this);
                                    PlanePanoramaController.this.mGet.toastLong(PlanePanoramaController.this.mGet.getString(R.string.camera_error_occurred_try_again));
                                    PlanePanoramaController.this.stopPanorama(false);
                                    PlanePanoramaController.this.mGet.getCameraDevice().startPreview();
                                }
                            }
                        });
                    }
                    CamLog.d(FaceDetector.TAG, "run attachStillImage() end :" + data.mId);
                    if (this.shootCount == 0) {
                        CamLog.d(FaceDetector.TAG, "attachSetJpegForCopyingExif START");
                        PlanePanoramaController.this.mPanoramaGP.attachSetJpegForCopyingExif(data.mImage);
                        CamLog.d(FaceDetector.TAG, "attachSetJpegForCopyingExif END");
                    }
                }
                CamLog.d(FaceDetector.TAG, "NativeMemoryAllocator free START");
                this.shootCount++;
                NativeMemoryAllocator.freeBuffer(data.mImage);
                NativeMemoryAllocator.freeBuffer(data.mMotionData);
                PlanePanoramaController planePanoramaController = PlanePanoramaController.this;
                planePanoramaController.mCntProcessd++;
                CamLog.d(FaceDetector.TAG, "NativeMemoryAllocator free END");
            }
        }
    }

    private class WaitSaveOutputThread extends Thread {
        private WaitSaveOutputThread() {
        }

        public void interrupt() {
            if (PlanePanoramaController.this.isProcessingFinishTask()) {
                PlanePanoramaController.this.mSaveOutputImageTask.cancel(true);
                PlanePanoramaController.this.mSaveOutputImageTask = null;
            }
            super.interrupt();
        }

        public void run() {
            CamLog.d(FaceDetector.TAG, "WaitSaveOutputTask START");
            if (PlanePanoramaController.this.mSaveOutputLatch != null && PlanePanoramaController.this.mSaveOutputLatch.getCount() > 0) {
                CamLog.d(FaceDetector.TAG, "WaitSaveOutputTask await START =" + PlanePanoramaController.this.mSaveOutputLatch.getCount());
                try {
                    PlanePanoramaController.this.mSaveOutputLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                CamLog.d(FaceDetector.TAG, "WaitSaveOutputTask await END =" + PlanePanoramaController.this.mSaveOutputLatch.getCount());
            }
            CamLog.d(FaceDetector.TAG, "WaitSaveOutputTask END");
            super.run();
        }
    }

    public PlanePanoramaController(ControllerFunction function) {
        super(function);
        this.mSaveTimeStart = 0;
        this.mStillProcTask = null;
    }

    private void resetToPreviewState() {
        if (this.mGet == null || this.mGet.isPausing() || this.mGet.getCameraDevice() == null) {
            CamLog.d(FaceDetector.TAG, "exit");
            return;
        }
        this.mBar.setImageBitmap(null);
        this.mGet.getCameraDevice().startPreview();
        this.mGet.setSwitcherVisible(true);
        this.mGet.setThumbnailButtonVisibility(0);
        this.mGet.enableCommand(true);
        this.mGet.setInCaptureProgress(false);
        this.mGet.setQuickFunctionMenuForcedDisable(false);
        this.mGet.setQuickButtonForcedDisable(false);
        this.mGet.showQuickFunctionController();
        this.mGet.quickFunctionControllerRefresh(true);
        this.mGet.setQuickButtonVisible(STILL_PROC_TASK_DELAY_TIME, 0, false);
        this.mGet.setQuickButtonMenuEnable(true, false);
        this.mGet.setLockChangeConfiguration(false);
        setCafSetting();
        this.mStatusShot = 0;
        this.mGet.setShutterButtonImage(true, this.mGet.getOrientationDegree());
        setVisiblePreviewMini(true, false);
        setVisibleArrowGuide(true, false, false);
        showGuideText(true, R.string.panorama_guide_tap_shutter_to_start);
        this.mGet.getCameraDevice().addCallbackBuffer(this.mPreviewBuff);
        this.mGet.getCameraDevice().setPreviewCallbackWithBuffer(this.mCallback);
        if (IS_PREVIEW_INPUT && ModelProperties.isMTKChipset() && !ModelProperties.isFixedFocusModel()) {
            this.mGet.restartPreview(null, false);
        }
    }

    private void resetByPausing() {
        if (this.mBar != null) {
            this.mBar.setImageBitmap(null);
        }
        if (this.mGet != null) {
            this.mGet.getImageListUri().clear();
            this.mGet.setSwitcherVisible(true);
            this.mGet.setThumbnailButtonVisibility(0);
            this.mGet.enableCommand(true);
            this.mGet.setInCaptureProgress(false);
            this.mGet.setQuickFunctionMenuForcedDisable(false);
            this.mGet.setQuickButtonForcedDisable(false);
            this.mGet.showQuickFunctionController();
            this.mGet.setLockChangeConfiguration(false);
            if (this.mWaitSaveOutputThread == null || !this.mWaitSaveOutputThread.isAlive()) {
                this.mStatusShot = 0;
            } else {
                this.mStatusShot = 4;
            }
        }
    }

    private void finishAttachStillImageTask() {
        CamLog.d(FaceDetector.TAG, "start waiting mCntReqShoot=" + this.mCntReqShoot + " mCntProcessd=" + this.mCntProcessd);
        while (this.mCntReqShoot > this.mCntProcessd) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.mStillProcTask = null;
        CamLog.d(FaceDetector.TAG, "end waiting");
    }

    public void startEngine() {
        CamLog.d(FaceDetector.TAG, "startEngine START");
        if (!checkMediator() || this.mGet.getCameraDevice() == null) {
            CamLog.d(FaceDetector.TAG, "exit startEngine checkMediator()=" + checkMediator() + " mGet.getCameraDevice()=" + this.mGet.getCameraDevice());
        } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
            this.mStatusShot = 0;
            if (this.mBaseView == null) {
                this.mBaseView = this.mGet.inflateStub(R.id.stub_plane_panorama);
            }
            this.mBaseView.setVisibility(0);
            if (this.mCallback == null) {
                this.mCallback = new PlanePanoramaCallback(this, this.mBaseView);
            }
            this.mCallback.setAppPanoramaDirection(this.mCallback.getAppPanoramaDirectionSettings());
            this.mPreviewW = this.mGet.getCameraDevice().getParameters().getPreviewSize().width;
            this.mPreviewH = this.mGet.getCameraDevice().getParameters().getPreviewSize().height;
            this.mPictureW = this.mGet.getCameraDevice().getParameters().getPictureSize().width;
            this.mPictureH = this.mGet.getCameraDevice().getParameters().getPictureSize().height;
            this.mBar = (ImageView) this.mBaseView.findViewById(R.id.plane_panorama_preview_bar);
            this.mBarLayout = (RelativeLayout) this.mBaseView.findViewById(R.id.plane_panorama_preview_bar_layout);
            LayoutParams lp = (LayoutParams) this.mBarLayout.getLayoutParams();
            lp.bottomMargin = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.preview_panel_width) + Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.preview_panel_marginBottom);
            this.mBarLayout.setLayoutParams(lp);
            this.mPreviewMini = (ImageView) this.mBaseView.findViewById(R.id.plane_panorama_preview_mini);
            this.mPreviewMiniLayout = (RotateLayout) this.mBaseView.findViewById(R.id.plane_panorama_preview_mini_layout);
            this.mPreviewMiniLayoutOutline = (RelativeLayout) this.mBaseView.findViewById(R.id.plane_panorama_preview_mini_layout_outline);
            this.mPreviewMiniLayoutArrow = (RelativeLayout) this.mBaseView.findViewById(R.id.plane_panorama_preview_mini_layout_arrow);
            this.mStartAndStopGuideTextLayout = (RelativeLayout) this.mBaseView.findViewById(R.id.guide_text_layout);
            this.mBackgroundPreviewLayout = (RelativeLayout) this.mBaseView.findViewById(R.id.plane_panorama_background_preview_layout);
            this.mCallback.setCameraPreviewSize(this.mPreviewW, this.mPreviewH);
            this.mPreviewBuff = new byte[((int) (((double) (this.mPreviewW * this.mPreviewH)) * 1.5d))];
            this.mGet.getCameraDevice().addCallbackBuffer(this.mPreviewBuff);
            this.mGet.getCameraDevice().setPreviewCallbackWithBuffer(this.mCallback);
            this.mStillProcList = new ArrayList();
            this.mIsShooting = false;
            this.mCurOrientaionDegree = this.mGet.getOrientationDegree();
            this.mInit = true;
            setVisiblePreviewMini(true, false);
            setVisibleArrowGuide(true, false, false);
            showGuideText(true, R.string.panorama_guide_tap_shutter_to_start);
            this.mGet.setShutterButtonImage(true, this.mGet.getOrientationDegree());
            this.mSaveInputDirPath = this.mGet.getCurrentStorageDirectory() + "/" + PlanePanoramaControllerBase.DIR_SAVE_INPUT;
            CamLog.d(FaceDetector.TAG, "startEngine END");
        } else {
            CamLog.d(FaceDetector.TAG, "exit startEngine");
        }
    }

    public void stopEngine() {
        CamLog.d(FaceDetector.TAG, "stopEngine START");
        synchronized (this.mSyncObj) {
            stopPanorama();
            Camera camera = this.mGet.getCameraDevice();
            if (camera != null) {
                camera.setPreviewCallbackWithBuffer(null);
                camera.stopPreview();
            }
            CamLog.d(FaceDetector.TAG, "mRequestTakePicture=" + this.mRequestTakePicture + " mCntReqShoot=" + this.mCntReqShoot);
            if (this.mRequestTakePicture) {
                this.mCntReqShoot--;
            }
            hide();
            unbind();
        }
        CamLog.d(FaceDetector.TAG, "stopEngine END");
    }

    private void unbind() {
        if (this.mCallback != null) {
            this.mCallback.unbind();
            this.mCallback = null;
        }
    }

    private void initPanoramaParam(InitParam param) {
        if (param != null) {
            float scale;
            int disp_w = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.lcd_width);
            int disp_h = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.lcd_height);
            param.format = CameraConstants.FREE_PANO_YVU420_SP;
            param.use_threshold = 10;
            param.preview_width = this.mPreviewW;
            param.preview_height = this.mPreviewH;
            if (IS_PREVIEW_INPUT) {
                param.still_width = this.mPreviewW;
                param.still_height = this.mPreviewH;
            } else {
                param.still_width = this.mPictureW;
                param.still_height = this.mPictureH;
            }
            param.angle_of_view_degree = getAngleOfViewDegree();
            param.draw_cur_image = 0;
            param.preview_box_foreground_alpha = Ola_ShotParam.AnimalMask_Random;
            param.preview_box_background_alpha = Common.KEYCODE_TESTMODE_CAMCORDER_PLAY_MOVING_FILE;
            param.direction = 6;
            param.dst_img_width = param.still_width * 10;
            param.dst_img_height = param.still_height;
            param.preview_img_width = param.preview_width * 10;
            param.preview_img_height = param.preview_height;
            param.output_rotation = this.mRotateOutput;
            param.preview_rotation = this.mRoratePreview;
            if (this.mRotateOutput == 90 || this.mRotateOutput == Tag.IMAGE_DESCRIPTION) {
                int tmp = param.dst_img_width;
                param.dst_img_width = param.dst_img_height;
                param.dst_img_height = tmp;
                tmp = param.preview_img_width;
                param.preview_img_width = param.preview_img_height;
                param.preview_img_height = tmp;
            }
            param.preview_shrink_ratio = Math.max((Math.max(param.preview_img_width, param.preview_img_height) / Math.max(disp_w, disp_h)) - 1, 1);
            MorphoPanoramaGP.calcImageSize(param, 360.0d);
            if (MultimediaProperties.VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX < param.dst_img_width) {
                scale = 30000.0f / ((float) param.dst_img_width);
                param.dst_img_width = MultimediaProperties.VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX;
                param.preview_img_width = (int) (((float) param.preview_img_width) * scale);
            }
            if (MultimediaProperties.VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX < param.dst_img_height) {
                scale = 30000.0f / ((float) param.dst_img_height);
                param.dst_img_height = MultimediaProperties.VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX;
                param.preview_img_height = (int) (((float) param.preview_img_height) * scale);
            }
            param.preview_img_width &= -2;
            param.preview_img_height &= -2;
        }
    }

    private void initMorphoPanoramaGP() {
        int[] buff_size = new int[1];
        this.mPanoramaGP = new MorphoPanoramaGP();
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(0, info);
        int degrees = 0;
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
        }
        this.mRotateOutput = ((info.orientation + (this.mCurOrientaionDegree + degrees)) + CameraConstants.DEGREE_360) % CameraConstants.DEGREE_360;
        this.mRotateUI = ((info.orientation - degrees) + CameraConstants.DEGREE_360) % CameraConstants.DEGREE_360;
        this.mRoratePreview = ((this.mRotateUI - this.mRotateOutput) + CameraConstants.DEGREE_360) % CameraConstants.DEGREE_360;
        this.mCallback.setPreviewCroppingAdjustByAuto(0);
        this.mInitParam = new InitParam();
        initPanoramaParam(this.mInitParam);
        checkOK(this.mPanoramaGP.initialize(this.mInitParam, buff_size), "initialize() -> ", true);
    }

    private void resetParamToTaking() {
        CamLog.d(FaceDetector.TAG, "resetParamToTaking");
        this.mDirection[0] = this.mInitParam.direction;
        this.mCallback.setPrevDirection(this.mInitParam.direction);
        this.mCallback.resetParamsBeforeTaking();
        this.mCntReqShoot = 0;
        this.mCntProcessd = 0;
        this.mTempParams = null;
        this.mTempLocation = null;
        this.mIsShooting = true;
        this.mSaveOutputImageTask = null;
        this.mWaitSaveOutputThread = null;
        this.mSaveOutputLatch = null;
    }

    public void startPanorama() {
        CamLog.d(FaceDetector.TAG, "startPanorama()");
        if (this.mGet.getCameraDevice() == null || isProcessingFinishTask()) {
            CamLog.d(FaceDetector.TAG, "exit startPanorama() isProcessing=" + isProcessingFinishTask());
            return;
        }
        this.mGet.hideQuickFunctionController();
        this.mGet.setSwitcherVisible(false);
        this.mGet.setThumbnailButtonVisibility(4);
        this.mGet.hideFocus();
        this.mGet.hideOptionMenu();
        this.mGet.setKeepScreenOn();
        this.mGet.getHandler().sendEmptyMessageDelayed(1, 500);
        this.mGet.getHandler().sendEmptyMessageDelayed(3, 500);
        this.mBar.setImageDrawable(null);
        if (this.mPanoramaGP == null) {
            initMorphoPanoramaGP();
        }
        if (this.mPanoramaGP != null) {
            CamLog.d(FaceDetector.TAG, "panorama version : " + MorphoPanoramaGP.getVersion());
            this.mPanoramaGP.setMotionlessThreshold(CameraConstants.TIME_MACHINE_ANI_INTERVAL);
            this.mPanoramaGP.setFarThreshold(5, 5);
            this.mPanoramaGP.setUseSensorThreshold(this.mUseSensorThres);
            if (this.mCallback == null) {
                this.mCallback = new PlanePanoramaCallback(this, this.mBaseView);
            }
            this.mCallback.allocateDisplayBuffers(this.mCallback.getAppDeviceRotation() + this.mCallback.getAppPanoramaDirection());
            checkOK(this.mPanoramaGP.setUseSensorAssist(0, 0), "setUseSensorAssist() -> ", false);
            checkOK(this.mPanoramaGP.start(), "start() -> ", false);
            resetParamToTaking();
            doStartPanoramaJob();
            return;
        }
        CamLog.d(FaceDetector.TAG, "exit mPanoramaGP = " + this.mPanoramaGP);
    }

    private void doStartPanoramaJob() {
        this.mGet.playRecordingSound(true);
        this.mStatusShot = 2;
        this.mGet.setShutterButtonImage(false, this.mGet.getOrientationDegree());
        this.mGet.setInCaptureProgress(true);
        setVisibleTakingGuide(true, false);
        setVisibleArrowGuide(true, true, true);
        this.mCallback.resetPreviewSkipCount();
        this.mCallback.resetImageIdAndStatusList();
        Parameters params = null;
        if (!(this.mGet == null || !this.mGet.checkPreviewController() || this.mGet.getLG() == null || this.mGet.getLGParam() == null)) {
            params = this.mGet.getLGParam().getParameters();
        }
        if (params != null) {
            params.setAutoExposureLock(false);
            params.setAutoWhiteBalanceLock(false);
        }
        if (this.mGet != null) {
            this.mCallback.rotateGuide(this.mGet.getOrientationDegree());
        }
    }

    public void stopPanorama() {
        stopPanorama(true);
    }

    private void stopPanorama(boolean needSaving) {
        CamLog.d(FaceDetector.TAG, "stopPanorama()");
        if (this.mPanoramaGP == null || this.mBar == null || this.mStatusShot >= 4) {
            CamLog.d(FaceDetector.TAG, "exit mStatusShot=" + this.mStatusShot);
        } else if (!isProcessingFinishTask()) {
            boolean isNeedSaving = needSaving;
            if (isNeedSaving) {
                isNeedSaving = !this.mGet.isPausing();
            }
            this.mGet.setInCaptureProgress(false);
            this.mGet.setMainButtonDisable();
            this.mGet.setSubButton(2, 0);
            this.mGet.clearFocusState();
            this.mGet.hideFocus();
            this.mGet.keepScreenOnAwhile();
            if (this.mGet.getCameraDevice() != null) {
                this.mGet.getCameraDevice().setPreviewCallbackWithBuffer(null);
            }
            if (this.mCallback != null) {
                this.mCallback.hideViews();
            }
            setVisiblePreviewBar(false, false);
            setVisibleTakingGuide(false, false);
            setCafSetting();
            if (isNeedSaving) {
                this.mGet.playRecordingSound(false);
                this.mStatusShot = 4;
                this.mGet.setShutterButtonImage(false, this.mGet.getOrientationDegree());
                this.mGet.doCommandUi(Command.ROTATE);
                this.mTempParams = this.mGet.getParameters();
                this.mTempLocation = this.mGet.getCurrentLocation();
            } else {
                this.mIsShooting = false;
            }
            this.mSaveOutputImageTask = new SaveOutputImageTask(this.mGet.getApplicationContext(), isNeedSaving);
            this.mSaveOutputImageTask.execute(new Void[0]);
        }
    }

    public boolean isProcessingFinishTask() {
        if (this.mSaveOutputImageTask == null || this.mSaveOutputImageTask.getStatus() == Status.FINISHED) {
            return false;
        }
        return true;
    }

    public void onPause() {
        CamLog.d(FaceDetector.TAG, "Panorama Controller onPause - start");
        if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
            stopEngine();
            resetByPausing();
            CamLog.d(FaceDetector.TAG, "Panorama Controller onPause -end");
            return;
        }
        CamLog.d(FaceDetector.TAG, "exit onPause");
    }

    public void addStillImage(StillImageData dat) {
        CamLog.d(FaceDetector.TAG, "addStillImage START");
        this.mStillProcList.add(dat);
        if (this.mStillProcTask == null) {
            this.mStillProcTask = new StillProcTask();
            this.mStillProcTask.start();
        }
        CamLog.d(FaceDetector.TAG, "addStillImage END");
    }

    public void onResume() {
        CamLog.d(FaceDetector.TAG, "Panorama Controller onResume - START");
        if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
            if (this.mWaitSaveOutputThread != null && this.mWaitSaveOutputThread.isAlive()) {
                CamLog.d(FaceDetector.TAG, "mWaitSaveOutputTask delay=" + (System.currentTimeMillis() - this.mSaveTimeStart));
                this.mGet.showProgressDialog();
                CamLog.d(FaceDetector.TAG, "mWaitSaveOutputTask still wait - START");
            }
            CamLog.d(FaceDetector.TAG, "Panorama Controller onResume - END");
            super.onResume();
            return;
        }
        CamLog.d(FaceDetector.TAG, "exit onPause");
    }
}
