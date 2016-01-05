package com.lge.camera.listeners;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.lge.camera.util.CamLog;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import com.lge.morpho.core.MorphoSensorFusion;
import com.lge.morpho.core.MorphoSensorFusion.SensorData;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;

public class FreePanoramaSensorEventListener implements SensorEventListener {
    private static final int MATRIX_SIZE = 16;
    private static final float NS2MS = 1.0E-6f;
    private long accelerometerTimeStamp;
    private float[] accelerometerValues;
    private float[] inRM;
    boolean isSetOffset;
    private Sensor mAccelerometer;
    private int mCameraOrientation;
    private FreePanoramaSensorEventListenerFunction mGet;
    private Sensor mGyroscope;
    private float[] mGyroscopeCorrectValue;
    private float[] mGyroscopeValue;
    private ArrayList<float[]> mGyroscopeValueList;
    private Sensor mMagneticField;
    private ArrayList<SensorData> mPartOfAccelerometerList;
    private ArrayList<SensorData> mPartOfGyroscopeList;
    private ArrayList<SensorData> mPartOfMagneticFieldList;
    private ArrayList<SensorData> mPartOfOrientationList;
    private ArrayList<SensorData> mPartOfRotationVectorList;
    private Sensor mRotationVector;
    private Object mSensorLockObj;
    private SensorManager mSensorManager;
    private int mWaitTime;
    private long magneticTimeStamp;
    private float[] magneticValues;
    private float[] orientationValues;
    private long prev_timestamp;

    public interface FreePanoramaSensorEventListenerFunction {
        MorphoSensorFusion getMorphoSensorFusion();

        int getPanoramaState();

        int getSensorFusionMode();

        void setPanoramaEngineState(int i);

        void setSensorCorrectionGuideCounter(int i);

        void setVisibleResetButton(boolean z);

        void setVisibleSensorCorrectionGuide(boolean z);

        void setVisibleTakingGuide(boolean z);
    }

    public FreePanoramaSensorEventListener(FreePanoramaSensorEventListenerFunction function) {
        this.mGyroscope = null;
        this.mAccelerometer = null;
        this.mMagneticField = null;
        this.mRotationVector = null;
        this.mGyroscopeValue = new float[3];
        this.mGyroscopeCorrectValue = new float[3];
        this.orientationValues = new float[3];
        this.magneticValues = null;
        this.accelerometerValues = null;
        this.prev_timestamp = 0;
        this.inRM = new float[MATRIX_SIZE];
        this.mSensorLockObj = new Object();
        this.isSetOffset = false;
        this.mGet = function;
    }

    public int getCameraOrientation() {
        return this.mCameraOrientation;
    }

    public void setCameraOrientation(int orientation) {
        this.mCameraOrientation = orientation;
    }

    public Sensor getGyroscope() {
        return this.mGyroscope;
    }

    public Sensor getAccelerometer() {
        return this.mAccelerometer;
    }

    public Sensor getMagneticField() {
        return this.mMagneticField;
    }

    public Sensor getRotationVector() {
        return this.mRotationVector;
    }

    public ArrayList<float[]> getGyroscopeValueList() {
        return this.mGyroscopeValueList;
    }

    public float[] getGyroscopeValue() {
        return this.mGyroscopeValue;
    }

    public void setWaitTime(int time) {
        this.mWaitTime = time;
    }

    public int getWaitTime() {
        return this.mWaitTime;
    }

    public Object getSensorLockObj() {
        return this.mSensorLockObj;
    }

    public void initSensorManager(Activity activity) {
        if (this.mSensorManager == null) {
            this.mSensorManager = (SensorManager) activity.getSystemService("sensor");
            for (Sensor sensor : this.mSensorManager.getSensorList(-1)) {
                if (sensor.getType() == 4) {
                    this.mGyroscope = this.mSensorManager.getDefaultSensor(4);
                    this.mGyroscopeValueList = new ArrayList();
                }
                if (sensor.getType() == 1) {
                    this.mAccelerometer = this.mSensorManager.getDefaultSensor(1);
                }
                if (sensor.getType() == 2) {
                    this.mMagneticField = this.mSensorManager.getDefaultSensor(2);
                }
                if (sensor.getType() == 11) {
                    this.mRotationVector = this.mSensorManager.getDefaultSensor(11);
                }
            }
            this.mPartOfGyroscopeList = new ArrayList();
            this.mPartOfAccelerometerList = new ArrayList();
            this.mPartOfMagneticFieldList = new ArrayList();
            this.mPartOfOrientationList = new ArrayList();
            this.mPartOfRotationVectorList = new ArrayList();
        }
    }

