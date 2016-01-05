package com.lge.camera.util;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.ITelephony.Stub;
import com.lge.olaworks.library.FaceDetector;

public class TelephonyUtil {
    public static boolean phoneIsIdle(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getCallState() == 0;
    }

    public static boolean phoneIsOffhook() {
        boolean phoneOffhook = false;
        try {
            ITelephony phone = Stub.asInterface(ServiceManager.checkService("phone"));
            if (phone != null) {
                phoneOffhook = phone.isOffhook();
            }
        } catch (RemoteException e) {
            CamLog.w(FaceDetector.TAG, "phone.isOffhook() failed", e);
        }
        return phoneOffhook;
    }

    public static boolean phoneInCall(Context context) {
        boolean z;
        String str = FaceDetector.TAG;
        StringBuilder append = new StringBuilder().append("CameraBLEIntentReceiver phoneInCall : ");
        if (!phoneIsIdle(context) || phoneIsOffhook()) {
            z = true;
        } else {
            z = false;
        }
        CamLog.d(str, append.append(z).toString());
        if (!phoneIsIdle(context) || phoneIsOffhook()) {
            return true;
        }
        return false;
    }

    public static boolean phoneInVTCall(Context context) {
        int vtCallState = ((TelephonyManager) context.getSystemService("phone")).getCallState();
        CamLog.d(FaceDetector.TAG, "phoneInVTCall : vtCallState = " + vtCallState);
        if (vtCallState >= 100) {
            return true;
        }
        return false;
    }

    public static boolean isRinging(Context context) {
        boolean isRining = false;
        try {
            ITelephony phone = Stub.asInterface(ServiceManager.checkService("phone"));
            if (phone != null) {
                isRining = phone.isRinging();
            }
        } catch (RemoteException e) {
            CamLog.w(FaceDetector.TAG, "phone.isRinging() failed", e);
        }
        return isRining;
    }
}
