package com.lge.morpho.app.morphopanorama;

import android.util.FloatMath;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.ArrayList;

public class FloatMathUtil {
    private static final float[] CORRECT_MAT_000;
    private static final float[] CORRECT_MAT_090;
    private static final float[] CORRECT_MAT_180;
    private static final float[] CORRECT_MAT_270;
    private static final double EPSILON = 1.0E-8d;
    private static final float NS2S = 1.0E-9f;

    public static void getRotationMatrix(float[] out_mat, float x, float y, float z) {
        float sinx = FloatMath.sin(x);
        float cosx = FloatMath.cos(x);
        float[] x_rmat = new float[]{RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, 0.0f, 0.0f, cosx, -sinx, 0.0f, sinx, cosx};
        float siny = FloatMath.sin(y);
        float cosy = FloatMath.cos(y);
        float[] y_rmat = new float[]{cosy, 0.0f, siny, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, -siny, 0.0f, cosy};
        float sinz = FloatMath.sin(z);
        float cosz = FloatMath.cos(z);
        float[] z_rmat = new float[]{cosz, -sinz, 0.0f, sinz, cosz, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X};
        mulMatrix3x3(y_rmat, y_rmat, x_rmat);
        mulMatrix3x3(out_mat, z_rmat, y_rmat);
    }

    public static void convMatrix16to9(float[] dst, float[] src) {
        if (src.length == 16 || dst.length == 9) {
            dst[0] = src[0];
            dst[1] = src[1];
            dst[2] = src[2];
            dst[3] = src[4];
            dst[4] = src[5];
            dst[5] = src[6];
            dst[6] = src[8];
            dst[7] = src[9];
            dst[8] = src[10];
        }
    }

    public static void getAngleDiff(float[] angle, float[] mat, float[] prev_mat) {
        if (angle.length == 3 && mat.length == 9 && prev_mat.length == 9) {
            float rd6 = ((prev_mat[2] * mat[0]) + (prev_mat[5] * mat[3])) + (prev_mat[8] * mat[6]);
            float rd7 = ((prev_mat[2] * mat[1]) + (prev_mat[5] * mat[4])) + (prev_mat[8] * mat[7]);
            float rd8 = ((prev_mat[2] * mat[2]) + (prev_mat[5] * mat[5])) + (prev_mat[8] * mat[8]);
            angle[0] = (float) Math.atan2((double) (((prev_mat[0] * mat[1]) + (prev_mat[3] * mat[4])) + (prev_mat[6] * mat[7])), (double) (((prev_mat[1] * mat[1]) + (prev_mat[4] * mat[4])) + (prev_mat[7] * mat[7])));
            angle[1] = (float) Math.asin((double) (-rd7));
            angle[2] = (float) Math.atan2((double) (-rd6), (double) rd8);
        }
    }

    public static void getDeltaRotationVector(float[] dst_vector, float[] values, long diff_time) {
        float dT = ((float) diff_time) * NS2S;
        float axisX = values[0];
        float axisY = values[1];
        float axisZ = values[2];
        float omegaMagnitude = FloatMath.sqrt(((axisX * axisX) + (axisY * axisY)) + (axisZ * axisZ));
        if (((double) omegaMagnitude) > EPSILON) {
            axisX /= omegaMagnitude;
            axisY /= omegaMagnitude;
            axisZ /= omegaMagnitude;
        }
        float thetaOverTwo = (omegaMagnitude * dT) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
        float sinThetaOverTwo = FloatMath.sin(thetaOverTwo);
        float cosThetaOverTwo = FloatMath.cos(thetaOverTwo);
        dst_vector[0] = sinThetaOverTwo * axisX;
        dst_vector[1] = sinThetaOverTwo * axisY;
        dst_vector[2] = sinThetaOverTwo * axisZ;
        dst_vector[3] = cosThetaOverTwo;
    }

    static {
        CORRECT_MAT_000 = new float[]{0.0f, RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X};
        CORRECT_MAT_090 = new float[]{RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X};
        CORRECT_MAT_180 = new float[]{0.0f, RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X};
        CORRECT_MAT_270 = new float[]{-1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X};
    }

    public static void rotateMatrix(float[] in_mat, float[] out_mat, int rotate) {
        switch (rotate) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                mulMatrix3x3(out_mat, in_mat, CORRECT_MAT_000);
            case MediaProviderUtils.ROTATION_90 /*90*/:
                mulMatrix3x3(out_mat, in_mat, CORRECT_MAT_090);
            case MediaProviderUtils.ROTATION_180 /*180*/:
                mulMatrix3x3(out_mat, in_mat, CORRECT_MAT_180);
            case Tag.IMAGE_DESCRIPTION /*270*/:
                mulMatrix3x3(out_mat, in_mat, CORRECT_MAT_270);
            default:
        }
    }

    public static void mulMatrix3x3(float[] dst_mat, float[] in_m1, float[] in_m2) {
        float[] matrix = new float[9];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                float sum = 0.0f;
                for (int k = 0; k < 3; k++) {
                    sum += in_m1[(i * 3) + k] * in_m2[(k * 3) + j];
                }
                matrix[(i * 3) + j] = sum;
            }
        }
        System.arraycopy(matrix, 0, dst_mat, 0, matrix.length);
    }

    public static double radianToDegree(double rad) {
        return Math.toDegrees(rad);
    }

    public static boolean getAverage(float[] dst_value, ArrayList<float[]> src_value_list) {
        if (dst_value == null || src_value_list == null) {
            return false;
        }
        int i;
        double[] total = new double[dst_value.length];
        int size = src_value_list.size();
        for (i = 0; i < size; i++) {
            float[] value = (float[]) src_value_list.get(i);
            for (int j = 0; j < dst_value.length; j++) {
                total[j] = total[j] + ((double) value[j]);
            }
        }
        if (size > 0) {
            for (i = 0; i < dst_value.length; i++) {
                dst_value[i] = (float) (total[i] / ((double) size));
            }
        }
        return true;
    }
}
