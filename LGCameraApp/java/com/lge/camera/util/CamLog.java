package com.lge.camera.util;

import android.util.Log;

public class CamLog {
    private static boolean LOG_ON = false;
    private static final int REAL_METHOD_POS = 2;
    private static int UI_HASH_CODE;

    static {
        LOG_ON = true;
        UI_HASH_CODE = 0;
    }

    private static String prefix() {
        StackTraceElement realMethod = new Throwable().getStackTrace()[REAL_METHOD_POS];
        return "[" + realMethod.getFileName() + ":" + realMethod.getLineNumber() + ":" + realMethod.getMethodName() + "()-" + "[Thread:" + (UI_HASH_CODE == Thread.currentThread().hashCode() ? "UI" : "Other") + "] ";
    }

    public static void setUiThreadHashCode(int uiHashCode) {
        UI_HASH_CODE = uiHashCode;
    }

    public static void setLogOn(boolean enable) {
        LOG_ON = enable;
    }

    public static boolean getLogOn() {
        return LOG_ON;
    }

    public static void d(String tag, String msg) {
        if (LOG_ON) {
            Log.d(tag, prefix() + msg);
        }
    }

    public static void i(String tag, String msg) {
        if (LOG_ON) {
            Log.i(tag, prefix() + msg);
        }
    }

    public static void e(String tag, String msg) {
        Log.e(tag, prefix() + msg);
    }

    public static void v(String tag, String msg) {
        if (LOG_ON) {
            Log.v(tag, prefix() + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (LOG_ON) {
            Log.w(tag, prefix() + msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (LOG_ON) {
            Log.d(tag, prefix() + msg, tr);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (LOG_ON) {
            Log.i(tag, prefix() + msg, tr);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (LOG_ON) {
            Log.e(tag, prefix() + msg, tr);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (LOG_ON) {
            Log.v(tag, prefix() + msg, tr);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (LOG_ON) {
            Log.w(tag, prefix() + msg, tr);
        }
    }
}
