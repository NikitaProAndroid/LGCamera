package com.lge.camera.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import com.lge.camera.R;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.RotateView;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class OnScreenHint {
    private static final float BOTTOM_PADDING_RATE_OF_SCREEN = 0.2f;
    private static int GRAVITY_LANDSCAPE = 0;
    private static int GRAVITY_REVERSE_LANDSCAPE = 0;
    public static final int TOASTMESSAGE_END = 2;
    public static final int TOASTMESSAGE_NORMAL = 0;
    public static final int TOASTMESSAGE_STORAGE = 1;
    static String[] mLastMessage;
    private static int sGRAVITY_PORTRAIT;
    private static int sGRAVITY_REVERSE_PORTRAIT;
    final Context mContext;
    int mGravity;
    private final Handler mHandler;
    private final Runnable mHide;
    float mHorizontalMargin;
    int mLcdSizeHeight;
    int mLcdSizeWidth;
    View mNextView;
    private final LayoutParams mParams;
    private final Runnable mShow;
    float mVerticalMargin;
    View mView;
    private final WindowManager mWM;
    int mX;
    int mY;

    static {
        GRAVITY_LANDSCAPE = 81;
        sGRAVITY_PORTRAIT = 8388629;
        GRAVITY_REVERSE_LANDSCAPE = 49;
        sGRAVITY_REVERSE_PORTRAIT = 8388627;
        mLastMessage = new String[TOASTMESSAGE_END];
    }

    public OnScreenHint(Context context) {
        this.mGravity = GRAVITY_LANDSCAPE;
        this.mHorizontalMargin = 0.0f;
        this.mVerticalMargin = 0.0f;
        this.mParams = new LayoutParams();
        this.mHandler = new Handler();
        this.mShow = new Runnable() {
            public void run() {
                OnScreenHint.this.handleShow();
            }
        };
        this.mHide = new Runnable() {
            public void run() {
                OnScreenHint.this.handleHide();
            }
        };
        this.mContext = context;
        this.mWM = (WindowManager) context.getSystemService("window");
        DisplayMetrics outMetrics = new DisplayMetrics();
        this.mWM.getDefaultDisplay().getMetrics(outMetrics);
        if (outMetrics.heightPixels > outMetrics.widthPixels) {
            this.mLcdSizeWidth = outMetrics.heightPixels;
            this.mLcdSizeHeight = outMetrics.widthPixels;
        } else {
            this.mLcdSizeWidth = outMetrics.widthPixels;
            this.mLcdSizeHeight = outMetrics.heightPixels;
        }
        this.mParams.height = -2;
        this.mParams.width = -2;
        this.mParams.flags = 24;
        this.mParams.format = -3;
        this.mParams.windowAnimations = R.style.Animation_OnScreenHint;
        this.mParams.type = PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME;
        this.mParams.setTitle("OnScreenHint");
        if (this.mContext == null || isConfigureLandscape(this.mContext.getResources())) {
            GRAVITY_LANDSCAPE = 81;
            sGRAVITY_PORTRAIT = 8388629;
            GRAVITY_REVERSE_LANDSCAPE = 49;
            sGRAVITY_REVERSE_PORTRAIT = 8388627;
            return;
        }
        GRAVITY_LANDSCAPE = 8388627;
        sGRAVITY_PORTRAIT = 81;
        GRAVITY_REVERSE_LANDSCAPE = 8388629;
        sGRAVITY_REVERSE_PORTRAIT = 49;
    }

    public void show() {
        if (this.mNextView == null) {
            throw new RuntimeException("setView must have been called");
        }
        this.mHandler.post(this.mShow);
    }

    public void cancel() {
        this.mHandler.post(this.mHide);
    }

    public void showImmediately() {
        if (this.mNextView == null) {
            throw new RuntimeException("setView must have been called");
        }
        handleShow();
    }

    public void cancelImmediately() {
        handleHide();
    }

    public static OnScreenHint makeText(Context context, CharSequence text) {
        return makeText(context, text, TOASTMESSAGE_NORMAL);
    }

    public static OnScreenHint makeText(Context context, CharSequence text, int orientation) {
        return makeText(context, text, orientation, TOASTMESSAGE_NORMAL);
    }

    public static OnScreenHint makeText(Context context, CharSequence text, int orientation, int selectedToastMessage) {
        OnScreenHint result = new OnScreenHint(context);
        View v = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.on_screen_hint, null);
        TextView tv = (TextView) v.findViewById(R.id.message);
        RotateLayout toastView = (RotateLayout) v.findViewById(R.id.rotate_toast);
        if (selectedToastMessage < 0 || selectedToastMessage >= TOASTMESSAGE_END) {
            selectedToastMessage = TOASTMESSAGE_NORMAL;
        }
        mLastMessage[selectedToastMessage] = text.toString();
        tv.setText(mLastMessage[selectedToastMessage]);
        boolean windowLand = isConfigureLandscape(context.getResources());
        switch (orientation) {
            case TOASTMESSAGE_STORAGE /*1*/:
                toastView.rotateLayout(windowLand ? 90 : TOASTMESSAGE_NORMAL);
                result.mGravity = sGRAVITY_PORTRAIT;
                break;
            case TOASTMESSAGE_END /*2*/:
                toastView.rotateLayout(windowLand ? MediaProviderUtils.ROTATION_180 : 90);
                result.mGravity = GRAVITY_REVERSE_LANDSCAPE;
                break;
            case LGKeyRec.EVENT_STARTED /*3*/:
                toastView.rotateLayout(windowLand ? Tag.IMAGE_DESCRIPTION : MediaProviderUtils.ROTATION_180);
                result.mGravity = sGRAVITY_REVERSE_PORTRAIT;
                break;
            default:
                toastView.rotateLayout(windowLand ? TOASTMESSAGE_NORMAL : Tag.IMAGE_DESCRIPTION);
                result.mGravity = GRAVITY_LANDSCAPE;
                break;
        }
        result.mNextView = v;
        return result;
    }

    public static OnScreenHint changeOrientation(Context context, int orientation) {
        return changeOrientation(context, orientation, TOASTMESSAGE_NORMAL);
    }

    public static OnScreenHint changeOrientation(Context context, int orientation, int selectedToastMessage) {
        if (selectedToastMessage < 0 || selectedToastMessage >= TOASTMESSAGE_END) {
            selectedToastMessage = TOASTMESSAGE_NORMAL;
        }
        OnScreenHint hint = makeText(context, mLastMessage[selectedToastMessage], orientation);
        hint.show();
        return hint;
    }

    public void setText(CharSequence s) {
        if (this.mNextView == null) {
            throw new RuntimeException("This OnScreenHint was not created with OnScreenHint.makeText()");
        }
        TextView tv = (TextView) this.mNextView.findViewById(R.id.message);
        if (tv == null) {
            throw new RuntimeException("This OnScreenHint was not created with OnScreenHint.makeText()");
        }
        tv.setText(s);
    }

    private synchronized void handleShow() {
        if (this.mView != this.mNextView) {
            handleHide();
            this.mView = this.mNextView;
            int gravity = this.mGravity;
            boolean windowLand = isConfigureLandscape(this.mContext.getResources());
            this.mParams.gravity = gravity;
            if ((gravity & 7) == 7) {
                if (windowLand) {
                    this.mParams.horizontalWeight = RotateView.DEFAULT_TEXT_SCALE_X;
                } else {
                    this.mParams.verticalWeight = RotateView.DEFAULT_TEXT_SCALE_X;
                }
            }
            if ((gravity & DialogCreater.DIALOG_ID_HELP_DUAL_RECORDING) == DialogCreater.DIALOG_ID_HELP_DUAL_RECORDING) {
                if (windowLand) {
                    this.mParams.verticalWeight = RotateView.DEFAULT_TEXT_SCALE_X;
                } else {
                    this.mParams.horizontalWeight = RotateView.DEFAULT_TEXT_SCALE_X;
                }
            }
            if (this.mGravity == GRAVITY_LANDSCAPE || this.mGravity == GRAVITY_REVERSE_LANDSCAPE) {
                if (windowLand) {
                    this.mParams.y = (int) (((float) this.mLcdSizeHeight) * BOTTOM_PADDING_RATE_OF_SCREEN);
                    this.mParams.width = this.mLcdSizeHeight;
                } else {
                    this.mParams.x = (int) (((float) this.mLcdSizeHeight) * BOTTOM_PADDING_RATE_OF_SCREEN);
                    this.mParams.height = this.mLcdSizeHeight;
                }
            } else if (this.mGravity == sGRAVITY_PORTRAIT || this.mGravity == sGRAVITY_REVERSE_PORTRAIT) {
                if (windowLand) {
                    this.mParams.x = (int) (((float) this.mLcdSizeWidth) * BOTTOM_PADDING_RATE_OF_SCREEN);
                    this.mParams.height = this.mLcdSizeHeight;
                } else {
                    this.mParams.y = (int) (((float) this.mLcdSizeWidth) * BOTTOM_PADDING_RATE_OF_SCREEN);
                    this.mParams.width = this.mLcdSizeHeight;
                }
            }
            try {
                this.mParams.verticalMargin = this.mVerticalMargin;
                this.mParams.horizontalMargin = this.mHorizontalMargin;
                if (this.mView.getParent() != null) {
                    this.mWM.removeView(this.mView);
                }
                this.mWM.addView(this.mView, this.mParams);
            } catch (Exception e) {
                CamLog.w(FaceDetector.TAG, String.format("OnScreenHint display failed.", new Object[TOASTMESSAGE_NORMAL]), e);
            }
        }
    }

    private synchronized void handleHide() {
        if (this.mView != null) {
            if (this.mView.getParent() != null) {
                this.mWM.removeView(this.mView);
            }
            this.mView = null;
        }
    }

    private static boolean isConfigureLandscape(Resources resource) {
        if (resource == null || resource.getConfiguration().orientation != TOASTMESSAGE_END) {
            return false;
        }
        return true;
    }
}
