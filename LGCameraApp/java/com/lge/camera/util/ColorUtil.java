package com.lge.camera.util;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.voiceshutter.library.LGKeyRec;

public class ColorUtil {
    private static double[] DELTA_INDEX;

    public static ColorMatrixColorFilter getBrightnessAndContrast(float brightness, float contrast) {
        ColorMatrix cm = new ColorMatrix();
        cm.set(new float[]{contrast, 0.0f, 0.0f, 0.0f, brightness, 0.0f, contrast, 0.0f, 0.0f, brightness, 0.0f, 0.0f, contrast, 0.0f, brightness, 0.0f, 0.0f, 0.0f, contrast, 0.0f});
        return new ColorMatrixColorFilter(cm);
    }

    public static ColorMatrixColorFilter getDimColor() {
        return getBrightnessAndContrast(50.0f, 0.6f);
    }

    public static ColorMatrixColorFilter getQuickButtonDimColor() {
        return getBrightnessAndContrast(-40.0f, 0.5f);
    }

    public static ColorMatrixColorFilter getDimColorExpand() {
        return getBrightnessAndContrast(70.0f, 0.2f);
    }

    public static ColorMatrixColorFilter getEnteringQFLColor() {
        return getBrightnessAndContrast(150.0f, 0.6f);
    }

    public static ColorMatrixColorFilter getDefaultColor() {
        return getBrightnessAndContrast(0.0f, RotateView.DEFAULT_TEXT_SCALE_X);
    }

    static {
        DELTA_INDEX = new double[]{0.0d, 0.01d, 0.02d, 0.04d, 0.05d, 0.06d, 0.07d, 0.08d, 0.1d, 0.11d, 0.12d, 0.14d, 0.15d, 0.16d, 0.17d, 0.18d, 0.2d, 0.21d, 0.22d, 0.24d, 0.25d, 0.27d, 0.28d, 0.3d, 0.32d, 0.34d, 0.36d, 0.38d, 0.4d, 0.42d, 0.44d, 0.46d, 0.48d, 0.5d, 0.53d, 0.56d, 0.59d, 0.62d, 0.65d, 0.68d, 0.71d, 0.74d, 0.77d, 0.8d, 0.83d, 0.86d, 0.89d, 0.92d, 0.95d, 0.98d, 1.0d, 1.06d, 1.12d, 1.18d, 1.24d, 1.3d, 1.36d, 1.42d, 1.48d, 1.54d, 1.6d, 1.66d, 1.72d, 1.78d, 1.84d, 1.9d, 1.96d, 2.0d, 2.12d, 2.25d, 2.37d, 2.5d, 2.62d, 2.75d, 2.87d, 3.0d, 3.2d, 3.4d, 3.6d, 3.8d, 4.0d, 4.3d, 4.7d, 4.9d, 5.0d, 5.5d, 6.0d, 6.5d, 6.8d, 7.0d, 7.3d, 7.5d, 7.8d, 8.0d, 8.4d, 8.7d, 9.0d, 9.4d, 9.6d, 9.8d, 10.0d};
    }

    public static void adjustHue(ColorMatrix cm, float value) {
        value = (cleanValue(value, CameraConstants.PIP_SUBWINDOW_INIT_HEIGHT_UVGA) / CameraConstants.PIP_SUBWINDOW_INIT_HEIGHT_UVGA) * 3.1415927f;
        if (value > 0.0f || value < 0.0f) {
            float cosVal = (float) Math.cos((double) value);
            float sinVal = (float) Math.sin((double) value);
            cm.postConcat(new ColorMatrix(new float[]{(((RotateView.DEFAULT_TEXT_SCALE_X - 0.213f) * cosVal) + 0.213f) + ((-1046092972) * sinVal), (((-1060571709) * cosVal) + 0.715f) + ((-1060571709) * sinVal), (((-1033073852) * cosVal) + 0.072f) + ((RotateView.DEFAULT_TEXT_SCALE_X - 0.072f) * sinVal), 0.0f, 0.0f, (((-1046092972) * cosVal) + 0.213f) + (0.143f * sinVal), (((RotateView.DEFAULT_TEXT_SCALE_X - 0.715f) * cosVal) + 0.715f) + (0.14f * sinVal), (((-1033073852) * cosVal) + 0.072f) + (-0.283f * sinVal), 0.0f, 0.0f, (((-1046092972) * cosVal) + 0.213f) + ((-(RotateView.DEFAULT_TEXT_SCALE_X - 0.213f)) * sinVal), (((-1060571709) * cosVal) + 0.715f) + (sinVal * 0.715f), (((RotateView.DEFAULT_TEXT_SCALE_X - 0.072f) * cosVal) + 0.072f) + (sinVal * 0.072f), 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X}));
        }
    }

