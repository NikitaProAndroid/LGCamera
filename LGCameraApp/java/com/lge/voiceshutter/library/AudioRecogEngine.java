package com.lge.voiceshutter.library;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.ISTAudioRecorder.RecorderState;

public class AudioRecogEngine {
    public static final boolean PCMDump = false;
    private static final int RECOG_ERROR_INIT_FAIL_MSG = 11;
    private static final int RECOG_RESULT_CALLBAKC_MSG = 1;
    private static final int RECOG_RESULT_ENGINESTOP_MSG = 2;
    private static final int SAMPLE_RATE = 16000;
    public static final int VOICE_ENGINE_ENGLISH = 1;
    public static final int VOICE_ENGINE_JAPAN = 2;
    public static final int VOICE_ENGINE_KOREA = 0;
    private static final int VOICE_ENGINE_START = 1;
    private static final int VOICE_ENGINE_STOP = 0;
    private boolean mAbortThread;
    private Callback mCallback;
    private Thread mEngThread;
    private int mEngineState;
    private Handler mHandler;
    private LGKeyRec mLGR;
    private ISTAudioRecorder mRecorder;
    private int mVoiceEngineKind;
    private String recogResult;

    public interface Callback {
        void onAudioEngineStartCallback(int i);

        void onAudioEngineStopCallback(int i);

        void onAudioRecogErrorCallback(int i);

        void onAudioRecogResultCallback(int i);
    }

