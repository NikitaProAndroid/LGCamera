package com.lge.camera.receiver;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.util.Common;

public class BroadCastReceiverDefine {
    public BLEReceiver mBLEReceiver;
    public BatteryReceiver mBatteryReceiver;
    public CallPopUpReceiver mCallPopUpReceiver;
    public CameraDayDreamReceiver mCameraDayDreamReceiver;
    public CameraSettingReceiverBySDM mCameraSettingReceiverBySDM;
    public CleanViewNaviBarReceiver mCleanViewNaviBarReceiver;
    public ReceiverMediatorBridge mGet;
    public HdmiReceiver mHdmiReceiver;
    public HeadsetReceiver mHeadsetReceiver;
    public CameraBroadCastReceiver mMediaReceiver;
    public MessageReceiver mMessageReceiver;
    public RotationModeReceiver mRotationModeReceiver;
    public CameraScreenOffReceiver mScreenOffReceiver;
    public SmartCoverReceiver mSmartCoverReceiver;
    public TemperatureReceiver mTemperatureReceiver;
    public VoiceMailReceiver mVoiceMailReceiver;

    public BroadCastReceiverDefine(ReceiverMediatorBridge function) {
        this.mGet = null;
        this.mGet = function;
        init();
    }

    public BroadCastReceiverDefine(ReceiverMediatorBridge function, boolean secureCamera) {
        this.mGet = null;
        this.mGet = function;
        init();
        if (secureCamera || Common.isQuickWindowCameraMode()) {
            this.mScreenOffReceiver = new CameraScreenOffReceiver(this.mGet);
            checkAndRegisterReceiver(getScreenOffReceiver(), getCameraScreenOffIntentFilter());
        }
    }

    private void init() {
        this.mMessageReceiver = new MessageReceiver(this.mGet);
        this.mBatteryReceiver = new BatteryReceiver(this.mGet);
        this.mVoiceMailReceiver = new VoiceMailReceiver(this.mGet);
        this.mCameraSettingReceiverBySDM = new CameraSettingReceiverBySDM(this.mGet);
        this.mHdmiReceiver = new HdmiReceiver(this.mGet);
        if (this.mGet.getApplicationMode() == 0) {
            this.mMediaReceiver = new CameraMediaBroadcastReceiver(this.mGet);
        } else {
            this.mMediaReceiver = new CamcorderMediaBroadcastReceiver(this.mGet);
        }
        this.mBLEReceiver = new BLEReceiver(this.mGet);
        if (ProjectVariables.temperatureCheckMethod() == 1 || ProjectVariables.temperatureCheckMethod() == 3) {
            this.mTemperatureReceiver = new TemperatureReceiver(this.mGet);
        }
        if (FunctionProperties.isSupportAudiozoom()) {
            this.mHeadsetReceiver = new HeadsetReceiver(this.mGet);
        }
        this.mCallPopUpReceiver = new CallPopUpReceiver(this.mGet);
        this.mSmartCoverReceiver = new SmartCoverReceiver(this.mGet);
        if (ProjectVariables.isSupportKDDICleanView()) {
            this.mCleanViewNaviBarReceiver = new CleanViewNaviBarReceiver(this.mGet);
        }
        this.mCameraDayDreamReceiver = new CameraDayDreamReceiver(this.mGet);
        if (FunctionProperties.isSupportedRotationWithoutAccelerometer()) {
            this.mRotationModeReceiver = new RotationModeReceiver(this.mGet);
        }
    }

    public CameraBroadCastReceiver getCameraBroadCastReceiver() {
        return this.mMediaReceiver;
    }

    public IntentFilter getCameraBroadCastReceiverIntentFilter() {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MEDIA_EJECT");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_SCANNER_STARTED");
        intentFilter.addAction("android.intent.action.MEDIA_SCANNER_FINISHED");
        intentFilter.addAction("android.intent.action.MEDIA_CHECKING");
        intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
        intentFilter.addAction("android.intent.action.MEDIA_NOFS");
        intentFilter.addAction("android.intent.action.MEDIA_REMOVED");
        intentFilter.addAction("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        intentFilter.addAction("android.intent.action.MEDIA_SHARED");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        intentFilter.addDataScheme("file");
        return intentFilter;
    }

    public MessageReceiver getMessageReceiver() {
        return this.mMessageReceiver;
    }

    public IntentFilter getMessageReceiverIntentFilter() {
        return new IntentFilter("com.lge.message.MSG_RECEIVED_ACTION");
    }

    public IntentFilter getSKTMessageReceiverIntentFilter() {
        IntentFilter intentFilter = new IntentFilter("lge.intent.action.UNREAD_SKT_MESSAGES");
        intentFilter.addAction("lge.intent.action.ACTION_UNREAD_SMS");
        return intentFilter;
    }

    public IntentFilter getLGSMSMessageReceiverIntentFilter() {
        return new IntentFilter(MessageReceiver.INTENT_SMS);
    }

    public IntentFilter getLGMMSMessageReceiverIntentFilter() {
        return new IntentFilter(MessageReceiver.INTENT_MMS);
    }