    public void registSensorManager() {
        if (this.mGyroscope != null) {
            this.mSensorManager.registerListener(this, this.mGyroscope, 1);
        }
        if (this.mAccelerometer != null) {
            this.mSensorManager.registerListener(this, this.mAccelerometer, 1);
        }
        if (this.mMagneticField != null) {
            this.mSensorManager.registerListener(this, this.mMagneticField, 1);
        }
        if (this.mRotationVector != null) {
            this.mSensorManager.registerListener(this, this.mRotationVector, 1);
        }
        checkSensors();
    }

    private void checkSensors() {
        CamLog.d(FaceDetector.TAG, "checkSensors Start");
        if (this.mSensorManager != null) {
            CamLog.d(FaceDetector.TAG, "print all sensors");
            for (Sensor sensor : this.mSensorManager.getSensorList(-1)) {
                CamLog.d(FaceDetector.TAG, "Sensor Type=" + sensor.getType() + " Name=" + sensor.getName());
            }
        } else {
            CamLog.d(FaceDetector.TAG, "mSensorManager is null");
        }
        CamLog.d(FaceDetector.TAG, "Can I use mGyroscope ? =" + this.mGyroscope);
        CamLog.d(FaceDetector.TAG, "Can I use mAccelerometer ? =" + this.mAccelerometer);
        CamLog.d(FaceDetector.TAG, "Can I use mMagneticField ? =" + this.mMagneticField);
        CamLog.d(FaceDetector.TAG, "Can I use mRotationVector ? =" + this.mRotationVector);
        CamLog.d(FaceDetector.TAG, "checkSensors End");
    }

    public void unRegistSensorManager() {
        if (this.mSensorManager != null) {
            this.mSensorManager.unregisterListener(this);
            this.mSensorManager = null;
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor == this.mAccelerometer) {
            CamLog.d(FaceDetector.TAG, "onAccuracyChanged Accelerometer accuracy->" + accuracy);
        }
        if (sensor == this.mMagneticField) {
            CamLog.d(FaceDetector.TAG, "onAccuracyChanged MagneticField accuracy->" + accuracy);
        }
    }

    public void onSensorChanged(SensorEvent event) {
        synchronized (this.mSensorLockObj) {
            SensorData sd = new SensorData(event.timestamp, event.values);
            if (event.sensor == this.mGyroscope) {
                doGyroscopeChanged(event, sd);
            }
            if (event.sensor == this.mAccelerometer) {
                this.accelerometerValues = (float[]) event.values.clone();
                this.accelerometerTimeStamp = event.timestamp;
                this.mPartOfAccelerometerList.add(sd);
            }
            if (event.sensor == this.mMagneticField) {
                this.magneticValues = (float[]) event.values.clone();
                this.magneticTimeStamp = event.timestamp;
                this.mPartOfMagneticFieldList.add(sd);
            }
            if (event.sensor == this.mRotationVector) {
                this.mPartOfRotationVectorList.add(sd);
            }
            updateOrientation();
        }
    }

    private void doGyroscopeChanged(SensorEvent event, SensorData sd) {
        if (this.mGet.getPanoramaState() == 2 && this.mWaitTime < PanoramaApplication.SENSOR_CORRECTION_TIME_EVERYTIME && this.mWaitTime >= 0) {
            int pre_time = this.mWaitTime;
            if (this.prev_timestamp != 0) {
                this.mWaitTime = (int) (((float) this.mWaitTime) + (((float) (sd.mTimeStamp - this.prev_timestamp)) * NS2MS));
            }
            if (this.mWaitTime >= PanoramaApplication.SENSOR_CORRECTION_TIME_EVERYTIME) {
                this.mGet.getMorphoSensorFusion().setAppState(1);
                this.mGet.setVisibleSensorCorrectionGuide(false);
                this.mGet.setPanoramaEngineState(3);
                this.mGet.setVisibleTakingGuide(true);
            } else if (this.mWaitTime > PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME) {
                if (pre_time <= PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME) {
                    this.mGet.getMorphoSensorFusion().setAppState(0);
                }
                this.mGet.setSensorCorrectionGuideCounter(this.mWaitTime / PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME);
                if (this.mGyroscope != null) {
                    this.mGyroscopeValueList.add(this.mGyroscopeValue.clone());
                }
            }
        }
        this.mPartOfGyroscopeList.add(sd);
        this.prev_timestamp = event.timestamp;
    }