    public AudioRecogEngine(Callback callback, int kind) {
        this.mVoiceEngineKind = VOICE_ENGINE_KOREA;
        this.mCallback = null;
        this.mRecorder = null;
        this.mLGR = null;
        this.mEngThread = null;
        this.mAbortThread = PCMDump;
        this.mEngineState = VOICE_ENGINE_KOREA;
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case AudioRecogEngine.VOICE_ENGINE_START /*1*/:
                        AudioRecogEngine.this.mCallback.onAudioRecogResultCallback(AudioRecogEngine.VOICE_ENGINE_START);
                    case AudioRecogEngine.VOICE_ENGINE_JAPAN /*2*/:
                        AudioRecogEngine.this.stop();
                    case AudioRecogEngine.RECOG_ERROR_INIT_FAIL_MSG /*11*/:
                        AudioRecogEngine.this.mCallback.onAudioRecogErrorCallback(AudioRecogEngine.VOICE_ENGINE_START);
                    default:
                }
            }
        };
        this.mCallback = callback;
        this.mEngineState = VOICE_ENGINE_KOREA;
        this.mVoiceEngineKind = kind;
    }

    public void start() {
        CamLog.d(FaceDetector.TAG, "AudioRecogEngine-start : mEngineState = " + this.mEngineState + ", mVoiceEngineKind = " + this.mVoiceEngineKind);
        if (this.mEngineState != VOICE_ENGINE_START) {
            this.mEngineState = VOICE_ENGINE_START;
            if (this.mLGR == null) {
                this.mLGR = new LGKeyRec(this.mVoiceEngineKind);
            }
            if (Initialize()) {
                if (this.mRecorder != null) {
                    this.mRecorder.start();
                }
                if (this.mEngThread != null) {
                    this.mEngThread.start();
                }
                CamLog.d(FaceDetector.TAG, "AudioRecogEngine : Voice Shutter engine Start.");
                Log.d("jaemin.joh", "MSG_SHOW_LISTENING");
                return;
            }
            CamLog.d(FaceDetector.TAG, "AudioRecogEngine : Fail Voice Shutter engine initialization");
            if (this.mHandler != null) {
                this.mHandler.sendEmptyMessage(VOICE_ENGINE_JAPAN);
                this.mHandler.sendEmptyMessage(RECOG_ERROR_INIT_FAIL_MSG);
            }
        }
    }

    public void stop() {
        if (this.mEngineState == VOICE_ENGINE_START) {
            this.mEngineState = VOICE_ENGINE_KOREA;
            if (!this.mAbortThread) {
                this.mAbortThread = true;
                try {
                    if (this.mEngThread != null) {
                        this.mEngThread.join();
                        this.mEngThread = null;
                    }
                } catch (InterruptedException e) {
                    CamLog.e(FaceDetector.TAG, "AudioRecogEngine : InterruptedException in stop():" + e);
                }
            }
            if (this.mLGR != null) {
                this.mLGR.DestroyRecognizer();
                this.mLGR = null;
            }
            if (this.mRecorder != null) {
                this.mRecorder.release();
                this.mRecorder = null;
            }
            CamLog.d(FaceDetector.TAG, "AudioRecogEngine : Voice Shutter engine is stopped.");
        }
    }

    private boolean Initialize() {
        this.mRecorder = new ISTAudioRecorder(6, SAMPLE_RATE, 16, VOICE_ENGINE_JAPAN);
        if (this.mRecorder.getRecorderState() == RecorderState.ERROR) {
            this.mRecorder.release();
            this.mRecorder = null;
            CamLog.d(FaceDetector.TAG, "Fail to open Audio Recorder");
            return PCMDump;
        }
        try {
            if (this.mLGR != null) {
                this.mLGR.Initialize();
                this.mLGR.Start();
            }
            this.mAbortThread = PCMDump;
            this.mEngThread = new Thread(new Runnable() {
                public void run() {
                    boolean bAdvance = true;
                    if (AudioRecogEngine.this.mLGR == null) {
                        AudioRecogEngine.this.mAbortThread = true;
                        AudioRecogEngine.this.mHandler.sendEmptyMessage(AudioRecogEngine.VOICE_ENGINE_JAPAN);
                        CamLog.d(FaceDetector.TAG, "mEngThread stop! mLGR is null. ");
                        return;
                    }
                    while (!AudioRecogEngine.this.mAbortThread) {
                        int event;
                        if (bAdvance) {
                            event = AudioRecogEngine.this.mLGR.Advance();
                        } else {
                            event = 8;
                            bAdvance = true;
                        }
                        switch (event) {
                            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                                AudioRecogEngine.this.recogResult = "recognition results: [[type=RAW, text=OK!!!]]";
                                Log.d("jaemin.joh", AudioRecogEngine.this.recogResult);
                                Log.d("jaemin.joh", "MSG_SHOW_RESULTS_IN_BROWSER");
                                if (AudioRecogEngine.this.mRecorder != null) {
                                    AudioRecogEngine.this.mRecorder.stop();
                                }
                                if (AudioRecogEngine.this.mLGR != null) {
                                    AudioRecogEngine.this.mLGR.Stop();
                                }
                                AudioRecogEngine.this.mAbortThread = true;
                                if (AudioRecogEngine.this.mHandler == null) {
                                    break;
                                }
                                AudioRecogEngine.this.mHandler.sendEmptyMessage(AudioRecogEngine.VOICE_ENGINE_START);
                                AudioRecogEngine.this.mHandler.sendEmptyMessage(AudioRecogEngine.VOICE_ENGINE_JAPAN);
                                break;
                            case LGKeyRec.EVENT_NEED_MORE_AUDIO /*8*/:
                                if (AudioRecogEngine.this.mRecorder == null) {
                                    break;
                                }
                                byte[] buf = AudioRecogEngine.this.mRecorder.getBuffer();
                                if (buf != null && AudioRecogEngine.this.mLGR != null) {
                                    AudioRecogEngine.this.mLGR.PutAudio(buf, AudioRecogEngine.VOICE_ENGINE_KOREA, buf.length, AudioRecogEngine.PCMDump);
                                    break;
                                }
                                SystemClock.sleep(25);
                                bAdvance = AudioRecogEngine.PCMDump;
                                break;
                            default:
                                break;
                        }
                    }
                    Log.e("guejun.jung", "thread break");
                }
            });
            return true;
        } catch (IllegalStateException e) {
            CamLog.e(FaceDetector.TAG, "IllegalStateException  mLGR.Initialize() :" + e);
            return PCMDump;
        }
    }
}
