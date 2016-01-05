package com.lge.voiceshutter.library;

import android.media.AudioRecord;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ISTAudioRecorder {
    public static final int JAVA_BUFFER_INTERVAL = 4000;
    public static final int NUM_JAVA_BUFFER = 80;
    private static final String TAG;
    public static final int TIMER_INTERVAL = 50;
    private List<ByteArray> BackupPool;
    private List<ByteArray> BufferPool;
    private int ChannelConfig;
    private int RecorderBufferSize;
    private int aFormat;
    private AudioRecord aRecorder;
    private int aSource;
    private short bSamples;
    public byte[] buffer;
    private int framePeriod;
    private RecorderState mState;
    private Thread mThread;
    private short nChannels;
    private int read_size;
    private int sRate;
    private final Object syncObj;

    public enum RecorderState {
        INITIALIZING,
        READY,
        RECORDING,
        ERROR,
        STOPPED
    }

    static {
        TAG = ISTAudioRecorder.class.getName();
    }

    public RecorderState getRecorderState() {
        return this.mState;
    }

    public ISTAudioRecorder(int audioSource, int sampleRate, int channelConfig, int audioFormat) {
        this.aRecorder = null;
        this.BufferPool = null;
        this.BackupPool = null;
        this.mThread = null;
        this.syncObj = new Object();
        if (audioFormat == 2) {
            try {
                this.bSamples = (short) 16;
            } catch (Exception e) {
                if (e.getMessage() != null) {
                    Log.e(TAG, e.getMessage());
                } else {
                    Log.e(TAG, "Unknown error occured while initializing recording");
                }
                this.mState = RecorderState.ERROR;
                return;
            }
        }
        this.bSamples = (short) 8;
        this.ChannelConfig = channelConfig;
        if (channelConfig == 16) {
            this.nChannels = (short) 1;
        } else {
            this.nChannels = (short) 2;
        }
        this.aSource = audioSource;
        this.sRate = sampleRate;
        this.aFormat = audioFormat;
        this.framePeriod = (sampleRate * TIMER_INTERVAL) / PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME;
        this.RecorderBufferSize = (((this.framePeriod * NUM_JAVA_BUFFER) * this.bSamples) / 8) * this.nChannels;
        if (this.RecorderBufferSize < AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)) {
            this.RecorderBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
            this.framePeriod = this.RecorderBufferSize / (((this.bSamples * NUM_JAVA_BUFFER) / 8) * this.nChannels);
            Log.w(TAG, "Increasing buffer size to " + Integer.toString(this.RecorderBufferSize));
        }
        this.aRecorder = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, this.RecorderBufferSize);
        if (this.aRecorder.getState() != 1) {
            this.aRecorder = null;
            throw new Exception("AudioRecord initialization failed");
        }
        this.read_size = 0;
        this.buffer = new byte[(this.RecorderBufferSize / NUM_JAVA_BUFFER)];
        this.BufferPool = Collections.synchronizedList(new LinkedList());
        this.BackupPool = Collections.synchronizedList(new LinkedList());
        this.mState = RecorderState.READY;
    }

    public byte[] getBuffer() {
        if (this.BufferPool == null || this.BufferPool == null || this.BufferPool.size() <= 0) {
            return null;
        }
        ByteArray buf = (ByteArray) this.BufferPool.remove(0);
        this.BackupPool.add(buf);
        if (this.BackupPool.size() > 40) {
            ByteArray byteArray = (ByteArray) this.BackupPool.remove(0);
        }
        return buf.array();
    }

    public void release() {
        if (this.mState == RecorderState.RECORDING) {
            stop();
        }
        if (this.aRecorder != null) {
            this.aRecorder.stop();
            this.aRecorder.release();
            this.aRecorder = null;
        }
    }

    public void reset() {
        try {
            if (this.mState != RecorderState.ERROR) {
                release();
                this.aRecorder = new AudioRecord(this.aSource, this.sRate, this.ChannelConfig, this.aFormat, this.RecorderBufferSize);
                this.read_size = 0;
                this.mState = RecorderState.READY;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            this.mState = RecorderState.ERROR;
        }
    }

    private void recording() {
        int size = 0;
        while (true) {
            try {
                SystemClock.sleep(25);
                synchronized (this.syncObj) {
                    if (this.mState != RecorderState.RECORDING) {
                        break;
                    }
                    if (this.aRecorder != null) {
                        size = this.aRecorder.read(this.buffer, 0, this.buffer.length);
                    }
                    if (size > 0) {
                        this.BufferPool.add(new ByteArray(this.buffer, size));
                        int ar_size = this.BufferPool.size();
                        if (ar_size > NUM_JAVA_BUFFER) {
                            Log.e(TAG, "BufferPool overflow: " + ar_size);
                            ByteArray buf = (ByteArray) this.BufferPool.remove(0);
                        }
                    }
                }
            } catch (Throwable th) {
                if (this.aRecorder != null) {
                    this.aRecorder.stop();
                    this.aRecorder.release();
                    this.aRecorder = null;
                }
            }
        }
        if (this.aRecorder != null) {
            this.aRecorder.stop();
            this.aRecorder.release();
            this.aRecorder = null;
        }
        if (this.aRecorder != null) {
            this.aRecorder.stop();
            this.aRecorder.release();
            this.aRecorder = null;
        }
    }

    public void start() {
        if (this.mState == RecorderState.READY) {
            this.mState = RecorderState.RECORDING;
            if (this.aRecorder != null) {
                this.aRecorder.startRecording();
            }
            this.mThread = new Thread() {
                public void run() {
                    Process.setThreadPriority(-19);
                    try {
                        ISTAudioRecorder.this.recording();
                    } catch (Exception e) {
                        Log.e(ISTAudioRecorder.TAG, e.getMessage());
                    }
                }
            };
            this.mThread.start();
            return;
        }
        Log.e(TAG, "start() called on illegal state:" + this.mState);
        this.mState = RecorderState.ERROR;
    }

    public void stop() {
        if (this.mState == RecorderState.RECORDING) {
            synchronized (this.syncObj) {
                this.mState = RecorderState.STOPPED;
            }
            try {
                if (this.mThread != null) {
                    this.mThread.join();
                    this.mThread = null;
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "abrupt thread" + e.getMessage());
            }
        } else {
            Log.e(TAG, "stop() called on illegal state");
            this.mState = RecorderState.ERROR;
        }
        if (this.aRecorder != null) {
            this.aRecorder.stop();
            this.aRecorder.release();
            this.aRecorder = null;
        }
        if (this.BufferPool != null) {
            while (!this.BufferPool.isEmpty()) {
                ByteArray byteArray = (ByteArray) this.BufferPool.remove(0);
            }
            this.BufferPool = null;
        }
        if (this.BackupPool != null) {
            while (!this.BackupPool.isEmpty()) {
                byteArray = (ByteArray) this.BackupPool.remove(0);
            }
            this.BackupPool = null;
        }
    }

    public void dump(String base_dir) {
        Exception e;
        Throwable th;
        File ftargetLocation = new File(base_dir);
        if (!ftargetLocation.exists()) {
            ftargetLocation.mkdirs();
        }
        Date recent = new Date();
        OutputStream fos = null;
        try {
            OutputStream fos2 = new FileOutputStream(base_dir + ((recent.getYear() + 1900) + "_" + recent.getMonth() + "_" + recent.getDate() + "_" + recent.getHours() + "_" + recent.getMinutes() + "_" + recent.getSeconds() + ".pcm"));
            while (!this.BackupPool.isEmpty()) {
                try {
                    ByteArray buf = (ByteArray) this.BackupPool.remove(0);
                    fos2.write(buf.array(), 0, buf.array().length);
                } catch (Exception e2) {
                    e = e2;
                    fos = fos2;
                } catch (Throwable th2) {
                    th = th2;
                    fos = fos2;
                }
            }
            if (fos2 != null) {
                try {
                    fos2.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        } catch (Exception e4) {
            e = e4;
            try {
                Log.e(TAG, " FileOutputStream error:" + e);
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e32) {
                        e32.printStackTrace();
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e322) {
                        e322.printStackTrace();
                    }
                }
                throw th;
            }
        }
    }
}
