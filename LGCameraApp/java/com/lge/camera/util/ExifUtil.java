package com.lge.camera.util;

import android.graphics.Bitmap;
import android.location.Location;
import android.media.ExifInterface;
import android.text.format.DateFormat;
import com.lge.camera.properties.CameraConstants;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Ola_Exif;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.Exif;
import com.lge.voiceshutter.library.LGKeyRec;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class ExifUtil {
    public static final int DEFAULT_IMAGE_EXIF_WIDTH = 800;
    private static final String TAG = "CameraApp";
    private static int mLength;
    private static int mOffSet;

    private ExifUtil() {
    }

    public static void setExif(String targetFilePath, String flashMode, float focalLength, Location loc, int imageWidth, int imageLength, String iso, int degree, String whiteBalace) {
        long dateTaken = System.currentTimeMillis();
        String nowTime = DateFormat.format("yyyy:MM:dd kk:mm:ss", dateTaken).toString();
        try {
            ExifInterface exif = new ExifInterface(targetFilePath);
            exif.setAttribute("DateTime", nowTime);
            if (flashMode != null) {
                exif.setAttribute("Flash", convertFlashModeToExifValue(flashMode));
            }
            if (focalLength > -1.0f) {
                exif.setAttribute("FocalLength", String.valueOf(CameraConstants.PIP_VIEW_ALLOWABLE_MOVEMENT_EXTENT_FOR_TOGGLE * focalLength) + "/100");
            }
            if (loc != null) {
                double latitude = loc.getLatitude();
                double longitude = loc.getLongitude();
                SimpleDateFormat dateStamp = new SimpleDateFormat("yyyy:MM:dd");
                dateStamp.setTimeZone(TimeZone.getTimeZone("UTC"));
                CamLog.d(TAG, "Lat:" + latitude + " LON:" + longitude);
                String lat_str = convertLocation(latitude, 2);
                String lat_str_ref = latitudeValueToNorS(latitude);
                String lon_str = convertLocation(longitude, 2);
                String lon_str_ref = longitudeValueToEorW(longitude);
                String str = "GPSDateStamp";
                exif.setAttribute(r24, dateStamp.format(Long.valueOf(loc.getTime())));
                Calendar gpsTimeStampCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                gpsTimeStampCalendar.setTimeInMillis(dateTaken);
                int hour = gpsTimeStampCalendar.get(11);
                int min = gpsTimeStampCalendar.get(12);
                int sec = gpsTimeStampCalendar.get(13);
                String timeStamp = String.format("%d/1,%d/1,%d/1", new Object[]{Integer.valueOf(hour), Integer.valueOf(min), Integer.valueOf(sec)});
                CamLog.d(TAG, "GPS_TIMESTAMP=" + timeStamp);
                exif.setAttribute("GPSTimeStamp", timeStamp);
                exif.setAttribute("GPSLatitude", lat_str);
                exif.setAttribute("GPSLatitudeRef", lat_str_ref);
                exif.setAttribute("GPSLongitude", lon_str);
                exif.setAttribute("GPSLongitudeRef", lon_str_ref);
            }
            exif.setAttribute("DateTimeOriginal", nowTime);
            exif.setAttribute("DateTimeDigitized", nowTime);
            exif.setAttribute("ImageWidth", String.valueOf(imageWidth));
            exif.setAttribute("ImageLength", String.valueOf(imageLength));
            if (iso != null) {
                exif.setAttribute("ISOSpeedRatings", iso);
            }
            exif.setAttribute("Make", Ola_Exif.MAKE_STR);
            exif.setAttribute("Model", CameraConstants.EXIF_STR_MODEL);
            exif.setAttribute("Orientation", String.valueOf(convertDegreeToExifOrientation(degree)));
            if (whiteBalace != null) {
                exif.setAttribute("WhiteBalance", convertWbModeToExifValue(whiteBalace));
            }
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setExifMakeModel(String targetFilePath) {
        try {
            ExifInterface exif = new ExifInterface(targetFilePath);
            exif.setAttribute("Make", Ola_Exif.MAKE_STR);
            exif.setAttribute("Model", CameraConstants.EXIF_STR_MODEL);
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String latlocConvert(double coordinate) {
        if (coordinate < -180.0d || coordinate > 180.0d || Double.isNaN(coordinate)) {
            throw new IllegalArgumentException("coordinate=" + coordinate);
        }
        StringBuilder sb = new StringBuilder();
        if (coordinate < 0.0d) {
            sb.append('-');
            coordinate = -coordinate;
        }
        int degrees = (int) Math.floor(coordinate);
        sb.append(degrees);
        sb.append("/1,");
        coordinate = (coordinate - ((double) degrees)) * 60.0d;
        int minutes = (int) Math.floor(coordinate);
        sb.append(minutes);
        sb.append("/1,");
        sb.append((coordinate - ((double) minutes)) * 60.0d);
        sb.append("/1");
        return sb.toString();
    }

    public static double[] locationConvert(double coordinate) {
        if (coordinate < -180.0d || coordinate > 180.0d || Double.isNaN(coordinate)) {
            throw new IllegalArgumentException("coordinate=" + coordinate);
        }
        if (coordinate < 0.0d) {
            coordinate = -coordinate;
        }
        result = new double[3];
        int degrees = (int) Math.floor(coordinate);
        CamLog.d(TAG, "locationConvert : degrees[" + degrees + "]");
        result[0] = (double) degrees;
        coordinate = (coordinate - ((double) degrees)) * 60.0d;
        int minutes = (int) Math.floor(coordinate);
        CamLog.d(TAG, "locationConvert : minutes[" + minutes + "]");
        result[1] = (double) minutes;
        int coordi = (int) Math.floor(1000.0d * ((coordinate - ((double) minutes)) * 60.0d));
        CamLog.d(TAG, "locationConvert : coordi[" + coordi + "]");
        result[2] = (double) coordi;
        return result;
    }

    public static String convertLocation(double coordinate, int type) {
        CamLog.d(TAG, "START coordinate=" + coordinate + " type=" + type);
        String[] arrCoordinate = Location.convert(coordinate, type).split(":");
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                sb.append(arrCoordinate[0]);
                sb.append("/1,0/1,0/1");
                break;
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                sb.append(arrCoordinate[0]);
                sb.append("/1,");
                sb.append(arrCoordinate[1]);
                sb.append("/1,0/1");
                break;
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                sb.append(arrCoordinate[0]);
                sb.append("/1,");
                sb.append(arrCoordinate[1]);
                sb.append("/1,");
                sb.append(arrCoordinate[2]);
                sb.append("/1");
                break;
        }
        if ('-' == sb.charAt(0)) {
            sb.deleteCharAt(0);
        }
        String result = sb.toString();
        CamLog.d(TAG, "END result=" + result);
        return result;
    }

    private static String latitudeValueToNorS(double value) {
        if (value > 0.0d) {
            return "N";
        }
        return "S";
    }

    private static String longitudeValueToEorW(double value) {
        if (value > 0.0d) {
            return "E";
        }
        return "W";
    }

    private static String convertFlashModeToExifValue(String mode) {
        int result = 0;
        if (LGT_Limit.ISP_AUTOMODE_AUTO.equals(mode)) {
            result = 24;
        } else if (CameraConstants.SMART_MODE_OFF.equals(mode)) {
            result = 16;
        } else if (CameraConstants.SMART_MODE_ON.equals(mode)) {
            result = 9;
        } else if (!"red-eye".equals(mode)) {
            if (CameraConstants.FLASH_TORCH.equals(mode)) {
                result = 1;
            } else {
                result = 0;
            }
        }
        return String.valueOf(result);
    }

    private static String convertWbModeToExifValue(String whiteBalace) {
        int result;
        if (LGT_Limit.ISP_AUTOMODE_AUTO.equals(whiteBalace)) {
            result = 0;
        } else {
            result = 1;
        }
        return String.valueOf(result);
    }

    public static int convertExifOrientationToDegree(int exifOrientation) {
        switch (exifOrientation) {
            case LGKeyRec.EVENT_STARTED /*3*/:
                return MediaProviderUtils.ROTATION_180;
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                return 90;
            case LGKeyRec.EVENT_NEED_MORE_AUDIO /*8*/:
                return Tag.IMAGE_DESCRIPTION;
            default:
                return 0;
        }
    }

    public static int convertDegreeToExifOrientation(int degree) {
        switch (degree) {
            case MediaProviderUtils.ROTATION_90 /*90*/:
                return 6;
            case MediaProviderUtils.ROTATION_180 /*180*/:
                return 3;
            case Tag.IMAGE_DESCRIPTION /*270*/:
                return 8;
            default:
                return 1;
        }
    }

    public static int getExifOrientationDegree(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        if (filepath == null) {
            CamLog.d(TAG, "filepath is null, return degree 0");
            return 0;
        }
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            CamLog.e(TAG, "cannot read exif", ex);
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt("Orientation", -1);
            if (orientation != -1) {
                degree = convertExifOrientationToDegree(orientation);
            } else {
                CamLog.d(TAG, "getExifOrientation : getAttributeInt return = " + orientation);
            }
        }
        CamLog.i(TAG, "file = " + filepath + ", Degree = " + degree);
        return degree;
    }

    public static int getExifWidth(String filepath) {
        ExifInterface exif = null;
        if (filepath == null) {
            CamLog.d(TAG, "filepath is null, return degree 0");
            return DEFAULT_IMAGE_EXIF_WIDTH;
        }
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            CamLog.e(TAG, "cannot read exif", ex);
        }
        int width = DEFAULT_IMAGE_EXIF_WIDTH;
        if (exif != null) {
            width = exif.getAttributeInt("ImageWidth", DEFAULT_IMAGE_EXIF_WIDTH);
        }
        CamLog.i(TAG, "file = " + filepath + ", Exif width = " + width);
        return width;
    }

    public static int getExifHeight(String filepath) {
        ExifInterface exif = null;
        if (filepath == null) {
            CamLog.d(TAG, "filepath is null, return degree 0");
            return DEFAULT_IMAGE_EXIF_WIDTH;
        }
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            CamLog.e(TAG, "cannot read exif", ex);
        }
        int height = DEFAULT_IMAGE_EXIF_WIDTH;
        if (exif != null) {
            height = exif.getAttributeInt("ImageLength", DEFAULT_IMAGE_EXIF_WIDTH);
        }
        CamLog.i(TAG, "file = " + filepath + ", Exif height = " + height);
        return height;
    }

    static {
        mOffSet = 0;
        mLength = 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int getOrientation(byte[] r11) {
        /*
        r10 = 1229531648; // 0x49492a00 float:823968.0 double:6.074693478E-315;
        r9 = 4;
        r8 = 2;
        r5 = 0;
        if (r11 != 0) goto L_0x0009;
    L_0x0008:
        return r5;
    L_0x0009:
        mOffSet = r5;
        mLength = r5;
        r6 = checkJpegSpec(r11);
        if (r6 != 0) goto L_0x001b;
    L_0x0013:
        r6 = "CameraApp";
        r7 = "Invalid jpeg spec, orientation is 0";
        com.lge.camera.util.CamLog.e(r6, r7);
        goto L_0x0008;
    L_0x001b:
        r6 = mLength;
        r7 = 8;
        if (r6 <= r7) goto L_0x0094;
    L_0x0021:
        r6 = mOffSet;
        r4 = pack(r11, r6, r9, r5);
        if (r4 == r10) goto L_0x0036;
    L_0x0029:
        r6 = 1296891946; // 0x4d4d002a float:2.14958752E8 double:6.40749757E-315;
        if (r4 == r6) goto L_0x0036;
    L_0x002e:
        r6 = "CameraApp";
        r7 = "Invalid byte order";
        com.lge.camera.util.CamLog.e(r6, r7);
        goto L_0x0008;
    L_0x0036:
        if (r4 != r10) goto L_0x0053;
    L_0x0038:
        r2 = 1;
    L_0x0039:
        r6 = mOffSet;
        r6 = r6 + 4;
        r6 = pack(r11, r6, r9, r2);
        r0 = r6 + 2;
        r6 = 10;
        if (r0 < r6) goto L_0x004b;
    L_0x0047:
        r6 = mLength;
        if (r0 <= r6) goto L_0x0055;
    L_0x004b:
        r6 = "CameraApp";
        r7 = "Invalid offset";
        com.lge.camera.util.CamLog.e(r6, r7);
        goto L_0x0008;
    L_0x0053:
        r2 = r5;
        goto L_0x0039;
    L_0x0055:
        r6 = mOffSet;
        r6 = r6 + r0;
        mOffSet = r6;
        r6 = mLength;
        r6 = r6 - r0;
        mLength = r6;
        r6 = mOffSet;
        r6 = r6 + -2;
        r0 = pack(r11, r6, r8, r2);
        r1 = r0;
    L_0x0068:
        r0 = r1 + -1;
        if (r1 <= 0) goto L_0x0094;
    L_0x006c:
        r6 = mLength;
        r7 = 12;
        if (r6 < r7) goto L_0x0094;
    L_0x0072:
        r6 = mOffSet;
        r4 = pack(r11, r6, r8, r2);
        r6 = 274; // 0x112 float:3.84E-43 double:1.354E-321;
        if (r4 != r6) goto L_0x0086;
    L_0x007c:
        r5 = mOffSet;
        r5 = r5 + 8;
        r3 = pack(r11, r5, r8, r2);
        r5 = r3;
        goto L_0x0008;
    L_0x0086:
        r6 = mOffSet;
        r6 = r6 + 12;
        mOffSet = r6;
        r6 = mLength;
        r6 = r6 + -12;
        mLength = r6;
        r1 = r0;
        goto L_0x0068;
    L_0x0094:
        r6 = "CameraApp";
        r7 = "Orientation not found";
        com.lge.camera.util.CamLog.i(r6, r7);
        goto L_0x0008;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.util.ExifUtil.getOrientation(byte[]):int");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean checkJpegSpec(byte[] r7) {
        /*
        r6 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r2 = 1;
        r5 = 2;
        r1 = 0;
    L_0x0005:
        r3 = mOffSet;
        r3 = r3 + 3;
        r4 = r7.length;
        if (r3 >= r4) goto L_0x0034;
    L_0x000c:
        r3 = mOffSet;
        r4 = r3 + 1;
        mOffSet = r4;
        r3 = r7[r3];
        r3 = r3 & 255;
        if (r3 != r6) goto L_0x0034;
    L_0x0018:
        r3 = mOffSet;
        r3 = r7[r3];
        r0 = r3 & 255;
        if (r0 == r6) goto L_0x0005;
    L_0x0020:
        r3 = mOffSet;
        r3 = r3 + 1;
        mOffSet = r3;
        r3 = 216; // 0xd8 float:3.03E-43 double:1.067E-321;
        if (r0 == r3) goto L_0x0005;
    L_0x002a:
        if (r0 == r2) goto L_0x0005;
    L_0x002c:
        r3 = 217; // 0xd9 float:3.04E-43 double:1.07E-321;
        if (r0 == r3) goto L_0x0034;
    L_0x0030:
        r3 = 218; // 0xda float:3.05E-43 double:1.077E-321;
        if (r0 != r3) goto L_0x0036;
    L_0x0034:
        r1 = r2;
    L_0x0035:
        return r1;
    L_0x0036:
        r3 = mOffSet;
        r3 = pack(r7, r3, r5, r1);
        mLength = r3;
        r3 = mLength;
        if (r3 < r5) goto L_0x004a;
    L_0x0042:
        r3 = mOffSet;
        r4 = mLength;
        r3 = r3 + r4;
        r4 = r7.length;
        if (r3 <= r4) goto L_0x0052;
    L_0x004a:
        r2 = "CameraApp";
        r3 = "Invalid length";
        com.lge.camera.util.CamLog.e(r2, r3);
        goto L_0x0035;
    L_0x0052:
        r3 = 225; // 0xe1 float:3.15E-43 double:1.11E-321;
        if (r0 != r3) goto L_0x0081;
    L_0x0056:
        r3 = mLength;
        r4 = 8;
        if (r3 < r4) goto L_0x0081;
    L_0x005c:
        r3 = mOffSet;
        r3 = r3 + 2;
        r4 = 4;
        r3 = pack(r7, r3, r4, r1);
        r4 = 1165519206; // 0x45786966 float:3974.5874 double:5.758429993E-315;
        if (r3 != r4) goto L_0x0081;
    L_0x006a:
        r3 = mOffSet;
        r3 = r3 + 6;
        r3 = pack(r7, r3, r5, r1);
        if (r3 != 0) goto L_0x0081;
    L_0x0074:
        r1 = mOffSet;
        r1 = r1 + 8;
        mOffSet = r1;
        r1 = mLength;
        r1 = r1 + -8;
        mLength = r1;
        goto L_0x0034;
    L_0x0081:
        r3 = mOffSet;
        r4 = mLength;
        r3 = r3 + r4;
        mOffSet = r3;
        mLength = r1;
        goto L_0x0005;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.util.ExifUtil.checkJpegSpec(byte[]):boolean");
    }

    private static int pack(byte[] bytes, int offset, int length, boolean littleEndian) {
        int step = 1;
        if (littleEndian) {
            offset += length - 1;
            step = -1;
        }
        int value = 0;
        int length2 = length;
        while (true) {
            length = length2 - 1;
            if (length2 <= 0) {
                return value;
            }
            value = (value << 8) | (bytes[offset] & Ola_ShotParam.AnimalMask_Random);
            offset += step;
            length2 = length;
        }
    }

    public static byte[] setNewExifInformation(byte[] originalByteArray, int orientation, Location loc, float focalLength) {
        return setNewExifInformation(originalByteArray, 0, 0, orientation, -1, 1, loc, focalLength);
    }

    public static byte[] setNewExifInformation(byte[] originalByteArray, int width, int height, int orientation, int minSideLength, int maxNumOfPixels, Location loc, float focalLength) {
        try {
            Bitmap thumbBitmap = Util.makeBitmap(originalByteArray, minSideLength, maxNumOfPixels);
            if (thumbBitmap == null) {
                CamLog.d(TAG, "originalBitmap is null");
                return null;
            }
            byte[] generatedExifJpg;
            Exif.setFocalLength(focalLength);
            if (width == 0 || height == 0) {
                generatedExifJpg = Exif.processNewExif(originalByteArray, thumbBitmap, loc, orientation);
            } else {
                generatedExifJpg = Exif.processNewExif(originalByteArray, thumbBitmap, width, height, loc, orientation);
            }
            if (generatedExifJpg == null) {
                CamLog.d(TAG, "processNewExif() is failed");
            }
            thumbBitmap.recycle();
            return generatedExifJpg;
        } catch (Exception e) {
            CamLog.e(TAG, String.format("EXIF data insert fail.", new Object[0]));
            e.printStackTrace();
            return null;
        }
    }

    public static void copyExif(ExifInterface src, ExifInterface dest, String[] exceptions) {
        for (Field f : ExifInterface.class.getFields()) {
            if (f.getName().startsWith("TAG_")) {
                try {
                    String key = (String) f.get(null);
                    if (key != null && (exceptions == null || !isExcpetionTag(key, exceptions))) {
                        String value = src.getAttribute(key);
                        if (value != null) {
                            dest.setAttribute(key, value);
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private static boolean isExcpetionTag(String tag, String[] exceptions) {
        for (String exceptionTag : exceptions) {
            if (tag.equals(exceptionTag)) {
                return true;
            }
        }
        return false;
    }
}
