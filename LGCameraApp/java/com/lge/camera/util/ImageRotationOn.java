package com.lge.camera.util;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.hardware.Camera.Parameters;
import android.location.Location;
import android.net.Uri;
import com.lge.camera.properties.ModelProperties;
import com.lge.olaworks.library.FaceDetector;

public class ImageRotationOn extends ImageHandler {
    private static final int ROTATE_CCR_90 = 270;
    private static final int ROTATE_CR_180 = 180;
    private static final int ROTATE_CR_90 = 90;
    private static final int ROTATE_ZERO = 0;
    private int mLastRotation;

    public ImageRotationOn() {
        this.mLastRotation = -1;
    }

    public void resetRotation() {
        this.mLastRotation = -1;
    }

    public boolean setRotation(Parameters param, int rotation) {
        if (rotation != -1) {
            CamLog.d(FaceDetector.TAG, "setRotation [" + this.mLastRotation + "/" + rotation + "]");
            if (!ModelProperties.isOMAP4Chipset()) {
                param.setRotation(rotation);
                this.mLastRotation = rotation;
                return true;
            } else if (this.mLastRotation != rotation) {
                param.setRotation(rotation);
                CamLog.d(FaceDetector.TAG, "setRotation [" + this.mLastRotation + "/" + rotation + "]");
                this.mLastRotation = rotation;
                return true;
            }
        }
        return false;
    }

    public void startOlaPanorama(Parameters param, int rotation) {
    }

    public Uri addImage(ContentResolver cr, String title, long dateTaken, Location location, String directory, String filename, int degree, boolean isBurst) {
        return ImageManager.addImage(cr, title, dateTaken, location, directory, filename, 0, isBurst);
    }

    public Uri addImage(ContentResolver cr, String title, long dateTaken, Location location, String directory, String filename, Bitmap source, byte[] jpegData, int degree, boolean isBurst) {
        return ImageManager.addImage(cr, title, dateTaken, location, directory, filename, source, jpegData, 0, isBurst);
    }

    public boolean saveTempFileForTimeMachineShot(byte[] jpegData, String directory, String filename, String ext) {
        return ImageManager.saveTempFileForTimeMachineShot(jpegData, directory, filename, ext);
    }

    public int saveContiShotImage(byte[] data, String filename, int rotation, int width, int height) {
        return -1;
    }

    public Bitmap getImage(Bitmap bmp, int degree, boolean mirror) {
        return getRotated(bmp, degree, mirror);
    }

    public Uri addJpegImage(ContentResolver cr, String title, long dateTaken, byte[] jpegData, Location location, String directory, String filename, int degree, boolean isBurst) {
        return ImageManager.addJpegImage(cr, title, dateTaken, jpegData, location, directory, filename, degree, isBurst);
    }

