package com.lge.morpho.utils.multimedia;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Build;
import android.text.format.DateFormat;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class JpegHandler {
    public static final int ROTATION_180 = 180;
    public static final int ROTATION_270 = 270;
    public static final int ROTATION_90 = 90;
    public static final int ROTATION_NORMAL = 0;

    public static void compressBitmap(Bitmap bitmap, String filePath, int encodeQuality) throws IOException {
        ByteArrayOutputStream byte_os = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, encodeQuality, byte_os);
        byte_os.flush();
        byte[] jpegByteArray = byte_os.toByteArray();
        byte_os.close();
        FileOutputStream fileOutput = new FileOutputStream(filePath);
        try {
            fileOutput.write(jpegByteArray, 0, jpegByteArray.length);
            fileOutput.flush();
        } catch (IOException e) {
            CamLog.e(FaceDetector.TAG, "compressBitmap error : ", e);
        } finally {
            fileOutput.close();
        }
    }

    public static Bitmap decodeFile(String filepath, int max_memory) {
        Options opt = new Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, opt);
        int dst_w = opt.outWidth;
        int dst_h = opt.outHeight;
        if (max_memory > 0) {
            while (max_memory <= (dst_w * dst_h) * 4) {
                dst_w >>= 1;
                dst_h >>= 1;
            }
        }
        opt.inSampleSize = Math.max(opt.outWidth / dst_w, opt.outHeight / dst_h);
        opt.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filepath, opt);
    }

    public static void getImageSize(String filepath, int[] width, int[] height) {
        Options opt = new Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, opt);
        width[0] = opt.outWidth;
        height[0] = opt.outHeight;
    }

    public static void saveAsFile(byte[] image, String filePath) throws IOException {
        FileOutputStream fileStream = new FileOutputStream(filePath);
        try {
            fileStream.write(image);
            fileStream.flush();
        } catch (IOException e) {
            CamLog.e(FaceDetector.TAG, "saveAsFile error : ", e);
        } finally {
            fileStream.close();
        }
    }

    public static void setExifData(String filePath, Location location, int orientation) {
        if (filePath != null) {
            try {
                int orientationRotate;
                ExifInterface exif = new ExifInterface(filePath);
                String nowTime = DateFormat.format("yyyy:MM:dd kk:mm:ss", System.currentTimeMillis()).toString();
                exif.setAttribute("DateTime", nowTime);
                exif.setAttribute("DateTimeOriginal", nowTime);
                exif.setAttribute("DateTimeDigitized", nowTime);
                exif.setAttribute("Make", Build.MANUFACTURER);
                exif.setAttribute("Model", ModelProperties.readModelName());
                switch (orientation) {
                    case ROTATION_90 /*90*/:
                        orientationRotate = 6;
                        break;
                    case ROTATION_180 /*180*/:
                        orientationRotate = 3;
                        break;
                    case ROTATION_270 /*270*/:
                        orientationRotate = 8;
                        break;
                    default:
                        orientationRotate = 1;
                        break;
                }
                exif.setAttribute("Orientation", "" + orientationRotate);
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    exif.setAttribute("GPSLatitude", locationValueToString(latitude));
                    exif.setAttribute("GPSLatitudeRef", latitudeValueToNorS(latitude));
                    exif.setAttribute("GPSLongitude", locationValueToString(longitude));
                    exif.setAttribute("GPSLongitudeRef", longitudeValueToEorW(longitude));
                }
                exif.saveAttributes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setInExif(String filePath, Location location) {
        if (filePath != null) {
            String nowTime = DateFormat.format("yyyy:MM:dd kk:mm:ss", System.currentTimeMillis()).toString();
            try {
                ExifInterface exif = new ExifInterface(filePath);
                exif.setAttribute("DateTime", nowTime);
                exif.setAttribute("DateTimeOriginal", nowTime);
                exif.setAttribute("DateTimeDigitized", nowTime);
                exif.setAttribute("Make", Build.BRAND);
                exif.setAttribute("Model", ModelProperties.readModelName());
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    SimpleDateFormat dateStamp = new SimpleDateFormat("yyyy:MM:dd");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("kk/1,mm/1,ss/1");
                    TimeZone tzUTC = TimeZone.getTimeZone("UTC");
                    dateStamp.setTimeZone(tzUTC);
                    simpleDateFormat.setTimeZone(tzUTC);
                    String lat_str = locationValueToString(latitude);
                    String lat_str_ref = latitudeValueToNorS(latitude);
                    String lon_str = locationValueToString(longitude);
                    String lon_str_ref = longitudeValueToEorW(longitude);
                    String str = "GPSDateStamp";
                    exif.setAttribute(r18, dateStamp.format(Long.valueOf(location.getTime())));
                    String format = simpleDateFormat.format(Long.valueOf(location.getTime()));
                    exif.setAttribute("GPSTimeStamp", format);
                    exif.setAttribute("GPSLatitude", lat_str);
                    exif.setAttribute("GPSLatitudeRef", lat_str_ref);
                    exif.setAttribute("GPSLongitude", lon_str);
                    exif.setAttribute("GPSLongitudeRef", lon_str_ref);
                }
                exif.saveAttributes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String locationValueToString(double value) {
        int degrees = new Double(value).intValue();
        String result = "" + degrees + "/1,";
        value = (value - ((double) degrees)) * 60.0d;
        int minutes = new Double(value).intValue();
        result = result + minutes + "/1,";
        return result + new Double((value - ((double) minutes)) * 60.0d).intValue() + "/1";
    }

    public static String latitudeValueToNorS(double value) {
        if (value > 0.0d) {
            return "N";
        }
        return "S";
    }

    public static String longitudeValueToEorW(double value) {
        if (value > 0.0d) {
            return "E";
        }
        return "W";
    }
}
