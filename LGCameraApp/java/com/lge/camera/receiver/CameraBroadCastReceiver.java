package com.lge.camera.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.View;
import com.lge.camera.CameraActivity;
import com.lge.camera.util.MainHandler;

public abstract class CameraBroadCastReceiver extends BroadcastReceiver {
    protected boolean mFinished;
    public ReceiverMediatorBridge mGet;

    public interface ReceiverMediatorBridge {
        boolean checkCameraShutterSoundLoaded();

        boolean checkSettingValue(String str, String str2);

        void checkStorage(boolean z);

        boolean checkStorageController();

        void clearSettingMenuAndSubMenu();

        void clearSubMenu();

        void doCommand(String str);

        void doCommand(String str, Object obj);

        void doCommand(String str, Object obj, Object obj2);

        void doCommandDelayed(String str, long j);

        void doCommandUi(String str);

        View findViewById(int i);

        CameraActivity getActivity();

        int getActualBatteryLevel();

        Context getApplicationContext();

        int getApplicationMode();

        int getBatteryLevel();

        Camera getCameraDevice();

        long getCurrentRecordingTime();

        int getDialogID();

        String getExternalStorageDir();

        MainHandler getHandler();

        boolean getInCaptureProgress();

        Parameters getParameters();

        String getPreviewSizeOnDevice();

        String getSettingValue(String str);

        Runnable getSnapshotRunnable();

        String getString(int i);

        int getSubMenuMode();

        int getVideoState();

        void hideOptionMenu();

        View inflateView(int i);

        boolean isDualRecordingActive();

        boolean isFlashOffByHighTemperature();

        boolean isOptionMenuShowing();

        boolean isPausing();

        boolean isPreviewing();

        boolean isRecordedLengthTooShort();

        boolean isRotateDialogVisible();

        void onDismissRotateDialog();

        void postOnUiThread(Runnable runnable);

        void postOnUiThread(Runnable runnable, long j);

        void removePanoramaView();

        void removePostRunnable(Object obj);

        void setActualBatteryLevel(int i);

        void setBatteryIndicator(int i);

        void setBatteryLevel(int i);

        void setBatteryTemper(int i);

        void setBatteryVisibility(int i);

        void setBlockTouchByCallPopUp(boolean z);

        void setButtonRemainEnabled(int i, boolean z, boolean z2);

        void setCurrentSettingMenuEnable(String str, boolean z);

        void setFlashOffByHighTemperature(boolean z);

        void setForced_audiozoom(boolean z);

        void setHeadsetstate(int i);

        void setIsCharging(boolean z);

        void setMainButtonDisable();

        void setMainButtonEnable();

        void setMediaScanning(boolean z);

        void setMediaUSBConnectAtStartRecord(boolean z);

        void setMessageIndicatorReceived(int i, boolean z);

        void setOrientationForced(int i);

        void setPreferenceMenuEnable(String str, boolean z, boolean z2);

        void setQuickButtonVisible(int i, int i2, boolean z);

        boolean setSetting(String str, String str2);

        void setSubMenuMode(int i);

        void setVoiceMailIndicator(int i);

        void showDialogPopup(int i);

        boolean showFocus();

        void showPanoramaView();

        void startHeatingwarning();

        boolean stopByUserAction();

        void stopHeatingwarning();

        void stopRecordingByPausing();

        void storageToasthide(boolean z);

        void toast(int i);

        void toastControllerHide(boolean z);

        void toastLong(String str);
    }

    public abstract void onReceive(Context context, Intent intent);

    public CameraBroadCastReceiver(ReceiverMediatorBridge function) {
        this.mFinished = false;
        this.mGet = null;
        this.mGet = function;
    }

    protected boolean checkOnReceive(Intent intent) {
        if (this.mGet == null || this.mGet.getActivity() == null || intent == null || intent.getAction() == null) {
            return false;
        }
        return true;
    }
}
