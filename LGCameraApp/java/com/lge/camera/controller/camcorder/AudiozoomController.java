package com.lge.camera.controller.camcorder;

import android.os.Message;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.VideoRecorder;
import com.lge.camera.command.Command;
import com.lge.camera.components.AudiozoomBar;
import com.lge.camera.components.AudiozoomBarLevelMeter;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.RotateTextView;
import com.lge.camera.controller.Controller;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AudioUtil;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.MainHandler;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import com.lge.olaworks.library.FaceDetector;

public class AudiozoomController extends Controller {
    private static final int AUDIOZOOM_MOCE_LANDSCAPE = 7;
    private static final int AUDIOZOOM_OFF = 0;
    public static final int MSG_HIDE_ANI = 0;
    private boolean isSetQFL;
    private int mAudioinput;
    private AudiozoomBarLevelMeter mAudiozoomBarLevelMeter;
    private AudiozoomBar mAudiozoomBarStep;
    private RotateLayout mAudiozoomGuideLayout;
    private RotateTextView mAudiozoomGuideString;
    private RotateLayout mAudiozoomGuideStringLayout;
    private boolean mAudiozoomStart;
    private boolean mAudiozoomStartInRecording;
    private String mAudiozoomvalue;
    private int mGuideViewHeight;
    private int mGuideViewLeftMargin;
    private int mGuideViewWidth;
    private MainHandler mHandler;
    private int mLevelMeterValue;
    private int mStartrecordingdegree;
    private Thread mThreadRefreshLevelMeter;

    public AudiozoomController(ControllerFunction function) {
        super(function);
        this.mHandler = this.mGet.getHandler();
        this.mGuideViewWidth = 0;
        this.mGuideViewHeight = 0;
        this.mGuideViewLeftMargin = 0;
        this.mAudiozoomStart = false;
        this.mAudiozoomStartInRecording = false;
        this.mAudiozoomBarLevelMeter = null;
        this.mAudiozoomBarStep = null;
        this.mAudiozoomGuideLayout = null;
        this.mAudiozoomGuideString = null;
        this.mAudiozoomGuideStringLayout = null;
        this.mAudioinput = -1;
        this.mAudiozoomvalue = CameraConstants.SMART_MODE_OFF;
        this.isSetQFL = false;
        this.mStartrecordingdegree = 0;
        this.mLevelMeterValue = 0;
        this.mThreadRefreshLevelMeter = null;
    }