    public IntentFilter getUnreadMessageReceiverIntentFilter() {
        return new IntentFilter("lge.intent.action.UNREAD_MESSAGES");
    }

    public BatteryReceiver getBatteryReceiver() {
        return this.mBatteryReceiver;
    }

    public IntentFilter getBatteryReceiverIntentFilter() {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("android.intent.action.BATTERY_LOW");
        if (ProjectVariables.isSupportHeat_detection()) {
            intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
            intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        }
        return intentFilter;
    }

    public TemperatureReceiver getTemperatureReceiver() {
        return this.mTemperatureReceiver;
    }

    public IntentFilter getTemperatureReceiverIntentFilter() {
        if (ProjectVariables.temperatureCheckMethod() == 1 || ProjectVariables.temperatureCheckMethod() == 3) {
            return new IntentFilter(ProjectVariables.ACTION_CAMERA_HIGH_TEMP_WARN);
        }
        return null;
    }

    public VoiceMailReceiver getVoiceMailReceiver() {
        return this.mVoiceMailReceiver;
    }

    public IntentFilter getVoiceMailReceiverIntentFilter() {
        return new IntentFilter("com.lge.vvm.NEW_VVM_NOTIFICATION_RECEIVED");
    }

    public CameraSettingReceiverBySDM getCameraSettingReceiverBySDM() {
        return this.mCameraSettingReceiverBySDM;
    }

    public IntentFilter getCameraSettingReceiverBySDMIntentFilter() {
        return new IntentFilter("com.innopath.activecare.CAMERA_UPDATED");
    }

    public HdmiReceiver getHdmiReceiver() {
        return this.mHdmiReceiver;
    }

