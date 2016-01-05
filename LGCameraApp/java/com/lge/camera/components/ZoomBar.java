package com.lge.camera.components;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.lge.camera.R;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ZoomBar extends BarView {
    public ZoomBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ZoomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomBar(Context context) {
        super(context);
    }

    public void getBarSettingValue() {
        int lValue = getCursorValue();
        if (this.mBarAction.getCameraId() != 1) {
            if (this.mBarAction.getSettingValue(this.barSettingKey).equals(CameraConstants.TYPE_PREFERENCE_NOT_FOUND)) {
                lValue = 0;
            } else {
                lValue = Integer.parseInt(this.mBarAction.getSettingValue(this.barSettingKey));
                int zoomCursorMaxStep = this.mBarAction.getZoomCursorMaxStep();
                int max = this.mBarAction.getMaxZoom();
                if (zoomCursorMaxStep == 90 && max > 0) {
                    setCursorMaxStep(max);
                }
            }
            setCursorValue(lValue);
            setCursor(lValue);
        }
    }

    public void setLayoutDimension() {
        this.MIN_CURSOR_POS = this.mBarAction.getPixelFromDimens(R.dimen.setting_adj_brightness_cursor_min_position);
        this.MAX_CURSOR_POS = this.mBarAction.getPixelFromDimens(R.dimen.setting_adj_cursor_bg_height);
        this.MAX_CURSOR_POS_PORT = this.mBarAction.getPixelFromDimens(R.dimen.setting_adj_cursor_bg_port_height);
        this.CURSOR_HEIGHT = (float) this.mBarAction.getPixelFromDimens(R.dimen.setting_adj_cursor_height);
        this.CURSOR_HEIGHT_PORT = (float) this.mBarAction.getPixelFromDimens(R.dimen.setting_adj_cursor_port_height);
        this.CURSOR_POS_HEIGHT = (int) ((((float) this.MAX_CURSOR_POS) - this.CURSOR_HEIGHT) - ((float) (this.MIN_CURSOR_POS * 2)));
        this.CURSOR_POS_HEIGHT_PORT = (int) ((((float) this.MAX_CURSOR_POS_PORT) - this.CURSOR_HEIGHT_PORT) - ((float) (this.MIN_CURSOR_POS * 2)));
        this.RELEASE_EXPAND_LEFT = this.mBarAction.getPixelFromDimens(R.dimen.zoom_out_button_releaseLeft);
        this.RELEASE_EXPAND_TOP = this.mBarAction.getPixelFromDimens(R.dimen.zoom_out_button_releaseTop);
        this.RELEASE_EXPAND_RIGHT = this.mBarAction.getPixelFromDimens(R.dimen.zoom_out_button_releaseRight);
        this.RELEASE_EXPAND_BOTTOM = this.mBarAction.getPixelFromDimens(R.dimen.zoom_out_button_releaseBottom);
    }

    public void updateBar(int step, boolean gesture, boolean isLongTouch, boolean actionEnd) {
        int lValue = getCursorValue();
        if (checkUpdateZoom(step)) {
            int zoomMaxValue = (int) this.mBarAction.getZoomMaxValue();
            if (actionEnd) {
                int zoomValue = lValue;
                if (getCursorMaxStep() == 90) {
                    zoomValue = (zoomValue * zoomMaxValue) / 90;
                }
                CamLog.d(FaceDetector.TAG, "zoombar : mValue = " + lValue);
                this.mBarAction.setSetting(this.barSettingKey, Integer.toString(zoomValue));
                return;
            }
            if (gesture) {
                if (getCursorMaxStep() == 90) {
                    lValue = (lValue * zoomMaxValue) / 90;
                }
                setCursorMaxStep(zoomMaxValue);
            } else {
                if (getCursorMaxStep() != 90) {
                    try {
                        lValue = (lValue * 90) / zoomMaxValue;
                    } catch (ArithmeticException e) {
                        CamLog.d(FaceDetector.TAG, "ArithmeticException zoomMaxValue = " + zoomMaxValue);
                    }
                }
                setCursorMaxStep(90);
            }
            int updatedValue = checkMaxAndMin(lValue + step);
            if (updatedValue != lValue) {
                lValue = updatedValue;
                Bundle bundle = new Bundle();
                bundle.putInt("mValue", lValue);
                if (lValue == 0) {
                    this.mBarAction.setSetting(this.barSettingKey, Integer.toString(lValue));
                }
                this.mBarAction.doCommand(this.barSettingCommand, null, bundle);
                resetDisplayTimeout();
                this.mBarAction.updateAllBars(0, lValue);
            }
            updateZoom(lValue);
            setCursorValue(lValue);
            setCursor(lValue);
        }
    }

    private void updateZoom(final int value) {
        if (this.mBarAction != null) {
            this.mBarAction.runOnUiThread(new Runnable() {
                public void run() {
                    if (ZoomBar.this.mBarAction != null) {
                        ZoomBar.this.mBarAction.removePostRunnable(this);
                        ZoomProgressBar progress = (ZoomProgressBar) ZoomBar.this.mBarAction.findViewById(R.id.zoom_bar_bg);
                        if (progress != null) {
                            progress.setProgress(value);
                        }
                        ZoomBar.this.updateZoomText();
                    }
                }
            });
        }
    }

    public void updateZoomText() {
        TextView zoomText = (TextView) this.mBarAction.findViewById(R.id.zoom_text);
        if (this.mBarAction != null && zoomText != null) {
            String str = String.format("x %.1f", new Object[]{Double.valueOf(Math.floor((double) (this.mBarAction.getZoomRatio() * 10.0f)) / 10.0d)});
            zoomText.setTypeface(Typeface.createFromAsset(this.mBarAction.getActivity().getAssets(), CameraConstants.ZOOM_MAGNIFICATION_FONT), 1);
            zoomText.setText(str);
        }
    }

    private int checkMaxAndMin(int inputValue) {
        if (inputValue > getCursorMaxStep()) {
            inputValue = getCursorMaxStep();
        }
        if (inputValue < 0) {
            return 0;
        }
        return inputValue;
    }

    public boolean checkUpdateZoom(int step) {
        if (this.mInitial && this.mBarAction != null && this.mBarAction.isPreviewing()) {
            return true;
        }
        return false;
    }

    public void updateBarWithValue(int value, boolean actionEnd) {
        int lValue = getCursorValue();
        if (this.mInitial && this.mBarAction.isPreviewing()) {
            int zoomMaxValue = (int) this.mBarAction.getZoomMaxValue();
            if (actionEnd) {
                int zoomValue = lValue;
                if (getCursorMaxStep() == 90) {
                    zoomValue = Math.round(((float) (zoomValue * zoomMaxValue)) / CameraConstants.SMARTZOOM_ZOOM_AREA_HEIGHT_UVGA);
                }
                this.mBarAction.setSetting(this.barSettingKey, Integer.toString(zoomValue));
            } else if (lValue != value) {
                lValue = value;
                if (getCursorMaxStep() == 90) {
                    lValue = Math.round(((float) (lValue * zoomMaxValue)) / CameraConstants.SMARTZOOM_ZOOM_AREA_HEIGHT_UVGA);
                }
                setCursorMaxStep(zoomMaxValue);
                lValue = checkMaxAndMin(lValue);
                setCursor(lValue);
                Bundle bundle = new Bundle();
                bundle.putInt("mValue", lValue);
                if (lValue == 0) {
                    this.mBarAction.setSetting(this.barSettingKey, Integer.toString(lValue));
                }
                this.mBarAction.doCommand(this.barSettingCommand, null, bundle);
                resetDisplayTimeout();
                this.mBarAction.updateAllBars(0, lValue);
                updateZoom(lValue);
                setCursorValue(lValue);
            }
        }
    }

    public void releaseBar() {
        if (this.mInitial) {
            int zoomMaxValue = (int) this.mBarAction.getZoomMaxValue();
            int zoomValue = getValue();
            if (getCursorMaxStep() == 90) {
                zoomValue = Math.round(((float) (zoomValue * zoomMaxValue)) / CameraConstants.SMARTZOOM_ZOOM_AREA_HEIGHT_UVGA);
            }
            CamLog.d(FaceDetector.TAG, "zoombar : mValue = " + zoomValue);
            this.mBarAction.setSetting(this.barSettingKey, Integer.toString(zoomValue));
            this.mBarAction.updateAllBars(0, getCursorValue());
        }
    }

    protected RotateLayout getBarLayout() {
        return (RotateLayout) this.mBarAction.findViewById(R.id.zoom_rotate_view);
    }

    protected View getBarParentLayout() {
        return this.mBarAction.findViewById(R.id.zoom);
    }
}