    public void initController() {
        this.mGet.inflateStub(R.id.stub_audiozoom_guide);
        this.mAudiozoomBarLevelMeter = (AudiozoomBarLevelMeter) this.mGet.findViewById(R.id.audiozoom_angle_bar_level);
        LayoutParams lp = this.mAudiozoomBarLevelMeter.getLayoutParams();
        this.mAudiozoomBarLevelMeter.initRecProgressBar(lp.width, lp.height);
        this.mAudiozoomBarStep = (AudiozoomBar) this.mGet.findViewById(R.id.audiozoom_angle_bar_step);
        lp = this.mAudiozoomBarStep.getLayoutParams();
        this.mAudiozoomBarStep.initRecProgressBar(lp.width, lp.height);
        this.mAudiozoomGuideLayout = (RotateLayout) this.mGet.findViewById(R.id.audiozoom_guide_rotatelayout);
        this.mAudiozoomGuideString = (RotateTextView) this.mGet.findViewById(R.id.audiozoom_guide_string);
        this.mAudiozoomGuideString.setText(this.mGet.getString(R.string.sp_audio_zoom_help_desc_in_recording));
        this.mAudiozoomGuideStringLayout = (RotateLayout) this.mGet.findViewById(R.id.audiozoom_guide_string_rotatelayout);
        setAudioZoomGuideViewLayout();
        this.mInit = true;
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        if (checkLayout()) {
            this.mAudiozoomBarLevelMeter.setVisibility(4);
            this.mAudiozoomBarStep.setVisibility(4);
            this.mAudiozoomGuideLayout.setVisibility(4);
            this.mAudiozoomGuideStringLayout.setVisibility(4);
        }
        try {
            if (this.mThreadRefreshLevelMeter != null) {
                this.mThreadRefreshLevelMeter.interrupt();
                this.mThreadRefreshLevelMeter.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        this.mHandler = null;
        this.mAudiozoomBarLevelMeter = null;
        this.mAudiozoomBarStep = null;
        this.mAudiozoomGuideLayout = null;
        this.mAudiozoomGuideString = null;
        this.mAudiozoomGuideStringLayout = null;
        this.mThreadRefreshLevelMeter = null;
    }

    private boolean checkLayout() {
        if (this.mAudiozoomBarLevelMeter == null || this.mAudiozoomBarStep == null || this.mAudiozoomGuideLayout == null || this.mAudiozoomGuideString == null || this.mAudiozoomGuideStringLayout == null) {
            return false;
        }
        return true;
    }

    public String getmAudiozoomvalue() {
        return this.mAudiozoomvalue;
    }

    public void setmAudiozoomvalue(String value) {
        if (this.mInit) {
            this.mAudiozoomvalue = value;
            if (CameraConstants.SMART_MODE_ON.equals(this.mAudiozoomvalue)) {
                this.isSetQFL = true;
                showAudiozoomReady();
                return;
            }
            this.isSetQFL = false;
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    AudiozoomController.this.mGet.removePostRunnable(this);
                    if (AudiozoomController.this.mAudiozoomGuideLayout != null) {
                        AudiozoomController.this.mAudiozoomGuideLayout.setVisibility(4);
                    }
                }
            });
            return;
        }
        CamLog.d(FaceDetector.TAG, "==>Audiozoom controller is not init");
    }

    public void setStartrecordingdegree(int degree) {
        this.mStartrecordingdegree = degree;
        if (!this.mGet.isConfigureLandscape()) {
            this.mStartrecordingdegree = (this.mStartrecordingdegree + 90) % CameraConstants.DEGREE_360;
        }
    }

    public int getStartrecordingdegree() {
        return this.mStartrecordingdegree;
    }

    public boolean getAudiozoomStart() {
        return this.mAudiozoomStart;
    }

    public void setAudiozoomStart(boolean AudiozoomStart) {
        this.mAudiozoomStart = AudiozoomStart;
    }

    public boolean getAudioZoomStartInRecording() {
        return this.mAudiozoomStartInRecording;
    }

    public void setAudioZoomStartInRecording(boolean start) {
        this.mAudiozoomStartInRecording = start;
    }

    public void startAudiozoom() {
        if (this.mGet == null || this.mGet.getVideoState() != 3 || this.mGet.getHeadsetstate() == 2 || this.mGet.getCameraId() != 0) {
            CamLog.d(FaceDetector.TAG, "Do not start Audiozoom");
        } else if (checkLayout()) {
            VideoRecorder.setAudiozoommetadata();
            int mZoomValue = Integer.parseInt(this.mGet.getSettingValue(Setting.KEY_ZOOM));
            int max = (int) this.mGet.getZoomMaxValue();
            if (this.mGet.getZoomCursorMaxStep() == 90 && max > 0) {
                mZoomValue = Math.round(((float) (mZoomValue * 90)) / ((float) max));
            }
            int zoomAngle = calculateangle(mZoomValue);
            if (this.mAudioinput != -1) {
                this.mAudioinput = -1;
            }
            setAudioinput();
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    if (AudiozoomController.this.mGet != null && AudiozoomController.this.checkLayout()) {
                        AudiozoomController.this.mGet.removePostRunnable(this);
                        if (!AudiozoomController.this.isSetQFL) {
                            AudiozoomController.this.showAudiozoomReady();
                        }
                        AudiozoomController.this.mAudiozoomGuideString.setText(Common.breakTextToMultiLine(AudiozoomController.this.mAudiozoomGuideString.getTextPaint(), AudiozoomController.this.mGet.getString(R.string.sp_audio_zoom_help_desc_in_recording), AudiozoomController.this.getTextArea()) + "\n" + AudiozoomController.this.mGet.getString(R.string.audio_zoom_warning));
                        int nAnglebar = 0;
                        try {
                            nAnglebar = (int) ((((float) AudiozoomController.this.mGet.getZoomBarValue()) / AudiozoomController.this.mGet.getZoomMaxValue()) * CameraConstants.PIP_VIEW_ALLOWABLE_MOVEMENT_EXTENT_FOR_TOGGLE);
                        } catch (ArithmeticException e) {
                            CamLog.e(FaceDetector.TAG, "ArithmeticException : ", e);
                        }
                        AudiozoomController.this.mAudiozoomBarLevelMeter.setStep(nAnglebar);
                        AudiozoomController.this.mAudiozoomBarStep.setProgress(nAnglebar);
                        AudiozoomController.this.mAudiozoomGuideLayout.findViewById(R.id.audiozoom_guide_box).setVisibility(0);
                        AudiozoomController.this.mAudiozoomGuideStringLayout.setVisibility(0);
                        AudiozoomController.this.mAudiozoomGuideLayout.setVisibility(0);
                        AudiozoomController.this.mAudiozoomBarLevelMeter.setVisibility(0);
                        AudiozoomController.this.mAudiozoomBarStep.setVisibility(0);
                        AudiozoomController.this.mThreadRefreshLevelMeter = new Thread(new Runnable() {
                            public void run() {
                                while (AudiozoomController.this.mGet.getVideoState() == 3 && AudiozoomController.this.mAudiozoomBarLevelMeter.getVisibility() == 0 && !Thread.interrupted()) {
                                    AudiozoomController.this.updateAudioZoomLevelMeter();
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        AudiozoomController.this.mThreadRefreshLevelMeter.start();
                    }
                }
            });
            CamLog.d(FaceDetector.TAG, "==>angle" + zoomAngle);
            VideoRecorder.setAudiozoom(zoomAngle, AUDIOZOOM_MOCE_LANDSCAPE);
            setAudiozoomStart(true);
        }
    }

    private int getTextArea() {
        return Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_width) - Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.quickfunction_layout_width);
    }

    public void stopAudiozoom() {
        if (this.mGet != null && checkLayout()) {
            setAudiozoomStart(false);
            VideoRecorder.setAudiozoom(0, 0);
            AudioUtil.setAudiodevice(this.mGet.getApplicationContext(), 4);
            this.mAudiozoomBarLevelMeter.setVisibility(8);
            this.mAudiozoomBarStep.setVisibility(8);
            setAudiozoombuttonstate();
            this.mAudiozoomGuideLayout.setVisibility(4);
            this.mAudiozoomGuideStringLayout.setVisibility(4);
            try {
                if (this.mThreadRefreshLevelMeter != null) {
                    this.mThreadRefreshLevelMeter.interrupt();
                    this.mThreadRefreshLevelMeter.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateAudiozoomvalue(boolean updateangle, int zoomvalue) {
        if (updateangle) {
            final int tmpzoom = zoomvalue;
            VideoRecorder.updateAudiozoom(calculateangle(zoomvalue));
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    if (AudiozoomController.this.mGet != null && AudiozoomController.this.mAudiozoomBarLevelMeter != null && AudiozoomController.this.mAudiozoomBarStep != null) {
                        AudiozoomController.this.mGet.removePostRunnable(this);
                        int nAnglebar = 0;
                        float max = AudiozoomController.this.mGet.getZoomMaxValue();
                        try {
                            if (((int) max) == tmpzoom) {
                                nAnglebar = 100;
                            } else if (tmpzoom == 0) {
                                nAnglebar = 0;
                            } else {
                                nAnglebar = (int) ((((float) tmpzoom) / max) * CameraConstants.PIP_VIEW_ALLOWABLE_MOVEMENT_EXTENT_FOR_TOGGLE);
                            }
                        } catch (ArithmeticException e) {
                            CamLog.e(FaceDetector.TAG, "ArithmeticException : ", e);
                        }
                        AudiozoomController.this.mAudiozoomBarLevelMeter.setStep(nAnglebar);
                        AudiozoomController.this.mAudiozoomBarStep.setProgress(nAnglebar);
                        if (AudiozoomController.this.mAudiozoomBarLevelMeter.getVisibility() != 0) {
                            AudiozoomController.this.mAudiozoomBarLevelMeter.setVisibility(0);
                        }
                        if (AudiozoomController.this.mAudiozoomBarStep.getVisibility() != 0) {
                            AudiozoomController.this.mAudiozoomBarStep.setVisibility(0);
                        }
                    }
                }
            });
            return;
        }
        setAudioinput();
    }

    public void updateAudioZoomLevelMeter() {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                AudiozoomController.this.mGet.removePostRunnable(this);
                if (AudiozoomController.this.mGet.getAudiozoomStart()) {
                    AudiozoomController.this.mLevelMeterValue = VideoRecorder.getAudioZoomLevelMeter();
                    AudiozoomController.this.mAudiozoomBarLevelMeter.setProgress(AudiozoomController.this.mLevelMeterValue);
                }
            }
        });
    }

    public int calculateangle(int zoomvalue) {
        float max = this.mGet.getZoomMaxValue();
        if (zoomvalue == 0) {
            return 90;
        }
        if (zoomvalue == ((int) max)) {
            return 0;
        }
        return (int) (CameraConstants.SMARTZOOM_ZOOM_AREA_HEIGHT_UVGA - (((float) zoomvalue) * (CameraConstants.SMARTZOOM_ZOOM_AREA_HEIGHT_UVGA / max)));
    }

    public void setAudioinput() {
        int Orientation = this.mGet.getOrientation();
        if (this.mAudioinput != Orientation) {
            this.mAudioinput = Orientation;
            AudioUtil.setAudiodevice(this.mGet.getApplicationContext(), this.mAudioinput);
        }
    }

    public void addMsgArg(int what, int arg) {
        if (this.mHandler != null) {
            Message msg = Message.obtain();
            msg.what = what;
            msg.arg1 = arg;
            this.mHandler.sendMessageDelayed(msg, (long) arg);
        }
    }

    public void setAudiozoombuttonstate() {
        int resid;
        int Headsetstate = this.mGet.getHeadsetstate();
        boolean isDim = true;
        boolean isEnable = true;
        if (this.mGet.isAudiozoom_ExceptionCase(true) || this.mGet.getVideoState() != 3) {
            isEnable = false;
            isDim = true;
        }
        if (!this.mAudiozoomStart) {
            resid = R.drawable.selector_btn_mode_audio_zoom_off_button;
        } else if (Headsetstate == 2) {
            resid = R.drawable.selector_btn_mode_audio_zoom_off_button;
        } else {
            resid = R.drawable.selector_btn_mode_audio_zoom_on_button;
        }
        this.mGet.setQuickButtonMenuEnable(2, isEnable, isDim);
        if (resid != 0) {
            this.mGet.changeButtonResource(2, resid);
        }
    }

    private void showAudiozoomReady() {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                if (AudiozoomController.this.mGet != null && AudiozoomController.this.checkLayout()) {
                    AudiozoomController.this.mGet.removePostRunnable(this);
                    if (AudiozoomController.this.mAudiozoomStart) {
                        AudiozoomController.this.mAudiozoomGuideLayout.findViewById(R.id.audiozoom_guide_box).setVisibility(0);
                    } else {
                        AudiozoomController.this.mAudiozoomGuideLayout.findViewById(R.id.audiozoom_guide_box).setVisibility(4);
                        AudiozoomController.this.mAudiozoomGuideStringLayout.setVisibility(4);
                    }
                    AudiozoomController.this.mAudiozoomGuideLayout.setVisibility(0);
                    if (AudiozoomController.this.mHandler.hasMessages(8)) {
                        AudiozoomController.this.mHandler.removeMessages(8);
                    }
                    AudiozoomController.this.addMsgArg(8, PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME);
                }
            }
        });
    }

    public void setForced_audiozoom(boolean isEnable) {
        if (!FunctionProperties.isSupportAudiozoom()) {
            return;
        }
        if (!isEnable) {
            int menuIndex = this.mGet.getQfIndex(Setting.KEY_CAMCORDER_AUDIOZOOM);
            if (this.mGet.isQuickFunctionList(menuIndex)) {
                if (this.mGet.isQuickFunctionSettingControllerShowing() && Setting.KEY_CAMCORDER_AUDIOZOOM.equals(this.mGet.getSelectedQuickFunctionMenuKey())) {
                    this.mGet.doCommand(Command.HIDE_QUICK_FUNCTION_SETTING_MENU);
                }
                this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMCORDER_AUDIOZOOM, false, false);
                this.mGet.setQuickFunctionControllerMenuIcon(menuIndex, 0);
                this.mGet.setSetting(Setting.KEY_CAMCORDER_AUDIOZOOM, CameraConstants.SMART_MODE_OFF);
                setmAudiozoomvalue(CameraConstants.SMART_MODE_OFF);
                return;
            }
            this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMCORDER_AUDIOZOOM, false, false);
        } else if (!this.mGet.isAudiozoom_ExceptionCase(false)) {
            this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMCORDER_AUDIOZOOM, true, false);
        }
    }

    public void startRotation(int degree) {
        if (this.mInit && this.mGet.isCamcorderRotation(false)) {
            if (this.mAudiozoomGuideLayout != null) {
                this.mAudiozoomGuideLayout.rotateLayout(degree);
            }
            if (this.mAudiozoomGuideStringLayout != null) {
                this.mAudiozoomGuideStringLayout.rotateLayout(degree);
            }
            RotateLayout rl = (RotateLayout) this.mGet.findViewById(R.id.audiozoom_angle_bar_rotatelayout);
            if (rl != null) {
                rl.rotateLayout(degree);
                return;
            }
            return;
        }
        CamLog.d(FaceDetector.TAG, "AudiozoomController is not initialize.");
    }

    private void setAudioZoomGuideViewLayout() {
        RelativeLayout view = (RelativeLayout) this.mGet.findViewById(R.id.audiozoom_guide);
        if (view != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
            if (this.mGet.isConfigureLandscape()) {
                params.width = this.mGuideViewWidth;
                params.height = this.mGuideViewHeight;
                params.leftMargin = this.mGuideViewLeftMargin;
            } else {
                params.height = this.mGuideViewWidth;
                params.width = this.mGuideViewHeight;
                params.topMargin = this.mGuideViewLeftMargin;
            }
            if (ModelProperties.isUVGAmodel() || ModelProperties.isXGAmodel()) {
                int shiftForUVGA;
                int lcdWidth = getPixelFromDimens(R.dimen.lcd_width);
                int lcdHeight = getPixelFromDimens(R.dimen.lcd_height);
                if (((float) this.mGuideViewWidth) / ((float) this.mGuideViewHeight) <= ((float) lcdWidth) / ((float) lcdHeight) || this.mGuideViewHeight > lcdHeight) {
                    shiftForUVGA = 0;
                } else {
                    shiftForUVGA = DialogCreater.DIALOG_ID_HELP_NIGHT;
                }
                if (this.mGet.isConfigureLandscape()) {
                    params.bottomMargin = shiftForUVGA;
                } else {
                    params.leftMargin = shiftForUVGA;
                }
            }
            view.setLayoutParams(params);
            view.setVisibility(0);
        }
    }

    public void setAudioZoomGuideViewLayout(int width, int height, int marginLeft) {
        this.mGuideViewWidth = width;
        this.mGuideViewHeight = height;
        this.mGuideViewLeftMargin = marginLeft;
        setAudioZoomGuideViewLayout();
    }
}
