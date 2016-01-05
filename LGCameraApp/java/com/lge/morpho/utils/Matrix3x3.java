package com.lge.morpho.utils;

import android.util.FloatMath;
import android.util.Log;
import com.lge.camera.components.RotateView;

public class Matrix3x3 {
    private float[] mMat;
    private double[] mMatD;

    public Matrix3x3(boolean set_identity) {
        this.mMatD = new double[9];
        this.mMat = new float[9];
        if (set_identity) {
            setIdentity();
        }
    }

    public Matrix3x3(float[] src) {
        this.mMatD = new double[9];
        this.mMat = new float[9];
        if (this.mMat.length == src.length) {
            System.arraycopy(src, 0, this.mMat, 0, this.mMat.length);
        }
    }

    public void setIdentity() {
        this.mMat[0] = RotateView.DEFAULT_TEXT_SCALE_X;
        this.mMat[1] = 0.0f;
        this.mMat[2] = 0.0f;
        this.mMat[3] = 0.0f;
        this.mMat[4] = RotateView.DEFAULT_TEXT_SCALE_X;
        this.mMat[5] = 0.0f;
        this.mMat[6] = 0.0f;
        this.mMat[7] = 0.0f;
        this.mMat[8] = RotateView.DEFAULT_TEXT_SCALE_X;
    }

    public void set(float[] src) {
        if (src.length == this.mMat.length) {
            System.arraycopy(src, 0, this.mMat, 0, this.mMat.length);
        }
    }

    public float[] get() {
        return this.mMat;
    }

    public double[] toDoubleArray() {
        for (int i = 0; i < this.mMat.length; i++) {
            this.mMatD[i] = (double) this.mMat[i];
        }
        return this.mMatD;
    }

    public void copyDoubleArray(double[] dst) {
        for (int i = 0; i < this.mMat.length; i++) {
            dst[i] = (double) this.mMat[i];
        }
    }

    public void print(String tag) {
        Log.d(tag, String.format("{ %6.3f, %6.3f, %6.3f  ", new Object[]{Float.valueOf(this.mMat[0]), Float.valueOf(this.mMat[1]), Float.valueOf(this.mMat[2])}));
        Log.d(tag, String.format("  %6.3f, %6.3f, %6.3f  ", new Object[]{Float.valueOf(this.mMat[3]), Float.valueOf(this.mMat[4]), Float.valueOf(this.mMat[5])}));
        Log.d(tag, String.format("  %6.3f, %6.3f, %6.3f }", new Object[]{Float.valueOf(this.mMat[6]), Float.valueOf(this.mMat[7]), Float.valueOf(this.mMat[8])}));
    }

    public static void multiply(Matrix3x3 dst_mat, Matrix3x3 src_mat1, Matrix3x3 src_mat2) {
        multiply(dst_mat.get(), src_mat1.get(), src_mat2.get());
    }

    public static void multiply(float[] dst_mat, float[] in_m1, float[] in_m2) {
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
        multiply(y_rmat, y_rmat, x_rmat);
        multiply(out_mat, z_rmat, y_rmat);
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
}
