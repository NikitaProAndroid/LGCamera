package com.lge.morpho.core;

public class MorphoSensorFusion {
    public static final int MAXIMUM_DATA_SIZE = 512;
    public static final int MODE_USE_ACCELEROMETER_AND_MAGNETIC_FIELD = 3;
    public static final int MODE_USE_ALL_SENSORS = 0;
    public static final int MODE_USE_GYROSCOPE = 1;
    public static final int MODE_USE_GYROSCOPE_AND_ROTATION_VECTOR = 4;
    public static final int MODE_USE_GYROSCOPE_WITH_ACCELEROMETER = 2;
    public static final int OFFSET_MODE_DYNAMIC = 1;
    public static final int OFFSET_MODE_STATIC = 0;
    public static final int ROTATE_0 = 0;
    public static final int ROTATE_180 = 2;
    public static final int ROTATE_270 = 3;
    public static final int ROTATE_90 = 1;
    public static final int SENSOR_TYPE_ACCELEROMETER = 1;
    public static final int SENSOR_TYPE_GYROSCOPE = 0;
    public static final int SENSOR_TYPE_MAGNETIC_FIELD = 2;
    public static final int SENSOR_TYPE_ROTATION_VECTOR = 3;
    public static final int STATE_CALC_OFFSET = 0;
    public static final int STATE_PROCESS = 1;
    private boolean mIsInitialized;
    private int mNative;

    public static class SensorData {
        public long mTimeStamp;
        public double[] mValues;

        public SensorData(long time_stamp, float[] values) {
            this.mTimeStamp = time_stamp;
            this.mValues = new double[values.length];
            for (int i = MorphoSensorFusion.STATE_CALC_OFFSET; i < values.length; i += MorphoSensorFusion.STATE_PROCESS) {
                this.mValues[i] = (double) values[i];
            }
        }

        public SensorData(long time_stamp, double[] values) {
            this.mTimeStamp = time_stamp;
            this.mValues = (double[]) values.clone();
        }
    }

    private final native int calc(int i);

    private final native int createNativeObject();

    private final native void deleteNativeObject(int i);

    private final native int finish(int i);

    private final native int initialize(int i);

    private static final native String nativeGetVersion();

    private final native int outputRotationAngle(int i, double[] dArr);

    private final native int outputRotationMatrix3x3(int i, int i2, double[] dArr);

    private final native int setAppState(int i, int i2);

    private final native int setMode(int i, int i2);

    private final native int setOffset(int i, SensorData sensorData, int i2);

    private final native int setOffsetMode(int i, int i2);

    private final native int setRotation(int i, int i2);

    private final native int setSensorData(int i, Object[] objArr, int i2);

    private final native int setSensorReliability(int i, int i2, int i3);

    static {
        try {
            System.loadLibrary("morpho_sensor_fusion_4");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    public static String getVersion() {
        return nativeGetVersion();
    }

    public MorphoSensorFusion() {
        this.mNative = STATE_CALC_OFFSET;
        this.mIsInitialized = false;
        int ret = createNativeObject();
        if (ret != 0) {
            this.mNative = ret;
        } else {
            this.mNative = STATE_CALC_OFFSET;
        }
    }

    public int initialize() {
        int ret;
        if (this.mNative != 0) {
            ret = initialize(this.mNative);
        } else {
            ret = Error.ERROR_STATE;
        }
        if (ret == 0) {
            this.mIsInitialized = true;
        }
        return ret;
    }

    public boolean isReady() {
        if (this.mNative == 0 || !this.mIsInitialized) {
            return false;
        }
        return true;
    }

    public int finish() {
        if (!isReady()) {
            return Error.ERROR_STATE;
        }
        int ret = finish(this.mNative);
        deleteNativeObject(this.mNative);
        this.mNative = STATE_CALC_OFFSET;
        return ret;
    }

    public int setMode(int mode) {
        if (isReady()) {
            return setMode(this.mNative, mode);
        }
        return Error.ERROR_STATE;
    }

    public int setAppState(int state) {
        if (isReady()) {
            return setAppState(this.mNative, state);
        }
        return Error.ERROR_STATE;
    }

    public int setRotation(int rotation) {
        if (isReady()) {
            return setRotation(this.mNative, rotation);
        }
        return Error.ERROR_STATE;
    }

    public int setSensorReliability(int rel, int sensor_type) {
        if (isReady()) {
            return setSensorReliability(this.mNative, rel, sensor_type);
        }
        return Error.ERROR_STATE;
    }

    public int setOffsetMode(int offset_mode) {
        if (isReady()) {
            return setOffsetMode(this.mNative, offset_mode);
        }
        return Error.ERROR_STATE;
    }

    public int setOffset(SensorData data, int sensor_type) {
        if (isReady()) {
            return setOffset(this.mNative, data, sensor_type);
        }
        return Error.ERROR_STATE;
    }

    public int setSensorData(Object[] data, int sensor_type) {
        if (isReady()) {
            return setSensorData(this.mNative, data, sensor_type);
        }
        return Error.ERROR_STATE;
    }

    public int calc() {
        if (isReady()) {
            return calc(this.mNative);
        }
        return Error.ERROR_STATE;
    }

    public int outputRotationMatrix3x3(int sensor_type, double[] dst_mat) {
        if (isReady()) {
            return outputRotationMatrix3x3(this.mNative, sensor_type, dst_mat);
        }
        return Error.ERROR_STATE;
    }

    public int outputRotationAngle(double[] angle) {
        if (isReady()) {
            return outputRotationAngle(this.mNative, angle);
        }
        return Error.ERROR_STATE;
    }
}
