package com.lge.systemservice.core;

import android.content.Context;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import com.lge.systemservice.core.IOsManager.Stub;

public class OsManager {
    static final String SERVICE_NAME = "osservice";
    private static final String TAG;
    private IOsManager mService;

    static {
        TAG = OsManager.class.getSimpleName();
    }

    OsManager(Context context) {
    }

    private final IOsManager getService() {
        if (this.mService == null) {
            this.mService = Stub.asInterface(ServiceManager.getService(SERVICE_NAME));
            if (this.mService != null) {
                try {
                    this.mService.asBinder().linkToDeath(new DeathRecipient() {
                        public void binderDied() {
                            OsManager.this.mService = null;
                        }
                    }, 0);
                } catch (RemoteException e) {
                    this.mService = null;
                }
            }
        }
        return this.mService;
    }

    public void setSystemProperty(String key, String val) {
        try {
            IOsManager service = getService();
            if (service != null) {
                service.setSystemProperty(key, val);
            }
        } catch (RemoteException e) {
            Log.d(TAG, "Failed to set system property: " + key + " as " + val, e);
        }
    }

    public void stopRingtoneSound() {
        try {
            IOsManager service = getService();
            if (service != null) {
                service.stopRingtoneSound();
            }
        } catch (RemoteException e) {
            Log.d(TAG, "Failed to stop ringtone sound", e);
        }
    }

    public void goToSleepWithForce(long time, int reason) {
        try {
            IOsManager service = getService();
            if (service != null) {
                service.goToSleepWithForce(time, reason);
            }
        } catch (RemoteException e) {
            Log.d(TAG, "Failed to start goToSleepWithForce", e);
        }
    }

    public void wakeUpWithForce(long time) {
        try {
            IOsManager service = getService();
            if (service != null) {
                service.wakeUpWithForce(time);
            }
        } catch (RemoteException e) {
            Log.d(TAG, "Failed to start wakeUpWithForce", e);
        }
    }
}
