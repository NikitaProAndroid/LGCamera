package com.lge.morpho.app.morphopanorama;

import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.ArrayList;

public class MathUtil {
    private static final double[] CORRECT_MAT_000;
    private static final double[] CORRECT_MAT_090;
    private static final double[] CORRECT_MAT_180;
    private static final double[] CORRECT_MAT_270;
    private static final double EPSILON = 1.0E-8d;
    private static final double NS2S = 9.999999717180685E-10d;

    public static void getRotationMatrix(double[] out_mat, double x, double y, double z) {
        double sinx = Math.sin(x);
        double cosx = Math.cos(x);
        x_rmat = new double[9];
        x_rmat[5] = -sinx;
        x_rmat[6] = 0.0d;
        x_rmat[7] = sinx;
        x_rmat[8] = cosx;
        double siny = Math.sin(y);
        double cosy = Math.cos(y);
        y_rmat = new double[9];
        y_rmat[6] = -siny;
        y_rmat[7] = 0.0d;
        y_rmat[8] = cosy;
        double sinz = Math.sin(z);
        double cosz = Math.cos(z);
        z_rmat = new double[9];
        z_rmat[1] = -sinz;
        z_rmat[2] = 0.0d;
        z_rmat[3] = sinz;
        z_rmat[4] = cosz;
        z_rmat[5] = 0.0d;
        z_rmat[6] = 0.0d;
        z_rmat[7] = 0.0d;
        z_rmat[8] = 1.0d;
        mulMatrix3x3(y_rmat, y_rmat, x_rmat);
        mulMatrix3x3(out_mat, z_rmat, y_rmat);
    }

    public static void convMatrix16to9(double[] dst, double[] src) {
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

    public static void getAngleDiff(double[] angle, double[] mat, double[] prev_mat) {
        if (angle.length == 3 && mat.length == 9 && prev_mat.length == 9) {
            double rd6 = ((prev_mat[2] * mat[0]) + (prev_mat[5] * mat[3])) + (prev_mat[8] * mat[6]);
            double rd7 = ((prev_mat[2] * mat[1]) + (prev_mat[5] * mat[4])) + (prev_mat[8] * mat[7]);
            double rd8 = ((prev_mat[2] * mat[2]) + (prev_mat[5] * mat[5])) + (prev_mat[8] * mat[8]);
            angle[0] = Math.atan2(((prev_mat[0] * mat[1]) + (prev_mat[3] * mat[4])) + (prev_mat[6] * mat[7]), ((prev_mat[1] * mat[1]) + (prev_mat[4] * mat[4])) + (prev_mat[7] * mat[7]));
            angle[1] = Math.asin(-rd7);
            angle[2] = Math.atan2(-rd6, rd8);
        }
    }

    public static void getDeltaRotationVector(double[] dst_vector, double[] values, double diff_time) {
        double dT = diff_time * NS2S;
        double axisX = values[0];
        double axisY = values[1];
        double axisZ = values[2];
        double omegaMagnitude = Math.sqrt(((axisX * axisX) + (axisY * axisY)) + (axisZ * axisZ));
        if (omegaMagnitude > EPSILON) {
            axisX /= omegaMagnitude;
            axisY /= omegaMagnitude;
            axisZ /= omegaMagnitude;
        }
        double thetaOverTwo = (omegaMagnitude * dT) / 2.0d;
        double sinThetaOverTwo = Math.sin(thetaOverTwo);
        double cosThetaOverTwo = Math.cos(thetaOverTwo);
        dst_vector[0] = sinThetaOverTwo * axisX;
        dst_vector[1] = sinThetaOverTwo * axisY;
        dst_vector[2] = sinThetaOverTwo * axisZ;
        dst_vector[3] = cosThetaOverTwo;
    }

    static {
        CORRECT_MAT_000 = new double[]{0.0d, 1.0d, 0.0d, -1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 1.0d};
        CORRECT_MAT_090 = new double[]{1.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 0.0d, 1.0d};
        CORRECT_MAT_180 = new double[]{0.0d, 1.0d, 0.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 1.0d};
        CORRECT_MAT_270 = new double[]{-1.0d, 0.0d, 0.0d, 0.0d, -1.0d, 0.0d, 0.0d, 0.0d, 1.0d};
    }

    public static void rotateMatrix(double[] in_mat, double[] out_mat, int rotate) {
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

    public static void mulMatrix3x3(double[] dst_mat, double[] in_m1, double[] in_m2) {
        double[] matrix = new double[9];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                double sum = 0.0d;
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

    public static boolean getAverage(double[] dst_value, ArrayList<double[]> src_value_list) {
        if (dst_value == null || src_value_list == null) {
            return false;
        }
        int i;
        double[] total = new double[dst_value.length];
        int size = src_value_list.size();
        for (i = 0; i < size; i++) {
            double[] value = (double[]) src_value_list.get(i);
            for (int j = 0; j < dst_value.length; j++) {
                total[j] = total[j] + value[j];
            }
        }
        if (size > 0) {
            for (i = 0; i < dst_value.length; i++) {
                dst_value[i] = total[i] / ((double) size);
            }
        }
        return true;
    }
}
