package com.lge.media;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.TimedTextEx;
import android.os.Parcel;
import android.util.Log;

public class MediaPlayerEx extends MediaPlayer {
    private static final String IMEDIA_PLAYER = "android.media.IMediaPlayer";
    private static final int LGE_INVOKE_GET_PARAM = 2113929218;
    private static final int LGE_INVOKE_SET_PARAM = 2113929217;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_AUDIO_ZOOM_INFO = 9200;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_AUDIO_ZOOM_INIT = 9201;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_AUDIO_ZOOM_START = 9202;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_HTTP_ADD_HEADER = 9001;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_HTTP_DLNAPLAYBACK = 9106;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_HTTP_FB_SCAN_MODE_START = 9012;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_HTTP_FF_SCAN_MODE_START = 9010;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_HTTP_GET_RESPONSE = 9003;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_HTTP_REMOVE_HEADER = 9002;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_HTTP_REQUEST_OPTION_CONNECTION_TIMEOUT = 9100;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_HTTP_REQUEST_OPTION_ENABLE_HTTPRANGE = 9104;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_HTTP_REQUEST_OPTION_ENABLE_TIMESEEK = 9105;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_HTTP_REQUEST_OPTION_KEEPCONNECTION_ON_PAUSE = 9103;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_HTTP_REQUEST_OPTION_KEEPCONNECTION_ON_PLAY = 9102;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_HTTP_REQUEST_OPTION_READ_TIMEOUT = 9101;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_HTTP_SB_SCAN_MODE_START = 9013;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_HTTP_SCAN_MODE_END = 9014;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_HTTP_SF_SCAN_MODE_START = 9011;
    private static final int LGE_MEDIAPLAYER_KEYPARAM_LGEAUDIO_3DMUSIC = 1862275074;
    private static final int LGE_MEDIAPLAYER_KEYPARAM_LGEAUDIO_CUSTOMEQ = 1862275073;
    private static final int LGE_MEDIAPLAYER_KEYPARAM_LGEAUDIO_EFFECT = 1862275072;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_LGE_HIFI_ENABLED = 6000;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_PLAYBACK_FRAMERATE = 5008;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_PLAY_ON_LOCKSCREEN = 9500;
    private static final int LGE_MEDIAPLAYER_KEYPARAM_SCREENCAPTURE = 9301;
    private static final int LGE_MEDIAPLAYER_KEYPARAM_SET_NORMALIZER = 1862275088;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_SYSTEM_INFO_DIVXSUPPORT = 9401;
    public static final int LGE_MEDIAPLAYER_KEYPARAM_SYSTEM_INFO_HIFISUPPORT = 9402;
    public static final int MEDIA_IMPLEMENT_ERROR_DRM_NOT_AUTHORIZED = 9400;
    public static final int MEDIA_IMPLEMENT_ERROR_NOT_AVAILABLE_NETWORK = 9300;
    public static final int MEDIA_IMPLEMENT_ERROR_NOT_EXIST_AUDIO = 9110;
    public static final int MEDIA_IMPLEMENT_ERROR_NOT_EXIST_VIDEO = 9120;
    public static final int MEDIA_IMPLEMENT_ERROR_NOT_SUPPORT_AUDIO = 9200;
    public static final int MEDIA_IMPLEMENT_ERROR_NOT_SUPPORT_BITRATE = 9130;
    public static final int MEDIA_IMPLEMENT_ERROR_NOT_SUPPORT_MEDIA = 9220;
    public static final int MEDIA_IMPLEMENT_ERROR_NOT_SUPPORT_RESOLUTIONS = 9100;
    public static final int MEDIA_IMPLEMENT_ERROR_NOT_SUPPORT_VIDEO = 9210;
    public static final String MEDIA_MIMETYPE_CONTAINER_MPEG2TS = "video/mp2ts";
    public static final String MEDIA_MIMETYPE_TEXT_ASS = "text/ass";
    public static final String MEDIA_MIMETYPE_TEXT_CLOSEDCAPTION = "text/closedcaption";
    public static final String MEDIA_MIMETYPE_TEXT_EX = "text/ex";
    public static final String MEDIA_MIMETYPE_TEXT_SSA = "text/ssa";
    public static final String MEDIA_MIMETYPE_TEXT_XSUB = "text/xsub";
    private static final int MEDIA_TIMED_TEXT_EX = 600;
    private static final String TAG = "MediaPlayerEX";
    private OnTimedTextExListener mOnTimedTextExListener;

    public interface OnTimedTextExListener {
        void onTimedTextEx(MediaPlayerEx mediaPlayerEx, TimedTextEx timedTextEx);
    }

    private native Bitmap _screenCapture() throws IllegalStateException;