    public static byte[] rotateYUV420(byte[] data, int width, int height, int rotation) {
        int size = width * height;
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        byte[] dest = new byte[((size * 3) / 2)];
        int j;
        int i;
        switch (rotation) {
            case ROTATE_CR_90 /*90*/:
                for (j = 0; j < width; j += 2) {
                    for (i = 0; i < height; i += 2) {
                        dest[(height * j) + i] = data[((height - (i + 1)) * width) + j];
                        dest[(i + 1) + (height * j)] = data[((height - (i + 2)) * width) + j];
                        dest[(height + i) + (height * j)] = data[(((height - (i + 1)) * width) + j) + 1];
                        dest[((height + i) + 1) + (height * j)] = data[(((height - (i + 2)) * width) + j) + 1];
                        dest[(size + i) + (halfHeight * j)] = data[((((halfHeight - 1) - (i / 2)) * width) + size) + j];
                        dest[((size + i) + (halfHeight * j)) + 1] = data[(((((halfHeight - 1) - (i / 2)) * width) + size) + j) + 1];
                    }
                }
                break;
            case ROTATE_CR_180 /*180*/:
                for (i = 0; i < height; i += 2) {
                    for (j = 0; j < width; j += 2) {
                        dest[(width * i) + j] = data[(((width - 2) - j) + ((height - (i + 1)) * width)) + 1];
                        dest[(j + 1) + (width * i)] = data[((width - 2) - j) + ((height - (i + 1)) * width)];
                        dest[(width + j) + (width * i)] = data[(((width - 2) - j) + ((height - (i + 2)) * width)) + 1];
                        dest[((width + j) + 1) + (width * i)] = data[((width - 2) - j) + ((height - (i + 2)) * width)];
                        dest[(size + j) + (halfWidth * i)] = data[(((width - 2) - j) + size) + (((halfHeight - 1) - (i / 2)) * width)];
                        dest[((size + j) + (halfWidth * i)) + 1] = data[((((width - 2) - j) + size) + (((halfHeight - 1) - (i / 2)) * width)) + 1];
                    }
                }
                break;
            case ROTATE_CCR_90 /*270*/:
                for (j = 0; j < width; j += 2) {
                    for (i = 0; i < height; i += 2) {
                        dest[(height * j) + i] = data[(((width - 2) - j) + (width * i)) + 1];
                        dest[(i + 1) + (height * j)] = data[((((width - 2) - j) + (width * i)) + width) + 1];
                        dest[(height + i) + (height * j)] = data[((width - 2) - j) + (width * i)];
                        dest[((height + i) + 1) + (height * j)] = data[(((width - 2) - j) + (width * i)) + width];
                        dest[(size + i) + (halfHeight * j)] = data[(((width - 2) - j) + size) + (halfWidth * i)];
                        dest[((size + i) + (halfHeight * j)) + 1] = data[((((width - 2) - j) + size) + (halfWidth * i)) + 1];
                    }
                }
                break;
        }
        return dest;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public byte[] convertYuvToJpeg(byte[] r17, int r18, int r19, int r20, int r21) {
        /*
        r16 = this;
        r3 = "CameraApp";
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "resolution [";
        r4 = r4.append(r5);
        r0 = r18;
        r4 = r4.append(r0);
        r5 = "x";
        r4 = r4.append(r5);
        r0 = r19;
        r4 = r4.append(r0);
        r5 = "] data size [";
        r4 = r4.append(r5);
        r0 = r17;
        r5 = r0.length;
        r4 = r4.append(r5);
        r5 = "]";
        r4 = r4.append(r5);
        r4 = r4.toString();
        com.lge.camera.util.CamLog.d(r3, r4);
        r3 = "CameraApp";
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "rotation = [";
        r4 = r4.append(r5);
        r0 = r20;
        r4 = r4.append(r0);
        r5 = "] ";
        r4 = r4.append(r5);
        r4 = r4.toString();
        com.lge.camera.util.CamLog.d(r3, r4);
        r0 = r17;
        r3 = r0.length;
        r4 = r18 * r19;
        r4 = r4 * 3;
        r4 = r4 / 2;
        if (r3 == r4) goto L_0x0066;
    L_0x0064:
        r12 = 0;
    L_0x0065:
        return r12;
    L_0x0066:
        if (r20 == 0) goto L_0x0082;
    L_0x0068:
        r14 = rotateYUV420(r17, r18, r19, r20);
        if (r14 == 0) goto L_0x00c0;
    L_0x006e:
        r17 = r14;
        r3 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        r0 = r20;
        if (r0 == r3) goto L_0x007c;
    L_0x0076:
        r3 = 90;
        r0 = r20;
        if (r0 != r3) goto L_0x0082;
    L_0x007c:
        r15 = r18;
        r18 = r19;
        r19 = r15;
    L_0x0082:
        r2 = new android.graphics.YuvImage;
        r4 = 17;
        r7 = 0;
        r3 = r17;
        r5 = r18;
        r6 = r19;
        r2.<init>(r3, r4, r5, r6, r7);
        r12 = 0;
        if (r2 == 0) goto L_0x0065;
    L_0x0093:
        r8 = new java.io.ByteArrayOutputStream;
        r8.<init>();
        r13 = 0;
        r11 = new android.graphics.Rect;	 Catch:{ Exception -> 0x00e4 }
        r3 = 0;
        r4 = 0;
        r0 = r18;
        r1 = r19;
        r11.<init>(r3, r4, r0, r1);	 Catch:{ Exception -> 0x00e4 }
        if (r11 == 0) goto L_0x00b2;
    L_0x00a6:
        r3 = r2.getStrides();	 Catch:{ Exception -> 0x00e4 }
        if (r3 == 0) goto L_0x00b2;
    L_0x00ac:
        r0 = r21;
        r13 = r2.compressToJpeg(r11, r0, r8);	 Catch:{ Exception -> 0x00e4 }
    L_0x00b2:
        if (r13 == 0) goto L_0x00cf;
    L_0x00b4:
        r12 = r8.toByteArray();	 Catch:{ Exception -> 0x00e4 }
    L_0x00b8:
        r8.close();	 Catch:{ Exception -> 0x00d1 }
        r8 = 0;
    L_0x00bc:
        r2 = 0;
        r17 = 0;
        goto L_0x0065;
    L_0x00c0:
        r3 = "CameraApp";
        r4 = "Rotated data is null!";
        r5 = 0;
        r5 = new java.lang.Object[r5];
        r4 = java.lang.String.format(r4, r5);
        com.lge.camera.util.CamLog.d(r3, r4);
        goto L_0x0082;
    L_0x00cf:
        r12 = 0;
        goto L_0x00b8;
    L_0x00d1:
        r10 = move-exception;
        r3 = "CameraApp";
        r4 = "Exception in finally block";
        r5 = 0;
        r5 = new java.lang.Object[r5];
        r4 = java.lang.String.format(r4, r5);
        com.lge.camera.util.CamLog.e(r3, r4);
        r10.printStackTrace();
        goto L_0x00bc;
    L_0x00e4:
        r9 = move-exception;
        r3 = "CameraApp";
        r4 = "Exception while YuvImage.compressToJpeg()";
        r5 = 0;
        r5 = new java.lang.Object[r5];	 Catch:{ all -> 0x010f }
        r4 = java.lang.String.format(r4, r5);	 Catch:{ all -> 0x010f }
        com.lge.camera.util.CamLog.e(r3, r4);	 Catch:{ all -> 0x010f }
        r9.printStackTrace();	 Catch:{ all -> 0x010f }
        r12 = 0;
        r8.close();	 Catch:{ Exception -> 0x00fc }
        r8 = 0;
        goto L_0x00bc;
    L_0x00fc:
        r10 = move-exception;
        r3 = "CameraApp";
        r4 = "Exception in finally block";
        r5 = 0;
        r5 = new java.lang.Object[r5];
        r4 = java.lang.String.format(r4, r5);
        com.lge.camera.util.CamLog.e(r3, r4);
        r10.printStackTrace();
        goto L_0x00bc;
    L_0x010f:
        r3 = move-exception;
        r8.close();	 Catch:{ Exception -> 0x0115 }
        r8 = 0;
    L_0x0114:
        throw r3;
    L_0x0115:
        r10 = move-exception;
        r4 = "CameraApp";
        r5 = "Exception in finally block";
        r6 = 0;
        r6 = new java.lang.Object[r6];
        r5 = java.lang.String.format(r5, r6);
        com.lge.camera.util.CamLog.e(r4, r5);
        r10.printStackTrace();
        goto L_0x0114;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.util.ImageRotationOn.convertYuvToJpeg(byte[], int, int, int, int):byte[]");
    }
}
