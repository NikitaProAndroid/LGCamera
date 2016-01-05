package com.lge.camera.receiver;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import com.lge.camera.R;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class MessageReceiver extends CameraBroadCastReceiver {
    private static final Uri CONTENT_URI_MMS;
    private static final Uri CONTENT_URI_SMS;
    private static final String FIELD_DATE = "date";
    private static final String FIELD_ID = "_id";
    public static final String INTENT_MMS = "com.lge.message.MMS_RECEIVED_ACTION_FOR_LGE_APPL";
    public static final String INTENT_SMS = "com.lge.message.SMS_RECEIVED_ACTION_FOR_LGE_APPL";

    static {
        CONTENT_URI_SMS = Uri.parse("content://sms/inbox");
        CONTENT_URI_MMS = Uri.parse("content://mms/inbox");
    }

    public MessageReceiver(ReceiverMediatorBridge bridge) {
        super(bridge);
    }

    public void onReceive(Context context, Intent intent) {
        if (checkOnReceive()) {
            CamLog.d(FaceDetector.TAG, "BroadCastReceiver action = " + intent.getAction());
            String action = intent.getAction();
            int messageType = 1;
            boolean isReadAllMsg = false;
            if ("com.lge.message.MSG_RECEIVED_ACTION".equals(action)) {
                messageType = doMessageReceivedAction(intent);
                if (ModelProperties.isJBPlusModel()) {
                    this.mGet.toastLong(this.mGet.getString(R.string.message_received));
                }
            } else if ("lge.intent.action.UNREAD_MESSAGES".equals(action)) {
                int remainMsgCount = Integer.parseInt(intent.getStringExtra("number"));
                CamLog.d(FaceDetector.TAG, "Remain message count is " + remainMsgCount);
                if (remainMsgCount == 0) {
                    isReadAllMsg = true;
                }
            }
            try {
                this.mGet.setMessageIndicatorReceived(messageType, isReadAllMsg);
            } catch (NumberFormatException e) {
                CamLog.e(FaceDetector.TAG, "failure to read msg number");
                e.printStackTrace();
            }
            CamLog.d(FaceDetector.TAG, "worning intent rescived!!");
        }
    }

    private int doMessageReceivedAction(Intent intent) {
        int messageType;
        String msg_type = intent.getStringExtra("msg_type");
        CamLog.d(FaceDetector.TAG, "mail received msg_type = " + msg_type);
        if (msg_type == null || !msg_type.equals(Setting.VIDEO_QUALITY_MMS)) {
            messageType = 1;
        } else {
            messageType = 2;
        }
        try {
            this.mGet.setMessageIndicatorReceived(messageType, false);
        } catch (NumberFormatException e) {
            CamLog.e(FaceDetector.TAG, "failure to read msg number");
            e.printStackTrace();
        }
        return messageType;
    }

    public int getRecentMessageType() {
        if (ModelProperties.isWifiOnlyModel(this.mGet.getApplicationContext())) {
            return 0;
        }
        return getRecentMessageTypeForNormal(this.mGet.getActivity().getContentResolver(), 0, 0, 0);
    }

    private int getRecentMessageTypeForNormal(ContentResolver cr, long smsReceivedTime, long mmsReceivedTime, int type) {
        Cursor cursor = null;
        String[] projection = new String[]{FIELD_ID, FIELD_DATE};
        String selectionMMS = "read=0 and m_type=132";
        try {
            cursor = cr.query(CONTENT_URI_SMS, projection, "read=0 and type=1", null, null);
            if (cursor != null) {
                smsReceivedTime = getRecentMessageTime(cursor);
                cursor.close();
            }
            cursor = cr.query(CONTENT_URI_MMS, projection, selectionMMS, null, null);
            if (cursor != null) {
                mmsReceivedTime = getRecentMessageTime(cursor);
            }
            type = getTypeByReceivedTime(smsReceivedTime, mmsReceivedTime, type);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable e) {
            CamLog.e(FaceDetector.TAG, "getRecentMessageTypeForNormal Exception! ", e);
            type = 0;
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return type;
    }

    private int getTypeByReceivedTime(long smsReceivedTime, long mmsReceivedTime, int type) {
        if (ModelProperties.isDomesticModel()) {
            mmsReceivedTime *= 1000;
        }
        if (smsReceivedTime > mmsReceivedTime) {
            return 1;
        }
        if (smsReceivedTime >= mmsReceivedTime) {
            return type;
        }
        if (ModelProperties.isDomesticModel()) {
            return 2;
        }
        return 1;
    }

    private long getRecentMessageTime(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            return 0;
        }
        cursor.moveToFirst();
        return cursor.getLong(cursor.getColumnIndex(FIELD_DATE));
    }

    private boolean checkOnReceive() {
        if (this.mGet != null && this.mGet.getActivity() != null) {
            return true;
        }
        CamLog.d(FaceDetector.TAG, String.format("activity is null", new Object[0]));
        return false;
    }
}