    public boolean setParameter(int r9, android.os.Parcel r10) {
        /* JADX: method processing error */
/*
        Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.ssa.SSATransform.placePhi(SSATransform.java:82)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:50)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r8 = this;
        r5 = 0;
        r2 = android.os.Parcel.obtain();
        r1 = android.os.Parcel.obtain();
        r3 = 0;
        r6 = "android.media.IMediaPlayer";	 Catch:{ RuntimeException -> 0x0034, all -> 0x003d }
        r2.writeInterfaceToken(r6);	 Catch:{ RuntimeException -> 0x0034, all -> 0x003d }
        r6 = 2113929217; // 0x7e000001 float:4.25353E37 double:1.044419804E-314;	 Catch:{ RuntimeException -> 0x0034, all -> 0x003d }
        r2.writeInt(r6);	 Catch:{ RuntimeException -> 0x0034, all -> 0x003d }
        r2.writeInt(r9);	 Catch:{ RuntimeException -> 0x0034, all -> 0x003d }
        r6 = 0;	 Catch:{ RuntimeException -> 0x0034, all -> 0x003d }
        r7 = r10.dataSize();	 Catch:{ RuntimeException -> 0x0034, all -> 0x003d }
        r2.appendFrom(r10, r6, r7);	 Catch:{ RuntimeException -> 0x0034, all -> 0x003d }
        r8.invoke(r2, r1);	 Catch:{ RuntimeException -> 0x0034, all -> 0x003d }
        r6 = r1.readInt();	 Catch:{ RuntimeException -> 0x0034, all -> 0x003d }
        if (r6 != 0) goto L_0x0032;
    L_0x0029:
        r3 = 1;
    L_0x002a:
        r2.recycle();
        r1.recycle();
        r4 = r3;
    L_0x0031:
        return r4;
    L_0x0032:
        r3 = r5;
        goto L_0x002a;
    L_0x0034:
        r0 = move-exception;
        r2.recycle();
        r1.recycle();
        r4 = r3;
        goto L_0x0031;
    L_0x003d:
        r5 = move-exception;
        r2.recycle();
        r1.recycle();
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.media.MediaPlayerEx.setParameter(int, android.os.Parcel):boolean");
    }

    static {
        System.loadLibrary("hook_jni");
    }

    public void setOnTimedTextExListener(OnTimedTextExListener listener) {
        this.mOnTimedTextExListener = listener;
    }

    public boolean setParameter(int key, String value) {
        Parcel p = Parcel.obtain();
        p.writeString(value);
        boolean ret = setParameter(key, p);
        p.recycle();
        return ret;
    }

    public boolean setParameter(int key, int value) {
        Parcel p = Parcel.obtain();
        p.writeInt(value);
        boolean ret = setParameter(key, p);
        p.recycle();
        return ret;
    }

    public void getParameter(int key, Parcel reply) {
        Parcel request = Parcel.obtain();
        try {
            request.writeInterfaceToken(IMEDIA_PLAYER);
            request.writeInt(LGE_INVOKE_GET_PARAM);
            request.writeInt(key);
            invoke(request, reply);
        } catch (RuntimeException e) {
        } finally {
            request.recycle();
        }
    }

    public Parcel getParcelParameter(int key) {
        Parcel p = Parcel.obtain();
        getParameter(key, p);
        return p;
    }

    public String getStringParameter(int key) {
        Parcel p = Parcel.obtain();
        getParameter(key, p);
        String ret = p.readString();
        p.recycle();
        return ret;
    }

    public int getIntParameter(int key) {
        Parcel p = Parcel.obtain();
        getParameter(key, p);
        int ret = p.readInt();
        p.recycle();
        return ret;
    }

    public int setLGAudioEffect(int iEnable, int iType, int iPath, int iMedia) {
        Parcel parcel = Parcel.obtain();
        parcel.writeInt(iEnable);
        parcel.writeInt(iType);
        parcel.writeInt(iPath);
        parcel.writeInt(iMedia);
        if (setParameter((int) LGE_MEDIAPLAYER_KEYPARAM_LGEAUDIO_EFFECT, parcel)) {
            return 0;
        }
        Log.e(TAG, "[setLGAudioEffect] setParameter fail");
        return 1;
    }

    public int setLGSoleCustomEQ(int iNumBand, int iNumGain) {
        Parcel parcel = Parcel.obtain();
        parcel.writeInt(iNumBand);
        parcel.writeInt(iNumGain);
        if (setParameter((int) LGE_MEDIAPLAYER_KEYPARAM_LGEAUDIO_CUSTOMEQ, parcel)) {
            return 0;
        }
        Log.e(TAG, "[setLGSoleCustomEQ] setParameter fail");
        return 1;
    }

    public void setLGSoundNormalizerOnOff(int normalizerOnOff) {
        Parcel parcel = Parcel.obtain();
        parcel.writeInt(normalizerOnOff);
        if (!setParameter((int) LGE_MEDIAPLAYER_KEYPARAM_SET_NORMALIZER, parcel)) {
            Log.e(TAG, "[setLGSoundNormalizerOnOff] setParameter fail");
        }
    }

    public Bitmap screenCapture() {
        Log.e(TAG, "[screenCapture] screenCapture start");
        return _screenCapture();
    }
}
