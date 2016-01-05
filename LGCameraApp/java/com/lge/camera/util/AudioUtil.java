package com.lge.camera.util;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioSystem;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.olaworks.library.FaceDetector;
import com.lge.systemservice.core.LGContext;
import com.lge.systemservice.core.VolumeVibratorManager;
import com.lge.voiceshutter.library.LGKeyRec;

public class AudioUtil {
    public static final String ACTION_AUDIO_STOP_NOTIFICATION = "com.lge.media.STOP_NOTIFICATION";
    public static boolean IS_MUTE_NOTIFICATION_STREAM;
    public static boolean IS_MUTE_SYSTEM_STREAM;
    public static int requestAudioFocusCount;
    private static boolean sIsStreamMuted;

    static {
        IS_MUTE_NOTIFICATION_STREAM = false;
        IS_MUTE_SYSTEM_STREAM = false;
        requestAudioFocusCount = 0;
        sIsStreamMuted = false;
    }

    public static void pauseAudioPlayback(Context context) {
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        context.sendBroadcast(i);
        if (ModelProperties.getCarrierCode() == 2) {
            Intent m = new Intent("com.iloen.melon.musicservicecommand");
            m.putExtra("command", "pause");
            context.sendBroadcast(m);
        }
        setAudioFocus(context, true);
    }

    public static void resumeAudioPlayback(Context context) {
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "togglepause");
        context.sendBroadcast(i);
        setAudioFocus(context, false);
    }

    public static void setAudioFocus(Context context, boolean requestAudioFocus) {
        AudioManager am = (AudioManager) context.getSystemService("audio");
        if (requestAudioFocus) {
            CamLog.d(FaceDetector.TAG, "++ Get audiofocus - not music pause");
            am.requestAudioFocus(null, 3, 2);
            requestAudioFocusCount++;
            return;
        }
        CamLog.d(FaceDetector.TAG, "-- Loose audioFocus");
        am.abandonAudioFocus(null);
        requestAudioFocusCount--;
        if (requestAudioFocusCount < 0) {
            requestAudioFocusCount = 0;
        }
    }

    public static void setAudioFocus(Context context, boolean requestAudioFocus, boolean isTransient) {
        if (isTransient) {
            setAudioFocus(context, requestAudioFocus);
            return;
        }
        AudioManager am = (AudioManager) context.getSystemService("audio");
        if (am == null) {
            return;
        }
        if (requestAudioFocus) {
            CamLog.d(FaceDetector.TAG, "++ Get audiofocus-stopAudioPlayback by get audiofocus");
            am.requestAudioFocus(null, 3, 1);
            requestAudioFocusCount++;
            return;
        }
        CamLog.d(FaceDetector.TAG, "-- Loose audioFocus");
        am.abandonAudioFocus(null);
        requestAudioFocusCount--;
        if (requestAudioFocusCount < 0) {
            requestAudioFocusCount = 0;
        }
    }

    public static void checkAudioFocus(Context context) {
        if (requestAudioFocusCount != 0) {
            CamLog.w(FaceDetector.TAG, "Check requestAudioFocusCount : current count is = " + requestAudioFocusCount);
            CamLog.w(FaceDetector.TAG, "Check requestAudioFocusCount : doing abandonAudioFocus");
            ((AudioManager) context.getSystemService("audio")).abandonAudioFocus(null);
            requestAudioFocusCount = 0;
        }
    }

    public static void setStopNotificationStream(Context context) {
        context.sendBroadcast(new Intent(ACTION_AUDIO_STOP_NOTIFICATION));
    }

    public static void setMuteNotificationStream(Context context, boolean set) {
        AudioManager am = (AudioManager) context.getSystemService("audio");
        if (am == null) {
            return;
        }
        if (set) {
            if (am.getStreamVolume(5) != 0) {
                CamLog.d(FaceDetector.TAG, "set mute to notification stream : ON");
                am.setStreamMute(5, true);
                IS_MUTE_NOTIFICATION_STREAM = true;
            }
        } else if (IS_MUTE_NOTIFICATION_STREAM) {
            CamLog.d(FaceDetector.TAG, "set mute to notification stream : OFF");
            am.setStreamMute(5, false);
            IS_MUTE_NOTIFICATION_STREAM = false;
        }
    }

    public static void setMuteSystemStream(Context context, boolean set) {
        AudioManager am = (AudioManager) context.getSystemService("audio");
        if (am == null) {
            return;
        }
        if (set) {
            if (am.getStreamVolume(1) != 0) {
                CamLog.d(FaceDetector.TAG, "set mute to notification stream : ON");
                am.setStreamMute(1, true);
                IS_MUTE_SYSTEM_STREAM = true;
            }
        } else if (IS_MUTE_SYSTEM_STREAM) {
            CamLog.d(FaceDetector.TAG, "set mute to notification stream : OFF");
            am.setStreamMute(1, false);
            IS_MUTE_SYSTEM_STREAM = false;
        }
    }

    public static void setAudiodevice(Context context, int Orientation) {
        String direction = "90";
        switch (Orientation) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                direction = "0";
                break;
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                direction = "90";
                break;
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                direction = "180";
                break;
            case LGKeyRec.EVENT_STARTED /*3*/:
                direction = "270";
                break;
            case LGKeyRec.EVENT_STOPPED /*4*/:
                direction = "360";
                break;
        }
        AudioManager am = (AudioManager) context.getSystemService("audio");
        if (am != null) {
            String value = "AUDIO_ZOOMING_MODE=" + direction;
            am.setParameters(value);
            CamLog.d(FaceDetector.TAG, "===>" + value);
        }
    }

    public static boolean getHasMic(Context context) {
        if (((AudioManager) context.getSystemService("audio")).getParameters("isWiredMic").equals(CameraConstants.ONEKEY_CONTROL_ENABLE_STRING)) {
            return true;
        }
        return false;
    }

    public static boolean isBluetoothA2dpOn() {
        if (AudioSystem.getDeviceConnectionState(128, "") == 0) {
            return false;
        }
        return true;
    }

    public static boolean isWiredHeadsetOn() {
        if (AudioSystem.getDeviceConnectionState(4, "") == 0 && AudioSystem.getDeviceConnectionState(8, "") == 0) {
            return false;
        }
        return true;
    }

    public static boolean isAudioManagerCallStatus(Context context) {
        AudioManager am = (AudioManager) context.getSystemService("audio");
        if (am != null) {
            int mode = am.getMode();
            if (mode == 2 || mode == 3 || mode == 1) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAudioRecording() {
        if (AudioSystem.getParameters("audiorecording_state").contains(CameraConstants.SMART_MODE_OFF)) {
            return false;
        }
        return true;
    }

    public static void setStreamMute(Context context, int[] stream, boolean state) {
        AudioManager am = (AudioManager) context.getSystemService("audio");
        CamLog.d("Audio", "setStreamMute = " + state);
        if (am == null) {
            return;
        }
        if (!state || !sIsStreamMuted) {
            if (state || sIsStreamMuted) {
                for (int streamMute : stream) {
                    am.setStreamMute(streamMute, state);
                }
                sIsStreamMuted = state;
            }
        }
    }

    public static void setStreamMute(Context context, boolean state) {
        setStreamMute(context, new int[]{1, 5, 2}, state);
    }

    public static void setVibrationMute(Context context, boolean state) {
        ((VolumeVibratorManager) new LGContext(context).getLGSystemService("volumevibrator")).setVibrateMute(state);
    }
}
