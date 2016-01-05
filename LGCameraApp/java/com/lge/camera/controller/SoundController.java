package com.lge.camera.controller;

import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.properties.ShutterSoundProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.camera.util.TelephonyUtil;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class SoundController extends Controller {
    public static int MAX_BURST_SHOT_SOUND = 0;
    public static int MAX_CONTINUOUS_SHOT_SOUND = 0;
    public static final int SHUTTER_SOUND_COUNT = 4;
    private final OnLoadCompleteListener completeListener;
    private final OnLoadCompleteListener completeListener_music;
    private AudioManager mAudioManager;
    private int mAudioMode;
    private int mClearSoundResultID;
    private int mContinuousSoundResultID;
    private int mMusicSoundSampleIDLoadedMaskID;
    private int[] mShutter;
    private boolean mShutterSoundLoaded;
    private int mShutterSoundResID;
    private Thread mSoundBuildThread;
    private int mSoundSampleIDBeforeLoaded;
    private int mSoundSampleIDLoadedMaskID;
    private int mSoundSampleIDRegisteredMaskID;
    private int mSound_Timer1sec;
    private int mSound_TimerLast;
    private int mSound_afFail;
    private int mSound_afSuccess;
    private int mSound_clearshot_delay;
    private int mSound_clearshot_snap;
    private int mSound_continuous_shutter;
    private SoundPool mSound_pool;
    private SoundPool mSound_pool_music;
    private int mSound_shutter;
    private int mSound_startRecording;
    private int mSound_stopRecording;
    private int mSound_voiceShutter_LG;
    private int mSound_voiceShutter_cheese;
    private int mSound_voiceShutter_kimchi;
    private int mSound_voiceShutter_smile;
    private int mSound_voiceShutter_torimasu;
    private int mSound_voiceShutter_whisky;
    private int msound_capture;
    private int voiceCommandStream;

    static {
        MAX_CONTINUOUS_SHOT_SOUND = 6;
        MAX_BURST_SHOT_SOUND = 39;
    }

    public SoundController(ControllerFunction function) {
        super(function);
        this.mSound_voiceShutter_cheese = 0;
        this.mSound_voiceShutter_smile = 0;
        this.mSound_voiceShutter_whisky = 0;
        this.mSound_voiceShutter_kimchi = 0;
        this.mSound_voiceShutter_LG = 0;
        this.mSound_voiceShutter_torimasu = 0;
        this.mSound_clearshot_delay = 0;
        this.mSound_clearshot_snap = 0;
        this.mSoundSampleIDBeforeLoaded = 0;
        this.mSoundSampleIDRegisteredMaskID = -1;
        this.mSoundSampleIDLoadedMaskID = 0;
        this.mMusicSoundSampleIDLoadedMaskID = 0;
        this.mShutterSoundLoaded = false;
        this.mSoundBuildThread = null;
        this.voiceCommandStream = 0;
        this.mShutterSoundResID = 0;
        this.completeListener = new OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                CamLog.v(FaceDetector.TAG, "onLoadComplete() id:" + sampleId + ",status:" + status);
                SoundController.this.setLoadedSoundMaskID(sampleId);
                if (!(status == 0 || sampleId != 1 || SoundController.this.mShutterSoundResID == 0)) {
                    SoundController.this.mSound_pool.load(SoundController.this.mGet.getApplicationContext(), SoundController.this.mShutterSoundResID, 1);
                    SoundController.this.mShutterSoundResID = 0;
                    CamLog.v(FaceDetector.TAG, "onLoadComplete() reload resID:" + SoundController.this.mShutterSoundResID);
                }
                if (SoundController.this.getSoundIDPlayedBeforeLoaded() == sampleId) {
                    if (sampleId == SoundController.this.mSound_continuous_shutter && ProjectVariables.useContinuousSound()) {
                        SoundController.this.mContinuousSoundResultID = SoundController.this.mSound_pool.play(sampleId, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X, 0, SoundController.MAX_CONTINUOUS_SHOT_SOUND, RotateView.DEFAULT_TEXT_SCALE_X);
                    } else {
                        SoundController.this.mSound_pool.play(sampleId, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X, 0, 0, RotateView.DEFAULT_TEXT_SCALE_X);
                    }
                    SoundController.this.setSoundSampleIDBeforeLoaded(0);
                }
                if (!SoundController.this.mShutterSoundLoaded && SoundController.this.checkShutterSoundLoaded()) {
                    SoundController.this.mGet.runOnUiThread(new Runnable() {
                        public void run() {
                            if (SoundController.this.checkMediator()) {
                                SoundController.this.mGet.removePostRunnable(this);
                                SoundController.this.mGet.setMainButtonEnable(CameraConstants.SOUNDCONTROLLER_LOCKKEY);
                            }
                        }
                    });
                    SoundController.this.mShutterSoundLoaded = true;
                }
                if (SoundController.this.checkAllSoundLoaded()) {
                    CamLog.w(FaceDetector.TAG, "All Sound Loaded");
                }
            }
        };
        this.completeListener_music = new OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                CamLog.v(FaceDetector.TAG, "completeListener_music() id:" + sampleId + ",status:" + status);
                SoundController.this.setLoadedMusicSoundMaskID(sampleId);
            }
        };
        this.mContinuousSoundResultID = 0;
        this.mClearSoundResultID = 0;
    }

    private boolean checkSoundLoaded(int soundID, boolean isMusicStream) {
        int soundMaskID = 1 << soundID;
        if (((isMusicStream ? this.mMusicSoundSampleIDLoadedMaskID : this.mSoundSampleIDLoadedMaskID) & soundMaskID) == soundMaskID) {
            return true;
        }
        return false;
    }

    private boolean checkShutterSoundLoaded() {
        if (this.mGet.getApplicationMode() == 1) {
            if (checkSoundLoaded(this.mSound_startRecording, false)) {
                return true;
            }
            return false;
        } else if (this.mGet.getApplicationMode() != 0) {
            return false;
        } else {
            int shutterSoundID = this.mSound_shutter;
            if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS)) {
                shutterSoundID = this.mSound_continuous_shutter;
            } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT)) {
                shutterSoundID = this.mSound_clearshot_snap;
            } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
                shutterSoundID = this.mSound_startRecording;
            } else {
                shutterSoundID = this.mSound_shutter;
            }
            if (checkSoundLoaded(shutterSoundID, false)) {
                return true;
            }
            return false;
        }
    }

    public boolean checkCameraShutterSoundLoaded() {
        if (checkSoundLoaded(this.mSound_shutter, false)) {
            return true;
        }
        return false;
    }

    private boolean checkAllSoundLoaded() {
        return this.mSoundSampleIDRegisteredMaskID == this.mSoundSampleIDLoadedMaskID;
    }

    private void setLoadedSoundMaskID(int sampleID) {
        this.mSoundSampleIDLoadedMaskID |= 1 << sampleID;
    }

    private void setLoadedMusicSoundMaskID(int sampleID) {
        this.mMusicSoundSampleIDLoadedMaskID |= 1 << sampleID;
    }

    private void setRegisteredSoundMaskID(int sampleID) {
        if (this.mSoundSampleIDRegisteredMaskID == -1) {
            this.mSoundSampleIDRegisteredMaskID = 1 << sampleID;
        } else {
            this.mSoundSampleIDRegisteredMaskID |= 1 << sampleID;
        }
    }

    private void loadingCameraSoundSourceInCamcorder() {
        int[] shutterSoundResourceId = new int[]{R.raw.cam_snap_0, R.raw.cam_snap_1, R.raw.cam_snap_2, R.raw.cam_snap_3};
        int index = SharedPreferenceUtil.getShutterSoundIndex(this.mGet.getApplicationContext());
        if (index != -1) {
            if (index < 0 || index > 3) {
                index = 0;
            }
            this.msound_capture = this.mSound_pool.load(this.mGet.getActivity().getApplicationContext(), shutterSoundResourceId[index], 1);
            this.mSound_shutter = this.msound_capture;
            setRegisteredSoundMaskID(this.msound_capture);
        } else {
            CamLog.w(FaceDetector.TAG, "KEY_SHUTTER_SOUND is not found.");
        }
        if (this.mSound_shutter == 0) {
            CamLog.d(FaceDetector.TAG, "Shutter Sound Load Failed");
        }
    }

    private void loadingCameraSoundSource() {
        this.mShutter = new int[SHUTTER_SOUND_COUNT];
        int[] shutterSoundResourceId = new int[]{R.raw.cam_snap_0, R.raw.cam_snap_1, R.raw.cam_snap_2, R.raw.cam_snap_3};
        ListPreference pref = this.mGet.findPreference(Setting.KEY_SHUTTER_SOUND);
        int index = 0;
        if (pref == null || pref.getValue().equals(CameraConstants.SMART_MODE_OFF)) {
            CamLog.w(FaceDetector.TAG, "KEY_SHUTTER_SOUND is not found.");
        } else if (ShutterSoundProperties.isSupportShutterSoundOff()) {
            index = Integer.parseInt(pref.getValue());
        } else {
            index = Integer.parseInt(pref.getValue());
        }
        if (index < 0 || index > 3) {
            index = 0;
        }
        this.mShutterSoundResID = shutterSoundResourceId[index];
        this.mShutter[index] = this.mSound_pool.load(this.mGet.getApplicationContext(), shutterSoundResourceId[index], 1);
        this.mSound_shutter = this.mShutter[index];
        setRegisteredSoundMaskID(this.mSound_shutter);
        for (int i = 0; i < SHUTTER_SOUND_COUNT; i++) {
            if (i != index) {
                this.mShutter[i] = this.mSound_pool.load(this.mGet.getApplicationContext(), shutterSoundResourceId[i], 1);
                setRegisteredSoundMaskID(this.mShutter[i]);
            }
        }
        if (this.mSound_shutter == 0) {
            CamLog.d(FaceDetector.TAG, "Shutter Sound Load Failed");
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    if (SoundController.this.checkMediator()) {
                        SoundController.this.mGet.removePostRunnable(this);
                        SoundController.this.mGet.setMainButtonEnable(CameraConstants.SOUNDCONTROLLER_LOCKKEY);
                    }
                }
            });
        }
        if (ModelProperties.isFixedFocusModel()) {
            this.mSound_afSuccess = 0;
            this.mSound_afFail = 0;
        } else {
            this.mSound_afSuccess = this.mSound_pool.load(this.mGet.getApplicationContext(), R.raw.af_success, 1);
            this.mSound_afFail = this.mSound_pool.load(this.mGet.getApplicationContext(), R.raw.af_failure, 1);
            setRegisteredSoundMaskID(this.mSound_afSuccess);
            setRegisteredSoundMaskID(this.mSound_afFail);
        }
        this.mSound_Timer1sec = this.mSound_pool.load(this.mGet.getApplicationContext(), R.raw.cam_timer_1sec, 1);
        setRegisteredSoundMaskID(this.mSound_Timer1sec);
        this.mSound_TimerLast = this.mSound_pool.load(this.mGet.getApplicationContext(), R.raw.cam_timer_last, 1);
        setRegisteredSoundMaskID(this.mSound_TimerLast);
        this.mSound_voiceShutter_cheese = this.mSound_pool_music.load(this.mGet.getApplicationContext(), R.raw.voicesound_cheese, 1);
        this.mSound_voiceShutter_smile = this.mSound_pool_music.load(this.mGet.getApplicationContext(), R.raw.voicesound_smile, 1);
        this.mSound_voiceShutter_whisky = this.mSound_pool_music.load(this.mGet.getApplicationContext(), R.raw.voicesound_whisky, 1);
        this.mSound_voiceShutter_kimchi = this.mSound_pool_music.load(this.mGet.getApplicationContext(), R.raw.voicesound_kimchi, 1);
        this.mSound_voiceShutter_LG = this.mSound_pool_music.load(this.mGet.getApplicationContext(), R.raw.voicesound_lg, 1);
        this.mSound_voiceShutter_torimasu = this.mSound_pool_music.load(this.mGet.getApplicationContext(), R.raw.voicesound_torimasu, 1);
        this.mSound_clearshot_delay = this.mSound_pool.load(this.mGet.getApplicationContext(), R.raw.snd_loop, 1);
        this.mSound_clearshot_snap = this.mSound_pool.load(this.mGet.getApplicationContext(), R.raw.shot_snap, 1);
        if (this.mGet.isAttachMode() && ModelProperties.isSupportShotModeModel()) {
            this.mSound_continuous_shutter = 0;
            return;
        }
        this.mSound_continuous_shutter = this.mSound_pool.load(this.mGet.getApplicationContext(), R.raw.continuous_shot, 1);
        setRegisteredSoundMaskID(this.mSound_continuous_shutter);
    }

    private void loadingCamcorderSoundSource() {
        this.mSound_startRecording = this.mSound_pool.load(this.mGet.getApplicationContext(), R.raw.camstart, 1);
        setRegisteredSoundMaskID(this.mSound_startRecording);
        if (this.mSound_startRecording == 0) {
            CamLog.d(FaceDetector.TAG, "Shutter Sound Load Failed");
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    if (SoundController.this.checkMediator()) {
                        SoundController.this.mGet.removePostRunnable(this);
                        SoundController.this.mGet.setMainButtonEnable(CameraConstants.SOUNDCONTROLLER_LOCKKEY);
                    }
                }
            });
        }
        this.mSound_stopRecording = this.mSound_pool.load(this.mGet.getApplicationContext(), R.raw.camstop, 1);
        setRegisteredSoundMaskID(this.mSound_stopRecording);
    }

    private void loadingCameraSoundSourceHWTunnedInCamcorder() {
        this.mSound_shutter = 0;
        int index = SharedPreferenceUtil.getShutterSoundIndex(this.mGet.getApplicationContext());
        if (index != -1) {
            if (index < 0 || index > 3) {
                index = 0;
            }
            this.msound_capture = this.mSound_pool.load("system/media/audio/camera/cam_snap_" + index + ".ogg", 1);
            this.mSound_shutter = this.msound_capture;
            setRegisteredSoundMaskID(this.msound_capture);
        }
        if (this.mSound_shutter == 0) {
            CamLog.d(FaceDetector.TAG, "Shutter Sound Load Failed");
        }
    }

    private void loadingCameraSoundSourceHWTunned() {
        String SOUND_RESOURCE_PATH = "system/media/audio/camera/";
        String SHUTTER_SOUND0 = "system/media/audio/camera/cam_snap_0.ogg";
        String SHUTTER_SOUND1 = "system/media/audio/camera/cam_snap_1.ogg";
        String SHUTTER_SOUND2 = "system/media/audio/camera/cam_snap_2.ogg";
        String SHUTTER_SOUND3 = "system/media/audio/camera/cam_snap_3.ogg";
        String TIMER_COUNT = "system/media/audio/camera/cameratimer.ogg";
        String TIMER_LAST = "system/media/audio/camera/cameratimerlast3.ogg";
        String AF_SUCCESS = "system/media/audio/camera/af_success.ogg";
        String AF_FAILURE = "system/media/audio/camera/af_failure.ogg";
        String CONTI_SOUND = "system/media/audio/camera/continuous_shot.ogg";
        String VOICE_CHEESE = "system/media/audio/camera/cam_snap_0.ogg";
        String VOICE_SMILE = "system/media/audio/camera/cam_snap_1.ogg";
        String VOICE_WHISKY = "system/media/audio/camera/cam_snap_2.ogg";
        String VOICE_KIMCHI = "system/media/audio/camera/cam_snap_3.ogg";
        String VOICE_LG = "system/media/audio/camera/cam_snap_0.ogg";
        String VOICE_TORIMASU = "system/media/audio/camera/cam_snap_0.ogg";
        String CLEAR_SHOT_LOOP = "system/media/audio/camera/snd_loop.ogg";
        String CLEAR_SHOT_SNAP = "system/media/audio/camera/shot_snap.ogg";
        this.mShutter = new int[SHUTTER_SOUND_COUNT];
        this.mShutter[0] = this.mSound_pool.load("system/media/audio/camera/cam_snap_0.ogg", 1);
        this.mShutter[1] = this.mSound_pool.load("system/media/audio/camera/cam_snap_1.ogg", 1);
        this.mShutter[2] = this.mSound_pool.load("system/media/audio/camera/cam_snap_2.ogg", 1);
        this.mShutter[3] = this.mSound_pool.load("system/media/audio/camera/cam_snap_3.ogg", 1);
        this.mSound_shutter = this.mShutter[0];
        ListPreference pref = this.mGet.findPreference(Setting.KEY_SHUTTER_SOUND);
        if (pref == null) {
            CamLog.w(FaceDetector.TAG, "KEY_SHUTTER_SOUND is not found.");
        } else if (!ShutterSoundProperties.isSupportShutterSoundOff() || pref.getValue().equals(CameraConstants.SMART_MODE_OFF)) {
            index = Integer.parseInt(pref.getValue());
            this.mSound_shutter = this.mShutter[index];
        } else {
            index = Integer.parseInt(pref.getValue());
            this.mSound_shutter = this.mShutter[index];
        }
        setRegisteredSoundMaskID(this.mSound_shutter);
        if (this.mSound_shutter == 0) {
            CamLog.d(FaceDetector.TAG, "Shutter Sound Load Failed");
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    if (SoundController.this.checkMediator()) {
                        SoundController.this.mGet.removePostRunnable(this);
                        SoundController.this.mGet.setMainButtonEnable(CameraConstants.SOUNDCONTROLLER_LOCKKEY);
                    }
                }
            });
        }
        this.mSound_continuous_shutter = this.mSound_pool.load("system/media/audio/camera/continuous_shot.ogg", 1);
        setRegisteredSoundMaskID(this.mSound_continuous_shutter);
        this.mSound_afSuccess = this.mSound_pool.load("system/media/audio/camera/af_success.ogg", 1);
        setRegisteredSoundMaskID(this.mSound_afSuccess);
        this.mSound_afFail = this.mSound_pool.load("system/media/audio/camera/af_failure.ogg", 1);
        setRegisteredSoundMaskID(this.mSound_afFail);
        this.mSound_Timer1sec = this.mSound_pool.load("system/media/audio/camera/cameratimer.ogg", 1);
        setRegisteredSoundMaskID(this.mSound_Timer1sec);
        this.mSound_TimerLast = this.mSound_pool.load("system/media/audio/camera/cameratimerlast3.ogg", 1);
        setRegisteredSoundMaskID(this.mSound_TimerLast);
        this.mSound_voiceShutter_cheese = this.mSound_pool_music.load("system/media/audio/camera/cam_snap_0.ogg", 1);
        this.mSound_voiceShutter_smile = this.mSound_pool_music.load("system/media/audio/camera/cam_snap_1.ogg", 1);
        this.mSound_voiceShutter_whisky = this.mSound_pool_music.load("system/media/audio/camera/cam_snap_2.ogg", 1);
        this.mSound_voiceShutter_kimchi = this.mSound_pool_music.load("system/media/audio/camera/cam_snap_3.ogg", 1);
        this.mSound_voiceShutter_LG = this.mSound_pool_music.load("system/media/audio/camera/cam_snap_0.ogg", 1);
        this.mSound_voiceShutter_torimasu = this.mSound_pool_music.load("system/media/audio/camera/cam_snap_0.ogg", 1);
        this.mSound_clearshot_delay = this.mSound_pool.load("system/media/audio/camera/snd_loop.ogg", 1);
        this.mSound_clearshot_snap = this.mSound_pool.load("system/media/audio/camera/shot_snap.ogg", 1);
    }

    private void loadingCamcorderSoundSourceHWTunned() {
        String SOUND_RESOURCE_PATH = "system/media/audio/camera/";
        String REC_START = "system/media/audio/camera/camstart.ogg";
        String REC_STOP = "system/media/audio/camera/camstop.ogg";
        this.mSound_startRecording = this.mSound_pool.load("system/media/audio/camera/camstart.ogg", 1);
        setRegisteredSoundMaskID(this.mSound_startRecording);
        if (this.mSound_startRecording == 0) {
            CamLog.d(FaceDetector.TAG, "Shutter Sound Load Failed");
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    if (SoundController.this.checkMediator()) {
                        SoundController.this.mGet.removePostRunnable(this);
                        SoundController.this.mGet.setMainButtonEnable(CameraConstants.SOUNDCONTROLLER_LOCKKEY);
                    }
                }
            });
        }
        this.mSound_stopRecording = this.mSound_pool.load("system/media/audio/camera/camstop.ogg", 1);
        setRegisteredSoundMaskID(this.mSound_stopRecording);
    }

    private void buildSoundPoolSoundSource() {
        waitSoundBuildThreadDone();
        if (this.mSound_pool == null) {
            this.mSoundBuildThread = new Thread(new Runnable() {
                public void run() {
                    int app_mode = SoundController.this.mGet.getApplicationMode();
                    SoundController.this.mSound_pool = new SoundPool(6, ShutterSoundProperties.getShutterStreamType(), 0);
                    SoundController.this.mSound_pool.setOnLoadCompleteListener(SoundController.this.completeListener);
                    SoundController.this.mSound_pool_music = new SoundPool(6, 3, 0);
                    SoundController.this.mSound_pool_music.setOnLoadCompleteListener(SoundController.this.completeListener_music);
                    SoundController.this.mSoundSampleIDRegisteredMaskID = -1;
                    SoundController.this.mSoundSampleIDLoadedMaskID = 0;
                    SoundController.this.mMusicSoundSampleIDLoadedMaskID = 0;
                    SoundController.this.mShutterSoundLoaded = false;
                    SoundController.this.mShutterSoundResID = 0;
                    CamLog.d(FaceDetector.TAG, "Sound Load-Start, app_mode:" + app_mode);
                    if (ProjectVariables.isHwTuning()) {
                        if (app_mode == 1) {
                            SoundController.this.loadingCamcorderSoundSourceHWTunned();
                            SoundController.this.loadingCameraSoundSourceHWTunnedInCamcorder();
                        } else if (app_mode == 0) {
                            SoundController.this.loadingCameraSoundSourceHWTunned();
                            SoundController.this.loadingCamcorderSoundSourceHWTunned();
                        }
                    } else if (app_mode == 1) {
                        SoundController.this.loadingCamcorderSoundSource();
                        SoundController.this.loadingCameraSoundSourceInCamcorder();
                    } else if (app_mode == 0) {
                        SoundController.this.loadingCameraSoundSource();
                        SoundController.this.loadingCamcorderSoundSource();
                    }
                    CamLog.d(FaceDetector.TAG, "Sound Load-end");
                }
            });
            this.mSoundBuildThread.start();
        }
    }

    public void initController() {
        if (ShutterSoundProperties.isDisableAudioFuction()) {
            this.mGet.setMainButtonEnable(CameraConstants.SOUNDCONTROLLER_LOCKKEY);
            return;
        }
        buildSoundPoolSoundSource();
        this.mInit = true;
    }

    public void onResume() {
        CamLog.d(FaceDetector.TAG, "onResume-start");
        if (this.mInit) {
            if (ShutterSoundProperties.isDisableAudioFuction()) {
                this.mGet.setMainButtonEnable(CameraConstants.SOUNDCONTROLLER_LOCKKEY);
            } else if (this.mSoundBuildThread == null && this.mSound_pool == null) {
                CamLog.v(FaceDetector.TAG, "retry buildSoundPoolSoundSource");
                buildSoundPoolSoundSource();
            }
            CamLog.d(FaceDetector.TAG, "onResume-end");
        }
    }

    public void onPause() {
        CamLog.d(FaceDetector.TAG, "onPause-start");
        if (!ShutterSoundProperties.isDisableAudioFuction()) {
            waitSoundBuildThreadDone();
        }
        super.onPause();
        CamLog.d(FaceDetector.TAG, "onPause-end");
    }

    public void onDestroy() {
        if (!ShutterSoundProperties.isDisableAudioFuction()) {
            waitSoundBuildThreadDone();
            CamLog.e(FaceDetector.TAG, "onDestroy-start, sound_pool release 1/2");
            if (this.mSound_pool != null) {
                unloadSoundPool(0, true, false);
                unloadSoundPool(1, true, false);
                unloadSoundPool(2, true, false);
                unloadSoundPool(3, true, false);
                this.mShutter = null;
                unloadSoundPool(this.msound_capture, false, false);
                unloadSoundPool(this.mSound_continuous_shutter, false, false);
                unloadSoundPool(this.mSound_afSuccess, false, false);
                unloadSoundPool(this.mSound_afFail, false, false);
                unloadSoundPool(this.mSound_Timer1sec, false, false);
                unloadSoundPool(this.mSound_TimerLast, false, false);
                unloadSoundPool(this.mSound_startRecording, false, false);
                unloadSoundPool(this.mSound_stopRecording, false, false);
                unloadSoundPool(this.mSound_voiceShutter_cheese, false, true);
                unloadSoundPool(this.mSound_voiceShutter_smile, false, true);
                unloadSoundPool(this.mSound_voiceShutter_whisky, false, true);
                unloadSoundPool(this.mSound_voiceShutter_kimchi, false, true);
                unloadSoundPool(this.mSound_voiceShutter_LG, false, true);
                unloadSoundPool(this.mSound_voiceShutter_torimasu, false, true);
                unloadSoundPool(this.mSound_clearshot_delay, false, false);
                unloadSoundPool(this.mSound_clearshot_snap, false, false);
                this.mSound_pool.setOnLoadCompleteListener(null);
                this.mSound_pool.release();
                this.mSound_pool = null;
                this.mSound_pool_music.setOnLoadCompleteListener(null);
                this.mSound_pool_music.release();
                this.mSound_pool_music = null;
            }
            CamLog.e(FaceDetector.TAG, "onDestroy-end, sound_pool release 2/2");
        }
        super.onDestroy();
    }

    private void unloadSoundPool(int index, boolean shutterSound, boolean isMusicStream) {
        CamLog.d(FaceDetector.TAG, "unloadSoundPool, start " + index);
        if (shutterSound) {
            if (this.mShutter != null && this.mShutter[index] > 0 && checkSoundLoaded(this.mShutter[index], false)) {
                CamLog.d(FaceDetector.TAG, "unloadSoundPool, mShutter[index] = " + this.mShutter[index]);
                this.mSound_pool.unload(this.mShutter[index]);
            }
        } else if (isMusicStream) {
            if (index > 0 && checkSoundLoaded(index, true)) {
                CamLog.d(FaceDetector.TAG, "unloadSoundPool_music, index = " + index);
                this.mSound_pool_music.unload(index);
            }
        } else if (index > 0 && checkSoundLoaded(index, false)) {
            CamLog.d(FaceDetector.TAG, "unloadSoundPool, index = " + index);
            this.mSound_pool.unload(index);
        }
        CamLog.d(FaceDetector.TAG, "unloadSoundPool, end ");
    }

    public void waitSoundBuildThreadDone() {
        try {
            if (this.mSoundBuildThread != null && this.mSoundBuildThread.isAlive()) {
                CamLog.d(FaceDetector.TAG, String.format("Wait for sound_pool load..", new Object[0]));
                this.mSoundBuildThread.join();
                this.mSoundBuildThread = null;
                CamLog.d(FaceDetector.TAG, String.format("sound_pool loaded..", new Object[0]));
            }
        } catch (InterruptedException e) {
            CamLog.e(FaceDetector.TAG, String.format("Failed to join sound_pool load thread!", new Object[0]));
            e.printStackTrace();
        }
    }

    private int getSoundIDPlayedBeforeLoaded() {
        return this.mSoundSampleIDBeforeLoaded;
    }

    private void setSoundSampleIDBeforeLoaded(int sampleID) {
        this.mSoundSampleIDBeforeLoaded = sampleID;
    }

    public void soundPlay(int soundSource) {
        this.mAudioManager = (AudioManager) this.mGet.getApplicationContext().getSystemService("audio");
        this.mAudioMode = this.mAudioManager.getRingerMode();
        if (this.mAudioMode == 0 || this.mAudioMode == 1) {
            if (!ShutterSoundProperties.isForcedShutterSound()) {
                return;
            }
            if (!(soundSource == this.mSound_shutter || soundSource == this.mSound_startRecording || soundSource == this.mSound_stopRecording || soundSource == this.mSound_continuous_shutter || soundSource == this.mSound_Timer1sec || soundSource == this.mSound_TimerLast)) {
                return;
            }
        }
        if (!ShutterSoundProperties.isDisableAudioFuction() && this.mSound_pool != null) {
            int result = this.mSound_pool.play(soundSource, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X, 0, 0, RotateView.DEFAULT_TEXT_SCALE_X);
            CamLog.i(FaceDetector.TAG, "mSound_pool.play :" + soundSource + "result :" + result);
            if (result == 0) {
                setSoundSampleIDBeforeLoaded(soundSource);
            }
        }
    }

    public void playShutterSound() {
        if (!ShutterSoundProperties.isSupportShutterSoundOff() || !this.mGet.getSettingValue(Setting.KEY_SHUTTER_SOUND).equals(CameraConstants.SMART_MODE_OFF)) {
            soundPlay(this.mSound_shutter);
        }
    }

    public void playRecordingSound(boolean start) {
        CamLog.d(FaceDetector.TAG, "playRecordingSound : start = " + start);
        if (this.mGet.getApplicationMode() != 0 || ((!this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE).equals(CameraConstants.TYPE_SHOTMODE_PANORAMA) && !this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE).equals(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) && !this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE).equals(CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) || !ShutterSoundProperties.isSupportShutterSoundOff() || !CameraConstants.SMART_MODE_OFF.equals(this.mGet.getSettingValue(Setting.KEY_SHUTTER_SOUND)))) {
            if (start) {
                soundPlay(this.mSound_startRecording);
            } else {
                soundPlay(this.mSound_stopRecording);
            }
        }
    }

    public void playTimerSound(int time) {
        CamLog.d(FaceDetector.TAG, "playTimerSound : time = " + time);
        if (!TelephonyUtil.phoneInCall(this.mGet.getApplicationContext())) {
            if (time <= 3) {
                soundPlay(this.mSound_TimerLast);
            } else {
                soundPlay(this.mSound_Timer1sec);
            }
        }
    }

    public void soundPlaycontinuous(int soundSource) {
        this.mAudioManager = (AudioManager) this.mGet.getApplicationContext().getSystemService("audio");
        this.mAudioMode = this.mAudioManager.getRingerMode();
        if (((this.mAudioMode != 0 && this.mAudioMode != 1) || (ShutterSoundProperties.isForcedShutterSound() && soundSource == this.mSound_continuous_shutter)) && !ShutterSoundProperties.isDisableAudioFuction() && this.mSound_pool != null) {
            CamLog.i(FaceDetector.TAG, "mSound_pool.play " + soundSource);
            this.mContinuousSoundResultID = this.mSound_pool.play(soundSource, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X, 0, MAX_CONTINUOUS_SHOT_SOUND, RotateView.DEFAULT_TEXT_SCALE_X);
            if (this.mContinuousSoundResultID == 0) {
                setSoundSampleIDBeforeLoaded(soundSource);
            }
        }
    }

    public void stopSoundContinuous() {
        this.mAudioManager = (AudioManager) this.mGet.getApplicationContext().getSystemService("audio");
        this.mAudioMode = this.mAudioManager.getRingerMode();
        if (!(((this.mAudioMode == 0 || this.mAudioMode == 1) && !ShutterSoundProperties.isForcedShutterSound()) || ShutterSoundProperties.isDisableAudioFuction() || this.mSound_pool == null)) {
            CamLog.i(FaceDetector.TAG, "mSound_pool.stop " + this.mContinuousSoundResultID);
            if (this.mContinuousSoundResultID != 0) {
                this.mSound_pool.stop(this.mContinuousSoundResultID);
            }
        }
        this.mContinuousSoundResultID = 0;
    }

    public void soundPlayBurstShot(int soundSource, boolean repeat) {
        this.mAudioManager = (AudioManager) this.mGet.getApplicationContext().getSystemService("audio");
        this.mAudioMode = this.mAudioManager.getRingerMode();
        if (((this.mAudioMode != 0 && this.mAudioMode != 1) || (ShutterSoundProperties.isForcedShutterSound() && soundSource == this.mSound_continuous_shutter)) && !ShutterSoundProperties.isDisableAudioFuction() && this.mSound_pool != null) {
            CamLog.i(FaceDetector.TAG, "mSound_pool.play soundSource : " + soundSource);
            if (this.mContinuousSoundResultID != 0) {
                stopSoundBurstShot();
            }
            if (repeat) {
                this.mContinuousSoundResultID = this.mSound_pool.play(soundSource, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X, 10, MAX_BURST_SHOT_SOUND, RotateView.DEFAULT_TEXT_SCALE_X);
            } else {
                this.mSound_pool.play(soundSource, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X, 0, 0, RotateView.DEFAULT_TEXT_SCALE_X);
            }
            if (this.mContinuousSoundResultID == 0) {
                setSoundSampleIDBeforeLoaded(soundSource);
            }
        }
    }

    public void stopSoundBurstShot() {
        this.mAudioManager = (AudioManager) this.mGet.getApplicationContext().getSystemService("audio");
        this.mAudioMode = this.mAudioManager.getRingerMode();
        if (!(((this.mAudioMode == 0 || this.mAudioMode == 1) && !ShutterSoundProperties.isForcedShutterSound()) || ShutterSoundProperties.isDisableAudioFuction() || this.mSound_pool == null || this.mContinuousSoundResultID == 0)) {
            CamLog.i(FaceDetector.TAG, "mSound_pool.stop mContinuousSoundResultID : " + this.mContinuousSoundResultID);
            this.mSound_pool.stop(this.mContinuousSoundResultID);
        }
        this.mContinuousSoundResultID = 0;
    }

    public void playContinuousShutterSound() {
        CamLog.d(FaceDetector.TAG, "playContinuousShutterSound");
        if (!ShutterSoundProperties.isSupportShutterSoundOff() || !CameraConstants.SMART_MODE_OFF.equals(this.mGet.getSettingValue(Setting.KEY_SHUTTER_SOUND))) {
            if (ProjectVariables.useContinuousSound()) {
                soundPlaycontinuous(this.mSound_continuous_shutter);
            } else {
                soundPlay(this.mSound_shutter);
            }
        }
    }

    public void playBurstShotShutterSound(boolean repeat) {
        CamLog.d(FaceDetector.TAG, "playBurstShotShutterSound");
        if (!ShutterSoundProperties.isSupportShutterSoundOff() || !CameraConstants.SMART_MODE_OFF.equals(this.mGet.getSettingValue(Setting.KEY_SHUTTER_SOUND))) {
            if (ProjectVariables.useContinuousSound()) {
                soundPlayBurstShot(this.mSound_continuous_shutter, repeat);
            } else {
                soundPlay(this.mSound_shutter);
            }
        }
    }

    public void playFreePanoramaShutterSound() {
        CamLog.d(FaceDetector.TAG, "playFreePanoramaShutterSound");
        if (!ShutterSoundProperties.isSupportShutterSoundOff() || !CameraConstants.SMART_MODE_OFF.equals(this.mGet.getSettingValue(Setting.KEY_SHUTTER_SOUND))) {
            soundPlay(this.mSound_continuous_shutter);
        }
    }

    public void playAFSound(boolean seccess) {
        CamLog.d(FaceDetector.TAG, "playAFSound : seccess=" + seccess);
        if (seccess) {
            if (checkSoundLoaded(this.mSound_afSuccess, false)) {
                soundPlay(this.mSound_afSuccess);
            }
        } else if (checkSoundLoaded(this.mSound_afFail, false)) {
            soundPlay(this.mSound_afFail);
        }
    }

    public void playClickSound() {
        this.mAudioManager = (AudioManager) this.mGet.getApplicationContext().getSystemService("audio");
        this.mAudioManager.playSoundEffect(0);
    }

    public void playVoiceCommandSound(int soundIndex) {
        CamLog.d(FaceDetector.TAG, "playVoiceCommandSound : soundIndex = " + soundIndex);
        if (this.mSound_pool_music != null) {
            int soundSource = 0;
            switch (soundIndex) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    soundSource = this.mSound_voiceShutter_cheese;
                    break;
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    soundSource = this.mSound_voiceShutter_smile;
                    break;
                case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                    soundSource = this.mSound_voiceShutter_whisky;
                    break;
                case LGKeyRec.EVENT_STARTED /*3*/:
                    soundSource = this.mSound_voiceShutter_kimchi;
                    break;
                case SHUTTER_SOUND_COUNT /*4*/:
                    soundSource = this.mSound_voiceShutter_LG;
                    break;
                case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                    soundSource = this.mSound_voiceShutter_torimasu;
                    break;
            }
            this.voiceCommandStream = this.mSound_pool_music.play(soundSource, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X, 0, 0, RotateView.DEFAULT_TEXT_SCALE_X);
            CamLog.i(FaceDetector.TAG, "voiceCommandStream.play :" + soundSource + "result :" + this.voiceCommandStream);
            if (this.voiceCommandStream == 0) {
                setSoundSampleIDBeforeLoaded(soundSource);
            }
        }
    }

    public void stopVoiceCommandSound() {
        CamLog.d(FaceDetector.TAG, "stopVoiceCommandSound ");
        if (this.mSound_pool_music != null && this.voiceCommandStream != 0) {
            this.mSound_pool_music.stop(this.voiceCommandStream);
        }
    }

    public void playClearShotShutterSound(boolean isEnd) {
        CamLog.d(FaceDetector.TAG, "playClearShotShutterSound");
        if (!ShutterSoundProperties.isSupportShutterSoundOff() || !CameraConstants.SMART_MODE_OFF.equals(this.mGet.getSettingValue(Setting.KEY_SHUTTER_SOUND))) {
            this.mAudioManager = (AudioManager) this.mGet.getApplicationContext().getSystemService("audio");
            this.mAudioMode = this.mAudioManager.getRingerMode();
            if ((this.mAudioMode != 0 && this.mAudioMode != 1) || ShutterSoundProperties.isForcedShutterSound()) {
                if (isEnd) {
                    this.mClearSoundResultID = this.mSound_pool.play(this.mSound_clearshot_delay, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X, 0, 0, RotateView.DEFAULT_TEXT_SCALE_X);
                    return;
                }
                this.mSound_pool.stop(this.mClearSoundResultID);
                this.mSound_pool.play(this.mSound_clearshot_snap, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X, 0, 0, RotateView.DEFAULT_TEXT_SCALE_X);
            }
        }
    }

    public void stopClearShotSound() {
        if (this.mSound_pool != null && this.mClearSoundResultID != 0) {
            this.mSound_pool.stop(this.mClearSoundResultID);
        }
    }

    public void changeShutterSound(int index) {
        if (this.mInit) {
            SharedPreferenceUtil.saveShutterSoundIndex(this.mGet.getApplicationContext(), index);
            if (index >= 0 && this.mShutter != null) {
                this.mSound_shutter = this.mShutter[index];
            }
        }
    }
}