    private void updateOrientation() {
        if (this.magneticValues != null && this.accelerometerValues != null) {
            SensorManager.getRotationMatrix(this.inRM, null, this.accelerometerValues, this.magneticValues);
            SensorManager.getOrientation(this.inRM, this.orientationValues);
            this.mPartOfOrientationList.add(new SensorData(this.accelerometerTimeStamp > this.magneticTimeStamp ? this.accelerometerTimeStamp : this.magneticTimeStamp, this.orientationValues));
            this.magneticValues = null;
            this.accelerometerValues = null;
        }
    }

    public boolean isUseSensor() {
        if (this.mGyroscope == null && ((this.mAccelerometer == null || this.mMagneticField == null) && this.mRotationVector == null)) {
            return false;
        }
        return true;
    }

    public void getSensorMatrix(double[] gyro_mat, double[] rv_mat, double[] ac_mat) {
        boolean next;
        do {
            if (this.mPartOfGyroscopeList.size() > 0) {
                setInputSensorData(getSensorDataArray(this.mPartOfGyroscopeList), 0);
            }
            if (this.mPartOfAccelerometerList.size() > 0) {
                setInputSensorData(getSensorDataArray(this.mPartOfAccelerometerList), 1);
            }
            if (this.mPartOfMagneticFieldList.size() > 0) {
                setInputSensorData(getSensorDataArray(this.mPartOfMagneticFieldList), 2);
            }
            if (this.mPartOfRotationVectorList.size() > 0) {
                setInputSensorData(getSensorDataArray(this.mPartOfRotationVectorList), 3);
            }
            if (!this.isSetOffset) {
                this.isSetOffset = true;
                this.mGet.getMorphoSensorFusion().setOffset(new SensorData(0, this.mGyroscopeCorrectValue), 0);
            }
            this.mGet.getMorphoSensorFusion().calc();
            this.mGet.getMorphoSensorFusion().outputRotationMatrix3x3(0, gyro_mat);
            this.mGet.getMorphoSensorFusion().outputRotationMatrix3x3(3, rv_mat);
            this.mGet.getMorphoSensorFusion().outputRotationMatrix3x3(1, ac_mat);
            if (this.mPartOfGyroscopeList.size() == 0 && this.mPartOfAccelerometerList.size() == 0 && this.mPartOfMagneticFieldList.size() == 0 && this.mPartOfRotationVectorList.size() == 0) {
                next = false;
                continue;
            } else {
                next = true;
                continue;
            }
        } while (next);
        clearArrayList(this.mPartOfGyroscopeList);
        clearArrayList(this.mPartOfAccelerometerList);
        clearArrayList(this.mPartOfMagneticFieldList);
        clearArrayList(this.mPartOfOrientationList);
        clearArrayList(this.mPartOfRotationVectorList);
        if (this.mGet.getSensorFusionMode() > 4) {
            System.arraycopy(rv_mat, 0, gyro_mat, 0, gyro_mat.length);
        }
    }

    private void clearArrayList(ArrayList<SensorData> sd_list) {
        if (sd_list != null && sd_list.size() > 0) {
            sd_list.clear();
        }
    }

    private Object[] getSensorDataArray(ArrayList<SensorData> sd_list) {
        if (sd_list == null || sd_list.size() <= 0) {
            return null;
        }
        int input_num;
        int i;
        int size = sd_list.size();
        if (size >= Ola_ImageFormat.YUVPACKED_LABEL) {
            input_num = Ola_ImageFormat.YUVPACKED_LABEL;
        } else {
            input_num = size;
        }
        Object[] dst = new Object[input_num];
        for (i = 0; i < input_num; i++) {
            dst[i] = new SensorData(((SensorData) sd_list.get(i)).mTimeStamp, ((SensorData) sd_list.get(i)).mValues);
        }
        for (i = 0; i < input_num; i++) {
            sd_list.remove(0);
        }
        return dst;
    }

    private void setInputSensorData(Object[] sd_array, int sensor_type) {
        if (sd_array != null) {
            this.mGet.getMorphoSensorFusion().setSensorData(sd_array, sensor_type);
        }
    }
}
