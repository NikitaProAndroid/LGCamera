package com.lge.camera.controller.camera;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.System;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.RotateImageView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AudioUtil;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.BaseEngine;
import com.lge.olaworks.library.FaceDetector;
import com.lge.systemservice.core.LEDManager;
import com.lge.systemservice.core.LGContext;
import com.lge.systemservice.core.LGLedRecord;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.Timer;
import java.util.TimerTask;

public class TimerController extends CameraController {
    private final int MSG_INIT_COUNTER;
    private final int MSG_START_COUNTER;
    private final int MSG_STOP_COUNTER;
    private final int TIMER_10SEC;
    private final int TIMER_3SEC;
    private final int TIMER_5SEC;
    private View mCounterView;
    private final Handler mHandler;
    private boolean mInGestureShotActivated;
    private boolean mInTimerShotCountdown;
    private LEDManager mLEDManager;
    private LGLedRecord mRecord;
    private Animation mTimerBGRotation;
    private int mTimerCaptureDelay;
    private int mTimerCaptureMode;
    private Timer mTimerCountDown;
    private int mTimerLedEnabled;
    private int[] timerDrawable;

    public TimerController(ControllerFunction function) {
        super(function);
        this.MSG_INIT_COUNTER = 0;
        this.MSG_START_COUNTER = 1;
        this.MSG_STOP_COUNTER = 2;
        this.TIMER_3SEC = 3;
        this.TIMER_5SEC = 5;
        this.TIMER_10SEC = 10;
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case LGKeyRec.EVENT_INVALID /*0*/:
                        CamLog.d(FaceDetector.TAG, "TimerShot INIT msg.arg1 = " + msg.arg1);
                        TimerController.this.displayInitCounter();
                    case LGKeyRec.EVENT_NO_MATCH /*1*/:
                        CamLog.d(FaceDetector.TAG, "TimerShot START msg.arg1 = " + msg.arg1);
                        TimerController.this.displayStartCounter(msg.arg1);
                    case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                        CamLog.d(FaceDetector.TAG, "TimerShot STOP msg.arg1 = " + msg.arg1);
                        TimerController.this.displayStopCounter();
                    default:
                }
            }
        };
        this.mInTimerShotCountdown = false;
        this.mInGestureShotActivated = false;
        this.mTimerCountDown = null;
        this.mCounterView = null;
        this.mTimerBGRotation = null;
        this.mTimerLedEnabled = 1;
        this.timerDrawable = new int[]{R.drawable.timer_num_1, R.drawable.timer_num_2, R.drawable.timer_num_3, R.drawable.timer_num_4, R.drawable.timer_num_5, R.drawable.timer_num_6, R.drawable.timer_num_7, R.drawable.timer_num_8, R.drawable.timer_num_9, R.drawable.timer_num_10};
        this.mInit = true;
    }

    public void reInitialize() {
        this.mCounterView = null;
    }

    public void setTimerSetting(int value) {
        this.mTimerCaptureMode = value;
        this.mTimerCaptureDelay = value;
        initEmotionalLEDForTimer();
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                TimerController.this.mGet.removePostRunnable(this);
                TimerController.this.mGet.updateTimerIndicator();
            }
        });
    }

    public int getTimerCaptureDelay() {
        return this.mTimerCaptureDelay;
    }

    private void initEmotionalLEDForTimer() {
        if (FunctionProperties.isSupportEmotionalLED()) {
            CamLog.d(FaceDetector.TAG, "Initialize Emotional LED");
            this.mTimerLedEnabled = System.getInt(this.mGet.getContentResolver(), "emotional_led_back_camera_timer_noti", 1);
            if (this.mTimerLedEnabled != 1) {
                CamLog.d(FaceDetector.TAG, "Timer LED Setting is disabled");
                return;
            }
            this.mLEDManager = (LEDManager) new LGContext(this.mGet.getApplicationContext()).getLGSystemService("emotionled");
            this.mRecord = new LGLedRecord();
            this.mRecord.priority = 0;
            this.mRecord.flags = 1;
            this.mRecord.whichLedPlay = 2;
            if (this.mInGestureShotActivated) {
                this.mRecord.patternFilePath = CameraConstants.ID_CAMERA_TIMER_EFFECT_3SEC;
                return;
            }
            switch (this.mTimerCaptureMode) {
                case LGKeyRec.EVENT_STARTED /*3*/:
                    this.mRecord.patternFilePath = CameraConstants.ID_CAMERA_TIMER_EFFECT_3SEC;
                case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                    this.mRecord.patternFilePath = CameraConstants.ID_CAMERA_TIMER_EFFECT_5SEC;
                case BaseEngine.DEFAULT_PRIORITY /*10*/:
                    this.mRecord.patternFilePath = CameraConstants.ID_CAMERA_TIMER_EFFECT_10SEC;
                default:
            }
        }
    }

    private void startLEDForTimer() {
        if (FunctionProperties.isSupportEmotionalLED() && this.mTimerLedEnabled == 1 && this.mGet.getActivity() != null && this.mLEDManager != null) {
            CamLog.d(FaceDetector.TAG, "Emotioinal LED is started");
            this.mLEDManager.stopPattern(this.mGet.getActivity().getPackageName(), 1);
            this.mLEDManager.startPattern(this.mGet.getActivity().getPackageName(), 0, this.mRecord);
        }
    }

    private void stopLEDForTimer() {
        if (FunctionProperties.isSupportEmotionalLED() && this.mTimerLedEnabled == 1 && this.mGet.getActivity() != null && this.mLEDManager != null) {
            CamLog.d(FaceDetector.TAG, "Emotioinal LED is stopped");
            this.mLEDManager.stopPattern(this.mGet.getActivity().getPackageName(), 0);
        }
    }

    public void startTimerShot() {
        CamLog.d(FaceDetector.TAG, "startTimerShot()");
        if (this.mTimerBGRotation == null) {
            this.mTimerBGRotation = AnimationUtils.loadAnimation(this.mGet.getApplicationContext(), R.anim.countdown_bg);
        }
        this.mGet.removeScheduledCommand(Command.DO_CAPTURE);
        this.mGet.hideFocus();
        if (!(this.mGet.getFocusState() == 0 || Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)) || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)))) {
            this.mGet.clearFocusState();
        }
        AudioUtil.setAudioFocus(this.mGet.getApplicationContext(), true);
        this.mTimerCaptureDelay = this.mTimerCaptureMode;
        if (this.mInGestureShotActivated) {
            this.mTimerCaptureDelay = 3;
            initEmotionalLEDForTimer();
        }
        this.mGet.playTimerSound(this.mTimerCaptureDelay);
        startLEDForTimer();
        this.mInTimerShotCountdown = true;
        this.mTimerCountDown = new Timer("timer_countdown");
        TimerTask taskCountDown = new TimerTask() {
            public void run() {
                CamLog.d(FaceDetector.TAG, "timer task (count down) " + TimerController.this.mTimerCaptureDelay);
                if (TimerController.this.mGet.isPausing()) {
                    CamLog.v(FaceDetector.TAG, "return : camera is pausing.. ");
                    return;
                }
                TimerController.this.addMsgArg(1, TimerController.this.mTimerCaptureDelay);
                if (TimerController.this.mTimerCaptureDelay > 0) {
                    TimerController.this.mGet.playTimerSound(TimerController.this.mTimerCaptureDelay);
                    TimerController.this.mTimerCaptureDelay = TimerController.this.mTimerCaptureDelay - 1;
                    return;
                }
                if (TimerController.this.mTimerCountDown != null) {
                    TimerController.this.mTimerCountDown.purge();
                    TimerController.this.mTimerCountDown.cancel();
                    TimerController.this.mTimerCountDown = null;
                    TimerController.this.stopLEDForTimer();
                }
                TimerController.this.mTimerCaptureDelay = TimerController.this.mTimerCaptureMode;
            }
        };
        this.mGet.setMainButtonDisable();
        this.mGet.setSwitcherVisible(false);
        if (ModelProperties.is3dSupportedModel()) {
            this.mGet.set3DSwitchVisible(false);
        }
        addMsgArg(0, this.mTimerCaptureDelay);
        this.mTimerCaptureDelay--;
        this.mGet.setThumbnailButtonVisibility(8);
        this.mTimerCountDown.scheduleAtFixedRate(taskCountDown, 1000, 1000);
    }

    public boolean stopTimerShot() {
        CamLog.d(FaceDetector.TAG, "stopTimerShot()");
        boolean result = false;
        timerAnimation(false);
        if (this.mInTimerShotCountdown) {
            if (this.mCounterView != null) {
                this.mCounterView.setVisibility(4);
            }
            AudioUtil.setAudioFocus(this.mGet.getApplicationContext(), false);
            if (this.mTimerCountDown != null) {
                this.mTimerCountDown.purge();
                this.mTimerCountDown.cancel();
                this.mTimerCountDown = null;
                stopLEDForTimer();
            }
            this.mTimerCaptureDelay = this.mTimerCaptureMode;
            this.mInTimerShotCountdown = false;
            if (!this.mGet.isPausing()) {
                this.mGet.cancelAutoFocus();
            }
            this.mGet.clearFocusState();
            if (this.mGet.isCafSupported() || FunctionProperties.isFrontTouchAESupported()) {
                this.mGet.hideFocus();
            }
            addMsgArg(2, 0);
            this.mHandler.removeMessages(1);
            this.mGet.setShutterButtonClicked(false);
            this.mGet.setMainButtonEnable();
            this.mGet.enableCommand(true);
            if (!this.mGet.isPausing()) {
                this.mGet.doCommand(Command.DISPLAY_PREVIEW);
            }
            result = true;
        }
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                TimerController.this.mGet.removePostRunnable(this);
                TimerController.this.mGet.updateTimerIndicator();
            }
        });
        this.mInGestureShotActivated = false;
        return result;
    }

    public void displayInitCounter() {
        CamLog.d(FaceDetector.TAG, "init counter");
        if (this.mCounterView == null) {
            try {
                this.mCounterView = this.mGet.inflateStub(R.id.stub_timer);
                if (this.mCounterView == null) {
                    CamLog.d(FaceDetector.TAG, " mCounterView is null.");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean("animation", false);
                this.mGet.doCommandUi(Command.ROTATE, bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.mCounterView != null) {
            this.mCounterView.setVisibility(0);
        }
        int resId = 0;
        if (this.mTimerCaptureMode == 5) {
            resId = R.drawable.timer_num_5;
        } else if (this.mTimerCaptureMode == 3) {
            resId = R.drawable.timer_num_3;
        } else if (this.mTimerCaptureMode == 10) {
            resId = R.drawable.timer_num_10;
        }
        if (this.mInGestureShotActivated) {
            resId = R.drawable.timer_num_3;
        }
        RotateImageView iv_timer_num = (RotateImageView) this.mGet.findViewById(R.id.timer_count);
        if (!(resId == 0 || iv_timer_num == null)) {
            iv_timer_num.setImageResource(resId);
        }
        timerAnimation(true);
    }

    public void displayStartCounter(int timerCapturedDelay) {
        int resId = 0;
        CamLog.d(FaceDetector.TAG, "start counter");
        timerAnimation(false);
        CamLog.d(FaceDetector.TAG, "timer task (count down) " + timerCapturedDelay);
        switch (timerCapturedDelay) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                addMsgArg(2, 0);
                return;
            default:
                if (timerCapturedDelay > 0) {
                    try {
                        resId = this.timerDrawable[timerCapturedDelay - 1];
                        break;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        CamLog.e(FaceDetector.TAG, "ArrayIndexOutOfBoundsException!", e);
                        break;
                    } catch (NullPointerException e2) {
                        CamLog.e(FaceDetector.TAG, "NullPointerException!", e2);
                        break;
                    } catch (IndexOutOfBoundsException e3) {
                        CamLog.e(FaceDetector.TAG, "IndexOutOfBoundsException!", e3);
                        break;
                    }
                }
                break;
        }
        RotateImageView iv_timer_num = (RotateImageView) this.mGet.findViewById(R.id.timer_count);
        if (iv_timer_num != null) {
            iv_timer_num.setImageResource(resId);
            timerAnimation(true);
        }
    }

    public void displayStopCounter() {
        CamLog.d(FaceDetector.TAG, "stop counter");
        if (this.mCounterView != null) {
            this.mCounterView.setVisibility(4);
        }
        if (this.mInTimerShotCountdown) {
            this.mGet.doCommand(Command.CAMERA_TIMER);
            this.mInGestureShotActivated = false;
            int focusState = this.mGet.getFocusState();
            CamLog.v(FaceDetector.TAG, "go take a picture:" + focusState);
            String flashMode = this.mGet.getSettingValue(Setting.KEY_FLASH);
            if (!FunctionProperties.isSupportGuideFlash() || CameraConstants.SMART_MODE_OFF.equals(flashMode) || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)) || focusState == 5 || focusState == 6 || focusState == 1 || focusState == 3) {
                if (focusState == 1 || focusState == 5) {
                    this.mGet.cancelAutoFocus();
                }
            } else if (!ModelProperties.isRenesasISP() || !LGT_Limit.ISP_AUTOMODE_AUTO.equals(flashMode)) {
                CamLog.d(FaceDetector.TAG, "doFocus : Flash on or auto");
                this.mGet.cancelAutoFocus();
                this.mGet.doFocus(true);
                this.mGet.doCommandUi(Command.DO_CAPTURE);
                return;
            } else if (this.mGet.getParameters() != null) {
                if ("1".equals(this.mGet.getParameters().get("is-lowlight"))) {
                    CamLog.d(FaceDetector.TAG, "doFocus : Flash auto and is-lowlight = 1");
                    this.mGet.cancelAutoFocus();
                    this.mGet.doFocus(true);
                    this.mGet.doCommandUi(Command.DO_CAPTURE);
                    return;
                }
            }
            this.mGet.doCommandUi(Command.TAKE_PICTURE);
            this.mInTimerShotCountdown = false;
        }
    }

    public void onPause() {
        if (this.mInit) {
            stopTimerShot();
        }
        super.onPause();
        this.mTimerBGRotation = null;
    }

    public void onDestroy() {
        this.mCounterView = null;
    }

    public boolean isTimerShotCountdown() {
        CamLog.d(FaceDetector.TAG, "isTimerShotCountdown : " + this.mInTimerShotCountdown);
        return this.mInTimerShotCountdown;
    }

    public void setTimerShotCountdown(boolean value) {
        CamLog.d(FaceDetector.TAG, "setTimerShotCountdown : " + value);
        this.mInTimerShotCountdown = value;
    }

    public boolean isGestureShotActivated() {
        CamLog.d(FaceDetector.TAG, "isGestureShotActivated : " + this.mInGestureShotActivated);
        return this.mInGestureShotActivated;
    }

    public void setGestureShotActivated(boolean value) {
        CamLog.d(FaceDetector.TAG, "setGestureShotActivated : " + value);
        this.mInGestureShotActivated = value;
    }

    public void timerAnimation(boolean start) {
        if (checkMediator()) {
            RotateImageView iv = (RotateImageView) this.mGet.findViewById(R.id.countdown_bg);
            if (iv != null) {
                if (this.mTimerBGRotation == null) {
                    this.mTimerBGRotation = AnimationUtils.loadAnimation(this.mGet.getApplicationContext(), R.anim.countdown_bg);
                }
                if (start) {
                    iv.clearAnimation();
                    if (this.mTimerBGRotation != null) {
                        iv.startAnimation(this.mTimerBGRotation);
                        return;
                    }
                    return;
                }
                iv.clearAnimation();
            }
        }
    }

    public void addMsgArg(int what, int arg) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg;
        this.mHandler.sendMessage(msg);
    }
}
