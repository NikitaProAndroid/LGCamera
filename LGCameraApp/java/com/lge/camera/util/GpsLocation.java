package com.lge.camera.util;

import android.hardware.Camera.Parameters;
import android.location.Location;
import com.lge.olaworks.library.FaceDetector;

public class GpsLocation {
    private boolean mGpsAvailable;

    public GpsLocation(boolean gpsAvailable) {
        this.mGpsAvailable = false;
        this.mGpsAvailable = gpsAvailable;
    }

    public boolean getGpsAvailable() {
        return this.mGpsAvailable;
    }

    public void setGpsAvailable(boolean gpsAvailable) {
        this.mGpsAvailable = gpsAvailable;
    }

    public boolean setGPSlocation(Parameters parameter, boolean recordLocation, Location currentLocation) {
        boolean bChangeParameter = false;
        Location loc = recordLocation ? currentLocation : null;
        if (loc != null) {
            Double latitude = Double.valueOf(loc.getLatitude());
            Double longitude = Double.valueOf(loc.getLongitude());
            Double pivot = Double.valueOf(0.0d);
            boolean hasLatLon = (latitude.compareTo(pivot) == 0 && longitude.compareTo(pivot) == 0) ? false : true;
            if (hasLatLon) {
                parameter.setGpsLatitude(latitude.doubleValue());
                parameter.setGpsLongitude(longitude.doubleValue());
                if (loc.hasAltitude()) {
                    Double altitude = Double.valueOf(loc.getAltitude());
                    long altitudeDividend = Double.valueOf(altitude.doubleValue() * 1000.0d).longValue();
                    if (altitudeDividend < 0) {
                        altitudeDividend *= -1;
                    }
                    parameter.setGpsAltitude(altitude.doubleValue());
                } else {
                    parameter.setGpsAltitude(0.0d);
                }
                if (loc.getTime() != 0) {
                    parameter.setGpsTimestamp(loc.getTime() / 1000);
                }
                bChangeParameter = true;
                this.mGpsAvailable = true;
            } else {
                loc = null;
                if (this.mGpsAvailable) {
                    this.mGpsAvailable = false;
                    bChangeParameter = true;
                    parameter.removeGpsData();
                }
            }
        } else if (this.mGpsAvailable) {
            this.mGpsAvailable = false;
            bChangeParameter = true;
            parameter.removeGpsData();
        }
        CamLog.d(FaceDetector.TAG, "loc = " + loc);
        return bChangeParameter;
    }
}
