package com.lge.hardware.sensors;

public class SensorUserCal {
    public static native synchronized int performUserCal(byte b, byte b2);

    static {
        System.loadLibrary("hook_jni");
    }
}
