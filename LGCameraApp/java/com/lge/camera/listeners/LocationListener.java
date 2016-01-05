package com.lge.camera.listeners;

import android.location.Location;
import android.os.Bundle;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class LocationListener implements android.location.LocationListener {
    private LocationListenerFunction mGet;
    Location mLastLocation;
    String mProvider;
    boolean mValid;

    public interface LocationListenerFunction {
        boolean checkGPSSettingValue();

        void updateGpsIndicator(String str);
    }

    public LocationListener(LocationListenerFunction function, String provider) {
        this.mValid = false;
        this.mGet = null;
        this.mGet = function;
        this.mProvider = provider;
        this.mLastLocation = new Location(this.mProvider);
    }

    public void onLocationChanged(Location newLocation) {
        Double Location = Double.valueOf(0.0d);
        this.mLastLocation.set(newLocation);
        this.mValid = true;
        try {
            if (this.mGet != null && !this.mGet.checkGPSSettingValue()) {
                return;
            }
            if ((Location.compareTo(Double.valueOf(newLocation.getLatitude())) != 0 || Location.compareTo(Double.valueOf(newLocation.getLongitude())) != 0) && this.mGet != null) {
                this.mGet.updateGpsIndicator(this.mProvider);
            }
        } catch (NullPointerException e) {
            CamLog.d(FaceDetector.TAG, "LocationListener onLocationChanged" + e);
        }
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
        this.mValid = false;
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (this.mGet == null || this.mGet.checkGPSSettingValue()) {
            switch (status) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    this.mValid = false;
                    if (this.mGet != null) {
                        this.mGet.updateGpsIndicator(this.mProvider);
                    }
                default:
            }
        }
    }

    public Location current() {
        return this.mValid ? this.mLastLocation : null;
    }
}
