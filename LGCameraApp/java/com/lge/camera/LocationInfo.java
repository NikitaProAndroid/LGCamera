package com.lge.camera;

import android.location.Location;
import android.location.LocationManager;
import com.lge.camera.listeners.LocationListener;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class LocationInfo {
    private LocationInfoFunction mGet;
    private LocationListener[] mLocationListeners;
    private LocationManager mLocationManager;
    private boolean mLocationOn;
    private boolean mRecordLocation;

    public interface LocationInfoFunction {
        String getSettingValue(String str);

        void updateGpsIndicator();
    }

    public void unbind() {
        this.mLocationListeners = null;
        this.mLocationManager = null;
    }

    public LocationInfo(LocationInfoFunction function) {
        this.mLocationListeners = null;
        this.mLocationManager = null;
        this.mGet = null;
        this.mGet = function;
    }

    public void setLocationListener(LocationListener[] listener) {
        this.mLocationListeners = listener;
    }

    public LocationListener[] getLocationListener() {
        return this.mLocationListeners;
    }

    public void setLocationManager(LocationManager manager) {
        this.mLocationManager = manager;
    }

    public LocationManager getLocationManager() {
        return this.mLocationManager;
    }

    public boolean getRecordLocation() {
        return this.mRecordLocation;
    }

    public void setRecordLocation(boolean set) {
        this.mRecordLocation = set;
    }

    public boolean getLocationOn() {
        return this.mLocationOn;
    }

    public void setLocationOn(boolean set) {
        this.mLocationOn = set;
    }

    public void startReceivingLocationUpdates() {
        CamLog.d(FaceDetector.TAG, "startReceivingLocationUpdates()");
        if (!this.mGet.getSettingValue(Setting.KEY_CAMERA_TAG_LOCATION).equals(CameraConstants.SMART_MODE_OFF) && this.mLocationManager != null) {
            try {
                this.mLocationManager.requestLocationUpdates("network", 1000, 0.0f, this.mLocationListeners[1]);
            } catch (SecurityException ex) {
                CamLog.i(FaceDetector.TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex2) {
                CamLog.d(FaceDetector.TAG, "provider does not exist " + ex2.getMessage());
            }
            try {
                this.mLocationManager.requestLocationUpdates("gps", 1000, 0.0f, this.mLocationListeners[0]);
            } catch (SecurityException ex3) {
                CamLog.i(FaceDetector.TAG, "fail to request location update, ignore", ex3);
            } catch (IllegalArgumentException ex22) {
                CamLog.d(FaceDetector.TAG, "provider does not exist " + ex22.getMessage());
            }
            this.mGet.updateGpsIndicator();
        }
    }

    public void stopReceivingLocationUpdates() {
        CamLog.d(FaceDetector.TAG, "stopReceivingLocationUpdates");
        if (this.mLocationManager != null) {
            for (android.location.LocationListener removeUpdates : this.mLocationListeners) {
                try {
                    this.mLocationManager.removeUpdates(removeUpdates);
                } catch (Exception ex) {
                    CamLog.i(FaceDetector.TAG, "fail to remove location listners, ignore", ex);
                }
            }
            this.mGet.updateGpsIndicator();
        }
    }

    public Location getCurrentLocation() {
        if (this.mGet.getSettingValue(Setting.KEY_CAMERA_TAG_LOCATION).equals(CameraConstants.SMART_MODE_OFF)) {
            return null;
        }
        for (LocationListener current : this.mLocationListeners) {
            Location location = current.current();
            if (location != null) {
                CamLog.i(FaceDetector.TAG, "getCurrentLocation return = " + location.toString());
                return location;
            }
        }
        CamLog.i(FaceDetector.TAG, "getCurrentLocation return = null");
        return null;
    }
}