    public static void adjustBrightness(ColorMatrix cm, float value) {
        value = cleanValue(value, CameraConstants.PIP_VIEW_ALLOWABLE_MOVEMENT_EXTENT_FOR_TOGGLE);
        if (value > 0.0f || value < 0.0f) {
            cm.postConcat(new ColorMatrix(new float[]{RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, 0.0f, 0.0f, value, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, 0.0f, value, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, value, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X}));
        }
    }

    public static void adjustContrast(ColorMatrix cm, int value) {
        value = (int) cleanValue((float) value, CameraConstants.PIP_VIEW_ALLOWABLE_MOVEMENT_EXTENT_FOR_TOGGLE);
        if (((float) value) > 0.0f || ((float) value) < 0.0f) {
            float x;
            if (value < 0) {
                x = (float) (((value / 100) * 127) + 127);
            } else {
                x = (float) (value % 1);
                if (x < 0.0f || x > 0.0f) {
                    x = (((float) DELTA_INDEX[value << 0]) * (RotateView.DEFAULT_TEXT_SCALE_X - x)) + (((float) DELTA_INDEX[(value << 0) + 1]) * x);
                } else {
                    x = (float) DELTA_INDEX[value];
                }
                x = (x * 127.0f) + 127.0f;
            }
            cm.postConcat(new ColorMatrix(new float[]{x / 127.0f, 0.0f, 0.0f, 0.0f, (127.0f - x) * 0.5f, 0.0f, x / 127.0f, 0.0f, 0.0f, (127.0f - x) * 0.5f, 0.0f, 0.0f, x / 127.0f, 0.0f, (127.0f - x) * 0.5f, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X}));
        }
    }

    public static void adjustSaturation(ColorMatrix cm, float value) {
        value = cleanValue(value, CameraConstants.PIP_VIEW_ALLOWABLE_MOVEMENT_EXTENT_FOR_TOGGLE);
        if (value > 0.0f || value < 0.0f) {
            float x = RotateView.DEFAULT_TEXT_SCALE_X + (value > 0.0f ? (3.0f * value) / CameraConstants.PIP_VIEW_ALLOWABLE_MOVEMENT_EXTENT_FOR_TOGGLE : value / CameraConstants.PIP_VIEW_ALLOWABLE_MOVEMENT_EXTENT_FOR_TOGGLE);
            cm.postConcat(new ColorMatrix(new float[]{((RotateView.DEFAULT_TEXT_SCALE_X - x) * 0.3086f) + x, (RotateView.DEFAULT_TEXT_SCALE_X - x) * 0.6094f, (RotateView.DEFAULT_TEXT_SCALE_X - x) * 0.082f, 0.0f, 0.0f, (RotateView.DEFAULT_TEXT_SCALE_X - x) * 0.3086f, ((RotateView.DEFAULT_TEXT_SCALE_X - x) * 0.6094f) + x, (RotateView.DEFAULT_TEXT_SCALE_X - x) * 0.082f, 0.0f, 0.0f, (RotateView.DEFAULT_TEXT_SCALE_X - x) * 0.3086f, (RotateView.DEFAULT_TEXT_SCALE_X - x) * 0.6094f, ((RotateView.DEFAULT_TEXT_SCALE_X - x) * 0.082f) + x, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X}));
        }
    }

    protected static float cleanValue(float p_val, float p_limit) {
        return Math.min(p_limit, Math.max(-p_limit, p_val));
    }

    public static ColorFilter adjustColor(int brightness, int contrast, int saturation, int hue) {
        ColorMatrix cm = new ColorMatrix();
        adjustHue(cm, (float) hue);
        adjustContrast(cm, contrast);
        adjustBrightness(cm, (float) brightness);
        adjustSaturation(cm, (float) saturation);
        return new ColorMatrixColorFilter(cm);
    }

    public int getSettingMenuDimColor() {
        return Color.argb(Ola_ShotParam.AnimalMask_Random, 51, 56, 59);
    }

    public static int getItemColor(int type) {
        switch (type) {
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
                return -1;
            case LGKeyRec.EVENT_STARTED /*3*/:
                return Color.argb(Ola_ShotParam.AnimalMask_Random, 190, 190, 190);
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                return Color.argb(Ola_ShotParam.AnimalMask_Random, 51, 56, 59);
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                return Color.argb(Ola_ShotParam.AnimalMask_Random, 0, 78, 96);
            default:
                return -16777216;
        }
    }
}