    public IntentFilter getHdmiReceiverIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addCategory("android.intent.category.DEFAULT");
        this.mHdmiReceiver.getClass();
        intentFilter.addAction("HDMI_CABLE_CONNECTED");
        this.mHdmiReceiver.getClass();
        intentFilter.addAction("HDMI_CABLE_DISCONNECTED");
        this.mHdmiReceiver.getClass();
        intentFilter.addAction("android.intent.action.HDMI_AUDIO_PLUG");
        this.mHdmiReceiver.getClass();
        intentFilter.addAction("android.intent.action.HDMI_PLUG");
        this.mHdmiReceiver.getClass();
        intentFilter.addAction("android.intent.action.DUALDISPLAY");
        return intentFilter;
    }

    public BLEReceiver getBLEReceiver() {
        return this.mBLEReceiver;
    }

    public IntentFilter getBLEReceiverIntentFilter() {
        return new IntentFilter(CameraConstants.BLE_ONEKEY_CHANGED);
    }

    public HeadsetReceiver getHeadsetReceiver() {
        return this.mHeadsetReceiver;
    }

    public IntentFilter getHeadReceiverIntentFilter() {
        return new IntentFilter("android.intent.action.HEADSET_PLUG");
    }

    public CallPopUpReceiver getCallPopUpReceiver() {
        return this.mCallPopUpReceiver;
    }

    public IntentFilter getCallPopUpReceiverIntentFilter() {
        IntentFilter intentFilter = new IntentFilter("com.lge.action.CALLALERTING_SHOW");
        intentFilter.addAction("com.lge.action.CALLALERTING_HIDE");
        intentFilter.addAction("com.lge.action.CALLALERTING_ANSWER");
        return intentFilter;
    }

    public IntentFilter getQuickClipIntentFilter() {
        return new IntentFilter("com.lge.systemui.qmemo");
    }

    public CameraScreenOffReceiver getScreenOffReceiver() {
        return this.mScreenOffReceiver;
    }

    public IntentFilter getCameraScreenOffIntentFilter() {
        return new IntentFilter("android.intent.action.SCREEN_OFF");
    }

    public IntentFilter getQuickCamCaseReceiverFilter() {
        return new IntentFilter(CameraConstants.INTENT_ACTION_CAMERA_FINISH);
    }

    public SmartCoverReceiver getSmartCoverReceiver() {
        return this.mSmartCoverReceiver;
    }

    public IntentFilter getSmartCoverReceiverFilter() {
        IntentFilter intentFilter = new IntentFilter(CameraConstants.ACTION_ACCESSORY_EVENT);
        intentFilter.addAction(CameraConstants.INTENT_ACTION_CAMERA_FINISH);
        return intentFilter;
    }

    public CleanViewNaviBarReceiver getCleanViewNaviBarReceiver() {
        return this.mCleanViewNaviBarReceiver;
    }

    public IntentFilter getCleanViewNaviBarReceiverFilter() {
        return new IntentFilter(CameraConstants.INTENT_ACTION_CLEAN_VIEW_RECEIVER);
    }

    public CameraDayDreamReceiver getCameraDayDreamReceiver() {
        return this.mCameraDayDreamReceiver;
    }

    public IntentFilter getCameraDayDreamReceiverFilter() {
        return new IntentFilter("android.intent.action.DREAMING_STARTED");
    }

    public RotationModeReceiver getRotationModeReceiver() {
        return this.mRotationModeReceiver;
    }

    public IntentFilter getRotationModeReceiverFilter() {
        return new IntentFilter("com.lge.android.intent.action.SWITCH_ROTATION_MODE");
    }

    public void registerReceiver() {
        checkAndRegisterReceiver(getCameraBroadCastReceiver(), getCameraBroadCastReceiverIntentFilter());
        checkAndRegisterReceiver(getBatteryReceiver(), getBatteryReceiverIntentFilter());
        checkAndRegisterReceiver(getCameraSettingReceiverBySDM(), getCameraSettingReceiverBySDMIntentFilter());
        checkAndRegisterReceiver(getMessageReceiver(), getMessageReceiverIntentFilter());
        checkAndRegisterReceiver(getMessageReceiver(), getSKTMessageReceiverIntentFilter());
        checkAndRegisterReceiver(getMessageReceiver(), getLGSMSMessageReceiverIntentFilter());
        checkAndRegisterReceiver(getMessageReceiver(), getLGMMSMessageReceiverIntentFilter());
        checkAndRegisterReceiver(getMessageReceiver(), getUnreadMessageReceiverIntentFilter());
        checkAndRegisterReceiver(getVoiceMailReceiver(), getVoiceMailReceiverIntentFilter());
        checkAndRegisterReceiver(getHdmiReceiver(), getHdmiReceiverIntentFilter());
        if (ProjectVariables.temperatureCheckMethod() == 1 || ProjectVariables.temperatureCheckMethod() == 3) {
            checkAndRegisterReceiver(getTemperatureReceiver(), getTemperatureReceiverIntentFilter());
        }
        checkAndRegisterReceiver(getBLEReceiver(), getBLEReceiverIntentFilter());
        if (FunctionProperties.isSupportAudiozoom()) {
            checkAndRegisterReceiver(getHeadsetReceiver(), getHeadReceiverIntentFilter());
        }
        checkAndRegisterReceiver(getCallPopUpReceiver(), getCallPopUpReceiverIntentFilter());
        checkAndRegisterReceiver(getSmartCoverReceiver(), getSmartCoverReceiverFilter());
        if (ProjectVariables.isSupportKDDICleanView()) {
            checkAndRegisterReceiver(getCleanViewNaviBarReceiver(), getCleanViewNaviBarReceiverFilter());
        }
        checkAndRegisterReceiver(getCameraDayDreamReceiver(), getCameraDayDreamReceiverFilter());
        checkAndRegisterReceiver(getRotationModeReceiver(), getRotationModeReceiverFilter());
    }

    public void unregisterReceivers() {
        checkAndUnRegisterReceiver(getCameraBroadCastReceiver());
        checkAndUnRegisterReceiver(getBatteryReceiver());
        checkAndUnRegisterReceiver(getMessageReceiver());
        checkAndUnRegisterReceiver(getVoiceMailReceiver());
        checkAndUnRegisterReceiver(getCameraSettingReceiverBySDM());
        checkAndUnRegisterReceiver(getHdmiReceiver());
        if (ProjectVariables.temperatureCheckMethod() == 1 || ProjectVariables.temperatureCheckMethod() == 3) {
            checkAndUnRegisterReceiver(getTemperatureReceiver());
        }
        checkAndUnRegisterReceiver(getBLEReceiver());
        checkAndUnRegisterReceiver(getHeadsetReceiver());
        checkAndUnRegisterReceiver(getCallPopUpReceiver());
        checkAndUnRegisterReceiver(getScreenOffReceiver());
        checkAndUnRegisterReceiver(getSmartCoverReceiver());
        checkAndUnRegisterReceiver(getCleanViewNaviBarReceiver());
        checkAndUnRegisterReceiver(getCameraDayDreamReceiver());
        checkAndUnRegisterReceiver(getRotationModeReceiver());
    }

    private void checkAndRegisterReceiver(BroadcastReceiver receiver, IntentFilter intentFilter) {
        if (receiver != null && intentFilter != null) {
            this.mGet.getActivity().registerReceiver(receiver, intentFilter);
        }
    }

    private void checkAndUnRegisterReceiver(BroadcastReceiver receiver) {
        if (receiver != null) {
            this.mGet.getActivity().unregisterReceiver(receiver);
        }
    }

    public void unbindReceiver() {
        this.mMediaReceiver = null;
        this.mBatteryReceiver = null;
        this.mMessageReceiver = null;
        this.mVoiceMailReceiver = null;
        this.mCameraSettingReceiverBySDM = null;
        this.mHdmiReceiver = null;
        this.mBLEReceiver = null;
        this.mCallPopUpReceiver = null;
        this.mScreenOffReceiver = null;
        this.mSmartCoverReceiver = null;
        this.mCleanViewNaviBarReceiver = null;
        this.mCameraDayDreamReceiver = null;
        this.mRotationModeReceiver = null;
    }

    public int getRecentMessageType() {
        return this.mMessageReceiver.getRecentMessageType();
    }
}
